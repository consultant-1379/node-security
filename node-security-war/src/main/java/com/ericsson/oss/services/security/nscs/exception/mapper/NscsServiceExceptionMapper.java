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
package com.ericsson.oss.services.security.nscs.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.oss.services.security.nscs.rest.response.ErrorResponse;

@Provider
public class NscsServiceExceptionMapper implements ExceptionMapper<NscsServiceException> {

    @Override
    public Response toResponse(final NscsServiceException e) {
        final ErrorResponse errorResponse = getErrorReport(e);
        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }

    private ErrorResponse getErrorReport(final NscsServiceException e) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setUserMessage(e.getMessage());
        errorResponse.setDeveloperMessage(e.getSuggestedSolution());
        errorResponse.setInternalErrorCode(String.valueOf(e.getErrorCode()));
        errorResponse.setErrorData(e.getErrorType().name());
        return errorResponse;
    }
}
