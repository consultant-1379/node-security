/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ssh;

import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED;
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED;
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.api.exception.InvalidAlgorithmKeySizeInNetworkElementSecurityException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.KeypairAlreadyGeneratedException;
import com.ericsson.nms.security.nscs.api.exception.KeypairNotFoundException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
public class SSHKeyNodeValidatorUtility {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    MoAttributeHandler moAttributeHandler;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    private Collection<String> supportedAlgorithmAndKeySize = null;

    /**
     * Validate the given list of nodes for sshkey commands.
     * 
     * Creates a map of algorithm and key size for valid nodes and a map of error for the invalid nodes.
     * 
     * For sshkey update the command can have a null algorithm type size parameter and in this case the algorithm and key size shall be extracted from
     * the NetworkElementSecurity MO. Due to TORF-708897 (the range of allowed values has been changed), it can happen that the value stored in
     * NetworkElementSecurity MO is no more valid (MO created with old version).
     * 
     * @param inputNodesList
     *            the input list of nodes.
     * @param validNodes
     *            the map of algorithm and key size for the valid nodes.
     * @param invalidNodesErrorMap
     *            the map of error for the invalid nodes.
     * @param sshkeyOperation
     *            the sshkey operation type.
     * @param algorithm
     *            the algorithm type size specified in sshkey command. It can be null for update operation.
     * @return
     */
    public boolean validateSshKeyInputNodes(final List<NodeReference> inputNodesList, final Map<NodeReference, String> validNodes,
            final Map<String, NscsServiceException> invalidNodesErrorMap, final String sshkeyOperation, final String algorithm) {

        boolean havingAllValidNodes = true;

        if ((algorithm == null || algorithm.isEmpty()) && SSH_KEY_TO_BE_UPDATED.equals(sshkeyOperation)) {
            // Get supported algorithm and key size from model
            supportedAlgorithmAndKeySize = nscsModelServiceImpl.getSupportedAlgorithmAndKeySize();
            nscsLogger.info("From model service: supportedAlgorithmAndKeySize is {}", supportedAlgorithmAndKeySize);
        }

        for (final NodeReference nodeRef : inputNodesList) {
            try {
                nscsLogger.info("Validating sshkey node fdn=[{}] name=[{}]", nodeRef.getFdn(), nodeRef.getName());
                final String algorithmAndKeySize = validateNodeSshKey(nodeRef, sshkeyOperation, algorithm);
                validNodes.put(nodeRef, algorithmAndKeySize);
            } catch (NetworkElementNotfoundException |  SecurityFunctionMoNotfoundException |
                     NetworkElementSecurityNotfoundException | KeypairAlreadyGeneratedException |
                     KeypairNotFoundException | CouldNotReadMoAttributeException |
                    InvalidArgumentValueException | InvalidAlgorithmKeySizeInNetworkElementSecurityException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef.getName(), exc);
                nscsLogger.error("Node [{}] has validation problem. Exception Message is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    private String validateNodeSshKey(final NodeReference nodeRef, final String sshkeyOperation, final String algorithm) {
        if (!isNodeExists(nodeRef)) {
            throw new NetworkElementNotfoundException();
        }

        if (!isNodeHasSecurityFunction(nodeRef)) {
            throw new SecurityFunctionMoNotfoundException();
        }

        if (!isNodeHasNetworkElementSecurity(nodeRef)) {
            throw  new NetworkElementSecurityNotfoundException();
        }

        return validateSshKeys(nodeRef, sshkeyOperation, algorithm);
    }

    private boolean isNodeExists(final NodeReference nodeRef) {
        return Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn()) && reader.exists(nodeRef.getFdn());
    }

    private boolean isNodeHasSecurityFunction(final NodeReference nodeRef) {
        return reader.exists(Objects.requireNonNull(Model.getNomalizedRootMO(nodeRef.getFdn())).
                securityFunction.withNames(nodeRef.getName()).fdn());
    }

    private boolean isNodeHasNetworkElementSecurity(final NodeReference nodeRef) {
        return reader.exists(Objects.requireNonNull(Model.getNomalizedRootMO(nodeRef.getFdn()))
                .securityFunction.networkElementSecurity.withNames(nodeRef.getName()).fdn());
    }

    private String validateSshKeys(final NodeReference nodeRef, final String sshkeyOperation, final String algorithm) {
        validateSshKey(nodeRef, sshkeyOperation, ModelDefinition.NetworkElementSecurity.ENM_SSH_PUBLIC_KEY);
        validateSshKey(nodeRef, sshkeyOperation, ModelDefinition.NetworkElementSecurity.ENM_SSH_PRIVATE_KEY);
        return validateAlgorithmTypeSize(nodeRef, sshkeyOperation, algorithm);
    }

    /**
     * Validate algorithm type size parameter of sshkey command for the given node.
     * 
     * For sshkey update the command can have a null algorithm type size parameter and in this case the algorithm and key size shall be extracted from
     * the NetworkElementSecurity MO. Due to TORF-708897 (the range of allowed values has been changed), it can happen that the value stored in
     * NetworkElementSecurity MO is no more valid (MO created with old version).
     * 
     * @param nodeRef
     *            the node reference.
     * @param sshkeyOperation
     *            the sshkey operation type.
     * @param algorithm
     *            the algorithm type size in the command.
     * @return the algorithm and key size to be used for this node.
     */
    private String validateAlgorithmTypeSize(final NodeReference nodeRef, final String sshkeyOperation, final String algorithm) {
        String algorithmAndKeySize = algorithm;
        if (algorithm == null || algorithm.isEmpty()) {
            // get algorithm and key size from NetworkElementSecurity MO
            algorithmAndKeySize = moAttributeHandler.getMOAttributeValue(nodeRef.getFdn(),
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(),
                    ModelDefinition.NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE);
            if (SSH_KEY_TO_BE_UPDATED.equals(sshkeyOperation) && !supportedAlgorithmAndKeySize.contains(algorithmAndKeySize)) {
                final String errmsg = String.format("NetworkElementSecurity MO has invalid value %s in attribute %s. Accepted values are %s",
                        algorithmAndKeySize, ModelDefinition.NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE, supportedAlgorithmAndKeySize);
                nscsLogger.error(errmsg);
                throw new InvalidAlgorithmKeySizeInNetworkElementSecurityException(errmsg);
            }
        }
        return algorithmAndKeySize;
    }

    /**
     * @param nodeRef
     *            : the reference to the node
     * @param sshkeyOperation:
     *            the ssh key operation In creation ENM_SSH_PUBLIC_KEY/ENM_SSH_PRIVATE_KEY must be empty During updating/deletion the
     *            ENM_SSH_PUBLIC_KEY/ENM_SSH_PRIVATE_KEY must be filled
     */
    private void validateSshKey (final NodeReference nodeRef, final String sshkeyOperation, final String key) {
        boolean isKeyGenerated;
        String nodeFdn = nodeRef.getFdn();

        try {
            String enmSshPublicKey = moAttributeHandler.getMOAttributeValue(nodeFdn,
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(),
                    key);
            isKeyGenerated = (enmSshPublicKey != null) && !enmSshPublicKey.isEmpty();
        } catch (Exception exc) {
            nscsLogger.error("Node [{}] has problem reading ssh key public attribute from NES. Exception Message is [{}], exc is ; [{}]",
                    nodeFdn, exc.getMessage(), exc.getClass().getName());
            throw new CouldNotReadMoAttributeException();
        }

        switch (sshkeyOperation) {
            case SSH_KEY_TO_BE_UPDATED:
                if (!isKeyGenerated) {
                    throw new KeypairNotFoundException();
                }
                break;
            case SSH_KEY_TO_BE_DELETED:
                nscsLogger.info("Node [{}] always perform delete ",nodeFdn);
                break;
            case SSH_KEY_TO_BE_CREATED:
                if (isKeyGenerated) {
                    throw new KeypairAlreadyGeneratedException();
                }
                break;
            default:
                throw new InvalidArgumentValueException();
        }
    }
}
