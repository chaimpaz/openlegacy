package org.openlegacy.providers.jt400;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400ZonedDecimal;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;

import org.openlegacy.annotations.rpc.Direction;
import org.openlegacy.rpc.RpcConnection;
import org.openlegacy.rpc.RpcField;
import org.openlegacy.rpc.RpcInvocationException;
import org.openlegacy.rpc.RpcInvokeAction;
import org.openlegacy.rpc.RpcResult;
import org.openlegacy.rpc.support.SimpleRpcResult;

import java.util.ArrayList;
import java.util.List;

public class Jt400RpcConnection implements RpcConnection {

	private AS400 as400Session;

	public Jt400RpcConnection(AS400 as400Session) {
		this.as400Session = as400Session;
	}

	public Object getDelegate() {
		return as400Session;
	}

	public void disconnect() {
		if (as400Session != null) {
			as400Session.disconnectAllServices();
		}
	}

	public boolean isConnected() {
		return as400Session != null;
	}

	public RpcResult invoke(RpcInvokeAction rpcInvokeAction) {
		ProgramCall program = new ProgramCall(as400Session);
		try {
			// Initialize the name of the program to run.
			String programName = rpcInvokeAction.getRpcName();

			List<RpcField> fields = rpcInvokeAction.getRpcFields();
			List<ProgramParameter> programParameters = new ArrayList<ProgramParameter>();

			for (RpcField rpcField : fields) {
				AS400DataType as400Field = initAs400DataType(rpcField, Direction.INPUT);
				if (as400Field == null) {
					programParameters.add(new ProgramParameter(rpcField.getLength()));
				} else {
					programParameters.add(new ProgramParameter(as400Field.toBytes(rpcField.getValue())));
				}

			}
			program.setProgram(programName, programParameters.toArray(new ProgramParameter[programParameters.size()]));
			// Run the program.
			if (program.run() != true) {
				StringBuilder sb = new StringBuilder();
				// Show the messages.
				AS400Message[] messagelist = program.getMessageList();
				for (int i = 0; i < messagelist.length; ++i) {
					sb.append(messagelist[i]);
				}
				throw (new RpcInvocationException(sb.toString()));
			}
			// Else no error, get output data.
			else {
				SimpleRpcResult rpcResult = new SimpleRpcResult();
				rpcResult.setRpcFields(fields);
				int count = 0;
				for (RpcField field : fields) {
					if (field.getDirection() != Direction.INPUT) {
						AS400DataType as400DataType = initAs400DataType(field, Direction.OUTPUT);
						Object value = as400DataType.toObject(programParameters.get(count).getOutputData());
						field.setValue(value);
					}
					count++;
				}

				return rpcResult;
			}
		} catch (Exception e) {
			throw (new RpcInvocationException(e));
		}
	}

	private AS400DataType initAs400DataType(RpcField rpcField, Direction direction) {
		AS400DataType as400Field = null;
		if (rpcField.getType() == String.class) {
			if (rpcField.getDirection() == Direction.INPUT_OUTPUT || rpcField.getDirection() == direction) {
				as400Field = new AS400Text(rpcField.getLength(), as400Session);
			}
		} else if (rpcField.getType() == Integer.class) {
			if (rpcField.getDirection() == Direction.INPUT_OUTPUT || rpcField.getDirection() == direction) {
				as400Field = new AS400ZonedDecimal(rpcField.getLength(), 0);
			}
		}
		return as400Field;
	}

}
