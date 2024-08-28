package com.ericsson.nms.security.nscs.iscf;

import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * User: emacgma
 * Date: 24/06/14
 *
 */
public class SubjectAltNameFormatTest {

    @Test
    public void testValidatePositiveCasesToInt() {
        assertEquals(0, SubjectAltNameFormat.NONE.toInt());
        assertEquals(1, SubjectAltNameFormat.IPV4.toInt());
        assertEquals(2, SubjectAltNameFormat.FQDN.toInt());
        assertEquals(3, SubjectAltNameFormat.IPV6.toInt());
    }

    @Test
    public void testValidatePositiveCases() {
        assertEquals(Integer.valueOf(0).toString(), SubjectAltNameFormat.NONE.toString());
        assertEquals(Integer.valueOf(1).toString(), SubjectAltNameFormat.IPV4.toString());
        assertEquals(Integer.valueOf(2).toString(), SubjectAltNameFormat.FQDN.toString());
        assertEquals(Integer.valueOf(3).toString(), SubjectAltNameFormat.IPV6.toString());
    }

    @Test
    public void testValidatePositiveCasesValueOf() {
        assertEquals(SubjectAltNameFormat.NONE, SubjectAltNameFormat.valueOf("NONE"));
        assertEquals(SubjectAltNameFormat.IPV4, SubjectAltNameFormat.valueOf("IPV4"));
        assertEquals(SubjectAltNameFormat.FQDN, SubjectAltNameFormat.valueOf("FQDN"));
        assertEquals(SubjectAltNameFormat.IPV6, SubjectAltNameFormat.valueOf("IPV6"));
    }

}
