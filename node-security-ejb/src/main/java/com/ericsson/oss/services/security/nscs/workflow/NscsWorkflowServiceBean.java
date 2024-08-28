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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.data.workflow.WorkflowStatus;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;

@Stateless
public class NscsWorkflowServiceBean implements NscsWorkflowService {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService nscsCmReaderService;

    @EServiceRef
    private WfQueryService wfQueryService;

    @EServiceRef
    private WorkflowHandler workflowHandler;

    @Override
    public Set<WorkflowStatus> getWorkflowsStatus(final List<NodeReference> nodeList) {
        final HashSet<NodeReference> nodeRefSet = new HashSet<>();
        for (final NodeReference nodeReference : nodeList) {
            final NormalizableNodeReference normalizableNodeReference = nscsCmReaderService.getNormalizableNodeReference(nodeReference);
            nodeRefSet.add(normalizableNodeReference);
        }
        return wfQueryService.getWorkflowStatus(nodeRefSet);
    }

    @Override
    public Map<String, Set<NSCSWorkflowInstance>> getWorkflowsStats() {
        return wfQueryService.getWorkflowRunningInstancesByName();
    }

    @Override
    public String deleteWorkflowInstance(final String instanceId) {
        String result = null;
        try {
            workflowHandler.cancelWorkflowInstance(instanceId);
            result = String.format("Successfully cancelled workflow instance [%s]", instanceId);
        } catch (final Exception e) {
            result = String.format("Exception [%s] msg [%s] when cancelling workflow instance [%s]",
                    e.getClass().getCanonicalName(), e.getMessage(), instanceId);
            logger.error(result, e);
        }
        return result;
    }
}
