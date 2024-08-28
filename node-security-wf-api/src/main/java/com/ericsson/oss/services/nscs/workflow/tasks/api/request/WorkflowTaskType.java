/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request;

/**
 * <p>
 * Enum that holds all the types of tasks provided by the Node security service to the security workflows
 * </p>
 *
 * @author emaynes.
 */
public enum WorkflowTaskType {
    PROTOTYPE_TASK,
    CPP_ENABLE_CORBA_SECURITY,
    CPP_DISABLE_CORBA_SECURITY,
    CPP_READ_FILE_TRANSFER_CLIENT_MODE,
    CPP_READ_TRUSTED_CERTIFICATE_INTALLATION_FAILURE,
    CPP_READ_TRUSTED_CERTIFICATE_IPSEC_INSTALLATION_STATE,
    CPP_READ_CERT_ENROLL_STATE,
    CPP_READ_CERT_ENROLL_IPSEC_STATE,
    CPP_ADAPT_SECURITY_LEVEL,
    CPP_ENABLE_SECURE_FILE_TRANSFER_CLIENT_MODE,
    CPP_CLEAR_INSTALL_TRUST_FLAGS,
    CPP_CLEAR_INSTALL_TRUST_IPSEC_FLAGS,
    CPP_INSTALL_TRUSTED_CERTIFICATE,
    CPP_INSTALL_TRUSTED_CERTIFICATE_IPSEC,
    CPP_CANCEL_CERT_ENROLLMENT,
    CPP_CANCEL_CERT_ENROLLMENT_IPSEC,
    CPP_INIT_CERT_ENROLLMENT,
    CPP_INIT_CERT_ENROLLMENT_IPSEC,
    CPP_DELETE_FILES_SMRS,
    CPP_CHANGE_IP_OAM_SETTING,
    CPP_DEACTIVATE_IPSEC,
    CPP_CHECK_CERT_ALREADY_INSTALLED,
    CPP_CHECK_TRUSTED_ALREADY_INSTALLED,
    CPP_CHECK_TRUSTED_OAM_ALREADY_INSTALLED,
    SSH_KEY_GENERATION,
    DELETE_ENM_SSH_KEY,
    CONFIGURE_MODELED_SSH_KEY,
    CONFIGURE_ENM_SSH_KEY,
    VERIFY_SSH_KEY,
    INVALIDATE_ENM_SSH_KEY,
    REVOKE_NODE_CERTIFICATE,
    CPP_ISSUE_CERT_ENROLLMENT_IPSEC,
    CPP_ISSUE_CERT_ENROLLMENT,
    CPP_ISSUE_TRUSTED_CERTIFICATE,
    CPP_ISSUE_TRUSTED_CERTIFICATE_IPSEC,
    CPP_CLEANUP_M2M_USER_AND_SMRS,
    COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED,
    COM_ECIM_CHECK_TRUSTED_CATEGORY,
    COM_ECIM_CHECK_ENROLLMENT_PROTOCOL,
    COM_ECIM_CHECK_NODE_CREDENTIAL,
    CPP_REMOVE_TRUST_OAM,
    CPP_REMOVE_TRUST_NEW_IPSEC,
    COM_ECIM_CHECK_REMOVE_TRUST,
    COM_ECIM_REMOVE_TRUST,
    COM_ECIM_VALIDATE_USER_PROVIDED_LDAP_CONFIGURATION,
    COM_ECIM_CONFIGURE_LDAP_ACTION,
    COM_ECIM_GET_LDAP_COMMON_CONFIG,
    COM_ECIM_GET_LDAP_NODE_CONFIG,
    COM_ECIM_CONFIGURE_CREDENTIAL_USERS,
    COM_ECIM_CONFIGURE_TRUST_USERS,
    COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT,
    COM_ECIM_PREPARE_START_ONLINE_ENROLLMENT,
    PERFORM_MO_ACTION,
    CHECK_MO_ACTION_PROGRESS,
    COM_ECIM_ENABLE_OR_DISABLE_CRL_CHECK,
    CPP_ENABLE_OR_DISABLE_CRL_CHECK,
    LOG_ERROR,
    LOG_SUCCESS,LOG_TIMEOUT,
    LOG_FAILURE,
    COM_ECIM_PREPARE_CHECK_ON_DEMAND_CRL_DOWNLOAD,
    COM_ECIM_PREPARE_ON_DEMAND_CRL_DOWNLOAD,
    CPP_ON_DEMAND_CRL_DOWNLOAD,
    SET_MO_ATTRIBUTES,
    CPP_ENABLE_HTTPS,
    CPP_DISABLE_HTTPS,
    CPP_NODE_VALIDATION_HTTPS,
    CPP_ADD_EXTERNAL_SERVER,
    RTSEL_DELETE_SERVER,
    CPP_VALIDATE_NODE_OAM_CERTIFICATE,
    CPP_COMPARE_HTTPS_STATUS_FOR_ACTIVATE,
    CPP_COMPARE_HTTPS_STATUS_FOR_DEACTIVATE,
    GET_HTTPS_STATUS_CLI,
    CPP_COMPARE_HTTP,
    CPP_COMPARE_HTTPS,
    COM_NODE_VALIDATION_FTPES,
    COM_ACTIVATE_FTPES,
    COM_DEACTIVATE_FTPES,
    COM_ECIM_CHECK_AND_UPDATE_ENDENTITY,
    CPP_GET_LAAD_FILES,
    CPP_READ_LAAD_INSTALLATION_FAILURE,
    CPP_CANCEL_INSTALL_LAAD_FAILURE,
    CPP_INSTALL_LAAD_ACTION,
    PREPARE_INTERNAL_CA_TRUSTED_ENTITY_INFO,
    PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO,
    COM_ECIM_PREPARE_EXT_CA_ENROLLMENT_INFO,
    COM_ECIM_CHECK_NODE_CREDENTIAL_FOR_EXTERNAL_CA_REISSUE,
    COM_ECIM_CHECK_IS_EXTERNAL_CA,
    VALIDATE_NODE_FOR_NTP_REMOVE,
    CPP_CHECK_AND_REMOVE_NTP_SERVER,
    REMOVE_NTP_KEYS,
    CPP_CHECK_KEYS_REMOVED,
    CPP_REMOVE_NTP_KEY_DATA_MAPPING,
    CHECK_NODE_SYNC_STATUS,
    CPP_GET_NTP_KEY_DATA,
    CPP_CONFIGURE_NTP_SERVER,
    NTP_CONFIGURE_FAILURE_STATUS_SENDER,
    CPP_CHECK_NTP_KEYS_INSTALLED,
    CPP_INSTALL_NTP_KEYS,
    COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO,
    COM_ECIM_VALIDATE_NODE_FOR_NTP_REMOVE,
    COM_ECIM_NTP_CONFIGURE,
    CBPOI_PREPARE_CA_TRUSTED_PEM_CERTIFICATES,
    CBPOI_CHECK_CERTIFICATES_ALREADY_INSTALLED,
    CBPOI_CHECK_CREATE_TRUST_CATEGORIES,
    CBPOI_PREPARE_INSTALL_TRUSTED_CERTS,
    CBPOI_CONFIGURE_SERVICES_TRUST_REFERENCE,
    CBPOI_CONFIGURE_LDAP_ACTION,
    PERFORM_SYNC_MO_ACTION,
    CBP_OI_CHECK_CMP_CONFIG,
    CBP_OI_CHECK_NODE_CREDENTIAL_CMP_CONFIG,
    CBP_OI_PREPARE_ONLINE_ENROLLMENT,
    CBP_OI_RESTORE_RENEWAL_MODE,
    CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES,
    CBP_OI_CHECK_REMOVE_TRUST,
    CBP_OI_REMOVE_TRUST,
    CBP_OI_POST_ONLINE_ENROLLMENT,
    TEST_CHECK_SOMETHING,
    TEST_DO_SOMETHING,
    LDAP_CONFIGURATION,
    DELETE_LDAP_PROXY_ACCOUNT;
}
