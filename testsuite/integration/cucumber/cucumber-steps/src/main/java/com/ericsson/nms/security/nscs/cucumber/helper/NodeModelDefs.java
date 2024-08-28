/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.cucumber.helper;

/**
 *
 * @author epaocas
 */
public class NodeModelDefs {

    // Normalized Namespaces
    public static final String NE_NS = "OSS_NE_DEF";
    public static final String NE_SEC_NS = "OSS_NE_SEC_DEF";
    public static final String NE_CM_NS = "OSS_NE_CM_DEF";
    public static final String CPP_MED_NS = "CPP_MED";
    public static final String COM_MED_NS = "COM_MED";
    public static final String HTTP_MED_NS = "HTTP_MED";

    // Mirror Namespaces
    public static final String OSS_TOP_NS = "OSS_TOP";

    // CPP Namespaces
    public static final String ERBS_MODEL_NS = "ERBS_NODE_MODEL";
    public static final String RNC_MODEL_NS = "RNC_NODE_MODEL";
    public static final String RBS_MODEL_NS = "RBS_NODE_MODEL";
    public static final String MGW_MODEL_NS = "MGW_NODE_MODEL";

    // COM/ECIM Namespaces
    public static final String SGSN_MME_TOP_NS = "SgsnMmeTop";
    public static final String COM_TOP_NS = "ComTop";
    public static final String SGSN_MME_SEC_M_NS = "SgsnMmeSecurityManagement";
    public static final String COM_SEC_M_NS = "ComSecM";
    public static final String SGSN_MME_CERT_M_NS = "SgsnMmeCertM";
    public static final String COM_CERT_M_NS = "RcsCertM";
    public static final String RCS_LDAP_AUTH = "RcsLdapAuthentication";
    public static final String SGSN_MME_SYS_M_NS = "SgsnMmeSysM";
    public static final String COM_SYS_M_NS = "ComSysM";
    public static final String COM_IKEV2_POLICY_PROFILE_NS = "RtnIkev2PolicyProfile";
    public static final String COM_OAM_ACCESS_POINT_NS = "RcsOamAccessPoint";
    // COM/ECIM Types
    public static final String SEC_M_TYPE = "SecM";
    public static final String CERT_M_TYPE = "CertM";
    public static final String NODE_CREDENTIAL_TYPE = "NodeCredential";
    public static final String ENROLLMENT_AUTHORITY_TYPE = "EnrollmentAuthority";
    public static final String ENROLLMENT_SERVER_GROUP_TYPE = "EnrollmentServerGroup";
    public static final String ENROLLMENT_SERVER_TYPE = "EnrollmentServer";
    public static final String TRUST_CATEGORY_TYPE = "TrustCategory";
    public static final String SYS_M_TYPE = "SysM";
    public static final String NETCONF_TLS_TYPE = "NetconfTls";
    public static final String IKEV2_POLICY_PROFILE_TYPE = "Ikev2PolicyProfile";
    public static final String TRANSPORT_TYPE = "Transport";

    // MO Types
    public static final String TLS_TYPE = "Tls";
    public static final String SSH_TYPE = "Ssh";

    public static final String NE_TYPE = "neType";
    public static final String PLATFORM_TYPE = "platformType";
    public static final String NETWORK_ELEMENT_ID = "networkElementId";
    public static final String OSS_MODEL_IDENTITY = "ossModelIdentity";

    public static final String SECURITY_FUNCTION_ID = "securityFunctionId";
    public static final String IPADDRESS = "ipAddress";

    public static final String HTTPS = "HTTPS";

