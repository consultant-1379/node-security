/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;

public class LdapProxySetCommand extends NscsPropertyCommand {

    private static final long serialVersionUID = -5883235428860891323L;

    private static final String ADMIN_STATUS_PROPERTY = "admin-status";
    private static final String XML_FILE_PROPERTY = "xmlfile";
    private static final String FORCE_PROPERTY = "force";

    /**
     * This method will return the wanted administrative status or null if property is missing.
     *
     * @return the administrative status or null if missing
     */
    public String getAdminStatus() {
        return getValueString(ADMIN_STATUS_PROPERTY);
    }

    /**
     * This method will return the location of input xml file which contains information required for ldap proxy set operation.
     *
     * @return the location of input xml file
     */
    public String getXmlFile() {
        return getValueString(XML_FILE_PROPERTY);
    }

    /**
     * Returns if force is requested.
     * 
     * If not specified execution confirmation shall be returned.
     * 
     * @return true if force is requested
     */
    public boolean isForce() {
        return hasProperty(FORCE_PROPERTY);
    }

}
