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
 * Exception thrown when database is unavailable.
 *
 */

public class DatabaseUnavailableException extends NscsSystemException {

    private static final long serialVersionUID = 5610847139449735712L;

    public DatabaseUnavailableException() {
        super(NscsErrorCodes.DATABASE_UNAVAILABLE);
    }

    public DatabaseUnavailableException(final String message) {
        super(formatMessage(NscsErrorCodes.DATABASE_UNAVAILABLE, message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.DATABASE_UNAVAILABLE;
    }

}
