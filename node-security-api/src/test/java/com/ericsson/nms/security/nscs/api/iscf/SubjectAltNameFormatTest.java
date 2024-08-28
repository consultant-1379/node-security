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
 * Class for testing the SubjectAltNameFormat enum type
 *
 * <ul>
 *     <li>Tests <code>toString()</code></li>
 *     <li>Tests <code>toInt()()</code></li>
 *     <li>Tests <code>values()</code></li>
 *     <li>Tests <code>valueOf()</code></li>
 * </ul>
 *
 * @author ealemca
 */
public class SubjectAltNameFormatTest {

    @Test
    public void testToInt() {
        SubjectAltNameFormat instance = SubjectAltNameFormat.FQDN;
        assertEquals(2, instance.toInt());
    }

    @Test
    public void testToString() {
        SubjectAltNameFormat instance = SubjectAltNameFormat.IPV4;
        assertEquals("1", instance.toString());
    }

    @Test
    public void testValueOf() {
        assertEquals(SubjectAltNameFormat.IPV6, SubjectAltNameFormat.valueOf("IPV6"));
    }

    @Test
    public void testValues() {
        assertNotNull(SubjectAltNameFormat.values());
    }

}
