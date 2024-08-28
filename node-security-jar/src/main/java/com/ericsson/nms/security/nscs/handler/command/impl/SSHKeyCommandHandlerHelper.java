/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants.NSCS_EOI_MOM;
import static com.ericsson.nms.security.nscs.data.ModelDefinition.AUTHORIZED_KEY_TYPE;
import static com.ericsson.nms.security.nscs.data.ModelDefinition.CBP_OI_SYSTEM_NS;
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED;
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED;
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand;
import com.ericsson.nms.security.nscs.api.exception.KeyGenerationHandlerException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsMultiInstanceCommandResponseBuilder;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.ssh.SSHKeyNodeValidatorUtility;
import com.ericsson.nms.security.nscs.ssh.SSHKeyRequestDto;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

public class SSHKeyCommandHandlerHelper {
    private static final String NODE_COLUMN = "Node";
    private static final String MODELED_SSH_KEY = "TRUE";
    private static final String NOT_MODELED_SSH_KEY = "FALSE";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;
    
    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @EJB
    private NscsCommandManager nscsCommandManager;

    @Inject
    private SSHKeyNodeValidatorUtility sshkeyNodeValidatorUtility;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsContextService nscsContextService;

    @Inject
    NscsCapabilityModelService capabilityModelService;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    private ItemsValiditySshKeyType itemsValiditySshKeyType;

    public NscsCommandResponse processSshKey(final KeyGeneratorCommand command,
                                             final CommandContext context,
                                             final SSHKeyConfigurationMode commandType) throws NscsServiceException {
        nscsLogger.commandHandlerStarted(command);

        NscsCommandResponse response;
        try {
            response = buildSshKeyResponse(command, commandType);
            updateCommandHandlerLogger(command);
        } catch (final NscsServiceException e) {
            final String errorMsg = String.format("%s Command failed due to %s.", "Failed of SSH Key Command.",
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw e;
        } catch (final Exception e) {
            final String errorMsg = String.format("%s Command failed due to unexpected %s.", "Failed of SSH Key Command.",
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw new KeyGenerationHandlerException(errorMsg, e);
        }
        return response;
    }

    private NscsCommandResponse buildSshKeyResponse(final KeyGeneratorCommand command,
                                                    final SSHKeyConfigurationMode commandType) throws NscsServiceException {

        // retrieve the list of nodes contained in the command
        final List<NodeReference> inputNodesList = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);

        // map of algorithm and key size for each valid node
        final Map<NodeReference, String> validNodesAlgorithmMap = new HashMap<>();

        // map of error for each invalid node
        final Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        // set a flag as a String to distinguish SSH key command
        final String sshkeyOperation = commandType.getSshKeyOperation();

        // get algorithm type size from command
        final String algorithm = command.getAlgorithmTypeSize();
        nscsLogger.info("From command: algorithm type size is [{}]", algorithm);

        // perform validation on node base
        final boolean areAllInputNodesValid = sshkeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodesList, validNodesAlgorithmMap, invalidNodesErrorMap,
                sshkeyOperation, algorithm);

        nscsLogger.info("algorithm and key size map for valid nodes is {}", validNodesAlgorithmMap);

        // start workflow
        return checkAndExecuteSshKeyWorkflows(areAllInputNodesValid, validNodesAlgorithmMap, inputNodesList, invalidNodesErrorMap, commandType);
    }

    private NscsCommandResponse checkAndExecuteSshKeyWorkflows(final boolean areAllInputNodesValid, final Map<NodeReference, String> validNodesAlgorithmMap,
            final List<NodeReference> uniqueNodes, final Map<String, NscsServiceException> invalidNodesErrorMap,
            final SSHKeyConfigurationMode commandType) {

        String responseMessageWithJobId;
        JobStatusRecord jobStatusRecord;

        // retrieve command type
        final String sshkeyOperation = commandType.getSshKeyOperation();

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodesAlgorithmMap.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        if (areAllInputNodesValid) {
            nscsLogger.info("All of the given input nodes [{}] are valid. An SSH KEY job needs to be added.", uniqueNodes.size());
            jobStatusRecord = nscsJobCacheHandler.insertJob(commandType.getNscsCommandType());

            final List<SSHKeyRequestDto> requests = configureSSHKeyForValidNodes(validNodesAlgorithmMap, sshkeyOperation);
            nscsCommandManager.executeSshKeyWorkflows(requests, jobStatusRecord);

            responseMessageWithJobId = String.format(commandType.getExecutedMessage(), jobStatusRecord.getJobId().toString());
            itemsValiditySshKeyType = ItemsValiditySshKeyType.ALL_ITEMS_ARE_VALID;
            return new NscsMultiInstanceCommandResponseBuilder().buildSuccessResponse(responseMessageWithJobId);
        } else {
            if (!validNodesAlgorithmMap.isEmpty()) {
                nscsLogger.info("Only [{}] of the given input nodes [{}] are valid. An SSH KEY job needs to be added.", validNodesAlgorithmMap.size(),
                        uniqueNodes.size());
                jobStatusRecord = nscsJobCacheHandler.insertJob(commandType.getNscsCommandType());

                List<SSHKeyRequestDto> requests = configureSSHKeyForValidNodes(validNodesAlgorithmMap, sshkeyOperation);
                nscsCommandManager.executeSshKeyWorkflows(requests, jobStatusRecord);

                responseMessageWithJobId = String.format(commandType.getPartiallyExecutedMessage(), jobStatusRecord.getJobId().toString());
                itemsValiditySshKeyType = ItemsValiditySshKeyType.ITEMS_PARTIALLY_VALID;
                return new NscsMultiInstanceCommandResponseBuilder().buildErrorResponse(responseMessageWithJobId, NODE_COLUMN, invalidNodesErrorMap);
            } else {
                nscsLogger.error("All of the given input nodes [{}] are invalid. No SSH KEY job needs to be added.", uniqueNodes.size());
                itemsValiditySshKeyType = ItemsValiditySshKeyType.ALL_ITEMS_ARE_INVALID;
                return new NscsMultiInstanceCommandResponseBuilder().buildErrorResponse(commandType.getAllFailedMessage(), NODE_COLUMN,
                        invalidNodesErrorMap);
            }
        }
    }

