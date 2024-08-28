package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CertificateIssueCommand;
import com.ericsson.nms.security.nscs.api.exception.CertificateIssueWfException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCommandArgumentException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentInfo;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.NodeEnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.node.certificate.validator.CertificateEnrollmentValidator;
import com.ericsson.nms.security.nscs.node.certificate.validator.CertificateIssueValidator;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Issues certificate for the provided list of nodes depending on the requested type: IPSEC or OAM.
 * </p>
 *
 * Created by enmadmin
 */
@CommandType(NscsCommandType.CERTIFICATE_ISSUE)
@Local(CommandHandlerInterface.class)
public class CertificateIssueHandler implements CommandHandler<CertificateIssueCommand> {

    public static final String CERTIFICATE_ISSUE_EXECUTED = "Successfully started a job to issue certificates for nodes";
    public static final String CERTIFICATE_ISSUE_EXECUTED_DYN_ISSUE = "Successfully started a job to issue certificates for valid nodes only. Invalid nodes are listed below :";
    public static final String CERTIFICATE_ISSUE_NOT_EXECUTED = "Certificate issue command not executed as some provided nodes are invalid. Details are given below :";

    public static final String XSD_VALIDATOR_FILE_NAME = "ValidatorInputForCertIssue.xsd";
    public static final String ENROLLMENT_INFO_NEW_SCHEMA = "EnrollmentInfoNewSchema.xsd";
    public static final String XML_SCHEMA_TRANSFORMATION_FILE = "xsl/EnrollmentXmlTransformationSchema.xsl";

    public static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail" };
    private static final int NO_OF_COLUMNS = 2;

    @Inject
    private NscsLogger logger;

    @EJB
    private NscsCommandManager commandManager;

    @Inject
    private CertificateIssueValidator certificateIssueValidator;

    @Inject
    private CertificateEnrollmentValidator certificateEnrollmentValidator;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    @Inject
    private XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Inject
    private XmlOperatorUtils xmlOperatorUtils;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private CliUtil cliUtil;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsCommandResponse process(final CertificateIssueCommand command, final CommandContext context) throws NscsServiceException {

        logger.info("Issue certificate command [{}]", command);

        final String certType = command.getCertType();
        if (commandManager.validateCertTypeValue(certType)) {
            String fileData = cliUtil.getCommandInputData(command, "file:");
            boolean isValidNewXml = false;
            boolean isValidOldXML = false;

            if (null == fileData || fileData.isEmpty()) {
                logger.error(NscsErrorCodes.INVALID_FILE_CONTENT);
                throw new InvalidFileContentException();
            } else {
                final boolean isEnrollmentWithExternalCA = command.getProperties().containsKey(CertificateIssueCommand.EXTERNAL_CA_PROPERTY);
                if (isEnrollmentWithExternalCA) {
                    logger.info("Enrollment using External Certificate Enrollment Authority");
                    if (certificateEnrollmentValidator.validateCertificateTypeForExtCa(certType)) {
                        isValidNewXml = xmlValidatorUtility.validateXMLSchema(fileData, ENROLLMENT_INFO_NEW_SCHEMA);
                    }
                } else {
                    logger.info("processing xml file provided by user");
                    logger.debug("userprovided xml input [{}] ", fileData);
                    isValidOldXML = xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME);
                    if (isValidOldXML) {
                        final String convertedFormatXml = xmlOperatorUtils.transformXmlSchema(fileData, XML_SCHEMA_TRANSFORMATION_FILE);
                        logger.info("Input XML after converting to new format: " + convertedFormatXml);
                        fileData = convertedFormatXml;
                        isValidNewXml = true;
                    } else {
                        isValidNewXml = xmlValidatorUtility.validateXMLSchema(fileData, ENROLLMENT_INFO_NEW_SCHEMA);
                    }
                }

                if (isValidNewXml) {
                    final EnrollmentDetails enrollmentDetails = xmlUnMarshallerUtility.xMLUnmarshaller(fileData, EnrollmentDetails.class);
                    return validateAndProcessRequest(command, context, enrollmentDetails, isEnrollmentWithExternalCA);
                } else {
                    logger.error(NscsErrorCodes.XML_VALIDATION_FAILED);
                    throw new InvalidInputXMLFileException();
                }
            }
        } else {
            logger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
            final String errmsg = String.format(" Invalid argument for parameter %s",
                    CertificateIssueCommand.CERT_TYPE_PROPERTY + ". Accepted arguments are " + commandManager.getValidCertificateTypes());
            logger.error(errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }
    }

