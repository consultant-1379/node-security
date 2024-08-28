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
 * Exception thrown by the NSCS Capability Model Service when something fails
 * @author emaborz
 */
public class NscsCapabilityModelException extends NscsServiceException {

	private static final long serialVersionUID = -4069014830596216484L;
    private String statusMessage;

    public NscsCapabilityModelException(final String statusMessage) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        this.statusMessage = statusMessage;
    }

    {{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}

    public NscsCapabilityModelException(final String message, final String statusMessage) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
        this.statusMessage = statusMessage;
    }

    public NscsCapabilityModelException(final String message, final String statusMessage, final String Ne) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_NODE, message));
        this.statusMessage = statusMessage;
    }

    public NscsCapabilityModelException(final String message, final Throwable cause, final String statusMessage) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
        this.statusMessage = statusMessage;
    }

    public NscsCapabilityModelException(final Throwable cause, final String statusMessage) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
        this.statusMessage = statusMessage;
    }

    public NscsCapabilityModelException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    public String getStatusMessage() {
        return statusMessage;
    }

	@Override
	public ErrorType getErrorType() {
		return ErrorType.CAPABILITY_MODEL_ERROR;
	}

    @Override
    public String toString() {
        return formatMessage(getMessage(), statusMessage);
    }

}
