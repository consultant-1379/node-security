/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.api.iscf;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class for testing IscfResponse POJO
 * @author ealemca
 */
public class IscfResponseTest {

    /**
     * Test of getter and setter methods, of class IscfResponse.
     */
    @Test
    public void testGettersAndSetters() throws Exception {
        IscfResponse beanUnderTest = new IscfResponse();
        byte[] testByteData = "13_CHARS_LONG".getBytes("UTF-8");
        String testChecksum = "CHECKSUM";
        String testRbsIntegrityCode = "RIC";
        beanUnderTest.setIscfContent(testByteData);
        beanUnderTest.setRbsIntegrityCode(testRbsIntegrityCode);
        beanUnderTest.setSecurityConfigChecksum(testChecksum);
        assertEquals(testByteData.length, beanUnderTest.getIscfContent().length);
        assertEquals(testChecksum, beanUnderTest.getSecurityConfigChecksum());
        assertEquals(testRbsIntegrityCode, beanUnderTest.getRbsIntegrityCode());
    }
    
}
