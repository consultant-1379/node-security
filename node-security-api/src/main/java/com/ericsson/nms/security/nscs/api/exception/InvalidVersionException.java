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

/**
 * Exception for Version
 */
public class InvalidVersionException extends RuntimeException {

	private static final long serialVersionUID = 4359323486747835106L;

	/**
     * Creates a new instance of exception
     * without any detail message.
	 */
	public InvalidVersionException() {
	}

	/**
     * Constructs an instance of exception
     * with the specified detail message.
     *
	 * @param message The detail message
	 */
	public InvalidVersionException(String message) {
		super(message);
	}

	/**
     * Constructs an instance of exception
     * with the cause.
     *
     * @param cause The cause
	 */
	public InvalidVersionException(Throwable cause) {
		super(cause);
	}

	/**
     * Constructs an instance of exception
     * with the specified detail message and the cause.
     *
     * @param message The detail message
     * @param cause The cause
	 */
	public InvalidVersionException(String message, Throwable cause) {
		super(message, cause);
	}

}
