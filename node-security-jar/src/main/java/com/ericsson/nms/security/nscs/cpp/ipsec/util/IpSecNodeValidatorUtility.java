/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;

/**
 * This Utility class is used to validate each node before starting the workflow process.
 *
 * @author emehsau
 */
public class IpSecNodeValidatorUtility {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @EServiceRef
    private WfQueryService wfQuery;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    CppIpSecOperationUtility cppIpSecOperationUtility;

    /**
     * This method validates the nodes to activate/deactivate ipsec
     *
     * @param inputNodesList       the input nodes list
     * @param validNodesList       the valid nodes list
     * @param invalidNodesErrorMap invalid node error details
     * @return This method returns true if all the provided nodes are valid
     */
    public boolean validateNodes(final List<Node> inputNodesList, final List<Node> validNodesList,
                                 final Map<String, NscsServiceException> invalidNodesErrorMap) {
        nscsLogger.debug(String.format("Input nodes list for IpSec activation/deactivation is  %s", inputNodesList));

        boolean havingAllValidNodes = true;
        for (final Node node : inputNodesList) {

            final String fdn = node.getNodeFdn();
            final NodeReference nodeRef = new NodeRef(fdn);

            try {
                validateNodeForIpSecOperation(nodeRef);
                validateNodeForIpSecConfigurationType(node);
                validNodesList.add(node);
            } catch (NetworkElementNotfoundException | NodeNotSynchronizedException | IpSecMoNotFoundException |
                     NodeIsInWorkflowException | IpSecActionException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef.getFdn(), exc);
                nscsLogger.error("Node [{}] has validation problem. Exception Message is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;

    }

    /**
     * This method validates the nodes to get IpSec status
     *
     * @param uniqueNodesList      the unique nodes list
     * @param validNodesList       the valid nodes list
     * @param invalidNodesErrorMap invalid node error details
     */
    public void validateNodesForIpsecStatus(final List<NodeReference> uniqueNodesList, final List<NormalizableNodeReference> validNodesList,
                                            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        nscsLogger.debug(String.format("validate nodes to get Ipsec Status with InputNodesList: %s", uniqueNodesList));

        for (final NodeReference nodeRef : uniqueNodesList) {
            try {
                validateNodeForIpSecOperation(nodeRef);
                final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
                validNodesList.add(normNode);
            } catch (NetworkElementNotfoundException | NodeNotSynchronizedException | IpSecMoNotFoundException |
                     NodeIsInWorkflowException exc) {
                invalidNodesErrorMap.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
    }

    /**
     * This method will validate whether given node exists, node is synchronized, node is associated with Workflow and node has valid IpSec MO or not.
     *
     * @param nodeRef : {@link NodeReference}
     * @return : {@link Boolean}
     * <p>
     * true: if node exists, sync and has IpSec MO.
     * </p>
     * false: if any of validation is failed
     */
    public boolean validateNodeForIpSecOperation(final NodeReference nodeRef) throws IpSecMoNotFoundException, NetworkElementNotfoundException,
            NodeIsInWorkflowException, NodeNotSynchronizedException {

        if (!isNodeExists(nodeRef)) {
            nscsLogger.error("NetworkElement [{}] doesn't exist.", nodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        if (null == normNode) {
            nscsLogger.error("Node [{}]  is not normalized.", nodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        } else {
            if (!isNodeSynchronized(normNode)) {
                nscsLogger.error("Node [{}]  is not synchronized", normNode.getFdn());
                throw new NodeNotSynchronizedException();
            }
        }

        if (!isNodeHasIpSecMO(normNode)) {
            nscsLogger.error("Node [{}]  doesn't have valid IpSecMO.", normNode.getFdn());
            throw new IpSecMoNotFoundException();
        }

        if (isNodeAssociatedWithExistingWF(normNode.getNormalizedRef())) {
            nscsLogger.error("Node [{}]  is already associated with Workflow.", normNode.getFdn());
            throw new NodeIsInWorkflowException();
        }
        return true;
    }

    /**
     * Method to check whether node exists or not.
     *
     * @param nodeRef : {@link NodeReference}
     * @return {@link Boolean}
     * <p>
     * true: if node exists
     * </p>
     * false: if node doesn't exists
     */
    public boolean isNodeExists(final NodeReference nodeRef) {
        return Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn()) && reader.exists(nodeRef.getFdn());
    }

    /**
     * Method to check whether node has IpSec or not.
     *
     * @param nodeRef : {@link NodeReference}
     * @return {@link Boolean}
     * <p>
     * true: if node has IpSec MO
     * </p>
     * false: if node doesn't has IpSec MO
     */
    public boolean isNodeHasIpSecMO(final NormalizableNodeReference nodeRef) {
        nscsLogger.debug("Checking IpSec MO Node exists for node : {} ", nodeRef.getFdn());
        return reader.exists(getIpsecFdn(nodeRef));
    }

    /**
     * Method to check whether node is synchronized or not.
     *
     * @param normNodeRef : {@link NormalizableNodeReference}
     * @return {@link Boolean}
     * <p>
     * true: if node is synchronized
     * </p>
     * false: if node is not synchronized
     */
    public boolean isNodeSynchronized(final NormalizableNodeReference normNodeRef) {
        boolean isSynch = false;
        final CmResponse response = reader.getMOAttribute(normNodeRef.getNormalizedRef(), Model.NETWORK_ELEMENT.cmFunction.type(),
                Model.NETWORK_ELEMENT.cmFunction.namespace(), CmFunction.SYNC_STATUS);
        nscsLogger.debug("Response is : {}, and size of response : {}", response, response.getCmObjects().size());
        if (response.getCmObjects().isEmpty() || response.getCmObjects().size() > 1) {
            nscsLogger.info("CmFunction MO is not configured for : {}", normNodeRef.getFdn());
        } else {
            final String status = (String) response.getCmObjects().iterator().next().getAttributes().get(CmFunction.SYNC_STATUS);
            nscsLogger.info("Node [{}] SYNC status is: [{}]", normNodeRef.getFdn(), status);
            if (ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name().equals(status)) {
                isSynch = true;
            }

        }
        return isSynch;
    }

    /**
     * Method to check whether node is associated with any workflow.
     *
     * @param nodeRef : {@link NodeReference}
     * @return {@link Boolean}
     * <p>
     * true: if node is already associated with workflow
     * </p>
     * false: if node is not associated with any existing WF
     */
    public boolean isNodeAssociatedWithExistingWF(final NodeReference nodeRef) {
        nscsLogger.debug("Checking whether node [{}] participating in any other workflow", nodeRef.getFdn());
        return wfQuery.isWorkflowInProgress(nodeRef);
    }

    /**
     * validateNodeForIpSecConfigurationType : check if IpSec configuration type is valid
     * @param node : the Node class
     */
    public void validateNodeForIpSecConfigurationType(final Node node) {
        CppIpSecConfigurationTypeResponse cppIpSecConfTypeRsp = cppIpSecOperationUtility.checkIpSecOperation(node);
        if(!cppIpSecConfTypeRsp.getValid()) {
            String errMsg = String.format("IpSec configuration for NetworkElement [%s] performed in the wrong state",
                    node.getNodeFdn());
            nscsLogger.error(errMsg);
            String extErrMsg = String.format(errMsg, cppIpSecConfTypeRsp.getMessage());
            IpSecActionException ipSecActionException = new IpSecActionException(extErrMsg);
            ipSecActionException.setSuggestedSolution(cppIpSecConfTypeRsp.getSuggestedSolution());
            throw ipSecActionException;
        }
    }

    private String getIpsecFdn(final NormalizableNodeReference normalizedReference) {

        final Mo rootMo = nscsCapabilityModelService.getMirrorRootMo(normalizedReference);
        final Mo iPSecMo = ((CppManagedElement) rootMo).ipSystem.ipSec;
        final String iPSecFdn = iPSecMo.withNames(normalizedReference.getFdn()).fdn();

        nscsLogger.debug("IPSec FDN " + iPSecFdn);
        return iPSecFdn;
    }
}
