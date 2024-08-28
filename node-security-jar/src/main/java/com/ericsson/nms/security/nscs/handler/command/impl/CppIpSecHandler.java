/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.CPP_IPSEC;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CppIpSecCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.IpSecActionException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.IpSecNodeValidatorUtility;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlValidatorUtils;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequest;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestType;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsMultiInstanceCommandResponseBuilder;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Starts the process to enable/disable IpSec of a node or list of nodes
 * </p>
 *
 * @author emehsau
 */

@CommandType(CPP_IPSEC)
@Local(CommandHandlerInterface.class)
public class CppIpSecHandler implements CommandHandler<CppIpSecCommand>, CommandHandlerInterface {
    private static final String NODE_COLUMN = "Node";
    private static final String IP_SEC_JOB_MSG = "Successfully started a job for IPSEC operation. Perform 'secadm job get -j %s' to get progress info.";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private XmlValidatorUtils xmlValidatorUtils;

    @Inject
    private IpSecNodeValidatorUtility ipSecNodeValidatorUtility;

    @Inject
    private CppIpSecStatusUtility ipSecStatusUtility;

    @Inject
    private FileUtil fileUtil;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @EJB
    private NscsCommandManager nscsCommandManager;

    @Inject
    private NscsContextService nscsContextService;

    private ItemsValidityType itemsValidityType;

