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

class NscsProxyAccountsTest extends CdiSpecification {

    def 'empty constructor'() {
        given:
        def NscsProxyAccounts nscsProxyAccounts = new NscsProxyAccounts()
        expect:
        nscsProxyAccounts != null
        nscsProxyAccounts.getProxyAccounts() != null
        nscsProxyAccounts.getProxyAccounts().isEmpty() == true
    }

    def 'set proxy accounts parameters'() {
        given:
        def NscsProxyAccounts nscsProxyAccounts = new NscsProxyAccounts()
        and:
        def NscsProxyAccount nscsProxyAccount = new NscsProxyAccount()
        nscsProxyAccount.setDn("this is the DN")
        nscsProxyAccount.setAdminStatus("this is the admin status")
        nscsProxyAccount.setCreateDate("this is the create date")
        nscsProxyAccount.setLastLoginDate("this is the last login date")
        def List<NscsProxyAccount> proxyAccountsList = [nscsProxyAccount]
        when:
        nscsProxyAccounts.setProxyAccounts(proxyAccountsList)
        then:
        nscsProxyAccounts != null
        nscsProxyAccounts.getProxyAccounts() != null
        nscsProxyAccounts.getProxyAccounts().isEmpty() == false
        nscsProxyAccounts.getProxyAccounts().size() == 1
        nscsProxyAccounts.getProxyAccounts().get(0) != null
        nscsProxyAccounts.getProxyAccounts().get(0).getDn() == "this is the DN"
        nscsProxyAccounts.getProxyAccounts().get(0).getAdminStatus() == "this is the admin status"
        nscsProxyAccounts.getProxyAccounts().get(0).getCreateDate() == "this is the create date"
        nscsProxyAccounts.getProxyAccounts().get(0).getLastLoginDate() == "this is the last login date"
    }

    def 'set proxy accounts parameters with empty list'() {
        given:
        def NscsProxyAccounts nscsProxyAccounts = new NscsProxyAccounts()
        and:
        def List<NscsProxyAccount> proxyAccountsList = []
        when:
        nscsProxyAccounts.setProxyAccounts(proxyAccountsList)
        then:
        nscsProxyAccounts != null
        nscsProxyAccounts.getProxyAccounts() != null
        nscsProxyAccounts.getProxyAccounts().isEmpty() == true
        nscsProxyAccounts.getProxyAccounts().size() == 0
    }

    def 'set proxy accounts parameters with null list'() {
        given:
        def NscsProxyAccounts nscsProxyAccounts = new NscsProxyAccounts()
        when:
        nscsProxyAccounts.setProxyAccounts(null)
        then:
        nscsProxyAccounts != null
        nscsProxyAccounts.getProxyAccounts() != null
        nscsProxyAccounts.getProxyAccounts().isEmpty() == true
        nscsProxyAccounts.getProxyAccounts().size() == 0
    }
}
