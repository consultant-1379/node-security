/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.control

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.LdapConfigurationException
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus

import spock.lang.Unroll

class IdentityManagementProxyTest extends CdiSpecification {

    @ObjectUnderTest
    private IdentityManagementProxy identityManagementProxy

    @MockedImplementation
    private IdentityManagementService identityManagementService

    def 'object under test'() {
        expect:
        identityManagementProxy != null
    }

    def 'successful create proxy account'() {
        given:
        identityManagementService.createProxyAgentAccount() >> new ProxyAgentAccountData ("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com", "osz45rph")
        when:
        def proxyAgentAccount = identityManagementProxy.createProxyAgentAccount()
        then:
        notThrown(Exception.class)
        and:
        proxyAgentAccount != null
        and:
        proxyAgentAccount.getUserDN() == "cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com"
        and:
        proxyAgentAccount.getUserPassword() == "osz45rph"
    }

    def 'failed create proxy account'() {
        given:
        identityManagementService.createProxyAgentAccount() >> { throw new IdentityManagementServiceException("IDMS error") }
        when:
        def proxyAgentAccount = identityManagementProxy.createProxyAgentAccount()
        then:
        thrown(LdapConfigurationException.class)
    }

    @Unroll
    def 'successful delete existent #isexistent proxy account'() {
        given:
        identityManagementService.deleteProxyAgentAccount(_) >> isexistent
        when:
        def deleted = identityManagementProxy.deleteProxyAgentAccount("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com")
        then:
        notThrown(Exception.class)
        and:
        deleted == isexistent
        where:
        isexistent << [true, false]
    }

    def 'failed delete proxy account'() {
        given:
        identityManagementService.deleteProxyAgentAccount(_) >> { throw new IdentityManagementServiceException("IDMS error") }
        when:
        def deleted = identityManagementProxy.deleteProxyAgentAccount("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com")
        then:
        thrown(LdapConfigurationException.class)
    }

    @Unroll
    def 'successful get all proxy accounts isLegacy #islegacy isSummary #issummary'() {
        given:
        identityManagementService.getProxyAgentAccount(islegacy, issummary) >> new ProxyAgentAccountGetData()
        when:
        def proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccounts(islegacy, issummary, null)
        then:
        notThrown(Exception.class)
        and:
        proxyAgentAccountGetData != null
        where:
        islegacy << [false, false, true, true]
        issummary << [false, true, false, true]
    }

    @Unroll
    def 'successful get all proxy accounts isLegacy #islegacy isSummary #issummary count #count'() {
        given:
        when:
        def proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccounts(islegacy, issummary, count)
        then:
        notThrown(Exception.class)
        and:
        proxyAgentAccountGetData != null
        where:
        islegacy << [
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            true
        ]
        issummary << [
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            true,
            false
        ]
        count << [
            0,
            0,
            0,
            1,
            1,
            1,
            2,
            2,
            2,
            3,
            3,
            3
        ]
    }

    @Unroll
    def 'failed get all proxy accounts isLegacy #islegacy isSummary #issummary'() {
        given:
        identityManagementService.getProxyAgentAccount(islegacy, issummary) >> { throw new IdentityManagementServiceException("IDMS error") }
        when:
        def proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccounts(islegacy, issummary, null)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        islegacy << [false, false, true, true]
        issummary << [false, true, false, true]
    }

