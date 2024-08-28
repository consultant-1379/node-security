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
 * Thrown by WorkflowTaskService implementation when an unexpected error has happened.
 * 
 * @author emaynes
 */
public class UnexpectedErrorException extends WorkflowTaskException {

    private static final long serialVersionUID = 208562023345661942L;

    public UnexpectedErrorException() {
    }

    public UnexpectedErrorException(final String message) {
        super(message);
    }

    public UnexpectedErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new UnexpectedErrorException with the default message of message 'An unexpected error has occurred'
     * 
     * @param cause
     */
    public UnexpectedErrorException(final Throwable cause) {
        super("An unexpected error has occurred", cause);
    }

    public UnexpectedErrorException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
