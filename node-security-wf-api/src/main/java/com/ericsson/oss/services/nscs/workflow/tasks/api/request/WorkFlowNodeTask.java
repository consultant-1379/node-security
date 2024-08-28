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
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.constraints.NotNull;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * Base class for all tasks that target a specific node Created by emaynes on 16/06/2014.
 */
public abstract class WorkFlowNodeTask extends WorkflowTask {

    private static final long serialVersionUID = -4416543173533109634L;

    /**
     * Key of the node FDN
     */
    public static final String NODE_FDN_PARAMETER = "nodeFdn";

    /**
     * Key of the jobID
     */
    public static final String JOB_ID = WorkflowParameterKeys.JOB_ID.toString();

    /**
     * Key of WF wake ID
     */
    public static final String WF_WAKE_ID = WorkflowParameterKeys.WF_WAKE_ID.toString();

    /**
     * Key of WF short description
     */
    public static final String SHORT_DESCRIPTION_KEY = WorkflowParameterKeys.SHORT_DESCRIPTION.toString();

    private NodeReference node;

    public WorkFlowNodeTask() {
    }

    public WorkFlowNodeTask(final WorkflowTaskType taskType) {
        super(taskType);
    }

    public WorkFlowNodeTask(final String fdn) {
        this(null, fdn);
    }

    public WorkFlowNodeTask(final WorkflowTaskType taskType, final String fdn) {
        super(taskType);
        setNodeFdn(fdn);
    }

    /**
     * Sets target node FDN
     * <p>
     * <b>This is a mandatory attribute</b>
     * </p>
     *
     * @param fdn
     *            the node FDN
     */
    public final void setNodeFdn(final String fdn) {
        setValueString(NODE_FDN_PARAMETER, fdn);
    }

    /**
     * <p>
     * <b>Mandatory attribute</b>
     * </p>
     *
     * @return target node FDN
     */
    @NotNull
    public final String getNodeFdn() {
        return getValueString(NODE_FDN_PARAMETER);
    }

    /**
     * @return the WF wake ID
     */
    public String getWfWakeId() {
        return (String) getValue(WF_WAKE_ID);
    }

    /**
     * @param wfWakeId
     *            WF wake ID to set
     */
    public void setWfWakeId(final String wfWakeId) {
        setValue(WF_WAKE_ID, wfWakeId);
    }

    /**
     * @return the jobid
     */
    public String getJobid() {
        return (String) getValue(JOB_ID);
    }

    /**
     * @param shortDescription
     *            the short description to set
     */
    public void setShortDescription(final String shortDescription) {
        setValue(SHORT_DESCRIPTION_KEY, shortDescription);
    }

    /**
     * @return the short description
     */
    public String getShortDescription() {
        final String shortDescription = (String) getValue(SHORT_DESCRIPTION_KEY);
        if (shortDescription == null) {
            return getClass().getSimpleName();
        }
        return shortDescription;
    }

    /**
     * @param jobid
     *            the jobid to set
     */
    public void setJobid(final String jobid) {
        setValue(JOB_ID, jobid);
    }

    /**
     * Get the target node
     *
     * @return NodeReference or null
     */
    public NodeReference getNode() {
        if (node == null && getNodeFdn() != null) {
            node = new NodeRef(getNodeFdn());
        }
        return node;
    }

    /**
     * Sets the target node.
     * <p>
     * This is semantically equals to setNodeFdn(node.getFdn());
     * </p>
     *
     * @param node
     *            a NodeReference instance
     */
    public void setNode(final NodeReference node) {
        this.node = node;
        if (node == null) {
            setNodeFdn(null);
        } else {
            setNodeFdn(node.getFdn());
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%s", getClass().getSimpleName(), getParameters());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkFlowNodeTask)) {
            return false;
        }

        final WorkFlowNodeTask that = (WorkFlowNodeTask) o;

        if (getNodeFdn() != null ? !getNodeFdn().equals(that.getNodeFdn()) : that.getNodeFdn() != null) {
            return false;
        }
        if (!getParameters().equals(that.getParameters())) {
            return false;
        }
        if (getTaskType() != that.getTaskType()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getTaskType() != null ? getTaskType().hashCode() : 0;
        result = 31 * result + getParameters().hashCode();
        result = 31 * result + (getNodeFdn() != null ? getNodeFdn().hashCode() : 0);
        return result;
    }

    /**
     * Return a string builder containing in a human-readable format the task parameters. Too long or serialized parameters are not printed.
     * @return string builder with task paramters
     */
    public StringBuilder stringify() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(":{");
        final Map<String, Object> taskParams = this.getParameters();
        if (taskParams != null) {
            stringifyTaskParameters(sb, taskParams);
        }
        sb.append("}");
        return sb;
    }

