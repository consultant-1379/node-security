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
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;

import static com.ericsson.nms.security.nscs.iscf.IscfTest.ISCF_TEST_ITERATION_COUNT;

import com.ericsson.nms.security.nscs.iscf.dto.EncryptedContentDto;
import com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.ISCFEncryptedContentDto;
import com.ericsson.nms.security.nscs.iscf.dto.IpsecEnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.SecEnrollmentDataDto;

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

/**
 * Class for testing ISCF containing both Security Level and IPSec configuration
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class CombinedIscfCreatorTest extends CreatorTest {
    
    private final byte[] testHash;
    private final byte[] testHmac;

    public CombinedIscfCreatorTest() throws Exception {
        this.testHash = "TESTHASHTESTHASHTESTHASH".getBytes(IscfConstants.UTF8_CHARSET);
        this.testHmac = "TESTHMACTESTHMACTESTHMAC".getBytes(IscfConstants.UTF8_CHARSET);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(CombinedIscfCreator.class);

    @Spy
    private IscfConfigurationBean config;

    @Mock
    IscfValidatorsGenerator checksumGen;

    @InjectMocks
    CombinedIscfCreator beanUnderTest;

    @Before
    public void setUp() throws Exception {
        doReturn(testHash).when(checksumGen).getChecksum(Mockito.anyString());
        doReturn(testHmac).when(checksumGen).getHmac(Mockito.anyString(), Mockito.any(byte[].class));
    }

    @Test
    public void testCreateLevel2FilesAndIpsecPositiveCase()throws Exception{
        log.debug("*********testCreateLevel2FilesAndIpsecPositiveCase");
        NodeAIData data = createStandardNodeAIData("Some content");
        data.getIpsecCertFileDtos().add(createCertFileDto("Some content",ISCF_TEST_CATEGORY_IPSEC ));
        data.setIpsecEnrollmentDataDto(createIpsecEnrollmentDataDto("Some content"));
        data.setIpsecCertExpirWarnTime(90);
        data.getIpsecAreas().add(IpsecArea.TRANSPORT);
        data.setIpsecUserLabel("ipsec_Sunita06");
        data.setSubjectAltName(new SubjectAltNameStringType("testSubjectAltName"));
        data.setSubjectAltNameFormat(SubjectAltNameFormat.IPV4);
        byte[] xmlOutput = null;
        String xmlOutputAsString = null;
        xmlOutput = beanUnderTest.create(data);
        xmlOutputAsString = new String(xmlOutput);
        System.out.println(String.format("XML --> %s", xmlOutputAsString));
        assertXMLIsValid(xmlOutput, xmlOutputAsString);
    }

    @Test
    public void testCreateLevel2FilesAndIpsecTransportOM() throws Exception {
        log.debug("*********testCreateLevel2FilesAndIpsecTransportOM");
        NodeAIData data = createStandardNodeAIData("Some content");
        data.getIpsecCertFileDtos().add(createCertFileDto("Some content",ISCF_TEST_CATEGORY_IPSEC ));
        data.setIpsecEnrollmentDataDto(createIpsecEnrollmentDataDto("Some content"));
        data.setIpsecCertExpirWarnTime(90);
        data.getIpsecAreas().add(IpsecArea.TRANSPORT);
        data.getIpsecAreas().add(IpsecArea.OM);
        data.setIpsecUserLabel("ipsec_Sunita06");
        data.setSubjectAltName(new SubjectAltNameStringType("testSubjectAltName"));
        data.setSubjectAltNameFormat(SubjectAltNameFormat.IPV4);
        byte[] xmlOutput = null;
        String xmlOutputAsString = null;
        xmlOutput = beanUnderTest.create(data);
        xmlOutputAsString = new String(xmlOutput);
        System.out.println(String.format("XML --> %s", xmlOutputAsString));
        assertXMLIsValid(xmlOutput, xmlOutputAsString);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_TRANSPORT_XML);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_OAM_XML);
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

    private IpsecEnrollmentDataDto createIpsecEnrollmentDataDto(String content) {
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

        IpsecEnrollmentDataDto dto = new IpsecEnrollmentDataDto();
        dto.setEnrollmentData(er);
        dto.setSubjectAltName("some name");
        dto.setSubjectAltNameFormat(SubjectAltNameFormat.IPV4);
        System.out.println("Enrollment Data:"+er.getCAFingerprint().toString());

        return dto;
    }


}
