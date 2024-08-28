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
package com.ericsson.nms.security.nscs.handler.command.impl;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.TestCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.TestWfsException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@CommandType(NscsCommandType.TEST_COMMAND)
@Local(CommandHandlerInterface.class)
public class TestCommandHandler implements CommandHandler<TestCommand>, CommandHandlerInterface {

    private static final String TEST_COMMAND_OK = "Test Command OK.";
    private static final String TEST_WFS_EXECUTED = "Successfully started a job to test workflows";
    private static final String TEST_WFS_NOT_EXECUTED = "No job started due to all invalid nodes.";

    @Inject
    private NscsLogger logger;

    @EJB
    private NscsCommandManager commandManager;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsCommandResponse process(final TestCommand command, final CommandContext context) throws NscsServiceException {
        logger.commandHandlerStarted(command);
        final String workflows = command.getWorkflows();
        if (workflows != null) {
            logger.commandHandlerOngoing(command, "Test " + workflows + " workflows is going to be executed");
            return testWorkflows(command);
        } else {
            logger.commandHandlerFinishedWithSuccess(command, TEST_COMMAND_OK);
            return NscsCommandResponse.message(TEST_COMMAND_OK);
        }
    }

    /**
     * @param command
     *            the test command
     *            the test command context
     * @return the command response
     */
    private NscsCommandResponse testWorkflows(final TestCommand command) {
        String jobIdMessage = "";
        try {
            final int numWorkflows = Integer.parseInt(command.getWorkflows());
            int valid = numWorkflows;
            int invalid = 0;
            if (numWorkflows < 0) {
                valid = Math.abs(numWorkflows);
                invalid = valid;
            }

            nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(valid), Integer.valueOf(invalid));

            if (valid > 0) {
                JobStatusRecord jobStatusRecord = cacheHandler.insertJob(NscsCommandType.TEST_COMMAND);
                jobIdMessage = String.format("%s. Perform 'secadm job get -j %s' to get progress info.", TEST_WFS_EXECUTED,
                        jobStatusRecord.getJobId().toString());
                commandManager.executeTestWfs(valid, jobStatusRecord);
            } else {
                jobIdMessage = TEST_WFS_NOT_EXECUTED;
            }
        } catch (final NumberFormatException numberFormatException) {
            final String errorMsg = String.format("Wrong value [%s]. Please specify an integer value.", command.getWorkflows());
            logger.error(errorMsg, numberFormatException);
            throw new TestWfsException(errorMsg);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new TestWfsException(e.getMessage(), e.getCause());
        }
        return NscsCommandResponse.message(jobIdMessage);
    }
}
