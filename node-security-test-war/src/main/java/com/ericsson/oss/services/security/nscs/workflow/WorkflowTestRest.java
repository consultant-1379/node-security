/*------------------------------------------------------------------------------
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
package com.ericsson.oss.services.security.nscs.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.workflow.WorkflowStatus;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance.WorkflowInstanceStatus;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

/**
 * REST used by Arquillian tests to verify workflow handlers.
 */
@Path("workflow/")
public class WorkflowTestRest {

    @Inject
    private Logger logger;

    @EServiceRef
    private WfQueryService wfQuery;

    @EServiceRef
    private WorkflowHandler workflowHandler;

    @Inject
    private NscsInstrumentationService nscsInstrumentationService;

    /**
     * Rest method to instantiate security workflows.
     * 
     * @param wfName
     *            name of the WorkFlow defined in the WorkflowService. At the moment there is only 1 workflow named "proto"
     * @param nodeList
     *            list of the nodes as a string separated with "&" for example
     * 
     *            http://localhost:8080/node-security/test/workflow/proto/node1&node2&node3
     * 
     *            http://localhost:8080/node-security/test/workflow/proto/node1&node2&node3?wfVariables=key1=value1,key2=value2
     * 
     * @return
     */
    @GET
    @Path("{wfName}/{nodeList}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getConfigFile(@PathParam("wfName") final String wfName, @PathParam("nodeList") final String nodeList, @QueryParam("wfVariables") final String wfVariables) {
        // @QueryParam("iscreate") final String isCreate,
        // @QueryParam("algorithm") final AlgorithmKeys algorithm

        logger.debug("RestResource invoked. Workflow name:  [{}], list of nodes: [{}], wfVariables [{}]", wfName, nodeList, wfVariables);
        try {
            final String[] nodeArray = nodeList.split("&");
            final List<NodeReference> nodes = NodeRef.from(nodeArray);

            Set<WorkflowInstance> wfInstances;

            if (wfVariables != null && !wfVariables.isEmpty()) {
                logger.debug("Invoking worflow with wfVariables");
                final Map<String, Object> workflowVars = new HashMap<String, Object>();

                // split key,value pair in wfVariablesby comma [,]
                for (String pair : wfVariables.split(",")) {
                    // split key and value by equal [=]
                    String[] kv = pair.split("=");
                    // set workflow variable name and value
                    workflowVars.put(kv[0], kv[1]);

                    logger.debug("key: [{}], value: [{}]", kv[0], kv[1]);
                }

                wfInstances = workflowHandler.startWorkflowInstances(nodes, wfName, workflowVars);
            } else {
                logger.debug("Invoking worflow without wfVariables");
                wfInstances = workflowHandler.startWorkflowInstances(nodes, wfName);
            }

            final String result = String.format("Workflow \"%s\" initiated. Number of workflows: %s. ", wfName, wfInstances.size());
            logger.info(result);
            return Response.status(Response.Status.OK).entity(result).build();

        } catch (final Exception e) {
            logger.warn("Failed to instantiate workflow");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to instantiate workflow: " + e.getMessage()).build();
        }
    }

