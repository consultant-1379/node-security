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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute;

/**
 * Enumeration of possible results of a service task managing the perform/check MO action.
 * 
 * @author emaborz
 *
 */
public enum WorkflowMoActionResult {
    ONGOING, FINISHED_WITH_SUCCESS, FINISHED_WITH_PENDING, IDLE;

}
