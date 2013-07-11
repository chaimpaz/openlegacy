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
import org.openlegacy.rpc.definitions.SimpleRpcListFieldTypeDefinition;
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

	private RpcFieldDefinition buildRpcFieldDefinition(ParameterStructure parameter, int order) {

		FieldInformation fieldInformation = fieldInformationFactory.getObject(parameter.getVariableDeclaration(),
				parameter.getOccurs());

		String javaFieldName = StringUtil.toJavaFieldName(parameter.getFieldName());
		SimpleRpcFieldDefinition rpcFieldDefinition = new SimpleRpcFieldDefinition(javaFieldName, General.class);
		rpcFieldDefinition.setOriginalName(parameter.getFieldName());
		rpcFieldDefinition.setOrder(order);
		rpcFieldDefinition.setLength(fieldInformation.getLength());

		rpcFieldDefinition.setJavaType(fieldInformation.getJavaType());
		rpcFieldDefinition.setFieldTypeDefinition(fieldInformation.getType());

		return rpcFieldDefinition;
	}

	@SuppressWarnings("static-method")
	private RpcFieldDefinition buildRpcArrayPartDefinition(String name, int occurs, RpcPartEntityDefinition itemDefinition,
			int order) {
		String javaFieldName = StringUtil.toJavaFieldName(name);
		SimpleRpcFieldDefinition rpcFieldDefinition = new SimpleRpcFieldDefinition(javaFieldName, General.class);
		rpcFieldDefinition.setOrder(order);
		rpcFieldDefinition.setJavaType(List.class);

		rpcFieldDefinition.setFieldTypeDefinition(new SimpleRpcListFieldTypeDefinition<RpcPartEntityDefinition>(0, occurs,
				itemDefinition, null));

		return rpcFieldDefinition;
	}

	RpcPartEntityDefinition buildRpcPartDefinition(String name, List<ParameterStructure> partFieldList, int order) {

		SimpleRpcPartEntityDefinition rpcPartEntityDefinition = new SimpleRpcPartEntityDefinition(null);
		rpcPartEntityDefinition.setPartName(StringUtil.toClassName(name));
		rpcPartEntityDefinition.setDisplayName(StringUtil.toDisplayName(name));
		rpcPartEntityDefinition.setOriginalName(name);
		rpcPartEntityDefinition.setOrder(order);
		Map<String, RpcFieldDefinition> rpcFieldsMap = rpcPartEntityDefinition.getFieldsDefinitions();
		Map<String, RpcPartEntityDefinition> rpcPartInnerParts = rpcPartEntityDefinition.getInnerPartsDefinitions();
		for (int internalOrder = 0; internalOrder < partFieldList.size(); internalOrder++) {
			ParameterStructure partField = partFieldList.get(internalOrder);
			String partFieldName = partField.getFieldName();
			if (partField.isSimple()) {
				rpcFieldsMap.put(partFieldName, buildRpcFieldDefinition(partField, internalOrder));
			} else {
				RpcPartEntityDefinition subPartEntityDefinition = buildRpcPartDefinition(partFieldName, partField.getSubFields(),
						internalOrder);
				if (partField.getOccurs() == 1) {

					rpcPartInnerParts.put(partFieldName, subPartEntityDefinition);
				} else {
					rpcFieldsMap.put(
							partFieldName,
							buildRpcArrayPartDefinition(partFieldName, partField.getOccurs(), subPartEntityDefinition,
									internalOrder));
				}
			}
		}
		return rpcPartEntityDefinition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.RpcEntityDefinitionBuilder#build(java.lang.String, java.util.List)
	 */
	public RpcEntityDefinition build(String entityName, List<ParameterStructure> paramtersNodes) {
		RpcEntityDefinition entityDefinition = new SimpleRpcEntityDefinition(entityName);

		for (int parameterOrder = 0; parameterOrder < paramtersNodes.size(); parameterOrder++) {
			ParameterStructure interfaceParmeter = paramtersNodes.get(parameterOrder);
			String fieldName = interfaceParmeter.getFieldName();
			if (interfaceParmeter.isSimple()) {

				entityDefinition.getFieldsDefinitions().put(fieldName, buildRpcFieldDefinition(interfaceParmeter, parameterOrder));
			} else {

				RpcPartEntityDefinition rpcPartEntityDefinition = buildRpcPartDefinition(fieldName,
						interfaceParmeter.getSubFields(), parameterOrder);
				if (interfaceParmeter.getOccurs() == 1) {
					entityDefinition.getPartsDefinitions().put(fieldName, rpcPartEntityDefinition);
				} else {
					entityDefinition.getFieldsDefinitions().put(
							fieldName,
							buildRpcArrayPartDefinition(fieldName, interfaceParmeter.getOccurs(), rpcPartEntityDefinition,
									parameterOrder));
				}
			}
			logger.debug(interfaceParmeter.toString());
		}

		return entityDefinition;
	}
}
