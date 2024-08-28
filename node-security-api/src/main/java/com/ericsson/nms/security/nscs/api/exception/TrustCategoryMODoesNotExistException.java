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
 * This exception will be thrown if node doens't have TrustCategory MO associated to it.
 * 
 * @author xramdag
 * 
 */
public class TrustCategoryMODoesNotExistException extends NscsServiceException {

    private static final long serialVersionUID = -5008901978388502673L;

    public TrustCategoryMODoesNotExistException() {
        super(NscsErrorCodes.TRUST_CATEGORY_MO_DOES_NOT_EXIST);
        setSuggestedSolution(NscsErrorCodes.ISSUE_CERT_FOR_TRUST_CATEGORY_MO);
    }

    public TrustCategoryMODoesNotExistException(final String message) {
        super(formatMessage(NscsErrorCodes.TRUST_CATEGORY_MO_DOES_NOT_EXIST, message));
    }

    public TrustCategoryMODoesNotExistException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.TRUST_CATEGORY_MO_DOES_NOT_EXIST, message), cause);
    }

    public TrustCategoryMODoesNotExistException(final Throwable cause) {
        super(NscsErrorCodes.TRUST_CATEGORY_MO_DOES_NOT_EXIST, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.TRUST_CATEGORY_MO_DOES_NOT_EXISTS
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.TRUST_CATEGORY_MO_DOES_NOT_EXISTS;
    }

}
