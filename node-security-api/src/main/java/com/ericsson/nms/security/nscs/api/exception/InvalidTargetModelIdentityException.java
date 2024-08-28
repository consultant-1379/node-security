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
package com.ericsson.nms.security.nscs.api.exception;

public class InvalidTargetModelIdentityException extends RuntimeException {

	private static final long serialVersionUID = 5162335908869392792L;

	/**
     * Creates a new instance of exception
     * without any detail message.
	 */
	public InvalidTargetModelIdentityException() {
	}

	/**
     * Constructs an instance of exception
     * with the specified detail message.
     *
	 * @param message The detail message
	 */
	public InvalidTargetModelIdentityException(String message) {
		super(message);
	}

}
