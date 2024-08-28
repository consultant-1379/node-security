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

package com.ericsson.nms.security.nscs.data;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean.KeyLength;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;

/**
 * Central repository for Model's related constants like MO types, namespaces and attributes.
 * <p>
 * It is recommended (not mandatory) no chain each MO definition in a hierarchical structure mirroring the actual Model definition in DPS. This way it
 * is possible to take advantage of the auxiliary method withNames(...).fdn() to generate a complete fdn of the element.
 * </p>
 * <p>
 * To create a new ModelDefinition :
 * <ol>
 * <li>Create a static subclass of Mo. E.g.: <code><pre>
 *                  public static class Security extends Mo {
 *                      // Most of the time you'll want a constructor that receives and pass the parent element
 *                      // and the namespace:
 *                      Security(Mo parent, String namespace){super(parent, "Security", namespace);}
 *
 *                      // add as many as needed constants representing attributes
 *                      public final String OPERATIONAL_SECURITY_LEVEL = "operationalSecurityLevel";
 *                 }
 *              </pre></code></li>
 * <li>Add and instance of the newly created Definition into the correspondent parent E.g.: <code><pre>
 *                  public static class SystemFunctions extends Mo {
 *                     // ...
 *
 *                     public final Security security = new Security(this, this.namespace());
 *
 *                     // ...
 *                  }
 *              </pre></code></li>
 * </ol>
 * </p>
 *
 * @see com.ericsson.nms.security.nscs.data.Model
 * @author emaynes
 */
public class ModelDefinition {

    // Normalized Namespaces
    public static final String NE_NS = "OSS_NE_DEF";
    public static final String NE_SEC_NS = "OSS_NE_SEC_DEF";
    public static final String NE_CM_NS = "OSS_NE_CM_DEF";
    public static final String NE_FM_NS = "OSS_NE_FM_DEF";
    public static final String CPP_MED_NS = "CPP_MED";
    public static final String COM_MED_NS = "COM_MED";
    public static final String HTTP_MED_NS = "HTTP_MED";
    public static final String ECEE_MED_NS = "ECEE_MED";
    public static final String CCD_MED_NS = "CCD_MED";

    // Mirror Namespaces
    public static final String OSS_TOP_NS = "OSS_TOP";

    // CPP Namespaces
    public static final String ERBS_MODEL_NS = "ERBS_NODE_MODEL";
    public static final String RNC_MODEL_NS = "RNC_NODE_MODEL";
    public static final String RBS_MODEL_NS = "RBS_NODE_MODEL";
    public static final String MGW_MODEL_NS = "MGW_NODE_MODEL";
    public static final String MRS_MODEL_NS = "MRS_NODE_MODEL";

    //CPP Types
    public static final String TIME_SETTING = "TimeSetting";
    public static final String NTP_SERVER = "NtpServer";

    // COM/ECIM Namespaces
    public static final String SGSN_MME_TOP_NS = "SgsnMmeTop";
    public static final String COM_TOP_NS = "ComTop";
    public static final String SGSN_MME_SEC_M_NS = "SgsnMmeSecurityManagement";
    public static final String COM_SEC_M_NS = "ComSecM";
    public static final String SGSN_MME_CERT_M_NS = "SgsnMmeCertM";
    public static final String COM_CERT_M_NS = "RcsCertM";
    public static final String RCS_LDAP_AUTH_NS = "RcsLdapAuthentication";
    public static final String SGSN_MME_SYS_M_NS = "SgsnMmeSysM";
    public static final String COM_SYS_M_NS = "ComSysM";
    public static final String COM_IKEV2_POLICY_PROFILE_NS = "RtnIkev2PolicyProfile";
    public static final String COM_OAM_ACCESS_POINT_NS = "RcsOamAccessPoint";
    public static final String COM_FILE_TPM_NS = "RcsFileTPM";
    public static final String COM_TIME_M_NS = "RcsTIMEM";
    public static final String REF_MIM_NS_ECIM_CERTM = "ECIM_CertM";
    public static final String REF_MIM_NS_ECIM_TIMEM = "ECIM_TimeM";
    public static final String REF_MIM_NS_ECIM_LDAP_AUTHENTICATION = "ECIM_LDAP_Authentication";
    // COM/ECIM Types
    public static final String SYSTEM_FUNCTIONS_TYPE = "SystemFunctions";
    public static final String SEC_M_TYPE = "SecM";
    public static final String USER_MANAGEMENT_TYPE = "UserManagement";
    public static final String LDAP_AUTHENTICATION_METHOD_TYPE = "LdapAuthenticationMethod";
    public static final String CERT_M_TYPE = "CertM";
    public static final String NODE_CREDENTIAL_TYPE = "NodeCredential";
    public static final String ENROLLMENT_AUTHORITY_TYPE = "EnrollmentAuthority";
    public static final String ENROLLMENT_SERVER_GROUP_TYPE = "EnrollmentServerGroup";
    public static final String ENROLLMENT_SERVER_TYPE = "EnrollmentServer";
    public static final String TRUST_CATEGORY_TYPE = "TrustCategory";
    public static final String TRUSTED_CERTIFICATE_TYPE = "TrustedCertificate";
    public static final String SYS_M_TYPE = "SysM";
    public static final String NETCONF_TLS_TYPE = "NetconfTls";
    public static final String IKEV2_POLICY_PROFILE_TYPE = "Ikev2PolicyProfile";
    public static final String TRANSPORT_TYPE = "Transport";
    public static final String FTP_TLS_SERVER_TYPE = "FtpTlsServer";
    public static final String FTP_TLS_TYPE = "FtpTls";
    public static final String FILE_TPM_TYPE = "FileTPM";
    public static final String TIME_M = "TimeM";
    public static final String NTP = "Ntp";
    public static final String NTP_SECURITY_POLICY = "NtpSecurityPolicy";
    public static final String ECIM_LDAP_TYPE = "Ldap";

    // MO Types
    public static final String SUB_NETWORK_TYPE = "SubNetwork";
    public static final String ME_CONTEXT_TYPE = "MeContext";
    public static final String NETWORK_ELEMENT_TYPE = "NetworkElement";
    public static final String MANAGED_ELEMENT_TYPE = "ManagedElement";
    public static final String VIRTUAL_NETWORK_FUNCTION_MANAGER_TYPE = "VirtualNetworkFunctionManager";
    public static final String NETWORK_FUNCTION_VIRTUALIZATION_ORCHESTRATOR_TYPE = "NetworkFunctionVirtualizationOrchestrator";
    public static final String VIRTUAL_INFRASTRUCTURE_MANAGER_TYPE = "VirtualInfrastructureManager";
    public static final String CLOUD_INFRASTRUCTURE_MANAGER_TYPE = "CloudInfrastructureManager";
    public static final String MANAGEMENT_SYSTEM_TYPE = "ManagementSystem";
    public static final String TLS_TYPE = "Tls";
    public static final String SSH_TYPE = "Ssh";

    //CBPOI Namespaces
    public static final String CBP_OI_SYSTEM_NS = "urn:ietf:params:xml:ns:yang:ietf-system";
    public static final String CBP_OI_SYSTEM_EXT_NS = "urn:rdns:com:ericsson:oammodel:ericsson-system-ext";
    public static final String VDU_TOP_NS = "VduTop";
    public static final String VCU_CP_TOP_NS = "VcuCpTop";
    public static final String VCU_UP_TOP_NS = "VcuUpTop";
    public static final String CBP_OI_TRUSTSTORE_NS = "urn:ietf:params:xml:ns:yang:ietf-truststore";
    public static final String CBP_OI_KEYSTORE_NS = "urn:ietf:params:xml:ns:yang:ietf-keystore";
    public static final String CBP_OI_KEYSTORE_EXT_NS = "urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext";

    // Unscoped CBPOI MO Types
    public static final String SYSTEM_TYPE = "system";
    // For both system,ldap MO and system,security,server,tcp,ldap
    public static final String LDAP_TYPE = "ldap";
    public static final String CBP_OI_SECURITY_TYPE = "security";
    public static final String CBP_OI_TLS_TYPE = "tls";
    public static final String SIMPLE_AUTHENTICATED_TYPE = "simple-authenticated";
    public static final String SERVER_TYPE = "server";
    public static final String TCP_TYPE = "tcp";
    public static final String LDAPS_TYPE = "ldaps";
    public static final String KEYSTORE_TYPE = "keystore";
    // For system,authentication
    public static final String AUTHENTICATION_TYPE = "authentication";
    public static final String USER_TYPE = "user";
    public static final String AUTHORIZED_KEY_TYPE = "authorized-key";
    // For keystore,cmp MO and keystore,asymmetric-keys,cmp MO and keystore,asymmetric-keys,asymmetric-key,cmp MO
    public static final String CMP_TYPE = "cmp";
    public static final String CERTIFICATE_AUTHORITIES_TYPE = "certificate-authorities";
    public static final String CERTIFICATE_AUTHORITY_TYPE = "certificate-authority";
    public static final String CMP_SERVER_GROUPS_TYPE = "cmp-server-groups";
    public static final String CMP_SERVER_GROUP_TYPE = "cmp-server-group";
    public static final String CMP_SERVER_TYPE = "cmp-server";
    public static final String TRUSTSTORE_TYPE = "truststore";
    public static final String TRUSTSTORE_CERTIFICATES_TYPE = "certificates";
    public static final String TRUSTSTORE_CERTIFICATE_TYPE = "certificate";
    public static final String ASYMMETRIC_KEYS_TYPE = "asymmetric-keys";
    public static final String ASYMMETRIC_KEY_TYPE = "asymmetric-key";
    public static final String KEYSTORE_CERTIFICATES_TYPE = "certificates";
    public static final String KEYSTORE_CERTIFICATE_TYPE = "certificate";

