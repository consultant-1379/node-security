/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.ciphersconfig;

import java.util.*;

import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

/**
 * This class defines the methods to validate command and nodes for get ciphers configuration.
 * @author tcsvijc
 *
 */
public class GetCiphersValidator extends CiphersConfigurationCommonValidator{

    /**
     * This method is used to validate the given nodes for ciphers configuration commands -  get ciphers. If all of the given input nodes are valid then this method returns
     * true. If any one of the given node is invalid then it returns false.
     * 
     * @param command
     *            CLI command of type CiphersConfigCommand
     * @param validNodesList
     *            Only valid nodes are added to this list.
     * @param invalidNodesErrorMap
     *            All invalid nodes are added to this map.
     * @return {@link Boolean}
     *         <p>
     *         true: if all nodes are valid.
     *         </p>
     *         false: if any one of the given node is invalid.
     */
    public boolean validateNodes(final CiphersConfigCommand command, final List<NodeReference> validNodesList, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {

        final List<NodeReference> uniqueNodes = new ArrayList<NodeReference>();

        uniqueNodes.addAll(new HashSet<NodeReference>(command.getNodes()));
        nscsLogger.debug("Number of unique nodes to validate for ciphers configuration: {}", uniqueNodes.size());

        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : uniqueNodes) {
            try {
                final String protocolType = command.getProtocolProperty();
                final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
                validateNode(normNode, nodeRef, protocolType);
                validNodesList.add(nodeRef);
            } catch (InvalidNodeNameException | NetworkElementNotfoundException | NscsCapabilityModelException | NodeNotCertifiableException | NodeNotSynchronizedException
                    | UnsupportedNodeTypeException | UnassociatedNetworkElementException | UnSupportedNodeReleaseVersionException | UnsupportedAlgorithmException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;

    }
}
