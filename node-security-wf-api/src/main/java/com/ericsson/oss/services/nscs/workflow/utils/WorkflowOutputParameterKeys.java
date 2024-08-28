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
package com.ericsson.oss.services.nscs.workflow.utils;

/**
 * This enum class contains the values of the keys of the outputParams map used to pass parameters through workflows.
 *
 */
public enum WorkflowOutputParameterKeys {

    ENROLLMENT_INFO("ENROLLMENT_INFO"),
    ENROLLMENT_MODE("ENROLLMENT_MODE"),
    ALGORITHM_KEYS("ALGORITHM_KEYS"),
    NODE_CREDENTIAL_FDN("NODE_CREDENTIAL_FDN"),
    CURRENT_NODE_CREDENTIAL_FDN("CURRENT_NODE_CREDENTIAL_FDN"),
    RESERVED_BY_USER("RESERVED_BY_USER"),
    RENEWAL_MODE("RENEWAL_MODE"),
    TRUSTED_CA_ENTITY_LIST("TRUSTED_CA_ENTITY_LIST"),
    ENROLLMENT_CA_ENTITY("ENROLLMENT_CA_ENTITY"),
    TRUSTED_CERTIFICATE_FDN("TRUSTED_CERTIFICATE_FDN"),
    TRUST_CATEGORY_FDN("TRUST_CATEGORY_FDN"),
    MO_ACTIONS("MO_ACTIONS"),
    TRUSTED_CERTIFICATE_FDN_LIST("TRUSTED_CERTIFICATE_FDN_LIST"),
    ENROLLMENT_CA_TRUSTED_CERTIFICATE_FDN("ENROLLMENT_CA_TRUSTED_CERTIFICATE_FDN"),
    IS_ONLINE_ENROLLMENT("IS_ONLINE_ENROLLMENT"),
    TRUSTED_ENTITY_INFO("TRUSTED_ENTITY_INFO"),
    INTERFACE_IP_ADDRESS_FDN("INTERFACE_IP_ADDRESS_FDN"),
    NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE("NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE"),
    MO_ACTION("MO_ACTION"),
    MAPPING_TO_BE_REMOVED_FOR_KEY_IDS("MAPPING_TO_BE_REMOVED_FOR_KEY_IDS"),
    CPP_ARE_NTP_KEYS_REMOVED("areNtpKeysRemoved"),
    NTP_KEY("NTP_KEY"),
    NTP_SERVER_IDS_TO_BE_DELETED_FROM_NODE("NTP_SERVER_IDS_TO_BE_DELETED_FROM_NODE"),
    CMP_SERVER_GROUP_NAME("CMP_SERVER_GROUP_NAME"),
    ASYMMETRIC_KEYS_CMP_FDN("ASYMMETRIC_KEYS_CMP_FDN"),
    ASYMMETRIC_KEY_CMP_FDN("ASYMMETRIC_KEY_CMP_FDN"),
    ASYMMETRIC_KEY_NAME("ASYMMETRIC_KEY_NAME"),
    TRUSTED_CERTS_NAME("TRUSTED_CERTS_NAME"),
    IS_START_CMP_REQUIRED("IS_START_CMP_REQUIRED"),
    MAX_NUM_OF_RETRIES("MAX_NUM_OF_RETRIES"),
    REMAINING_NUM_OF_RETRIES("REMAINING_NUM_OF_RETRIES"),
    PUBLIC_SSH_KEY("PUBLIC_SSH_KEY"),
    ENCRYPTED_PRIVATE_SSH_KEY("ENCRYPTED_PRIVATE_SSH_KEY"),
    BASE_DN("baseDn"),
    PREVIOUS_BIND_DN("previousBindDn"),
    BIND_DN("bindDn"),
    BIND_PASSWORD("bindPassword"),
    LDAP_SERVER_PORT("serverPort"),
    LDAP_IP_ADDRESS("ldapIpAddress"),
    FALLBACK_LDAP_IP_ADDRESS("fallbackLdapIpAddress");

    private final String wfOutputParameterKeyName;

    private WorkflowOutputParameterKeys(final String name) {
        this.wfOutputParameterKeyName = name;
    }

    @Override
    public String toString() {
        return this.wfOutputParameterKeyName;
    }
}
