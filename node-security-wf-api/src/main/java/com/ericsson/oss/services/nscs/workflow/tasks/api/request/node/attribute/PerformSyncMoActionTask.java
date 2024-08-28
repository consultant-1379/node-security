/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2020
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to perform a synchronous action on a given MO.
 * 
 * Synchronous actions are actions performed by node (EOI YANG based) in a synchronous way, the result is returned when the node has completed the
 * action itself. No need to poll for the action progress report.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.PERFORM_SYNC_MO_ACTION
 * </p>
 */
public class PerformSyncMoActionTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 7870917115641093639L;

    /**
     * Key of the output parameters parameter in the workflow parameters map.
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    /**
     * Short description of the task.
     */
    public static final String SHORT_DESCRIPTION = "Perform sync action";

    public PerformSyncMoActionTask() {
        super(WorkflowTaskType.PERFORM_SYNC_MO_ACTION);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public PerformSyncMoActionTask(final String fdn) {
        super(WorkflowTaskType.PERFORM_SYNC_MO_ACTION, fdn);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    /**
     * @return the outputParams
     */
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> getOutputParams() {
        return (Map<String, Serializable>) getValue(OUTPUT_PARAMS_KEY);
    }

    /**
     * @param outputParams
     *            the outputParams to set
     */
    public void setOutputParams(final Map<String, Serializable> outputParams) {
        setValue(OUTPUT_PARAMS_KEY, outputParams);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }
}
