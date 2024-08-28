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
package com.ericsson.oss.services.security.nscs.ldap.proxy

import java.nio.charset.StandardCharsets

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException
import com.ericsson.nms.security.nscs.util.CliUtil
import com.ericsson.nms.security.nscs.util.FileUtil
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountCounters
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccountsData

import spock.lang.Unroll

class NscsLdapProxyHelperTest extends CdiSpecification {

    @ObjectUnderTest
    private NscsLdapProxyHelper nscsLdapProxyHelper

    @MockedImplementation
    private FileUtil fileUtil

    @MockedImplementation
    private CliUtil cliUtil

    def 'object under test'() {
        expect:
        nscsLdapProxyHelper != null
    }

    def 'convert proxy agent data from IDMS to NSCS format for null proxy accounts'() {
        given:
        def ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData();
        def ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters();
        proxyAgentAccountCounters.setNumOfProxyAccount(0);
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(0);
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(0);
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(0);
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters);
        when:
        def NscsProxyAccountsData nscsProxyAccountsData = nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData)
        then:
        nscsProxyAccountsData != null
        and:
        nscsProxyAccountsData.getProxyAccountsCounters() != null
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfProxyAccounts() == 0
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedProxyAccounts() == 0
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfLegacyProxyAccounts() == 0
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedLegacyProxyAccounts() == 0
        and:
        nscsProxyAccountsData.getProxyAccounts() != null
    }

    def 'convert null proxy agent data from IDMS to NSCS format'() {
        given:
        when:
        def NscsProxyAccountsData nscsProxyAccountsData = nscsLdapProxyHelper.fromIdmsDto((ProxyAgentAccountGetData)null)
        then:
        nscsProxyAccountsData != null
        and:
        nscsProxyAccountsData.getProxyAccountsCounters() == null
        and:
        nscsProxyAccountsData.getProxyAccounts() == null
    }

    def 'convert proxy agent data from IDMS to NSCS format for empty proxy accounts'() {
        given:
        def ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData();
        def ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters();
        proxyAgentAccountCounters.setNumOfProxyAccount(0);
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(0);
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(0);
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(0);
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters);
        def List<ProxyAgentAccountDetails> proxyAgentAccounts = new ArrayList<>();
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccounts);
        when:
        def NscsProxyAccountsData nscsProxyAccountsData = nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData)
        then:
        nscsProxyAccountsData != null
        and:
        nscsProxyAccountsData.getProxyAccountsCounters() != null
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfProxyAccounts() == 0
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedProxyAccounts() == 0
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfLegacyProxyAccounts() == 0
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedLegacyProxyAccounts() == 0
        and:
        nscsProxyAccountsData.getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().isEmpty() == true
    }

    def 'convert proxy agent data from IDMS to NSCS format for legacy proxy account'() {
        given:
        def ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
        def ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
        proxyAgentAccountCounters.setNumOfProxyAccount(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(1)
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(1)
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters)
        def List<ProxyAgentAccountDetails> proxyAgentAccounts = new ArrayList<>()
        def ProxyAgentAccountDetails proxyAgentAccount = new ProxyAgentAccountDetails()
        proxyAgentAccount.setUserDn("cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com")
        proxyAgentAccount.setCreateTimestamp(2 * 365 * 24 * 60 * 60 * 1000L + 1000L * 1)
        proxyAgentAccount.setLastLoginTime(2 * 365 * 24 * 60 * 60 * 1000L + 1000L * 2)
        proxyAgentAccounts.add(proxyAgentAccount)
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccounts)
        when:
        def NscsProxyAccountsData nscsProxyAccountsData = nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData)
        then:
        nscsProxyAccountsData != null
        and:
        nscsProxyAccountsData.getProxyAccountsCounters() != null
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfLegacyProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedLegacyProxyAccounts() == 1
        and:
        nscsProxyAccountsData.getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().isEmpty() == false
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().size() == 1
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0) != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getDn() == "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com"
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getAdminStatus() == "ENABLED"
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().startsWith("1972-01-01 ") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().endsWith(":00:01") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getLastLoginDate().startsWith("1972-01-01 ") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getLastLoginDate().endsWith(":00:02") == true
    }

    def 'convert proxy agent data from IDMS to NSCS format for legacy never logged proxy account'() {
        given:
        def ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
        def ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
        proxyAgentAccountCounters.setNumOfProxyAccount(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(1)
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(1)
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters)
        def List<ProxyAgentAccountDetails> proxyAgentAccounts = new ArrayList<>()
        def ProxyAgentAccountDetails proxyAgentAccount = new ProxyAgentAccountDetails()
        proxyAgentAccount.setUserDn("cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com")
        proxyAgentAccount.setCreateTimestamp(2 * 365 * 24 * 60 * 60 * 1000L + 1000L * 1)
        proxyAgentAccounts.add(proxyAgentAccount)
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccounts)
        when:
        def NscsProxyAccountsData nscsProxyAccountsData = nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData)
        then:
        nscsProxyAccountsData != null
        and:
        nscsProxyAccountsData.getProxyAccountsCounters() != null
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfLegacyProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedLegacyProxyAccounts() == 1
        and:
        nscsProxyAccountsData.getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().isEmpty() == false
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().size() == 1
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0) != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getDn() == "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com"
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getAdminStatus() == "ENABLED"
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().startsWith("1972-01-01 ") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().endsWith(":00:01") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getLastLoginDate() == "NEVER"
    }

    @Unroll
    def 'convert proxy agent data from IDMS to NSCS format for legacy proxy account with admin status #adminstatus'() {
        given:
        def ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
        def ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
        proxyAgentAccountCounters.setNumOfProxyAccount(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(1)
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(1)
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters)
        def List<ProxyAgentAccountDetails> proxyAgentAccounts = new ArrayList<>()
        def ProxyAgentAccountDetails proxyAgentAccount = new ProxyAgentAccountDetails()
        proxyAgentAccount.setUserDn("cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com")
        proxyAgentAccount.setAdminStatus(adminstatus);
        proxyAgentAccount.setCreateTimestamp(2 * 365 * 24 * 60 * 60 * 1000L + 1000L * 1)
        proxyAgentAccount.setLastLoginTime(2 * 365 * 24 * 60 * 60 * 1000L + 1000L * 2)
        proxyAgentAccounts.add(proxyAgentAccount)
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccounts)
        when:
        def NscsProxyAccountsData nscsProxyAccountsData = nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData)
        then:
        nscsProxyAccountsData != null
        and:
        nscsProxyAccountsData.getProxyAccountsCounters() != null
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfLegacyProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedLegacyProxyAccounts() == 1
        and:
        nscsProxyAccountsData.getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().isEmpty() == false
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().size() == 1
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0) != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getDn() == "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com"
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getAdminStatus() == expectedadminstatus
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().startsWith("1972-01-01 ") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().endsWith(":00:01") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getLastLoginDate().startsWith("1972-01-01 ") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getLastLoginDate().endsWith(":00:02") == true
        where:
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
        expectedadminstatus << ["DISABLED", "ENABLED"]
    }

    @Unroll
    def 'convert proxy agent data from IDMS to NSCS format for new proxy account with admin status #adminstatus'() {
        given:
        def ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
        def ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
        proxyAgentAccountCounters.setNumOfProxyAccount(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(1)
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(1)
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters)
        def List<ProxyAgentAccountDetails> proxyAgentAccounts = new ArrayList<>()
        def ProxyAgentAccountDetails proxyAgentAccount = new ProxyAgentAccountDetails()
        def String index = UUID.randomUUID().toString()
        def String dn = String.format("cn=ProxyAccount_%s,ou=proxyagentlockable,ou=com,dc=enmapache,dc=com", index)
        proxyAgentAccount.setUserDn(dn);
        proxyAgentAccount.setAdminStatus(adminstatus);
        proxyAgentAccount.setCreateTimestamp(2 * 365 * 24 * 60 * 60 * 1000L + 1000L * 1)
        proxyAgentAccount.setLastLoginTime(2 * 365 * 24 * 60 * 60 * 1000L + 1000L * 2)
        proxyAgentAccounts.add(proxyAgentAccount)
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccounts)
        when:
        def NscsProxyAccountsData nscsProxyAccountsData = nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData)
        then:
        nscsProxyAccountsData != null
        and:
        nscsProxyAccountsData.getProxyAccountsCounters() != null
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfLegacyProxyAccounts() == 1
        nscsProxyAccountsData.getProxyAccountsCounters().getNumOfRequestedLegacyProxyAccounts() == 1
        and:
        nscsProxyAccountsData.getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts() != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().isEmpty() == false
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().size() == 1
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0) != null
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getDn() == dn
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getAdminStatus() == expectedadminstatus
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().startsWith("1972-01-01 ") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getCreateDate().endsWith(":00:01") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getLastLoginDate().startsWith("1972-01-01 ") == true
        nscsProxyAccountsData.getProxyAccounts().getProxyAccounts().get(0).getLastLoginDate().endsWith(":00:02") == true
        where:
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
        expectedadminstatus << ["DISABLED", "ENABLED"]
    }

    @Unroll
    def 'convert valid proxy agent DN #dn from NSCS to IDMS format'() {
        given:
        when:
        def String idmsDN = nscsLdapProxyHelper.toIdmsProxyAccountDN(dn)
        then:
        notThrown(NscsLdapProxyException.class)
        and:
        idmsDN == dn
        where:
        dn << [
            "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com",
            "cn=ProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagent,ou=com,dc=enmapache,dc=com",
            "cn=ProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagentlockable,ou=com,dc=enmapache,dc=com",
            "cn=ProxyAccount_1,ou=proxyagent,dc=apache,dc=com",
            "cn=ProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagent,dc=apache,dc=com",
            "cn=ProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagentlockable,dc=apache,dc=com"
        ]
    }

    @Unroll
    def 'convert invalid proxy agent DN #dn from NSCS to IDMS format'() {
        given:
        when:
        def String idmsDN = nscsLdapProxyHelper.toIdmsProxyAccountDN(dn)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        dn << [
            "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc",
            "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,=",
            "cn=InvalidProxyAccount_1,ou=proxyagent,ou=com,dc=enmapache,dc=com",
            "cn=InvalidProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagent,ou=com,dc=enmapache,dc=com",
            "cn=InavlidProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagentlockable,ou=com,dc=enmapache,dc=com",
            "cn=InvalidProxyAccount_1,ou=proxyagent,dc=apache,dc=com",
            "cn=InvalidProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagent,dc=apache,dc=com",
            "cn=InvalidProxyAccount_3e98de23-aa6e-44c8-b134-02848e5c0104,ou=proxyagentlockable,dc=apache,dc=com",
            "uid=ProxyAccount_1,ou=proxyagent,dc=apache,dc=com",
            "cn=ProxyAccount_1,ou=proxyagent",
            "cn=ProxyAccount_1,ou=proxyagent,",
            "cn=ProxyAccount_1,ou=proxyagentlockable,",
            "cn=ProxyAccount_1,ou=People,dc=apache,dc=com",
            "cn=ProxyAccount_1,dc=apache,dc=com",
            "dc=apache,dc=com",
            "dc=com",
            "uid=administrator,ou=People,dc=apache,dc=com",
            "uid=mm-backup,ou=M2MUsers,dc=apache,dc=com",
            "cn=administrator,ou=proxyagent,dc=apache,dc=com",
            "cn=mm-backup,ou=ou=proxyagent,dc=apache,dc=com",
            "",
            null
        ]
    }

    @Unroll
    def 'convert valid administrative status #adminstatus from NSCS to IDMS format'() {
        given:
        when:
        def ProxyAgentAccountAdminStatus idmsAdminStatus = nscsLdapProxyHelper.toIdmsAdminStatus(adminstatus)
        then:
        notThrown(NscsLdapProxyException.class)
        and:
        idmsAdminStatus == expectedadminstatus
        where:
        adminstatus << [
            "DISABLED",
            "ENABLED"
        ]
        expectedadminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'convert invalid administrative status #adminstatus from NSCS to IDMS format'() {
        given:
        when:
        def ProxyAgentAccountAdminStatus idmsAdminStatus = nscsLdapProxyHelper.toIdmsAdminStatus(adminstatus)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        adminstatus << [
            "disabled",
            "enabled",
            "other",
            "",
            null
        ]
    }

    @Unroll
    def 'convert valid inactivity period by days #inactivityperiod from NSCS to IDMS format'() {
        given:
        def Calendar now = Calendar.getInstance()
        when:
        def Long idmsInactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodByDays(now, inactivityperiod)
        then:
        notThrown(NscsLdapProxyException.class)
        and:
        idmsInactivityPeriod != 0L
        where:
        inactivityperiod << [
            "1",
            Integer.MAX_VALUE.toString()
        ]
    }

    @Unroll
    def 'convert invalid inactivity period by days #inactivityperiod from NSCS to IDMS format'() {
        given:
        def Calendar now = Calendar.getInstance()
        when:
        def Long idmsInactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodByDays(now, inactivityperiod)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        inactivityperiod << [
            "0",
            "-1",
            Integer.MIN_VALUE.toString(),
            "not an integer",
            "",
            null
        ]
    }

    @Unroll
    def 'convert valid inactivity period by hours #inactivityperiod from NSCS to IDMS format'() {
        given:
        def Calendar now = Calendar.getInstance()
        when:
        def Long idmsInactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodByHours(now, inactivityperiod)
        then:
        notThrown(NscsLdapProxyException.class)
        and:
        idmsInactivityPeriod != 0L
        where:
        inactivityperiod << [
            "1",
            Integer.MAX_VALUE.toString()
        ]
    }

    @Unroll
    def 'convert invalid inactivity period by hours #inactivityperiod from NSCS to IDMS format'() {
        given:
        def Calendar now = Calendar.getInstance()
        when:
        def Long idmsInactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodByHours(now, inactivityperiod)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        inactivityperiod << [
            "0",
            "-1",
            Integer.MIN_VALUE.toString(),
            "not an integer",
            "",
            null
        ]
    }

    @Unroll
    def 'convert valid inactivity period by seconds #inactivityperiod from NSCS to IDMS format'() {
        given:
        def Calendar now = Calendar.getInstance()
        when:
        def Long idmsInactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodBySeconds(now, inactivityperiod)
        then:
        notThrown(NscsLdapProxyException.class)
        and:
        idmsInactivityPeriod != 0L
        where:
        inactivityperiod << [
            "1",
            Integer.MAX_VALUE.toString()
        ]
    }

    @Unroll
    def 'convert invalid inactivity period by seconds #inactivityperiod from NSCS to IDMS format'() {
        given:
        def Calendar now = Calendar.getInstance()
        when:
        def Long idmsInactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodBySeconds(now, inactivityperiod)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        inactivityperiod << [
            "0",
            "-1",
            Integer.MIN_VALUE.toString(),
            "not an integer",
            "",
            null
        ]
    }

    @Unroll
    def 'convert valid count #count from NSCS to IDMS format'() {
        given:
        when:
        def Integer idmsCount = nscsLdapProxyHelper.toIdmsCount(count)
        then:
        notThrown(NscsLdapProxyException.class)
        and:
        idmsCount == expected
        where:
        count << [
            "0",
            Integer.MAX_VALUE.toString(),
            null
        ]
        expected << [0, Integer.MAX_VALUE, null]
    }

    @Unroll
    def 'convert invalid count #count from NSCS to IDMS format'() {
        given:
        when:
        def Integer idmsCount = nscsLdapProxyHelper.toIdmsCount(count)
        then:
        thrown(NscsLdapProxyException.class)
        where:
        count << [
            "-1",
            Integer.MIN_VALUE.toString(),
            "not an integer",
            ""
        ]
    }

    def 'create deletable download file identifier with success'() {
        given:
        def String fileContent = "this is the fle content"
        def String filename = "this is the filename"
        def String contentType = "this is the content type"
        and:
        fileUtil.createDeletableDownloadFileIdentifier(_, _, _) >> "this is the created file identifier"
        when:
        def String fileIdentifier = nscsLdapProxyHelper.createDeletableDownloadFileIdentifier(fileContent.getBytes(StandardCharsets.UTF_8), filename, contentType)
        then:
        notThrown(NscsLdapProxyException.class)
        and:
        fileIdentifier == "this is the created file identifier"
    }

    def 'create deletable download file identifier with error'() {
        given:
        def String fileContent = "this is the fle content"
        def String filename = "this is the filename"
        def String contentType = "this is the content type"
        and:
        fileUtil.createDeletableDownloadFileIdentifier(_, _, _) >> { throw new IOException() }
        when:
        def String fileIdentifier = nscsLdapProxyHelper.createDeletableDownloadFileIdentifier(fileContent.getBytes(StandardCharsets.UTF_8), filename, contentType)
        then:
        thrown(NscsLdapProxyException.class)
    }
}
