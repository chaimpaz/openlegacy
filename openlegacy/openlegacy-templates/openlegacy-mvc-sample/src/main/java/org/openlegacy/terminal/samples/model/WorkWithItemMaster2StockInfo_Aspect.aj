// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.openlegacy.terminal.samples.model;

import org.openlegacy.terminal.samples.model.WorkWithItemMaster2.StockInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

privileged @SuppressWarnings("unused") aspect StockInfoTable_Aspect {
    
    declare @type: StockInfo : @Component;
	declare @type: StockInfo : @Scope("prototype");
    
	
	
    
    public String StockInfo.getCreatedDate(){
    	return this.createdDate;
    }

    public String StockInfo.getCreatedBy(){
    	return this.createdBy;
    }

    public String StockInfo.getAmendedDate(){
    	return this.amendedDate;
    }
    
}