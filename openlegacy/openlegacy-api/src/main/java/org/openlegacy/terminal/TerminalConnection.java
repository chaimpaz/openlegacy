package org.openlegacy.terminal;

import org.openlegacy.terminal.spi.TerminalSendAction;

/**
 * Emulation vendors needs to implement this class
 */
public interface TerminalConnection {

	TerminalScreen getSnapshot();

	TerminalConnection doAction(TerminalSendAction terminalSendAction);

	Object getDelegate();
}
