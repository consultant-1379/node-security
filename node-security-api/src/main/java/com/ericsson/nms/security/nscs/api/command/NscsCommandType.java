/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.api.command;

/**
 * Enumeration of the supported command types.
 * 
 * <p>
 * An entry should be added here for each new supported Nscs command.
 * </p>
 * Created by emaynes on 01/05/2014.
 */
public enum NscsCommandType {
        CPP_GET_SL, 
        CPP_SET_SL, 
        CPP_INSTALL_LAAD, 
        CREATE_CREDENTIALS, 
        UPDATE_CREDENTIALS,
        GET_CREDENTIALS,
        ADD_TARGET_GROUPS, 
        CPP_IPSEC_STATUS, 
        CPP_IPSEC, 
        CREATE_SSH_KEY,
        UPDATE_SSH_KEY,
        DELETE_SSH_KEY,
        IMPORT_NODE_SSH_PRIVATE_KEY,
        TEST_COMMAND,
        CERTIFICATE_ISSUE,
        SNMP_AUTHPRIV,
        SNMP_AUTHNOPRIV,
        TRUST_DISTRIBUTE,
        SET_ENROLLMENT,
        GET_CERT_ENROLL_STATE,
        GET_TRUST_CERT_INSTALL_STATE,
        CERTIFICATE_REISSUE,
        LDAP_CONFIGURATION,
        LDAP_RECONFIGURATION,
        LDAP_RENEW,
        LDAP_PROXY_GET,
        LDAP_PROXY_SET,
        LDAP_PROXY_DELETE,
        TRUST_REMOVE,
        CRL_CHECK_ENABLE,
        GET_JOB,
        CRL_CHECK_DISABLE,
        CRL_CHECK_GET_STATUS,
        ON_DEMAND_CRL_DOWNLOAD,
        SET_CIPHERS,
        GET_CIPHERS,
        ENROLLMENT_INFO_FILE,
        RTSEL_ACTIVATE,
        RTSEL_DEACTIVATE,
        RTSEL_GET,
        RTSEL_DELETE,
        HTTPS_ACTIVATE,
        HTTPS_DEACTIVATE,
        HTTPS_GET_STATUS,
        GET_SNMP,
        FTPES_ACTIVATE,
        FTPES_DEACTIVATE,
        FTPES_GET_STATUS,
        GET_NODE_SPECIFIC_PASSWORD,
        CAPABILITY_GET,
        LAAD_FILES_DISTRIBUTE,
        NTP_LIST,
        NTP_REMOVE,
        NTP_CONFIGURE,
        SSO_ENABLE,
        SSO_DISABLE,
        SSO_GET;
}
