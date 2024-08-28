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
package com.ericsson.nms.security.nscs.integration.jee.test.command.ciphersconfig;

/**
 * This class will holds the constants used by Set ciphers and Get ciphers test classes.
 *
 */
public class CiphersConfigTestConstants {

    public static final String SYNC = "SYNCHRONIZED";
    public static final String UNSYNC = "UNSYNCHRONIZED";
    public static final String INPUT_FILE_PATH = "/ciphersconfig/FileInput.txt";
    public static final String DG2_NODE_NAME = "DG200001";
    public static final String DG2_NODE_NAME_2 = "DG200002";
    public static final String DG2_NODE_NAME_3 = "DG200003";
    public static final String DG2_NODE_NAME_4 = "DG200004";
    public static final String ERBS_NODE_NAME = "ERBS0001";
    public static final String ERBS_NODE_NAME_2 = "ERBS0002";
    public static final String ERBS_NE_NAME = "ERBS0001-NE";
    public static final String ERBS_NE_NAME_2 = "ERBS0002-NE";
    public static final String ML_INDOOR_NODE_NAME = "MINI-LINK-INDOOR_1";
    public static final String COMMAND_SECADM = "secadm";
    public static final String COMMAND_WITH_INVALID_SYNTAX = "set ciphers --protocol SSL/HTTPS --ciphers ALL:SHA256:DES:MD5 -n nodeName";
    public static final String COMMAND_WITH_INVALID_PROTOCOL_RESPONSE_MESSAGE = "Accepted arguments are [SSH/SFTP, SSL/HTTPS/TLS]";
    public static final String COMMAND_WITH_INVALID_NODE_NAME_RESPONSE_MESSAGE = "The NetworkElement specified does not exist";
    public static final String COMMAND_SYNTAX_ERROR = "10001 : Command syntax error";
    public static final String COMMAND_WITH_NODE_NOT_IN_SYNC_RESPONSE_MESSAGE = "The node specified is not synchronized";
    public static final String COMMAND_WITH_UNASSOICIATED_NETWORK_ELEMENT_RESPONSE_MESSAGE = "The MeContext MO does not exist for the associated NetworkElement MO";
    public static final String COMMAND_WITH_UNSUPPORTED_NODE_TYPE_RESPONSE_MESSAGE = "Unsupported Node Type";
    public static final String COMMAND_WITH_UNSUPPORTED_NODE_RELEASE_VERSION_RESPONSE_MESSAGE = "Unsupported Node Release Version";
    public static final String COM_ECIM_SUPPORTED_NODE_RELEASE_VERSION = "17A-R2YX";
    public static final String COM_ECIM_UNSUPPORTED_NODE_RELEASE_VERSION = "16A-R28CJ";
    public static final String CPP_SUPPORTED_NODE_RELEASE_VERSION = "19.Q2-J.2.650";
    public static final String CPP_UNSUPPORTED_NODE_RELEASE_VERSION = "17A-H.1.120";

    // set ciphers
    public static final String SET_COMMAND_WITH_FILE_INPUT = "set ciphers -pr SSL/HTTPS/TLS -cf ALL:SHA256:DES:MD5 -nf file:File.txt";
    public static final String SET_COMMAND_WITH_INVALID_PROTOCOL = "set ciphers -pr SSL/HTTPS -cf ALL:SHA256:DES:MD5 -n " + DG2_NODE_NAME;
    public static final String SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE = "Successfully started a job to set ciphers for node(s)";
    public static final String SET_CIPHERS_PARTIAL_SUCCESS_RESPONSE_MESSAGE = "Successfully started a job to set ciphers for some nodes.";
    public static final String SET_CIPHERS_TLS_COMMAND = "set ciphers -pr SSL/HTTPS/TLS -cf ALL:SHA256 -n ";
    public static final String SET_CIPHERS_TLS_WITH_INVALID_CIPHER_FILTER_COMMAND = "set ciphers -pr SSL/HTTPS/TLS -cf @12#@1 -n ";
    public static final String INVALID_CIPHER_FILTER_VALUE_RESPONSE_MESSAGE = "Invalid cipherfilter value.";
    public static final String SET_CIPHERS_SSH_COMMAND = "set ciphers -pr SSH/SFTP -enc 3des-cbc -kex ssh-rss -mac hmac-sha1 -n ";

