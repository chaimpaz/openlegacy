package org.openlegacy.rpc.mock;

import org.openlegacy.rpc.RpcConnection;
import org.openlegacy.rpc.RpcInvokeAction;
import org.openlegacy.rpc.RpcResult;
import org.openlegacy.rpc.RpcSnapshot;

import java.util.List;

public class MockRpcConnection implements RpcConnection {

	private List<RpcSnapshot> snapshots;

	public MockRpcConnection(List<RpcSnapshot> rpcSnapshots) {
		snapshots = rpcSnapshots;
	}

	public Object getDelegate() {
		return this;
	}

	public boolean isConnected() {
		return true;
	}

	public void disconnect() {
		// do nothing
	}

	public RpcResult invoke(RpcInvokeAction rpcInvokeAction) {
		return snapshots.get(0).getRpcResult();
	}

}
