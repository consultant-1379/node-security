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
package com.ericsson.nms.security.nscs.api.credentials;

import java.io.Serializable;
import com.ericsson.nms.security.nscs.api.enums.SnmpAuthProtocol;
import com.ericsson.nms.security.nscs.api.enums.SnmpPrivProtocol;

public class SnmpV3Attributes implements Serializable {

    private static final long serialVersionUID = -4013768353177388791L;

    private SnmpAuthProtocol authProtocol;
    private String authKey;
    private SnmpPrivProtocol privProtocol;
    private String privKey;

    public SnmpV3Attributes() {
        this.authProtocol = SnmpAuthProtocol.NONE;
        this.authKey = null;
        this.privProtocol = SnmpPrivProtocol.NONE;
        this.privKey = null;
    }

    public SnmpV3Attributes(final SnmpAuthProtocol authProtocol, final String authKey) {
        this.authProtocol = authProtocol;
        this.authKey = authKey;
        this.privProtocol = SnmpPrivProtocol.NONE;
        this.privKey = null;
    }

    public SnmpV3Attributes(final SnmpAuthProtocol authProtocol, final String authKey, final SnmpPrivProtocol privProtocol, final String privKey) {
        this.authProtocol = authProtocol;
        this.authKey = authKey;
        this.privProtocol = privProtocol;
        this.privKey = privKey;
    }

    public SnmpAuthProtocol getAuthProtocolAttr() {
        return authProtocol;
    }

    public void setAuthProtocol(final SnmpAuthProtocol authProtocol) {
        this.authProtocol = authProtocol;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(final String authKey) {
        this.authKey = authKey;
    }

    public SnmpPrivProtocol getPrivProtocolAttr() {
        return privProtocol;
    }

    public void setPrivProtocol(final SnmpPrivProtocol privProtocol) {
        this.privProtocol = privProtocol;
    }

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(final String privKey) {
        this.privKey = privKey;
    }

    @Override
    public String toString() {
        final String authProtocolStr = (getAuthProtocolAttr() == null) ? SnmpAuthProtocol.NONE.name() : getAuthProtocolAttr().toString();
        final String authKeyStr = (getAuthKey() == null) ? null : "********";
        final String privProtocolStr = (getPrivProtocolAttr() == null) ? SnmpPrivProtocol.NONE.name() : getPrivProtocolAttr().toString();
        final String privKeyStr = (getPrivKey() == null) ? null : "********";

        return String.format(
                "SnmpV3Attributes: AuthProtocol: %s, AuthKey: %s, PrivProtocol: %s, PrivKey: %s",
                authProtocolStr, authKeyStr, privProtocolStr, privKeyStr);
    }
}
