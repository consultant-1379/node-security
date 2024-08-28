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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for nodeInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nodeInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nodeFdns" type="{}nodeFdns" minOccurs="1" maxOccurs="1"/&gt;
 *         &lt;element name="entityProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 *         &lt;element name="enrollmentMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 *         &lt;element name="keySize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 * @author tcsviga
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodeInfo", propOrder = {
    "nodeFdns",
    "entityProfileName",
    "enrollmentMode",
    "keySize"
})
public class NodeInfo {

    @XmlElement(required = true)
    protected NodeFdns nodeFdns;
    protected String entityProfileName;
    protected String enrollmentMode;
    protected String keySize;

    /**
     * Gets the value of the nodeFdns property.
     *
     * @return possible object is {@link NodeFdns }
     *
     */
    public NodeFdns getNodeFdns() {
        return nodeFdns;
    }

    /**
     * Sets the value of the nodeFdns property.
     *
     * @param value
     *            allowed object is {@link NodeFdns }
     *
     */
    public void setNodeFdns(final NodeFdns value) {
        this.nodeFdns = value;
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

}
