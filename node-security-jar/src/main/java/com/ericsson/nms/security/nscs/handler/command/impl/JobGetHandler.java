/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2019
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.List;
import java.util.UUID;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.JobGetCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetJobResponseBuilder;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.JobMustExistValidator;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

/**
 * Gets job info of the requested jobs.
 */
@UseValidator({ JobMustExistValidator.class })
@CommandType(NscsCommandType.GET_JOB)
@Local(CommandHandlerInterface.class)
public class JobGetHandler implements CommandHandler<JobGetCommand>, CommandHandlerInterface {

    @Inject
    private Logger logger;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    /**
     *
     * @param command
     *            the NscsNodeCommand command
     * @param context
     *            a CommandContext instance
     * @return NscsCommandResponse instance with job info.
     * @throws NscsServiceException
     *             on any error occurrence
     */
    @Override
    public NscsCommandResponse process(final JobGetCommand command, final CommandContext context) throws NscsServiceException {

        final boolean isSummary = command.isSummary();
        if (logger.isDebugEnabled()) {
            logger.debug("job GetHandler: summary={}", isSummary);
        }

        final NscsGetJobResponseBuilder responseBuilder = new NscsGetJobResponseBuilder();
        responseBuilder.setIsSummary(isSummary);
        responseBuilder.addHeader();

        NscsCommandResponse response = null;
        if (JobGetCommand.ALL_JOBS_VALUE.equals(command.getValueString(JobGetCommand.JOB_NUMBER_PROPERTY))) {
            response = prepareAllJobsResponse(command, responseBuilder);
        } else {
            response = prepareJobsResponse(command, responseBuilder);
        }

        return response;
    }

    /**
     * Prepare the command response for requested jobs.
     * 
     * @param command
     *            the NscsNodeCommand command
     * @param responseBuilder
     *            the response builder
     * @return the command response
     */
    private NscsCommandResponse prepareJobsResponse(final JobGetCommand command, final NscsGetJobResponseBuilder responseBuilder) {
        final List<String> jobList = command.getJobList();

        for (final String id : jobList) {
            logger.debug("Fetching job UUID: {}", id);
            final JobDto record = cacheHandler.getJob(UUID.fromString(id), command.getWfFilters());
            responseBuilder.addJobInfo(record);
        }

        return responseBuilder.getCommandResponse();
    }

    /**
     * Prepare the command response for all jobs.
     * 
     * @param command
     *            the NscsNodeCommand command
     * @param responseBuilder
     *            the response builder
     * @return the command response
     */
    private NscsCommandResponse prepareAllJobsResponse(final JobGetCommand command, final NscsGetJobResponseBuilder responseBuilder) {
        logger.debug("Fetching All jobs");
        final List<JobDto> jobs = cacheHandler.getAllJobs(command.getWfFilters());

        if (jobs.size() > 0) {
            for (final JobDto record : jobs) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding Job Info from JobDto for jobid {}", record.getJobId() != null ? record.getJobId().toString() : null);
                }
                responseBuilder.addJobInfo(record);
            }

            return responseBuilder.getCommandResponse();

        } else {
            return responseBuilder.noJobFoundCommandResponse();
        }
    }

}
