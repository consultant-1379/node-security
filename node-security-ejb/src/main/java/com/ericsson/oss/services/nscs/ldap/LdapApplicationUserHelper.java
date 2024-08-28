/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.ldap;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.DpsNodeLoader;

@Stateless
public class LdapApplicationUserHelper {

    @Inject
    DpsNodeLoader dpsNodeLoader;

    /**
     * Queries the DPS to retrieve the entire list of NetworkElementSecurity object poid that support ldapApplicationUser
     *
     * @return a map of pairs <nes poid, fdn poid>
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<Long, String> getNESWithLdapUser() {
        return dpsNodeLoader.getNESWithLdapUser();
    }

    /**
     * Set the ldapApplicationPassword for the specified NetworkElementSecurity object.
     *
     * @param poId
     *            the id of the NetworkSecurityObject to update
     * @param password
     *            the ldapUserPassword value to set.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateNESWithLdapUser(final Long poId, final String password) {
        dpsNodeLoader.updateNESWithLdapUser(poId, password);
    }
}
