package org.openlegacy.rpc.definitions;

import org.openlegacy.definitions.PartEntityDefinition;

public interface RpcPartEntityDefinition extends PartEntityDefinition<RpcFieldDefinition> {

	public String getOriginalName();

	public int getOrder();

}
