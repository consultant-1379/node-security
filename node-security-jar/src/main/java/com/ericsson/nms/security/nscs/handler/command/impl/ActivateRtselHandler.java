/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsRtselCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.RtselWfException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.RtselValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselConfig;
import com.ericsson.nms.security.nscs.rtsel.request.model.RtselConfiguration;
import com.ericsson.nms.security.nscs.rtsel.request.model.ServerConfig;
import com.ericsson.nms.security.nscs.rtsel.utility.ActivateRtselResponseBuilder;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselJobInfo;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselXMLValidator;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.XsdErrorHandler;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Initiates the process to CommandHandler to activate RTSEL on the given nodes
 *
 * @author xchowja
 *
 */
@CommandType(NscsCommandType.RTSEL_ACTIVATE)
@Local(CommandHandlerInterface.class)
public class ActivateRtselHandler implements CommandHandler<RtselCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsRtselCommandManager nscsRtselCommandManager;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    @Inject
    private XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Inject
    private ActivateRtselResponseBuilder activateRtselResponseBuilder;

    @Inject
    private RtselValidator rtselValidator;

    @Inject
    private CliUtil cliUtil;

    @Inject
    private RtselXMLValidator rtselXMLValidator;

    @Inject
    private NscsContextService nscsContextService;

    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

    @Override
    public NscsCommandResponse process(final RtselCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);

        final RtselConfiguration rtselConfiguration = getRtselConfigurationFromCommand(command);

        final List<NodeInfoDetails> validNodesInXml = new ArrayList<>();
        final List<RtselJobInfo> rtselJobInfoList = new ArrayList<>();

        validateRtselConfiguration(command, rtselConfiguration, validNodesInXml, rtselJobInfoList);

        final Integer numValidNodes = getNumValidNodesInRtselJobInfoList(rtselJobInfoList);
        nscsContextService.initItemsStatsForAsyncCommand(numValidNodes, Integer.valueOf(invalidNodesErrorMap.size()));

        JobStatusRecord jobStatusRecord = null;

        if (!rtselJobInfoList.isEmpty()) {
            try {
                jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.RTSEL_ACTIVATE);
                nscsRtselCommandManager.executeActivateRtselWfs(rtselJobInfoList, jobStatusRecord);
            } catch (Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new RtselWfException();
            }
        }
        if (!validNodesInXml.isEmpty() && invalidNodesErrorMap.isEmpty()) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, RtselConstants.ACTIVATE_RTSEL_SUCCESS_FOR_ALL_NODES);
            return activateRtselResponseBuilder.buildResponseForAllValidInputNodes(jobStatusRecord);
        } else if (!validNodesInXml.isEmpty() && !invalidNodesErrorMap.isEmpty()) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, RtselConstants.ACTIVATE_RTSEL_PARTIAL_FAILED);
            return activateRtselResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
        } else {
            nscsLogger.commandHandlerFinishedWithError(command, RtselConstants.ACTIVATE_RTSEL_FAILED_ALL_NODES);
            return activateRtselResponseBuilder.buildResponseForAllInvalidInputNodes(command, invalidNodesErrorMap);
        }
    }

    /**
     * Validate the given RTSEL configuration for the given command.
     * 
     * Produce a list of valid and invalid nodes and, if at least one valid node is present, the info to start an RTSEL job.
     * 
     * @param command
     *            the command.
     * @param rtselConfiguration
     *            the RTSEL configuration.
     * @param validNodesInXml
     *            the list of valid nodes.
     * @param rtselJobInfoList
     *            the info to start the RTSEL job.
     * @throws {@link
     *             InvalidArgumentValueException} if RTSEL configuration is not valid (empty or null server name or duplicated server name).
     */
    private void validateRtselConfiguration(final RtselCommand command, final RtselConfiguration rtselConfiguration,
            final List<NodeInfoDetails> validNodesInXml, final List<RtselJobInfo> rtselJobInfoList) {
        final List<NodeRtselConfig> nodeRtselConfigList = rtselConfiguration.getNodeRtselConfig();
        final Set<String> duplicateNodes = rtselValidator.getDuplicateNodesForActivateRtsel(rtselConfiguration.getNodeRtselConfig(),
                invalidNodesErrorMap);

        for (NodeRtselConfig nodeRtselConfig : nodeRtselConfigList) {
            final List<ServerConfig> serverConfigList = nodeRtselConfig.getServerConfig();
            List<String> serverNames = new ArrayList<>();
            nscsLogger.info("serverConfigList {}", serverConfigList.size());
            for (final ServerConfig serverConfig : serverConfigList) {
                if (serverConfig.getServerName() == null || serverConfig.getServerName().trim().isEmpty()) {
                    final String errorMessage = String.format(NscsErrorCodes.SERVER_NAME_EMPTY);
                    nscsLogger.error(errorMessage);
                    throw new InvalidArgumentValueException(errorMessage);
                }
                if (serverNames.contains(serverConfig.getServerName())) {
                    final String errorMessage = String.format(NscsErrorCodes.DUPLICATE_SERVER_NAME, serverConfig.getServerName());
                    nscsLogger.error(errorMessage);
                    throw new InvalidArgumentValueException(errorMessage);
                }
                serverNames.add(serverConfig.getServerName());
                rtselXMLValidator.validateExternalServerProtocol(serverConfig.getExtServProtocol());
                rtselXMLValidator.validateExternalServerAddress(serverConfig.getExtServerAddress());
            }
            rtselXMLValidator.validateExternalServerLogLevel(nodeRtselConfig.getExtServerLogLevel());
            rtselXMLValidator.validateConnectionTimeOut(nodeRtselConfig.getConnAttemptTimeOut());

            final List<NodeInfoDetails> nodeInfoDetailsList = new ArrayList<>();
            rtselValidator.validateNodes(nodeRtselConfig, nodeInfoDetailsList, duplicateNodes, invalidNodesErrorMap, command.getCommandType());

            if (!nodeInfoDetailsList.isEmpty()) {
                validNodesInXml.addAll(nodeInfoDetailsList);
                rtselJobInfoList.add(new RtselJobInfo(nodeInfoDetailsList, nodeRtselConfig));
            }
        }
    }

    /**
     * Get the RTSEL configuration from command.
     * 
     * The XML file is retrieved, validated, unmarshalled.
     * 
     * @param command
     *            the command.
     * @return the RTSEL configuration.
     * @throws {@link
     *             InvalidInputXMLFileException} if XML file validation fails.
     */
    private RtselConfiguration getRtselConfigurationFromCommand(final RtselCommand command) {
        final String userProvidedRTSELConfiguration = cliUtil.getCommandInputData(command, FileConstants.FILE_URI);
        final XsdErrorHandler xsdErrorHandler = xmlValidatorUtility.validateXMLSchemaWithErrorHandler(userProvidedRTSELConfiguration,
                RtselConstants.RTSEL_XSD_VALIDATOR_FILE);

        if (!xsdErrorHandler.isValid()) {
            nscsLogger.error(xsdErrorHandler.formatErrorMessages());
            nscsLogger.commandHandlerFinishedWithError(command, NscsErrorCodes.INVALID_INPUT_XML_FILE);
            throw (new InvalidInputXMLFileException(NscsErrorCodes.INVALID_XML, NscsErrorCodes.XML_SCHEMA_VALIDATIONS_FAILED)
                    .setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_VALID_XML_SCHEMA));
        }

        return xmlUnMarshallerUtility.xMLUnmarshaller(userProvidedRTSELConfiguration, RtselConfiguration.class);
    }

    /**
     * Get number of valid nodes present in the given Rtsel Job Info list.
     * 
     * @param rtselJobInfoList
     *            the Rtsel Job Info list.
     * @return the number of nodes.
     */
    private Integer getNumValidNodesInRtselJobInfoList(final List<RtselJobInfo> rtselJobInfoList) {
        int numValidNodes = 0;
        for (final RtselJobInfo rtselJobInfo : rtselJobInfoList) {
            for (final NodeInfoDetails nodeInfoDetails : rtselJobInfo.getNodeInfoDetailsList()) {
                numValidNodes += nodeInfoDetails.getNodeFdnsList().size();
            }
        }
        return Integer.valueOf(numValidNodes);
    }
}
