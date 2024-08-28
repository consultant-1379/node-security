/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
 * This exception will be thrown if node doens't have Security MO associated to it.
 * 
 * @author xlakdag
 * 
 */
public class SecurityMODoesNotExistException extends NscsServiceException {


	private static final long serialVersionUID = 8184096114959273993L;

	public SecurityMODoesNotExistException() {
        super(NscsErrorCodes.SECURITY_MO_DOES_NOT_EXIST);
        setSuggestedSolution(NscsErrorCodes.ISSUE_CERT_FOR_SECURITY_MO);
    }

    public SecurityMODoesNotExistException(final String message) {
        super(formatMessage(NscsErrorCodes.SECURITY_MO_DOES_NOT_EXIST, message));
    }

    public SecurityMODoesNotExistException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.SECURITY_MO_DOES_NOT_EXIST, message), cause);
    }

    public SecurityMODoesNotExistException(final Throwable cause) {
        super(NscsErrorCodes.SECURITY_MO_DOES_NOT_EXIST, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.TRUST_CATEGORY_MO_DOES_NOT_EXISTS
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.SECURITY_MO_DOES_NOT_EXISTS;
    }

}
