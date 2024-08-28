/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.iscf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustedCertificateData;

public class SecurityDataResponseTest {

    TrustedCertificateData other;
    TrustedCertificateData other1;

    public void setup(String... arg) {
        other = new TrustedCertificateData("1", "62:A9:86:2D:C3:6B:CD:B2:A0:C0:5F:74:2C:0B:B1:AF:72:3C:A6:1B",
                "O=ERICSSON, OU=BUCI_DUAC_NAM, C=SE, CN=NE_OAM_CA", "NE_OAM_CA",
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM","caIsserDn");
        other1 = new TrustedCertificateData(arg[0], arg[1], arg[2], arg[3], arg[4], arg[5], arg[6]);
    }

    @Test
    public void testHashCode() {

        setup("2", "62:A9:86:2D:C3:6B:CD:B2:A0:C0:5F:74:2C:0B:B1:AF:72:3C:A6:1B", "O=ERICSSON, OU=BUCI_DUAC_NAM, C=SE, CN=NE_OAM_CA", "NE_OAM_CA",
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));
        assertNotNull(other.toString());
        assertNotNull(other.hashCode());
        assertNotNull(other.getCaName());
        assertNotNull(other.getCaPem());
        assertNotNull(other.getCaSubjectName());
        assertNotNull(other.getTdpsUri());
        assertNotNull(other.getTrustedCertificateFdn());
        assertNotNull(other.getTrustedCertificateFingerPrint());
        assertNotNull(other.getCaIssuerDn());

        setup("3", "62:A8:86:2D:C3:6B:CD:B2:A0:C0:5F:74:2C:0B:B1:AF:72:3C:A6:1B", "O=ERICSSON, OU=BUCI_DUAC_NAM, C=SE, CN=NE_OAM_CA", "NULL",
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "62:A9:86:2D:C3:6B:CD:B2:A0:C0:5F:74:2C:0B:B1:AF:72:3C:A6:1B", "O=ERICSSON, OU=BUCI_DUAC_NAM, C=SE, CN=NE_OAM_CA", "NE_OAM_CA",
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "62:A9:86:2D:C3:6B:CD:B2:A0:C0:5F:74:2C:0B:B1:AF:72:3C:A6:1B",
                "O=ERICSSON, OU=BUCI_DUAC_NAM, C=SE, CN=NE_OAM_CA", null,
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");

        assertFalse(other.equals(other1));

        other1 = null;
        assertFalse(other.equals(other1));

        setup("1", "62:A9:86:2D:C3:6B:CD:B2:A0:C0:5F:74:2C:0B:B1:AF:72:3C:A6:1B", "O=ERICSSON, OU=BUCI_DUAC_NAM, C=SE, CN=NE_OAM_CA", "NE_OAM_CA",
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("2", "62:A9:86:2D:C3:6B:CD:B2:A0:C0:5F:74:2C:0B:B1:AF:72:3C:A6:1B", null, "NE_OAM_CA",
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "subjectName", "NE_OAM_CA",
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", null,
                "http://131.160.152.19:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/1e3478e75012e7fb/active/ENM_PKI_Root_CA", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME1", "test", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME1", "test", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", null, "CA_NAME", "test", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", null, "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", "test1", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", "test", null,"caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM1", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        assertTrue(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", null);
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIsserDn");
        assertFalse(other.equals(other1));

        setup("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", "caIssuerDn");
        other = new TrustedCertificateData("1", "fingerprint", "SubjectName", "CA_NAME", "test", "caPEM", null);
        assertFalse(other.equals(other1));
    }
}
