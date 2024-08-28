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
package com.ericsson.nms.security.nscs.rtsel.delete.request.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodes", propOrder = { "nodeFdn" })
public class Nodes {

    @XmlElement(required = true)
    protected Set<String> nodeFdn;

    /**
     * Gets the value of the nodeFdn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is
     * not a <CODE>set</CODE> method for the nodeFdn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getNodeFdn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     * 
     * @return the set of Fdns
     */
    public Set<String> getNodeFdn() {
        if (nodeFdn == null) {
            nodeFdn = new HashSet<String>();
        }
        return this.nodeFdn;
    }

    /**
     * @param nodeFdn
     *            the nodeFdn to set
     */
    public void setNodeFdn(final Set<String> nodeFdn) {
        this.nodeFdn = nodeFdn;
    }
}
