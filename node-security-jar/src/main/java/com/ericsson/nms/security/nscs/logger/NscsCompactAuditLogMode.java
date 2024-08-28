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
package com.ericsson.nms.security.nscs.logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;

/**
 * The management mode, from a Compact Audit Log point of view, of a command.
 * 
 * The possible CAL modes are:
 * 
 * - (synchronous) get command performed on Node/ProxyAccount/Job/Capability/Unknown resource that shall be present in CAL,
 * 
 * - synchronous command performed on Node/ProxyAccount resource that shall be present in CAL,
 * 
 * - asynchronous command performed on Node resource that shall be present in CAL,
 * 
 * - command that shall not be present in CAL.
 */
public enum NscsCompactAuditLogMode {
    COMPACT_AUDIT_LOGGED_GET_NODE_CMD,
    COMPACT_AUDIT_LOGGED_SYNC_NODE_CMD,
    COMPACT_AUDIT_LOGGED_GET_PROXY_ACCOUNT_CMD,
    COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD,
    COMPACT_AUDIT_LOGGED_SYNC_SINGLE_PROXY_ACCOUNT_CMD,
    COMPACT_AUDIT_LOGGED_GET_JOB_CMD,
    COMPACT_AUDIT_LOGGED_GET_CAPABILITY_CMD,
    COMPACT_AUDIT_LOGGED_GET_UNKNOWN_CMD,
    COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD,
    COMPACT_AUDIT_LOGGED_SYNC_NODE_CACHE_REST,
    COMPACT_AUDIT_LOGGED_SYNC_NODE_REST,
    NOT_COMPACT_AUDIT_LOGGED;

