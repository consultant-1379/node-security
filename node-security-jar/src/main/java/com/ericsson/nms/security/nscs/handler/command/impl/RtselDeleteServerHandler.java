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
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.RtselWfException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.RtselValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselDetails;
import com.ericsson.nms.security.nscs.rtsel.request.model.RtselDeleteServerConfiguration;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselDeleteServerDetails;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselDeleteServerResponseBuilder;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.XsdErrorHandler;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Initiates the process to CommandHandler to delete the RTSEL server configuration on the given nodes
 *
 * @author xchowja
 *
 */
@CommandType(NscsCommandType.RTSEL_DELETE)
@Local(CommandHandlerInterface.class)
public class RtselDeleteServerHandler implements CommandHandler<RtselCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private RtselDeleteServerResponseBuilder rtselDeleteServerResponseBuilder;

    @Inject
    private CliUtil cliUtil;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    @Inject
    private XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Inject
    private RtselValidator rtselValidator;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @EJB
    private NscsRtselCommandManager nscsRtselCommandManager;

    @Inject
    private NscsContextService nscsContextService;

    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();

    @Override
    public NscsCommandResponse process(final RtselCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.info("process method in {}", command);
        nscsLogger.commandHandlerStarted(command);

        JobStatusRecord jobStatusRecord = null;

        final String userProvidedRTSELConfiguration = cliUtil.getCommandInputData(command, FileConstants.FILE_URI);
        final XsdErrorHandler xsdErrorHandler = xmlValidatorUtility.validateXMLSchemaWithErrorHandler(userProvidedRTSELConfiguration, RtselConstants.RTSEL_DELETE_SERVER_XSD_VALIDATOR_FILE);
        if (!xsdErrorHandler.isValid()) {
            nscsLogger.error(xsdErrorHandler.formatErrorMessages());
            nscsLogger.commandHandlerFinishedWithError(command, NscsErrorCodes.INVALID_INPUT_XML_FILE);
            throw (new InvalidInputXMLFileException(NscsErrorCodes.INVALID_XML, NscsErrorCodes.XML_SCHEMA_VALIDATIONS_FAILED)
                    .setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_VALID_XML_SCHEMA));
        }

        final RtselDeleteServerConfiguration rtselDeleteServerConfiguration = xmlUnMarshallerUtility.xMLUnmarshaller(userProvidedRTSELConfiguration, RtselDeleteServerConfiguration.class);
        final List<NodeRtselDetails> nodeRtselDetailsList = rtselDeleteServerConfiguration.getNodeRtselDetails();

        final Set<String> duplicateNodes = rtselValidator.getDuplicateNodesForRtselDeleteServer(rtselDeleteServerConfiguration.getNodeRtselDetails(), invalidNodesErrorMap);

        final List<String> validNodesInXml = new ArrayList<String>();
        final List<RtselDeleteServerDetails> rtselDeleteServerDetailsList = new ArrayList<RtselDeleteServerDetails>();

        for (final NodeRtselDetails nodeRtselDetails : nodeRtselDetailsList) {
            final List<String> validNodeFdnsList = new ArrayList<String>();
            rtselValidator.validateNodesToDeleteServerDetails(nodeRtselDetails, validNodeFdnsList, duplicateNodes, invalidNodesErrorMap, command.getCommandType());
            if (!validNodeFdnsList.isEmpty()) {
                validNodesInXml.addAll(validNodeFdnsList);
                rtselDeleteServerDetailsList.add(new RtselDeleteServerDetails(validNodeFdnsList, nodeRtselDetails.getServers().getServerName()));
            }
        }

        final Integer numValidNodes = getNumValidNodesInRtselDeleteServerDetailsList(rtselDeleteServerDetailsList);
        nscsContextService.initItemsStatsForAsyncCommand(numValidNodes, Integer.valueOf(invalidNodesErrorMap.size()));

        if (!rtselDeleteServerDetailsList.isEmpty()) {
            try {
                jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.RTSEL_DELETE);
                nscsRtselCommandManager.executeRtselDeleteServerWfs(rtselDeleteServerDetailsList, jobStatusRecord);
            } catch (Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new RtselWfException();
            }
        }
        if (validNodesInXml.size() != 0 && invalidNodesErrorMap.size() == 0) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, RtselConstants.RTSEL_DELETE_SERVER_SUCCESS_FOR_ALL_NODES);
            return rtselDeleteServerResponseBuilder.buildResponseForAllValidInputNodes(jobStatusRecord);
        } else if (validNodesInXml.size() != 0 && invalidNodesErrorMap.size() != 0) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, RtselConstants.RTSEL_DELETE_SERVER_PARTIAL_FAILED);
            return rtselDeleteServerResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
        } else {
            nscsLogger.commandHandlerFinishedWithError(command, RtselConstants.RTSEL_DELETE_SERVER_FAILED_ALL_NODES);
            return rtselDeleteServerResponseBuilder.buildResponseForAllInvalidInputNodes(command, invalidNodesErrorMap);
        }
    }

    /**
     * Get number of valid nodes present in the given Rtsel Delete Server Details list.
     * 
     * @param rtselDeleteServerDetailsList
     *            the Rtsel Delete Server Details list.
     * @return the number of nodes.
     */
    private Integer getNumValidNodesInRtselDeleteServerDetailsList(final List<RtselDeleteServerDetails> rtselDeleteServerDetailsList) {
        int numValidNodes = 0;
        for (final RtselDeleteServerDetails rtselDeleteServerDetails : rtselDeleteServerDetailsList) {
            numValidNodes += rtselDeleteServerDetails.getNodeFdnsList().size();
        }
        return Integer.valueOf(numValidNodes);
    }

}
