package com.ericsson.nms.security.nscs.util;

import java.math.BigInteger;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.InvalidKeyException;

import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NscsTrustedEntityInfoTest {
    @InjectMocks
    NscsTrustedEntityInfo beanUnderTest;

    @Test
    public void testEquals() {
        beanUnderTest = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        NscsTrustedEntityInfo otherBean = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        assertEquals(beanUnderTest, otherBean);
    }

    @Test
    public void testDiffersFromNull() {
        beanUnderTest = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        NscsTrustedEntityInfo otherBean = null;
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByName() {
        beanUnderTest = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        NscsTrustedEntityInfo otherBean = new NscsTrustedEntityInfo("ENM_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersBySN() {
        beanUnderTest = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        NscsTrustedEntityInfo otherBean = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1235"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByIssuer() {
        beanUnderTest = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        NscsTrustedEntityInfo otherBean = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_Infrastructure_CA", "https://NE_OAM_CA");
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByTdpsUrl() {
        beanUnderTest = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA");
        NscsTrustedEntityInfo otherBean = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://ENM_OAM_CA");
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testEqualsforNewConstructor()
            throws InvalidKeyException, NoSuchProviderException, SecurityException, SignatureException, NoSuchAlgorithmException {

        beanUnderTest = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA", null,
                CertificateStatus.ACTIVE);
        NscsTrustedEntityInfo otherBean = new NscsTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", "https://NE_OAM_CA", null,
                CertificateStatus.ACTIVE);
        assertEquals(beanUnderTest, otherBean);
    }

    @Test
    public void testGetAndSetX509Cert() {
        beanUnderTest.setX509Certificate(null);
        assertEquals(null, beanUnderTest.getX509Certificate());
    }

    @Test
    public void testGetAndSetCertificateStatus() {
        beanUnderTest.setCertificateStatus(CertificateStatus.ACTIVE);
        assertEquals(CertificateStatus.ACTIVE, beanUnderTest.getCertificateStatus());
    }

    @Test
    public void testSetAndGetCrlUriIpv4() {
        TrustedEntityInfo trustedEntityInfo = new TrustedEntityInfo();
        trustedEntityInfo.setEntityName("NE_OAM_CA");
        trustedEntityInfo.setCertificateSerialNumber("1234");
        trustedEntityInfo.setIssuerDN("ENM_PKI_Root_CA");
        trustedEntityInfo.setIpv4TrustDistributionPointURL("https://NE_OAM_CA");
        trustedEntityInfo.setX509Certificate(null);
        trustedEntityInfo.setCertificateStatus(CertificateStatus.ACTIVE);

        trustedEntityInfo.setDnsCrlDistributionPointURL("dns");
        trustedEntityInfo.setIpv4CrlDistributionPointURL("ipv4");
        trustedEntityInfo.setIpv6CrlDistributionPointURL("ipv6");

        beanUnderTest = new NscsTrustedEntityInfo(trustedEntityInfo, false);
        boolean result = beanUnderTest.getCrlsUri().get(0).equals("ipv4");
        assertTrue(result);
    }
    @Test
    public void testSetAndGetCrlUriIpv6() {
        TrustedEntityInfo trustedEntityInfo = new TrustedEntityInfo();
        trustedEntityInfo.setEntityName("NE_OAM_CA");
        trustedEntityInfo.setCertificateSerialNumber("1234");
        trustedEntityInfo.setIssuerDN("ENM_PKI_Root_CA");
        trustedEntityInfo.setIpv4TrustDistributionPointURL("https://NE_OAM_CA");
        trustedEntityInfo.setX509Certificate(null);
        trustedEntityInfo.setCertificateStatus(CertificateStatus.ACTIVE);

        trustedEntityInfo.setDnsCrlDistributionPointURL("dns");
        trustedEntityInfo.setIpv4CrlDistributionPointURL("ipv4");
        trustedEntityInfo.setIpv6CrlDistributionPointURL("ipv6");

        beanUnderTest = new NscsTrustedEntityInfo(trustedEntityInfo, true);
        boolean result = beanUnderTest.getCrlsUri().get(0).equals("ipv6");
        assertTrue(result);
    }
    @Test
    public void testSetAndGetCrlUriDnsOnly() {
        TrustedEntityInfo trustedEntityInfo = new TrustedEntityInfo();
        trustedEntityInfo.setEntityName("NE_OAM_CA");
        trustedEntityInfo.setCertificateSerialNumber("1234");
        trustedEntityInfo.setIssuerDN("ENM_PKI_Root_CA");
        trustedEntityInfo.setIpv4TrustDistributionPointURL("https://NE_OAM_CA");
        trustedEntityInfo.setX509Certificate(null);
        trustedEntityInfo.setCertificateStatus(CertificateStatus.ACTIVE);

        trustedEntityInfo.setDnsCrlDistributionPointURL("dns");
        trustedEntityInfo.setIpv4CrlDistributionPointURL(null);
        trustedEntityInfo.setIpv6CrlDistributionPointURL(null);

        beanUnderTest = new NscsTrustedEntityInfo(trustedEntityInfo, false);
        boolean result = beanUnderTest.getCrlsUri().get(0).equals("dns");
        assertTrue(result);
    }
    @Test
    public void testSetAndGetCrlUriAllNull() {
        TrustedEntityInfo trustedEntityInfo = new TrustedEntityInfo();
        trustedEntityInfo.setEntityName("NE_OAM_CA");
        trustedEntityInfo.setCertificateSerialNumber("1234");
        trustedEntityInfo.setIssuerDN("ENM_PKI_Root_CA");
        trustedEntityInfo.setIpv4TrustDistributionPointURL("https://NE_OAM_CA");
        trustedEntityInfo.setX509Certificate(null);
        trustedEntityInfo.setCertificateStatus(CertificateStatus.ACTIVE);

        trustedEntityInfo.setDnsCrlDistributionPointURL(null);
        trustedEntityInfo.setIpv4CrlDistributionPointURL(null);
        trustedEntityInfo.setIpv6CrlDistributionPointURL(null);

        beanUnderTest = new NscsTrustedEntityInfo(trustedEntityInfo, false);
        boolean result = beanUnderTest.getCrlsUri().size() == 0;
        assertTrue(result);
    }
    @Test
    public void testSetAndGetCrlUriAllEmpty() {
        TrustedEntityInfo trustedEntityInfo = new TrustedEntityInfo();
        trustedEntityInfo.setEntityName("NE_OAM_CA");
        trustedEntityInfo.setCertificateSerialNumber("1234");
        trustedEntityInfo.setIssuerDN("ENM_PKI_Root_CA");
        trustedEntityInfo.setIpv4TrustDistributionPointURL("https://NE_OAM_CA");
        trustedEntityInfo.setX509Certificate(null);
        trustedEntityInfo.setCertificateStatus(CertificateStatus.ACTIVE);

        trustedEntityInfo.setDnsCrlDistributionPointURL("");
        trustedEntityInfo.setIpv4CrlDistributionPointURL("");
        trustedEntityInfo.setIpv6CrlDistributionPointURL("");

        beanUnderTest = new NscsTrustedEntityInfo(trustedEntityInfo, false);
        boolean result = beanUnderTest.getCrlsUri().size() == 0;
        assertTrue(result);
    }
}
