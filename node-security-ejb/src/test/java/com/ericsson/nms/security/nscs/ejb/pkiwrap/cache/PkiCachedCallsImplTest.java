/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ejb.pkiwrap.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

@RunWith(MockitoJUnitRunner.class)
public class PkiCachedCallsImplTest {

    private static final String OAM_CA_NAME = "ENM_OAM_CA";
    private static final String INFRA_CA_NAME = "ENM_Infrastructure_CA";
    private static final String ROOT_CA_NAME = "ENM_PKI_Root_CA";
    private static final String INTERMEDIATE_EXT_CA_NAME = "PrimeTowerIntermediateCA";
    private static final String ROOT_EXT_CA_NAME = "PrimeTowerRootCA";

    @Spy
    private final Logger log = LoggerFactory.getLogger(PkiCachedCallsImpl.class);

    @Mock
    NscsPkiEntitiesManagerJar nscsPkiEntitiesManagerJar;

    @InjectMocks
    PkiCachedCallsImpl pkiCachedCallsImpl;

    @Before
    public void setup() throws NscsPkiEntitiesManagerException, EntityNotFoundException, CertificateException {
        pkiCachedCallsImpl.initialize();
        final EntityCategory entitycategory = new EntityCategory();
        doReturn(entitycategory).when(nscsPkiEntitiesManagerJar).getPkiEntityCategory(NodeEntityCategory.OAM);
        final EntityCategory entitycategory1 = new EntityCategory();
        doReturn(entitycategory1).when(nscsPkiEntitiesManagerJar).getPkiEntityCategory(NodeEntityCategory.IPSEC);
        final EntityProfile entityProfile = new EntityProfile();
        doReturn(entityProfile).when(nscsPkiEntitiesManagerJar).getEntityProfile("epprofilename");
        final EntityProfile entityProfile1 = new EntityProfile();
        doReturn(entityProfile1).when(nscsPkiEntitiesManagerJar).getEntityProfile("epprofilename1");
        doReturn(false).when(nscsPkiEntitiesManagerJar).isEntityProfileNameAvailable("epprofilename");
        doReturn(false).when(nscsPkiEntitiesManagerJar).isEntityProfileNameAvailable("epprofilename1");
        doReturn(true).when(nscsPkiEntitiesManagerJar).isEntityProfileNameAvailable("epprofilename2");
        final Set<NscsPair<String, Boolean>> trustedCAs = new HashSet<>();
        doReturn(trustedCAs).when(nscsPkiEntitiesManagerJar).getTrustedCAs("epprofilename");
        final Set<NscsPair<String, Boolean>> trustedCA1s = new HashSet<>();
        doReturn(trustedCA1s).when(nscsPkiEntitiesManagerJar).getTrustedCAs("epprofilename1");
        final List<X509Certificate> certificates = new ArrayList<>();
        doReturn(certificates).when(nscsPkiEntitiesManagerJar).getCAChain("caname");
        final List<X509Certificate> certificates1 = new ArrayList<>();
        doReturn(certificates1).when(nscsPkiEntitiesManagerJar).getCAChain("caname1");
        final CAEntity cAEntity = new CAEntity();
        doReturn(cAEntity).when(nscsPkiEntitiesManagerJar).getCAEntity("subjectname");
        final CAEntity cAEntity1 = new CAEntity();
        doReturn(cAEntity1).when(nscsPkiEntitiesManagerJar).getCAEntity("subjectname1");
        final X509Certificate mockCert = Mockito.mock(X509Certificate.class);
        doReturn(mockCert).when(nscsPkiEntitiesManagerJar).findPkiRootCACertificate("caname");
        final X509Certificate mockCert1 = Mockito.mock(X509Certificate.class);
        doReturn(mockCert1).when(nscsPkiEntitiesManagerJar).findPkiRootCACertificate("caname1");

        final X509Certificate mockCertForInternalExternalTrustTest = Mockito.mock(X509Certificate.class);
        final List<X509Certificate> internalCATrustCertificateList1 = new ArrayList<X509Certificate>();
        internalCATrustCertificateList1.add(mockCertForInternalExternalTrustTest);
        final List<X509Certificate> internalCATrustCertificateList2 = new ArrayList<X509Certificate>();
        internalCATrustCertificateList2.add(mockCertForInternalExternalTrustTest);
        doReturn(internalCATrustCertificateList1).when(nscsPkiEntitiesManagerJar).getInternalCATrusts("internalCAName1");
        doReturn(internalCATrustCertificateList2).when(nscsPkiEntitiesManagerJar).getInternalCATrusts("internalCAName2");
        Mockito.when(nscsPkiEntitiesManagerJar.getInternalCATrusts("internalCANameNotExists"))
                .thenThrow(new NscsPkiEntitiesManagerException("EntityNotFoundException"));
        doReturn(internalCATrustCertificateList1).when(nscsPkiEntitiesManagerJar).getExternalCATrusts("externalCAName1");
        doReturn(internalCATrustCertificateList2).when(nscsPkiEntitiesManagerJar).getExternalCATrusts("externalCAName2");
        Mockito.when(nscsPkiEntitiesManagerJar.getExternalCATrusts("externalCANameNotExists"))
                .thenThrow(new NscsPkiEntitiesManagerException("EntityNotFoundException"));
    }