    @Override
    public NscsCommandResponse process(final CppIpSecCommand command, final CommandContext context) throws NscsServiceException {
        nscsLogger.commandHandlerStarted(command);

        NscsCommandResponse response;
        try {
            response = buildIpSecResponse(command);
            updateCommandHandlerLogger(command, itemsValidityType);
        } catch (final NscsServiceException e) {
            final String errorMsg = String.format("%s Command failed due to %s.", "Failed of IPsec Command.",
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw e;
        } catch (final Exception e) {
            final String errorMsg = String.format("%s Command failed due to unexpected %s.", "Failed of IPsec Command.",
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw new IpSecActionException(errorMsg, e);
        }
        return response;
    }

    private NscsCommandResponse buildIpSecResponse(final CppIpSecCommand command) throws NscsServiceException {

        final String inputFileLocation = command.getXmlInputFile();
        final String fileData = getIpSecInputData(command, inputFileLocation);

        if (fileData.isEmpty()) {
            throw new InvalidFileContentException();
        } else {
            final boolean isValidXMLFile = xmlValidatorUtils.validateXMLSchema(fileData);

            if (isValidXMLFile) {
                final Nodes xmlNodes = createNodesFromIpSecConfiguration(fileData);
                final List<Node> xmlNodeList = xmlNodes.getNode();

                final List<Node> validNodesList = new ArrayList<>();
                final Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
                final List<Node> inputNodes = new ArrayList<>(xmlNodeList);

                //check if uniqueness is working properly
                final List<Node> uniqueNodes = new ArrayList<>(new HashSet<>(inputNodes));
                nscsLogger.debug("Number of unique nodes {}", uniqueNodes.size());

                final boolean areAllInputNodesValid = ipSecNodeValidatorUtility.validateNodes(uniqueNodes, validNodesList, invalidNodesErrorMap);
                return  checkAndExecuteIpSecWorkflows(areAllInputNodesValid,validNodesList, uniqueNodes, invalidNodesErrorMap);

            } else {
                nscsLogger.error(NscsErrorCodes.XML_VALIDATION_FAILED);
                throw new InvalidInputXMLFileException();
            }
        }
    }

    private NscsCommandResponse checkAndExecuteIpSecWorkflows(final boolean areAllInputNodesValid,
                                               final List<Node> validNodesList,
                                               final List<Node> uniqueNodes,
                                               final Map<String, NscsServiceException> invalidNodesErrorMap) {
        String responseMessageWithJobId;
        JobStatusRecord jobStatusRecord;

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodesList.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        if (areAllInputNodesValid) {
            nscsLogger.info("All of the given input nodes [{}] are valid. An IPSec job needs to be added.", uniqueNodes.size());
            jobStatusRecord = nscsJobCacheHandler.insertJob(CPP_IPSEC);
            List<IpSecRequest> requests = configureIpSecChangeForValidNodes(validNodesList);
            nscsCommandManager.executeIpSecWorkflows(requests, jobStatusRecord);
            responseMessageWithJobId = String.format(IP_SEC_JOB_MSG, jobStatusRecord.getJobId().toString());
            itemsValidityType = ItemsValidityType.ALL_ITEMS_ARE_VALID;
            return new NscsMultiInstanceCommandResponseBuilder().buildSuccessResponse(responseMessageWithJobId);
        } else {
            if (!validNodesList.isEmpty()) {
                nscsLogger.info("Only [{}] of the given input nodes [{}] are valid. An IpSec job needs to be added.", validNodesList.size(),
                        uniqueNodes.size());
                jobStatusRecord = nscsJobCacheHandler.insertJob(CPP_IPSEC);
                List<IpSecRequest> requests = configureIpSecChangeForValidNodes(validNodesList);
                nscsCommandManager.executeIpSecWorkflows(requests, jobStatusRecord);
                String partiallyExecutedMessage = String.format(NscsErrorCodes.OPERATION_WITH_SOME_INVALID_NODES_FORMAT,
                        IP_SEC_JOB_MSG);
                responseMessageWithJobId = String.format(partiallyExecutedMessage, jobStatusRecord.getJobId().toString());
                itemsValidityType = ItemsValidityType.ITEMS_PARTIALLY_VALID;
                return new NscsMultiInstanceCommandResponseBuilder().buildErrorResponse(
                        responseMessageWithJobId, NODE_COLUMN, invalidNodesErrorMap);
            } else {
                nscsLogger.error("All of the given input nodes [{}] are invalid. No IpSec job needs to be added.", uniqueNodes.size());
                itemsValidityType = ItemsValidityType.ALL_ITEMS_ARE_INVALID;
                String errorResponseMessage = String.format(NscsErrorCodes.OPERATION_WITH_ALL_INVALID_NODES_FORMAT, "Error during IpSec operation.");
                return new NscsMultiInstanceCommandResponseBuilder().buildErrorResponse(
                        errorResponseMessage, NODE_COLUMN, invalidNodesErrorMap);
            }
        }
    }

    /**
     * This method get the String equivalent of input file.
     *
     * @param inputFileLocation : location of the input file
     * @return {@link String}
     */
    private String getIpSecInputData(final CppIpSecCommand command, final String inputFileLocation) {
        final Map<String, Object> properties = command.getProperties();

        String content;
        final String filePath = (String) properties.get(Constants.FILE_PATH);
        final String fileName = (String) properties.get(Constants.FILE_NAME);

        if (inputFileLocation != null && !inputFileLocation.isEmpty()) {
            if (!fileUtil.isValidFileExtension(fileName, Constants.FILE_EXT_XML)) {
                final String errorMsg = String.format(NscsErrorCodes.INVALID_FILE_TYPE_NOT_XML);
                nscsLogger.error(errorMsg);
                throw new CommandSyntaxException(errorMsg);
            }

            final String osAppropriatePath = System.getProperty("file.separator").equalsIgnoreCase("/") ? filePath : filePath.substring(1);
            try {
                final Path path = Paths.get(URLDecoder.decode(osAppropriatePath, "UTF-8"));
                final Charset charset = StandardCharsets.UTF_8;
                final byte[] data = Files.readAllBytes(path);
                content = new String(data, charset);
            } catch (IOException e) {
                final String errorMsg = String.format("Error in the stringify of the xml file : %s", NscsLogger.stringifyException(e));
                nscsLogger.error(errorMsg, e);
                throw new CommandSyntaxException(errorMsg);
            }
        } else {
            final String errorMsg = "Location of the xml input file not found";
            nscsLogger.error(errorMsg);
            throw new CommandSyntaxException(errorMsg);
        }

        if (content.isEmpty()) {
            final String errorMsg = String.format("File [{%s}] is empty", inputFileLocation);
            nscsLogger.error(errorMsg);
            throw new CommandSyntaxException(errorMsg);
        }
        return content;
    }

    /**
     * Method to create List of nodes with user data.
     *
     * @param fileContent : Content of user input file.
     * @return {@link Nodes}
     */
    private Nodes createNodesFromIpSecConfiguration(final String fileContent) {
        Nodes nodes;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Nodes.class);
            Unmarshaller jaxbUnmarshaller;
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            nodes = (Nodes) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(fileContent)));

        } catch (final JAXBException e) {
            // If there is any JAXB conversion error, we are throwing an invalid
            // XML exception.
            // It should not happen as we have already validated input with XSD.
            nscsLogger.error("Invalid input XML file. Unmarshalling of XML failed. Excp:{}",e);
            throw new InvalidInputXMLFileException();
        }
        return nodes;
    }

    private List<IpSecRequest> configureIpSecChangeForValidNodes(List<Node> validNodesList) {
        final List<IpSecRequest> requests = new ArrayList<>();

        for (final Node validNode : validNodesList) {
            IpSecRequest request = new IpSecRequest();

            final String fdn = validNode.getNodeFdn();
            final NodeReference nodeRef = new NodeRef(fdn);
            request.setNodeFdn(fdn);
            final IpSecRequestType requestType = CppIpSecStatusUtility.getIpSecRequestTypeFromInput(validNode);
            if (requestType == IpSecRequestType.IP_SEC_ENABLE_CONF1 && validNode.getEnableOMConfiguration1() != null) {
                validNode.getEnableOMConfiguration1().setVid(ipSecStatusUtility.getOamVlanId(nodeRef));
            } else if (requestType == IpSecRequestType.IP_SEC_DISABLE && validNode.getDisableOMConfiguration() != null) {
                validNode.getDisableOMConfiguration().setVid(ipSecStatusUtility.getOamVlanId(nodeRef));
            }
            request.setXmlRepresntationOfNode(validNode);
            request.setIpSecRequestType(requestType);
            nscsLogger.info("IPsec change command validation is successful for the node {} for the IPsec request type {}.", fdn, requestType);

            requests.add(request);
        }
        return requests;
    }

    private void updateCommandHandlerLogger(CppIpSecCommand command, ItemsValidityType itemsValidityType) {
        switch (itemsValidityType) {
            case ALL_ITEMS_ARE_VALID:
                nscsLogger.commandHandlerFinishedWithSuccess(command,
                        "workflows started successfully for all valid nodes");
                break;
            case ITEMS_PARTIALLY_VALID:
                nscsLogger.commandHandlerFinishedWithSuccess(command,
                        "workflows started successfully for some valid nodes");
                break;
            default:
            case ALL_ITEMS_ARE_INVALID:
                nscsLogger.commandHandlerFinishedWithError(command,
                        "no workflows started for all invalid nodes");
                break;
        }
    }
    private enum ItemsValidityType {
        ALL_ITEMS_ARE_VALID(),
        ITEMS_PARTIALLY_VALID(),
        ALL_ITEMS_ARE_INVALID();
        ItemsValidityType() {
        }
    }
}