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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.crl.revocation.RevocationReason;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityAlreadyExistsException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.InvalidInvalidityDateException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.IssuerCertificateRevokedException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.RevocationServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.RootCertificateRevocationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.ExpiredCertificateException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.RevokedCertificateException;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * This class is used to interface with PKI Manager for the methods related to certificate handling.
 * 
 * @author zlaxsri
 */
public class NscsPkiCertificateManagerJar {

    @Inject
    Logger logger;

    @Inject
    private NscsContextService nscsContextService;

    @Inject
    PkiApiManagers pkiApiManagers;

    private static final String NSCS_CONTEXT_USER_NAME = "NSCS";

    private void setContextData() {
        nscsContextService.setUserNameContextValue(NSCS_CONTEXT_USER_NAME);
    }

    /**
     * Revokes the certificates of the given entity.
     * 
     * @param entityName
     *            is the name of the Entity.
     * @param reason
     *            is the RevocationReason enum which has the reason values defined by RFC5280.
     * @param invalidityDate
     *            is the date on which it is known or suspected that the private key was compromised or that the Certificate otherwise became invalid.
     * @throws NscsPkiCertificateManagerException
     *             thrown if the revocation of certificate fails
     */
    public void revokeEntityCertificates(final String entityName, final RevocationReason reason, final Date invalidityDate) throws NscsPkiCertificateManagerException {
        logger.info("revokeEntityCertificates() : entity name[{}]", entityName);
        setContextData();
        try {
            pkiApiManagers.getPkiRevocationService().revokeEntityCertificates(entityName, reason, invalidityDate);
        } catch (CertificateNotFoundException | EntityAlreadyExistsException | EntityNotFoundException | ExpiredCertificateException | InvalidEntityAttributeException | InvalidInvalidityDateException
                | IssuerCertificateRevokedException | RevocationServiceException | RevokedCertificateException | RootCertificateRevocationException e) {
            final String err = "revokeEntityCertificates() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiCertificateManagerException(err, e);
        }
    }

    /**
     * Returns a list of certificates issued for the CAEntity or Entity based on CertificateStatus
     * 
     * @param entityName
     *            name of the CAEntity or Entity
     * @param status
     *            The list of {@link CertificateStatus} values for which Certificates have to be listed
     * @return list of certificates of the given CAEntity or entity based on status.
     * @throws NscsPkiCertificateManagerException
     *             Thrown to indicate any internal database errors or any unconditional exceptions.
     */
    public List<Certificate> listCertificates(final String entityName, final CertificateStatus... status) throws NscsPkiCertificateManagerException {
        logger.info("listCertificates() : entity name[{}]", entityName);
        List<Certificate> certificateList;
        setContextData();
        try {
            certificateList = pkiApiManagers.getEntityCertificateManagementService().listCertificates_v1(entityName, status);
        } catch (CertificateServiceException | EntityNotFoundException | InvalidEntityAttributeException e) {
            final String err = "listCertificates() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiCertificateManagerException(err, e);
        }
        return certificateList;
    }

    /**
     * Check whether the node has been installed with a valid certificate issued by ENM CA or not.
     * 
     * @param entityName
     *            is the name of the Entity.
     * @param serialNumber
     *            Serial number of the node certificate.
     * @param issuerDN
     *            Issuer DN for the node certificate.
     * @return true if node certificate is valid on PKI otherwise false.
     */
    public boolean isNodeHasValidCertificate(final String entityName, final String serialNumber, final String issuerDN) {
        return pkiApiManagers.getEntityCertificateManagementService().isValidCertificate(entityName, serialNumber, issuerDN);
    }
}