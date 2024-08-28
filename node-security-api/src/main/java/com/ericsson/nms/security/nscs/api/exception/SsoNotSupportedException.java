/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
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
 * <p>Exception thrown by the command parser in case sso operation is performed on
 * unsupported node</p>
 * Created by zkllsmg on 02/25/2020.
 */

public class SsoNotSupportedException extends NscsServiceException {
    private static final long serialVersionUID = 3509806957175133051L;

    public SsoNotSupportedException() {
        super(NscsErrorCodes.SSO_NOT_SUPPORTED_FOR_THE_NODETYPE);
        setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE);
    }

    public SsoNotSupportedException(final String message) {
        super(formatMessage(NscsErrorCodes.SSO_NOT_SUPPORTED_FOR_THE_NODETYPE, message));
        setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE);
    }

    /**
     * Gets the error type
     * @return ErrorType.COMMAND_SYNTAX_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.SSO_NOT_SUPPORTED_FOR_THE_NODE_TYPE;
    }
}
