// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.openlegacy.terminal.samples.model;

import java.util.*;
import org.openlegacy.terminal.ScreenEntity;

privileged @SuppressWarnings("unused") aspect Items_Aspect {

    declare parents: Items implements ScreenEntity;
    private String Items.focusField;
    
	

	

    

    public List<ItemsRecord> Items.getItemsRecords(){
    	return this.itemsRecords;
    }
    



    public String Items.getPositionTo(){
    	return this.positionTo;
    }
    
    public void Items.setPositionTo(String positionTo){
    	this.positionTo = positionTo;
    }




    public String Items.getFocusField(){
    	return focusField;
    }
    public void Items.setFocusField(String focusField){
    	this.focusField = focusField;
    }
    
}