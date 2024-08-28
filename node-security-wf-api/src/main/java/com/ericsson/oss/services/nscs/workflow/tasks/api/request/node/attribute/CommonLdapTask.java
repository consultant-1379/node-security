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

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class CommonLdapTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 769978935767635837L;

    public static final String IS_RENEW_KEY = WorkflowParameterKeys.IS_RENEW.toString();
    public static final String LDAP_WORKFLOW_CONTEXT_KEY = WorkflowParameterKeys.LDAP_WORKFLOW_CONTEXT.toString();

    public CommonLdapTask(final WorkflowTaskType taskType, final String shortDescription) {
        super(taskType);
        setShortDescriptionLocal(shortDescription);
    }

    public CommonLdapTask(final WorkflowTaskType taskType, final String shortDescription, final String fdn) {
        super(taskType, fdn);
        setShortDescriptionLocal(shortDescription);
    }

    /**
     * @return the isRenew
     */
    public Boolean getIsRenew() {
        return (Boolean) getValue(IS_RENEW_KEY);
    }

    /**
     * @param isRenew
     *            the isRenew to set
     */
    public void setIsRenew(final Boolean isRenew) {
        setValue(IS_RENEW_KEY, isRenew);
    }

    /**
     * @return the ldapWorkflowContext
     */
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> getLdapWorkflowContext() {
        return (Map<String, Serializable>) getValue(LDAP_WORKFLOW_CONTEXT_KEY);
    }

    /**
     * @param ldapWorkflowContext
     *            the ldapWorkflowContext to set
     */
    public void setLdapWorkflowContext(final Map<String, Serializable> ldapWorkflowContext) {
        setValue(LDAP_WORKFLOW_CONTEXT_KEY, ldapWorkflowContext);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        setShortDescription(shortDescription);
    }

}
