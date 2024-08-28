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

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.oss.itpf.security.pki.common.model.crl.revocation.RevocationReason;

/**
 * Interface to define methods related to certificate handling in PKI manager.
 * 
 * @author zlaxsri
 */
@Local
public interface NscsPkiCertificateManager {

    /**
     * Revokes the active and inactive certificates of the given entity. While revoking certificates, RevocationReason is given as link RevocationReason.UNSPECIFIED and invalidityDate is given as
     * null value.
     * 
     * It will list the certificates first and will revoke them if available.
     * 
     * @param entityName
     *            is the name of the Entity.
     * @throws NscsPkiCertificateManagerException
     *             thrown if the revocation of certificate fails
     */
    void revokeEntityCertificates(String entityName) throws NscsPkiCertificateManagerException;

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
