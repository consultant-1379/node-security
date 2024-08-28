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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ericsson.nms.security.nscs.api.exception.LdapConfigurationException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountCounters;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyConstants;

/**
 * This class wraps access to Identity Management Service (IDMS) remotely exposed by identitymgmtservices.
 */
public class IdentityManagementProxy {

    @EServiceRef
    private IdentityManagementService identityManagementService;

    /**
     * Creates a new proxy account.
     * 
     * @return the created proxy account.
     * @throws LdapConfigurationException
     *             if creation failed (error code is LDAP_CONFIGURE_OPERATION_FAILED).
     */
    public ProxyAgentAccountData createProxyAgentAccount() {

        ProxyAgentAccountData proxyAgentAccountData = null;
        try {
            proxyAgentAccountData = identityManagementService.createProxyAgentAccount();
        } catch (final IdentityManagementServiceException e) {
            throw new LdapConfigurationException(NscsErrorCodes.LDAP_PROXY_ACCOUNT_CREATION_FAILED, e);
        }
        return proxyAgentAccountData;
    }

    /**
     * Deletes the proxy account of given DN.
     * 
     * @param proxyAccountDN
     *            the proxy account DN.
     * @return true if proxy account successfully deleted or false if proxy account not existent.
     * @throws LdapConfigurationException
     *             if deletion failed (error code is LDAP_RECONFIGURE_OPERATION_FAILED).
     */
    public Boolean deleteProxyAgentAccount(final String proxyAccountDN) {

        Boolean proxyAgentDeleteResponse = false;
        try {
            proxyAgentDeleteResponse = identityManagementService.deleteProxyAgentAccount(proxyAccountDN);
        } catch (IdentityManagementServiceException e) {
            throw new LdapConfigurationException(NscsErrorCodes.LDAP_PROXY_ACCOUNT_DELETION_FAILED, e);
        }
        return proxyAgentDeleteResponse;
    }

