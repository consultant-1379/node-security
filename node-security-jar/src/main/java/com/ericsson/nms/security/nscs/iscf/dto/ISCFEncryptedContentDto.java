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
 * {@link com.ericsson.nms.security.nscs.iscf.xml.ISCFEncryptedContent}
 *
 */
public class ISCFEncryptedContentDto {

    protected EncryptedContentDto encryptedContent;

    /**
     * Gets the value of the encryptedContent property.
     *
     * @return possible object is {@link EncryptedContent }
     *
     */
    public EncryptedContentDto getEncryptedContent() {
        return encryptedContent;
    }

    /**
     * Sets the value of the encryptedContent property.
     *
     * @param value allowed object is {@link EncryptedContent }
     *
     */
    public void setEncryptedContent(final EncryptedContentDto value) {
        this.encryptedContent = value;
    }

}
