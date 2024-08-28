/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.resources;

public class NscsResourcesConstants {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_NO_CONTENT = "NO_CONTENT";
    public static final String STATUS_NOT_FOUND = "NOT_FOUND";
    public static final String STATUS_GONE = "GONE";

    public static final String NODES_RESOURCE = "nodes";
    public static final String NODES_RESOURCE_DOMAINS_SUB_RESOURCE = "domains";
    public static final String NODES_RESOURCE_LDAP_SUB_RESOURCE = "ldap";

    private NscsResourcesConstants() {
        // added to hide the implicit public constructor
    }
}
