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
package com.ericsson.nms.security.nscs.data.moaction;

/**
 * Enumeration of possible states of an MO action according to value as read on
 * node and respect of a requested action.
 * <p>
 * Values:
 * <ul>
 * <li>IDLE: no action ever performed</li>
 * <li>ONGOING: expected action currently still ongoing</li>
 * <li>FINISHED_WITH_SUCCESS: expected action successfully finished</li>
 * <li>FINISHED_WITH_ERROR: expected action finished with failure</li>
 * <li>OTHER_ACTION_FINISHED: unexpected action finished</li>
 * <li>OTHER_ACTION_ONGOING: unexpected action currently ongoing</li>
 * </ul>
 * </p>
 * 
 * @author emaborz
 *
 */
public enum MoActionState {
    IDLE, ONGOING, FINISHED_WITH_SUCCESS, FINISHED_WITH_ERROR, OTHER_ACTION_FINISHED, OTHER_ACTION_ONGOING;

}
