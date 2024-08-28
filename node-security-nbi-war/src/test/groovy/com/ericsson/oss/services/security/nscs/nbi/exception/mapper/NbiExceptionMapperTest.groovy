/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.exception.mapper

import javax.ws.rs.core.Response

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.exception.NscsSecurityViolationException
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.oss.services.security.nscs.nbi.exception.mapper.NbiExceptionMapper.CommandSyntaxExceptionHandler
import com.ericsson.oss.services.security.nscs.nbi.exception.mapper.NbiExceptionMapper.ExceptionHandler
import com.ericsson.oss.services.security.nscs.nbi.exception.mapper.NbiExceptionMapper.JsonProcessingExceptionHandler
import com.ericsson.oss.services.security.nscs.nbi.exception.mapper.NbiExceptionMapper.NetworkElementSecurityNotfoundExceptionHandler
import com.ericsson.oss.services.security.nscs.nbi.exception.mapper.NbiExceptionMapper.NodeDoesNotExistExceptionHandler
import com.ericsson.oss.services.security.nscs.nbi.exception.mapper.NbiExceptionMapper.NscsBadRequestExceptionHandler
import com.ericsson.oss.services.security.nscs.nbi.exception.mapper.NbiExceptionMapper.NscsSecurityViolationExceptionHandler
import com.fasterxml.jackson.core.JsonProcessingException

class NbiExceptionMapperTest extends CdiSpecification {

    def "build toResponse for NscsSecurityViolationException"() {
        given:
        def exception = new NscsSecurityViolationException()
        when:
        def handler = new NscsSecurityViolationExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for NscsBadRequestException"() {
        given:
        def exception = new NscsBadRequestException()
        when:
        def handler = new NscsBadRequestExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for JsonProcessingException"() {
        given:
        def exception = new JsonProcessingException("message")
        when:
        def handler = new JsonProcessingExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for NetworkElementSecurityNotfoundException"() {
        given:
        def exception = new NetworkElementSecurityNotfoundException()
        when:
        def handler = new NetworkElementSecurityNotfoundExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for CommandSyntaxException"() {
        given:
        def exception = new CommandSyntaxException()
        when:
        def handler = new CommandSyntaxExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for NodeDoesNotExistException"() {
        given:
        def exception = new NodeDoesNotExistException()
        when:
        def handler = new NodeDoesNotExistExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for a generic NscsServiceException not explicitly mapped"() {
        given:
        def exception = new NodeNotSynchronizedException()
        when:
        def handler = new ExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for an exception with causedBy without message"() {
        given:
        def causedBy = new NullPointerException()
        def exception = new UnexpectedErrorException("null pointer", causedBy)
        when:
        def handler = new ExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        and:
        response.getEntity() != null
    }

    def "build toResponse for an exception with causedBy with message"() {
        given:
        def causedBy = new NullPointerException("null obj")
        def exception = new UnexpectedErrorException("null pointer", causedBy)
        when:
        def handler = new ExceptionHandler()
        Response response = handler.toResponse(exception)
        then:
        response != null
        and:
        response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        and:
        response.getEntity() != null
    }
}
