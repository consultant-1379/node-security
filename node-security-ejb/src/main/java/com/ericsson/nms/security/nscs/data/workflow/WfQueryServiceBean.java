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
package com.ericsson.nms.security.nscs.data.workflow;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.wfs.api.query.Query;
import com.ericsson.oss.services.wfs.api.query.QueryBuilder;
import com.ericsson.oss.services.wfs.api.query.QueryBuilderFactory;
import com.ericsson.oss.services.wfs.api.query.QueryType;
import com.ericsson.oss.services.wfs.api.query.Restriction;
import com.ericsson.oss.services.wfs.api.query.RestrictionBuilder;
import com.ericsson.oss.services.wfs.api.query.SortDirection;
import com.ericsson.oss.services.wfs.api.query.WorkflowObject;
import com.ericsson.oss.services.wfs.api.query.instance.WorkflowInstanceQueryAttributes;
import com.ericsson.oss.services.wfs.api.query.progress.WorkflowProgressQueryAttributes;
import com.ericsson.oss.services.wfs.jee.api.WorkflowQueryServiceRemote;

@Stateless
public class WfQueryServiceBean implements WfQueryService {

    @Inject
    private NscsContextService nscsContextService;

	@EServiceRef
	// TODO use LOCAL interface
	// private WorkflowQueryServiceLocal queryService;
	private WorkflowQueryServiceRemote queryService;

	@Inject
	Logger log;

	public static final String NSCS_BUSINESS_KEY_PREFIX = "secwf_";

	public static final String WORKFLOW_INSTANCE_QUERY_ATTRIBUTES_EXECUTIONID = "executionId";

	@Override
	public Map<String, Set<NSCSWorkflowInstance>> getWorkflowRunningInstancesByName() {

		log.debug("Getting Running WorkflowInstances");

		final Map<String, Set<NSCSWorkflowInstance>> workflowsStatsSet = getWFRunningInstancesByName();

		return workflowsStatsSet;

	}

	@Override
	public Set<WorkflowStatus> getWorkflowStatus(
			final Set<? extends NodeReference> nodes) {

		log.debug("Getting WorkflowStatus for list of nodes");

		final Set<WorkflowStatus> workflowsStatusSet = new HashSet<>();

		for (final NodeReference node : nodes) {
			log.debug("Node: [{}]", node.getName());

			final WorkflowStatus workflowStatus = getWFFinalStatusByBusinessKey(node);
			if (workflowStatus != null) {
				workflowsStatusSet.add(workflowStatus);
			}
		}

		return workflowsStatusSet;
	}

