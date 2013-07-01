package org.openlegacy.designtime.rpc.source.parsers;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openlegacy.definitions.FieldTypeDefinition;
import org.openlegacy.definitions.PartEntityDefinition;
import org.openlegacy.definitions.support.SimpleNumericFieldTypeDefinition;
import org.openlegacy.designtime.DesigntimeException;
import org.openlegacy.rpc.definitions.RpcEntityDefinition;
import org.openlegacy.rpc.definitions.RpcFieldDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import koopa.tokenizers.cobol.SourceFormat;

public class CobolParserTest {

	@Test
	public void testCobolParserSimpleNumber() throws IOException, DesigntimeException {
		OpenlegacyCobolParser cobolParser = new OpenlegacyCobolParser();
		cobolParser.setFormat(SourceFormat.FREE);

		String sourceFile = "simpleNumber.cbl";
		String entityName = org.openlegacy.utils.FileUtils.fileWithoutAnyExtension(sourceFile);
		String source = IOUtils.toString(getClass().getResource(sourceFile));
		RpcEntityDefinition rpcEntityDefinition = cobolParser.parse(source, entityName);
		Assert.assertNotNull(rpcEntityDefinition);
		Assert.assertEquals(1, rpcEntityDefinition.getFieldsDefinitions().size());
		Map<String, RpcFieldDefinition> fieldDefinitionMap = rpcEntityDefinition.getFieldsDefinitions();
		RpcFieldDefinition firstField = fieldDefinitionMap.get("PARAM1");
		Assert.assertTrue(Math.abs(firstField.getLength() - 2) < 0.001);
		Assert.assertEquals(Integer.class, firstField.getJavaType());
		FieldTypeDefinition fieldTypeDefinition = firstField.getFieldTypeDefinition();
		Assert.assertEquals(SimpleNumericFieldTypeDefinition.class, fieldTypeDefinition.getClass());
		Assert.assertEquals(99.0, ((SimpleNumericFieldTypeDefinition)fieldTypeDefinition).getMaximumValue(), 0.01);

	}

	@Test
	public void testCobolParser() throws IOException, DesigntimeException {
		OpenlegacyCobolParser cobolParser = new OpenlegacyCobolParser();
		cobolParser.setFormat(SourceFormat.FREE);

		String sourceFile = "as400.cbl";
		String source = IOUtils.toString(getClass().getResource(sourceFile));
		String entityName = sourceFile.substring(0, sourceFile.indexOf(".") > 0 ? sourceFile.indexOf(".") : sourceFile.length());
		RpcEntityDefinition rpcEntityDefinition = cobolParser.parse(source, entityName);
		Assert.assertNotNull(rpcEntityDefinition);
		Assert.assertEquals(0, rpcEntityDefinition.getFieldsDefinitions().size());
		Assert.assertEquals(1, rpcEntityDefinition.getPartsDefinitions().size());
		Map<String, PartEntityDefinition<RpcFieldDefinition>> partEntityDefinitionMap = rpcEntityDefinition.getPartsDefinitions();
		PartEntityDefinition<RpcFieldDefinition> child = partEntityDefinitionMap.get("PARAM1");
		Map<String, RpcFieldDefinition> childFieldsMap = child.getFieldsDefinitions();
		Assert.assertEquals(2, childFieldsMap.size());
		RpcFieldDefinition child1 = childFieldsMap.get("CHILD1");
		Assert.assertTrue(Math.abs(child1.getLength() - 2) < 0.001);
		Assert.assertEquals(Integer.class, child1.getJavaType());
		FieldTypeDefinition fieldTypeDefinition = child1.getFieldTypeDefinition();
		Assert.assertEquals(SimpleNumericFieldTypeDefinition.class, fieldTypeDefinition.getClass());
		Assert.assertEquals(99.0, ((SimpleNumericFieldTypeDefinition)fieldTypeDefinition).getMaximumValue(), 0.01);

	}

	@Test
	public void replaceInFile() throws IOException {

		String sourceBeforeFile = "sampcpy1.cpy";
		String sourceAfterFile = "sampcpy1r.cpy";
		String source = IOUtils.toString(getClass().getResource(sourceBeforeFile));

		TemporaryFolder tempFolder = new TemporaryFolder();
		tempFolder.create();
		File tempFile = tempFolder.newFile(sourceBeforeFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		writer.write(source);
		writer.close();
		OpenlegacyCobolParser cobolParser = new OpenlegacyCobolParser();

		cobolParser.replaceInFile(":XXX:", "C00-", tempFile.getPath(), "");

		String expexted = IOUtils.toString(getClass().getResource(sourceAfterFile));
		String actual = IOUtils.toString(new FileReader(new File(tempFile.getPath())));
		Assert.assertTrue(expexted.equals(actual));
	}

	// public void testPreProcess() throws IOException {
	// Map<String, BufferedReader> streamMap = new HashMap<String, BufferedReader>();
	// streamMap.put("sampcpy1.cpy",
	// new BufferedReader(new FileReader(new File("C:\\openlegacy\\cobol_examples\\sampcpy1.cpy"))));
	// streamMap.put("sampcpy2.cpy",
	// new BufferedReader(new FileReader(new File("C:\\openlegacy\\cobol_examples\\sampcpy2.cpy"))));
	// OpenlegacyCobolParser cobolParser = new OpenlegacyCobolParser();
	// cobolParser.setDelTempFiles(false);
	// cobolParser.preProcess(streamMap);
	//
	// String sourceFile = "sameprog.cbl";
	// String source = IOUtils.toString(getClass().getResource(sourceFile));
	// String entityName = sourceFile.substring(0, sourceFile.indexOf(".") > 0 ? sourceFile.indexOf(".") :
	// sourceFile.length());
	// cobolParser.parse(source, entityName);

	// }

}
