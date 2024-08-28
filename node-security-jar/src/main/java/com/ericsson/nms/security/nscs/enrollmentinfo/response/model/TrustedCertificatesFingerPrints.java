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

package com.ericsson.nms.security.nscs.enrollmentinfo.response.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for TrustedCertificatesFingerPrints complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrustedCertificatesFingerPrints">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="certificateFingerPrint" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author tcsviga
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrustedCertificatesFingerPrints", propOrder = {
    "certificateFingerPrint"
})
public class TrustedCertificatesFingerPrints {

    @XmlElement(required = true)
    protected List<String> certificateFingerPrint;


    public void setCertificateFingerPrint(final List<String> certificateFingerPrint) {
        this.certificateFingerPrint = certificateFingerPrint;
    }

    public List<String> getCertificateFingerPrint() {
        if (certificateFingerPrint == null) {
            certificateFingerPrint = new ArrayList<String>();
        }
        return this.certificateFingerPrint;
    }

}
