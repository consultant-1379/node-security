/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.api.laad.ex;

/**
 * Exception thrown by LaadService implementation when a
 * Laad file installation or generation fails
 * @author enatbol
 */
public class LaadServiceException extends Exception {
    private static final long serialVersionUID = 1L;
    protected LaadServiceException.Error error;

    public enum Error {

        FAILED_TO_GENERATE_LAAD {
            @Override
            public String toString() {
                return "Failed to generate laad files!";
            }
        },
        FAILED_TO_STORE_LAAD {
            @Override
            public String toString() {
                return "Failed to store laad filess!";
            }
        }
    }

    public LaadServiceException.Error getError() {
        return error;
    }

    public LaadServiceException(final LaadServiceException.Error error, final Throwable cause) {
        super(cause);
        this.error = error;
    }

    public LaadServiceException(final LaadServiceException.Error error) {
        super();
        this.error = error;
    }

    public LaadServiceException() {
        super();
    }
}
