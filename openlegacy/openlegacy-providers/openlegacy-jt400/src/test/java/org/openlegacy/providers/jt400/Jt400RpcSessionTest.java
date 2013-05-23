package org.openlegacy.providers.jt400;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlegacy.providers.jt400.mockup.DummyRpcEntity;
import org.openlegacy.rpc.RpcSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@ContextConfiguration("Jt400RpcSessionTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class Jt400RpcSessionTest {

	@Inject
	private RpcSession rpcSession;

	@Test
	public void testJt400RpcSession() {
		DummyRpcEntity dummyRpcEntity = new DummyRpcEntity();
		rpcSession.getEntity(dummyRpcEntity);
	}
}
