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
 * {@link com.ericsson.nms.security.nscs.iscf.xml.ISCFEnrollmentData}
 */
public class ISCFEnrollmentDataDto {

    protected EnrollmentDataDto enrollmentData;

    /**
     * Gets the value of the enrollmentData property.
     *
     * @return possible object is {@link EnrollmentDataDto }
     *
     */
    public EnrollmentDataDto getEnrollmentData() {
        return enrollmentData;
    }

    /**
     * Sets the value of the enrollmentData property.
     *
     * @param value allowed object is {@link EnrollmentDataDto }
     *
     */
    public void setEnrollmentData(final EnrollmentDataDto value) {
        this.enrollmentData = value;
    }

}
