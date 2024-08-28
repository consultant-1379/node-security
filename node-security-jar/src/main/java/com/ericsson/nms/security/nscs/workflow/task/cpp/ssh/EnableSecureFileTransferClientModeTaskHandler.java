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
package com.ericsson.nms.security.nscs.workflow.task.cpp.ssh;

import java.util.UUID;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.cpp.model.CPPCommands;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.SecurityLevelCommonUtils;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.iscf.IscfConfigurationBean;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.mediation.sec.model.SSHCommandJob;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableSecureFileTransferClientModeTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_ENABLE_SECURE_FILE_TRANSFER_CLIENT_MODE
 * </p>
 * <p>
 * Enables secure file transfer mode on the node
 * </p>
 * 
 * @author emaynes on 16/06/2014.
 */
@WFTaskType(WorkflowTaskType.CPP_ENABLE_SECURE_FILE_TRANSFER_CLIENT_MODE)
@Local(WFTaskHandlerInterface.class)
public class EnableSecureFileTransferClientModeTaskHandler
        implements WFActionTaskHandler<EnableSecureFileTransferClientModeTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    @Modeled
    private EventSender<SSHCommandJob> commandJobSender;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    protected IscfConfigurationBean config;

    @Inject
    protected SecurityLevelCommonUtils SecurityLevelCommonUtils;

    @Override
    public void processTask(final EnableSecureFileTransferClientModeTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normRef = readerService.getNormalizableNodeReference(task.getNode());
        if (normRef == null) {
            final String errorMessage = String
                    .format("Failed get normalized node ref for [" + task.getNode() + "], Can't send SSH command to node.");
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new InvalidNodeException(errorMessage);
        }
        nscsLogger.debug(task, "Found normalized node reference [" + normRef.getFdn() + "]");

        final String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity
                .withNames(normRef.getName()).fdn();

        final String ipLogonServerAddress = config.getIscfLogonServerAddress();

        nscsLogger.info(task, "ipLogonServerAddress :::::::::::::::::::" + ipLogonServerAddress);

        final String managedElementDataFdn = SecurityLevelCommonUtils.getManagedElementDataFdn(normRef);
        nscsLogger.info(task, "ManagedElementData FDN [" + managedElementDataFdn + "]");

        final String updateMessage = String.format("attribute [%s] of ManagedElementData [%s] to value [%s]",
                ModelDefinition.ManagedElementData.LOGON_SERVER_ADDRESS, managedElementDataFdn, ipLogonServerAddress);

        nscsLogger.info(task, "Updating " + updateMessage);

        writer.withSpecification(managedElementDataFdn)
                .setAttribute(ModelDefinition.ManagedElementData.LOGON_SERVER_ADDRESS, ipLogonServerAddress).updateMO();

        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);

        final SSHCommandJob sshCommandJob = new SSHCommandJob();
        sshCommandJob.setJobId(String.format("%s-%s", task.getNode().getName(), UUID.randomUUID().toString()));
        sshCommandJob.setCommandToExecute(CPPCommands.SECMODE_F_S.toString());
        sshCommandJob.setNodeAddress(networkElementSecurityFdn);

        nscsLogger.info(task, "Sending SSH command [" + sshCommandJob + "]");
        commandJobSender.send(sshCommandJob);
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully sent SSH command " + sshCommandJob);

        nscsLogger.info("Command [" + sshCommandJob + "] sent to the node [" + task.getNodeFdn() + "].");

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "SFTP enabled on node.");
    }
}
