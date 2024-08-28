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
package com.ericsson.oss.services.nscs.workflow.tasks.api.exception;

/**
 * Thrown by workflow task handlers when the provided node FDN is invalid.
 * 
 * @author emaynes
 */
public class InvalidNodeException extends WorkflowTaskException {

    private static final long serialVersionUID = 1194130157601874182L;

    public InvalidNodeException() {
    }

    public InvalidNodeException(final String message) {
        super(message);
    }

    public InvalidNodeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidNodeException(final Throwable cause) {
        super(cause);
    }

    public InvalidNodeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
