package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.definitions.FieldTypeDefinition;
import org.openlegacy.definitions.support.SimpleTextFieldTypeDefinition;

public class CobolTextFormater implements FieldFormater {

	private double length;

	CobolTextFormater(String flatPic) {
		length = flatPic.length();
	}

	public double getLength() {

		return length;
	}

	public Class<?> getJavaType() {

		return String.class;
	}

	public FieldTypeDefinition getType() {
		return new SimpleTextFieldTypeDefinition();
	}
}
