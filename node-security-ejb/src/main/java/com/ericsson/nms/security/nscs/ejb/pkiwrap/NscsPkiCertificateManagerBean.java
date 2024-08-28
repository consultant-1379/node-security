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
package com.ericsson.nms.security.nscs.ejb.pkiwrap;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.pki.*;
import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.crl.revocation.RevocationReason;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;

/**
 * Implementation of the NscsPkiCertificateManager interface.
 * 
 * @author zlaxsri
 */
@Stateless
public class NscsPkiCertificateManagerBean implements NscsPkiCertificateManager {

    @Inject
    Logger logger;

    @Inject
    NscsPkiCertificateManagerRisc nscsPkiCertificateManagerRisc;
    
    @Inject
    NscsPkiEntitiesManagerRisc nscsPkiEntitiesManagerRisc;

    @Override
    public void revokeEntityCertificates(final String entityName) throws NscsPkiCertificateManagerException {
        List<Certificate> certificateList = null;
        try {
            if (!nscsPkiEntitiesManagerRisc.isEntityNameAvailable(entityName, EntityType.ENTITY)) {
                certificateList = nscsPkiCertificateManagerRisc.listCertificates(entityName, CertificateStatus.ACTIVE, CertificateStatus.INACTIVE);
            }
        } catch (NscsPkiCertificateManagerException | NscsPkiEntitiesManagerException e) {
            logger.error("Error in retrieving certificates : " + e.getMessage());
        }
        if (certificateList != null && !certificateList.isEmpty()) {
            nscsPkiCertificateManagerRisc.revokeEntityCertificates(entityName, RevocationReason.UNSPECIFIED, null);
        } else {
            logger.info("Entity Name : {}, No active certificates available for revocation", entityName);
        }

    }
    
    @Override
    public boolean isNodeHasValidCertificate(final String entityName, final String serialNumber, final String issuerDN) {
        boolean isValidCertificateOnPKI = false;
        try {
            if (!nscsPkiEntitiesManagerRisc.isEntityNameAvailable(entityName, EntityType.ENTITY)) {
                isValidCertificateOnPKI = nscsPkiCertificateManagerRisc.isNodeHasValidCertificate(entityName, serialNumber, issuerDN);
            }
        } catch (NscsPkiEntitiesManagerException e) {
            logger.error("Error in retrieving certificates : " + e.getMessage());
        }
        return isValidCertificateOnPKI;
    }
}