package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.rpc.definitions.RpcEntityDefinition;

public interface ParseResults {

	int getErrorsCount();

	String getError(int i);

	int getWarningCount();

	String getWarning(int i);

	void getEntityDefinition(RpcEntityDefinition rpcEntityDefinition);

}
