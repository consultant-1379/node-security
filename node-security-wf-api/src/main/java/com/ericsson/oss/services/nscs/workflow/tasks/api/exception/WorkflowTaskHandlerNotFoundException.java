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
 * Thrown by the WorkflowTaskService implementation when no task handler can be determined by a given
 * {@link com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType} Created by emaynes on 16/06/2014.
 */
public class WorkflowTaskHandlerNotFoundException extends WorkflowTaskException {

    private static final long serialVersionUID = 3212018893617774266L;

    public WorkflowTaskHandlerNotFoundException() {
    }

    public WorkflowTaskHandlerNotFoundException(final String message) {
        super(message);
    }

    public WorkflowTaskHandlerNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WorkflowTaskHandlerNotFoundException(final Throwable cause) {
        super(cause);
    }

    public WorkflowTaskHandlerNotFoundException(final String message, final Throwable cause, final boolean enableSuppression,
                                                final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
