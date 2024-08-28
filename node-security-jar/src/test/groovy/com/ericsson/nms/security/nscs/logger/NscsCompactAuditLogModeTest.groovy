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
package com.ericsson.nms.security.nscs.logger

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType

import spock.lang.Shared
import spock.lang.Unroll

class NscsCompactAuditLogModeTest extends CdiSpecification {

    @Shared
    getNodeCompactAuditLogged = [
        NscsCommandType.CPP_GET_SL,
        NscsCommandType.GET_CREDENTIALS,
        NscsCommandType.CPP_IPSEC_STATUS,
        NscsCommandType.GET_CERT_ENROLL_STATE,
        NscsCommandType.GET_TRUST_CERT_INSTALL_STATE,
        NscsCommandType.CRL_CHECK_GET_STATUS,
        NscsCommandType.GET_CIPHERS,
        NscsCommandType.RTSEL_GET,
        NscsCommandType.HTTPS_GET_STATUS,
        NscsCommandType.GET_SNMP,
        NscsCommandType.FTPES_GET_STATUS,
        NscsCommandType.GET_NODE_SPECIFIC_PASSWORD,
        NscsCommandType.NTP_LIST,
        NscsCommandType.SSO_GET
    ]

    @Shared
    syncNodeCompactAuditLogged = [
        NscsCommandType.CREATE_CREDENTIALS,
        NscsCommandType.UPDATE_CREDENTIALS,
        NscsCommandType.SNMP_AUTHPRIV,
        NscsCommandType.SNMP_AUTHNOPRIV,
        NscsCommandType.IMPORT_NODE_SSH_PRIVATE_KEY,
        NscsCommandType.SSO_DISABLE,
        NscsCommandType.SSO_ENABLE,
        NscsCommandType.ENROLLMENT_INFO_FILE
    ]

    @Shared
    getProxyAccountCompactAuditLogged = [
        NscsCommandType.LDAP_PROXY_GET
    ]

    @Shared
    syncWithForceCompactAuditLogged = [
        NscsCommandType.LDAP_PROXY_DELETE,
        NscsCommandType.LDAP_PROXY_SET
    ]

    @Shared
    getJobCompactAuditLogged = [
        NscsCommandType.GET_JOB
    ]

    @Shared
    getCapabilityCompactAuditLogged = [
        NscsCommandType.CAPABILITY_GET
    ]

    @Shared
    getUnknownCompactAuditLogged = [
        NscsCommandType.TEST_COMMAND
    ]

    @Shared
    asyncCompactAuditLogged = [
        NscsCommandType.CERTIFICATE_ISSUE,
        NscsCommandType.CERTIFICATE_REISSUE,
        NscsCommandType.TRUST_DISTRIBUTE,
        NscsCommandType.TRUST_REMOVE,
        NscsCommandType.CPP_IPSEC,
        NscsCommandType.CPP_SET_SL,
        NscsCommandType.CREATE_SSH_KEY,
        NscsCommandType.UPDATE_SSH_KEY,
        NscsCommandType.LDAP_CONFIGURATION,
        NscsCommandType.LDAP_RECONFIGURATION,
        NscsCommandType.CRL_CHECK_ENABLE,
        NscsCommandType.CRL_CHECK_DISABLE,
        NscsCommandType.ON_DEMAND_CRL_DOWNLOAD,
        NscsCommandType.SET_CIPHERS,
        NscsCommandType.RTSEL_ACTIVATE,
        NscsCommandType.RTSEL_DEACTIVATE,
        NscsCommandType.RTSEL_DELETE,
        NscsCommandType.HTTPS_ACTIVATE,
        NscsCommandType.HTTPS_DEACTIVATE,
        NscsCommandType.FTPES_ACTIVATE,
        NscsCommandType.FTPES_DEACTIVATE,
        NscsCommandType.LAAD_FILES_DISTRIBUTE,
        NscsCommandType.NTP_CONFIGURE,
        NscsCommandType.NTP_REMOVE
    ]

    @Shared
    asyncWithForceCompactAuditLogged = [
        NscsCommandType.LDAP_RENEW
    ]

    @Shared
    unsupportedNotCompactAuditLogged = [
        NscsCommandType.CPP_INSTALL_LAAD,
        NscsCommandType.ADD_TARGET_GROUPS,
        NscsCommandType.SET_ENROLLMENT
    ]

    @Shared
    nodeCacheRestPostCompactAuditLogged = [
        "/node-security/2.0/nodes"
    ]

    @Shared
    nodeRestPostCompactAuditLogged = [
        "/node-security/nodes/seclevel"
    ]

    @Shared
    unsupportedRestNotCompactAuditLogged = [
        "/node-security/nodes/notlogged"
    ]

    def 'successful synchronous secadm ldap configure manual'() {
        given:
        def NscsCommandType cmdType = NscsCommandType.LDAP_CONFIGURATION
        def String commandText = 'secadm ldap configure --manual'
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdType, commandText, isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_SINGLE_PROXY_ACCOUNT_CMD
    }

