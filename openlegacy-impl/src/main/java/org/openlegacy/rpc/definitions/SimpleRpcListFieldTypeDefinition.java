package org.openlegacy.rpc.definitions;

import org.openlegacy.definitions.support.AbstractListFieldTypeDefinition;

public class SimpleRpcListFieldTypeDefinition<F> extends AbstractListFieldTypeDefinition implements RpcListFieldTypeDefinition<F> {

	private F itemTypeDefinition;
	private Class<?> itemJavaType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.rpc.definitions.RpcListFieldTypeDefinition#getItemFieldTypeDefinition()
	 */
	public F getItemTypeDefinition() {
		return itemTypeDefinition;
	}

	public SimpleRpcListFieldTypeDefinition(int fieldLength, int count, F itemTypeDefinition, Class<?> itemJavaType) {
		super(fieldLength, count);
		this.itemTypeDefinition = itemTypeDefinition;
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
