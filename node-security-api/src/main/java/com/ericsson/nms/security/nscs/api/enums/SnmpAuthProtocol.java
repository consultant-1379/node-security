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
package com.ericsson.nms.security.nscs.api.enums;

/**
 * 
 * @author ebarmos, emelant
 */
public enum SnmpAuthProtocol {

    NONE("0"), MD5("1"), SHA1("2");

    private String authProtocol;

    private SnmpAuthProtocol(final String authProtocol) {
        this.authProtocol = authProtocol;
    }

    public String getSnmpAuthProtocol() {
        return this.authProtocol;
    }
}