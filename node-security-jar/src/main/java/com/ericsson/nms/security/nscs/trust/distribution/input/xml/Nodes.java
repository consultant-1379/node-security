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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Node" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="NodeFdn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="interfaceFdn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "node" })
@XmlRootElement(name = "Nodes")
public class Nodes {
    @XmlElement(name = "Node", required = true)
    protected List<Nodes.NodeTrustInfo> node;

    /**
     * Gets the value of the node property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be
     * present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the node property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getNode().add(newItem);
     * </pre>
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Nodes.NodeTrustInfo }
     * 
     * @return list Nodes.NodeTrustInfo
     */
    public List<Nodes.NodeTrustInfo> getNode() {
        if (node == null) {
            node = new ArrayList<>();
        }
        return this.node;
    }

    /**
     * Sets the list of nodes
     *
     * @param nodes
     *            input list of nodes
     */
    public void setNode(final List<Nodes.NodeTrustInfo> nodes) {
        this.node = nodes;
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     *
     * <p>
     * The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType
     *   &lt;complexContent
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"
     *       &lt;sequence
     *         &lt;element name="NodeFdn" type="{http://www.w3.org/2001/XMLSchema}string"
     *         &lt;element name="interfaceFdn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"
     *       &lt;/sequence
     *     &lt;/restriction
     *   &lt;/complexContent
     * &lt;/complexType
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "Node", propOrder = { "nodeFdn", "interfaceFdn" })
    public static class NodeTrustInfo {

        @XmlElement(name = "NodeFdn", required = true)
        protected String nodeFdn;

        @XmlElement(name = "InterfaceFdn")
        protected String interfaceFdn = "";

        public NodeTrustInfo() {
            super();
        }

        /**
         * @param nodeFdn the nodeFdn
         */
        public NodeTrustInfo(final String nodeFdn) {
            super();
            this.nodeFdn = nodeFdn;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "nodeFdn: " + this.nodeFdn + ", InterfaceFdn: " + this.interfaceFdn;
        }

        /**
         * Gets the value of the nodeFdn property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getNodeFdn() {
            return nodeFdn;
        }

        /**
         * Sets the value of the nodeFdn property.
         *
         * @param value
         *            allowed object is {@link String }
         *
         */
        public void setNodeFdn(final String value) {
            this.nodeFdn = value;
        }

        /**
         * Gets the value of the interfaceFdn property.
         *
         * @return interfaceFdn {@link String }
         *
         */
        public String getInterfaceFdn() {
            return interfaceFdn;
        }

        /**
         * Sets the value of the interfaceFdn property.
         *
         * @param interfaceFdn
         *            allowed object is {@link String }
         *
         */
        public void setInterfaceFdn(final String interfaceFdn) {
            this.interfaceFdn = interfaceFdn;
        }
    }
}
