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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class IpsecChecksumGeneratorTest extends DataCollectorTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(IpsecChecksumGenerator.class);

    @Spy
    private IscfConfigurationBean config;

    @InjectMocks
    private IpsecChecksumGenerator beanUnderTest;

    @Test
    public void testChecksumGeneratorBasic() throws Exception {
        NodeAIData data = generateBasicNodeAIDataWithIpsec();
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertNotNull("Checksum result should not be null", result);
        assertFalse("Checksum result should not be empyt string", "".equals(result));
    }

    @Test
    public void testChecksumGeneratorIpsecHasOnlyOneDigest() throws Exception {
        NodeAIData data = generateBasicNodeAIDataWithIpsec();
        String result = beanUnderTest.getSecurityConfigChecksum(data);
        log.info("Checksum result: {}", result);
        assertFalse("Checksum should not contain comma", result.contains(","));
        
    }

    private NodeAIData generateBasicNodeAIDataWithIpsec() throws Exception {
        NodeAIData data = new NodeAIData();
        data.setRic("TEST_RIC".getBytes(IscfConstants.UTF8_CHARSET));
        data.setFdn(ISCF_TEST_FDN);
        data.setLogicalName(ISCF_TEST_LOGICAL_NAME);
        data.setLogonServerAddress(ISCF_TEST_LOGON_URI);
        data.getIpsecCertSpecs().add(generateCertSpec());
        return data;
    }

}
