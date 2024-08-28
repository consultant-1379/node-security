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
package com.ericsson.nms.security.nscs.enrollmentinfo.utility;

import java.util.List;

import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes.NodeTrustInfo;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.TrustedCACertificates;

/**
 * This class holds the TrustedCACertificates for a List of nodes
 *
 * @author xvekkar
 *
 */
public class TrustedCAInformation {

    private TrustedCACertificates trustedCACertificates;
    private List<NodeTrustInfo> validNodes;

    /**
     * Gets the TrustedCACertificates
     *
     * @return the TrustedCACertificates
     */
    public TrustedCACertificates getTrustedCACertificates() {
        return trustedCACertificates;
    }

    /**
     * Sets the TrustedCACertificates
     *
     * @param TrustedCACertificates
     *            the TrustedCACertificates to set
     */
    public void setTrustedCACertificates(final TrustedCACertificates trustedCACertificates) {
        this.trustedCACertificates = trustedCACertificates;
    }

    /**
     * Gets the list of valid Nodes
     *
     * @return the validNodes
     */
    public List<NodeTrustInfo> getValidNodes() {
        return validNodes;
    }

    /**
     * Sets the List of valid Nodes
     *
     * @param validNodes
     *            the validNodes to set
     */
    public void setValidNodes(final List<NodeTrustInfo> validNodes) {
        this.validNodes = validNodes;
    }

}
