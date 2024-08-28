package com.ericsson.nms.security.nscs.api.iscf;

/**
 * Represents the type of IPSEC required: TRANSPORT, OM
 *
 * @author emacgma
 */
public enum IpsecArea {

    TRANSPORT("Traffic"),
    OM("OAM")
    ;

    private String ipsecArea;

    private IpsecArea(final String ipsecArea) {
        this.ipsecArea = ipsecArea;
    }

    @Override
    public String toString() {
        return this.ipsecArea;
    }
}
