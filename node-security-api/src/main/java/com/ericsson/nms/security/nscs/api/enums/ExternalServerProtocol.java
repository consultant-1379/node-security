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
package com.ericsson.nms.security.nscs.api.enums;

/**
 * @author tcsviga
 *
 */
public enum ExternalServerProtocol {
    TLS_OVER_TCP, UDP;

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * @param value input to translate to ExternalServerProtocol
     * @return Get the ExternalServerProtocol from the String value
     * 
     */
    public static ExternalServerProtocol getExternalServerProtocol(final String value) {
        ExternalServerProtocol externalServerProtocol = null;
        switch (value) {
        case "TLS_OVER_TCP":
            externalServerProtocol = ExternalServerProtocol.TLS_OVER_TCP;
            break;
        case "UDP":
            externalServerProtocol = ExternalServerProtocol.UDP;
            break;
        default:
            break;
        }
        return externalServerProtocol;
    }
}
