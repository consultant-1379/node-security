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
 * This exception will be thrown if validation of XML with XSD fails.
 * 
 * @author emehsau
 * 
 */
public class InvalidInputXMLFileException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT);
		}
	}

	/**
	 * Constructs a new InvalidInputXMLFileException with INVALID_INPUT_XML_FILE
	 * as its detail message
	 */
	public InvalidInputXMLFileException() {
		super(NscsErrorCodes.INVALID_INPUT_XML_FILE);
	}

	/**
	 * Constructs a new InvalidInputXMLFileException with INVALID_INPUT_XML_FILE
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public InvalidInputXMLFileException(final String message) {
		super(formatMessage(NscsErrorCodes.INVALID_INPUT_XML_FILE, message));
	}

	/**
	 * Constructs a new InvalidInputXMLFileException with the given message
	 * appended by user message as its detail message
	 *
	 * @param message the message
	 * @param messageDescription the message description
	 */
	public InvalidInputXMLFileException(final String message, final String messageDescription) {
	        super(formatMessage(message, messageDescription));
	    }
	
	/**
	 * Constructs a new InvalidInputXMLFileException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidInputXMLFileException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.INVALID_INPUT_XML_FILE, message),
				cause);
	}

	/**
	 * Constructs a new InvalidInputXMLFileException exception with the
	 * specified cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public InvalidInputXMLFileException(final Throwable cause) {
		super(NscsErrorCodes.INVALID_INPUT_XML_FILE, cause);
	}

	/**
	 * @return ErrorType.INVALID_INPUT_XML_FILE
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.INVALID_INPUT_XML_FILE;
	}

}
