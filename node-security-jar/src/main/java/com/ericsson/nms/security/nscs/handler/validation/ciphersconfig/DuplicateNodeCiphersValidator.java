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
import java.util.concurrent.ConcurrentHashMap;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.*;

/**
 * This class defines the methods to validate duplicate node ciphers for ciphers configuration.
 * 
* @author tcsvijc
 *
 */
public class DuplicateNodeCiphersValidator extends CiphersConfigurationCommonValidator{

    /**
     * This method is used to validate the nodes list given as an input to check whether it has any duplicate protocol values for the same node.
     * 
     *  If it contains duplicate protocol types for the same node, it ignores the node for processing and add it to invalidNodesErrorMap.
     * 
     * @param inputNodeCiphersList
     *                   is the list of NodeReference values
     * @param invalidNodesErrorMap
     *                   invalid nodes are added to this map
     * @return Map<String, NodeCiphers>
     *                   map of valid NodeCiphers objects with key as node name and NodeCiphers object as value
     */
    public Map<String, NodeCiphers> validate(final List<NodeCiphers> inputNodeCiphersList, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        
        nscsLogger.info("DuplicateNodeCiphersValidator.validate() method starts..");
        final Map<String, NodeCiphers> nodeCiphersStagedMap = new ConcurrentHashMap<String, NodeCiphers>();
        final Set<String> duplicateSshSet = new HashSet<String>();
        final Set<String> duplicateTlsSet = new HashSet<String>();
        Set<String> nodeReference = null;
        NodeCiphers targetNodeCiphers;
        String nodeName;
        for (NodeCiphers nodeCiphers : inputNodeCiphersList) {
            nodeReference = nodeCiphers.getNodes().getNodeFdn();
            for (String node : nodeReference) {
                if (!nodeCiphersStagedMap.containsKey(node)) {
                    targetNodeCiphers = constructTargetNodeCipher(node, nodeCiphers);
                    nodeCiphersStagedMap.put(node, targetNodeCiphers);
                } else {
                    targetNodeCiphers = nodeCiphersStagedMap.get(node);
                }
                removeDuplicateCiphers(node, nodeCiphers, targetNodeCiphers, duplicateSshSet, duplicateTlsSet);
            }
        }

        for (Map.Entry<String, NodeCiphers> entry : nodeCiphersStagedMap.entrySet()) {
            nodeName = entry.getKey();
            targetNodeCiphers = entry.getValue();
            if (duplicateSshSet.contains(nodeName) && duplicateTlsSet.contains(nodeName)) {
                nodeCiphersStagedMap.remove(nodeName);
                invalidNodesErrorMap.put(NodeRef.from(nodeName).get(0), new DuplicateNodeNamesException(NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_NODE_NAMES,
                        NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_SSH_TLS ).setSuggestedSolution(NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_NOT_ALLOWED));
            } else if (duplicateSshSet.contains(nodeName)) {
                targetNodeCiphers.setSshProtocol(null);
                if(targetNodeCiphers.getTlsProtocol() == null){
                    nodeCiphersStagedMap.remove(nodeName);
                }
                invalidNodesErrorMap.put(NodeRef.from(nodeName).get(0), new DuplicateNodeNamesException(NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_NODE_NAMES,
                        NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_SSH).setSuggestedSolution(NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_NOT_ALLOWED));
            } else if (duplicateTlsSet.contains(nodeName)) {
                targetNodeCiphers.setTlsProtocol(null);
                if(targetNodeCiphers.getSshProtocol() == null){
                    nodeCiphersStagedMap.remove(nodeName);
                }
                invalidNodesErrorMap.put(NodeRef.from(nodeName).get(0), new DuplicateNodeNamesException(NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_NODE_NAMES,
                        NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_TLS).setSuggestedSolution(NscsErrorCodes.CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_NOT_ALLOWED));
            }
        }
        nscsLogger.info("DuplicateNodeCiphersValidator.validate() method ends..");
        return nodeCiphersStagedMap;
    }

    private NodeCiphers constructTargetNodeCipher(final String nodeName, final NodeCiphers nodeCiphers) {
        final NodeCiphers tempNodeCiphers = new NodeCiphers();
        final Nodes nodes = new Nodes();
        final HashSet<String> nodesSet = new HashSet<String>();
        tempNodeCiphers.setNodes(nodes);
        tempNodeCiphers.getNodes().setNodeFdn(nodesSet);
        tempNodeCiphers.getNodes().getNodeFdn().add(nodeName);
        return tempNodeCiphers;
    }

    private void removeDuplicateCiphers(final String nodeName, final NodeCiphers sourceNodeCiphers, final NodeCiphers targetNodeCiphers, final Set<String> duplicateSshSet,
            final Set<String> duplicateTlsSet) {
        removeDuplicateSshCiphers(nodeName, sourceNodeCiphers, targetNodeCiphers, duplicateSshSet);
        removeDuplicateTlsCiphers(nodeName, sourceNodeCiphers, targetNodeCiphers, duplicateTlsSet);
    }

    private void removeDuplicateSshCiphers(final String nodeName, final NodeCiphers sourceNodeCiphers, final NodeCiphers targetNodeCiphers, final Set<String> duplicateSshSet) {
        SshProtocol sshProtocol = null;

        if (sourceNodeCiphers.getSshProtocol() != null) {
            if (targetNodeCiphers.getSshProtocol() == null) {
                sshProtocol = new SshProtocol();
                targetNodeCiphers.setSshProtocol(sshProtocol);
                if (sourceNodeCiphers.getSshProtocol().getEncryptCiphers() != null) {
                    EncryptCiphers encryptCiphers = new EncryptCiphers();
                    encryptCiphers.setCipher(sourceNodeCiphers.getSshProtocol().getEncryptCiphers().getCipher());
                    sshProtocol.setEncryptCiphers(encryptCiphers);
                }
                if (sourceNodeCiphers.getSshProtocol().getKeyExchangeCiphers() != null) {
                    KeyExchangeCiphers keyExchangeCiphers = new KeyExchangeCiphers();
                    keyExchangeCiphers.setCipher(sourceNodeCiphers.getSshProtocol().getKeyExchangeCiphers().getCipher());
                    sshProtocol.setKeyExchangeCiphers(keyExchangeCiphers);
                }
                if (sourceNodeCiphers.getSshProtocol().getMacCiphers() != null) {
                    MacCiphers macCiphers = new MacCiphers();
                    macCiphers.setCipher(sourceNodeCiphers.getSshProtocol().getMacCiphers().getCipher());
                    sshProtocol.setMacCiphers(macCiphers);
                }
            } else {
                duplicateSshSet.add(nodeName);
            }
        }
    }

    private void removeDuplicateTlsCiphers(final String nodeName, final NodeCiphers sourceNodeCiphers, final NodeCiphers targetNodeCiphers, final Set<String> duplicateTlsSet) {
        TlsProtocol tlsProtocol = null;

        if (sourceNodeCiphers.getTlsProtocol() != null) {
            if (targetNodeCiphers.getTlsProtocol() == null) {
                tlsProtocol = new TlsProtocol();
                targetNodeCiphers.setTlsProtocol(tlsProtocol);
                if (sourceNodeCiphers.getTlsProtocol().getCipherFilter() != null) {
                    tlsProtocol.setCipherFilter(sourceNodeCiphers.getTlsProtocol().getCipherFilter());
                }
            } else {
                duplicateTlsSet.add(nodeName);
            }
        }
    }
}
