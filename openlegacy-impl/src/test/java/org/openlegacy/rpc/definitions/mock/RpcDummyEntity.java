package org.openlegacy.rpc.definitions.mock;

import org.openlegacy.annotations.rpc.Direction;
import org.openlegacy.annotations.rpc.RpcEntity;
import org.openlegacy.annotations.rpc.RpcField;

@RpcEntity("/dir/name")
public class RpcDummyEntity {

	@RpcField(direction = Direction.INPUT, length = 20)
	String firstName;

	@RpcField(direction = Direction.INPUT, length = 20)
	String lastName;

	@RpcField(direction = Direction.INPUT, length = 3)
	Integer age;

	@RpcField(direction = Direction.OUTPUT, length = 100)
	String message;
}
