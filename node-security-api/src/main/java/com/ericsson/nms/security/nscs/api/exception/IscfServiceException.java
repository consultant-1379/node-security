/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.api.exception;

import javax.ejb.ApplicationException;

/**
 * Exception for ISCF services
 *
 * @author ealemca
 */
@ApplicationException(rollback = true)
public class IscfServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>IscfServiceException</code> without detail message.
     */
    public IscfServiceException() {
    }

    /**
     * Constructs an instance of <code>IscfServiceException</code> with the specified detail
     * message.
     *
     * @param msg the detail message
     */
    public IscfServiceException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>IscfServiceException</code> with the specified detail
     * message and the cause.
     *
     * @param msg the detail message
     * @param cause the cause
     */
    public IscfServiceException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs an instance of <code>IscfServiceException</code> with the cause.
     *
     * @param cause the cause
     */
    public IscfServiceException(final Throwable cause) {
        super(cause);
    }

}
