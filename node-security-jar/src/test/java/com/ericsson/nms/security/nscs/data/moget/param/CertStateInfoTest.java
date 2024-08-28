package com.ericsson.nms.security.nscs.data.moget.param;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.ExtendedCertDetails;

public class CertStateInfoTest {

    private static final String NODENAME = "Node123";
    private static final String STATE = "State";
    private static final String ERROR_MSG = "Error Message";
    private static final String SUBJECT = "CN=Node123,O=ERICSSON,OU=BUCI_DUAC_NAM,C=SE";
    private static final String ISSUER = "CN=NE_OAM_CA,O=ERICSSON,OU=BUCI_DUAC_NAM,C=SE";
    private static final String SERIAL = "19:b1:75:26:58:5e:1e:32";
    private static final String SUBJECT_ALT_NAME = "1.2.3.4";

    private CertDetails validCertDetails = ExtendedCertDetails.certDetailsFactory(ISSUER, SERIAL, SUBJECT, SUBJECT_ALT_NAME);
    private CertDetails invalidCertDetails = ExtendedCertDetails.certDetailsFactory();

    @Test
    public void testCertStateInfoNullString() {
        CertStateInfo certStateInfo = new CertStateInfo(null);
        assertEquals(CertStateInfo.NOT_AVAILABLE, certStateInfo.getNodeName());
        testNotAvailableCertStateInfo(certStateInfo);
    }

    @Test
    public void testCertStateInfoEmptyString() {
        CertStateInfo certStateInfo = new CertStateInfo("");
        assertEquals(CertStateInfo.NOT_AVAILABLE, certStateInfo.getNodeName());
        testNotAvailableCertStateInfo(certStateInfo);
    }

    @Test
    public void testCertStateInfoString() {
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        testNotAvailableCertStateInfo(certStateInfo);
    }

