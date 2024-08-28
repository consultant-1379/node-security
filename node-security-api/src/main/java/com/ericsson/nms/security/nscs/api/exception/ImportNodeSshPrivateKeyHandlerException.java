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

public class ImportNodeSshPrivateKeyHandlerException extends NscsServiceException {

    private static final long serialVersionUID = -7494304427215011760L;

    public ImportNodeSshPrivateKeyHandlerException() {
        super(NscsErrorCodes.IMPORT_NODE_SSH_PRIVATE_KEY_HANDLER_ERROR);
    }

    public ImportNodeSshPrivateKeyHandlerException(final String message) {
        super(formatMessage(NscsErrorCodes.IMPORT_NODE_SSH_PRIVATE_KEY_HANDLER_ERROR, message));
    }

    public ImportNodeSshPrivateKeyHandlerException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.IMPORT_NODE_SSH_PRIVATE_KEY_HANDLER_ERROR, message), cause);
    }

    public ImportNodeSshPrivateKeyHandlerException(final Throwable cause) {
        super(NscsErrorCodes.IMPORT_NODE_SSH_PRIVATE_KEY_HANDLER_ERROR, cause);
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.IMPORT_NODE_SSH_PRIVATE_KEY_HANDLER_ERROR;
    }

}
