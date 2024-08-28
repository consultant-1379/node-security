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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckNodeCredentialTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

@RunWith(MockitoJUnitRunner.class)
public class ComEcimCheckNodeCredentialTaskHandlerTest {

    @InjectMocks
    private ComEcimCheckNodeCredentialTaskHandler comEcimCheckNodeCredentialTaskHandler;

    @Mock
    private NscsLogger nscslogger;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private NormalizableNodeReference mockNormalizableNodeReference;

    @Mock
    private NscsCapabilityModelService capabilityService;

    @Mock
    private NSCSComEcimNodeUtility comEcimNodeUtility;

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @Mock
    private ComEcimMoNaming comEcimMoNaming;

    @Mock
    private MoObject mockMoObject;

    @Mock
    private CppSecurityService securityService;

    @Mock
    private NscsCMWriterService writerService;

    @Mock
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Mock
    private NscsCMWriterService.WriterSpecificationBuilder trustedCertificateSpec;

    @Mock
    private NodeValidatorUtility nodeValidatorUtility;

    private static final Mo MO = Model.ME_CONTEXT.comManagedElement;
    private static final Mo CERT_MO = ((ComEcimManagedElement) MO).systemFunctions.secM.certM;
    private static final String NODE123 = "node123";
    private static final NodeReference NODE = new NodeRef(NODE123);
    private static final String PICO_NODE_NAME = "PICO-123";
    private static final String PICO_NODE_ROOT_FDN = String.format("MeContext=%s,ManagedElement=%s", PICO_NODE_NAME, PICO_NODE_NAME);
    private static final String PICO_NODE_CERTM = String.format("%s,SystemFunctions=1,SecM=1,CertM=1", PICO_NODE_ROOT_FDN);
    private static final String IPSEC_ENTITY_PROFILE_NAME = "MicroRBSIPSec_SAN_CHAIN_EP";
    private static final AlgorithmKeys ALGOKEYS = AlgorithmKeys.RSA_2048;
    private static final String RADIO_NODE_ENROLLMENT_URI = "https://localhost:8443/app/resource";
    private static final String ACTIVATED = "ACTIVATED";
    private static final String EMPTY_STRING = "";
    private static final String ISCF_TEST_FINGERPRINT = "SHA-1 Fingerprint=SO:ME:FI:NG:ER:PR:IN:TT";
    private static final String NODE_TYPE = "ERBS";
    private static final String MODEL_IDENTIFIER = null;
    private static final String SCHEMA = "schema";
    private static final String NAME_SPACE = "namespace";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String ENROLLMENT_MODE = "CMPv2_INITIAL";
    private static final String ALGORITHM_KEYS = "RSA_4096";
    private static ComEcimCheckNodeCredentialTask comEcimCheckNodeCredentialTask = new ComEcimCheckNodeCredentialTask();
    private static Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
    private static NscsModelInfo nscsModelInfo = new NscsModelInfo(SCHEMA, NAME_SPACE, NAME, VERSION);
    private static Map<String, NscsModelInfo> nscsModelInfos = new HashMap<String, NscsModelInfo>();
    private static NodeModelInformation NodeModelInformation = new NodeModelInformation(MODEL_IDENTIFIER, ModelIdentifierType.MIM_VERSION, NODE_TYPE);
    private static List<String> reservedByUser = new ArrayList<>();
    private static CmResponse cmResponse = null;
    private static final String EXTERNAL_CA = "EXTERNAL_CA";

    private static ScepEnrollmentInfo enrollmentInfo;

