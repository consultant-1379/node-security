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

import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;

/**
 * Data Transfer Object for
 * {@link com.ericsson.nms.security.nscs.iscf.xml.IpsecEnrollmentData}
 */
public class IpsecEnrollmentDataDto {

    protected String subjectAltName;
    protected SubjectAltNameFormat subjectAltNameFormat;
    protected int rollbackTimeout;
    protected EnrollmentDataDto enrollmentData;

    /**
     * Gets the value of the enrollmentData property.
     *
     * @return possible object is {@link com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto }
     *
     */
    public EnrollmentDataDto getEnrollmentData() {
        return enrollmentData;
    }

    /**
     * Sets the value of the enrollmentData property.
     *
     * @param value allowed object is {@link com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto }
     *
     */
    public void setEnrollmentData(final EnrollmentDataDto value) {
        this.enrollmentData = value;
    }

    /**
     * Gets the value of the subjectAltName property
     *
     * @return subjectAltName
     */
    public String getSubjectAltName() {
        return subjectAltName;
    }

    /**
     * Gets the value of the subjectAltNameFormat property
     *
     * @return subjectAltNameFormat
     */
    public SubjectAltNameFormat getSubjectAltNameFormat() {

        return subjectAltNameFormat;
    }

    /**
     * Sets the value of the subjectAltName property
     *
     * @param subjectAltName
     */
    public void setSubjectAltName(final String subjectAltName) {
        this.subjectAltName = subjectAltName;
    }

    /**
     * Sets the value of the subjectAltNameFormat property
     *
     * @param subjectAltNameFormat
     */
    public void setSubjectAltNameFormat(final SubjectAltNameFormat subjectAltNameFormat) {
        this.subjectAltNameFormat = subjectAltNameFormat;
    }

    /**
     * Gets the value of the rollbackTimeout property.
     *
     * @return
     */
    public int getRollbackTimeout() {
        return rollbackTimeout;
    }

    /**
     * Sets the value of the rollbackTimeout property.
     *
     * @param value
     */
    public void setRollbackTimeout(final int value) {
        this.rollbackTimeout = value;
    }
}
