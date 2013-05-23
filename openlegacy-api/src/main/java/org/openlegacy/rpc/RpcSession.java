package org.openlegacy.rpc;

import org.openlegacy.Session;

public interface RpcSession extends Session {

	<R extends RpcEntity> R getEntity(R rpcEntity);
}
