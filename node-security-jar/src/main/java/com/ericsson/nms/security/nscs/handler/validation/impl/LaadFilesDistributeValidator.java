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

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.laad.utility.LaadFilesDistributeConstants;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

/**
 * This class defines the methods to validate nodes for distribute LAAD files.
 *
 * @author tcsgoja
 */
public class LaadFilesDistributeValidator {

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsLogger nscsLogger;

    public LaadFilesDistributeValidator() {
        super();
    }

    /**
     * This method is used to validate nodes reference and type.
     *
     * @param inputNodes
     *            All unique input node list to validate.
     * @param validNodes
     *            Only valid nodes are added to this list.
     * @param invalidNodesError
     *            All invalid nodes are added to this map.
     * @return
     */
    public boolean validateNodes(final List<NodeReference> inputNodes, final List<String> validNodes, final Map<NodeReference, NscsServiceException> invalidNodesError) {

        nscsLogger.debug("Number of unique nodes to validate for laad: {}", inputNodes.size());

        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : inputNodes) {
            try {
                final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
                validateNode(nodeRef);
                validateFmAlarmSupervision(normNode);
                validateNodeType(normNode);
                validNodes.add(nodeRef.getFdn());
            } catch (InvalidAlarmServiceStateException | NetworkElementNotfoundException | NodeDoesNotExistException | NodeNotCertifiableException | NodeNotSynchronizedException
                    | NscsCapabilityModelException | SecurityFunctionMoNotfoundException | UnassociatedNetworkElementException | UnsupportedNodeTypeException exc) {
                havingAllValidNodes = false;
                invalidNodesError.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    private void validateNode(final NodeReference nodeRef) {
        nscsLogger.info("Validating Node [{}] for distribute LAAD files ", nodeRef.getFdn());

        nodeValidatorUtility.validateNode(nodeRef);

    }

    private void validateFmAlarmSupervision(final NormalizableNodeReference normNode) {
        if (!nodeValidatorUtility.isFmSupervisionEnabled(normNode)) {
            nscsLogger.error("Alarm Supervision is disabled for the node [{}]", normNode.getName());
            throw new InvalidAlarmServiceStateException(NscsErrorCodes.ALARM_SUPERVISION_NOT_ENABLED_ON_NODE)
                    .setSuggestedSolution(NscsErrorCodes.ALARM_SUPERVISION_ENABLE_SUGGESTED_SOLUTION);
        }
    }

    private void validateNodeType(final NormalizableNodeReference normNode) {

        nscsLogger.info("Validating Node Type [{}] for distribute LAAD files ", normNode.getFdn());

        if (!nodeValidatorUtility.isCliCommandSupported(normNode, NscsCapabilityModelService.LAAD_COMMAND)) {
            nscsLogger.error("Node [{}] of Type [{}] doesn't support for laad distribute command.", normNode.getFdn(), normNode.getNeType());
            throw new UnsupportedNodeTypeException().setSuggestedSolution(LaadFilesDistributeConstants.LAAD_DISTRIBUTE_NODE_TYPE_SUGGESTED_SOLUTION);
        }

    }

}
