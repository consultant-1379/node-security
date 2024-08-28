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

package com.ericsson.nms.security.nscs.certificate.issue.input.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for NodeEnrollmentDetails complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="NodeEnrollmentDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nodes" type="{}Nodes"/>
 *         &lt;element name="externalCAEnrollmentInfo" type="{}ExternalCAEnrollmentInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NodeEnrollmentDetails", propOrder = { "nodes", "externalCAEnrollmentInfo" })
public class NodeEnrollmentDetails {

    @XmlElement(name = "Nodes", required = true)
    protected Nodes nodes;
    protected ExternalCAEnrollmentInfo externalCAEnrollmentInfo;

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
     * Gets the value of the externalCAEnrollmentInfo property.
     *
     * @return possible object is {@link ExternalCAEnrollmentInfo }
     *
     */
    public ExternalCAEnrollmentInfo getExternalCAEnrollmentInfo() {
        return externalCAEnrollmentInfo;
    }

    /**
     * Sets the value of the externalCAEnrollmentInfo property.
     *
     * @param value
     *            allowed object is {@link ExternalCAEnrollmentInfo }
     *
     */
    public void setExternalCAEnrollmentInfo(final ExternalCAEnrollmentInfo value) {
        this.externalCAEnrollmentInfo = value;
    }
}