    @Before
    public void setUp() {

        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), ENROLLMENT_MODE);
        outputParams.put(WorkflowOutputParameterKeys.ALGORITHM_KEYS.toString(), ALGORITHM_KEYS);
        comEcimCheckNodeCredentialTask.setTrustedCertCategory(TrustedCertCategory.CORBA_PEERS.toString());
        comEcimCheckNodeCredentialTask.setNode(NODE);
        comEcimCheckNodeCredentialTask.setIsReissue("true");
        comEcimCheckNodeCredentialTask.setEnrollmentMode(ENROLLMENT_MODE);
        comEcimCheckNodeCredentialTask.setOutputParams(outputParams);
        nscsModelInfos.put("NodeCredential", nscsModelInfo);
        nscsModelInfos.put("EnrollmentAuthority", nscsModelInfo);
        nscsModelInfos.put("EnrollmentServerGroup", nscsModelInfo);
        nscsModelInfos.put("EnrollmentServer", nscsModelInfo);
        final Entity entity = createEntity(NODE.getFdn());
        try {
            enrollmentInfo = new ScepEnrollmentInfoImpl(entity, RADIO_NODE_ENROLLMENT_URI, null, DigestAlgorithm.MD5, 10, "challengePWD", "2048",
                    EnrollmentMode.CMPv2_VC, null, null);
        } catch (final CertificateEncodingException | NoSuchAlgorithmException e) {
        }
        enrollmentInfo.setPkiRootCertFingerPrint(ISCF_TEST_FINGERPRINT.getBytes());
        reservedByUser.add(null);
        cmResponse = buildCmResponse(NODE123, IpSec.FEATURE_STATE, ACTIVATED);
        Mockito.when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizableNodeReference);
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("BSC");
        Mockito.when(mockNormalizableNodeReference.getFdn()).thenReturn(NODE.getFdn());
        Mockito.when(capabilityService.getMirrorRootMo(Mockito.any(NormalizableNodeReference.class))).thenReturn(MO);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(NODE.getFdn(), CERT_MO)).thenReturn(PICO_NODE_CERTM);
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.eq(mockNormalizableNodeReference))).thenReturn(NODE.getFdn());
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("HLR-FE");
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("vHLR-FE");
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("HLR-FE-BSP");
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("HLR-FE-IS");

    }

    @Test
    public void testProcessTask_CreateEnrollmentAuthority() throws CppSecurityServiceException {

        comEcimCheckNodeCredentialTask.setIsReissue("false");
        final ScepEnrollmentInfoImpl enrollInfo = (ScepEnrollmentInfoImpl) (enrollmentInfo);
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment info";
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.any(NormalizableNodeReference.class))).thenReturn(EMPTY_STRING);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(null);
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        comEcimCheckNodeCredentialTask.setOutputParams(outputParams);
        Mockito.when(writerService.createMo(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyMap())).thenReturn(null);
        final String result = comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        Assert.assertNotNull(result);

    }

    @Test
    public void testProcessTask_CheckAndUpdateEnrollmentAuthority() throws CppSecurityServiceException {

        comEcimCheckNodeCredentialTask.setIsReissue("false");
        final ScepEnrollmentInfoImpl enrollInfo = (ScepEnrollmentInfoImpl) (enrollmentInfo);
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment info";
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        outputParams.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), EXTERNAL_CA);

        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.SUBJECT_NAME)).thenReturn(enrollmentInfo.getDistinguishedName());
        Mockito.when(mockMoObject.getAttribute(NodeCredential.SUBJECT_ALT_NAME)).thenReturn(NODE123);

        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        comEcimCheckNodeCredentialTask.setOutputParams(outputParams);
        Mockito.when(writerService.withSpecification()).thenReturn(trustedCertificateSpec);
        Mockito.when(trustedCertificateSpec.setNotNullAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(trustedCertificateSpec);
        final String result = comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        Assert.assertNotNull(result);

    }

    @Test
    public void testProcessTask_CheckEnrollmentServer() throws CppSecurityServiceException {

        comEcimCheckNodeCredentialTask.setIsReissue("false");
        final ScepEnrollmentInfoImpl enrollInfo = (ScepEnrollmentInfoImpl) (enrollmentInfo);
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment info";
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.SUBJECT_NAME)).thenReturn("CN=NetworkElement=node1234");
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        comEcimCheckNodeCredentialTask.setOutputParams(outputParams);
        Mockito.when(writerService.withSpecification()).thenReturn(trustedCertificateSpec);
        Mockito.when(trustedCertificateSpec.setNotNullAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(trustedCertificateSpec);
        Mockito.when(readerService.getMos(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(cmResponse);
        final String result = comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        Assert.assertNotNull(result);

    }

    @Test
    public void testProcessTask_NodeCredentialReservedByUser() throws CppSecurityServiceException {

        comEcimCheckNodeCredentialTask.setIsReissue("false");
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.SUBJECT_NAME)).thenReturn("CN=NetworkElement=node1234");
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        Mockito.when(securityService.generateEnrollmentInfo(Mockito.any(EnrollingInformation.class))).thenReturn(enrollmentInfo);
        Mockito.when(writerService.withSpecification()).thenReturn(trustedCertificateSpec);
        Mockito.when(trustedCertificateSpec.setNotNullAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(trustedCertificateSpec);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.RESERVED_BY_USER)).thenReturn(reservedByUser);
        final String result = comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        Assert.assertNotNull(result);

    }

    @Test
    public void testProcessTask_NodeCredentialReservedByUserNull() throws CppSecurityServiceException {

        comEcimCheckNodeCredentialTask.setIsReissue("false");
        final ScepEnrollmentInfoImpl enrollInfo = (ScepEnrollmentInfoImpl) (enrollmentInfo);
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment info";
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        comEcimCheckNodeCredentialTask.setOutputParams(outputParams);
        Mockito.when(nscsNodeUtility.getEnrollmentMode(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(EnrollmentMode.CMPv2_VC);
        Mockito.when(nscsNodeUtility.getAlgorithmKeys(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(AlgorithmKeys.RSA_4096);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.SUBJECT_NAME)).thenReturn("CN=NetworkElement=node1234");
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        Mockito.when(writerService.withSpecification()).thenReturn(trustedCertificateSpec);
        Mockito.when(trustedCertificateSpec.setNotNullAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(trustedCertificateSpec);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.RESERVED_BY_USER)).thenReturn(null);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.ENROLLMENT_SERVER_GROUP)).thenReturn(NodeCredential.ENROLLMENT_SERVER_GROUP);
        final String result = comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        Assert.assertNotNull(result);

    }

    @Test
    public void testProcessTask_NodeCredentialReservedByUser_WithParameterKeysNull() throws CppSecurityServiceException {

        comEcimCheckNodeCredentialTask.setIsReissue("false");
        final ScepEnrollmentInfoImpl enrollInfo = (ScepEnrollmentInfoImpl) (enrollmentInfo);
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment info";
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), null);
        outputParams.put(WorkflowOutputParameterKeys.ALGORITHM_KEYS.toString(), null);
        outputParams.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(),"EXTERNAL_CA");

        comEcimCheckNodeCredentialTask.setOutputParams(outputParams);
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.any(NormalizableNodeReference.class))).thenReturn(null);
        Mockito.when(nscsNodeUtility.getEnrollmentMode(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(EnrollmentMode.CMPv2_VC);
        Mockito.when(nscsNodeUtility.getAlgorithmKeys(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(AlgorithmKeys.RSA_4096);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(null);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.SUBJECT_NAME)).thenReturn("CN=NetworkElement=node1234");
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        Mockito.when(writerService.withSpecification()).thenReturn(trustedCertificateSpec);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.ENROLLMENT_AUTHORITY)).thenReturn("enrollmentAuthorityFdn");
        Mockito.when(trustedCertificateSpec.setNotNullAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(trustedCertificateSpec);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.RESERVED_BY_USER)).thenReturn(new ArrayList<String>());
        final String result = comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        Assert.assertNotNull(result);

    }

    @Test
    public void testProcessTask_NodeCredentialReservedByUserNull_WithParameterKeysEmpty() throws CppSecurityServiceException {

        comEcimCheckNodeCredentialTask.setIsReissue("false");
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), "");
        outputParams.put(WorkflowOutputParameterKeys.ALGORITHM_KEYS.toString(), "");
        comEcimCheckNodeCredentialTask.setTrustedCertCategory(TrustedCertCategory.IPSEC.toString());
        comEcimCheckNodeCredentialTask.setOutputParams(outputParams);
        Mockito.when(nscsNodeUtility.getEnrollmentMode(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(EnrollmentMode.CMPv2_VC);
        Mockito.when(nscsNodeUtility.getAlgorithmKeys(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(AlgorithmKeys.RSA_4096);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.SUBJECT_NAME)).thenReturn("CN=NetworkElement=node1234");
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        Mockito.when(securityService.generateEnrollmentInfo(Mockito.any(EnrollingInformation.class))).thenReturn(enrollmentInfo);
        Mockito.when(writerService.withSpecification()).thenReturn(trustedCertificateSpec);
        Mockito.when(trustedCertificateSpec.setNotNullAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(trustedCertificateSpec);
        Mockito.when(mockMoObject.getAttribute(NodeCredential.RESERVED_BY_USER)).thenReturn(null);
        final String result = comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        Assert.assertNotNull(result);

    }

    @Test
    public void testProcessTask_MissingMoException_certMFdnNull() {

        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(NODE.getFdn(), CERT_MO)).thenReturn(null);
        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(MissingMoException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_MissingMoException_certMFdnEmpty() {

        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(NODE.getFdn(), CERT_MO)).thenReturn(EMPTY_STRING);
        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(MissingMoException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_IllegalArgumentException() throws CppSecurityServiceException {

        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenThrow(IllegalArgumentException.class);
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(UnexpectedErrorException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_NscsModelServiceException() throws CppSecurityServiceException {

        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenThrow(NscsModelServiceException.class);
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(UnexpectedErrorException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_NscsModelInfosNull() throws CppSecurityServiceException {

        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(null);
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(UnexpectedErrorException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_MissingMoException_NodeCredentialMoObjNull() {
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(null);
        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(MissingMoException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_MissingMoException_NodeCredentialFdnNull() {
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.any(NormalizableNodeReference.class))).thenReturn(null);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn("Defaultname");
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);

        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(MissingMoException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_MissingMoException_NodeCredentialFdnEmpty() {
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.any(NormalizableNodeReference.class))).thenReturn(EMPTY_STRING);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn("Defaultname");
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(null);

        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(MissingMoException.class, e.getClass());
        }

    }

    @Test
    public void testProcessTask_MissingMoException_Exception() throws CppSecurityServiceException {
        comEcimCheckNodeCredentialTask.setIsReissue("false");
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.any(NormalizableNodeReference.class))).thenReturn(EMPTY_STRING);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(nscsModelInfos);
        Mockito.when(comEcimMoNaming.getDefaultName(Mockito.anyString(), Mockito.anyString(), Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(NODE123);
        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(null);
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        Mockito.when(securityService.generateEnrollmentInfo(Mockito.any(EnrollingInformation.class))).thenReturn(enrollmentInfo);
        try {
            comEcimCheckNodeCredentialTaskHandler.processTask(comEcimCheckNodeCredentialTask);
        } catch (final Exception e) {
            Assert.assertEquals(UnexpectedErrorException.class, e.getClass());
        }

    }

    private Entity createEntity(final String fdn) {

        final EntityInfo entityInfo = new EntityInfo();
        final CertificateAuthority certificateAuthority = new CertificateAuthority();
        certificateAuthority.setName("NE_OAM_CA");
        certificateAuthority.setIssuer(certificateAuthority);
        entityInfo.setName(fdn);
        entityInfo.setId(1);

        final SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
        subjectAltNameField.setType(SubjectAltNameFieldType.IP_ADDRESS);

        final SubjectAltNameString subjectAltNameValueString = new SubjectAltNameString();
        subjectAltNameValueString.setValue("12.13.14.15");
        subjectAltNameField.setValue(subjectAltNameValueString);
        final List<SubjectAltNameField> subjectAltNameValueList = new ArrayList<>();
        subjectAltNameValueList.add(subjectAltNameField);

        final SubjectAltName subjectAltNameValues = new SubjectAltName();
        subjectAltNameValues.setSubjectAltNameFields(subjectAltNameValueList);

        final Subject subject = new Subject();

        final SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue(fdn);

        final List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);
        subject.setSubjectFields(entSubjectFieldList);

        entityInfo.setSubject(subject);
        entityInfo.setSubjectAltName(subjectAltNameValues);
        entityInfo.setOTP("OTP");

        final Entity ee = new Entity();
        ee.setType(EntityType.ENTITY);
        final EntityProfile ep = new EntityProfile();
        ep.setActive(true);
        ep.setName(IPSEC_ENTITY_PROFILE_NAME);
        ee.setEntityProfile(ep);
        entityInfo.setStatus(EntityStatus.NEW);
        ee.setEntityInfo(entityInfo);
        entityInfo.setIssuer(certificateAuthority.getIssuer());

        final Algorithm keyGenerationAlgorithm = new Algorithm();
        keyGenerationAlgorithm.setKeySize((ALGOKEYS.getKeySize()));
        ee.setKeyGenerationAlgorithm(keyGenerationAlgorithm);

        return ee;

    }

    private CmResponse buildCmResponse(final String nodeName, final String attribute, final Object expectedValue) {

        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(attribute, expectedValue);
        attributesMap.put(EnrollmentServer.PROTOCOL, "CMP");
        attributesMap.put(EnrollmentServer.URI, "uri");

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + nodeName);
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        cmResponse.setTargetedCmObjects(cmObjects);
        return cmResponse;

    }
}