    /**
     * Stringify in the given string builder the task parameters in a human-readable format.
     * 
     * @param sb
     *            the string builder.
     * @param taskParams
     *            the task parameters.
     */
    private void stringifyTaskParameters(final StringBuilder sb, final Map<String, Object> taskParams) {
        String delimiter = "";
        for (final Entry<String, Object> taskParamEntry : taskParams.entrySet()) {
            final String taskParamKey = taskParamEntry.getKey();
            final Object taskParamValue = taskParamEntry.getValue();
            sb.append(delimiter);
            delimiter = ",";
            sb.append(taskParamKey);
            if (taskParamValue != null) {
                stringifyTaskParameter(sb, taskParamKey, taskParamValue);
            } else {
                sb.append("=null");
            }
        }
    }

    /**
     * Stringify in the given string builder the given task parameter in a human-readable format.
     * 
     * @param sb
     *            the string builder.
     * @param taskParamKey
     *            the task parameter key.
     * @param taskParamValue
     *            the task parameter value.
     */
    private void stringifyTaskParameter(final StringBuilder sb, final String taskParamKey, final Object taskParamValue) {
        if (WorkflowParameterKeys.OUTPUT_PARAMS.toString().equals(taskParamKey)) {
            stringifyOutputParamsTaskParameter(sb, taskParamValue);
        } else if (WorkflowParameterKeys.LDAP_WORKFLOW_CONTEXT.toString().equals(taskParamKey)) {
            stringifyLdapWorkflowContextTaskParameter(sb, taskParamValue);
        } else {
            sb.append("=");
            sb.append(taskParamValue);
        }
    }

    /**
     * Stringify in the given string builder the given OUTPUT_PARAMS task parameter in a human-readable format.
     * 
     * @param sb
     *            the string builder.
     * @param taskParamValue
     *            the OUTPUT_PARAMS task parameter value.
     */
    private void stringifyOutputParamsTaskParameter(final StringBuilder sb, final Object taskParamValue) {
        // This is an output parameters, a map containing potentially serialized items to be excluded.
        @SuppressWarnings("unchecked")
        final Map<String, Serializable> outputParams = (Map<String, Serializable>) taskParamValue;
        sb.append(":{");
        String delimiter = "";
        for (final Entry<String, Serializable> outputParamEntry : outputParams.entrySet()) {
            final String outputParamKey = outputParamEntry.getKey();
            final Serializable outputParamValue = outputParamEntry.getValue();
            sb.append(delimiter);
            delimiter = ",";
            sb.append(outputParamKey);
            if (outputParamValue != null) {
                if (WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.MO_ACTIONS.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN_LIST.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.TRUSTED_ENTITY_INFO.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.NTP_KEY.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.MO_ACTION.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString().equals(outputParamKey)
                        || WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString().equals(outputParamKey)) {
                    // Serialized parameter item. Print only the string length
                    sb.append(":{len=");
                    sb.append(outputParamValue.toString().length());
                    sb.append("}");
                } else {
                    sb.append("=");
                    sb.append(outputParamValue);
                }
            } else {
                sb.append("=null");
            }
        }
        sb.append("}");
    }

    /**
     * Stringify in the given string builder the given OUTPUT_PARAMS task parameter in a human-readable format.
     * 
     * @param sb
     *            the string builder.
     * @param taskParamValue
     *            the OUTPUT_PARAMS task parameter value.
     */
    private void stringifyLdapWorkflowContextTaskParameter(final StringBuilder sb, final Object taskParamValue) {
        // This is an LDAP workflow context, a map containing potentially bind password to be excluded.
        @SuppressWarnings("unchecked")
        final Map<String, Serializable> ldapWorkflowContext = (Map<String, Serializable>) taskParamValue;
        sb.append(":{");
        String delimiter = "";
        for (final Entry<String, Serializable> ldapWorkflowContextEntry : ldapWorkflowContext.entrySet()) {
            final String ldapWorkflowContextKey = ldapWorkflowContextEntry.getKey();
            final Serializable ldapWorkflowContextValue = ldapWorkflowContextEntry.getValue();
            sb.append(delimiter);
            delimiter = ",";
            sb.append(ldapWorkflowContextKey);
            if (ldapWorkflowContextValue != null) {
                if (WorkflowOutputParameterKeys.BIND_PASSWORD.toString().equals(ldapWorkflowContextKey)
                        || WorkflowParameterKeys.BIND_PASSWORD.toString().equals(ldapWorkflowContextKey)) {
                    // Bind password item. Do not print the value.
                    sb.append("=***");
                } else {
                    sb.append("=");
                    sb.append(ldapWorkflowContextValue);
                }
            } else {
                sb.append("=null");
            }
        }
        sb.append("}");
    }
}
