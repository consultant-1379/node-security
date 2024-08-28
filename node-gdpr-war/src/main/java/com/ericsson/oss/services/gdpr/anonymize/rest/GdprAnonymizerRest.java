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
package com.ericsson.oss.services.gdpr.anonymize.rest;

import com.ericsson.oss.services.gdpr.anonymize.GdprAnonymizerDelegate;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("anonymizer/")
public class GdprAnonymizerRest {

    @Inject
    private GdprAnonymizerDelegate gdprAnonymizerDelegate;

    @Inject
    Logger logger;

    @POST
    @Path("anonymize")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getGdprAnonymizedName(final GdprAnonymizerDto dto) {

        final String filename = dto.getFilename();
        if (dto.getFilename() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Params null").build();
        }
        try {
            final String result = gdprAnonymizerDelegate.gdprBuildAnonymization(filename);
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("anonymizeWithSalt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getGdprAnonymizedNameWithSalt(final GdprAnonymizerDto dto) {

        final String fileName = dto.getFilename();
        final String salt = dto.getSalt();
        if ((fileName == null) || (salt == null)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Params null").build();
        }
        try {
            final String result = gdprAnonymizerDelegate.gdprBuildAnonymization(fileName, salt);
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }
    }
}