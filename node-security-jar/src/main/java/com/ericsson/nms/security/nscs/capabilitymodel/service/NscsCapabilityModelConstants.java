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
package com.ericsson.nms.security.nscs.capabilitymodel.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Auxiliary class containing the constants related to NSCS Capability Models.
 * 
 * Please note that two distinct Capability Models are defined for NSCS: NSCS_CLI containing all capabilities related to management of "secadm" CLI
 * commands and NSCS_SECM containing all capabilities related to management of node security.
 * 
 * @author emaborz
 * 
 */
public class NscsCapabilityModelConstants {

    /**
     * NSCS Capability Model global constants
     */
    public static final String NSCS_TARGET_TYPE_SPECIFIC_CAPABILITYSUPPORT_MODEL_VER = "1.0.0";

    /**
     * NSCS Capability Model Name
     */
    public static final String NSCS_CAPABILITY_MODEL = "NSCS";

    /**
     * NSCS Capability Names
     */
    public static final String NSCS_CAPABILITY_UNSUPPORTED_COMMANDS = "unsupportedSecadmCliCommands";
    public static final String NSCS_CAPABILITY_CREDS_PARAMS = "credentialsParams";
    public static final String NSCS_CAPABILITY_SUPPORTED_CERT_TYPES = "supportedCertificateTypes";
    public static final String NSCS_CAPABILITY_SUPPORTED_TRUST_CATEGORIES = "supportedTrustCategories";
    public static final String NSCS_CAPABILITY_DEFAULT_ENTITY_PROFILES = "defaultEntityProfiles";
    public static final String NSCS_CAPABILITY_ISSUE_CERT_WORKFLOWS = "issueCertificateWorkflows";
    public static final String NSCS_CAPABILITY_TRUST_DISTR_WORKFLOWS = "trustDistributeWorkflows";
    public static final String NSCS_CAPABILITY_TRUST_REMOVE_WORKFLOWS = "trustRemoveWorkflows";
    public static final String NSCS_CAPABILITY_MOM_TYPE = "momType";
    public static final String NSCS_CAPABILITY_IS_CERT_MANAGEMENT_SUPPORTED = "isCertificateManagementSupported";
    public static final String NSCS_CAPABILITY_SUPPORTED_SECURITY_LEVELS = "supportedSecurityLevels";
    public static final String NSCS_CAPABILITY_SUPPORTED_ENROLLMENT_MODES = "supportedEnrollmentModes";
    public static final String NSCS_CAPABILITY_DEFAULT_ENROLLMENT_MODE = "defaultEnrollmentMode";
    public static final String NSCS_CAPABILITY_DEFAULT_KEY_ALGORITHM = "defaultKeyAlgorithm";
    public static final String NSCS_CAPABILITY_DEFAULT_FINGERPRINT_ALGORITHM = "defaultFingerprintAlgorithm";
    public static final String NSCS_CAPABILITY_IS_SYNC_ENROLLMENT_SUPPORTED = "isSynchronousEnrollmentSupported";
    public static final String NSCS_CAPABILITY_IS_CONF_SUBJECT_NAME_USED_FOR_ENROLL = "isConfiguredSubjectNameUsedForEnrollment";
    public static final String NSCS_CAPABILITY_IS_IKEV2_POLICY_PROFILE_SUPPORTED = "isIkev2PolicyProfileSupported";
    public static final String NSCS_CAPABILITY_IS_LDAP_COMMON_USER_SUPPORTED = "isLdapCommonUserSupported";
    public static final String NSCS_CAPABILITY_DEFAULT_INITIAL_OTP_COUNT = "defaultInitialOtpCount";
    public static final String NSCS_CAPABILITY_IS_DEPRECATED_ENROLLMENT_AUTHORITY_USED = "isDeprecatedEnrollmentAuthorityUsed";
    public static final String NSCS_CAPABILITY_IS_DEPRECATED_AUTHORITY_TYPE_SUPPORTED = "isDeprecatedAuthorityTypeSupported";
    public static final String NSCS_CAPABILITY_CRL_CHECK_SUPPORTED_CERT_TYPES = "crlCheckSupportedCertificateTypes";
    public static final String NSCS_CRL_CHECK_WORKFLOWS = "crlCheckWorkflows";
    public static final String NSCS_ON_DEMAND_CRL_DOWNLOAD_WORKFLOW = "onDemandCrlDownloadWorkflow";
    public static final String NSCS_CAPABILITY_SUPPORTED_CIPHER_PROTOCOL_TYPES = "supportedCipherProtocolTypes";
    public static final String NSCS_CAPABILITY_CIPHER_MO_ATTRIBUTES = "cipherMoAttributes";
    public static final String NSCS_CAPABILITY_COM_ECIM_DEFAULT_NODE_CREDENTIAL_ID = "comEcimDefaultNodeCredentialId";
    public static final String NSCS_CAPABILITY_COM_ECIM_DEFAULT_TRUST_CATEGORY_ID = "comEcimDefaultTrustCategoryId";
    public static final String NSCS_CAPABILITY_IS_EMPTY_CIPHER_SUPPORTED = "isEmptyCiphersSupported";
    public static final String NSCS_CAPABILITY_ENROLLMENT_CA_AUTHORIZATION_MODES = "enrollmentCAAuthorizationModes";
    public static final String NSCS_CAPABILITY_DEFAULT_PASSPHRASE_HASH_ALGORITHM = "defaultPassPhraseHashAlgorithm";
    public static final String NSCS_CAPABILITY_PUSH_M2M_USER = "pushM2MUser";
    public static final String NSCS_CAPABILITY_NTP_REMOVE_WORKFLOW = "ntpRemoveWorkflow";
    public static final String NSCS_CAPABILITY_NTP_CONFIGURE_WORKFLOW = "ntpConfigureWorkflow";
    public static final String NSCS_CAPABILITY_IS_NODE_SSH_PRIVATEKEY_IMPORT_SUPPORTED = "isNodeSshPrivateKeyImportSupported";
    public static final String NSCS_CAPABILITY_CONFIGURE_LDAP_WORKFLOW = "configureLdapWorkflow";
    public static final String NSCS_CAPABILITY_LDAP_MO_NAME = "ldapMoName";
    public static final String NSCS_CAPABILITY_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID = "defaultEnrollmentCaTrustCategoryId";
    public static final String NSCS_CAPABILITY_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES = "defaultOtpValidityPeriodInMinutes";

