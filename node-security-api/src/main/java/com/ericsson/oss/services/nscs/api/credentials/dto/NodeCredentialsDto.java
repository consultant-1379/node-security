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
package com.ericsson.oss.services.nscs.api.credentials.dto;

import java.io.Serializable;

/**
 * Models the node credentials DTO.
 */
public class NodeCredentialsDto implements Serializable {

    private static final long serialVersionUID = -9057704389365903412L;

    private String nodeNameOrFdn;
    private CredentialAttributesDto nodeCredentials;

    public NodeCredentialsDto() {
        /**
         * Empty constructor used by JSON parser.
         */
    }

    /**
     * @return the nodeNameOrFdn
     */
    public String getNodeNameOrFdn() {
        return nodeNameOrFdn;
    }

    /**
     * @param nodeNameOrFdn
     *            the nodeNameOrFdn to set
     */
    public void setNodeNameOrFdn(final String nodeNameOrFdn) {
        this.nodeNameOrFdn = nodeNameOrFdn;
    }

    /**
     * @return the nodeCredentials
     */
    public CredentialAttributesDto getNodeCredentials() {
        return nodeCredentials;
    }

    /**
     * @param nodeCredentials
     *            the nodeCredentials to set
     */
    public void setNodeCredentials(final CredentialAttributesDto nodeCredentials) {
        this.nodeCredentials = nodeCredentials;
    }

    @Override
    public String toString() {
        final String nodeCredentialsList = nodeCredentials != null ? nodeCredentials.toString() : null;
        return "NodeCredentialsDto [nodeNameOrFdn=" + nodeNameOrFdn + ", nodeCredentials=" + nodeCredentialsList + "]";
    }
}
