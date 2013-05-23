package org.openlegacy.rpc.support;

import org.openlegacy.SessionProperties;
import org.openlegacy.exceptions.EntityNotFoundException;
import org.openlegacy.rpc.RpcEntity;
import org.openlegacy.rpc.RpcSession;
import org.openlegacy.support.AbstractSession;

public class DefaultRpcSession extends AbstractSession implements RpcSession {

	public Object getDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getEntity(Class<T> entityClass, Object... keys) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getEntity(String entityName, Object... keys) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	public SessionProperties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public <R extends RpcEntity> R getEntity(R rpcEntity) {
		// TODO Auto-generated method stub
		return null;
	}

}
