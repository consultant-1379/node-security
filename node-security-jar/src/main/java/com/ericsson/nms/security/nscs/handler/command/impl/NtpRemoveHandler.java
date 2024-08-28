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

import java.io.UnsupportedEncodingException;
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
import com.ericsson.nms.security.nscs.api.command.types.NtpRemoveCommand;
import com.ericsson.nms.security.nscs.api.enums.NtpRemoveInputType;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.NtpValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.delete.request.model.Nodes;
import com.ericsson.nms.security.nscs.ntp.delete.request.model.Nodes.Node;
import com.ericsson.nms.security.nscs.ntp.utility.NtpRemoveWorkflowData;
import com.ericsson.nms.security.nscs.ntp.utility.NtpResponseBuilder;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.topologyCollectionsService.exception.rest.EmptyFileException;

/**
 * Initiates the process to remove the NTP Server associated with the given key id on the nodes.
 *
 * @author xvekkar
 *
 */
@CommandType(NscsCommandType.NTP_REMOVE)
@Local(CommandHandlerInterface.class)
public class NtpRemoveHandler implements CommandHandler<NtpRemoveCommand>, CommandHandlerInterface {

    private static final String INVALID_SYNTAX = "Either node name provided has incorrect format or multiple nodes are provided in the command. Command with KeyID is supported only for single node.";
    private static final String NTP_REMOVE_SUCCESS = "Successfully removed installed key ids on valid nodes ";
    private static final String NTP_REMOVE_FAILED = "NTP remove command has been failed to execute on all the nodes";

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NtpValidator ntpValidator;

    @Inject
    private XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Inject
    private CliUtil cliUtil;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @EJB
    private NscsNtpCommandManager nscsNtpCommandManager;