    @Test
    public void testCertStateInfoStringNullStringNullStringNullCertDetails() {
        CertDetails certDetails = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, null, null, certDetails);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        testNotAvailableCertStateInfo(certStateInfo);
    }

    @Test
    public void testCertStateInfoStringStringStringNullCertDetails() {
        CertDetails certDetails = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certDetails);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        assertEquals(STATE, certStateInfo.getState());
        assertEquals(ERROR_MSG, certStateInfo.getErrorMsg());
        testNotAvailableCertificates(certStateInfo.getCertificates());
    }

    @Test
    public void testCertStateInfoStringStringStringCertDetails() {
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, validCertDetails);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        assertEquals(STATE, certStateInfo.getState());
        assertEquals(ERROR_MSG, certStateInfo.getErrorMsg());
        testCertificates(certStateInfo.getCertificates(), 1, validCertDetails);
    }

    @Test
    public void testCertStateInfoStringNullStringNullStringNullListCertDetails() {
        List<CertDetails> certificates = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, null, null, certificates);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        testNotAvailableCertStateInfo(certStateInfo);
    }

    @Test
    public void testCertStateInfoStringStringStringNullListCertDetails() {
        List<CertDetails> certificates = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        assertEquals(STATE, certStateInfo.getState());
        assertEquals(ERROR_MSG, certStateInfo.getErrorMsg());
        testNotAvailableCertificates(certStateInfo.getCertificates());
    }

    @Test
    public void testCertStateInfoStringStringStringListCertDetails() {
        List<CertDetails> certificates = new ArrayList<>();
        certificates.add(validCertDetails);
        certificates.add(validCertDetails);
        certificates.add(validCertDetails);
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        assertEquals(STATE, certStateInfo.getState());
        assertEquals(ERROR_MSG, certStateInfo.getErrorMsg());
        testCertificates(certStateInfo.getCertificates(), certificates.size(), validCertDetails);
    }

    @Test
    public void testCertStateInfoStringStringStringListSomeInvalidCertDetails() {
        List<CertDetails> certificates = new ArrayList<>();
        certificates.add(validCertDetails);
        certificates.add(invalidCertDetails);
        certificates.add(validCertDetails);
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertEquals(NODENAME, certStateInfo.getNodeName());
        assertEquals(STATE, certStateInfo.getState());
        assertEquals(ERROR_MSG, certStateInfo.getErrorMsg());
        testCertificates(certStateInfo.getCertificates(), certificates.size(), validCertDetails, invalidCertDetails);
    }

    @Test
    public void testIsInvalidCertStateInfoNullString() {
        CertStateInfo certStateInfo = new CertStateInfo(null);
        assertTrue(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoEmptyString() {
        CertStateInfo certStateInfo = new CertStateInfo("");
        assertTrue(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoString() {
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME);
        assertTrue(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoStringNullStringNullStringNullCertDetails() {
        CertDetails certDetails = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, null, null, certDetails);
        assertTrue(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoStringStringStringNullCertDetails() {
        CertDetails certDetails = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certDetails);
        assertTrue(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoStringStringStringCertDetails() {
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, validCertDetails);
        assertFalse(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoStringNullStringNullStringNullListCertDetails() {
        List<CertDetails> certificates = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, null, null, certificates);
        assertTrue(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoStringStringStringNullListCertDetails() {
        List<CertDetails> certificates = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertTrue(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoStringStringStringListCertDetails() {
        List<CertDetails> certificates = new ArrayList<>();
        certificates.add(validCertDetails);
        certificates.add(validCertDetails);
        certificates.add(validCertDetails);
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertFalse(certStateInfo.isInvalid());
    }

    @Test
    public void testIsInvalidCertStateInfoStringStringStringListSomeInvalidCertDetails() {
        List<CertDetails> certificates = new ArrayList<>();
        certificates.add(validCertDetails);
        certificates.add(invalidCertDetails);
        certificates.add(validCertDetails);
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertFalse(certStateInfo.isInvalid());
    }

    @Test
    public void testIsNotAvailableCertStateInfoNullString() {
        CertStateInfo certStateInfo = new CertStateInfo(null);
        assertTrue(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoEmptyString() {
        CertStateInfo certStateInfo = new CertStateInfo("");
        assertTrue(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoString() {
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME);
        assertTrue(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoStringNullStringNullStringNullCertDetails() {
        CertDetails certDetails = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, null, null, certDetails);
        assertTrue(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailabledCertStateInfoStringStringStringNullCertDetails() {
        CertDetails certDetails = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certDetails);
        assertTrue(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoStringStringStringCertDetails() {
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, validCertDetails);
        assertFalse(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoStringNullStringNullStringNullListCertDetails() {
        List<CertDetails> certificates = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, null, null, certificates);
        assertTrue(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoStringStringStringNullListCertDetails() {
        List<CertDetails> certificates = null;
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertTrue(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoStringStringStringListCertDetails() {
        List<CertDetails> certificates = new ArrayList<>();
        certificates.add(validCertDetails);
        certificates.add(validCertDetails);
        certificates.add(validCertDetails);
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertFalse(certStateInfo.isNotAvailable());
    }

    @Test
    public void testIsNotAvailableCertStateInfoStringStringStringListSomeInvalidCertDetails() {
        List<CertDetails> certificates = new ArrayList<>();
        certificates.add(validCertDetails);
        certificates.add(invalidCertDetails);
        certificates.add(validCertDetails);
        CertStateInfo certStateInfo = new CertStateInfo(NODENAME, STATE, ERROR_MSG, certificates);
        assertFalse(certStateInfo.isNotAvailable());
    }

    private void testNotAvailableCertStateInfo(final CertStateInfo certStateInfo) {
        assertEquals(CertStateInfo.NOT_AVAILABLE, certStateInfo.getState());
        assertEquals(CertStateInfo.NOT_AVAILABLE, certStateInfo.getErrorMsg());
        assertNotNull(certStateInfo.getCertificates());
        assertEquals(1, certStateInfo.getCertificates().size());
        testNotAvailableCertificates(certStateInfo.getCertificates());
    }

    private void testNotAvailableCertificates(final List<CertDetails> certificates) {
        assertNotNull(certificates);
        Iterator<CertDetails> it = certificates.iterator();
        while (it.hasNext()) {
            CertDetails certDetails = it.next();
            assertNull(certDetails.getIssuer());
            assertNull(certDetails.getSerial());
            assertNull(certDetails.getSubject());
            if (certDetails instanceof ExtendedCertDetails) {
                assertNull(((ExtendedCertDetails) certDetails).getSubjectAltName());
            }
        }
    }

    private void testCertificates(final List<CertDetails> certificates, final int size, final CertDetails expectedCertDetails) {
        assertNotNull(certificates);
        assertEquals(size, certificates.size());
        Iterator<CertDetails> it = certificates.iterator();
        while (it.hasNext()) {
            CertDetails certDetails = it.next();
            assertEquals(expectedCertDetails.getIssuer(), certDetails.getIssuer());
            assertEquals(expectedCertDetails.getSerial(), certDetails.getSerial());
            assertEquals(expectedCertDetails.getSubject(), certDetails.getSubject());
            if (certDetails instanceof ExtendedCertDetails && expectedCertDetails instanceof ExtendedCertDetails) {
                assertEquals(((ExtendedCertDetails) expectedCertDetails).getSubjectAltName(), ((ExtendedCertDetails) certDetails).getSubjectAltName());
            }
        }
    }

    private void testCertificates(final List<CertDetails> certificates, final int size, final CertDetails validCertDetails, final CertDetails invalidCertDetails) {
        assertNotNull(certificates);
        assertEquals(size, certificates.size());
        Iterator<CertDetails> it = certificates.iterator();
        while (it.hasNext()) {
            CertDetails certDetails = it.next();
            if (certDetails.isInvalid()) {
                assertEquals(invalidCertDetails.getIssuer(), certDetails.getIssuer());
                assertEquals(invalidCertDetails.getSerial(), certDetails.getSerial());
                assertEquals(invalidCertDetails.getSubject(), certDetails.getSubject());
                if (certDetails instanceof ExtendedCertDetails && invalidCertDetails instanceof ExtendedCertDetails) {
                    assertEquals(((ExtendedCertDetails) invalidCertDetails).getSubjectAltName(), ((ExtendedCertDetails) certDetails).getSubjectAltName());
                }
            } else {
                assertEquals(validCertDetails.getIssuer(), certDetails.getIssuer());
                assertEquals(validCertDetails.getSerial(), certDetails.getSerial());
                assertEquals(validCertDetails.getSubject(), certDetails.getSubject());
                if (certDetails instanceof ExtendedCertDetails && validCertDetails instanceof ExtendedCertDetails) {
                    assertEquals(((ExtendedCertDetails) validCertDetails).getSubjectAltName(), ((ExtendedCertDetails) certDetails).getSubjectAltName());
                }
            }
        }
    }
}
