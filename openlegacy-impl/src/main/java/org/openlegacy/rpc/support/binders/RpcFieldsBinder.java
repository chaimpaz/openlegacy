package org.openlegacy.rpc.support.binders;

import org.openlegacy.rpc.RpcEntityBinder;
import org.openlegacy.rpc.RpcField;
import org.openlegacy.rpc.RpcInvokeAction;
import org.openlegacy.rpc.RpcResult;
import org.openlegacy.rpc.definitions.RpcEntityDefinition;
import org.openlegacy.rpc.definitions.RpcFieldDefinition;
import org.openlegacy.rpc.services.RpcEntitiesRegistry;
import org.openlegacy.rpc.support.SimpleRpcField;
import org.openlegacy.rpc.utils.SimpleRpcPojoFieldAccessor;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

public class RpcFieldsBinder implements RpcEntityBinder {

	@Inject
	private RpcEntitiesRegistry rpcEntitiesRegistry;

	public void populateEntity(Object entity, RpcResult result) {
		RpcEntityDefinition rpcDefinition = rpcEntitiesRegistry.get(entity.getClass());

		SimpleRpcPojoFieldAccessor fieldAccesor = new SimpleRpcPojoFieldAccessor(entity);

		Collection<RpcFieldDefinition> fieldsDefinitions = rpcDefinition.getFieldsDefinitions().values();
		int index = 0;
		List<RpcField> rpcFields = result.getRpcFields();
		for (RpcFieldDefinition rpcFieldDefinition : fieldsDefinitions) {
			if (index >= rpcFields.size()) {
				break;
			}
			fieldAccesor.setFieldValue(rpcFieldDefinition.getName(), rpcFields.get(index).getValue());
			index++;
		}

	}

	public void populateAction(RpcInvokeAction sendAction, Object entity) {
		SimpleRpcPojoFieldAccessor fieldAccesor = new SimpleRpcPojoFieldAccessor(entity);

		RpcEntityDefinition rpcEntityDefinition = rpcEntitiesRegistry.get(entity.getClass());

		Collection<RpcFieldDefinition> fieldsDefinitions = rpcEntityDefinition.getFieldsDefinitions().values();
		for (RpcFieldDefinition rpcFieldDefinition : fieldsDefinitions) {
			Object value = fieldAccesor.getFieldValue(rpcFieldDefinition.getName());
			SimpleRpcField rpcField = new SimpleRpcField();
			rpcField.setValue(value);
			rpcField.setLength(rpcFieldDefinition.getLength());
			rpcField.setDirection(rpcFieldDefinition.getDirection());
			sendAction.getRpcFields().add(rpcField);
		}
	}

}
