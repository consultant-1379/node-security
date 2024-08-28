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

import spock.lang.Shared
import spock.lang.Unroll

class NscsProxyAccountsDataTest extends CdiSpecification {

    @Shared
    private NscsProxyAccountsCounters nscsProxyAccountsCounters = new NscsProxyAccountsCounters()

    @Shared
    private NscsProxyAccounts nscsProxyAccounts = new NscsProxyAccounts()

    def 'empty constructor'() {
        given:
        def NscsProxyAccountsData nscsProxyAccountsData = new NscsProxyAccountsData()
        expect:
        nscsProxyAccountsData != null
        nscsProxyAccountsData.getProxyAccountsCounters() == null
        nscsProxyAccountsData.getProxyAccounts() == null
    }

    @Unroll
    def 'set proxy accounts counters to #counters'() {
        given:
        def NscsProxyAccountsData nscsProxyAccountsData = new NscsProxyAccountsData()
        when:
        nscsProxyAccountsData.setProxyAccountsCounters(counters)
        then:
        nscsProxyAccountsData != null
        nscsProxyAccountsData.getProxyAccountsCounters() == counters
        nscsProxyAccountsData.getProxyAccounts() == null
        where:
        counters << [
            nscsProxyAccountsCounters,
            null
        ]
    }

    @Unroll
    def 'set proxy accounts to #proxyaccounts'() {
        given:
        def NscsProxyAccountsData nscsProxyAccountsData = new NscsProxyAccountsData()
        when:
        nscsProxyAccountsData.setProxyAccounts(proxyaccounts)
        then:
        nscsProxyAccountsData != null
        nscsProxyAccountsData.getProxyAccountsCounters() == null
        nscsProxyAccountsData.getProxyAccounts() == proxyaccounts
        where:
        proxyaccounts << [
            nscsProxyAccounts,
            null
        ]
    }
}
