package org.openlegacy.providers.jt400;

import com.ibm.as400.access.AS400;

import org.openlegacy.rpc.RpcConnection;
import org.openlegacy.rpc.RpcConnectionFactory;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

public class Jt400RpcConnectionFactory implements RpcConnectionFactory {

	@Inject
	private ApplicationContext applicationContext;

	public RpcConnection getConnection() {
		AS400 as400 = applicationContext.getBean(AS400.class);
		return new Jt400RpcConnection(as400);
	}

	public void disconnect(RpcConnection rpcConnection) {
		rpcConnection.disconnect();

	}

}
