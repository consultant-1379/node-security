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
public class InvalidEntityProfileNameDefaultXmlException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT);
		}
	}

	/**
	 * Constructs a new InvalidEntityProfileNameDefaultXmlException with DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST
	 * as its detail message
	 */
	public InvalidEntityProfileNameDefaultXmlException() {
		super(NscsErrorCodes.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST);
	}

	/**
	 * Constructs a new InvalidEntityProfileNameDefaultXmlException with DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public InvalidEntityProfileNameDefaultXmlException(final String message) {
		super(formatMessage(NscsErrorCodes.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST, message));
	}

	/**
	 * Constructs a new InvalidEntityProfileNameDefaultXmlException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidEntityProfileNameDefaultXmlException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST, message),
				cause);
	}

	/**
	 * Constructs a new InvalidEntityProfileNameDefaultXmlException exception with the
	 * specified cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidEntityProfileNameDefaultXmlException(final Throwable cause) {
		super(NscsErrorCodes.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST, cause);
	}

	/**
	 * @return ErrorType.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST;
	}

}
