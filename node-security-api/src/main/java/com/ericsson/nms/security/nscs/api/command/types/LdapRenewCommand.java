/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

public class LdapRenewCommand extends LdapConfigurationCommand {

    private static final long serialVersionUID = 7283501870832034524L;

    private static final String FORCE_PROPERTY = "force";

    /**
     * Returns if force is requested.
     * 
     * If not specified execution confirmation shall be returned.
     * 
     * @return true if force is requested.
     */
    public boolean isForce() {
        return hasProperty(FORCE_PROPERTY);
    }

}
