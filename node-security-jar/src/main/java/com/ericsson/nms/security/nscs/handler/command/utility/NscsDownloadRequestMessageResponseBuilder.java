/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.utility;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse;

public class NscsDownloadRequestMessageResponseBuilder extends NscsMultiInstanceCommandResponseBuilder {

    public NscsDownloadRequestMessageResponseBuilder(final boolean isDownloadRequestMessage) {
        super(isDownloadRequestMessage);
    }

    /**
     * Builds a successful command response returning a file.
     * 
     * @param fileIdentifier
     *            the file identifier.
     * @param message
     *            the message returned.
     * @return a {@link NscsDownloadRequestMessageCommandResponse} successful response.
     */
    public NscsCommandResponse buildSuccessResponse(final String fileIdentifier, final String message) {
        final NscsDownloadRequestMessageCommandResponse nscsDownloadReqMsgcommandResponse = (NscsDownloadRequestMessageCommandResponse) getResponse();
        nscsDownloadReqMsgcommandResponse.setFileIdentifier(fileIdentifier);
        nscsDownloadReqMsgcommandResponse.setMessage(message);
        return nscsDownloadReqMsgcommandResponse;
    }
}
