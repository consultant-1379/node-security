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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.ssh;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task to generate SSH keys on a node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.SSH_KEY_GENERATION
 * </p>
 *
 * @author elucbot
 */
public class ConfigureSSHKeyGenerationTask extends WorkflowActionTask {

    private static final long serialVersionUID = 8711564827460518970L;

    public static final String SSHKEYS_GENERATION_ISCREATE_KEY = WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ISCREATE.toString();
    public static final String SSHKEYS_OPERATION_KEY = WorkflowParameterKeys.KEY_SSHKEYS_OPERATION.toString();
    public static final String SSHKEYS_GENERATION_ALGORITHM_KEY = WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString();

    public static final String SHORT_DESCRIPTION = "SSH Key";

    /**
     * Constructs ConfigureSSHKeyGeneration.
     */
    public ConfigureSSHKeyGenerationTask() {
        super(WorkflowTaskType.SSH_KEY_GENERATION);
        setShortDescriptionLocal();
    }

    /**
     * Constructs ConfigureSSHKeyGeneration.
     *
     * @param nodeName
     *            of the NE
     */
    public ConfigureSSHKeyGenerationTask(final String nodeName) {
        super(WorkflowTaskType.SSH_KEY_GENERATION, nodeName);
        setShortDescriptionLocal();
    }

    /**
     * Gets key: sSHKeyGeneration_isCreate
     *
     * @return sSHKeyGeneration_isCreate (boolean) from String
     */
    public boolean getIsCreate() {
        return Boolean.parseBoolean((String) getValue(SSHKEYS_GENERATION_ISCREATE_KEY));
    }

    /**
     * Sets key: sSHKeyGeneration_isCreate
     *
     * @param isCreate
     *            (boolean) to String
     */
    public void setIsCreate(final String isCreate) {
        this.setValue(SSHKEYS_GENERATION_ISCREATE_KEY, isCreate);
    }

    /**
     * Gets key: SSHKEYS_OPERATION_KEY
     * 
     * @return SSHKEY_OPERATION as String
     */
    public String getSshkeyOperation() {
        return (String) getValue(SSHKEYS_OPERATION_KEY);
    }

    /**
     * Sets key: SSHKEYS_OPERATION_KEY
     * 
     * @param sshkeyOperation as String
     *
     */
    public void setSshkeyOperation(final String sshkeyOperation) {
        this.setValue(SSHKEYS_OPERATION_KEY, sshkeyOperation);
    }

    /**
     * Gets key: sSHKeyGeneration_Algorithm
     * 
     * @return sSHKeyGeneration_Algorithm (AlgorithmKeys)
     */
    public AlgorithmKeys getAlgorithm() {
        try {
            return AlgorithmKeys.valueOf((String) getValue(SSHKEYS_GENERATION_ALGORITHM_KEY));
        } catch (Exception e) {//NOSONAR
            return null;
        }
    }

    /**
     * Sets key: sSHKeyGeneration_Algorithm
     * 
     * @param algorithm
     *            (AlgorithmKeys)
     */
    public void setAlgorithm(final String algorithm) {
        this.setValue(SSHKEYS_GENERATION_ALGORITHM_KEY, algorithm);
    }

    private void setShortDescriptionLocal() {
        super.setShortDescription(ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION);
    }
}
