package com.ericsson.nms.security.nscs.cpp.level.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.SetSecurityLevelException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelProcessor;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequest;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequestType;
import com.ericsson.nms.security.nscs.cpp.level.processor.qualifiers.SecurityLevelRequestType;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

/**
 * SecLevelProcessor implementation that starts a security level activation for a single node
 *
 * @author eabdsin
 */
@SecurityLevelRequestType(SecLevelRequestType.ACTIVATE_SECURITY_LEVEL)
public class ActivateSecurityLevelCommandHandlerImpl implements SecLevelProcessor {

    @Inject
    Logger log;

    @Inject
    CppSecurityService cppService;

    @Inject
    SystemRecorder systemRecorder;

    @EServiceRef
    WorkflowHandler wfHandler;

    @Inject
    private NscsLogger nscsLogger;

    public static final String ACTIVATE_LEVEL_2_WORKFLOW_ID = WorkflowNames.WORKFLOW_CPPActivateSL2.toString(); //"CPPActivateSL2";

    @Override
    public void processCommand(final SecLevelRequest request) {

        processActivateSecurityLevelCommand(request, null, 1);
    }

    private boolean isLevel12Needed(final SecLevelRequest cmd) {
        return (cmd.getCurrentSecurityLevel() == SecurityLevel.LEVEL_1
                && (cmd.getRequiredSecurityLevel() == SecurityLevel.LEVEL_2 || cmd.getRequiredSecurityLevel() == SecurityLevel.LEVEL_3));
    }

    // Instantiate a SL1 -> SL2 workflow
    private void instantiateActivateLevel2Workflow(final NodeReference node) {

        try {
            final WorkflowInstance instance = wfHandler.startWorkflowInstance(node, ACTIVATE_LEVEL_2_WORKFLOW_ID);
            log.info("Activated SecurityLevel2 workflow via command [workflowDefinitionId : {}]",
                    (instance != null ? instance.getWorkflowDefinitionId() : " "));

        } catch (final RuntimeException e) {
            log.warn("Failed to activate security on the node with fdn = [{}]", node.getFdn());
            throw new SetSecurityLevelException(e);
        }

    }

    @Override
    public WfResult processCommand(final SecLevelRequest request, final JobStatusRecord jobStatusRecord, final int workflowId) {

        return processActivateSecurityLevelCommand(request, jobStatusRecord, workflowId);
    }

    // Instantiate a SL1 -> SL2 workflow
    WfResult instantiateActivateLevel2Workflow(final NodeReference node, final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        // Set Corba peers trust category for the trust distribution
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
        try {

            result = wfHandler.getScheduledWorkflowInstanceResult(node, ACTIVATE_LEVEL_2_WORKFLOW_ID, workflowVars, jobStatusRecord, workflowId);

            if (result != null) {
                nscsLogger.workFlowStarted(ACTIVATE_LEVEL_2_WORKFLOW_ID, result.getWfWakeId().toString(), node.getFdn(), "");

            }

        } catch (final RuntimeException e) {
            log.error("Failed to activate security on the node with fdn = [{}]", node.getFdn());
            throw new SetSecurityLevelException(e);
        }

        return result;

    }

    private WfResult processActivateSecurityLevelCommand(final SecLevelRequest request, final JobStatusRecord jobStatusRecord, final int workflowId) {
        if (request == null || request.getNodeFDN() == null) {
            throw new IllegalArgumentException("Invalid Security level request");

        }

        WfResult result = null;
        nscsLogger.info(
                "Starting process for security level activation of node, name : {}, fdn : {}, Current SecurityLevel : {}, Required SecurityLevel : {}",
                request.getNodeName(), request.getNodeFDN(), request.getCurrentSecurityLevel(), request.getRequiredSecurityLevel());

        systemRecorder.recordSecurityEvent("Node Security Service - Activating the Node Security level",
                "Starting the Processing of Activate Security Command",
                "node name .." + request.getNodeName() + "node fdn .." + request.getNodeFDN() + "   node currently level .."
                        + request.getCurrentSecurityLevel() + "   node required level.." + request.getRequiredSecurityLevel(),
                "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.NOTICE, "IN-PROGRESS");

        if (isLevel12Needed(request)) {

            final NodeReference nodeReference = new NodeRef(request.getNodeFDN());

            if (jobStatusRecord != null) {
                result = instantiateActivateLevel2Workflow(nodeReference, jobStatusRecord, workflowId);
            } else {
                instantiateActivateLevel2Workflow(nodeReference);
            }

            log.debug("Node : {}, needs security level 2 activated, starting workflow", request.getNodeFDN());

        } else {
            log.debug("Level 2 does not need to be set for node : {}, Current SecurityLevel : {}", request.getNodeFDN(),
                    request.getCurrentSecurityLevel());
        }

        log.info("Finished process for security level activation of node : {}", request.getNodeName());

        return result;
    }

}
