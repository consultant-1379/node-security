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
package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

/**
 * This class hold the constant values related to Set ciphers handler
 *
 * @author tcsnapa
 *
 */
public class CiphersConstants {
    public static final String PROTOCOL_TYPE_SSH = "SSH/SFTP";
    public static final String PROTOCOL_TYPE_TLS = "SSL/HTTPS/TLS";
    public static final String ENCRYPT_ALGOS = "encryptalgos";
    public static final String KEX = "keyexchangealgos";
    public static final String MACS = "macalgos";
    public static final String CIPHER_FILTER = "cipherFilter";
    public static final String SET_CIPHERS_EXECUTED = "Successfully started a job to set ciphers for node(s)";
    public static final String SET_CIPHERS_EXECUTED_DYN_ISSUE = "Successfully started a job to set ciphers for some nodes. Perform 'secadm job get -j %s' to get progress information. Error details are listed below for other nodes:";
    public static final String SET_CIPHERS_NOT_EXECUTED = "Set ciphers command has not been executed for all nodes. Error details are listed below:";
    public static final int NO_OF_COLUMNS = 3;
    public static final String SECM_MO = "SecM";
    public static final String SECURITY_MO = "Security";
    public static final String KEY_EXCHANGE_ALGORITHMS = "KEY EXCHANGE ALGORITHMS";
    public static final String ENCRYPTION_ALGORITHMS = "ENCRYPTION ALGORITHMS";
    public static final String MAC_ALGORITHMS = "MAC ALGORITHMS";
    public static final String SUPPORTED_CIPHERS = "supportedCiphers";
    public static final String ENABLED_CIPHERS = "enabledCiphers";
    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String LIST_CIPHERS_CSV_FILE = "CiphersList.csv";
    public static final String SELECTED_KEY_EXCHANGE_ALGORITHMS = "SELECTED KEY EXCHANGE ALGORITHMS";
    public static final String SELECTED_ENCRYPTION_ALGORITHMS = "SELECTED ENCRYPTION ALGORITHMS";
    public static final String SELECTED_MAC_ALGORITHMS = "SELECTED MAC ALGORITHMS";
    public static final String CIPHERS_CONFIGURATION_XSD = "CiphersConfiguration.xsd";
    public static final String EMPTY_TAG = "[<empty>]";
    public static final String EMPTY_CIPHERFILTER_TAG = "<empty>";
}
