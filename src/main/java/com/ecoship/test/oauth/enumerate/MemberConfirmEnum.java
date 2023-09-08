package com.ecoship.test.oauth.enumerate;

public enum MemberConfirmEnum {

	BEFORE_CONFIRM(false),
    OK_CONFIRM(true);

    boolean code;
    
    MemberConfirmEnum(boolean code) {
    	this.code = code;
    }
}