    // CBPOI MO attributes
    public static final String SECURITY_USER_BASE_DN_ATTR = "user-base-dn";
    public static final String SIMPLE_AUTHENTICATED_BIND_DN_ATTR = "bind-dn";
    public static final String SIMPLE_AUTHENTICATED_BIND_CRD_ATTR = "bind-password";
    public static final String LDAP_SERVER_NAME_ATTR = "name";
    public static final String TCP_ADDRESS_ATTR = "address";
    public static final String LDAPS_PORT_ATTR = "port";
    public static final String TCP_LDAP_PORT_ATTR = "port";
    public static final String CERTIFICATE_AUTHORITY_NAME_ATTR = "name";
    public static final String CMP_SERVER_CA_CERTS_ATTR = "ca-certs";
    public static final String CMP_SERVER_CERTIFICATE_AUTHORITY_ATTR = "certificate-authority";
    public static final String CMP_SERVER_PRIORITY_ATTR = "priority";
    public static final String CMP_SERVER_URI_ATTR = "uri";
    public static final String ASYMMETRIC_KEY_CMP_CMP_SERVER_GROUP_ATTR = "cmp-server-group";
    public static final String ASYMMETRIC_KEY_CMP_TRUSTED_CERTS_ATTR = "trusted-certs";
    public static final String ASYMMETRIC_KEY_CMP_RENEWAL_MODE_ATTR = "renewal-mode";
    public static final String ASYMMETRIC_KEY_CMP_RENEWAL_MODE_MANUAL = "manual";
    public static final String ASYMMETRIC_KEY_CMP_RENEWAL_MODE_AUTOMATIC = "automatic";
    public static final String KEYSTORE_CERTIFICATE_NAME_ATTR = "name";
    public static final String KEYSTORE_CERTIFICATE_CERT_ATTR = "cert";
    public static final String TRUSTSTORE_CERTIFICATES_DESCRIPTION_ATTR = "description";
    public static final String TRUSTSTORE_CERTIFICATE_NAME_ATTR = "name";
    public static final String TRUSTSTORE_CERTIFICATE_CERT_ATTR = "cert";
    public static final String USER_GROUP_ID_ATTR = "group-id";
    public static final String USER_USER_ID_ATTR = "user-id";
    public static final String USER_DEFAULT_SHELL_ATTR = "default-shell";
    public static final String USER_HOME_DIRECTORY_ATTR = "home-directory";
    public static final String AUTHORIZED_KEY_NAME_ATTR = "name";
    public static final String AUTHORIZED_KEY_ALGORITHM_ATTR = "algorithm";
    public static final String AUTHORIZED_KEY_COMMENT_ATTR = "comment";
    public static final String AUTHORIZED_KEY_KEY_DATA_ATTR = "key-data";

    // CBPOI MO actions
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP = "start-cmp";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_ALGORITHM = "algorithm";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_CERTIFICATE_NAME = "certificate-name";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_CMP_SERVER_GROUP = "cmp-server-group";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_NAME = "name";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_CHALLENGE = "password";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_SUBJECT = "subject";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_SUBJECT_ALT_NAMES = "subject-alternative-names";
    public static final String ASYMMETRIC_KEYS_CMP_START_CMP_TRUSTED_CERTS = "trusted-certs";
    public static final String ASYMMETRIC_KEY_CMP_RENEW_CMP = "renew-cmp";
    public static final String ASYMMETRIC_KEY_CMP_RENEW_CMP_ALGORITHM = "algorithm";
    public static final String ASYMMETRIC_KEY_CMP_RENEW_CMP_SUBJECT_ALT_NAMES = "subject-alternative-names";
    public static final String CERTIFICATES_INSTALL_CERTIFICATE_PEM = "install-certificate-pem";
    public static final String CERTIFICATES_INSTALL_CERTIFICATE_PEM_NAME = "name";
    public static final String CERTIFICATES_INSTALL_CERTIFICATE_PEM_PEM = "pem";

    // Normalized attributes
    public static final String IP_ADDRESS = "ipAddress";

    // Association endpoints
    public static final String NETWORK_ELEMENT_REF_ENDPOINT = "networkElementRef";
    public static final String NODE_ROOT_REF_ENDPOINT = "nodeRootRef";

    public static abstract class NormalizedRootMO extends Mo {
        NormalizedRootMO(final String type, final String ns) {
            super(type, ns);
        }

        public SecurityFunction securityFunction = null;
    }

    public static class VirtualNetworkFunctionManager extends NormalizedRootMO {
        VirtualNetworkFunctionManager() {
            super(VIRTUAL_NETWORK_FUNCTION_MANAGER_TYPE, NE_NS);
            super.securityFunction = new SecurityFunction(this);
        }

        public final HttpConnectivityInformation httpConnectivityInformation = new HttpConnectivityInformation(this, HTTP_MED_NS);

        public static final String VM_TYPE = "vmType";
    }

    public static class VirtualInfrastructureManager extends NormalizedRootMO {
        VirtualInfrastructureManager() {
            super(VIRTUAL_INFRASTRUCTURE_MANAGER_TYPE, NE_NS);
            super.securityFunction = new SecurityFunction(this);
        }

        public final EceeConnectivityInformation eceeConnectivityInformation = new EceeConnectivityInformation(this, ECEE_MED_NS);

        public static final String VIM_TYPE = "vimType";
    }


    public static class CloudInfrastructureManager extends NormalizedRootMO {
        CloudInfrastructureManager() {
            super(CLOUD_INFRASTRUCTURE_MANAGER_TYPE, NE_NS);
            super.securityFunction = new SecurityFunction(this);
        }

        public final CcdConnectivityInformation ccdConnectivityInformation = new CcdConnectivityInformation(this, CCD_MED_NS);

        public static final String CIM_TYPE = "cimType";
    }

    public static class NetworkFunctionVirtualizationOrchestrator extends NormalizedRootMO {
        NetworkFunctionVirtualizationOrchestrator() {
            super(NETWORK_FUNCTION_VIRTUALIZATION_ORCHESTRATOR_TYPE, NE_NS);
            super.securityFunction = new SecurityFunction(this);
        }
    }


    public static class ManagementSystem extends NormalizedRootMO {
        ManagementSystem() {
            super(MANAGEMENT_SYSTEM_TYPE, NE_NS);
            super.securityFunction = new SecurityFunction(this);
        }

        public static final String MS_TYPE = "msType";
    }

    public static class NetworkElement extends NormalizedRootMO {
        NetworkElement() {
            super(NETWORK_ELEMENT_TYPE, NE_NS);
            super.securityFunction = new SecurityFunction(this);
        }

        public final CmFunction cmFunction = new CmFunction(this);
        public final FmFunction fmFunction = new FmFunction(this);
        public final ComConnectivityInformation comConnectivityInformation = new ComConnectivityInformation(this, COM_MED_NS);
        public final CppConnectivityInformation cppConnectivityInformation = new CppConnectivityInformation(this, CPP_MED_NS);

        public static final String NE_TYPE = "neType";
        public static final String PLATFORM_TYPE = "platformType";
        public static final String NETWORK_ELEMENT_ID = "networkElementId";
        public static final String OSS_MODEL_IDENTITY = "ossModelIdentity";

    }

    public static class SecurityFunction extends Mo {
        SecurityFunction(final Mo parent) {
            super(parent, "SecurityFunction", NE_SEC_NS);
        }

        public final NetworkElementSecurity networkElementSecurity = new NetworkElementSecurity(this);

        public static final String SECURITY_FUNCTION_ID = "securityFunctionId";
    }

    public static class HttpConnectivityInformation extends Mo {
        HttpConnectivityInformation(final Mo parent, final String namespace) {
            super(parent, "HttpConnectivityInformation", namespace);
        }

        public static final String IPADDRESS = "ipAddress";

    }

    public static class EceeConnectivityInformation extends Mo {
        EceeConnectivityInformation(final Mo parent, final String namespace) {
            super(parent, "EceeConnectivityInformation", namespace);
        }

        public static final String IPADDRESS = "ipAddress";

    }

     public static class CcdConnectivityInformation extends Mo {
        CcdConnectivityInformation(final Mo parent, final String namespace) {
            super(parent, "CcdConnectivityInformation", namespace);
        }

        public static final String IPADDRESS = "ipAddress";

    }

