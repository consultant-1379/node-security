/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

import java.util.Objects;

public class CipherTlsProtocolInfo {
    private final String name;
    private String protocolVersion;
    private final boolean isEnabled;

    /**
     * @param name
     * @param protocolVersion
     */
    public CipherTlsProtocolInfo(final String name, final String protocolVersion, final boolean isEnabled) {
        this.name = name;
        this.protocolVersion = protocolVersion;
        this.isEnabled = isEnabled;
    }

    /**
     * @return the validNodesList
     */
    public String getName() {
        return name;
    }

    /**
     * @return the protocolVersion
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * @return the protocolVersion
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setProtocolVersion(final String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final CipherTlsProtocolInfo objToCompare = (CipherTlsProtocolInfo) obj;
        return Objects.equals(name, objToCompare.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protocolVersion, isEnabled);
    }
}
