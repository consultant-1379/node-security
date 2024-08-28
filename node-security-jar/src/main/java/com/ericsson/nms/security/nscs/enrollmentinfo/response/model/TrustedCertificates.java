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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 *         &lt;element name="trustedCertificate" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="caSubjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cafingerprint" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="tdpsUri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="caPem" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "", propOrder = { "trustedCertificate" })
public class TrustedCertificates implements Serializable {

    private static final long serialVersionUID = 7374636630063666413L;

    @XmlElement(required = true)
    private List<TrustedCertificate> trustedCertificate;

    /**
     * @param trustedCertificate
     *            the trustedCertificate to set
     */
    public void setTrustedCertificate(List<TrustedCertificate> trustedCertificate) {
        trustedCertificate = new ArrayList<>(trustedCertificate);
        this.trustedCertificate = Collections.unmodifiableList(trustedCertificate);
    }

    /**
     * Gets the value of the trustedCertificate property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be
     * present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the trustedCertificate property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getTrustedCertificate().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link EnrollmentInfo.VerboseEnrollmentInfo.TrustedCertificates.TrustedCertificate }
     *
     */
    public List<TrustedCertificate> getTrustedCertificate() {
        if (trustedCertificate == null) {
            trustedCertificate = new ArrayList<>();
        }
        return new ArrayList<>(this.trustedCertificate);
    }
}