    /**
     * Rest method to get workflows status for nodes list. If nodeList is null or empty, all nodes with Network Element Security MO will be returned.
     * 
     * @param nodeList
     *            list of the nodes as a string separated with "&" for example
     * 
     *            http://localhost:8080/node-security/test/workflow/getwfstatus?nodeList=node1&node2&node3
     * 
     *            http://localhost:8080/node-security/test/workflow/getwfstatus
     * @return
     */
    @GET
    @Path("getwfstatus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getWfStatus(@QueryParam("nodeList") final String nodeList) {

        logger.debug("RestResource getwfstatus invoked");

        final List<NodeReference> nodes = new ArrayList<NodeReference>();

        if (nodeList != null && !nodeList.isEmpty()) {
            logger.debug("List of nodes: [{}]", nodeList);
            final String[] nodeArray = nodeList.split("&");
            nodes.addAll(NodeRef.from(nodeArray));
        } else {
            // TODO Read all nodes having Network Element Security MO created
            logger.debug("Empty List of nodes, data from DPS must be read TO BE DONE!");
        }

        final HashSet<NodeReference> nodeRefSet = new HashSet<>(nodes);
        final Set<WorkflowStatus> workflowsStatusSet = wfQuery.getWorkflowStatus(nodeRefSet);

        String result = "";
        for (WorkflowStatus wfs : workflowsStatusSet) {
            result += String.format("getStepName [%s], getWorkflowInstance [%s], getEventTime [%s], getEventType [%s]", wfs.getStepName(), wfs.getWorkflowInstance(), wfs.getEventTime().toString(),
                    wfs.getEventType());
        }

        logger.info(result);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Rest method to get workflows stats, eg number of running workflow
     * 
     * http://localhost:8080/node-security/test/workflow/getwfstats
     * 
     * @return
     */
    @GET
    @Path("getwfstats")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getWfStats() {

        logger.debug("RestResource getwfstats invoked");

        final Map<String, Set<NSCSWorkflowInstance>> workflowsStatsSet = wfQuery.getWorkflowRunningInstancesByName();

        String result = "";
        int totcount = 0;
        for (Map.Entry<String, Set<NSCSWorkflowInstance>> entry : workflowsStatsSet.entrySet()) {

            String instanceDetails = "";
            for (NSCSWorkflowInstance instance : entry.getValue()) {
                instanceDetails += String.format("{key: %s, executionId: %s},", instance.getBusinessKey(), instance.getExecutionId());
            }

            result += String.format("Workflow [%s] - num. of running instances [%s], instancesDetails [%s] \n", entry.getKey(), entry.getValue().size(), instanceDetails);
            totcount += entry.getValue().size();
        }

        result += String.format("Total num. [%s] \n", totcount);

        logger.info(result);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Rest method to get the final status o workflows that were started and which instance was saved into NscsInstrumentationBean
     * 
     * http://localhost:8080/node-security/test/workflow/getwffinalstatus
     * 
     * @return
     */
    @GET
    @Path("getwffinalstatus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getWfFinalStatus() {

        logger.info("RestResource getwffinalstatus invoked");

        // invoke the getNumOfRunningWorkflows() to have also the map of
        // completed workflows updated with final status
        long runningWf = nscsInstrumentationService.getNumOfRunningWorkflows();
        logger.info("Got [{}] running workflows", runningWf);

        int completed = 0;
        //		for (Map.Entry<String, NSCSWorkflowInstance> entry : nscsInstrumentedBean
        //				.getWorkflowInstancesMap().entrySet()) {
        //			if (entry.getValue().getState() != WorkflowInstanceStatus.RUNNING) {
        //				logger.info("Instance of workflow completed [{}]", entry
        //						.getValue().toString());
        //				completed++;
        //			}
        //		}

        String result = String.format("Total [%s] completed, See log for results!", completed);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Rest method to get the total counter of completed workflows given the final status [FAILED, SUCCESS]
     * 
     * http://localhost:8080/node-security/test/workflow/getwffinalstatus/failed/count
     * 
     * http://localhost:8080/node-security/test/workflow/getwffinalstatus/success/count
     * 
     * @return
     */
    @GET
    @Path("getwffinalstatus/{status}/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getWfFinalStatusCount(@PathParam("status") final String status) {

        logger.info("RestResource getwffinalstatus/[{}]/count invoked", status);
        String result = "No data!!!";

        WorkflowInstanceStatus wiStatus = WorkflowInstanceStatus.valueOf(status.toUpperCase());

        logger.info("Detected WorkflowInstanceStatus [{}]", wiStatus.name());
        switch (wiStatus) {
        case FAILED:
        case TIMEOUT:
            logger.info("Getting getNumOfFailedWorkflows()");
            result = String.valueOf(nscsInstrumentationService.getNumOfFailedWorkflows());
            break;
        case SUCCESS:
            logger.info("Getting getNumOfSuccessfulWorkflows()");
            result = String.valueOf(nscsInstrumentationService.getNumOfSuccessfulWorkflows());
            break;
        default:
            logger.info("Default");
            break;
        }

        // logger.info(result);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Rest method to reset the data saved into NscsInstrumentationBean for started workflow
     * 
     * http://localhost:8080/node-security/test/workflow/resetwfinstances
     * 
     * @return
     */
    @GET
    @Path("resetwfinstances")
    @Produces(MediaType.TEXT_PLAIN)
    public Response resetwfinstances() {

        logger.debug("RestResource resetwfinstances invoked");
        String result = "";

        //		nscsInstrumentedBean.resetWorkflowInstancesMap();

        result = String.format("Resetting workflowInstancesMap on Instrumented Bean, actual size [%s]", 0);

        logger.info(result);

        // logger.info(result);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * REst method to delete a running workflow instance
     * 
     * http://localhost:8080/node-security/test/workflow/delete/{instance}
     * 
     * @return
     */
    @GET
    @Path("delete/{instanceId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deletewfinstance(@PathParam("instanceId") final String instanceId) {

        logger.debug("RestResource delete invoked with WF instance [{}]", instanceId);
        String result = "";

        try {
            workflowHandler.cancelWorkflowInstance(instanceId);
        } catch (Exception e) {
            String error = String.format("Exception when cancelling workflow instance [%s], error [%s]", instanceId, e.getMessage());
            logger.error(error);
            return Response.status(Response.Status.OK).entity(error).build();
        }

        result = String.format("Cancelled workflow instance [%s] result [%s]", instanceId, true);

        logger.info(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

}
