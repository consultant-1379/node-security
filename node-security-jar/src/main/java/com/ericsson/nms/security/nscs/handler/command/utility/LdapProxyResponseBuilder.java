/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2019
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.utility;

import java.util.Map;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsConfirmationCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyConstants;

/**
 * Auxiliary class to build the response to 'secadm ldap proxy' commands.
 */
public class LdapProxyResponseBuilder extends NscsMultiInstanceCommandResponseBuilder {

    private static final String PROXY_ACCOUNT = "Proxy Account";

    /**
     * Build a download request response to a successfully performed secadm ldap proxy get command.
     * 
     * @param fileIdentifier
     *            the file identifier.
     * @param message
     *            the message.
     * @return the download request response.
     */
    public NscsCommandResponse buildLdapProxyGetSuccessResponse(final String fileIdentifier, final String message) {
        return new NscsDownloadRequestMessageCommandResponse(0, fileIdentifier, message);
    }

    /**
     * Build a confirmation command response to secadm ldap proxy set command.
     * 
     * @return the confirmation command response.
     */
    public NscsCommandResponse buildLdapProxySetConfirmationResponse() {
        final NscsConfirmationCommandResponse nscsConfirmationCommandResponse = new NscsConfirmationCommandResponse(
                NscsLdapProxyConstants.LDAP_PROXY_SET_WARNING_UPDATE_PROXY_ACCOUNT_ADMIN_STATUS_CONFIRMATION);
        nscsConfirmationCommandResponse.setAdditionalConfirmationMessages(NscsLdapProxyConstants.LDAP_PROXY_WARNING_PLEASE_CHECK_ONLINE_HELP);
        return nscsConfirmationCommandResponse;
    }

    /**
     * Build a success response to secadm ldap proxy set command successfully performed on all the given proxy accounts.
     * 
     * @param numOfProxyAccounts
     *            the number of proxy accounts successfully updated.
     * @return the response.
     */
    public NscsCommandResponse buildLdapProxySetSuccessResponse(final int numOfProxyAccounts) {
        return buildSuccessResponse(String.format(NscsLdapProxyConstants.LDAP_PROXY_SET_ALL_SUCCESS_FORMAT, numOfProxyAccounts));
    }

    /**
     * Build an error response to secadm ldap proxy set command successfully performed on none or some of the given proxy accounts.
     * 
     * Details of failed proxy accounts are returned too.
     * 
     * @param numOfProxyAccounts
     *            the number of proxy accounts to update.
     * @param numOfSuccessProxyAccounts
     *            the number of proxy accounts successfully updated.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     * @return the partial success response.
     */
    public NscsCommandResponse buildLdapProxySetErrorResponse(final int numOfProxyAccounts, final int numOfSuccessProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        final String additionalInformation = numOfSuccessProxyAccounts > 0
                ? String.format(NscsLdapProxyConstants.LDAP_PROXY_SET_PARTIAL_SUCCESS_FORMAT, numOfSuccessProxyAccounts, numOfProxyAccounts)
                : String.format(NscsLdapProxyConstants.LDAP_PROXY_SET_ALL_FAILED_FORMAT, numOfProxyAccounts);
        return buildErrorResponse(additionalInformation, PROXY_ACCOUNT, failedProxyAccounts);
    }

    /**
     * Build a confirmation command response to secadm ldap proxy delete command.
     * 
     * @return the confirmation command response.
     */
    public NscsCommandResponse buildLdapProxyDeleteConfirmationResponse() {
        final NscsConfirmationCommandResponse nscsConfirmationCommandResponse = new NscsConfirmationCommandResponse(
                NscsLdapProxyConstants.LDAP_PROXY_DELETE_WARNING_DELETE_PROXY_ACCOUNT_CONFIRMATION);
        nscsConfirmationCommandResponse.setAdditionalConfirmationMessages(NscsLdapProxyConstants.LDAP_PROXY_WARNING_PLEASE_CHECK_ONLINE_HELP);
        return nscsConfirmationCommandResponse;
    }

    /**
     * Build a success response to secadm ldap proxy delete command successfully performed on all the given proxy accounts.
     * 
     * @param numOfProxyAccounts
     *            the number of proxy accounts successfully deleted.
     * @return the response.
     */
    public NscsCommandResponse buildLdapProxyDeleteSuccessResponse(final int numOfProxyAccounts) {
        return buildSuccessResponse(String.format(NscsLdapProxyConstants.LDAP_PROXY_DELETE_ALL_SUCCESS_FORMAT, numOfProxyAccounts));
    }

    /**
     * Build an error response to secadm ldap proxy delete command successfully performed on none or some of the given proxy accounts.
     * 
     * Details of failed proxy accounts are returned too.
     * 
     * @param numOfProxyAccounts
     *            the number of proxy accounts to delete.
     * @param numOfSuccessProxyAccounts
     *            the number of proxy accounts successfully deleted.
     * @param failedProxyAccounts
     *            the map of failed proxy accounts.
     * @return the partial success response.
     */
    public NscsCommandResponse buildLdapProxyDeleteErrorResponse(final int numOfProxyAccounts, final int numOfSuccessProxyAccounts,
            final Map<String, NscsServiceException> failedProxyAccounts) {
        final String additionalInformation = numOfSuccessProxyAccounts > 0
                ? String.format(NscsLdapProxyConstants.LDAP_PROXY_DELETE_PARTIAL_SUCCESS_FORMAT, numOfSuccessProxyAccounts, numOfProxyAccounts)
                : String.format(NscsLdapProxyConstants.LDAP_PROXY_DELETE_ALL_FAILED_FORMAT, numOfProxyAccounts);
        return buildErrorResponse(additionalInformation, PROXY_ACCOUNT, failedProxyAccounts);
    }

}
