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
package com.ericsson.nms.security.nscs.ldap.control;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.PlatformSpecificConfigurationProviderException;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader;

@ApplicationScoped
public class PlatformSpecificConfigurationProvider {

    @Inject
    private PlatformConfigurationReader platformConfigurationReader;

    public String getBaseDN() {
        final String baseDN = platformConfigurationReader.getProperty(LdapConstants.COM_INF_LDAP_ROOT_SUFFIX);
        if( baseDN == null )
        {
            throw new PlatformSpecificConfigurationProviderException(PlatformSpecificConfigurationProviderException.LDAP_BASE_DN_INVALID);
        }
        return baseDN;
    }
}