    /**
     * NSCS Capability Model constants
     */
    public static final String NSCS_CAPABILITY_PARAMS_EXPECTED_ATTRIBUTE = "expected";
    public static final String NSCS_CAPABILITY_PARAMS_UNEXPECTED_ATTRIBUTE = "unexpected";
    public static final String NSCS_CAPABILITY_MIRROR_ROOT_MO_INFO_TYPE_ATTRIBUTE = "type";
    public static final String NSCS_CAPABILITY_MIRROR_ROOT_MO_INFO_NS_ATTRIBUTE = "namespace";
    public static final String NSCS_CPP_MOM = "CPP";
    public static final String NSCS_ECIM_MOM = "ECIM";
    public static final String NSCS_EOI_MOM = "EOI";
    public static final String NSCS_CAPABILITY_CIPHERS_PROTOCOL_TYPE_SSH = "SSH_SFTP";
    public static final String NSCS_CAPABILITY_CIPHERS_PROTOCOL_TYPE_TLS = "SSL_HTTPS_TLS";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_MAC = "selected_mac";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_KEY_EXCHANGE = "selected_key_exchange";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_CIPHER = "selected_cipher";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_MAC = "supported_mac";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_KEY_EXCHANGE = "supported_key_exchange";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_CIPHER = "supported_cipher";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_ENABLED_CIPHER = "enabled_cipher";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_CIPHER_FILTER = "cipher_filter";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_TLS = "Tls";
    public static final String NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SSH = "Ssh";

    /**
     * Get the names of all security capability models and, for each model, the names of its capabilities.
     * 
     * @return the names of all security capability models and, for each model, the names of its capabilities.
     */
    public static Map<String, Set<String>> getCapabilityModels() {
        final Set<String> nscsCapabilities = new HashSet<String>();
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_UNSUPPORTED_COMMANDS);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_CREDS_PARAMS);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CERT_TYPES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENTITY_PROFILES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_ISSUE_CERT_WORKFLOWS);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_DISTR_WORKFLOWS);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_REMOVE_WORKFLOWS);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_MOM_TYPE);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CERT_MANAGEMENT_SUPPORTED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_SECURITY_LEVELS);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_ENROLLMENT_MODES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENROLLMENT_MODE);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_KEY_ALGORITHM);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_FINGERPRINT_ALGORITHM);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_SYNC_ENROLLMENT_SUPPORTED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CONF_SUBJECT_NAME_USED_FOR_ENROLL);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_IKEV2_POLICY_PROFILE_SUPPORTED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_LDAP_COMMON_USER_SUPPORTED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_INITIAL_OTP_COUNT);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_ENROLLMENT_AUTHORITY_USED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_AUTHORITY_TYPE_SUPPORTED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_CRL_CHECK_SUPPORTED_CERT_TYPES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CRL_CHECK_WORKFLOWS);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_ON_DEMAND_CRL_DOWNLOAD_WORKFLOW);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CIPHER_PROTOCOL_TYPES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHER_MO_ATTRIBUTES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_NODE_CREDENTIAL_ID);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_TRUST_CATEGORY_ID);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_EMPTY_CIPHER_SUPPORTED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_ENROLLMENT_CA_AUTHORIZATION_MODES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_PASSPHRASE_HASH_ALGORITHM);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_TRUST_CATEGORIES);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_NTP_REMOVE_WORKFLOW);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_PUSH_M2M_USER);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_NTP_CONFIGURE_WORKFLOW);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_NODE_SSH_PRIVATEKEY_IMPORT_SUPPORTED);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_CONFIGURE_LDAP_WORKFLOW);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_LDAP_MO_NAME);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID);
        nscsCapabilities.add(NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES);

        final Map<String, Set<String>> models = new HashMap<String, Set<String>>();
        models.put(NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL, nscsCapabilities);
        return models;
    }

}
