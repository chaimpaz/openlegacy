// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.openlegacy.as400.menus;

import java.util.*;
import org.openlegacy.terminal.ScreenEntity;

privileged @SuppressWarnings("unused") aspect DisplayJobStatusAttributes_Aspect {

    declare parents: DisplayJobStatusAttributes implements ScreenEntity;
    private String DisplayJobStatusAttributes.focusField;
    
	

	

	

	

	

	

	

	

	

	

	

	

	

	

	

	

	

    

    public String DisplayJobStatusAttributes.getCurrentUserProfile(){
    	return this.currentUserProfile;
    }
    



    public String DisplayJobStatusAttributes.getDate(){
    	return this.date;
    }
    



    public String DisplayJobStatusAttributes.getDate1(){
    	return this.date1;
    }
    



    public String DisplayJobStatusAttributes.getJob(){
    	return this.job;
    }
    



    public String DisplayJobStatusAttributes.getJobUserIdentity(){
    	return this.jobUserIdentity;
    }
    



    public Integer DisplayJobStatusAttributes.getNumber(){
    	return this.number;
    }
    



    public Integer DisplayJobStatusAttributes.getProgramReturnCode(){
    	return this.programReturnCode;
    }
    



    public String DisplayJobStatusAttributes.getSetBy(){
    	return this.setBy;
    }
    



    public String DisplayJobStatusAttributes.getSpecialEnvironment(){
    	return this.specialEnvironment;
    }
    



    public String DisplayJobStatusAttributes.getStatusOfJob(){
    	return this.statusOfJob;
    }
    



    public String DisplayJobStatusAttributes.getSubsystem(){
    	return this.subsystem;
    }
    



    public Integer DisplayJobStatusAttributes.getSubsystemPoolId(){
    	return this.subsystemPoolId;
    }
    



    public String DisplayJobStatusAttributes.getSystem(){
    	return this.system;
    }
    



    public String DisplayJobStatusAttributes.getTime(){
    	return this.time;
    }
    



    public String DisplayJobStatusAttributes.getTime1(){
    	return this.time1;
    }
    



    public String DisplayJobStatusAttributes.getTypeOfJob(){
    	return this.typeOfJob;
    }
    



    public String DisplayJobStatusAttributes.getUser(){
    	return this.user;
    }
    




    public String DisplayJobStatusAttributes.getFocusField(){
    	return focusField;
    }
    public void DisplayJobStatusAttributes.setFocusField(String focusField){
    	this.focusField = focusField;
    }
    
}
