/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.api.iscf.dto;

import java.io.Serializable;

public class NodeIdentifierDto implements Serializable {

    private static final long serialVersionUID = 5932705312021391708L;

    private String nodeFdn = null;
    // node serial number is optional. If not available then it should be set to null.
    // If specified it changes the CN of the subject name of the involved certificate!
    private String nodeSn = null;

    public NodeIdentifierDto() {
    }

    /**
     * Constructor with given FDN and serial number.
     *
     * @param nodeFdn The node FDN. This MUST be provided and cannot be empty string or null.
     * @param nodeSn The node serial number. Can be null if not available.
     */
    public NodeIdentifierDto(final String nodeFdn, final String nodeSn) {
        this.nodeFdn = nodeFdn;
        this.nodeSn = nodeSn;
    }

    /**
     * @return the nodeFdn
     */
    public String getNodeFdn() {
        return nodeFdn;
    }

    /**
     * @param nodeFdn
     *            the nodeFdn to set
     */
    public void setNodeFdn(final String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }

    /**
     * @return the nodeSn
     */
    public String getNodeSn() {
        return nodeSn;
    }

    /**
     * @param nodeSn
     *            the nodeSn to set
     */
    public void setNodeSn(final String nodeSn) {
        this.nodeSn = nodeSn;
    }

}
