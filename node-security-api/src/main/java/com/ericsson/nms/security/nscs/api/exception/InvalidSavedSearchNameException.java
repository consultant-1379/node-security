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

import java.util.List;
import java.util.Set;

/**
 * This exception is thrown when the given saved search name does not exist in the system or no node names resolved.
 * 
 * @author xpradks
 */
public class InvalidSavedSearchNameException extends NscsServiceException {

    private static final long serialVersionUID = 3772828200017009019L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SAVED_SEARCH_NAME);
        }
    }

    public InvalidSavedSearchNameException() {
        super(NscsErrorCodes.INVALID_SAVED_SEARCH_NAME);
    }

    public InvalidSavedSearchNameException(final List<String> invalidNames) {
        super(String.format(NscsErrorCodes.INVALID_SAVED_SEARCH_NAME, invalidNames.toString()));
    }

    public InvalidSavedSearchNameException(final String message,final List<String> invalidNames) {
        super(String.format(message, invalidNames.toString()));
    }

    public InvalidSavedSearchNameException(final String message,final Set<String> invalidNames) {
        super(String.format(message, invalidNames.toString()));
    }

    public InvalidSavedSearchNameException(final List<String> invalidNames, final Throwable cause) {
        super(String.format(NscsErrorCodes.INVALID_SAVED_SEARCH_NAME, invalidNames.toString()), cause);

    }
    public InvalidSavedSearchNameException(final Throwable cause) {
        super(NscsErrorCodes.INVALID_SAVED_SEARCH_NAME, cause);
    }
    /**
     * @return ErrorType.INVALID_SAVED_SEARCH_NAME_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_SAVED_SEARCH_NAME_ERROR;
    }
}
