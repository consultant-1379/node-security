/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.util;

import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;

/**
 * This class hold the constant values related to NTP list command
 *
 * @author 1639556
 *
 */
public class NtpConstants {

    private NtpConstants() {
        super();
    }

    public static final String UNDEFINED_NTP_SERVICE_STATUS = "undefined";
    public static final String NODE_NAME_HEADER = "Node Name";
    public static final String SERVICE_STATUS_HEADER = "Service Status";
    public static final String KEY_ID = "keyId";
    public static final String INSTALLED_NTP_KEY_IDS = "installedNtpKeyIds";
    public static final String NTP_USER_LABEL = "User Label";
    public static final String NTP_SERVER_ID = "Server Id";
    public static final String SERVER_ADDRESS_HEADER = "Server Address";
    public static final String ERROR_DETAILS_HEADER = "Error Details";
    public static final String NA = "NA";
    public static final String NTP_FEATURE_SUPPORTED_NODE_TYPE_SUGGESTED_SOLUTION = "Check online help for ntp list supported node types/versions.";
    public static final String SUGGESTED_SOLUTION = "Suggested Solution";
    public static final String SERVICE_STATUS = "serviceStatus";
    public static final String ENM_HOST_ID = "web_host_default";
    public static final String NTP_DETAILS_MSG = "Unsupported Node Type/Version";
    public static final String NTP_SUGGESTED_SOLUTION = NTP_FEATURE_SUPPORTED_NODE_TYPE_SUGGESTED_SOLUTION;
    public static final String NTP_SERVER_ERR_MSG = "No associated NTPServer configuration is found";
    public static final String NTP_SERVER_SOLUTION = "Run secadm ntp remove command to remove ntp key id on node";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String INSTALLED_NTP_KEYIDS_DOESNOT_EXISTS = "Node does not have installedNtpKeyIds attribute under TimeSetting MO";

    public static final String NTP_SERVICE_GET_KEY_DATA_URL = "http://dhcp-service:8080/ntp-service/ntpkeys/fetch/key";
    public static final String NTP_SERVICE_STATUS_URL = "http://dhcp-service:8080/ntp-service/ntpkeys/configure/status";
    public static final String ITSERVICES_IPV4_IP_ADDR_PROPERTY = "itservices_service_IPs";
    public static final String ITSERVICES_IPV6_IP_ADDR_PROPERTY = "itservices_service_IPv6_IPs";
    public static final String NTP_SERVER_ENM_USER_LABEL = "ENM_Internal_NTP";
    public static final String WEB_HOST_DEFAULT_PROPERTY = "web_host_default";

    public static final String CPP_NTP_SERVER = "NtpServer";
    public static final String CPP_USER_LABEL = "userLabel";
    public static final String CPP_NTP_KEY_ID = "ntpKeyId";
    public static final String CPP_NTP_SERVER_ID = "NtpServerId";
    public static final int CPP_MAX_NTP_KEYS = 10;
    public static final int CPP_POLL_TIMES = 4;
    public static final int CPP_MAX_NTP_SERVERS = 10;
    public static final String NTP_CONFIG_EXECUTED = "Successfully started a job to configure NTP server details on the given node(s)";
    public static final String NTP_CONFIG_PARTIALLY_EXECUTED = "Successfully started a job to configure NTP server details on some nodes. Perform 'secadm job get -j %s' to get progress information.";
    public static final String NTP_CONFIG_NOT_EXECUTED = "NTP configure command has been failed to execute on all the nodes. Error details are listed below:";
    public static final String NTP_REMOVE_EXECUTED = "Successfully started a job to remove NTP server details on the given node(s)";
    public static final String NTP_REMOVE_PARTIALLY_EXECUTED = "Successfully started a job to remove NTP server details on some nodes. Perform 'secadm job get -j %s' to get progress information.";
    public static final String NTP_REMOVE_NOT_EXECUTED = "NTP remove command has been failed to execute on all the nodes. Error details are listed below:";

    public static final String SUPPORTED_KEY_ALGO_DOESNOT_EXISTS = "Node does not have supportedKeyAlgorithm attribute under Ntp MO";
    public static final String COLON_SEPARATOR = " : ";
    public static final String NOT_SUPPOERTED = "Not Supported";
    public static final int POLL_TIMES = 5;
    public static final String NTP_SERVICE_REMOVE_MAPPING = "http://dhcp-service:8080/ntp-service/ntpkeys/key/clearmapping";
    public static final String ADMINISTRATIVE_STATE_UNLOCKED = "UNLOCKED";
    public static final String CPP_MAX_NTP_KEYS_EXCEEDED_EXCEP_MSG = "Maximum Ntp keys limit exceeded on node.";
    public static final String CPP_MAX_NTP_SERVERS_EXCEEDED_EXCEP_MSG = "Maximum Ntp servers limit exceeded on node.";
    public static final Mo CPP_NTP_SERVER_MO = Model.ME_CONTEXT.managedElement.systemFunctions.timeSetting.ntpServer;
    public static final Mo COM_NTP_SERVER_MO = Model.ME_CONTEXT.comManagedElement.systemFunctions.sysM.timeM.ntp.ntpServer;
    public static final Mo COM_NTP_SECURITY_POLICY_MO = Model.ME_CONTEXT.comManagedElement.systemFunctions.sysM.timeM.ntp.ntpSecurityPolicy;
}
