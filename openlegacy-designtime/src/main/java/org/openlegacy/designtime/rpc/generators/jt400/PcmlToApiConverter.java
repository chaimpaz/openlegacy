package org.openlegacy.designtime.rpc.generators.jt400;

import org.openlegacy.utils.XmlSerializationUtil;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

public class PcmlToApiConverter {

	public String generateApi(InputStream pcml) throws JAXBException {

		Pcml pcmlEntity = XmlSerializationUtil.deserialize(Pcml.class, pcml);

		// GenerateUtil.writeToFile()
		return null;

	}
}
