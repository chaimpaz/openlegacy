package org.openlegacy.rpc.definitions;

import org.openlegacy.definitions.FieldTypeDefinition;
import org.openlegacy.definitions.support.AbstractListFieldTypeDefinition;

public class SimpleRpcListFieldTypeDefinition extends AbstractListFieldTypeDefinition implements RpcListFieldTypeDefinition {

	FieldTypeDefinition itemFieldTypeDefinition;
	Class<?> itemJavaType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.rpc.definitions.RpcListFieldTypeDefinition#getItemFieldTypeDefinition()
	 */
	public FieldTypeDefinition getItemFieldTypeDefinition() {
		return itemFieldTypeDefinition;
	}

	public SimpleRpcListFieldTypeDefinition(int fieldLength, int count, FieldTypeDefinition itemFieldTypeDefinition,
			Class<?> itemJavaType) {
		super(fieldLength, count);
		this.itemFieldTypeDefinition = itemFieldTypeDefinition;
		this.itemJavaType = itemJavaType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.rpc.definitions.RpcListFieldTypeDefinition#getItemJavaType()
	 */
	public Class<?> getItemJavaType() {
		return itemJavaType;
	}

}
