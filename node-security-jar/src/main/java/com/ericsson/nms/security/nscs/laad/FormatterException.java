/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad;

/**
 * Exception thrown when there is an error during LAAD file generation
 * @author enatbol
 */
public class FormatterException extends Exception {
    private static final long serialVersionUID = 1L;

    FormatterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    FormatterException(final String message) {
        super(message);
    }
}
