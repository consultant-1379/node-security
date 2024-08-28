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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.Local;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.EnrollmentInfoFileCommand;
import com.ericsson.nms.security.nscs.api.exception.DuplicateNodeNamesException;
import com.ericsson.nms.security.nscs.api.exception.GenerateEnrollmentInfoException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsUnMarshaller;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsValidator;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetailsList;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.EnrollmentInfoProvider;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoServiceException;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.EnrollmentInfoConstants;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.itpf.security.pki.common.commonutils.JaxbUtil;
import com.ericsson.oss.services.security.nscs.command.enrollmentinfo.GenerateEnrollmentInfoResponseBuilder;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Initiates the process to download the enrollmentInfo file for the provided list of nodes.
 * </p>
 *
 * @author xgvgvgv
 *
 */
@CommandType(NscsCommandType.ENROLLMENT_INFO_FILE)
@Local(CommandHandlerInterface.class)
public class GenerateEnrollmentInfoFileHandler implements CommandHandler<EnrollmentInfoFileCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    @Inject
    private NodeDetailsValidator nodeDetailsValidator;

    @Inject
    private NodeDetailsUnMarshaller nodeDetailsUnMarshaller;

    @Inject
    private EnrollmentInfoProvider enrollmentInfoProvider;

    @Inject
    private CliUtil cliUtil;

    @Inject
    private FileUtil fileUtil;

    @Inject
    private XmlOperatorUtils xmlOperatorUtils;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsCommandResponse process(final EnrollmentInfoFileCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);

        NscsCommandResponse nscsCommandResponse = null;
        try {
            final String fileData = cliUtil.getCommandInputData(command, FileConstants.FILE_URI);
            if (null == fileData || fileData.isEmpty()) {
                nscsLogger.error(NscsErrorCodes.INVALID_FILE_CONTENT);
                nscsLogger.commandHandlerFinishedWithError(command, NscsErrorCodes.INVALID_FILE_CONTENT);
                throw new InvalidInputXMLFileException();
            }

            final boolean isValidXmlFile = xmlValidatorUtility.validateXMLSchema(fileData, EnrollmentInfoConstants.XSD_VALIDATOR_FILE);
            if (!isValidXmlFile) {
                nscsLogger.error(NscsErrorCodes.XML_VALIDATION_FAILED);
                nscsLogger.commandHandlerFinishedWithError(command, NscsErrorCodes.INVALID_INPUT_XML_FILE);
                throw new InvalidInputXMLFileException(NscsErrorCodes.INVALID_INPUT_XML_FILE);
            }

            final NodeDetailsList nodesDetailsList = nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData);
            final List<NodeDetails> nodes = nodesDetailsList.getList();
            if (nodes == null || nodes.isEmpty()) {
                final String errorMsg = "No nodes specified in XML file.";
                nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
                throw new InvalidInputXMLFileException(errorMsg);
            }

            final List<NodeDetails> uniqueNodes = buildListWithoutDuplicates(nodes);
            nscsCommandResponse = generateEnrollmentInfoForNodes(command, uniqueNodes);

        } catch (final InvalidInputXMLFileException e) {
            nscsLogger.error(NscsErrorCodes.INVALID_INPUT_XML_FILE, e.getMessage());
            nscsLogger.commandHandlerFinishedWithError(command, e.getMessage());
            return NscsMessageCommandResponse.message("Error " + e.getErrorCode() + ":" + NscsErrorCodes.INVALID_INPUT_XML_FILE);

        } catch (final GenerateEnrollmentInfoException e) {
            nscsLogger.error(NscsErrorCodes.ENROLLMENT_INFO_ERROR, e.getMessage());
            nscsLogger.commandHandlerFinishedWithError(command, e.getMessage());
            return NscsMessageCommandResponse.message("Error " + ":" + NscsErrorCodes.ENROLLMENT_INFO_ERROR);

        } catch (final Exception e) {
            nscsLogger.error(NscsErrorCodes.THIS_IS_AN_UNEXPECTED_SYSTEM_ERROR + e.getMessage());
            nscsLogger.commandHandlerFinishedWithError(command, e.getMessage());
            return NscsMessageCommandResponse.message(NscsErrorCodes.THIS_IS_AN_UNEXPECTED_SYSTEM_ERROR);
        }

        nscsLogger.commandHandlerFinishedWithSuccess(command, EnrollmentInfoConstants.SUCCESS_MESSAGE);

        return nscsCommandResponse;
    }

    /**
     * Checks duplicates in given input list, removing duplicates (all configuration parameters are equal) and throwing an exception if not equal
     * items related to same node and same cert type (conflicting duplicates referring to same end entity) are present.
     * 
     * @param nodes
     *            the input node list.
     * @return a new list without duplicates.
     * @throws {@link
     *             DuplicateNodeNamesException} if not equal items related to same node and same cert type (conflicting duplicates) are present.
     */
    private List<NodeDetails> buildListWithoutDuplicates(final List<NodeDetails> nodes) {
        nscsLogger.info("Command performed on node list of size {}", nodes.size());
        List<NodeDetails> uniqueNodes;
        try {
            final Comparator<NodeDetails> comparator = (NodeDetails o1, NodeDetails o2) -> (o1.compareEndEntity(o2));
            Set<NodeDetails> nodesSet = new TreeSet<>(comparator);
            nodesSet.addAll(nodes);
            uniqueNodes = new ArrayList<>(nodesSet);
        } catch (final DuplicateNodeNamesException e) {
            final String errorMsg = String.format("Duplicate node exception occurred: %s", NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            throw new InvalidInputXMLFileException(e.getMessage());
        }
        nscsLogger.info("Going to execute command on unique node list of size {}", uniqueNodes.size());
        return uniqueNodes;
    }

    /**
     * Generates the enrollment info for the given list of unique nodes.
     * 
     * @param command
     *            the command.
     * @param uniqueNodes
     *            the list of unique nodes.
     * @return the command response.
     */
    private NscsCommandResponse generateEnrollmentInfoForNodes(final EnrollmentInfoFileCommand command, final List<NodeDetails> uniqueNodes) {
        final boolean verbose = command.isVerbose();
        nscsLogger.info("Verbose mode [[{}]", verbose);
        final List<InternalEnrollmentInfo> validNodes = new ArrayList<>();
        final Map<String, NscsServiceException> invalidNodes = new HashMap<>();
        for (final NodeDetails node : uniqueNodes) {
            generateEnrollmentInfoForNode(node, validNodes, invalidNodes, verbose);
        }
        return buildGenerateEnrollmentInfoResponse(uniqueNodes, validNodes, invalidNodes);
    }

    /**
     * Generates the enrollment info for the given node.
     * 
     * @param node
     *            the node.
     * @param validNodes
     *            the list of valid node enrollment info.
     * @param invalidNodes
     *            the map of invalid nodes.
     * @param verbose
     *            the verbose mode.
     */
    private void generateEnrollmentInfoForNode(final NodeDetails node, final List<InternalEnrollmentInfo> validNodes,
            final Map<String, NscsServiceException> invalidNodes, final boolean verbose) {
        try {
            final EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validate(node);
            final EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, verbose);
            validNodes.add(new InternalEnrollmentInfo(enrollmentInfo, enrollmentRequestInfo.getNodeName()));
        } catch (final NscsServiceException e) {
            final String errorMsg = String.format("Exception occurred generating enrollment info for node %s: %s.", node.getNodeFdn(),
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            invalidNodes.put(node.getNodeFdn(), e);
        } catch (final EnrollmentInfoServiceException e) {
            final String errorMsg = String.format("Enrollment info exception occurred generating enrollment info for node %s: %s.", node.getNodeFdn(),
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            invalidNodes.put(node.getNodeFdn(), new GenerateEnrollmentInfoException(errorMsg, e));
        }
    }

    /**
     * Builds the command response for the given list of valid nodes and map of invalid nodes.
     * 
     * @param uniqueNodes
     *            the list of unique nodes.
     * @param validNodes
     *            the list of valid node enrollment info.
     * @param invalidNodes
     *            the map of invalid nodes.
     * @return the command response.
     */
    private NscsCommandResponse buildGenerateEnrollmentInfoResponse(final List<NodeDetails> uniqueNodes,
            final List<InternalEnrollmentInfo> validNodes, final Map<String, NscsServiceException> invalidNodes) {
        NscsCommandResponse nscsCommandResponse = null;

        nscsContextService.initItemsStatsForSyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(invalidNodes.size()));

        // Policy : the command returns response for valid nodes (if any) and, for each invalid node, a compliance with error details is returned.
        if (invalidNodes.isEmpty()) {
            nscsCommandResponse = buildSuccessGenerateEnrollmentInfoResponse(uniqueNodes, validNodes);
        } else {
            if (validNodes.isEmpty()) {
                nscsCommandResponse = buildErrorGenerateEnrollmentInfoResponse(uniqueNodes, invalidNodes);
            } else {
                nscsCommandResponse = buildPartialSuccessGenerateEnrollmentInfoResponse(uniqueNodes, validNodes, invalidNodes);
            }
        }

        nscsContextService.updateItemsResultStatsForSyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(0));

        return nscsCommandResponse;
    }

    /**
     * Builds the successful command response for the given list of valid nodes.
     * 
     * @param uniqueNodes
     *            the list of unique nodes.
     * @param validNodes
     *            the list of valid node enrollment info.
     * @return the successful command response.
     * @throws JAXBException
     *             if JAXB conversion fails.
     * @throws IOException
     *             if I/O exception occurs.
     */
    private NscsCommandResponse buildSuccessGenerateEnrollmentInfoResponse(final List<NodeDetails> uniqueNodes,
            final List<InternalEnrollmentInfo> validNodes) {
        nscsLogger.info("All of the given input nodes [{}] are valid.", uniqueNodes.size());
        final String fileIdentifier = buildSuccessFileIdentifier(validNodes);
        final String message = EnrollmentInfoConstants.SUCCESS_MESSAGE;
        final GenerateEnrollmentInfoResponseBuilder responseBuilder = new GenerateEnrollmentInfoResponseBuilder(true);
        return responseBuilder.buildSuccessResponse(fileIdentifier, message);
    }

    /**
     * Builds the error command response for the given map of invalid nodes.
     * 
     * @param uniqueNodes
     *            the list of unique nodes.
     * @param invalidNodes
     *            the map of invalid nodes.
     * @return the error command response.
     */
    private NscsCommandResponse buildErrorGenerateEnrollmentInfoResponse(final List<NodeDetails> uniqueNodes,
            final Map<String, NscsServiceException> invalidNodes) {
        nscsLogger.error("All of the given input nodes [{}] are invalid.", uniqueNodes.size());
        final GenerateEnrollmentInfoResponseBuilder responseBuilder = new GenerateEnrollmentInfoResponseBuilder(false);
        return responseBuilder.buildErrorResponse(EnrollmentInfoConstants.ENROLLMENT_INFO_FAILED_FOR_ALL_NODES, invalidNodes);
    }

    /**
     * Builds the partially successful command response for the given list of valid nodes and the given map of invalid nodes.
     * 
     * @param uniqueNodes
     *            the list of unique nodes.
     * @param validNodes
     *            the list of valid node enrollment info.
     * @param invalidNodes
     *            the map of invalid nodes.
     * @return the partially successful command response.
     */
    private NscsCommandResponse buildPartialSuccessGenerateEnrollmentInfoResponse(final List<NodeDetails> uniqueNodes,
            final List<InternalEnrollmentInfo> validNodes,
            final Map<String, NscsServiceException> invalidNodes) {
        nscsLogger.info("Only [{}] of the given input nodes [{}] are valid.", validNodes.size(), uniqueNodes.size());
        final String fileIdentifier = buildSuccessFileIdentifier(validNodes);
        final String message = EnrollmentInfoConstants.ENROLLMENT_INFO_COMMAND_INITIATED_FOR_SOME_NODES;
        final GenerateEnrollmentInfoResponseBuilder responseBuilder = new GenerateEnrollmentInfoResponseBuilder(true);
        return responseBuilder.buildPartialSuccessResponse(fileIdentifier, message, invalidNodes);
    }

    /**
     * Builds the success file identifier for the given list of valid nodes.
     * 
     * If a single node is valid, an XML file is returned. If multiple nodes are valid, a tar.gz file is returned containing an XML file for each
     * valid node.
     * 
     * @param validNodes
     *            the list of valid nodes.
     * @return the file identifier.
     * @throws JAXBException
     * @throws IOException
     */
    private String buildSuccessFileIdentifier(final List<InternalEnrollmentInfo> validNodes) {
        String fileIdentifier = null;
        try {
            if (validNodes.size() == 1) {
                final InternalEnrollmentInfo internalEnrollmentInfo = validNodes.get(0);
                final String enrollmentInfoXmlContent = JaxbUtil.getXml(internalEnrollmentInfo.getEnrollmentInfo(), true);
                final byte[] fileContent = enrollmentInfoXmlContent.getBytes(StandardCharsets.UTF_8);
                final String fileName = String.format("%s%s", internalEnrollmentInfo.getNodeName(),
                        EnrollmentInfoConstants.ENROLLMENT_CONFIGURATION_XML);
                final String contentType = FileConstants.XML_CONTENT_TYPE;
                fileIdentifier = createDeletableDownloadFileIdentifier(fileContent, fileName, contentType);
            } else {
                final Map<String, ByteArrayOutputStream> nodeByteArrayOutputStream = new LinkedHashMap<>();
                for (final InternalEnrollmentInfo internalEnrollmentInfo : validNodes) {
                    final ByteArrayOutputStream byteArrayOutputStream = xmlOperatorUtils
                            .convertObjectToXmlStream(internalEnrollmentInfo.getEnrollmentInfo(), EnrollmentInfo.class);
                    final String fileName = String.format("%s%s", internalEnrollmentInfo.getNodeName(), FileConstants.XML_EXTENSION);
                    nodeByteArrayOutputStream.put(fileName, byteArrayOutputStream);
                }
                final byte[] fileContent = fileUtil.getArchiveFileBytes(nodeByteArrayOutputStream,
                        EnrollmentInfoConstants.ENROLLMENT_INFO_XML_FILE_NAME);
                final String fileName = String.format("%s%s", EnrollmentInfoConstants.ENROLLMENT_CONFIGURATION_ZIP_FILE_NAME,
                        FileConstants.TAR_GZ_EXTENSION);
                final String contentType = FileConstants.GZIP_CONTENT_TYPE;
                fileIdentifier = createDeletableDownloadFileIdentifier(fileContent, fileName, contentType);
            }
        } catch (final JAXBException | IOException e) {
            final String errorMessage = String.format("Exception occurred : %s", NscsLogger.stringifyException(e));
            throw new GenerateEnrollmentInfoException(errorMessage, e);
        }
        return fileIdentifier;
    }

    /**
     * Creates a deletable download file holder identifier.
     * 
     * @param fileContents
     *            bytes to be written to the downloading file.
     * @param fileName
     *            name of the download file.
     * @param contentType
     *            type of content of downloading file.
     * @return the download file holder identifier
     * @throws {@link
     *             GenerateEnrollmentInfoException} thrown when failure occurs while preparing file download.
     */
    private String createDeletableDownloadFileIdentifier(final byte[] fileContents, final String fileName, final String contentType) {
        try {
            return fileUtil.createDeletableDownloadFileIdentifier(fileContents, fileName, contentType);
        } catch (final IOException e) {
            final String errorMessage = String.format(EnrollmentInfoConstants.IO_EXCEPTION_FILE_FORMAT, fileName);
            throw new GenerateEnrollmentInfoException(errorMessage, e);
        }
    }

    /**
     * Auxiliary inner class to manage enrollment info for a node.
     */
    class InternalEnrollmentInfo {
        private EnrollmentInfo enrollmentInfo;
        private String nodeName;

        /**
         * @param enrollmentInfo
         *            the enrollment info.
         * @param nodeName
         *            the node name.
         */
        public InternalEnrollmentInfo(EnrollmentInfo enrollmentInfo, String nodeName) {
            this.enrollmentInfo = enrollmentInfo;
            this.nodeName = nodeName;
        }

        /**
         * @return the enrollmentInfo
         */
        public EnrollmentInfo getEnrollmentInfo() {
            return enrollmentInfo;
        }

        /**
         * @return the nodeName
         */
        public String getNodeName() {
            return nodeName;
        }

    }
}
