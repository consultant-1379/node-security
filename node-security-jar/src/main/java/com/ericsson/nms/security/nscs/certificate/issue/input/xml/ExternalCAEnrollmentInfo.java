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

import com.ericsson.nms.security.nscs.trust.distribution.input.xml.TrustedCACertificates;

/**
 * <p>
 * Java class for ExternalCAEnrollmentInfo complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExternalCAEnrollmentInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="externalCAEnrollmentInfo" type="{}ExternalCAEnrollmentDetails" minOccurs="1" maxOccurs="1"/>
*         &lt;element name="trustedCACertificates" type="{}TrustedCACertificates" minOccurs="0" maxOccurs="1"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalCAEnrollmentInfo", propOrder = {"externalCAEnrollmentDetails","trustedCACertificates"})
public class ExternalCAEnrollmentInfo {

    @XmlElement(required = true)
    protected ExternalCAEnrollmentDetails externalCAEnrollmentDetails;
    @XmlElement(name = "TrustedCACertificates", required = false)
    protected TrustedCACertificates trustedCACertificates;

    /**
     * Gets the value of the externalCAEnrollmentDetails property.
     *
     * @return possible object is {@link ExternalCAEnrollmentDetails }
     *
     */
    public ExternalCAEnrollmentDetails getExternalCAEnrollmentDetails() {
        return externalCAEnrollmentDetails;
    }

    /**
     * Sets the value of the externalCAEnrollmentDetails property.
     *
     * @param externalCAEnrollmentDetails
     *            allowed object is {@link ExternalCAEnrollmentDetails }
     *
     */
    public void setExternalCAEnrollmentDetails(final ExternalCAEnrollmentDetails externalCAEnrollmentDetails) {
        this.externalCAEnrollmentDetails = externalCAEnrollmentDetails;
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
     * @param trustedCACertificates
     *            allowed object is {@link TrustedCACertificates }
     *
     */
    public void setTrustedCACertificates(final TrustedCACertificates trustedCACertificates) {
        this.trustedCACertificates = trustedCACertificates;
    }

}