    private Set<NscsPair<String, Boolean>> generateTrustedCAsPair(final boolean isMS9, final boolean isChainRequired) {
        final Set<NscsPair<String, Boolean>> trustedCAsPair = new HashSet<>();
        if (isMS9) {
            if (isChainRequired) {
                trustedCAsPair.add(new NscsPair<String, Boolean>(OAM_CA_NAME, false));
                trustedCAsPair.add(new NscsPair<String, Boolean>(INFRA_CA_NAME, false));
                trustedCAsPair.add(new NscsPair<String, Boolean>(ROOT_CA_NAME, true));
            } else {
                trustedCAsPair.add(new NscsPair<String, Boolean>(OAM_CA_NAME, false));
                trustedCAsPair.add(new NscsPair<String, Boolean>(INFRA_CA_NAME, false));
                trustedCAsPair.add(new NscsPair<String, Boolean>(ROOT_CA_NAME, false));
                trustedCAsPair.add(new NscsPair<String, Boolean>(INTERMEDIATE_EXT_CA_NAME, false));
                trustedCAsPair.add(new NscsPair<String, Boolean>(ROOT_EXT_CA_NAME, false));
            }
        } else {
            trustedCAsPair.add(new NscsPair<String, Boolean>(OAM_CA_NAME, false));
            trustedCAsPair.add(new NscsPair<String, Boolean>(INFRA_CA_NAME, false));
            trustedCAsPair.add(new NscsPair<String, Boolean>(ROOT_CA_NAME, false));
        }
        return trustedCAsPair;
    }

    @Test
    public void getPkiEntityCategoryTest() throws NscsPkiEntitiesManagerException {

        final EntityCategory ec = pkiCachedCallsImpl.getPkiEntityCategory(NodeEntityCategory.OAM);
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getPkiEntityCategory(Mockito.any(NodeEntityCategory.class));
        final EntityCategory ec2 = pkiCachedCallsImpl.getPkiEntityCategory(NodeEntityCategory.OAM);
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getPkiEntityCategory(Mockito.any(NodeEntityCategory.class));

        assertTrue("The cache is NOT OK", ec == ec2);

        final EntityCategory ec3 = pkiCachedCallsImpl.getPkiEntityCategory(NodeEntityCategory.IPSEC);
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getPkiEntityCategory(Mockito.any(NodeEntityCategory.class));
        assertTrue("The cache is NOT OK", ec != ec3);

        pkiCachedCallsImpl.purgeAll(true);

        final EntityCategory ec4 = pkiCachedCallsImpl.getPkiEntityCategory(NodeEntityCategory.IPSEC);
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getPkiEntityCategory(Mockito.any(NodeEntityCategory.class));
        assertTrue("The cache is NOT OK", ec4 == ec3);

        pkiCachedCallsImpl.purgeAll(false);

        pkiCachedCallsImpl.getPkiEntityCategory(NodeEntityCategory.IPSEC);
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getPkiEntityCategory(Mockito.any(NodeEntityCategory.class));

    }

    @Test
    public void getPkiEntityCategoryNegativeTest() throws NscsPkiEntitiesManagerException {
        final EntityCategory ec = pkiCachedCallsImpl.getPkiEntityCategory(null);
        assertTrue(ec == null);
    }

