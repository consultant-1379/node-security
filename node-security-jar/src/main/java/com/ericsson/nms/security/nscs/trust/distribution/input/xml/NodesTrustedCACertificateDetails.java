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
package com.ericsson.nms.security.nscs.trust.distribution.input.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for NodesTrustedCACertificateDetails complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NodesTrustedCACertificateDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Nodes" type="{}Nodes"/&gt;
 *         &lt;element name="TrustedCACertificates" type="{}TrustedCACertificates"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NodesTrustedCACertificateDetails", propOrder = { "nodes", "trustedCACertificates" })
public class NodesTrustedCACertificateDetails {

    @XmlElement(name = "Nodes", required = true)
    protected Nodes nodes;
    @XmlElement(name = "TrustedCACertificates", required = true)
    protected TrustedCACertificates trustedCACertificates;

    /**
     * Gets the value of the nodes property.
     * 
     * @return possible object is {@link Nodes }
     * 
     */
    public Nodes getNodes() {
        return nodes;
    }

    /**
     * Sets the value of the nodes property.
     * 
     * @param value
     *            allowed object is {@link Nodes }
     * 
     */
    public void setNodes(final Nodes value) {
        this.nodes = value;
    }

    /**
     * Gets the value of the trustedCACertificates property.
     * 
     * @return possible object is {@link TrustedCACertificates }
     * 
     */
    public TrustedCACertificates getTrustedCACertificates() {
        return trustedCACertificates;
    }

    /**
     * Sets the value of the trustedCACertificates property.
     * 
     * @param value
     *            allowed object is {@link TrustedCACertificates }
     * 
     */
    public void setTrustedCACertificates(final TrustedCACertificates value) {
        this.trustedCACertificates = value;
    }

}
