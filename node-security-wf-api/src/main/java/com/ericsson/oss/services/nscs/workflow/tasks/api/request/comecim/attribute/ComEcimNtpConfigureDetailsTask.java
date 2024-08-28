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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * Workflow task representing a request configure Ntp server details on the COM ECIM node
 *
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_NTP_CONFIGURE
 *
 * @author xkihari
 */
public class ComEcimNtpConfigureDetailsTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 837313860736768884L;

    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Com Ntp Configure server details";

    public ComEcimNtpConfigureDetailsTask() {
        super(WorkflowTaskType.COM_ECIM_NTP_CONFIGURE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimNtpConfigureDetailsTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_NTP_CONFIGURE, fdn);
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
