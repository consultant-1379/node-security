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
 * This exception is thrown when the given collection name does not exist in the system or no node names resolved..
 * 
 * @author xpradks
 */
public class InvalidCollectionNameException extends NscsServiceException {

    private static final long serialVersionUID = 584321412342737729L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.PLEASE_SPECIFY_A_VALID_COLLECTION_NAME);
        }
    }

    public InvalidCollectionNameException() {
        super(NscsErrorCodes.INVALID_COLLECTION_NAME);
    }

    public InvalidCollectionNameException(final List<String> invalidNames) {
        super(String.format(NscsErrorCodes.INVALID_COLLECTION_NAME, invalidNames.toString()));
    }

    public InvalidCollectionNameException(final String message, final List<String> invalidNames) {
        super(String.format(message, invalidNames.toString()));
    }

    public InvalidCollectionNameException(final String message, final Set<String> invalidNames) {
        super(String.format(message, invalidNames.toString()));
    }

    public InvalidCollectionNameException(final List<String> invalidNames, final Throwable cause) {
        super(String.format(NscsErrorCodes.INVALID_COLLECTION_NAME, invalidNames.toString()), cause);

    }
    public InvalidCollectionNameException(final Throwable cause) {
        super(NscsErrorCodes.INVALID_COLLECTION_NAME, cause);
    }
    /**
     * @return ErrorType.INVALID_COLLECTION_NAME_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_COLLECTION_NAME_ERROR;
    }
}
