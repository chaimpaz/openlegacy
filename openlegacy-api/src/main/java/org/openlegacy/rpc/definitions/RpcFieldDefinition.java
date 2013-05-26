package org.openlegacy.rpc.definitions;

import org.openlegacy.annotations.rpc.Direction;
import org.openlegacy.definitions.FieldDefinition;

public interface RpcFieldDefinition extends FieldDefinition {

	Direction getDirection();

	int getLength();
}
