package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.rpc.definitions.RpcEntityDefinition;

import java.util.List;

/**
 * Build an RpcEntityDefinition from list of parameters.
 * 
 */

public interface RpcEntityDefinitionBuilder {

	RpcEntityDefinition build(List<ParameterStructure> paramtersNodes);

}