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
 * <p>
 * Exception thrown by the OnDemandCrlDownloadHandler in case of a workflow error.
 * </p>
 * 
 * @author xramdag
 */
public class OnDemandCrlDownloadWfException extends NscsServiceException {
    /**
     * 
     */
    private static final long serialVersionUID = 3909996030632859895L;

    public OnDemandCrlDownloadWfException() {
        super(NscsErrorCodes.ON_DEMAND_CRL_DOWNLOAD_WF_FAILED);
    }

    public OnDemandCrlDownloadWfException(final String message) {
        super(formatMessage(NscsErrorCodes.ON_DEMAND_CRL_DOWNLOAD_WF_FAILED, message));
    }

    public OnDemandCrlDownloadWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.ON_DEMAND_CRL_DOWNLOAD_WF_FAILED, message), cause);
    }

    public OnDemandCrlDownloadWfException(final Throwable cause) {
        super(NscsErrorCodes.ON_DEMAND_CRL_DOWNLOAD_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.ONDEMAND_CRL_DOWNLOAD_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.ON_DEMAND_CRL_DOWNLOAD_WF_FAILED;
    }
}
