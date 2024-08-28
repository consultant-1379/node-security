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
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.LdapProxySetCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.LdapProxyResponseBuilder;
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import com.ericsson.oss.services.security.nscs.command.EventDataCommandIdentifier;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyConstants;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyHelper;
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccount;

/**
 * Command handler of ldap proxy set command.
 */
@CommandType(NscsCommandType.LDAP_PROXY_SET)
@Local(CommandHandlerInterface.class)
public class LdapProxySetCommandHandler implements CommandHandler<LdapProxySetCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private IdentityManagementProxy identityManagementProxy;

    @Inject
    private NscsLdapProxyHelper nscsLdapProxyHelper;

    @Inject
    private LdapProxyResponseBuilder ldapProxyResponseBuilder;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsCommandResponse process(final LdapProxySetCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);
        NscsCommandResponse response;
        try {
            response = buildLdapProxySetResponse(command);
            nscsLogger.commandHandlerFinishedWithSuccess(command, NscsLdapProxyConstants.LDAP_PROXY_SET_SUCCESS);
        } catch (final NscsServiceException e) {
            final String errorMsg = String.format("%sCommand failed due to %s.", NscsLdapProxyConstants.LDAP_PROXY_SET_FAILURE,
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw e;
        } catch (final Exception e) {
            final String errorMsg = String.format("%sCommand failed due to unexpected %s.", NscsLdapProxyConstants.LDAP_PROXY_SET_FAILURE,
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw new NscsLdapProxyException(errorMsg, e);
        }
        return response;
    }

    /**
     * Builds the response to ldap proxy set command.
     * 
     * @param command
     *            the ldap proxy set command.
     * @return the response to ldap proxy set command.
     */
    private NscsCommandResponse buildLdapProxySetResponse(final LdapProxySetCommand command) {

        final ProxyAgentAccountAdminStatus proxyAgentAccountAdminStatus = nscsLdapProxyHelper.toIdmsAdminStatus(command.getAdminStatus());
        final List<NscsProxyAccount> nscsProxyAccounts = nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(command);
        if (!command.isForce()) {
            return ldapProxyResponseBuilder.buildLdapProxySetConfirmationResponse();
        }
        final Map<String, NscsServiceException> failedProxyAccounts = new HashMap<>();
        final List<NscsProxyAccount> successProxyAccounts = new ArrayList<>();
        updateProxyAccountsAdminStatus(proxyAgentAccountAdminStatus, nscsProxyAccounts, successProxyAccounts, failedProxyAccounts);
        updateCommandHandlerStatsFormatter(proxyAgentAccountAdminStatus, nscsProxyAccounts, successProxyAccounts, failedProxyAccounts);

        if (failedProxyAccounts.isEmpty()) {
            return ldapProxyResponseBuilder.buildLdapProxySetSuccessResponse(nscsProxyAccounts.size());
        } else {
            return ldapProxyResponseBuilder.buildLdapProxySetErrorResponse(nscsProxyAccounts.size(), successProxyAccounts.size(),
                    failedProxyAccounts);
        }
    }

    /**
     * Updates the admin status of specified list of proxy accounts.
     * 
     * A map for failed proxy accounts and a list of successfully updated proxy accounts is returned too.
     * 
     * @param proxyAgentAccountAdminStatus
     *            the wanted admin status.
     * @param nscsProxyAccounts
     *            the list of to be updated NSCS proxy accounts.
     * @param successProxyAccounts
     *            the list of successfully updated NSCS proxy accounts.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     */
    private void updateProxyAccountsAdminStatus(final ProxyAgentAccountAdminStatus proxyAgentAccountAdminStatus,
            final List<NscsProxyAccount> nscsProxyAccounts, final List<NscsProxyAccount> successProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        for (final NscsProxyAccount nscsProxyAccount : nscsProxyAccounts) {
            updateProxyAccountAdminStatus(proxyAgentAccountAdminStatus, nscsProxyAccount, successProxyAccounts, failedProxyAccounts);
        }
    }

    /**
     * Updates the admin status of specified proxy account.
     * 
     * A map for failed proxy accounts and a list of successfully updated proxy accounts is returned too.
     * 
     * @param proxyAgentAccountAdminStatus
     *            the wanted admin status.
     * @param nscsProxyAccount
     *            the to be updated NSCS proxy account.
     * @param successProxyAccounts
     *            the list of successfully updated NSCS proxy accounts.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     */
    private void updateProxyAccountAdminStatus(final ProxyAgentAccountAdminStatus proxyAgentAccountAdminStatus,
            final NscsProxyAccount nscsProxyAccount, final List<NscsProxyAccount> successProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        try {
            final String proxyAccountDN = nscsLdapProxyHelper.toIdmsProxyAccountDN(nscsProxyAccount.getDn());
            final Boolean updated = identityManagementProxy.updateProxyAccountAdminStatus(proxyAccountDN, proxyAgentAccountAdminStatus);
            if (updated) {
                successProxyAccounts.add(nscsProxyAccount);
            } else {
                final NscsLdapProxyException nscsLdapProxyException = new NscsLdapProxyException("Not existent proxy account.");
                failedProxyAccounts.put(proxyAccountDN, nscsLdapProxyException);
            }
        } catch (final NscsLdapProxyException e) {
            final String errorMsg = String.format("Exception occurred updating admin status %s for proxy account %s : %s.",
                    proxyAgentAccountAdminStatus.toString(), nscsProxyAccount.getDn(), NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            failedProxyAccounts.put(nscsProxyAccount.getDn(), e);
        }
    }

    /**
     * Update the NSCS command handler statistics formatter and the context.
     * 
     * @param proxyAgentAccountAdminStatus
     *            the wanted admin status.
     * @param nscsProxyAccounts
     *            the list of to be updated NSCS proxy accounts.
     * @param successProxyAccounts
     *            the list of successfully updated NSCS proxy accounts.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     */
    private void updateCommandHandlerStatsFormatter(final ProxyAgentAccountAdminStatus proxyAgentAccountAdminStatus,
            final List<NscsProxyAccount> nscsProxyAccounts, final List<NscsProxyAccount> successProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        final EventDataCommandIdentifier commandId = ProxyAgentAccountAdminStatus.DISABLED.equals(proxyAgentAccountAdminStatus)
                ? EventDataCommandIdentifier.LDAP_PROXY_DISABLE
                : EventDataCommandIdentifier.LDAP_PROXY_ENABLE;
        final Integer total = Integer.valueOf(nscsProxyAccounts.size());
        final Integer success = Integer.valueOf(successProxyAccounts.size());
        final Integer failed = Integer.valueOf(failedProxyAccounts.size());
        nscsLogger.updateCommandHandlerStatsFormatter(commandId, total, success, failed);

        // update context, valid and success are the same
        // invalid are the failed
        // failed are 0
        nscsContextService.updateItemsStatsForSyncCommand(success, failed, success, 0);
    }

}
