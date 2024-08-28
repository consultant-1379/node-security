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

import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.List;

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
 * Class for testing CombinedChecksumGenerator. Tests checksum generation against mock data
 * and checks against the legacy method for generating digests and formatting the output
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class CombinedChecksumGeneratorTest extends DataCollectorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(CombinedChecksumGenerator.class);

    @Spy
    private IscfConfigurationBean config;

    @InjectMocks
    CombinedChecksumGenerator beanUnderTest;

    @Test
    public void testChecksumGeneratorBasic() throws Exception {
        NodeAIData data = generateCombinedNodeAIData();
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertNotNull("Checksum result should not be null", result);
        assertFalse("Checksum result should not be empyt string", "".equals(result));
    }

    @Test
    public void testChecksumGeneratorBasicUnsecureMode() throws Exception {
        doReturn("Unsecure").when(config).getFileTransferClientMode();
        doReturn("Unsecure").when(config).getTelnetAndFtpServersMode();
        NodeAIData data = generateCombinedNodeAIData();
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertNotNull("Checksum result should not be null", result);
        assertFalse("Checksum result should not be empyt string", "".equals(result));
    }

    @Test
    public void testCheckSumGeneratorHasSecLevelAndIpsecChecksums() throws Exception {
        NodeAIData data = generateCombinedNodeAIData();
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertTrue("Checksum should contain comma", result.contains(","));
        String[] digests = result.split(",");
        assertEquals("Checksum for SL2 and IPSec should have 2 digests", 2, digests.length);
    }

    @Test
    public void testChecksumNoLevelTwo() throws Exception {
        NodeAIData data = generateCombinedNodeAIData();
        data.setWantedSecLevel(SecurityLevel.LEVEL_1);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_1);
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
//        assertTrue("Checksum should contain comma", result.contains(","));
        String[] digests = result.split(",");
        assertEquals("Checksum for SL1 and IPSec should have 1 digests", 1, digests.length);
    }

    @Test
    public void testChecksumGeneratorProducesSameHashAsLegacy() throws Exception {
        final List<byte[]> digests = new LinkedList<>();
        final MessageDigest md = MessageDigest.getInstance(IscfConstants.DEFAULT_HASH_ALGORITHM);
        digests.add( md.digest("TEST_DATA".getBytes(IscfConstants.UTF8_CHARSET) ) );
        digests.add( md.digest("OTHER_TEST_DATA".getBytes(IscfConstants.UTF8_CHARSET) ) );
        String newChecksum = beanUnderTest.encode(beanUnderTest.mergeDigests(digests));
        String legacyChecksum = legacyByteArrayToString(legacyMergeDigests(digests), true);
        log.info("ENM checksum    : {}", newChecksum);
        log.info("Legacy checksum : {}", legacyChecksum);
        assertEquals("ENM checksum and legacy checksum are different", newChecksum, legacyChecksum);
    }

    private NodeAIData generateCombinedNodeAIData() throws Exception {
        NodeAIData data = new NodeAIData();
        data.setRic("TEST_RIC".getBytes(IscfConstants.UTF8_CHARSET));
        data.setFdn(ISCF_TEST_FDN);
        data.setLogicalName(ISCF_TEST_LOGICAL_NAME);
        data.setLogonServerAddress(ISCF_TEST_LOGON_URI);
        data.getIpsecCertSpecs().add(generateCertSpec());
        data.setWantedSecLevel(SecurityLevel.LEVEL_2);
        data.setMinimumSecLevel(SecurityLevel.LEVEL_2);
        data.getSecLevelCertSpecs().add(generateCertSpec());
        data.getSecLevelCertSpecs().add(generateCertSpec());
        return data;
    }

    private byte[] legacyMergeDigests(final List<byte[]> mds) {
        final byte [] res = new byte[mds.get(0).length];
        for (final byte [] md: mds) {
            if (md.length == mds.get(0).length) {
                for (int i = 0; i < md.length; i++) {
                    res[i] ^= md[i];
                }
            } else {
                throw new IllegalArgumentException("Digest length mismatch.");
            }
        }
        return res;
    }

    private String legacyByteArrayToString(final byte[] arr, final boolean withColon) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SHA1=");
        for (int i = 0; i < arr.length; i++)
        {
            final int j = 0xff & arr[i];
            if (j < 0x10)
            {
                sb.append("0");
            }
            sb.append(Integer.toHexString(j).toUpperCase());
            if (withColon && i + 1 < arr.length)
            {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}
