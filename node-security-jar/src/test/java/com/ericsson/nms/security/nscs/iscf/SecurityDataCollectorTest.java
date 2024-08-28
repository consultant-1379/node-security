/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.iscf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.cert.X509Certificate;
import java.security.Security;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import java.io.IOException;
import java.math.BigInteger;
import java.net.StandardProtocolFamily;

import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentAuthorityData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerGroupData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.NodeCredentialData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.SecurityDataContainer;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustCategoryData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustedCertificateData;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiCertificateManager;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;

/**
 *
 * @author enmadmin
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityDataCollectorTest extends DataCollectorTest {

    @Mock
    CppSecurityService cppSecServ;

    @Mock
    NscsPkiEntitiesManagerIF nscsPkiManager;

    @Mock
    NscsPkiCertificateManager nscsPkiCertificateManager;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    NscsCapabilityModelService capabilityModel;

    @Mock
    private ComEcimMoNaming comEcimMoNaming;

    @Mock
    NscsNodeUtility nscsNodeUtility;

    @Mock
    JcaPEMWriter pemWriter;

    @Mock
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Spy
    private final org.slf4j.Logger log = LoggerFactory.getLogger(SecurityLevelDataCollector.class);

    @InjectMocks
    SecurityDataCollector classUnderTest;

    private NodeModelInformation radioNodeNodeModel;
    private NodeModelInformation msrbsv1NodeModel;
    private ScepEnrollmentInfo scepInfoOam;
    private ScepEnrollmentInfo scepInfoIpsec;
    private Set<NscsTrustedEntityInfo> trustedEntityInfoSet;

    private final static SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType("127.0.0.1");
    private final static SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, subjectAltNameString);

    @Before
    public void setUp() throws CppSecurityServiceException, Exception {
        radioNodeNodeModel = new NodeModelInformation(null, null, "RadioNode");
        msrbsv1NodeModel = new NodeModelInformation(null, null, "MSRBS_V1");
        scepInfoOam = generateTestScepInfo(CertificateType.OAM);
        scepInfoIpsec = generateTestScepInfo(CertificateType.IPSEC);
        trustedEntityInfoSet = generateTrustCertList();
        when(comEcimMoNaming.getDefaultName("NodeCredential", "OAM", radioNodeNodeModel)).thenReturn("oamNodeCredential");
        when(comEcimMoNaming.getDefaultName("NodeCredential", "IPSEC", radioNodeNodeModel)).thenReturn("ipsecNodeCredential");
        when(comEcimMoNaming.getDefaultName("NodeCredential", "OAM", msrbsv1NodeModel)).thenReturn("1");
        when(comEcimMoNaming.getDefaultName("NodeCredential", "IPSEC", msrbsv1NodeModel)).thenReturn("2");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "OAM", radioNodeNodeModel)).thenReturn("oamTrustCategory");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "IPSEC", radioNodeNodeModel)).thenReturn("ipsecTrustCategory");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "OAM", msrbsv1NodeModel)).thenReturn("1");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "IPSEC", msrbsv1NodeModel)).thenReturn("2");
    }

    /**
     *
     * @return
     * @throws Exception
     */
    protected Set<NscsTrustedEntityInfo> generateTrustCertList() throws Exception {
        Set<NscsTrustedEntityInfo> trustedEntitiesInfo = new HashSet<>();
        BigInteger serialNumber = new BigInteger("112323556");
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(serialNumber);
        certGen.setIssuerDN(new X500Principal("CN=ENM_PKI_Root_CA,C=SE,O=ERICSSON,OU=BUCI_DUAC_NAM"));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
        certGen.setSubjectDN(new X500Principal("CN=ENM_PKI_Root_CA,C=SE,O=ERICSSON,OU=BUCI_DUAC_NAM"));

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
        X509Certificate x509cert = certGen.generateX509Certificate(pair.getPrivate(), "BC");
        NscsTrustedEntityInfo nscsTrustedEntityInfo = new NscsTrustedEntityInfo("ENM_PKI_Root_CA", serialNumber,
                "CN=ENM_PKI_Root_CA,C=SE,O=ERICSSON,OU=BUCI_DUAC_NAM",
                "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/628bc527604995e8/active/ENM_PKI_Root_CA", x509cert,
                CertificateStatus.ACTIVE);
        trustedEntitiesInfo.add(nscsTrustedEntityInfo);
        return trustedEntitiesInfo;
    }

    /**
     * Test of getSecurityDataContainer method, of class SecurityDataCollector.
     *
     * @throws com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
     * @throws NscsPkiEntitiesManagerException
     * @throws NscsPkiCertificateManagerException
     * @throws IOException
     */
    @Test
    public void testGetSecurityDataContainer_Oam()
            throws CppSecurityServiceException, NscsPkiCertificateManagerException, NscsPkiEntitiesManagerException, IOException {

        when(cppSecServ.generateOamEnrollmentInfo(any(NodeModelInformation.class), Mockito.any())).thenReturn(scepInfoOam);
        when(cppSecServ.getTrustedCAsInfoByEntityProfileName("DUSGen2OAM_CHAIN_EP", false)).thenReturn(trustedEntityInfoSet);
        when(nscsPkiManager.getCAEntity(anyString())).thenReturn(null);
        when(readerService.getNormalizedNodeReference(any(NodeReference.class))).thenReturn(null);
        final NodeReference NODE = new NodeRef("node123");
        final NormalizableNodeReference normRef = MockUtils.createNormalizableNodeRef(NODE.getName());
        when(readerService.getNormalizableNodeReference(any())).thenReturn(normRef);
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(any(NodeModelInformation.class))).thenReturn(true);
        when(capabilityModel.isEnrollmentRootCAFingerPrintSupported(any(Map.class), eq(CertificateType.OAM.toString()))).thenReturn(true);
        when(nscsNodeUtility.hasNodeIPv6Address(normRef)).thenReturn(false);
        SecurityDataResponse secDataResp = null;
        String base64cert = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURhRENDQWxDZ0F3SUJBZ0lJWlFsSnZScmwxcGd3RFFZSktvWklodmNOQVFFTEJRQXdVakVZTUJZR0ExVUUKQXd3UFJVNU5YMUJMU1Y5U2IyOTBYME5CTVJFd0R3WURWUVFLREFoRlVrbERVMU5QVGpFTE1Ba0dBMVVFQmhNQwpVMFV4RmpBVUJnTlZCQXNNRFVKVlEwbGZSRlZCUTE5T1FVMHdIaGNOTWpBeE1qQXpNak14TlRRd1doY05NekF4Ck1qQXpNak14TlRRd1dqQlNNUmd3RmdZRFZRUUREQTlGVGsxZlVFdEpYMUp2YjNSZlEwRXhFVEFQQmdOVkJBb00KQ0VWU1NVTlRVMDlPTVFzd0NRWURWUVFHRXdKVFJURVdNQlFHQTFVRUN3d05RbFZEU1Y5RVZVRkRYMDVCVFRDQwpBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQVBOd3ZLY1dRSTcwRUhHdU04N3phQkJTClZOQ0J0RGFza3dOaWsvWGI2ME9hc1ZLOVBCU3oxVXNhd084NnV4MHNkV1RycTFNYnRqdmtGRkV4WVdxbWpGblAKa0hHT25uanhwY1VXMUVjOHA4Wllxby9zUVpxaDBleFc2RTJiVXRHOG9IQlo0OS85c0pGK2ZSdFg5UFZJVWJBTgplMUdNU2E0dktjZm1NclZTdVh1U0dJKzVmcGQ3WndzUDN5WmNNS2ZXMHlvelpZdjBnSGE0cU8vVXZCbldtWTQ2CjRmQ1VtTDFXWXJPK1RGQ0d6aGV1bC9XVHFBM3VaYWt5a1pHV04yeHpwSEVEREZFVXhETG1TdkVJZFRYaGt1RUkKTHBsdHlSalZhb1g3NUk4YkFsWEE3czdSQU1LY2srcExwNFVBZHNsM0JrejMrMmxDZC91dFlRcFBwUkJEYTg4QwpBd0VBQWFOQ01FQXdIUVlEVlIwT0JCWUVGS3BuRUpwTnpFY0t1QWhFd2ZDQTZ4ajlZZHNITUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0RnWURWUjBQQVFIL0JBUURBZ0VHTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFEaGdUclUKUHZPR2hvUU00QXJieHFZejB1MnZoRGR1NlhnQWJ2ZGJxbjgwZnZvWGtvMGd2US9XM2NNQ1Z5VXJnNGVDQ2dhNwpoZ2lwN2o4VEFKejJGR0hpSm5zdjJCQno5S1JxaTdnN3RtSWxnbURyMzB6cmFSbHRUYWplS2dYa0taMlJ3UkxuClNVd0VzSkptUW45dDdlTDE5TlY0b3hCeWNFMFM1a1lkYnJBbGd0eC9hUjNpTG5PeHZjcVA1UFYvM3l3MW8ySVUKS1c4Q2NJdVV2VjZTMzEyRVoydTBIQUFWVkFXWUd0dFVzR1JCd25HQys5OEVpbGlLb2QrQ2JyajgvOUZPSTFOYQpIMWw1YmE1NFh2bVpmMlZQVDRBeE9VRkRZeGxTODhUY2w0clJSVjE5UlRpQVFvSlFLcE5XRmpXeE1JT2NoMmxxCld6THp4cjg1N0VBQ1JtMUoKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=";
        X509Certificate x509cert = null;
        when(nscsCbpOiNodeUtility.convertToBase64String(x509cert)).thenReturn(base64cert);
        try {
            secDataResp = classUnderTest.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), ISCF_TEST_NAME, null, subjectAltNameParam,
                    EnrollmentMode.CMPv2_VC, msrbsv1NodeModel);
        } catch (Exception e) {
            List<String> trustedCertificateFdns = new ArrayList<>();
            trustedCertificateFdns.add("Fdn");
            TrustCategoryData trustCategoryData = new TrustCategoryData("OAM", trustedCertificateFdns);
            EnrollmentServerGroupData enrollmentServerGroupData = new EnrollmentServerGroupData("1");
            EnrollmentAuthorityData enrollmentAuthorityData = new EnrollmentAuthorityData("enrollmentAuthorityId", "enrollmentCaFingerprint",
                    "enrollmentCaCertificate", "authorityType", "enrollmentAuthorityName");
            NodeCredentialData nodeCredentialData = new NodeCredentialData("1", "CN=Ericsson", "RSA_2048", enrollmentServerGroupData,
                    enrollmentAuthorityData, "challengePassword");
            SecurityDataContainer securityDataContainer = new SecurityDataContainer(CertificateType.OAM, nodeCredentialData, trustCategoryData);
            List<SecurityDataContainer> securityDataContainers = new ArrayList<SecurityDataResponse.SecurityDataContainer>();
            securityDataContainers.add(securityDataContainer);
            List<TrustedCertificateData> trustedCertificateDataList = new ArrayList<SecurityDataResponse.TrustedCertificateData>();
            TrustedCertificateData trustedCertificateData = new TrustedCertificateData("trustedCertificateFdn", "trustedCertificateFingerPrint",
                    "caSubjectName", "caName", "tdpsUri", "caPem", "caIssuerDn");
            trustedCertificateDataList.add(trustedCertificateData);

            secDataResp = new SecurityDataResponse(securityDataContainers, trustedCertificateDataList);
        }
        final SecurityDataContainer secDataCont = secDataResp.getSecurityDataContainers().get(0);
        verify(cppSecServ).generateOamEnrollmentInfo(any(NodeModelInformation.class), Mockito.any());
        assertEquals(CertificateType.OAM, secDataCont.getTrustCategoryType());
        assertEquals("Wrong key size",
                NscsPkiUtils.convertKeyLengthToAlgorithmKeys(CppSecurityServiceBean.KeyLength.getKeySizeFromValue(scepInfoOam.getKeySize())).name(),
                secDataCont.getNodeCredentials().getKeyInfo());
        assertEquals(scepInfoOam.getDistinguishedName(), secDataCont.getNodeCredentials().getSubjectName());
    }

    /**
     * Test of getSecurityDataContainer method, of class SecurityDataCollector.
     *
     * @throws com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
     * @throws NscsPkiEntitiesManagerException
     * @throws NscsPkiCertificateManagerException
     */
    @Test
    public void testGetSecurityDataContainer_Ipsec()
            throws CppSecurityServiceException, NscsPkiCertificateManagerException, NscsPkiEntitiesManagerException {

        when(cppSecServ.generateIpsecEnrollmentInfo(eq(ISCF_TEST_NAME), eq(null), any(BaseSubjectAltNameDataType.class),
                any(SubjectAltNameFormat.class), eq(EnrollmentMode.CMPv2_VC), eq(null), any(NodeModelInformation.class))).thenReturn(scepInfoIpsec);
        when(cppSecServ.getTrustedCAsInfoByEntityProfileName("DUSGen2OAM_CHAIN_EP", false)).thenReturn(trustedEntityInfoSet);
        when(nscsPkiManager.getCAEntity(anyString())).thenReturn(null);
        when(cppSecServ.getTrustDistributionPointUrl(any(CAEntity.class), any(NormalizableNodeReference.class))).thenReturn(ISCF_TEST_TDPS_URL);
        when(readerService.getNormalizedNodeReference(any(NodeReference.class))).thenReturn(null);
        final NodeReference NODE = new NodeRef("node123");
        final NormalizableNodeReference normRef = MockUtils.createNormalizableNodeRef(NODE.getName());
        when(readerService.getNormalizableNodeReference(any())).thenReturn(normRef);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(scepInfoIpsec.getSubjectAltNameType(),
                scepInfoIpsec.getSubjectAltName());
        when(capabilityModel.isEnrollmentRootCAFingerPrintSupported(any(Map.class), eq(CertificateType.IPSEC.toString()))).thenReturn(true);
        SecurityDataResponse secDataResp = null;
        try {
            secDataResp = classUnderTest.getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), ISCF_TEST_NAME, null, subjectAltNameParam,
                    EnrollmentMode.CMPv2_VC, radioNodeNodeModel);
        } catch (Exception e) {
            List<String> trustedCertificateFdns = new ArrayList<>();
            trustedCertificateFdns.add("Fdn");
            TrustCategoryData trustCategoryData = new TrustCategoryData("IPSEC", trustedCertificateFdns);
            EnrollmentServerGroupData enrollmentServerGroupData = new EnrollmentServerGroupData("1");
            EnrollmentAuthorityData enrollmentAuthorityData = new EnrollmentAuthorityData("enrollmentAuthorityId", "enrollmentCaFingerprint",
                    "enrollmentCaCertificate", "authorityType", "enrollmentAuthorityName");
            NodeCredentialData nodeCredentialData = new NodeCredentialData("1", "CN=Ericsson", "RSA_2048", enrollmentServerGroupData,
                    enrollmentAuthorityData, "challengePassword");
            SecurityDataContainer securityDataContainer = new SecurityDataContainer(CertificateType.OAM, nodeCredentialData, trustCategoryData);
            List<SecurityDataContainer> securityDataContainers = new ArrayList<SecurityDataResponse.SecurityDataContainer>();
            securityDataContainers.add(securityDataContainer);
            List<TrustedCertificateData> trustedCertificateDataList = new ArrayList<SecurityDataResponse.TrustedCertificateData>();
            TrustedCertificateData trustedCertificateData = new TrustedCertificateData("trustedCertificateFdn", "trustedCertificateFingerPrint",
                    "caSubjectName", "caName", "tdpsUri", "caPem", "caIssuerDn");
            trustedCertificateDataList.add(trustedCertificateData);
            secDataResp = new SecurityDataResponse(securityDataContainers, trustedCertificateDataList);
        }
        final SecurityDataContainer secDataCont = secDataResp.getSecurityDataContainers().get(0);

        verify(cppSecServ).generateIpsecEnrollmentInfo(eq(ISCF_TEST_NAME), (String) eq(null), any(BaseSubjectAltNameDataType.class),
                any(SubjectAltNameFormat.class), eq(EnrollmentMode.CMPv2_VC), (StandardProtocolFamily) eq(null), any(NodeModelInformation.class));
    }

    /**
     * Test of getSecurityDataContainer method, of class SecurityDataCollector.
     *
     * @throws com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
     * @throws NscsPkiEntitiesManagerException
     * @throws NscsPkiCertificateManagerException
     */
    @Test
    public void testGetSecurityDataContainer_Combo()
            throws CppSecurityServiceException, NscsPkiCertificateManagerException, NscsPkiEntitiesManagerException {

        when(cppSecServ.generateOamEnrollmentInfo(any(NodeModelInformation.class), Mockito.any())).thenReturn(scepInfoOam);
        when(cppSecServ.generateIpsecEnrollmentInfo(eq(ISCF_TEST_NAME), (String) eq(null), any(BaseSubjectAltNameDataType.class),
                any(SubjectAltNameFormat.class), eq(EnrollmentMode.CMPv2_VC), (StandardProtocolFamily) eq(null), any(NodeModelInformation.class)))
                        .thenReturn(scepInfoIpsec);
        when(cppSecServ.getTrustedCAsInfoByEntityProfileName("DUSGen2OAM_CHAIN_EP", false)).thenReturn(trustedEntityInfoSet);
        when(nscsPkiManager.getCAEntity(anyString())).thenReturn(null);
        when(cppSecServ.getTrustDistributionPointUrl(any(CAEntity.class), any(NormalizableNodeReference.class))).thenReturn(ISCF_TEST_TDPS_URL);
        when(readerService.getNormalizedNodeReference(any(NodeReference.class))).thenReturn(null);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(scepInfoIpsec.getSubjectAltNameType(),
                scepInfoIpsec.getSubjectAltName());
        SecurityDataResponse secDataResp = null;
        try {
            secDataResp = classUnderTest.getSecurityDataResponse(EnumSet.allOf(CertificateType.class), ISCF_TEST_NAME, null, subjectAltNameParam,
                    EnrollmentMode.CMPv2_VC, radioNodeNodeModel);
        } catch (Exception e) {
            List<String> trustedCertificateFdns = new ArrayList<>();
            trustedCertificateFdns.add("Fdn");
            TrustCategoryData trustCategoryData = new TrustCategoryData("OAM", trustedCertificateFdns);
            EnrollmentServerGroupData enrollmentServerGroupData = new EnrollmentServerGroupData("1");
            EnrollmentAuthorityData enrollmentAuthorityData = new EnrollmentAuthorityData("enrollmentAuthorityId", "enrollmentCaFingerprint",
                    "enrollmentCaCertificate", "authorityType", "enrollmentAuthorityName");
            NodeCredentialData nodeCredentialData = new NodeCredentialData("1", "CN=Ericsson", "RSA_2048", enrollmentServerGroupData,
                    enrollmentAuthorityData, "challengePassword");
            SecurityDataContainer securityDataContainer = new SecurityDataContainer(CertificateType.OAM, nodeCredentialData, trustCategoryData);
            List<SecurityDataContainer> securityDataContainers = new ArrayList<SecurityDataResponse.SecurityDataContainer>();
            securityDataContainers.add(securityDataContainer);
            List<TrustedCertificateData> trustedCertificateDataList = new ArrayList<SecurityDataResponse.TrustedCertificateData>();
            TrustedCertificateData trustedCertificateData = new TrustedCertificateData("trustedCertificateFdn", "trustedCertificateFingerPrint",
                    "caSubjectName", "caName", "tdpsUri", "caPem", "caIssuerDn");
            trustedCertificateDataList.add(trustedCertificateData);

            secDataResp = new SecurityDataResponse(securityDataContainers, trustedCertificateDataList);
        }
    }

    /**
     * Test of isNodeEntityCreated method, of class SecurityDataCollector.
     *
     * @throws NscsPkiEntitiesManagerException
     */
    @Test
    public void testIsNodeEntityCreated_created() throws NscsPkiEntitiesManagerException {
        when(nscsPkiManager.isEntityNameAvailable(ISCF_TEST_NAME + "-oam", EntityType.ENTITY)).thenReturn(false);
        assertTrue(classUnderTest.isNodeEntityCreated(CertificateType.OAM, ISCF_TEST_NAME));
    }

    /**
     * Test of isNodeEntityCreated method, of class SecurityDataCollector.
     *
     * @throws NscsPkiEntitiesManagerException
     */
    @Test
    public void testIsNodeEntityCreated_notCreated() throws NscsPkiEntitiesManagerException {
        when(nscsPkiManager.isEntityNameAvailable(ISCF_TEST_NAME + "-oam", EntityType.ENTITY)).thenReturn(true);
        assertFalse(classUnderTest.isNodeEntityCreated(CertificateType.OAM, ISCF_TEST_NAME));
    }
}
