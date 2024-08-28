package com.ericsson.nms.security.nscs.api.exception;

/**
 * <P>Exception thrown every time a system implementation error is detected.</P>
 * Created by emaynes on 03/06/2014.
 */
public abstract class NscsSystemException extends NscsServiceException{

    protected NscsSystemException() {
        super();
    }

    protected NscsSystemException(final String message) {
        super(message);
    }

    protected NscsSystemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected NscsSystemException(final Throwable cause) {
        super(cause);
    }
}
