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
 * Java class for ExternalCAEnrollmentDetails complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExternalCAEnrollmentDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="certificateAuthorityDn" type="{}String" minOccurs="1" maxOccurs="1"/>
 *         &lt;element name="caCertificate" type="{}String" minOccurs="1" maxOccurs="1"/>
 *         &lt;element name="enrollmentServerUrl" type="{}String" minOccurs="1" maxOccurs="1"/>
 *         &lt;element name="enrollmentInterface" type="{}String" minOccurs="0" maxOccurs="1"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalCAEnrollmentDetails", propOrder = { "certificateAuthorityDn", "caCertificate", "enrollmentServerUrl" })
public class ExternalCAEnrollmentDetails {

    @XmlElement(required = true)
    protected String certificateAuthorityDn;
    @XmlElement(required = true)
    protected String caCertificate;
    @XmlElement(required = true)
    protected String enrollmentServerUrl;

    /**
     * Gets the value of the certificateAuthorityDn property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCertificateAuthorityDn() {
        return certificateAuthorityDn;
    }

    /**
     * Sets the value of the certificateAuthorityDn property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setCertificateAuthorityDn(final String value) {
        this.certificateAuthorityDn = value;
    }

    /**
     * Gets the value of the caCertificate property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCACertificate() {
        return caCertificate;
    }

    /**
     * Sets the value of the caCertificate property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setCACertificate(final String value) {
        this.caCertificate = value;
    }

    /**
     * Gets the value of the enrollmentServerUrl property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getEnrollmentServerUrl() {
        return enrollmentServerUrl;
    }

    /**
     * Sets the value of the enrollmentServerUrl property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setEnrollmentServerUrl(final String value) {
        this.enrollmentServerUrl = value;
    }

}
