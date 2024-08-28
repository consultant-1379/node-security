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
package com.ericsson.oss.services.nscs.workflow.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.exception.WorkflowHandlerException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.data.workflow.WfQueryServiceBean;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.cache.NscsWorkflowNodeStatusDataHandler;
import com.ericsson.nms.security.nscs.workflow.task.util.NscsWorkflowTaskHandlerUtil;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import com.ericsson.oss.services.wfs.api.WorkflowMessageCorrelationException;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;
import com.ericsson.oss.services.wfs.jee.api.WorkflowInstanceServiceRemote;

/**
 * Implementation of the WorkflowHandler. Uses WorkflowService to initiate workflows.
 *
 *
 * @author egbobcs
 * @see com.ericsson.oss.services.nscs.workflow.api.WorkflowHandler
 */
@Stateless
public class WorkflowHandlerImpl implements WorkflowHandler {

    public static final String NOT_AVAILABLE_WORKFLOW_ID = "N/A";

    @EServiceRef
    WorkflowInstanceServiceRemote wfsInstanceService;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    MOGetServiceFactory moGetServiceFactory;

    @EJB
    NscsWorkflowNodeStatusDataHandler nscsWorkflowNodeStatusDataHandler;

    @Inject
    private NscsWorkflowTaskHandlerUtil nscsWorkflowTaskHandlerUtil;

    public static final String NODE_KEY = "NODE_FDN";

    @Inject
    protected Logger logger;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    /**
     * Initiates security workflows using WorkflowService
     *
     * WorkflowService DOES NOT supports Camunda's multi instance feature so we need to iterate through the nodes and start the workflows one by one.
     *
     * @param nodes
     *            a list of NodeReferences
     * @param workflowName
     *            name of the workflow
     * @return Set<WorkflowInstance> set of the initiated workflows
     */

    @Override
    public Set<WorkflowInstance> startWorkflowInstances(final List<NodeReference> nodes, final String workflowName) {

        final Set<WorkflowInstance> workflows = new HashSet<WorkflowInstance>();
        //This could be optimized. We don't know how we will solve the throttling
        //so there is no sense to make this more optimized just YET.
        for (final NodeReference node : nodes) {

            final Map<String, Object> workflowVars = new HashMap<String, Object>();

            workflowVars.put(NODE_KEY, node.getFdn());

            final String businessKey = getBusinessKey(node);
            logger.info("Executing workflow \"{}\" for node \"{}\" using businessKey \"{}\"", workflowName, node, businessKey);
            final WorkflowInstance wf = startWorkflow(businessKey, workflowName, workflowVars);
            logger.info("Workflow \"{}\" is started", businessKey);
            workflows.add(wf);
        }
        return workflows;
    }

    /**
     * Gets the WorkflowInstance by FDN
     *
     * @param node
     *            NodeReference instance representing the node
     * @return WorkflowInstance
     */

    @Override
    public WorkflowInstance getWorkflowByFdn(final NodeReference node) {
        logger.warn("getWorkflowByFdn(): Not implemented yet");
        return null;
    }

