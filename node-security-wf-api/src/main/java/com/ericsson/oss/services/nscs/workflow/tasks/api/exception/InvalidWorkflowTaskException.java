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
 * Thrown by workflow task service implementation when the provided WorkflowTask
 * instance validation fail.
 * <p>Validation constraints are specified using beans validation annotation</p>
 * 
 * @author emaynes
 */
public class InvalidWorkflowTaskException extends WorkflowTaskException {

    private static final long serialVersionUID = 1194130157601874182L;

    public InvalidWorkflowTaskException() {
    }

    public InvalidWorkflowTaskException(final String message) {
        super(message);
    }

    public InvalidWorkflowTaskException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidWorkflowTaskException(final Throwable cause) {
        super(cause);
    }

    public InvalidWorkflowTaskException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
