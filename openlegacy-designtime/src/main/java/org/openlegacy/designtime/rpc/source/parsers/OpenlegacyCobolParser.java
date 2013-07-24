package org.openlegacy.designtime.rpc.source.parsers;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.designtime.rpc.source.CodeParser;
import org.openlegacy.exceptions.OpenLegacyProviderException;
import org.openlegacy.rpc.definitions.RpcEntityDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import koopa.parsers.ParseResults;
import koopa.parsers.cobol.CobolParser;
import koopa.tokens.Token;
import koopa.trees.antlr.jaxen.Jaxen;
import koopa.util.Tuple;

/**
 * A warper of koopa COBOL parser, It fetch COBOL program interface from antlr tree and generate RpcEntityDefinition.
 * 
 */

public class OpenlegacyCobolParser implements CodeParser {

	private final static String PARAGRAPH_QUERY = "//paragraph";
	private final static String PROCEDURE_QUERY = "//procedureDivision";
	private final static String ROOT_PROGRAM_QUERY_TEMPLATE = "//paragraph//cobolWord[text()='%s']";
	private final static String USE_PARAMETER_QUERY = "//usingPhrase//cobolWord//text()";
	private final static String USE_COPYBOOK_QUERY = "//linkageSection//copyStatement";
	private final static String COPY_ROOT_QUERY = "//copyStatement";
	private final static String COPY_FILE_QUERY = "//textName//cobolWord//text()";
	private final static String OPERAND_QUERY = "//copyOperandName//pseudoLiteral//text()";
	private final static String COPYBOOK_REPLACE_QUERY = "//copyReplacementInstruction";
	private final static String PARAMETER_DEFINITION_QUERY = "//linkageSection//dataDescriptionEntry_format1";
	private final static String PARAMETER_COPYBOOK_DEFINITION_QUERY = "//dataDescriptionEntry_format1";
	private final static String PARAMETER_USED_QUERY = "//identifier_format2//cobolWord//text()";
	private final static Log logger = LogFactory.getLog(OpenlegacyCobolParser.class);

	private String mainProcedureName;

	private String copyBookPath;

	private boolean delTempFiles;

	private CobolParser cobolParser;

	private RpcEntityDefinitionBuilder rpcEntityDefinitionBuilder;

	private ParseResults parseResults;

	public int getErrorsCount() {
		return parseResults.getErrorCount();
	}

	public String getError(int i) {
		final Tuple<Token, String> error = parseResults.getError(i);
		return (error.getFirst() + " " + error.getSecond());
	}

	public int getWarningCount() {
		return parseResults.getWarningCount();
	}

	public String getWarning(int i) {
		final Tuple<Token, String> warning = parseResults.getWarning(i);
		return (warning.getFirst() + " " + warning.getSecond());
	}

	public String getMainProcedureName() {
		return mainProcedureName;
	}

	public void setMainProcedureName(String mainProcedureName) {
		this.mainProcedureName = mainProcedureName;
	}

	public String getCopyBookPath() {
		return copyBookPath;
	}

