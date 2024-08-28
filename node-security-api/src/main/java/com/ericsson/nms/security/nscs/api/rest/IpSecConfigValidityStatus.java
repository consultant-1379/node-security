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
import java.util.List;

public class IpSecConfigValidityStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<IpSecConfigInvalidElement> ipsecConfigInvalidElements;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<IpSecConfigInvalidElement> getIpsecConfigInvalidElements() {
        return ipsecConfigInvalidElements;
    }

    public void setIpsecConfigInvalidElements(final List<IpSecConfigInvalidElement> ipsecConfigInvalidElements) {
        this.ipsecConfigInvalidElements = ipsecConfigInvalidElements;
    }

    @Override
    public String toString() {
        return "IpSecConfigSwitchStatus [name=" + name + ", ipsecconfig=" + ipsecConfigInvalidElements + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipsecConfigInvalidElements == null) ? 0 : ipsecConfigInvalidElements.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        final IpSecConfigValidityStatus other = (IpSecConfigValidityStatus) obj;
        if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
