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
public class SshKeyWfException extends NscsServiceException {

    private static final long serialVersionUID = 2887846172970293024L;

    public SshKeyWfException() {
        super(NscsErrorCodes.SSH_KEY_WF_FAILED);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.SSH_KEY_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.SSH_KEY_WF_FAILED;
    }
}
