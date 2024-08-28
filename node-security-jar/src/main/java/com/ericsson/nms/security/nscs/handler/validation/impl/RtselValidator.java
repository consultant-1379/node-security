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

import java.util.*;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ServerInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeInfo;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselConfig;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselDetails;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselServerConfiguration;

/**
 * This class defines the methods to validate command and nodes for set rtsel configuration.
 * 
 * @author xchowja
 *
 */
public class RtselValidator extends RtselCommonValidator {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private RtselServerConfiguration rtselServerConfiguration;

    /**
     * This method is used to validate the given nodes for RTSEL commands. If all of the given input nodes are valid then this method will return true. If any one of the given node is invalid then
     * this method will return false.
     * 
     * @param nodeRtselConfig
     * @param nodeInfoDetailsList
     * @param duplicateNodes
     * @param invalidNodesErrorMap
     * @throws NscsServiceException
     */
    public void validateNodes(final NodeRtselConfig nodeRtselConfig, final List<NodeInfoDetails> nodeInfoDetailsList, final Set<String> duplicateNodes,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final NscsCommandType commandType) throws NscsServiceException {

        final List<NodeInfo> nodeInfoList = nodeRtselConfig.getNodes().getNodeInfo();
        NodeInfoDetails nodeInfoDetails = null;
        for (NodeInfo nodeInfo : nodeInfoList) {
            nodeInfo.getNodeFdns().getNodeFdn().removeAll(duplicateNodes);
            final Set<String> inputUniqueNodeFdnsList = nodeInfo.getNodeFdns().getNodeFdn();
            final List<String> validNodeFdnsList = new ArrayList<String>();
            NodeReference nodeRef = null;
            for (String nodeFdn : inputUniqueNodeFdnsList) {
                try {
                    nodeRef = new NodeRef(nodeFdn);
                    final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
                    validateNode(normNode, nodeRef, commandType);
                    validateEnrollmentModeSupport(normNode, nodeInfo.getEnrollmentMode());
                    validNodeFdnsList.add(nodeRef.getFdn());
                } catch (InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException | NodeNotSynchronizedException | UnassociatedNetworkElementException
                        | UnsupportedNodeTypeException | InvalidArgumentValueException exc) {
                    invalidNodesErrorMap.put(nodeRef, exc);
                    nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
                }
            }

            if (!validNodeFdnsList.isEmpty()) {
                nodeInfoDetails = new NodeInfoDetails();
                nodeInfoDetails.setNodeFdnsList(validNodeFdnsList);
                nodeInfoDetails.setEnrollmentMode(nodeInfo.getEnrollmentMode());
                nodeInfoDetails.setEntityProfileName(nodeInfo.getEntityProfileName());
                nodeInfoDetails.setKeySize(nodeInfo.getKeySize());

                nodeInfoDetailsList.add(nodeInfoDetails);
            }
        }
    }

    /**
     * This method will check the duplicate nodes form the input xml file
     * 
     * @param inputNodeRtselConfigList
     *            list of NodeRtselConfig objects
     * @return Set of duplicates nodes
     */
    public Set<String> getDuplicateNodesForActivateRtsel(final List<NodeRtselConfig> inputNodeRtselConfigList, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        final Set<String> uniqueNodes = new HashSet<String>();
        final Set<String> duplicateNodes = new HashSet<String>();
        for (NodeRtselConfig nodeRtselConfig : inputNodeRtselConfigList) {
            final List<NodeInfo> nodeInfoList = nodeRtselConfig.getNodes().getNodeInfo();
            for (NodeInfo nodeInfo : nodeInfoList) {
                for (String nodeFdn : nodeInfo.getNodeFdns().getNodeFdn()) {
                    if (!uniqueNodes.add(nodeFdn)) {
                        duplicateNodes.add(nodeFdn);
                    }
                }
            }
        }
        for (String duplicateNode : duplicateNodes) {
            invalidNodesErrorMap.put(NodeRef.from(duplicateNode).get(0), new DuplicateNodeNamesException(NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NODE_NAMES,
                    NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NODE_FDN).setSuggestedSolution(NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NOT_ALLOWED));
        }
        return duplicateNodes;
    }