    def 'failed synchronous secadm ldap configure manual'() {
        given:
        def NscsCommandType cmdType = NscsCommandType.LDAP_CONFIGURATION
        def String commandText = 'secadm ldap configure --manual'
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdType, commandText, isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_SINGLE_PROXY_ACCOUNT_CMD
    }

    @Unroll
    def 'successful get node command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_NODE_CMD
        where:
        cmdtype << getNodeCompactAuditLogged
    }

    @Unroll
    def 'failed get node command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_NODE_CMD
        where:
        cmdtype << getNodeCompactAuditLogged
    }

    @Unroll
    def 'successful synchronous node command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_NODE_CMD
        where:
        cmdtype << syncNodeCompactAuditLogged
    }

    @Unroll
    def 'failed synchronous node command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_NODE_CMD
        where:
        cmdtype << syncNodeCompactAuditLogged
    }

    @Unroll
    def 'successful get proxy account command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_PROXY_ACCOUNT_CMD
        where:
        cmdtype << getProxyAccountCompactAuditLogged
    }

    @Unroll
    def 'failed get proxy account command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_PROXY_ACCOUNT_CMD
        where:
        cmdtype << getProxyAccountCompactAuditLogged
    }

    @Unroll
    def 'successful get job command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_JOB_CMD
        where:
        cmdtype << getJobCompactAuditLogged
    }

    @Unroll
    def 'failed get job command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_JOB_CMD
        where:
        cmdtype << getJobCompactAuditLogged
    }

    @Unroll
    def 'successful get capability command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_CAPABILITY_CMD
        where:
        cmdtype << getCapabilityCompactAuditLogged
    }

    @Unroll
    def 'failed get capability command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_CAPABILITY_CMD
        where:
        cmdtype << getCapabilityCompactAuditLogged
    }

    @Unroll
    def 'successful get unknown command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_UNKNOWN_CMD
        where:
        cmdtype << getUnknownCompactAuditLogged
    }

    @Unroll
    def 'failed get unknown command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_GET_UNKNOWN_CMD
        where:
        cmdtype << getUnknownCompactAuditLogged
    }

    @Unroll
    def 'successful synchronous with force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command --force", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD
        where:
        cmdtype << syncWithForceCompactAuditLogged
    }

    @Unroll
    def 'failed synchronous with force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command --force", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD
        where:
        cmdtype << syncWithForceCompactAuditLogged
    }

    @Unroll
    def 'successful synchronous without force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED
        where:
        cmdtype << syncWithForceCompactAuditLogged
    }

    @Unroll
    def 'failed synchronous without force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD
        where:
        cmdtype << syncWithForceCompactAuditLogged
    }

    @Unroll
    def 'successful asynchronous command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD
        where:
        cmdtype << asyncCompactAuditLogged
    }

    @Unroll
    def 'failed asynchronous command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD
        where:
        cmdtype << asyncCompactAuditLogged
    }

    @Unroll
    def 'successful asynchronous with force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command --force", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD
        where:
        cmdtype << asyncWithForceCompactAuditLogged
    }

    @Unroll
    def 'failed asynchronous with force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command --force", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD
        where:
        cmdtype << asyncWithForceCompactAuditLogged
    }

    @Unroll
    def 'successful asynchronous without force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED
        where:
        cmdtype << asyncWithForceCompactAuditLogged
    }

    @Unroll
    def 'failed asynchronous without force command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD
        where:
        cmdtype << asyncWithForceCompactAuditLogged
    }

    @Unroll
    def 'successful unsupported command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = false
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED
        where:
        cmdtype << unsupportedNotCompactAuditLogged
    }

    @Unroll
    def 'failed unsupported command type #cmdtype'() {
        given:
        def Boolean isCommandFinishedWithError = true
        when:
        def NscsCompactAuditLogMode calMode =  NscsCompactAuditLogMode.fromCmdType(cmdtype, "secadm command", isCommandFinishedWithError)
        then:
        calMode == NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED
        where:
        cmdtype << unsupportedNotCompactAuditLogged
    }

    @Unroll
    def 'node cache rest POST #restpath'() {
        given:
        when:
        def NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromRestUrlPath(restpath, "POST")
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_NODE_CACHE_REST
        where:
        restpath << nodeCacheRestPostCompactAuditLogged
    }

    @Unroll
    def 'node cache rest GET #restpath'() {
        given:
        when:
        def NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromRestUrlPath(restpath, "GET")
        then:
        calMode == NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED
        where:
        restpath << nodeCacheRestPostCompactAuditLogged
    }

    @Unroll
    def 'node rest POST #restpath'() {
        given:
        when:
        def NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromRestUrlPath(restpath, "POST")
        then:
        calMode == NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_NODE_REST
        where:
        restpath << nodeRestPostCompactAuditLogged
    }

    @Unroll
    def 'node rest GET #restpath'() {
        given:
        when:
        def NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromRestUrlPath(restpath, "GET")
        then:
        calMode == NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED
        where:
        restpath << nodeRestPostCompactAuditLogged
    }

    @Unroll
    def 'unsupported rest not logged #restpath'() {
        given:
        when:
        def NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromRestUrlPath(restpath, "any")
        then:
        calMode == NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED
        where:
        restpath << unsupportedRestNotCompactAuditLogged
    }
}
