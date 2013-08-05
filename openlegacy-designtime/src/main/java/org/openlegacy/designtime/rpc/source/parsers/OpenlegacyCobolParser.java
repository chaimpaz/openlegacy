package org.openlegacy.designtime.rpc.source.parsers;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.designtime.rpc.source.CodeParser;
import org.openlegacy.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import koopa.parsers.ParseResults;
import koopa.parsers.cobol.CobolParser;
import koopa.trees.antlr.jaxen.Jaxen;

/**
 * A warper of koopa COBOL parser, It fetch COBOL program interface from antlr tree and generate RpcEntityDefinition.
 * 
 */

public class OpenlegacyCobolParser implements CodeParser {

	private final static String USE_COPYBOOK_QUERY = "//linkageSection//copyStatement";
	private final static String COPY_ROOT_QUERY = "//copyStatement";
	private final static String COPY_FILE_QUERY = "//textName//cobolWord//text()";
	private final static String OPERAND_QUERY = "//copyOperandName//pseudoLiteral//text()";
	private final static String COPYBOOK_REPLACE_QUERY = "//copyReplacementInstruction";

	private final static String COPYBOOK_EXTENSION = ".cpy";
	private final static String COBOL_EXTENSION = ".cbl";
	private final static Log logger = LogFactory.getLog(OpenlegacyCobolParser.class);

	private String copyBookPath;

	private CobolParser cobolParser;

	public String getCopyBookPath() {
		return copyBookPath;
	}

	@SuppressWarnings("static-method")
	private String writeToTempFile(String source, String extension) throws IOException {

		File tempFile = File.createTempFile("temp", extension);

		tempFile.deleteOnExit();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.codeParser#parse(java.lang.String, java.util.Map)
	 */
	public org.openlegacy.designtime.rpc.source.parsers.ParseResults parse(String source, Map<String, InputStream> streamMap)
			throws IOException {
		copyBookPath = CobolParserUtils.CreateTmpDir("CopyBookDir");
		CobolParserUtils.copyStreamsToFile(copyBookPath, streamMap);
		return parse(source, COBOL_EXTENSION);

	}

	public org.openlegacy.designtime.rpc.source.parsers.ParseResults parse(String source, String fileName) {

		String tempFileName = "";
		String extension = FileUtils.fileExtension(fileName);
		boolean isCopyBook = extension.equals(COPYBOOK_EXTENSION);
		if (isCopyBook) {
			source = source.replaceAll(":.*:", "");
		}

		try {
			tempFileName = writeToTempFile(source, extension);
		} catch (Exception e) {// Catch exception if any
			logger.fatal(e);
			return null;
		}
		try {
			ParseResults parseResults = cobolParser.parse(new File(tempFileName));
			if (handlePreProcess(parseResults.getTree())) {
				parseResults = cobolParser.parse(new File(tempFileName));
			}

			CobolParseResults olParseResults = new CobolParseResults(parseResults, isCopyBook);

			if (!parseResults.isValidInput()) {
				throw (new OpenLegacyParseException("Koopa input is invalid", olParseResults));
			}
			return olParseResults;

		} catch (Exception e) {
			throw (new OpenLegacyParseException("Koopa input is invalid", e));
		}

	}

	public CobolParser getCobolParser() {
		return cobolParser;
	}

	public void setCobolParser(CobolParser cobolParser) {
		this.cobolParser = cobolParser;
	}

}
