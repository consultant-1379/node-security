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
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing the request to perform the LDAP configuration for given node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.LDAP_CONFIGURATION
 * </p>
 */
public class CommonLdapConfigurationTask extends CommonLdapTask {

    private static final long serialVersionUID = -1996924573860298617L;

    public static final String TLS_MODE_KEY = WorkflowParameterKeys.TLS_MODE.toString();
    public static final String IS_TLS_KEY = WorkflowParameterKeys.USE_TLS.toString();
    public static final String USER_LABEL_KEY = WorkflowParameterKeys.USER_LABEL.toString();

    private static final String SHORT_DESCRIPTION = "LDAP Configuration";

    public CommonLdapConfigurationTask() {
        super(WorkflowTaskType.LDAP_CONFIGURATION, SHORT_DESCRIPTION);
    }

    public CommonLdapConfigurationTask(final String fdn) {
        super(WorkflowTaskType.LDAP_CONFIGURATION, SHORT_DESCRIPTION, fdn);
    }

    /**
     * @return the tlsMode
     */
    public String getTlsMode() {
        return (String) getValue(TLS_MODE_KEY);
    }

    /**
     * @param tlsMode
     *            the tlsMode to set
     */
    public void setTlsMode(final String tlsMode) {
        setValue(TLS_MODE_KEY, tlsMode);
    }

    /**
     * @return the isTls
     */
    public Boolean getIsTls() {
        return (Boolean) getValue(IS_TLS_KEY);
    }

    /**
     * @param isTls
     *            the isTls to set
     */
    public void setIsTls(final Boolean isTls) {
        setValue(IS_TLS_KEY, isTls);
    }

    /**
     * @return the userLabel
     */
    public String getUserLabel() {
        return (String) getValue(USER_LABEL_KEY);
    }

    /**
     * @param userLabel
     *            the userLabel to set
     */
    public void setUserLabel(final String userLabel) {
        setValue(USER_LABEL_KEY, userLabel);
    }
}