    /**
     * Return the CAL mode of the given command type.
     * 
     * Some secadm commands have a different CAL mode according to specific options specified in the command (for example the secadm ldap configure
     * applies to different resources according to the presence or absence of --manual option). Some secadm commands requiring --force option to be
     * applied have a different CAL mode according to the presence or absence of this option and according to the result of the command (they are not
     * logged if performed with success but without --force option, they are logged if performed with error even without --force option). This is the
     * reason why the whole command text and the command result are passed as parameters.
     * 
     * @param cmdType
     *            the command type.
     * @param commandText
     *            the command text.
     * @param isCommandFinishedWithError
     *            true if command failed, true if command is successful.
     * @return the CAL mode.
     */
    public static NscsCompactAuditLogMode fromCmdType(final NscsCommandType cmdType, final String commandText, final Boolean isCommandFinishedWithError) {
        NscsCompactAuditLogMode calMode = NOT_COMPACT_AUDIT_LOGGED;
        switch (cmdType) {
        case GET_CREDENTIALS:
            // break intentionally omitted
        case GET_SNMP:
            // break intentionally omitted
        case SSO_GET:
            // break intentionally omitted
        case CPP_GET_SL:
            // break intentionally omitted
        case CPP_IPSEC_STATUS:
            // break intentionally omitted
        case GET_CERT_ENROLL_STATE:
            // break intentionally omitted
        case GET_TRUST_CERT_INSTALL_STATE:
            // break intentionally omitted
        case CRL_CHECK_GET_STATUS:
            // break intentionally omitted
        case GET_CIPHERS:
            // break intentionally omitted
        case RTSEL_GET:
            // break intentionally omitted
        case HTTPS_GET_STATUS:
            // break intentionally omitted
        case FTPES_GET_STATUS:
            // break intentionally omitted
        case GET_NODE_SPECIFIC_PASSWORD:
            // break intentionally omitted
        case NTP_LIST:
            calMode = COMPACT_AUDIT_LOGGED_GET_NODE_CMD;
            break;

        case CREATE_CREDENTIALS:
            // break intentionally omitted
        case UPDATE_CREDENTIALS:
            // break intentionally omitted
        case SNMP_AUTHNOPRIV:
            // break intentionally omitted
        case SNMP_AUTHPRIV:
            // break intentionally omitted
        case IMPORT_NODE_SSH_PRIVATE_KEY:
            // break intentionally omitted
        case SSO_ENABLE:
            // break intentionally omitted
        case SSO_DISABLE:
            // break intentionally omitted
        case ENROLLMENT_INFO_FILE:
            calMode = COMPACT_AUDIT_LOGGED_SYNC_NODE_CMD;
            break;

        case LDAP_PROXY_GET:
            calMode = COMPACT_AUDIT_LOGGED_GET_PROXY_ACCOUNT_CMD;
            break;

        case LDAP_PROXY_SET:
            // break intentionally omitted
        case LDAP_PROXY_DELETE:
            if (commandText.contains("--force") || isCommandFinishedWithError) {
                calMode = COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD;
            }
            break;

        case GET_JOB:
            calMode = COMPACT_AUDIT_LOGGED_GET_JOB_CMD;
            break;

        case CAPABILITY_GET:
            calMode = COMPACT_AUDIT_LOGGED_GET_CAPABILITY_CMD;
            break;

        case TEST_COMMAND:
            if (!commandText.contains("--workflows") || isCommandFinishedWithError) {
                calMode = COMPACT_AUDIT_LOGGED_GET_UNKNOWN_CMD;
            } else {
                calMode = COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD;
            }
            break;

        case CERTIFICATE_ISSUE:
            // break intentionally omitted
        case CERTIFICATE_REISSUE:
            // break intentionally omitted
        case TRUST_DISTRIBUTE:
            // break intentionally omitted
        case TRUST_REMOVE:
            // break intentionally omitted
        case CPP_IPSEC:
            // break intentionally omitted
        case CPP_SET_SL:
            // break intentionally omitted
        case CREATE_SSH_KEY:
            // break intentionally omitted
        case UPDATE_SSH_KEY:
            // break intentionally omitted
        case DELETE_SSH_KEY:
            // break intentionally omitted
	    case LDAP_RECONFIGURATION:
            // break intentionally omitted
        case CRL_CHECK_ENABLE:
            // break intentionally omitted
        case CRL_CHECK_DISABLE:
            // break intentionally omitted
        case ON_DEMAND_CRL_DOWNLOAD:
            // break intentionally omitted
        case SET_CIPHERS:
            // break intentionally omitted
        case RTSEL_ACTIVATE:
            // break intentionally omitted
        case RTSEL_DEACTIVATE:
            // break intentionally omitted
        case RTSEL_DELETE:
            // break intentionally omitted
        case HTTPS_ACTIVATE:
            // break intentionally omitted
        case HTTPS_DEACTIVATE:
            // break intentionally omitted
        case FTPES_ACTIVATE:
            // break intentionally omitted
        case FTPES_DEACTIVATE:
            // break intentionally omitted
        case LAAD_FILES_DISTRIBUTE:
            // break intentionally omitted
        case NTP_CONFIGURE:
            // break intentionally omitted
        case NTP_REMOVE:
            calMode = COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD;
            break;

        case LDAP_RENEW:
            if (commandText.contains("--force") || isCommandFinishedWithError) {
                calMode = COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD;
            }
            break;

        case LDAP_CONFIGURATION:
            if (commandText.contains("--manual")) {
                calMode = COMPACT_AUDIT_LOGGED_SYNC_SINGLE_PROXY_ACCOUNT_CMD;
            } else {
                calMode = COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD;
            }
            break;

        case CPP_INSTALL_LAAD:
            // break intentionally omitted
        case ADD_TARGET_GROUPS:
            // break intentionally omitted
        case SET_ENROLLMENT:
            // break intentionally omitted
        default:
            break;
        }
        return calMode;
    }

    /**
     * Return the CAL mode of the given rest URL path and method.
     * 
     * @param restUrlPath
     *            the rest URL path.
     * @param restMethod
     *            the rest method.
     * @return the CAL mode.
     */
    static NscsCompactAuditLogMode fromRestUrlPath(final String restUrlPath, final String restMethod) {
        NscsCompactAuditLogMode calMode = NOT_COMPACT_AUDIT_LOGGED;
        switch (restUrlPath) {
        case "/node-security/2.0/nodes":
            if ("POST".equals(restMethod)) {
                calMode = COMPACT_AUDIT_LOGGED_SYNC_NODE_CACHE_REST;
            }
            break;

        case "/node-security/nodes/seclevel":
            if ("POST".equals(restMethod)) {
                calMode = COMPACT_AUDIT_LOGGED_SYNC_NODE_REST;
            }
            break;

        default:
            break;
        }
        return calMode;
    }
}
