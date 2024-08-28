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
package com.ericsson.nms.security.nscs.workflow.task.cpp;

/**
 * <p>
 * Class holding a number of constant values used by security workflows.
 * </p>
 *
 * @author emaynes on 19/06/2014.
 */
public abstract class WFMessageConstants {

    public static final String CPP_COMMAND_FILE_TRANSFER_CLIENT_MODE_SUCCESS = "CPPCommandFileTransferClientModeSuccess";
    public static final String CPP_EVENT_9_DOWNLOAD_OF_TRUSTED_CERTIFICATES_COMPLETED = "CPPEvent9DownloadOfTrustedCertificatesCompleted";
    public static final String CPP_EVENT_27_NODE_CREDENTIALS_INSTALLED = "CPPEvent27NodeCredentialsInstalled";
    public static final String CPP_COMMAND_OPERATIONAL_SECURITY_LEVEL_SUCCESS = "CPPCommandOperationalSecurityLevelSuccess";

    public static final String CPP_COMMAND_FILE_TRANSFER_CLIENT_MODE_FAIL = "CPPCommandFileTransferClientModeFail";
    public static final String CPP_COMMAND_OPERATIONAL_SECURITY_LEVEL_FAIL = "CPPCommandOperationalSecurityLevelFail";
    public static final String CPP_FILE_TRANSFER_CLIENT_MODE_SECURE = "secure";
    public static final String CPP_FILE_TRANSFER_CLIENT_MODE_UNSECURE = "unsecure";

    public static final String CPP_ATT_TRUSTED_CERTIFICATE_INSTALLATION_FAILURE_CHANGE = "ATTtrustedCertificateInstallationFailureChange";
    public static final String CPP_ATT_CERT_ENROLL_STATE_CHANGE = "ATTcertEnrollStateChange";
    public static final String CPP_ATT_IP_HOSTLINK_IPADDRESS_CHANGE_SUCCESS = "ATTipAddressChange";

    public static final String CPP_ATT_CONFIGURED_SECURITY_LEVEL_CHANGE_SUCCESS = "ATTConfiguredSecurityLevelChangeSuccess";
    public static final String CPP_ATT_CONFIGURED_SECURITY_LEVEL_CHANGE_FAIL = "ATTConfiguredSecurityLevelChangeFail";

    public static final String CPP_COMMAND_INIT_CERT_ENROLLMENT_IPSEC_SUCCESS = "CPPCommandInitCertEnrollmentIpSecSuccess";
    public static final String CPP_COMMAND_INIT_CERT_ENROLLMENT_IPSEC_FAILED = "CPPCommandInitCertEnrollmentIpSecFailed";
    public static final String CPP_COMMAND_TRUSTED_CERT_INSTALL_IPSEC_SUCCESS = "CPPCommandTrustedCertInstallIpSecSuccess";
    public static final String CPP_COMMAND_TRUSTED_CERT_INSTALL_IPSEC_FAILED = "CPPCommandTrustedCertInstallIpSecFailed";

    public static final String SSH_KEY_GENERATION_COMMAND_SUCCESS = "SSHKeyGenCommandSuccess";
    public static final String SSH_KEY_GENERATION_COMMAND_FAIL = "SSHKeyGenCommandFail";

    public static final String COM_ECIM_CERTM_REPORT_PROGRESS_CHANGE = "ATTreportProgressChange";
    public static final String COM_ECIM_CERTM_INSTALLTRUSTFROMURI_ACTION_FAILED = "COMECIMAlarmTrustedCertificateInstallationFault";
    public static final String COM_ECIM_CERTM_INSTALLTRUSTFROMURI_ACTION_SUCCESS = "COMECIMEventTrustedCertificatesCompleted";
    public static final String COM_ECIM_NODECREDENTIAL_CANCELENROLLMENT_ACTION_FAILED = "ComEcimCancelEnrollmentTaskFailed";
    public static final String COM_ECIM_NODECREDENTIAL_CANCELENROLLMENT_ACTION_SUCCESS = "ATTenrollmentProgressChange";

    public static final String COM_ECIM_ISSUE_CERTM_INSTALLTRUSTFROMURI_ACTION_FAILED = "COMECIMIssueAlarmTrustedCertificateInstallationFault";
    public static final String COM_ECIM_ISSUE_CERTM_INSTALLTRUSTFROMURI_ACTION_SUCCESS = "COMECIMIssueEventTrustedCertificatesCompleted";

    // TODO Temporarily used CPP events...
    public static final String COM_ECIM_NODECREDENTIAL_START_ONLINE_ENROLLMENT_ACTION_FAILED = "CPPAlarm167CredentialsEnrollmentFault";
    public static final String COM_ECIM_NODECREDENTIAL_START_ONLINE_ENROLLMENT_ACTION_SUCCESS = "CPPEvent27NodeCredentialsInstalled";

    public static final String COM_ECIM_DOWNLOAD_CRL_ACTION_FAILED = "Failed";
    public static final String COM_ECIM_DOWNLOAD_CRL_ACTION_SUCCESS = "Success";

    public static final String CPP_COMMAND_HTTPS_SUCCESS = "CPPCommandHttpsSuccess";
    public static final String CPP_COMMAND_HTTPS_FAIL = "CPPCommandHttpsFail";
    
    public static final String CPP_NODE_HTTPS = "CppCommandHttps";
    public static final String CPP_NODE_HTTP = "CppCommandHttp";
    
    public static final String WEB_SERVER_UNSECURE_REGEX = "WebServer.*unsecure";

    public static final String WEB_SERVER_PROPERTY = "WebServer";

    public static final String TEST_ACTION_FAILED = "SomethingFailed";
    public static final String TEST_ACTION_SUCCESS = "SomethingSuccessful";
}
