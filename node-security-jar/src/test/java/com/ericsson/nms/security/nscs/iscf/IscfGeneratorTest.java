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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class IscfGeneratorTest extends IscfTest {

    private final static SubjectAltNameStringType subjectAltNameString
            = new SubjectAltNameStringType(XML_HEADER_STR);
    private final static SubjectAltNameParam subjectAltNameParam
            = new SubjectAltNameParam(SubjectAltNameFormat.NONE, subjectAltNameString);

    @Spy
    private final Logger log = LoggerFactory.getLogger(BaseIscfGenerator.class);

    @Spy
    private RicGenerator ricGenerator;

    @Mock
    SecurityLevelIscfCreator secLevelCreator;

    @Mock
    SecurityLevelDataCollector secLevelCollector;

    @Mock
    SecurityLevelChecksumGenerator secLevelChecksum;

    @InjectMocks
    SecurityLevelIscfGenerator secLevelGenerator;

    @Mock
    IpsecIscfCreator ipsecCreator;

    @Mock
    IpsecDataCollector ipsecCollector;

    @Mock
    IpsecChecksumGenerator ipsecChecksum;

    @InjectMocks
    IpsecIscfGenerator ipsecGenerator;

    @Mock
    CombinedIscfCreator combinedCreator;

    @Mock
    CombinedDataCollector combinedCollector;

    @Mock
    CombinedChecksumGenerator combinedChecksum;

    @InjectMocks
    CombinedIscfGenerator combinedGenerator;

    @Before
    public void setUp() throws Exception {
        doReturn(ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)).when(ricGenerator).generateSalt();
        doReturn(ISCF_RIC_STRING).when(ricGenerator).generateRIC();
        doReturn(Mockito.mock(NodeAIData.class)).when(secLevelCollector).getNodeAIData(Mockito.any(SecurityLevel.class),
            Mockito.any(SecurityLevel.class),
            Mockito.any(String.class),
            Mockito.any(String.class),
            Mockito.any(EnrollmentMode.class),
            Mockito.any(NodeModelInformation.class),
            Mockito.any(byte[].class));
        doReturn(Mockito.mock(NodeAIData.class)).when(ipsecCollector).getNodeAIData(Mockito.any(String.class),
            Mockito.any(String.class),
            Mockito.any(String.class),
            Mockito.any(SubjectAltNameStringType.class),
            Mockito.any(SubjectAltNameFormat.class),
            Mockito.any(HashSet.class),
            Mockito.any(EnrollmentMode.class),
            Mockito.any(NodeModelInformation.class),
            Mockito.any(byte[].class));
        doReturn(Mockito.mock(NodeAIData.class)).when(combinedCollector).getNodeAIData(Mockito.any(String.class),
            Mockito.any(String.class),
            Mockito.any(SecurityLevel.class),
            Mockito.any(SecurityLevel.class),
            Mockito.any(String.class),
            Mockito.any(SubjectAltNameStringType.class),
            Mockito.any(SubjectAltNameFormat.class),
            Mockito.any(HashSet.class),
            Mockito.any(EnrollmentMode.class),
            Mockito.any(NodeModelInformation.class),
            Mockito.any(byte[].class));
        doReturn(ISCF_TEST_DN.getBytes(IscfConstants.UTF8_CHARSET)).when(ipsecCreator).create(Mockito.any(NodeAIData.class));
        doReturn(ISCF_TEST_DN.getBytes(IscfConstants.UTF8_CHARSET)).when(secLevelCreator).create(Mockito.any(NodeAIData.class));
        doReturn(ISCF_TEST_DN.getBytes(IscfConstants.UTF8_CHARSET)).when(combinedCreator).create(Mockito.any(NodeAIData.class));
    }

    @Test
    public void testInitGenerator() throws Exception {
        secLevelGenerator.initGenerator(SecurityLevel.LEVEL_1, SecurityLevel.LEVEL_1,
                ISCF_TEST_DN, ISCF_TEST_DN, EnrollmentMode.SCEP, ISCF_CPP_MODEL_INFO);
        ipsecGenerator.initGenerator(ISCF_TEST_DN, ISCF_TEST_DN, ISCF_TEST_DN,
                subjectAltNameParam, null, 
                EnrollmentMode.SCEP, ISCF_CPP_MODEL_INFO);
        combinedGenerator.initGenerator(SecurityLevel.LEVEL_1, SecurityLevel.LEVEL_1,
                ISCF_TEST_DN, ISCF_TEST_DN, ISCF_TEST_DN, subjectAltNameParam, null, 
                EnrollmentMode.SCEP, ISCF_CPP_MODEL_INFO);
    }

    @Test
    public void testSecLevelGeneratorGenerate() throws Exception {
        secLevelGenerator.generate();
        verify(secLevelChecksum).getSecurityConfigChecksum(Mockito.any(NodeAIData.class));
    }

    @Test
    public void testIpsecGeneratorGenerate() throws Exception {
        ipsecGenerator.generate();
        verify(ipsecChecksum).getSecurityConfigChecksum(Mockito.any(NodeAIData.class));
    }

    @Test
    public void testCombinedGeneratorGenerate() throws Exception {
        combinedGenerator.generate();
        verify(combinedChecksum).getSecurityConfigChecksum(Mockito.any(NodeAIData.class));
    }

}
