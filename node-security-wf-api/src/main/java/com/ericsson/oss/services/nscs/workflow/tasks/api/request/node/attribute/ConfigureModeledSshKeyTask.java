/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task to configure modeled SSH keys on a node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY
 * </p>
 */
public class ConfigureModeledSshKeyTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -6176292308921934435L;

    public static final String SSH_KEY_OPERATION_KEY = WorkflowParameterKeys.KEY_SSHKEYS_OPERATION.toString();
    public static final String SSH_KEYS_GENERATION_ALGORITHM_KEY = WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString();
    public static final String IS_MODELED_SSH_KEY_KEY = WorkflowParameterKeys.IS_MODELED_SSH_KEY.toString();
    public static final String MOM_TYPE_KEY = WorkflowParameterKeys.MOM_TYPE.toString();

    public static final String SHORT_DESCRIPTION = "Modeled SSH Key";

    public ConfigureModeledSshKeyTask() {
        super(WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY);
        setShortDescriptionLocal();
    }

    public ConfigureModeledSshKeyTask(final String nodeName) {
        super(WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY, nodeName);
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
     * @return the isModeledSSHKey
     */
    public String getIsModeledSshKey() {
        return (String) getValue(IS_MODELED_SSH_KEY_KEY);
    }

    /**
     * @param isModeledSshKey
     *            the isModeledSshKey as String to set
     */
    public void setIsModeledSshKey(final String isModeledSshKey) {
        setValue(IS_MODELED_SSH_KEY_KEY, isModeledSshKey);
    }

    /**
     * @return the momType
     */
    public String getMomType() {
        return (String) getValue(MOM_TYPE_KEY);
    }

    /**
     * @param momType
     *            the momType to set
     */
    public void setMomType(final String momType) {
        this.setValue(MOM_TYPE_KEY, momType);
    }

    private void setShortDescriptionLocal() {
        super.setShortDescription(ConfigureModeledSshKeyTask.SHORT_DESCRIPTION);
    }

}
