package com.ericsson.nms.security.nscs.handler.command.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.InvalidJobException;
import com.ericsson.nms.security.nscs.api.exception.MaxNodesExceededException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelProcessor;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequest;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequestType;
import com.ericsson.nms.security.nscs.cpp.level.SecurityLevelProcessorFactory;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.XmlValidatorUtils;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.CppSetSecurityLevelValidator;
import com.ericsson.nms.security.nscs.iscf.IscfConfigurationBean;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Starts the process to change security level of a node or list of nodes
 * </p>
 * Created by emaynes on 04/05/2014.
 */
@CommandType(NscsCommandType.CPP_SET_SL)
@Local(CommandHandlerInterface.class)
public class CppSetSecurityLevelHandler implements CommandHandler<CppSecurityLevelCommand>, CommandHandlerInterface {

    public static final String SECURITY_LEVEL_INITIATED_CHECK_THE_LOGS_FOR_DETAILS = "Security level change initiated, check the system logs for results";
    public static final String SECURITY_LEVEL_INITIATED = "Security level change initiated. Perform 'secadm job get -j %s' to get progress info.";
    public static final String SECURITY_LEVEL_INITIATED_DYN_ISSUE = "Security level change initiated for some node(s). Perform 'secadm job get -j %s' to get progress info. Error details are listed below for other nodes :";
    public static final String SECURITY_LEVEL_NOT_INITIATED = "Failed to initiate Security level change for all the provided node(s). Error details are listed below :";
    public static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };

    private static final int NO_OF_COLUMNS = 3;
    private static final int MAX_NO_OF_NODES_ALLOWED = 450;

    @Inject
    private Logger logger;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private SecurityLevelProcessorFactory securityLevelProcessorFactory;

    @Inject
    private XmlValidatorUtils xmlValidatorUtils;

    @Inject
    protected IscfConfigurationBean config;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private CppSetSecurityLevelValidator cppSetSecurityLevelValidator;

    @EJB
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    @Inject
    private NscsContextService nscsContextService;

    /**
     *
     * @param command
     *            CppSetSecurityLevelCommand instance
     * @param context
     *            a CommandContext instance
     * @return NscsMessageCommandResponse with a success message
     * @throws NscsServiceException
     */

    //SL2 dheeraj code

    @Override
    public NscsCommandResponse process(final CppSecurityLevelCommand command, final CommandContext context) throws NscsServiceException {

        String responseMessageWithJobId = "";
        JobStatusRecord jobStatusRecord = null;

        logger.debug("Set Security level change command [{}]", command);
        final String inputFileLocation = command.getXmlInputFile();

        final String fileData = getSecLevelInputData(command, inputFileLocation);
        if (null == fileData || fileData.isEmpty()) {
            throw new InvalidFileContentException();
        } else {
            final boolean isValidXMLFile = xmlValidatorUtils.validateXMLSchemaForSecLevel(fileData);
            if (isValidXMLFile) {
                final Nodes xmlNodes = createNodesFromSecLevelConfiguration(fileData);
                final List<Node> xmlNodeList = xmlNodes.getNode();
                if (xmlNodeList.size() > MAX_NO_OF_NODES_ALLOWED) {
                    logger.warn("Number of nodes, for Sec Level operation, specified exceeds the maximum : {}", MAX_NO_OF_NODES_ALLOWED);
                    throw new MaxNodesExceededException(MAX_NO_OF_NODES_ALLOWED);
                }

                logger.info("Starting process to change the security level of nodes : {}", context.getValidNodes());
                logger.debug("Set security level command invoked {}", command);

                final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>();
                final Map<Node, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
                final List<Node> inputNodes = new ArrayList<Nodes.Node>(xmlNodeList);
                final List<Node> uniqueNodes = new ArrayList<Nodes.Node>(new HashSet<Nodes.Node>(inputNodes));
                final Map<String, SecurityLevel> currentSecurityLevels = new HashMap<String, SecurityLevel>();
                final Map<String, String> requestedEnrollmentModes = new HashMap<String, String>();

                logger.debug("Number of input nodes {}", uniqueNodes.size());
                final SecurityLevel requiredSecurityLevel = SecurityLevel.getSecurityLevel(command.getSecurityLevel());

                final boolean areInputNodesValid = cppSetSecurityLevelValidator.validateNodes(uniqueNodes, validNodesList, invalidNodesErrorMap,
                        currentSecurityLevels, requiredSecurityLevel, requestedEnrollmentModes);

                nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodesList.size()),
                        Integer.valueOf(invalidNodesErrorMap.size()));

                if (areInputNodesValid) {

                    jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CPP_SET_SL);
                    logger.info("All of the given input nodes are Valid. Security level change workflow need to be executed.");
                    startLevelChangeForValidNodes(validNodesList, requestedEnrollmentModes, currentSecurityLevels, requiredSecurityLevel,
                            jobStatusRecord);

                    responseMessageWithJobId = String.format(SECURITY_LEVEL_INITIATED, jobStatusRecord.getJobId().toString());

                    return NscsCommandResponse.message(responseMessageWithJobId);
                } else {
                    final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                    response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                    if (!validNodesList.isEmpty()) {

                        logger.debug("Number of Valid Nodes are :{}", validNodesList.size());

                        jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CPP_SET_SL);
                        startLevelChangeForValidNodes(validNodesList, requestedEnrollmentModes, currentSecurityLevels, requiredSecurityLevel,
                                jobStatusRecord);

                        responseMessageWithJobId = String.format(SECURITY_LEVEL_INITIATED_DYN_ISSUE, jobStatusRecord.getJobId().toString());

                        if (!invalidNodesErrorMap.isEmpty()) {
                            logger.debug("invalidNodesErrorMap size is {}", invalidNodesErrorMap.size());
                            final Set<java.util.Map.Entry<Node, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
                            for (final java.util.Map.Entry<Node, NscsServiceException> entry : entrySet) {

                                String errorCode = "" + entry.getValue().getErrorCode();

                                //This Exception will occur when node is already in requested security level since it is not an error
                                //we are making error code as NotApplicable
                                if (entry.getValue() instanceof InvalidJobException) {
                                    errorCode = "NA";
                                }

                                response.add(entry.getKey().getNodeFdn(),
                                        new String[] { errorCode, entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
                            }
                        }
                        response.setAdditionalInformation(responseMessageWithJobId);
                        return response;
                    } else {
                        logger.info("All the given nodes are invalid!");
                        logger.debug("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
                        for (final java.util.Map.Entry<Node, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {

                            String errorCode = "" + entry.getValue().getErrorCode();

                            if (entry.getValue() instanceof InvalidJobException) {
                                errorCode = "NA";
                            }

                            response.add(entry.getKey().getNodeFdn(),
                                    new String[] { errorCode, entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
                        }

                        response.setAdditionalInformation(SECURITY_LEVEL_NOT_INITIATED);
                        return response;
                    }
                }
            } else {
                logger.warn(NscsErrorCodes.XML_VALIDATION_FAILED);
                throw new InvalidInputXMLFileException();
            }
        }
    }

    /**
     * To start workflow processing Command
     *
     * @param validNodesList
     *            the Norm Node Reference list
     * @param requiredSecurityLevel
     *            the required security level change
     * @param currentSecurityLevels
     *            the currentSecurity Levels of nodes
     * @param requestedEnrollmentModes
     *            the requested enrollment mode type
     */
    private void startLevelChangeForValidNodes(final List<NormalizableNodeReference> validNodesList,
                                               final Map<String, String> requestedEnrollmentModes,
                                               final Map<String, SecurityLevel> currentSecurityLevels, final SecurityLevel requiredSecurityLevel,
                                               final JobStatusRecord jobStatusRecord) {

        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();

        int workflowId = 1;
        for (final NormalizableNodeReference normNode : validNodesList) {

            final SecLevelRequest request = constructSecurityLevelRequest(normNode, requestedEnrollmentModes, currentSecurityLevels,
                    requiredSecurityLevel);

            final SecLevelProcessor levelProcessor = securityLevelProcessorFactory.createSecLevelProcessor(request);

            final WfResult result = levelProcessor.processCommand(request, jobStatusRecord, workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    /**
     * Method to create List of nodes with user data.
     *
     * @param fileContent
     *            : Content of user input file.
     * @return {@link Nodes}
     */
    private Nodes createNodesFromSecLevelConfiguration(final String fileContent) {
        Nodes nodes = null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Nodes.class);
            Unmarshaller jaxbUnmarshaller = null;
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            nodes = (Nodes) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(fileContent)));
        } catch (final JAXBException e) {
            // If there is any JAXB conversion error, we are throwing an invalid
            // XML exception.
            // It should not happen as we have already validated input with XSD.
            logger.error("Invalid input XML file. Unmarshalling of XML failed.");
            throw new InvalidInputXMLFileException();
        }
        return nodes;
    }

    protected SecLevelRequestType constructTheLevelRequestType(final SecLevelRequest secLevelRequest) {
        SecLevelRequestType requestType = null;
        if (secLevelRequest.getCurrentSecurityLevel().compareTo(secLevelRequest.getRequiredSecurityLevel()) < 0) {

            requestType = SecLevelRequestType.ACTIVATE_SECURITY_LEVEL;
        } else if (secLevelRequest.getCurrentSecurityLevel().compareTo(secLevelRequest.getRequiredSecurityLevel()) > 0) {

            requestType = SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL;
        }
        return requestType;
    }

    private void setEnrollmentMode(final String enrollmentMode, final NormalizableNodeReference normNode) {

        try {
            logger.info("Updating Enrollment Mode in NetworkElementSecurity MO {}", normNode.getFdn());

            final String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normNode.getName())
                    .fdn();

            logger.info("Updating Enrollment Mode in NetworkElementSecurity MO {} NES fdn", networkElementSecurityFdn);

            writer.withSpecification(networkElementSecurityFdn).setAttribute(NetworkElementSecurity.ENROLLMENT_MODE, enrollmentMode).updateMO();

            logger.info("EnrolmentMode succesfully set for node {}", normNode.getFdn());

        } catch (final Exception e) {
            logger.info("Update of Enrollment mode in NetworkElementSecurity MO failed!", e);

        }

    }

    /**
     * This method with get the String equivalent of input file.
     *
     * @param inputFileLocation
     *            : location of the input file
     * @return {@link String}
     */
    private String getSecLevelInputData(final CppSecurityLevelCommand command, final String inputFileLocation) {
        /*
         * final Map<String, Object> properties = command.getProperties(); final byte[] fileDataInByte = (byte[]) properties .get(inputFileLocation);
         * String fileData = null; if (fileDataInByte != null) { try { fileData = new String(fileDataInByte, "UTF-8"); } catch
         * (UnsupportedEncodingException e) { logger.error(NscsErrorCodes.INVALID_ENCODING); throw new
         * InvalidFileContentException(NscsErrorCodes.INVALID_ENCODING); } } else { logger.error("Invalid content of input XML file."); throw new
         * InvalidFileContentException(); }
         *
         * return fileData;
         */

        final Map<String, Object> properties = command.getProperties();

        String content = null;

        logger.info("inputFileLocation: {}", inputFileLocation);
        final String filePath = (String) properties.get("filePath");

        if (inputFileLocation != null && !inputFileLocation.isEmpty()) {
            final String osAppropriatePath = System.getProperty("file.separator").equalsIgnoreCase("/") ? filePath : filePath.substring(1);
            try {
                final Path path = Paths.get(URLDecoder.decode(osAppropriatePath, "UTF-8"));

                final Charset charset = Charset.forName("UTF-8");

                final byte[] data = Files.readAllBytes(path);

                content = new String(data, charset);

            } catch (final IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
                //logger.debug("{} [{}]", FOUND_NO_DATA, filePath);
                logger.error(e.getMessage());
                throw new CommandSyntaxException();
            }
        } else {
            //logger.debug("{} [{}]", FOUND_NO_DATA, filePath);
            logger.info(" inputFileLocation is null {}", filePath);
            throw new CommandSyntaxException();
        }

        if (content.isEmpty()) {
            logger.debug("File [{}] is empty", inputFileLocation);
            throw new CommandSyntaxException();
        }
        return content;
    }

    private SecLevelRequest constructSecurityLevelRequest(final NormalizableNodeReference normNode,
                                                          final Map<String, String> requestedEnrollmentModes,
                                                          final Map<String, SecurityLevel> currentSecurityLevels,
                                                          final SecurityLevel requiredSecurityLevel) {

        final String fdn = normNode.getFdn();

        setEnrollmentMode(requestedEnrollmentModes.get(fdn), normNode);

        final SecurityLevel currentSecurityLevel = currentSecurityLevels.get(fdn);
        logger.debug("CurrentSecurityLevel for node name {} is {}", fdn, currentSecurityLevel);

        final SecLevelRequest request = new SecLevelRequest();
        request.setRequiredSecurityLevel(requiredSecurityLevel);
        request.setCurrentSecurityLevel(currentSecurityLevel);
        request.setNodeName(normNode.getName());
        request.setNodeFDN(normNode.getFdn());

        final SecLevelRequestType requestType = constructTheLevelRequestType(request);

        request.setSecLevelRequestType(requestType);

        return request;
    }

}
