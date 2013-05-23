package org.openlegacy.rpc.definitions;

import java.util.List;

import org.openlegacy.FieldType;
import org.openlegacy.definitions.FieldDefinition;
import org.openlegacy.definitions.support.AbstractEntityDefinition;

public class SimpleRpcEntityDefinition extends AbstractEntityDefinition<RpcFieldDefinition> implements RpcEntityDefinition {

	private String programPath;

	public SimpleRpcEntityDefinition(String entityName, Class<?> entityClass) {
		super(entityName, entityClass);
	}

	public String getProgramPath() {
		return programPath;
	}

	public void setProgramPath(String programPath) {
		this.programPath = programPath;
	}

	public List<? extends FieldDefinition> getFieldDefinitions(
			Class<? extends FieldType> fieldType) {
		return null;
	}
}
