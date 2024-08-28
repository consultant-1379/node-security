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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Base class that represents task coming from security workflows that need to be fulfilled by node security service
 * </p>
 * <p>
 * A task is compound of a WorkflowTaskType and additional parameters required by the task type. Even Though parameters are stored in a Map, it is
 * recommended that each WorkflowTaskType has it's own specific sub-class of WorkflowTask exposing it's particular view of the map in form of getters
 * and setters. See {@link com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkFlowNodeTask}
 * </p>
 *
 * @author emaynes on 13/06/2014.
 * @see com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkFlowNodeTask
 */
public abstract class WorkflowTask implements Serializable {

    private static final long serialVersionUID = -2629612231725731855L;

    public static final String BUSINESS_KEY_PARAMETER = "businessKey";
    public static final String EXECUTION_ID_PARAMETER = "executionId";
    public static final String WORKFLOW_DEFINITION_ID_PARAMETER = "workflowDefinitionId";
    public static final String WORKFLOW_INSTANCE_ID_PARAMETER = "workflowInstanceId";
    public static final String ACTIVATION_STEP_PARAMETER = "activationStep";
    public static final String ERROR_DETAILS = "errorDetails";

    private WorkflowTaskType taskType;

    private final Map<String, Object> parameters = new HashMap<>();

    /**
     * WorkflowTask constructor
     *
     * @param taskType
     *            the WorkflowTaskType of this WorkflowActionTask
     */
    public WorkflowTask(final WorkflowTaskType taskType) {
        this.taskType = taskType;
    }

    /**
     * WorkflowTask constructor
     */
    protected WorkflowTask() {
    }

    /**
     * Gets current WorkflowTaskType of this WorkflowTask
     *
     * @return WorkflowTaskType
     */
    public final WorkflowTaskType getTaskType() {
        return taskType;
    }

    /**
     * Sets current WorkflowTaskType of this WorkflowTask
     *
     * @param taskType
     *            the intended WorkflowTaskType
     */
    public final void setTaskType(final WorkflowTaskType taskType) {
        this.taskType = taskType;
    }

    /**
     * Gets the Map containing the parameters of this WorkflowTask
     *
     * @return Map with the parameters
     */
    public final Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     *
     * @param businessKey the businessKey
     */
    public final void setBusinessKey(final String businessKey) {
        setValueString(BUSINESS_KEY_PARAMETER, businessKey);
    }

    /**
     *
     * @return the businessKey
     */
    public final String getBusinessKey() {
        return getValueString(BUSINESS_KEY_PARAMETER);
    }

    /**
     *
     * @param executionId the executionId
     */
    public final void setExecutionId(final String executionId) {
        setValueString(EXECUTION_ID_PARAMETER, executionId);
    }

    /**
     *
     * @return the executionId
     */
    public final String getExecutionId() {
        return getValueString(EXECUTION_ID_PARAMETER);
    }

    /**
     *
     * @param workflowDefinitionId the workflowDefinitionId
     */
    public final void setWorkflowDefinitionId(final String workflowDefinitionId) {
        setValueString(WORKFLOW_DEFINITION_ID_PARAMETER, workflowDefinitionId);
    }

    /**
     *
     * @return the workflowDefinitionId
     */
    public final String getWorkflowDefinitionId() {
        return getValueString(WORKFLOW_DEFINITION_ID_PARAMETER);
    }

    /**
     *
     * @param workflowInstanceId the workflowInstanceId
     */
    public final void setWorkflowInstanceId(final String workflowInstanceId) {
        setValueString(WORKFLOW_INSTANCE_ID_PARAMETER, workflowInstanceId);
    }

    /**
     *
     * @return the workflowInstanceId
     */
    public final String getWorkflowInstanceId() {
        return getValueString(WORKFLOW_INSTANCE_ID_PARAMETER);
    }

    /**
     *
     * @param activationStep the activationStep
     */
    public final void setActivationStep(final String activationStep) {
        setValueString(ACTIVATION_STEP_PARAMETER, activationStep);
    }

    /**
     *
     * @return the activationStep
     */
    public final String getActivationStep() {
        return getValueString(ACTIVATION_STEP_PARAMETER);
    }

    /**
     *
     * @param errorDetails the errorDetails
     */
    public final void setErrorDetails(final String errorDetails) {
        setValueString(ERROR_DETAILS, errorDetails);
    }

    /**
     *
     * @return the errorDetails
     */
    public final String getErrorDetails() {
        return getValueString(ERROR_DETAILS);
    }

    /**
     * Utility method for subclasses to facilitate getting a parameter value
     *
     * @param parameter
     *            name of the parameter
     * @return null or the current value associated with the supplied parameter name
     */
    protected Object getValue(final String parameter) {
        return getParameters().get(parameter);
    }

    /**
     * Utility method for subclasses to facilitate setting a parameter value
     *
     * @param parameter
     *            name of the parameter
     * @param value
     *            value to be associated with the parameter name
     */
    protected void setValue(final String parameter, final Object value) {
        getParameters().put(parameter, value);
    }

    protected String getValueString(final String parameter) {
        final Object value = getValue(parameter);
        return value == null ? null : value.toString();
    }

    protected void setValueString(final String parameter, final Object value) {
        setValue(parameter, value == null ? null : value.toString());
    }

    protected boolean hasParameter(final String parameter) {
        return this.getParameters().containsKey(parameter);
    }

}
