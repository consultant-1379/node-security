/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.rest;

import java.io.Serializable;

public class IpSecConfigSwitchStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private String ipsecconfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpsecconfig() {
        return ipsecconfig;
    }

    public void setIpsecconfig(String ipsecconfig) {
        this.ipsecconfig = ipsecconfig;
    }

    @Override
    public boolean equals(Object obj) {
        IpSecConfigSwitchStatus other = (IpSecConfigSwitchStatus) obj;
        if (!name.equals(other.name))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "IpSecConfigSwitchStatus [name=" + name + ", type=" + type + ", ipsecconfig=" + ipsecconfig + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipsecconfig == null) ? 0 : ipsecconfig.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
}
