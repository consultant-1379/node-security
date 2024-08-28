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
package com.ericsson.oss.services.gdpr.anonymize.rest;

import java.io.Serializable;

/**
 * Class used for POST REST : map Json parameter given to POST
 */
public class GdprAnonymizerDto implements Serializable {

    private static final long serialVersionUID = 5822610754917181392L;
    private String filename;
    private String salt;

    public String getFilename() {
        return filename;
    }

    public String getSalt() {
        return salt;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public void setSalt(final String salt) {
        this.salt = salt;
    }
}
