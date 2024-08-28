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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for nodeRtselConfig complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nodeRtselConfig"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nodes" type="{}Nodes" minOccurs="1" maxOccurs="1"/&gt;
 *         &lt;element name="serverConfig" type="{}ServerConfig" minOccurs="1" maxOccurs="2"/&gt;
 *         &lt;element name="connAttemptTimeOut" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0" maxOccurs="1"/&gt;
 *         &lt;element name="extServerLogLevel" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1"/&gt;
 *         &lt;element name="extServerAppName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "nodeRtselConfig", propOrder = {
    "nodes",
    "serverConfig",
    "connAttemptTimeOut",
    "extServerLogLevel",
    "extServerAppName"
})
public class NodeRtselConfig {

    @XmlElement(required = true)
    protected Nodes nodes;
    @XmlElement(required = true)
    protected List<ServerConfig> serverConfig;
    protected Integer connAttemptTimeOut;
    protected String extServerLogLevel;
    protected String extServerAppName;

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
     * Gets the value of the serverConfig property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serverConfig property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServerConfig().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServerConfig }
     * 
     * @return the list of ServerConfig
     */
    public List<ServerConfig> getServerConfig() {
        if (serverConfig == null) {
            serverConfig = new ArrayList<ServerConfig>();
        }
        return this.serverConfig;
    }

    /**
     * Gets the value of the connAttemptTimeOut property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getConnAttemptTimeOut() {
        return connAttemptTimeOut;
    }

    /**
     * Sets the value of the connAttemptTimeOut property.
     *
     * @param value
     *            allowed object is {@link Integer }
     *
     */
    public void setConnAttemptTimeOut(final Integer value) {
        this.connAttemptTimeOut = value;
    }

    /**
     * Gets the value of the extServerLogLevel property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public String getExtServerLogLevel() {
        return extServerLogLevel;
    }

    /**
     * Sets the value of the extServerLogLevel property.
     *
     * @param value
     *            allowed object is {@link Integer }
     *
     */
    public void setExtServerLogLevel(final String value) {
        this.extServerLogLevel = value;
    }

    /**
     * Gets the value of the extServerAppName property.
     *
     * @return possible object is {@link String }
     * 
     */
    public String getExtServerAppName() {
        return extServerAppName;
    }

    /**
     * Sets the value of the extServerAppName property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setExtServerAppName(final String value) {
        this.extServerAppName = value;
    }

}
