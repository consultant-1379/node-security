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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

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
public class SecurityLevelChecksumGeneratorTest extends DataCollectorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(SecurityLevelChecksumGenerator.class);

    @Spy
    private IscfConfigurationBean config;

    @InjectMocks
    private SecurityLevelChecksumGenerator beanUnderTest;

    @Test
    public void testChecksumGeneratorBasic() throws Exception {
        NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        data.getSecLevelCertSpecs().add(generateCertSpec());
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertNotNull("Checksum result should not be null", result);
        assertFalse("Checksum result should not be empyt string", "".equals(result));
    }

    @Test
    public void testChecksumGeneratorBasicUnsecure() throws Exception {
        doReturn("Unsecure").when(config).getFileTransferClientMode();
        doReturn("Unsecure").when(config).getTelnetAndFtpServersMode();
        NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        data.getSecLevelCertSpecs().add(generateCertSpec());
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertNotNull("Checksum result should not be null", result);
        assertFalse("Checksum result should not be empyt string", "".equals(result));
    }

    @Test
    public void testChecksumNoLevelTwo() throws Exception {
        NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_1);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_1);
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        String[] digests = result.split(",");
        assertEquals("Checksum for SL1 should have 1 digests", 1, digests.length);
    }

    @Test
    public void testChecksumSecurityLevelTwoHasTwoDigestsAndComma() throws Exception {
        NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        data.getSecLevelCertSpecs().add(generateCertSpec());
        data.getSecLevelCertSpecs().add(generateCertSpec());
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertTrue("Checksum should contain comma", result.contains(","));
        String[] digests = result.split(",");
        assertEquals("Checksum for SL2 should have 2 digests", 2, digests.length);
        
    }

    @Test
    public void testChecksumGeneratorHashLengths() throws Exception {
        NodeAIData data = generateBasicNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        data.getSecLevelCertSpecs().add(generateCertSpec());
        data.getSecLevelCertSpecs().add(generateCertSpec());
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        String[] digests = result.split(",");
        for(String digest : digests) {
            assertEquals("All digest lengths should be the same", digest.length(), digests[0].length());
        }
    }
}
