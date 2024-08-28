package com.ericsson.nms.security.nscs.utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NscsNodeUtilityTest {

    private static final String IPV4_ADD = "192.168.56.1";
    private static final String IPV6_ADD = "2001:1b70:82a1:103::64:19b";
    private static final String DNS_TYPE = "DNS:";
    private static final String IP_TYPE = "IP:";
    private static final String PROBE = "PROBE";
    private static final String FQDN = "FQDN";
    private static final String IPV4 = "IPV4";
    private static final String IPV6 = "IPV6";

    @Test
    public void testPrepareNodeCredentialSubjectAltName_IPV6() {
        NscsNodeUtility nscsNodeUtility = new NscsNodeUtility();
        final String subjectAltNameValue = IPV6_ADD;
        final String subjectAltNameType = IPV6;
        String subjectAltNameMo = nscsNodeUtility.prepareNodeCredentialSubjectAltName(subjectAltNameValue, subjectAltNameType);
        assertEquals(subjectAltNameMo, subjectAltNameValue);
    }

    @Test
    public void testPrepareNodeCredentialSubjectAltName_IPV4() {
        NscsNodeUtility nscsNodeUtility = new NscsNodeUtility();
        final String subjectAltNameValue = IPV4_ADD;
        final String subjectAltNameType = IPV4;
        String subjectAltNameMo = nscsNodeUtility.prepareNodeCredentialSubjectAltName(subjectAltNameValue, subjectAltNameType);
        assertEquals(subjectAltNameMo, IP_TYPE + subjectAltNameValue);
    }

    @Test
    public void testPrepareNodeCredentialSubjectAltName_FQDN() {
        NscsNodeUtility nscsNodeUtility = new NscsNodeUtility();
        final String subjectAltNameValue = PROBE;
        final String subjectAltNameType = FQDN;
        String subjectAltNameMo = nscsNodeUtility.prepareNodeCredentialSubjectAltName(subjectAltNameValue, subjectAltNameType);
        assertEquals(subjectAltNameMo, DNS_TYPE + subjectAltNameValue);
    }

    @Test
    public void testPrepareNodeCredentialSubjectAltName() {
        NscsNodeUtility nscsNodeUtility = new NscsNodeUtility();
        final String subjectAltNameValue = PROBE;
        final String subjectAltNameType = PROBE;
        String subjectAltNameMo = nscsNodeUtility.prepareNodeCredentialSubjectAltName(subjectAltNameValue, subjectAltNameType);
        assertEquals(subjectAltNameMo, subjectAltNameValue);
    }
}
