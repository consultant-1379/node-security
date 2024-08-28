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
import java.util.List;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to validate the Node synchronization for NTP remove and validate key IDs to be removed.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.VALIDATE_NODE_FOR_NTP_REMOVE
 * </p>
 *
 * @author xvekkar
 */
public class ValidateNodeForNtpRemoveTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 8094123196974353855L;
    public static final String SHORT_DESCRIPTION = "Validate Node for NTP";
    public static final String NTP_KEY_IDS = WorkflowParameterKeys.NTP_KEY_IDS.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();
    public static final String NTP_SERVER_IDS = WorkflowParameterKeys.NTP_SERVER_IDS.toString();

    public ValidateNodeForNtpRemoveTask() {
        super(WorkflowTaskType.VALIDATE_NODE_FOR_NTP_REMOVE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ValidateNodeForNtpRemoveTask(final String fdn) {
        super(WorkflowTaskType.VALIDATE_NODE_FOR_NTP_REMOVE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the ntpKeyIdList
     */
    @SuppressWarnings("unchecked")
    public List<String> getNtpKeyIdList() {
        return (List<String>) getValue(NTP_KEY_IDS);
    }

    public void setNtpKeyIdList(final List<String> ntpKeyIds) {
        setValue(NTP_KEY_IDS, ntpKeyIds);
    }

    /**
     * @return the ntpServerIdList
     */
    @SuppressWarnings("unchecked")
    public List<String> getNtpServerIdList() {
        return (List<String>) getValue(NTP_SERVER_IDS);
    }

    public void setNtpServerIdList(final List<String> ntpServerIds) {
        setValue(NTP_SERVER_IDS, ntpServerIds);
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
