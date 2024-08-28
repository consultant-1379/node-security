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

import java.util.Arrays;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.VerifySshKeyTask;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

/**
 * <p>
 * Task handler for WorkflowTaskType.VERIFY_SSH_KEY. For create/update operations only, verify SSH keys in NetworkElementSecurity MO for a node by
 * reading FROM_DELEGATE attributes.
 * </p>
 */
@WFTaskType(WorkflowTaskType.VERIFY_SSH_KEY)
@Local(WFTaskHandlerInterface.class)
public class VerifySshKeyTaskHandler implements WFQueryTaskHandler<VerifySshKeyTask>, WFTaskHandlerInterface {

    private static final String SKIPPED = "SKIPPED";
    private static final String SUCCESSFUL = "SUCCESSFUL";
    private static final String FAILED = "FAILED";

    private static final String MISSING_MO_OF_TYPE_FORMAT = "Missing MO of type [%s]";
    private static final String UNSUPPORTED_MOM_TYPE_FORMAT = "Unsupported MOM type [%s]";
    private static final String MISSING_MO_OF_TYPE_AND_NAME_FORMAT = "Missing MO of type [%s] and name [%s]";

    private String result;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public String processTask(final VerifySshKeyTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        try {
            final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(task.getNode());
            nscsLogger.info(task, "From task : mirrorRootFdn [{}] targetType [{}] targetModelIdentity [{}]", normalizableNodeRef.getFdn(),
                    normalizableNodeRef.getNeType(), normalizableNodeRef.getOssModelIdentity());

            processVerifySshKey(task, normalizableNodeRef);
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
     * Process the verification of SSH key for the given task and the given node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @throws {@link
     *             WorkflowTaskException} if NetworkElementSecurity MO does not exist or MOM type is not supported.
     */
    private void processVerifySshKey(final VerifySshKeyTask task, final NormalizableNodeReference normalizableNodeRef) {

        if (!SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED.equals(task.getSshkeyOperation())) {

            final ManagedObject networkElementMO = nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef);
            final ManagedObject networkElementSecurityMO = nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO);
            if (networkElementSecurityMO != null) {
                if (NscsCapabilityModelConstants.NSCS_EOI_MOM.equals(task.getMomType())) {
                    cbpOiVerifySshKey(task, normalizableNodeRef, networkElementSecurityMO);
                } else {
                    final String errorMessage = String.format(UNSUPPORTED_MOM_TYPE_FORMAT, task.getMomType());
                    nscsLogger.error(task, errorMessage);
                    throw new WorkflowTaskException(errorMessage);
                }
            } else {
                final String errorMessage = String.format(MISSING_MO_OF_TYPE_FORMAT,
                        Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type());
                nscsLogger.error(task, "{} for node [{}]", errorMessage, normalizableNodeRef.getName());
                throw new WorkflowTaskException(errorMessage);
            }
        } else {
            setResult(SKIPPED);
        }
    }

    /**
     * Verify SSH key for the given task on the given CBP-OI node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     */
    private void cbpOiVerifySshKey(final VerifySshKeyTask task, final NormalizableNodeReference normalizableNodeRef,
            final ManagedObject networkElementSecurityMO) {
        final String userName = networkElementSecurityMO.getAttribute(ModelDefinition.NetworkElementSecurity.SECURE_USER_NAME);
        final ManagedObject userMO = nscsDpsUtils.getUserMO(normalizableNodeRef, userName);
        if (userMO == null) {
            final String errorMessage = String.format(MISSING_MO_OF_TYPE_AND_NAME_FORMAT, ModelDefinition.USER_TYPE, userName);
            nscsLogger.error(task, "{} for node [{}]", errorMessage, normalizableNodeRef.getFdn());
            throw new WorkflowTaskException(errorMessage);
        }
        try {
            userMO.getAttributes(Arrays.asList(ModelDefinition.USER_GROUP_ID_ATTR, ModelDefinition.USER_USER_ID_ATTR,
                    ModelDefinition.USER_DEFAULT_SHELL_ATTR, ModelDefinition.USER_HOME_DIRECTORY_ATTR));
            setResult(SUCCESSFUL);
        } catch (final Exception e) {
            final String errorMessage = String.format("Failed get attributes from delegate for MO [%s] name [%s] on node [%s]",
                    ModelDefinition.USER_TYPE, userName, normalizableNodeRef.getFdn());
            nscsLogger.error(task, e, errorMessage);
            setResult(FAILED);
        }

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
