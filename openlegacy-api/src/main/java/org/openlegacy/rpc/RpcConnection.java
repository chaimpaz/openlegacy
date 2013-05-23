package org.openlegacy.rpc;

public interface RpcConnection {

	Object getDelegate();

	boolean isConnected();

	void disconnect();

	RpcResult invoke(RpcInvokeAction rpcInvokeAction);
}
