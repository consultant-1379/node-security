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
 * This exception will be thrown if node attributes are invalid or missing for the specified command parameters.
 * 
 * @author enmadmin
 * 
 */
public class InvalidInputNodeListException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT);
		}
	}

	/**
	 * Constructs a new InvalidInputNodeListException with INVALID_INPUT_NODE_LIST_FOR_COMMAND
	 * as its detail message
	 */
	public InvalidInputNodeListException() {
		super(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND);
	}

	/**
	 * Constructs a new InvalidInputNodeListException with INVALID_INPUT_NODE_LIST_FOR_COMMAND
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public InvalidInputNodeListException(final String message) {
		super(formatMessage(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND, message));
	}

	/**
	 * Constructs a new InvalidInputNodeListException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidInputNodeListException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND, message),
				cause);
	}

	/**
	 * Constructs a new InvalidInputNodeListException exception with the
	 * specified cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidInputNodeListException(final Throwable cause) {
		super(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND, cause);
	}

	/**
	 * @return ErrorType.INVALID_INPUT_NODE_LIST_FOR_COMMAND
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.INVALID_INPUT_NODE_LIST_FOR_COMMAND;
	}

}
