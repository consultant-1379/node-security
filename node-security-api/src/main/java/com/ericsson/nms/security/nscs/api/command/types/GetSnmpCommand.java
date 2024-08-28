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
package com.ericsson.nms.security.nscs.api.command.types;


/**
 * Representation of the get snmp command.
 *
 */
public class GetSnmpCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 1703846753876177424L;

    public static final String PLAIN_TEXT_PROPERTY = "plaintext";

    public static final String AUTH_PASSWD_PROPERTY = "AuthPassword";
    public static final String PRIV_PASSWD_PROPERTY = "PrivatePassword";

    public static final String PLAIN_TEXT_SHOW = "show";
    public static final String PLAIN_TEXT_HIDE = "hide";
    public static final String AUTH_PROTOCOL = "snmpAuthProtocol";
    public static final String PRIV_PROTOCOL = "snmpPrivAlgorithm";

    /**
     * @return String - The plain text option entered in the command
     */
    public String getPlainText() {
        return getValueString(PLAIN_TEXT_PROPERTY);
    }

    /**
     *
     * @return snmpPrivProtocol parameter
     */
    public String getPrivProtocol() {
        return getValueString(PRIV_PROTOCOL);
    }

    /**
     *
     * @return snmpAuthProtocol parameter
     */
    public String getAuthProtocol() {
        return getValueString(AUTH_PROTOCOL);
    }

}
