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
package com.ericsson.oss.services.security.nscs.util;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.CredentialService;
import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelService;
import com.ericsson.nms.security.nscs.data.DpsNodeLoader;
import com.ericsson.nms.security.nscs.ejb.iscf.IscfServiceDelegate;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService;
import com.ericsson.oss.itpf.smrs.SmrsService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.WorkflowTaskService;

@Stateless
public class EServiceHolder {

    @EServiceRef
    private SmrsService smrsService;

    @EServiceRef
    private WorkflowHandler wfh;

    @EServiceRef
    private IscfServiceDelegate iscfService;

    @EServiceRef
    private CredentialService credentialService;

    @EServiceRef
    private WorkflowTaskService workflowTaskService;

    @EServiceRef
    private EntityManagementService pkiEntManager;

    @Inject
    private DpsNodeLoader dpsNodeLoader;

    @EServiceRef
    private NscsModelService nscsModelService;

    public SmrsService getSmrsService() {
        return smrsService;
    }

    public WorkflowHandler getWorkflowHandler() {
        return wfh;
    }

    public IscfServiceDelegate getIscfService() {
        return iscfService;
    }

    public CredentialService getCredentialService() {
        return credentialService;
    }

    @Produces
    public WorkflowTaskService getWorkflowTaskService() {
        return workflowTaskService;
    }

    public EntityManagementService getEntityManagementService() {
        return pkiEntManager;
    }

    public NscsModelService getNscsModelService() {
        return nscsModelService;
    }

    public void getLdapUser() {
        this.dpsNodeLoader.getNESWithLdapUser();
    }

}
