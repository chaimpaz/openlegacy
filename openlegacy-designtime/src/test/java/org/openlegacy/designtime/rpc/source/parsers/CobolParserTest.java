package org.openlegacy.designtime.rpc.source.parsers;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.openlegacy.definitions.FieldTypeDefinition;
import org.openlegacy.definitions.PartEntityDefinition;
import org.openlegacy.definitions.support.SimpleExtendedNumericFieldTypeDefinition;
import org.openlegacy.definitions.support.SimpleTextFieldTypeDefinition;
import org.openlegacy.designtime.DesigntimeException;
import org.openlegacy.rpc.definitions.RpcEntityDefinition;
import org.openlegacy.rpc.definitions.RpcFieldDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcListFieldTypeDefinition;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

@ContextConfiguration("CobolParserTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CobolParserTest {

	@Inject
	OpenlegacyCobolParser openlegacyCobolParser;

	double precise = 0.001;

	@Test
	public void testCobolParserSimpleField() throws IOException, DesigntimeException {

		String sourceFile = "simpleField.cbl";
		SimpleExtendedNumericFieldTypeDefinition simpleExtendedNumericFieldTypeDefinition;
		RpcFieldDefinition fieldDefinition;
		FieldTypeDefinition fieldTypeDefinition;

		String entityName = org.openlegacy.utils.FileUtils.fileWithoutAnyExtension(sourceFile);
		String source = IOUtils.toString(getClass().getResource(sourceFile));
		RpcEntityDefinition rpcEntityDefinition = openlegacyCobolParser.parse(source, entityName);

		Assert.assertNotNull(rpcEntityDefinition);
		Assert.assertEquals(3, rpcEntityDefinition.getFieldsDefinitions().size());
		Map<String, RpcFieldDefinition> fieldDefinitions = rpcEntityDefinition.getFieldsDefinitions();

		// Fetch Integer
		fieldDefinition = fieldDefinitions.get("PARAM1");
		Assert.assertEquals(new Integer(2), fieldDefinition.getLength());
		Assert.assertEquals(Integer.class, fieldDefinition.getJavaType());
		fieldTypeDefinition = fieldDefinition.getFieldTypeDefinition();
		Assert.assertEquals(SimpleExtendedNumericFieldTypeDefinition.class, fieldTypeDefinition.getClass());
		simpleExtendedNumericFieldTypeDefinition = (SimpleExtendedNumericFieldTypeDefinition)fieldTypeDefinition;
		Assert.assertEquals(99.0, ((SimpleExtendedNumericFieldTypeDefinition)fieldTypeDefinition).getMaximumValue(), precise);
		Assert.assertEquals(0, simpleExtendedNumericFieldTypeDefinition.getDecimalPlaces());

		// Fetch Decimal
		fieldDefinition = fieldDefinitions.get("PARAM2");
		Assert.assertEquals(new Integer(1), fieldDefinition.getLength());
		Assert.assertEquals(Double.class, fieldDefinition.getJavaType());
		Assert.assertEquals(SimpleExtendedNumericFieldTypeDefinition.class, fieldTypeDefinition.getClass());
		fieldTypeDefinition = fieldDefinition.getFieldTypeDefinition();
		simpleExtendedNumericFieldTypeDefinition = (SimpleExtendedNumericFieldTypeDefinition)fieldTypeDefinition;
		Assert.assertEquals(9.9, simpleExtendedNumericFieldTypeDefinition.getMaximumValue(), precise);
		Assert.assertEquals(1, simpleExtendedNumericFieldTypeDefinition.getDecimalPlaces());

		// String
		fieldDefinition = fieldDefinitions.get("PARAM3");
		Assert.assertEquals(new Integer(4), fieldDefinition.getLength());
		Assert.assertEquals(String.class, fieldDefinition.getJavaType());
		fieldTypeDefinition = fieldDefinition.getFieldTypeDefinition();
		Assert.assertEquals(SimpleTextFieldTypeDefinition.class, fieldTypeDefinition.getClass());

	}

	@Test
	public void testCobolParserSimpleStructure() throws IOException, DesigntimeException {

		String sourceFile = "as400.cbl";
		List<String> childs = new ArrayList<String>();
		childs.add("CHILD1");
		childs.add("CHILD2");
		String source = IOUtils.toString(getClass().getResource(sourceFile));
		String entityName = org.openlegacy.utils.FileUtils.fileWithoutAnyExtension(sourceFile);
		RpcEntityDefinition rpcEntityDefinition = openlegacyCobolParser.parse(source, entityName);
		Assert.assertNotNull(rpcEntityDefinition);
		Assert.assertEquals(0, rpcEntityDefinition.getFieldsDefinitions().size());
		Assert.assertEquals(1, rpcEntityDefinition.getPartsDefinitions().size());
		Map<String, PartEntityDefinition<RpcFieldDefinition>> partEntityDefinition = rpcEntityDefinition.getPartsDefinitions();
		PartEntityDefinition<RpcFieldDefinition> child = partEntityDefinition.get("PARAM1");

		Map<String, RpcFieldDefinition> childFields = child.getFieldsDefinitions();
		Assert.assertEquals(2, childFields.size());

		// CHILDS
		for (String childName : childs) {
			RpcFieldDefinition childField = childFields.get(childName);
			Assert.assertEquals(new Integer(2), childField.getLength());
			Assert.assertEquals(Integer.class, childField.getJavaType());
			FieldTypeDefinition fieldTypeDefinition = childField.getFieldTypeDefinition();
			Assert.assertEquals(SimpleExtendedNumericFieldTypeDefinition.class, fieldTypeDefinition.getClass());
			Assert.assertEquals(99.0, ((SimpleExtendedNumericFieldTypeDefinition)fieldTypeDefinition).getMaximumValue(), precise);
		}
	}

	@Test
	public void testCobolParserSimpleFieldArray() throws IOException {

		String sourceFile = "array.cbl";
		String entityName = org.openlegacy.utils.FileUtils.fileWithoutAnyExtension(sourceFile);
		String source = IOUtils.toString(getClass().getResource(sourceFile));
		FieldTypeDefinition fieldTypeDefinition;
		RpcEntityDefinition rpcEntityDefinition = openlegacyCobolParser.parse(source, entityName);
		Assert.assertNotNull(rpcEntityDefinition);

		Map<String, RpcFieldDefinition> fieldDefinitions = rpcEntityDefinition.getFieldsDefinitions();
		Assert.assertEquals(1, fieldDefinitions.size());
		RpcFieldDefinition fieldDefinition = fieldDefinitions.get("FIELD");
		Assert.assertNotNull(fieldDefinition);
		Assert.assertEquals(new Integer(10), fieldDefinition.getLength());
		Assert.assertEquals(List.class, fieldDefinition.getJavaType());
		fieldTypeDefinition = fieldDefinition.getFieldTypeDefinition();
		Assert.assertEquals(SimpleRpcListFieldTypeDefinition.class, fieldTypeDefinition.getClass());
		SimpleRpcListFieldTypeDefinition simpleRpcListFieldTypeDefinition = (SimpleRpcListFieldTypeDefinition)fieldTypeDefinition;
		Assert.assertEquals(5, simpleRpcListFieldTypeDefinition.getCount());
		Assert.assertEquals(SimpleTextFieldTypeDefinition.class,
				simpleRpcListFieldTypeDefinition.getItemFieldTypeDefinition().getClass());
		Assert.assertEquals(String.class, simpleRpcListFieldTypeDefinition.getItemJavaType());
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
	// @Test

}
