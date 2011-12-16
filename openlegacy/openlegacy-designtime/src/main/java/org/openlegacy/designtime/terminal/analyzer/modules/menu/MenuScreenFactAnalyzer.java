package org.openlegacy.designtime.terminal.analyzer.modules.menu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.designtime.terminal.analyzer.ScreenFact;
import org.openlegacy.designtime.terminal.analyzer.ScreenFactAnalyzer;
import org.openlegacy.designtime.terminal.analyzer.support.ScreenEntityDefinitionsBuilderUtils;
import org.openlegacy.designtime.terminal.model.ScreenEntityDesigntimeDefinition;
import org.openlegacy.modules.menu.Menu;
import org.openlegacy.terminal.TerminalSnapshot;
import org.openlegacy.terminal.definitions.ScreenFieldDefinition;
import org.openlegacy.utils.ClassUtils;

public class MenuScreenFactAnalyzer implements ScreenFactAnalyzer {

	private final static Log logger = LogFactory.getLog(MenuScreenFactAnalyzer.class);

	public void analyze(ScreenEntityDesigntimeDefinition screenEntityDefinition, ScreenFact screenFact) {

		MenuScreenFact menuScreenFact = (MenuScreenFact)screenFact;

		screenEntityDefinition.getReferredClasses().add(ClassUtils.getImportDeclaration(Menu.MenuEntity.class));
		screenEntityDefinition.setType(Menu.MenuEntity.class);
		TerminalSnapshot snapshot = screenEntityDefinition.getSnapshot();

		ScreenFieldDefinition fieldDefinition = ScreenEntityDefinitionsBuilderUtils.addField(screenEntityDefinition,
				menuScreenFact.getSelectionField(), Menu.SELECTION_LABEL);
		if (fieldDefinition == null) {
			logger.warn("Menu selection field not added to screen entity");
			return;
		}

		ScreenEntityDefinitionsBuilderUtils.defineFieldType(screenEntityDefinition, fieldDefinition, Menu.MenuSelectionField.class);

		for (MenuItemFact menuItem : menuScreenFact.getMenuItems()) {
			snapshot.getFields().remove(menuItem.getCodeField());
			snapshot.getFields().remove(menuItem.getCaptionField());
		}

	}

}
