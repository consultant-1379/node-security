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
package com.ericsson.oss.services.nscs.cache.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.security.nscs.job.NscsJobCacheManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST related to job cache.
 */
@Path("job/")
public class JobCacheServiceRest {

    private Logger logger = LoggerFactory.getLogger(JobCacheServiceRest.class);

    @Inject
    private NscsJobCacheManager nscsJobCacheManager;

    /**
     * Get all jobs present in NSCS job cache.
     * 
     * @return the jobs.
     * @throws JsonProcessingException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllJobs() throws JsonProcessingException {

        logger.debug("GET all jobs");
        final List<JobDto> jobDtos = nscsJobCacheManager.getAllJobs();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jobDtos);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Get job list present in NSCS job cache.
     * 
     * @param ids
     *            the job list (comma-separated list of UUIDs).
     * @return the requested jobs.
     * @throws JsonProcessingException
     */
    @GET
    @Path("{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobList(@PathParam("ids") final String ids) throws JsonProcessingException {

        logger.debug("GET job list: {}", ids);
        final List<JobDto> jobDtos = nscsJobCacheManager.getJobList(ids);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jobDtos);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Abort the job of given UUID present in NSCS job cache.
     * 
     * @param uuid
     *            the job UUID.
     * @return the aborted job.
     * @throws JsonProcessingException
     */
    @PATCH
    @Path("abort/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response abortJob(@PathParam("uuid") final String uuid) throws JsonProcessingException {

        logger.debug("PATCH abort job: {}", uuid);
        final JobDto jobDto = nscsJobCacheManager.abortJob(uuid);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jobDto);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }
}
