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
package com.ericsson.nms.security.nscs.rtsel.utility;

/**
 * This class hold the constant values related to the ActivateRtselHandler
 */

public class RtselConstants {

    public static final String RTSEL_XSD_VALIDATOR_FILE = "RtselConfigurationSchema.xsd";
    public static final String SUCCESS_MESSAGE = "Rtsel XML Validation is successfully completed.";
    public static final String DEACTIVATE_SUCCESS_MESSAGE = "Rtsel deactivation is successfully completed.";
    public static final String ACTIVATE_RTSEL_SUCCESS_FOR_ALL_NODES = "Successfully started a job to activate RTSEL for node(s)";
    public static final String RTSEL_DELETE_SERVER_SUCCESS_FOR_ALL_NODES = "Successfully started a job to delete RTSEL server details for node(s)";
    public static final String ACTIVATE_RTSEL_FAILED_FOR_SOME_NODES = "Successfully started job to activate RTSEL for some nodes. Perform 'secadm job get -j %s' to get progress information for these nodes. Error details for failed nodes are listed below:";
    public static final String RTSEL_DELETE_SERVER_FAILED_FOR_SOME_NODES = "Successfully started a job to delete RTSEL server details for some nodes. Perform 'secadm job get -j %s' to get progress information for these nodes. Error details for failed nodes are listed below:";
    public static final String ACTIVATE_RTSEL_FAILED_FOR_ALL_NODES = "Activate RTSEL command has not been executed for all nodes. Error details are listed below:";
    public static final String RTSEL_DELETE_SERVER_FAILED_FOR_ALL_NODES = "Delete RTSEL server details command has not been executed for all nodes. Error details are listed below:";
    public static final String ACTIVATE_RTSEL_PARTIAL_FAILED = "Successfully started job to activate RTSEL for some nodes.";
    public static final String ACTIVATE_RTSEL_FAILED_ALL_NODES = "Activate RTSEL command has not been executed for all nodes.";
    public static final String DEACTIVATE_RTSEL_SUCCESS_FOR_ALL_NODES = "Successfully started job to deactivate RTSEL FeatureState for all valid node(s)";
    public static final String DEACTIVATE_RTSEL_FAILED_FOR_SOME_NODES = "Successfully started job to deactivate RTSEL FeatureState only for valid node(s). Perform 'secadm job get -j %s' to get progress information for these nodes. Error details for failed nodes are listed below:";
    public static final String DEACTIVATE_RTSEL_FAILED_FOR_ALL_NODES = "Deactivate RTSEL command has not been executed for any node(s). Error details are listed below:";
    public static final String DEACTIVATE_RTSEL_PARTIAL_FAILED = "Successfully started job to deactivate RTSEL FeatureState only for valid node(s).";
    public static final String DEACTIVATE_RTSEL_FAILED_ALL_NODES = "Deactivate RTSEL command has not been executed for any node(s).";
    public static final String RTSEL_DELETE_SERVER_PARTIAL_FAILED = "Successfully started job to delete RTSEL server details for some nodes.";
    public static final String RTSEL_DELETE_SERVER_FAILED_ALL_NODES = "Delete RTSEL server details command has not been executed for all nodes.";
    public static final int NO_OF_COLUMNS = 3;
    public static final String VALID_NODE_FDNS_LIST = "ValidNodeFdnsList";
    public static final String ENROLLMENT_MODE = "EnrollmentMode";
    public static final String ENTITY_PROFILE_NAME = "EntityProfileName";
    public static final String KEY_SIZE = "KeySize";
    public static final String RTSEL_DETAILS_FILE = "RtselDetails.txt";
    public static final String STATUS = "status";
    public static final String EXT_SERVER_ADDRESS = "extServerAddress";
    public static final String EXT_SERVER_PROTOCOL = "extServProtocol";
    public static final String EMPTY_STRING = "";
    public static final String SERVER_NAME_HEADER = "Server Name : ";
    public static final String ADDRESS_HEADER = "Address : ";
    public static final String PROTOCOL_HEADER = "Protocol : ";
    public static final String EXT_SERVER_LIST_CONFIG = "extServerListConfig";
    public static final String NOT_APPLICABLE = "NA";
    public static final String NODE_NAME_HEADER = "Node Name";
    public static final String SYSLOG_SERVER_HEADER = "SysLog Server";
    public static final String FEATURE_STATE_HEADER = "FeatureState";
    public static final String SERVER_LOG_LEVEL_HEADER = "Severity Log Level";
    public static final String CONNECTION_TIMEOUT_HEADER = "Connection Attempt TimeOut";
    public static final String APPLICATION_NAME_HEADER = "Application Name";
    public static final String STATUS_HEADER = "Status";
    public static final String ERROR_DETAILS_HEADER = "Error Details";
    public static final String COMMAND_EXECUTED_SUCCESSFULLY = "Command executed successfully";
    public static final int MAX_NUM_OF_SYSLOG_SERVERS = 2;
    public static final String EXT_SERVER_NAME = "serverName";
    public static final String CONN_TIMEOUT = "connAttemptTimeOut";
    public static final String EXT_SERVER_APPNAME = "extServerAppName";
    public static final String EXT_SERVER_LOGLEVEL = "extServerLogLevel";
    public static final String FEATURESTATE = "featureState";
    public static final String LICENSESTATE = "licenseState";
    public static final String FEATURESTATE_ACTIVATED = "ACTIVATED";
    public static final String FEATURESTATE_DEACTIVATED = "DEACTIVATED";
    public static final String EXT_SERVER = "extServer";
    public static final String RTSEL_DELETE_SERVER_XSD_VALIDATOR_FILE = "RtselDeleteServerConfigurationSchema.xsd";
    public static final int MIN_CONN_TIME_OUT = 10;
    public static final int MAX_CONN_TIME_OUT = 3600;
    public static final String COLON = ":";
    public static final String LT_SQUARE_BRACE = "[";
    public static final String RT_SQUARE_BRACE = "]";
    public static final String RT_SQUARE_BRACE_COLON = "]:";
    public static final String DOT = "." ;
}
