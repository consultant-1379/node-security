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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for TrustedCACertificateInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrustedCACertificateInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="TDPSUrl" type="{}minStringLength"/&gt;
 *         &lt;element name="TrustedCACertIssuerDn" type="{}minStringLength"/&gt;
 *         &lt;element name="CertificateSerialNumber" type="{}minStringLength"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrustedCACertificateInfo", propOrder = {

})
public class TrustedCACertificateInfo {

    @XmlElement(name = "TDPSUrl", required = true)
    protected String tdpsUrl;
    @XmlElement(name = "TrustedCACertIssuerDn", required = true)
    protected String trustedCACertIssuerDn;
    @XmlElement(name = "CertificateSerialNumber", required = true)
    protected String certificateSerialNumber;

    /**
     * Gets the value of the tdpsUrl property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTDPSUrl() {
        return tdpsUrl;
    }

    /**
     * Sets the value of the tdpsUrl property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setTDPSUrl(final String value) {
        this.tdpsUrl = value;
    }

    /**
     * Gets the value of the trustedCACertIssuerDn property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTrustedCACertIssuerDn() {
        return trustedCACertIssuerDn;
    }

    /**
     * Sets the value of the trustedCACertIssuerDn property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setTrustedCACertIssuerDn(final String value) {
        this.trustedCACertIssuerDn = value;
    }

    /**
     * Gets the value of the certificateSerialNumber property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    /**
     * Sets the value of the certificateSerialNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setCertificateSerialNumber(final String value) {
        this.certificateSerialNumber = value;
    }

}
