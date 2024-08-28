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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * This class defines the methods to validate command and nodes for get rtsel configuration details.
 * 
 * @author tcsviku
 *
 */
public class RtselConfigurationDetailsValidator extends RtselCommonValidator {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService nscsCmReaderService;

    /**
     * This method is used to validate the given nodes for rtsel commands.
     *
     * @param command
     *            CLI command of type RtselCommand.
     * @param validNodes
     *            Only valid nodes are added to this list.
     * @param invalidNodesError
     *            All invalid nodes are added to this map.
     */
    public void validateNodes(final RtselCommand command, final List<NormalizableNodeReference> validNodes, final Map<NodeReference, NscsServiceException> invalidNodesError, final NscsCommandType commandType) {

        final List<NodeReference> uniqueNodes = new ArrayList<NodeReference>();

        uniqueNodes.addAll(new HashSet<NodeReference>(command.getNodes()));
        nscsLogger.debug("Number of unique nodes to validate for rtsel: {}", uniqueNodes.size());

        for (final NodeReference nodeRef : uniqueNodes) {
            try {
                final NormalizableNodeReference normNode = nscsCmReaderService.getNormalizableNodeReference(nodeRef);
                validateNode(normNode, nodeRef, commandType);
                validNodes.add(normNode);
            } catch (InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException | NodeNotSynchronizedException | NscsCapabilityModelException
                    | UnassociatedNetworkElementException | UnsupportedNodeTypeException exc) {
                invalidNodesError.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }

    }
}