    /**
     * This method validates the given unique nodes for RTSEL Deactivate command. It puts the valid nodes to a List and invalid nodes to a Map with error exception.
     * 
     * @param uniqueNodes
     * @param validNodeDetailsList
     * @param invalidNodesErrorMap
     */
    public void validateNodesForDeactivate(final List<NodeReference> uniqueNodes, final List<String> validNodeDetailsList, final NscsCommandType commandType, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        for (NodeReference nodeRef : uniqueNodes) {
            try {
                final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
                validateNode(normNode, nodeRef,commandType);
                validNodeDetailsList.add(nodeRef.getFdn());
            } catch (InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException | NodeNotSynchronizedException | UnassociatedNetworkElementException
                    | UnsupportedNodeTypeException exc) {
            	invalidNodesErrorMap.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
    }

    /**
     * This method is used to validate the given nodes for RTSEL Delete command. If all of the given input nodes are valid then this method will return true. If any one of the given node is invalid
     * then this method will return false.
     * 
     * @param nodeInfoDetails
     * @param validNodeFdnsList
     * @param duplicateNodes
     * @param invalidNodesErrorMap
     * @param commandType
     * @throws NscsServiceException
     */
    public void validateNodesToDeleteServerDetails(final NodeRtselDetails nodeInfoDetails, final List<String> validNodeFdnsList, final Set<String> duplicateNodes,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final NscsCommandType commandType) throws NscsServiceException {
        final Set<String> inputFdnsList = nodeInfoDetails.getNodes().getNodeFdn();
        inputFdnsList.removeAll(duplicateNodes);

        for (final String nodeFdn : inputFdnsList) {
            NodeReference nodeRef = null;
            try {
                nodeRef = new NodeRef(nodeFdn);
                final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
                validateNode(normNode, nodeRef, commandType);
                validateServerNames(normNode, nodeInfoDetails);
                validNodeFdnsList.add(nodeRef.getFdn());
            } catch (InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException | NodeNotSynchronizedException | UnassociatedNetworkElementException
                    | UnsupportedNodeTypeException | ServerNameNotFoundException exc) {

                invalidNodesErrorMap.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
    }
    private void validateServerNames(final NormalizableNodeReference normNode, final NodeRtselDetails nodeInfoDetails) throws ServerNameNotFoundException {
        nscsLogger.debug("validateServerNames method of RtselValidator");
        final List<Map<String, Object>> serverDetailsList = (List<Map<String, Object>>) rtselServerConfiguration.getRtselServerDetails(normNode);
        final Set<String> inputServerNamesList = nodeInfoDetails.getServers().getServerName();
        if (serverDetailsList == null || serverDetailsList.isEmpty()) {
            final String errorMessage = String.format(NscsErrorCodes.RTSEL_SERVER_NAMES_NOT_FOUND_TO_DELETE, inputServerNamesList);
            nscsLogger.error(errorMessage);
            throw new ServerNameNotFoundException(errorMessage);
        }

        final Set<String> serverNamesFromNodeList = new HashSet<String>();
        for (final Map<String, Object> serverDetails : serverDetailsList) {
            serverNamesFromNodeList.add(serverDetails.get(ServerInfo.SERVER_NAME).toString());
        }
        boolean isServerNamesExists = true;
        if (inputServerNamesList.size() > serverNamesFromNodeList.size()) {
            isServerNamesExists = false;
        } else if (!serverNamesFromNodeList.containsAll(inputServerNamesList)) {
            isServerNamesExists = false;
        }
        if (!isServerNamesExists) {
            final String errorMessage = String.format(NscsErrorCodes.RTSEL_SERVER_NAMES_NOT_FOUND_TO_DELETE, inputServerNamesList);
            nscsLogger.error(errorMessage);
            throw new ServerNameNotFoundException(errorMessage);
        }

    }

    /**
     * This method will check the duplicate nodes form the input xml file
     * 
     * @param nodeRtselDetailsList
     *            list of NodeRTSELDetails objects
     * @param invalidNodesErrorMap
     *            map of invalid node details
     * @return Set of duplicates nodes
     */
    public Set<String> getDuplicateNodesForRtselDeleteServer(final List<NodeRtselDetails> nodeRtselDetailsList, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        final Set<String> uniqueNodes = new HashSet<String>();
        final Set<String> duplicateNodes = new HashSet<String>();
        for (final NodeRtselDetails nodeRTSELDetails : nodeRtselDetailsList) {
            for (final String nodeFdn : nodeRTSELDetails.getNodes().getNodeFdn()) {
                if (!uniqueNodes.add(nodeFdn)) {
                    duplicateNodes.add(nodeFdn);
                }
            }
        }
        for (final String duplicateNode : duplicateNodes) {
            invalidNodesErrorMap.put(NodeRef.from(duplicateNode).get(0), new DuplicateNodeNamesException(NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NODE_NAMES,
                    NscsErrorCodes.RTSEL_DELETE_SERVER_DUPLICATE_NODE_FDN).setSuggestedSolution(NscsErrorCodes.RTSEL_DELETE_SERVER_DUPLICATE_NOT_ALLOWED));
        }
        return duplicateNodes;
    }

}
