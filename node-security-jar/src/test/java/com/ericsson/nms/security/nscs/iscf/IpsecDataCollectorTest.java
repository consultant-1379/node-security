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
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

import java.util.*;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

import static org.mockito.Matchers.eq;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class IpsecDataCollectorTest extends DataCollectorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(IpsecDataCollector.class);

    @Spy
    private IscfConfigurationBean config;

    @Spy
    private IscfEncryptor iscfEncryptor;

    @Spy
    private RicGenerator ricGenerator;

    @Mock
    private CppSecurityService cpp;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @InjectMocks
    IpsecDataCollector beanUnderTest;

    @Before
    public void setUp() throws Exception {
        doReturn(ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)).when(ricGenerator).generateSalt();
        doReturn(ISCF_RIC_STRING).when(ricGenerator).generateRIC();
        doReturn(generateTestScepInfo(CertificateType.OAM))
                .when(cpp)
                .generateIpsecEnrollmentInfo(Mockito.anyString(), Mockito.anyString(), Mockito.any(SubjectAltNameStringType.class), 
                        Mockito.any(SubjectAltNameFormat.class),
                        Mockito.any(EnrollmentMode.class), 
                        Mockito.any(NodeModelInformation.class))
                ;
        doReturn(generateTestTrustStoreInfo(TrustedCertCategory.IPSEC))
                .when(cpp)
                .getTrustStoreForNode(eq(TrustedCertCategory.IPSEC), Mockito.any(NodeRef.class), eq(false))
                ;
        doReturn(generateTestTrustStoreInfo(TrustedCertCategory.IPSEC))
                .when(cpp)
                .getTrustStoreForAP(eq(TrustedCertCategory.IPSEC), Mockito.anyString(),
                        Mockito.any(NodeModelInformation.class))
                ;
        doReturn("MOCK_ENCRYPTED_DATA".getBytes(IscfConstants.UTF8_CHARSET))
                .when(iscfEncryptor)
                .encrypt(
                        Mockito.any(byte[].class),
                        Mockito.any(byte[].class),
                        Mockito.any(byte[].class)
                );
