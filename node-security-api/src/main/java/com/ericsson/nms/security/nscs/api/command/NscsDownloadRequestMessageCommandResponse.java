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

package com.ericsson.nms.security.nscs.api.command;

/**
 * 
 * A subclass of NscsNameMultipleValueCommandResponse representing the download request to script-engine. This class should be used when a File download along with a message has to be sent to
 * script-engine
 * 
 * @author xgvgvgv
 * 
 */
public class NscsDownloadRequestMessageCommandResponse extends NscsNameMultipleValueCommandResponse {

    private static final long serialVersionUID = 1L;
    private String fileIdentifier = null;
    private String message;

    public NscsDownloadRequestMessageCommandResponse(int numberOfColumns) {
        super(numberOfColumns);
    }

    public NscsDownloadRequestMessageCommandResponse(final int numberOfColumns, final String fileIdentifier, final String message) {
        super(numberOfColumns);

        this.fileIdentifier = fileIdentifier;
        this.message = message;
    }

    @Override
    public NscsCommandResponseType getResponseType() {
        return NscsCommandResponseType.DOWNLOAD_REQ_MESSAGE;
    }

    /**
     * Unique key used by webcli to store file in memory
     * 
     * @return the file identifier used by webcli
     */
    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(final String fileId) {
        this.fileIdentifier = fileId;
    }

    /**
     * @return the response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     * 
     * @param message
     *            the response message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

}
