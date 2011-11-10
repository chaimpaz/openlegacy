package org.openlegacy.terminal.support;

import org.openlegacy.CustomHostAction;
import org.openlegacy.HostAction;
import org.openlegacy.exceptions.HostEntityNotFoundException;
import org.openlegacy.modules.HostSessionModule;
import org.openlegacy.support.AbstractHostSession;
import org.openlegacy.terminal.ScreenEntity;
import org.openlegacy.terminal.TerminalConnectionListener;
import org.openlegacy.terminal.TerminalScreen;
import org.openlegacy.terminal.TerminalSession;
import org.openlegacy.terminal.exceptions.ScreenEntityNotAccessibleException;
import org.openlegacy.terminal.exceptions.ScreenEntityNotFoundException;
import org.openlegacy.terminal.spi.ScreenEntityBinder;
import org.openlegacy.terminal.spi.SessionNavigator;
import org.openlegacy.terminal.spi.TerminalSendAction;

import java.text.MessageFormat;
import java.util.Collection;

import javax.inject.Inject;

/**
 * A default session class exposes screenEntity building and sending
 * 
 * 
 */
public class DefaultTerminalSession extends AbstractHostSession implements TerminalSession {

	@Inject
	private ScreenEntityBinder screenEntityBinder;

	private TerminalConnectionDelegator terminalConnection;

	private ScreenEntity entity;

	@Inject
	private SessionNavigator sessionNavigator;

	@SuppressWarnings("unchecked")
	public <T> T getEntity(Class<T> screenEntityClass) throws HostEntityNotFoundException {

		// check if the entity matched the cached entity
		if (entity == null || entity.getClass() != screenEntityClass) {
			sessionNavigator.navigate(this, screenEntityClass);

			TerminalScreen hostScreen = getSnapshot();

			entity = (ScreenEntity)screenEntityBinder.buildScreenEntity(screenEntityClass, hostScreen);
			if (entity == null) {
				throw (new ScreenEntityNotFoundException(MessageFormat.format("Screen entity class {0} not matched",
						screenEntityClass)));
			}
		}
		return (T)entity;
	}

	@SuppressWarnings("unchecked")
	public <S extends ScreenEntity> S getEntity() {
		if (entity == null) {
			TerminalScreen hostScreen = getSnapshot();
			entity = screenEntityBinder.buildScreenEntity(hostScreen);
			if (entity == null) {
				throw (new ScreenEntityNotFoundException("Current screen is has not matched a screen entity is the registry"));
			}
		}
		return (S)entity;
	}

	public Object doAction(HostAction hostAction, ScreenEntity screenEntity) {
		return doActionInner(hostAction, screenEntity);
	}

	public <S extends ScreenEntity, T extends ScreenEntity> T doAction(HostAction hostAction, S screenEntity,
			Class<T> expectedEntity) {
		try {
			@SuppressWarnings("unchecked")
			T object = (T)doActionInner(hostAction, screenEntity);
			return object;
		} catch (ClassCastException e) {
			throw (new ScreenEntityNotAccessibleException(e));
		}

	}

	private Object doActionInner(HostAction hostAction, ScreenEntity screenEntity) {

		entity = null;

		if (hostAction instanceof CustomHostAction) {
			((CustomHostAction)hostAction).perform(this);
		} else {
			TerminalSendAction terminalSendAction = screenEntityBinder.buildSendFields(getSnapshot(), hostAction, screenEntity);

			notifyModulesBeforeSend(terminalSendAction);
			terminalConnection.doAction(terminalSendAction);
			notifyModulesAfterSend();
		}

		return getEntity();
	}

	private void notifyModulesBeforeSend(TerminalSendAction terminalSendAction) {
		Collection<? extends HostSessionModule> modulesList = getSessionModules().getModules();
		for (HostSessionModule sessionModule : modulesList) {
			if (sessionModule instanceof TerminalConnectionListener) {
				((TerminalConnectionListener)sessionModule).beforeSendAction(terminalConnection, terminalSendAction);
			}
		}
	}

	private void notifyModulesAfterSend() {
		Collection<? extends HostSessionModule> modulesList = getSessionModules().getModules();
		for (HostSessionModule sessionModule : modulesList) {
			if (sessionModule instanceof TerminalConnectionListener) {
				((TerminalConnectionListener)sessionModule).afterSendAction(terminalConnection);
			}
		}
	}

	public TerminalScreen getSnapshot() {
		return terminalConnection.getSnapshot();
	}

	public Object getDelegate() {
		return terminalConnection.getDelegate();
	}

	public void setTerminalConnection(TerminalConnectionDelegator terminalConnection) {
		this.terminalConnection = terminalConnection;
	}

	public void disconnect() {
		terminalConnection.disconnect();
	}

	public boolean isConnected() {
		return terminalConnection.isConnected();
	}

}
