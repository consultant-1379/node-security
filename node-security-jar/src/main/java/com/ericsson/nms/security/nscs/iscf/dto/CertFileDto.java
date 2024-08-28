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
 * {@link com.ericsson.nms.security.nscs.iscf.xml.CertFile}
 *
 */
public class CertFileDto
        extends ISCFEncryptedContentDto {

    protected String category;
    protected String certFingerprint;
    protected String certSerialNumber;

    /**
     * Gets the value of the category property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCategory(final String value) {
        this.category = value;
    }

    /**
     * Gets the value of the certFingerprint property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCertFingerprint() {
        return certFingerprint;
    }

    /**
     * Sets the value of the certFingerprint property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCertFingerprint(final String value) {
        this.certFingerprint = value;
    }

    /**
     * Gets the value of the certSerialNumber property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCertSerialNumber() {
        return certSerialNumber;
    }

    /**
     * Sets the value of the certSerialNumber property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCertSerialNumber(final String value) {
        this.certSerialNumber = value;
    }

}
