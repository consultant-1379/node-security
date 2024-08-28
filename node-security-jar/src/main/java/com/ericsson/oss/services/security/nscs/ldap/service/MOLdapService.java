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
package com.ericsson.oss.services.security.nscs.ldap.service;

import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask;

/**
 * Interface to provide MO LDAP operations on nodes.
 */
public interface MOLdapService {

    /**
     * Validate the LDAP configuration contained in the given task for the given node.
     * 
     * @param task
     *            the task.
     * @param nodeReference
     *            the normalizable node reference.
     * @throws {@link
     *             IllegalArgumentException} if LDAP configuration is invalid.
     */
    public void validateLdapConfiguration(CommonLdapConfigurationTask task, NormalizableNodeReference normalizable);

    /**
     * Configure the LDAP client on the given node according to the ldap workflow context of the given task.
     * 
     * The ldap workflow context of the given task is modified adding the bind DN configured on the node before updating it.
     * 
     * @param task
     *            the task.
     * @param nodeReference
     *            the normalizable node reference.
     * @throws {@link
     *             MissingMoException} if LDAP MO is not present in the node.
     */
    public void ldapConfigure(CommonLdapConfigurationTask task, NormalizableNodeReference normalizable);

}
