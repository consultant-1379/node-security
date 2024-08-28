package com.ericsson.nms.security.nscs.api.exception;

/**
 * Class to Encapsulate all NodeSecurity Error Codes and their Suggested Solutions
 */
public class NscsErrorCodes {
    public static final String PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX = "Please check Online Help for correct syntax.";
    public static final String SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS = "An error occurred while executing the command on the system. Consult the error and command logs for more information.";
    public static final String SYNTAX_ERROR = "Command syntax error";
    public static final String THERE_ARE_ISSUES_WITH_MORE_THAN_ONE_OF_THE_NODES_SPECIFIED = "There are issues with more than one of the nodes specified";
    public static final String UNEXPECTED_INTERNAL_ERROR = "Unexpected Internal Error";
    public static final String SSO_NOT_SUPPORTED_FOR_THE_NODETYPE = "SSO is not supported for one or more node types";
    public static final String THE_NODE_SPECIFIED_DOES_NOT_EXIST = "The node specified does not exist";
    public static final String THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST = "The NetworkElement specified does not exist";
    public static final String PLEASE_SPECIFY_A_VALID_NETWORK_ELEMENT_THAT_EXISTS_IN_THE_SYSTEM = "Please specify a valid NetworkElement that exists in the system.";
    public static final String THE_CONTENTS_OF_THE_FILE_PROVIDED_ARE_NOT_IN_THE_CORRECT_FORMAT = "The contents of the file provided are not in the correct format";
    public static final String PLEASE_SEE_THE_ONLINE_HELP_FOR_THE_CORRECT_FORMAT_OF_THE_CONTENTS_OF_THE_FILE = "Please see the Online Help for the correct format of the contents of the file.";
    public static final String INVALID_TARGET_GROUP_ERROR = "Invalid target group error";
    public static final String TARGET_GROUP = "target group";
    public static final String PROVIDE_EXISTING_TARGET_GROUPS_ONLY = "Provide existing target groups only.";
    public static final String MANDATORY_ATTRIBUTES_NOT_SPECIFIED_IN_THE_COMMAND = "Mandatory attributes not specified in the command";
    public static final String PLEASE_SPECIFY_MANDATORY_ATTRIBUTE_S = "Please specify mandatory attribute(s).";
    public static final String NETWORK_ELEMENT_NOT_FOUND_FOR_THIS_MECONTEXT = "The NetworkElement MO does not exist for the associated MeContext MO";
    public static final String MECONTEXT_NOT_FOUND = "The MeContext MO does not exist for the associated NetworkElement MO";
    public static final String CREATE_A_NETWORK_ELEMENT_MO_ASSOCIATED_TO_THIS_MECONTEXT = "Please create the NetworkElement MO and any other required MOs for the associated MeContext MO.";
    public static final String CREDENTIALS_ALREADY_EXIST_FOR_THE_NODE_SPECIFIED = "The node specified already has credentials defined";
    public static final String PLEASE_SPECIFY_NODES_WITHOUT_EXISTING_CREDENTIALS_DEFINED = "Please specify nodes without existing credentials defined.";
    public static final String PLEASE_CREATE_CREDENTIALS_FOR_THE_NODE = "Please create credentials for the node (secadm credentials create...) and re-run command.";
    public static final String CREDENTIALS_DO_NOT_EXIST_FOR_THE_NODE_SPECIFIED = "The node specified requires the node credentials to be defined";
    public static final String UNSUCCESSFUL_NODE_CREDENTIALS_CREATE = "Credentials creation operation has been unsuccessful.";
    public static final String THE_NODE_SPECIFIED_IS_NOT_SYNCHRONIZED = "The node specified is not synchronized";
    public static final String ALARM_SUPERVISION_NOT_ENABLED_ON_NODE = "Alarm Supervision is not enabled on specified node(s)";
    public static final String PLEASE_ENSURE_THE_NODE_SPECIFIED_IS_SYNCHRONIZED = "Please ensure the node specified is synchronized.";
    public static final String PLEASE_ENSURE_ENTITY_HAS_VALID_CATEGORY = "Please ensure the entity has valid category.";
    public static final String ENABLE_ALARM_SUPERVISION_ON_NODE = "Ensure the alarm supervision is enabled on the Node.";
    public static final String ELEMENT = "Element";
    public static final String UPDATE_FAILED = "Update failed.";
    public static final String NODE = "Node";
    public static final String PLEASE_CREATE_THE_ME_CONTEXT_CORRESPONDING_TO_THE_SPECIFIED_MO = "Please create the MeContext corresponding to the specified MO.";
    public static final String UNSUPPORTED_COMMAND_ARGUMENT = "Unsupported command argument";
    public static final String INSTALL_LAAD_ERROR = "One or more NEs entered are not at security level 3. Operation cancelled, Validate NEs provided using secadm cpp-get-securitylevel and re-enter command.";
    public static final String THE_SPECIFIED_SECURITY_LEVEL_IS_NOT_SUPPORTED = "The specified security level is not supported";
    public static final String PLEASE_SPECIFY_A_SUPPORTED_SECURITY_LEVEL_ONLY_SECURITY_LEVEL_1_AND_2_ARE_SUPPORTED = "Please specify a supported security level;  Only Security Level 1 and 2 are supported.";
    public static final String NODE_LIST_CANNOT_BE_STAR = "Node list cannot be '*'";
    public static final String PLEASE_SPECIFY_NODES_USING_NODELIST_OR_NODEFILE = "Please specify nodes using --nodelist or --nodefile.";
    public static final String DUPLICATE_NODE_NAMES = "The list of nodes specified contains duplicates";
    public static final String PLEASE_REMOVE_DUPLICATES_FROM_NODE_LIST = "Please remove the duplicates and re-run command.";
    public static final String SECURITY_FUNCTION_NOT_FOUND_FOR_THIS_NODE = "The SecurityFunction MO does not exist for the node specified";
    public static final String CREATE_A_SECURITY_FUNCTION_MO_ASSOCIATED_TO_THIS_NODE = "Please create the SecurityFunction MO and any other required MOs for the node.";
    public static final String PLEASE_CHECK_SUGGESTED_SOLUTION_FOR_EACH_NODE = "Please check suggested solution for each node and re-run command when these issues are addressed. Alternatively, omit these nodes from the list and re-run the command";
    public static final String REQUESTED_LEVEL_ALREADY_SET_MESSAGE = "The node specified is already at the requested security level";
    public static final String REQUESTED_LEVEL_ALREADY_SET_SOLUTION = "Please check the requested security level for the node specified.";
    public static final String THIS_IS_AN_UNEXPECTED_SYSTEM_ERROR = "This is an unexpected system error, please check the error log for more details.";
    // TODO if/when --continue is implemented
    // public static final String PLEASE_CHECK_SUGGESTED_SOLUTION_FOR_EACH_NODE
    // =
    // "Please check suggested solution for each node and re-run command when these issues are addressed. Alternatively, omit these nodes from the command or use the --continue option to ignore these nodes.";
    // public static final String REQUESTED_LEVEL_ALREADY_SET_SOLUTION =
    // "Check security level for nodes requiring to be changed or use --continue to ignore nodes already at requested security level";
    public static final String NO_NODES_WITH_SECURITY_MO_FOUND = "No nodes with Security MO found";
    public static final String NO_NODES_FOUND_AT_REQUESTED_SECURITY_LEVEL = "No nodes found at requested Security Level";
    public static final String NODE_IS_IN_ONGOING_CONFIGURATION_CHANGE = "The node specified is already involved in a security configuration change";
    public static final String PLEASE_WAIT_UNTIL_CURRENT_ACTION_COMPLETE = "Please wait until the configuration change is completed and rerun command ";
    public static final String INVALID_FILE_CONTENT = "File content is not valid.";
    public static final String INVALID_INPUT_XML_FILE = "Input xml validation with Schema failed.";
    public static final String PLEASE_PROVIDE_VALID_INPUT_ENCODING = "Please provide the XML file with UTF-8 character encoding. For details please check Online Help.";
    public static final String PLEASE_PROVIDE_VALID_INPUT = "Please provide the valid XML. For details please check Online Help.";
    public static final String INVALID_ENCODING = "Invalid character encoding of XML file. Encoding type is not UTF-8.";
    public static final String XML_VALIDATION_FAILED = "Validation of input XML file with XSD failed.";
    public static final String INVALID_INPUT_NODE_LIST_FOR_COMMAND = "Some of the input node attributes are missing or invalid.";
    public static final String REQUESTED_ENTITY_PROFILE_NAME_DOES_NOT_EXIST = "Requested EntityProfileName does not exist.";
    public static final String REQUESTED_ENTITY_PROFILE_NAME_EXC = "Requested EntityProfileName returned exception.";
    public static final String DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST = "Default EntityProfileName does not exist.";
    public static final String REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE = "Requested AlgorithmKeySize is not supported for this node. ";
    public static final String SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY = "For CertificateType IPSEC subjectAltName and subjAltNameType can't be empty.";
    public static final String REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED = "Requested SubjectAlternativeNameType is not supported. ";
    public static final String REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID = "Requested SubjectAlternativeName is invalid. ";
    public static final String PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT = "Please specify a valid SubjectAlternativeName format.";
    public static final String SETTING_FILE_GENERATION_FAILED = "Generation of IpForOamSetting file failed.";
    public static final String SUMMARY_FILE_GENERATION_FAILED = "Generation of Summary file failed.";
    public static final String INVALID_ALGORITHM = "Invalid algorithm used for hashing.";
    public static final String IP_SEC_ACTION_ERROR = "IPSec activate/deactivate operation is failed.";
    public static final String REQUESTED_ENTITY_EXC = "Requested Entity returned exception.";
    public static final String INVALID_ENROLLMENT_MODE = "Invalid enrollmentMode";

