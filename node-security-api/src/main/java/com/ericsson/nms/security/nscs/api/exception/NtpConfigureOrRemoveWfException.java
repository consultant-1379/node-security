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
 * Exception thrown by the ConfigureNtpHandler or RemoveNtpHandler in case of a workflow error.
 * </p>
 * 
 * @author xvekkar
 */
public class NtpConfigureOrRemoveWfException extends NscsServiceException {

    private static final long serialVersionUID = 2887846172970293024L;

    public NtpConfigureOrRemoveWfException() {
        super(NscsErrorCodes.NTP_CONFIGURE_OR_REMOVE_WF_FAILED);
    }

    public NtpConfigureOrRemoveWfException(final String message) {
        super(formatMessage(NscsErrorCodes.NTP_CONFIGURE_OR_REMOVE_WF_FAILED, message));
    }

    public NtpConfigureOrRemoveWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.NTP_CONFIGURE_OR_REMOVE_WF_FAILED, message), cause);
    }

    public NtpConfigureOrRemoveWfException(final Throwable cause) {
        super(NscsErrorCodes.NTP_CONFIGURE_OR_REMOVE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.NTP_CONFIGURE_OR_REMOVE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NTP_CONFIGURE_OR_REMOVE_WF_FAILED;
    }
}
