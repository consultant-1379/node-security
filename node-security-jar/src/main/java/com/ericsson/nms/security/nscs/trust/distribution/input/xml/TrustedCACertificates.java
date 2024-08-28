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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for TrustedCACertificates complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TrustedCACertificates"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TrustedCACertificateInfo" type="{}TrustedCACertificateInfo" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrustedCACertificates", propOrder = { "trustedCACertificateInfo" })
public class TrustedCACertificates {

    @XmlElement(name = "TrustedCACertificateInfo", required = true)
    protected List<TrustedCACertificateInfo> trustedCACertificateInfo;

    /**
     * Gets the value of the trustedCACertificateInfo property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be
     * present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the trustedCACertificateInfo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getTrustedCACertificateInfo().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link TrustedCACertificateInfo }
     *
     *@return  list of TrustedCACertificateInfo
     */
    public List<TrustedCACertificateInfo> getTrustedCACertificateInfo() {
        if (trustedCACertificateInfo == null) {
            trustedCACertificateInfo = new ArrayList<>();
        }
        return this.trustedCACertificateInfo;
    }

}