    /**
     * Gets from Identity Management Service all requested proxy accounts.
     * 
     * If isLegacy is true, only proxy accounts present in legacy branch are requested.
     * 
     * If isSummary is true, only proxy accounts counters (not details) are requested.
     * 
     * @param isLegacy
     *            if true, only proxy accounts present in legacy branch are requested.
     * @param isSummary
     *            if true, only proxy accounts counters (not details) are requested.
     * @param count
     *            the maximum number of proxy accounts to return.
     * @throws NscsLdapProxyException
     *             if any exception received from Identity Management Service
     */
    public ProxyAgentAccountGetData getAllProxyAccounts(final Boolean isLegacy, final Boolean isSummary, final Integer count) {
        // mock to be removed
        if (count != null) {
            return buildMockProxyAccountData(count, isLegacy, isSummary);
        }
        try {
            return identityManagementService.getProxyAgentAccount(isLegacy, isSummary);
        } catch (final Exception e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_EXCEPTION_READING_ALL_PROXY_ACCOUNTS_FORMAT, isLegacy,
                    isSummary);
            throw new NscsLdapProxyException(message, e);
        }
    }

    /**
     * Gets from Identity Management Service all requested proxy accounts by admin status.
     * 
     * If isLegacy is true, only proxy accounts present in legacy branch are requested.
     * 
     * If isSummary is true, only proxy accounts counters (not details) are requested.
     * 
     * @param adminStatus
     *            the admin status.
     * @param isLegacy
     *            if true, only proxy accounts present in legacy branch are requested.
     * @param isSummary
     *            if true, only proxy accounts counters (not details) are requested.
     * @throws NscsLdapProxyException
     *             if any exception received from Identity Management Service
     */
    public ProxyAgentAccountGetData getAllProxyAccountsByAdminStatus(final ProxyAgentAccountAdminStatus adminStatus, final Boolean isLegacy,
            final Boolean isSummary) {
        try {
            return identityManagementService.getProxyAgentAccountByAdminStatus(adminStatus, isLegacy, isSummary);
        } catch (final Exception e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_EXCEPTION_READING_PROXY_ACCOUNTS_BY_PARAM_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_PARAM, adminStatus, isLegacy, isSummary);
            throw new NscsLdapProxyException(message, e);
        }
    }

    /**
     * Gets from Identity Management Service all requested proxy accounts by inactivity period.
     * 
     * If isLegacy is true, only proxy accounts present in legacy branch are requested.
     * 
     * If isSummary is true, only proxy accounts counters (not details) are requested.
     * 
     * @param inactivityPeriod
     *            the inactivity period.
     * @param isLegacy
     *            if true, only proxy accounts present in legacy branch are requested.
     * @param isSummary
     *            if true, only proxy accounts counters (not details) are requested.
     * @throws NscsLdapProxyException
     *             if any exception received from Identity Management Service
     */
    public ProxyAgentAccountGetData getAllProxyAccountsByInactivityPeriod(final Long inactivityPeriod, final Boolean isLegacy,
            final Boolean isSummary) {
        try {
            return identityManagementService.getProxyAgentAccountByInactivityPeriod(inactivityPeriod, isLegacy, isSummary);
        } catch (final Exception e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_EXCEPTION_READING_PROXY_ACCOUNTS_BY_PARAM_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM, inactivityPeriod, isLegacy, isSummary);
            throw new NscsLdapProxyException(message, e);
        }
    }

    /**
     * Gets from Identity Management Service the details of the proxy account of given DN.
     * 
     * @param proxyAccountDN
     *            the proxy account DN.
     * @return the details of proxy account.
     */
    public ProxyAgentAccountDetails getProxyAccountDetails(final String proxyAccountDN) {
        try {
            return identityManagementService.getProxyAgentAccountDetails(proxyAccountDN);
        } catch (final Exception e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_EXCEPTION_READING_PROXY_ACCOUNT_BY_DN_FORMAT, proxyAccountDN);
            throw new NscsLdapProxyException(message, e);
        }
    }

    /**
     * Updates the admin status of the proxy account of given DN.
     * 
     * @param proxyAccountDN
     *            the proxy account DN.
     * @param adminStatus
     *            the admin status to set.
     * @return true if proxy account admin status updated or false if proxy account not existent.
     */
    public Boolean updateProxyAccountAdminStatus(final String proxyAccountDN, final ProxyAgentAccountAdminStatus adminStatus) {
        Boolean proxyAgentUpdateResponse = false;
        try {
            proxyAgentUpdateResponse = identityManagementService.updateProxyAgentAccountAdminStatus(proxyAccountDN, adminStatus);
        } catch (final Exception e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_SET_EXCEPTION_UPDATING_PROXY_ACCOUNT_DN_PARAM_VALUE_FORMAT,
                    proxyAccountDN, NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_PARAM, adminStatus);
            throw new NscsLdapProxyException(message, e);
        }
        return proxyAgentUpdateResponse;
    }

    /**
     * Gets the password of the given M2M user.
     * 
     * @param m2mUser
     *            the M2M user.
     * @return the password of the M2M user.
     */
    public char[] getM2MPassword(final String m2mUser) {
        return identityManagementService.getM2MPassword(m2mUser);
    }

    /**
     * Mock to build a fake ProxyAgentAccountGetData containing a given number of proxy account(s) as returned by IDMS.
     * 
     * To be removed.
     * 
     * @param count
     *            number of fake returned proxy accounts.
     * @param isLegacy
     *            only proxy accounts on legacy branch are generated
     * @param isSummary
     *            only summary is requested
     * @return the object containing the requested proxy accounts.
     */
    private ProxyAgentAccountGetData buildMockProxyAccountData(final Integer count, final Boolean isLegacy, final Boolean isSummary) {
        final Integer numProxyAccounts = count / 2;
        final Integer numLegacyProxyAccounts = count / 2 + count % 2;
        final Integer numTotProxyAccounts = numProxyAccounts + numLegacyProxyAccounts;
        final ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData();
        final ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters();
        proxyAgentAccountCounters.setNumOfProxyAccount(numTotProxyAccounts);
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(numTotProxyAccounts);
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(numLegacyProxyAccounts);
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(numLegacyProxyAccounts);
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters);
        if (!isSummary) {
            final List<ProxyAgentAccountDetails> proxyAgentAccounts = new ArrayList<>();
            if (!isLegacy) {
                for (Integer index = 1; index <= numProxyAccounts; index++) {
                    final ProxyAgentAccountDetails proxyAgentAccount = buildProxyAgentAccountDetails(index, false);
                    proxyAgentAccounts.add(proxyAgentAccount);
                }
            }
            for (Integer index = 1; index <= numLegacyProxyAccounts; index++) {
                final ProxyAgentAccountDetails proxyAgentAccount = buildProxyAgentAccountDetails(index, true);
                proxyAgentAccounts.add(proxyAgentAccount);
            }
            proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccounts);
        }
        return proxyAgentAccountGetData;
    }

    private ProxyAgentAccountDetails buildProxyAgentAccountDetails(final Integer index, final Boolean isLegacy) {
        final ProxyAgentAccountDetails proxyAgentAccount = new ProxyAgentAccountDetails();
        // legacy proxy accounts are in proxyagent branch, not legacy proxy accounts are in proxyagentlockable branch
        // legacy proxy accounts use index, not legacy proxy accounts use UUID
        proxyAgentAccount.setUserDn(String.format("cn=ProxyAccount_%s,ou=proxyagent%s,ou=com,dc=enmapache,dc=com",
                getProxyAccountIndexAsString(index, isLegacy), isLegacy ? "" : "lockable"));
        // odd index is enabled, even index is disabled
        proxyAgentAccount.setAdminStatus(index % 2 == 0 ? ProxyAgentAccountAdminStatus.DISABLED : ProxyAgentAccountAdminStatus.ENABLED);
        // legacy proxy accounts are of 1972, not legacy proxy accounts are of 1974
        proxyAgentAccount.setCreateTimestamp((isLegacy ? 2 : 4) * 365 * 24 * 60 * 60 * 1000L + 1000L * index);
        proxyAgentAccount.setLastLoginTime((isLegacy ? 2 : 4) * 365 * 24 * 60 * 60 * 1000L + 2000L * index);
        return proxyAgentAccount;
    }

    private String getProxyAccountIndexAsString(final Integer index, final Boolean isLegacy) {
        if (isLegacy) {
            return Integer.toString(index);
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
