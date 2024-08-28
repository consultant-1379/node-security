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
 * Class for testing the IpsecArea enum type
 *
 * <ul>
 *     <li>Tests <code>toString()</code></li>
 *     <li>Tests <code>values()</code></li>
 * </ul>
 *
 * @author ealemca
 */
public class IpsecAreaTest {

    @Test
    public void testValues() {
        IpsecArea area = IpsecArea.valueOf("OM");
        assertEquals(IpsecArea.OM, area);
        area = IpsecArea.valueOf("TRANSPORT");
        assertEquals(IpsecArea.TRANSPORT, area);
    }

    @Test
    public void testToString() {
        IpsecArea area = IpsecArea.OM;
        assertEquals("OAM", area.toString());
        area = IpsecArea.TRANSPORT;
        assertEquals("Traffic", area.toString());
    }

}
