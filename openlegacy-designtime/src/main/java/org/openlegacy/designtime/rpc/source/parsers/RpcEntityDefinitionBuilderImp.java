package org.openlegacy.designtime.rpc.source.parsers;

/**
 * Build an RpcEntityDefinition from list of parameters.
 * 
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.FieldType.General;
import org.openlegacy.rpc.definitions.RpcEntityDefinition;
import org.openlegacy.rpc.definitions.RpcFieldDefinition;
import org.openlegacy.rpc.definitions.RpcPartEntityDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcEntityDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcFieldDefinition;
import org.openlegacy.rpc.definitions.SimpleRpcPartEntityDefinition;
import org.openlegacy.utils.StringUtil;

import java.util.List;
import java.util.Map;

public class RpcEntityDefinitionBuilderImp implements RpcEntityDefinitionBuilder {

	private final static Log logger = LogFactory.getLog(RpcEntityDefinitionBuilderImp.class);

	private FieldInformationFactory fieldInformationFactory;

	public FieldInformationFactory getFieldInformationFactory() {
		return fieldInformationFactory;
	}

	public void setFieldInformationFactory(FieldInformationFactory fieldInformationFactory) {
		this.fieldInformationFactory = fieldInformationFactory;
	}

	private RpcFieldDefinition buildRpcFieldDefinition(ParameterStructure parameter) {

		FieldInformation fieldInformation = fieldInformationFactory.getObject(parameter.getVariableDeclaration(),
				parameter.getOccurs());

		String javaFieldName = StringUtil.toJavaFieldName(parameter.getFieldName());
		SimpleRpcFieldDefinition rpcFieldDefinition = new SimpleRpcFieldDefinition(javaFieldName, General.class);
		rpcFieldDefinition.setOriginalName(parameter.getFieldName());

		rpcFieldDefinition.setLength(fieldInformation.getLength());

		rpcFieldDefinition.setJavaType(fieldInformation.getJavaType());
		rpcFieldDefinition.setFieldTypeDefinition(fieldInformation.getType());
		return rpcFieldDefinition;
	}

	RpcPartEntityDefinition buildRpcPartDefinition(String name, List<ParameterStructure> partFieldList) {

		SimpleRpcPartEntityDefinition rpcPartEntityDef = new SimpleRpcPartEntityDefinition(null);
		rpcPartEntityDef.setPartName(StringUtil.toClassName(name));
		rpcPartEntityDef.setDisplayName(StringUtil.toDisplayName(name));
		rpcPartEntityDef.setOriginalName(name);
		Map<String, RpcFieldDefinition> rpcFieldsMap = rpcPartEntityDef.getFieldsDefinitions();
		Map<String, RpcPartEntityDefinition> rpcPartInnerParts = rpcPartEntityDef.getInnerPartsDefinitions();
		for (ParameterStructure partField : partFieldList) {
			if (partField.isSimple()) {
				rpcFieldsMap.put(partField.getFieldName(), buildRpcFieldDefinition(partField));
			} else {
				rpcPartInnerParts.put(partField.getFieldName(),
						buildRpcPartDefinition(partField.getFieldName(), partField.getSubFields()));
			}
		}
		return rpcPartEntityDef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.RpcEntityDefinitionBuilder#build(java.lang.String, java.util.List)
	 */
	public RpcEntityDefinition build(String entityName, List<ParameterStructure> paramtersNodes) {
		RpcEntityDefinition entityDefinition = new SimpleRpcEntityDefinition(entityName);

		for (int parameterIdx = 0; parameterIdx < paramtersNodes.size(); parameterIdx++) {
			ParameterStructure interfaceParmeter = paramtersNodes.get(parameterIdx);

			if (interfaceParmeter.isSimple()) {
				entityDefinition.getFieldsDefinitions().put(interfaceParmeter.getFieldName(),
						buildRpcFieldDefinition(interfaceParmeter));
			} else {

				entityDefinition.getPartsDefinitions().put(interfaceParmeter.getFieldName(),
						buildRpcPartDefinition(interfaceParmeter.getFieldName(), interfaceParmeter.getSubFields()));
			}
			logger.debug(interfaceParmeter.toString());
		}

		return entityDefinition;
	}

}
