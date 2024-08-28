/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse.NscsCommandResponseType

import spock.lang.Unroll

class NscsDownloadRequestMessageCommandResponseTest extends CdiSpecification {

    def "constructor with number of columns"() {
        given:
        def response = new NscsDownloadRequestMessageCommandResponse(2)
        expect:
        response != null
        and:
        response.getResponseType() == NscsCommandResponseType.DOWNLOAD_REQ_MESSAGE
        response.isDownloadRequestMessageResponseType() == true
        and:
        response.getValueSize() == 2
        and:
        response.getFileIdentifier() == null
        response.getMessage() == null
    }

    def "constructor with number of columns, file identifier, message"() {
        given:
        def response = new NscsDownloadRequestMessageCommandResponse(2, "this is the fileidentifier", "this is the message")
        expect:
        response != null
        and:
        response.getResponseType() == NscsCommandResponseType.DOWNLOAD_REQ_MESSAGE
        response.isDownloadRequestMessageResponseType() == true
        and:
        response.getValueSize() == 2
        and:
        response.getFileIdentifier() == "this is the fileidentifier"
        response.getMessage() == "this is the message"
    }

    @Unroll
    def "set file identifier #fileidentifier, message #message "() {
        given:
        def response = new NscsDownloadRequestMessageCommandResponse(2)
        and:
        response.setFileIdentifier(fileidentifier)
        response.setMessage(message)
        expect:
        response != null
        and:
        response.getResponseType() == NscsCommandResponseType.DOWNLOAD_REQ_MESSAGE
        response.isDownloadRequestMessageResponseType() == true
        and:
        response.getValueSize() == 2
        and:
        response.getFileIdentifier() == fileidentifier
        response.getMessage() == message
        where:
        fileidentifier << [
            null,
            "this is the fileidentifier"
        ]
        message << [null, "this is the message"]
    }
}
