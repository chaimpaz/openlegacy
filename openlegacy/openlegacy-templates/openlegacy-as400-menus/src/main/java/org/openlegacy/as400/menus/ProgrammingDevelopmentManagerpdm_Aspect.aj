// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.openlegacy.as400.menus;

import java.util.*;
import org.openlegacy.terminal.ScreenEntity;

privileged @SuppressWarnings("unused") aspect ProgrammingDevelopmentManagerpdm_Aspect {

    declare parents: ProgrammingDevelopmentManagerpdm implements ScreenEntity;
    private String ProgrammingDevelopmentManagerpdm.focusField;
    
	

    

    public SpecifyLibrariesToWorkWith ProgrammingDevelopmentManagerpdm.getSpecifyLibrariesToWorkWith(){
    	return this.specifyLibrariesToWorkWith;
    }
    




    public String ProgrammingDevelopmentManagerpdm.getFocusField(){
    	return focusField;
    }
    public void ProgrammingDevelopmentManagerpdm.setFocusField(String focusField){
    	this.focusField = focusField;
    }
    
}
