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
import com.ericsson.nms.security.nscs.api.command.types.LdapProxyDeleteCommand;
import com.ericsson.nms.security.nscs.api.exception.LdapConfigurationException;
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.LdapProxyResponseBuilder;
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import com.ericsson.oss.services.security.nscs.command.EventDataCommandIdentifier;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyConstants;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyHelper;
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccount;

/**
 * Command handler of ldap proxy delete command.
 */
@CommandType(NscsCommandType.LDAP_PROXY_DELETE)
@Local(CommandHandlerInterface.class)
public class LdapProxyDeleteCommandHandler implements CommandHandler<LdapProxyDeleteCommand>, CommandHandlerInterface {

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
    public NscsCommandResponse process(final LdapProxyDeleteCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);
        NscsCommandResponse response;
        try {
            response = buildLdapProxyDeleteResponse(command);
            nscsLogger.commandHandlerFinishedWithSuccess(command, NscsLdapProxyConstants.LDAP_PROXY_DELETE_SUCCESS);
        } catch (final NscsServiceException e) {
            final String errorMsg = String.format("%sCommand failed due to %s.", NscsLdapProxyConstants.LDAP_PROXY_DELETE_FAILURE,
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw e;
        } catch (final Exception e) {
            final String errorMsg = String.format("%sCommand failed due to unexpected %s.", NscsLdapProxyConstants.LDAP_PROXY_DELETE_FAILURE,
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw new NscsLdapProxyException(errorMsg, e);
        }
        return response;
    }

    /**
     * Builds the response to ldap proxy delete command.
     * 
     * @param command
     *            the ldap proxy delete command.
     * @return the response to ldap proxy delete command.
     */
    private NscsCommandResponse buildLdapProxyDeleteResponse(final LdapProxyDeleteCommand command) {

        final List<NscsProxyAccount> nscsProxyAccounts = nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(command);
        if (!command.isForce()) {
            return ldapProxyResponseBuilder.buildLdapProxyDeleteConfirmationResponse();
        }
        final Map<String, NscsServiceException> failedProxyAccounts = new HashMap<>();
        final List<NscsProxyAccount> successProxyAccounts = new ArrayList<>();
        deleteProxyAccounts(nscsProxyAccounts, successProxyAccounts, failedProxyAccounts);
        updateCommandHandlerStatsFormatter(nscsProxyAccounts, successProxyAccounts, failedProxyAccounts);

        if (failedProxyAccounts.isEmpty()) {
            return ldapProxyResponseBuilder.buildLdapProxyDeleteSuccessResponse(nscsProxyAccounts.size());
        } else {
            return ldapProxyResponseBuilder.buildLdapProxyDeleteErrorResponse(nscsProxyAccounts.size(), successProxyAccounts.size(),
                    failedProxyAccounts);
        }
    }

    /**
     * Deletes the specified list of proxy accounts.
     * 
     * A map for failed proxy accounts and a list of successfully deleted proxy accounts is returned too.
     * 
     * @param nscsProxyAccounts
     *            the list of to be deleted NSCS proxy accounts.
     * @param successProxyAccounts
     *            the list of successfully deleted NSCS proxy accounts.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     */
    private void deleteProxyAccounts(final List<NscsProxyAccount> nscsProxyAccounts, final List<NscsProxyAccount> successProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        for (final NscsProxyAccount nscsProxyAccount : nscsProxyAccounts) {
            deleteProxyAccount(nscsProxyAccount, successProxyAccounts, failedProxyAccounts);
        }
    }

    /**
     * Deletes the specified proxy account.
     * 
     * The specified proxy account shall be in admin status DISABLED to be deleted.
     * 
     * The map for failed proxy accounts and the list of successfully deleted proxy accounts is updated too.
     * 
     * @param nscsProxyAccount
     *            the to be deleted NSCS proxy account.
     * @param successProxyAccounts
     *            the list of successfully deleted NSCS proxy accounts.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     */
    private void deleteProxyAccount(final NscsProxyAccount nscsProxyAccount, final List<NscsProxyAccount> successProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        try {
            final String proxyAccountDN = nscsLdapProxyHelper.toIdmsProxyAccountDN(nscsProxyAccount.getDn());
            final ProxyAgentAccountDetails proxyAgentAccountDetails = identityManagementProxy.getProxyAccountDetails(proxyAccountDN);
            if (ProxyAgentAccountAdminStatus.DISABLED.equals(proxyAgentAccountDetails.getAdminStatus())) {
                final Boolean deleted = identityManagementProxy.deleteProxyAgentAccount(proxyAccountDN);
                if (deleted) {
                    successProxyAccounts.add(nscsProxyAccount);
                } else {
                    final NscsLdapProxyException nscsLdapProxyException = new NscsLdapProxyException("Not existent proxy account.");
                    failedProxyAccounts.put(proxyAccountDN, nscsLdapProxyException);
                }
            } else {
                final NscsLdapProxyException nscsLdapProxyException = new NscsLdapProxyException("Only disabled proxy account can be deleted.");
                failedProxyAccounts.put(proxyAccountDN, nscsLdapProxyException);
            }
        } catch (final NscsLdapProxyException | LdapConfigurationException e) {
            final String errorMsg = String.format("Exception occurred deleting proxy account %s : %s.", nscsProxyAccount.getDn(),
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            failedProxyAccounts.put(nscsProxyAccount.getDn(), e);
        }
    }

    /**
     * Update the NSCS command handler statistics formatter and the context.
     * 
     * @param nscsProxyAccounts
     *            the list of to be deleted NSCS proxy accounts.
     * @param successProxyAccounts
     *            the list of successfully deleted NSCS proxy accounts.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     */
    private void updateCommandHandlerStatsFormatter(final List<NscsProxyAccount> nscsProxyAccounts, final List<NscsProxyAccount> successProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        final Integer total = Integer.valueOf(nscsProxyAccounts.size());
        final Integer success = Integer.valueOf(successProxyAccounts.size());
        final Integer failed = Integer.valueOf(failedProxyAccounts.size());
        nscsLogger.updateCommandHandlerStatsFormatter(EventDataCommandIdentifier.LDAP_PROXY_DELETE, total, success, failed);

        // update context, valid and success are the same
        // invalid are the failed
        // failed are 0
        nscsContextService.updateItemsStatsForSyncCommand(success, failed, success, 0);
    }

}
