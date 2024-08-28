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
public class AlgorithmKeySizeNotSupportedXmlException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT);
		}
	}

	/**
	 * Constructs a new AlgorithmKeySizeNotSupportedXmlException with REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE
	 * as its detail message
	 */
	public AlgorithmKeySizeNotSupportedXmlException() {
		super(NscsErrorCodes.REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE);
	}

	/**
	 * Constructs a new AlgorithmKeySizeNotSupportedXmlException with REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public AlgorithmKeySizeNotSupportedXmlException(final String message) {
		super(formatMessage(NscsErrorCodes.REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE, message));
	}

	/**
	 * Constructs a new AlgorithmKeySizeNotSupportedXmlException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public AlgorithmKeySizeNotSupportedXmlException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE, message),
				cause);
	}

	/**
	 * Constructs a new AlgorithmKeySizeNotSupportedXmlException exception with the
	 * specified cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public AlgorithmKeySizeNotSupportedXmlException(final Throwable cause) {
		super(NscsErrorCodes.REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE, cause);
	}

	/**
	 * @return ErrorType.REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE;
	}

}
