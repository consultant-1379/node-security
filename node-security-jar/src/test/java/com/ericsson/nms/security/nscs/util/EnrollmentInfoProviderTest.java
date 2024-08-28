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
package com.ericsson.nms.security.nscs.util;

import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentAuthorityData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerGroupData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.NodeCredentialData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.SecurityDataContainer;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustCategoryData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustedCertificateData;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.EnrollmentInfoProvider;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoService;
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoServiceException;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;

@RunWith(MockitoJUnitRunner.class)
public class EnrollmentInfoProviderTest {

    @InjectMocks
    EnrollmentInfoProvider enrollmentInfoProvider;

    @Mock
    Logger logger;

    @Mock
    NscsCMReaderService nscsCMReaderService;

    @Mock
    NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    EnrollmentInfoService enrollmentInfoService;

    @Mock
    private NormalizableNodeReference normNodeRef;

    @Mock
    CppSecurityServiceBean cppSecurityServiceBean;

    @Mock
    CAEntity caEntity;

    @Mock
    private NscsPkiEntitiesManagerIF nscsPkiEntitiesManager;

    private final String NODE_FDN = "VPP00001";
    private final NodeReference NODE = new NodeRef(NODE_FDN);
    private final String FINGER_PRINT = "fingerprint";
    private final String ID = "1";
    private final String AUTHORITY_TYPE = "CA";
    private final String ENROLLMENT_AUTHORITY_NAME = "RootCA";
    private final String SUBJECT_NAME = " ";
    private final String KEY_INFO = " ";
    private final String CHALLANGE_PASSWORD = "P@55w0rd";
    private SecurityDataResponse response;
    private TrustCategoryData trustCategories;
    private List<EnrollmentServerData> enrollmentServerDataList = new ArrayList<EnrollmentServerData>();
    private EnrollmentServerGroupData enrollmentServerGroupData = new EnrollmentServerGroupData(ID, enrollmentServerDataList);
    private EnrollmentAuthorityData enrollmentAuthorityData = new EnrollmentAuthorityData(ID, FINGER_PRINT, null, AUTHORITY_TYPE,
            ENROLLMENT_AUTHORITY_NAME);
    private NodeCredentialData nodeCredentials = new NodeCredentialData(ID, SUBJECT_NAME, KEY_INFO, enrollmentServerGroupData,
            enrollmentAuthorityData, CHALLANGE_PASSWORD);
    private NodeModelInformation modelInfo = new NodeModelInformation("E.1.239", ModelIdentifierType.MIM_VERSION, "ERBS");
    private SecurityDataContainer securityDataContainer = new SecurityDataContainer(CertificateType.IPSEC, nodeCredentials, trustCategories);

    private List<String> crlsUri = new ArrayList<>(Collections.singleton("http://192.168.0.155:8092/pki-cdps?ca_name=ENM_Infrastructure_CA&ca_cert_serialnumber=595931a2dc485aa0"));
    private TrustedCertificateData trustedCertificateData = new TrustedCertificateData(NODE_FDN, FINGER_PRINT, "1002", "CN=Sample_cert", "common",
            "pem", "issuerDn");
    private List<SecurityDataContainer> securityDataContainers = new ArrayList<SecurityDataContainer>();
    private List<TrustedCertificateData> trustedCertificateDatas = new ArrayList<TrustedCertificateData>();
    EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();

