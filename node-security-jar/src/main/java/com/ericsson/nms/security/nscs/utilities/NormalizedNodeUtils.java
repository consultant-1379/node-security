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
package com.ericsson.nms.security.nscs.utilities;

import java.util.*;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * Utility class to get the normalized nodes from the given inputs.
 *
 * @author xkihari
 */

public class NormalizedNodeUtils {

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsLogger nscsLogger;

    /**
     * Return the list of normalized node for the given list of node references
     *
     * @param inputNodes
     *            the input node list
     *
     * @param invalidNodesError
     *            invalid node error details
     *
     * @return normNodes
     *            the list of normalized nodes
     */
    public List<NormalizableNodeReference> getNormalizedNodes(final List<NodeReference> inputNodes,
            final Map<NodeReference, NscsServiceException> invalidNodesError) {
        final List<NormalizableNodeReference> normNodes = new ArrayList<>();
        for (final NodeReference nodeRef : inputNodes) {
            final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
            if (normNode == null) {
                if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn()) && reader.exists(nodeRef.getFdn())) {
                    nscsLogger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    invalidNodesError.put(nodeRef, new UnassociatedNetworkElementException());
                }
                nscsLogger.error("Invalid Node Name [{}]", nodeRef.getFdn());
                invalidNodesError.put(nodeRef, new NodeDoesNotExistException());
                continue;
            }
            normNodes.add(normNode);
        }
        return normNodes;
    }

    /**
     * Return the list of normalized node for the given cmresponse.
     *
     * @param cmresponse
     *            cmresponse containing the list of all matching MOs
     *
     * @return normNodes
     *            the list of normalized nodes
     */
    public List<NormalizableNodeReference> getNormalizedNodesFromCmResponse(final CmResponse cmresponse) {
        final Set<NormalizableNodeReference> normNodes = new LinkedHashSet<>();
        for (final CmObject cmObject : cmresponse.getCmObjects()) {
            final NodeReference nodeRef = new NodeRef(cmObject.getFdn());
            normNodes.add(reader.getNormalizableNodeReference(nodeRef));
        }
        return new ArrayList<>(normNodes);
    }

}
