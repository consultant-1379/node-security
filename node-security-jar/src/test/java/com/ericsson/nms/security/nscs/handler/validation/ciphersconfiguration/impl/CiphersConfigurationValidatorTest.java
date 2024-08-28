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
package com.ericsson.nms.security.nscs.handler.validation.ciphersconfiguration.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedAlgorithmException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelMock;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.impl.ComEcimMOGetServiceImpl;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.EncryptCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.KeyExchangeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.MacCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.SshProtocol;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.TlsProtocol;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.impl.GetCiphersConfigurationImpl;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConfigurationUtil;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.CiphersConfigurationCommonValidator;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.EmptyCiphersValidator;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.SetCiphersValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@RunWith(MockitoJUnitRunner.class)
public class CiphersConfigurationValidatorTest {

    @InjectMocks
    CiphersConfigurationCommonValidator ciphersConfigurationValidator;

    @InjectMocks
    SetCiphersValidator setCiphersValidator;

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CiphersConfigurationCommonValidator.class);

    @Mock
    private NormalizableNodeReference mockNormalizedNodeReference;

    @Mock
    private NscsCapabilityModelService mockNscsCapabilityModelService;

    @Mock
    NscsNodeUtility nscsNodeUtility;

    @Mock
    private NscsCMReaderService mockReaderService;

    @Mock
    private NodeModelInformation mockNodeModelInformation;

    @Mock
    private NscsLogger nscsLogger;

    @InjectMocks
    private NscsCapabilityModelMock capabilityModel;

    @Mock
    private CmResponse mockCMResponse;

    @Mock
    private CmObject mockCmObject;

    @Mock
    private Map<String, Object> mockAttributeMap;

    @Mock
    private CiphersConfigCommand command;

    @Mock
    NodeValidatorUtility nodeValidatorUtility;

    @Mock
    private GetCiphersConfigurationImpl getCiphersConfigurationImpl;

    @Mock
    private CiphersConfigurationUtil ciphersConfigurationUtil;

    @Mock
    private EmptyCiphersValidator emptyCiphersValidator;

    private Map<String, Object> commandMap;
    private Map<NodeReference, NscsServiceException> invalidNodesErrorMap;
    final List<Node> inputList = new LinkedList<Node>();
    private final List<NodeReference> inputNodeRefList = new LinkedList<NodeReference>();

    private static final String NODE_NAME = "Node1";

    private final NodeReference nodeRef = new NodeRef(NODE_NAME);

    private static final String KEY_EXCHANGE_ALGORITHMS = "diffie-hellman-group1-sha1,diffiehellman-group14-sha1";
    private static final String ENCRYPTION_ALGORITHMS = "3des-cbc,aes128-ctr";
    private static final String MAC_ALGORITHMS = "hmac-sha1,hmac-sha2-256";
    private static final String CIPHER_FILTER = "ALL:RSA";

    ComEcimMOGetServiceImpl comEcimMOGetServiceImpl = null;

    private final String ERBS_OSS_MODEL_IDENTITY = "17Q3-J.1.22";
    private final String ERBS_NE_TYPE = "ERBS";

    private final String RADIONODE_OSS_MODEL_IDENTITY = "17.Q3-R25C05";
    private final String RADIONODE_NE_TYPE = "RadioNode";
    private final String CIPHER_FILTER_VALUE = "-aRSA:-3DES:SHA256";
    private final String INVALID_PROTOCOL_TYPE = "ABC/DEF";
    private final String INVALID_MAC_ALGORITHM = "hmac-sha33,hmac-sha2-44";
    private final String EMPTY_CIPHER_FILTER = "";
    private final String INVALID_CIPHER_FILTER = ":SHA256:DES:MD5";

    @Before
    public void setup() {
        invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();
        inputNodeRefList.add(nodeRef);
        commandMap = new HashMap<String, Object>();
        comEcimMOGetServiceImpl = mock(ComEcimMOGetServiceImpl.class);
        Mockito.when(mockReaderService.getNormalizableNodeReference(Matchers.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);

        final Map<String, List<String>> ciphersMap = new HashMap<String, List<String>>();
        ciphersMap.put(CiphersConstants.KEY_EXCHANGE_ALGORITHMS, Arrays.asList(KEY_EXCHANGE_ALGORITHMS));
        ciphersMap.put(CiphersConstants.ENCRYPTION_ALGORITHMS, Arrays.asList(ENCRYPTION_ALGORITHMS));
        ciphersMap.put(CiphersConstants.MAC_ALGORITHMS, Arrays.asList(MAC_ALGORITHMS));

        final Map<String, Map<String, List<String>>> cipherMap = new HashMap<String, Map<String, List<String>>>();
        cipherMap.put(CiphersConstants.SUPPORTED_CIPHERS, ciphersMap);

        final List<String> validProtocolTypesForSetCiphers = new ArrayList<String>();
        validProtocolTypesForSetCiphers.add(CiphersConstants.ENCRYPT_ALGOS);
        validProtocolTypesForSetCiphers.add(CiphersConstants.KEX);
        validProtocolTypesForSetCiphers.add(CiphersConstants.MACS);

        when(getCiphersConfigurationImpl.getSshCiphers(mockNormalizedNodeReference, CiphersConstants.PROTOCOL_TYPE_SSH)).thenReturn(cipherMap);
        when(ciphersConfigurationUtil.getValidArgsToSetSshCiphers()).thenReturn(validProtocolTypesForSetCiphers);
    }

    /**
     * test method for {@link NscsCommandManagerBean#validateCommandForSetCiphers()} with invalid ssh command
     */
    @Test(expected = InvalidArgumentValueException.class)
    public void testValidateCommand_invalidSshCommand() {

        command = new CiphersConfigCommand();
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, CIPHER_FILTER_VALUE);
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_SSH);
        command.setProperties(commandMap);
        ciphersConfigurationValidator.validateCommand(command);
    }

    /**
     * test method for {@link NscsCommandManagerBean#validateCommandForSetCiphers()} with invalid tls command
     */
    @Test(expected = InvalidArgumentValueException.class)
    public void testValidateCommand_invalidTlsCommand() {
        command = new CiphersConfigCommand();
        commandMap.put(CiphersConfigCommand.KEX_PROPERTY, KEY_EXCHANGE_ALGORITHMS);
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        command.setProperties(commandMap);
        ciphersConfigurationValidator.validateCommand(command);
    }

    /**
     * test method for {@link NscsCommandManagerBean#validateCommandForSetCiphers()} with invalid protocol type
     */
    @Test(expected = InvalidArgumentValueException.class)
    public void testValidateCommand_invalidProtocol() {
        command = new CiphersConfigCommand();
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, INVALID_PROTOCOL_TYPE);
        command.setProperties(commandMap);
        ciphersConfigurationValidator.validateCommand(command);
    }

    /**
     * Test method to validate nodes successfully for SSH protocol
     */
    @Test
    public void testValidateNodes_SSH_SuccessScenario() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_SSH);
        commandMap.put(CiphersConfigCommand.MACS_PROPERTY, MAC_ALGORITHMS);
        commandMap.put(CiphersConfigCommand.KEX_PROPERTY, KEY_EXCHANGE_ALGORITHMS);
        commandMap.put(CiphersConfigCommand.ENCRYPT_ALGOS_PROPERTY, ENCRYPTION_ALGORITHMS);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(true);
        final NodeModelInformation nmi = new NodeModelInformation(RADIONODE_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                RADIONODE_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertTrue(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.isEmpty());
    }

    /**
     * Test method to throw UnsupportedAlgorithmException when nodes are validated against incorrect cipher value for SSH protocol
     */
    @Test
    public void testValidateCipherAlgorithms_SSH_UnsupportedAlgorithmException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_SSH);
        commandMap.put(CiphersConfigCommand.MACS_PROPERTY, INVALID_MAC_ALGORITHM);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(true);
        final NodeModelInformation nmi = new NodeModelInformation(RADIONODE_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                RADIONODE_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof UnsupportedAlgorithmException);
    }

    /**
     * Test method to validate nodes successfully for TLS protocol
     */
    @Test
    public void testValidateNodes_TLS_SuccessScenario() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, CIPHER_FILTER);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(true);
        final NodeModelInformation nmi = new NodeModelInformation(RADIONODE_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                RADIONODE_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertTrue(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.isEmpty());
    }

    /**
     * Test method to validate ERBS nodes against empty cipher filter value for TLS protocol
     */
    @Test
    public void testValidateNodes_TLS_Empty_Cipher_Filter_SuccessScenario() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, EMPTY_CIPHER_FILTER);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(true);
        final NodeModelInformation nmi = new NodeModelInformation(ERBS_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER, ERBS_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isEmptyValueSupportedForCiphers(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertTrue(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.isEmpty());
    }

    @Test
    public void testValidateNodes_UnassociatedNetworkElementException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_SSH);
        commandMap.put(CiphersConfigCommand.MACS_PROPERTY, MAC_ALGORITHMS);
        commandMap.put(CiphersConfigCommand.KEX_PROPERTY, KEY_EXCHANGE_ALGORITHMS);
        commandMap.put(CiphersConfigCommand.ENCRYPT_ALGOS_PROPERTY, ENCRYPTION_ALGORITHMS);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(mockReaderService.getNormalizableNodeReference(Matchers.any(NodeReference.class))).thenReturn(null);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof UnassociatedNetworkElementException);
    }

    @Test
    public void testValidateNodes_InvalidNodeNameException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, CIPHER_FILTER);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(mockReaderService.getNormalizableNodeReference(Matchers.any(NodeReference.class))).thenReturn(null);
        Mockito.when(mockReaderService.exists(Matchers.matches(nodeRef.getFdn()))).thenReturn(false);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof InvalidNodeNameException);
    }

    @Test
    public void testValidateNode_NetworkElementNotfoundException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(null);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof NetworkElementNotfoundException);
    }

    @Test
    public void testValidateNodes_UnsupportedNodeTypeException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(false);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof UnsupportedNodeTypeException);
    }

    @Test
    public void testValidateNodes_TLS_NodeNotCertifiableException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, CIPHER_FILTER);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(false);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(true);
        final NodeModelInformation nmi = new NodeModelInformation(RADIONODE_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                RADIONODE_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof NodeNotCertifiableException);
    }

    @Test
    public void testValidateNodes_SSH_NodeNotCertifiableException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_SSH);
        commandMap.put(CiphersConfigCommand.MACS_PROPERTY, MAC_ALGORITHMS);
        commandMap.put(CiphersConfigCommand.KEX_PROPERTY, KEY_EXCHANGE_ALGORITHMS);
        commandMap.put(CiphersConfigCommand.ENCRYPT_ALGOS_PROPERTY, ENCRYPTION_ALGORITHMS);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(false);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_SSH);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(true);
        final NodeModelInformation nmi = new NodeModelInformation(RADIONODE_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                RADIONODE_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertTrue(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.isEmpty());
    }

    @Test
    public void testValidateNodes_NodeNotCertifiableException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, CIPHER_FILTER);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_TLS);
        final NodeModelInformation nmi = new NodeModelInformation(RADIONODE_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                RADIONODE_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof NodeNotCertifiableException);
    }

    @Test
    public void testValidateNodes_NodeNotSynchronizedException() {
        final List<NodeReference> validNodesInXml = new ArrayList<NodeReference>();
        final NodeCiphers nodeCiphers;
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, CIPHER_FILTER);
        nodeCiphers = constructNodeCipher(commandMap);
        mockDataForValidateNode();
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(mockNscsCapabilityModelService.isCliCommandSupported(mockNormalizedNodeReference, NscsCapabilityModelService.CIPHERS_COMMAND))
                .thenReturn(true);
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_TLS);
        Mockito.when(nodeValidatorUtility.isNodeSynchronized(mockNormalizedNodeReference)).thenReturn(false);
        final NodeModelInformation nmi = new NodeModelInformation(RADIONODE_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                RADIONODE_NE_TYPE);
        Mockito.when(mockNscsCapabilityModelService.isCiphersConfigurationSupported(nmi)).thenReturn(true);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
        assertFalse(setCiphersValidator.validateNodes(command, nodeCiphers, validNodesInXml, invalidNodesErrorMap));
        assertTrue(invalidNodesErrorMap.get(nodeRef) instanceof NodeNotSynchronizedException);
    }

    private NodeCiphers constructNodeCipher(final Map<String, Object> commandMap) {
        final com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.Nodes nodes = new com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.Nodes();
        final NodeCiphers nodeCiphers = new NodeCiphers();
        final HashSet<String> nodesSet = new HashSet<String>();
        nodeCiphers.setNodes(nodes);
        nodeCiphers.getNodes().setNodeFdn(nodesSet);
        nodeCiphers.getNodes().getNodeFdn().add(NODE_NAME);
        if (CiphersConstants.PROTOCOL_TYPE_TLS.equals(commandMap.get(CiphersConfigCommand.PROTOCOL_PROPERTY))) {
            final TlsProtocol tlsProtocol = new TlsProtocol();
            tlsProtocol.setCipherFilter((String) commandMap.get(CiphersConfigCommand.CIPHER_FILTER_PROPERTY));
            nodeCiphers.setTlsProtocol(tlsProtocol);
        } else if (CiphersConstants.PROTOCOL_TYPE_SSH.equals(commandMap.get(CiphersConfigCommand.PROTOCOL_PROPERTY))) {
            final SshProtocol sshProtocol = new SshProtocol();
            final EncryptCiphers encryptCiphers = new EncryptCiphers();
            final KeyExchangeCiphers keyExchangeCiphers = new KeyExchangeCiphers();
            final MacCiphers macCiphers = new MacCiphers();

            encryptCiphers.setCipher(Arrays.asList((String) commandMap.get(CiphersConfigCommand.ENCRYPT_ALGOS_PROPERTY)));
            keyExchangeCiphers.setCipher(Arrays.asList((String) commandMap.get(CiphersConfigCommand.KEX_PROPERTY)));
            macCiphers.setCipher(Arrays.asList((String) commandMap.get(CiphersConfigCommand.MACS_PROPERTY)));

            sshProtocol.setEncryptCiphers(encryptCiphers);
            sshProtocol.setKeyExchangeCiphers(keyExchangeCiphers);
            sshProtocol.setMacCiphers(macCiphers);

            nodeCiphers.setSshProtocol(sshProtocol);
        }
        return nodeCiphers;
    }

    public void mockDataForValidateNode() {
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(command.getNodes()).thenReturn(inputNodeRefList);
        Mockito.when(mockReaderService.getNormalizableNodeReference(Matchers.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
        Mockito.when(mockReaderService.exists(Matchers.matches(nodeRef.getFdn()))).thenReturn(true);
        doReturn(NODE_NAME).when(mockNormalizedNodeReference).getName();
        doReturn("RadioNode").when(mockNormalizedNodeReference).getNeType();
        doReturn(NODE_NAME).when(mockNormalizedNodeReference).getFdn();
        final String ossModelIdentity = mockNormalizedNodeReference.getOssModelIdentity();
        Mockito.when(mockNscsCapabilityModelService.getSupportedCipherProtocolTypes(mockNormalizedNodeReference))
                .thenReturn(Arrays.asList(CiphersConstants.PROTOCOL_TYPE_SSH, CiphersConstants.PROTOCOL_TYPE_TLS));
        Mockito.when(mockReaderService.getNormalizableNodeReference(Matchers.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        final NodeModelInformation nmi = new NodeModelInformation(ossModelIdentity, ModelIdentifierType.MIM_VERSION, NODE_NAME);
        Mockito.when(mockReaderService.getNodeModelInformation(Matchers.anyString())).thenReturn(nmi);
    }
}
