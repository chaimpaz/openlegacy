// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.someorg.examples.screens;

import org.openlegacy.terminal.ScreenEntity;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

privileged aspect WarehouseDetails_Aspect {
    
    declare @type: WarehouseDetails : @Component;
	declare @type: WarehouseDetails : @Scope("prototype");
    

    declare parents: WarehouseDetails implements ScreenEntity;
    
    


    
}
