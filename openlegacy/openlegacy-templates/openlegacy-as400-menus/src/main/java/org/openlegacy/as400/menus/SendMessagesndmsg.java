package org.openlegacy.as400.menus;

import org.openlegacy.annotations.screen.*;
import org.openlegacy.terminal.actions.TerminalActions;
import org.openlegacy.terminal.actions.TerminalAction.AdditionalKey;

@ScreenEntity()
@ScreenIdentifiers(identifiers = { 
				@Identifier(row = 1, column = 20, value = "           Send Message (SNDMSG)           "), 
				@Identifier(row = 5, column = 2, value = "Message text . . . . . . . . . ."), 
				@Identifier(row = 12, column = 2, value = "To user profile  . . . . . . . .") 
				})
@ScreenActions(actions = { 
				@Action(action = TerminalActions.F3.class, displayName = "Exit", alias = "exit"), 
				@Action(action = TerminalActions.F4.class, displayName = "Prompt", alias = "prompt"), 
				@Action(action = TerminalActions.F5.class, displayName = "Refresh", alias = "refresh"), 
				@Action(action = TerminalActions.F10.class, displayName = "Additional parameters", alias = "additionalParameters"), 
				@Action(action = TerminalActions.F12.class, displayName = "Cancel", alias = "cancel") 
				})
public class SendMessagesndmsg {
    
	
	@ScreenField(row = 5, column = 37, endColumn= 80 ,labelColumn= 2 ,editable= true ,displayName = "Message text")
    private String messageText;
	
	@ScreenField(row = 12, column = 37, endColumn= 46 ,labelColumn= 2 ,editable= true ,displayName = "To user profile")
    private String toUserProfile;

    


 
}
