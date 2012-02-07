package org.openlegacy.providers.applinx;

import com.sabratec.applinx.common.runtime.GXScreenPosition;
import com.sabratec.util.GXPosition;

import org.openlegacy.terminal.TerminalPosition;
import org.openlegacy.terminal.support.SimpleTerminalPosition;

public class ApxPositionUtil {

	public static TerminalPosition toScreenPosition(GXPosition position) {
		return new SimpleTerminalPosition(position.getRow(), position.getColumn());
	}

	public static TerminalPosition toScreenPosition(GXScreenPosition position) {
		return new SimpleTerminalPosition(position.getRow(), position.getColumn());
	}

	public static GXPosition toPosition(TerminalPosition position) {
		return new GXPosition(position.getRow(), position.getColumn());
	}
}