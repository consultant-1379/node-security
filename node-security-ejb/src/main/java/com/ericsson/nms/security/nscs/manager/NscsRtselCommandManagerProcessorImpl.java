/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.command.manager.NscsRtselCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.exception.RtselWfException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselConfig;
import com.ericsson.nms.security.nscs.rtsel.request.model.ServerConfig;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * This class is having the implementation of NscsRtselCommandManagerProcessor interface.
 *
 * @author xchowja
 */
@Stateless
public class NscsRtselCommandManagerProcessorImpl implements NscsRtselCommandManagerProcessor {

    @Inject
    private Logger logger;

    @EServiceRef
    WorkflowHandler wfHandler;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeActivateRtselSingleWf(final String nodeFdn, final NodeInfoDetails nodeInfoDetails, final NodeRtselConfig nodeRtselConfig,
            final JobStatusRecord jobStatusRecord, final int workflowId) {
        logger.debug("executeActivateRtselSingleWf() - nodeFdn: [{}]", nodeFdn);
        WfResult result = null;
        final String workFlowName = WorkflowNames.WORKFLOW_CPPACTIVATERTSEL.toString();
        final NodeReference nodeRef = new NodeRef(nodeFdn);

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.SYSLOG_SERVERS.toString());
        workflowVars.put(WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString(), nodeInfoDetails.getEntityProfileName());
        workflowVars.put(WorkflowParameterKeys.ENROLLMENT_MODE.toString(), nodeInfoDetails.getEnrollmentMode());
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(), nodeInfoDetails.getKeySize());
        workflowVars.put(WorkflowParameterKeys.SERVER_CONFIG.toString(), buildRtselServerConfiguration(nodeRtselConfig));
        workflowVars.put(WorkflowParameterKeys.MO_ATTRIBUTES_KEY_VALUES.toString(), getRtselMoAttributeValues(nodeRtselConfig));
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workFlowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RtselWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] For Activate RTSEL", workFlowName, nodeRef);

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeDeActivateRtselSingleWf(final String nodeFdn, final JobStatusRecord jobStatusRecord, final int workflowId) {
        logger.debug("executeDeactivateRtselSingleWf() - nodeFdn: [{}]", nodeFdn);
        WfResult result = null;
        final String workFlowName = WorkflowNames.WORKFLOW_CPPDEACTIVATERTSEL.toString();
        final NodeReference nodeRef = new NodeRef(nodeFdn);

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.MO_ATTRIBUTES_KEY_VALUES.toString(), getRtselDeActivateMoAttributeValues());
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workFlowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RtselWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] For DeActivate RTSEL", workFlowName, nodeRef);

        return result;
    }

    private Map<String, Map<String, Object>> getRtselMoAttributeValues(final NodeRtselConfig nodeRtselConfig) {
        final Map<String, Object> rtselMoAttributesValueMap = new HashMap<String, Object>();
        if (nodeRtselConfig.getConnAttemptTimeOut() != null) {
            rtselMoAttributesValueMap.put(RtselConstants.CONN_TIMEOUT, nodeRtselConfig.getConnAttemptTimeOut());
        }
        if (nodeRtselConfig.getExtServerLogLevel() != null) {
            rtselMoAttributesValueMap.put(RtselConstants.EXT_SERVER_LOGLEVEL, nodeRtselConfig.getExtServerLogLevel());
        }
        if (nodeRtselConfig.getExtServerAppName() != null) {
            rtselMoAttributesValueMap.put(RtselConstants.EXT_SERVER_APPNAME, nodeRtselConfig.getExtServerAppName());
        }
        rtselMoAttributesValueMap.put(RtselConstants.FEATURESTATE, RtselConstants.FEATURESTATE_ACTIVATED);

        final Map<String, Map<String, Object>> rtselMoAttributeValues = new HashMap<String, Map<String, Object>>();
        rtselMoAttributeValues.put(Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog.toString(), rtselMoAttributesValueMap);

        return rtselMoAttributeValues;
    }

    private Map<String, Map<String, Object>> getRtselDeActivateMoAttributeValues() {
        final Map<String, Object> rtselMoAttributesValueMap = new HashMap<String, Object>();
        rtselMoAttributesValueMap.put(RtselConstants.FEATURESTATE, RtselConstants.FEATURESTATE_DEACTIVATED);
        final Map<String, Map<String, Object>> rtselMoAttributeValues = new HashMap<String, Map<String, Object>>();
        rtselMoAttributeValues.put(Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog.toString(), rtselMoAttributesValueMap);

        return rtselMoAttributeValues;
    }

    private List<Map<String, Object>> buildRtselServerConfiguration(final NodeRtselConfig nodeRtselConfig) {
        final List<Map<String, Object>> rtselServerConfigWorkflowVars = new ArrayList<Map<String, Object>>();
        for (final ServerConfig serverConfig : nodeRtselConfig.getServerConfig()) {
            final Map<String, Object> rtselServerConfigParams = new HashMap<String, Object>();
            if (serverConfig.getExtServerAddress() != null) {
                rtselServerConfigParams.put(RtselConstants.EXT_SERVER_ADDRESS, serverConfig.getExtServerAddress());
            }
            if (serverConfig.getExtServProtocol() != null) {
                rtselServerConfigParams.put(RtselConstants.EXT_SERVER_PROTOCOL, serverConfig.getExtServProtocol());
            }
            if (serverConfig.getServerName() != null) {
                rtselServerConfigParams.put(RtselConstants.EXT_SERVER_NAME, serverConfig.getServerName());
            }
            rtselServerConfigWorkflowVars.add(rtselServerConfigParams);
        }

        return rtselServerConfigWorkflowVars;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeRtselDeleteServerWfs(final String nodeFdn, final Set<String> serverNames, final JobStatusRecord jobStatusRecord,
            final int workflowId) {

        logger.debug("executeRtselDeleteServerWfs()-nodeFdn: [{}]", nodeFdn);
        WfResult result = null;
        final String workFlowName = WorkflowNames.WORKFLOW_CPP_RTSEL_DELETE_SERVER.toString();
        final NodeReference nodeRef = new NodeRef(nodeFdn);
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.SERVER_NAMES.toString(), serverNames);
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workFlowName, workflowVars, jobStatusRecord, workflowId);

        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RtselWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] For RTSEL delete server(s)", workFlowName, nodeRef);

        return result;
    }
}
