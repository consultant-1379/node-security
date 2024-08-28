/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.extidp.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.oss.services.nscs.extidp.ExtIdpDto;
import com.ericsson.oss.services.nscs.extidp.service.ExtIdpService;

@Path("extidp/")
public class ExtIdpRestResource {

    @Inject
    private Logger logger;

    @Inject
    private ExtIdpService extIdpService;

    @POST
    @Path("bind")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getBind(final ExtIdpDto dto) {

        String encValue = null;
        try {
            encValue = extIdpService.bind(dto.getValue());
        } catch (final Exception e) {
            logger.error("Exception : class [" + e.getClass().getName() + "] : msg [" + e.getMessage() + "]");
        }
        return Response.ok().entity(encValue).build();
    }
}
