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
package com.ericsson.oss.services.nscs.cache.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.exception.RbacException;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.interfaces.CacheServiceBean;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("nodesCache/")
public class NodesCacheServiceRest {

    private static final String RBAC_ERROR_CODE = "11102";
    private static final String CACHE_CLEARED = "Cache Cleared";

    @Inject
    private Logger logger;

    @Inject
    CacheServiceBean cacheService;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response update() {
        logger.info("Forcing node cache update.");

        try {
            cacheService.update();
        } catch (final RbacException e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".count:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity("Cache Updated").build();

    }

    @Path("{offset}/{limit}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response cacheContent(@PathParam("offset") final int offset, @PathParam("limit") final String limit) {
        logger.info("getting cache content.");
        String result = "";
        try {

            final ObjectMapper mapper = new ObjectMapper();
            final List<NodesConfigurationStatusRecord> records = cacheService.content(offset, limit);
            result = mapper.writeValueAsString(records);

        } catch (final RbacException e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();

        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".count:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response cachecount() {
        logger.info("getting cache content size.");
        int count = 0;

        try {

            count = cacheService.count();

        } catch (final RbacException e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();

        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".count:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(count).build();

    }

    @Path("{nodeName}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNode(@PathParam(value = "nodeName") final String nodeName) {

        logger.info("Getting node" + nodeName);
        String result = "";

        try {

            final ObjectMapper mapper = new ObjectMapper();
            final NodesConfigurationStatusRecord record = cacheService.getNode(nodeName);

            if (record != null) {
                result = mapper.writeValueAsString(record);
            } else {
                throw new Exception("Node is not present in cache");
            }

        } catch (final RbacException e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();

        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".count:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(result).build();

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response clear() {

        logger.info("Clearing cache");
        String result = "";

        try {

            cacheService.clear();
            result = CACHE_CLEARED;

        } catch (final RbacException e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();

        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".count:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(result).build();

    }

    private String formatResponseMessage(final String code, final String message) {

        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> result = new HashMap<String, Object>();

        String jsonMessage = "";

        result.put("code", code);
        result.put("message", message);
        try {
            jsonMessage = mapper.writeValueAsString(result);

        } catch (final JsonGenerationException e) {
            logger.error("Error in: " + getClass() + ":" + e.getMessage());
        } catch (final JsonMappingException e) {
            logger.error("Error in: " + getClass() + ":" + e.getMessage());
        } catch (final IOException e) {
            logger.error("Error in: " + getClass() + ":" + e.getMessage());
        }

        return jsonMessage;

    }

}
