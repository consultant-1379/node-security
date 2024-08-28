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

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.workflow.WorkflowStatus;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;

@Local
public interface NscsWorkflowService {

    Set<WorkflowStatus> getWorkflowsStatus(List<NodeReference> nodeList);

    Map<String, Set<NSCSWorkflowInstance>> getWorkflowsStats();

    String deleteWorkflowInstance(String instanceId);
}
