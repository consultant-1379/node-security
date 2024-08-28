/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.api.iscf;

/**
 * Represents the format of SubjectAltName
 *
 * SubjectAltNameFormat.NONE(0)
 * SubjectAltNameFormat.IPV4(1)
 * SubjectAltNameFormat.FQDN(2)
 * SubjectAltNameFormat.IPV6(3)
 *
 * @author ealemca
 * @author emacgma
 */
public enum SubjectAltNameFormat {

    NONE(0),
    IPV4(1),
    FQDN(2),
    IPV6(3),
    RFC822_NAME(4)
    ;

    private int subjectAltNameFormat;

    private SubjectAltNameFormat(final int subjectAltNameFormat) {
        this.subjectAltNameFormat = subjectAltNameFormat;
    }

    /**
     * Get the integer value of this SubjectAltNameFormat
     *
     * @return subjectAltNameFormat
     */
    public int toInt() {
        return this.subjectAltNameFormat;
    }

    /**
     * Get the String value of this SubjectAltNameFormat
     *
     * @return subjectAltNameFormat
     */
    @Override
    public String toString() {
        return String.valueOf(this.subjectAltNameFormat);
    }

}
