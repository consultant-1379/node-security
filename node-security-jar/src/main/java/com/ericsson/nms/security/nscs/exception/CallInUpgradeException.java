/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.exception;

public class CallInUpgradeException extends RuntimeException{

	public String message;
	
	
	public CallInUpgradeException(String message) {
		this.message = message;
	}
	
	 @Override
	    public String getMessage(){
	        return message;
	    }
	
	
}
