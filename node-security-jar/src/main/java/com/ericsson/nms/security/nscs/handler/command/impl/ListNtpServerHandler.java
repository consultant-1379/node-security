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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.ListNtpCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.ListNtpServerResponseBuilder;
import com.ericsson.nms.security.nscs.handler.validation.impl.NtpValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;

/**
 * <p>
 * Lists the NTPServer details of the requested nodes
 * </p>
 *
 * @author zkndsrv
 *
 */
@CommandType(NscsCommandType.NTP_LIST)
@Local(CommandHandlerInterface.class)
public class ListNtpServerHandler implements CommandHandler<ListNtpCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private FileUtil fileUtil;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NtpValidator ntpValidator;

    @Inject
    private ListNtpServerResponseBuilder listNtpServerResponseBuilder;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

    /**
     * This method is used to process the ntp list command for requested nodes.
     *
     * @param command
     *            NscsNodeCommand
     * @param context
     *            a CommandContext instance
     * @return NscsNameValueCommandResponse instance with node names.
     */
    @Override
    public NscsCommandResponse process(final ListNtpCommand command, final CommandContext context) {

        nscsLogger.commandHandlerStarted(command);
        Map<String, List<NtpServer>> ntpserverDetails = new LinkedHashMap<>();
        final Map<String, Object> properties = command.getProperties();
        final String fileName = (String) properties.get(Constants.FILE_NAME);
        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        final List<NodeReference> uniqueNodes = new ArrayList<>(new HashSet<>(inputNodes));
        final List<NormalizableNodeReference> validNodes = new ArrayList<>();
        NscsNameMultipleValueCommandResponse nscsCommandResponse = null;

        if (fileName != null && !fileUtil.isValidFileExtension(fileName, Constants.FILE_EXT_TXT)) {
            nscsLogger.error("Input Node file: {} to list NTP key id command. " + NscsErrorCodes.INVALID_FILE_NOT_TXT_TYPE, fileName);
            nscsLogger.commandHandlerFinishedWithError(command, "Input Node file: " + fileName + " to list NTP key id command. " + NscsErrorCodes.INVALID_FILE_NOT_TXT_TYPE);
            throw new InvalidArgumentValueException(NscsErrorCodes.INVALID_FILE_NOT_TXT_TYPE).setSuggestedSolution(NscsErrorCodes.CHECK_ONLINE_HELP_FOR_VALID_TXT_FILE_TEMPLATE);
        }

        ntpValidator.validateNodes(uniqueNodes, validNodes, invalidNodesErrorMap);

        for (final NormalizableNodeReference nodeRef : validNodes) {
            List<NtpServer> listNtpServers = moGetServiceFactory.listNtpServerDetails(nodeRef);
            ntpserverDetails.put(nodeRef.getName(), listNtpServers);
        }

        if (!validNodes.isEmpty() && invalidNodesErrorMap.size() == 0) {
            nscsCommandResponse = listNtpServerResponseBuilder.buildResponseForAllvalidInputNodes(ntpserverDetails);
            nscsLogger.info("invalidNodesErrorMap {}", invalidNodesErrorMap);

        } else if (invalidNodesErrorMap.size() > 0 && !validNodes.isEmpty()) {
            nscsCommandResponse = listNtpServerResponseBuilder.buildResponseForPartialValidInputNodes(ntpserverDetails, invalidNodesErrorMap);

        } else if (invalidNodesErrorMap.size() > 0) {
            nscsCommandResponse = listNtpServerResponseBuilder.buildResponseForAllInvalidInputNodes(invalidNodesErrorMap);
        }
        return nscsCommandResponse;
    }

}
