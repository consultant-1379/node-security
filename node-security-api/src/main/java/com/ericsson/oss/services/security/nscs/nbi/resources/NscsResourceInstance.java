/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.resources;

import java.io.Serializable;

public class NscsResourceInstance implements Serializable {

    private static final long serialVersionUID = 3709672049663038035L;

    private String status;
    private String resource;
    private String resourceId;
    private String subResource;
    private String subResourceId;

    public NscsResourceInstance() {
        super();
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource
     *            the resource to set
     */
    public void setResource(final String resource) {
        this.resource = resource;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * @param resourceId
     *            the resourceId to set
     */
    public void setResourceId(final String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * @return the subResource
     */
    public String getSubResource() {
        return subResource;
    }

    /**
     * @param subResource
     *            the subResource to set
     */
    public void setSubResource(final String subResource) {
        this.subResource = subResource;
    }

    /**
     * @return the subResourceId
     */
    public String getSubResourceId() {
        return subResourceId;
    }

    /**
     * @param subResourceId
     *            the subResourceId to set
     */
    public void setSubResourceId(final String subResourceId) {
        this.subResourceId = subResourceId;
    }

}
