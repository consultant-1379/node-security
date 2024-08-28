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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedAlgorithmException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConfigurationUtil;

/**
 * This class defines the methods to validate command and nodes for set ciphers configuration.
 *
 * @author tcsvijc
 *
 */
public class SetCiphersValidator extends CiphersConfigurationCommonValidator {

    /**
     * This method is used to validate the given nodes for ciphers configuration commands - 'set ciphers'. If all of the given input nodes are valid
     * then this method will return true. If any one of the given node is invalid then this method will return false.
     *
     * @param command
     *            CLI command of type CiphersConfigCommand
     * @param nodeCiphers
     *            object of NodeCiphers
     * @param validNodesList
     *            Only valid nodes are added to this list.
     * @param invalidNodesErrorMap
     *            All invalid nodes are added to this map.
     * @return {@link Boolean}
     *         <p>
     *         true: if all nodes are valid.
     *         </p>
     *         false: if any one of the given node is invalid.
     *
     */
    public boolean validateNodes(final CiphersConfigCommand command, final NodeCiphers nodeCiphers, final List<NodeReference> validNodesList,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {

        final List<NodeReference> uniqueNodes = new ArrayList<>();
        uniqueNodes.addAll(new HashSet<>(NodeRef.from(nodeCiphers.getNodes().getNodeFdn())));
        nscsLogger.info("Number of unique nodes to validate for ciphers configuration: {}", uniqueNodes.size());

        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : uniqueNodes) {
            try {
                final List<String> inputProtocolTypes = CiphersConfigurationUtil.getInputProcotolTypes(nodeCiphers);
                nscsLogger.info("Input has Protocol types {}", inputProtocolTypes);
                final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);

                if (nodeCiphers.getSshProtocol() != null) {
                    validateXMLForSSH(nodeCiphers);
                }
                validateNode(nodeRef, inputProtocolTypes);
                final boolean isEmptyCipherSupported = nscsCapabilityModelService.isEmptyValueSupportedForCiphers(normNode);
                nscsLogger.info("empty ciphers supported... [{}] for netype [{}]", isEmptyCipherSupported, normNode.getNeType());

                emptyCiphersValidator.validateNodeCiphers(nodeCiphers, isEmptyCipherSupported);
                if (nodeCiphers.getSshProtocol() != null) {
                    validateSupportedCiphersForSsh(normNode, nodeCiphers);
                }

                validNodesList.add(nodeRef);
            } catch (InvalidInputXMLFileException | InvalidArgumentValueException | InvalidNodeNameException | NetworkElementNotfoundException
                    | NscsCapabilityModelException | NodeNotCertifiableException | NodeNotSynchronizedException | UnsupportedNodeTypeException
                    | UnassociatedNetworkElementException | UnsupportedAlgorithmException | UnSupportedNodeReleaseVersionException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }
}