    public static class ComConnectivityInformation extends Mo {
        ComConnectivityInformation(final Mo parent, final String namespace) {
            super(parent, "ComConnectivityInformation", namespace);
        }

        public static final String IPADDRESS = "ipAddress";

        public static final String FILE_TRANSFER_PROTOCOL = "fileTransferProtocol";

        public enum BasicFileTransferProtocolType {
            SFTP, FTPES, FTP;
        }

        public static final String FTP_TLS_SERVER_PORT = "ftpTlsServerPort";
    }

    public static class CppConnectivityInformation extends Mo {
        CppConnectivityInformation(final Mo parent, final String namespace) {
            super(parent, "CppConnectivityInformation", namespace);
        }

        public static final String IPADDRESS = "ipAddress";

        public static final String HTTPS = "HTTPS";
    }

    public static class NetworkElementSecurity extends Mo {
        NetworkElementSecurity(final Mo parent) {
            super(parent, "NetworkElementSecurity", "OSS_NE_SEC_DEF");
        }

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
        public static final String NODECLI_USER_NAME = "nodeCliUserName";
        public static final String NODECLI_USER_PASSPHRASE = "nodeCliUserPassword";
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
        public static final String AUTH_ALGO = "authAlgorithm";
        public static final String PRIV_ALGO = "privAlgorithm";

        public static final String SUMMARY_FILE_HASH = "summaryFileHash";

        public static final String PROXY_ACCOUNT_DN = "proxyAccountDn";

    }

    public static class CmFunction extends Mo {
        CmFunction(final Mo parent) {
            super(parent, "CmFunction", NE_CM_NS);
        }

        // Attributes
        public static final String SYNC_STATUS = "syncStatus";
        public static final String LOST_SYNCHRONIZATION = "lostSynchronization";

        // Actions without parameters
        public static final String SYNC = "sync";

        public enum SyncStatusValue {
            SYNCHRONIZED, UNSYNCHRONIZED, TOPOLOGY, ATTRIBUTE, PENDING;
        }
    }

    public static class FmFunction extends Mo {
        FmFunction(final Mo parent) {
            super(parent, "FmFunction", NE_FM_NS);
        }

        // Attributes
        public static final String CURRENT_SERVICE_STATE = "currentServiceState";

        public enum AlarmStatusValue {
            IDLE, IN_SERVICE;
        }
    }

    public static class SubNetwork extends Mo {
        SubNetwork() {
            super(SUB_NETWORK_TYPE, OSS_TOP_NS);
        }
    }

    public static class MeContext extends Mo {
        MeContext() {
            super(ME_CONTEXT_TYPE, OSS_TOP_NS);
        }

        public final CppManagedElement managedElement = new CppManagedElement(this, ERBS_MODEL_NS);
        public final CppManagedElement rncManagedElement = new CppManagedElement(this, RNC_MODEL_NS);
        public final CppManagedElement rbsManagedElement = new CppManagedElement(this, RBS_MODEL_NS);
        public final CppManagedElement mgwManagedElement = new CppManagedElement(this, MGW_MODEL_NS);
        public final CppManagedElement mrsManagedElement = new CppManagedElement(this, MRS_MODEL_NS);
        public final ComEcimManagedElement sgsnMmeManagedElement = new ComEcimManagedElement(this, SGSN_MME_TOP_NS);
        public final ComEcimManagedElement comManagedElement = new ComEcimManagedElement(this, COM_TOP_NS);
    }

    // CPP section
    public static class CppManagedElement extends Mo {
        CppManagedElement(final String namespace) {
            super(MANAGED_ELEMENT_TYPE, namespace);
        }

        CppManagedElement(final Mo parent, final String namespace) {
            super(parent, MANAGED_ELEMENT_TYPE, namespace);
        }

        public final SystemFunctions systemFunctions = new SystemFunctions(this, this.namespace());
        public final IpSystem ipSystem = new IpSystem(this, this.namespace());
        public final IpOam ipOam = new IpOam(this, this.namespace());
        public final ENodeBFunction eNodeBFunction = new ENodeBFunction(this, this.namespace());
        public final TransportNetwork transportNetwork = new TransportNetwork(this, this.namespace());
        public final NodeManagementFunction nodeManagementFunction = new NodeManagementFunction(this, this.namespace());
        public final ManagedElementData managedElementData = new ManagedElementData(this, this.namespace());
        public static final String MIM_INFO = "mimInfo";

    }

    public static class SystemFunctions extends Mo {
        SystemFunctions(final Mo parent, final String namespace) {
            super(parent, SYSTEM_FUNCTIONS_TYPE, namespace);
        }

        public final Security security = new Security(this, this.namespace());

        public final Licensing licensing = RNC_MODEL_NS.equals(this.namespace())?new Licensing(this, this.namespace()):null;

        public final TimeSetting timeSetting = new TimeSetting(this, this.namespace());

    }

    public static class IpSystem extends Mo {
        IpSystem(final Mo parent, final String namespace) {
            super(parent, "IpSystem", namespace);
        }

        public final IpSec ipSec = new IpSec(this, this.namespace());
        public final IpAccessSctp ipAccessSctp = new IpAccessSctp(this, this.namespace());
        public final IpAccessHostEt ipAccessHostEt = new IpAccessHostEt(this, this.namespace());
        public final VpnInterface vpnInterface = new VpnInterface(this, this.namespace());
    }

    public static class IpOam extends Mo {
        IpOam(final Mo parent, final String namespace) {
            super(parent, "IpOam", namespace);
        }

        public final Ip ip = new Ip(this, this.namespace());
        public static final String IP_OAM_ID = "IpOamId";

    }

    public static class IpAccessSctp extends Mo {
        IpAccessSctp(final Mo parent, final String namespace) {
            super(parent, "IpAccessSctp", namespace);
        }

        public static final String IPACCESS_HOSTET_REF1 = "ipAccessHostEtRef1";
    }

    public static class IpAccessHostEt extends Mo {
        IpAccessHostEt(final Mo parent, final String namespace) {
            super(parent, "IpAccessHostEt", namespace);
        }

        public final IpSyncRef ipSyncRef = new IpSyncRef(this, this.namespace());
        public static final String IP_INTERFACE_MO_REF = "ipInterfaceMoRef";
        public static final String IP_ACCESS_HOST_ET_ID = "IpAccessHostEtId";

    }

    public static class IpSyncRef extends Mo {
        IpSyncRef(final Mo parent, final String namespace) {
            super(parent, "IpSyncRef", namespace);
        }
    }

    public static class ENodeBFunction extends Mo {
        ENodeBFunction(final Mo parent, final String namespace) {
            super(parent, "ENodeBFunction", namespace);
        }

        public final RbsConfiguration rbsConfiguration = new RbsConfiguration(this, this.namespace());
        public static final String SCTP_REF = "sctpRef";
        public static final String UP_IPACCESS_HOST_REF = "upIpAccessHostRef";
    }

    public static class TransportNetwork extends Mo {
        TransportNetwork(final Mo parent, final String namespace) {
            super(parent, "TransportNetwork", namespace);
        }

        public final Sctp sctp = new Sctp(this, this.namespace());
    }

    public static class NodeManagementFunction extends Mo {
        NodeManagementFunction(final Mo parent, final String namespace) {
            super(parent, "NodeManagementFunction", namespace);
        }

        public final RbsConfiguration rbsConfiguration = new RbsConfiguration(this, this.namespace());
    }

    public static class ManagedElementData extends Mo {

        ManagedElementData(final Mo parent, final String namespace) {
            super(parent, "ManagedElementData", namespace);
        }

        // Attributes
        public static final String LOGON_SERVER_ADDRESS = "logonServerAddress";

    }

    public static class Sctp extends Mo {
        Sctp(final Mo parent, final String namespace) {
            super(parent, "Sctp", namespace);
        }

        public static final String IPACCESS_SCTP_REF = "ipAccessSctpRef";
    }

    public static class Ip extends Mo {
        Ip(final Mo parent, final String namespace) {
            super(parent, "Ip", namespace);
        }

        public final IpHostLink ipHostLink = new IpHostLink(this, this.namespace());
    }

    public static class IpHostLink extends Mo {
        IpHostLink(final Mo parent, final String namespace) {
            super(parent, "IpHostLink", namespace);
        }

        public static final String IP_INTERFACE_MO_REF = "ipInterfaceMoRef";
        public static final String IP_HOST_LINK_ID = "IpHostLinkId";

    }

    public static class VpnInterface extends Mo {
        VpnInterface(final Mo parent, final String namespace) {
            super(parent, "VpnInterface", namespace);
        }

        public static final String VPN_INTERFACE_ID = "VpnInterfaceId";
        public static final String IP_ACCESS_HOST_ET_REF = "ipAccessHostEtRef";
    }

    public static class RbsConfiguration extends Mo {
        RbsConfiguration(final Mo parent, final String namespace) {
            super(parent, "RbsConfiguration", namespace);
        }

        // Actions with parameters
        public static final String CHANGE_IP_FOR_OAM_SETTING = "changeIpForOamSetting";

    }

