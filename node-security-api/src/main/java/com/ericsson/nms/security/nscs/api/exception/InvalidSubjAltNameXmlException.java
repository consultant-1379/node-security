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
public class InvalidSubjAltNameXmlException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT);
		}
	}

	/**
	 * Constructs a new InvalidSubjAltNameXmlException with REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID
	 * as its detail message
	 */
	public InvalidSubjAltNameXmlException() {
		super(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID +
				NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT);
	}

	/**
	 * Constructs a new InvalidSubjAltNameXmlException with REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public InvalidSubjAltNameXmlException(final String message) {
		super(formatMessage(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID +
				NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT, message));
	}

	/**
	 * Constructs a new InvalidSubjAltNameXmlException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidSubjAltNameXmlException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID +
				NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT, message),
				cause);
	}

	/**
	 * Constructs a new InvalidSubjAltNameXmlException exception with the
	 * specified cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidSubjAltNameXmlException(final Throwable cause) {
		super(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID +
				NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT, cause);
	}

	/**
	 * @return ErrorType.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID;
	}

}
