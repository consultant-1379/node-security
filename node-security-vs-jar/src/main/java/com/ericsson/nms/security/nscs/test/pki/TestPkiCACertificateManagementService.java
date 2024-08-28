/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.test.pki;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.*;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.request.PKCS10CertificationRequestHolder;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.model.CAReIssueInfo;
import com.ericsson.oss.itpf.security.pki.manager.exception.InvalidOperationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.algorithm.AlgorithmNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.*;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.CANotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.InvalidCAException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.*;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.*;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.MissingMandatoryFieldException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificaterequest.CertificateRequestGenerationException;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.itpf.security.pki.manager.model.certificate.*;
import com.ericsson.oss.services.security.pkimock.api.MockCACertificateManagementService;

/**
 *
 * @author enmadmin
 */
@Stateless
public class TestPkiCACertificateManagementService implements CACertificateManagementService, CertificateManagementService {

    @EServiceRef
    MockCACertificateManagementService mockCertService;

    @Override
    public Certificate generateCertificate(final String string)
            throws AlgorithmNotFoundException, CANotFoundException, CertificateGenerationException, CertificateServiceException,
            ExpiredCertificateException, InvalidCAException, InvalidEntityAttributeException, RevokedCertificateException {
        return mockCertService.generateCertificate(string);
    }

    @Override
    public void importCertificate(final String string, final X509Certificate xc, final boolean bln, final CAReIssueType carit)
            throws AlgorithmNotFoundException, CANotFoundException, CertificateGenerationException, CertificateNotFoundException,
            CertificateServiceException, ExpiredCertificateException, InvalidCAException, IssuerCertificateRevokedException, InvalidEntityException,
            InvalidEntityAttributeException, InvalidInvalidityDateException, InvalidOperationException, RevokedCertificateException,
            RootCertificateRevocationException, RevocationServiceException {
        mockCertService.importCertificate(string, xc, bln, carit);
    }

    @Override
    public List<Certificate> listCertificates(final String string, final CertificateStatus... css)
            throws CertificateNotFoundException, CertificateServiceException, EntityNotFoundException, InvalidEntityAttributeException {
        return mockCertService.listCertificates_v1(string, css);
    }

    @Override
    public List<Certificate> listCertificates_v1(final String string, final CertificateStatus... css)
            throws CertificateServiceException, EntityNotFoundException {
        return mockCertService.listCertificates_v1(string, css);
    }

    @Override
    public void renewCertificate(final String string, final ReIssueType rit)
            throws AlgorithmNotFoundException, CANotFoundException, CertificateGenerationException, CertificateServiceException,
            ExpiredCertificateException, InvalidCAException, InvalidEntityException, InvalidEntityAttributeException, RevokedCertificateException {
        mockCertService.renewCertificate(string, rit);
    }

    //    public void reKeyCertificate(String string, ReIssueType rit) throws AlgorithmNotFoundException,
    //            CANotFoundException, CertificateGenerationException, CertificateServiceException,
    //            InvalidCAException, KeyPairGenerationException {
    //        mockCertService.reKeyCertificate(string, rit);
    //    }

