/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2019
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
 * Exception thrown by the Test Command Handler in case of test workflows.
 * </p>
 */
public class TestWfsException extends NscsServiceException {

    private static final long serialVersionUID = 5176396756343825131L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
        }
    }

    public TestWfsException() {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }

    public TestWfsException(final String message) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
    }

    public TestWfsException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
    }

    public TestWfsException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNEXPECTED_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNEXPECTED_ERROR;
    }
}