    public static class IpSec extends Mo {

        IpSec(final Mo parent, final String namespace) {
            super(parent, "IpSec", namespace);
        }

        // Attributes
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
        public enum IpSecCertEnrollStateValue {
            IDLE, ONGOING, ERROR;
        }

        /**
         * Indicates the state of the installation process for the trusted certificates
         */
        public enum IpSecTrustedCertInstallStateValue {
            IDLE, ONGOING, ERROR;
        }

        /**
         * Possible states set by operator for licensed feature.
         */
        public enum ActivationVals {
            ACTIVATED, DEACTIVATED;
        }

        /**
         * Possible states of the license for licensed feature. For details use link :
         * http://cpistore.internal.ericsson.com/alexserv?ID=29840&fn=15554 -AOM901110_S11V1-V1Uen.T.488.html
         */
        public enum StateVals {
            ENABLED, DISABLE;
        }

        public static class IpSecCertInfo {
            public static final String FINGERPRINT = "fingerprint";
            public static final String ISSUER = "issuer";
            public static final String NOT_VALID_AFTER = "notValidAfter";
            public static final String NOT_VALID_BEFORE = "notValidBefore";
            public static final String SERIAL_NUMBER = "serialNumber";
            public static final String SUBJECT = "subject";
            public static final String SUBJECT_ALT_NAME = "subjectAltName";
        }
    }

    public static class Licensing extends Mo {
        Licensing(final Mo parent, final String namespace) {
            super(parent, "Licensing", namespace);
        }

        public final RncFeature rncFeature = new RncFeature(this, this.namespace());

    }

    public static class RncFeature extends Mo {

        RncFeature(final Mo parent, final String namespace) {
            super(parent, "RncFeature", namespace);
        }

        // Attributes
        public static final String FEATURE_STATE = "featureState";
        public static final String IS_LICENSED_CONTROLLED = "isLicenseControlled";
        public static final String KEY_ID = "keyId";
        public static final String LICENSE_STATE = "licenseState";
        public static final String RESERVED_BY = "reservedBy";
        public static final String RNC_FEATURE_ID = "RncFeatureId";
        public static final String SERVICE_STATE = "serviceState";
        public static final String TYPE_OF_TAC_RELATION = "typeOfTacRelation";

        /**
         * Possible states set by operator for feature state.
         */
        public enum ActivationVals {
            ACTIVATED, DEACTIVATED;
        }

        /**
         * Possible states of the license for licensed state.
         */
        public enum StateVals {
            ENABLED, DISABLE;
        }

        /**
         * Possible values of is licensed controlled.
         */
        public enum BoolenVals {
            FALSE, TRUE;
        }

    }

    public static class Security extends Mo {
        Security(final Mo parent, final String namespace) {
            super(parent, "Security", namespace);
        }

        // Attributes
        public static final String OPERATIONAL_SECURITY_LEVEL = "operationalSecurityLevel";
        public static final String FILE_TRANSFER_CLIENT_MODE = "fileTransferClientMode";
        public static final String TRUSTED_CERTIFICATE_INSTALLATION_FAILURE = "trustedCertificateInstallationFailure";
        public static final String CERT_ENROLL_STATE = "certEnrollState";
        public static final String NODE_CERTIFICATE = "nodeCertificate";
        public static final String CERT_ENROLL_ERROR_MSG = "certEnrollErrorMsg";
        public static final String INSTALLED_TRUSTED_CERTIFICATES = "installedTrustedCertificates";
        public static final String CERT_REV_STATUS_CHECK = "certRevStatusCheck";
        public static final String CRL_ON_DEMAND_UPDATE_INTERVAL = "crlEarlyUpdateInterval";
        public static final String WEBSERVER = "webServer";

        public static final String LOCAL_AA_DATABASE_INSTALLATION_FAILURE = "localAADatabaseInstallationFailure";

        public static final String USER_AUTHENTICATION_AND_AUTHORIZATION = "userAuthenticationAndAuthorization";

        // Actions without parameters
        public static final String CANCEL_CERT_ENROLLMENT = "cancelCertEnrollment";
        public static final String CANCEL_INSTALL_TRUSTED_CERTIFICATES = "cancelInstallTrustedCertificates";
        public static final String ADAPT_SECURITY_LEVEL = "adaptSecurityLevel";
        public static final String CONFIRM_NEW_CREDS = "confirmNewCreds";
        public static final String CANCEL_INSTALL_LOCAL_AA_DATABASE = "cancelInstallLocalAADatabase";

        // Actions with parameters
        public static final String INIT_CERT_ENROLLMENT = "initCertEnrollment";
        public static final String INSTALL_TRUSTED_CERTIFICATES = "installTrustedCertificates";
        public static final String REMOVE_TRUSTED_CERTIFICATES = "removeTrustedCert";
        public static final String INSTALL_LOCAL_AA_DATABASE = "installLocalAADatabase";

        public enum FileTransferClientModeValue {
            FTP, SFTP;
        }

        public enum CertEnrollStateValue {
            IDLE, PREPARING_REQUEST, POLLING, NEW_CREDS_AWAIT_CONF, ERROR;
        }

        public static class NodeCertInfo {
            public static final String FINGERPRINT = "fingerprint";
            public static final String ISSUER = "issuer";
            public static final String NOT_VALID_AFTER = "notValidAfter";
            public static final String NOT_VALID_BEFORE = "notValidBefore";
            public static final String SERIAL_NUMBER = "serialNumber";
            public static final String SUBJECT = "subject";
            public static final String CATEGORY = "category";
        }

        public final SecuritySsh ssh = new SecuritySsh(this, this.namespace());
        public final SecurityTls tls = new SecurityTls(this, this.namespace());
        public final RealTimeSecLog realTimeSecLog = new RealTimeSecLog(this, this.namespace());

    }

    public static class TimeM extends Mo {

        TimeM(final Mo parent, final String namespace) {
            super(parent, TIME_M, namespace);
        }
        public final Ntp ntp = new Ntp(this, this.namespace());
    }

    public static class Ntp extends Mo {

        Ntp(final Mo parent, final String namespace) {
            super(parent, NTP, namespace);
        }

        public static final String SUPPORTED_KEY_ALGO = "supportedKeyAlgorithm";
        public final NtpSecurityPolicy ntpSecurityPolicy = new NtpSecurityPolicy(this, this.namespace());
        public final TimeMntpServer ntpServer = new TimeMntpServer(this, this.namespace());
    }

    public static class TimeMntpServer extends Mo {

        TimeMntpServer(final Mo parent, final String namespace) {
            super(parent, NTP_SERVER, namespace);
        }

        public static final String USER_LABEL = "userLabel";
        public static final String NTP_SERVER_ID = "ntpServerId";
        public static final String ADMINISTRATIVE_STATE = "administrativeState";
        public static final String NTP_SECURITY_POLICY = "ntpSecurityPolicy";
        public static final String SERVER_ADDRESS = "serverAddress";
    }

    public static class TimeSetting extends Mo {
        TimeSetting(final Mo parent, final String namespace) {
            super(parent, TIME_SETTING , namespace);
        }

        public static final String INSTALLED_NTP_KEY_IDS = "installedNtpKeyIds";
        public static final String REMOVE_NTP_KEYS = "removeNtpKeys";
        public static final String INSTALL_NTP_KEYS_ACTION = "installNtpKeys";
        public final NtpServer ntpServer = new NtpServer(this, this.namespace());
    }

    public static class NtpServer extends Mo {

        NtpServer(final Mo parent, final String namespace) {
            super(parent, NTP_SERVER , namespace);
        }

        public static final String NTP_KEY_ID = "ntpKeyId";
        public static final String NTP_SERVER_ID = "NtpServerId";
        public static final String SERVER_ADDRESS = "serverAddress";
        public static final String SERVICE_ACTIVE = "serviceActive";
        public static final String SERVICE_STATUS = "serviceStatus";
        public static final String USER_LABEL = "userLabel";
    }

    public static class NtpSecurityPolicy extends Mo {

        NtpSecurityPolicy(final Mo parent, final String namespace) {
            super(parent, NTP_SECURITY_POLICY, namespace);
        }

        // Attributes
        public static final String KEY_ID = "keyId";
        public static final String NTP_SERVER_REF = "ntpServerRef";
        public static final String KEY_ALGORITHM = "keyAlgorithm";
        public static final String NTP_SECURITY_POLICY_ID = "ntpSecurityPolicyId";
        public static final String PRE_SHARED_KEY = "preSharedKey";
    }

    public static class RealTimeSecLog extends Mo {
        RealTimeSecLog(final Mo parent, final String namespace) {
            super(parent, "RealTimeSecLog", namespace);
        }

        // Attributes
        public static final String CONN_ATTEMPT_TIME_OUT = "connAttemptTimeOut";
        public static final String EXT_SERVER_APP_NAME = "extServerAppName";
        public static final String EXT_SERVER_LIST_CONFIG = "extServerListConfig";
        public static final String EXT_SERVER_LIST_INFO = "extServerListInfo";
        public static final String EXT_SERVER_LOG_LEVEL = "extServerLogLevel";
        public static final String FEATURE_STATE = "featureState";
        public static final String LICENSE_STATE = "licenseState";
        public static final String REAL_TIME_SEC_LOG_ID = "RealTimeSecLogId";
        public static final String STATUS = "status";
        public static final String USER_LABEL = "userLabel";

