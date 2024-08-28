package com.ericsson.nms.security.nscs.util;

import static org.junit.Assert.*;

import java.net.StandardProtocolFamily;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;

@RunWith(MockitoJUnitRunner.class)
public class NodeDetailsTest {

    NodeDetails other;
    NodeDetails other1;

    public void setup() {
        other = new NodeDetails();
        other1 = new NodeDetails();
    }

    @Test
    public void testHashCode() {

        setup();
        other.setCertType("OAM");
        other1.setCertType("IPSec");
        assertFalse(other.equals(other1));
        setup();
        other1.setCommonName("test");
        other1.setCommonName("Sample_CommonName");
        assertFalse(other.equals(other1));
        setup();
        other.setKeySize("OAM");
        other1.setKeySize("RSA_2048");
        assertFalse(other.equals(other1));
        setup();
        other.setCommonName("OAM");
        other1.setCommonName("RSA_2048");
        assertFalse(other.equals(other1));
        setup();
        other.setEnrollmentMode("OAM");
        other1.setEnrollmentMode("RSA_2048");
        assertFalse(other.equals(other1));
        setup();
        other.setKeySize("OAM");
        other1.setKeySize("RSA_2048");
        assertFalse(other.equals(other1));

        setup();
        other.setNodeFdn(null);
        other1.setNodeFdn("RSA_2048");
        assertFalse(other.equals(other1));
        setup();
        other.setNodeFdn("OAM");
        other1.setNodeFdn("RSA_2048");
        assertFalse(other.equals(other1));

        setup();
        other.setSubjectAltName("Sample");
        other1.setSubjectAltName("null");
        assertFalse(other.equals(other1));

        setup();
        other.setSubjectAltName("Sample");
        other1.setSubjectAltName("Sample");
        assertTrue(other.equals(other1));

        setup();
        other.setSubjectAltNameType("AltName");
        other1.setSubjectAltNameType("null");
        assertFalse(other.equals(other1));

        setup();
        other.setEntityProfileName("EP");
        other1.setEntityProfileName("null");
        assertFalse(other.equals(other1));

        setup();
        other.setIpVersion(StandardProtocolFamily.INET);
        assertNotNull(other.getIpVersion());
        other1.setIpVersion(null);
        assertFalse(other.equals(other1));

        setup();
        other.setIpVersion(null);
        other1.setIpVersion(StandardProtocolFamily.INET);
        assertFalse(other.equals(other1));

        setup();
        other = null;
        assertNull(other);
        setup();
        other.setCertType(other.getCertType());
        other.setCommonName(other.getCommonName());
        other.setEnrollmentMode(other.getEnrollmentMode());
        other.setEntityProfileName(other.getEntityProfileName());
        other.setKeySize(other.getKeySize());
        other.setNodeFdn(other.getNodeFdn());
        other.setSubjectAltName(other.getSubjectAltName());
        other.setSubjectAltNameType(other.getSubjectAltNameType());
        other.hashCode();
    }
}
