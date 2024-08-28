/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.moaction.CommonConfigureLdapOnNodeTask;

public class CbpOiConfigureLdapOnNodeTask extends CommonConfigureLdapOnNodeTask {

    private static final long serialVersionUID = -8649267946370699738L;
    public static final String SHORT_DESCRIPTION = "Configure LDAP on CBP-OI node";

    public CbpOiConfigureLdapOnNodeTask() {
        super(WorkflowTaskType.CBPOI_CONFIGURE_LDAP_ACTION);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public CbpOiConfigureLdapOnNodeTask(final String fdn) {
        super(WorkflowTaskType.CBPOI_CONFIGURE_LDAP_ACTION, fdn);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    private void setShortDescriptionLocal( final String shortDescription) {
        setShortDescription(shortDescription);
    }
}