    public static final String MAX_NO_NODES_SUPPORTED = "The current maximum number of nodes supported is %d. Please specify a valid number of nodes";
    public static final String NUMBER_OF_NODES_SPECIFIED_EXCEEDS_THE_MAXIMUM = "Number of nodes specified exceeds the maximum";
    public static final String IP_SEC_NOT_FOUND_FOR_THIS_NODE = "The IpSec MO does not exist for the node specified";
    public static final String CREATE_A_IPSEC_MO_ASSOCIATED_TO_THIS_NODE = "Please create the IpSec MO and any other required MOs for the node.";

    public static final String UNSUPPORTED_NODE_TYPE = "Unsupported Node Type";
    public static final String INVALID_ARGUMENT_VALUE = "Invalid argument value";
    public static final String NO_VALID_NODE_FOUND = "No valid node is found in the system.";
    public static final String KEYPAIR_NOT_FOUND = "Key pair not found";
    public static final String KEYPAIR_ALREADY_GENERATED = "Key pair already generated";
    public static final String INVALID_SSH_KEY_GENERATED = "Invalid ssh key generated";
    public static final String CERTIFICATE_ISSUE_WF_FAILED = "Certificate issue workflow is failed";
    public static final String TRUST_DISTRIBUTE_WF_FAILED = "Trust distribute workflow is failed";
    public static final String TRUST_REMOVE_WF_FAILED = "Trust remove workflow is failed";
    public static final String CERTIFICATE_REISSUE_WF_FAILED = "Certificate reissue workflow is failed";
    public static final String NODE_NOT_CERTIFIABLE = "Cannot generate a certificate for this kind of node";
    public static final String SPECIFY_A_CERTFIABLE_NODE = "Please specify a certifiable node.";
    public static final String RELAUNCH_CONTINUE = "Relaunch the command with option --continue";
    public static final String INVALID_ENTITY_CATEGORY = "Invalid entity category";
    public static final String INVALID_WANTED_MO = "Invalid wantedMo {} for node {} with certificate type {}";
    public static final String NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY = "Can't find nodes with entities with specified category.";

