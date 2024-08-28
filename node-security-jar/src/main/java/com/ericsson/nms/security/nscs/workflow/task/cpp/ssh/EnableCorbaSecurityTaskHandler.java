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
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.mediation.sec.model.SSHCommandJob;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableCorbaSecurityTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_ENABLE_CORBA_SECURITY
 * </p>
 * <p>
 * Enable corba security on the node
 * </p>
 * @author  emaynes on 16/06/2014.
 */
@WFTaskType(WorkflowTaskType.CPP_ENABLE_CORBA_SECURITY)
@Local(WFTaskHandlerInterface.class)
public class EnableCorbaSecurityTaskHandler implements WFActionTaskHandler<EnableCorbaSecurityTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    @Modeled
    private EventSender<SSHCommandJob> commandJobSender;

    @Override
    public void processTask(final EnableCorbaSecurityTask task) {
    	nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(task.getNode());
        if ( normRef == null ){
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Could not find normalized node reference for " + task.getNode() + ". Can't send SSH command to node.");
            throw new InvalidNodeException("Not a valid node: Cannot find normalized reference.");
        }
        nscsLogger.debug("Found normalized node reference : " + normRef.getFdn());

        final String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normRef.getName()).fdn();

        final SSHCommandJob sshCommandJob = new SSHCommandJob();
        sshCommandJob.setJobId(String.format("%s-%s", task.getNode().getName(), UUID.randomUUID().toString()));
        sshCommandJob.setCommandToExecute(CPPCommands.SECMODE_L_2.toString());
        sshCommandJob.setNodeAddress(networkElementSecurityFdn);
        
        nscsLogger.workFlowTaskHandlerOngoing(task, "Execution of command: " + CPPCommands.SECMODE_L_2.toString() + "on node: " + networkElementSecurityFdn);
        commandJobSender.send(sshCommandJob);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Command [" + sshCommandJob + "] sent to the node [" + task.getNodeFdn() + "] with Success");
    }
}