	@Override
	public boolean isWorkflowInProgress(final NodeReference node) {

		log.debug("Starting hasWorkflowInstanceInProgress for: \"{}\"",
				node.getFdn());
		final WorkflowStatus workflowStatus = getWFFinalStatusByBusinessKey(node);

		if (workflowStatus == null) {
			log.debug("WorkflowStatus is null.");
		} else {
			log.debug(
					"WorkflowStatus details:workflowStatus id \"{}\" ,workflowStatus stepName  \"{}\" ,workflowStatus eventTime \"{}\"",
					workflowStatus.getWorkflowInstance(),
					workflowStatus.getStepName(), workflowStatus.getEventTime());
		}

		if (workflowStatus == null) {
			return false;
		} else if (workflowStatus.isStarted() && !workflowStatus.isCompleted()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Set<NodeReference> getWorkflowsInProgress(
			final Set<? extends NodeReference> nodes) {
		final Set<NodeReference> nodesInProgress = new HashSet<>();
		for (final NodeReference node : nodes) {
			if (isWorkflowInProgress(node)) {
				nodesInProgress.add(node);
			}
		}
		return nodesInProgress;
	}

	@Override
	public WorkflowStatus getfinalResultForWorkflowExecutionId(
			final String workflowName, final String workflowExecutionId,
			final Map<String, Set<NSCSWorkflowInstance>> workflowRunning) {
		return wfFinalStatusForWorkflowExecutionId(workflowName,
				workflowExecutionId, workflowRunning);
	}

	protected WorkflowStatus getWFFinalStatusByBusinessKey(
			final NodeReference node) {

		log.debug("Starting getWFFinalStatusByBusinessKey for: [{}]",
				node.getFdn());
		WorkflowStatus workflowStatus = null;

		workflowStatus = getWFFinalStatus(
				WorkflowProgressQueryAttributes.QueryParameters.BUSINESS_KEY,
				getBusinessKey(node));

		return workflowStatus;
	}

	protected WorkflowStatus wfFinalStatusForWorkflowExecutionId(
			final String workflowName, final String workflowExecutionId,
			final Map<String, Set<NSCSWorkflowInstance>> workflowRunning) {

		log.debug("Starting wfFinalStatus for workflowExecutionId [{}]",
				workflowExecutionId);
		WorkflowStatus workflowStatus = null;
		boolean isWorkflowInstanceRunning = false;

		if (workflowRunning == null) {
			log.debug("The workflowRunnig map is null");
			// If no Map of actual running workflows is passed, it will fetch
			// the actual list of workflows running right now
			if (this.isWfInstanceRunning(workflowName, workflowExecutionId)) {
				isWorkflowInstanceRunning = true;
				log.debug(
						"Since the workflowExecutionId [{}] is running, return null as final WorkflowStatus",
						workflowExecutionId);
			}
		} else {

			if (workflowRunning.get(workflowName) != null) {
				log.debug("Found key [{}] in workflowRunnig map", workflowName);

				// at least the same workflow name has been already run in the
				// past...
				// so maybe this specific ExecutionId may be included in the map
				// of the actual running workflows

				for (NSCSWorkflowInstance instance : workflowRunning
						.get(workflowName)) {
					if (instance.getExecutionId().equals(workflowExecutionId)) {
						log.debug(
								"Found value [{}] as running instance of [{}] in workflowRunnig map",
								workflowExecutionId, workflowName);
						isWorkflowInstanceRunning = true;
						break;
					}
				}
			}
		}

		if (!isWorkflowInstanceRunning) {
			log.debug("Workflow execution Id {} is completed",
					workflowExecutionId);

			workflowStatus = getWFFinalStatus(
					WorkflowProgressQueryAttributes.QueryResult.WORKFLOW_INSTANCE_ID,
					workflowExecutionId);
		}

		return workflowStatus;
	}

	/**
	 * @param workflowObject
	 * @return
	 */
	private WorkflowStatus buildWorkflowStatus(final WorkflowObject workflowObject) {
		WorkflowStatus workflowStatus;
		final String instanceId = (String) workflowObject
				.getAttribute(WorkflowProgressQueryAttributes.QueryResult.WORKFLOW_INSTANCE_ID);
		final String stepName = (String) workflowObject
				.getAttribute(WorkflowProgressQueryAttributes.QueryResult.NODE_NAME);
		final Date eventTime = (Date) workflowObject
				.getAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TIME);
		final String eventType = (String) workflowObject
				.getAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TYPE);

		log.debug(
				"Final Result data: businesskey '{}'\" instance ID \"{}\", stepName \"{}\", eventTime\"{}\"",
				workflowObject
						.getAttribute(WorkflowProgressQueryAttributes.QueryResult.BUSINESS_KEY),
				instanceId, stepName, eventTime);
		workflowStatus = new WorkflowStatus(instanceId, stepName, eventTime,
				eventType);
		return workflowStatus;
	}