        // Actions with parameters
        public static final String ADD_EXTERNAL_SERVER = "addExternalServer";
        public static final String DELETE_EXTERNAL_SERVER = "deleteExternalServer";

    }

    public static class ServerInfo {
        public static final String EXT_SERVER_ADRESS = "extServerAddress";
        public static final String EXT_SERVER_PROTOCOL = "extServProtocol";
        public static final String SERVER_NAME = "serverName";
    }

    // COM/ECIM section
    public static class ComEcimManagedElement extends Mo {
        ComEcimManagedElement(final String namespace) {
            super(MANAGED_ELEMENT_TYPE, namespace);
        }

        ComEcimManagedElement(final Mo parent, final String namespace) {
            super(parent, MANAGED_ELEMENT_TYPE, namespace);
        }

        // Attributes
        public static final String MANAGED_ELEMENT_ID = "managedElementId";

        public final ComEcimSystemFunctions systemFunctions = new ComEcimSystemFunctions(this, this.namespace());
        public final Transport transport = new Transport(this, this.namespace());
    }

    public static class ComEcimSystemFunctions extends Mo {

        public static Map<String, String> secMNamespace = new HashMap<String, String>();
        static {
            secMNamespace.put(SGSN_MME_TOP_NS, SGSN_MME_SEC_M_NS);
            secMNamespace.put(COM_TOP_NS, COM_SEC_M_NS);
        }

        public static Map<String, String> sysMNamespace = new HashMap<String, String>();
        static {
            sysMNamespace.put(SGSN_MME_TOP_NS, SGSN_MME_SYS_M_NS);
            sysMNamespace.put(COM_TOP_NS, COM_SYS_M_NS);
        }

        ComEcimSystemFunctions(final Mo parent, final String namespace) {
            super(parent, "SystemFunctions", namespace);
        }

        // Attributes
        public static final String SYSTEM_FUNCTIONS_ID = "systemFunctionsId";

        public final SecM secM = new SecM(this, secMNamespace.get(this.namespace()));
        public final SysM sysM = new SysM(this, sysMNamespace.get(this.namespace()));
    }

    public static class SecM extends Mo {

        public static Map<String, String> certMNamespace = new HashMap<String, String>();
        static {
            certMNamespace.put(SGSN_MME_SEC_M_NS, SGSN_MME_CERT_M_NS);
            certMNamespace.put(COM_SEC_M_NS, COM_CERT_M_NS);
        }

        SecM(final Mo parent, final String namespace) {
            super(parent, "SecM", namespace);
        }

        // Attributes
        public static final String SEC_M_ID = "secMId";

        // Children
        public final CertM certM = new CertM(this, certMNamespace.get(this.namespace()));
        public final UserManagement userManagement = new UserManagement(this, this.namespace());

        public final SecMSsh ssh = new SecMSsh(this, this.namespace());
        public final SecMTls tls = new SecMTls(this, this.namespace());
    }

    public static class SecMSsh extends Mo {
        // Attributes
        public static final String SUPPORTED_MACS = "supportedMacs";
        public static final String SUPPORTED_KEY_EXCHANGES = "supportedKeyExchanges";
        public static final String SUPPORTED_CIPHERS = "supportedCiphers";
        public static final String SSH_ID = "sshId";
        public static final String SELECTED_MACS = "selectedMacs";
        public static final String SELECTED_KEY_EXCHANGES = "selectedKeyExchanges";
        public static final String SELECTED_CIPHERS = "selectedCiphers";

        SecMSsh(final Mo parent, final String namespace) {
            super(parent, "Ssh", namespace);
        }

    }

    public static class SecMTls extends Mo {
        // Attributes
        public static final String TLS_ID = "tlsId";
        public static final String SUPPORTED_CIPHERS = "supportedCiphers";
        public static final String ENABLED_CIPHERS = "enabledCiphers";
        public static final String CIPHER_FILTER = "cipherFilter";

        SecMTls(final Mo parent, final String namespace) {
            super(parent, "Tls", namespace);
        }

    }

    public static class SecuritySsh extends Mo {
        // Attributes
        public static final String SUPPORTED_MAC = "supportedMac";
        public static final String SUPPORTED_KEY_EXCHANGE = "supportedKeyExchange";
        public static final String SUPPORTED_CIPHER = "supportedCipher";
        public static final String SSH_ID = "SshId";
        public static final String SELECTED_MAC = "selectedMac";
        public static final String SELECTED_KEY_EXCHANGE = "selectedKeyExchange";
        public static final String SELECTED_CIPHER = "selectedCipher";

        SecuritySsh(final Mo parent, final String namespace) {
            super(parent, "Ssh", namespace);
        }

    }

    public static class SecurityTls extends Mo {
        // Attributes
        public static final String TLS_ID = "TlsId";
        public static final String SUPPORTED_CIPHER = "supportedCipher";
        public static final String ENABLED_CIPHER = "enabledCipher";
        public static final String CIPHER_FILTER = "cipherFilter";

        SecurityTls(final Mo parent, final String namespace) {
            super(parent, "Tls", namespace);
        }

    }

    public static class UserManagement extends Mo {

        public static Map<String, String> ldapAuthMethodNamespace = new HashMap<String, String>();
        static {
            ldapAuthMethodNamespace.put(COM_SEC_M_NS, RCS_LDAP_AUTH_NS);
        }

        UserManagement(final Mo parent, final String namespace) {
            super(parent, USER_MANAGEMENT_TYPE, namespace);
        }

        // Attributes
        public static final String USER_MANAGEMENT_ID = "userManagementId";
        public static final String USER_LABEL = "userLabel";
        public static final String TARGET_TYPE = "targetType";

        // Children
        public final LdapAuthenticationMethod ldapAuthenticationMethod = new LdapAuthenticationMethod(this,
                ldapAuthMethodNamespace.get(this.namespace()));

    }

    public static class LdapAuthenticationMethod extends Mo {

        LdapAuthenticationMethod(final Mo parent, final String namespace) {
            super(parent, LDAP_AUTHENTICATION_METHOD_TYPE, namespace);
        }

        // Attributes
        public static final String USER_LABEL = "userLabel";
        public static final String LDAP_AUTHENTICATION_METHOD_ID = "ldapAuthenticationMethodId";
        // Children
        public final Ldap ldap = new Ldap(this, this.namespace());

    }

    public static class Ldap extends Mo {

        Ldap(final Mo parent, final String namespace) {
            super(parent, "Ldap", namespace);
        }

        // Attributes
        public static final String LDAP_ID = "ldapId";
        public static final String USE_TLS = "useTls";
        public static final String TLS_MODE = "tlsMode";
        public static final String USER_LABEL = "userLabel";
        public static final String SERVER_PORT = "serverPort";
        public static final String LDAP_IP_ADDRESS = "ldapIpAddress";
        public static final String FALLBACK_LDAP_IP_ADDRESS = "fallbackLdapIpAddress";
        public static final String BIND_DN = "bindDn";
        public static final String BIND_PASSWORD = "bindPassword";
        public static final String BASE_DN = "baseDn";
        public static final String TRUST_CATEGORY = "trustCategory";
        public static final String NODE_CREDENTIAL = "nodeCredential";

    }

    public static class CertM extends Mo {
        CertM(final Mo parent, final String namespace) {
            super(parent, "CertM", namespace);
        }

        // Attributes
        public static final String CERT_M_ID = "certMId";
        public static final String LOCAL_FILE_STORE_PATH = "localFileStorePath";
        public static final String REPORT_PROGRESS = "reportProgress";
        public static final String USER_LABEL = "userLabel";

        // Actions without parameters
        public static final String CANCEL = "cancel";
        public static final String DOWNLOAD_CRL = "downloadCrl";

        // Actions with parameters
        public static final String INSTALL_TRUSTED_CERT_FROM_URI = "installTrustedCertFromUri";
        public static final String INSTALL_TRUSTED_CERT_FROM_URI_FINGERPRINT = "fingerprint";
        public static final String INSTALL_TRUSTED_CERT_FROM_URI_URI = "uri";
        public static final String INSTALL_TRUSTED_CERT_FROM_URI_URI_PASSWORD = "uriPassword";

        public static MoParams installTrustedCertFromUriToMoParams(final String fingerprint, final String uri, final String uriPassword) {
            final MoParams params = new MoParams();
            params.addParam(INSTALL_TRUSTED_CERT_FROM_URI_FINGERPRINT, fingerprint);
            params.addParam(INSTALL_TRUSTED_CERT_FROM_URI_URI, uri);
            params.addParam(INSTALL_TRUSTED_CERT_FROM_URI_URI_PASSWORD, uriPassword);
            return params;
        }

