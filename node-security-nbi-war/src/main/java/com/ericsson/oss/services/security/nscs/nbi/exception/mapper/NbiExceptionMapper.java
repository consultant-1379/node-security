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
package com.ericsson.oss.services.security.nscs.nbi.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.exception.NscsSecurityViolationException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.ErrorResponseNbiDto;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * NBI exception mapper
 */
public class NbiExceptionMapper {

    private NbiExceptionMapper() {
        // intentionally left empty
    }

    @Provider
    public static class NscsSecurityViolationExceptionHandler implements ExceptionMapper<NscsSecurityViolationException> {

        @Override
        public Response toResponse(final NscsSecurityViolationException exception) {
            return buildResponse(exception, Response.Status.FORBIDDEN);
        }
    }

    @Provider
    public static class NscsBadRequestExceptionHandler implements ExceptionMapper<NscsBadRequestException> {

        @Override
        public Response toResponse(final NscsBadRequestException exception) {
            return buildResponse(exception, Response.Status.BAD_REQUEST);
        }
    }

    @Provider
    public static class JsonProcessingExceptionHandler implements ExceptionMapper<JsonProcessingException> {
        final Logger logger = LoggerFactory.getLogger(JsonProcessingExceptionHandler.class);

        @Override
        public Response toResponse(final JsonProcessingException exception) {
            logger.info("Exception: [{}]", exception.getClass().getCanonicalName());
            logger.info("message: [{}]", exception.getMessage());
            final Throwable causedBy = exception.getCause();
            if (causedBy != null) {
                logger.info("causedBy: [{}]", causedBy.getClass().getCanonicalName());
                logger.info("message: [{}]", causedBy.getMessage());
            } else {
                logger.info("NULL causedBy");
            }
            return buildResponse(exception, Response.Status.BAD_REQUEST);
        }
    }

    @Provider
    public static class NetworkElementSecurityNotfoundExceptionHandler implements ExceptionMapper<NetworkElementSecurityNotfoundException> {

        @Override
        public Response toResponse(final NetworkElementSecurityNotfoundException exception) {
            return buildResponse(exception, Response.Status.BAD_REQUEST);
        }
    }

    @Provider
    public static class CommandSyntaxExceptionHandler implements ExceptionMapper<CommandSyntaxException> {

        @Override
        public Response toResponse(final CommandSyntaxException exception) {
            return buildResponse(exception, Response.Status.BAD_REQUEST);
        }
    }

    @Provider
    public static class NodeDoesNotExistExceptionHandler implements ExceptionMapper<NodeDoesNotExistException> {

        @Override
        public Response toResponse(final NodeDoesNotExistException exception) {
            return buildResponse(exception, Response.Status.NOT_FOUND);
        }
    }

    @Provider
    public static class ExceptionHandler implements ExceptionMapper<Exception> {
        final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

        @Override
        public Response toResponse(final Exception exception) {
            logger.info("Exception: [{}]", exception.getClass().getCanonicalName());
            logger.info("message: [{}]", exception.getMessage());
            final Throwable causedBy = exception.getCause();
            if (causedBy != null) {
                logger.info("causedBy: [{}]", causedBy.getClass().getCanonicalName());
                logger.info("message: [{}]", causedBy.getMessage());
            } else {
                logger.info("NULL causedBy");
            }
            return buildResponse(exception, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Build response for the given exception and the given response status.
     * 
     * @param exception
     *            the exception.
     * @param responseStatus
     *            the response status.
     * @return the response.
     */
    private static Response buildResponse(final Exception exception, final Response.Status responseStatus) {
        final ErrorResponseNbiDto errorResponse = NbiExceptionMapper.buildNbiErrorResponse(exception, responseStatus);
        return Response.status(responseStatus).entity(errorResponse).build();
    }

    /**
     * Build NBI error response DTO for the given exception and the given response status.
     * 
     * @param exception
     *            the exception.
     * @param responseStatus
     *            the response status.
     * @return the NBI error response DTO.
     */
    private static ErrorResponseNbiDto buildNbiErrorResponse(final Exception exception, final Response.Status responseStatus) {
        final ErrorResponseNbiDto errorResponse = new ErrorResponseNbiDto();
        errorResponse.setHttpStatus(String.format("%s - %s", responseStatus.getStatusCode(), responseStatus.getReasonPhrase()));
        errorResponse.setMessage(exception.getMessage());
        final String causedBy = (exception.getCause() != null)
                ? String.format("%s: %s", exception.getCause().getClass().getCanonicalName(), exception.getCause().getMessage())
                : null;
        errorResponse.setCausedBy(causedBy);
        if (exception instanceof NscsServiceException) {
            errorResponse.setSuggestedSolution(((NscsServiceException) exception).getSuggestedSolution());
        }
        return errorResponse;
    }

}
