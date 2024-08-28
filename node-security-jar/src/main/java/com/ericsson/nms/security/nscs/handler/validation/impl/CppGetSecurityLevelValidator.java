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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

/**
 * This class defines the methods to validate command and nodes for getting security level.
 *
 * @author xlakdag
 * @since 08/02/2018
 */
public class CppGetSecurityLevelValidator {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private Logger logger;

    /**
     * To validate the nodes for get security level
     *
     * @param normNodes
     *            the normalized node list
     * @param validNodesList
     *            the valid node list
     * @param invalidNodesErrorMap
     *            invalid node error details
     * @return This method will return true if all the provided nodes are valid ones
     */
    public boolean validateNodes(final List<NormalizableNodeReference> normNodes, final List<NormalizableNodeReference> validNodesList,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        nscsLogger.debug(String.format("validateNodesForGetSecurityLevel.normNodes: %s", normNodes));

        boolean havingAllValidNodes = true;
        for (final NormalizableNodeReference normNode : normNodes) {
            try {
                validateNode(normNode);
                validNodesList.add(normNode);

            } catch (UnassociatedNetworkElementException | NodeDoesNotExistException | NetworkElementNotfoundException | NodeNotSynchronizedException
                    | SecurityFunctionMoNotfoundException | UnsupportedNodeTypeException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(normNode, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", normNode.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    private void validateNode(final NormalizableNodeReference normNode)
            throws NetworkElementNotfoundException, NodeDoesNotExistException, NodeNotSynchronizedException, NscsCapabilityModelException,
            SecurityFunctionMoNotfoundException, UnassociatedNetworkElementException, UnsupportedNodeTypeException {

        final NodeReference normalizedNodeRef = normNode.getNormalizedRef();

        if (normalizedNodeRef == null) {
            logger.error("NetworkElement MO doesn't exist for [{}].", normNode.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!nodeValidatorUtility.isNodeExists(normalizedNodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", normalizedNodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!nodeValidatorUtility.hasNodeSecurityFunctionMO(normalizedNodeRef)) {
            logger.error("Node [{}]  doesn't have valid SecurityFunctionMO.", normalizedNodeRef.getFdn());
            throw new SecurityFunctionMoNotfoundException();
        }

        if (!nodeValidatorUtility.isNodeSynchronized(normNode)) {
            logger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }

        if (!nodeValidatorUtility.isNeTypeSupported(normNode, NscsCapabilityModelService.SECURITYLEVEL_COMMAND)) {
            final String errorMsg = String.format("Unsupported neType[%s]", normNode.getNeType());
            logger.error("NE Type validation failed: {}", errorMsg);
            throw new UnsupportedNodeTypeException().setSuggestedSolution("Check Online Help for Supported Nodes.");
        }
    }

    /**
     * To validate the nodes based on fdn obtained from cmresponse
     *
     * @param invalidNodesErrorMap
     *            invalid node error details
     * @return This method returns true if provided nodes are valid
     */
    public boolean isValidNode(final String nodeFdn, final Map<NodeReference, NscsServiceException> invalidNodesError) {
        final Set<NodeReference> invalidNodesList = invalidNodesError.keySet();
        for (final NodeReference nodeRef : invalidNodesList) {
            nscsLogger.debug("Comparing names of nodeRef {} and CMResponse {}", nodeRef.getName(), extractNodeName(nodeFdn));
            if (extractNodeName(nodeFdn).equalsIgnoreCase(nodeRef.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * To extract the node name from FDN
     *
     * @param fdn
     *            node FDN
     */
    public String extractNodeName(final String fdn) {
        return Model.ME_CONTEXT.extractName(fdn);
    }
}
