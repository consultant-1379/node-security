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
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="certificates">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="certificate" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
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
@XmlType(name = "", propOrder = { "name", "certificates" })
public class TrustCategory implements Serializable {

    private static final long serialVersionUID = 6282069693575628184L;

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected Certificates certificates;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param name
     *            allowed object is {@link String }
     *
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the value of the certificates property.
     *
     * @return possible object is {@link Certificates }
     *
     */
    public Certificates getCertificates() {
        return certificates;
    }

    /**
     * Sets the value of the certificates property.
     *
     * @param certificates
     *            allowed object is {@link Certificates }
     *
     */
    public void setCertificates(final Certificates certificates) {
        this.certificates = certificates;
    }
}