    @Unroll
    def 'successful get proxy accounts by admin status #adminstatus isLegacy #islegacy isSummary #issummary'() {
        given:
        identityManagementService.getProxyAgentAccountByAdminStatus(adminstatus, islegacy, issummary) >> new ProxyAgentAccountGetData()
        when:
        def proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByAdminStatus(adminstatus, islegacy, issummary)
        then:
        notThrown(Exception.class)
        and:
        proxyAgentAccountGetData != null
        where:
        islegacy << [
            false,
            false,
            true,
            true,
            false,
            false,
            true,
            true
        ]
        issummary << [
            false,
            true,
            false,
            true,
            false,
            true,
            false,
            true
        ]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED,
            ProxyAgentAccountAdminStatus.ENABLED,
            ProxyAgentAccountAdminStatus.ENABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'failed get proxy accounts by admin status #adminstatus isLegacy #islegacy isSummary #issummary'() {
        given:
        identityManagementService.getProxyAgentAccountByAdminStatus(adminstatus, islegacy, issummary) >> { throw new IdentityManagementServiceException("IDMS error") }
        when:
        def proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByAdminStatus(adminstatus, islegacy, issummary)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        islegacy << [
            false,
            false,
            true,
            true,
            false,
            false,
            true,
            true
        ]
        issummary << [
            false,
            true,
            false,
            true,
            false,
            true,
            false,
            true
        ]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED,
            ProxyAgentAccountAdminStatus.ENABLED,
            ProxyAgentAccountAdminStatus.ENABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'successful get proxy accounts by inactivity period #inactivityperiod isLegacy #islegacy isSummary #issummary'() {
        given:
        identityManagementService.getProxyAgentAccountByInactivityPeriod(inactivityperiod, islegacy, issummary) >> new ProxyAgentAccountGetData()
        when:
        def proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByInactivityPeriod(inactivityperiod, islegacy, issummary)
        then:
        notThrown(Exception.class)
        and:
        proxyAgentAccountGetData != null
        where:
        islegacy << [
            false,
            false,
            true,
            true,
            false,
            false,
            true,
            true
        ]
        issummary << [
            false,
            true,
            false,
            true,
            false,
            true,
            false,
            true
        ]
        inactivityperiod << [
            0L,
            0L,
            0L,
            0L,
            1000L,
            1000L,
            1000L,
            1000L
        ]
    }

    @Unroll
    def 'failed get proxy accounts by inactivity period #inactivityperiod isLegacy #islegacy isSummary #issummary'() {
        given:
        identityManagementService.getProxyAgentAccountByInactivityPeriod(inactivityperiod, islegacy, issummary) >> { throw new IdentityManagementServiceException("IDMS error") }
        when:
        def proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByInactivityPeriod(inactivityperiod, islegacy, issummary)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        islegacy << [
            false,
            false,
            true,
            true,
            false,
            false,
            true,
            true
        ]
        issummary << [
            false,
            true,
            false,
            true,
            false,
            true,
            false,
            true
        ]
        inactivityperiod << [
            0L,
            0L,
            0L,
            0L,
            1000L,
            1000L,
            1000L,
            1000L
        ]
    }

    def 'successful get existent proxy account'() {
        given:
        identityManagementService.getProxyAgentAccountDetails(_) >> new ProxyAgentAccountDetails()
        when:
        def proxyAgentAccountDetails = identityManagementProxy.getProxyAccountDetails("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com")
        then:
        notThrown(Exception.class)
        and:
        proxyAgentAccountDetails != null
    }

    def 'successful get not existent proxy account'() {
        given:
        identityManagementService.getProxyAgentAccountDetails(_) >> null
        when:
        def proxyAgentAccountDetails = identityManagementProxy.getProxyAccountDetails("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com")
        then:
        notThrown(Exception.class)
        and:
        proxyAgentAccountDetails == null
    }

    def 'failed get proxy account'() {
        given:
        identityManagementService.getProxyAgentAccountDetails(_) >> { throw new IdentityManagementServiceException("IDMS error") }
        when:
        def proxyAgentAccountDetails = identityManagementProxy.getProxyAccountDetails("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com")
        then:
        thrown(NscsLdapProxyException.class)
    }

    @Unroll
    def 'successful update admin status to #adminstatus for existent #isexistent proxy account'() {
        given:
        identityManagementService.updateProxyAgentAccountAdminStatus(_, _) >> isexistent
        when:
        def updated = identityManagementProxy.updateProxyAccountAdminStatus("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com", adminstatus)
        then:
        notThrown(Exception.class)
        and:
        updated == isexistent
        where:
        isexistent << [true, false, true, false]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'failed update admin status to #adminstatus for proxy account'() {
        given:
        identityManagementService.updateProxyAgentAccountAdminStatus(_, _) >> { throw new IdentityManagementServiceException("IDMS error") }
        when:
        def updated = identityManagementProxy.updateProxyAccountAdminStatus("cn=ProxyAccount_4,ou=proxyaccount,dc=apache,dc=com", adminstatus)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    def 'get M2M password'() {
        given:
        identityManagementService.getM2MPassword("m2muser") >> "m2mpassword".getBytes()
        when:
        def bytearray = identityManagementProxy.getM2MPassword("m2muser")
        then:
        new String(bytearray) == "m2mpassword"
    }
}
