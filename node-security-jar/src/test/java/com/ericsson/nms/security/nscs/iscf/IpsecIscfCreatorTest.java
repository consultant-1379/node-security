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

import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.iscf.dto.EncryptedContentDto;
import com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.ISCFEncryptedContentDto;
import com.ericsson.nms.security.nscs.iscf.dto.IpsecEnrollmentDataDto;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class IpsecIscfCreatorTest extends CreatorTest {

    private final byte[] testHash;
    private final byte[] testHmac;

    public IpsecIscfCreatorTest() throws Exception {
        this.testHash = "TESTHASHTESTHASHTESTHASH".getBytes(IscfConstants.UTF8_CHARSET);
        this.testHmac = "TESTHMACTESTHMACTESTHMAC".getBytes(IscfConstants.UTF8_CHARSET);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(IpsecIscfCreator.class);

    @Spy
    private IscfConfigurationBean config;

    @Mock
    IscfValidatorsGenerator checksumGen;

    @InjectMocks
    IpsecIscfCreator beanUnderTest;

    @Before
    public void setUp() throws Exception {
        doReturn(testHash).when(checksumGen).getChecksum(Mockito.anyString());
        doReturn(testHmac).when(checksumGen).getHmac(Mockito.anyString(), Mockito.any(byte[].class));
    }

    @Test
    public void testCreateIpsecPositiveCaseTransportOnly()throws Exception{
        log.debug("*********testCreateIpsecPositiveCaseTransportOnly");
        NodeAIData data = createStandardNodeAIDataWithIpsecFiles("Some content");
        data.setIpsecCertExpirWarnTime(90);
        data.getIpsecAreas().add(IpsecArea.TRANSPORT);
        data.setIpsecUserLabel("ipsec_Sunita06");
        data.setSubjectAltName(new SubjectAltNameStringType("100.111.111.111"));
        data.setSubjectAltNameFormat(SubjectAltNameFormat.IPV4);
        byte[] xmlOutput = null;
        String xmlOutputAsString = null;
        xmlOutput = beanUnderTest.create(data);
        xmlOutputAsString = new String(xmlOutput);
        System.out.println(String.format("XML --> %s", xmlOutputAsString));
        assertXMLIsValid(xmlOutput, xmlOutputAsString);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_TRANSPORT_XML);
        assertXMLIsValidAndDoesNotContainString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_OAM_XML);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "ipsec_Sunita06");
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "100.111.111.111");
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "ipsecPeer");
     }

    @Test
    public void testCreateIpsecPositiveCaseTransportAndOm()throws Exception{
        log.debug("*********testCreateIpsecPositiveCaseTransportAndOm");
        NodeAIData data = createStandardNodeAIDataWithIpsecFiles("Some content");
        data.setIpsecCertExpirWarnTime(90);
        data.getIpsecAreas().add(IpsecArea.TRANSPORT);
        data.getIpsecAreas().add(IpsecArea.OM);
        data.setIpsecUserLabel("ipsec_Sunita06");
        data.setSubjectAltName(new SubjectAltNameStringType("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        data.setSubjectAltNameFormat(SubjectAltNameFormat.IPV4);
        byte[] xmlOutput = null;
        String xmlOutputAsString = null;
        xmlOutput = beanUnderTest.create(data);
        xmlOutputAsString = new String(xmlOutput);
        System.out.println(String.format("XML --> %s", xmlOutputAsString));
        assertXMLIsValid(xmlOutput, xmlOutputAsString);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_TRANSPORT_XML);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_OAM_XML);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "ipsec_Sunita06");
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "ipsecPeer");
    }

    @Test
    public void testCreateIpsecPositiveCaseOmOnly()throws Exception{
        log.debug("*********testCreateIpsecPositiveCaseOmOnly");
        NodeAIData data = createStandardNodeAIDataWithIpsecFiles("Some content");
        data.setIpsecCertExpirWarnTime(90);
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
        assertXMLIsValidAndDoesNotContainString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_TRANSPORT_XML);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, ISCF_IPSEC_OAM_XML);
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "ipsec_Sunita06");
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "testSubjectAltName");
        assertXMLIsValidAndContainsString(xmlOutput, xmlOutputAsString, "ipsecPeer");
    }

    private NodeAIData createStandardNodeAIDataWithIpsecFiles(String content) throws Exception {
        NodeAIData data = new NodeAIData();
        data.setRic("TEST_RIC".getBytes(IscfConstants.UTF8_CHARSET));
        data.setFdn(ISCF_TEST_FDN);
        data.setLogicalName(ISCF_TEST_LOGICAL_NAME);
        data.setLogonServerAddress(ISCF_TEST_LOGON_URI);
        data.getIpsecCertFileDtos().add(createCertFileDto(content,ISCF_TEST_CATEGORY_IPSEC ));
        data.setIpsecEnrollmentDataDto(createIpsecEnrollmentDataDto(content));
        return data;
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
