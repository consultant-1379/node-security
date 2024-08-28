/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
 * This exception will be thrown if node release version is not supported.
 * 
 * @author xramdag
 * 
 */
public class UnSupportedNodeReleaseVersionException extends NscsServiceException {

    private static final long serialVersionUID = 2231852120012567613L;

    public UnSupportedNodeReleaseVersionException() {
        super(NscsErrorCodes.UNSUPPORTED_NODE_RELEASE_VERSION);
        setSuggestedSolution(NscsErrorCodes.USE_VALID_NODE_RELEASE_VERSION);
    }

    public UnSupportedNodeReleaseVersionException(final String message) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_NODE_RELEASE_VERSION, message));
    }

    public UnSupportedNodeReleaseVersionException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_NODE_RELEASE_VERSION, message), cause);
    }

    public UnSupportedNodeReleaseVersionException(final Throwable cause) {
        super(NscsErrorCodes.UNSUPPORTED_NODE_RELEASE_VERSION, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_CERTIFICATE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNSUPPORTED_NODE_RELEASE_VERSION;
    }
}
