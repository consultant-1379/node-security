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
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.command.manager.NscsLaadCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.exception.LaadFilesDistributionWFException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * This class is having the implementation of NscsLaadCommandManagerProcessor interface.
 *
 * @author xkihari
 */
@Stateless
public class NscsLaadCommandManagerProcessorImpl implements NscsLaadCommandManagerProcessor {

    @Inject
    private NscsLogger nscsLogger;

    @EServiceRef
    WorkflowHandler wfHandler;

    public static final String LAAD_DISTRIBUTION_WORKFLOW_ID = WorkflowNames.WORKFLOW_CPP_LAAD_FILES_DISTRIBUTION.toString();

    @Override
    public WfResult executeLaadFilesDistributeWorkflow(final String nodeFdn, final JobStatusRecord jobStatusRecord, int workflowId) {
        WfResult result = null;
        final NodeReference nodeReference = new NodeRef(nodeFdn);
        final Map<String, Object> workflowVars = new HashMap<>();
        workflowVars.put(WorkflowParameterKeys.NODE_KEY.toString(), nodeFdn);
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS.toString());
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeReference, LAAD_DISTRIBUTION_WORKFLOW_ID, workflowVars, jobStatusRecord, workflowId);
            if (result != null) {
                nscsLogger.workFlowStarted(LAAD_DISTRIBUTION_WORKFLOW_ID, result.getWfWakeId().toString(), WorkflowParameterKeys.NODE_KEY.toString(),
                        "");
            }

        } catch (final Exception e) {
            nscsLogger.error("Failed to distribute LAAD Files to node with fdn = [{}]", nodeFdn);
            nscsLogger.workFlowFinishedWithError(LAAD_DISTRIBUTION_WORKFLOW_ID, Integer.toString(workflowId), nodeFdn, e.getMessage());
            throw new LaadFilesDistributionWFException(e);
        }

        return result;
    }
}
