// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package apps.inventory.screens;

import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import apps.inventory.screens.WarehouseTypes.WarehouseTypeRow;

privileged @SuppressWarnings("unused") aspect WarehouseTypeRowTable_Aspect {
    
    declare @type: WarehouseTypeRow : @Component;
	declare @type: WarehouseTypeRow : @Scope("prototype");
    
	
	
	
    
    public String WarehouseTypeRow.getAction(){
    	return this.action;
    }
    
    public void WarehouseTypeRow.setAction(String action){
    	this.action = action;
    }

    public String WarehouseTypeRow.getWarehouseType(){
    	return this.warehouseType;
    }
    

    public String WarehouseTypeRow.getWarehouseTypeDescription(){
    	return this.warehouseTypeDescription;
    }
    


    public int WarehouseTypeRow.hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
    }

    public boolean WarehouseTypeRow.equals(Object other){
    	// TODO exclude terminal fields
		return EqualsBuilder.reflectionEquals(this,other);
    }
}