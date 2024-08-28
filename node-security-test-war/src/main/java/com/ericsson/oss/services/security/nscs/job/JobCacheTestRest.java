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
package com.ericsson.oss.services.security.nscs.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.ejb.startup.NscsJobCacheEvicter;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.jobs.NscsJobCacheHandlerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST used by Arquillian tests to verify job handlers.
 */
@Path("job/")
public class JobCacheTestRest {

    private Logger logger = LoggerFactory.getLogger(JobCacheTestRest.class);

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @EJB
    private NscsJobCacheEvicter cacheEvicter;

    private static final String TEST_NODE = "LTE03ERBS00003";

    private static int WF_CONGESTION_THRESHOLD = 250;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllJobs() {

        String result = "";
        final ObjectMapper mapper = new ObjectMapper();
        final List<JobDto> jobList = cacheHandler.getAllJobs();

        try {
            result = mapper.writeValueAsString(jobList);
        } catch (final Exception e) {
            logger.debug("[{}.{}] Error getting job List {}", getClass(), "getAllJobs", e.getMessage());
        }

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("{Ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobList(@PathParam("Ids") final String ids) {

        String result = "";
        final ObjectMapper mapper = new ObjectMapper();
        final List<String> idsList = Arrays.asList(ids.split(","));
        final List<UUID> uidList = new ArrayList<UUID>();

        for (final String s : idsList) {
            uidList.add(UUID.fromString(s));
        }
        final List<JobDto> jobs = cacheHandler.getJobList(uidList);

        try {
            result = mapper.writeValueAsString(jobs);
        } catch (final Exception e) {
            logger.debug("[{}.{}] Error getting job List {}", getClass(), "getJobList", e.getMessage());
        }

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertJob() {

        final JobStatusRecord jobStatusRecord = cacheHandler.insertJob(NscsCommandType.GET_JOB);
        final String msg = String.format("Created job: %s", jobStatusRecord.getJobId().toString());
        logger.debug(msg);
        return Response.status(Response.Status.OK).entity(msg).build();
    }

    @PUT
    @Path("{uuid}/{name}/{status}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response UpdateWf(@PathParam("uuid") final String s, @PathParam("name") final String name,
                             @PathParam("status") final WfStatusEnum status) {

        WfResult result = null;
        final UUID uuid = UUID.fromString(s);
        String msg = "";
        Response.Status httpStatus = Response.Status.OK;

        final JobDto record = cacheHandler.getJob(uuid);
        if (record != null) {

            logger.info("JOB PUT REST on job id {}", record.getJobId().toString());

            final JobStatusRecord jobStatusRecord = cacheHandler.getJobStatusRecord(record.getJobId());

            boolean found = false;

            for (final Map.Entry<String, WfResult> e : record.getStatus().entrySet()) {

                result = e.getValue();

                if (result != null && result.getNodeName().equals(name)) {
                    result.setStatus(status);

                    msg = String.format("Updated job: %s", uuid);
                    cacheHandler.updateWorkflow(result);

                    found = true;

                    break;
                }

            }

            if (!found) {

                final String wfResultUUIDString = jobStatusRecord.getJobId().toString() + "1";

                final UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes());
                logger.info("JOB PUT REST generated workflow id {}", wfWakeId.toString());

                result = new WfResult();
                result.setWfId("N/A");
                result.setWfWakeId(wfWakeId);
                result.setJobId(uuid);
                result.setNodeName(name);
                result.setStatus(status);
                result.setMessage("Workflow " + wfWakeId + " terminated with success test-id: " + (new Date()).getTime());

                final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>();
                map.put(result.getWfWakeId(), result);
                //Insert wf in cache and set the job wf size list
                cacheHandler.insertWorkflowBatch(map);
                //Fake update of wf and set the job status (dep on wf status)
                cacheHandler.updateWorkflow(result);
            }

        } else {
            msg = String.format("Job id %s does not exist!", s);
            httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
        }
        logger.debug(msg);
        return Response.status(httpStatus).entity(msg).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response clearCache() {

        cacheHandler.clearCache();
        final String msg = "Cache cleaned";

        logger.debug(msg);
        return Response.status(Response.Status.OK).entity(msg).build();
    }

    @DELETE
    @Path("{interval}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response evictCache(@PathParam("interval") final long interval) {

        cacheEvicter.evict(interval);
        final String msg = String.format("Evicted cache entires older than %s msec", interval);

        logger.debug(msg);
        return Response.status(Response.Status.OK).entity(msg).build();
    }

    @GET
    @Path("pending")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPendingWorkflows() {

        int count = 0;
        final List<WfResult> pendingWfs = cacheHandler.getPendingWorkflows();
        if (pendingWfs != null) {
            count = pendingWfs.size();
        }
        final String msg = String.format("There are %s PENDING workflows", count);
        logger.debug(msg);

        return Response.status(Response.Status.OK).entity(msg).build();
    }

    @GET
    @Path("running")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkRunningWorkflows() {

        final int count = cacheHandler.getRunningWorkflowsCount();
        final String msg = String.format("There are %s RUNNING workflows", count);
        logger.debug(msg);

        return Response.status(Response.Status.OK).entity(msg).build();
    }

    @GET
    @Path("runningOnTestNode/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRunningWFonTestNode(@PathParam("name") final String name) {

        final String nodeName = name != null ? name : TEST_NODE;
        final Map<String, List<WfResult>> wfMap = cacheHandler.getRunningAndPendingWorkflows(WF_CONGESTION_THRESHOLD);
        final int count = (cacheHandler.checkNoRunningWFByNodeName(nodeName, wfMap.get(NscsJobCacheHandlerImpl.WF_KEY_RUNNING))) ? 0 : 1;
        final String msg = String.format("There are %s RUNNING workflows on test node:%s", count, nodeName);
        return Response.status(Response.Status.OK).entity(msg).build();
    }

}
