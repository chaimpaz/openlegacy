package org.openlegacy.designtime.rpc.source.parsers;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.designtime.rpc.source.CodeParser;
import org.openlegacy.exceptions.OpenLegacyProviderException;
import org.openlegacy.rpc.definitions.RpcEntityDefinition;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import koopa.parsers.ParseResults;
import koopa.parsers.cobol.CobolParser;
import koopa.tokens.Token;
import koopa.trees.antlr.jaxen.Jaxen;
import koopa.util.Tuple;

/*
 * A warper of koopa COBOL parser, It fetch COBOL program interface from antlr tree and generate RpcEntityDefinition.
 * 
 */

public class OpenlegacyCobolParser implements CodeParser {

	private final static String ROOT_PROGRAM_QUERY_TEMPLATE = "//paragraph//cobolWord[text()='%s']";
	private final static String USE_PARAMETER_QUERY = "//usingPhrase//cobolWord//text()";
	private final static String USE_COPYBOOK_QUERY = "//linkageSection//copyStatement";
	private final static String COPYBOOK_REPLACE_QUERY = "//linkageSection//copyReplacementInstruction";
	private final static String PARAMETER_DEFINITION_QUERY = "//dataDescriptionEntry_format1";
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

	private void setCopyBookPath() {
		boolean needToFind = true;
		String sysTempDir = FileUtils.getTempDirectoryPath();
		File tempDir;
		String randomDirName = null;
		while (needToFind) {
			randomDirName = sysTempDir + "CopyBookFiles_" + ((Double)Math.random()).toString();
			tempDir = new File(randomDirName);
			if (!tempDir.exists()) {
				needToFind = false;
				tempDir.mkdir();
				if (delTempFiles == true) {
					tempDir.deleteOnExit();
				}
			}
		}
		copyBookPath = randomDirName + File.separator;
		List<File> koppaSerachPath = new ArrayList<File>();
		koppaSerachPath.add(new File(copyBookPath));
		cobolParser.setCopybookPath(koppaSerachPath);
	}

	public void replaceInFile(String oldstring, String newstring, String path, String fileName) throws IOException {

		String fullPathName = path + fileName;
		File orig = new File(fullPathName);
		File fp = new File(fullPathName + ".bk");
		orig.renameTo(fp);
		File out = new File(fullPathName);
		BufferedReader reader = new BufferedReader(new FileReader(fp));
		PrintWriter writer = new PrintWriter(new FileWriter(out));
		String line = null;
		while ((line = reader.readLine()) != null) {
			writer.println(line.replaceAll(oldstring, newstring));
		}
		reader.close();
		writer.close();
	}

	public void preProcess(Map<String, BufferedReader> streamMap) throws IOException {
		setCopyBookPath();
		for (String copyBookName : streamMap.keySet()) {
			File copyBookFile = new File(copyBookPath + copyBookName);
			if (delTempFiles) {
				copyBookFile.deleteOnExit();
			}
			PrintWriter out = new PrintWriter(new FileWriter(copyBookFile));
			String tmpString;
			while ((tmpString = streamMap.get(copyBookName).readLine()) != null) {
				out.println(tmpString);
			}
			out.close();
		}
	}

	private String writeToTempFile(String source) throws IOException {

		File tempFile = File.createTempFile("temp", ".cbl");
		if (delTempFiles == true) {
			tempFile.deleteOnExit();
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(source);
		// Close the output stream
		out.close();
		return tempFile.getPath();

	}

	private boolean preProcess(CommonTree rootNode) throws IOException {

		boolean result = false;
		if (!Jaxen.evaluate(rootNode, USE_COPYBOOK_QUERY).isEmpty()) {

			@SuppressWarnings("unchecked")
			List<CommonTree> replacingNodes = (List<CommonTree>)Jaxen.evaluate(rootNode, COPYBOOK_REPLACE_QUERY);
			if (!replacingNodes.isEmpty()) {
				result = true;
				for (CommonTree toReplace : replacingNodes) {

					String fileName = toReplace.getParent().getParent().getChild(0).getText();
					String oldString = toReplace.getChild(0).getChild(0).getText();
					String newString = toReplace.getChild(0).getChild(0).getText();
					replaceInFile(oldString, newString, copyBookPath, fileName);

				}
			}

		}
		return result;
	}

	public RpcEntityDefinition parse(String source, String rpcEntityName) {

		String tempFileName = "";
		RpcEntityDefinition entityDefinition = null;
		try {
			tempFileName = writeToTempFile(source);
			;
		} catch (Exception e) {// Catch exception if any
			return null;
		}
		try {
			parseResults = cobolParser.parse(new File(tempFileName));
			if (preProcess(parseResults.getTree())) {
				parseResults = cobolParser.parse(new File(tempFileName));
			}

		} catch (Exception e) {
			logger.debug("Failed to parse file");
			throw (new OpenLegacyProviderException("Koopa input is invalid", e));
		}

		if (!parseResults.isValidInput()) {
			throw (new OpenLegacyProviderException("Koopa input is invalid"));
		}

		String rootProgramQueryString = String.format(ROOT_PROGRAM_QUERY_TEMPLATE, mainProcedureName);
		CommonTree rootNode = parseResults.getTree();

		List<String> procedureParamtersNames = new ArrayList<String>();
		List<ParameterStructure> convertedParamtersNodes = new ArrayList<ParameterStructure>();
		try {

			@SuppressWarnings("unchecked")
			List<CommonTree> programRootNode = (List<CommonTree>)Jaxen.evaluate(rootNode, rootProgramQueryString);
			CommonTree programRoot = (CommonTree)programRootNode.get(0).getParent().getParent().getParent().getParent();

			@SuppressWarnings("unchecked")
			List<CommonTree> procedureParamtersNodes = (List<CommonTree>)Jaxen.evaluate(programRoot, USE_PARAMETER_QUERY);

			for (CommonTree procedureParamtersNode : procedureParamtersNodes) {
				procedureParamtersNames.add(procedureParamtersNode.getText());
			}
			@SuppressWarnings("unchecked")
			List<CommonTree> jaxsonParamtersNodes = (List<CommonTree>)Jaxen.evaluate(rootNode, PARAMETER_DEFINITION_QUERY);
			for (int parameterIdx = 0; parameterIdx < jaxsonParamtersNodes.size(); parameterIdx++) {

				convertedParamtersNodes.add(new KoopaParameterStructure(jaxsonParamtersNodes.get(parameterIdx)));
			}

			for (int parameterIdx = 0; parameterIdx < convertedParamtersNodes.size(); parameterIdx++) {
				KoopaParameterStructure cobolParmeter = (KoopaParameterStructure)convertedParamtersNodes.get(parameterIdx);
				if (!cobolParmeter.isSimple()) {
					cobolParmeter.collectSubFields(convertedParamtersNodes, parameterIdx);
				}
				if (!procedureParamtersNames.contains(cobolParmeter.getFieldName())) {
					convertedParamtersNodes.remove(parameterIdx);
					parameterIdx++;
				}

			}
			entityDefinition = rpcEntityDefinitionBuilder.build(rpcEntityName, convertedParamtersNodes);
			// } catch (XPathExpressionException e) {
		} catch (Exception e) {
			logger.debug("failed at query stage");
			throw new OpenLegacyProviderException("Koopa input is invalid");
		}

		return entityDefinition;
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
