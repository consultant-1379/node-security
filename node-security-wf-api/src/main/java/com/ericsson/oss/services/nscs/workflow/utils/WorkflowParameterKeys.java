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
 * Defines the name of WFS parameters keys to be set/read when running a flow, to pass parameters from CLI Handler and WFS Task/Handler in
 * 
 * @author elucbot
 * 
 */
public enum WorkflowParameterKeys {

    KEY_SSHKEYS_GENERATION_ISCREATE("IS_CREATE"),
    KEY_SSHKEYS_OPERATION("SSHKEY_OPERATION"),
    KEY_SSHKEYS_GENERATION_ALGORITHM("ALGORITHM"),
    IS_MODELED_SSH_KEY("isModeledSSHKey"),
    MOM_TYPE("momType"),
    CERTIFICATE_ISREISSUE("IS_REISSUE"),
    CERTIFICATE_ID("CERT_ID"),
    CERTIFICATE_AUTHORITY_ID("CA_ID"),
    IPSEC_SUB_ALT_NAME("SUB_ALT_NAME"),
    IPSEC_SUB_ALT_NAME_TYPE("SUB_ALT_NAME_TYPE"),
    ROLLBACK_TIMEOUT("ROLLBACK_TIMEOUT"),
    ENROLLMENT_MODE("ENROLLMENT_MODE"),
    ENTITY_PROFILE_NAME("ENTITY_PROFILE_NAME"),
    REVOCATION_REASON("REVOCATION_REASON"),
    TRUSTED_CATEGORY("TRUSTED_CATEGORY"),
    TRUST_CERTS("TRUST_CERTS"),
    OUTPUT_PARAMS("OUTPUT_PARAMS"),
    BASE_DN("baseDn"),
    BIND_DN("bindDn"),
    BIND_PASSWORD("bindPassword"),
    LDAP_SERVER_PORT("serverPort"),
    LDAP_IP_ADDRESS("ldapIpAddress"),
    FALLBACK_LDAP_IP_ADDRESS("fallbackLdapIpAddress"),
    TLS_MODE("tlsMode"),
    USE_TLS("useTls"),
    USER_LABEL("userLabel"),
    LDAP_WORKFLOW_CONTEXT("ldapWorkFlowContext"),
    IS_RENEW("isRenew"),
    COMMON_NAME("COMMON_NAME"),
    CERT_TYPE("cert_type"),
    JOB_ID("job_id"),
    INNERWF_PARAM("innerWFParam"),
    INNERWF_CHILD("innerWFChild"),
    SCHEDULED_WF("scheduledWF"),
    CRL_CHECK_STATUS("crl_check_status"),
    NODE_KEY("NODE_FDN"),
    WF_WAKE_ID("WF_WAKE_ID"),
    MO_ATTRIBUTES_KEY_VALUES("mOAttributesKey"),
    SHORT_DESCRIPTION("SHORT_DESCRIPTION"),
    SERVER_CONFIG("serverConfig"),
    SERVER_NAMES("serverNames"),
    EXTERNAL_TRUSTED_CA_CERTIFICATE_INFO("trustedCaCertificateInfo"),
    CERTIFICATE_ENROLLMENT_CA("certificateEnrollmentCA"),
    EXTERNAL_CA_INTERFACE_FDN("interfaceFdn"),
    EXTERNAL_CA_CERTIFICATE_AUTHORITY_DN("certificateAuthorityDn"),
    EXTERNAL_CA_CERTIFICATE_SUBJECT_DN("certificateSubjectDn"),
    EXTERNAL_CA_CERTIFICATE("caCertificate"),
    EXTERNAL_CA_ENROLLMENT_SERVER_URL("enrollmentServerUrl"),
    EXTERNAL_CA_CHALLENGE_PASSWORD("challengePassword"),
    IS_TRUST_DISTRIBUTION_REQUIRED("isTrustDistributionRequired"),                                                                                                                                                                                                                   EXTERNAL_CA_SUBJECT_ALT_NAME("subjectAltName"), IS_EXTERNAL_CA_REISSUE("isExternalCaReissue"), 
    NTP_KEY_IDS("KeyIdList"),
    NODE_NTP_SERVER_KEY_ID_INFO("nodeNtpServerKeyIdInfo"),
    NTP_KEY("ntpKey"),
    NTP_SERVER_IDS("serverIdList"),
    TEST_CHECK_RESULT("testCheckResult"),
    TEST_ACTION_RESULT("testActionResult");

    private final String wfParameterKeyName;

    private WorkflowParameterKeys(final String name) {
        this.wfParameterKeyName = name;
    }

    @Override
    public String toString() {
        return this.wfParameterKeyName;
    }
}
