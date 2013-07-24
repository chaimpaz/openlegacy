package org.openlegacy.designtime.rpc.model.support;

import org.openlegacy.rpc.definitions.SimpleRpcEntityDefinition;

public class SimpleRpcEntityDesigntimeDefinition extends SimpleRpcEntityDefinition {

	private String packageName;

	public SimpleRpcEntityDesigntimeDefinition() {
		super();
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
