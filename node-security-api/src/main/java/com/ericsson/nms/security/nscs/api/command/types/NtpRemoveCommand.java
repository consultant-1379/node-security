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
package com.ericsson.nms.security.nscs.api.command.types;

/**
 * NtpRemoveCommand class is used to get input parameters from NTP Remove command
 *
 * @author tcsviku
 *
 */
public class NtpRemoveCommand extends NscsNodeCommand {

    private static final long serialVersionUID = -1227589443186721808L;

    public static final String XML_FILE_PROPERTY = "xmlfile";
    public static final String KEY_ID_LIST = "keyidlist";
    public static final String NODE_NAME = "nodename";
    public static final String SERVER_ID_LIST = "serveridlist";

    /**
     * * This method will return the required xml file with node-names and their respective keyId's to perform ntp remove operation.
     * 
     * @return String returns xml file property
     */
    public String getXmlInputFile() {
        return getValueString(XML_FILE_PROPERTY);
    }

    /**
     * @return the nodename
     */
    public static String getNodeName() {
        return NODE_NAME;
    }

    /**
     * @return String returns key ID to retrive from command.
     */
    public String getKeyIdList() {
        return getValueString(KEY_ID_LIST);
    }

    /**
     * @return String returns server ID to retrive from command.
     */
    public String getServerIdList() {
        return getValueString(SERVER_ID_LIST);
    }

}
