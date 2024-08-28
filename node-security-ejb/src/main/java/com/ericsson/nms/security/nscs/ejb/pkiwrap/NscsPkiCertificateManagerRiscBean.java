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

import java.util.Date;
import java.util.List;

import javax.ejb.*;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.pki.NscsPkiCertificateManagerRisc;
import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.nms.security.nscs.pki.NscsPkiCertificateManagerJar;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.crl.revocation.RevocationReason;

/**
 * Implementation of the NscsPkiCertificateManagerRisc interface.
 * 
 * @author zlaxsri
 */
@Stateless
public class NscsPkiCertificateManagerRiscBean implements NscsPkiCertificateManagerRisc {

    @Inject
    NscsPkiCertificateManagerJar nscsPkiCertificateManagerJar;

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void revokeEntityCertificates(final String entityName, final RevocationReason reason, final Date invalidityDate) throws NscsPkiCertificateManagerException {
        nscsPkiCertificateManagerJar.revokeEntityCertificates(entityName, reason, invalidityDate);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<Certificate> listCertificates(final String entityName, final CertificateStatus... status) throws NscsPkiCertificateManagerException {
        return nscsPkiCertificateManagerJar.listCertificates(entityName, status);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public boolean isNodeHasValidCertificate(final String entityName, final String serialNumber, final String issuerDN) {
        return nscsPkiCertificateManagerJar.isNodeHasValidCertificate(entityName, serialNumber, issuerDN);
    }
}