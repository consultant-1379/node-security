/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.test.pki;

import java.util.List;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.request.CertificateRequest;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.EntityCertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.model.KeyStoreInfo;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.model.KeyStoreType;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.algorithm.AlgorithmNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.InvalidCAException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.InvalidOTPException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.OTPExpiredException;
import com.ericsson.oss.itpf.security.pki.manager.exception.external.credentialmgmt.ExternalCredentialMgmtServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateGenerationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.ExpiredCertificateException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.InvalidCertificateStatusException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.RevokedCertificateException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificaterequest.InvalidCertificateRequestException;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.services.security.pkimock.api.MockEntityCertificateManagementService;

/**
 *
 * @author enmadmin
 */
@Stateless
public class TestPkiEntityCertificateManagementService implements EntityCertificateManagementService {

    @EServiceRef
    MockEntityCertificateManagementService mockConfigService;

    @Override
    public Certificate generateCertificate(final String string, final CertificateRequest cr) throws AlgorithmNotFoundException,
            CertificateGenerationException, CertificateServiceException, EntityNotFoundException, ExpiredCertificateException, InvalidCAException,
            InvalidCertificateRequestException, InvalidEntityException, InvalidEntityAttributeException, RevokedCertificateException {
        return null;
    }

    @Override
    public KeyStoreInfo generateCertificate(final String string, final char[] chars, final KeyStoreType kst)
            throws AlgorithmNotFoundException, CertificateGenerationException, CertificateServiceException, EntityNotFoundException,
            ExpiredCertificateException, InvalidCAException, InvalidEntityException, InvalidEntityAttributeException, RevokedCertificateException {
        return null;
    }

    @Override
    public Certificate renewCertificate(final String string, final CertificateRequest cr)
            throws AlgorithmNotFoundException, CertificateGenerationException, CertificateServiceException, EntityNotFoundException,
            InvalidCAException, InvalidCertificateRequestException, InvalidEntityException {
        return null;
    }

    @Override
    public KeyStoreInfo reKeyCertificate(final String string, final char[] chars, final KeyStoreType kst)
            throws AlgorithmNotFoundException, CertificateGenerationException, CertificateServiceException, EntityNotFoundException,
            ExpiredCertificateException, InvalidCAException, InvalidEntityException, InvalidEntityAttributeException, RevokedCertificateException {
        return null;
    }

    @Override
    public CertificateChain getCertificateChain(final String string) throws CertificateServiceException, InvalidCAException,
            InvalidCertificateStatusException, InvalidEntityException, InvalidEntityAttributeException {
        return null;
    }

    @Override
    public List<CertificateChain> getCertificateChainList(final String entityName, final CertificateStatus... certificateStatus)
            throws CertificateServiceException, InvalidCAException, InvalidEntityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Certificate> getTrustCertificates(final String string) throws CertificateServiceException, EntityNotFoundException,
            ExternalCredentialMgmtServiceException, InvalidCAException, InvalidEntityAttributeException, ProfileNotFoundException {
        return mockConfigService.getTrustCertificates(string);
    }

    @Override
    public void publishCertificate(final String string) throws CertificateServiceException, EntityNotFoundException {
    }

    @Override
    public void unPublishCertificate(final String string) throws CertificateServiceException, EntityNotFoundException {
    }

    @Override
    public List<Certificate> listCertificates(final String string, final CertificateStatus... css)
            throws CertificateNotFoundException, CertificateServiceException, EntityNotFoundException {
        return null;
    }

    @Override
    public List<Certificate> listCertificates_v1(final String string, final CertificateStatus... css)
            throws CertificateServiceException, EntityNotFoundException {
        return null;
    }
    
    @Override
    public Certificate generateCertificate(final String string, final CertificateRequest cr, final String string1)
            throws AlgorithmNotFoundException, CertificateGenerationException, CertificateServiceException, EntityNotFoundException,
            ExpiredCertificateException, InvalidCAException, InvalidCertificateRequestException, InvalidEntityException,
            InvalidEntityAttributeException, OTPExpiredException, InvalidOTPException, RevokedCertificateException {
        return null;
    }

    @Override
    public boolean isValidCertificate(String arg0, String arg1, String arg2) throws CertificateServiceException, EntityNotFoundException,
            InvalidEntityAttributeException {
        return false;
    }

    @Override
    public boolean isCertificateExist(final String arg0, final String arg1, final String arg2) {
        return false;
    }

    @Override
    public void deleteCertificate(String s, String s1) throws CertificateServiceException, EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteCertificates(String s, CertificateStatus certificateStatus) throws CertificateServiceException, EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
