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
package com.ericsson.nms.security.nscs;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.data.workflow.WorkflowStatus;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;
import com.ericsson.oss.services.security.nscs.util.NscsRestResult;
import com.ericsson.oss.services.security.nscs.workflow.NscsWorkflowManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST related to workflows.
 */
@Path("workflow/")
public class WorkflowRestResource {

    private Logger logger = LoggerFactory.getLogger(WorkflowRestResource.class);

    @Inject
    private NscsWorkflowManager nscsWorkflowManager;

    @EServiceRef
    private WorkflowHandler workflowHandler;

    /**
     * Get from Workflow Query Service of Workflow Service the status of workflows related to a given <nodeList> (nodes separated with "&") path
     * parameter.
     * 
     * @param nodeList
     *            list of the nodes as a string separated with "&" for example
     * 
     *            http://localhost:8080/node-security/workflow/status?nodeList=node1&node2&node3
     * 
     *            A single node can be specified by node name or by FDN (both mirrored and normalized). So valid values are:
     * 
     *            netsim_LTE02ERBS00002
     * 
     *            SubNetwork=Europe,SubNetwork=Ireland,SubNetwork=ERBS-SUBNW-1,MeContext=netsim_LTE02ERBS00002
     * 
     *            NetworkElement=netsim_LTE02ERBS00002
     * 
     * @return the status of requested workflows.
     * @throws JsonProcessingException
     */
    @GET
    @Path("status/{nodelist}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWfStatus(@PathParam("nodelist") final String nodeList) throws JsonProcessingException {

        logger.debug("GET workflow status for node list: {}", nodeList);
        final Set<WorkflowStatus> workflowsStatus = nscsWorkflowManager.getWorkflowsStatus(nodeList);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(workflowsStatus);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Get from Workflow Query Service of Workflow Service the running workflows stats.
     * 
     * http://localhost:8080/node-security/workflow/stats
     * 
     * @return the stats of running workflows.
     * @throws JsonProcessingException
     */
    @GET
    @Path("stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWfStats() throws JsonProcessingException {

        logger.debug("GET running workflow stats");
        final Map<String, Set<NSCSWorkflowInstance>> workflowsStats = nscsWorkflowManager.getWorkflowsStats();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(workflowsStats);
        logger.debug(result);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Delete via Workflow Service a running workflow of given instance ID.
     * 
     * @param instanceId
     *            the workflow ID as returned by start workflow.
     * 
     * @return the result of the delete operation.
     * @throws JsonProcessingException
     */
    @DELETE
    @Path("{instanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteWfInstance(@PathParam("instanceId") final String instanceId) throws JsonProcessingException {

        final String inputParams = String.format("DELETE workflow instance ID %s", instanceId);
        logger.debug(inputParams);
        final String nscsResult = nscsWorkflowManager.deleteWorkflowInstance(instanceId);
        final NscsRestResult nscsRestResult = new NscsRestResult(inputParams, nscsResult);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nscsRestResult);
        logger.debug(result);
        return Response.status(Response.Status.OK).entity(result).build();
    }
}
