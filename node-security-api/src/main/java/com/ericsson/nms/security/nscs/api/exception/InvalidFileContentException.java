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
 * This exception will be thrown if character encoding of XML is not UTF-8.
 * 
 * @author emehsau
 * 
 */
public class InvalidFileContentException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT_ENCODING);
		}
	}

	/**
	 * Constructs a new InvalidFileContentException with INVALID_FILE_CONTENT as
	 * its detail message
	 */
	public InvalidFileContentException() {
		super(NscsErrorCodes.INVALID_FILE_CONTENT);
	}

	/**
	 * Constructs a new InvalidFileContentException with INVALID_FILE_CONTENT
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public InvalidFileContentException(final String message) {
		super(formatMessage(NscsErrorCodes.INVALID_FILE_CONTENT, message));
	}

	/**
	 * Constructs a new InvalidFileContentException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */

	public InvalidFileContentException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.INVALID_FILE_CONTENT, message),
				cause);
	}

	/**
	 * Constructs a new InvalidFileContentException exception with the specified
	 * cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidFileContentException(final Throwable cause) {
		super(NscsErrorCodes.INVALID_FILE_CONTENT, cause);
	}

	/**
	 * @return ErrorType.INVALID_FILE_CONTENT
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.INVALID_FILE_CONTENT;
	}

}
