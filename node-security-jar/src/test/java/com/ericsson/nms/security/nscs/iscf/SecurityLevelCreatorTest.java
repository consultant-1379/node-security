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

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.iscf.dto.EncryptedContentDto;
import com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.ISCFEncryptedContentDto;
import com.ericsson.nms.security.nscs.iscf.dto.SecEnrollmentDataDto;

import javax.xml.bind.MarshalException;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;

import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class SecurityLevelCreatorTest extends CreatorTest {
    
    private final byte[] testHash;
    private final byte[] testHmac;

    public SecurityLevelCreatorTest() throws Exception {
        this.testHash = "TESTHASHTESTHASHTESTHASH".getBytes(IscfConstants.UTF8_CHARSET);
        this.testHmac = "TESTHMACTESTHMACTESTHMAC".getBytes(IscfConstants.UTF8_CHARSET);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(SecurityLevelIscfCreator.class);

    @Spy
    private IscfConfigurationBean config;

    @Mock
    IscfValidatorsGenerator checksumGen;

    @InjectMocks
    SecurityLevelIscfCreator beanUnderTest;

    @Before
    public void setUp() throws Exception {
        doReturn(testHash).when(checksumGen).getChecksum(Mockito.anyString());
        doReturn(testHmac).when(checksumGen).getHmac(Mockito.anyString(), Mockito.any(byte[].class));
    }

    @Test
    public void testCreateISCFLevel2PositiveCase() throws Exception {
        NodeAIData data = createStandardNodeAIData("Some content");
        byte[] xmlOutput = null;
        String xmlOutputAsString = null;
        xmlOutput = beanUnderTest.create(data);
        xmlOutputAsString = new String(xmlOutput);
        System.out.println(String.format("XML --> %s", xmlOutputAsString));
        assertXMLIsValid(xmlOutput, xmlOutputAsString);
    }

    /*
     * The <certFile> encrypted content node only contains empty strings. It is
     * unclear whether this test case should pass, but it validates against the
     * current XSD
     */
    @Test
    public void testCreateISCFLevel2NegativeCaseNullCertFiles() throws Exception {
        NodeAIData data = createBadNodeAIData();
        byte[] xmlOutput = null;
        String xmlOutputAsString = null;
        xmlOutput = beanUnderTest.create(data);
        xmlOutputAsString = new String(xmlOutput);
        System.out.println(String.format("XML --> %s", xmlOutputAsString));
        assertXMLIsValid(xmlOutput, xmlOutputAsString);

    }

    @Test
    public void testIncompleteNodeAIDataNoCertFiles() throws Exception {
        exception.expect(MarshalException.class);
        NodeAIData data = createNodeAIDataWithoutLevel2Files();
        byte[] xmlOutput = beanUnderTest.create(data);
        assertNotNull(xmlOutput);
    }

    private NodeAIData createStandardNodeAIData(String content) throws Exception{
        NodeAIData data = new NodeAIData();
        data.setRic("TEST_RIC".getBytes(IscfConstants.UTF8_CHARSET));
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_1);
        data.setFdn(ISCF_TEST_FDN);
        data.setLogicalName(ISCF_TEST_LOGICAL_NAME);
        data.setLogonServerAddress(ISCF_TEST_LOGON_URI);
        data.setSecEnrollmentData(createSecEnrollmentData("something"));
        if(data.getSecLevelCertFileDtos()!=null){
            data.getSecLevelCertFileDtos().add(createCertFileDto(content, ISCF_TEST_CATEGORY_CORBA));
            data.getSecLevelCertFileDtos().add(createCertFileDto(content, ISCF_TEST_CATEGORY_CORBA));
        }
        return data;
    }

    private NodeAIData createBadNodeAIData() throws Exception {
        return createStandardNodeAIData("");
    }

    private NodeAIData createNodeAIDataWithoutLevel2Files() throws Exception {
        NodeAIData data = new NodeAIData();
        data.setRic("TEST_RIC".getBytes(IscfConstants.UTF8_CHARSET));
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_1);
        data.setFdn(ISCF_TEST_FDN);
        data.setLogicalName(ISCF_TEST_LOGICAL_NAME);
        data.setLogonServerAddress(ISCF_TEST_LOGON_URI);
        data.setSecEnrollmentData(createSecEnrollmentData("something"));
        return data;
    }

    private SecEnrollmentDataDto createSecEnrollmentData(String content) {
        EncryptedContentDto ec = new EncryptedContentDto();
        ec.setPBKDF2IterationCount(ISCF_TEST_ITERATION_COUNT);
        ec.setPBKDF2Salt(content.getBytes());
        ec.setValue(content.getBytes());

        ISCFEncryptedContentDto dataChallengePassword = new ISCFEncryptedContentDto();
        dataChallengePassword.setEncryptedContent(ec);

        EnrollmentDataDto er = new EnrollmentDataDto();
        er.setCAFingerprint(ISCF_TEST_FINGERPRINT);
        er.setDistinguishedName(ISCF_TEST_DN);
        er.setEnrollmentServerURL(ISCF_TEST_ENROLLMENT_URI);
        er.setDataChallengePassword(dataChallengePassword);
        er.setKeyLength(ISCF_TEST_KEY_LENGTH_MAX);
        er.setEnrollmentTimeLimit(ISCF_TEST_ENROLLMENT_TIME_MIN);

        SecEnrollmentDataDto dto = new SecEnrollmentDataDto();
        dto.setRollbackTimeout(10);
        dto.setEnrollmentData(er);

        return dto;
    }

}
