package com.ericsson.nms.security.nscs.cpp.service;

/**
 *
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.CACertSftpPublisher;
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.EnrollmentPartialInfos;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiCertificateManager;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsAddressRequest;
import com.ericsson.oss.itpf.smrs.SmrsService;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Tests the Implementation of the CppSecurityService interface.
 *
 * @see com.ericsson.nms.security.nscs.cpp.service.CppSecurityService
 *
 * @author eabdsin
 */
@RunWith(MockitoJUnitRunner.class)
public class CppSecurityServiceBeanTest {

    private static final String NODE_FDN = "node123";
    private static final String URL = "http://192.168.33.27:8080/cgi-bin/pkiclient.exe";
    private static final String DN = "DN";
    private static final String IPSEC_ENTITY_PROFILE_NAME = "MicroRBSIPSec_SAN_CHAIN_EP";
    private static final NodeReference NODE = new NodeRef(NODE_FDN);
    private static final NormalizableNodeReference normRef = MockUtils.createNormalizableNodeRef(NODE.getName());
    private static final boolean isNetworkElementSecurityExisting = true;
    private static final boolean isNscsPkiEntityExisting = true;

    private static final NodeModelInformation CPP_13B_MODEL_INFO = new NodeModelInformation("4.1.189", ModelIdentifierType.MIM_VERSION, "ERBS");
    private static final NodeModelInformation CPP_15B_MODEL_INFO = new NodeModelInformation("5.1.63", ModelIdentifierType.MIM_VERSION, "ERBS");
    private static final NodeModelInformation CPP_15B_CADN_THRESHOLD_MODEL_INFO = new NodeModelInformation("5.1.200", ModelIdentifierType.MIM_VERSION,
            "ERBS");
    private static final NodeModelInformation CPP_15B_CADN_SUPPORTED_MODEL_INFO = new NodeModelInformation("5.1.239", ModelIdentifierType.MIM_VERSION,
            "ERBS");
    private static final NodeModelInformation CPP_16B_MODEL_INFO = new NodeModelInformation("6.1.000", ModelIdentifierType.MIM_VERSION, "ERBS");
    private static final AlgorithmKeys ALGOKEYS = AlgorithmKeys.RSA_2048;
    private static final NodeModelInformation VDU_MODEL_INFO = new NodeModelInformation("1.0", ModelIdentifierType.MIM_VERSION, "vDU");
    private static final String PKI_SERIAL_NUMBER = "2190ed9b096fb2cb";
    private static final BigInteger NSCS_SERIAL_NUMBER = CertDetails.convertHexadecimalSerialNumberToDecimalFormat(PKI_SERIAL_NUMBER);
    private static final SmrsAccount smrsAccount = new SmrsAccount("mm-cert-user", "/home/smrs");

    private static final String DG2_OAM_ENTITY_PROFILE_NAME = "DUSGen2OAM_CHAIN_EP";
    private static final String OAM_CA_NAME = "ENM_OAM_CA";
    private static final String INFRA_CA_NAME = "ENM_Infrastructure_CA";
    private static final String ROOT_CA_NAME = "ENM_PKI_Root_CA";
    private static final String INTERMEDIATE_EXT_CA_NAME = "PrimeTowerIntermediateCA";
    private static final String ROOT_EXT_CA_NAME = "PrimeTowerRootCA";

    public static final String MONAMESPACE = "RNC_NODE_MODEL";
    public static final String MOTYPE = "RncFunction";
    public static final String RNCTYPE = "rncType";

    @Mock
    Logger log;

    @Mock
    CppSecurityService cppService;

    @Mock
    SystemRecorder systemRecorder;

    @InjectMocks
    private CppSecurityServiceBean beanUnderTest;

    @Mock
    CACertSftpPublisher sftpPublisher;

    @Mock
    SmrsService smrsService;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    NscsCMWriterService writer;

    @Mock
    MoObject eNodeBFunction;

    @Mock
    NscsPkiEntitiesManagerIF nscsPkiManager;

    @Mock
    NscsPkiCertificateManager nscsPkiCertificateManager;

    @Mock
    NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    NormalizableNodeReference normalizableNodeReference;

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws NscsPkiEntitiesManagerException {
        doReturn(CPP_15B_MODEL_INFO.getModelIdentifier()).when(eNodeBFunction).getVersion();
        doReturn(CPP_15B_MODEL_INFO).when(readerService).getNodeModelInformation(NODE_FDN);
        doReturn(eNodeBFunction).when(readerService).getMoObjectByFdn(anyString());
        doReturn(normRef).when(readerService).getNormalizableNodeReference(NODE);
        doReturn(isNetworkElementSecurityExisting).when(readerService).exists(anyString());
        doReturn("").when(readerService).getModelVersion(anyString(), anyString());
        smrsAccount.setPassword("secret");

        setUpData_NetworkElementSecurity_enrollmentmode(NODE.getName());

        final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder = org.mockito.Mockito
                .mock(NscsCMWriterService.WriterSpecificationBuilder.class);
        when(specificationBuilder.setNotNullAttribute(any(String.class), any(Object.class))).thenReturn(specificationBuilder);
        when(specificationBuilder.setFdn(any(String.class))).thenReturn(specificationBuilder);
        when(writer.withSpecification()).thenReturn(specificationBuilder);
        setUpData_NscsPkiManager_Entity(NODE.getFdn(), isNscsPkiEntityExisting);
    }

