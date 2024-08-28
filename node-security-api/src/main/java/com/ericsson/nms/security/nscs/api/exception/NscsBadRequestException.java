/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

public class NscsBadRequestException extends NscsServiceException {

    private static final long serialVersionUID = -7274982387509626826L;

    public NscsBadRequestException() {
        super(NscsErrorCodes.BAD_REQUEST);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT_PARAMETERS);
    }

    public NscsBadRequestException(final String message) {
        super(formatMessage(NscsErrorCodes.BAD_REQUEST, message));
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT_PARAMETERS);
    }

    public NscsBadRequestException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.BAD_REQUEST, message), cause);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT_PARAMETERS);
    }

    public NscsBadRequestException(final String message, final Throwable cause, final String suggestedSolution) {
        super(formatMessage(NscsErrorCodes.BAD_REQUEST, message), cause);
        setSuggestedSolutionLocal(suggestedSolution);
    }

    public NscsBadRequestException(final String message, final String suggestedSolution) {
        super(formatMessage(NscsErrorCodes.BAD_REQUEST, message));
        setSuggestedSolutionLocal(suggestedSolution);
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.BAD_REQUEST;
    }

    private void setSuggestedSolutionLocal(final String suggestedSolution) {
        super.setSuggestedSolution(suggestedSolution);
    }
}
