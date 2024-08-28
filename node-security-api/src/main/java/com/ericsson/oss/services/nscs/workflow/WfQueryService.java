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
package com.ericsson.oss.services.nscs.workflow;

import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.workflow.WorkflowStatus;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;

/**
 * Query WF
 * @author egbobcs
 *
 */
@EService
@Remote
public interface WfQueryService {

	 boolean isWorkflowInProgress(final NodeReference node);
	 Set<NodeReference> getWorkflowsInProgress(final Set<? extends NodeReference> nodes);
	 Set<WorkflowStatus> getWorkflowStatus(final Set<? extends NodeReference> nodes);
	 Map<String, Set<NSCSWorkflowInstance>> getWorkflowRunningInstancesByName();
	 String getWorkflowDefinitionIdFromName(final String workflowName);
	 WorkflowStatus getfinalResultForWorkflowExecutionId(final String workflowName, final String workflowExecutionId, final Map<String, Set<NSCSWorkflowInstance>> workflowsRunning);

}
