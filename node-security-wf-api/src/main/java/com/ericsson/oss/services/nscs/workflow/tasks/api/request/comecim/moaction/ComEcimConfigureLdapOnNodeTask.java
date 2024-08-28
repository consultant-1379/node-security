/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.moaction.CommonConfigureLdapOnNodeTask;


/**
 * <p>
 * Workflow task representing the request to perform the ldapConfigure action for COM ECIM node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CONFIGURE_LDAP_ACTION
 * </p>
 *
 */
public class ComEcimConfigureLdapOnNodeTask extends CommonConfigureLdapOnNodeTask {

    private static final long serialVersionUID = -8649267946370699738L;

    private static final String SHORT_DESCRIPTION = "Configure ComEcim LDAP";

    public ComEcimConfigureLdapOnNodeTask() {
        super(WorkflowTaskType.COM_ECIM_CONFIGURE_LDAP_ACTION);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public ComEcimConfigureLdapOnNodeTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_CONFIGURE_LDAP_ACTION, fdn);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    private void setShortDescriptionLocal( final String shortDescription) {
        setShortDescription(shortDescription);
    }
}
