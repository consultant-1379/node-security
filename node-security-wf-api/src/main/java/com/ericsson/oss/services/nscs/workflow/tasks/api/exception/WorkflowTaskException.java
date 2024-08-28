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

import javax.ejb.ApplicationException;

/**
 * Base exception for all workflow task services.
 * 
 * @author emaynes
 */
@ApplicationException(rollback = true)
public class WorkflowTaskException extends RuntimeException {

    private static final long serialVersionUID = 743162175204130768L;

    public WorkflowTaskException() {
    }

    public WorkflowTaskException(final String message) {
        super(message);
    }

    public WorkflowTaskException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WorkflowTaskException(final Throwable cause) {
        super(cause);
    }

    public WorkflowTaskException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
