/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.pki;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.EntityCertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.ExtCACertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.configurationmanagement.api.PKIConfigurationManagementService;
import com.ericsson.oss.itpf.security.pki.manager.crlmanagement.api.RevocationService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ExtCAManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ProfileManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.custom.EntityManagementCustomService;

@ApplicationScoped
public class PkiApiManagers {

    @Inject
    Logger logger;

    @EServiceRef
    private EntityManagementService pkiEntityManager;

    @EServiceRef
    private EntityManagementCustomService pkiEntityCustomManager;

    @EServiceRef
    private ProfileManagementService pkiProfileManagementService;

    @EServiceRef
    private CACertificateManagementService pkiCACertificateManager;

    @EServiceRef
    private EntityCertificateManagementService pkiEntityCertificateManagementService;

    @EServiceRef
    private PKIConfigurationManagementService pkiConfigurationManagementService;

    @EServiceRef
    private ExtCAManagementService pkiExtCAManagementService;

    @EServiceRef
    private ExtCACertificateManagementService pkiExtCACertificateManagementService;

    @EServiceRef
    private RevocationService pkiRevocationService;

    RevocationService getPkiRevocationService() {
        return pkiRevocationService;
    }

    ExtCAManagementService getExtCAManagementService() {
        return pkiExtCAManagementService;
    }

    EntityCertificateManagementService getEntityCertificateManagementService() {
        return pkiEntityCertificateManagementService;
    }

    ExtCACertificateManagementService getExtCACertificateManagementService() {
        return pkiExtCACertificateManagementService;
    }

    EntityManagementService getEntityManagementService() {
        if (NscsPkiMockManagement.useMockEntityManager()) {
            logger.info("Using Mock PKI Entity Manager");
            throw new UnsupportedOperationException("Not supported anymore");
        } else {
            return pkiEntityManager;
        }
    }

    EntityManagementCustomService getEntityManagementCustomService() {
        if (NscsPkiMockManagement.useMockEntityManager()) {
            logger.info("Using Mock PKI Entity Manager");
            throw new UnsupportedOperationException("Not supported anymore");
        } else {
            return pkiEntityCustomManager;
        }
    }

    CACertificateManagementService getCACertificateManagementService() {
        if (NscsPkiMockManagement.useMockCertificateManager()) {
            logger.info("Using Mock PKI Certificate Manager");
            throw new UnsupportedOperationException("Not supported anymore");
        } else {
            return pkiCACertificateManager;
        }
    }

    ProfileManagementService getProfileManagementService() {
        if (NscsPkiMockManagement.useMockProfileManager()) {
            logger.info("Using Mock PKI Profile Manager");
            throw new UnsupportedOperationException("Not supported anymore");
        } else {
            return pkiProfileManagementService;
        }
    }

    PKIConfigurationManagementService getConfigurationManagementService() {
        if (NscsPkiMockManagement.useMockPkiConfigurationManager()) {
            logger.info("Using Mock PKI Configuration Manager");
            throw new UnsupportedOperationException("Not supported anymore");
        } else {
            return pkiConfigurationManagementService;
        }
    }
}
