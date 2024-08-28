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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedAlgorithmException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.impl.GetCiphersConfigurationImpl;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConfigurationUtil;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

/**
 * This class defines the methods to validate command and nodes for ciphers configuration.
 *
 * @author tcsnapa
 *
 */
public class CiphersConfigurationCommonValidator {

    @Inject
    protected NscsLogger nscsLogger;

    @Inject
    protected NscsCMReaderService reader;

    @Inject
    protected NodeValidatorUtility nodeValidatorUtility;

    @Inject
    protected NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    protected CiphersConfigurationUtil ciphersConfigurationUtil;

    @Inject
    protected GetCiphersConfigurationImpl getCiphersConfigurationImpl;

    @Inject
    protected EmptyCiphersValidator emptyCiphersValidator;

    /**
     * This method is used to validate the ciphers configuration commands(set and get ciphers)
     *
     * @param command
     *            CLI command of type CiphersConfigCommand
     * @throws InvalidArgumentValueException
     *             is thrown when invalid arguments are given for the command parameters
     */
    public void validateCommand(final CiphersConfigCommand command) throws InvalidArgumentValueException {
        final String protocolType = command.getProtocolProperty();
        final List<String> validArgsList = new ArrayList<String>();
        String errorMessage = "";
        boolean isCommandValid = true;
        if (protocolType != null) {
            switch (protocolType) {
            case CiphersConstants.PROTOCOL_TYPE_SSH: {
                if (command.getCipherFilterProperty() != null
                        || command.getKexProperty() == null && command.getMacsProperty() == null && command.getEncryptAlgosProperty() == null) {
                    validArgsList.addAll(ciphersConfigurationUtil.getValidArgsToSetSshCiphers());
                    errorMessage = String.format(" Invalid argument(s) given for the protocol %s",
                            command.getProtocolProperty() + ". Accepted arguments are " + validArgsList);
                    isCommandValid = false;
                }
            }
                break;
            case CiphersConstants.PROTOCOL_TYPE_TLS: {
                if (command.getCipherFilterProperty() == null || command.getKexProperty() != null || command.getMacsProperty() != null
                        || command.getEncryptAlgosProperty() != null) {
                    validArgsList.addAll(ciphersConfigurationUtil.getValidArgsToSetTlsCiphers());
                    errorMessage = String.format(" Invalid argument(s) given for the protocol %s",
                            command.getProtocolProperty() + ". Accepted arguments are " + validArgsList);
                    isCommandValid = false;
                }
            }
                break;
            default: {
                validArgsList.addAll(ciphersConfigurationUtil.getValidProtocolTypesForCiphersConfiguration());
                errorMessage = String.format("Invalid argument given for protocol. Accepted arguments are " + validArgsList);
                isCommandValid = false;
            }
            }
        } else {
            throw new CommandSyntaxException();
        }
        if (!isCommandValid) {
            nscsLogger.error(errorMessage);
            nscsLogger.commandHandlerFinishedWithError(command, errorMessage);
            throw new InvalidArgumentValueException(NscsErrorCodes.CIPHERS_CONFIG_INVALID_ARGUMENT_VALUE, errorMessage)
                    .setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SYNTAX);
        }
    }

    /**
     * This method is used to validate the nodes (set and get ciphers)
     *
     * @param normNode
     * @param nodeRef
     * @param protocolType
     *
     * @return boolean
     *
     * @throws InvalidNodeNameException
     * @throws NetworkElementNotfoundException
     * @throws NodeNotCertifiableException
     * @throws NodeNotSynchronizedException
     * @throws NscsCapabilityModelException
     * @throws UnassociatedNetworkElementException
     * @throws UnsupportedNodeTypeException
     * @throws UnSupportedNodeReleaseVersionException
     */
    public boolean validateNode(final NormalizableNodeReference normNode, final NodeReference nodeRef, final String protocolType)
            throws InvalidNodeNameException, NetworkElementNotfoundException, NodeNotCertifiableException, NodeNotSynchronizedException,
            NscsCapabilityModelException, UnassociatedNetworkElementException, UnsupportedNodeTypeException, UnSupportedNodeReleaseVersionException {
        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
                if (reader.exists(nodeRef.getFdn())) {
                    nscsLogger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    throw new UnassociatedNetworkElementException();
                }
            }
            nscsLogger.error("Invalid Node Name [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }

        final NodeReference normalizedNodeRef = normNode.getNormalizedRef();

        if (normalizedNodeRef == null || !nodeValidatorUtility.isNodeExists(normalizedNodeRef)) {
            nscsLogger.error("NetworkElement [{}] doesn't exist.", normNode.getFdn());
            throw new NetworkElementNotfoundException();
        }

        validateNodeType(normNode, Arrays.asList(protocolType));
        validateNodeReleaseVersion(normNode);

        if (protocolType.equals(CiphersConstants.PROTOCOL_TYPE_TLS)) {
            if (!nodeValidatorUtility.isCertificateSupportedForNode(normNode)) {
                nscsLogger.error("Node [{}] must be certifiable for TLS based communication.", normNode.getFdn());
                throw new NodeNotCertifiableException();
            }
        }

        if (!nodeValidatorUtility.isNodeSynchronized(normNode)) {
            nscsLogger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }
        return true;
    }

    /**
     * This method validates for the node release version, invalid nodes, unsupported node type.
     *
     * @param nodeRef
     * @param inputProtocolTypes
     * @return
     *
     * @throws InvalidNodeNameException
     * @throws NetworkElementNotfoundException
     * @throws NodeNotCertifiableException
     * @throws NodeNotSynchronizedException
     * @throws NscsCapabilityModelException
     * @throws UnassociatedNetworkElementException
     * @throws UnsupportedNodeTypeException
     * @throws UnSupportedNodeReleaseVersionException
     */
    public boolean validateNode(final NodeReference nodeRef, final List<String> inputProtocolTypes) throws InvalidNodeNameException,
            NetworkElementNotfoundException, NscsCapabilityModelException, NodeNotCertifiableException, NodeNotSynchronizedException,
            NscsCapabilityModelException, UnassociatedNetworkElementException, UnsupportedNodeTypeException, UnSupportedNodeReleaseVersionException {

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
                if (reader.exists(nodeRef.getFdn())) {
                    nscsLogger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    throw new UnassociatedNetworkElementException();
                }
            }
            nscsLogger.error("Invalid Node Name [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }

        final NodeReference normalizedNodeRef = normNode.getNormalizedRef();

        if (normalizedNodeRef == null || !nodeValidatorUtility.isNodeExists(normalizedNodeRef)) {
            nscsLogger.error("NetworkElement [{}] doesn't exist.", normNode.getFdn());
            throw new NetworkElementNotfoundException();
        }

        validateNodeType(normNode, inputProtocolTypes);
        validateNodeReleaseVersion(normNode);

        if (inputProtocolTypes.contains(CiphersConstants.PROTOCOL_TYPE_TLS)) {
            if (!nodeValidatorUtility.isCertificateSupportedForNode(normNode)) {
                nscsLogger.error("Node [{}] must be certifiable for TLS based communication.", normNode.getFdn());
                throw new NodeNotCertifiableException();
            }
        }

        nscsLogger.info("Node [{}] need not to be certifiable for SSH based communication.", normNode.getFdn());

        if (!nodeValidatorUtility.isNodeSynchronized(normNode)) {
            nscsLogger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }
        return true;
    }

    /**
     * This method is uses to validate the node to check whether it is supported or not for set ciphers command.
     *
     * @param normNode
     * @param inputProtocolTypes
     * @throws UnsupportedNodeTypeException
     */
    public void validateNodeType(final NormalizableNodeReference normNode, final List<String> inputProtocolTypes)
            throws UnsupportedNodeTypeException {
        boolean isSupported = true;
        final boolean isCliCommandSupported = nscsCapabilityModelService.isCliCommandSupported(normNode, NscsCapabilityModelService.CIPHERS_COMMAND);
        if (isCliCommandSupported) {
            final List<String> supportedProtocolTypes = nscsCapabilityModelService.getSupportedCipherProtocolTypes(normNode);
            for (final String protocolType : inputProtocolTypes) {
                if (!supportedProtocolTypes.contains(protocolType)) {
                    isSupported = false;
                    break;
                }
            }
        } else {
            isSupported = false;
        }
        if (!isSupported) {
            final String errorMsg = String.format("Unsupported node type %s  for %s ciphers configuration.", normNode.getNeType(),
                    inputProtocolTypes);
            nscsLogger.error("NE Type validation failed: {}", errorMsg);
            throw new UnsupportedNodeTypeException();
        }
    }

    /**
     * This method is used to validate the node release version. .
     *
     * @param normNode
     * @throws UnSupportedNodeReleaseVersionException
     */
    public void validateNodeReleaseVersion(final NormalizableNodeReference normNode) throws UnSupportedNodeReleaseVersionException {
        final NodeModelInformation nodemodelInfo = reader.getNodeModelInformation(normNode.getFdn());
        nscsLogger.info("Validating node release version {} of Node[{}] for ciphers configuration ", nodemodelInfo.getModelIdentifier(),
                normNode.getFdn());
        final Boolean isCiphersConfigurationSupported = nscsCapabilityModelService.isCiphersConfigurationSupported(nodemodelInfo);
        if (!isCiphersConfigurationSupported) {
            nscsLogger.error("Node Release version[{}] is not supported for ciphers configuration ", nodemodelInfo.getModelIdentifier());
            throw new UnSupportedNodeReleaseVersionException()
                    .setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE_RELEASE_VERSION);
        }
    }

    /**
     * this method reads supported algorithms from the node and validate with the input algorithms. If the input algorithm is not present in supported
     * algorithms list, that node will be added to the errorCiphers list.
     *
     * @param normNode
     * @param nodeCiphers
     * @throws UnsupportedAlgorithmException
     */
    public void validateSupportedCiphersForSsh(final NormalizableNodeReference normNode, final NodeCiphers nodeCiphers)
            throws UnsupportedAlgorithmException {
        final Map<String, Map<String, List<String>>> ciphersMap = getCiphersConfigurationImpl.getSshCiphers(normNode,
                CiphersConstants.PROTOCOL_TYPE_SSH);
        if (ciphersMap == null) {
            return;
        }
        final List<String> errorCiphers = new ArrayList<String>();
        final Map<String, List<String>> supportedCiphersMap = ciphersMap.get(CiphersConstants.SUPPORTED_CIPHERS);

        if (supportedCiphersMap.get(CiphersConstants.ENCRYPTION_ALGORITHMS) == null
                || supportedCiphersMap.get(CiphersConstants.KEY_EXCHANGE_ALGORITHMS) == null
                || supportedCiphersMap.get(CiphersConstants.MAC_ALGORITHMS) == null) {
            return;
        }
        if (nodeCiphers.getSshProtocol().getEncryptCiphers() != null && !nodeCiphers.getSshProtocol().getEncryptCiphers().getCipher().isEmpty()
                && !validateSupportedCiphers(nodeCiphers.getSshProtocol().getEncryptCiphers().getCipher(),
                        supportedCiphersMap.get(CiphersConstants.ENCRYPTION_ALGORITHMS))) {
            errorCiphers.add(CiphersConstants.ENCRYPTION_ALGORITHMS);
        }

        if (nodeCiphers.getSshProtocol().getKeyExchangeCiphers() != null
                && !nodeCiphers.getSshProtocol().getKeyExchangeCiphers().getCipher().isEmpty()
                && !validateSupportedCiphers(nodeCiphers.getSshProtocol().getKeyExchangeCiphers().getCipher(),
                        supportedCiphersMap.get(CiphersConstants.KEY_EXCHANGE_ALGORITHMS))) {
            errorCiphers.add(CiphersConstants.KEY_EXCHANGE_ALGORITHMS);
        }

        if (nodeCiphers.getSshProtocol().getMacCiphers() != null && !nodeCiphers.getSshProtocol().getMacCiphers().getCipher().isEmpty()
                && !validateSupportedCiphers(nodeCiphers.getSshProtocol().getMacCiphers().getCipher(),
                        supportedCiphersMap.get(CiphersConstants.MAC_ALGORITHMS))) {
            errorCiphers.add(CiphersConstants.MAC_ALGORITHMS);
        }

        if (!errorCiphers.isEmpty()) {
            nscsLogger.error(NscsErrorCodes.UNSUPPORTED_ALGORITHM + ":Found in {} ", errorCiphers);
            throw new UnsupportedAlgorithmException("Found in " + errorCiphers);
        }
    }

    private boolean validateSupportedCiphers(final List<String> inputCiphersList, final List<String> supportedCiphersList) {
        if (!supportedCiphersList.containsAll(inputCiphersList)) {
            return false;
        }
        return true;
    }

    /**
     * This method checks for sshProtocol algorithms in the input XML file. If there is no algorithm present under sshProtocol element, it throws the
     * InvalidInputXMLFileException.
     *
     * @param nodeCiphers
     * @throws InvalidInputXMLFileException
     */
    public void validateXMLForSSH(final NodeCiphers nodeCiphers) throws InvalidInputXMLFileException {
        if (nodeCiphers.getSshProtocol().getEncryptCiphers() == null && nodeCiphers.getSshProtocol().getKeyExchangeCiphers() == null
                && nodeCiphers.getSshProtocol().getMacCiphers() == null) {
            nscsLogger.error(String.format("Invalid input xml for"));
            throw new InvalidInputXMLFileException();
        }

    }

}
