/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.nodes

import javax.ws.rs.core.Response

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException

class UnrecognizedPropertyExceptionMapperTest extends CdiSpecification {

    @ObjectUnderTest
    UnrecognizedPropertyExceptionMapper unrecognizedPropertyExceptionMapper

    def UnrecognizedPropertyException exception = Mock()

    def "build toResponse"() {
        given:

        when: "building toResponse"
        Response response = unrecognizedPropertyExceptionMapper.toResponse(exception)
        then: "response should not be null"
        response != null
        and: "response status should be BAD_REQUEST"
        response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
    }
}