    public static final String CHECK_OLH_FOR_SUPPORTED_CERT_TYPES = "Check online help for the supported certificate types.";
    public static final String CHECK_OLH_FOR_SUPPORTED_TRUST_CATEGORY_TYPES = "Check online help for the supported trust category types.";

    public static final String KEYGEN_HANDLER_ERROR = "Key generation failed. Workflow cannot be invoked";
    public static final String IMPORT_NODE_SSH_PRIVATE_KEY_HANDLER_ERROR = "Node Ssh Private Key import failed";
    public static final String VALID_NODES_NORMALIZABLE_NODES_MISMATCH = "Valid node list size mismatch normalizable node list size";
    public static final String COULD_NOT_READ_MO_ATTRIBUTES = "Could not read Mo Attribute";
    public static final String ENTITY_FOR_NODE_NOT_FOUND = "There are no entities related to the specified node.";
    public static final String ENTITY_WITH_VALID_CATEGORY_NOT_FOUND = "Entity has not a valid category.";
    public static final String ENTITY_WITH_ACTIVE_CERTIFICATE_NOT_FOUND = "Entity has not an active certificate.";
    public static final String UNSUPPORTED_CERTIFICATE_TYPE = "Unsupported Certificate Type";
    public static final String UNSUPPORTED_TRUST_CATEGORY_TYPE = "Unsupported Trust Category Type";

