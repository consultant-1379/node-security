/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.pki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.crl.revocation.RevocationReason;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.EntityCertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.crlmanagement.api.RevocationService;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Test class to test the methods related to PKI entity and certificate management.
 * 
 * @author enmadmin
 */
@RunWith(MockitoJUnitRunner.class)
public class NscsPkiCertificateManagerJarTest {

    @Mock
    Logger logger;

    @Mock
    EntityCertificateManagementService entityCertificateManagementService;

    @Mock
    PkiApiManagers pkiApiManagers;

    @InjectMocks
    NscsPkiCertificateManagerJar nscsPkiCertificateManagerJar;

    @Mock
    NscsContextService ctxService;

    @Mock
    RevocationService revocationService;

    public static final String CERTIFICATE_TYPE = "X.509";
    private static Certificate certificate;
    
    /**
     * Method to test listCertificates for Entity.
     *
     * @throws CertificateException
     * @throws IOException
     * @throws NscsPkiCertificateManagerException
     */
    @Test
    public void testListCertificates() throws CertificateException, IOException, NscsPkiCertificateManagerException {

        final String entityName = "Entity";
        final List<Certificate> certificates = new ArrayList<Certificate>();

        certificate = getCertificate("certificates/Entity.crt");
        certificates.add(certificate);
        Mockito.when(pkiApiManagers.getEntityCertificateManagementService()).thenReturn(entityCertificateManagementService);
        Mockito.when(entityCertificateManagementService.listCertificates_v1(entityName, CertificateStatus.ACTIVE, CertificateStatus.INACTIVE)).thenReturn(certificates);

        nscsPkiCertificateManagerJar.listCertificates(entityName, CertificateStatus.ACTIVE, CertificateStatus.INACTIVE);

        assertNotNull(certificates);
        assertEquals(1, certificates.size());
    }

    /**
     * Method to test whether it returns an empty list if certificates are not found.
     *
     * @throws CertificateException
     * @throws IOException
     */
    @Test
    public void testListCertificates_EmptyList() throws CertificateException, IOException, NscsPkiCertificateManagerException {

        final String entityName = "Entity";
        List<Certificate> returnedCertificates = new ArrayList<Certificate>();

        Mockito.when(pkiApiManagers.getEntityCertificateManagementService()).thenReturn(entityCertificateManagementService);
        Mockito.when(entityCertificateManagementService.listCertificates_v1(entityName, CertificateStatus.ACTIVE, CertificateStatus.INACTIVE)).thenReturn(returnedCertificates);

        returnedCertificates = nscsPkiCertificateManagerJar.listCertificates(entityName, CertificateStatus.ACTIVE, CertificateStatus.INACTIVE);

        assertNotNull(returnedCertificates);
        assertEquals(returnedCertificates.size(), 0);
    }

    /**
     * Method to test revokeEntityCertificates.
     *
     * @throws CertificateException
     * @throws IOException
     * @throws NscsPkiCertificateManagerException
     */
    @Test
    public void testRevokeEntityCertificates_EmptyList() throws CertificateException, IOException, NscsPkiCertificateManagerException {
        final String entityName = "Entity";
        final RevocationReason revocationReason = RevocationReason.UNSPECIFIED;
        final Date invalidityDate = null;

        Mockito.when(pkiApiManagers.getPkiRevocationService()).thenReturn(revocationService);

        nscsPkiCertificateManagerJar.revokeEntityCertificates(entityName, revocationReason, invalidityDate);
        verify(revocationService).revokeEntityCertificates(entityName, revocationReason, invalidityDate);
    }

    /**
     * Map X509Certificate object to Certificate model.
     * 
     * @param x509certificate
     *            X509Certificate that to be mapped to model.
     * @return Certificate model mapped from X509Certificate.
     */
    private Certificate toCertificate(final X509Certificate x509certificate) {
        final Certificate certificate = new Certificate();
        certificate.setX509Certificate(x509certificate);
        certificate.setIssuedTime(x509certificate.getNotBefore());
        certificate.setNotBefore(x509certificate.getNotBefore());
        certificate.setNotAfter(x509certificate.getNotAfter());
        certificate.setSerialNumber(x509certificate.getSerialNumber().toString());
        certificate.setStatus(CertificateStatus.ACTIVE);
        return certificate;
    }

    /**
     * Generates Certificate model from the certificate file.
     * 
     * @param filename
     *            name of the certificate file.
     * @return Certificate model formed from the file.
     * @throws IOException
     * @throws CertificateException
     */
    private Certificate getCertificate(final String filename) throws IOException, CertificateException {
        final X509Certificate x509Certificate = getX509Certificate(filename);
        final Certificate certificate = toCertificate(x509Certificate);
        return certificate;
    }

    /**
     * Generates X509Certificate object from the certificate file.
     * 
     * @param filename
     *            name of the certificate file.
     * @return X509Certifcate object from certificate file.
     * @throws IOException
     * @throws CertificateException
     */
    private X509Certificate getX509Certificate(final String filename) throws IOException, CertificateException {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
        return (X509Certificate) certificateFactory.generateCertificate(inputStream);
    }

}
