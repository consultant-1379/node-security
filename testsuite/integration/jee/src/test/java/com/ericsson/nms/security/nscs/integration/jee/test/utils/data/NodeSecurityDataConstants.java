package com.ericsson.nms.security.nscs.integration.jee.test.utils.data;

import java.util.Arrays;
import java.util.List;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;

public class NodeSecurityDataConstants {

    public static final String MANAGED_ELEMENT = "ManagedElement";
    public static final String SYSTEM_FUNCTIONS = "SystemFunctions";
    public static final String IP_SYSTEM = "IpSystem";
    public static final String IP_SEC = "IpSec";
    public static final String MO_NAME = "1";
    public static final String SECURITY = "Security";
    public static final String NODE_NAME1 = "ERBS2";
    public static final String NODE_NAME2 = "ERBS1"; // Deliberate
    public static final String CPP_NODE_NAME1 = "CPP_ERBS2";
    public static final String CPP_NODE_NAME2 = "CPP_ERBS1"; // Deliberate
    public static final String NODE_NAME4 = "RBS13";
    public static final String NODE_AUTOREISSUE_NAME1 = "AutoReissue1";

    // different naming
    // pattern for
    // Search use-cases

    // constants for radio nodes :
    public static final String NODE_NAME3 = "DUG22";
    public static final String MECONTEXT_FDN3 = "MeContext=" + NODE_NAME3;
    public static final String AUTH_PROTOCOL = "MD5";
    public static final String AUTH_KEY = "ericsson1235";
    public static final String PRIV_PROTOCOL = "DES";
    public static final String PRIV_KEY = "ericsson1234";

    // constants for MINI-LINK nodes:
    public static final String NODE_NAME_ML_INDOOR = "MINI-LINK-INDOOR_1";

    // constants for MINI-LINK-CN210 nodes:
    public static final String NODE_NAME_ML_CN210 = "MINI-LINK-CN210_1";

    // constants for MINI-LINK-CN510R1 nodes:
    public static final String NODE_NAME_ML_CN510R1 = "MINI-LINK-CN510R1_1";

    // constants for MINI-LINK-CN510R2 nodes:
    public static final String NODE_NAME_ML_CN510R2 = "MINI-LINK-CN510R2_1";

    // constants for MINI-LINK-CN810R1 nodes:
    public static final String NODE_NAME_ML_CN810R1 = "MINI-LINK-CN810R1_1";

    // constants for MINI-LINK-CN810R2 nodes:
    public static final String NODE_NAME_ML_CN810R2 = "MINI-LINK-CN810R2_1";

    // constants for MINI-LINK-665x nodes:
    public static final String NODE_NAME_ML_665x = "MINI-LINK-665x";

    // constants for MINI-LINK-669x nodes:
    public static final String NODE_NAME_ML_669x = "MINI-LINK-669x";

    // constants for MINI-LINK-MW2 nodes:
    public static final String NODE_NAME_ML_MW2 = "MINI-LINK-MW2";

    // constants for MINI-LINK-6352 nodes
    public static final String NODE_NAME_ML_6352 = "MINI-LINK-6352_1";

    // constants for MINI-LINK-6351 nodes:
    public static final String NODE_NAME_ML_6351 = "MINI-LINK-6351_1";

    // constants for MINI-LINK-6366 nodes:
    public static final String NODE_NAME_ML_6366 = "MINI-LINK-6366_1";

    // constants for MINI-LINK-PT2020 nodes:
    public static final String NODE_NAME_ML_PT2020 = "MINI-LINK-PT2020_1";

    //constants for Switch 6391 node:
    public static final String NODE_SWITCH_6391 = "Switch_6391_1";

    //constants for Fronthaul 6392 node:
    public static final String NODE_FRONTHAUL_6392 = "FRONTHAUL_6392_1";

    // constants for Cisco/Juniper nodes:
    public static final String NODE_NAME_CISCO_ASR9000 = "CISCO-ASR9000_3";
    public static final String NODE_NAME_CISCO_ASR900 = "CISCO-ASR900_4";
    public static final String NODE_NAME_JUNIPER_MX = "JUNIPER_MX_5";

