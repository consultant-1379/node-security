/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to check the provided key ID is removed or not.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CHECK_KEYS_REMOVED
 * </p>
 *
 * @author xvekkar
 */
public class CppCheckKeysRemovedTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 7261423245001544009L;

    public static final String SHORT_DESCRIPTION = "Check NTP keys removed";

    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public CppCheckKeysRemovedTask() {
        super(WorkflowTaskType.CPP_CHECK_KEYS_REMOVED);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CppCheckKeysRemovedTask(final String fdn) {
        super(WorkflowTaskType.CPP_CHECK_KEYS_REMOVED, fdn);
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
