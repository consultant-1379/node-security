/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.control;

import java.util.Map;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsConfirmationCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsMultiInstanceCommandResponseBuilder;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;

/**
 * Provides the Ldap Configuration Response
 * 
 * @author xsrirko
 */
public class LdapConfigurationResponseObjectBuilder extends NscsMultiInstanceCommandResponseBuilder {

    private static final String NODE_COLUMN = "Node";

    /**
     * Build the Ldap Configuration Response Object for a manual command.
     * 
     * @param ldapConfiguration
     *            the LDAP configuration parameters.
     * @return the response.
     */
    public NscsCommandResponse build(final Map<String, Object> ldapConfiguration) {
        final NscsNameValueCommandResponse response = NscsCommandResponse.nameValue();
        response.add(LdapConstants.PROPERTY, LdapConstants.VALUE);
        response.add(LdapConstants.TLS_PORT, (String) ldapConfiguration.get(LdapConstants.TLS_PORT));
        response.add(LdapConstants.LDAPS_PORT, (String) ldapConfiguration.get(LdapConstants.LDAPS_PORT));
        response.add(LdapConstants.FALLBACK_LDAP_IPV6_ADDRESS, (String) ldapConfiguration.get(LdapConstants.FALLBACK_LDAP_IPV6_ADDRESS));
        response.add(LdapConstants.LDAP_IPV6_ADDRESS, (String) ldapConfiguration.get(LdapConstants.LDAP_IPV6_ADDRESS));
        response.add(LdapConstants.FALLBACK_LDAP_IPV4_ADDRESS, (String) ldapConfiguration.get(LdapConstants.FALLBACK_LDAP_IPV4_ADDRESS));
        response.add(LdapConstants.LDAP_IPV4_ADDRESS, (String) ldapConfiguration.get(LdapConstants.LDAP_IPV4_ADDRESS));
        response.add(LdapConstants.BIND_PASSWORD, (String) ldapConfiguration.get(LdapConstants.BIND_PASSWORD));
        response.add(LdapConstants.BIND_DN, (String) ldapConfiguration.get(LdapConstants.BIND_DN));
        response.add(LdapConstants.BASE_DN, (String) ldapConfiguration.get(LdapConstants.BASE_DN));
        return response;
    }

    /**
     * Build a confirmation response to secadm ldap renew command.
     * 
     * @return the confirmation response.
     */
    public NscsCommandResponse buildLdapRenewConfirmationResponse() {
        final NscsConfirmationCommandResponse nscsConfirmationCommandResponse = new NscsConfirmationCommandResponse(
                LdapConstants.LDAP_RENEW_WARNING_RENEW_LDAP_CONFIGURATION_CONFIRMATION);
        nscsConfirmationCommandResponse.setAdditionalConfirmationMessages(LdapConstants.LDAP_RENEW_WARNING_PLEASE_CHECK_ONLINE_HELP);
        return nscsConfirmationCommandResponse;
    }

    /**
     * Build an error response to secadm ldap commands. Details of invalid nodes are reported too.
     * 
     * @param errorMessage
     *            the error message.
     * @param invalidNodes
     *            the map of the invalid nodes (key is the node FDN, value is an NscsServiceException).
     * @return the response.
     */
    public NscsCommandResponse buildErrorResponse(final String errorMessage, final Map<String, NscsServiceException> invalidNodes) {
        return buildErrorResponse(errorMessage, NODE_COLUMN, invalidNodes);
    }

}