    @Inject
    private NtpResponseBuilder ntpResponseBuilder;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsCommandResponse process(final NtpRemoveCommand command, final CommandContext context) {

        nscsLogger.commandHandlerStarted(command);

        final Map<String, Object> properties = command.getProperties();
        final String fileName = (String) properties.get(Constants.FILE_NAME);
        if (fileName != null) {
            if (properties.containsKey("nodefile")) {
                ntpValidator.verifyFileExtension(fileName, Constants.FILE_EXT_TXT);
            } else if (properties.containsKey("xmlfile")) {
                ntpValidator.verifyFileExtension(fileName, Constants.FILE_EXT_XML);
            }
        }

        final List<NodeReference> inputNodes = getInputNodes(command);

        final List<NodeReference> uniqueNodes = new ArrayList<>(new HashSet<>(inputNodes));
        final List<NormalizableNodeReference> validNodes = new ArrayList<>();

        nscsLogger.info("Number of input nodes provided to perform ntp remove command {}", inputNodes.size());

        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        ntpValidator.validateNodes(uniqueNodes, validNodes, invalidNodesErrorMap);

        nscsLogger.info("valid nodes from the provided nodes to perform ntp remove command : {}", validNodes);

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        JobStatusRecord jobStatusRecord = null;

        if (!validNodes.isEmpty()) {

            jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.NTP_REMOVE);
            if (properties.containsKey(NtpRemoveCommand.XML_FILE_PROPERTY)) {
                initiateWorkflowForXmlData(command, validNodes, jobStatusRecord);
            } else {
                final List<NtpRemoveWorkflowData> ntpRemoveWorkflowData = prepareWorkflowObject(command);
                initiateNtpRemoveWorkflow(ntpRemoveWorkflowData, jobStatusRecord);
            }
        }
        final NscsCommandResponse nscsCommandResponse = ntpResponseBuilder.buildResponseForNtp(jobStatusRecord, invalidNodesErrorMap);
        if (invalidNodesErrorMap.isEmpty()) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, NTP_REMOVE_SUCCESS);
        } else {
            nscsLogger.commandHandlerFinishedWithError(command, NTP_REMOVE_FAILED);
        }
        return nscsCommandResponse;
    }

    private void initiateNtpRemoveWorkflow(final List<NtpRemoveWorkflowData> ntpRemoveWorkflowDataList, final JobStatusRecord jobStatusRecord) {
        nscsNtpCommandManager.removeNtpServerData(ntpRemoveWorkflowDataList, jobStatusRecord);

    }

    private List<NtpRemoveWorkflowData> prepareWorkflowObject(final NtpRemoveCommand command) {

        final List<NtpRemoveWorkflowData> ntpRemoveWorkflowDataList = new ArrayList<>();
        final Map<String, Object> properties = command.getProperties();
        final String nodeName = (String) properties.get(NtpRemoveCommand.NODE_NAME);
        final NodeReference nodeReference = new NodeRef(nodeName);
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeReference);
        List<String> serverIdOrKeyIdList;

        NtpRemoveInputType ntpRemoveInputType = NtpRemoveInputType.KEYIDS;
        if (properties.containsKey(NtpRemoveCommand.SERVER_ID_LIST)) {
            ntpRemoveInputType = NtpRemoveInputType.SERVERIDS;
            serverIdOrKeyIdList = (List<String>) properties.get(NtpRemoveCommand.SERVER_ID_LIST);
        } else {
            serverIdOrKeyIdList = (List<String>) properties.get(NtpRemoveCommand.KEY_ID_LIST);
        }
        ntpRemoveWorkflowDataList.add(new NtpRemoveWorkflowData(normNode.getFdn(), serverIdOrKeyIdList, ntpRemoveInputType));

        return ntpRemoveWorkflowDataList;

    }

    private List<NodeReference> getInputNodes(final NtpRemoveCommand command) {

        List<NodeReference> inputNodes = new ArrayList<>();
        if (command.getProperties().containsKey(NtpRemoveCommand.XML_FILE_PROPERTY)) {
            final String fileData = getInputData(command, "file:");
            ntpValidator.validateFileDataForNtpRemove(fileData);
            final String userProvidedNTPConfiguration = cliUtil.getCommandInputData(command, FileConstants.FILE_URI);
            final Nodes nodes = xmlUnMarshallerUtility.xMLUnmarshaller(userProvidedNTPConfiguration, Nodes.class);
            ntpValidator.validateXMLFileNodeDataForNtpRemove(nodes);
            final List<Nodes.Node> nodeList = nodes.getNode();
            for (final Nodes.Node node : nodeList) {
                final NodeReference nodeRef = new NodeRef(node.getNodeFdn());
                inputNodes.add(nodeRef);
            }

        } else if (command.getProperties().containsKey(NtpRemoveCommand.NODE_NAME)) {
            final String nodeFdn = (String) command.getProperties().get(NtpRemoveCommand.NODE_NAME);
            try {
                inputNodes.add(new NodeRef(nodeFdn));
            } catch (final InvalidArgumentValueException invalidArgumentValueException) {
                throw new InvalidArgumentValueException(INVALID_SYNTAX);
            }
        } else {
            inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        }
        return inputNodes;
    }

    private String getInputData(final NtpRemoveCommand command, final String propertyKey) {

        nscsLogger.info("command {}, propertyKey {}", command, propertyKey);

        final Map<String, Object> properties = command.getProperties();
        final byte[] fileDataInByte = (byte[]) properties.get(propertyKey);

        String fileData = null;
        if (fileDataInByte != null) {
            try {
                fileData = new String(fileDataInByte, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                nscsLogger.error(NscsErrorCodes.INVALID_ENCODING, e);
                throw new InvalidFileContentException(NscsErrorCodes.INVALID_ENCODING);
            }
        } else {
            nscsLogger.error("Empty input XML file");
            throw new EmptyFileException("Empty input XML file");
        }
        nscsLogger.debug("fileData: {}", fileData);
        return fileData;
    }

    private void initiateWorkflowForXmlData(final NtpRemoveCommand command, final List<NormalizableNodeReference> validNodes,
                                            final JobStatusRecord jobStatusRecord) {
        final String userProvidedNTPConfiguration = cliUtil.getCommandInputData(command, FileConstants.FILE_URI);
        final Nodes nodes = xmlUnMarshallerUtility.xMLUnmarshaller(userProvidedNTPConfiguration, Nodes.class);
        final List<Nodes.Node> nodeList = nodes.getNode();

        final List<NtpRemoveWorkflowData> ntpRemoveWorkflowData = getWorkflowDataFromXmlInput(nodeList, validNodes);
        initiateNtpRemoveWorkflow(ntpRemoveWorkflowData, jobStatusRecord);
    }

    private List<NtpRemoveWorkflowData> getWorkflowDataFromXmlInput(final List<Node> nodeList, final List<NormalizableNodeReference> validNodes) {

        final List<NtpRemoveWorkflowData> ntpRemoveWorkflowDataList = new ArrayList<>();
        for (final Nodes.Node node : nodeList) {
            final Nodes.Node.KeyIds keyIds = node.getKeyIds();
            List<String> serverIdorKeyIdList;

            NtpRemoveInputType ntpRemoveInputType = NtpRemoveInputType.KEYIDS;
            if (keyIds != null) {
                serverIdorKeyIdList = keyIds.getKeyIdList();
            } else {
                ntpRemoveInputType = NtpRemoveInputType.SERVERIDS;
                final Nodes.Node.ServerIds serverIds = node.getServerIds();
                serverIdorKeyIdList = serverIds.getServerIdList();
            }

            for (final NormalizableNodeReference validNode : validNodes) {
                if (validNode.getFdn().contains(node.getNodeFdn())) {
                    ntpRemoveWorkflowDataList.add(new NtpRemoveWorkflowData(validNode.getFdn(), serverIdorKeyIdList, ntpRemoveInputType));
                }
            }
        }
        return ntpRemoveWorkflowDataList;
    }
}