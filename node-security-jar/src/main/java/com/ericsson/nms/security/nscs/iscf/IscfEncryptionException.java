/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.iscf;

/**
 *
 * @author ealemca
 */
public class IscfEncryptionException extends Exception {

	private static final long serialVersionUID = 5935916771886436201L;

	/**
     * Creates a new instance of <code>ISCFEncryptionException</code> without
     * detail message.
     */
    public IscfEncryptionException() {
    }

    /**
     * Constructs an instance of <code>ISCFEncryptionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public IscfEncryptionException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ISCFEncryptionException</code> with the
     * specified detail message and the underlying cause.
     *
     * @param msg the detail message.
     * @param cause
     */
    public IscfEncryptionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
