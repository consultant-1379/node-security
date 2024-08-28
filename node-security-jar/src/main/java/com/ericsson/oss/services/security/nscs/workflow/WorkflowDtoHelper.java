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

import java.util.ArrayList;
import java.util.List;

import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;

/**
 * Auxiliary class to manage conversions between REST DTO and Internal NSCS format of workflow.
 */
public final class WorkflowDtoHelper {

    private WorkflowDtoHelper() {
    }

    /**
     * Converts a given string containing a list of nodes as a string separated with "&" (REST DTO format) to a list of node references (NSCS internal
     * format).
     * 
     * @param nodeList
     *            the string containing a list of nodes separated with "&".
     * @return the list of node references.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static List<NodeReference> fromNodeListDto(final String nodeList) {
        if (nodeList == null || nodeList.isEmpty()) {
            final String errorMessage = "Null or empty REST DTO node list";
            throw new NscsBadRequestException(errorMessage);
        }
        final List<NodeReference> nodeReferences = new ArrayList<>();
        final String[] nodeArray = nodeList.split("&");
        try {
            nodeReferences.addAll(NodeRef.from(nodeArray));
        } catch (final Exception e) {
            final String errorMessage = String.format("Got exception %s : %s converting REST DTO node list %s", e.getClass().getCanonicalName(),
                    e.getMessage(), nodeList);
            throw new NscsBadRequestException(errorMessage, e);
        }

        return nodeReferences;
    }

    /**
     * Converts a given string containing a workflow instance ID from REST DTO format to NSCS internal format.
     * 
     * @param instanceId
     *            the workflow instance ID.
     * @return the workflow instance ID.
     * @throws NscsBadRequestException
     *             if workflow instance ID is null or empty.
     */
    public static String fromWorkflowInstanceIdDto(final String instanceId) {
        if (instanceId == null || instanceId.isEmpty()) {
            final String errorMessage = "Null or empty REST DTO workflow instance ID";
            throw new NscsBadRequestException(errorMessage);
        }
        return instanceId;
    }
}
