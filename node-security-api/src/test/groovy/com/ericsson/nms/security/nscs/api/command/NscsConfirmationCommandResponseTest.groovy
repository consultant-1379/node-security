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

class NscsConfirmationCommandResponseTest extends CdiSpecification {

    @Unroll
    def "constructor with confirmation message #confirmation"() {
        given:
        def response = new NscsConfirmationCommandResponse(confirmation)
        expect:
        response != null
        and:
        response.getResponseType() == NscsCommandResponseType.CONFIRMATION
        response.isConfirmationResponseType() == true
        and:
        response.getConfirmationMessage() == confirmation
        response.getAdditionalConfirmationMessages().isEmpty() == true
        where:
        confirmation << [
            null,
            "this is the confirmation message"
        ]
    }

    @Unroll
    def "set confirmation message #confirmation"() {
        given:
        def response = new NscsConfirmationCommandResponse("this is the confirmation message")
        and:
        response.setConfirmationMessage(confirmation)
        expect:
        response != null
        and:
        response.getResponseType() == NscsCommandResponseType.CONFIRMATION
        response.isConfirmationResponseType() == true
        and:
        response.getConfirmationMessage() == confirmation
        response.getAdditionalConfirmationMessages().isEmpty() == true
        where:
        confirmation << [
            null,
            "this is the new confirmation message"
        ]
    }

    def "set additional confirmation messages"() {
        given:
        def response = new NscsConfirmationCommandResponse("this is the confirmation message")
        and:
        response.setAdditionalConfirmationMessages("this is the first addititional confirmation message", "this is the second addititional confirmation message")
        expect:
        response != null
        and:
        response.getResponseType() == NscsCommandResponseType.CONFIRMATION
        response.isConfirmationResponseType() == true
        and:
        response.getConfirmationMessage() == "this is the confirmation message"
        response.getAdditionalConfirmationMessages().isEmpty() == false
        response.getAdditionalConfirmationMessages().size() == 2
    }
}