    public static final String ROOT_USER_NAME = "rootUserName";
    public static final String ROOT_USER_PASSWORD = "rootUserPassword";
    public static final String SECURE_USER_PASSWORD = "secureUserPassword";
    public static final String SECURE_USER_NAME = "secureUserName";
    public static final String NWIEA_SECURE_USER_NAME = "nwieaSecureUserName";
    public static final String NWIEA_SECURE_USER_PASSWORD = "nwieaSecureUserPassword";
    public static final String NWIEB_SECURE_USER_NAME = "nwiebSecureUserName";
    public static final String NWIEB_SECURE_USER_PASSWORD = "nwiebSecureUserPassword";
    public static final String NORMAL_USER_PASSWORD = "normalUserPassword";
    public static final String NORMAL_USER_NAME = "normalUserName";
    public static final String LDAP_APPLICATION_USER_PASSWORD = "ldapApplicationUserPassword";
    public static final String LDAP_APPLICATION_USER_NAME = "ldapApplicationUserName";
    public static final String NETWORK_ELEMENT_SECURITY_ID = "NetworkElementSecurityId";
    public static final String TARGET_GROUPS = "targetGroups";

    public static final String ALGORITHM_AND_KEY_SIZE = "algorithmAndKeySize";
    public static final String ENM_SSH_PUBLIC_KEY = "enmSshPublicKey";
    public static final String ENM_SSH_PRIVATE_KEY = "enmSshPrivateKey";

    public static final String ENROLLMENT_MODE = "enrollmentMode";

    public static final String AUTH_PROTOCOL = "snmpAuthProtocol";
    public static final String AUTH_KEY = "snmpAuthKey";
    public static final String PRIV_PROTOCOL = "snmpPrivProtocol";
    public static final String PRIV_KEY = "snmpPrivKey";
    public static final String AUTH_PASSWD = "snmpAuthPassword";
    public static final String PRIV_PASSWD = "snmpPrivPassword";

    public static final String SUMMARY_FILE_HASH = "summaryFileHash";

    public static final String SYNC_STATUS = "syncStatus";
    public static final String LOST_SYNCHRONIZATION = "lostSynchronization";

    public static enum SyncStatusValue {

        SYNCHRONIZED, UNSYNCHRONIZED, TOPOLOGY, ATTRIBUTE, PENDING;
    }
    public static final String MIM_INFO = "mimInfo";

    public static final String IP_OAM_ID = "IpOamId";

    public static final String IPACCESS_HOSTET_REF1 = "ipAccessHostEtRef1";
    public static final String IP_INTERFACE_MO_REF = "ipInterfaceMoRef";
    public static final String IP_ACCESS_HOST_ET_ID = "IpAccessHostEtId";

    public static final String SCTP_REF = "sctpRef";
    public static final String UP_IPACCESS_HOST_REF = "upIpAccessHostRef";
    public static final String LOGON_SERVER_ADDRESS = "logonServerAddress";

    public static final String IPACCESS_SCTP_REF = "ipAccessSctpRef";
    public static final String IP_HOST_LINK_ID = "IpHostLinkId";

    public static final String VPN_INTERFACE_ID = "VpnInterfaceId";
    public static final String IP_ACCESS_HOST_ET_REF = "ipAccessHostEtRef";
    public static final String CHANGE_IP_FOR_OAM_SETTING = "changeIpForOamSetting";

    public static final String TRUSTED_CERT_INST_STATE = "trustedCertInstallState";
    public static final String CERT_ENROLL_STATE = "certEnrollState";
    public static final String CERTIFICATE = "certificate";
    public static final String INSTALLED_TRUSTED_CERTIFICATES = "installedTrustedCertificates";
    public static final String FEATURE_STATE = "featureState";
    public static final String LICENSE_STATE = "licenseState";
    public static final String CERT_ENROLL_ERROR_MSG = "certEnrollErrorMsg";
    public static final String TRUSTED_CERT_INST_ERROR_MSG = "trustedCertInstallErrorMsg";

    // Actions without parameters
    public static final String CANCEL_CERT_ENROLLMENT = "cancelCertEnrollment";
    public static final String CANCEL_INSTALL_TRUSTED_CERTIFICATES = "cancelInstallTrustedCertificates";

    // Actions with parameters
    public static final String INIT_CERT_ENROLLMENT = "initCertEnrollment";
    public static final String INSTALL_TRUSTED_CERTIFICATES = "installTrustedCertificates";
    public static final String REMOVE_TRUSTED_CERTIFICATES = "removeTrustedCert";

