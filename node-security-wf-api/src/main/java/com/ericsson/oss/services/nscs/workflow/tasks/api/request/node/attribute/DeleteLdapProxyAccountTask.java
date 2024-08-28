/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class DeleteLdapProxyAccountTask extends CommonLdapTask {

    private static final long serialVersionUID = -63552954242567301L;

    private static final String SHORT_DESCRIPTION = "Delete LDAP Proxy Account";

    public DeleteLdapProxyAccountTask() {
        super(WorkflowTaskType.DELETE_LDAP_PROXY_ACCOUNT, SHORT_DESCRIPTION);
    }

    public DeleteLdapProxyAccountTask(final String fdn) {
        super(WorkflowTaskType.DELETE_LDAP_PROXY_ACCOUNT, SHORT_DESCRIPTION, fdn);
    }

}
