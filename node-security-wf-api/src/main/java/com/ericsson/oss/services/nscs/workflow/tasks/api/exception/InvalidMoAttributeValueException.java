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
public class InvalidMoAttributeValueException extends WorkflowTaskException {

    private static final long serialVersionUID = 1194130157601874182L;

    public InvalidMoAttributeValueException() {
    }

    public InvalidMoAttributeValueException(final String nodeName, final String attribute, final String invalidValue) {
        super(String.format("Value '%s' is invalid for attribute '%s' at node [%s].", invalidValue, attribute, nodeName));
    }

    public InvalidMoAttributeValueException(final String nodeName, final String attribute, final String invalidValue, final String expected) {
        super(String.format("Value '%s' is invalid for attribute '%s' at node [%s]. Expected value(s): %s", invalidValue, attribute, nodeName,
                expected));
    }

    public InvalidMoAttributeValueException(final String message) {
        super(message);
    }

    public InvalidMoAttributeValueException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidMoAttributeValueException(final Throwable cause) {
        super(cause);
    }

    public InvalidMoAttributeValueException(final String message, final Throwable cause, final boolean enableSuppression,
                                            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
