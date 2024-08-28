package com.ericsson.nms.security.nscs.api.model.service;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;

public class NscsModelServiceException extends NscsServiceException {

	private static final long serialVersionUID = -2291618492337520961L;

	private String statusMessage;

	public NscsModelServiceException(final String statusMessage) {
		super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
		this.statusMessage = statusMessage;
	}

	{
		{
			setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
		}
	}

	public NscsModelServiceException(final String message, final String statusMessage) {
		super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
		this.statusMessage = statusMessage;
	}

	public NscsModelServiceException(final String message, final Throwable cause, final String statusMessage) {
		super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
		this.statusMessage = statusMessage;
	}

	public NscsModelServiceException(final Throwable cause, final String statusMessage) {
		super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
		this.statusMessage = statusMessage;
	}

	public NscsModelServiceException(final Throwable cause) {
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
