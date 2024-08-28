/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.util;

import java.io.Serializable;

/**
 * This class contains the NscsTrustedEntityInfo and trustedEntityInfo details
 *
 * @author xkumkam
 *
 */
public class TrustedCAInterfaceFdnDetails implements Serializable {

    private static final long serialVersionUID = 6914014730501055688L;

    NscsTrustedEntityInfo trustedEntityInfo;
    String interfaceIpAddressFdn;

    /**
     * @return the interfaceIpAddressFdn
     */
    public String getInterfaceIpAddressFdn() {
        return interfaceIpAddressFdn;
    }

    /**
     * @param interfaceIpAddressFdn
     *            the interfaceIpAddressFdn to set
     */
    public void setInterfaceIpAddressFdn(final String interfaceIpAddressFdn) {
        this.interfaceIpAddressFdn = interfaceIpAddressFdn;
    }

    /**
     * @return the trustedEntityInfo
     */
    public NscsTrustedEntityInfo getTrustedEntityInfo() {
        return trustedEntityInfo;
    }

    /**
     * @param trustedEntityInfo
     *            the trustedEntityInfo to set
     */
    public void setTrustedEntityInfo(final NscsTrustedEntityInfo trustedEntityInfo) {
        this.trustedEntityInfo = trustedEntityInfo;
    }
}
