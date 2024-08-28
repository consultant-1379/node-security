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

import spock.lang.Unroll

class NscsProxyAccountsCountersTest extends CdiSpecification {

    def 'empty constructor'() {
        given:
        def NscsProxyAccountsCounters nscsProxyAccountsCounters = new NscsProxyAccountsCounters()
        expect:
        nscsProxyAccountsCounters != null
        nscsProxyAccountsCounters.getNumOfProxyAccounts() == null
        nscsProxyAccountsCounters.getNumOfRequestedProxyAccounts() == null
        nscsProxyAccountsCounters.getNumOfLegacyProxyAccounts() == null
        nscsProxyAccountsCounters.getNumOfRequestedLegacyProxyAccounts() == null
    }

    @Unroll
    def 'set proxy accounts counters with #counter'() {
        given:
        def NscsProxyAccountsCounters nscsProxyAccountsCounters = new NscsProxyAccountsCounters()
        when:
        nscsProxyAccountsCounters.setNumOfProxyAccounts(counter)
        nscsProxyAccountsCounters.setNumOfRequestedProxyAccounts(counter)
        nscsProxyAccountsCounters.setNumOfLegacyProxyAccounts(counter)
        nscsProxyAccountsCounters.setNumOfRequestedLegacyProxyAccounts(counter)
        then:
        nscsProxyAccountsCounters != null
        nscsProxyAccountsCounters.getNumOfProxyAccounts() == counter
        nscsProxyAccountsCounters.getNumOfRequestedProxyAccounts() == counter
        nscsProxyAccountsCounters.getNumOfLegacyProxyAccounts() == counter
        nscsProxyAccountsCounters.getNumOfRequestedLegacyProxyAccounts() == counter
        where:
        counter << [
            0,
            1,
            Integer.MAX_VALUE,
            null
        ]
    }
}
