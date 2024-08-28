/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.utilities;

import static com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT;
import static com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes.REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE;
import static com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes.REQUESTED_ENTITY_PROFILE_NAME_DOES_NOT_EXIST;
import static com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID;
import static com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED;
import static com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY;
import static com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility.ACCEPTED_ARGUMENTS_ARE;
import static com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility.ACCEPTED_KEY_ALGORITHMS_ARE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.types.CrlCheckCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.AlgorithmKeySizeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.api.exception.InvalidEntityProfileNameDefaultXmlException;
import com.ericsson.nms.security.nscs.api.exception.InvalidEntityProfileNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputNodeListException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidSubjAltNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.MaxNodesExceededException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameSubjAltNameTypeEmptyXmlException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameTypeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCertificateTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedTrustCategoryTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelMock;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.validation.impl.TrustValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.CertificateProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;

@RunWith(MockitoJUnitRunner.class)
public class NodeValidatorUtilityTest {

    private final int MAX_NUM_WF_ALLOWED = 100;

    @Spy
    Logger logger = LoggerFactory.getLogger(NodeValidatorUtility.class);

    @Mock
    NscsLogger nscsLogger;

    @Mock
    private NscsCMReaderService readerServiceMock;

    @Mock
    private NodeReference nodeReferenceMock;

    @Mock
    private NormalizableNodeReference normalizedNodeReferenceMock;

    @Mock
    private CmResponse cmResponseMock;

    @Mock
    private CmObject cmObjectMock;

    @Mock
    private Map<String, Object> attributeMapMock;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelServiceMock;

    @Mock
    private NscsPkiEntitiesManagerIF nscsPkiManagerMock;

    @Mock
    private MoObject mo;

    @Mock
    private CertificateProfile certificateProfileMock;

    @Mock
    private EntityProfile entityProfileMock;

    @Mock
    private NodeModelInformation nodeModelInformationMock;

    @Mock
    private NSCSComEcimNodeUtility nodeUtilityMock;

    @Mock
    ModelDefinition modelDefinitionMock;

    @Mock
    private MOGetServiceFactory moGetServiceFactoryMock;

    @Mock
    MOGetService moGetServiceMock;

    @Mock
    private NscsNodeUtility nscsNodeUtilityMock;

    @Mock
    private MoAttributeHandler moAttributeHandlerMock;

    @InjectMocks
    private NodeValidatorUtility testObj;

    @Mock
    TrustValidator trustValidator;

    private final String nodeName = "Node123";
    private final String IPSEC = "IPSEC";
    private final String OAM = "OAM";
    private final NodeReference nodeRef = new NodeRef(nodeName);
    private static CrlCheckCommand command;
    private Map<String, Object> commandMap = new HashMap<String, Object>();
    private List<String> inputNodesList;
    private static String enableCrlCheckcommand;

    @InjectMocks
    NscsCapabilityModelMock capabilityModel;
    private static final String RADIO_NODE_NAME = "RADIO-NODE-123";
    private static final String RADIO_NODE_ROOT_FDN = String.format("ManagedElement=%s", RADIO_NODE_NAME);
    private static final String RADIO_NODE_CERT_M_FDN = String.format("%s,SystemFunctions=1,SecM=1,CertM=1", RADIO_NODE_ROOT_FDN);
    private static final String RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN = String.format("%s,TrustCategory=ipsecTrustCategory", RADIO_NODE_CERT_M_FDN);
    private static final NodeModelInformation RADIO_NODE_NAME_MODEL_INFO = new NodeModelInformation("17A-5.1.63", ModelIdentifierType.MIM_VERSION,
            RADIO_NODE_NAME);
    private static final String OnDemandCrlDownloadCommand = NscsCapabilityModelService.CRLDOWNLOAD_COMMAND;