    public static final String PLEASE_ENSURE_THE_ALARMSUPERVISION_SET_TRUE = "Please ensure alarmSupervisionClient is set to true for the node";
    public static final String THE_NODE_SPECIFIED_IS_NOT_SUPERVISED = "The node specified is not alarmSupervised";

    public static final String LDAP_CONFIGURATION_ERROR = "Ldap Configuration operation is failed.";
    public static final String LDAP_PROXY_ACCOUNT_CREATION_FAILED = "Ldap Proxy Account creation is failed.";
    public static final String LDAP_PROXY_ACCOUNT_DELETION_FAILED = "Ldap Proxy Account deletion is failed.";
    public static final String OPERATION_WITH_SOME_INVALID_NODES_FORMAT = "%s Some input nodes are invalid, see error details in following table:";
    public static final String OPERATION_WITH_ALL_INVALID_NODES_FORMAT = "%s All input nodes are invalid, see error details in following table:";

    public static final String LDAP_PROXY_ERROR = "Ldap Proxy operation is failed";

    public static final String PLEASE_PROVIDE_VALID_JOB_ID = "Please provide a correct job id.";
    public static final String PLEASE_PROVIDE_VALID_FILE_NAME = "Please provide a valid filename.";
    public static final String SECURITY_VIOLATION_SUGGESTED_SOLUTION = "Make sure you have the requested rights.";
    public static final String SECURITY_VIOLATION_EXCEPTION_MESSAGE = "Security violation exception.";

    public static final String NTP_DETAILS_MSG = "Unsupported Node Type/Version";
    public static final String NTP_SUGGESTED_SOLUTION = "Check online help for ntp supported node types/versions.";

