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
 * <p>Exception thrown by the command handler when activating or deactivating IpSec</p>
 *
 * @author emehsau
 */
public class IpSecActionException extends NscsServiceException {

	private static final long serialVersionUID = 6216964805973982432L;

	{{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}

    public IpSecActionException() {
        super(NscsErrorCodes.IP_SEC_ACTION_ERROR);
    }

    public IpSecActionException(final String message) {
        super(formatMessage(NscsErrorCodes.IP_SEC_ACTION_ERROR, message));
    }

    public IpSecActionException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.IP_SEC_ACTION_ERROR, message), cause);
    }

    public IpSecActionException(final Throwable cause) {
        super(NscsErrorCodes.IP_SEC_ACTION_ERROR, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.IP_SEC_ACTION_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.IP_SEC_ACTION_ERROR;
    }
}
