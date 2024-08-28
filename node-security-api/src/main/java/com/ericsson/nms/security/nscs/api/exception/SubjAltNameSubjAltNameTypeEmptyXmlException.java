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
public class SubjAltNameSubjAltNameTypeEmptyXmlException extends NscsServiceException {

	private static final long serialVersionUID = 148350927299058365L;

	{
		{
			setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT);
		}
	}

	/**
	 * Constructs a new SubjAltNameSubjAltNameTypeEmptyXmlException with SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY
	 * as its detail message
	 */
	public SubjAltNameSubjAltNameTypeEmptyXmlException() {
		super(NscsErrorCodes.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY);
	}

	/**
	 * Constructs a new SubjAltNameSubjAltNameTypeEmptyXmlException with SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY
	 * appended by user message as its detail message
	 * 
	 * @param message
	 *            : User message
	 */
	public SubjAltNameSubjAltNameTypeEmptyXmlException(final String message) {
		super(formatMessage(NscsErrorCodes.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY, message));
	}

	/**
	 * Constructs a new SubjAltNameSubjAltNameTypeEmptyXmlException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            : User message
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public SubjAltNameSubjAltNameTypeEmptyXmlException(final String message,
			final Throwable cause) {
		super(formatMessage(NscsErrorCodes.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY, message),
				cause);
	}

	/**
	 * Constructs a new AlgorithmKeySizeEmptyXmlException exception with the
	 * specified cause
	 * 
	 * @param cause
	 *            : {@link Throwable} cause of exception
	 */
	public SubjAltNameSubjAltNameTypeEmptyXmlException(final Throwable cause) {
		super(NscsErrorCodes.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY, cause);
	}

	/**
	 * @return ErrorType.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY;
	}

}
