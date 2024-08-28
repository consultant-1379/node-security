/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.cucumber.helper;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;

import com.ericsson.nms.security.nscs.api.IscfService;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelService;
import com.ericsson.oss.gdpr.anonymize.api.GdprAnonymizer;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ProfileManagementService;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.security.pkimock.api.MockEntityManagementService;
import com.ericsson.oss.services.security.pkimock.api.MockProfileManagementService;

@Stateless
public class EServiceProducer {

    @EServiceRef
    private GdprAnonymizer gdprAnonymizer;

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    @EServiceRef
    private IscfService iscfService;

    @EServiceRef
    private MockEntityManagementService pkiEntityManager;

    @EServiceRef
    private MockProfileManagementService pkiProfileManager;

    @EServiceRef(qualifier = "secadm")
    private CommandHandler commandHandler;

    @EServiceRef
    private NscsModelService nscsModelService;

    @Produces
    public GdprAnonymizer getGdprService() {
        return gdprAnonymizer;
    }

    @Produces
    public DataPersistenceService getDataPersistenceService() {
        return dataPersistenceService;
    }

    @Produces
    public IscfService getIscfService() {
        return iscfService;
    }

    @Produces
    public EntityManagementService getEntityManagementService() {
        return pkiEntityManager;
    }

    @Produces
    public ProfileManagementService getProfileManagementService() {
        return pkiProfileManager;
    }

    @Produces
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Produces
    public NscsModelService getNscsModelService() {
        return nscsModelService;
    }

}