//        doReturn(false)
//                .when(nscsCapabilityModelService)
//                .isSynchronousEnrollmentSupported(Mockito.any(NodeModelInformation.class))
//                ;
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseNullLogicalName() throws Exception {
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("Logical Name cannot be null");
        NodeAIData data = generateBasicNodeAIData();
        data.setLogicalName(null);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseInvalidLogicalName() throws Exception {
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("Logical Name must contain an alphanumeric character");
        NodeAIData data = generateBasicNodeAIData();
        data.setLogicalName("   ");
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    @Ignore
    public void testValidateNodeAIDataNegativeCaseNullUserLabel() throws Exception {
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("User Label cannot be null");
        NodeAIData data = generateBasicNodeAIData();
        data.setIpsecUserLabel(null);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    @Ignore
    public void testValidateNodeAIDataNegativeCaseInvalidUserLabel() throws Exception {
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("User Label must contain an alphanumeric character");
        NodeAIData data = generateBasicNodeAIData();
        data.setIpsecUserLabel("   ");
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseUserLabelTooLong() throws Exception {
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        NodeAIData data = getIpsecNodeAIData(wantedIpsecAreas);
        StringBuilder  userLabel = new StringBuilder();   
        for (int i = 0; i < 150; i++)
            userLabel.append((i % 26) + 0x41);
        data.setIpsecUserLabel(userLabel.toString());
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("User Label too long");
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    @Ignore
    public void testValidateIpsecNodeAIDataNegativeCaseSubjectAltNameNull() throws Exception {
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        NodeAIData data = getIpsecNodeAIData(wantedIpsecAreas);
//        NodeAIData data = generateBasicNodeAIData();
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("Property \"subjectAltName\" cannot be null string");
        data.setSubjectAltName(null);
        beanUnderTest.validateNodeAIData(data);
    }

//    @Test
//    public void testValidateIpsecNodeAIDataNegativeCaseSubjectAltNameEmptyString() throws Exception {
//        NodeAIData data = generateBasicNodeAIData();
//        exception.expect(InvalidNodeAIDataException.class);
//        exception.expectMessage("Property \"subjectAltName\" cannot be null or empty string");
//        data.setSubjectAltName(new SubjectAltNameStringType(""));
//        beanUnderTest.validateNodeAIData(data);
//    }

    @Test
    public void testValidateIpsecNodeAIDataNegativeCaseIpsecAreasListEmpty() throws Exception {
        NodeAIData data = generateBasicNodeAIData();
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("At least one of IP Security area must be present");
        data.setSubjectAltName(new SubjectAltNameStringType("someName"));
        data.getIpsecAreas().clear();
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testGetNodeAIDataBasicDataRetrievalIpsecTrafficOnly() throws Exception {
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);

        NodeAIData result = getIpsecNodeAIData(wantedIpsecAreas);
        String expEnrollmentUrl = ISCF_TEST_ENROLLMENT_URI;
        String actualEnrollmentUrl = result.getIpsecEnrollmentDataDto().getEnrollmentData().getEnrollmentServerURL();
        String expFingerprint = calculateEnrollmentFingerprint(ISCF_TEST_FINGERPRINT_CONTENT);
        String actualFingerprint = result.getIpsecEnrollmentDataDto().getEnrollmentData().getCAFingerprint();
        String expDN = ISCF_TEST_DN;
        String actualDN = result.getIpsecEnrollmentDataDto().getEnrollmentData().getDistinguishedName();
        byte[] actualOtp = result
                .getIpsecEnrollmentDataDto()
                .getEnrollmentData()
                .getDataChallengePassword()
                .getEncryptedContent()
                .getValue()
                ;
        byte[] expOtp = ISCF_TEST_OTP_STR.getBytes();

        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expFingerprint, actualFingerprint);
        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expDN, actualDN);
        if(expOtp.length == actualOtp.length) {
            assertFalse(Arrays.equals(expOtp, actualOtp));
        }
    }


    @Test
    public void testGetNodeAIDataBasicDataRetrievalIpsecOmOnly() throws Exception {
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);

        NodeAIData result = getIpsecNodeAIData(wantedIpsecAreas);

        String expEnrollmentUrl = ISCF_TEST_ENROLLMENT_URI;
        String actualEnrollmentUrl = result.getIpsecEnrollmentDataDto().getEnrollmentData().getEnrollmentServerURL();
        String expFingerprint = calculateEnrollmentFingerprint(ISCF_TEST_FINGERPRINT_CONTENT);
        String actualFingerprint = result.getIpsecEnrollmentDataDto().getEnrollmentData().getCAFingerprint();
        String expDN = ISCF_TEST_DN;
        String actualDN = result.getIpsecEnrollmentDataDto().getEnrollmentData().getDistinguishedName();
        byte[] actualOtp = result
                .getIpsecEnrollmentDataDto()
                .getEnrollmentData()
                .getDataChallengePassword()
                .getEncryptedContent()
                .getValue()
                ;
        byte[] expOtp = ISCF_TEST_OTP_STR.getBytes();

        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expFingerprint, actualFingerprint);
        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expDN, actualDN);
        if(expOtp.length == actualOtp.length) {
            assertFalse(Arrays.equals(expOtp, actualOtp));
        }
    }

    @Test
    public void testGetNodeAIDataBasicDataRetrievalIpsecOmAndTransport() throws Exception {
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);

        NodeAIData result = getIpsecNodeAIData(wantedIpsecAreas);

        String expEnrollmentUrl = ISCF_TEST_ENROLLMENT_URI;
        String actualEnrollmentUrl = result.getIpsecEnrollmentDataDto().getEnrollmentData().getEnrollmentServerURL();
        String expFingerprint = calculateEnrollmentFingerprint(ISCF_TEST_FINGERPRINT_CONTENT);
        String actualFingerprint = result.getIpsecEnrollmentDataDto().getEnrollmentData().getCAFingerprint();
        String expDN = ISCF_TEST_DN;
        String actualDN = result.getIpsecEnrollmentDataDto().getEnrollmentData().getDistinguishedName();
        byte[] actualOtp = result
                .getIpsecEnrollmentDataDto()
                .getEnrollmentData()
                .getDataChallengePassword()
                .getEncryptedContent()
                .getValue()
                ;
        byte[] expOtp = ISCF_TEST_OTP_STR.getBytes();

        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expFingerprint, actualFingerprint);
        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expDN, actualDN);
        if(expOtp.length == actualOtp.length) {
            assertFalse(Arrays.equals(expOtp, actualOtp));
        }
    }

    @Test
    public void testGetNodeAIDataBasicDataRetrievalIpsecOmOnlyIPV4AltName() throws Exception {
        NodeAIData result = getIpsecNodeAIData(SubjectAltNameFormat.IPV4, 
                new SubjectAltNameStringType(ISCF_TEST_IPV4_SUBJECT_ALT_NAME));

        String expEnrollmentUrl = ISCF_TEST_ENROLLMENT_URI;
        String actualEnrollmentUrl = result.getIpsecEnrollmentDataDto().getEnrollmentData().getEnrollmentServerURL();
        String expFingerprint = calculateEnrollmentFingerprint(ISCF_TEST_FINGERPRINT_CONTENT);
        String actualFingerprint = result.getIpsecEnrollmentDataDto().getEnrollmentData().getCAFingerprint();
        String expDN = ISCF_TEST_DN;
        String actualDN = result.getIpsecEnrollmentDataDto().getEnrollmentData().getDistinguishedName();
        String expSubjectAltNameVal = ((SubjectAltNameStringType)result.getSubjectAltName()).toString();
        SubjectAltNameFormat expSubjectAltNameFormat = result.getSubjectAltNameFormat();
        byte[] actualOtp = result
                .getIpsecEnrollmentDataDto()
                .getEnrollmentData()
                .getDataChallengePassword()
                .getEncryptedContent()
                .getValue()
                ;
        byte[] expOtp = ISCF_TEST_OTP_STR.getBytes();

        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expFingerprint, actualFingerprint);
        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expDN, actualDN);
        assertEquals(expSubjectAltNameVal, ISCF_TEST_IPV4_SUBJECT_ALT_NAME);
        assertEquals(expSubjectAltNameFormat, SubjectAltNameFormat.IPV4);

        if(expOtp.length == actualOtp.length) {
            assertFalse(Arrays.equals(expOtp, actualOtp));
        }
    }

    @Test
    public void testGetNodeAIDataBasicDataRetrievalIpsecOmOnlyIPV6AltName() throws Exception {
        NodeAIData result = getIpsecNodeAIData(SubjectAltNameFormat.IPV6, 
                new SubjectAltNameStringType(ISCF_TEST_IPV6_SUBJECT_ALT_NAME));
        
        String expEnrollmentUrl = ISCF_TEST_ENROLLMENT_URI;
        String actualEnrollmentUrl = result.getIpsecEnrollmentDataDto().getEnrollmentData().getEnrollmentServerURL();
        String expFingerprint = calculateEnrollmentFingerprint(ISCF_TEST_FINGERPRINT_CONTENT);
        String actualFingerprint = result.getIpsecEnrollmentDataDto().getEnrollmentData().getCAFingerprint();
        String expDN = ISCF_TEST_DN;
        String actualDN = result.getIpsecEnrollmentDataDto().getEnrollmentData().getDistinguishedName();
        String expSubjectAltNameVal = ((SubjectAltNameStringType)result.getSubjectAltName()).toString();
        SubjectAltNameFormat expSubjectAltNameFormat = result.getSubjectAltNameFormat();
        byte[] actualOtp = result
                .getIpsecEnrollmentDataDto()
                .getEnrollmentData()
                .getDataChallengePassword()
                .getEncryptedContent()
                .getValue()
                ;
        byte[] expOtp = ISCF_TEST_OTP_STR.getBytes();

        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expFingerprint, actualFingerprint);
        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expDN, actualDN);
        assertEquals(expSubjectAltNameVal, ISCF_TEST_IPV6_SUBJECT_ALT_NAME);
        assertEquals(expSubjectAltNameFormat, SubjectAltNameFormat.IPV6);

        if(expOtp.length == actualOtp.length) {
            assertFalse(Arrays.equals(expOtp, actualOtp));
        }
    }

    @Test
    public void testGetNodeAIDataIpsecCertSpecs() throws Exception {
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        NodeAIData result = getIpsecNodeAIData(wantedIpsecAreas);
        assertNotNull("IPSec cert specs should not be null", result.getIpsecCertSpecs());
        assertFalse("IPSec cert specs should not be empty", result.getIpsecCertSpecs().isEmpty());
    }

    private NodeAIData getIpsecNodeAIData(Set<IpsecArea> wantedIpsecAreas) throws Exception {
        NodeAIData result = beanUnderTest.getNodeAIData(ISCF_TEST_FDN,
                ISCF_TEST_LOGICAL_NAME,
                ISCF_TEST_USER_LABEL,
                new SubjectAltNameStringType(ISCF_TEST_IPV4_SUBJECT_ALT_NAME),
                SubjectAltNameFormat.IPV4,
                wantedIpsecAreas,
                EnrollmentMode.SCEP,
                ISCF_CPP_MODEL_INFO,
                ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)
        );
        return result;
    }

    private NodeAIData getIpsecNodeAIData(SubjectAltNameFormat subjectAltNameFormat,
                        BaseSubjectAltNameDataType subjectAltNameData) throws Exception {
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        NodeAIData result = beanUnderTest.getNodeAIData(ISCF_TEST_FDN,
                ISCF_TEST_LOGICAL_NAME,
                ISCF_TEST_USER_LABEL,
                subjectAltNameData,
                subjectAltNameFormat,
                wantedIpsecAreas,
                EnrollmentMode.SCEP,
                ISCF_CPP_MODEL_INFO,
                ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)
        );
        return result;
    }

}
