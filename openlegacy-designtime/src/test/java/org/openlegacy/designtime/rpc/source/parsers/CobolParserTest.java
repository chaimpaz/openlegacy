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
import org.openlegacy.rpc.definitions.RpcPartEntityDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcListFieldTypeDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcPartEntityDefinition;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
	public void testCobolParserArrays() throws IOException {

		String sourceFile = "array.cbl";
		String entityName = org.openlegacy.utils.FileUtils.fileWithoutAnyExtension(sourceFile);
		String source = IOUtils.toString(getClass().getResource(sourceFile));
		FieldTypeDefinition fieldTypeDefinition;
		RpcEntityDefinition rpcEntityDefinition = openlegacyCobolParser.parse(source, entityName);
		RpcFieldDefinition fieldDefinition;
		RpcFieldDefinition childField;

		Assert.assertNotNull(rpcEntityDefinition);
		Map<String, RpcFieldDefinition> fieldDefinitions = rpcEntityDefinition.getFieldsDefinitions();
		Assert.assertEquals(2, fieldDefinitions.size());

		// Simple
		fieldDefinition = fieldDefinitions.get("FIELD");
		Assert.assertNotNull(fieldDefinition);
		Assert.assertEquals(new Integer(10), fieldDefinition.getLength());
		Assert.assertEquals(List.class, fieldDefinition.getJavaType());
		fieldTypeDefinition = fieldDefinition.getFieldTypeDefinition();
		Assert.assertEquals(SimpleRpcListFieldTypeDefinition.class, fieldTypeDefinition.getClass());
		@SuppressWarnings("unchecked")
		SimpleRpcListFieldTypeDefinition<FieldTypeDefinition> simpleRpcListFieldTypeDefinition = (SimpleRpcListFieldTypeDefinition<FieldTypeDefinition>)fieldTypeDefinition;
		Assert.assertEquals(5, simpleRpcListFieldTypeDefinition.getCount());
		Assert.assertEquals(SimpleTextFieldTypeDefinition.class,
				simpleRpcListFieldTypeDefinition.getItemTypeDefinition().getClass());
		Assert.assertEquals(String.class, simpleRpcListFieldTypeDefinition.getItemJavaType());

		// Array of part
		fieldDefinition = fieldDefinitions.get("PART");
		Assert.assertNotNull(fieldDefinition);
		Assert.assertEquals(List.class, fieldDefinition.getJavaType());
		fieldTypeDefinition = fieldDefinition.getFieldTypeDefinition();
		@SuppressWarnings("unchecked")
		SimpleRpcListFieldTypeDefinition<RpcPartEntityDefinition> arrayRpcListFieldTypeDefinition = (SimpleRpcListFieldTypeDefinition<RpcPartEntityDefinition>)fieldTypeDefinition;
		Assert.assertEquals(3, arrayRpcListFieldTypeDefinition.getCount());
		Assert.assertEquals(SimpleRpcPartEntityDefinition.class,
				arrayRpcListFieldTypeDefinition.getItemTypeDefinition().getClass());
		RpcPartEntityDefinition rpcPartEntityDefinition = arrayRpcListFieldTypeDefinition.getItemTypeDefinition();
		Assert.assertEquals("PART", rpcPartEntityDefinition.getPartName());
		Map<String, RpcFieldDefinition> partFieldDefinitions = rpcPartEntityDefinition.getFieldsDefinitions();
		Assert.assertEquals(2, partFieldDefinitions.size());

		// First Child
		childField = partFieldDefinitions.get("CHILD1");
		Assert.assertNotNull(childField);
		Assert.assertEquals(String.class, childField.getJavaType());
		Assert.assertEquals(new Integer(10), childField.getLength());

		// second Child
		childField = partFieldDefinitions.get("CHILD2");
		Assert.assertNotNull(childField);
		Assert.assertEquals(List.class, childField.getJavaType());
		fieldTypeDefinition = childField.getFieldTypeDefinition();

		Assert.assertEquals(new Integer(2), childField.getLength());

		@SuppressWarnings("unchecked")
		SimpleRpcListFieldTypeDefinition<FieldTypeDefinition> internalListDefention = (SimpleRpcListFieldTypeDefinition<FieldTypeDefinition>)fieldTypeDefinition;
		Assert.assertEquals(6, internalListDefention.getCount());
		Assert.assertEquals(2, internalListDefention.getFieldLength());
		Assert.assertEquals(SimpleExtendedNumericFieldTypeDefinition.class,
				internalListDefention.getItemTypeDefinition().getClass());
		Assert.assertEquals(Integer.class, internalListDefention.getItemJavaType());

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

	@Test
	public void testPreProcessJustCopy() throws IOException {

		Map<String, InputStream> streamMap = new HashMap<String, InputStream>();

		streamMap.put("sampcpy1.cpy", getClass().getResourceAsStream("sampcpy1.cpy"));
		streamMap.put("sampcpy2.cpy", getClass().getResourceAsStream("sampcpy2.cpy"));

		openlegacyCobolParser.preProcess(streamMap);
		String copyBookPath = openlegacyCobolParser.getCopyBookPath();
		File copyBookDir = new File(copyBookPath);
		String fileList[] = copyBookDir.list();
		Assert.assertArrayEquals(fileList, streamMap.keySet().toArray());

	}
}
