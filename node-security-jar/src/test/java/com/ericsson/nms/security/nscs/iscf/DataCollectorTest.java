/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.iscf;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;

/**
 * Class containing methods common to both Security Level data collection tests
 * and IPSec data collection tests
 *
 * @author ealemca
 */
public class DataCollectorTest extends IscfTest {
	
    protected NodeAIData generateBasicNodeAIData() {
        NodeAIData nodeData = new NodeAIData();
        nodeData.setFdn(ISCF_TEST_FDN);
        nodeData.setRic(ISCF_RIC_STRING.getBytes());
        nodeData.setLogicalName(ISCF_TEST_LOGICAL_NAME);
        nodeData.setLogonServerAddress(ISCF_TEST_LOGON_URI);
        nodeData.setIpsecUserLabel(ISCF_TEST_USER_LABEL);
        return nodeData;
    }

    protected ScepEnrollmentInfo generateTestScepInfo(CertificateType certType) {
        EntityInfo entityInfo = new EntityInfo();
        if (certType.equals(CertificateType.OAM))
            entityInfo.setName(ISCF_TEST_NAME + "-oam");
        else
            entityInfo.setName(ISCF_TEST_NAME + "-ipsec");
        
        final Map<SubjectFieldType, String> subjMap = new HashMap<>();
        subjMap.put(SubjectFieldType.COMMON_NAME, ISCF_TEST_NAME);
        Subject subject = new Subject();
        
        SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue(ISCF_TEST_NAME);

        List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);
        subject.setSubjectFields(entSubjectFieldList);
        entityInfo.setSubject(subject);
        
        SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
        subjectAltNameField.setType(SubjectAltNameFieldType.IP_ADDRESS);        
        SubjectAltNameString  subjectAltNameValueString = new SubjectAltNameString();
        subjectAltNameValueString.setValue(ISCF_TEST_IPV4_SUBJECT_ALT_NAME);
        subjectAltNameField.setValue(subjectAltNameValueString);
        List<SubjectAltNameField> subjectAltNameValueList = new ArrayList<>();
        subjectAltNameValueList.add(subjectAltNameField);        
        SubjectAltName subjectAltNameValues = new SubjectAltName();
        subjectAltNameValues.setSubjectAltNameFields(subjectAltNameValueList);
        entityInfo.setSubjectAltName(subjectAltNameValues);
        
        entityInfo.setOTP(ISCF_TEST_OTP_STR);
        Entity ee = new Entity();
        ee.setType(EntityType.ENTITY);
//        ee.setOTP(ISCF_TEST_OTP_STR);
//        ee.setEntityProfileName(profile);
        entityInfo.setStatus(EntityStatus.NEW);
        ee.setEntityInfo(entityInfo);
        EntityProfile entityProfile = new EntityProfile();
        entityProfile.setName("DUSGen2OAM_CHAIN_EP");
        ee.setEntityProfile(entityProfile);
      
        EnrollmentInfo serverInfo = new EnrollmentInfo();
        serverInfo.setEnrollmentURL(ISCF_TEST_ENROLLMENT_URI);
        ScepEnrollmentInfo scep = null;
        try {
            scep = new ScepEnrollmentInfoImpl(
                    ee,
                    ISCF_TEST_ENROLLMENT_URI,
                    null,
                    DigestAlgorithm.SHA1,
                    ISCT_TEST_ROLLBACK_TIMEOUT,
                    "challengePWD", 
                    ISCF_TEST_KEYSIZE_STR,
                    EnrollmentMode.CMPv2_VC,
                    null, null);
        } catch (NoSuchAlgorithmException | CertificateEncodingException ex) {
            Logger.getLogger(DataCollectorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((scep != null) && (scep.getServerCertFingerPrint() == null))
            scep.setServerCertFingerPrint(ISCF_TEST_FINGERPRINT_CONTENT.getBytes());
        if ((scep != null) && (scep.getPkiRootCertFingerPrint() == null))
            scep.setPkiRootCertFingerPrint(ISCF_TEST_ROOT_CA_FINGERPRINT_CONTENT.getBytes());
        return scep;
    }

    protected ScepEnrollmentInfo generateTestEnrollmentInfo(final String profile
                                       /* ,final EnrollmentInfo.EnrollmentServer server */) {
        ScepEnrollmentInfo scepInfo = generateTestScepInfo(CertificateType.OAM);
        scepInfo.setKSandEMSupported(true);
        scepInfo.setCertificateAuthorityDnSupported(true);
        scepInfo.setEnrollmentProtocol("0");
        scepInfo.setKeySize("0");
        scepInfo.setCertificateAuthorityDn("SKYFALL_TMP");
        return scepInfo;
    }

    protected TrustStoreInfo generateTestTrustStoreInfo(final TrustedCertCategory category) throws Exception {
        Set<CertSpec> certSpecs = new HashSet<>();
        List<SmrsAccountInfo> accountInfos = new ArrayList<>();
        certSpecs.add(generateCertSpec());
        certSpecs.add(generateCertSpec());
        accountInfos.add(generateAccountInfo());
        TrustStoreInfo trust = new TrustStoreInfo(
                category,
                certSpecs,
                accountInfos, 
                DigestAlgorithm.SHA1
        );
        return trust;
    }

    protected CertSpec generateCertSpec() throws Exception {
        CertSpec mockSpec = Mockito.mock(CertSpec.class);
//        PKIX509Certificate mockHolder = Mockito.mock(PKIX509Certificate.class);
        X509Certificate mockCert = Mockito.mock(X509Certificate.class);

        doReturn("dummyFingerprint".getBytes()).when(mockSpec).getFingerPrint();
        doReturn(ISCF_TEST_SERIAL_NUMBER).when(mockSpec).getSerial();
        doReturn(mockCert).when(mockSpec).getCertHolder();
//        doReturn(mockCert).when(mockHolder).getCertificate();
        doReturn("dummyCert".getBytes()).when(mockCert).getEncoded();

        return mockSpec;
    }

    protected SmrsAccountInfo generateAccountInfo() {
        return Mockito.mock(SmrsAccountInfo.class);
    }

    protected String calculateEnrollmentFingerprint(String fingerprint) throws Exception{
        byte[] fingerprintBytes = fingerprint.getBytes();
        String fingerprintContent = CertSpec.bytesToHex(fingerprintBytes);
        return "SHA1 Fingerprint=" + fingerprintContent;
    }

}
