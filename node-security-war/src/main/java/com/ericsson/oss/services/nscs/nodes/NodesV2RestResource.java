/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.exception.RbacException;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.NodeNamesDTO;
import com.ericsson.oss.services.nscs.nodes.interfaces.NscsNodesListHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.security.nscs.interceptor.RestLoggerInterceptor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("2.0/nodes/")
public class NodesV2RestResource {

    private static final String RBAC_ERROR_CODE = "11102";

    @Inject
    private Logger logger;

    @Inject
    NscsNodesListHandler nodesListHandler;

    @Context
    private HttpServletRequest request;

    @Inject
    private NscsContextService nscsContextService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors({ RestLoggerInterceptor.class })
    public Response fetch(final NodeNamesDTO dto) {

        logger.info("Fetching Nodes.");
        final ObjectMapper mapper = new ObjectMapper();
        List<NodesConfigurationStatusRecord> page = new ArrayList<NodesConfigurationStatusRecord>();

        String result = "";

        try {
            page = nodesListHandler.getNodes(dto);
            result = mapper.writeValueAsString(page);
        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());

            setContextErrorDetail(e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();
        }

        return Response.status(Response.Status.OK).entity(result).build();

    }

    /**
     * Set error detail in context for the given exception.
     * 
     * @param e
     *            the exception.
     */
    private void setContextErrorDetail(final Exception e) {
        logger.error("REST_CAL with exception [{}] caused by [{}]", e.getClass().getCanonicalName(),
                e.getCause() != null ? e.getCause().getClass().getCanonicalName() : null);
        String errorDetail = e.getMessage() != null ? e.getMessage() : String.format("Exception [%s] occurred.", e.getClass().getCanonicalName());
        if (e instanceof RbacException || e.getCause() instanceof RbacException) {
            errorDetail = "Security violation exception.";
        }
        nscsContextService.setErrorDetailContextValue(errorDetail);
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
