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
package com.ericsson.nms.security.nscs.workflow.task.util;

import java.util.Map;
import java.util.UUID;

import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * Utility class containing common methods used by workflow task handlers.
 *
 */
public class NscsWorkflowTaskHandlerUtil {

    /**
     * Utility function to return parameter for scheduled work flow.
     * 
     * @param wakeID
     * @param nodeName
     *
     * @return innerWorkflowVars.
     */
    public Map<String, Object> buildVarForScheduledWorkflow(final Map<String, Object> workflowVars, final String jobID, final String workflowName, final String nodeName, final UUID wakeID) {
        workflowVars.put(WorkflowParameterKeys.JOB_ID.toString(), jobID);
        workflowVars.put(WorkflowParameterKeys.INNERWF_CHILD.toString(), workflowName);
        workflowVars.put(WorkflowParameterKeys.NODE_KEY.toString(), nodeName);
        workflowVars.put(WorkflowParameterKeys.WF_WAKE_ID.toString(), wakeID.toString());

        return workflowVars;
    }
}
