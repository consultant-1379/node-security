/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.timer;

import java.util.Map;

/**
 * Listener that is called every time that a interval job is executed.
 */
public interface IntervalJobAction {

    /**
     * Called when a {@link IntervalJobService} is executed. If <code>true</code> is returned the job is terminated.
     * 
     * @param params
     *            parameters needed.
     * @return <code>true</code> the job is terminated. Otherwise the job keep it's interval execution.
     */
    boolean doAction(final Map<JobActionParameters, Object> params);

    /**
     * Defined parameters for {@link IntervalJobAction}.
     */
    public static enum JobActionParameters {
        CM_READER, WORKFLOW_HANDLER, MO_ACTION_SERVICE, SYSTEM_RECORDER,CAPABILITY_SERVICE;
    }
}