    public static final String UNSUPPORTED_NODE_RELEASE_VERSION = "Unsupported Node Release Version";
    public static final String TRUST_CATEGORY_MO_DOES_NOT_EXIST = "Trust Category MO does not exist for the given node.";
    public static final String SECURITY_MO_DOES_NOT_EXIST = "Security MO does not exist for the given node.";
    public static final String ISSUE_CERT_FOR_TRUST_CATEGORY_MO = "Perform Online certificate Enrollment on the node for TrustCategory MO to be present.";
    public static final String ISSUE_CERT_FOR_SECURITY_MO = "Issue certificate to the node for Security MO to be present.";
    public static final String USE_VALID_NODE_RELEASE_VERSION = "Use a node with supported node release version. Please check Online Help for supported release versions.";
    public static final String CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED = "Crl Check enable or disable workflow is failed.";
    public static final String SPECIFY_A_VALID_NODE = "Please specify a valid node that exists in the system.";
    public static final String ON_DEMAND_CRL_DOWNLOAD_WF_FAILED = "On demand Crl download workflow is failed.";
    public static final String SET_CIPHERS_WF_FAILED = "Set Ciphers workflow has failed.";
    public static final String SPECIFY_A_VALID_NODE_THAT_HAS_TLS_MO = "Please specify a valid node that has Tls MO.";
    public static final String SPECIFY_A_VALID_NODE_THAT_HAS_SSH_MO = "Please specify a valid node that has Ssh MO.";
    public static final String SSH_MO_DOES_NOT_EXIST = "Ssh MO does not exist for the given node.";
    public static final String TLS_MO_DOES_NOT_EXIST = "Tls MO does not exist for the given node.";
    public static final String UNSUPPORTED_ENROLLMENT_MODE = "Unsupported Enrollment Mode. Supported Enrollment Modes are :";
    public static final String SPECIFY_A_SUPPORTED_ALGORITHM = "Provided input algorithm(s) are not supported on the node(s) supported algorithms list. Check online help for the list of supported algorithms on the node(s) using Get Ciphers command.";
    public static final String UNSUPPORTED_ALGORITHM = "Unsupported Algorithm(s)";
    public static final String CIPHERS_CONFIG_DUPLICATE_NODE_NAMES = "Duplicate Node(s)";
    public static final String CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_NOT_ALLOWED = "Remove duplicate nodes based on the protocol types.";
    public static final String CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_SSH_TLS = "Duplicate Node Name found for sshProtocol and tlsProtocol ciphers.";
    public static final String CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_SSH = "Duplicate Node Name found for sshProtocol ciphers.";
    public static final String CIPHERS_CONFIG_DUPLICATE_PROTOCOL_TYPES_TLS = "Duplicate Node Name found for tlsProtocol ciphers.";
    public static final String CIPHERS_CONFIG_INVALID_ARGUMENT_VALUE = "Invalid Argument Value";
    public static final String INVALID_INPUT_VALUE = "Invalid Input Value";
    public static final String INVALID_CIPHERFILTER_VALUE = "Invalid cipherfilter value.";
    public static final String REFER_TO_ONLINE_HELP_TO_BUILD_PROPER_CIPHER_FILTER = "Refer to Online Help to build proper cipher filter.";
    public static final String REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE_RELEASE_VERSION = "Refer to Online Help for supported release versions.";
    public static final String REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE = "Refer to Online Help for supported nodes.";
    public static final String REFER_TO_ONLINE_HELP_FOR_SYNTAX = "Refer to Online Help for correct syntax.";
    public static final String INVALID_XML = "Invalid XML";
    public static final String XML_SCHEMA_VALIDATIONS_FAILED = "XML schema validation failed";
    public static final String REFER_TO_ONLINE_HELP_FOR_VALID_XML_SCHEMA = "Refer to Online Help for valid XML schema.";
    public static final String SPECIFY_VALID_CIPHERS = "Provide valid ciphers.";
    public static final String EMPTY_CIPHERFILTER_VALUE = "Found empty cipher filter.";
    public static final String PROVIDE_VALID_CIPHERFILTER = "Provide valid cipher filter.";
    public static final String RTSEL_WF_FAILED = "Rtsel workflow has failed.";
    public static final String RTSEL_CONFIG_DUPLICATE_NODE_NAMES = "Duplicate Node(s)";
    public static final String RTSEL_CONFIG_DUPLICATE_NOT_ALLOWED = "Remove duplicate nodes from nodeFdns tag of input XML file.";
    public static final String RTSEL_CONFIG_DUPLICATE_NODE_FDN = "Duplicate Node Name found in the nodeFdns tag of input XMl file.";
    public static final String NTP_KEY_NOT_FOUND = "Unable to find the NTP Key in ntp server.";
    public static final String NTP_KEY_MAPPING_NOT_FOUND_EXCEPTION = "No NTP Key Mapping exists for the given node in ntp server.";

