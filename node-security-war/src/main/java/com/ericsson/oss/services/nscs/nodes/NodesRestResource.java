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
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.cpp.seclevel.dto.SecLevelDTO;
import com.ericsson.nms.security.nscs.cpp.seclevel.dto.SecLevelSwitchStatusDTO;
import com.ericsson.nms.security.nscs.exception.RbacException;
import com.ericsson.nms.security.nscs.model.SecurityLevelSwitchStatus;
import com.ericsson.nms.security.nscs.rest.local.service.NodeSecuritySeviceLocal;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.NodesDTO;
import com.ericsson.oss.services.nodes.util.HttpUtil;
import com.ericsson.oss.services.nscs.nodes.interfaces.NscsNodesListHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.security.nscs.interceptor.RestLoggerInterceptor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("nodes/")
public class NodesRestResource {

    private static final String RBAC_ERROR_CODE = "11102";

    @Inject
    private Logger logger;

    @Context
    private HttpServletRequest request;

    @Inject
    NscsNodesListHandler nodesListHandler;

    @EServiceRef
    NodeSecuritySeviceLocal nodeSecuritySeviceLocal;

    @Inject
    private NscsContextService nscsContextService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(final NodesDTO dto) {

        logger.info("Fetching Nodes.");

        final ObjectMapper mapper = new ObjectMapper();
        List<NodesConfigurationStatusRecord> page = new ArrayList<NodesConfigurationStatusRecord>();

        String userId = null;
        String result = "";

        try {
            userId = HttpUtil.getUserIdFromHeader(request);
            page = nodesListHandler.getPage(dto, userId);
            result = mapper.writeValueAsString(page);

        } catch (final RbacException e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();

        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".fetch:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();
        }

        return Response.status(Response.Status.OK).entity(result).build();

    }

    @Path("count")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response count(final NodesDTO dto) {
        logger.info("Counting Nodes.");

        int count = 0;
        String userId = null;
        try {
            userId = HttpUtil.getUserIdFromHeader(request);
            count = nodesListHandler.getCount(dto, userId);

        } catch (final RbacException e) {
            logger.error("Error in: " + getClass() + ".count:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();

        } catch (final Exception e) {
            logger.error("Error in: " + getClass() + ".count:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(formatResponseMessage(RBAC_ERROR_CODE, e.getMessage())).build();
        }

        return Response.status(Response.Status.OK).entity(count).build();

    }

    /**
     * This method is apply the Security level change initiation from Level (1 to 2) and (2 to 1)
     *
     * @return JSON which contains success or failure messages of the security level initiation
     */

    @POST
    @Path("seclevel")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Interceptors({ RestLoggerInterceptor.class })
    public Response changeSecurityLevel(final SecLevelDTO secLevelDTO) {

        final List<SecLevelSwitchStatusDTO> secLevelSwitchStatusDTOList = new ArrayList<SecLevelSwitchStatusDTO>();
        List<SecurityLevelSwitchStatus> secLevelSwitchStatusList = new ArrayList<SecurityLevelSwitchStatus>();

        logger.debug("Get size of nodes that are sent for process {} ", secLevelDTO.getNodeNames().size());
        logger.debug("Get the security level that is needed for the nodes {} ", secLevelDTO.getWantedSecLevel());
        try {
            SecurityLevel securityLevel = SecurityLevel.getSecurityLevel(secLevelDTO.getWantedSecLevel());
            secLevelSwitchStatusList = nodeSecuritySeviceLocal.changeSecurityLevel(secLevelDTO.getNodeNames(), securityLevel);
            //        } catch (final SecurityViolationException e) {
            //            return Response.status(Status.BAD_REQUEST).entity("The User is not authorized to perform the operation, " + e.getMessage()).build();
            //        }
        } catch (final Exception e) {

            String msg = "Error has occurred";
            if (e instanceof SecurityViolationException) {
                msg = "The User is not authorized to perform the operation";
            }

            logger.error(msg + "," + e.getMessage());

            setContextErrorDetail(e);

            return Response.status(Status.BAD_REQUEST).entity(msg).build();
        }
        for (final SecurityLevelSwitchStatus secLevelSwitchStatus : secLevelSwitchStatusList) {

            final SecLevelSwitchStatusDTO secLevelSwitchStatusDTO = new SecLevelSwitchStatusDTO();
            secLevelSwitchStatusDTO.setCode(secLevelSwitchStatus.getCode());
            secLevelSwitchStatusDTO.setNodeName(secLevelSwitchStatus.getNodeName());
            secLevelSwitchStatusDTO.setMessage(secLevelSwitchStatus.getMessage());
            secLevelSwitchStatusDTOList.add(secLevelSwitchStatusDTO);

        }

        return Response.status(Status.OK).entity(secLevelSwitchStatusDTOList).build();
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
        if (e instanceof SecurityViolationException || e.getCause() instanceof SecurityViolationException) {
            errorDetail = "Security violation exception.";
        } else if (e instanceof InvalidNodeException) {
            errorDetail = "Empty node list.";
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
