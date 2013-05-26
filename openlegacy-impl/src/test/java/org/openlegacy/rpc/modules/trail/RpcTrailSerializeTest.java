package org.openlegacy.rpc.modules.trail;

import org.junit.Test;
import org.openlegacy.rpc.persistance.RpcPersistedSnapshot;
import org.openlegacy.rpc.support.SimpleRpcField;
import org.openlegacy.rpc.support.SimpleRpcInvokeAction;
import org.openlegacy.rpc.support.SimpleRpcResult;
import org.openlegacy.utils.XmlSerializationUtil;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBException;

public class RpcTrailSerializeTest {

	@Test
	public void testTrail() throws JAXBException {
		RpcPersistedTrail rpcTrail = new RpcPersistedTrail();
		RpcPersistedSnapshot rpcSnapshot = new RpcPersistedSnapshot();

		SimpleRpcInvokeAction rpcInvokeAction = new SimpleRpcInvokeAction("test");
		SimpleRpcField rpcField = new SimpleRpcField();
		rpcField.setLength(10);
		rpcField.setValue("hello");

		rpcInvokeAction.getRpcFields().add(rpcField);
		rpcInvokeAction.setRpcPath("rpctest");

		rpcSnapshot.setRpcInvokeAction(rpcInvokeAction);

		SimpleRpcResult result = new SimpleRpcResult();
		result.setRpcFields(rpcInvokeAction.getRpcFields());

		rpcSnapshot.setRpcResult(result);
		rpcTrail.getSnapshots().add(rpcSnapshot);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmlSerializationUtil.serialize(RpcPersistedTrail.class, rpcTrail, baos);
		// System.out.println(new String(baos.toByteArray()));
	}
}