    /**
     * IpSecCertEnrollState for IpSec
     */
    public static enum IpSecCertEnrollStateValue {

        IDLE, ONGOING, ERROR;
    }

    /**
     * Indicates the state of the installation process for the trusted
     * certificates
     */
    public static enum IpSecTrustedCertInstallStateValue {

        IDLE, ONGOING, ERROR;
    }

    /**
     * Possible states set by operator for licensed feature.
     */
    public static enum ActivationVals {

        ACTIVATED, DEACTIVATED;
    }

    /**
     * Possible states of the license for licensed feature. For details use link
     * : http://cpistore.internal.ericsson.com/alexserv?ID=29840&fn=15554
     * -AOM901110_S11V1-V1Uen.T.488.html
     */
    public enum StateVals {

        ENABLED, DISABLE;
    }

    public static final String FINGERPRINT = "fingerprint";
    public static final String ISSUER = "issuer";
    public static final String NOT_VALID_AFTER = "notValidAfter";
    public static final String NOT_VALID_BEFORE = "notValidBefore";
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String SUBJECT = "subject";
    public static final String SUBJECT_ALT_NAME = "subjectAltName";
    public static final String OPERATIONAL_SECURITY_LEVEL = "operationalSecurityLevel";
    public static final String FILE_TRANSFER_CLIENT_MODE = "fileTransferClientMode";
    public static final String TRUSTED_CERTIFICATE_INSTALLATION_FAILURE = "trustedCertificateInstallationFailure";
    public static final String NODE_CERTIFICATE = "nodeCertificate";
    public static final String CERT_REV_STATUS_CHECK = "certRevStatusCheck";
    public static final String CRL_ON_DEMAND_UPDATE_INTERVAL = "crlEarlyUpdateInterval";
    public static final String WEBSERVER = "webServer";

    // Actions without parameters
    public static final String ADAPT_SECURITY_LEVEL = "adaptSecurityLevel";
    public static final String CONFIRM_NEW_CREDS = "confirmNewCreds";

    // Actions with parameters
    public static enum FileTransferClientModeValue {

        FTP, SFTP;
    }

    public static enum CertEnrollStateValue {

        IDLE, PREPARING_REQUEST, POLLING, NEW_CREDS_AWAIT_CONF, ERROR;
    }

    public static final String CATEGORY = "category";

    public static final String CONN_ATTEMPT_TIME_OUT = "connAttemptTimeOut";
    public static final String EXT_SERVER_APP_NAME = "extServerAppName";
    public static final String EXT_SERVER_LIST_CONFIG = "extServerListConfig";
    public static final String EXT_SERVER_LIST_INFO = "extServerListInfo";
    public static final String EXT_SERVER_LOG_LEVEL = "extServerLogLevel";
    public static final String REAL_TIME_SEC_LOG_ID = "RealTimeSecLogId";
    public static final String STATUS = "status";
    public static final String USER_LABEL = "userLabel";

    // Actions with parameters
    public static final String ADD_EXTERNAL_SERVER = "addExternalServer";
    public static final String DELETE_EXTERNAL_SERVER = "deleteExternalServer";

    public static final String EXT_SERVER_ADRESS = "extServerAddress";
    public static final String EXT_SERVER_PROTOCOL = "extServProtocol";
    public static final String SERVER_NAME = "serverName";
    public static final String MANAGED_ELEMENT_ID = "managedElementId";

    public static final String SYSTEM_FUNCTIONS_ID = "systemFunctionsId";

    public static final String SEC_M_ID = "secMId";

    public static final String SUPPORTED_MACS = "supportedMacs";
    public static final String SUPPORTED_KEY_EXCHANGES = "supportedKeyExchanges";
    public static final String SUPPORTED_CIPHERS = "supportedCiphers";
    public static final String SSH_ID = "sshId";
    public static final String SELECTED_MACS = "selectedMacs";
    public static final String SELECTED_KEY_EXCHANGES = "selectedKeyExchanges";
    public static final String SELECTED_CIPHERS = "selectedCiphers";

    public static final String TLS_ID = "tlsId";
    public static final String ENABLED_CIPHERS = "enabledCiphers";
    public static final String CIPHER_FILTER = "cipherFilter";