        public static final String REMOVE_TRUSTED_CERT = "removeTrustedCert";
        public static final String REMOVE_TRUSTED_CERT_TRUSTED_CERT = "trustedCert";

        public static MoParams removeTrustedCertToMoParams(final String trustedCert) {
            final MoParams params = new MoParams();
            params.addParam(REMOVE_TRUSTED_CERT_TRUSTED_CERT, trustedCert);
            return params;
        }

        // Children
        public final VendorCredential vendorCredential = new VendorCredential(this, this.namespace());
        public final EnrollmentAuthority enrollmentAuthority = new EnrollmentAuthority(this, this.namespace());
        public final NodeCredential nodeCredential = new NodeCredential(this, this.namespace());
        public final TrustCategory trustCategory = new TrustCategory(this, this.namespace());
        public final TrustedCertificate trustedCertificate = new TrustedCertificate(this, this.namespace());
        public final CertMCapabilities certMCapabilities = new CertMCapabilities(this, this.namespace());
        public final EnrollmentServerGroup enrollmentServerGroup = new EnrollmentServerGroup(this, this.namespace());
    }

    public static abstract class Certificate extends Mo {
        Certificate(final Mo parent, final String type, final String namespace) {
            super(parent, type, namespace);
        }

        public static final String CERTIFICATE_CONTENT = "certificateContent";
        public static final String CERTIFICATE_STATE = "certificateState";

        public enum CertificateState {
            EXPIRED("2"), NOT_VALID_YET("1"), REVOKED("3"), VALID("0");

            private final String value;

            private CertificateState(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }
    }

    public static class VendorCredential extends Certificate {
        VendorCredential(final Mo parent, final String namespace) {
            super(parent, "VendorCredential", namespace);
        }

        public static final String VENDOR_CREDENTIAL_ID = "vendorCredentialId";
    }

    public static class EnrollmentAuthority extends Certificate {
        EnrollmentAuthority(final Mo parent, final String namespace) {
            super(parent, ENROLLMENT_AUTHORITY_TYPE, namespace);
        }

        public static final String AUTHORITY_TYPE = "authorityType";
        public static final String ENROLLMENT_AUTHORITY_ID = "enrollmentAuthorityId";
        public static final String ENROLLMENT_AUTHORITY_NAME = "enrollmentAuthorityName";
        public static final String ENROLLMENT_CA_CERTIFICATE = "enrollmentCaCertificate";
        public static final String ENROLLMENT_CA_FINGERPRINT = "enrollmentCaFingerprint";
        public static final String USER_LABEL = "userLabel";

        public enum AuthorityType {
            CERTIFICATION_AUTHORITY("0"), REGISTRATION_AUTHORITY("1");

            private final String value;

            private AuthorityType(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }
    }

    public static class NodeCredential extends Certificate {
        NodeCredential(final Mo parent, final String namespace) {
            super(parent, NODE_CREDENTIAL_TYPE, namespace);
        }

        // Attributes
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
        public static final String SUBJECT_ALT_NAME = "subjectAltName";
        public static final String USER_LABEL = "userLabel";

        // Actions without parameters
        public static final String CANCEL_ENROLLMENT = "cancelEnrollment";

        // Actions with parameters
        public static final String INSTALL_CREDENTIAL_FROM_URI = "installCredentialFromUri";
        public static final String INSTALL_CREDENTIAL_FROM_URI_CREDENTIAL_PASSWORD = "credentialPassword";
        public static final String INSTALL_CREDENTIAL_FROM_URI_FINGERPRINT = "fingerprint";
        public static final String INSTALL_CREDENTIAL_FROM_URI_URI = "uri";
        public static final String INSTALL_CREDENTIAL_FROM_URI_URI_PASSWORD = "uriPassword";

        public static MoParams installCredentialFromUriToMoParams(final String credentialPassword, final String fingerprint, final String uri,
                final String uriPassword) {
            final MoParams params = new MoParams();
            params.addParam(INSTALL_CREDENTIAL_FROM_URI_CREDENTIAL_PASSWORD, credentialPassword);
            params.addParam(INSTALL_CREDENTIAL_FROM_URI_FINGERPRINT, fingerprint);
            params.addParam(INSTALL_CREDENTIAL_FROM_URI_URI, uri);
            params.addParam(INSTALL_CREDENTIAL_FROM_URI_URI_PASSWORD, uriPassword);
            return params;
        }

        public static final String START_OFFLINE_CSR_ENROLLMENT = "startOfflineCsrEnrollment";
        public static final String START_OFFLINE_CSR_ENROLLMENT_URI = "uri";
        public static final String START_OFFLINE_CSR_ENROLLMENT_URI_PASSWORD = "uriPassword";

        public static MoParams startOfflineCsrEnrollmentToMoParams(final String uri, final String uriPassword) {
            final MoParams params = new MoParams();
            params.addParam(START_OFFLINE_CSR_ENROLLMENT_URI, uri);
            params.addParam(START_OFFLINE_CSR_ENROLLMENT_URI_PASSWORD, uriPassword);
            return params;
        }

        public static final String START_ONLINE_ENROLLMENT = "startOnlineEnrollment";
        public static final String START_ONLINE_ENROLLMENT_CHALLENGE_PASSWORD = "challengePassword";

        public static MoParams startOnlineEnrollmentToMoParams(final String challengePassword) {
            final MoParams params = new MoParams();
            params.addParam(START_ONLINE_ENROLLMENT_CHALLENGE_PASSWORD, challengePassword);
            return params;
        }

        public enum KeyInfo {
            RSA_1024("0"),
            RSA_2048("1"),
            RSA_3072("2"),
            RSA_4096("3"),
            ECDSA_160("4"),
            ECDSA_224("5"),
            ECDSA_256("6"),
            ECDSA_384("7"),
            ECDSA_512("8"),
            ECDSA_521("9");

            private final String value;

            private KeyInfo(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }

            public static KeyInfo fromKeyLength(final KeyLength keyLength) {
                KeyInfo keyInfo = null;
                switch (keyLength) {
                case ECDSA160:
                    keyInfo = KeyInfo.ECDSA_160;
                    break;
                case ECDSA224:
                    keyInfo = KeyInfo.ECDSA_224;
                    break;
                case ECDSA256:
                    keyInfo = KeyInfo.ECDSA_256;
                    break;
                case ECDSA384:
                    keyInfo = KeyInfo.ECDSA_384;
                    break;
                case ECDSA512:
                    keyInfo = KeyInfo.ECDSA_512;
                    break;
                case ECDSA521:
                    keyInfo = KeyInfo.ECDSA_521;
                    break;
                case RSA1024:
                    keyInfo = KeyInfo.RSA_1024;
                    break;
                case RSA2048:
                    keyInfo = KeyInfo.RSA_2048;
                    break;
                case RSA3072:
                    keyInfo = KeyInfo.RSA_3072;
                    break;
                case RSA4096:
                    keyInfo = KeyInfo.RSA_4096;
                    break;
                default:
                    break;
                }
                return keyInfo;
            }
        }

        public enum RenewalMode {
            AUTOMATIC("1"), MANUAL("0");

            private final String value;

            private RenewalMode(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }
    }

    public static class TrustCategory extends Mo {
        TrustCategory(final Mo parent, final String namespace) {
            super(parent, TRUST_CATEGORY_TYPE, namespace);
        }

        public static final String RESERVED_BY_USER = "reservedByUser";
        public static final String TRUST_CATEGORY_ID = "trustCategoryId";
        public static final String TRUSTED_CERTIFICATES = "trustedCertificates";
        public static final String CRL_INTERFACE = "crlInterface";
        public static final String USER_LABEL = "userLabel";
        public static final String CRL_CHECK = "crlCheck";
    }

    public static class TrustedCertificate extends Certificate {
        TrustedCertificate(final Mo parent, final String namespace) {
            super(parent, "TrustedCertificate", namespace);
        }

        public static final String MANAGED_STATE = "managedState";
        public static final String RESERVED_BY_CATEGORY = "reservedByCategory";
        public static final String RESERVED_BY = "reservedBy";
        public static final String TRUSTED_CERTIFICATE_ID = "trustedCertificateId";

        public enum ManagedCertificateState {
            DISABLED("1"), ENABLED("0");

            private final String value;

            private ManagedCertificateState(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }
    }

    public static class CertMCapabilities extends Mo {
        CertMCapabilities(final Mo parent, final String namespace) {
            super(parent, "CertMCapabilities", namespace);
        }

        public static final String CERT_M_CAPABILITIES_ID = "certMCapabilitiesId";
        public static final String ENROLLMENT_SUPPORT = "enrollmentSupport";
        public static final String FINGERPRINT_SUPPORT = "fingerprintSupport";

        public enum EnrollmentSupport {
            OFFLINE_CSR("0"), OFFLINE_PKCS12("1"), ONLINE_CMP("3"), ONLINE_SCEP("2");

            private final String value;

            private EnrollmentSupport(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }

