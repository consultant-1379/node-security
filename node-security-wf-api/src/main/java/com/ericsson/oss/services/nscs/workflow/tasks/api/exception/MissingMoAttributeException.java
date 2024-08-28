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
 * Thrown by workflow task handlers when an attribute can't be found in the target node.
 * 
 * @author emaynes
 */
public class MissingMoAttributeException extends WorkflowTaskException {

    private static final long serialVersionUID = 1194130157601874182L;

    public MissingMoAttributeException() {
    }

    public MissingMoAttributeException(final String nodeName, final String moType, final String attribute) {
        super(String.format("Can't get value of %s.%s at node [%s].", moType, attribute, nodeName));
    }

    public MissingMoAttributeException(final String message) {
        super(message);
    }

    public MissingMoAttributeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MissingMoAttributeException(final Throwable cause) {
        super(cause);
    }

    public MissingMoAttributeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
