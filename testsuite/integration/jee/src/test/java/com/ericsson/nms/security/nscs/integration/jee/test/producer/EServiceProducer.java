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
package com.ericsson.nms.security.nscs.integration.jee.test.producer;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;

import com.ericsson.nms.security.nscs.api.IscfService;
import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelService;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.configurationmanagement.api.PKIConfigurationManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ProfileManagementService;
import com.ericsson.oss.itpf.smrs.SmrsService;
import com.ericsson.oss.services.cm.cmreader.api.CmReaderService;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.WorkflowTaskService;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.security.pkimock.api.MockCACertificateManagementService;
import com.ericsson.oss.services.security.pkimock.api.MockConfigurationManagementService;
import com.ericsson.oss.services.security.pkimock.api.MockEntityManagementService;
import com.ericsson.oss.services.security.pkimock.api.MockProfileManagementService;
import com.ericsson.oss.services.wfs.jee.api.WorkflowQueryServiceRemote;

@Stateless
public class EServiceProducer {

    @EServiceRef
    private SmrsService smrsService;

    @EServiceRef
    private WorkflowHandler wfh;

    @EServiceRef
    private WorkflowTaskService workflowTaskService;

    @EServiceRef
    private WorkflowQueryServiceRemote workflowQueryServiceRemote;

    @EServiceRef
    private WfQueryService wfQueryService;

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    @EServiceRef(qualifier = "secadm")
    private CommandHandler seCommandHandler;

    @EServiceRef
    private CmReaderService cmReader;

    @EServiceRef
    private IscfService iscfService;

    @EServiceRef
    MockProfileManagementService pkiProfManager;

    @EServiceRef
    MockEntityManagementService pkiEntityManager;

    @EServiceRef
    MockCACertificateManagementService pkiCertManager;

    @EServiceRef
    MockConfigurationManagementService pkiConfigManager;

    @EServiceRef
    private NscsModelService nscsModelService;

    @Produces
    public SmrsService getSmrsService() {
        return smrsService;
    }

    @Produces
    public WorkflowHandler getWorkflowHandler() {
        return wfh;
    }

    @Produces
    public DataPersistenceService getDataPersistenceService() {
        return dataPersistenceService;
    }

    @Produces
    public CommandHandler getScriptEngineCommandHandler() {
        return seCommandHandler;
    }

    @Produces
    public CmReaderService getCmReaderService() {
        return cmReader;
    }

    @Produces
    public WorkflowTaskService getWorkflowTaskService() {
        return workflowTaskService;
    }

    @Produces
    public WorkflowQueryServiceRemote getWorkflowQueryServiceRemote() {
        return workflowQueryServiceRemote;
    }

    @Produces
    public WfQueryService getWfQueryService() {
        return wfQueryService;
    }

    @Produces
    public IscfService getIscfService() {
        return iscfService;
    }

    @Produces
    public ProfileManagementService getProfileManagementService() {
        return pkiProfManager;
    }

    @Produces
    public EntityManagementService getEntityManagementService() {
        return pkiEntityManager;
    }

    @Produces
    public CACertificateManagementService getCertificateManagementService() {
        return pkiCertManager;
    }

    @Produces
    public PKIConfigurationManagementService getPkiConfigurationManagementService() {
        return pkiConfigManager;
    }

    @Produces
    public NscsModelService getNscsModelService() {
        return nscsModelService;
    }

}