    @Override
    public void publishCertificate(final String arg0) throws CANotFoundException, CertificateServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unPublishCertificate(final String arg0) throws CANotFoundException, CertificateServiceException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#rekeyCertificate(java.lang.String,
     * com.ericsson.oss.itpf.security.pki.common.model.certificate.ReIssueType)
     */
    @Override
    public void rekeyCertificate(final String arg0, final ReIssueType arg1)
            throws AlgorithmNotFoundException, CANotFoundException, CertificateGenerationException, CertificateServiceException,
            ExpiredCertificateException, InvalidCAException, InvalidEntityException, InvalidEntityAttributeException, RevokedCertificateException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#rekeyCertificate(com.ericsson.oss.itpf.
     * security.pki.manager.certificatemanagement.api.model.CAReIssueInfo, com.ericsson.oss.itpf.security.pki.common.model.certificate.ReIssueType)
     */
    @Override
    public void rekeyCertificate(final CAReIssueInfo arg0, final ReIssueType arg1) throws AlgorithmNotFoundException, CANotFoundException,
            CertificateGenerationException, CertificateNotFoundException, CertificateServiceException, ExpiredCertificateException,
            InvalidCAException, InvalidEntityException, InvalidEntityAttributeException, InvalidInvalidityDateException,
            IssuerCertificateRevokedException, RevokedCertificateException, RevocationServiceException, RootCertificateRevocationException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#renewCertificate(com.ericsson.oss.itpf.
     * security.pki.manager.certificatemanagement.api.model.CAReIssueInfo, com.ericsson.oss.itpf.security.pki.common.model.certificate.ReIssueType)
     */
    @Override
    public void renewCertificate(final CAReIssueInfo arg0, final ReIssueType arg1) throws AlgorithmNotFoundException, CANotFoundException,
            CertificateGenerationException, CertificateNotFoundException, CertificateServiceException, InvalidCAException,
            IssuerCertificateRevokedException, RevocationServiceException, RootCertificateRevocationException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#renewCertificates(java.util.Set)
     */
    @Override
    public void renewCertificates(final Set<String> arg0)
            throws AlgorithmNotFoundException, CANotFoundException, CertificateGenerationException, CertificateServiceException, InvalidCAException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#renewCertificates(java.util.List)
     */
    @Override
    public void renewCertificates(final List<CAReIssueInfo> arg0) throws AlgorithmNotFoundException, CANotFoundException,
            CertificateGenerationException, CertificateNotFoundException, CertificateServiceException, ExpiredCertificateException,
            InvalidCAException, RevokedCertificateException, RevocationServiceException, RootCertificateRevocationException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#listIssuedCertificates(com.ericsson.oss.
     * itpf.security.pki.common.model.certificate.CACertificateIdentifier,
     * com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus[])
     */
    @Override
    public List<CertificateInfo> listIssuedCertificates(final CACertificateIdentifier arg0, final CertificateStatus... arg1)
            throws CertificateNotFoundException, CertificateServiceException, CANotFoundException, MissingMandatoryFieldException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#listIssuedCertificates(com.ericsson.oss.
     * itpf.security.pki.manager.model.certificate.DNBasedCertificateIdentifier,
     * com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus[])
     */
    @Override
    public List<CertificateInfo> listIssuedCertificates(final DNBasedCertificateIdentifier arg0, final CertificateStatus... arg1)
            throws CertificateNotFoundException, CertificateServiceException, CANotFoundException, MissingMandatoryFieldException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#getCertificateChainList(java.lang.String,
     * com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus[])
     */
    @Override
    public List<CertificateChain> getCertificateChainList(final String entityName, final CertificateStatus... certificateStatus)
            throws CertificateServiceException, InvalidCAException, InvalidCertificateStatusException, InvalidEntityException,
            InvalidEntityAttributeException {
        return mockCertService.getCertificateChainList(entityName, certificateStatus);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService#getCertificateChain(java.lang.String)
     */
    @Override
    public List<Certificate> getCertificateChain(final String entityName) throws CertificateServiceException, InvalidCAException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteCertificate(String s, String s1) throws CertificateServiceException, EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteCertificates(String s, CertificateStatus certificateStatus) throws CertificateServiceException, EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PKCS10CertificationRequestHolder generateCSR(final String string, final boolean bln)
            throws AlgorithmNotFoundException, CANotFoundException, CertificateRequestGenerationException, CertificateServiceException,
            InvalidCAException, InvalidEntityAttributeException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PKCS10CertificationRequestHolder getCSR(final String string)
            throws CANotFoundException, CertificateRequestGenerationException, CertificateServiceException, InvalidOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void forceImportCertificate(final String string, final X509Certificate xc, final boolean bln, final CAReIssueType carit)
            throws AlgorithmNotFoundException, CANotFoundException, CertificateGenerationException, CertificateNotFoundException,
            CertificateServiceException, ExpiredCertificateException, InvalidCAException, IssuerCertificateRevokedException, InvalidEntityException,
            InvalidEntityAttributeException, InvalidInvalidityDateException, InvalidOperationException, RevokedCertificateException,
            RevocationServiceException, RootCertificateRevocationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
