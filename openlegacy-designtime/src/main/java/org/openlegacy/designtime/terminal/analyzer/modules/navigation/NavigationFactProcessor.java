package org.openlegacy.designtime.terminal.analyzer.modules.navigation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.designtime.terminal.analyzer.ScreenFact;
import org.openlegacy.designtime.terminal.analyzer.ScreenFactProcessor;
import org.openlegacy.designtime.terminal.model.ScreenEntityDesigntimeDefinition;
import org.openlegacy.modules.login.Login.LoginEntity;
import org.openlegacy.modules.menu.Menu.MenuEntity;
import org.openlegacy.terminal.TerminalActionMapper;
import org.openlegacy.terminal.TerminalField;
import org.openlegacy.terminal.TerminalSnapshot;
import org.openlegacy.terminal.actions.CombinedTerminalAction;
import org.openlegacy.terminal.actions.TerminalAction;
import org.openlegacy.terminal.actions.TerminalAction.AdditionalKey;
import org.openlegacy.terminal.actions.TerminalActions.ENTER;
import org.openlegacy.terminal.definitions.FieldAssignDefinition;
import org.openlegacy.terminal.definitions.ScreenEntityDefinition;
import org.openlegacy.terminal.definitions.ScreenFieldDefinition;
import org.openlegacy.terminal.definitions.SimpleFieldAssignDefinition;
import org.openlegacy.terminal.utils.FieldsQuery;
import org.openlegacy.terminal.utils.FieldsQuery.FieldsCriteria;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * Analyze a navigation fact compound of the previous snapshot, and it's definitions
 * 
 */
public class NavigationFactProcessor implements ScreenFactProcessor {

	@Inject
	private TerminalActionMapper terminalActionMapper;

	private final static Log logger = LogFactory.getLog(NavigationFactProcessor.class);

	public void process(ScreenEntityDesigntimeDefinition screenEntityDefinition, ScreenFact screenFact) {

		NavigationFact navigationFact = (NavigationFact)screenFact;

		ScreenEntityDesigntimeDefinition accessedFromScreenEntityDefinition = navigationFact.getAccessedFromScreenEntityDefinition();
		TerminalSnapshot accessedFromSnapshot = navigationFact.getAccessedFromSnapshot();

		screenEntityDefinition.setAccessedFromSnapshot(accessedFromSnapshot);
		screenEntityDefinition.setAccessedFromScreenDefinition(accessedFromScreenEntityDefinition);

		if (abortWhenHasPasswordFields(accessedFromSnapshot)) {
			return;
		}

		// do not define navigation from login screen
		if (accessedFromScreenEntityDefinition.getType() == LoginEntity.class) {
			return;
		}

		ScreenNavigationDesignTimeDefinition navigationDefinition = new ScreenNavigationDesignTimeDefinition();
		screenEntityDefinition.setNavigationDefinition(navigationDefinition);

		navigationDefinition.setAccessedFromEntityName(accessedFromScreenEntityDefinition.getEntityName());

		TerminalAction action = terminalActionMapper.getAction(navigationFact.getAccessedFromSnapshot().getCommand());
		if (action instanceof CombinedTerminalAction) {
			CombinedTerminalAction combinedAction = (CombinedTerminalAction)action;
			// add action definition only if it's not ENTER
			if (combinedAction.getTerminalAction() != ENTER.class || combinedAction.getAdditionalKey() != AdditionalKey.NONE) {
				navigationDefinition.setTerminalAction(action);
			}
		}

		// build assigned fields for menu screens only
		if (accessedFromScreenEntityDefinition.getType() == MenuEntity.class) {
			buildAssignedFields(screenEntityDefinition, navigationDefinition, accessedFromScreenEntityDefinition,
					accessedFromSnapshot);
		}
	}

	private static boolean abortWhenHasPasswordFields(TerminalSnapshot accessedFromSnapshot) {
		List<TerminalField> passwordFields = FieldsQuery.queryFields(accessedFromSnapshot, new FieldsCriteria() {

			public boolean match(TerminalField terminalField) {
				return terminalField.isPassword();
			}
		});

		if (passwordFields.size() > 0) {
			return true;
		}
		return false;
	}

	private static void buildAssignedFields(ScreenEntityDesigntimeDefinition screenEntityDefinition,
			ScreenNavigationDesignTimeDefinition navigationDefinition,
			ScreenEntityDesigntimeDefinition accessedFromScreenEntityDefinition, TerminalSnapshot accessedFromSnapshot) {
		List<TerminalField> modifiedFields = FieldsQuery.queryFields(accessedFromSnapshot,
				FieldsQuery.ModifiedFieldsCriteria.instance());
		for (TerminalField terminalField : modifiedFields) {
			// look for field name on the access-from entity definition
			ScreenFieldDefinition fieldDefinition = getFieldDefinitionByPosition(accessedFromScreenEntityDefinition,
					terminalField);
			if (fieldDefinition == null) {
				logger.warn(MessageFormat.format(
						"Field {0} was modified, and wasn''t found on screen definition {1}. Required for declaring screen navigation for {2}",
						terminalField, accessedFromScreenEntityDefinition.getEntityName(), screenEntityDefinition.getEntityName()));
				// clear the assigned fields to avoid problematic navigation by the generated code
				navigationDefinition.getAssignedFields().clear();
				continue;
			}
			FieldAssignDefinition assignFieldDefinition = new SimpleFieldAssignDefinition(fieldDefinition.getName(),
					terminalField.getValue());
			navigationDefinition.getAssignedFields().add(assignFieldDefinition);
		}
	}

	private static ScreenFieldDefinition getFieldDefinitionByPosition(ScreenEntityDefinition accessedFromScreenEntityDefinition,
			TerminalField terminalField) {
		Collection<ScreenFieldDefinition> fieldDefinitions = accessedFromScreenEntityDefinition.getFieldsDefinitions().values();
		for (ScreenFieldDefinition screenFieldDefinition : fieldDefinitions) {
			if (screenFieldDefinition.getPosition().equals(terminalField.getPosition())) {
				return screenFieldDefinition;
			}
		}
		return null;
	}

	public boolean accept(ScreenFact screenFact) {
		return (screenFact instanceof NavigationFact);
	}

}