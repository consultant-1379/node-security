/*------------------------------------------------------------------------------
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsNtpCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.NtpConfigureCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.NtpValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.utility.NTPConfigureResponseBuilder;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Configures the node with NTP server details
 *
 * @author xkihari
 */
@CommandType(NscsCommandType.NTP_CONFIGURE)
@Local(CommandHandlerInterface.class)
public class NtpConfigureHandler implements CommandHandler<NtpConfigureCommand>, CommandHandlerInterface {

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Inject
    private NtpValidator ntpValidator;

    @EJB
    private NscsNtpCommandManager nscsNtpCommandManager;

    @Inject
    private NTPConfigureResponseBuilder ntpConfigureResponseBuilder;

    @Inject
    private NscsLogger logger;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsCommandResponse process(final NtpConfigureCommand command, final CommandContext context) {

        logger.commandHandlerStarted(command);

        logger.info("Ntp configure command [{}]", command);

        final Map<String, Object> properties = command.getProperties();
        final String fileName = (String) properties.get(Constants.FILE_NAME);

        ntpValidator.verifyFileExtension(fileName, Constants.FILE_EXT_TXT);

        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        final List<NodeReference> uniqueNodes = new ArrayList<>(new HashSet<>(inputNodes));

        final List<NormalizableNodeReference> validNodes = new ArrayList<>();

        logger.info("Number of input nodes for Ntp configure command: {}", inputNodes.size());

        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        ntpValidator.validateNodes(uniqueNodes, validNodes, invalidNodesErrorMap);

        logger.info("Valid nodes for Ntp configure command: {}", validNodes);

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        NscsCommandResponse response = null;

        if (!validNodes.isEmpty()) {
            final JobStatusRecord jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.NTP_CONFIGURE);
            nscsNtpCommandManager.configureNtpServer(validNodes, jobStatusRecord);

            if (invalidNodesErrorMap.isEmpty()) {
                logger.commandHandlerFinishedWithSuccess(command, NtpConstants.NTP_CONFIG_EXECUTED);
                response = ntpConfigureResponseBuilder.buildResponseForAllValidInputNodes(jobStatusRecord);
            } else {
                logger.commandHandlerFinishedWithSuccess(command, NtpConstants.NTP_CONFIG_PARTIALLY_EXECUTED);
                response = ntpConfigureResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
            }

        } else {
            logger.commandHandlerFinishedWithError(command, NtpConstants.NTP_CONFIG_NOT_EXECUTED);
            response = ntpConfigureResponseBuilder.buildResponseForAllInvalidInputNodes(invalidNodesErrorMap);
        }
        return response;

    }

}
