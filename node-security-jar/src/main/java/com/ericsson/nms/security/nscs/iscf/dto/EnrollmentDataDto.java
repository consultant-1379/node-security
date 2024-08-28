/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.iscf.dto;

/**
 * Data Transfer Object for
 * {@link com.ericsson.nms.security.nscs.iscf.xml.EnrollmentData}
 */
public class EnrollmentDataDto {

    protected ISCFEncryptedContentDto dataChallengePassword;
    protected String distinguishedName;
    protected String enrollmentServerURL;
    protected String caFingerprint;
    protected Integer keyLength;
    protected Integer enrollmentTimeLimit;
    protected Integer enrollmentMode;
    protected String certificateAuthorityDn;

    /**
     * Gets the value of the dataChallengePassword property.
     *
     * @return possible object is {@link ISCFEncryptedContent }
     *
     */
    public ISCFEncryptedContentDto getDataChallengePassword() {
        return dataChallengePassword;
    }

    /**
     * Sets the value of the dataChallengePassword property.
     *
     * @param value allowed object is {@link ISCFEncryptedContent }
     *
     */
    public void setDataChallengePassword(final ISCFEncryptedContentDto value) {
        this.dataChallengePassword = value;
    }

    /**
     * Gets the value of the distinguishedName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDistinguishedName() {
        return distinguishedName;
    }

    /**
     * Sets the value of the distinguishedName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDistinguishedName(final String value) {
        this.distinguishedName = value;
    }

    /**
     * Gets the value of the enrollmentServerURL property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getEnrollmentServerURL() {
        return enrollmentServerURL;
    }

    /**
     * Sets the value of the enrollmentServerURL property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setEnrollmentServerURL(final String value) {
        this.enrollmentServerURL = value;
    }

    /**
     * Gets the value of the caFingerprint property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCAFingerprint() {
        return caFingerprint;
    }

    /**
     * Sets the value of the caFingerprint property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCAFingerprint(final String value) {
        this.caFingerprint = value;
    }

    /**
     * Gets the value of the keyLength property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getKeyLength() {
        return keyLength;
    }

    /**
     * Sets the value of the keyLength property.
     *
     * @param value allowed object is {@link Integer }
     *
     */
    public void setKeyLength(final Integer value) {
        this.keyLength = value;
    }

    /**
     * Gets the value of the enrollmentTimeLimit property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getEnrollmentTimeLimit() {
        return enrollmentTimeLimit;
    }

    /**
     * Sets the value of the enrollmentTimeLimit property.
     *
     * @param value allowed object is {@link Integer }
     *
     */
    public void setEnrollmentTimeLimit(final Integer value) {
        this.enrollmentTimeLimit = value;
    }

    /**
     * Gets the value of the enrollmentMode property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getEnrollmentMode() {
        return enrollmentMode;
    }

    /**
     * Sets the value of the enrollmentMode property.
     *
     * @param value allowed object is {@link Integer }
     *
     */
    public void setEnrollmentMode(Integer value) {
        this.enrollmentMode = value;
    }

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
     * @param value allowed object is {@link String }
     *
     */
    public void setCertificateAuthorityDn(String value) {
        this.certificateAuthorityDn = value;
    }

}
