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
 *         &lt;element name="Node" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NodeFdn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="EntityProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="subjectAltName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                   &lt;element name="subjectAltNameType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                   &lt;element name="enrollmentMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                   &lt;element name="keySize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                   &lt;element name="commonName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="certificateSubjectDn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>    
 *                   &lt;element name="challengePhrase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "node" })
@XmlRootElement(name = "Nodes")
public class Nodes {
    @XmlElement(name = "Node", required = true)
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
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Nodes.Node }
     * 
     * 
     */
    public List<Nodes.Node> getNode() {
        if (node == null) {
            node = new ArrayList<Nodes.Node>();
        }
        return this.node;
    }

    /**
     * Sets the list of nodes
     *
     * @param nodes
     *            input list of nodes
     */
    public void setNode(final List<Nodes.Node> nodes) {
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
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="NodeFdn" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="EntityProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="subjectAltName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="subjectAltNameType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="enrollmentMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="keySize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="commonName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="certificateSubjectDn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>    
     *         &lt;element name="challengePhrase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="interfaceFdn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "Node", propOrder = { "nodeFdn", "entityProfileName", "subjectAltName", "subjectAltNameType", "enrollmentMode", "keySize",
            "commonName", "certificateSubjectDn", "challengePhrase", "interfaceFdn" })
    public static class Node {

        @XmlElement(name = "NodeFdn", required = true)
        protected String nodeFdn;
        @XmlElement(name = "EntityProfileName")
        protected String entityProfileName = "";
        @XmlElement(name = "SubjectAltName")
        protected String subjectAltName = "";
        @XmlElement(name = "SubjectAltNameType")
        protected String subjectAltNameType = "";
        @XmlElement(name = "EnrollmentMode")
        protected String enrollmentMode = "";
        @XmlElement(name = "KeySize")
        protected String keySize = "";
        @XmlElement(name = "CommonName")
        protected String commonName = "";
        @XmlElement(name = "CertificateSubjectDn")
        protected String certificateSubjectDn = "";
        @XmlElement(name = "ChallengePhrase")
        protected String challengePhrase = "";
        @XmlElement(name = "InterfaceFdn")
        protected String interfaceFdn = "";

        public Node() {
            super();
        }

        /**
         * @param nodeFdn
         */
        public Node(final String nodeFdn) {
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
            return "nodeFdn: " + this.nodeFdn + ", EntityProfileName: " + this.entityProfileName + ", SubjectAltName: " + this.subjectAltName
                    + ", SubjectAltNameType: " + this.subjectAltNameType + ", EnrollmentMode: " + this.enrollmentMode + ", KeySize: " + this.keySize
                    + ", CommonName: " + this.commonName + ", InterfaceFdn: " + this.interfaceFdn;
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
         * Gets the value of the entityProfileName property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getEntityProfileName() {
            return entityProfileName;
        }

        /**
         * Sets the value of the entityProfileName property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setEntityProfileName(final String value) {
            this.entityProfileName = value;
        }

        /**
         * Gets the value of the subjectAltName property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getSubjectAltName() {
            return subjectAltName;
        }

        /**
         * Sets the value of the subjectAltName property.
         *
         * @param value
         *            allowed object is {@link String }
         *
         */
        public void setSubjectAltName(final String value) {
            this.subjectAltName = value;
        }

        /**
         * Gets the value of the subjectAltNameType property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getSubjectAltNameType() {
            return subjectAltNameType;
        }

        /**
         * Sets the value of the subjectAltNameType property.
         *
         * @param value
         *            allowed object is {@link String }
         *
         */
        public void setSubjectAltNameType(final String value) {
            this.subjectAltNameType = value;
        }

        /**
         * Gets the value of the enrollmentMode property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getEnrollmentMode() {
            return enrollmentMode;
        }

        /**
         * Sets the value of the enrollmentMode property.
         *
         * @param value
         *            allowed object is {@link String }
         *
         */
        public void setEnrollmentMode(final String value) {
            this.enrollmentMode = value;
        }

        /**
         * Gets the value of the keySize property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getKeySize() {
            return keySize;
        }

        /**
         * Sets the value of the keySize property.
         *
         * @param value
         *            allowed object is {@link String }
         *
         */
        public void setKeySize(final String value) {
            this.keySize = value;
        }

        /**
         * Gets the value of the commonName property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getCommonName() {
            return commonName;
        }

        /**
         * Sets the value of the commonName property.
         *
         * @param value
         *            allowed object is {@link String }
         *
         */
        public void setCommonName(final String value) {
            this.commonName = value;
        }

        /**
         * Gets the value of the certificateSubjectDn property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getCertificateSubjectDn() {
            return certificateSubjectDn;
        }

        /**
         * Sets the value of the certificateSubjectDn property.
         *
         * @param certificateSubjectDn
         *            allowed object is {@link String }
         *
         */
        public void setCertificateSubjectDn(final String certificateSubjectDn) {
            this.certificateSubjectDn = certificateSubjectDn;
        }

        /**
         * Gets the value of the challengePhrase property.
         *
         * @return challengePhrase {@link String }
         *
         */
        public String getChallengePhrase() {
            return challengePhrase;
        }

        /**
         * Sets the value of the challengePhrase property.
         *
         * @param challengePhrase
         *            allowed object is {@link String }
         *
         */
        public void setChallengePhrase(final String challengePhrase) {
            this.challengePhrase = challengePhrase;
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
