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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to perform action on a given list of MOs.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CHECK_MO_ACTION_PROGRESS
 * </p>
 *
 * @author emaborz
 */
public class CheckMoActionProgressTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 4749364569438428610L;

    /**
     * Key of the output parameters in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();
    public static final String SHORT_DESCRIPTION = "Check action";

    public CheckMoActionProgressTask() {
        super(WorkflowTaskType.CHECK_MO_ACTION_PROGRESS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CheckMoActionProgressTask(final String fdn) {
        super(WorkflowTaskType.CHECK_MO_ACTION_PROGRESS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
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
}
