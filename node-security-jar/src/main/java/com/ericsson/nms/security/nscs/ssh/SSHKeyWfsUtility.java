/*------------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.ssh;

import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED;
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED;

/**
 * This class maps ssh command parameters into ssh workflow parameters.
 * It builds the ssh workflow map needed by task handlers of the workflow when it runs
 */
public class SSHKeyWfsUtility {

    public SSHKeyWfsConfigurationDto configureSshKeyWorkflow(final SSHKeyRequestDto request) {

        SSHKeyWfsConfigurationDto sshKeyRequestWfsConfiguration = new SSHKeyWfsConfigurationDto();

        sshKeyRequestWfsConfiguration.setNodeFdn(request.getFdn());
        sshKeyRequestWfsConfiguration.setWorkflowName(WorkflowNames.WORKFLOW_SSHKeyGeneration.toString());

        String sshKeyOperation = request.getSshkeyOperation();
        sshKeyRequestWfsConfiguration.getWorkflowParams().put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(),
                request.getAlgorithm());
        sshKeyRequestWfsConfiguration.getWorkflowParams().put(WorkflowParameterKeys.KEY_SSHKEYS_OPERATION.toString(),
                sshKeyOperation);

        /* It needs to guarantee backward compatibility: old version was using KEY_SSHKEYS_GENERATION_ISCREATE
        *  to distinguish ssh key command
        */
        String isCreate = null;
        if (sshKeyOperation.equals(SSH_KEY_TO_BE_CREATED)) {
            isCreate= "true";
        } else if (sshKeyOperation.equals(SSH_KEY_TO_BE_UPDATED)) {
            isCreate= "false";
        }
        sshKeyRequestWfsConfiguration.getWorkflowParams().put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ISCREATE.toString(),
                isCreate);

        // add new parameters to manage also the IPOS-OI nodes
        sshKeyRequestWfsConfiguration.getWorkflowParams().put(WorkflowParameterKeys.MOM_TYPE.toString(), request.getMomType());
        sshKeyRequestWfsConfiguration.getWorkflowParams().put(WorkflowParameterKeys.IS_MODELED_SSH_KEY.toString(), request.isModeledSshKey());

        return sshKeyRequestWfsConfiguration;
    }
}
