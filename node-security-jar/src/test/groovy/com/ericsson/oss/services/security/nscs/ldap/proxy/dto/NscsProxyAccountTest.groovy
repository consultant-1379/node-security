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
package com.ericsson.oss.services.security.nscs.ldap.proxy.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class NscsProxyAccountTest extends CdiSpecification {

    def 'empty constructor'() {
        given:
        def NscsProxyAccount nscsProxyAccount = new NscsProxyAccount()
        expect:
        nscsProxyAccount != null
        nscsProxyAccount.getDn() == null
        nscsProxyAccount.getAdminStatus() == null
        nscsProxyAccount.getCreateDate() == null
        nscsProxyAccount.getLastLoginDate() == null
    }

    def 'set proxy account parameters'() {
        given:
        def NscsProxyAccount nscsProxyAccount = new NscsProxyAccount()
        when:
        nscsProxyAccount.setDn("this is the DN")
        nscsProxyAccount.setAdminStatus("this is the admin status")
        nscsProxyAccount.setCreateDate("this is the create date")
        nscsProxyAccount.setLastLoginDate("this is the last login date")
        then:
        nscsProxyAccount != null
        nscsProxyAccount.getDn() == "this is the DN"
        nscsProxyAccount.getAdminStatus() == "this is the admin status"
        nscsProxyAccount.getCreateDate() == "this is the create date"
        nscsProxyAccount.getLastLoginDate() == "this is the last login date"
    }
}
