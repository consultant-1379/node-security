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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityLevelDataCollectorTest extends DataCollectorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(SecurityLevelDataCollector.class);

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
    SecurityLevelDataCollector beanUnderTest;

    @Before
    public void setUp() throws Exception {
        doReturn(ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)).when(ricGenerator).generateSalt();
        doReturn(ISCF_RIC_STRING).when(ricGenerator).generateRIC();
        doReturn(generateTestEnrollmentInfo(OAM_ENTITY_PROFILE_NAME))
                .when(cpp)
                .generateOamEnrollmentInfo(Mockito.anyString(), Mockito.anyString(), Mockito.any(SubjectAltNameStringType.class), 
                        Mockito.any(SubjectAltNameFormat.class),
                        Mockito.any(EnrollmentMode.class), Mockito.any(NodeModelInformation.class))
                ;
        doReturn(generateTestTrustStoreInfo(TrustedCertCategory.CORBA_PEERS))
                .when(cpp)
                .getTrustStoreForNode(eq(TrustedCertCategory.CORBA_PEERS), Mockito.any(NodeRef.class), eq(false))
                ;
        doReturn(generateTestTrustStoreInfo(TrustedCertCategory.CORBA_PEERS))
                .when(cpp)
                .getTrustStoreForAP(eq(TrustedCertCategory.CORBA_PEERS), Mockito.anyString(),
                        Mockito.any(NodeModelInformation.class))
                ;
         doReturn("MOCK_ENCRYPTED_DATA".getBytes(IscfConstants.UTF8_CHARSET))
                .when(iscfEncryptor)
                .encrypt(
                        Mockito.any(byte[].class),
                        Mockito.any(byte[].class),
                        Mockito.any(byte[].class)
                );
        doReturn(false)
                .when(nscsCapabilityModelService)
                .isSynchronousEnrollmentSupported(ISCF_CPP_MODEL_INFO)
                ;
         doReturn(true)
                .when(nscsCapabilityModelService)
                .isSynchronousEnrollmentSupported(ISCF_COMECIM_MODEL_INFO)
                ;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testValidateNodeAIDataPositiveCaseEqualSecurityLevels() {
        final NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        try {
            beanUnderTest.validateNodeAIData(data);
        } catch (final SecurityLevelNotSupportedException ex) {
            fail("Unexpected validation scenario: " + ex.getMessage());
        }
    }

    @Test
    public void testValidateNodeAIDataPositiveCaseDifferentSecurityLevels() {
        final NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_1);
        try {
            assertTrue(beanUnderTest.validateNodeAIData(data));
        } catch (final SecurityLevelNotSupportedException ex) {
            fail("Validation exception: " + ex.getMessage());
        }
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseNullLogicalName() throws Exception {
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("Logical Name cannot be null");
        final NodeAIData data = generateBasicNodeAIData();
        data.setLogicalName(null);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseInvalidLogicalName() throws Exception {
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("Logical Name must contain an alphanumeric character");
        final NodeAIData data = generateBasicNodeAIData();
        data.setLogicalName("   ");
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseUnsupportedWantedSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level invalid");
        final NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_NOT_SUPPORTED);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_1);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseUnsupportedMinimumSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Minimum Security Level invalid");
        final NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_NOT_SUPPORTED);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseWantedSecurityLevelLessThanMinimumSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Minimum Security Level cannot be greater than Wanted Security Level");
        final NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_1);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseNullWantedSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level and Minimum Security Level cannot be null");
        final NodeAIData nodeData = beanUnderTest.getNodeAIData(null, SecurityLevel.LEVEL_1, 
                ISCF_TEST_FDN, ISCF_TEST_LOGICAL_NAME, null, ISCF_CPP_MODEL_INFO, "".getBytes());
        beanUnderTest.validateNodeAIData(nodeData);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseNullMinimumSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level and Minimum Security Level cannot be null");
        final NodeAIData nodeData = beanUnderTest.getNodeAIData(SecurityLevel.LEVEL_1, null, ISCF_TEST_FDN, 
                ISCF_TEST_LOGICAL_NAME, null, ISCF_CPP_MODEL_INFO, "".getBytes());
        beanUnderTest.validateNodeAIData(nodeData);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseSecurityLevelBothNull() throws Exception {
        final NodeAIData data = generateBasicNodeAIData();
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level and Minimum Security Level cannot be null");
        data.setMinimumSecLevel(null);
        data.setWantedSecLevel(null);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testGetNodeAIDataNegativeCaseNullWantedSecurityLevel() throws Exception {
        final SecurityLevel wantedSecurityLevel = SecurityLevel.LEVEL_2;
        final SecurityLevel minimumSecurityLevel = SecurityLevel.LEVEL_2;
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final NodeAIData expResult = generateBasicNodeAIData();
        expResult.setWantedSecLevel(wantedSecurityLevel);
        expResult.setMinimumSecLevel(minimumSecurityLevel);
        final NodeAIData result = beanUnderTest.getNodeAIData(
                wantedSecurityLevel,
                minimumSecurityLevel,
                ISCF_TEST_FDN,
                ISCF_TEST_LOGICAL_NAME,
                wantedEnrollmentMode,
                ISCF_CPP_MODEL_INFO,
                ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)
        );
        assertEquals(expResult.getWantedSecLevel(), result.getWantedSecLevel());
    }

    @Test
    public void testGetNodeAIDataBasicDataRetrievalCpp() throws Exception {
        final SecurityLevel wantedSecurityLevel = SecurityLevel.LEVEL_2;
        final SecurityLevel minimumSecurityLevel = SecurityLevel.LEVEL_2;
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final NodeAIData result = beanUnderTest.getNodeAIData(
                wantedSecurityLevel,
                minimumSecurityLevel,
                ISCF_TEST_FDN,
                ISCF_TEST_LOGICAL_NAME,
                wantedEnrollmentMode,
                ISCF_CPP_MODEL_INFO,
                ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)
        );
        final String expEnrollmentUrl = ISCF_TEST_ENROLLMENT_URI;
        final String actualEnrollmentUrl = result.getEnrollmentDto().getEnrollmentData().getEnrollmentServerURL();
        final String expFingerprint = calculateEnrollmentFingerprint(ISCF_TEST_FINGERPRINT_CONTENT);
        final String actualFingerprint = result.getEnrollmentDto().getEnrollmentData().getCAFingerprint();
        final String expDN = ISCF_TEST_DN;
        final String actualDN = result.getEnrollmentDto().getEnrollmentData().getDistinguishedName();
        final byte[] actualOtp = result
                .getEnrollmentDto()
                .getEnrollmentData()
                .getDataChallengePassword()
                .getEncryptedContent()
                .getValue()
                ;
        final byte[] expOtp = ISCF_TEST_OTP_STR.getBytes();
        
        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expFingerprint, actualFingerprint);
        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertEquals(expDN, actualDN);
        if(expOtp.length == actualOtp.length) {
            assertFalse(Arrays.equals(expOtp, actualOtp));
        }
    }

    @Test
    public void testGetNodeAIDataBasicDataRetrievalComEcim() throws Exception {
        final SecurityLevel wantedSecurityLevel = SecurityLevel.LEVEL_2;
        final SecurityLevel minimumSecurityLevel = SecurityLevel.LEVEL_2;
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final NodeAIData result = beanUnderTest.getNodeAIData(
                wantedSecurityLevel,
                minimumSecurityLevel,
                ISCF_TEST_FDN,
                ISCF_TEST_LOGICAL_NAME,
                wantedEnrollmentMode,
                ISCF_COMECIM_MODEL_INFO,
                ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)
        );
        final String expEnrollmentUrl = ISCF_TEST_ENROLLMENT_URI + "/" + CppSecurityService.ENROLLMENT_URL_ECIM_SUFFIX;
        final String actualEnrollmentUrl = result.getEnrollmentDto().getEnrollmentData().getEnrollmentServerURL();
        final String expFingerprint = calculateEnrollmentFingerprint(ISCF_TEST_FINGERPRINT_CONTENT);
        final String actualFingerprint = result.getEnrollmentDto().getEnrollmentData().getCAFingerprint();
        final String expDN = ISCF_TEST_DN;
        final String actualDN = result.getEnrollmentDto().getEnrollmentData().getDistinguishedName();
        final byte[] actualOtp = result
                .getEnrollmentDto()
                .getEnrollmentData()
                .getDataChallengePassword()
                .getEncryptedContent()
                .getValue()
                ;
        final byte[] expOtp = ISCF_TEST_OTP_STR.getBytes();
        
//        assertEquals(expEnrollmentUrl, actualEnrollmentUrl);
        assertTrue(expEnrollmentUrl.startsWith(actualEnrollmentUrl));
        assertEquals(expFingerprint, actualFingerprint);
        assertEquals(expDN, actualDN);
        if(expOtp.length == actualOtp.length) {
            assertFalse(Arrays.equals(expOtp, actualOtp));
        }
    }

    @Test
    public void testGetNodeAIDataSecLevelCertSpecs() throws Exception {
        final SecurityLevel wantedSecurityLevel = SecurityLevel.LEVEL_2;
        final SecurityLevel minimumSecurityLevel = SecurityLevel.LEVEL_2;
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final NodeAIData result = beanUnderTest.getNodeAIData(
                wantedSecurityLevel,
                minimumSecurityLevel,
                ISCF_TEST_FDN,
                ISCF_TEST_LOGICAL_NAME,
                wantedEnrollmentMode,
                ISCF_CPP_MODEL_INFO,
                ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)
        );
        assertNotNull("Security Level cert specs should not be null", result.getSecLevelCertSpecs());
        assertFalse("Security Level cert specs should not be empty", result.getSecLevelCertSpecs().isEmpty());
    }
}
