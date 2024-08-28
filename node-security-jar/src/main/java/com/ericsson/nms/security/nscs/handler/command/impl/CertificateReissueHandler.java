package com.ericsson.nms.security.nscs.handler.command.impl;

import java.math.BigInteger;
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

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CertificateReissueCommand;
import com.ericsson.nms.security.nscs.api.enums.RevocationReason;
import com.ericsson.nms.security.nscs.api.exception.CertificateReissueWfException;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Reissues certificate for a list of nodes obtained by input parameters. Possible use cases for input properties are: CERT_TYPE [NODELIST | NODEFILE]
 * CA_NAME CA_NAME [NODELIST | NODEFILE] CA_NAME SERIAL_NUMBER
 * </p>
 *
 * Created by enmadmin
 */
@CommandType(NscsCommandType.CERTIFICATE_REISSUE)
@Local(CommandHandlerInterface.class)
public class CertificateReissueHandler implements CommandHandler<CertificateReissueCommand>, CommandHandlerInterface {

    public static final String CERTIFICATE_REISSUE_EXECUTED = "Successfully started a job to reissue certificates for nodes";
    public static final String CERTIFICATE_REISSUE_EXECUTED_NON_BLOCKING_ERR = "Successfully started a job to reissue certificates for valid nodes only. Invalid nodes are listed below :";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_BLOCKING_ERR = "Certificate reissue command not executed as some provided nodes are invalid. Details are given below :";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_CA_NOT_FOUND = "Certificate reissue command not executed. Input CA not found.";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_ENTITIES_NOT_FOUND = "Certificate reissue command not executed. Entities signed by input CA not found.";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_ENTITIES_WITH_CERT_TYPE_NOT_FOUND = "Certificate reissue command not executed. Entities signed by input CA with requested certificate type not found.";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_CA_OR_ENTITIES_NOT_FOUND = "Certificate reissue command not executed. Input CA or entities not found.";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_INVALID_SERIAL_NUMBER = "Following input serial numbers are invalid: ";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_INVALID_ENTITY_CATEGORY = "Certificate reissue command not executed. Following retrieved entities have wrong category: ";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_DUPLICATED_INPUT_NODES = "Certificate reissue command not executed. The list of specified nodes contains duplicates: ";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_NO_ASSOCIATED_NODES = "Certificate reissue command not executed. Can't find any node associated to valid entities.";
    public static final String CERTIFICATE_REISSUE_NOT_EXECUTED_NO_ASSOCIATED_ENTITIES = "Certificate reissue command not executed. Can't find any entity associated to valid nodes.";

    public static final String xsdValidatorFileName = "ValidatorInputForCertIssue.xsd";

    public static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail" };
    public static final String[] ERROR_ENTITY_HEADER = new String[] { "Entity Name", "Error Code", "Error Detail" };
    public static final String[] ERROR_ENTITY_CATEGORY_HEADER = new String[] { "Entity Name", "Category" };

    private static final int NO_OF_COLUMNS = 2;

    private static final String COMMAND_SYNTAX_EXCEPTION = "CommandSyntaxException: input property combination not supported. ";

    @Inject
    private Logger logger;

