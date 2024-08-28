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
package com.ericsson.nms.security.nscs.workflow.task.node.attribute;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.ConfigureEnmSshKeyTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;
import com.ericsson.oss.services.security.nscs.workflow.task.util.SshKeyWorkflowHelper;

/**
 * <p>
 * Task handler for WorkflowTaskType.CONFIGURE_ENM_SSH_KEY. Configure the SSH key on ENM NetworkElementSecurity MO.
 * </p>
 */
@WFTaskType(WorkflowTaskType.CONFIGURE_ENM_SSH_KEY)
@Local(WFTaskHandlerInterface.class)
public class ConfigureEnmSshKeyTaskHandler implements WFQueryTaskHandler<ConfigureEnmSshKeyTask>, WFTaskHandlerInterface {

    private static final String SKIPPED = "SKIPPED";
    private static final String UPDATED_ON_ENM = "UPDATED_ON_ENM";

    private static final String MISSING_MO_OF_TYPE_FORMAT = "Missing MO of type [%s]";
    private static final String WRONG_ALGORITHM_KEYS_FORMAT = "Wrong AlgorithmKeys value [%s]";

    private String result;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Inject
    private SshKeyWorkflowHelper sshKeyWorkflowHelper;

    @Override
    public String processTask(final ConfigureEnmSshKeyTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        try {
            final NormalizableNodeReference normalizedNodeRef = readerService.getNormalizedNodeReference(task.getNode());
            nscsLogger.info(task, "From task : normalizedRootFdn [{}] targetType [{}] targetModelIdentity [{}]", normalizedNodeRef.getFdn(),
                    normalizedNodeRef.getNeType(), normalizedNodeRef.getOssModelIdentity());

            processConfigureEnmSshKey(task, normalizedNodeRef);
        } catch (final WorkflowTaskException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, e.getMessage());
            throw e;
        } catch (final Exception e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, NscsLogger.stringifyException(e));
            throw e;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", getResult());
        return getResult();
    }

    /**
     * Process the configuration of ENM SSH key for the given task and the given node.
     * 
     * @param task
     *            the task.
     * @param normalizedNodeRef
     *            the normalized node reference.
     */
    private void processConfigureEnmSshKey(final ConfigureEnmSshKeyTask task, final NormalizableNodeReference normalizedNodeRef) {

        if (!SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED.equals(task.getSshkeyOperation())) {
            final ManagedObject networkElementSecurityMO = validateConfigureEnmSshKey(task, normalizedNodeRef);

            configureEnmSshKey(task, networkElementSecurityMO);
        } else {
            setResult(SKIPPED);
        }
    }

    /**
     * Validate the configuration of ENM SSH key for the given task and the given node.
     * 
     * @param task
     *            the task.
     * @param normalizedNodeRef
     *            the normalized node reference.
     * @return the NetworkElementSecurity MO.
     * @throws {@link
     *             WorkflowTaskException} if validation fails.
     */
    private ManagedObject validateConfigureEnmSshKey(final ConfigureEnmSshKeyTask task, final NormalizableNodeReference normalizedNodeRef) {

        final ManagedObject networkElementMO = nscsDpsUtils.getNormalizedRootMo(normalizedNodeRef);
        final ManagedObject networkElementSecurityMO = nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO);
        if (networkElementSecurityMO == null) {
            final String errorMessage = String.format(MISSING_MO_OF_TYPE_FORMAT,
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type());
            nscsLogger.error(task, "{} for node [{}]", errorMessage, normalizedNodeRef.getName());
            throw new WorkflowTaskException(errorMessage);
        }
        return networkElementSecurityMO;
    }

    /**
     * Configure ENM SSH key for the given task on the given NetworkElementSecurity MO.
     * 
     * @param task
     *            the task.
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     * @throws {@link
     *             WorkflowTaskException} if algorithm or SSH keys are invalid.
     */
    private void configureEnmSshKey(final ConfigureEnmSshKeyTask task, final ManagedObject networkElementSecurityMO) {
        AlgorithmKeys algorithmKey;
        try {
            algorithmKey = AlgorithmKeys.valueOf(task.getAlgorithm());
        } catch (final Exception e) {
            final String errorMessage = String.format(WRONG_ALGORITHM_KEYS_FORMAT, task.getAlgorithm());
            nscsLogger.error(task, e, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
        nscsLogger.info("Extracting KeyPair for algorithm {} key size {} from output parameters", algorithmKey.getAlgorithm(),
                algorithmKey.getKeySize());
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Output params not yet set! ";
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        final String publicSSHKey = (String) outputParams.get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString());
        final String encryptedPrivateSSHKey = (String) outputParams.get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString());

        updateNetworkElementSecurityMO(task, networkElementSecurityMO, algorithmKey, publicSSHKey, encryptedPrivateSSHKey);
    }

    /**
     * Update the NetworkElementSecurity MO.
     * 
     * @param task
     *            the task.
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     * @param algorithmKey
     *            the algorithm and key size enumerated.
     * @param publicSSHKey
     *            the public key.
     * @param encryptedPrivateSSHKey
     *            the encrypted private key.
     */
    private void updateNetworkElementSecurityMO(final ConfigureEnmSshKeyTask task, final ManagedObject networkElementSecurityMO,
            final AlgorithmKeys algorithmKey, final String publicSSHKey, final String encryptedPrivateSSHKey) {
        final String updateResult = sshKeyWorkflowHelper.updateNetworkElementSecurityMO(networkElementSecurityMO, algorithmKey.toString(),
                publicSSHKey, encryptedPrivateSSHKey);
        setResult(UPDATED_ON_ENM);
        nscsLogger.info(task, updateResult);
    }

    /**
     * @return the result
     */
    private String getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    private void setResult(final String result) {
        this.result = result;
    }

}