    /**
     * Method to setup initial test data.
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        inputNodesList = new LinkedList<String>();
        inputNodesList.add("node1");
        inputNodesList.add("node2");
        inputNodesList.add("node3");

        commandMap = new HashMap<String, Object>();
        commandMap.put(CrlCheckCommand.CERT_TYPE_PROPERTY, IPSEC);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, inputNodesList);

        command = new CrlCheckCommand();
        command.setProperties(commandMap);

        enableCrlCheckcommand = command.toString();

    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNode(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test
    public void testValidateNode() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        assertTrue("Node is valid ", testObj.validateNode(nodeRef));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNode(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test(expected = NodeDoesNotExistException.class)
    public void testValidateNode_NodeDoesNotExistException() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        testObj.validateNode(nodeRef);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNode(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test(expected = UnassociatedNetworkElementException.class)
    public void testValidateNode_UnassociatedNetworkElementException() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        testObj.validateNode(nodeRef);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNode(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test(expected = NetworkElementNotfoundException.class)
    public void testValidateNode_NotExistingNode() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        testObj.validateNode(nodeRef);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNode(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test(expected = NodeNotCertifiableException.class)
    public void testValidateConfigParamsForNode_NotCertifiable() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(false);
        testObj.validateNode(nodeRef);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNode(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test(expected = NodeNotSynchronizedException.class)
    public void testValidateDynamicParamsForNode_NotSynch() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.PENDING.name());
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        testObj.validateNode(nodeRef);
    }

    @Test
    public void testValidateNodeIssueOam() throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException,
            NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException,
            InvalidInputNodeListException, InvalidEntityProfileNameXmlException, InvalidEntityProfileNameDefaultXmlException,
            AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException, SubjAltNameTypeNotSupportedXmlException,
            InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        assertTrue("Node is valid ", testObj.validateNodeIssue(inputNode, OAM));
    }

    @Test(expected = InvalidNodeNameException.class)
    public void testValidateNodeIssue_InvalidNodeNameException() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        final Node inputNode = new Node(nodeRef.getFdn());
        testObj.validateNodeIssue(inputNode, IPSEC);
    }

    @Test(expected = UnassociatedNetworkElementException.class)
    public void testValidateNodeIssue_UnassociatedNetworkElementException() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        testObj.validateNodeIssue(inputNode, IPSEC);
    }

    @Test(expected = NetworkElementNotfoundException.class)
    public void testValidateNodeIssue_NotExistingNode() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        final Node inputNode = new Node(nodeRef.getFdn());
        testObj.validateNodeIssue(inputNode, IPSEC);
    }

    @Test(expected = NodeNotCertifiableException.class)
    public void testValidateConfigParamsForNodeIssue_NotCertifiable() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(false);
        final Node inputNode = new Node(nodeRef.getFdn());
        testObj.validateNodeIssue(inputNode, IPSEC);
    }

    @Test(expected = UnsupportedCertificateTypeException.class)
    public void testValidateNodeIssue_UnsupportedCertType() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(false);
        final Node inputNode = new Node(nodeRef.getFdn());
        testObj.validateNodeIssue(inputNode, IPSEC);
    }

    @Test(expected = InvalidEntityProfileNameXmlException.class)
    public void testValidateIssue_InvalidEntityProfile() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        final String invalidEp = "Invalid_EP";
        inputNode.setEntityProfileName(invalidEp);
        when(nscsPkiManagerMock.isEntityProfileNameAvailable(inputNode.getEntityProfileName())).thenReturn(true);
        try {
            testObj.validateNodeIssue(inputNode, OAM);
        } catch (final Exception e) {
            assertEquals(REQUESTED_ENTITY_PROFILE_NAME_DOES_NOT_EXIST, e.getMessage());
            throw e;
        }
    }


    @Test(expected = AlgorithmKeySizeNotSupportedXmlException.class)
    public void testValidateIssue_ValidEntityProfile_UnsupportedKeySize() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        final String validEp = "Valid_EP";
        final String unsupportedKeySize = "RSA_1024";
        final String entityProfileName = "DUSGen2OAM_CHAIN_EP";
        inputNode.setEntityProfileName(validEp);
        inputNode.setKeySize(unsupportedKeySize);
        when(nscsPkiManagerMock.isEntityProfileNameAvailable(inputNode.getEntityProfileName())).thenReturn(false);
        final List<Algorithm> algorithms = new ArrayList<>();
        final Algorithm algorithm = new Algorithm();
        algorithm.setName("RSA");
        algorithm.setKeySize(2048);
        algorithms.add(algorithm);
        certificateProfileMock = new CertificateProfile();
        certificateProfileMock.setKeyGenerationAlgorithms(algorithms);
        entityProfileMock = new EntityProfile();
        entityProfileMock.setCertificateProfile(certificateProfileMock);
        entityProfileMock.setName(entityProfileName);
        when(nscsPkiManagerMock.getEntityProfile(inputNode.getEntityProfileName())).thenReturn(entityProfileMock);

        try {
            testObj.validateNodeIssue(inputNode, OAM);
        } catch (final Exception e) {
            assertEquals(REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE + " : " + "The given Key Algorithm [" + unsupportedKeySize + "] is not in supported list of Entity Profile ["
                    + entityProfileName + "]. " + ACCEPTED_KEY_ALGORITHMS_ARE + "[RSA_2048]", e.getMessage());
            throw e;
        }
    }


    @Test(expected = AlgorithmKeySizeNotSupportedXmlException.class)
    public void testValidateIssue_EmptyEntityProfile_ExistingEntity_UnsupportedKeySize() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        final String unsupportedKeySize = "RSA_1024";
        final String entityProfileName = "DUSGen2OAM_CHAIN_EP";
        inputNode.setKeySize(unsupportedKeySize);
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        final List<Algorithm> algorithms = new ArrayList<>();
        final Algorithm algorithm = new Algorithm();
        algorithm.setName("RSA");
        algorithm.setKeySize(2048);
        algorithms.add(algorithm);
        certificateProfileMock = new CertificateProfile();
        certificateProfileMock.setKeyGenerationAlgorithms(algorithms);
        entityProfileMock = new EntityProfile();
        entityProfileMock.setCertificateProfile(certificateProfileMock);
        entityProfileMock.setName(entityProfileName);
        final Entity entity = new Entity();
        entity.setEntityProfile(entityProfileMock);
        when(nscsPkiManagerMock.getPkiEntity(anyString())).thenReturn(entity);
        try {
            testObj.validateNodeIssue(inputNode, OAM);
        } catch (final Exception e) {
            assertEquals(REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE + " : " + "The given Key Algorithm [" + unsupportedKeySize + "] is not in supported list of Entity Profile ["
                    + entityProfileName + "]. " + ACCEPTED_KEY_ALGORITHMS_ARE + "[RSA_2048]", e.getMessage());
            throw e;
        }
    }


    @Test(expected = AlgorithmKeySizeNotSupportedXmlException.class)
    public void testValidateIssue_EmptyEntityProfile_NotExistingEntity_UnsupportedKeySize() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        final String unsupportedKeySize = "RSA_1024";
        final String entityProfileName = "DUSGen2OAM_CHAIN_EP";
        inputNode.setKeySize(unsupportedKeySize);
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        when(nscsCapabilityModelServiceMock.getDefaultEntityProfile(any(NodeModelInformation.class), any(NodeEntityCategory.class))).thenReturn(
                "Default_EP");
        when(nscsPkiManagerMock.isEntityProfileNameAvailable("Default_EP")).thenReturn(false);
        final List<Algorithm> algorithms = new ArrayList<>();
        final Algorithm algorithm = new Algorithm();
        algorithm.setName("RSA");
        algorithm.setKeySize(2048);
        algorithms.add(algorithm);
        certificateProfileMock = new CertificateProfile();
        certificateProfileMock.setKeyGenerationAlgorithms(algorithms);
        entityProfileMock = new EntityProfile();
        entityProfileMock.setCertificateProfile(certificateProfileMock);
        entityProfileMock.setName(entityProfileName);
        when(nscsPkiManagerMock.getEntityProfile(anyString())).thenReturn(entityProfileMock);
        try {
            testObj.validateNodeIssue(inputNode, OAM);
        } catch (final Exception e) {
            assertEquals(REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE + " : " + "The given Key Algorithm [" + unsupportedKeySize + "] is not in supported list of Entity Profile ["
                    + entityProfileName + "]. " + ACCEPTED_KEY_ALGORITHMS_ARE + "[RSA_2048]", e.getMessage());
            throw e;
        }
    }

    @Test(expected = SubjAltNameSubjAltNameTypeEmptyXmlException.class)
    public void testValidateIssue_NotExistingEntity_EmptySubjAltName_and_Type() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());

        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        try {
            testObj.validateNodeIssue(inputNode, IPSEC);
        } catch (final Exception e) {
            final String err = String.format(SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY);
            assertEquals(err, e.getMessage());
            throw e;
        }
    }

    @Test
    public void testValidateIssue_ExistingEntity_EmptySubjAltName_and_Type() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());

        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        synchronizationMock();

        assertTrue(testObj.validateNodeIssue(inputNode, IPSEC));
    }

    private void synchronizationMock() {
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(CmFunction.SyncStatusValue.SYNCHRONIZED.name());
    }

    @Test(expected = SubjAltNameTypeNotSupportedXmlException.class)
    public void testValidateIssue_ExistingEntity_UnsupportedSubjAltNameType() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        inputNode.setSubjectAltNameType("X400_ADDRESS");
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        try {
            testObj.validateNodeIssue(inputNode, IPSEC);
        } catch (final Exception e) {
            final List<String> supportedSubjectAltNameFieldType = new ArrayList<>();
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.IP_ADDRESS.name());
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.DNS_NAME.name());
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.RFC822_NAME.name());
            assertEquals(
                    REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED + " : " + ACCEPTED_ARGUMENTS_ARE + supportedSubjectAltNameFieldType,
                    e.getMessage());
            throw e;
        }
    }

    @Test(expected = InvalidSubjAltNameXmlException.class)
    public void testValidateIssue_ExistingEntity_UnsupportedSubjAltName() throws NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        inputNode.setSubjectAltNameType("IP_ADDRESS");
        inputNode.setSubjectAltName("300.168.0.42");
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        try {
            testObj.validateNodeIssue(inputNode, IPSEC);
        } catch (final Exception e) {
            final List<String> supportedSubjectAltNameFieldType = new ArrayList<>();
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.IP_ADDRESS.name());
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.DNS_NAME.name());
            assertEquals(REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID + PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT, e.getMessage());
            throw e;
        }
    }

    @Test(expected = NodeNotSynchronizedException.class)
    public void testValidateDynamicParamsForNodeIssue_NotSynch() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.PENDING.name());
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        final Node inputNode = new Node(nodeRef.getFdn());
        testObj.validateNodeIssue(inputNode, OAM);
    }

    // START tests of validateNodeTrustDistr method
    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNodeTrust(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test
    public void testValidateNodeTrustDistr() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, "OAM")).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        List<String> supportedTrustCategoryList = new ArrayList<>();
        supportedTrustCategoryList.add("IPSEC");
        when((List<String>)nscsCapabilityModelServiceMock.getCapabilityValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(supportedTrustCategoryList );
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNodeTrust(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test(expected = InvalidNodeNameException.class)
    public void testValidateNodeTrustDistr_InvalidNodeNameException() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        testObj.validateNodeTrust(nodeRef, OAM, false);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNodeTrust(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test(expected = UnassociatedNetworkElementException.class)
    public void testValidateNodeTrustDistr_UnassociatedNetworkElementException() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        testObj.validateNodeTrust(nodeRef, OAM, false);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNodeTrust(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test(expected = NetworkElementNotfoundException.class)
    public void testValidateNodeTrustDistr_NotExistingNode() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        testObj.validateNodeTrust(nodeRef, OAM, false);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNodeTrust(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test(expected = NodeNotCertifiableException.class)
    public void testValidateNodeTrustDistr_ConfigParams_NotCertifiable() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(false);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, NscsCapabilityModelService.TRUST_COMMAND)).thenReturn(
                true);
        testObj.validateNodeTrust(nodeRef, OAM, false);
    }

    @Test(expected = UnsupportedTrustCategoryTypeException.class)
    public void testValidateNodeTrustDistr_ConfigParams_IpsecCertUnsupported() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(false);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, NscsCapabilityModelService.TRUST_COMMAND)).thenReturn(
                true);
        List<String> supportedTrustCategoryList = new ArrayList<>();
        supportedTrustCategoryList.add(OAM);
        when((List<String>)nscsCapabilityModelServiceMock.getCapabilityValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(supportedTrustCategoryList );
        testObj.validateNodeTrust(nodeRef, IPSEC, false);
    }

    @Test(expected = UnsupportedTrustCategoryTypeException.class)
    public void testValidateNodeTrustDistr_ConfigParams_OamCertUnsupported() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(false);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, NscsCapabilityModelService.TRUST_COMMAND)).thenReturn(
                true);
        List<String> supportedTrustCategoryList = new ArrayList<>();
        supportedTrustCategoryList.add(IPSEC);
        when((List<String>)nscsCapabilityModelServiceMock.getCapabilityValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(supportedTrustCategoryList );
        testObj.validateNodeTrust(nodeRef, OAM, false);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNodeTrust(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test(expected = NodeNotSynchronizedException.class)
    public void testValidateNodeTrustDistr_DynamicParams_NotSynch() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, "OAM")).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(normalizedNodeReferenceMock.getNeType()).thenReturn("ERBS");
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.PENDING.name());
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        List<String> supportedTrustCategoryList = new ArrayList<>();
        supportedTrustCategoryList.add(OAM);
        when((List<String>)nscsCapabilityModelServiceMock.getCapabilityValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(supportedTrustCategoryList );
        when(nscsCapabilityModelServiceMock.isTrustCategoryTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, NscsCapabilityModelService.TRUST_COMMAND)).thenReturn(
                true);
        testObj.validateNodeTrust(nodeRef, OAM, false);
    }

    // end tests of validateNodeTrustDistr method

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isNodeExists(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test
    public void testIsNodeExists() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        assertTrue("Node must Exists", testObj.isNodeExists(nodeRef));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isNodeExists(com.ericsson.nms.security.nscs.api.model.NodeReference)} .
     */
    @Test
    public void testIsNodeExists_Neg() {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        assertFalse(testObj.isNodeExists(nodeRef));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#hasNodeSecurityFunctionMO(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test
    public void testIsNodeHasSecurityFunctionMO() {
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        assertTrue("Node must Exists", testObj.hasNodeSecurityFunctionMO(nodeRef));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#hasNodeSecurityFunctionMO(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test
    public void testIsNodeHasSecurityFunctionMO_Neg() {
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(false);
        assertFalse(testObj.hasNodeSecurityFunctionMO(nodeRef));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isNodeSynchronized(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test
    public void testIsNodeSynchronized() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        synchronizationMock();
        assertTrue("Node must be synchronized", testObj.isNodeSynchronized(normalizedNodeReferenceMock));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isNodeSynchronized(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test
    public void testIsNodeSynchronized_Neg() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.PENDING.name());
        assertFalse("Node must be synchronized", testObj.isNodeSynchronized(normalizedNodeReferenceMock));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isCertificateSupportedForNode(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test
    public void testIsCertificateSupportedForNode() {
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        assertTrue(testObj.isCertificateSupportedForNode(normalizedNodeReferenceMock));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isCertificateSupportedForNode(com.ericsson.nms.security.nscs.api.model.NodeReference)}
     * .
     */
    @Test
    public void testIsCertificateSupportedForNode_Neg() {
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(false);
        assertFalse(testObj.isCertificateSupportedForNode(normalizedNodeReferenceMock));
    }

    @Test
    public void testIsCliCommandSupported() {
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, "any")).thenReturn(true);
        assertTrue(testObj.isCliCommandSupported(normalizedNodeReferenceMock, "any"));
    }

    @Test
    public void testIsCliCommandSupported_Neg() {
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, "any")).thenReturn(false);
        assertFalse(testObj.isCliCommandSupported(normalizedNodeReferenceMock, "any"));
    }

    @Test
    public void testIsCertificateTypeSupported() {
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, "certType")).thenReturn(true);
        assertTrue(testObj.isCertificateTypeSupported(normalizedNodeReferenceMock, "certType"));
    }

    @Test
    public void testIsCertificateTypeSupported_Neg() {
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, "certType")).thenReturn(false);
        assertFalse(testObj.isCertificateTypeSupported(normalizedNodeReferenceMock, "certType"));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isNumberOfNodesAllowed(int)} .
     */
    @Test
    public void testIsNumberOfNodesAllowed() {
        assertTrue(testObj.isNumberOfNodesAllowed(MAX_NUM_WF_ALLOWED));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#isNumberOfNodesAllowed(int)} .
     */
    @Test(expected = MaxNodesExceededException.class)
    public void testIsNumberOfNodesAllowed_Neg() {
        testObj.isNumberOfNodesAllowed(MAX_NUM_WF_ALLOWED + 1);
    }

    @Test
    public void testValidateNodeForCrlCheckIPSEC() {

        MockObejct(IPSEC);
        when(moGetServiceFactoryMock.validateNodeForCrlCheckMO(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        assertTrue(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility#validateNodeForCrlCheck(NodeReference,String)} .
     */
    @Test
    public void testValidateNodeForCrlCheckOAM() {
        MockObejct(OAM);
        when(moGetServiceFactoryMock.validateNodeForCrlCheckMO(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        assertTrue(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));
    }

    private void MockObejct(final String certType) {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);

        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RadioNode").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupportedforCrlCheck(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupportedforCrlCheck(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, enableCrlCheckcommand)).thenReturn(true);

        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        synchronizationMock();

        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        final NodeModelInformation nmi = RADIO_NODE_NAME_MODEL_INFO;
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nmi);

        final Mo mo = Model.ME_CONTEXT.comManagedElement;
        when(nscsCapabilityModelServiceMock.getMirrorRootMo(eq(normalizedNodeReferenceMock))).thenReturn(mo);
        when(nodeUtilityMock.getTrustCategoryFdn(RADIO_NODE_ROOT_FDN, mo, certType, normalizedNodeReferenceMock)).thenReturn(
                RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN);
    }

    @Test(expected = NodeDoesNotExistException.class)
    public void testNodeDoesNotExistExceptionForCRLCheck() throws NodeDoesNotExistException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RBS").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();

        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));

    }

    @Test(expected = NetworkElementNotfoundException.class)
    public void testNetworkElementNotfoundExceptionForCRLCheck() throws NetworkElementNotfoundException {

        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);

        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RadioNode").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();

        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));

    }

    @Test(expected = NetworkElementNotfoundException.class)
    public void testisNodeExistsForCRLCheck() throws NetworkElementNotfoundException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));
        assertFalse(testObj.isNodeExists(nodeRef));

    }

    @Test(expected = NodeNotCertifiableException.class)
    public void testNodeNotCertifiableExceptionForCRLCheck() throws NodeNotCertifiableException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));
    }

    @Test(expected = UnsupportedCertificateTypeException.class)
    public void testUnsupportedCertificateTypeExceptionForCRLCheck() throws UnsupportedCertificateTypeException {
        final NodeModelInformation RADIO_NODE_MODEL_INFO = new NodeModelInformation("12A-5.1.63", ModelIdentifierType.MIM_VERSION, RADIO_NODE_NAME);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RadioNode").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();
        synchronizationMock();
        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, enableCrlCheckcommand)).thenReturn(true);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        final NodeModelInformation nmi = RADIO_NODE_MODEL_INFO;
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nmi);

        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, true));
    }

    @Test(expected = NodeNotSynchronizedException.class)
    public void testNodeNotSynchronizedExceptionForCRLCheck() throws NodeNotSynchronizedException {
        final NodeModelInformation RADIO_NODE_MODEL_INFO = new NodeModelInformation("16A-5.1.63", ModelIdentifierType.MIM_VERSION, RADIO_NODE_NAME);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RadioNode").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();

        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, OAM)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);

        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.PENDING.name());

        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);

        final NodeModelInformation nmi = RADIO_NODE_MODEL_INFO;
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nmi);

        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, true));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));
    }

    @Test(expected = UnsupportedNodeTypeException.class)
    public void testUnSupportedNodeReleaseVersionExceptionForCRLCheck() throws UnsupportedNodeTypeException {
        final NodeModelInformation RADIO_NODE_MODEL_INFO = new NodeModelInformation("16A-5.1.63", ModelIdentifierType.MIM_VERSION, RADIO_NODE_NAME);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RadioNode").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();

        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, enableCrlCheckcommand)).thenReturn(false);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupportedforCrlCheck(normalizedNodeReferenceMock, IPSEC)).thenReturn(false);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        synchronizationMock();

        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);

        final NodeModelInformation nmi = RADIO_NODE_MODEL_INFO;
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nmi);

        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, enableCrlCheckcommand)).thenReturn(false);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);

        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = TrustCategoryMODoesNotExistException.class)
    public void testTrustCategoryMODoesNotExistsExceptionForCRLCheck() throws TrustCategoryMODoesNotExistException {

        final NodeModelInformation RADIO_NODE_MODEL_INFO = new NodeModelInformation("17A-5.1.63", ModelIdentifierType.MIM_VERSION, RADIO_NODE_NAME);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RadioNode").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();

        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, enableCrlCheckcommand)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupported(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        when(nscsCapabilityModelServiceMock.isCertTypeSupportedforCrlCheck(normalizedNodeReferenceMock, IPSEC)).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        synchronizationMock();

        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);

        when(moGetServiceFactoryMock.validateNodeForCrlCheckMO(normalizedNodeReferenceMock, IPSEC)).thenThrow(
                TrustCategoryMODoesNotExistException.class);
        when(moGetServiceFactoryMock.validateNodeForCrlCheckMO(normalizedNodeReferenceMock, OAM)).thenThrow(
                TrustCategoryMODoesNotExistException.class);

        final NodeModelInformation nmi = RADIO_NODE_MODEL_INFO;
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nmi);

        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, IPSEC, false));
        assertFalse(testObj.validateNodeForCrlCheck(enableCrlCheckcommand, normalizedNodeReferenceMock, OAM, false));

    }

    public void mockDataForValidateNode() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn()))).thenReturn(true);
        doReturn(RADIO_NODE_NAME).when(normalizedNodeReferenceMock).getName();
        doReturn("RadioNode").when(normalizedNodeReferenceMock).getNeType();
        doReturn(RADIO_NODE_ROOT_FDN).when(normalizedNodeReferenceMock).getFdn();

        when(nscsCapabilityModelServiceMock.isCertificateManagementSupported(normalizedNodeReferenceMock)).thenReturn(true);

        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        synchronizationMock();
    }

    @Test(expected = UnsupportedNodeTypeException.class)
    public void testValidateNodeForOnDemandCrlDownload_UnsupportedNodeTypeException() {

        mockDataForValidateNode();
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, OnDemandCrlDownloadCommand)).thenReturn(false);
        assertFalse(testObj.validateNodeForOnDemandCrlDownload(OnDemandCrlDownloadCommand, normalizedNodeReferenceMock));
    }

    @Test(expected = UnsupportedNodeTypeException.class)
    public void testValidateNodeForOnDemandCrlDownload_UnSupportedNodeReleaseVersionException() {
        final NodeModelInformation RADIO_NODE_MODEL_INFO = new NodeModelInformation("16A-5.1.63", ModelIdentifierType.MIM_VERSION, RADIO_NODE_NAME);
        mockDataForValidateNode();
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, OnDemandCrlDownloadCommand)).thenReturn(false);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);

        final NodeModelInformation nmi = RADIO_NODE_MODEL_INFO;
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nmi);

        assertFalse(testObj.validateNodeForOnDemandCrlDownload(OnDemandCrlDownloadCommand, normalizedNodeReferenceMock));
    }

    @Test
    public void testValidateNodeForOnDemandCrlDownload() {
        final NodeModelInformation RADIO_NODE_MODEL_INFO = new NodeModelInformation("16B-5.1.63", ModelIdentifierType.MIM_VERSION, RADIO_NODE_NAME);
        mockDataForValidateNode();
        when(nscsCapabilityModelServiceMock.isCliCommandSupported(normalizedNodeReferenceMock, OnDemandCrlDownloadCommand)).thenReturn(true);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);

        final NodeModelInformation nmi = RADIO_NODE_MODEL_INFO;
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nmi);
        assertTrue(testObj.validateNodeForOnDemandCrlDownload(OnDemandCrlDownloadCommand, normalizedNodeReferenceMock));
    }

    @Test
    public void testValidateNodeForHttpsStatus() {
        fullHttpsStatusMock();
        testObj.validateNodeForHttpsStatus(nodeReferenceMock);
        verifyIfNodeValidationForHttpsPassed();
    }

    @Test(expected = NodeDoesNotExistException.class)
    public void testValidateNodeForHttpsStatusNodeNotExistsException() {
        when(readerServiceMock.exists(anyString())).thenReturn(false);
        testObj.validateNodeForHttpsStatus(nodeReferenceMock);
    }

    @Test(expected = InvalidNodeException.class)
    public void testValidateNodeForHttpsStatusInvalidNodeExceptionForNormalizedReference() {
        nodeExistsMock();
        when(readerServiceMock.getNormalizedNodeReference(any(NodeReference.class))).thenThrow(InvalidNodeException.class);
        testObj.validateNodeForHttpsStatus(nodeReferenceMock);
    }

    @Test(expected = InvalidNodeException.class)
    public void testValidateNodeForHttpsStatusNullNodeReference() {
        nodeExistsMock();
        when(readerServiceMock.getNormalizedNodeReference(any(NodeReference.class))).thenReturn(null);
        testObj.validateNodeForHttpsStatus(nodeReferenceMock);
    }

    @Test(expected = UnsupportedNodeTypeException.class)
    public void testValidateNodeForHttpsStatusUnsupportedCliCommand() {
        nodeExistsMock();
        getNormalizedReferenceMock();
        when(
                nscsCapabilityModelServiceMock.isCliCommandSupported(any(NormalizableNodeReference.class),
                        matches(NscsCapabilityModelService.HTTPS_COMMAND))).thenReturn(false);
        testObj.validateNodeForHttpsStatus(nodeReferenceMock);
    }

    @Test(expected = NodeNotSynchronizedException.class)
    public void testValidateNodeForHttpsStatusSynchronizedException() {
        nodeExistsMock();
        getNormalizedReferenceMock();
        httpsCliCommandSupportMock();
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(CmFunction.SyncStatusValue.UNSYNCHRONIZED.name());

        testObj.validateNodeForHttpsStatus(nodeReferenceMock);
    }

    @Test(expected = UnSupportedNodeReleaseVersionException.class)
    public void testValidateNodeForHttpsStatusNoHttpsStatusAttributeException() {
        nodeExistsMock();
        getNormalizedReferenceMock();
        httpsCliCommandSupportMock();
        synchronizationMock();
        when(
                moAttributeHandlerMock.getMOAttributeValue(anyString(), matches(Model.ME_CONTEXT.managedElement.systemFunctions.security.type()),
                        matches(Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace()), matches(ModelDefinition.Security.WEBSERVER)))
                .thenThrow(new CouldNotReadMoAttributeException(ModelDefinition.Security.WEBSERVER));

        testObj.validateNodeForHttpsStatus(nodeReferenceMock);
    }

    @Test
    public void testGetNodeNameFromEntity() {
        final Entity entity = new Entity();

        final EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName("LTE05XXXX08-oam");
        entity.setEntityInfo(entityInfo);
        String nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertTrue(nodeName.isEmpty()); // It is not an entity associated to a node (entityCategory missing)

        final EntityCategory entCat = new EntityCategory();
        entCat.setName("NODE-OAM");
        entCat.setId(1);
        entCat.setModifiable(false);
        entity.setCategory(entCat);
        entityInfo.setName("LTE05XXXX08-oam");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertEquals("LTE05XXXX08", nodeName);

        entityInfo.setName("-oam-LTE05XXXX08-oam");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertEquals("-oam-LTE05XXXX08", nodeName);

        entityInfo.setName("XXX-LTE05XXXX08-oam");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertEquals("XXX-LTE05XXXX08", nodeName);

        entityInfo.setName("LTE05XXXX08-OAM");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertTrue(nodeName.isEmpty()); // It is not an entity name associated to a node

        entityInfo.setName("LTE05XXXX08-oam-oam");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertEquals("LTE05XXXX08-oam", nodeName);

        entityInfo.setName("LTE05XXXX08-oam-ipsec");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertTrue(nodeName.isEmpty()); // It is not an entity name associated to a node for oam entity category

        entityInfo.setName("LTE05XXXX08-oam_old");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertTrue(nodeName.isEmpty());// It is not an entity name associated to a node

        entityInfo.setName("LTE05XXXX08-oam-aaa");
        entity.setEntityInfo(entityInfo);
        nodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
        assertTrue(nodeName.isEmpty());// It is not an entity name associated to a node
    }

    @Test(expected = NodeDoesNotExistException.class)
    public void testValidateNodeForFtpesNodeDoesNotExistsException() {
        when(readerServiceMock.exists(anyString())).thenReturn(false);
        testObj.validateNodeForFtpes(nodeReferenceMock);
    }

    @Test(expected = InvalidNodeException.class)
    public void testValidateNodeForFtpesInvalidNodeExceptionForNormalizedReference() {
        nodeExistsMock();
        when(readerServiceMock.getNormalizedNodeReference(any(NodeReference.class))).thenThrow(InvalidNodeException.class);
        testObj.validateNodeForFtpes(nodeReferenceMock);
    }

    @Test(expected = InvalidNodeException.class)
    public void testValidateNodeForFtpesNullNodeReference() {
        nodeExistsMock();
        when(readerServiceMock.getNormalizedNodeReference(any(NodeReference.class))).thenReturn(null);
        testObj.validateNodeForFtpes(nodeReferenceMock);
    }

    @Test(expected = UnsupportedNodeTypeException.class)
    public void testValidateNodeForFtpesUnsupportedCliCommand() {
        nodeExistsMock();
        getNormalizedReferenceMock();
        when(
                nscsCapabilityModelServiceMock.isCliCommandSupported(any(NormalizableNodeReference.class),
                        matches(NscsCapabilityModelService.FTPES_COMMAND))).thenReturn(false);
        testObj.validateNodeForFtpes(nodeReferenceMock);
    }

    @Test(expected = NodeNotSynchronizedException.class)
    public void testValidateNodeForFtpesNotSynchronizedException() {
        nodeExistsMock();
        getNormalizedReferenceMock();
        when(
                nscsCapabilityModelServiceMock.isCliCommandSupported(any(NormalizableNodeReference.class),
                        matches(NscsCapabilityModelService.FTPES_COMMAND))).thenReturn(true);
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class), matches(Model.NETWORK_ELEMENT.cmFunction.type()),
                        matches(Model.NETWORK_ELEMENT.cmFunction.namespace()), matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS))).thenReturn(CmFunction.SyncStatusValue.UNSYNCHRONIZED.name());

        testObj.validateNodeForFtpes(nodeReferenceMock);
    }

    @Test
    public void testValidateXmlForNode() {
        Node inputNode = new Node(nodeRef.getFdn());
        inputNode.setSubjectAltNameType("IP_ADDRESS");
        inputNode.setSubjectAltName("12.12.12.12");
        testObj.validateXmlForNode(inputNode, "certType");
    }

    @Test
    public void testValidateSanIpAddressValidIP() {
        testObj.validateSanIpAddress("12.12.12.12");
    }

    @Test(expected = InvalidSubjAltNameXmlException.class)
    public void testValidateSanIpAddressInvalidIP() {
        testObj.validateSanIpAddress("12.12.");
    }

    private void nodeExistsMock() {
        when(readerServiceMock.exists(anyString())).thenReturn(true);
    }

    private void getNormalizedReferenceMock() {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
    }

    private void httpsCliCommandSupportMock() {
        when(
                nscsCapabilityModelServiceMock.isCliCommandSupported(any(NormalizableNodeReference.class),
                        matches(NscsCapabilityModelService.HTTPS_COMMAND))).thenReturn(true);
    }

    private void httpsStatusAttributeMock() {
        when(
                readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class),
                        matches(Model.ME_CONTEXT.managedElement.systemFunctions.security.type()),
                        matches(Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace()), matches(ModelDefinition.Security.WEBSERVER)))
                .thenReturn(cmResponseMock);
    }

    private void verifyIfNodeValidationForHttpsPassed() {
        verify(readerServiceMock, times(1)).exists(normalizedNodeReferenceMock.getFdn());
        verify(moAttributeHandlerMock, times(1)).getMOAttributeValue(normalizedNodeReferenceMock.getFdn(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), ModelDefinition.Security.WEBSERVER);
    }

    private void fullHttpsStatusMock() {
        nodeExistsMock();
        getNormalizedReferenceMock();
        httpsCliCommandSupportMock();
        synchronizationMock();
        httpsStatusAttributeMock();
    }
}
