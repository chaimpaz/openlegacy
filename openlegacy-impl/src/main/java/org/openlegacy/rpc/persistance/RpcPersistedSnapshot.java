package org.openlegacy.rpc.persistance;

import org.openlegacy.rpc.RpcInvokeAction;
import org.openlegacy.rpc.RpcResult;
import org.openlegacy.rpc.RpcSnapshot;
import org.openlegacy.rpc.support.SimpleRpcInvokeAction;
import org.openlegacy.rpc.support.SimpleRpcResult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class RpcPersistedSnapshot implements RpcSnapshot {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "invoke", type = SimpleRpcInvokeAction.class)
	private RpcInvokeAction rpcInvokeAction;

	@XmlElement(name = "result", type = SimpleRpcResult.class)
	private RpcResult rpcResult;

	public RpcInvokeAction getRpcInvokeAction() {
		return rpcInvokeAction;
	}

	public void setRpcInvokeAction(RpcInvokeAction rpcInvokeAction) {
		this.rpcInvokeAction = rpcInvokeAction;
	}

	public RpcResult getRpcResult() {
		return rpcResult;
	}

	public void setRpcResult(RpcResult rpcResult) {
		this.rpcResult = rpcResult;
	}

}
