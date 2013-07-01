package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.definitions.FieldTypeDefinition;

public interface FieldFormatter {

	public double getLength();

	public Class<?> getJavaType();

	FieldTypeDefinition getType();
}