package org.openlegacy.designtime.rpc.source.parsers;

import java.util.List;

public interface ParameterStructure {

	public String getVariableDeclaration();

	public int getLevel();

	public String getFieldName();

	public int getOccurs();

	public List<ParameterStructure> getSubFields();

	public boolean isSimple();

}