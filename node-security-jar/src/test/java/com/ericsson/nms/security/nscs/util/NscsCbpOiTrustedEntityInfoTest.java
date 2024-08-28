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
package com.ericsson.nms.security.nscs.util;

import org.junit.Test;
import java.math.BigInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NscsCbpOiTrustedEntityInfoTest {

    @InjectMocks
    NscsCbpOiTrustedEntityInfo beanUnderTest;

    static final String pemCertificate = "PEM_CERTIFICATE";
    static final String trustCategoryName = "oamCmpCaTrustCategory";
    static final String entityName = "NE_OAM_CA";
 

    @Test
    public void testEquals() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        NscsCbpOiTrustedEntityInfo otherBean = new NscsCbpOiTrustedEntityInfo("NE_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        assertEquals(beanUnderTest, otherBean);
    }

    @Test
    public void testDiffersFromNull() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        NscsCbpOiTrustedEntityInfo otherBean = null;
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByName() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        NscsCbpOiTrustedEntityInfo otherBean = new NscsCbpOiTrustedEntityInfo("ENM_OAM_CA", new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersBySN() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        NscsCbpOiTrustedEntityInfo otherBean = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1235"), "ENM_PKI_Root_CA", pemCertificate);
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByIssuer() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        NscsCbpOiTrustedEntityInfo otherBean = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_Infrastructure_CA", pemCertificate);
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByCertificate() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        NscsCbpOiTrustedEntityInfo otherBean = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_Infrastructure_CA", "PEM_CERTIFICATE_2");
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByTrustCategory() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        beanUnderTest.setTrustCategoryName(trustCategoryName);
        NscsCbpOiTrustedEntityInfo otherBean = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        otherBean.setTrustCategoryName("oamTrustCategory");
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testDiffersByInstalled() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        NscsCbpOiTrustedEntityInfo otherBean = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        otherBean.setInstalled(true);
        assertFalse(beanUnderTest.equals(otherBean));
    }

    @Test
    public void testStringify() {
        beanUnderTest = new NscsCbpOiTrustedEntityInfo(entityName, new BigInteger("1234"), "ENM_PKI_Root_CA", pemCertificate);
        beanUnderTest.setTrustCategoryName(trustCategoryName);
        final String trustCategoryFdn = "O=Ericsson, CN=" + trustCategoryName;
        beanUnderTest.setTrustCategoryFdn(trustCategoryFdn);
        final String stringifiedEntity = beanUnderTest.stringify();
        assertTrue(stringifiedEntity.startsWith("Trusted Entity : name [" + entityName + "]"));
        assertTrue(stringifiedEntity.contains("Trust Category name [" + trustCategoryName + "]"));
        assertTrue(stringifiedEntity.contains("Trust Category FDN [" + trustCategoryFdn + "]"));
        assertTrue(stringifiedEntity.contains("SN [" + "1234" + "]"));
        assertTrue(stringifiedEntity.contains("issuer [" + "ENM_PKI_Root_CA" + "]"));
        assertTrue(stringifiedEntity.contains("PEM Certificate [" + pemCertificate));
    }
}
