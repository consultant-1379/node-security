/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
 * <p>
 * Exception thrown by the command validation methods in case an unsupported TrustCategory type condition is detected.
 * </p>
 */
public class UnsupportedTrustCategoryTypeException extends NscsServiceException {

    private static final long serialVersionUID = -2383993651023849059L;

    public UnsupportedTrustCategoryTypeException() {
        super(NscsErrorCodes.UNSUPPORTED_TRUST_CATEGORY_TYPE);
        setSuggestedSolution(NscsErrorCodes.CHECK_OLH_FOR_SUPPORTED_TRUST_CATEGORY_TYPES);
    }

    /**
     * Gets the error type
     * 
     * @return ErrorType.UNSUPPORTED_TRUSTCATEGORY_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNSUPPORTED_TRUST_CATEGORY_TYPE;
    }

}
