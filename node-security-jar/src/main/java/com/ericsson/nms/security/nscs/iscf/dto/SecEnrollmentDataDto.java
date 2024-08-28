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
 * {@link com.ericsson.nms.security.nscs.iscf.xml.SecEnrollmentData}
 */
public class SecEnrollmentDataDto
        extends ISCFEnrollmentDataDto {

    protected int rollbackTimeout;

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
