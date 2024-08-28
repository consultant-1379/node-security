/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.response.model;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NodeCredentialId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EnrollmentServerGroupId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EnrollmentServerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EnrollmentAuthority" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cacerts" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TrustedCerts" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AuthorityType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AuthorityName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodeCredentialId",
    "enrollmentServerGroupId",
    "enrollmentServerId",
    "enrollmentAuthority",
    "cacerts",
    "trustedCerts",
    "authorityType",
    "authorityName"
})
public class EnrollmentCmpConfig implements Serializable {

    private static final long serialVersionUID = 8233528982363805238L;

    @XmlElement(name = "NodeCredentialId", required = true)
    protected String nodeCredentialId;
    @XmlElement(name = "EnrollmentServerGroupId", required = true)
    protected String enrollmentServerGroupId;
    @XmlElement(name = "EnrollmentServerId", required = true)
    protected String enrollmentServerId;
    @XmlElement(name = "EnrollmentAuthority", required = true)
    protected String enrollmentAuthority;
    @XmlElement(required = true)
    protected String cacerts;
    @XmlElement(name = "TrustedCerts", required = true)
    protected String trustedCerts;
    @XmlElement(name = "AuthorityType", required = true)
    protected String authorityType;
    @XmlElement(name = "AuthorityName", required = true)
    protected String authorityName;

    /**
     * Gets the value of the nodeCredentialId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNodeCredentialId() {
        return nodeCredentialId;
    }

    /**
     * Sets the value of the nodeCredentialId property.
     *
     * @param nodeCredentialId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNodeCredentialId(final String nodeCredentialId) {
        this.nodeCredentialId = nodeCredentialId;
    }

    /**
     * Gets the value of the enrollmentServerGroupId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnrollmentServerGroupId() {
        return enrollmentServerGroupId;
    }

    /**
     * Sets the value of the enrollmentServerGroupId property.
     *
     * @param enrollmentServerGroupId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnrollmentServerGroupId(final String enrollmentServerGroupId) {
        this.enrollmentServerGroupId = enrollmentServerGroupId;
    }

    /**
     * Gets the value of the enrollmentServerId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnrollmentServerId() {
        return enrollmentServerId;
    }

    /**
     * Sets the value of the enrollmentServerId property.
     *
     * @param enrollmentServerId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnrollmentServerId(final String enrollmentServerId) {
        this.enrollmentServerId = enrollmentServerId;
    }

    /**
     * Gets the value of the enrollmentAuthority property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnrollmentAuthority() {
        return enrollmentAuthority;
    }

    /**
     * Sets the value of the enrollmentAuthority property.
     *
     * @param enrollmentAuthority
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnrollmentAuthority(final String enrollmentAuthority) {
        this.enrollmentAuthority = enrollmentAuthority;
    }

    /**
     * Gets the value of the cacerts property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCacerts() {
        return cacerts;
    }

    /**
     * Sets the value of the cacerts property.
     *
     * @param cacerts
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCacerts(final String cacerts) {
        this.cacerts = cacerts;
    }

    /**
     * Gets the value of the trustedCerts property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTrustedCerts() {
        return trustedCerts;
    }

    /**
     * Sets the value of the trustedCerts property.
     *
     * @param trustedCerts
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTrustedCerts(final String trustedCerts) {
        this.trustedCerts = trustedCerts;
    }

    /**
     * Gets the value of the authorityType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuthorityType() {
        return authorityType;
    }

    /**
     * Sets the value of the authorityType property.
     *
     * @param authorityType
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuthorityType(final String authorityType) {
        this.authorityType = authorityType;
    }

    /**
     * Gets the value of the authorityName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuthorityName() {
        return authorityName;
    }

    /**
     * Sets the value of the authorityName property.
     *
     * @param authorityName
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuthorityName(final String authorityName) {
        this.authorityName = authorityName;
    }

}