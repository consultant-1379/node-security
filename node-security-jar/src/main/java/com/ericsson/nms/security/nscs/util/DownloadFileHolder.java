/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.util;

import java.io.Serializable;

/**
 * Bean class to keep the file content with corresponding details
 * 
 * @author tcsmave
 *
 */
public class DownloadFileHolder implements Serializable {

    private static final long serialVersionUID = -8153187032734754082L;

    String fileName;
    String contentType;
    byte[] contentToBeDownloaded;
    boolean isDeletable = false;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String filename) {
        this.fileName = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContentToBeDownloaded() {
        return contentToBeDownloaded;
    }

    public void setContentToBeDownloaded(final byte[] contentToBeDownloaded) {
        this.contentToBeDownloaded = contentToBeDownloaded;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(final boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

}
