/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * This class holds the information about EnrollmentInfo.
 * </p>
 * 
 * @author xgvgvgv
 * 
 */
@XmlRootElement
public class EnrollmentInfo implements Serializable {

    private static final long serialVersionUID = -7010478569832541427L;

    private String challengePassword;
    private String enrollmentCaFingerprint;
    private String keyInfo;
    private String subjectName;
    private String issuerCA;
    private String url;
    private TrustedCertificatesFingerPrints trustedCertificatesFingerPrints;
    protected VerboseEnrollmentInfo verboseEnrollmentInfo;

    /**
     * Gets the values of the verboseEnrollmentInfo.
     *
     * @return possible object is {@link VerboseEnrollmentInfo }
     *
     */
    public VerboseEnrollmentInfo getVerboseEnrollmentInfo() {
        return verboseEnrollmentInfo;
    }

    /**
     * Sets the value of the verboseEnrollmentInfo.
     *
     * allows possible object is {@link verboseEnrollmentInfo }
     *
     */
    public void setVerboseEnrollmentInfo(final VerboseEnrollmentInfo verboseEnrollmentInfo) {
        this.verboseEnrollmentInfo = verboseEnrollmentInfo;
    }

    /**
     * Gets the value of the challengePassword property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getChallengePassword() {
        return challengePassword;
    }

    /**
     * Sets the value of the challengePassword property.
     * 
     * @return possible object is {@link String }
     * 
     */

    @XmlElement
    public void setChallengePassword(final String challengePassword) {
        this.challengePassword = challengePassword;
    }

    /**
     * Gets the value of the enrollmentCaFingerprint property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getEnrollmentCaFingerprint() {
        return enrollmentCaFingerprint;
    }

    /**
     * Sets the value of the enrollmentCaFingerprint property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlElement
    public void setEnrollmentCaFingerprint(final String enrollmentCaFingerprint) {
        this.enrollmentCaFingerprint = enrollmentCaFingerprint;
    }

    /**
     * Gets the value of the keyInfo property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getKeyInfo() {
        return keyInfo;
    }

    /**
     * Sets the value of the keyInfo property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlElement
    public void setKeyInfo(final String keyInfo) {
        this.keyInfo = keyInfo;
    }

    /**
     * Gets the value of the subjectName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Sets the value of the subjectName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlElement
    public void setSubjectName(final String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * Gets the value of the issuerCA property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getIssuerCA() {
        return issuerCA;
    }

    /**
     * Sets the value of the issuerCA property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlElement
    public void setIssuerCA(final String issuerCA) {
        this.issuerCA = issuerCA;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlElement
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Gets the value of the trustedCertificateFingerPrints property.
     * 
     * @return possible object is {@link TrustedCertificatesFingerPrints }
     * 
     */
    public TrustedCertificatesFingerPrints getTrustedCertificateFingerPrints() {
        return trustedCertificatesFingerPrints;
    }

    /**
     * Sets the value of the trustedCertificateFingerPrints property.
     * 
     * @param value
     *            allowed object is {@link TrustedCertificatesFingerPrints }
     * 
     */
    @XmlElement
    public void setTrustedCertificateFingerPrints(final TrustedCertificatesFingerPrints trustedCertificatesFingerPrints) {
        this.trustedCertificatesFingerPrints = trustedCertificatesFingerPrints;
    }

}
