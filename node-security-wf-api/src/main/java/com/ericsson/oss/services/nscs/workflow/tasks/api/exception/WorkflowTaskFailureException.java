/*------------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2017
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
 * Exception thrown by workflow task handlers when a node failure occurs.
 *
 * @author emaborz
 *
 */
public class WorkflowTaskFailureException extends WorkflowTaskException {

    private static final long serialVersionUID = 4356794667048533813L;

    public WorkflowTaskFailureException() {
    }

    public WorkflowTaskFailureException(final String message) {
        super(message);
    }

    public WorkflowTaskFailureException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WorkflowTaskFailureException(final Throwable cause) {
        super(cause);
    }

    public WorkflowTaskFailureException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
