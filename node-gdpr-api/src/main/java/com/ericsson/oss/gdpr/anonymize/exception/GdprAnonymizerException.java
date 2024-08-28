/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.gdpr.anonymize.exception;

public class GdprAnonymizerException extends RuntimeException{
    private static final long serialVersionUID = -5844087027093618344L;

    public GdprAnonymizerException(final String message) {
        super(message);
    }

    public GdprAnonymizerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GdprAnonymizerException(final Throwable cause) {
        super(cause);
    }

}