    // Useful constants for tests:
    public static final String MECONTEXT_FDN1 = "MeContext=" + NODE_NAME1;
    public static final String MANAGED_ELEMENT_FDN1 = MECONTEXT_FDN1 + ",ManagedElement=1";
    //	public static final String ENODEB_FUNCTION_FDN1 = MANAGED_ELEMENT_FDN1
    //			+ ",ENodeBFunction=1";
    public static final String MECONTEXT_FDN2 = "MeContext=" + NODE_NAME2;
    public static final String MANAGED_ELEMENT_FDN2 = MECONTEXT_FDN2 + ",ManagedElement=1";
    public static final String MECONTEXT_CPP_FDN1 = "MeContext=" + CPP_NODE_NAME1;
    public static final String MECONTEXT_CPP_FDN2 = "MeContext=" + CPP_NODE_NAME2;
    //public static final String ENODEB_FUNCTION_FDN2 = MANAGED_ELEMENT_FDN2
    //		+ ",ENodeBFunction=1";
    public static final String GENERATION_COUNTER = "generationCounter";
    public static final String EXAMPLE_ATTRIBUTE_ENODEB_FUNCTION = "zzzTemporary2";
    public static final String EXAMPLE_ATTRIBUTE_ENODEB_FUNCTION_VALUE = "my test value";

    public static final String TOP_NAMESPACE = "OSS_TOP";
    public static final String NODE_NAMESPACE = "OSS_NE_DEF";
    public static final String TOP_NAMESPACE_VERSION = "3.0.0";
    public static final String MANDATORY_MANAGED_ELEMENT_ATTRIBUTE = "ManagedElementId";
    public static final String MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE = "1";
    //public static final String MANDATORY_ENODEB_FUNCTION_ATTRIBUTE = "ENodeBFunctionId";
    public static final String MANDATORY_ENODEB_FUNCTION_ATTRIBUTE_VALUE = "1";
    public static final String MANDATORY_ANR_FUNCTION_ATTRIBUTE = "AnrFunctionId";
    public static final String MANDATORY_ANR_FUNCTION_ATTRIBUTE_VALUE = "1";
    // Useful constants for tests:
    //public static final String MECONTEXT_FDN = "MeContext=" + NODE_NAME;
    //public static final String MANAGED_ELEMENT_FDN = MECONTEXT_FDN
    //		+ ",ManagedElement=1";
    //	public static final String ENODEB_FUNCTION_FDN = MANAGED_ELEMENT_FDN
    //			+ ",ENodeBFunction=1";
    //	public static final String ENODEB_FUNCTION_FDN_FOR_COMPLEX_ATTR = MANAGED_ELEMENT_FDN
    //			+ ",ENodeBFunction=12";
    public static final String MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE = "SystemFunctionsId";
    public static final String MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE_VALUE = "1";
    public static final String MANDATORY_SECURITY_ATTRIBUTE = "SecurityId";
    public static final String MANDATORY_SECURITY_ATTRIBUTE_VALUE = "1";
    public static final String MANDATORY_IP_SYSTEM_ATTRIBUTE = "IpSystemId";
    public static final String MANDATORY_IP_SYSTEM_ATTRIBUTE_VALUE = "1";
    public static final String MANDATORY_IP_SEC_ATTRIBUTE = "IpSecId";
    public static final String MANDATORY_IP_SEC_ATTRIBUTE_VALUE = "1";
    public static final String LATEST_STATE_CHANGE_ATTRIBUTE = "latestStateChange";
    public static final String DESCRIPTION_ATTRIBUTE = "description";
    public static final String OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE = "operationalSecurityLevel";
    public static final SecurityLevel OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_VALUE = SecurityLevel.LEVEL_1;
    public static final String OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_VALUE_2 = "LEVEL_2";
    public static final String LATEST_STATE_CHANGE_ATTRIBUTE_VALUE = "0";
    public static final String LATEST_STATE_CHANGE_ATTRIBUTE_VALUE_2 = "1";
    public static final String FILE_TRANSFER_CLIENT_MODE = "fileTransferClientMode";
    public static final String FILE_TRANSFER_CLIENT_MODE_VALUE = "FTP";
    public static final String DESCRIPTION_ATTRIBUTE_VALUE = "abc";
    public static final String DESCRIPTION_ATTRIBUTE_VALUE_2 = "abcdefghijklmnop";
    public static final String STATE_ATTRIBUTE = "state";
    public static final String STATE_ATTRIBUTE_VALUE = "DEACTIVATED";
    public static final String STATE_ATTRIBUTE_VALUE_2 = "ACTIVATED";
    public static final String ACTIVE_USER_PROFILE_COMPLEX_ATTRIBUTE = "activeUserDefProfilesInfo";
    public static final String USER_LABEL_ATTRIBUTE = "userLabel";
    public static final String USER_LABEL_ATTRIBUTE_VALUE = "SystemFunctions user label";
    public static final String USER_LABEL_SECURITY_ATTRIBUTE_VALUE = "Security user label";
    public static final String USER_LABEL_SECURITY_ATTRIBUTE_VALUE_2 = "Security user label1";
    public static final String IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE = "serialNumber";
    public static final String IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE = "12345";
    public static final String IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE = "subjectAltName";
    public static final String IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE = "test_subject";
    public static final String IP_SEC_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE = "notValidAfter";
    public static final String IP_SEC_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE = "20141106210627";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE = "subject";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE = "CN=subject_0";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE = "issuer";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE = "CN=issuer_0";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE = "serialNumber";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE = "1234554321";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE = "subjectAltName";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE = "test_subject";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE = "notValidAfter";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE = "20141106210627";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE = "subject";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE = "CN=subject_1";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE = "issuer";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE = "CN=issuer_1";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE = "serialNumber";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE = "5432112345";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE = "subjectAltName";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE = "test_subject_1";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE = "notValidAfter";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE = "20141201221111";

