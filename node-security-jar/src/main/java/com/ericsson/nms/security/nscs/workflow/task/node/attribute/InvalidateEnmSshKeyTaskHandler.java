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

import javax.ejb.Local;
import javax.inject.Inject;

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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.InvalidateEnmSshKeyTask;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;
import com.ericsson.oss.services.security.nscs.workflow.task.util.SshKeyWorkflowHelper;

/**
 * <p>
 * Task handler for WorkflowTaskType.INVALIDATE_ENM_SSH_KEY. For create or update operations only, invalidate SSH keys in NetworkElementSecurity MO
 * for a node.
 * </p>
 */
@WFTaskType(WorkflowTaskType.INVALIDATE_ENM_SSH_KEY)
@Local(WFTaskHandlerInterface.class)
public class InvalidateEnmSshKeyTaskHandler implements WFQueryTaskHandler<InvalidateEnmSshKeyTask>, WFTaskHandlerInterface {

    private static final String SKIPPED = "SKIPPED";
    private static final String UPDATED_ON_ENM = "UPDATED_ON_ENM";

    private static final String MISSING_MO_OF_TYPE_FORMAT = "Missing MO of type [%s]";

    private String result;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private SshKeyWorkflowHelper sshKeyWorkflowHelper;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public String processTask(final InvalidateEnmSshKeyTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        try {
            final NormalizableNodeReference normalizedNodeRef = readerService.getNormalizedNodeReference(task.getNode());
            nscsLogger.info(task, "From task : normalizedRootFdn [{}] targetType [{}] targetModelIdentity [{}]", normalizedNodeRef.getFdn(),
                    normalizedNodeRef.getNeType(), normalizedNodeRef.getOssModelIdentity());

            processInvalidateEnmSshKey(task, normalizedNodeRef);
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
     * Process the invalidate of ENM SSH key for the given task and the given node.
     * 
     * @param task
     *            the task.
     * @param normalizedNodeRef
     *            the normalized node reference.
     * @throws {@link
     *             WorkflowTaskException} if NetworkElementSecurity MO does not exist.
     */
    private void processInvalidateEnmSshKey(final InvalidateEnmSshKeyTask task, final NormalizableNodeReference normalizedNodeRef) {

        if (!SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED.equals(task.getSshkeyOperation())) {

            final ManagedObject networkElementMO = nscsDpsUtils.getNormalizedRootMo(normalizedNodeRef);
            final ManagedObject networkElementSecurityMO = nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO);
            if (networkElementSecurityMO != null) {
                invalidateEnmSshKey(task, networkElementSecurityMO);
            } else {
                final String errorMessage = String.format(MISSING_MO_OF_TYPE_FORMAT,
                        Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type());
                nscsLogger.error(task, "{} for node [{}]", errorMessage, normalizedNodeRef.getName());
                throw new WorkflowTaskException(errorMessage);
            }
        } else {
            setResult(SKIPPED);
        }
    }

    /**
     * Invalidate ENM SSH key for the given task from the given NetworkElementSecurity MO.
     * 
     * @param task
     *            the task.
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     */
    private void invalidateEnmSshKey(final InvalidateEnmSshKeyTask task, final ManagedObject networkElementSecurityMO) {
        final String updateResult = sshKeyWorkflowHelper.updateNetworkElementSecurityMO(networkElementSecurityMO, null,
                SSHKeyGenConstants.SSH_KEY_INVALID, SSHKeyGenConstants.SSH_KEY_INVALID);
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
