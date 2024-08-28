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
 * Workflow task representing a request to remove the NTP key data mapping for a node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_REMOVE_NTP_KEY_DATA_MAPPING
 * </p>
 *
 * @author xvekkar
 */
public class CPPRemoveNtpKeyDataMappingTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 1796515246383156667L;

    public static final String SHORT_DESCRIPTION = "Remove NTP key data mapping";

    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public CPPRemoveNtpKeyDataMappingTask() {
        super(WorkflowTaskType.CPP_REMOVE_NTP_KEY_DATA_MAPPING);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CPPRemoveNtpKeyDataMappingTask(final String fdn) {
        super(WorkflowTaskType.CPP_REMOVE_NTP_KEY_DATA_MAPPING, fdn);
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
