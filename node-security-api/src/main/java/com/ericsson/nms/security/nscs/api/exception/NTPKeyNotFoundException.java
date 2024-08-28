/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

/**
 * Thrown when a NTP key id is not present in the NTP Server.
 *
 * @author xkihari
 */
public class NTPKeyNotFoundException extends NscsServiceException {

    private static final long serialVersionUID = -3164459083651080028L;

    public NTPKeyNotFoundException() {
        super(NscsErrorCodes.NTP_KEY_NOT_FOUND);
    }

    public NTPKeyNotFoundException(final String message) {
        super(formatMessage(NscsErrorCodes.NTP_KEY_NOT_FOUND, message));
    }

    public NTPKeyNotFoundException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.NTP_KEY_NOT_FOUND, message), cause);
    }

    public NTPKeyNotFoundException(final Throwable cause) {
        super(NscsErrorCodes.NTP_KEY_NOT_FOUND, cause);
    }

    /**
     * @return ErrorType.NTP_KEY_NOT_FOUND
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NTP_KEY_NOT_FOUND;
    }
}