            public static EnrollmentSupport fromEnrollmentMode(final EnrollmentMode enrollmentMode) {
                EnrollmentSupport enrollmentSupport = null;
                switch (enrollmentMode) {
                case CMPv2_INITIAL:
                    // break intentionally omitted
                case CMPv2_UPDATE:
                    // break intentionally omitted
                case CMPv2_VC:
                    enrollmentSupport = ONLINE_CMP;
                    break;
                case OFFLINE_CSR:
                    enrollmentSupport = OFFLINE_CSR;
                    break;
                case OFFLINE_PKCS12:
                    enrollmentSupport = OFFLINE_PKCS12;
                    break;
                case ONLINE_SCEP:
                    // break intentionally omitted
                case SCEP:
                    enrollmentSupport = ONLINE_SCEP;
                    break;
                case MANUAL:
                    // break intentionally omitted
                case NOT_SUPPORTED:
                    // break intentionally omitted
                default:
                    break;
                }
                return enrollmentSupport;
            }
        }

        public enum FingerprintSupport {
            SHA_1("0"), SHA_224("1"), SHA_256("2"), SHA_384("3"), SHA_512("4");

            private final String value;

            private FingerprintSupport(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }
    }

    public static class EnrollmentServerGroup extends Certificate {
        EnrollmentServerGroup(final Mo parent, final String namespace) {
            super(parent, ENROLLMENT_SERVER_GROUP_TYPE, namespace);
        }

        public static final String ENROLLMENT_SERVER_GROUP_ID = "enrollmentServerGroupId";
        public static final String USER_LABEL = "userLabel";

        public final EnrollmentServer enrollmentServer = new EnrollmentServer(this, this.namespace());
    }

    public static class EnrollmentServer extends Certificate {
        EnrollmentServer(final Mo parent, final String namespace) {
            super(parent, ENROLLMENT_SERVER_TYPE, namespace);
        }

        public static final String ENROLLMENT_AUTHORITY = "enrollmentAuthority";
        public static final String ENROLLMENT_SERVER_ID = "enrollmentServerId";
        public static final String PROTOCOL = "protocol";
        public static final String URI = "uri";
        public static final String ENROLLMENT_INTERFACE = "enrollmentInterface";
        public static final String USER_LABEL = "userLabel";

        public enum EnrollmentProtocol {
            CMP("1"), SCEP("0");

            private final String value;

            private EnrollmentProtocol(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }

            public static EnrollmentProtocol fromEnrollmentMode(final EnrollmentMode enrollmentMode) {
                EnrollmentProtocol enrollmentProtocol = null;
                switch (enrollmentMode) {
                case CMPv2_INITIAL:
                    // break intentionally omitted
                case CMPv2_UPDATE:
                    // break intentionally omitted
                case CMPv2_VC:
                    enrollmentProtocol = CMP;
                    break;
                case ONLINE_SCEP:
                    // break intentionally omitted
                case SCEP:
                    enrollmentProtocol = SCEP;
                    break;
                case MANUAL:
                    // break intentionally omitted
                case NOT_SUPPORTED:
                    // break intentionally omitted
                case OFFLINE_CSR:
                    // break intentionally omitted
                case OFFLINE_PKCS12:
                    // break intentionally omitted
                default:
                    break;
                }
                return enrollmentProtocol;
            }
        }
    }

    public static class SysM extends Mo {

        SysM(final Mo parent, final String namespace) {
            super(parent, "SysM", namespace);
        }
        // Attributes
        public static final String SYS_M_ID = "sysMId";

        // Children
        public final NetconfTls netconfTls = (COM_SYS_M_NS.equals(this.namespace()) ? new NetconfTls(this, this.namespace()) : null);
        public final OamAccessPoint oamAccessPoint = (COM_SYS_M_NS.equals(this.namespace()) ? new OamAccessPoint(this, COM_OAM_ACCESS_POINT_NS)
                : null);
        public final FileTPM fileTPM = (COM_SYS_M_NS.equals(this.namespace()) ? new FileTPM(this, COM_FILE_TPM_NS) : null);
        public final TimeM timeM = (COM_SYS_M_NS.equals(this.namespace()) ? new TimeM(this, COM_TIME_M_NS) : null);
    }

    public static class NetconfTls extends Mo {

        NetconfTls(final Mo parent, final String namespace) {
            super(parent, "NetconfTls", namespace);
        }

        // Attributes
        public static final String NETCONF_TLS_ID = "netconfTlsId";
        public static final String ADMINISTRATIVE_STATE = "administrativeState";
        public static final String TRUST_CATEGORY = "trustCategory";
        public static final String NODE_CREDENTIAL = "nodeCredential";

        public enum BasicAdmState {
            LOCKED, UNLOCKED
        }
    }

    public static class OamAccessPoint extends Mo {

        OamAccessPoint(final Mo parent, final String namespace) {
            super(parent, "OamAccessPoint", namespace);
        }

        // Attributes
        public static final String OAM_ACCESS_POINT_ID = "oamAccessPointId";
        public static final String IPV4ADDRESS = "ipv4address";
    }

    public static class FileTPM extends Mo {

        FileTPM(final Mo parent, final String namespace) {
            super(parent, FILE_TPM_TYPE, namespace);
        }

        // Attributes
        public static final String FILE_TPM_ID = "fileTPMId";

        // Children
        public final FtpTls ftpTls = new FtpTls(this, this.namespace());

        // Children from ECIM FileTPM 2.1
        public final FtpServer ftpServer = new FtpServer(this, this.namespace());

    }

    // FileTPM 2.1
    public static class FtpServer extends Mo {

        FtpServer(final Mo parent, final String namespace) {
            super(parent, FILE_TPM_TYPE, namespace);
        }

        // Attributes
        public static final String FTP_SERVER_ID = "ftpServerId";
        public static final String IDLE_TIMER = "idleTimer";

        // Children
        public final FtpTlsServer ftpTlsServer = new FtpTlsServer(this, this.namespace());

    }

    public static class FtpTls extends Mo {

        FtpTls(final Mo parent, final String namespace) {
            super(parent, FTP_TLS_TYPE, namespace);
        }

        // Attributes
        public static final String FTP_TLS_ID = "ftpTlsId";
        public static final String NODE_CREDENTIAL = "nodeCredential";
        public static final String TRUST_CATEGORY = "trustCategory";

        // Children
        public final FtpTlsServer ftpTlsServer = new FtpTlsServer(this, this.namespace());
    }

    public static class FtpTlsServer extends Mo {

        FtpTlsServer(final Mo parent, final String namespace) {
            super(parent, FTP_TLS_SERVER_TYPE, namespace);
        }

        // Attributes
        public static final String ADMINISTRATIVE_STATE = "administrativeState";
        public static final String FTP_TLS_SERVER_ID = "ftpTlsServerId";

        //Attributes for FileTPM 2.1
        public static final String PORT = "port";
        public static final String MAX_DATA_PORT = "maxDataPort";
        public static final String MIN_DATA_PORT = "minDataPort";
        public static final String NODE_CREDENTIAL = "nodeCredential";
        public static final String TRUST_CATEGORY = "trustCategory";

        public enum BasicAdmState {
            LOCKED, UNLOCKED
        }
    }

    public static class Transport extends Mo {

        Transport(final Mo parent, final String namespace) {
            super(parent, "Transport", namespace);
        }

        // Attributes
        public static final String TRANSPORT_ID = "transportId";

        // Children
        public final Ikev2PolicyProfile ikev2PolicyProfile = (COM_TOP_NS.equals(this.namespace())
                ? new Ikev2PolicyProfile(this, COM_IKEV2_POLICY_PROFILE_NS) : null);
    }

    public static class Ikev2PolicyProfile extends Mo {

        Ikev2PolicyProfile(final Mo parent, final String namespace) {
            super(parent, "Ikev2PolicyProfile", namespace);
        }

        // Attributes
        public static final String IKEV2_POLICY_PROFILE_ID = "ikev2PolicyProfileId";
        public static final String IKEV2_PROPOSAL = "ikev2Proposal";
        public static final String TRUST_CATEGORY = "trustCategory";
        public static final String CREDENTIAL = "credential";
    }

    /**
     * Super type of all Model definitions.
     * <p>
     * It is a holder for type and namespace information and also provides auxiliary functions like urn(), extractName() and withNames()
     * </p>
     */
    public abstract static class Mo {
        private final Mo parent;
        private final String type;
        private final String namespace;
        private String urnBase;
        private Pattern namePattern;

        public Mo(final String type) {
            this.type = type;
            this.parent = null;
            this.namespace = "";
            createUrnBase();
            createNamePattern();
        }

        public Mo(final String type, final String namespace) {
            this.parent = null;
            this.type = type;
            this.namespace = namespace;
            createUrnBase();
            createNamePattern();
        }

        public Mo(final Mo parent, final String type) {
            this.parent = parent;
            this.type = type;
            this.namespace = "";
            createUrnBase();
            createNamePattern();
        }

        public Mo(final Mo parent, final String type, final String namespace) {
            this.parent = parent;
            this.type = type;
            this.namespace = namespace;
            createUrnBase();
            createNamePattern();
        }

