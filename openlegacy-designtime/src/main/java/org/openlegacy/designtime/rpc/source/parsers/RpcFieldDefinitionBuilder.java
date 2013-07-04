package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.FieldType.General;
import org.openlegacy.rpc.definitions.RpcFieldDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcFieldDefinition;
import org.openlegacy.utils.StringUtil;

public class RpcFieldDefinitionBuilder {

	private static FieldFormatterFactory fieldFormaterFactory = null;

	public RpcFieldDefinitionBuilder(FieldFormatterFactory fieldFormaterFactory) {
		RpcFieldDefinitionBuilder.fieldFormaterFactory = fieldFormaterFactory;
	}

	public RpcFieldDefinition getRpcFieldDefinition(String fieldName, String Vareclartion) {
		FieldFormatter fieldFormater = fieldFormaterFactory.getObject(Vareclartion);

		String javaFieldName = StringUtil.toJavaFieldName(fieldName);
		SimpleRpcFieldDefinition rpcFieldDefinition = new SimpleRpcFieldDefinition(javaFieldName, General.class);
		rpcFieldDefinition.setOriginalName(fieldName);

		rpcFieldDefinition.setLength((int)fieldFormater.getLength());
		rpcFieldDefinition.setJavaType(fieldFormater.getJavaType());
		rpcFieldDefinition.setFieldTypeDefinition(fieldFormater.getType());
		return rpcFieldDefinition;
	}

}
