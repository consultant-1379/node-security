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

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.ldap.LdapApplicationUserManager;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

@Stateless
public class LdapApplicationUserManagerBean implements LdapApplicationUserManager {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    LdapApplicationUserHelper ldapApplicationUserHelper;

    @Inject
    private PasswordHelper passwordHelper;

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void propagateLdapApplicationUserPassword(final String password) {
        final Map<Long, String> nesList = ldapApplicationUserHelper.getNESWithLdapUser();

        for (final Map.Entry<Long, String> nes : nesList.entrySet()) {
            try {
                ldapApplicationUserHelper.updateNESWithLdapUser(nes.getKey(), passwordHelper.encryptEncode(password));
            } catch (final Exception ex) {
                nscsLogger.error("propagateLdapApplicationUserPassword error for fdn " + nes.getValue());
            }
        }
    }
}
