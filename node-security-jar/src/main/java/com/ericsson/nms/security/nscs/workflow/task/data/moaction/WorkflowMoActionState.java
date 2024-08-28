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
package com.ericsson.nms.security.nscs.workflow.task.data.moaction;

/**
 * Enumeration of possible states of an MO action managed by service tasks of
 * perform/check MO action.
 * <p>
 * Values:
 * <ul>
 * <li>PERFORMING_IT: performed action currently still ongoing</li>
 * <li>PENDING: pending action waiting to be performed</li>
 * <li>CHECK_IT: to be checked action, just check if it is currently ongoing</li>
 * </ul>
 * </p>
 * 
 * @author emaborz
 *
 */
public enum WorkflowMoActionState {
    PERFORMING_IT, PENDING, CHECK_IT;

}