    private NscsCommandResponse validateAndProcessRequest(final CertificateIssueCommand command, final CommandContext context,
            final EnrollmentDetails enrollmentDetails, final boolean isEnrollmentWithExternalCA) {

        final String certificateType = command.getCertType();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList = enrollmentDetails.getNodeEnrollmentDetails();

        final List<NodeEnrollmentDetails> enrollmentInfoList = new ArrayList<>();

        List<Node> validNodesList = null;
        for (NodeEnrollmentDetails nodeEnrollmentDetails : nodeEnrollmentDetailsList) {
            validNodesList = new ArrayList<>();
            final ExternalCAEnrollmentInfo extCAEnrollmentInfo = nodeEnrollmentDetails.getExternalCAEnrollmentInfo();

            validateCommandForExtCA(isEnrollmentWithExternalCA, extCAEnrollmentInfo);

            final List<Node> xmlNodeList = nodeEnrollmentDetails.getNodes().getNode();
            logger.info("Number of nodes read {}", xmlNodeList.size());
            certificateIssueValidator.validate(xmlNodeList, certificateType, validNodesList, invalidNodesErrorMap, isEnrollmentWithExternalCA);

            if (validNodesList.size() != 0) {
                NodeEnrollmentDetails nodeEnrollDetails = new NodeEnrollmentDetails();
                nodeEnrollDetails.setExternalCAEnrollmentInfo(nodeEnrollmentDetails.getExternalCAEnrollmentInfo());
                Nodes nodes = new Nodes();
                nodes.setNode(validNodesList);
                nodeEnrollDetails.setNodes(nodes);

                enrollmentInfoList.add(nodeEnrollDetails);
            }
        }
        return initiateWorkflow(certificateType, invalidNodesErrorMap, context, enrollmentInfoList);
    }

    private void validateCommandForExtCA(final boolean isEnrollmentWithExternalCA, final ExternalCAEnrollmentInfo extCAEnrollmentInfo) {
        if (isEnrollmentWithExternalCA) {
            if (extCAEnrollmentInfo == null) {
                final String errmsg = "ExternalCAEnrollmentInfo should be provided for enrollment with External CA";
                logger.error(errmsg);
                throw new InvalidInputXMLFileException(errmsg);
            }

        } else {
            if (extCAEnrollmentInfo != null) {
                logger.error(NscsErrorCodes.UNSUPPORTED_COMMAND_ARGUMENT);
                final String errmsg = "ExternalCAEnrollmentInfo is not supported for command provided. ";
                logger.error("ExternalCAEnrollmentInfo is not supported for command provided. ");
                throw new UnsupportedCommandArgumentException(errmsg, "Try Removing ExternalCAEnrollmentInfo tag in input xml provided");
            }
        }
    }

    private NscsCommandResponse initiateWorkflow(final String certType, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            final CommandContext context, final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList) {
        String jobIdMessage = "";

        final Integer numValidNodes = getNumValidNodesInNodeEnrollmentDetailsList(nodeEnrollmentDetailsList);
        nscsContextService.initItemsStatsForAsyncCommand(numValidNodes, Integer.valueOf(invalidNodesErrorMap.size()));

        if (invalidNodesErrorMap.isEmpty()) {
            jobIdMessage = buildResponseForValidInputNodes(nodeEnrollmentDetailsList, certType);
            return NscsCommandResponse.message(jobIdMessage);
        } else {
            // only DYNAMIC PARAMS ERRORS are found
            if (!nodeEnrollmentDetailsList.isEmpty()) {
                jobIdMessage = buildResponseForValidInputNodes(nodeEnrollmentDetailsList, certType);
                final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
                for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
                    response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(),
                            entry.getValue().getSuggestedSolution() });
                }
                response.setAdditionalInformation(jobIdMessage);
                return response;
            } else {
                logger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
                for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
                    context.setAsInvalidOrFailed(entry.getKey(), entry.getValue());
                }
                return NscsCommandResponse.message(CERTIFICATE_ISSUE_NOT_EXECUTED);
            }
        }
    }

    /**
     * Get number of valid nodes present in the given Node Enrollment Details list.
     * 
     * @param nodeEnrollmentDetailsList
     *            the Node Enrollment Details list.
     * @return the number of nodes.
     */
    private Integer getNumValidNodesInNodeEnrollmentDetailsList(final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList) {
        int numValidNodes = 0;
        for (final NodeEnrollmentDetails nodeEnrollmentDetails : nodeEnrollmentDetailsList) {
            numValidNodes += nodeEnrollmentDetails.getNodes().getNode().size();
        }
        return Integer.valueOf(numValidNodes);
    }

    private String buildResponseForValidInputNodes(final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList, final String certType) {
        String jobIdMessage = "";
        try {
            JobStatusRecord jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_ISSUE);
            logger.info("All of the given input nodes are Valid. Certificate issue workflow need to be executed.");
            jobIdMessage = CERTIFICATE_ISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString()
                    + "' to get progress info.";
            commandManager.executeCertificateIssueWfs(nodeEnrollmentDetailsList, certType, jobStatusRecord);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new CertificateIssueWfException();
        }
        return jobIdMessage;
    }
}
