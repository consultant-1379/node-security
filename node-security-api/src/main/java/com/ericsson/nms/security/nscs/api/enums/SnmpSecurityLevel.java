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
package com.ericsson.nms.security.nscs.api.enums;

public enum SnmpSecurityLevel {
    NO_AUTH_NO_PRIV("NO_AUTH_NO_PRIV"),
    AUTH_NO_PRIV("AUTH_NO_PRIV"),
    AUTH_PRIV("AUTH_PRIV");

    private String securityLevel;

    private SnmpSecurityLevel(final String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getSnmpSecurityLevel() {
        return this.securityLevel;
    }
}
