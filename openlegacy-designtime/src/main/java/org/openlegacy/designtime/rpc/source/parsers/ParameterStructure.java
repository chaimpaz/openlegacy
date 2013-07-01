package org.openlegacy.designtime.rpc.source.parsers;

import java.util.List;

public interface ParameterStructure {

	public String getVariableDeclartion();

	public int getLevel();

	public String getFieldName();

	public List<ParameterStructure> getSubFieldsList();

	public boolean isSimple();

}