package com.ericsson.nms.security.nscs.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CertDetailsTest {

    private static final String DEC_SN = "1851389729377754674";
    private static final String DEC_16_BYTES_SN = "308008147789412154081106666664421348297";
    private static final BigInteger SN = new BigInteger(DEC_SN);
    private static final BigInteger SN_16_BYTES = new BigInteger(DEC_16_BYTES_SN);
    private static final String HEX_SN = "19b17526585e1e32";
    private static final String HEX_0X_SN = "0x19b17526585e1e32";
    private static final String HEX_COLON_SN = "19:b1:75:26:58:5e:1e:32";
    private static final String HEX_COLON_16_BYTES_SN = "e7:b8:36:24:7d:59:6c:2c:01:7f:2b:dc:e9:19:d3:c9";
    private static final String DN = "CN=NE_OAM_CA,O=ERICSSON,OU=BUCI_DUAC_NAM,C=SE";
    private static final String LOWER_CASE_DN = "CN=NE_OAM_CA,O=Ericsson,OU=BUCI_DUAC_NAM,C=SE";
    private static final String SAME_DN = "C=SE,O=ERICSSON,OU=BUCI_DUAC_NAM,CN=NE_OAM_CA";
    private static final String RDN = "CN=NE_OAM_CA";
    private static final String CN = "NE_OAM_CA";
    private static final String ENM_PICO_DN = "CN=C829930736.Ericsson.SE,C=SE,O=Ericsson";
    private static final String NODE_PICO_DN = "C=SE,O=Ericsson";
    private static final String SAME_NODE_PICO_DN = "O=Ericsson,C=SE";
    private static final String DIFFERENT_NODE_PICO_DN = "C=CH,O=Swisscom";
    private static final String PKI_DN = "CN=ENM_PKI_Root_CA,DN=ENM,T=aTitle,GIVENNAME=aName,SN=1000001";
    private static final String NODE_DN = "CN=ENM_PKI_Root_CA,dnQualifier=ENM,title=aTitle,GN=aName,serialNumber=1000001";
    private static final String NODE_REVERSE_ORDER_DN = "serialNumber=1000001,GN=aName,CN=ENM_PKI_Root_CA,dnQualifier=ENM,title=aTitle";
    private static final String PKI_SUPPORTED_EXTRA_FIELDS_DN = "CN=ENM_PKI_Root_CA,C=US,L=Westlake,ST=TX,O=Ericsson,OU=ericssonOAM,DN=ENM,T=aTitle,GIVENNAME=aName,SN=1000001";
    private static final String NODE_SUPPORTED_EXTRA_FIELDS_DN = "CN=ENM_PKI_Root_CA,C=US,L=Westlake,ST=TX,O=Ericsson,OU=ericssonOAM,dnQualifier=ENM,title=aTitle,GN=aName,serialNumber=1000001";
    private static final String NODE_SUPPORTED_EXTRA_FIELDS_REVERSE_ORDER_DN = "C=US,O=Ericsson,OU=ericssonOAM,CN=ENM_PKI_Root_CA,L=Westlake,ST=TX,dnQualifier=ENM,title=aTitle,GN=aName,serialNumber=1000001";
    private static final String PKI_UNSUPPORTED_EXTRA_FIELDS_DN = "CN=ENM_PKI_Root_CA,SURNAME=aSurname,C=US,L=Westlake,ST=TX,O=Ericsson,OU=ericssonOAM,DN=ENM,T=aTitle,GIVENNAME=aName,SN=1000001";
    private static final String NODE_UNSUPPORTED_EXTRA_FIELDS_DN = "CN=ENM_PKI_Root_CA,SN=aSurname,C=US,L=Westlake,ST=TX,O=Ericsson,OU=ericssonOAM,dnQualifier=ENM,title=aTitle,GN=aName,serialNumber=1000001";
    private static final String PKI_1_DN = "CN=myCN, STREET=myStreet, GIVENNAME=myGN, L=myLocation, SERIALNUMBER=987654, C=IT, ST=myState, O=myOrg, OU=myOrgUnit";
    private static final String NODE_1_DN = "OU=myOrgUnit,O=myOrg,ST=myState,C=IT,SN=987654,L=myLocation,GN=myGN,STREET=myStreet,CN=myCN";
    private static final String NODE_DISTINGUISHED_NAME = "domainComponent=com,domainComponent=att,domainComponent=mx,CN=mx-MXSPR1W16PCA02-CA";
    private static final String PKI_DISTINGUISHED_NAME = "DC=com,DC=att,DC=mx,CN=mx-MXSPR1W16PCA02-CA";

    @Test
    public void testConvertHexadecimalSerialNumberToDecimalFormatWithNull() {
        assertNull(CertDetails.convertHexadecimalSerialNumberToDecimalFormat(null));
    }

    @Test
    public void testConvertHexadecimalSerialNumberToDecimalFormatWithEmpty() {
        assertNull(CertDetails.convertHexadecimalSerialNumberToDecimalFormat(""));
    }

    @Test
    public void testConvertHexadecimalSerialNumberToDecimalFormatWithInvalid() {
        assertNull(CertDetails.convertHexadecimalSerialNumberToDecimalFormat("INVALID"));
    }

    @Test
    public void testConvertHexadecimalSerialNumberToDecimalFormatWithDec() {
        assertFalse(SN.equals(CertDetails.convertHexadecimalSerialNumberToDecimalFormat(DEC_SN)));
    }

    @Test
    public void testConvertHexadecimalSerialNumberToDecimalFormatWithHex() {
        assertTrue(SN.equals(CertDetails.convertHexadecimalSerialNumberToDecimalFormat(HEX_SN)));
    }

    @Test
    public void testConvertHexadecimalSerialNumberToDecimalFormatWithHex0x() {
        assertTrue(SN.equals(CertDetails.convertHexadecimalSerialNumberToDecimalFormat(HEX_0X_SN)));
    }

    @Test
    public void testConvertHexadecimalSerialNumberToDecimalFormatWithHexColon() {
        assertTrue(SN.equals(CertDetails.convertHexadecimalSerialNumberToDecimalFormat(HEX_COLON_SN)));
    }

    @Test
    public void testConvertHexadecimalSerialNumber16BytesToDecimalFormatWithHexColon() {
        assertTrue(SN_16_BYTES.equals(CertDetails.convertHexadecimalSerialNumberToDecimalFormat(HEX_COLON_16_BYTES_SN)));
    }

    @Test
    public void testConvertSerialNumberToDecimalFormatWithNull() {
        assertNull(CertDetails.convertSerialNumberToDecimalFormat(null));
    }

    @Test
    public void testConvertSerialNumberToDecimalFormatWithEmpty() {
        assertNull(CertDetails.convertSerialNumberToDecimalFormat(""));
    }

    @Test
    public void testConvertSerialNumberToDecimalFormatWithInvalid() {
        assertNull(CertDetails.convertSerialNumberToDecimalFormat("INVALID"));
    }

    @Test
    public void testConvertSerialNumberToDecimalFormatWithDec() {
        assertTrue(SN.equals(CertDetails.convertSerialNumberToDecimalFormat(DEC_SN)));
    }

    @Test
    public void testConvertSerialNumberToDecimalFormatWithHex() {
        assertTrue(SN.equals(CertDetails.convertSerialNumberToDecimalFormat(HEX_SN)));
    }

    @Test
    public void testConvertSerialNumberToDecimalFormatWithHex0x() {
        assertTrue(SN.equals(CertDetails.convertSerialNumberToDecimalFormat(HEX_0X_SN)));
    }

    @Test
    public void testConvertSerialNumberToDecimalFormatWithHexColon() {
        assertTrue(SN.equals(CertDetails.convertSerialNumberToDecimalFormat(HEX_COLON_SN)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void matchesDNWithNull() {
        CertDetails.matchesDN(null, DEC_SN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void matchesDNWithSecondNull() {
        CertDetails.matchesDN(DEC_SN, null);
    }

    @Test
    public void matchesDNWithEmpty() {
        assertTrue(CertDetails.matchesDN("", ""));
    }

    @Test
    public void matchesDNWithInvalid() {
        assertFalse(CertDetails.matchesDN("INVALID", "INVALID"));
    }

    @Test
    public void testMatchesDN() {
        assertTrue(CertDetails.matchesDN(DN, DN));
        assertTrue(CertDetails.matchesDN(DN, LOWER_CASE_DN));
        assertTrue(CertDetails.matchesDN(DN, SAME_DN));
        assertTrue(CertDetails.matchesDN(SAME_DN, DN));
        assertFalse(CertDetails.matchesDN(DN, RDN));
        assertFalse(CertDetails.matchesDN(DN, CN));
        assertFalse(CertDetails.matchesDN(RDN, CN));
        assertFalse(CertDetails.matchesDN(NODE_PICO_DN, ENM_PICO_DN));
        assertTrue(CertDetails.matchesDN(PKI_DN, NODE_DN));
        assertTrue(CertDetails.matchesDN(PKI_DN, NODE_REVERSE_ORDER_DN));
        assertTrue(CertDetails.matchesDN(NODE_DN, NODE_REVERSE_ORDER_DN));
        assertTrue(CertDetails.matchesDN(PKI_SUPPORTED_EXTRA_FIELDS_DN, NODE_SUPPORTED_EXTRA_FIELDS_DN));
        assertTrue(CertDetails.matchesDN(PKI_SUPPORTED_EXTRA_FIELDS_DN, NODE_SUPPORTED_EXTRA_FIELDS_REVERSE_ORDER_DN));
        assertFalse(CertDetails.matchesDN(PKI_UNSUPPORTED_EXTRA_FIELDS_DN, NODE_UNSUPPORTED_EXTRA_FIELDS_DN));
        assertTrue(CertDetails.matchesDN(PKI_1_DN, NODE_1_DN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void partiallyMatchesDNWithNullPartial() {
        CertDetails.partiallyMatchesDN(null, DEC_SN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void partiallyMatchesDNWithNullFull() {
        CertDetails.partiallyMatchesDN(DEC_SN, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void partiallyMatchesDNWithEmptyPartial() {
        CertDetails.partiallyMatchesDN("", "ANY");
    }

    @Test
    public void partiallyMatchesDNWithInvalid() {
        assertFalse(CertDetails.partiallyMatchesDN("INVALID", "INVALID"));
    }

    @Test
    public void testPartiallyMatchesDN() {
        assertTrue(CertDetails.partiallyMatchesDN(DN, DN));
        assertTrue(CertDetails.partiallyMatchesDN(DN, SAME_DN));
        assertTrue(CertDetails.partiallyMatchesDN(SAME_DN, DN));
        assertFalse(CertDetails.partiallyMatchesDN(DN, RDN));
        assertTrue(CertDetails.partiallyMatchesDN(RDN, DN));
        assertFalse(CertDetails.partiallyMatchesDN(DN, CN));
        assertFalse(CertDetails.partiallyMatchesDN(RDN, CN));
        assertTrue(CertDetails.partiallyMatchesDN(NODE_PICO_DN, NODE_PICO_DN));
        assertTrue(CertDetails.partiallyMatchesDN(ENM_PICO_DN, ENM_PICO_DN));
        assertFalse(CertDetails.partiallyMatchesDN(ENM_PICO_DN, NODE_PICO_DN));
        assertTrue(CertDetails.partiallyMatchesDN(NODE_PICO_DN, ENM_PICO_DN));
        assertFalse(CertDetails.partiallyMatchesDN(ENM_PICO_DN, SAME_NODE_PICO_DN));
        assertTrue(CertDetails.partiallyMatchesDN(SAME_NODE_PICO_DN, ENM_PICO_DN));
        assertFalse(CertDetails.partiallyMatchesDN(NODE_PICO_DN, ""));
        assertFalse(CertDetails.partiallyMatchesDN(DIFFERENT_NODE_PICO_DN, ENM_PICO_DN));
        assertTrue(CertDetails.partiallyMatchesDN(PKI_DN, NODE_DN));
        assertTrue(CertDetails.partiallyMatchesDN(PKI_DN, NODE_REVERSE_ORDER_DN));
        assertTrue(CertDetails.partiallyMatchesDN(NODE_DN, NODE_REVERSE_ORDER_DN));
        assertTrue(CertDetails.partiallyMatchesDN(PKI_SUPPORTED_EXTRA_FIELDS_DN, NODE_SUPPORTED_EXTRA_FIELDS_DN));
        assertTrue(CertDetails.partiallyMatchesDN(PKI_SUPPORTED_EXTRA_FIELDS_DN, NODE_SUPPORTED_EXTRA_FIELDS_REVERSE_ORDER_DN));
        assertFalse(CertDetails.partiallyMatchesDN(PKI_UNSUPPORTED_EXTRA_FIELDS_DN, NODE_UNSUPPORTED_EXTRA_FIELDS_DN));
    }

    @Test
    public void matchesSN() {
        assertTrue(CertDetails.matchesSN(DEC_SN, DEC_SN));
        assertTrue(CertDetails.matchesSN(DEC_SN, HEX_SN));
        assertTrue(CertDetails.matchesSN(DEC_SN, HEX_0X_SN));
        assertTrue(CertDetails.matchesSN(DEC_SN, HEX_COLON_SN));
        assertTrue(CertDetails.matchesSN(HEX_SN, HEX_SN));
        assertTrue(CertDetails.matchesSN(HEX_SN, HEX_0X_SN));
        assertTrue(CertDetails.matchesSN(HEX_SN, HEX_COLON_SN));
        assertTrue(CertDetails.matchesSN(HEX_0X_SN, HEX_0X_SN));
        assertTrue(CertDetails.matchesSN(HEX_0X_SN, HEX_COLON_SN));
        assertTrue(CertDetails.matchesSN(HEX_COLON_SN, HEX_COLON_SN));
    }

    @Test
    public void testEqualsDN() {
        final Map<String, Object> opensslStr = new HashMap<String, Object>();
        opensslStr.put("subject",
                "dnQualifier=sdf, title=asdf, OU=asd, O=sadf, C=IN, SN=sdf, ST=asdf, CN=asdf, serialNumber=sdf, GN=asdf, street=asdf, L=asdf");
        opensslStr.put("issuer",
                "dnQualifier=sdf, title=asdf, OU=asd, O=sadf, C=IN, SN=sdf, ST=asdf, CN=asdf, serialNumber=sdf, GN=asdf, street=asdf, L=asdf");
        opensslStr.put("serialNumber", "d4064c7b736c652");
        final CertDetails opensslCert = new CertDetails(opensslStr);
        final Map<String, Object> javaStr = new HashMap<String, Object>();
        javaStr.put("subject",
                "L=asdf, STREET=asdf, GIVENNAME=asdf, SERIALNUMBER=sdf, CN=asdf, ST=asdf, SURNAME=sdf, C=IN, O=sadf, OU=asd, T=asdf, DNQ=sdf");
        javaStr.put("issuer",
                "L=asdf, STREET=asdf, GIVENNAME=asdf, SERIALNUMBER=sdf, CN=asdf, ST=asdf, SURNAME=sdf, C=IN, O=sadf, OU=asd, T=asdf, DNQ=sdf");
        javaStr.put("serialNumber", "954873929937634898");
        final CertDetails javaCert = new CertDetails(javaStr);
        assertTrue(opensslCert.equals(javaCert));

        final Map<String, Object> pkiCertMap = new HashMap<String, Object>();
        pkiCertMap.put("subject", "CN=mySubjectCN");
        pkiCertMap.put("issuer", PKI_DN);
        pkiCertMap.put("serialNumber", "d4064c7b736c652");
        final CertDetails pkiCert = new CertDetails(pkiCertMap);
        final Map<String, Object> nodeCertMap = new HashMap<String, Object>();
        nodeCertMap.put("subject", "CN=mySubjectCN");
        nodeCertMap.put("issuer", NODE_DN);
        nodeCertMap.put("serialNumber", "d4064c7b736c652");
        final CertDetails nodeCert = new CertDetails(nodeCertMap);
        assertTrue(pkiCert.equals(nodeCert));
        assertTrue(nodeCert.equals(pkiCert));

    }

    @Test
    public void testAlignNodeCertDNFieldNamesWithRFCWithDifferentDns() {
        assertTrue(CertDetails.alignNodeCertDNFieldNamesWithRFC(NODE_DISTINGUISHED_NAME).equals(PKI_DISTINGUISHED_NAME));
    }

    @Test
    public void testAlignNodeCertDNFieldNamesWithRFCWithSameDns() {
        assertTrue(CertDetails.alignNodeCertDNFieldNamesWithRFC(PKI_DISTINGUISHED_NAME).equals(PKI_DISTINGUISHED_NAME));
    }

    @Test
    public void testAlignNodeCertDNFieldNamesWithRFCWithNullAsDn() {
        final String nodeDistinguishedName = null;
        assertNull(null,CertDetails.alignNodeCertDNFieldNamesWithRFC(nodeDistinguishedName));
    }

    @Test
    public void testMatchesNotAlignedToRfcDN() {
        assertTrue(CertDetails.matchesNotAlignedToRfcDN(NODE_DISTINGUISHED_NAME, PKI_DISTINGUISHED_NAME));
        assertFalse(CertDetails.matchesNotAlignedToRfcDN(NODE_DISTINGUISHED_NAME, DN));
    }

    @Test
    public void testPartiallyMatchesNotAlignedToRfcDN() {
        assertTrue(CertDetails.partiallyMatchesNotAlignedToRfcDN(NODE_DISTINGUISHED_NAME, PKI_DISTINGUISHED_NAME));
        assertFalse(CertDetails.partiallyMatchesNotAlignedToRfcDN(NODE_DISTINGUISHED_NAME, DN));
    }
}