    @EJB
    private NscsCommandManager commandManager;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsContextService nscsContextService;

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.handler.command.CommandHandler#process (com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand,
     * com.ericsson.nms.security.nscs.handler.CommandContext)
     */
    @Override
    public NscsCommandResponse process(final CertificateReissueCommand command, final CommandContext context)
            throws NscsServiceException, CommandSyntaxException {

        logger.info("Reissue certificate command [{}]", command);
        String inputReason = RevocationReason.UNSPECIFIED.toString();
        String jobIdMessage = "";

        final String inputCertType = command.getCertType();
        validateCertType(inputCertType);

        if (command.getProperties().containsKey(CertificateReissueCommand.REASON_PROPERTY)) {
            inputReason = command.getReason();
            logger.info("Input reason is: [{}]", inputReason);
            validateRevocationReason(inputReason);
        }

        if (command.getProperties().containsKey(CertificateReissueCommand.CA_PROPERTY)) {

            //GET ALL ENTITIES SIGNED BY INPUT CA FROM PKI
            final String inputCA = command.getCA();
            List<Entity> entityList = new ArrayList<>();

            try {
                if (!nscsPkiManager.isEntityNameAvailable(inputCA, EntityType.CA_ENTITY)) {
                    entityList = nscsPkiManager.getEntityListByIssuerName(inputCA);
                } else {
                    logger.info("Input CA {} not found.", inputCA);
                    throw new InvalidArgumentValueException(CERTIFICATE_REISSUE_NOT_EXECUTED_CA_NOT_FOUND);
                }
            } catch (final NscsPkiEntitiesManagerException ex) {
                logger.warn("Input CA {} not found or entities not retrieved. message: {}", inputCA, ex.getMessage());
                throw new InvalidArgumentValueException(CERTIFICATE_REISSUE_NOT_EXECUTED_CA_OR_ENTITIES_NOT_FOUND);
            } catch (final Exception ex) {
                logger.warn("Generic error retrieving entities from CA {}", inputCA, ex);
                throw new InvalidArgumentValueException(CERTIFICATE_REISSUE_NOT_EXECUTED_CA_OR_ENTITIES_NOT_FOUND);
            }

            //RETRIEVE VALID ENTITIES
            if (entityList == null || entityList.isEmpty()) {
                logger.info("No entities retrieved signed by CA: {}", inputCA);
                return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_ENTITIES_NOT_FOUND);
            } else {
                logger.info("entityList size: {}, entity: {}", entityList.size(), printEntityNames(entityList));

                //Filter by input certType
                final List<Entity> validCategoryEntities = getEntitiesByCategory(entityList, inputCertType);
                if (validCategoryEntities == null || validCategoryEntities.isEmpty()) {
                    logger.info("No entities retrieved signed by CA {} with category {}", inputCA, inputCertType);
                    return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_ENTITIES_WITH_CERT_TYPE_NOT_FOUND);
                }
                logger.info("validCategoryEntities size: {}, entity: {}", validCategoryEntities.size(), printEntityNames(validCategoryEntities));

                // SERIAL NUMBER INPUT OPTION
                if (command.getProperties().containsKey(CertificateReissueCommand.SERIAL_NUMBER_PROPERTY)) {

                    //Verify all input Serial Numbers are valid
                    final Set<String> invalidSerialNumbers = new HashSet<>();
                    final Set<Entity> validSerialNumberEntities = new HashSet<>();
                    final List<String> inputSerialNumbers = fetchSingleValues(command.getSerialNumber());

                    for (final String serialNumber : inputSerialNumbers) {
                        boolean isSnFound = false;
                        for (final Entity entity : validCategoryEntities) {
                            if (entity.getEntityInfo().getActiveCertificate() != null) {
                                logger.info("inputSerialNumber: {}, entitySerialNumber: {}", serialNumber,
                                        entity.getEntityInfo().getActiveCertificate().getSerialNumber());
                                final BigInteger inputSN = CertDetails.convertSerialNumberToDecimalFormat(serialNumber);
                                // Entity serial number is always in hexadecimal format!
                                final String entitySerialNumber = entity.getEntityInfo().getActiveCertificate().getSerialNumber();
                                final BigInteger entitySN = CertDetails.convertHexadecimalSerialNumberToDecimalFormat(entitySerialNumber);
                                logger.info("After normalization: inputSN[{}], entitySerialNumber[{}], entitySN[{}]", serialNumber,
                                        entitySerialNumber, entitySN);
                                if (inputSN.equals(entitySN)) {
                                    validSerialNumberEntities.add(entity);
                                    isSnFound = true;
                                    break;
                                }
                            }
                        }
                        if (!isSnFound) {
                            invalidSerialNumbers.add(serialNumber);
                        }
                    }

                    if (!invalidSerialNumbers.isEmpty()) {
                        logger.info("Following Input Serial Numbers are Invalid: {}", invalidSerialNumbers);
                        throw new InvalidArgumentValueException(
                                CERTIFICATE_REISSUE_NOT_EXECUTED_INVALID_SERIAL_NUMBER + invalidSerialNumbers.toString());
                    }

                    //Verify all entities have an associated node
                    final List<Entity> entitiesWithNode = new ArrayList<>();
                    final Map<Entity, NodeReference> associatedNodesEntity = new HashMap<>();
                    final List<Entity> entitiesWithoutNode = new ArrayList<>();
                    for (final Entity entity : validSerialNumberEntities) {
                        final String entityNodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);

                        logger.debug("nodeName {}", entityNodeName);
                        if (!entityNodeName.isEmpty() && commandManager.isNodePresent(entityNodeName)) {
                            entitiesWithNode.add(entity);
                            final NodeReference associatedNode = new NodeRef(entityNodeName);
                            associatedNodesEntity.put(entity, associatedNode);
                        } else {
                            entitiesWithoutNode.add(entity);
                        }
                    }
                    if (!entitiesWithoutNode.isEmpty()) {
                        logger.warn("Following valid entities have no associated node: [{}]", printEntityNames(entitiesWithoutNode));
                    }

                    if (entitiesWithNode.isEmpty()) {
                        return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_NO_ASSOCIATED_NODES);
                    }

                    logger.info("Following valid entities have an associated node: [{}]. " + "Validating nodes...",
                            printEntityNames(entitiesWithNode));

                    // verify associated nodes are valid
                    final Map<Entity, NodeReference> validNodes = new HashMap<>();
                    final Map<Entity, NscsServiceException> blockingErrors = new HashMap<>();
                    final Map<String, String[]> nonBlockingErrors = new HashMap<>();
                    final boolean areInputNodesValid = commandManager.validateNodesForCertificateReissue(associatedNodesEntity, entitiesWithoutNode,
                            validNodes, blockingErrors, nonBlockingErrors);

                    nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(blockingErrors.size()));

                    JobStatusRecord jobStatusRecord;
                    if (areInputNodesValid) {
                        //Starting workflow for entities with:
                        //- valid Category [OAM | IPSEC] corresponding to input certType
                        //- an activeCertificate, with serialNumber among input Serial Numbers
                        //- association with an existing node
                        //- associated node is valid

                        try {
                            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                            commandManager.executeCertificateReissueWfs(validNodes, inputReason, inputCertType, jobStatusRecord);
                        } catch (final Exception ex) {
                            logger.error(ex.getMessage(), ex);
                            throw new CertificateReissueWfException();
                        }
                        jobIdMessage = CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString()
                                + "' to get progress info.";
                        return NscsCommandResponse.message(jobIdMessage);
                    } else {
                        //only NON BLOCKING ERRORS are found
                        if (!nonBlockingErrors.isEmpty() && nonBlockingErrors.size() == blockingErrors.size()) {

                            if (validNodes.entrySet().size() != 0) {
                                try {
                                    jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                                    commandManager.executeCertificateReissueWfs(validNodes, inputReason, inputCertType, jobStatusRecord);
                                    jobIdMessage = CERTIFICATE_REISSUE_EXECUTED_NON_BLOCKING_ERR + ". Perform 'secadm job get -j "
                                            + jobStatusRecord.getJobId().toString() + "' to get progress info.";
                                } catch (final Exception ex) {
                                    logger.error(ex.getMessage(), ex);
                                    throw new CertificateReissueWfException();
                                }
                            }
                            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                            final Set<java.util.Map.Entry<String, String[]>> entrySet = nonBlockingErrors.entrySet();
                            for (final java.util.Map.Entry<String, String[]> entry : entrySet) {
                                response.add(entry.getKey(), entry.getValue());
                            }

                            response.setAdditionalInformation(jobIdMessage);
                            return response;
                            //there are also BLOCKING ERRORS
                        } else {
                            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                            final Set<java.util.Map.Entry<Entity, NscsServiceException>> entrySet = blockingErrors.entrySet();
                            for (final java.util.Map.Entry<Entity, NscsServiceException> entry : entrySet) {
                                response.add(entry.getKey().getEntityInfo().getName(), new String[] { entry.getValue().getMessage() });
                            }
                            response.setAdditionalInformation(CERTIFICATE_REISSUE_NOT_EXECUTED_BLOCKING_ERR);
                            return response;
                        }
                    }

                }

                // One of the input option from NODE_LIST, NODE_LIST_FILE, SAVED_SEARCH_NAME, COLLECTION_NAME
                else if (command.getProperties().containsKey(CertificateReissueCommand.NODE_LIST_PROPERTY)
                        || command.getProperties().containsKey(CertificateReissueCommand.NODE_LIST_FILE_PROPERTY)
                        || command.getProperties().containsKey(CertificateReissueCommand.SAVED_SERACH_NAME_PROPERTY)
                        || command.getProperties().containsKey(CertificateReissueCommand.COLLECTION_NAME_PROPERTY)) {
                    final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);

                    final Set<String> duplicatedNodes = commandManager.validateDuplicatedNodes(inputNodes);
                    if (!duplicatedNodes.isEmpty()) {
                        logger.info("Following Input Nodes are duplicated: {}", duplicatedNodes);
                        return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_DUPLICATED_INPUT_NODES + duplicatedNodes);
                    }

                    final Map<Entity, NodeReference> validEntityNodesMap = new HashMap<>();
                    final Map<NodeReference, NscsServiceException> blockingErrors = new HashMap<>();
                    final Map<String, String[]> nonBlockingErrors = new HashMap<>();
                    final boolean areInputNodesValid = commandManager.validateNodesWithEntitiesForCertificateReissue(validCategoryEntities,
                            inputCertType, inputNodes, validEntityNodesMap, blockingErrors, nonBlockingErrors);

                    nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validEntityNodesMap.size()),
                            Integer.valueOf(blockingErrors.size()));

                    JobStatusRecord jobStatusRecord;
                    if (areInputNodesValid) {
                        try {
                            if (validEntityNodesMap.isEmpty()) {
                                return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_NO_ASSOCIATED_ENTITIES);
                            }
                            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                            commandManager.executeCertificateReissueWfs(validEntityNodesMap, inputReason, inputCertType, jobStatusRecord);
                        } catch (final Exception ex) {
                            logger.error(ex.getMessage(), ex);
                            throw new CertificateReissueWfException();
                        }
                        jobIdMessage = CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString()
                                + "' to get progress info.";
                        return NscsCommandResponse.message(jobIdMessage);
                    } else {
                        //only NON BLOCKING ERRORS are found
                        if (!nonBlockingErrors.isEmpty() && nonBlockingErrors.size() == blockingErrors.size()) {
                            logger.info("only non-blocking errors occur [{}]", nonBlockingErrors);
                            if (validEntityNodesMap.entrySet().size() != 0) {
                                try {
                                    jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                                    commandManager.executeCertificateReissueWfs(validEntityNodesMap, inputReason, inputCertType, jobStatusRecord);
                                    jobIdMessage = CERTIFICATE_REISSUE_EXECUTED_NON_BLOCKING_ERR + ". Perform 'secadm job get -j "
                                            + jobStatusRecord.getJobId().toString() + "' to get progress info.";
                                } catch (final Exception ex) {
                                    logger.error(ex.getMessage(), ex);
                                    //								throw new CertificateReissueWfException();
                                }
                            }
                            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                            final Set<java.util.Map.Entry<String, String[]>> entrySet = nonBlockingErrors.entrySet();
                            for (final java.util.Map.Entry<String, String[]> entry : entrySet) {
                                response.add(entry.getKey(), entry.getValue());
                            }

                            response.setAdditionalInformation(jobIdMessage);
                            return response;
                            //there are also BLOCKING ERRORS
                        } else {
                            logger.info("blocking errors occur [{}]", blockingErrors);
                            for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : blockingErrors.entrySet()) {
                                context.setAsInvalidOrFailed(entry.getKey(), entry.getValue());
                            }
                            return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_BLOCKING_ERR);
                        }
                    }
                }

                //NO FILTERING INPUT OPTION
                else {

                    final Map<Entity, NodeReference> associatedNodesEntity = new HashMap<>();
                    final List<Entity> entitiesWithoutNode = new ArrayList<>();
                    for (final Entity entity : validCategoryEntities) {
                        final String entityNodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);
                        logger.debug("nodeName {}", entityNodeName);
                        if (!entityNodeName.isEmpty() && commandManager.isNodePresent(entityNodeName)) {
                            final NodeReference associatedNode = new NodeRef(entityNodeName);
                            associatedNodesEntity.put(entity, associatedNode);
                        } else {
                            entitiesWithoutNode.add(entity);
                        }
                    }
                    if (!entitiesWithoutNode.isEmpty()) {
                        logger.warn("Following valid entities have no nodes associated: {}", printEntityNames(entitiesWithoutNode));
                    }

                    //                    List<Entity> validEntities = new ArrayList<Entity>();
                    //                    Map<Entity, NscsServiceException> invalidEntityBlockingErrors = new HashMap<Entity, NscsServiceException>();
                    //                    boolean areEntitiesValid = commandManager.validateEntitiesForCertificateReissue(inputCertType, entitiesWithNode,
                    //                            validEntities, invalidEntityBlockingErrors);

                    //                    if (areEntitiesValid) {

                    //                        Map<Entity, NodeReference> associatedNodesEntity = new HashMap<>();
                    //                        List<Entity> entitiesWithNode = new ArrayList<>();
                    //                        List<Entity> entitiesWithoutNode = new ArrayList<>();
                    //                        for (Entity entity : validEntities) {
                    //                            String entityName = entity.getEntityInfo().getName();
                    //                            String entityNodeName = entityName.split("-")[0];
                    //                            logger.debug("nodeName {}", entityNodeName);
                    //                            if (commandManager.isNodePresent(entityNodeName)) {
                    //                                entitiesWithNode.add(entity);
                    //                                NodeReference associatedNode = new NodeRef(entityNodeName);
                    //                                associatedNodesEntity.put(entity, associatedNode);
                    //                            } else {
                    //                                entitiesWithoutNode.add(entity);
                    //                            }
                    //                        }
                    //
                    //                        if (!entitiesWithoutNode.isEmpty()) {
                    //                            logger.warn("Following valid entities have no nodes associated: {}", printEntityNames(entitiesWithoutNode));
                    //                        }

                    // verify associated nodes are valid
                    final Map<Entity, NodeReference> validNodes = new HashMap<>();
                    final Map<Entity, NscsServiceException> blockingErrors = new HashMap<>();
                    final Map<String, String[]> nonBlockingErrors = new HashMap<>();
                    final boolean areInputNodesValid = commandManager.validateNodesForCertificateReissue(associatedNodesEntity, entitiesWithoutNode,
                            validNodes, blockingErrors, nonBlockingErrors);

                    nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(blockingErrors.size()));

                    JobStatusRecord jobStatusRecord;
                    if (areInputNodesValid) {
                        //Starting workflow for:
                        // -valid entities
                        //- association with an existing node
                        //- associated node is valid

                        try {
                            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                            commandManager.executeCertificateReissueWfs(validNodes, inputReason, inputCertType, jobStatusRecord);
                        } catch (final Exception ex) {
                            logger.error(ex.getMessage(), ex);
                            throw new CertificateReissueWfException();
                        }
                        jobIdMessage = CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString()
                                + "' to get progress info.";
                        return NscsCommandResponse.message(jobIdMessage);
                    } else {
                        //only NON BLOCKING ERRORS are found
                        if (!nonBlockingErrors.isEmpty() && nonBlockingErrors.size() == blockingErrors.size()) {
                            if (validNodes.entrySet().size() != 0) {

                                try {
                                    jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                                    commandManager.executeCertificateReissueWfs(validNodes, inputReason, inputCertType, jobStatusRecord);
                                    jobIdMessage = CERTIFICATE_REISSUE_EXECUTED_NON_BLOCKING_ERR + ". Perform 'secadm job get -j "
                                            + jobStatusRecord.getJobId().toString() + "' to get progress info.";
                                } catch (final Exception ex) {
                                    logger.error(ex.getMessage(), ex);
                                    throw new CertificateReissueWfException();
                                }

                            }
                            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                            final Set<java.util.Map.Entry<String, String[]>> entrySet = nonBlockingErrors.entrySet();
                            for (final java.util.Map.Entry<String, String[]> entry : entrySet) {
                                response.add(entry.getKey(), entry.getValue());
                            }

                            response.setAdditionalInformation(jobIdMessage);
                            return response;
                            //there are also BLOCKING ERRORS
                        } else {
                            //								for (java.util.Map.Entry<Entity, NscsServiceException> entry : blockingErrors.entrySet()) {
                            //									context.setAsInvalidOrFailed(entry.getKey(), entry.getValue());
                            //								}
                            //								return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_BLOCKING_ERR);
                            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                            final Set<java.util.Map.Entry<Entity, NscsServiceException>> entrySet = blockingErrors.entrySet();
                            for (final java.util.Map.Entry<Entity, NscsServiceException> entry : entrySet) {
                                response.add(entry.getKey().getEntityInfo().getName(), new String[] { entry.getValue().getMessage() });
                            }
                            response.setAdditionalInformation(CERTIFICATE_REISSUE_NOT_EXECUTED_BLOCKING_ERR);
                            return response;
                        }
                    }

                    //                    } else {
                    //                        //Blocking errors for entities are found
                    //                        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                    //                        response.add(ERROR_ENTITY_HEADER[0], Arrays.copyOfRange(ERROR_ENTITY_HEADER, 1, ERROR_ENTITY_HEADER.length));
                    //                        for (java.util.Map.Entry<Entity, NscsServiceException> entry : invalidEntityBlockingErrors.entrySet()) {
                    //                            String[] errValue = { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage() };
                    //                            response.add(entry.getKey().getEntityInfo().getName(), errValue);
                    //                        }
                    //                        response.setAdditionalInformation(CERTIFICATE_REISSUE_NOT_EXECUTED_BLOCKING_ERR);
                    //                        return response;
                    //                    }
                }
            }
        }

        else {

            if ((command.getProperties().containsKey(CertificateReissueCommand.NODE_LIST_PROPERTY)
                    || command.getProperties().containsKey(CertificateReissueCommand.NODE_LIST_FILE_PROPERTY)
                    || command.getProperties().containsKey(CertificateReissueCommand.SAVED_SERACH_NAME_PROPERTY)
                    || command.getProperties().containsKey(CertificateReissueCommand.COLLECTION_NAME_PROPERTY))
                    && !command.getProperties().containsKey(CertificateReissueCommand.SERIAL_NUMBER_PROPERTY)) {

                final List<NodeReference> validNodes = new ArrayList<>();
                final Map<NodeReference, NscsServiceException> blockingErrors = new HashMap<>();
                final Map<String, String[]> nonBlockingErrors = new HashMap<>();

                final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
                logger.info("Number of input nodes {}", inputNodes.size());

                final Set<String> duplicatedNodes = commandManager.validateDuplicatedNodes(inputNodes);
                if (!duplicatedNodes.isEmpty()) {
                    logger.info("Following Input Nodes are duplicated: {}", duplicatedNodes);
                    return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_DUPLICATED_INPUT_NODES + duplicatedNodes);
                }

                //validate input nodes
                final boolean areInputNodesValid = commandManager.validateNodesWithEntitiesForCertificateReissue(inputCertType,
                        inputNodes, validNodes, blockingErrors, nonBlockingErrors);

                nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(blockingErrors.size()));

                JobStatusRecord jobStatusRecord;
                if (areInputNodesValid) {

                    try {
                        jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                        commandManager.executeCertificateReissueWfs(validNodes, inputReason, inputCertType, jobStatusRecord);
                    } catch (final Exception ex) {
                        logger.error(ex.getMessage(), ex);
                        throw new CertificateReissueWfException();
                    }
                    jobIdMessage = CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString()
                            + "' to get progress info.";
                    return NscsCommandResponse.message(jobIdMessage);

                } else {
                    //only DYNAMIC PARAMS ERRORS are found
                    if (!nonBlockingErrors.isEmpty() && nonBlockingErrors.size() == blockingErrors.size()) {
                        if (validNodes.size() != 0) {
                            try {
                                jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                                commandManager.executeCertificateReissueWfs(validNodes, inputReason, inputCertType, jobStatusRecord);
                                jobIdMessage = CERTIFICATE_REISSUE_EXECUTED_NON_BLOCKING_ERR + ". Perform 'secadm job get -j "
                                        + jobStatusRecord.getJobId().toString() + "' to get progress info.";
                            } catch (final Exception ex) {
                                logger.error(ex.getMessage(), ex);
                                throw new CertificateReissueWfException();
                            }

                        }
                        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
                        response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
                        final Set<java.util.Map.Entry<String, String[]>> entrySet = nonBlockingErrors.entrySet();
                        for (final java.util.Map.Entry<String, String[]> entry : entrySet) {
                            response.add(entry.getKey(), entry.getValue());
                        }

                        response.setAdditionalInformation(jobIdMessage);
                        return response;
                        //there are also BLOCKING ERRORS
                    } else {
                        for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : blockingErrors.entrySet()) {
                            context.setAsInvalidOrFailed(entry.getKey(), entry.getValue());
                        }
                        return NscsCommandResponse.message(CERTIFICATE_REISSUE_NOT_EXECUTED_BLOCKING_ERR);
                    }
                }

            } else {
                logger.error(COMMAND_SYNTAX_EXCEPTION);
                throw new CommandSyntaxException();
            }
        }

    }

    //	private Set<String> validateDuplicatedNodes(List<NodeReference> inputNodes) {
    //		final Map<NodeReference, Boolean> nonUniqueNodes = new HashMap<>(inputNodes.size());
    //
    //		Set <String> duplicatedNodes = new HashSet<>();
    //        for(NodeReference node : inputNodes) {
    //            if(nonUniqueNodes.containsKey(node)){
    //                nonUniqueNodes.put(node, Boolean.TRUE);
    //            } else {
    //                nonUniqueNodes.put(node, Boolean.FALSE);
    //            }
    //        }
    //
    //        for (Map.Entry<NodeReference, Boolean> entry : nonUniqueNodes.entrySet()) {
    //            if ( entry.getValue() ) {
    //                duplicatedNodes.add(entry.getKey().getFdn());
    //            }
    //        }
    //		return duplicatedNodes;
    //	}

    //	private boolean validateEntityCategory(final Entity entity) {
    //
    //			String category = entity.getCategory().getName();
    //			logger.debug("category {}", category);
    //			NodeEntityCategory nodeEntityCategory = nscsPkiManager.findNodeEntityCategory(entity.getCategory());
    //			if (nodeEntityCategory != null) {
    //				if (NodeEntityCategory.OAM.equals(nodeEntityCategory) ||
    //					NodeEntityCategory.IPSEC.equals(nodeEntityCategory)) {
    //					return true;
    //				}
    //			}
    //			return false;
    //	}

    private List<Entity> getEntitiesByCategory(final List<Entity> entities, final String certType) {

        final List<Entity> filteredEntities = new ArrayList<>();
        for (final Entity ee : entities) {
            logger.debug("category {}", ee.getCategory());
            final NodeEntityCategory nodeEntityCategory = NscsPkiEntitiesManagerJar.findNodeEntityCategory(ee.getCategory());
            if (nodeEntityCategory != null) {
                if (certType.equals(nodeEntityCategory.name())) {
                    filteredEntities.add(ee);
                }
            }
        }
        return filteredEntities;
    }

    private void validateCertType(final String certType) {
        if (!commandManager.validateCertTypeValue(certType)) {
            logger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
            final String errmsg = String.format(" Invalid argument for parameter %s",
                    CertificateReissueCommand.CERT_TYPE_PROPERTY + ". Accepted arguments are " + commandManager.getValidCertificateTypes());
            logger.error(errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }
    }

    private boolean validateRevocationReason(final String reason) {
        if (!commandManager.validateReasonValue(reason)) {
            logger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
            final String errmsg = String.format(" Invalid argument for parameter %s",
                    CertificateReissueCommand.REASON_PROPERTY + ". Accepted arguments are " + CertificateReissueCommand.getRevocationReasonlist());
            logger.error(errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }
        return true;
    }

    private List<String> fetchSingleValues(String values) {
        final List<String> finalValues = new ArrayList<>();
        if (values.startsWith("[") && values.endsWith("]")) {
            values = values.substring(1, values.length() - 1);
        }
        for (final String s : values.split(",")) {
            finalValues.add(s.trim());
        }
        return finalValues;
    }

    private String printEntityNames(final List<Entity> entityList) {
        String entityNames = "";
        for (final Entity e : entityList) {
            entityNames += e.getEntityInfo().getName() + " ";
        }
        return entityNames;
    }

}
