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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to perform MO action to remove NTP keys from node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.REMOVE_NTP_KEYS
 * </p>
 *
 * @author xvekkar
 */
public class RemoveNtpKeysTask extends WorkflowQueryTask {
    
    private static final long serialVersionUID = 8630484658147084060L;
    /**
     * 
     * Key of the output parameters in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();
    public static final String SHORT_DESCRIPTION = "Remove NTP keys";

    public RemoveNtpKeysTask() {
        super(WorkflowTaskType.REMOVE_NTP_KEYS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public RemoveNtpKeysTask(final String fdn) {
        super(WorkflowTaskType.REMOVE_NTP_KEYS, fdn);
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