    public static final String CPP_ACTIVATE_SL_2 = WorkflowNames.WORKFLOW_CPPActivateSL2.toString(); //"CPPActivateSL2";

    public static final String NETWORK_ELEMENT_SEC_ROOT_USER_NAME = "rootuser";
    public static final String NETWORK_ELEMENT_SEC_ROOT_USER_PASSWORD = "pasw2349";
    public static final String NETWORK_ELEMENT_SEC_SECURE_USER_PASSWORD = "439oi23";
    public static final String NETWORK_ELEMENT_SEC_SECURE_USER_NAME = "secureuser";
    public static final String NETWORK_ELEMENT_SEC_NORMAL_USER_PASSWORD = "097234";
    public static final String NETWORK_ELEMENT_SEC_NORMAL_USER_NAME = "normaluser";
    public static final String NETWORK_ELEMENT_SEC_ID = "1";
    public static final List<String> NETWORK_ELEMENT_SEC_TARGET_GROUPS = Arrays.asList("defaultTargetGroup");
    public static final String NETWORK_ELEMENT_SEC_NAME = "1";
    public static final String NETWORK_ELEMENT_VERSION = "2.0.0";
    public static final String NETWORK_ELEMENT_SEC_VERSION = "4.1.0";
    public static final String NETWORK_ELEMENT_OSS_MODEL_IDENTITY_VERSION = "20.Q1-J.4.606"; // 6607-651-025"; //"3560-414-071"; //"397-5538-122";
    public static final String NETWORK_ELEMENT_OSS_MODEL_IDENTITY_RADIO_VERSION = "17A-R2YX"; //"17A-R2GB";
    public static final String NETWORK_ELEMENT_SEC_ENROLLMENT_MODE = EnrollmentMode.SCEP.toString();
    public static final String NETWORK_ELEMENT_SEC_ALGORITHM_KEY_SIZE = AlgorithmKeys.RSA_1024.toString();
    public static final String NETWORK_ELEMENT_SEC_PUBLIC_KEY = "";
    public static final String NETWORK_ELEMENT_SEC_PRIVATE_KEY = "";

    public static final String CERT_ENROLL_STATE = "certEnrollState";

    public static final String SEC_M = "SecM";
    public static final String MANDATORY_SEC_M_ATTRIBUTE = "secMId";
    public static final String MANDATORY_SEC_M_ATTRIBUTE_VALUE = "1";

    public static final String CERT_M = "CertM";
    public static final String MANDATORY_CERT_M_ATTRIBUTE = "certMId";
    public static final String MANDATORY_CERT_M_ATTRIBUTE_VALUE = "1";

    public static final String CERT_M_CAPABILITIES = "CertMCapabilities";
    public static final String MANDATORY_CERT_M_CAPABILITIES_ATTRIBUTE = "certMCapabilitiesId";
    public static final String MANDATORY_CERT_M_CAPABILITIES_ATTRIBUTE_VALUE = "1";
    public static final String ENROLLMENT_SUPPORT_ATTRIBUTE = "certMCapabilitiesId";
    public static final String FINGERPRINT_SUPPORT_ATTRIBUTE = "fingerprintSupport";

    public static final String NETWORK_ELEMENT_CPP_OSS_MODEL_IDENTITY_VERSION = "17B-H.1.220";
    public static final String RCS_SEC_M_NS = "RcsSecM";

    public static final String ERBS_TARGET_TYPE = "ERBS";
    public static final String RADIO_TARGET_TYPE = "RadioNode";

}
