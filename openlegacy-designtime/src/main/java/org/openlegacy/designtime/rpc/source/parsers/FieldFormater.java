package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.definitions.FieldTypeDefinition;

public interface FieldFormater {

	public double getLength();

	public Class<?> getJavaType();

	FieldTypeDefinition getType();
}