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
package com.ericsson.oss.services.security.nscs.command.enrollmentinfo;

import java.util.Map;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsDownloadRequestMessageResponseBuilder;

/**
 * Auxiliary class to build the response to 'secadm generateenrollmentinfo' command.
 */
public class GenerateEnrollmentInfoResponseBuilder extends NscsDownloadRequestMessageResponseBuilder {

    private static final String NODE_COLUMN = "Node";

    public GenerateEnrollmentInfoResponseBuilder(final boolean isDownloadRequestMessage) {
        super(isDownloadRequestMessage);
    }

    /**
     * Build an error response to secadm generateenrollmentinfo command. Details of invalid nodes are reported too.
     * 
     * @param errorMessage
     *            the error message.
     * @param invalidNodes
     *            the map of the invalid nodes (key is the node FDN, value is an NscsServiceException).
     * @return the response.
     */
    public NscsCommandResponse buildErrorResponse(final String errorMessage, final Map<String, NscsServiceException> invalidNodes) {
        return buildErrorResponse(errorMessage, NODE_COLUMN, invalidNodes);
    }

    /**
     * Builds a partially successful command response returning a file and a table of invalid nodes.
     * 
     * @param fileIdentifier
     *            the file identifier.
     * @param errorMessage
     *            the error message.
     * @param invalidNodes
     *            the map of invalid nodes.
     * @return a {@link NscsDownloadRequestMessageCommandResponse} partially successful response.
     */
    public NscsCommandResponse buildPartialSuccessResponse(final String fileIdentifier, final String errorMessage,
            final Map<String, NscsServiceException> invalidNodes) {
        final NscsDownloadRequestMessageCommandResponse nscsDownloadReqMsgcommandResponse = (NscsDownloadRequestMessageCommandResponse) getResponse();
        nscsDownloadReqMsgcommandResponse.setFileIdentifier(fileIdentifier);
        nscsDownloadReqMsgcommandResponse.setMessage("");
        return buildErrorResponse(errorMessage, NODE_COLUMN, invalidNodes);
    }

}
