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
public class SubjAltNameTypeNotSupportedXmlException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT);
		}
	}

	/**
	 * Constructs a new SubjAltNameTypeNotSupportedXmlException with REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED
	 * as its detail message
	 */
	public SubjAltNameTypeNotSupportedXmlException() {
		super(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED);
	}

	/**
	 * Constructs a new SubjAltNameTypeNotSupportedXmlException with REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public SubjAltNameTypeNotSupportedXmlException(final String message) {
		super(formatMessage(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED, message));
	}

	/**
	 * Constructs a new SubjAltNameTypeNotSupportedXmlException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public SubjAltNameTypeNotSupportedXmlException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED, message),
				cause);
	}

	/**
	 * Constructs a new SubjAltNameTypeNotSupportedXmlException exception with the
	 * specified cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public SubjAltNameTypeNotSupportedXmlException(final Throwable cause) {
		super(NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED, cause);
	}

	/**
	 * @return ErrorType.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED;
	}

}
