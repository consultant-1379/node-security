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
 * Exception thrown when alarm supervision is not enabled or when the alarm
 * current service state value is not IN_SERVICE on the given nodes
 * </p>
 *
 * @author zkankee
 */
public class InvalidAlarmServiceStateException extends NscsServiceException{

    private static final long serialVersionUID = -4348822320736211558L;

    public InvalidAlarmServiceStateException(final String customMessage) {
        super(customMessage);
   }

    /*
     * @return ErrorType.INVALID_ALARM_SERVICE_STATE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_ALARM_SERVICE_STATE;
    }
}