    public static final String SUPPORTED_MAC = "supportedMac";
    public static final String SUPPORTED_KEY_EXCHANGE = "supportedKeyExchange";
    public static final String SUPPORTED_CIPHER = "supportedCipher";
    public static final String SELECTED_MAC = "selectedMac";
    public static final String SELECTED_KEY_EXCHANGE = "selectedKeyExchange";
    public static final String SELECTED_CIPHER = "selectedCipher";

    public static final String ENABLED_CIPHER = "enabledCipher";
    public static final String USER_MANAGEMENT_ID = "userManagementId";
    public static final String TARGET_TYPE = "targetType";

    public static final String LDAP_AUTHENTICATION_METHOD_ID = "ldapAuthenticationMethodId";
    public static final String LDAP_ID = "ldapId";
    public static final String USE_TLS = "useTls";
    public static final String TLS_MODE = "tlsMode";
    public static final String SERVER_PORT = "serverPort";
    public static final String LDAP_IP_ADDRESS = "ldapIpAddress";
    public static final String FALLBACK_LDAP_IP_ADDRESS = "fallbackLdapIpAddress";
    public static final String BIND_DN = "bindDn";
    public static final String BIND_PASSWORD = "bindPassword";
    public static final String BASE_DN = "baseDn";
    public static final String TRUST_CATEGORY = "trustCategory";
    public static final String NODE_CREDENTIAL = "nodeCredential";

    public static final String CERT_M_ID = "certMId";
    public static final String LOCAL_FILE_STORE_PATH = "localFileStorePath";
    public static final String REPORT_PROGRESS = "reportProgress";

    // Actions without parameters
    public static final String CANCEL = "cancel";
    public static final String DOWNLOAD_CRL = "downloadCrl";

    // Actions with parameters
    public static final String INSTALL_TRUSTED_CERT_FROM_URI = "installTrustedCertFromUri";
    public static final String INSTALL_TRUSTED_CERT_FROM_URI_FINGERPRINT = "fingerprint";
    public static final String INSTALL_TRUSTED_CERT_FROM_URI_URI = "uri";
    public static final String INSTALL_TRUSTED_CERT_FROM_URI_URI_PASSWORD = "uriPassword";

    public static final String REMOVE_TRUSTED_CERT = "removeTrustedCert";
    public static final String REMOVE_TRUSTED_CERT_TRUSTED_CERT = "trustedCert";

    public static final String CERTIFICATE_CONTENT = "certificateContent";
    public static final String CERTIFICATE_STATE = "certificateState";

    public static enum CertificateState {

        EXPIRED("2"), NOT_VALID_YET("1"), REVOKED("3"), VALID("0");

        private final String value;