    @Override
    public void dispatchMessage(final NodeReference node, final String message) {
        logger.info("Dispatching message: " + message + " to workflow with node: " + node);
        final String businessKey = getBusinessKey(node);

        try {
            wfsInstanceService.correlateMessage(message, businessKey);
            logger.info("Message [{}] dispatched to workflow with business key [{}]", message, businessKey);
        } catch (final WorkflowMessageCorrelationException e) {
            logger.warn("Caught WorkflowMessageCorrelationException while correlating message [{}]. Cause is [{}]. Re-throwing.", message,
                    e.getMessage());
            throw new WorkflowHandlerException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowInstance startWorkflowInstance(final NodeReference node, final String workflowName) {
        final Map<String, Object> workflowVars = new HashMap<>();
        workflowVars.put(NODE_KEY, node.getFdn());
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
        final String businessKey = getBusinessKey(node);
        logger.info("Executing workflow \"{}\" for node \"{}\" using businessKey \"{}\"", workflowName, node, businessKey);
        final WorkflowInstance wf = startWorkflow(businessKey, workflowName, workflowVars);
        logger.info("Workflow \"{}\" is started", businessKey);
        return wf;
    }

    @Override
    public WfResult getScheduledWorkflowInstanceResult(final NodeReference node, final String childWFName, final Map<String, Object> workflowParams,
            final JobStatusRecord jobStatusRecord, final int workflowId) {

        final String wfResultUUIDString = jobStatusRecord.getJobId().toString() + workflowId;
        final UUID wakeID = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes());
        final Map<String, Object> wfMap = nscsWorkflowTaskHandlerUtil.buildVarForScheduledWorkflow(workflowParams,
                jobStatusRecord.getJobId().toString(), childWFName, node.getFdn(), wakeID);

        final long jobIdTimeStamp = (jobStatusRecord != null && jobStatusRecord.getInsertDate() != null) ? jobStatusRecord.getInsertDate().getTime()
                : 0l;

        final long timestamp = jobIdTimeStamp + (new Date()).getTime();
        final WfResult data = new WfResult();
        data.setJobId(jobStatusRecord.getJobId());
        data.setTimestamp(timestamp);
        data.setWfId(NOT_AVAILABLE_WORKFLOW_ID);
        data.setNodeName(node.getName());
        data.setStatus(WfStatusEnum.PENDING);
        data.setWfWakeId(wakeID);
        data.setWfParams(wfMap);
        logger.debug("WorkflowHandlerImpl ready with WfResult data [{}]", data);
        return data;

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public JobStatusRecord getJobStatusRecord(final UUID jobId) {
        return cacheHandler.getJobStatusRecord(jobId);
    }

    @Override
    public WorkflowInstance dispatch(final Map<String, Object> wfParams) {
        final String nodeFdn = (String) wfParams.get(WorkflowParameterKeys.NODE_KEY.toString());
        final String wfName = (String) wfParams.get(WorkflowParameterKeys.INNERWF_CHILD.toString());
        //        final String jobId = (String) wfParams.get(WorkflowParameterKeys.JOB_ID.toString());
        //        final String wfStatusId = (String) wfParams.get(WorkflowParameterKeys.WFSTATUSID.toString());

        final String businessKey = getBusinessKey(new NodeRef(nodeFdn));
        logger.info("Executing workflow \"{}\" for node \"{}\" using businessKey \"{}\"", wfParams, nodeFdn, businessKey);
        final WorkflowInstance wf = startWorkflow(businessKey, wfName, wfParams);
        logger.info("Workflow \"{}\" is started", businessKey);
        return wf;
        //        if (wf != null) {
        //            //TODO remove the update here!!!
        //            UUID jobID = UUID.fromString(jobId);
        //            JobStatusRecord jobInfo = cacheHandler.getJob(jobID);
        //            WfResult data = jobInfo.getStatus(wfStatusId);
        //            data.setWfId(wf.getId());
        //            data.setStatus(WfStatusEnum.RUNNING);
        //            cacheHandler.updateJob(jobID, data);
        //        }

    }

    @Override
    public WorkflowInstance startWorkflowInstance(final NodeReference node, final String wfName, final Map<String, Object> workflowParams) {
        final Map<String, Object> wfMap = new HashMap<>();
        logger.warn("filling workflowParams: {}", workflowParams);
        wfMap.putAll(workflowParams);
        logger.warn("filling {} : {}", NODE_KEY, node.getFdn());
        wfMap.put(NODE_KEY, node.getFdn());
        final String businessKey = getBusinessKey(node);
        logger.warn("Executing workflow \"{}\" for node \"{}\" using businessKey \"{}\"", wfMap, node, businessKey);
        final WorkflowInstance wf = startWorkflow(businessKey, wfName, wfMap);
        logger.info("Workflow \"{}\" is started", businessKey);
        return wf;
    }

    /**
     * Starts a workflow using workflowService
     *
     * @param workflowName
     *            the workflow name
     * @param workflowVars
     *            the workflow variables
     * @param businessKey
     *            the business key
     */
    private WorkflowInstance startWorkflow(final String businessKey, final String workflowName, final Map<String, Object> workflowVars) {
        final WorkflowInstance workflowInstance = wfsInstanceService.startWorkflowInstanceByDefinitionId(workflowName, businessKey, workflowVars);

        if (workflowInstance != null) {
            //Add new workflow instance to NscsInstrumentationBean
            final String workflowInstanceId = workflowInstance.getId();
            //nscsInstrumentedBean.addWorkflowInstance(new NSCSWorkflowInstance(workflowInstanceId, workflowName, businessKey));
            logger.debug("Added NSCSWorkflowInstance to NscsInstrumentationBean with " + "workflowInstance.getId() [{}], " + "workflowName [{}], "
                    + "businessKey [{}]", workflowInstanceId, workflowName, businessKey);
            final String nodeKey = (String) workflowVars.get(NODE_KEY);
            final String additionalInfo = String.format("Workflow successfully started: business key [%s]: params [%s]", businessKey, workflowVars);
            nscsLogger.workFlowStarted(workflowName, workflowInstanceId, nodeKey, additionalInfo);

            nscsWorkflowNodeStatusDataHandler.updateNodeCacheStatusByWorkflow(nodeKey, workflowName, false);

        }

        return workflowInstance;
    }

    /**
     * Gets the business key based on the node FDN.
     *
     * @param nodeFdn
     *            the node fdn
     * @return the business key with node fdn
     */
    String getBusinessKey(final NodeReference node) {
        final NodeReference normalized = getNormalizedRef(node);
        return WfQueryServiceBean.NSCS_BUSINESS_KEY_PREFIX + normalized.getFdn();
    }

    /**
     * Initiates security workflows using WorkflowService with parameters
     *
     * WorkflowService DOES NOT supports Camunda's multi instance feature so we need to iterate through the nodes and start the workflows one by one.
     *
     * @param nodes
     *            NodeReference instances
     * @param workflowName
     *            name of the workflow
     * @param workflowParams
     *            a map containing workflow IN parameters
     * @return Set<WorkflowInstance> set of the initiated workflows
     */
    @Override
    public Set<WorkflowInstance> startWorkflowInstances(final List<NodeReference> nodes, final String workflowName,
            final Map<String, Object> workflowParams) {

        final Set<WorkflowInstance> workflows = new HashSet<WorkflowInstance>();

        for (final NodeReference node : nodes) {
            final Map<String, Object> workflowVars = new HashMap<String, Object>();
            if (null != workflowParams) {
                workflowVars.putAll(workflowParams);
            }
            workflowVars.put(NODE_KEY, node.getFdn());
            final String businessKey = getBusinessKey(node);
            logger.info("Executing workflow \"{}\" for node \"{}\" using businessKey \"{}\"", workflowName, node, businessKey);
            final WorkflowInstance wf = startWorkflow(businessKey, workflowName, workflowVars);
            logger.info("Workflow \"{}\" is started", businessKey);
            workflows.add(wf);
        }
        return workflows;
    }

    NodeReference getNormalizedRef(final NodeReference node) {
        if (node instanceof NormalizableNodeReference) {
            logger.warn("NodeRef [{}] is already Normalizable, returning", node);
            return node;
        }

        final NormalizableNodeReference normRef = readerService.getNormalizableNodeReference(node);

        if (normRef == null) {
            logger.warn("Could not find normalized node reference for {}. Returning not normalized NodeReference", node);
            return node;
        } else {
            return normRef;
        }
    }

    @Override
    public void cancelWorkflowInstance(final String workflowInstanceId) {
        logger.info("Cancelling workflow instance [{}]", workflowInstanceId);
        wfsInstanceService.cancelWorkflowInstance(workflowInstanceId);
        logger.info("Cancelled workflow instance [{}]", workflowInstanceId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void insertWorkflowBatch(final Map<UUID, WfResult> wfResultMap) {
        logger.debug("insertWorkflowBatch() - WorkflowHandlerImpl ready to update workflow list size [{}]", wfResultMap.size());
        cacheHandler.insertWorkflowBatch(wfResultMap);
        logger.info("insertWorkflowBatch() - WorkflowHandlerImpl updated workflow list size [{}]", wfResultMap.size());

    }
}
