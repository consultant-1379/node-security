/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.manager;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.command.manager.NscsNtpCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.enums.NtpRemoveInputType;
import com.ericsson.nms.security.nscs.api.exception.NtpConfigureOrRemoveWfException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ntp.utility.NtpRemoveWorkflowData;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * This class is having the implementation of NscsNtpCommandManagerProcessor interface.
 *
 * @author zmalsiv
 *
 */
@Stateless
public class NscsNtpCommandManagerProcessorImpl implements NscsNtpCommandManagerProcessor {

    @Inject
    private NscsCapabilityModelService capabilityModel;

    @Inject
    private Logger logger;

    @EServiceRef
    private WorkflowHandler wfHandler;

    @Inject
    private NscsCMReaderService reader;

    @Override
    public WfResult executeNtpRemoveWorkflow(final NtpRemoveWorkflowData ntpRemoveWorkflowData, final JobStatusRecord jobStatusRecord,
                                             final int workflowId) {

        WfResult result = null;
        final Map<String, Object> workflowVars = new HashMap<>();
        String ntpRemoveInputType = WorkflowParameterKeys.NTP_KEY_IDS.toString();
        if (NtpRemoveInputType.SERVERIDS.equals(ntpRemoveWorkflowData.getNtpRemoveInputType())) {
            ntpRemoveInputType = WorkflowParameterKeys.NTP_SERVER_IDS.toString();
        }
        workflowVars.put(ntpRemoveInputType, ntpRemoveWorkflowData.getKeyIdOrServerIdList());
        logger.debug("{} : [{}] for node name [{}] and  workflowVars : [{}]", ntpRemoveInputType, ntpRemoveWorkflowData.getKeyIdOrServerIdList(),
                ntpRemoveWorkflowData.getNodeFdn(), workflowVars);
        final NodeReference nodeReference = new NodeRef(ntpRemoveWorkflowData.getNodeFdn());
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeReference);
        String workflowName = null;
        workflowName = capabilityModel.getNtpRemoveWorkflow(normNode);
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeReference, workflowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new NtpConfigureOrRemoveWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] ", workflowId, nodeReference);
        return result;
    }

    @Override
    public WfResult executeNtpConfigureWorkflow(final String nodeFdn, final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        final NodeReference nodeReference = new NodeRef(nodeFdn);
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeReference);
        final String workflowName = capabilityModel.getNtpConfigureWorkflow(normNode);
        final Map<String, Object> workflowVars = new HashMap<>();
        workflowVars.put(WorkflowParameterKeys.NTP_KEY.toString(), null);
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeReference, workflowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new NtpConfigureOrRemoveWfException();
        }
        logger.debug("Got scheduled workflow for child workflowName [{}] for node: [{}] ", workflowName, nodeReference);
        return result;
    }

}
