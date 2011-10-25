package org.openlegacy.recognizers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlegacy.AbstractTest;
import org.openlegacy.adapter.terminal.SendKeyActions;
import org.openlegacy.recognizers.pattern.MainMenu;
import org.openlegacy.recognizers.pattern.SignOn;
import org.openlegacy.terminal.TerminalSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PatternBasedScreensRecognizerTest extends AbstractTest {

	@Autowired
	TerminalSession terminalSession;

	@Test
	public void testPattern() throws IOException {
		SignOn signOn = terminalSession.getEntity(SignOn.class);
		Assert.assertNotNull(signOn);
		terminalSession.doAction(SendKeyActions.ENTER, null, null);
		MainMenu mainMenu = terminalSession.getEntity(MainMenu.class);
		Assert.assertNotNull(mainMenu);
	}

}
