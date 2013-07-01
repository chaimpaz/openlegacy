package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.rpc.definitions.RpcFieldDefinition;
import org.openlegacy.rpc.definitions.RpcPartEntityDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcPartEntityDefinition;
import org.openlegacy.utils.StringUtil;

import java.util.List;
import java.util.Map;

public class RpcPartEntityDefinitionBuilder {

	private static RpcFieldDefinitionBuilder fieldDefinitionBuilder = null;

	public RpcPartEntityDefinitionBuilder(RpcFieldDefinitionBuilder fieldDefinitionBuilder) {
		RpcPartEntityDefinitionBuilder.fieldDefinitionBuilder = fieldDefinitionBuilder;

	}

	RpcPartEntityDefinition getRpcPartDefinition(String name, List<ParameterStructure> partFieldList) {

		SimpleRpcPartEntityDefinition rpcPartEntityDef = new SimpleRpcPartEntityDefinition(null);
		rpcPartEntityDef.setPartName(StringUtil.toClassName(name));
		rpcPartEntityDef.setDisplayName(StringUtil.toDisplayName(name));
		rpcPartEntityDef.setOriginalName(name);
		Map<String, RpcFieldDefinition> rpcFieldsMap = rpcPartEntityDef.getFieldsDefinitions();
		Map<String, RpcPartEntityDefinition> rpcPartInnerParts = rpcPartEntityDef.getInnerPartsDefinitions();
		for (ParameterStructure partField : partFieldList) {
			if (partField.isSimple()) {
				rpcFieldsMap.put(partField.getFieldName(),
						fieldDefinitionBuilder.getRpcFieldDefinition(partField.getFieldName(), partField.getVariableDeclaration()));
			} else {
				rpcPartInnerParts.put(partField.getFieldName(),
						getRpcPartDefinition(partField.getFieldName(), partField.getSubFields()));
			}
		}
		return rpcPartEntityDef;
	}

}
