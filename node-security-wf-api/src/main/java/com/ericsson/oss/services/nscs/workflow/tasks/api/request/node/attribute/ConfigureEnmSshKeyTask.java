/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
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
 * Workflow task to configure SSH keys on ENM NetworkElementSecurity MO.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CONFIGURE_ENM_SSH_KEY
 * </p>
 */
public class ConfigureEnmSshKeyTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -4950077349145316315L;

    public static final String SSH_KEY_OPERATION_KEY = WorkflowParameterKeys.KEY_SSHKEYS_OPERATION.toString();
    public static final String SSH_KEYS_GENERATION_ALGORITHM_KEY = WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Set ENM SSH Key";

    public ConfigureEnmSshKeyTask() {
        super(WorkflowTaskType.CONFIGURE_ENM_SSH_KEY);
        setShortDescriptionLocal();
    }

    public ConfigureEnmSshKeyTask(final String nodeName) {
        super(WorkflowTaskType.CONFIGURE_ENM_SSH_KEY, nodeName);
        setShortDescriptionLocal();
    }

    /**
     * @return the sshkeyOperation
     */
    public String getSshkeyOperation() {
        return (String) getValue(SSH_KEY_OPERATION_KEY);
    }

    /**
     * @param sshkeyOperation
     *            the sshkeyOperation to set
     */
    public void setSshkeyOperation(final String sshkeyOperation) {
        this.setValue(SSH_KEY_OPERATION_KEY, sshkeyOperation);
    }

    /**
     * @return the sshkeysGenerationAlgorithm as String
     */
    public String getAlgorithm() {
        return (String) getValue(SSH_KEYS_GENERATION_ALGORITHM_KEY);
    }

    /**
     * @param algorithm
     *            the algorithm as String to set
     */
    public void setAlgorithm(final String algorithm) {
        this.setValue(SSH_KEYS_GENERATION_ALGORITHM_KEY, algorithm);
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

    private void setShortDescriptionLocal() {
        super.setShortDescription(ConfigureEnmSshKeyTask.SHORT_DESCRIPTION);
    }

}