    @SuppressWarnings("unchecked")
    private void setUpData_NscsPkiManager_Entity(final String fdn, final boolean isNscsPkiEntityExisting) throws NscsPkiEntitiesManagerException {
        if (isNscsPkiEntityExisting) {
            final Entity entity = createEntity(fdn);
            when(nscsPkiManager.isEntityNameAvailable(anyString(), any(EntityType.class))).thenReturn(false);
            when(nscsPkiManager.getPkiEntity(anyString())).thenReturn(entity);
        } else {
            when(nscsPkiManager.isEntityNameAvailable(anyString(), any(EntityType.class))).thenReturn(true);
            when(nscsPkiManager.getPkiEntity(anyString())).thenReturn(null).thenThrow(EntityNotFoundException.class);
        }
    }

    private void setUpData_NetworkElementSecurity_enrollmentmode(final String nodeName) {
        CmResponse cmResponse_enrollmentMode = null;
        cmResponse_enrollmentMode = buildCmResponse(nodeName, NetworkElementSecurity.ENROLLMENT_MODE, EnrollmentMode.SCEP.toString());
        mockCmReaderService_enrollmentMode(cmResponse_enrollmentMode);
    }

    private void mockCmReaderService_enrollmentMode(final CmResponse enrollmentModeCmResponse) {

        Mockito.when(readerService.getMOAttribute(any(String.class), Mockito.eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type()),
                Mockito.eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace()),
                Mockito.eq(NetworkElementSecurity.ENROLLMENT_MODE))).thenReturn(enrollmentModeCmResponse);
    }

    private CmResponse buildCmResponse(final String nodeName, final String attribute, final Object expectedValue) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(attribute, expectedValue);

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + nodeName);
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;
    }

    private EnrollmentPartialInfos getEnrollmentPartialInfos() {
        return getEnrollmentPartialInfos(NODE_FDN);
    }

    private EnrollmentPartialInfos getEnrollmentPartialInfos(final String fdn) {

        final Entity ee = createEntity(fdn);

        return new EnrollmentPartialInfos(ee, getEnrollmentServerInfo(), AlgorithmKeys.RSA_2048);
    }

    private Entity createEntity(final String fdn) {
        final EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(fdn + "-ipsec");
        entityInfo.setId(1);

        final SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
        subjectAltNameField.setType(SubjectAltNameFieldType.IP_ADDRESS);

        CertificateAuthority certificateAuthority = new CertificateAuthority();
        certificateAuthority.setName("Issuer");
        entityInfo.setIssuer(certificateAuthority);
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

        final Algorithm keyGenerationAlgorithm = new Algorithm();
        keyGenerationAlgorithm.setKeySize((ALGOKEYS.getKeySize()));
        ee.setKeyGenerationAlgorithm(keyGenerationAlgorithm);

        return ee;
    }

    /**
     * Test the Generate SCEP Enrollment Info for a given set of nodes.
     *
     * @throws Exception
     */
    @Test (expected = CppSecurityServiceException.class)
    public void testISCFGenerateOAMEnrollmentInfo() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = VDU_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.CMPv2_INITIAL)).thenReturn(true);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.CMPv2_INITIAL, nmi);

        assertTrue(scepEnrollmentInfo != null);
        verify(nscsPkiManager).getEnrollmentEntityInfo(any());
    }

    @Test
    public void testISCFGenerateOAMEnrollmentInfo_blankFdn() throws Exception {

        final NodeModelInformation nmi = CPP_15B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        exception.expect(CppSecurityServiceException.class);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN + "  ", null, null, null, EnrollmentMode.SCEP,
                nmi);
    }

    @Test
    public void testISCFGenerateOAMEnrollmentInfo_invalidFdn() throws Exception {

        final NodeModelInformation nmi = CPP_15B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        exception.expect(CppSecurityServiceException.class);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo("@#$%", null, null, null, EnrollmentMode.SCEP, nmi);
    }

    @Test
    public void testISCFGenerateOAMEnrollmentInfo_nullNodeType() throws Exception {

        final NodeModelInformation nmi = new NodeModelInformation(CPP_15B_MODEL_INFO.getModelIdentifier(),
                CPP_15B_MODEL_INFO.getModelIdentifierType(), "ERBS");
        nmi.setNodeType(null);
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        exception.expect(CppSecurityServiceException.class);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP, nmi);
    }

    @Test
    public void testISCFGenerateOAMEnrollmentInfo_nullEnrollmentMode() throws Exception {

        final NodeModelInformation nmi = CPP_15B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        exception.expect(CppSecurityServiceException.class);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, null, nmi);
    }

    @Test
    public void testISCFGenerateOAMEnrollmentInfo_unsupportedEnrollmentMode() throws Exception {

        final NodeModelInformation nmi = CPP_13B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        exception.expect(CppSecurityServiceException.class);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null,
                EnrollmentMode.CMPv2_INITIAL, nmi);
    }

    @Test
    public void testISCFGenerateOIPSECEnrollmentInfo_invalidSubjectAltName() throws Exception {

        final NodeModelInformation nmi = CPP_15B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        exception.expect(CppSecurityServiceException.class);
        final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType("%$##@");
        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo("@#$%", null, subjectAltNameString,
                SubjectAltNameFormat.IPV4, EnrollmentMode.SCEP, nmi);
    }

    // Xml Scalability purpose
    @Ignore
    @Test
    public void testSCEPEnrollmentInfo_KSandEMSupported_for14BNodes() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = CPP_15B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isKSandEMSupported(any(NodeModelInformation.class))).thenReturn(true);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.SCEP)).thenReturn(true);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP, nmi);

        assertTrue(scepEnrollmentInfo != null);
        assertTrue("KSandEMSupported should be true for MIM version " + nmi.getModelIdentifier(), scepEnrollmentInfo.isKSandEMSupported());
    }

    // Xml Scalability purpose
    @Ignore
    @Test
    public void testSCEPEnrollmentInfo_KSandEMSupported_for13BNodes() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = CPP_13B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.SCEP)).thenReturn(true);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP,
                CPP_13B_MODEL_INFO);

        assertTrue(scepEnrollmentInfo != null);
        assertFalse("KSandEMSupported should be false for MIM version " + nmi.getModelIdentifier(), scepEnrollmentInfo.isKSandEMSupported());
    }

    // Xml Scalability purpose
    @Ignore
    @Test
    public void testSCEPEnrollmentInfo_CertificateAuthorityDnSupported_for13BNodes() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = CPP_13B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.SCEP)).thenReturn(true);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP, nmi);

        assertTrue(scepEnrollmentInfo != null);
        assertFalse("CertificateAuthorityDnSupported should be false for MIM version " + nmi.getModelIdentifier(),
                scepEnrollmentInfo.isCertificateAuthorityDnSupported());
    }

    // Xml Scalability purpose
    @Ignore
    @Test
    public void testSCEPEnrollmentInfo_CertificateAuthorityDnSupported_MIMVersion_LowerThan_5_1_200() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = CPP_15B_MODEL_INFO;
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.SCEP)).thenReturn(true);

        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP, nmi);

        assertTrue(scepEnrollmentInfo != null);
        assertFalse("CertificateAuthorityDnSupported should be false for MIM version " + nmi.getModelIdentifier(),
                scepEnrollmentInfo.isCertificateAuthorityDnSupported());
    }

    // Xml Scalability purpose
    @Ignore
    @Test
    public void testSCEPEnrollmentInfo_CertificateAuthorityDnSupported_MIMVersion_5_1_200() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = CPP_15B_CADN_THRESHOLD_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isCertificateAuthorityDnSupported(any(NodeModelInformation.class))).thenReturn(true);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.SCEP)).thenReturn(true);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP, nmi);

        assertTrue(scepEnrollmentInfo != null);
        assertTrue("CertificateAuthorityDnSupported should be true for MIM version " + nmi.getModelIdentifier(),
                scepEnrollmentInfo.isCertificateAuthorityDnSupported());
    }

    // Xml Scalability purpose
    @Ignore
    @Test
    public void testSCEPEnrollmentInfo_CertificateAuthorityDnSupported_MIMVersion_GreaterThan_5_1_200() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = CPP_15B_CADN_SUPPORTED_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isCertificateAuthorityDnSupported(any(NodeModelInformation.class))).thenReturn(true);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.SCEP)).thenReturn(true);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP, nmi);

        assertTrue(scepEnrollmentInfo != null);
        assertTrue("CertificateAuthorityDnSupported should be true for MIM version greater than "
                + CPP_15B_CADN_THRESHOLD_MODEL_INFO.getModelIdentifier(), scepEnrollmentInfo.isCertificateAuthorityDnSupported());
    }

    // Xml Scalability purpose
    @Ignore
    @Test
    public void testSCEPEnrollmentInfo_CertificateAuthorityDnSupported_MIMVersion_GreaterThan_5_1_200_B() throws Exception {
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());

        final NodeModelInformation nmi = CPP_16B_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isCertificateAuthorityDnSupported(any(NodeModelInformation.class))).thenReturn(true);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.SCEP)).thenReturn(true);

        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(NODE_FDN, null, null, null, EnrollmentMode.SCEP, nmi);

        assertTrue(scepEnrollmentInfo != null);
        assertTrue("CertificateAuthorityDnSupported should be true for MIM version greater than "
                + CPP_15B_CADN_THRESHOLD_MODEL_INFO.getModelIdentifier(), scepEnrollmentInfo.isCertificateAuthorityDnSupported());
    }

    @Test
    public void testGetTrustChainForEntity() throws Exception {
        when(smrsService.getFileServerAddress(any(SmrsAddressRequest.class))).thenReturn("Address");
        when(smrsService.getNodeSpecificAccount(anyString(), anyString(), anyString())).thenReturn(smrsAccount);
        final Set<NscsPair<String, Boolean>> dNSet = new HashSet<>();
        dNSet.add(new NscsPair<String, Boolean>(DN, false));
        when(nscsPkiManager.getTrustedCAs(anyString())).thenReturn(dNSet);
        when(nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_15B_MODEL_INFO)).thenReturn(DigestAlgorithm.SHA256);
        when(readerService.getNormalizedNodeReference((NodeReference) Matchers.anyObject())).thenReturn(normalizableNodeReference);
        final TrustStoreInfo trustStoreinfo = beanUnderTest.getTrustStoreForAP(TrustedCertCategory.CORBA_PEERS, NODE_FDN, CPP_15B_MODEL_INFO);
        assertTrue(trustStoreinfo != null);
        assertTrue(trustStoreinfo.getCategory() == TrustedCertCategory.CORBA_PEERS);
        assertTrue(trustStoreinfo.getFingerPrintAlgorithm() == DigestAlgorithm.SHA256);

        verify(nscsPkiManager).getTrustCertificatesFromProfile(anyString());
        verify(nscsPkiManager).getPkiEntity(anyString());
    }

    @Test
    public void testGetTrustChainForNode() throws Exception {
        when(smrsService.getFileServerAddress(any(SmrsAddressRequest.class))).thenReturn("Address");
        when(smrsService.getNodeSpecificAccount(anyString(), anyString(), anyString())).thenReturn(smrsAccount);
        final Set<NscsPair<String, Boolean>> dNSet = new HashSet<>();
        dNSet.add(new NscsPair<String, Boolean>(DN, false));
        when(nscsPkiManager.getTrustedCAs(IPSEC_ENTITY_PROFILE_NAME)).thenReturn(dNSet);
        setUpData_NscsPkiManager_Entity(NODE.getFdn(), false);
        when(readerService.getNodeModelInformation(NODE.getFdn())).thenReturn(CPP_15B_MODEL_INFO);
        when(nscsCapabilityModelService.getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.OAM)).thenReturn(IPSEC_ENTITY_PROFILE_NAME);
        when(nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_15B_MODEL_INFO)).thenReturn(DigestAlgorithm.SHA256);
        when(readerService.getNormalizedNodeReference((NodeReference) Matchers.anyObject())).thenReturn(normalizableNodeReference);
        List<String> users=new ArrayList<>();
        users.add("mm-cert-user");
        final TrustStoreInfo trustStoreinfo = beanUnderTest.getTrustStoreForNode(TrustedCertCategory.CORBA_PEERS, NODE, true);
        assertTrue(trustStoreinfo != null);
        assertTrue(trustStoreinfo.getCategory() == TrustedCertCategory.CORBA_PEERS);
        assertTrue(trustStoreinfo.getFingerPrintAlgorithm() == DigestAlgorithm.SHA256);

        verify(smrsService).getNodeSpecificAccount(anyString(), anyString(), anyString());
        verify(smrsService).getFileServerAddress(any(SmrsAddressRequest.class));
        verify(nscsPkiManager).getTrustCertificatesFromProfile(anyString());
    }

    @Test
    public void testCancelSCEPEnrollment() throws CppSecurityServiceException, NscsPkiCertificateManagerException, NscsPkiEntitiesManagerException {
        beanUnderTest.cancelSCEPEnrollment("node1");
        verify(nscsPkiManager).deleteEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, "node1"));
        verify(nscsPkiManager).deleteEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, "node1"));
    }

    @Test
    public void testSmrsAddressMultiple() {
        final String address = "1.2.3.4,1.2.3.5";
        when(smrsService.getFileServerAddress(any(SmrsAddressRequest.class))).thenReturn(address);
        final Set<String> setAddress = beanUnderTest.fetchSmrsAddresses("ERBS", null);

        assertEquals(2, setAddress.size());
        assertTrue(setAddress.contains("1.2.3.4"));
        assertTrue(setAddress.contains("1.2.3.5"));
    }

    @Test
    public void testSmrsAddressSingle() {
        final String address = "1.2.3.4";
        when(smrsService.getFileServerAddress(any(SmrsAddressRequest.class))).thenReturn(address);
        final Set<String> setAddress = beanUnderTest.fetchSmrsAddresses("ERBS", null);

        assertEquals(1, setAddress.size());
        assertTrue(setAddress.contains("1.2.3.4"));
    }

    @Test
    public void testGetTrustedCAInfoByNameWithNullName() {
        try {
            beanUnderTest.getTrustedCAInfoByName(null, false);
            assert (false);
        } catch (final CppSecurityServiceException e) {
            assert (true);
        }
    }

    @Test
    public void testGetTrustedCAInfoByNameWithValidNameIPv4Node() {
        testGetTrustedCAInfoByName(false, INFRA_CA_NAME, ROOT_CA_NAME);
    }

    @Test
    public void testGetTrustedCAInfoByNameWithValidNameIPv6Node() {
        testGetTrustedCAInfoByName(true, INFRA_CA_NAME, ROOT_CA_NAME);
    }

    @Test
    public void testGetTrustedCAsInfoByEntityProfileNameWithNullName() {
        try {
            beanUnderTest.getTrustedCAsInfoByEntityProfileName(null, false);
            assert (false);
        } catch (final CppSecurityServiceException e) {
            assert (true);
        }
    }

    @Test
    public void testGetTrustedCAsInfoByEntityProfileNameWithValidNameIPv4NodeNoMS9() {
        testGetTrustedCAsInfoByEntityProfileName(DG2_OAM_ENTITY_PROFILE_NAME, false, false, true);
    }

    @Test
    public void testGetTrustedCAsInfoByEntityProfileNameWithValidNameIPv6NodeNoMS9() {
        testGetTrustedCAsInfoByEntityProfileName(DG2_OAM_ENTITY_PROFILE_NAME, true, false, true);
    }

    @Test
    public void testGetTrustedCAsInfoByEntityProfileNameWithValidNameIPv4NodeMS9ActiveCert() {
        testGetTrustedCAsInfoByEntityProfileName(DG2_OAM_ENTITY_PROFILE_NAME, false, true, true);
    }

    @Test
    public void testGetTrustedCAsInfoByEntityProfileNameWithValidNameIPv4NodeMS9AInactiveCert() {
        testGetTrustedCAsInfoByEntityProfileName(DG2_OAM_ENTITY_PROFILE_NAME, false, true, false);
    }

    @Test (expected = CppSecurityServiceException.class)
    public void testGenerateOAMEnrollmentInfo_nullNodeRef() throws Exception {

        final NodeModelInformation nmi = VDU_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.CMPv2_INITIAL)).thenReturn(true);
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        String subjectAltName = "192.12.12.12";
        SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.IPV4;
        SubjectAltNameParam subjectAltNameParam = null;
        final NodeIdentifier nodeIdentifier = new NodeIdentifier("nodeFdn", null);
        if (subjectAltName != null && subjectAltNameFormat != null) {
            final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
            subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
        }
        enrollmentRequestInfo.setCommonName("CommonName");
        enrollmentRequestInfo.setCertType("OAM");
        enrollmentRequestInfo.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        enrollmentRequestInfo.setKeySize("RSA_2048");
        enrollmentRequestInfo.setNodeIdentifier(nodeIdentifier);
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltNameParam);
        enrollmentRequestInfo.setNodeName(NODE_FDN);
        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(nmi, enrollmentRequestInfo);
    }

    @Test
    public void testGenerateOAMEnrollmentInfo() throws Exception {
        final NodeReference node = new NodeRef("NodeName");
        when(readerService.getNormalizedNodeReference((NodeReference) Matchers.anyObject())).thenReturn(normalizableNodeReference);
        final NodeModelInformation nmi = VDU_MODEL_INFO;
        when(readerService.getNodeModelInformation(anyString())).thenReturn(nmi);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(nmi, EnrollmentMode.CMPv2_INITIAL)).thenReturn(true);
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        String subjectAltName = "192.12.12.12";
        SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.IPV4;
        SubjectAltNameParam subjectAltNameParam = null;
        final NodeIdentifier nodeIdentifier = new NodeIdentifier("nodeFdn", null);
        if (subjectAltName != null && subjectAltNameFormat != null) {
            final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
            subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
        }

        final Entity entity = createEntity(NODE_FDN);
        enrollmentRequestInfo.setCommonName("CommonName");
        enrollmentRequestInfo.setCertType("OAM");
        enrollmentRequestInfo.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        enrollmentRequestInfo.setKeySize("RSA_2048");
        enrollmentRequestInfo.setNodeIdentifier(nodeIdentifier);
        enrollmentRequestInfo.setNodeName(NODE_FDN);
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltNameParam);
        EnrollmentPartialInfos enrollmentPartialInfo = new EnrollmentPartialInfos(entity, null, ALGOKEYS);
        Map<String, String> enrollmentCAAuthorizationModes = new HashMap<String, String>();
        enrollmentCAAuthorizationModes.put("1", "1");
        Mockito.when(nscsCapabilityModelService.getEnrollmentCAAuthorizationModes(normRef)).thenReturn(enrollmentCAAuthorizationModes);
        Mockito.when(nscsCapabilityModelService.getDefaultDigestAlgorithm(any())).thenReturn(DigestAlgorithm.SHA256);
        Mockito.when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(enrollmentPartialInfo);
        Mockito.when(nscsNodeUtility.getEnrollmentModeFromNES(NODE_FDN)).thenReturn(EnrollmentMode.CMPv2_INITIAL);
        Mockito.when(normalizableNodeReference.getNormalizedRef()).thenReturn(node);
        Mockito.when(readerService.exists(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames("normalizedRefName").fdn())).thenReturn(true);
        final ScepEnrollmentInfo scepEnrollmentInfo = beanUnderTest.generateOamEnrollmentInfo(nmi, enrollmentRequestInfo);
    }

    @Test
    public void testGenerateOamEnrollmentInfo_WithNodeFDN() throws CppSecurityServiceException, NscsPkiEntitiesManagerException {
        doReturn(CPP_15B_MODEL_INFO).when(readerService).getNodeModelInformation(NODE_FDN);
        Mockito.when(nscsNodeUtility.getEnrollmentMode(NODE_FDN)).thenReturn(EnrollmentMode.CMPv2_INITIAL);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(readerService.getNormalizedNodeReference(any())).thenReturn(normRef);
        Mockito.when(nscsNodeUtility.getEnrollmentModeFromNES(NODE_FDN)).thenReturn(EnrollmentMode.CMPv2_INITIAL);
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());
        when(nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_15B_MODEL_INFO)).thenReturn(DigestAlgorithm.SHA256);
        Map<String, String> enrollmentCAAuthorizationModes = new HashMap<String, String>();
        enrollmentCAAuthorizationModes.put("1", "1");
        Mockito.when(nscsCapabilityModelService.getEnrollmentCAAuthorizationModes(normRef)).thenReturn(enrollmentCAAuthorizationModes);
        beanUnderTest.generateOamEnrollmentInfo(NODE_FDN);
    }

    @Test
    public void testGenerateIpsecEnrollmentInfo_WithNodeFDN_WithSAN() throws CppSecurityServiceException, NscsPkiEntitiesManagerException {
        doReturn(CPP_15B_MODEL_INFO).when(readerService).getNodeModelInformation(NODE_FDN);
        Mockito.when(nscsNodeUtility.getEnrollmentMode(NODE_FDN)).thenReturn(EnrollmentMode.CMPv2_INITIAL);
        when(nscsCapabilityModelService.isEnrollmentModeSupported(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(readerService.getNormalizedNodeReference(any())).thenReturn(normRef);
        Mockito.when(nscsNodeUtility.getEnrollmentModeFromNES(NODE_FDN)).thenReturn(EnrollmentMode.CMPv2_INITIAL);
        when(nscsPkiManager.getEnrollmentEntityInfo(any())).thenReturn(getEnrollmentPartialInfos());
        when(nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_15B_MODEL_INFO)).thenReturn(DigestAlgorithm.SHA256);
        Map<String, String> enrollmentCAAuthorizationModes = new HashMap<String, String>();
        enrollmentCAAuthorizationModes.put("1", "1");
        Mockito.when(nscsCapabilityModelService.getEnrollmentCAAuthorizationModes(normRef)).thenReturn(enrollmentCAAuthorizationModes);
        SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.IPV4;
        BaseSubjectAltNameDataType subjectAltNameDataType = new SubjectAltNameStringType("192.12.12.12");
        beanUnderTest.generateIpsecEnrollmentInfo(NODE_FDN, subjectAltNameDataType, subjectAltNameFormat);
    }

    @Test (expected = IscfServiceException.class)
    public void testTrustDistributionPointUrlException() throws CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsPkiManager.getTrustedCAInfoByName(anyString())).thenThrow(IscfServiceException.class);
        beanUnderTest.getTrustDistributionPointUrl("caEntityName", "Test_Node");
    }

    @Test
    public void testTrustDistributionPointUrl() throws CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(readerService.getNormalizedNodeReference(any())).thenReturn(normRef);
        TrustedEntityInfo pkiTrustedCAInfo = new TrustedEntityInfo();
        pkiTrustedCAInfo.setIpv4TrustDistributionPointURL("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/6e7c1b2151e42a73/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiManager.getTrustedCAInfoByName(anyString())).thenReturn(pkiTrustedCAInfo);
        beanUnderTest.getTrustDistributionPointUrl("caEntityName", "Test_Node");
    }

    @Test
    public void testTrustDistributionPointUrl_IPv6() throws CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(readerService.getNormalizedNodeReference(any())).thenReturn(normRef);
        TrustedEntityInfo pkiTrustedCAInfo = new TrustedEntityInfo();
        pkiTrustedCAInfo.setIpv4TrustDistributionPointURL("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/6e7c1b2151e42a73/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiManager.getTrustedCAInfoByName(anyString())).thenReturn(pkiTrustedCAInfo);
        Mockito.when(nscsNodeUtility.hasNodeIPv6Address(any())).thenReturn(true);
        beanUnderTest.getTrustDistributionPointUrl("caEntityName", "Test_Node");
    }

    @Test
    public void testTrustDistributionPointUrl_nullTrustedCAInfo() throws CppSecurityServiceException, NscsPkiEntitiesManagerException {
        beanUnderTest.getTrustDistributionPointUrl("caEntityName", "Test_Node");
    }

    private static X509Certificate generateV3Certificate()
            throws InvalidKeyException, NoSuchProviderException, SignatureException, NoSuchAlgorithmException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=TestIssuerDN"));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
        certGen.setSubjectDN(new X500Principal("CN=TestSubjectDN"));

        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(1024, new SecureRandom());
        final KeyPair pair = kpGen.generateKeyPair();
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
        certGen.addExtension(X509Extensions.SubjectAlternativeName, false,
                new GeneralNames(new GeneralName(GeneralName.rfc822Name, "test@test.test")));
        return certGen.generateX509Certificate(pair.getPrivate(), "BC");
    }

    private EnrollmentInfo getEnrollmentServerInfo() {
        final EnrollmentInfo ei = new EnrollmentInfo();
        ei.setEnrollmentURL(URL);
        try {
            ei.setCaCertificate(generateV3Certificate());
        } catch (InvalidKeyException | NoSuchProviderException | SignatureException | NoSuchAlgorithmException ex) {
        }
        return ei;
    }

    private void testGetTrustedCAInfoByName(final boolean isIpv6, final String caName, final String issuerName) {
        final TrustedEntityInfo pkiTrustedCAInfo = generateTrustedCAInfo(caName, issuerName, true);
        assertNotNull(pkiTrustedCAInfo);
        try {
            when(nscsPkiManager.getTrustedCAInfoByName(eq(INFRA_CA_NAME))).thenReturn(pkiTrustedCAInfo);
        } catch (final NscsPkiEntitiesManagerException e) {
            assert (false);
        }
        try {
            final NscsTrustedEntityInfo trustedCAInfo = beanUnderTest.getTrustedCAInfoByName(caName, isIpv6);
            verify(nscsPkiManager).getTrustedCAInfoByName(caName);
            verify(nscsPkiManager, never()).getEntityProfile(anyString());
            assertNotNull(trustedCAInfo);
            assertEquals(trustedCAInfo.getName(), caName);
            assertEquals(trustedCAInfo.getIssuer(), getDNByName(issuerName));
            assertEquals(trustedCAInfo.getSerialNumber(), NSCS_SERIAL_NUMBER);
            final String tdpsUrl = isIpv6 ? getIPv6TdpsUrl(caName, PKI_SERIAL_NUMBER, issuerName)
                    : getIPv4TdpsUrl(caName, PKI_SERIAL_NUMBER, issuerName);
            assertEquals(trustedCAInfo.getTdpsUrl(), tdpsUrl);
        } catch (CppSecurityServiceException | NscsPkiEntitiesManagerException e) {
            assert (false);
        }
    }

    private void testGetTrustedCAsInfoByEntityProfileName(final String entityProfileName, final boolean isIpv6, final boolean isMS9,
                                                          final boolean activeCert) {
        final Set<NscsPair<String, NscsPair<String, Boolean>>> expectedTrustedCAs = new HashSet<NscsPair<String, NscsPair<String, Boolean>>>();
        final Set<NscsPair<String, String>> expectedTrustedCAsPair = new HashSet<NscsPair<String, String>>();
        expectedTrustedCAs.add(new NscsPair<String, NscsPair<String, Boolean>>(OAM_CA_NAME, new NscsPair<String, Boolean>(INFRA_CA_NAME, true)));
        expectedTrustedCAsPair.add(new NscsPair<String, String>(OAM_CA_NAME, getDNByName(INFRA_CA_NAME)));
        expectedTrustedCAs.add(new NscsPair<String, NscsPair<String, Boolean>>(INFRA_CA_NAME, new NscsPair<String, Boolean>(ROOT_CA_NAME, true)));
        expectedTrustedCAsPair.add(new NscsPair<String, String>(INFRA_CA_NAME, getDNByName(ROOT_CA_NAME)));
        if (isMS9) {
            expectedTrustedCAs.add(
                    new NscsPair<String, NscsPair<String, Boolean>>(ROOT_CA_NAME, new NscsPair<String, Boolean>(INTERMEDIATE_EXT_CA_NAME, true)));
            expectedTrustedCAsPair.add(new NscsPair<String, String>(ROOT_CA_NAME, getDNByName(INTERMEDIATE_EXT_CA_NAME)));
            expectedTrustedCAs.add(new NscsPair<String, NscsPair<String, Boolean>>(INTERMEDIATE_EXT_CA_NAME,
                    new NscsPair<String, Boolean>(ROOT_EXT_CA_NAME, activeCert)));
            expectedTrustedCAsPair.add(new NscsPair<String, String>(INTERMEDIATE_EXT_CA_NAME, getDNByName(ROOT_EXT_CA_NAME)));
            expectedTrustedCAs.add(
                    new NscsPair<String, NscsPair<String, Boolean>>(ROOT_EXT_CA_NAME, new NscsPair<String, Boolean>(ROOT_EXT_CA_NAME, activeCert)));
            expectedTrustedCAsPair.add(new NscsPair<String, String>(ROOT_EXT_CA_NAME, getDNByName(ROOT_EXT_CA_NAME)));
        } else {
            expectedTrustedCAs.add(new NscsPair<String, NscsPair<String, Boolean>>(ROOT_CA_NAME, new NscsPair<String, Boolean>(ROOT_CA_NAME, true)));
            expectedTrustedCAsPair.add(new NscsPair<String, String>(ROOT_CA_NAME, getDNByName(ROOT_CA_NAME)));
        }

        for (final NscsPair<String, String> exp : expectedTrustedCAsPair) {
            System.out.println("EXP L: " + exp.getL() + " EXP R: " + exp.getR());
        }
        final Set<TrustedEntityInfo> pkiTrustedCAsInfo = generateTrustedCAsInfo(expectedTrustedCAs, isMS9, activeCert);
        assertNotNull(pkiTrustedCAsInfo);
        assertTrue(pkiTrustedCAsInfo.size() >= 0);
        try {
            when(nscsPkiManager.getTrustedCAsInfoByEntityProfileName(entityProfileName)).thenReturn(pkiTrustedCAsInfo);
        } catch (final NscsPkiEntitiesManagerException e) {
            assert (false);
        }
        try {
            final Set<NscsTrustedEntityInfo> trustedCAsInfo = beanUnderTest.getTrustedCAsInfoByEntityProfileName(entityProfileName, isIpv6);
            verify(nscsPkiManager).getTrustedCAsInfoByEntityProfileName(eq(entityProfileName));
            assertNotNull(trustedCAsInfo);
            assertEquals(pkiTrustedCAsInfo.size(), trustedCAsInfo.size());
            final Iterator<NscsTrustedEntityInfo> itTrustedCAsInfo = trustedCAsInfo.iterator();
            while (itTrustedCAsInfo.hasNext()) {
                final NscsTrustedEntityInfo trustedCAInfo = itTrustedCAsInfo.next();
                final NscsPair<String, String> actualTrustedCAPair = new NscsPair<String, String>(trustedCAInfo.getName(), trustedCAInfo.getIssuer());
                System.out.println("ACT L: " + actualTrustedCAPair.getL() + " ACT R: " + actualTrustedCAPair.getR());
                assertTrue(expectedTrustedCAsPair.contains(actualTrustedCAPair));
                assertEquals(trustedCAInfo.getSerialNumber(), NSCS_SERIAL_NUMBER);
            }
        } catch (CppSecurityServiceException | NscsPkiEntitiesManagerException e) {
            assert (false);
        }
    }

    private TrustedEntityInfo generateTrustedCAInfo(final String caName, final String issuerName, final boolean isActive) {
        final TrustedEntityInfo trustedCAInfo = new TrustedEntityInfo();
        trustedCAInfo.setEntityName(caName);
        trustedCAInfo.setEntityType(EntityType.CA_ENTITY);
        trustedCAInfo.setCertificateStatus(isActive ? CertificateStatus.ACTIVE : CertificateStatus.INACTIVE);
        trustedCAInfo.setSubjectDN(getDNByName(caName));
        trustedCAInfo.setIssuerDN(issuerName);
        trustedCAInfo.setIssuerFullDN(getDNByName(issuerName));
        trustedCAInfo.setCertificateSerialNumber(PKI_SERIAL_NUMBER);
        trustedCAInfo.setIpv4TrustDistributionPointURL(getIPv4TdpsUrl(caName, PKI_SERIAL_NUMBER, issuerName));
        trustedCAInfo.setIpv6TrustDistributionPointURL(getIPv6TdpsUrl(caName, PKI_SERIAL_NUMBER, issuerName));
        return trustedCAInfo;
    }

    private Set<TrustedEntityInfo> generateTrustedCAsInfo(final Set<NscsPair<String, NscsPair<String, Boolean>>> expectedTrustedCAsPair,
                                                          final boolean isMS9, final boolean activeCert) {
        final Set<TrustedEntityInfo> pkiTrustedCAsInfo = new HashSet<>();
        for (final NscsPair<String, NscsPair<String, Boolean>> trustedCA : expectedTrustedCAsPair) {
            pkiTrustedCAsInfo.add(generateTrustedCAInfo(trustedCA.getL(), trustedCA.getR().getL(), trustedCA.getR().getR()));
        }
        return pkiTrustedCAsInfo;
    }

    private static String getDNByName(final String name) {
        return "CN=" + name + ",OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE";
    }

    private static String getIPv4TdpsUrl(final String caName, final String serial, final String issuerName) {
        return "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/" + caName + "/" + serial + "/active/" + issuerName;
    }

    private static String getIPv6TdpsUrl(final String caName, final String serial, final String issuerName) {
        return "http://[2001:1b70:82a1:103::181]:8093/pki-ra-tdps/ca_entity/" + caName + "/" + serial + "/active/" + issuerName;
    }

}
