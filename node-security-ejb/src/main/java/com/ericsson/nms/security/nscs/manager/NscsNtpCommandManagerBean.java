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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.command.manager.NscsNtpCommandManager;
import com.ericsson.nms.security.nscs.api.command.manager.NscsNtpCommandManagerProcessor;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ntp.utility.NtpRemoveWorkflowData;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

/**
 * This class is used to start WorkflowInstance for NTP remove command on target nodes.
 *
 * @author zmalsiv
 *
 */
@Stateless
public class NscsNtpCommandManagerBean implements NscsNtpCommandManager {

    @EJB
    private NscsNtpCommandManagerProcessor nscsNtpCommandManagerProcessor;

    @EJB
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    /**
     * This method is used to start WorkflowInstance for removal of ntp server data on nodes.
     *
     * @param ntpRemoveWorkFlowDataList
     *            contains details of node and respective key Id's or server id's data to be removed on the target node.
     * @param jobStatusRecord
     *            The NTP Remove command jobStatusRecord
     *
     */
    @Override
    public void removeNtpServerData(final List<NtpRemoveWorkflowData> ntpRemoveWorkFlowDataList, final JobStatusRecord jobStatusRecord) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        int workflowId = 1;
        for (final NtpRemoveWorkflowData ntpRemoveWorkFlowData : ntpRemoveWorkFlowDataList) {
            final WfResult result = nscsNtpCommandManagerProcessor.executeNtpRemoveWorkflow(ntpRemoveWorkFlowData, jobStatusRecord, workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    /**
     * This method is used to start WorkflowInstance for configure Ntp server for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            The NTP configure command jobStatusRecord
     *
     */
    @Override
    public void configureNtpServer(final List<NormalizableNodeReference> nodes, final JobStatusRecord jobStatusRecord) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        int workflowId = 1;
        for (final NormalizableNodeReference normNode : nodes) {
            final WfResult result = nscsNtpCommandManagerProcessor.executeNtpConfigureWorkflow(normNode.getFdn(), jobStatusRecord, workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);

    }

}