    private List<SSHKeyRequestDto> configureSSHKeyForValidNodes(final Map<NodeReference, String> validNodesAlgorithmMap,
                                                                final String sshkeyOperation) {
        final List<SSHKeyRequestDto> requests = new ArrayList<>();
        
        for (final Map.Entry<NodeReference, String> entry : validNodesAlgorithmMap.entrySet()) {
            final NodeReference validNode = entry.getKey();
            final String algorithm = entry.getValue();

            SSHKeyRequestDto request = new SSHKeyRequestDto();
            final String fdn = validNode.getFdn();
            request.setFdn(fdn);
            request.setSshkeyOperation(sshkeyOperation);
            request.setAlgorithm(algorithm);

            // added to manage also the IPOS-OI nodes
            String momType = capabilityModelService.getMomType(validNode);
            request.setMomType(momType);
            request.setModeledSshKey(NOT_MODELED_SSH_KEY);
            if (NSCS_EOI_MOM.equals(momType)) {
                String isModeledSshKey = isAuthorizedKeyExists(validNode) ? MODELED_SSH_KEY : NOT_MODELED_SSH_KEY;
                request.setModeledSshKey(isModeledSshKey);
            }

            requests.add(request);
        }
        nscsLogger.info("Parameters for ssh key workflow are :{}", requests);
        return requests;
    }

    private boolean isAuthorizedKeyExists(final NodeReference nodeRef) {
        boolean ret = true;

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        final String targetCategory = normNode.getTargetCategory();
        final String targetType = normNode.getNeType();
        final String targetModelIdentity = normNode.getOssModelIdentity();
        nscsLogger.info(" Name space for the MO : {} and  Type for Mo  is {}", CBP_OI_SYSTEM_NS, AUTHORIZED_KEY_TYPE);
        try {
            nscsModelServiceImpl.getModelInfoWithRefMimNs(targetCategory, targetType, targetModelIdentity, CBP_OI_SYSTEM_NS, AUTHORIZED_KEY_TYPE);
        } catch (final NscsModelServiceException e) {
            nscsLogger.error(e, e.getMessage());
            ret = false;
        }
        return ret;
    }

    private void updateCommandHandlerLogger(KeyGeneratorCommand command) {
        switch (itemsValiditySshKeyType) {
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

    private enum ItemsValiditySshKeyType {
        ALL_ITEMS_ARE_VALID(),
        ITEMS_PARTIALLY_VALID(),
        ALL_ITEMS_ARE_INVALID();
        ItemsValiditySshKeyType() {
        }
    }

    public enum SSHKeyConfigurationMode {
        CREATE_SSH_KEY ("create ssh key", SSH_KEY_TO_BE_CREATED, "Successfully started a job for creating SSH key. Perform 'secadm job get -j %s' to get progress info.", "Error during creating ssh key.", NscsCommandType.CREATE_SSH_KEY),
        UPDATE_SSH_KEY ("update ssh key", SSH_KEY_TO_BE_UPDATED,"Successfully started a job for updating SSH key. Perform 'secadm job get -j %s' to get progress info.", "Error during updating ssh key.", NscsCommandType.UPDATE_SSH_KEY),
        DELETE_SSH_KEY ("delete ssh key", SSH_KEY_TO_BE_DELETED,"Successfully started a job to delete SSH key. Perform 'secadm job get -j %s' to get progress info.", "Error during deleting ssh key.", NscsCommandType.DELETE_SSH_KEY);

        private final String sshkeyName;
        private final String sshkeyOperation;
        private final String sshkeyExecutedMessage;
        private final String sshkeyFailedMessage;
        private final NscsCommandType sshkeyNscsCommandType;

        SSHKeyConfigurationMode(String name, String sshkeyOperation, String executedMessage, String failedMessage, NscsCommandType nscsCommandType) {
            this.sshkeyName = name;
            this.sshkeyOperation = sshkeyOperation;
            this.sshkeyExecutedMessage = executedMessage;
            this.sshkeyFailedMessage = failedMessage;
            this.sshkeyNscsCommandType = nscsCommandType;
        }

        public String getName() {
            return sshkeyName;
        }

        public String getExecutedMessage() {
            return sshkeyExecutedMessage;
        }


        public String getSshKeyOperation() {
            return sshkeyOperation;
        }

        public String getPartiallyExecutedMessage() {
            return String.format(NscsErrorCodes.OPERATION_WITH_SOME_INVALID_NODES_FORMAT, sshkeyExecutedMessage);
        }

        public String getFailedMessage() {
            return sshkeyFailedMessage;
        }

        public String getAllFailedMessage() {
            return String.format(NscsErrorCodes.OPERATION_WITH_ALL_INVALID_NODES_FORMAT, sshkeyFailedMessage);
        }

        public NscsCommandType getNscsCommandType() {
            return sshkeyNscsCommandType;
        }
    }

}
