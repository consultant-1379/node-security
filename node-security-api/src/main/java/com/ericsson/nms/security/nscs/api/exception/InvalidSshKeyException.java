/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

public class InvalidSshKeyException extends NscsServiceException {

    private static final long serialVersionUID = -7494304427215011760L;

    public InvalidSshKeyException() {
        super(NscsErrorCodes.INVALID_SSH_KEY_GENERATED);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }

    public InvalidSshKeyException(final String message) {
        super(formatMessage(NscsErrorCodes.INVALID_SSH_KEY_GENERATED, message));
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }

    public InvalidSshKeyException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.INVALID_SSH_KEY_GENERATED, message), cause);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }


    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public NscsServiceException.ErrorType getErrorType() {
        return NscsServiceException.ErrorType.SSH_INVALID_KEY_GENERATED;
    }

    private NscsServiceException setSuggestedSolutionLocal(final String suggestedSolution) {
        return super.setSuggestedSolution(suggestedSolution);
    }
}
