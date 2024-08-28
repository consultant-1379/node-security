/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
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
 * <p>
 * Workflow task representing a request to check if the requested enrollment protocol is supported by COM ECIM node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL
 * </p>
 *
 * @author elucbot
 */
public class ComEcimCheckEnrollmentProtocolTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 5358926465934208576L;
    public static final String ENROLLMENT_PROTOCOL_KEY = WorkflowParameterKeys.ENROLLMENT_MODE.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Check enrollProtocol";

    public ComEcimCheckEnrollmentProtocolTask() {
        super(WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL);
        setValue(ENROLLMENT_PROTOCOL_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimCheckEnrollmentProtocolTask(final String fdn, final String enrollmentMode) {
        super(WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL, fdn);
        setValue(ENROLLMENT_PROTOCOL_KEY, enrollmentMode);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the enrollment mode.
     *
     * @return enrollment mode.
     */
    public String getEnrollmentMode() {
        return (String) getValue(ENROLLMENT_PROTOCOL_KEY);
    }

    /**
     * Sets the enrollment mode.
     *
     * @param enrollmentMode
     *            enrollment mode.
     */
    public void setEnrollmentMode(final String enrollmentMode) {
        setValue(ENROLLMENT_PROTOCOL_KEY, enrollmentMode);
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
