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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.utility.NtpUtility;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.util.NtpKeyIdData;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppCheckAndRemoveNTPServerTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CHECK_AND_REMOVE_NTP_SERVER
 * </p>
 * <p>
 * Check provided to verify key IDs are mapped to NTP Server MO on target nodes. If any provided key Id is mapped to NTP server MO then that NTP
 * server will be removed. Also If the key ids belong to Ntp Server in the same environment, then list of key ids is prepared to remove the mapping of
 * Key ids and nodes from Ntp Service database .
 * </p>
 *
 * @author tcsviku
 *
 */
@WFTaskType(WorkflowTaskType.CPP_CHECK_AND_REMOVE_NTP_SERVER)
@Local(WFTaskHandlerInterface.class)
public class CppCheckAndRemoveNTPServerTaskHandler implements WFQueryTaskHandler<CppCheckAndRemoveNTPServerTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCMWriterService nscsCMWriterService;

    @Inject
    NtpUtility ntpUtility;

    private static final String ALL = "all";

    @Override
    public String processTask(final CppCheckAndRemoveNTPServerTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(task.getNode());
        final String mirrorRootFdn = normNode.getFdn();
        final Map<String, Serializable> outputParams = task.getOutputParams();
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        encodedWfQueryTaskResult = deleteNtpServerData(task, mirrorRootFdn, outputParams);
        final String successMessage = "NTP KeyId Info has been created successfully.";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;

    }

    private String deleteNtpServerData(final CppCheckAndRemoveNTPServerTask task, final String mirrorRootFdn,
                                       final Map<String, Serializable> outputParams) {

        final String serializedNodeNtpKeyIdsInfo = (String) outputParams
                .get(WorkflowOutputParameterKeys.NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE.toString());
        final NtpKeyIdData nodeNtpKeyIdInfo = NscsObjectSerializer.readObject(serializedNodeNtpKeyIdsInfo);
        final String serializedNtpServerIdsListToBeDeleted = (String) outputParams
                .get(WorkflowOutputParameterKeys.NTP_SERVER_IDS_TO_BE_DELETED_FROM_NODE.toString());
        final List<String> ntpServerIdsListToBeDeleted = NscsObjectSerializer.readObject(serializedNtpServerIdsListToBeDeleted);
        final String[] requestedAttrsNtpServer = { ModelDefinition.NtpServer.NTP_KEY_ID, ModelDefinition.NtpServer.NTP_SERVER_ID,
                ModelDefinition.NtpServer.SERVER_ADDRESS };

        CmResponse ntpServerResponse = null;
        ntpServerResponse = reader.getMos(mirrorRootFdn, NtpConstants.CPP_NTP_SERVER_MO.type(), NtpConstants.CPP_NTP_SERVER_MO.namespace(),
                requestedAttrsNtpServer);

        if (!isValidCmResponse(ntpServerResponse)) {
            final String errorMessage = "Failed to read NTP server data from node.";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        if (nodeNtpKeyIdInfo != null) {
            return deleteNtpServerForKeyId(task, ntpServerResponse, nodeNtpKeyIdInfo, outputParams);
        }
        return deleteNtpServerForServerId(task, ntpServerResponse, ntpServerIdsListToBeDeleted, outputParams);

    }

    private boolean isValidCmResponse(final CmResponse cmResponse) {
        return ((cmResponse != null && cmResponse.getCmObjects() != null) && (!cmResponse.getCmObjects().isEmpty()));
    }

    private String deleteNtpServerForKeyId(final CppCheckAndRemoveNTPServerTask task, final CmResponse ntpServerResponse,
                                           final NtpKeyIdData nodeNtpKeyIdInfo, final Map<String, Serializable> outputParams) {

        final Set<Integer> keyIdsTobeRemovedFromInstalledList = new HashSet<>();
        final Set<Integer> mappingToBeRemovedForKeyId = new HashSet<>();

        for (final CmObject ntpServerCmObj : ntpServerResponse.getCmObjects()) {
            final Integer ntpKeyId = new Integer ((ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_KEY_ID)).toString());
            final String ntpServerId = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_SERVER_ID);
            final String ntpServerAddress = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.SERVER_ADDRESS);

            if (nodeNtpKeyIdInfo.getKeyIdList().contains(ntpKeyId)) {
                nscsCMWriterService.deleteMo(ntpServerCmObj.getFdn());
                nscsLogger.info("Ntp server deleted: ntpServerId [{}] on node: [{}]", ntpServerId, ntpServerCmObj.getFdn());
                if (ntpUtility.buildNtpserverIdFromEnmHostId(ntpServerAddress).equalsIgnoreCase(ntpServerId)) {
                    mappingToBeRemovedForKeyId.add(ntpKeyId);
                }
            }
        }

        return serializeResult(task, new LinkedList<>(keyIdsTobeRemovedFromInstalledList), new LinkedList<>(mappingToBeRemovedForKeyId), outputParams);

    }

    private String deleteNtpServerForServerId(final CppCheckAndRemoveNTPServerTask task, final CmResponse ntpServerResponse,
                                              final List<String> ntpServerIdsListToBeDeleted, final Map<String, Serializable> outputParams) {
        final boolean areAllServersSelected = areAllServersSelected(ntpServerIdsListToBeDeleted);
        final Set<Integer> keyIdsTobeRemovedFromInstalledList = new HashSet<>();
        final Set<Integer> mappingToBeRemovedForKeyId = new HashSet<>();

        for (final CmObject ntpServerCmObj : ntpServerResponse.getCmObjects()) {
            final Integer ntpKeyId = new Integer ((ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_KEY_ID)).toString());
            final String ntpServerId = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_SERVER_ID);
            final String ntpServerAddress = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.SERVER_ADDRESS);

            if (areAllServersSelected || ntpServerIdsListToBeDeleted.contains(ntpServerId)) {
                nscsCMWriterService.deleteMo(ntpServerCmObj.getFdn());

                if (ntpKeyId != 0 && !isKeyIdMappedToOtherServer(ntpKeyId, ntpServerIdsListToBeDeleted, ntpServerResponse.getCmObjects(),
                        areAllServersSelected)) {
                    keyIdsTobeRemovedFromInstalledList.add(ntpKeyId);
                    if (ntpUtility.buildNtpserverIdFromEnmHostId(ntpServerAddress).equalsIgnoreCase(ntpServerId)) {
                        mappingToBeRemovedForKeyId.add(ntpKeyId);
                    }
                }

            }
        }

        return serializeResult(task, new LinkedList<>(keyIdsTobeRemovedFromInstalledList), new LinkedList<>(mappingToBeRemovedForKeyId), outputParams);
    }

    private boolean isKeyIdMappedToOtherServer(final Integer ntpKeyId, final List<String> ntpServerIdsListToBeDeleted,
                                               final Collection<CmObject> ntpServersOnNode, final boolean areAllServersSelected) {
        if (areAllServersSelected) {
            return false;
        }

        for (final CmObject ntpServerCmObj : ntpServersOnNode) {
            final Integer keyId = new Integer ((ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_KEY_ID)).toString());
            final String ntpServerId = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_SERVER_ID);

            if (!ntpServerIdsListToBeDeleted.contains(ntpServerId) && ntpKeyId.equals(keyId)) {
                return true;
            }
        }
        return false;
    }

    private boolean areAllServersSelected(final List<String> ntpServerIdsListToBeDeleted) {
        return (ntpServerIdsListToBeDeleted.size() == 1 && ALL.equalsIgnoreCase(ntpServerIdsListToBeDeleted.get(0).trim()));
    }

    private String serializeResult(final CppCheckAndRemoveNTPServerTask task, final List<Integer> keyIdsTobeRemovedFromInstalledList,
                                   final List<Integer> mappingToBeRemovedForKeyId, final Map<String, Serializable> outputParams) {

        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        String serializedMappingToBeRemovedForKeyId = null;
        String serializedNtpKeyIds = null;
        try {
            serializedMappingToBeRemovedForKeyId = NscsObjectSerializer.writeObject(mappingToBeRemovedForKeyId);
            if (!keyIdsTobeRemovedFromInstalledList.isEmpty()) {
                final NtpKeyIdData keyIdsData = new NtpKeyIdData(task.getNodeFdn(), keyIdsTobeRemovedFromInstalledList);
                serializedNtpKeyIds = NscsObjectSerializer.writeObject(keyIdsData);
                outputParams.put(WorkflowOutputParameterKeys.NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE.toString(), serializedNtpKeyIds);
            }

        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing node key Id info to remove mapping from NTP server";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.MAPPING_TO_BE_REMOVED_FOR_KEY_IDS.toString(), serializedMappingToBeRemovedForKeyId);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params for key Ids to be Removed from the node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully prepared node NTP key ID List to be removed from the node";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;
    }
}