/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
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
 * Exception thrown when too many child MOs of a given type are found under a parent MO.
 */
public class TooManyChildMosException extends NscsServiceException {

    private static final long serialVersionUID = 1433973191261494926L;

    public TooManyChildMosException() {
        super(NscsErrorCodes.TOO_MANY_CHILD_MOS);
    }

    public TooManyChildMosException(final String message) {
        super(formatMessage(NscsErrorCodes.TOO_MANY_CHILD_MOS, message));
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.TOO_MANY_CHILD_MOS;
    }

}
