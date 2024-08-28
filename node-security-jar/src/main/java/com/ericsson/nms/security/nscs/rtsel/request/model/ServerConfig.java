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
 * Java class for ServerConfig complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServerConfig"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="extServerAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 *         &lt;element name="extServProtocol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 *         &lt;element name="serverName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 * @author tcsviga
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServerConfig", propOrder = {

})
public class ServerConfig {

    @XmlElement(required = true)
    protected String extServerAddress;
    @XmlElement(required = true)
    protected String extServProtocol;
    @XmlElement(required = true)
    protected String serverName;

    /**
     * Gets the value of the extServerAddress property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getExtServerAddress() {
        return extServerAddress;
    }

    /**
     * Sets the value of the extServerAddress property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setExtServerAddress(final String value) {
        this.extServerAddress = value;
    }

    /**
     * Gets the value of the extServProtocol property.
     *
     * @return possible object is {@link String }
     * 
     */
    public String getExtServProtocol() {
        return extServProtocol;
    }

    /**
     * Sets the value of the extServProtocol property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setExtServProtocol(final String value) {
        this.extServProtocol = value;
    }

    /**
     * Gets the value of the serverName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the value of the serverName property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setServerName(final String value) {
        this.serverName = value;
    }

}
