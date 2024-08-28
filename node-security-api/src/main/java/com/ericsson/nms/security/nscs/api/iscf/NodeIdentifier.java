/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.iscf;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author enmadmin
 */
public class NodeIdentifier implements Serializable {

    private static final long serialVersionUID = 2812642427707586640L;

    final String fdn;
    final String serialNumber; // SN is optional. If not available then it should be set to null.

    /**
     * Node Identifying information
     *
     * @param fdn The node FDN. This MUST be provided and cannot be empty string or null.
     * @param serialNumber The node serial number. Can be null if not available.
     *
     */
    public NodeIdentifier(final String fdn, final String serialNumber) {
        this.fdn = fdn;
        this.serialNumber = serialNumber;
    }

    public String getFdn() {
        return fdn;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String toString() {
        return "NodeIdentifier{" + "fdn=" + fdn + ", serialNumber=" + serialNumber + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(fdn, serialNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NodeIdentifier other = (NodeIdentifier) obj;
        return Objects.equals(fdn, other.fdn) && Objects.equals(serialNumber, other.serialNumber);
    }

}
