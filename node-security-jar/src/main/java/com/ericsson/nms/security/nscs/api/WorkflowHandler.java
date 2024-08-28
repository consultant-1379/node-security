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
package com.ericsson.nms.security.nscs.api;

import java.util.*;

import javax.ejb.Remote;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

/**
 * WorkflowHandler supports of initiating security workflows.
 *
 * @author egbobcs
 *
 */
@EService
@Remote
public interface WorkflowHandler {

    /**
     * Gets the WorkflowInstance by FDN
     *
     * @param node
     *            NodeReference instance
     * @return WorkflowInstance
     */
    WorkflowInstance getWorkflowByFdn(final NodeReference node);

    /**
     * Starts security workflow instances for the supplied nodes.
     *
     * @param nodes
     *            NodeReference instances
     * @param workflowName
     *            name of the workflow
     * @return Set<WorkflowInstance> set of the initiated workflows
     */
    Set<WorkflowInstance> startWorkflowInstances(final List<NodeReference> nodes, final String workflowName);

    /**
     * Starts security workflow instance for the supplied node.
     *
     * @param node
     *            NodeReference instance
     * @param workflowName
     *            name of the workflow
     * @return WorkflowInstance the initiated workflow instance
     */
    WorkflowInstance startWorkflowInstance(final NodeReference node, final String workflowName);

    /**
     * To be used later to dispatch messages towards to the nodes
     *
     * @param node
     *            The NodeReference instance
     * @param message
     *            The message to be dispatched
     */
    void dispatchMessage(final NodeReference node, final String message);

    /**
     * Starts security workflow instances for the supplied nodes with parameters.
     *
     * @param nodes
     *            NodeReference instances
     * @param workflowName
     *            name of the workflow
     * @param workflowParams
     *            a map containing workflow IN parameters
     * @return Set<WorkflowInstance> set of the initiated workflows
     */
    Set<WorkflowInstance> startWorkflowInstances(final List<NodeReference> nodes, final String workflowName,
                                                 final Map<String, Object> workflowParams);

    /**
     * Starts security workflow instance for the supplied node with additional data.
     *
     * @param node
     *            : {@link NodeReference} The NodeReference instance
     * @param wfName
     *            : {@link String} name of the workflow
     * @param workflowParams
     *            : {@link HashMap} additional parameter required for workflow.
     * @return WorkflowInstance the initiated workflow instance
     */
    WorkflowInstance startWorkflowInstance(NodeReference node, String wfName, Map<String, Object> workflowParams);

    /**
     * @param workflowInstanceId
     */
    void cancelWorkflowInstance(String workflowInstanceId) throws Exception;

    /**
     * @param wfParams
     * @return
     */
    WorkflowInstance dispatch(Map<String, Object> wfParams);

    /**
     * @param jobId
     * @param node
     * @param childWFName
     * @param workflowParams
     * @param jobStatusRecord
     * @param workflowId
     *            The id used to calculate the UUID of WfResult, depending on #JobStatusRecord UUID
     * @return
     */
    WfResult getScheduledWorkflowInstanceResult(final NodeReference node, final String childWFName, final Map<String, Object> workflowParams,
                                                final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * @param wfResultList
     */
    void insertWorkflowBatch(final Map<UUID, WfResult> wfResultMap);

    /**
     * @param jobId
     * @return
     */
    JobStatusRecord getJobStatusRecord(final UUID jobId);

}
