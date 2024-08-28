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
 * Exception for Workflow related errors
 *
 * @author ealemca
 */
@ApplicationException(rollback = false)
public class WorkflowHandlerException extends RuntimeException {

    private static final long serialVersionUID = -1L;

    public WorkflowHandlerException() {
        
    }

    public WorkflowHandlerException(final String message) {
        super(message);
    }

    public WorkflowHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WorkflowHandlerException(final Throwable cause) {
        super(cause);
    }

}
