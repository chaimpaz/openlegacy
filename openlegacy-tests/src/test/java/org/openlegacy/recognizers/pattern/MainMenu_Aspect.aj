// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.openlegacy.recognizers.pattern;

import org.openlegacy.terminal.TerminalField;
import org.openlegacy.terminal.TerminalScreen;
import org.springframework.stereotype.Component;

privileged aspect MainMenu_Aspect {
    
    declare @type: MainMenu : @Component;
    
    private TerminalScreen MainMenu.terminalScreen;
    
    private TerminalField MainMenu.companyField;
    private TerminalField MainMenu.selectionField;
    
    public TerminalScreen MainMenu.getTerminalScreen(){
		return terminalScreen;
    }
    
    public String MainMenu.getCompany(){
    	return this.company;
    }
    public void MainMenu.setCompany(String company){
    	this.company = company;
    }

    public TerminalField MainMenu.getCompanyField(){
    	return companyField;
    }

    public String MainMenu.getSelection(){
    	return this.company;
    }
    public void MainMenu.setSelection(String selection){
    	this.selection = selection;
    }

    public TerminalField MainMenu.getSelectionField(){
    	return selectionField;
    }
}