	private String writeToTempFile(String source, String extension) throws IOException {

		File tempFile = File.createTempFile("temp", extension);
		if (delTempFiles == true) {
			tempFile.deleteOnExit();
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(source);
		// Close the output stream
		out.close();
		return tempFile.getPath();
	}

	private boolean handlePreProcess(CommonTree rootNode) throws IOException {

		boolean result = false;

		if (!Jaxen.evaluate(rootNode, USE_COPYBOOK_QUERY).isEmpty()) {
			result = true;
			List<File> koppaSerachPath = new ArrayList<File>();
			koppaSerachPath.add(new File(copyBookPath));
			cobolParser.setCopybookPath(koppaSerachPath);
			cobolParser.setPreprocessing(true);

			@SuppressWarnings("unchecked")
			List<CommonTree> copybookNodes = (List<CommonTree>)Jaxen.evaluate(rootNode, COPY_ROOT_QUERY);
			if (!copybookNodes.isEmpty()) {

				for (CommonTree copybookNode : copybookNodes) {
					copybookNode.setParent(null);
					@SuppressWarnings("unchecked")
					List<CommonTree> fileNode = (List<CommonTree>)Jaxen.evaluate(copybookNode, COPY_FILE_QUERY);
					String fileName = fileNode.get(0).getText() + ".cpy";
					@SuppressWarnings("unchecked")
					List<CommonTree> replaceNodes = (List<CommonTree>)Jaxen.evaluate(copybookNode, COPYBOOK_REPLACE_QUERY);
					if (!replaceNodes.isEmpty()) {

						CommonTree replaceNode = replaceNodes.get(0);
						replaceNode.setParent(null);
						@SuppressWarnings("unchecked")
						List<CommonTree> operandNodes = (List<CommonTree>)Jaxen.evaluate(replaceNode, OPERAND_QUERY);
						String oldString = removeDelimiter(operandNodes.get(0).getText());
						String newString = removeDelimiter(operandNodes.get(1).getText());
						CobolParserUtils.replaceStringInFile(oldString, newString, copyBookPath, fileName);
					}
				}

			}
		}
		return result;
	}

	@SuppressWarnings("static-method")
	private String removeDelimiter(String name) {
		int length = name.length();
		return name.substring(2, length - 2);
	}

	public void preProcess(Map<String, InputStream> streamMap) throws IOException {
		copyBookPath = CobolParserUtils.CreateTmpDir("CopyBookDir");
		CobolParserUtils.copyStreamsToFile(copyBookPath, streamMap);

	}

	public RpcEntityDefinition parse(String source) {
		return parseInternal(source, ".cbl", false, PARAMETER_DEFINITION_QUERY);
	}

	public RpcEntityDefinition parseCopyBook(String source) {
		return parseInternal(source, ".cpy", true, PARAMETER_COPYBOOK_DEFINITION_QUERY);
	}

	private RpcEntityDefinition parseInternal(String source, String extension, boolean isCopybook, String queryString) {

		String tempFileName = "";

		try {
			tempFileName = writeToTempFile(source, extension);
			;
		} catch (Exception e) {// Catch exception if any
			return null;
		}
		try {
			parseResults = cobolParser.parse(new File(tempFileName));
			if (handlePreProcess(parseResults.getTree())) {
				parseResults = cobolParser.parse(new File(tempFileName));
			}

		} catch (Exception e) {
			logger.debug("Failed to parse file");
			throw (new OpenLegacyProviderException("Koopa input is invalid", e));
		}

		if (!parseResults.isValidInput()) {
			throw (new OpenLegacyProviderException("Koopa input is invalid"));
		}

		CommonTree rootNode = parseResults.getTree();

		List<ParameterStructure> allParamtersNodes = new ArrayList<ParameterStructure>();

		try {

			@SuppressWarnings("unchecked")
			List<CommonTree> jaxsonParamtersNodes = (List<CommonTree>)Jaxen.evaluate(rootNode, queryString);
			Map<String, String> fieldToTopLevelName = arrangeInLevels(allParamtersNodes, jaxsonParamtersNodes);
			if (isCopybook) {
				return rpcEntityDefinitionBuilder.build(allParamtersNodes);
			} else {
				List<ParameterStructure> interfaceParamtersNodes = filterUsedParameter(allParamtersNodes, fieldToTopLevelName);
				return rpcEntityDefinitionBuilder.build(interfaceParamtersNodes);
			}
			// } catch (XPathExpressionException e) {

		} catch (Exception e) {
			logger.debug("failed at query stage");
			throw new OpenLegacyProviderException("Koopa input is invalid");
		}
	}

	private List<ParameterStructure> filterUsedParameter(List<ParameterStructure> allParamtersNodes,
			Map<String, String> fieldToTopLevelName) {
		List<String> usedParamtersNames = getParameterNames();
		List<ParameterStructure> interfaceParamtersNodes = new ArrayList<ParameterStructure>();
		List<String> interfaceParamtersNames = new ArrayList<String>();

		for (String prameterName : usedParamtersNames) {
			if (fieldToTopLevelName.containsKey(prameterName) && !interfaceParamtersNames.contains(prameterName)) {
				interfaceParamtersNames.add(fieldToTopLevelName.get(prameterName));
			}
		}
		for (ParameterStructure parameterNode : allParamtersNodes) {
			if (interfaceParamtersNames.contains(parameterNode.getFieldName())) {
				interfaceParamtersNodes.add(parameterNode);
			}
		}
		return interfaceParamtersNodes;
	}

	private static Map<String, String> arrangeInLevels(List<ParameterStructure> allParamtersNodes,
			List<CommonTree> jaxsonParamtersNodes) {
		for (int parameterIdx = 0; parameterIdx < jaxsonParamtersNodes.size(); parameterIdx++) {

			allParamtersNodes.add(new KoopaParameterStructure(jaxsonParamtersNodes.get(parameterIdx)));
		}

		Map<String, String> fieldToTopLevelName = new HashMap<String, String>();
		for (int parameterIdx = 0; parameterIdx < allParamtersNodes.size(); parameterIdx++) {
			KoopaParameterStructure cobolParmeter = (KoopaParameterStructure)allParamtersNodes.get(parameterIdx);
			fieldToTopLevelName.put(cobolParmeter.getFieldName(), cobolParmeter.getFieldName());
			if (!cobolParmeter.isSimple()) {
				fieldToTopLevelName.putAll(cobolParmeter.collectSubFields(allParamtersNodes, parameterIdx + 1,
						cobolParmeter.getFieldName()));
			}
		}
		return fieldToTopLevelName;
	}

	private List<String> getParameterNames() {

		CommonTree rootNode = parseResults.getTree();
		List<String> paramtersNames = new ArrayList<String>();
		CommonTree programRoot = null;

		@SuppressWarnings("unchecked")
		List<CommonTree> programRootNode = (List<CommonTree>)Jaxen.evaluate(rootNode, PARAGRAPH_QUERY);

		if (programRootNode.size() == 1) {
			programRoot = (CommonTree)programRootNode.get(0).getParent();
		} else if (programRootNode.size() > 1) {
			String rootProgramQueryString = String.format(ROOT_PROGRAM_QUERY_TEMPLATE, mainProcedureName);

			@SuppressWarnings("unchecked")
			List<CommonTree> retryProgramRootNode = (List<CommonTree>)Jaxen.evaluate(rootNode, rootProgramQueryString);
			// Assume there is only one main
			programRoot = (CommonTree)retryProgramRootNode.get(0).getParent().getParent().getParent().getParent();
		} else {
			@SuppressWarnings("unchecked")
			List<CommonTree> retryProgramRootNode = (List<CommonTree>)Jaxen.evaluate(rootNode, PROCEDURE_QUERY);
			programRoot = retryProgramRootNode.get(0);
		}

		if (programRoot == null) {
			return paramtersNames;
		}

		programRoot.setParent(null);

		@SuppressWarnings("unchecked")
		List<CommonTree> procedureParamtersNodes = (List<CommonTree>)Jaxen.evaluate(programRoot, USE_PARAMETER_QUERY);

		for (CommonTree procedureParamtersNode : procedureParamtersNodes) {
			paramtersNames.add(procedureParamtersNode.getText());
		}

		if (paramtersNames.isEmpty()) {
			@SuppressWarnings("unchecked")
			List<CommonTree> usedParameters = (List<CommonTree>)Jaxen.evaluate(programRoot, PARAMETER_USED_QUERY);
			for (CommonTree parameter : usedParameters) {
				paramtersNames.add(parameter.getText());
			}
		}

		return paramtersNames;
	}

	public void setDelTempFiles(boolean delTempFiles) {
		this.delTempFiles = delTempFiles;
	}

	public CobolParser getCobolParser() {
		return cobolParser;
	}

	public void setCobolParser(CobolParser cobolParser) {
		this.cobolParser = cobolParser;
	}

	public RpcEntityDefinitionBuilder getRpcEntityDefinitionBuilder() {
		return rpcEntityDefinitionBuilder;
	}

	public void setRpcEntityDefinitionBuilder(RpcEntityDefinitionBuilder rpcEntityDefinitionBuilder) {
		this.rpcEntityDefinitionBuilder = rpcEntityDefinitionBuilder;
	}

}