    public static final String INPUT_XML_WITH_DUPLICATE_SSH_PROTOCOL_RESPONSE_MESSAGE = "Duplicate Node Name found for sshProtocol ciphers.";
    public static final String INPUT_XML_WITH_DUPLICATE_TLS_PROTOCOL_RESPONSE_MESSAGE = "Duplicate Node Name found for tlsProtocol ciphers";
    public static final String INPUT_XML_WITH_DUPLICATE_TLS_SSH_PROTOCOL_RESPONSE_MESSAGE = "Duplicate Node Name found for sshProtocol and tlsProtocol ciphers.";
    public static final String INVALID_INPUT_XML_RESPONSE_MESSAGE = "Invalid XML : XML schema validation failed";

    // get ciphers
    public static final String GET_COMMAND_WITH_INVALID_PROTOCOL = "get ciphers -pr SSL/HTTPS -n " + DG2_NODE_NAME;
    public static final String GET_CIPHERS_TLS_COMMAND = "get ciphers -pr SSL/HTTPS/TLS -n ";
    public static final String GET_CIPHERS_SSH_COMMAND = "get ciphers -pr SSH/SFTP -n ";
    public static final String GET_CIPHERS_RESPONSE_MESSGAE = "Command Executed Successfully";
    public static final String GET_CIPHERS_FILE_OUTPUT_RESPONSE_MESSAGE = "Ciphers file downloaded successfully.";

    // xml file input
    public static final String SET_SSH_AND_TLS_CIPHERS_FOR_SINGLE_NODE = "Scenario1.xml";
    public static final String SET_SSH_AND_TLS_CIPHERS_FOR_MULTIPLE_NODES = "Scenario2.xml";
    public static final String SET_ONLY_SSH_CIPHERS_FOR_SINGLE_NODE = "Scenario3.xml";
    public static final String SET_ONLY_TLS_CIPHERS_FOR_SINGLE_NODE = "Scenario4.xml";
    public static final String SET_ONLY_SSH_CIPHERS_FOR_MULTIPLE_NODES = "Scenario5.xml";
    public static final String SET_ONLY_TLS_CIPHERS_FOR_MULTIPLE_NODES = "Scenario6.xml";
    public static final String SET_ONLY_KEX_CIPHERS_FOR_SINGLE_NODE = "Scenario7.xml";
    public static final String SET_ONLY_MAC_CIPHERS_FOR_SINGLE_NODE = "Scenario8.xml";
    public static final String SET_ONLY_ENC_CIPHERS_FOR_SINGLE_NODE = "Scenario9.xml";
    public static final String SET_KEX_AND_MAC_CIPHERS_FOR_SINGLE_NODE = "Scenario10.xml";
    public static final String SET_KEX_AND_ENC_CIPHERS_FOR_SINGLE_NODE = "Scenario11.xml";
    public static final String SET_MAC_AND_ENC_CIPHERS_FOR_SINGLE_NODE = "Scenario12.xml";
    public static final String SET_DUPLICATE_SSH_CIPHERS_FOR_SINGLE_NODE = "Scenario13.xml";
    public static final String SET_DUPLICATE_TLS_CIPHERS_FOR_SINGLE_NODE = "Scenario14.xml";
    public static final String SET_DUPLICATE_TLS_SSH_CIPHERS_FOR_SINGLE_NODE = "Scenario15.xml";
    public static final String SET_INVALID_INPUT_XML = "Scenario16.xml";
    public static final String SET_CIPHERS_FILE_INPUT = "FileInput.txt";
    public static final String SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND = "set ciphers -xf file:File.txt";

}
