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
package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Node if NTP feature is not supported
 * when one or more nodes in the command don't have supported NTP server MO
 * associated to it.</p>
 * Created by zkndsrv
 */
public class NtpOperationNotSupportedException extends NscsServiceException {

    private static final long serialVersionUID = -6038662241428304105L;

    public NtpOperationNotSupportedException() {
        super(NscsErrorCodes.NTP_DETAILS_MSG);
        setSuggestedSolution(NscsErrorCodes.NTP_SUGGESTED_SOLUTION);
    }

    public NtpOperationNotSupportedException(final String customMessage) {
        super(customMessage);
        setSuggestedSolution(NscsErrorCodes.NTP_SUGGESTED_SOLUTION);
    }

    /*
     * @return ErrorType.NTP_OPERATION_NOT_SUPPORTED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NTP_OPERATION_NOT_SUPPORTED;
    }

}
