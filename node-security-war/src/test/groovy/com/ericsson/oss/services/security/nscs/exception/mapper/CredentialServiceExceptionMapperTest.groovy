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
package com.ericsson.oss.services.security.nscs.exception.mapper

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.CredentialServiceException

class CredentialServiceExceptionMapperTest extends CdiSpecification {

    @ObjectUnderTest
    CredentialServiceExceptionMapper credentialServiceExceptionMapper

    def "instance"() {
        expect:
        credentialServiceExceptionMapper != null
    }

    def "to response"() {
        given:
        def exception = new CredentialServiceException("this is the message")
        when:
        def response = credentialServiceExceptionMapper.toResponse(exception)
        then:
        response != null
    }
}
