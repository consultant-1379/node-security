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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
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
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
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
public class CombinedDataCollectorTest extends DataCollectorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(CombinedDataCollector.class);

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
    CombinedDataCollector beanUnderTest;

    @Before
    public void setUp() throws Exception {
        doReturn(ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)).when(ricGenerator).generateSalt();
        doReturn(ISCF_RIC_STRING).when(ricGenerator).generateRIC();
        doReturn(generateTestScepInfo(CertificateType.OAM))
                .when(cpp)
                .generateIpsecEnrollmentInfo(Mockito.anyString(), Mockito.anyString(), Mockito.any(BaseSubjectAltNameDataType.class), Mockito.any(SubjectAltNameFormat.class),
                        Mockito.any(EnrollmentMode.class), Mockito.any(NodeModelInformation.class))
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
        doReturn(generateTestEnrollmentInfo(OAM_ENTITY_PROFILE_NAME))
                .when(cpp)
                .generateOamEnrollmentInfo(Mockito.anyString(), Mockito.anyString(), Mockito.any(BaseSubjectAltNameDataType.class), Mockito.any(SubjectAltNameFormat.class),
                        Mockito.any(EnrollmentMode.class), 
                        Mockito.any(NodeModelInformation.class))
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
        doReturn(false)
                .when(nscsCapabilityModelService)
                .isSynchronousEnrollmentSupported(Mockito.any(NodeModelInformation.class))
                ;
        doReturn(false)
                .when(nscsCapabilityModelService)
                .isSynchronousEnrollmentSupported(Mockito.any(NodeModelInformation.class))
                ;
//        doReturn(1024).when(config).getCipherIterationCount();
//        doReturn(128).when(config).getCipherKeySize();
//        doReturn(128).when(config).getCipherInitialisationVectorSize();
        doReturn("MOCK_ENCRYPTED_DATA".getBytes(IscfConstants.UTF8_CHARSET))
                .when(iscfEncryptor)
                .encrypt(
                        Mockito.any(byte[].class),
                        Mockito.any(byte[].class),
                        Mockito.any(byte[].class)
                );
    }

    @Test
    public void testGetNodeAIDataCertSpecs() throws Exception {
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        final NodeAIData result = getCombinedNodeAIData(wantedIpsecAreas);
        assertNotNull("Security Level cert specs should not be null", result.getSecLevelCertSpecs());
        assertFalse("Security Level cert specs should not be empty", result.getSecLevelCertSpecs().isEmpty());
        assertNotNull("IPSec cert specs should not be null", result.getIpsecCertSpecs());
        assertFalse("IPSec cert specs should not be empty", result.getIpsecCertSpecs().isEmpty());
    }

    @Test
    public void testValidateNodeAIDataPositiveCaseEqualSecurityLevels() throws Exception {
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        try {
            beanUnderTest.validateNodeAIData(data);
        } catch (final SecurityLevelNotSupportedException ex) {
            fail("Unexpected validation scenario: " + ex.getMessage());
        }
    }

    @Test
    public void testValidateNodeAIDataPositiveCaseDifferentSecurityLevels() throws Exception {
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
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
    @Ignore
    public void testValidateNodeAIDataNegativeCaseNullUserLabel() throws Exception {
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("User Label cannot be null");
        final NodeAIData data = generateBasicNodeAIData();
        data.setIpsecUserLabel(null);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseUserLabelTooLong() throws Exception {
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
        final StringBuilder  userLabel = new StringBuilder();   
        for (int i = 0; i < 150; i++)
            userLabel.append((i % 26) + 0x41);
        data.setIpsecUserLabel(userLabel.toString());
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("User Label too long");
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseUnsupportedWantedSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level invalid");
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
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
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_3);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseNullWantedSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level and Minimum Security Level cannot be null");
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData nodeData = beanUnderTest.getNodeAIData(ISCF_TEST_FDN, ISCF_TEST_LOGICAL_NAME, null, SecurityLevel.LEVEL_1,
        		"", new SubjectAltNameStringType(""), SubjectAltNameFormat.IPV4, wantedIpsecAreas,
        		EnrollmentMode.SCEP,
        		ISCF_CPP_MODEL_INFO,
        		"".getBytes());
        beanUnderTest.validateNodeAIData(nodeData);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseNullMinimumSecurityLevel() throws Exception {
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level and Minimum Security Level cannot be null");
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData nodeData = beanUnderTest.getNodeAIData(ISCF_TEST_FDN, ISCF_TEST_LOGICAL_NAME, SecurityLevel.LEVEL_1, null,
                "", new SubjectAltNameStringType(""), SubjectAltNameFormat.IPV4, wantedIpsecAreas,
                EnrollmentMode.SCEP,
        		ISCF_CPP_MODEL_INFO,
        		"".getBytes());
        beanUnderTest.validateNodeAIData(nodeData);
    }

    @Test
    public void testValidateNodeAIDataNegativeCaseSecurityLevelBothNull() throws Exception {
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
        exception.expect(SecurityLevelNotSupportedException.class);
        exception.expectMessage("Wanted Security Level and Minimum Security Level cannot be null");
        data.setMinimumSecLevel(null);
        data.setWantedSecLevel(null);
        beanUnderTest.validateNodeAIData(data);
    }

    @Test
    @Ignore
    public void testValidateIpsecNodeAIDataNegativeCaseSubjectAltNameNull() throws Exception {
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("Property \"subjectAltName\" cannot be null string");
        data.setSubjectAltName(null);
        beanUnderTest.validateNodeAIData(data);
    }

//    @Test
//    public void testValidateIpsecNodeAIDataNegativeCaseSubjectAltNameEmptyString() throws Exception {
//        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
//        wantedIpsecAreas.add(IpsecArea.OM);
//        NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
//        exception.expect(InvalidNodeAIDataException.class);
//        exception.expectMessage("Property \"subjectAltName\" cannot be null or empty string");
//        data.setSubjectAltName(new SubjectAltNameStringType(""));
//        beanUnderTest.validateNodeAIData(data);
//    }

    @Test
    public void testValidateIpsecNodeAIDataNegativeCaseIpsecAreasListEmpty() throws Exception {
        final Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        final NodeAIData data = getCombinedNodeAIData(wantedIpsecAreas);
        exception.expect(InvalidNodeAIDataException.class);
        exception.expectMessage("At least one of IP Security area must be present");
        data.setSubjectAltName(new SubjectAltNameStringType("someName"));
        data.getIpsecAreas().clear();
        beanUnderTest.validateNodeAIData(data);
    }

    private NodeAIData getCombinedNodeAIData(final Set<IpsecArea> wantedIpsecAreas) throws Exception {
        final NodeAIData result = beanUnderTest.getNodeAIData(ISCF_TEST_FDN,
                ISCF_TEST_LOGICAL_NAME,
                SecurityLevel.LEVEL_2,
                SecurityLevel.LEVEL_2,
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

}
