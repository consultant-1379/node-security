package com.ericsson.nms.security.nscs.cpp.level.processor;

import java.util.HashMap;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.exception.SetSecurityLevelException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelProcessor;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequest;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequestType;
import com.ericsson.nms.security.nscs.cpp.level.processor.qualifiers.SecurityLevelRequestType;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

/**
 * SecLevelProcessor implementation that starts a security level deactivation for a list of nodes
 *
 * @author eabdsin
 */
@SecurityLevelRequestType(SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL)
public class DeactivateStatusCommandHandlerImpl implements SecLevelProcessor {

    public static final String DEACTIVATE_LEVEL_2_WORKFLOW_ID = "CPPDeactivateSL2";

    @Inject
    Logger log;

    @Inject
    SystemRecorder systemRecorder;

    @EServiceRef
    WorkflowHandler wfHandler;

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    @Override
    public void processCommand(final SecLevelRequest cmd) {

        processDeactivateSecurityLevelCommand(cmd, null, 1);
    }

    private void instantiateDeactivateLevel2Workflow(final NodeReference node) {

        try {
            final WorkflowInstance instance = wfHandler.startWorkflowInstance(node, DEACTIVATE_LEVEL_2_WORKFLOW_ID);
            log.info("Deactivated SecurityLevel 2 workflow via command [workflowDefinitionId : {}]",
                    (instance != null ? instance.getWorkflowDefinitionId() : " "));

        } catch (final RuntimeException e) {
            log.warn("Failed to deactivate security on the node with fdn = [{}]", node.getFdn());
            throw new SetSecurityLevelException(e);
        }

    }

    @Override
    public WfResult processCommand(final SecLevelRequest cmd, final JobStatusRecord jobStatusRecord, final int workflowId) {

        return processDeactivateSecurityLevelCommand(cmd, jobStatusRecord, workflowId);
    }

    private WfResult instantiateDeactivateLevel2Workflow(final NodeReference node, final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(node, DEACTIVATE_LEVEL_2_WORKFLOW_ID, new HashMap<String, Object>(),
                    jobStatusRecord, workflowId);
            if (result != null) {
                nscsLogger.workFlowStarted(DEACTIVATE_LEVEL_2_WORKFLOW_ID, result.getWfWakeId().toString(), node.getFdn(), "");
            }

        } catch (final RuntimeException e) {
            log.error("Failed to deactivate security on the node with fdn = [{}]", node.getFdn());
            throw new SetSecurityLevelException(e);
        }
        return result;
    }

    private WfResult processDeactivateSecurityLevelCommand(final SecLevelRequest cmd, final JobStatusRecord jobStatusRecord, final int workflowId) {

        log.info(
                "Starting process for security level de-activation of node, name : {}, fdn : {}, Current SecurityLevel : {}, Required SecurityLevel : {}",
                cmd.getNodeName(), cmd.getNodeFDN(), cmd.getCurrentSecurityLevel(), cmd.getRequiredSecurityLevel());

        systemRecorder.recordSecurityEvent("Node Security Service - Deactivating the Node Security level",
                "Starting the Processing of Activate Security Command", "anonymous", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.NOTICE,
                "IN-PROGRESS");

        final NodeReference nodeReference = new NodeRef(cmd.getNodeFDN());
        WfResult result = null;

        if (jobStatusRecord != null) {
            result = instantiateDeactivateLevel2Workflow(nodeReference, jobStatusRecord, workflowId);
        } else {
            instantiateDeactivateLevel2Workflow(nodeReference);
        }

        log.info("Finished process for security level de-activation of node : {}", cmd.getNodeName());
        return result;
    }
}