package org.openlegacy.designtime.rpc.generators;

import freemarker.template.TemplateException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlegacy.designtime.rpc.model.support.SimpleRpcEntityDesigntimeDefinition;
import org.openlegacy.designtime.rpc.source.parsers.OpenlegacyCobolParser;
import org.openlegacy.test.utils.AssertUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import japa.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

@ContextConfiguration("RpcJavaGeneratorTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RpcEntityCodeGeneratorTest {

	@Inject
	private OpenlegacyCobolParser openlegacyCobolParser;

	@Inject
	private RpcJavaGenerator rpcJavaGenerator;

	@Test
	public void testGenerateSimpleField() throws IOException, TemplateException, ParseException {

		testGenerate("simpleField.cbl", "simpleField.java.expected");
	}

	@Test
	public void testCobolParserSimpleStructure() throws IOException, TemplateException, ParseException {
		testGenerate("as400sample.cbl", "as400sample.java.expected");
	}

	@Test
	public void testGenerateList() throws IOException, TemplateException, ParseException {

		testGenerate("arraySample.cbl", "arraySample.java.expected");
	}

	@Test
	public void testGenerteTree() throws IOException, TemplateException, ParseException {
		testGenerate("sampprog_expand.cbl", "sampprog_expand.java.expected");
	}

	private void testGenerate(String sourceFile, String expectJava) throws IOException, TemplateException, ParseException {

		String source = IOUtils.toString(getClass().getResource(sourceFile));
		String entityName = org.openlegacy.utils.FileUtils.fileWithoutAnyExtension(sourceFile);

		SimpleRpcEntityDesigntimeDefinition rpcEntityDesigntimeDefinition = (SimpleRpcEntityDesigntimeDefinition)openlegacyCobolParser.parse(source);

		rpcEntityDesigntimeDefinition.setPackageName("test.com");
		rpcEntityDesigntimeDefinition.setEntityName(entityName);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		rpcJavaGenerator.generate(rpcEntityDesigntimeDefinition, baos);

		// System.err.println(baos);
		byte[] expectedBytes = IOUtils.toByteArray(getClass().getResourceAsStream(expectJava));

		AssertUtils.assertContent(expectedBytes, baos.toByteArray());
	}
}
