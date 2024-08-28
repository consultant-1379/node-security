/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

public class LdapConfigurationCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 1L;

    public static final String XML_FILE_PROPERTY = "xmlfile";

    /**
     * This method will return the location of input LDAP Configuration xml file.
     * 
     * @return inputFile
     */
    public String getXmlInputFile() {
        return getValueString(XML_FILE_PROPERTY);
    }

    /**
     * Convenience method to check if the given property exists in the property Map
     * 
     * @param property
     *            name of the property
     * @return true if Properties Map contains a property with the given name
     */
    public boolean hasProperty(final String property) {
        return this.getProperties().containsKey(property);
    }

}