    @Test
    public void getPkiEntityProfileTest() throws NscsPkiEntitiesManagerException {
        final EntityProfile ep = pkiCachedCallsImpl.getPkiEntityProfile("epprofilename");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getEntityProfile(Mockito.any(String.class));
        final EntityProfile ep1 = pkiCachedCallsImpl.getPkiEntityProfile("epprofilename");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getEntityProfile(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", ep == ep1);
        final EntityProfile ep2 = pkiCachedCallsImpl.getPkiEntityProfile("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getEntityProfile(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", ep2 != ep1);

        pkiCachedCallsImpl.purgeAll(true);

        final EntityProfile ep3 = pkiCachedCallsImpl.getPkiEntityProfile("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getEntityProfile(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", ep3 == ep2);

        pkiCachedCallsImpl.purgeAll(false);
        pkiCachedCallsImpl.getPkiEntityProfile("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getEntityProfile(Mockito.any(String.class));

    }

    @Test
    public void getPkiEntityProfileNegativeTest() throws NscsPkiEntitiesManagerException {
        final EntityProfile ep = pkiCachedCallsImpl.getPkiEntityProfile(null);
        assertTrue(ep == null);
    }

    @Test
    public void getTrustedCAsTest() throws NscsPkiEntitiesManagerException {
        final Set<NscsPair<String, Boolean>> trustedCAs = pkiCachedCallsImpl.getTrustedCAs("epprofilename");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getTrustedCAs(Mockito.any(String.class));
        final Set<NscsPair<String, Boolean>> trustedCA1s = pkiCachedCallsImpl.getTrustedCAs("epprofilename");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getTrustedCAs(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", trustedCAs == trustedCA1s);
        final Set<NscsPair<String, Boolean>> trustedCA2s = pkiCachedCallsImpl.getTrustedCAs("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getTrustedCAs(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", trustedCA2s != trustedCA1s);

        pkiCachedCallsImpl.purgeAll(true);

        final Set<NscsPair<String, Boolean>> trustedCA3s = pkiCachedCallsImpl.getTrustedCAs("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getTrustedCAs(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", trustedCA3s == trustedCA2s);

        pkiCachedCallsImpl.purgeAll(false);
        pkiCachedCallsImpl.getTrustedCAs("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getTrustedCAs(Mockito.any(String.class));

    }

    @Test
    public void getTrustedCAsNegativeTest() throws NscsPkiEntitiesManagerException {
        final Set<NscsPair<String, Boolean>> trustedCAs = pkiCachedCallsImpl.getTrustedCAs(null);
        assertTrue(trustedCAs == null);
    }

    // getCAChain
    @Test
    public void getCAChainTest() throws NscsPkiEntitiesManagerException, EntityNotFoundException, CertificateException {
        final List<CertificateChain> certificates = pkiCachedCallsImpl.getCAChain("caname");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getCAChain(Mockito.any(String.class));
        final List<CertificateChain> certificates1 = pkiCachedCallsImpl.getCAChain("caname");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getCAChain(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", certificates == certificates1);
        final List<CertificateChain> certificates2 = pkiCachedCallsImpl.getCAChain("caname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getCAChain(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", certificates2 != certificates1);

        pkiCachedCallsImpl.purgeAll(true);
        final List<CertificateChain> certificates3 = pkiCachedCallsImpl.getCAChain("caname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getCAChain(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", certificates3 == certificates2);

        pkiCachedCallsImpl.purgeAll(false);
        pkiCachedCallsImpl.getCAChain("caname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getCAChain(Mockito.any(String.class));
    }

    @Test
    public void getCATrustsNegativeTest() throws NscsPkiEntitiesManagerException, EntityNotFoundException, CertificateException {
        final List<CertificateChain> certificates = pkiCachedCallsImpl.getCAChain(null);
        assertTrue(certificates == null);
    }

    @Test
    public void getCAEntityTest() throws NscsPkiEntitiesManagerException {
        final CAEntity cAEntity = pkiCachedCallsImpl.getCAEntity("subjectname");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getCAEntity(Mockito.any(String.class));
        final CAEntity cAEntity1 = pkiCachedCallsImpl.getCAEntity("subjectname");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getCAEntity(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", cAEntity == cAEntity1);

        final CAEntity cAEntity2 = pkiCachedCallsImpl.getCAEntity("subjectname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getCAEntity(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", cAEntity2 != cAEntity1);

        pkiCachedCallsImpl.purgeAll(true);

        final CAEntity cAEntity3 = pkiCachedCallsImpl.getCAEntity("subjectname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getCAEntity(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", cAEntity3 == cAEntity2);

        pkiCachedCallsImpl.purgeAll(false);
        pkiCachedCallsImpl.getCAEntity("subjectname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getCAEntity(Mockito.any(String.class));

    }

    @Test
    public void getCAEntityNegativeTest() throws NscsPkiEntitiesManagerException {
        final CAEntity cAEntity = pkiCachedCallsImpl.getCAEntity(null);
        assertTrue(cAEntity == null);
    }

    @Test
    public void getRootCACertificateTest() throws NscsPkiEntitiesManagerException {
        final X509Certificate x509Certificate = pkiCachedCallsImpl.getRootCACertificate("caname");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).findPkiRootCACertificate(Mockito.any(String.class));
        final X509Certificate x509Certificate1 = pkiCachedCallsImpl.getRootCACertificate("caname");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).findPkiRootCACertificate(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", x509Certificate == x509Certificate1);

        final X509Certificate x509Certificate2 = pkiCachedCallsImpl.getRootCACertificate("caname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).findPkiRootCACertificate(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", x509Certificate2 != x509Certificate1);

        pkiCachedCallsImpl.purgeAll(true);

        final X509Certificate x509Certificate3 = pkiCachedCallsImpl.getRootCACertificate("caname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).findPkiRootCACertificate(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", x509Certificate3 == x509Certificate2);

        pkiCachedCallsImpl.purgeAll(false);
        pkiCachedCallsImpl.getRootCACertificate("caname1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).findPkiRootCACertificate(Mockito.any(String.class));

    }

    @Test
    public void getRootCACertificateNegativeTest() throws NscsPkiEntitiesManagerException {
        final X509Certificate x509Certificate = pkiCachedCallsImpl.getRootCACertificate(null);
        assertTrue(x509Certificate == null);
    }

    @Test
    public void isEntityProfileNameAvailableTest() throws NscsPkiEntitiesManagerException {
        final boolean isAvailable = pkiCachedCallsImpl.isEntityProfileNameAvailable("epprofilename");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).isEntityProfileNameAvailable(Mockito.any(String.class));
        assertFalse(isAvailable);
        pkiCachedCallsImpl.getPkiEntityProfile("epprofilename");
        final boolean isAvailable1 = pkiCachedCallsImpl.isEntityProfileNameAvailable("epprofilename");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).isEntityProfileNameAvailable(Mockito.any(String.class));
        assertFalse(isAvailable1);
        final boolean isAvailable2 = pkiCachedCallsImpl.isEntityProfileNameAvailable("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).isEntityProfileNameAvailable(Mockito.any(String.class));
        assertFalse(isAvailable2);

        pkiCachedCallsImpl.purgeAll(true);

        final boolean isAvailable3 = pkiCachedCallsImpl.isEntityProfileNameAvailable("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).isEntityProfileNameAvailable(Mockito.any(String.class));
        assertFalse(isAvailable3);

        pkiCachedCallsImpl.purgeAll(false);
        final boolean isAvailable4 = pkiCachedCallsImpl.isEntityProfileNameAvailable("epprofilename1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(4)).isEntityProfileNameAvailable(Mockito.any(String.class));
        assertFalse(isAvailable4);

        final boolean isAvailable5 = pkiCachedCallsImpl.isEntityProfileNameAvailable("epprofilename2");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(5)).isEntityProfileNameAvailable(Mockito.any(String.class));
        assertTrue(isAvailable5);

        final boolean isAvailable6 = pkiCachedCallsImpl.isEntityProfileNameAvailable("epprofilename2");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(6)).isEntityProfileNameAvailable(Mockito.any(String.class));
        assertTrue(isAvailable6);

        final boolean isAvailable7 = pkiCachedCallsImpl.isEntityProfileNameAvailable(null);
        assertFalse(isAvailable7);
    }

    @Test
    public void getInternalCATrustsTest() throws NscsPkiEntitiesManagerException, CertificateException {

        final List<X509Certificate> internalCertificates1 = pkiCachedCallsImpl.getInternalCATrusts("internalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getInternalCATrusts(Mockito.any(String.class));
        final List<X509Certificate> internalCertificates2 = pkiCachedCallsImpl.getInternalCATrusts("internalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getInternalCATrusts(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", internalCertificates2 == internalCertificates1);
        final List<X509Certificate> internalCertificates3 = pkiCachedCallsImpl.getInternalCATrusts("internalCAName2");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getInternalCATrusts(Mockito.any(String.class));
        assertFalse("The cache is NOT OK", internalCertificates3 == internalCertificates1);

        try {
            final List<X509Certificate> internalCertificates4 = pkiCachedCallsImpl.getInternalCATrusts("internalCANameNotExists");
        } catch (final NscsPkiEntitiesManagerException e) {
            assertTrue(e.getMessage().contains("EntityNotFoundException"));
        }
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getInternalCATrusts(Mockito.any(String.class));
        //assertTrue("Expected EntityNotFoundException not catched", internalCertificates4 == null);
        try {

            final List<X509Certificate> internalCertificates5 = pkiCachedCallsImpl.getInternalCATrusts(null);
        } catch (final NscsPkiEntitiesManagerException e) {
            assertTrue(e.getMessage().contains("EntityNotFoundException"));
        }
        //assertTrue("Expected EntityNotFoundException not catched", internalCertificates5 == null);

        pkiCachedCallsImpl.purgeAll(true);
        final List<X509Certificate> internalCertificates6 = pkiCachedCallsImpl.getInternalCATrusts("internalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getInternalCATrusts(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", internalCertificates6 == internalCertificates1);
        pkiCachedCallsImpl.purgeAll(false);
        final List<X509Certificate> internalCertificates7 = pkiCachedCallsImpl.getInternalCATrusts("internalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(4)).getInternalCATrusts(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", internalCertificates7 == internalCertificates1);
    }

    @Test
    public void getExternalCATrustsTest() throws NscsPkiEntitiesManagerException, CertificateException {

        final List<X509Certificate> externalCertificates1 = pkiCachedCallsImpl.getExternalCATrusts("externalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getExternalCATrusts(Mockito.any(String.class));
        final List<X509Certificate> externalCertificates2 = pkiCachedCallsImpl.getExternalCATrusts("externalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(1)).getExternalCATrusts(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", externalCertificates2 == externalCertificates1);
        final List<X509Certificate> externalCertificates3 = pkiCachedCallsImpl.getExternalCATrusts("externalCAName2");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(2)).getExternalCATrusts(Mockito.any(String.class));
        assertFalse("The cache is NOT OK", externalCertificates3 == externalCertificates1);

        try {
            final List<X509Certificate> externalCertificates4 = pkiCachedCallsImpl.getExternalCATrusts("externalCANameNotExists");
        } catch (final NscsPkiEntitiesManagerException e) {
            assertTrue(e.getMessage().contains("EntityNotFoundException"));
        }
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getExternalCATrusts(Mockito.any(String.class));
        //        assertTrue("Expected EntityNotFoundException not catched", externalCertificates4 == null);
        try {

            final List<X509Certificate> externalCertificates5 = pkiCachedCallsImpl.getExternalCATrusts(null);
        } catch (final NscsPkiEntitiesManagerException e) {
            assertTrue(e.getMessage().contains("EntityNotFoundException"));
        }
        //    assertTrue("Expected EntityNotFoundException not catched", externalCertificates5 == null);

        pkiCachedCallsImpl.purgeAll(true);
        final List<X509Certificate> externalCertificates6 = pkiCachedCallsImpl.getExternalCATrusts("externalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(3)).getExternalCATrusts(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", externalCertificates6 == externalCertificates1);
        pkiCachedCallsImpl.purgeAll(false);
        final List<X509Certificate> externalCertificates7 = pkiCachedCallsImpl.getExternalCATrusts("externalCAName1");
        Mockito.verify(nscsPkiEntitiesManagerJar, Mockito.times(4)).getExternalCATrusts(Mockito.any(String.class));
        assertTrue("The cache is NOT OK", externalCertificates7 == externalCertificates1);
    }
}
