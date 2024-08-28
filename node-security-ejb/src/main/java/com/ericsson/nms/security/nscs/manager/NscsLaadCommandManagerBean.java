/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
import com.ericsson.nms.security.nscs.api.command.manager.NscsLaadCommandManager;
import com.ericsson.nms.security.nscs.api.command.manager.NscsLaadCommandManagerProcessor;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

/**
 * This class is having the implementation of NscsLaadCommandManager interface.
 *
 * @author tcsgoja
 *
 */
@Stateless
public class NscsLaadCommandManagerBean implements NscsLaadCommandManager {

    @EJB
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    @EJB
    private NscsLaadCommandManagerProcessor nscsLaadCommandManagerProcessor;

    /**
     * Executes the executeLaadFilesDistributeWorkFlow for the valid nodes
     *
     * @param validNodeList
     *            Valid nodes to process in executeLaadFilesDistributeWorkflow
     * @param jobStatusRecord
     *            provides job status for each node
     * @author xkihari
     */

    @Override
    public void executeLaadFilesDistributeWorkFlow(final List<String> validNodeList, final JobStatusRecord jobStatusRecord) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        int workflowId = 1;
        for (String nodeFDN : validNodeList) {
            final WfResult result = nscsLaadCommandManagerProcessor.executeLaadFilesDistributeWorkflow(nodeFDN, jobStatusRecord, workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }
}
