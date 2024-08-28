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
package com.ericsson.oss.services.security.nscs.nbi.logger;

/**
 * NBI REST identifier as reported in SFWK event data.
 */
public enum EventDataNbiRestIdentifier {
    V1_NODE_CREDENTIALS_PUT,
    V1_NODE_SNMP_PUT,
    V1_NODE_DOMAIN_POST,
    V1_NODE_DOMAIN_DELETE,
    V1_NODE_LDAP_POST,
    V1_NODE_LDAP_DELETE;

    private static final char BLANK_CHAR = ' ';
    private static final char UNDERSCORE_CHAR = '_';

    /**
     * Return the REST identifier as string used in SFWK event data.
     * 
     * @return the REST identifier.
     */
    public String toEventData() {
        return this.name();
    }

    /**
     * Return the REST identifier as string used in COMMAND_LOGGER and COMPACT_AUDIT_LOGGER for the operation slogan.
     * 
     * @return the REST identifier.
     */
    public String toOperationSlogan() {
        return this.name().replace(UNDERSCORE_CHAR, BLANK_CHAR);
    }
}
