/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ntp.delete.request.model;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="node" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="nodeFdn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="keyIds" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="keyId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="serverIds" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="serverId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "node" })
@XmlRootElement(name = "nodes")
public class Nodes {

    @XmlElement(required = true)
    protected List<Nodes.Node> node;

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
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Nodes.Node }
     *
     *
     */
    public List<Nodes.Node> getNode() {
        if (node == null) {
            node = new ArrayList<>();
        }
        return this.node;
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     *
     * <p>
     * The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="nodeFdn" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="keyIds" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="keyId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="serverIds" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="serverId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "nodeFdn", "keyIds", "serverIds" })
    public static class Node {

        @XmlElement(required = true)
        protected String nodeFdn;
        protected Nodes.Node.KeyIds keyIds;
        protected Nodes.Node.ServerIds serverIds;

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
         * Gets the value of the keyIds property.
         *
         * @return possible object is {@link Nodes.Node.KeyIds }
         *
         */
        public Nodes.Node.KeyIds getKeyIds() {
            return keyIds;
        }

        /**
         * Sets the value of the keyIds property.
         *
         * @param value
         *            allowed object is {@link Nodes.Node.KeyIds }
         *
         */
        public void setKeyIds(final Nodes.Node.KeyIds value) {
            this.keyIds = value;
        }

        /**
         * Gets the value of the serverIds property.
         *
         * @return possible object is {@link Nodes.Node.ServerIds }
         *
         */
        public Nodes.Node.ServerIds getServerIds() {
            return serverIds;
        }

        /**
         * Sets the value of the serverIds property.
         *
         * @param value
         *            allowed object is {@link Nodes.Node.ServerIds }
         *
         */
        public void setServerIds(final Nodes.Node.ServerIds value) {
            this.serverIds = value;
        }

        /**
         * <p>
         * Java class for anonymous complex type.
         *
         * <p>
         * The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="keyId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "keyId" })
        public static class KeyIds {

            @XmlElement(required = true)
            protected List<String> keyId;

            /**
             * Gets the value of the keyId property.
             *
             * <p>
             * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
             * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the keyId property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             *
             * <pre>
             * getKeyId().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list {@link String }
             *
             *
             */
            public List<String> getKeyIdList() {
                if (keyId == null) {
                    keyId = new ArrayList<>();
                }
                return this.keyId;
            }

        }

        /**
         * <p>
         * Java class for anonymous complex type.
         *
         * <p>
         * The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="serverId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "serverId" })
        public static class ServerIds {

            @XmlElement(required = true)
            protected List<String> serverId;

            /**
             * Gets the value of the serverId property.
             *
             * <p>
             * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
             * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the serverId property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             *
             * <pre>
             * getServerId().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list {@link String }
             *
             *
             */
            public List<String> getServerIdList() {
                if (serverId == null) {
                    serverId = new ArrayList<>();
                }
                return this.serverId;
            }

        }

    }

}
