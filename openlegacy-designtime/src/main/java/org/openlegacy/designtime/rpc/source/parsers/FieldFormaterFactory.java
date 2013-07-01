package org.openlegacy.designtime.rpc.source.parsers;

public interface FieldFormaterFactory {

	public abstract FieldFormater getObject(String flatePicture);

}