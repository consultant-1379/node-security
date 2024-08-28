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
 * <p>
 * Thrown when a NTP key mappings are not found in NTP Server database
 * </p>
 *
 * @author xjangop
 */
public class NTPKeyMappingNotFoundException extends NscsServiceException {

    private static final long serialVersionUID = 8965091895219241512L;

    public NTPKeyMappingNotFoundException() {
        super(NscsErrorCodes.NTP_KEY_MAPPING_NOT_FOUND_EXCEPTION);
    }


    public NTPKeyMappingNotFoundException(final String message) {
        super(formatMessage(NscsErrorCodes.NTP_KEY_MAPPING_NOT_FOUND_EXCEPTION, message));
    }

    public NTPKeyMappingNotFoundException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.NTP_KEY_MAPPING_NOT_FOUND_EXCEPTION, message), cause);
    }

    public NTPKeyMappingNotFoundException(final Throwable cause) {
        super(NscsErrorCodes.NTP_KEY_MAPPING_NOT_FOUND_EXCEPTION, cause);
    }

    /**
     * @return ErrorType.NTP_KEY_MAPPING_NOT_FOUND
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NTP_KEY_MAPPING_NOT_FOUND;
    }
}
