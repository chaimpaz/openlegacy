package org.openlegacy.designtime.rpc.source.parsers;

public interface FieldFormatterFactory {

	public abstract FieldFormatter getObject(String flatePicture);

}