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

public class InvalidNodeTypeException extends RuntimeException {

	private static final long serialVersionUID = -8008299008955693201L;

	/**
     * Creates a new instance of exception
     * without any detail message.
	 */
	public InvalidNodeTypeException() {
	}

	/**
     * Constructs an instance of exception
     * with the specified detail message.
     *
	 * @param message The detail message
	 */
	public InvalidNodeTypeException(String message) {
		super(message);
	}

	/**
     * Constructs an instance of exception
     * with the cause.
     *
     * @param cause The cause
	 */
	public InvalidNodeTypeException(Throwable cause) {
		super(cause);
	}

	/**
     * Constructs an instance of exception
     * with the specified detail message and the cause.
     *
     * @param message The detail message
     * @param cause The cause
	 */
	public InvalidNodeTypeException(String message, Throwable cause) {
		super(message, cause);
	}

}
