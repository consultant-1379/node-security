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
package com.ericsson.nms.security.nscs.node.certificate.validator;

import static com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes.*;
import static com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility.ACCEPTED_ARGUMENTS_ARE;
import static com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility.ACCEPTED_KEY_ALGORITHMS_ARE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.CertificateProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

@RunWith(MockitoJUnitRunner.class)
public class CertificateIssueValidatorTest {

    private static final String IPSEC = "IPSEC";

    @InjectMocks
    CertificateIssueValidator testObj;

    @Mock
    NscsLogger logger;

    @Mock
    private NscsCMReaderService readerServiceMock;

    @Mock
    private NormalizableNodeReference normalizedNodeReferenceMock;

    @Mock
    private NscsPkiEntitiesManagerIF nscsPkiManagerMock;

    @Mock
    private CertificateProfile certificateProfileMock;

    @Mock
    private NodeModelInformation nodeModelInformationMock;

    @Mock
    private EntityProfile entityProfileMock;

    @Mock
    private NscsCommandManager mockCommandManager;

    @Mock
    private CertificateEnrollmentValidator certEnrollmentValidatorMock;

    @Mock
    private NodeValidatorUtility nodeValidatorUtility;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    private final String nodeName = "Node123";
    private final String OAM = "OAM";
    private final NodeReference nodeRef = new NodeRef(nodeName);
    private final Node inputNode = new Node(nodeRef.getFdn());
    private List<Node> inputNodesList = new LinkedList<Node>();
    private Map<NodeReference, NscsServiceException> invalidNodeErrors = new HashMap<NodeReference, NscsServiceException>();
    final Map<String, String[]> invalidNodeDynamicErrors = new HashMap<String, String[]>();
    final List<String> enrollmentValuesErrorMsg = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        final String validEp = "Valid_EP";
        final String unsupportedKeySize = "RSA_1024";
        inputNode.setEntityProfileName(validEp);
        inputNode.setKeySize(unsupportedKeySize);
        inputNodesList.add(inputNode);
        final List<Algorithm> algorithms = new ArrayList<>();
        final Algorithm algorithm = new Algorithm();
        algorithm.setName("RSA");
        algorithm.setKeySize(2048);
        algorithms.add(algorithm);
        certificateProfileMock = new CertificateProfile();
        certificateProfileMock.setKeyGenerationAlgorithms(algorithms);
        entityProfileMock = new EntityProfile();
        entityProfileMock.setCertificateProfile(certificateProfileMock);
    }

    @Test
    public void testValidateIssue_ValidEntityProfile_UnsupportedKeySize() throws NscsPkiEntitiesManagerException {
        when(nscsPkiManagerMock.isEntityProfileNameAvailable(inputNode.getEntityProfileName())).thenReturn(false);
        when(nscsPkiManagerMock.getEntityProfile(inputNode.getEntityProfileName())).thenReturn(entityProfileMock);
        when(mockCommandManager.isEnrollmentModeSupportedForNodeList(Mockito.anyList(), Mockito.eq(enrollmentValuesErrorMsg))).thenReturn(true);
        try {
            testObj.validate(inputNodesList, OAM, inputNodesList, invalidNodeErrors, false);
        } catch (final Exception e) {
            assertEquals(REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE + " : " + "The given Key Algorithm [" + inputNode.getKeySize() + "] is not in supported list of Entity Profile ["
                    + inputNode.getEntityProfileName() + "]. " + ACCEPTED_KEY_ALGORITHMS_ARE + "[RSA_2048]", e.getMessage());

        }
    }

    @Test
    public void testValidateIssue_EmptyEntityProfile_ExistingEntity_UnsupportedKeySize() throws NscsPkiEntitiesManagerException {
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        final Entity entity = new Entity();
        entity.setEntityProfile(entityProfileMock);
        when(nscsPkiManagerMock.getPkiEntity(anyString())).thenReturn(entity);
        when(mockCommandManager.isEnrollmentModeSupportedForNodeList(Mockito.anyList(), Mockito.eq(enrollmentValuesErrorMsg))).thenReturn(true);
        try {
            testObj.validate(inputNodesList, OAM, inputNodesList, invalidNodeErrors, true);
        } catch (final Exception e) {
            assertEquals(REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE + " : " + "The given Key Algorithm [" + inputNode.getKeySize() + "] is not in supported list of Entity Profile ["
                    + inputNode.getEntityProfileName() + "]. " + ACCEPTED_KEY_ALGORITHMS_ARE + "[RSA_2048]", e.getMessage());
        }
    }

    @Test
    public void testValidateIssue_EmptyEntityProfile_NotExistingEntity_UnsupportedKeySize() throws NscsPkiEntitiesManagerException {
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(nscsPkiManagerMock.isEntityProfileNameAvailable("Default_EP")).thenReturn(false);
        when(nscsPkiManagerMock.getEntityProfile(anyString())).thenReturn(entityProfileMock);
        when(mockCommandManager.isEnrollmentModeSupportedForNodeList(Mockito.anyList(), Mockito.eq(enrollmentValuesErrorMsg))).thenReturn(true);
        try {
            testObj.validate(inputNodesList, OAM, inputNodesList, invalidNodeErrors, true);
        } catch (final Exception e) {
            assertEquals(REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE + " : " + "The given Key Algorithm [" + inputNode.getKeySize() + "] is not in supported list of Entity Profile ["
                    + inputNode.getEntityProfileName() + "]. " + ACCEPTED_KEY_ALGORITHMS_ARE + "[RSA_2048]", e.getMessage());
        }
    }

    @Test
    public void testValidateIssue_NotExistingEntity_EmptySubjAltName_and_Type() throws NscsPkiEntitiesManagerException {
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(mockCommandManager.isEnrollmentModeSupportedForNodeList(Mockito.anyList(), Mockito.eq(enrollmentValuesErrorMsg))).thenReturn(true);
        try {
            testObj.validate(inputNodesList, OAM, inputNodesList, invalidNodeErrors, true);
        } catch (final Exception e) {
            assertEquals(SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY, e.getMessage());
        }
    }

    @Test
    public void testValidateIssue_ExistingEntity_UnsupportedSubjAltNameType() throws NscsPkiEntitiesManagerException {
        inputNode.setSubjectAltNameType("X400_ADDRESS");
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        when(mockCommandManager.isEnrollmentModeSupportedForNodeList(Mockito.anyList(), Mockito.eq(enrollmentValuesErrorMsg))).thenReturn(true);
        try {
            testObj.validate(inputNodesList, IPSEC, inputNodesList, invalidNodeErrors, true);
        } catch (final Exception e) {
            final List<String> supportedSubjectAltNameFieldType = new ArrayList<>();
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.IP_ADDRESS.name());
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.DNS_NAME.name());
            assertEquals(REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED + " : " + ACCEPTED_ARGUMENTS_ARE + supportedSubjectAltNameFieldType,
                    e.getMessage());
        }
    }

    @Test
    public void testValidateIssue_ExistingEntity_UnsupportedSubjAltName() throws NscsPkiEntitiesManagerException {
        inputNode.setSubjectAltNameType("IP_ADDRESS");
        inputNode.setSubjectAltName("300.168.0.42");
        when(nscsPkiManagerMock.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(readerServiceMock.getNodeModelInformation(anyString())).thenReturn(nodeModelInformationMock);
        when(mockCommandManager.isEnrollmentModeSupportedForNodeList(Mockito.anyList(), Mockito.eq(enrollmentValuesErrorMsg))).thenReturn(true);
        try {
            testObj.validate(inputNodesList, IPSEC, inputNodesList, invalidNodeErrors, true);
        } catch (final Exception e) {
            final List<String> supportedSubjectAltNameFieldType = new ArrayList<>();
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.IP_ADDRESS.name());
            supportedSubjectAltNameFieldType.add(SubjectAltNameFieldType.DNS_NAME.name());
            assertEquals(REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID + PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT, e.getMessage());
        }
    }

}