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
 * *
 * <p>
 * Workflow task represents deletion of NtpSecurityPolicy MO and NtpServer MO for the key IDs.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO
 * </p>
 *
 * @author xvekkar
 *
 */
public class ComEcimDeleteNtpSecPolicyAndNtpServerMoTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 6848850468855821437L;

    public static final String SHORT_DESCRIPTION = "NTP Remove on COM node";

    /**
     * Key of the outPutParams value in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public ComEcimDeleteNtpSecPolicyAndNtpServerMoTask() {
        super(WorkflowTaskType.COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimDeleteNtpSecPolicyAndNtpServerMoTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO, fdn);
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