        private CertificateState(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
    public static final String VENDOR_CREDENTIAL_ID = "vendorCredentialId";

    public static final String AUTHORITY_TYPE = "authorityType";
    public static final String ENROLLMENT_AUTHORITY_ID = "enrollmentAuthorityId";
    public static final String ENROLLMENT_AUTHORITY_NAME = "enrollmentAuthorityName";
    public static final String ENROLLMENT_CA_CERTIFICATE = "enrollmentCaCertificate";
    public static final String ENROLLMENT_CA_FINGERPRINT = "enrollmentCaFingerprint";

    public static enum AuthorityType {

        CERTIFICATION_AUTHORITY("0"), REGISTRATION_AUTHORITY("1");

        private final String value;

        private AuthorityType(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static final String ENROLLMENT_AUTHORITY = "enrollmentAuthority";
    public static final String ENROLLMENT_PROGRESS = "enrollmentProgress";
    public static final String ENROLLMENT_SERVER_GROUP = "enrollmentServerGroup";
    public static final String ENROLLMENT_TIMER = "enrollmentTimer";
    public static final String EXPIRY_ALARM_THRESHOLD = "expiryAlarmThreshold";
    public static final String KEY_INFO = "keyInfo";
    public static final String NODE_CREDENTIAL_ID = "nodeCredentialId";
    public static final String RENEWAL_MODE = "renewalMode";
    public static final String RESERVED_BY_USER = "reservedByUser";
    public static final String SUBJECT_NAME = "subjectName";
    public static final String CANCEL_ENROLLMENT = "cancelEnrollment";

    // Actions with parameters
    public static final String INSTALL_CREDENTIAL_FROM_URI = "installCredentialFromUri";
    public static final String INSTALL_CREDENTIAL_FROM_URI_CREDENTIAL_PASSWORD = "credentialPassword";
    public static final String INSTALL_CREDENTIAL_FROM_URI_FINGERPRINT = "fingerprint";
    public static final String INSTALL_CREDENTIAL_FROM_URI_URI = "uri";
    public static final String INSTALL_CREDENTIAL_FROM_URI_URI_PASSWORD = "uriPassword";

    public static final String START_OFFLINE_CSR_ENROLLMENT = "startOfflineCsrEnrollment";
    public static final String START_OFFLINE_CSR_ENROLLMENT_URI = "uri";
    public static final String START_OFFLINE_CSR_ENROLLMENT_URI_PASSWORD = "uriPassword";

    public static final String START_ONLINE_ENROLLMENT = "startOnlineEnrollment";
    public static final String START_ONLINE_ENROLLMENT_CHALLENGE_PASSWORD = "challengePassword";

    public static enum KeyInfo {

        ECDSA_160("4"), ECDSA_224("5"), ECDSA_256("6"), ECDSA_384("7"), ECDSA_512("8"), RSA_1024("0"), RSA_2048(
                "1"), RSA_3072("2"), RSA_4096("3");

        private final String value;

        private KeyInfo(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    public static enum RenewalMode {

        AUTOMATIC("1"), MANUAL("0");

        private final String value;

        private RenewalMode(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static final String TRUST_CATEGORY_ID = "trustCategoryId";
    public static final String TRUSTED_CERTIFICATES = "trustedCertificates";
    public static final String CRL_CHECK = "crlCheck";
    public static final String MANAGED_STATE = "managedState";
    public static final String RESERVED_BY_CATEGORY = "reservedByCategory";
    public static final String TRUSTED_CERTIFICATE_ID = "trustedCertificateId";

    public static enum ManagedCertificateState {

        DISABLED("1"), ENABLED("0");

        private final String value;

        private ManagedCertificateState(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static final String CERT_M_CAPABILITIES_ID = "certMCapabilitiesId";
    public static final String ENROLLMENT_SUPPORT = "enrollmentSupport";
    public static final String FINGERPRINT_SUPPORT = "fingerprintSupport";

    public static enum EnrollmentSupport {

        OFFLINE_CSR("0"), OFFLINE_PKCS12("1"), ONLINE_CMP("3"), ONLINE_SCEP("2");

        private final String value;

        private EnrollmentSupport(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    public static enum FingerprintSupport {

        SHA_1("0"), SHA_224("1"), SHA_256("2"), SHA_384("3"), SHA_512("4");

        private final String value;

        private FingerprintSupport(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static final String ENROLLMENT_SERVER_ID = "enrollmentServerId";
    public static final String PROTOCOL = "protocol";
    public static final String URI = "uri";

    public static enum EnrollmentProtocol {

        CMP("1"), SCEP("0");

        private final String value;

        private EnrollmentProtocol(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    public static final String NETCONF_TLS_ID = "netconfTlsId";
    public static final String ADMINISTRATIVE_STATE = "administrativeState";

    public static enum BasicAdmState {

        LOCKED, UNLOCKED;
    }

    public static final String OAM_ACCESS_POINT_ID = "oamAccessPointId";
    public static final String IPV4ADDRESS = "ipv4address";
    public static final String TRANSPORT_ID = "transportId";

    public static final String IKEV2_POLICY_PROFILE_ID = "ikev2PolicyProfileId";
    public static final String IKEV2_PROPOSAL = "ikev2Proposal";
    public static final String CREDENTIAL = "credential";

    public static final String ACTION_ID = "actionId";
    public static final String SYS_M_ID = "sysMId";

}