	private WorkflowStatus getWFFinalStatus(final String queryKey,
			final String queryValue) {

		log.debug(
				"Starting getWFFinalStatus with restriction key: [{}], value [{}]",
				queryKey, queryValue);
		WorkflowStatus workflowStatus = null;

		final QueryBuilder builder = QueryBuilderFactory
				.getDefaultQueryBuilder();
		final Query query = builder
				.createTypeQuery(QueryType.WORKFLOW_PROGRESS_QUERY);

		final RestrictionBuilder restrictionBuilder = query
				.getRestrictionBuilder();
		final Restriction restrictionCriteria = restrictionBuilder.isEqual(
				queryKey, queryValue);

		final Restriction eventTypeRestriction = restrictionBuilder.isEqual(
				WorkflowProgressQueryAttributes.QueryParameters.EVENT_TYPE,
				WorkflowProgressQueryAttributes.EventType.END);

		final Restriction composite = restrictionBuilder.allOf(
				restrictionCriteria, eventTypeRestriction);
		query.setRestriction(composite);
		query.addSortingOrder(
				WorkflowProgressQueryAttributes.SortableColumns.EVENT_TIME,
				SortDirection.DESCENDING);

		log.debug("Query Data : [{}], [{}]", query.getQueryType(),
				query.toString());

		final List<WorkflowObject> result = performQuery(query);

		log.debug("Result size:: " + result.size() + " is empty: "
				+ result.isEmpty());

		if (result.size() > 1) {
			// THIS IS FOR DEBUG ONLY
			// IN CASE OF MULTIPLE INSTANCES RETURNED, JUST PRINT ALL DATA
			for (WorkflowObject wfo : result) {
				final String instanceId = (String) wfo
						.getAttribute(WorkflowProgressQueryAttributes.QueryResult.WORKFLOW_INSTANCE_ID);
				final String stepName = (String) wfo
						.getAttribute(WorkflowProgressQueryAttributes.QueryResult.NODE_NAME);
				final Date eventTime = (Date) wfo
						.getAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TIME);
				final String eventType = (String) wfo
						.getAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TYPE);
				log.debug(
						"Multiple Results: Result data: businesskey [{}] instance ID [{}], stepName [{}], eventTime[{}], eventType[{}]",
						wfo.getAttribute(WorkflowProgressQueryAttributes.QueryResult.BUSINESS_KEY),
						instanceId, stepName, eventTime, eventType);
			}
		}

		if (!result.isEmpty()) {
			// GET THE VERY FIRST ITEM, TO RETURN ONLY THE LATEST RESULT
			final WorkflowObject workflowObject = result.iterator().next();

			log.debug("WorkflowObject - type [{}]", workflowObject.getType());

			for (Map.Entry<String, Object> attr : workflowObject
					.getAttributes().entrySet()) {
				log.debug("Attribute MAP key/value: [{}]/[{}]", attr.getKey(),
						(attr.getValue() == null ? "null" : attr.getValue()
								.toString()));
			}
			workflowStatus = buildWorkflowStatus(workflowObject);
		}

		return workflowStatus;
	}

	protected boolean isWfInstanceRunning(String workflowName,
			String workflowExecutionId) {

		log.debug(
				"Starting isWfInstanceRunning for workflowName {},  workflowExecutionId {}",
				workflowName, workflowExecutionId);
		boolean isRunning = false;

		final QueryBuilder batchWorkflowQueryBuilder = QueryBuilderFactory
				.getDefaultQueryBuilder();
		final Query query = batchWorkflowQueryBuilder
				.createTypeQuery(QueryType.WORKFLOW_INSTANCE_QUERY);
		final RestrictionBuilder restrictionBuilder = query
				.getRestrictionBuilder();
		final Restriction activeStateWorkflows = restrictionBuilder.isEqual(
				WorkflowInstanceQueryAttributes.QueryParameters.STATE,
				WorkflowInstanceQueryAttributes.State.ACTIVE);
		final Restriction workflowNameRestriction = restrictionBuilder
				.isEqual(
						WorkflowInstanceQueryAttributes.QueryParameters.WORKFLOW_DEFINITION_ID,
						workflowName);
		final Restriction activeWorkflowNameRestriction = restrictionBuilder
				.allOf(activeStateWorkflows, workflowNameRestriction);
		query.setRestriction(activeWorkflowNameRestriction);
		log.debug("Query Data : type [{}], toString [{}]",
				query.getQueryType(), query.toString());
		List<WorkflowObject> resultList = performQuery(query);
		log.debug("Result size:: " + resultList.size()
				+ " for workflowExecutionId {}", workflowExecutionId);

		// handle mutiple result, find the onw with the instance ID
		for (WorkflowObject wfo : resultList) {

			log.debug("WorkflowObject - type [{}]", wfo.getType());

			final String executionId = (String) wfo
					.getAttribute(WORKFLOW_INSTANCE_QUERY_ATTRIBUTES_EXECUTIONID);
			if (workflowExecutionId.equals(executionId)) {
				isRunning = true;
				break;
			}
		}

		if (isRunning) {
			log.debug("Found the workflowExecutionId {} is still running...",
					workflowExecutionId);
		} else {
			log.debug("The workflowExecutionId {} is completed, size {}...",
					workflowExecutionId, resultList.size());
		}
		return isRunning;
	}

	/**
	 * @return Hashmap with KEY = WorkflowName, VALUE=Set<NSCSWorkflowInstance>
	 *         of actual running workflows
	 */
	protected Map<String, Set<NSCSWorkflowInstance>> getWFRunningInstancesByName() {

		log.debug("Starting wfInstanceQuery");
		final Map<String, Set<NSCSWorkflowInstance>> workflowsInstanceMap = new HashMap<String, Set<NSCSWorkflowInstance>>();

		final QueryBuilder batchWorkflowQueryBuilder = QueryBuilderFactory
				.getDefaultQueryBuilder();
		final Query query = batchWorkflowQueryBuilder
				.createTypeQuery(QueryType.WORKFLOW_INSTANCE_QUERY);
		final RestrictionBuilder restrictionBuilder = query
				.getRestrictionBuilder();
		final Restriction activeStateWorkflows = restrictionBuilder.isEqual(
				WorkflowInstanceQueryAttributes.QueryParameters.STATE,
				WorkflowInstanceQueryAttributes.State.ACTIVE);

		// for each workflow name from Enum type

		Restriction workflowNameRestriction = null;
		Restriction activeWorkflowNameRestriction = null;
		List<WorkflowObject> resultList = null;

		for (WorkflowNames wfName : WorkflowNames.values()) {
			log.debug("Getting wfInstanceQuery for wfName {}",
					wfName.getWorkflowName());
			workflowNameRestriction = restrictionBuilder
					.isEqual(
							WorkflowInstanceQueryAttributes.QueryParameters.WORKFLOW_DEFINITION_ID,
							wfName.getWorkflowName());
			activeWorkflowNameRestriction = restrictionBuilder.allOf(
					activeStateWorkflows, workflowNameRestriction);
			query.setRestriction(activeWorkflowNameRestriction);
			log.debug("Query Data: type [{}], toString [{}]",
					query.getQueryType(), query.toString());
			resultList = performQuery(query);
			log.debug("Result size: " + resultList.size() + " for wfName {}",
					wfName.getWorkflowName());

			workflowsInstanceMap.put(wfName.getWorkflowName(),
					new HashSet<NSCSWorkflowInstance>());

			for (WorkflowObject wfo : resultList) {

				log.debug("WorkflowObject - type [{}]", wfo.getType());

				final String workflowInstanceId = (String) wfo
						.getAttribute(WorkflowInstanceQueryAttributes.QueryParameters.WORKFLOW_INSTANCE_ID);
				final String executionId = (String) wfo
						.getAttribute(WORKFLOW_INSTANCE_QUERY_ATTRIBUTES_EXECUTIONID);
				// Try to get the businesskey from running workflow
				String businessKey = "";
				if (wfo.getAttribute(WorkflowProgressQueryAttributes.QueryParameters.BUSINESS_KEY) != null) {
					businessKey = (String) wfo
							.getAttribute(WorkflowProgressQueryAttributes.QueryParameters.BUSINESS_KEY);
				} else {
					log.warn(
							"No value found for attribute {}",
							WorkflowProgressQueryAttributes.QueryParameters.BUSINESS_KEY);
				}

				// TODO
				// startTIme should be read from WorkflowObject
				NSCSWorkflowInstance instance = new NSCSWorkflowInstance(
						executionId, wfName.getWorkflowName(),
						businessKey);

				workflowsInstanceMap.get(wfName.getWorkflowName())
						.add(instance);
			}
		}

		return workflowsInstanceMap;
	}

	private String getBusinessKey(final NodeReference nodeFdn) {
		return NSCS_BUSINESS_KEY_PREFIX + nodeFdn.getFdn();
	}

	@Override
	public String getWorkflowDefinitionIdFromName(String workflowName) {
		log.debug("Getting wfDefinitionID for wfName {}", workflowName);
		String workflowDefinitionId = "";
		final QueryBuilder batchWorkflowQueryBuilder = QueryBuilderFactory
				.getDefaultQueryBuilder();
		final Query query = batchWorkflowQueryBuilder
				.createTypeQuery(QueryType.WORKFLOW_INSTANCE_QUERY);
		final RestrictionBuilder restrictionBuilder = query
				.getRestrictionBuilder();
		final Restriction activeStateWorkflows = restrictionBuilder.isEqual(
				WorkflowInstanceQueryAttributes.QueryParameters.STATE,
				WorkflowInstanceQueryAttributes.State.ACTIVE);
		final Restriction workflowNameRestriction = restrictionBuilder
				.isEqual(
						WorkflowInstanceQueryAttributes.QueryParameters.WORKFLOW_DEFINITION_ID,
						workflowName);
		final Restriction activeWorkflowNameRestriction = restrictionBuilder
				.allOf(activeStateWorkflows, workflowNameRestriction);
		query.setRestriction(activeWorkflowNameRestriction);
		log.debug("Query Data : type [{}], toString [{}]",
				query.getQueryType(), query.toString());
		List<WorkflowObject> resultList = performQuery(query);
		log.debug("Result size:: " + resultList.size() + " for wfName {}",
				workflowName);

		if (resultList.size() > 0) {
			workflowDefinitionId = (String) resultList
					.iterator()
					.next()
					.getAttribute(
							WorkflowInstanceQueryAttributes.QueryParameters.WORKFLOW_INSTANCE_ID);
		}
		log.debug("Return wfDefinitionID {}", workflowDefinitionId);

		return workflowDefinitionId;
	}

        private List<WorkflowObject> performQuery(final Query query) {
            /**
             * TORF-685969 User ID overwritten in context for secadm ipsec and securitylevel commands
             */
            final String oldUserId = nscsContextService.getUserIdContextValue();
            final List<WorkflowObject> workflowObjects;
            try {
                // Setting the context data
                nscsContextService.setUserIdContextValue("Administrator");
                workflowObjects = queryService.executeQuery(query);
            } finally {
                nscsContextService.setUserIdContextValue(oldUserId);
            }
            return workflowObjects;
        }
    }
