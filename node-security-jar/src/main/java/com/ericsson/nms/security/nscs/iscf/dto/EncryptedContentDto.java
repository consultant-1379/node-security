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

import java.math.BigInteger;

/**
 * Data Transfer Object for
 * {@link com.ericsson.nms.security.nscs.iscf.xml.EncryptedContent}
 */
public class EncryptedContentDto {

    protected byte[] value;
    protected byte[] pbkdf2Salt;
    protected BigInteger pbkdf2IterationCount;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is byte[]
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is byte[]
     */
    public void setValue(final byte[] value) {
        this.value = value;
    }

    /**
     * Gets the value of the pbkdf2Salt property.
     *
     * @return possible object is byte[]
     */
    public byte[] getPBKDF2Salt() {
        return pbkdf2Salt;
    }

    /**
     * Sets the value of the pbkdf2Salt property.
     *
     * @param value allowed object is byte[]
     */
    public void setPBKDF2Salt(final byte[] value) {
        this.pbkdf2Salt = value;
    }

    /**
     * Gets the value of the pbkdf2IterationCount property.
     *
     * @return possible object is {@link BigInteger }
     *
     */
    public BigInteger getPBKDF2IterationCount() {
        return pbkdf2IterationCount;
    }

    /**
     * Sets the value of the pbkdf2IterationCount property.
     *
     * @param value allowed object is {@link BigInteger }
     *
     */
    public void setPBKDF2IterationCount(final BigInteger value) {
        this.pbkdf2IterationCount = value;
    }

}
