package org.openlegacy.designtime.rpc.generators.jt400;

import org.junit.Test;

import javax.xml.bind.JAXBException;

public class PcmlToApiTest {

	@Test
	public void testPcmlToApi() throws JAXBException {
		PcmlToApiConverter converter = new PcmlToApiConverter();

		converter.generateApi(getClass().getResourceAsStream("qsyrusri.pcml"));
	}
}