    @Before
    public void setUp() {
        String subjectAltName = "SAN";
        SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.IPV4;
        SubjectAltNameParam subjectAltNameParam = null;
        final NodeIdentifier nodeIdentifier = new NodeIdentifier("nodeFdn", null);
        if (subjectAltName != null && subjectAltNameFormat != null) {
            final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType("SAN");
            subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
        }
        enrollmentRequestInfo.setCommonName("CommonName");
        enrollmentRequestInfo.setCertType("OAM");
        enrollmentRequestInfo.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        enrollmentRequestInfo.setKeySize("RSA_2048");
        enrollmentRequestInfo.setNodeIdentifier(nodeIdentifier);
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltNameParam);
    }
    @Test
    public void test() throws EnrollmentInfoServiceException {

        securityDataContainers.add(securityDataContainer);
        trustedCertificateData.setCrlsUri(crlsUri);
        trustedCertificateDatas.add(trustedCertificateData);
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class),Mockito.any())).thenReturn(response);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, false);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfo() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {

        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        securityDataContainers.add(securityDataContainer);
        trustedCertificateData.setCrlsUri(crlsUri);
        trustedCertificateDatas.add(trustedCertificateData);
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        defaultEnrollmentCaTrustCategoryId.put("OAM", "oamCmpCaTrustCategory");
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByEmptyTrustCategory() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        securityDataContainers.add(securityDataContainer);
        trustedCertificateData.setCrlsUri(crlsUri);
        trustedCertificateDatas.add(trustedCertificateData);
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByEmptyTrustCategoryId() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        securityDataContainers.add(securityDataContainer);
        trustedCertificateData.setCrlsUri(crlsUri);
        trustedCertificateDatas.add(trustedCertificateData);
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        defaultEnrollmentCaTrustCategoryId.put("OAM", "");
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByNullTrustCategoryId() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        securityDataContainers.add(securityDataContainer);
        trustedCertificateData.setCrlsUri(crlsUri);
        trustedCertificateDatas.add(trustedCertificateData);
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(null);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByNullFingerPrint() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        prepareSecurityDataContainer(null,"");
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        defaultEnrollmentCaTrustCategoryId.put("OAM", "oamCmpCaTrustCategory");
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByEmptyFingerPrint() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        securityDataContainer.getNodeCredentials().getEnrollmentAuthority().getEnrollmentCaFingerprint();
        prepareSecurityDataContainer("","ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=2");
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        defaultEnrollmentCaTrustCategoryId.put("OAM", "oamCmpCaTrustCategory");
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByNAFingerPrint() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        prepareSecurityDataContainer("NA",null);
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        defaultEnrollmentCaTrustCategoryId.put("OAM", "oamCmpCaTrustCategory");
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByNonMatchedCaCertificate() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        prepareSecurityDataContainer(null,"ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=2");
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        defaultEnrollmentCaTrustCategoryId.put("OAM", "oamCmpCaTrustCategory");
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    @Test
    public void testGetEnrollmentInfoByMatchedCaCertificate() throws EnrollmentInfoServiceException, CppSecurityServiceException, NscsPkiEntitiesManagerException {
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(NODE)).thenReturn(normNodeRef);
        Mockito.when(normNodeRef.getNeType()).thenReturn("vDU");
        Mockito.when(cppSecurityServiceBean.getTrustDistributionPointUrl(caEntity, normNodeRef)).thenReturn("http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");
        Mockito.when(nscsPkiEntitiesManager.getCAEntity(toString())).thenReturn(caEntity);
        prepareSecurityDataContainer(null,"VPP00001");
        response = new SecurityDataResponse(securityDataContainers, trustedCertificateDatas);
        Mockito.when(nscsCMReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(modelInfo);
        Map<String,String> defaultEnrollmentCaTrustCategoryId = new HashMap<>();
        defaultEnrollmentCaTrustCategoryId.put("OAM", "oamCmpCaTrustCategory");
        Mockito.when(enrollmentInfoService.generateSecurityDataOam(Mockito.any(NodeModelInformation.class), Mockito.any())).thenReturn(response);
        Mockito.when(nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(Mockito.any(NodeModelInformation.class))).thenReturn(defaultEnrollmentCaTrustCategoryId);
        final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        Assert.assertNotNull(enrollmentInfo);
        ;
    }

    private void prepareSecurityDataContainer(String fingerPrint, String enrollmentCaCertificate){
        enrollmentAuthorityData = new EnrollmentAuthorityData(ID, fingerPrint, enrollmentCaCertificate, AUTHORITY_TYPE,
                 ENROLLMENT_AUTHORITY_NAME);
        nodeCredentials = new NodeCredentialData(ID, SUBJECT_NAME, KEY_INFO, enrollmentServerGroupData,
                enrollmentAuthorityData, CHALLANGE_PASSWORD);
        securityDataContainer = new SecurityDataContainer(CertificateType.IPSEC, nodeCredentials, trustCategories);
        securityDataContainers.add(securityDataContainer);
        trustedCertificateData.setCrlsUri(crlsUri);
        trustedCertificateDatas.add(trustedCertificateData);
    }
}