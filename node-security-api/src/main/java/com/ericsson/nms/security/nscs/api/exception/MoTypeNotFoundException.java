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

public class MoTypeNotFoundException extends NscsServiceException {

    private static final long serialVersionUID = 5825818482381424261L;

    public MoTypeNotFoundException() {
        super(NscsErrorCodes.MO_TYPE_NOT_FOUND);
    }

    public MoTypeNotFoundException(final String message) {
        super(formatMessage(NscsErrorCodes.MO_TYPE_NOT_FOUND, message));
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.MO_TYPE_NOT_FOUND;
    }

}