    public static final String HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED = "HTTPS Activate or Deactivate workflow is failed.";
    public static final String GET_HTTPS_WF_FAILED = "Get Https status for unsynchronized nodes workflow is failed.";
    public static final String SUPPORTED_LOG_LEVELS = "Supported Log Levels are";
    public static final String SUPPORTED_PROTOCOLS = "Supported Protocols are";
    public static final String INVALID_ARGUMENT_VALUES = "Input values provided in the XML is invalid.Please check the xml and provide valid values.";
    public static final String INVALID_EXTERNAL_SERVER_PROTOCOL = "External server protocol provided in the input XML is invalid, %s.";
    public static final String INVALID_EXTERNAL_SERVER_LOGLEVEL = "External server log level provided in the input XML is invalid, %s.";

    public static final String INVALID_EXTERNAL_SERVER_ADDRESS = "External Syslog server address does not belong to IPv4 Or IPv6 Or DNS";
    public static final String DUPLICATE_SERVER_NAME = "Duplicate Server name %s is found in XML file for the Nodes";
    public static final String FAILED_TO_CONFIGURE_EXT_SERVER = "Error Occured while configuring ExternalServer";
    public static final String SERVER_NAME_EMPTY = "Server Name cannot be empty";
    public static final String RTSEL_SERVER_NAMES_NOT_FOUND_TO_DELETE = "ServerName(s) %s is/are not configured on the node to delete.";
    public static final String RTSEL_DELETE_SERVER_NAMES_NOT_VALID = "Please provide valid ServerName(s) to delete on the node.";
    public static final String RTSEL_DELETE_SERVER_NAMES_NOT_FOUND = "ServerName(s) not found";
    public static final String RTSEL_DELETE_SERVER_DUPLICATE_NOT_ALLOWED = "Remove duplicate nodes from nodeFdn tag of input XML file.";
    public static final String RTSEL_DELETE_SERVER_DUPLICATE_NODE_FDN = "Duplicate Node name found in the nodeFdn tag of input XML file.";
    public static final String RTSEL_DELETE_SERVER_NAMES_ON_NODE_FAILED = "Error occured while deleting ServerName(s) {} on the node.";
    public static final String ENROLLMENT_INFO_ERROR = "Error Occured while generating enrollment information file.";
    public static final String UNSUPPORTED_NODE = "Operation not allowed for this node type";
    public static final String FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED = "FTPES Activate or Deactivate workflow is failed.";
    public static final String INVALID_CONN_ATTEMPT_TIME_OUT = "Connection attempt time out value provided in the input XML is invalid.";
    public static final String DATABASE_UNAVAILABLE = "Database is unavailable.";
    public static final String INVALID_SAVED_SEARCH_NAME = "Invalid saved search name(s). The specified name(s) %s does not exist or cannot be accessed by user.";
    public static final String INVALID_COLLECTION_NAME = "Invalid collection name(s). The specified name(s) %s does not exist or cannot be accessed by user.";
    public static final String INVALID_NODE_NAME_EXPRESSION = "Invalid node name expression(s). The specified expression(s) %s can not be resolved to any node name(s).";
    public static final String PLEASE_SPECIFY_A_VALID_SAVED_SEARCH_NAME = "Please specify a valid saved search name(s).";
    public static final String PLEASE_SPECIFY_A_VALID_COLLECTION_NAME = "Please specify a valid collection name(s).";
    public static final String PLEASE_SPECIFY_A_VALID_NODE_NAME_EXPRESSION = "Please specify a valid node name expression(s).";
    public static final String NODE_FILE_MUST_NOT_CONTAIN_STAR = "Node File must not contain *";
    public static final String PLEASE_SPECIFY_VALID_NODE_NAMES_IN_NODEFILE = "Please specify valid node names in nodefile.";
    public static final String UNAUTHORISED_ACCESS_FOR_SAVED_SEARCH_NAME = "The specified saved search name(s) %s cannot be accessed by user.";
    public static final String UNAUTHORISED_ACCESS_FOR_COLLECTION_NAME = "The specified collection name(s) %s cannot be accessed by user.";
    public static final String EMPTY_SET_FOR_SAVED_SEARCH_OR_COLLECTION = "The specified name(s) %s returns empty set . 0 Node instances found.";
    public static final String INVALID_SYNTAX_FOR_SAVED_SEARCH_NAME = "Invalid saved search name(s). The specified name(s) %s is not in correct syntax.";
    public static final String INVALID_SYNTAX_FOR_COLLECTION_NAME = "Invalid collection name(s). The specified name(s) %s is not in correct syntax.";
    public static final String INVALID_FILE_TYPE_NOT_TXT = "Unsupported File Type. Only files with txt extension are allowed.";
    public static final String INVALID_FILE_TYPE_NOT_XML = "Unsupported File Type. Only files with xml extension are allowed.";
    public static final String ALARM_SUPERVISION_ENABLE_SUGGESTED_SOLUTION = "Check online help for enabling alarm supervision on the nodes.";
    public static final String LAAD_FILES_DISTRIBUTION_WF_FAILED = "Laad Files Distribution Workflow has failed.";
    public static final String INVALID_FILE_NOT_TXT_TYPE = "Unsupported File Type. Only file with txt extension is allowed.";
    public static final String CHECK_ONLINE_HELP_FOR_VALID_TXT_FILE_TEMPLATE = "Check online help for valid text file template.";
    public static final String IPSEC_TRUST_DISTRIBUTION_IS_ALLOWED_FOR_EXTERNAL_CA = "The Trust Distribution with ExternalCA is allowed only when certType is IPSEC";
    public static final String INVALID_COMMAND_FOR_EXTERNAL_CA = "xml file and --extca is mandatory for External CA trust distribution";
    public static final String KEY_ALGORITHM_NOT_SUPPORTED_BY_ENTITY_PROFILE = "The given Key Algorithm [{}] is not in supported list of Entity Profile [{}]. Accepted Key Algorithms are {}";
    public static final String NTP_CONFIGURE_OR_REMOVE_WF_FAILED = "NTP Configure or Remove workflow is failed.";
    public static final String EMTPY_FILE_ERROR = "Empty input XML file";
    public static final String UNSUPPORTED_FILE_TYPE = "Unsupported File Type.";
    public static final String SUPPORTED_FILE_TYPE = "Supported File Type: ";
    public static final String INVALID_FILE_NAME = "Invalid File Name: ";
    public static final String EXAMPLE_NODE_SSH_PRIVATE_KEY_FILE_NAME = "nodeSshPrivateKeyFile.txt";
    public static final String LDAP_CONFIGURE_WF_FAILED = "LDAP configure workflow is failed.";
    public static final String TOO_MANY_CHILD_MOS = "Too many child MOs";
    public static final String PLATFORM_CONFIGURATION_UNAVAILABLE = "Platform configuration Unavailable";
    public static final String BAD_REQUEST = "Bad request";
    public static final String PLEASE_PROVIDE_VALID_INPUT_PARAMETERS = "Please provide valid input parameters.";
    public static final String PLEASE_PROVIDE_VALID_UUID = "Please provide a valid UUID.";
    public static final String PLEASE_PROVIDE_COMMA_SEPARATED_UUID_LIST = "Please provide a comma-separated list of valid UUIDs.";
    public static final String ALLOWED_VALUES = "Allowed value(s): ";
    public static final String MO_TYPE_NOT_FOUND = "MO type not found";
    public static final String PLEASE_CHECK_NSCS_CAPABILITY_MODEL_FOR_UNSUPPORTED_SECADM_CLI_COMMAND = "Please check Node Security Capability Model, this secadm command is defined as unsupported for this target.";

    public static final String GENERATE_ENROLLMENT_INFO_ERROR = "Generate enrollment info is failed";

    public static final String IPSEC_CONFIGURE_WF_FAILED = "IPSEC configure workflow is failed.";

    public static final String SSH_KEY_WF_FAILED = "SSH KEY workflow is failed.";

    public static final String INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO = "Invalid algorithmAndKeySize in NetworkElementSecurity MO";
    public static final String PLEASE_PERFORM_SSHKEY_UPDATE_WITH_VALID_ALGO_TYPE_SIZE = "Please perform sshkey update command specifying a valid algorithm-type-size. Check online help for more details.";

}
