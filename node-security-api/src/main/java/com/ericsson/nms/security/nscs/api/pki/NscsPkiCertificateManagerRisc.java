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
package com.ericsson.nms.security.nscs.api.pki;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.crl.revocation.RevocationReason;

/**
 * Interface to define methods related to certificate handling in PKI manager.
 * 
 * @author zlaxsri
 */
@Local
public interface NscsPkiCertificateManagerRisc {

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
    void revokeEntityCertificates(String entityName, RevocationReason reason, Date invalidityDate) throws NscsPkiCertificateManagerException;

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
    List<Certificate> listCertificates(String entityName, CertificateStatus... status) throws NscsPkiCertificateManagerException;
    
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
    boolean isNodeHasValidCertificate(final String entityName, final String serialNumber, final String issuerDN);
}