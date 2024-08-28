/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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
 * This exception will be thrown if server(s) is/are not properly configured on the CPP nodes.
 * @author xchowja
 *
 */
public class ServerNameNotFoundException extends NscsServiceException {

    /**
     */
    private static final long serialVersionUID = 1L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.RTSEL_DELETE_SERVER_NAMES_NOT_VALID);
        }
    }

    public ServerNameNotFoundException() {
        super(NscsErrorCodes.RTSEL_DELETE_SERVER_NAMES_NOT_FOUND);
    }

    public ServerNameNotFoundException(final String message) {
        super(formatMessage(NscsErrorCodes.RTSEL_DELETE_SERVER_NAMES_NOT_FOUND, message));
    }

    public ServerNameNotFoundException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.RTSEL_DELETE_SERVER_NAMES_NOT_FOUND, message), cause);
    }

    public ServerNameNotFoundException(final Throwable cause) {
        super(NscsErrorCodes.RTSEL_DELETE_SERVER_NAMES_NOT_FOUND, cause);
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.SERVER_NAME_NOT_FOUND;
    }

}
