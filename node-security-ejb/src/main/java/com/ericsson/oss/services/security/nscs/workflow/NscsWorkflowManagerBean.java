/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.workflow;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.workflow.WorkflowStatus;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;
import com.ericsson.oss.services.security.nscs.interceptor.NscsRecordedCommand;
import com.ericsson.oss.services.security.nscs.interceptor.NscsSecurityViolationHandled;

@Stateless
public class NscsWorkflowManagerBean implements NscsWorkflowManager {

    private static final String RESOURCE = "nodesec_workflow";
    private static final String READ = "read";
    private static final String DELETE = "delete";

    @Inject
    private NscsWorkflowService nscsWorkflowService;

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public Set<WorkflowStatus> getWorkflowsStatus(final String nodeList) {
        final List<NodeReference> nodeReferences = WorkflowDtoHelper.fromNodeListDto(nodeList);
        return nscsWorkflowService.getWorkflowsStatus(nodeReferences);
    }

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public Map<String, Set<NSCSWorkflowInstance>> getWorkflowsStats() {
        return nscsWorkflowService.getWorkflowsStats();
    }

    @Override
    @Authorize(resource = RESOURCE, action = DELETE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public String deleteWorkflowInstance(final String instanceId) {
        final String workflowInstanceId = WorkflowDtoHelper.fromWorkflowInstanceIdDto(instanceId);
        return nscsWorkflowService.deleteWorkflowInstance(workflowInstanceId);
    }

}