        private final void createUrnBase() {
            this.urnBase = String.format("//%s/%s/", namespace, type);
        }

        private final void createNamePattern() {
            namePattern = Pattern.compile(String.format("%s=([^,]+)(,|$)", type()));
        }

        /**
         * Creates a String representation of the MO's URN : //&lt;namespace&gt;/&lt;type&gt;/&lt;version&gt;
         * <p>
         * Eg.:<BR>
         * </BR>
         * //CPP_NE_SECURITY/NetworkElementSecurity/1.0.1
         * </p>
         *
         * @param version
         * @return the URN
         */
        public String urn(final String version) {
            return urnBase + version;
        }

        /**
         * @return the MO's type. Eg.: MeContext
         */
        public final String type() {
            return type;
        }

        /**
         * @return the MO's namespace. Eg.: OSS_TOP
         */
        public String namespace() {
            return namespace;
        }

        /**
         * @return the MO's parent. Eg.: MeContext
         */
        public Mo parent() {
            return this.parent;
        }

        /**
         * Creates a NamedMo instance using given names or FDN.
         * <p>
         * A NamedMo is necessary in order to be able to create FDNs.
         * </p>
         * <p>
         * Some examples:
         * </p>
         * <code><pre>
         *     Model.ME_CONTEXT.withNames("node1").fdn();
         *     // will return "MeContext=node1"
         *     Model.ME_CONTEXT.withNames("MeContext=node1").fdn();
         *     // will return "MeContext=node1"
         *     Model.ME_CONTEXT.withNames("SubNetwork=subnetwork1,MeContext=node1").fdn();
         *     // will return "MeContext=node1"
         * </pre></code>
         * <p>
         * It is possible to use it with a chained structure too:
         * </p>
         * <code><pre>
         *     Model.ME_CONTEXT.networkElementSecurity.withNames("node1", "secname").fdn();
         *     // will return "MeContext=node1,NetworkElementSecurity=secname"
         * </pre></code>
         * <p>
         * It is NOT mandatory to specify names for each element. Default name is "1":
         * </p>
         * <code><pre>
         *     Model.ME_CONTEXT.networkElementSecurity.withNames().fdn();
         *     // will return "MeContext=1,NetworkElementSecurity=1"
         *
         *     Model.ME_CONTEXT.networkElementSecurity.withNames(null, "secname").fdn();
         *     // will return "MeContext=1,NetworkElementSecurity=secname"
         *
         *     // and the most likely usages:
         *
         *     Model.ME_CONTEXT.networkElementSecurity.withNames("node1").fdn();
         *     // will return "MeContext=node1,NetworkElementSecurity=1"
         *
         *     Model.ME_CONTEXT.systemFunctions.security.withNames("node1").fdn();
         *     // will return "MeContext=node1,SystemFunctions=1,Security=1"
         *     Model.ME_CONTEXT.systemFunctions.security.withNames("MeContext=node1").fdn();
         *     // will return "MeContext=node1,SystemFunctions=1,Security=1"
         *     Model.ME_CONTEXT.systemFunctions.security.withNames("SubNetwork=subnetwork1,MeContext=node1").fdn();
         *     // will return "SubNetwork=subnetwork1,MeContext=node1,SystemFunctions=1,Security=1"
         * </pre></code>
         *
         * @param names
         * @return
         */
        public NamedMo withNames(final String... names) {
            return new NamedMo(this, names);
        }

        /**
         * Extract the element name from a FDN String.
         * <p>
         * Some examples:
         * </p>
         * <code><pre>
         *     Model.ME_CONTEXT.extractName("MeContext=node1");
         *     // will return "node1"
         *
         *     Model.ME_CONTEXT.extractName("MeContext=node1,SystemFunctions=1,Security=1");
         *     // will return "node1"
         *
         *     Model.ME_CONTEXT.networkElementSecurity.extractName("MeContext=node1,NetworkElementSecurity=secname");
         *     // will return "secname"
         * </pre></code>
         *
         * @param fdn
         * @return
         */
        public String extractName(final String fdn) {
            final Matcher matcher = namePattern.matcher(fdn);
            String name = null;
            if (matcher.find()) {
                name = matcher.group(1);
            }
            return name;
        }

        /**
         * Checks if the provided FDN contains this type.
         *
         * @param fdn
         *            String with a FDN
         * @return true if the provided FDN contains this type
         */
        public boolean isPresent(final String fdn) {
            final Matcher matcher = namePattern.matcher(fdn);
            return matcher.find();
        }

        public String getFdnByParentFdn(final String parentFdn, final String name) {
            if (parentFdn == null || name == null) {
                return null;
            }
            final String rdn = String.format(",%s=%s", this.type, name);
            return parentFdn.concat(rdn);
        }

        /**
         * Same as type()
         *
         * @return
         */
        @Override
        public String toString() {
            return type;
        }
    }

    public static class NamedMo extends Mo {
        private static final Pattern DN_PATTERN = Pattern.compile("([^,]+)\\s*=\\s*([^,]+)(,|$)", Pattern.CASE_INSENSITIVE);
        private String[] namesOrFdn;

        public NamedMo(final Mo source, final String... namesOrFdn) {
            super(source.parent, source.type, source.namespace);
            this.namesOrFdn = namesOrFdn;
        }

        public String fdn() {
            final Deque<Mo> mos = getOrderedMos();

            final String nameOrFdn = getMoNameAt(0);
            if (nameOrFdn != null && (nameOrFdn.contains("=") || nameOrFdn.contains(","))) {
                final Matcher matcher = DN_PATTERN.matcher(nameOrFdn);
                final List<String> currNames = new ArrayList<>();
                while (matcher.find()) {
                    final String currType = matcher.group(1);
                    final String currName = matcher.group(2);
                    final SubNetwork subNetworkMo = new SubNetwork();
                    if (subNetworkMo.type().equals(currType)) {
                        mos.addFirst(subNetworkMo);
                    }
                    currNames.add(currName);
                }

                if (currNames.size() > 0) {
                    this.namesOrFdn = currNames.toArray(new String[currNames.size()]);
                }
            }

            Mo current;

            final StringBuilder fdn = new StringBuilder();
            String name;
            for (int i = 0; !mos.isEmpty(); i++) {
                current = mos.pop();
                name = getMoNameAt(i);
                if (i > 0) {
                    fdn.append(",");
                }
                fdn.append(current.type()).append("=").append(name);
            }

            return fdn.toString();
        }

        @Override
        public boolean isPresent(final String fdn) {
            int i = 0;
            for (final Mo mo : getOrderedMos()) {
                final String nameAtFdn = mo.extractName(fdn);
                if (nameAtFdn == null || !nameAtFdn.equals(getMoNameAt(i))) {
                    return false;
                }
                i++;
            }
            return true;
        }

        private String getMoNameAt(final int i) {
            String name;
            name = (namesOrFdn != null) ? (i < namesOrFdn.length && namesOrFdn[i] != null ? namesOrFdn[i] : "1") : "1";
            return name;
        }

        private Deque<Mo> getOrderedMos() {
            final Deque<Mo> mos = new LinkedList<>();
            Mo current = this;
            do {
                mos.addFirst(current);
                current = current.parent;
            } while (current != null);
            return mos;
        }

        /**
         * Same as fdn()
         *
         * @return
         */
        @Override
        public String toString() {
            return fdn();
        }
    }

    /**
     * Get Reduced Distinguished Name (RDN) name of a given RDN type for a given Distinguished Name (DN) of format: RDN1,RDN2,... and where a generic
     * RDN is of format Type=name.
     *
     * @param dn
     *            the Distinguished Name (DN).
     * @param rdnType
     *            the RDN type.
     * @return the RDN name or null if RDN is not present in DN.
     */
    public static String getRdnNameByTypeFromDn(final String dn, final String rdnType) {
        final Pattern namePattern = Pattern.compile(String.format("%s=([^,]+)(,|$)", rdnType));
        final Matcher matcher = namePattern.matcher(dn);
        String rdnName = null;
        if (matcher.find()) {
            rdnName = matcher.group(1);
        }
        return rdnName;
    }

    public static class AsyncActionProgress {

        public static final String ACTION_ID = "actionId";
        public static final String ACTION_NAME = "actionName";
        public static final String ADDITIONAL_INFO = "additionalInfo";
        public static final String PROGRESS_INFO = "progressInfo";
        public static final String PROGRESS_PERCENTAGE = "progressPercentage";
        public static final String RESULT = "result";
        public static final String RESULT_INFO = "resultInfo";
        public static final String STATE = "state";

        public enum ActionStateType {
            CANCELLING("1"), RUNNING("2"), FINISHED("3"), CANCELLED("4");

            private final String value;

            private ActionStateType(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }

        public enum ActionResultType {
            SUCCESS("1"), FAILURE("2"), NOT_AVAILABLE("3");

            private final String value;

            private ActionResultType(final String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }

    }

    public static class CertificateContent {

        public static final String ISSUER = "issuer";
        public static final String SERIAL_NUMBER = "serialNumber";
        public static final String SUBJECT_DIST_NAME = "subject";

    }
}
