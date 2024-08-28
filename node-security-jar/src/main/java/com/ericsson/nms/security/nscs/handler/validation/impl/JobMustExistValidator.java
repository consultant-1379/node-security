package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.JobGetCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidJobException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

/**
 * Checks if all nodes in the given command exists
 *
 * @author emaynes
 */
public class JobMustExistValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    private static final String NO_JOB_FOUND = "No job found";
    private static final String INVALID_JOB_IDENTIFIER = "Invalid job identifier";

    /**
     * Checks if all nodes in the given command exists
     *
     * @param command
     *            - expects to be a NscsNodeCommand
     */
    @Override
    @SuppressWarnings("unchecked")
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
        logger.debug("Starting NodeMustExistValidator with command type: {}", command.getCommandType());

        if (command.getValueString(JobGetCommand.JOB_NUMBER_PROPERTY) == null
                || command.getValueString(JobGetCommand.JOB_NUMBER_PROPERTY).equals("")) {

            logger.debug("Job Id not specified");
            final InvalidJobException exception = new InvalidJobException(INVALID_JOB_IDENTIFIER);
            throw exception;
        }

        else if (command.getValueString(JobGetCommand.JOB_NUMBER_PROPERTY) != null
                && !command.getValueString(JobGetCommand.JOB_NUMBER_PROPERTY).equals("*")) {

            final List<String> idList = (List<String>) command.getProperties().get(JobGetCommand.JOB_NUMBER_PROPERTY);
            for (final String jobId : idList) {
                JobDto record = null;
                try {
                    record = cacheHandler.getJob(UUID.fromString(jobId));

                } catch (final Exception e) {
                    logger.error(INVALID_JOB_IDENTIFIER + " " + jobId);
                    throw new InvalidJobException(INVALID_JOB_IDENTIFIER + " " + jobId);
                }

                if (record == null) {
                    throw new InvalidJobException(NO_JOB_FOUND + " " + jobId);
                }
            }

        } else {
            logger.debug("Job Get command correctly validated");
        }
    }

}
