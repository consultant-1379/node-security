package com.ericsson.nms.security.nscs.handler.command.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import com.ericsson.nms.security.nscs.api.command.types.TrustDistributeCommand;
import com.ericsson.nms.security.nscs.api.exception.EntitiesWithValidCategoryForNodesNotFound;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.TrustDistributeWfException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.TrustedCAInformation;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.TrustValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.ExternalTrustedCACertificatesDetails;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes.NodeTrustInfo;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.NodesTrustedCACertificateDetails;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.topologyCollectionsService.exception.rest.EmptyFileException;

/**
 * <p>
 * Distributes trusts for the provided list of nodes depending on the requested type: IPSEC,OAM and LAAD.
 * </p>
 *
 * Created by enmadmin
 */
@UseValidator({ NoDuplNodeNamesAllowedValidator.class })
@CommandType(NscsCommandType.TRUST_DISTRIBUTE)
@Local(CommandHandlerInterface.class)
public class TrustDistributeHandler implements CommandHandler<TrustDistributeCommand>, CommandHandlerInterface {

    public static final String TRUST_DISTRIBUTION_EXECUTED = "Successfully started a job for trust distribution to nodes";
    public static final String TRUST_DISTRIBUTION_EXECUTED_DYN_ISSUE = "Successfully started a job for trust distribution of valid nodes only. Perform 'secadm job get -j %s' to get progress info of valid nodes.";
    public static final String TRUST_DISTRIBUTION_NOT_EXECUTED = "Trust distribute command not executed as no input node is valid.";
    public static final String TRUST_DISTRIBUTION_EMPTY = "Trust distribute command not executed as no entities are retrieved";
    public static final String DEPRECATED_WARNING_MESSAGE = " [Warning: The command with --certType option will be deprecated in the future. Use --trustcategory property instead of --certType]";
    public static final String INVALID_ARGUMENT = " Invalid argument for parameter %s";
    public static final String INPUT_CA_NOT_VALID = ". Input CA value is not valid.";
    public static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };
    private static final int NO_OF_COLUMNS = 3;

    @Inject
    private NscsLogger logger;

    @EJB
    private NscsCommandManager commandManager;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private TrustValidator trustValidator;

    @Inject
    private XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Inject
    private NscsContextService nscsContextService;

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.nms.security.nscs.handler.command.CommandHandler#process (com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand,
     * com.ericsson.nms.security.nscs.handler.CommandContext)
     */
    @Override
    public NscsCommandResponse process(final TrustDistributeCommand command, final CommandContext context) throws NscsServiceException {

        logger.info("Trust distribute command [{}]", command);
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
        response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
        final List<NodeTrustInfo> validNodesList = new ArrayList<>();
        final Map<NodeTrustInfo, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        Boolean hasCertTypeProperty = false;
        String trustCategory = "";
        JobStatusRecord jobStatusRecord = null;
        final List<TrustedCAInformation> trustedCAInformationlist = new ArrayList<>();

        if (command.getCertType() != null) {
            trustCategory = command.getCertType();
            hasCertTypeProperty = true;
        } else {
            trustCategory = command.getTrustCategory();
        }

        trustValidator.validateTrustDistributeCommand(command);
        trustValidator.validateCommandForCertTypeAndTrustCategory(command.getCertType(), trustCategory);

        if (command.getCaValue() != null) {
            checkEntityNameAvailability(command.getCaValue());
        }

        prepareTrustedCAInformationList(trustCategory, command, trustedCAInformationlist, invalidNodesErrorMap);

        final Integer numValidNodes = getNumValidNodesFromTrustedCAInformationList(trustedCAInformationlist);
        nscsContextService.initItemsStatsForAsyncCommand(numValidNodes, Integer.valueOf(invalidNodesErrorMap.size()));

        if (invalidNodesErrorMap.isEmpty()) {
            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.TRUST_DISTRIBUTE);
            initiateWorkflow(trustCategory, command.getCaValue(), jobStatusRecord, trustedCAInformationlist);
            return NscsCommandResponse.message(prepareSuccessResponse(jobStatusRecord, hasCertTypeProperty));
        } else {
            if (!validNodesList.isEmpty() || !trustedCAInformationlist.isEmpty()) {
                jobStatusRecord = cacheHandler.insertJob(NscsCommandType.TRUST_DISTRIBUTE);
                initiateWorkflow(trustCategory, command.getCaValue(), jobStatusRecord, trustedCAInformationlist);
                return prepareMultipleValueCommandResponse(invalidNodesErrorMap, hasCertTypeProperty, jobStatusRecord, response);
            } else
                return prepareFailureResponse(invalidNodesErrorMap, response, hasCertTypeProperty);
        }
    }

    /**
     * Get the number of valid nodes from the given Trusted CA Information list.
     * 
     * @param trustedCAInformationList
     *            the Trusted CA Information list.
     * @return the number of valid nodes.
     */
    private Integer getNumValidNodesFromTrustedCAInformationList(final List<TrustedCAInformation> trustedCAInformationList) {
        int numValidNodes = 0;
        for (final TrustedCAInformation trustedCAInformation : trustedCAInformationList) {
            numValidNodes += trustedCAInformation.getValidNodes().size();
        }
        return Integer.valueOf(numValidNodes);
    }

    private void checkEntityNameAvailability(final String inputCA) {
        logger.info("checking available CA name {}", inputCA);
        try {
            if (nscsPkiManager.isEntityNameAvailable(inputCA, EntityType.CA_ENTITY) && nscsPkiManager.isExtCaNameAvailable(inputCA)) {
                final String errmsg = String.format(INVALID_ARGUMENT, TrustDistributeCommand.CA_PROPERTY + INPUT_CA_NOT_VALID);
                logger.error(errmsg);
                throw new InvalidArgumentValueException(errmsg);
            }
        } catch (final NscsPkiEntitiesManagerException ex) {
            logger.warn("CA name {} not available !!! An internal Database error or service exception has occurred: {}", inputCA, ex.getMessage());
            final String errmsg = String.format(INVALID_ARGUMENT, TrustDistributeCommand.CA_PROPERTY + INPUT_CA_NOT_VALID);
            logger.error(errmsg);
            throw new InvalidArgumentValueException(errmsg);
        } catch (final Exception ex) {
            logger.warn("CA name {} not available !!! Generic error retrieving entities from CA {}", inputCA, ex);
            final String errmsg = String.format(INVALID_ARGUMENT, TrustDistributeCommand.CA_PROPERTY + INPUT_CA_NOT_VALID);
            logger.error(errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }

        logger.debug("CA name found. Looking for entities by CA {}", inputCA);
    }

    private Set<String> getNodeNames(final String trustCategory) {
        final Set<String> nodeNamesFromEntities = new HashSet<>();
        List<Entity> entityList = null;
        try {
            entityList = nscsPkiManager.getEntities();
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while getEntities";
            logger.error(errorMessage);
            throw new EntitiesWithValidCategoryForNodesNotFound();
        }
        if (entityList != null) {
            logger.debug("entityList size: {}, entity: {}", entityList.size(), printEntityNames(entityList));
            for (final Entity entity : entityList) {
                final String entityName = entity.getEntityInfo().getName();
                final EntityCategory entityCategory = entity.getCategory();
                logger.debug("EntityCategory for entity [{}] is [{}]", entity.getEntityInfo().getName(), entity.getCategory());
                final NodeEntityCategory nodeEntityCategory = NscsPkiEntitiesManagerJar.findNodeEntityCategory(entityCategory);
                if (nodeEntityCategory != null && trustCategory.equals(nodeEntityCategory.name())) {
                    final String nodeName = entityName.split("-")[0];
                    logger.debug("trustCategory is [{}], nodeName {}", trustCategory, nodeName);
                    nodeNamesFromEntities.add(nodeName);
                }
            }
        } else {
            logger.info(TRUST_DISTRIBUTION_EMPTY + "for CA name [{}]");
            throw new EntitiesWithValidCategoryForNodesNotFound();
        }
        if (nodeNamesFromEntities.isEmpty()) {
            throw new EntitiesWithValidCategoryForNodesNotFound();
        }
        return nodeNamesFromEntities;
    }

    private String printEntityNames(final List<Entity> entityList) {
        String entityNames = "";
        for (final Entity e : entityList) {
            entityNames += e.getEntityInfo().getName() + " ";
        }
        return entityNames;
    }

    private NscsCommandResponse prepareMultipleValueCommandResponse(final Map<NodeTrustInfo, NscsServiceException> invalidNodesErrorMap,
            final Boolean hasCertProperty, JobStatusRecord jobStatusRecord, final NscsNameMultipleValueCommandResponse response) {

        String jobIdMessage = preparePartialSuccessResponse(jobStatusRecord, hasCertProperty);
        final Set<java.util.Map.Entry<NodeTrustInfo, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
        for (java.util.Map.Entry<NodeTrustInfo, NscsServiceException> entry : entrySet) {
            response.add(entry.getKey().getNodeFdn(),
                    new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
        }
        response.setAdditionalInformation(jobIdMessage + " Invalid node details are given below : ");
        return response;
    }

    private NscsCommandResponse prepareFailureResponse(final Map<NodeTrustInfo, NscsServiceException> invalidNodesErrorMap,
            final NscsNameMultipleValueCommandResponse response, final Boolean hasCertProperty) {

        final Set<java.util.Map.Entry<NodeTrustInfo, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
        for (java.util.Map.Entry<NodeTrustInfo, NscsServiceException> entry : entrySet) {
            response.add(entry.getKey().getNodeFdn(),
                    new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
        }

        if (hasCertProperty) {
            response.setAdditionalInformation(TRUST_DISTRIBUTION_NOT_EXECUTED + DEPRECATED_WARNING_MESSAGE + " Details are given below : ");
        } else {
            response.setAdditionalInformation(TRUST_DISTRIBUTION_NOT_EXECUTED + " Details are given below : ");
        }
        return response;
    }

    private void initiateWorkflow(final String trustCategory, final String inputCA, final JobStatusRecord jobStatusRecord,
            final List<TrustedCAInformation> trustedCAInformationlist) {
        try {
            commandManager.executeTrustDistributeWfs(trustCategory, inputCA, jobStatusRecord, trustedCAInformationlist);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new TrustDistributeWfException();
        }
    }

    private String prepareSuccessResponse(JobStatusRecord jobStatusRecord, final Boolean hasCertProperty) {
        String successMessage = "";
        if (jobStatusRecord != null) {
            successMessage = TRUST_DISTRIBUTION_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString()
                    + "' to get progress info.";
            if (hasCertProperty) {
                successMessage = successMessage + DEPRECATED_WARNING_MESSAGE;
            }
        }
        return successMessage;
    }

    private String preparePartialSuccessResponse(JobStatusRecord jobStatusRecord, final Boolean hasCertProperty) {
        String successMessage = "";
        if (jobStatusRecord != null) {
            successMessage = String.format(TRUST_DISTRIBUTION_EXECUTED_DYN_ISSUE, jobStatusRecord.getJobId().toString());
            if (hasCertProperty) {
                successMessage = successMessage + DEPRECATED_WARNING_MESSAGE;
            }
        }
        return successMessage;
    }

    private void prepareTrustedCAInformationList(final String trustCategory, final TrustDistributeCommand command,
            final List<TrustedCAInformation> trustedCAInformationlist, final Map<NodeTrustInfo, NscsServiceException> invalidNodesErrorMap) {

        final Boolean isExternalCa = command.getProperties().containsKey(TrustDistributeCommand.EXTERNAL_CA_PROPERTY);
        final List<NodeTrustInfo> validNodesList = new ArrayList<>();
        if (isExternalCa) {
            String fileData = getInputData(command, "file:");
            trustValidator.validateTrustCategoryTypeForExtCa(trustCategory);
            trustValidator.validateFileDataForExtCa(fileData);

            final ExternalTrustedCACertificatesDetails externalTrustedCACertificatesDetails = xmlUnMarshallerUtility.xMLUnmarshaller(fileData,
                    ExternalTrustedCACertificatesDetails.class);
            final List<NodesTrustedCACertificateDetails> nodesTrustedCACertificateDetailsList = externalTrustedCACertificatesDetails
                    .getNodesTrustedCACertificateDetails();
            for (final NodesTrustedCACertificateDetails nodesTrustedCACertificateDetails : nodesTrustedCACertificateDetailsList) {
                final List<NodeTrustInfo> inputNodes = nodesTrustedCACertificateDetails.getNodes().getNode();
                trustValidator.validateNodesForTrustDistribute(inputNodes, trustCategory, validNodesList, invalidNodesErrorMap, isExternalCa);
                if (!validNodesList.isEmpty()) {
                    final TrustedCAInformation trustedCAInformation = new TrustedCAInformation();
                    trustedCAInformation.setValidNodes(validNodesList);
                    trustedCAInformation.setTrustedCACertificates(nodesTrustedCACertificateDetails.getTrustedCACertificates());
                    trustedCAInformationlist.add(trustedCAInformation);
                }
            }
        } else {
            trustValidator.validateNodesForTrustDistribute(getInputNodes(command, trustCategory), trustCategory, validNodesList, invalidNodesErrorMap,
                    isExternalCa);
            if (!validNodesList.isEmpty()) {
                final TrustedCAInformation trustedCAInformation = new TrustedCAInformation();
                trustedCAInformation.setValidNodes(validNodesList);
                trustedCAInformationlist.add(trustedCAInformation);
            }
        }
    }

    /**
     * This method will be used to read the input xml file data from the given trust distribution command.
     *
     * @param command
     *            : TrustDistributeCommand
     * @param propertyKey
     * @return {@link String}
     */
    private String getInputData(final TrustDistributeCommand command, final String propertyKey) {

        logger.info("command {}, propertyKey {}", command, propertyKey);

        final Map<String, Object> properties = command.getProperties();
        final byte[] fileDataInByte = (byte[]) properties.get(propertyKey);

        String fileData = null;
        if (fileDataInByte != null) {
            try {
                fileData = new String(fileDataInByte, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                logger.error(NscsErrorCodes.INVALID_ENCODING, e);
                throw new InvalidFileContentException(NscsErrorCodes.INVALID_ENCODING);
            }
        } else {
            logger.error("Empty input XML file");
            throw new EmptyFileException("Empty input XML file");
        }
        logger.debug("fileData: {}", fileData);
        return fileData;
    }

    private List<NodeTrustInfo> getInputNodes(TrustDistributeCommand command, final String trustCategory) {
        List<NodeReference> inputNodes = new ArrayList<>();
        if (command.getProperties().containsKey(TrustDistributeCommand.NODE_LIST_FILE_PROPERTY)
                || command.getProperties().containsKey(TrustDistributeCommand.NODE_LIST_PROPERTY)
                || command.getProperties().containsKey(TrustDistributeCommand.SAVED_SERACH_NAME_PROPERTY)
                || command.getProperties().containsKey(TrustDistributeCommand.COLLECTION_NAME_PROPERTY)) {
            inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        } else {
            inputNodes = NodeRef.from(getNodeNames(trustCategory));
        }
        final List<NodeTrustInfo> trustNodes = new ArrayList<>();
        for (final NodeReference nodeRef : inputNodes) {
            final NodeTrustInfo node = new NodeTrustInfo();
            node.setNodeFdn(nodeRef.getFdn());
            trustNodes.add(node);
        }

        logger.debug("Number of input nodes {}", inputNodes.size());
        return trustNodes;
    }

}
