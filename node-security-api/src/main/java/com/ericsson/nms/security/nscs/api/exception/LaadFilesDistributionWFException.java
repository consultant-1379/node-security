/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
 * Exception thrown by the LAAD Files Distribute Commnad Handlers in case of a work flow error.
 * </p>
 *
 * @author tcsgoja
 *
 */
public class LaadFilesDistributionWFException extends NscsServiceException {

	private static final long serialVersionUID = -6543957749486156558L;

    public LaadFilesDistributionWFException() {
        super(NscsErrorCodes.LAAD_FILES_DISTRIBUTION_WF_FAILED);
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }

    public LaadFilesDistributionWFException(final String message) {
        super(formatMessage(NscsErrorCodes.LAAD_FILES_DISTRIBUTION_WF_FAILED, message));
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }

    public LaadFilesDistributionWFException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.LAAD_FILES_DISTRIBUTION_WF_FAILED, message), cause);
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }

    public LaadFilesDistributionWFException(final Throwable cause) {
        super(NscsErrorCodes.LAAD_FILES_DISTRIBUTION_WF_FAILED, cause);
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }

    /*
     * Gets the error type LAAD_FILES_DISTRIBUTION_WF_FAILED
     *
     * @return ErrorType.LAAD_FILES_DISTRIBUTION_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.LAAD_FILES_DISTRIBUTION_WF_FAILED;
    }
}
