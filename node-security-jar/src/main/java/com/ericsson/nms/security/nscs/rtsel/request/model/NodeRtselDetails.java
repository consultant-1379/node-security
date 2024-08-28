/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.rtsel.request.model;

import javax.xml.bind.annotation.*;

import com.ericsson.nms.security.nscs.rtsel.delete.request.model.Nodes;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodeRtselDetails", propOrder = { "nodes", "servers" })
public class NodeRtselDetails {

    @XmlElement(required = true)
    protected Nodes nodes;

    @XmlElement(required = true)
    protected Servers servers;

    public Nodes getNodes() {
        return nodes;
    }

    public void setNodes(final Nodes value) {
        this.nodes = value;
    }

    /**
     * @return the servers 
     */
    public Servers getServers() {
        return servers;
    }

    /**
     * @param servers
     *            the servers to set
     */
    public void setServers(final Servers servers) {
        this.servers = servers;
    }

}
