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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeSetting;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpKeyIdData;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ValidateNodeForNtpRemoveTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * Task handler for WorkflowTaskType.VALIDATE_NODE_FOR_NTP_REMOVE
 *
 * Workflow task handler representing a request to validate the Node synchronization for NTP remove and validate key IDs to be removed.
 *
 * @author xvekkar
 */
@WFTaskType(WorkflowTaskType.VALIDATE_NODE_FOR_NTP_REMOVE)
@Local(WFTaskHandlerInterface.class)
public class ValidateNodeForNtpRemoveTaskHandler implements WFQueryTaskHandler<ValidateNodeForNtpRemoveTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NodeValidatorUtility nodeValidator;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    private static final String ALL = "all";

    @SuppressWarnings("unchecked")
    @Override
    public String processTask(final ValidateNodeForNtpRemoveTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(task.getNode());

        final String mirrorRootFdn = normNode.getFdn();

        final Map<String, Serializable> outputParams = task.getOutputParams();

        String serializeResult = null;

        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing validation for node: " + mirrorRootFdn + ".");

        verifyNodeSynchronization(task, normNode);

        final Map<String, Object> ntpDetails = getNtpDetails(task, mirrorRootFdn);

        final List<Integer> installedNtpKeyIds = (List<Integer>) ntpDetails.get(TimeSetting.INSTALLED_NTP_KEY_IDS);
        if (task.getNtpKeyIdList() != null && !task.getNtpKeyIdList().isEmpty()) {
            final NtpKeyIdData ntpKeyIdsListToBeRemoved = getNtpKeyIdData(task, mirrorRootFdn, installedNtpKeyIds);
            serializeResult = (String) serializeKeyIdsResult(task, ntpKeyIdsListToBeRemoved, outputParams);

        } else if (task.getNtpServerIdList() != null && !task.getNtpServerIdList().isEmpty()) {
            final List<String> ntpServerIdList = task.getNtpServerIdList();
            serializeResult = serializeServerIdsResult(task, ntpServerIdList, outputParams);

        } else {
            final String errorMessage = "Both keyid's and serverid's input list is empty.";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        final String successMessage = "Successfully completed node validation for workflow.";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return serializeResult;
    }

    private NtpKeyIdData getNtpKeyIdData(final ValidateNodeForNtpRemoveTask task, final String mirrorRootFdn,
                                         final List<Integer> installedNtpKeyIds) {
        final List<String> nodeNtpKeyIdList = task.getNtpKeyIdList();
        NtpKeyIdData ntpKeyIdsListToBeRemoved = null;

        if (installedNtpKeyIds.isEmpty()) {
            final String errorMessage = "NO key id is installed on the node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        boolean areAllKeysSelected = false;
        if (nodeNtpKeyIdList.size() == 1 && ALL.equalsIgnoreCase(nodeNtpKeyIdList.get(0).trim())) {
            areAllKeysSelected = true;
        }

        if (areAllKeysSelected) {
            ntpKeyIdsListToBeRemoved = new NtpKeyIdData(mirrorRootFdn, installedNtpKeyIds);
        } else {

            final List<Integer> keyIdsTobeRemoved = getKeyIdsTobeRemoved(task, nodeNtpKeyIdList, installedNtpKeyIds);

            if (!keyIdsTobeRemoved.isEmpty()) {
                ntpKeyIdsListToBeRemoved = new NtpKeyIdData(mirrorRootFdn, keyIdsTobeRemoved);
            } else {
                final String errorMessage = "None of the provided key ids is installed on the node";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new WorkflowTaskException(errorMessage);
            }
        }

        return ntpKeyIdsListToBeRemoved;
    }

    private List<Integer> getKeyIdsTobeRemoved(final ValidateNodeForNtpRemoveTask task, final List<String> ntpKeyIdList,
                                               final List<Integer> installedNtpKeyIds) {
        final List<Integer> keyIdsTobeRemoved = new ArrayList<>();
        for (final String keyId : ntpKeyIdList) {
            try {
                if (installedNtpKeyIds.contains(Integer.parseInt(keyId))) {
                    keyIdsTobeRemoved.add(Integer.parseInt(keyId.trim()));
                }
            } catch (final NumberFormatException exe) {
                final String errorMessage = "Given Key ID " + keyId + " is not an integer.";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
        }
        return keyIdsTobeRemoved;
    }

    private Map<String, Object> getNtpDetails(final ValidateNodeForNtpRemoveTask task, final String mirrorRootFdn) {
        final Mo timeSettingMo = Model.ME_CONTEXT.managedElement.systemFunctions.timeSetting;
        final String[] requestedAttrs = { TimeSetting.INSTALLED_NTP_KEY_IDS };

        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, timeSettingMo.type(), requestedAttrs);
        final Map<String, Object> ntpDetails = new HashMap<>();
        final String moFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, timeSettingMo.type(), timeSettingMo.namespace(), ntpDetails,
                requestedAttrs);
        if (moFdn == null) {
            final String errorMessage = "Error while reading MO " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(mirrorRootFdn, timeSettingMo.type());
        }
        return ntpDetails;
    }

    private void verifyNodeSynchronization(final ValidateNodeForNtpRemoveTask task, final NormalizableNodeReference normNode) {
        if (!nodeValidator.isNodeSynchronized(normNode)) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "process Task ValidateNodeForNtpTask for node [" + normNode + "] failed.");
            throw new WorkflowTaskException("process task Validate Node For Ntp failed");
        }
    }

    private Object serializeKeyIdsResult(final ValidateNodeForNtpRemoveTask task, final NtpKeyIdData ntpKeyIdsListToBeRemoved,
                                         final Map<String, Serializable> outputParams) {

        Map<String, Serializable> outParams = outputParams;
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params");
            outParams = new HashMap<>();
        }

        /*
         * Serialize node ntp key id info in output parameters
         */
        if (ntpKeyIdsListToBeRemoved != null) {
            String serializedNtpKeyIdsListToBeRemoved = null;
            try {
                serializedNtpKeyIdsListToBeRemoved = NscsObjectSerializer.writeObject(ntpKeyIdsListToBeRemoved);
            } catch (final IOException e1) {
                final String errorMessage = NscsLogger.stringifyException(e1)
                        + " while serializing output params for key Ids to be removed from the node";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            outParams.put(WorkflowOutputParameterKeys.NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE.toString(), serializedNtpKeyIdsListToBeRemoved);
        }

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params for key Ids to be removed from the node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully prepared node NTP key ID List to be removed from the node";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;
    }

    private String serializeServerIdsResult(final ValidateNodeForNtpRemoveTask task, final List<String> ntpServerIdList,
                                            final Map<String, Serializable> outputParams) {
        Map<String, Serializable> outParams = outputParams;
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params");
            outParams = new HashMap<>();
        }

        /*
         * Serialize node ntp server id info in output parameters
         */

        String serializedNtpServerIdsListToBeDeleted = null;
        try {
            serializedNtpServerIdsListToBeDeleted = NscsObjectSerializer.writeObject(ntpServerIdList);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1)
                    + " while serializing output params for server Ids to be deleted from the node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outParams.put(WorkflowOutputParameterKeys.NTP_SERVER_IDS_TO_BE_DELETED_FROM_NODE.toString(), serializedNtpServerIdsListToBeDeleted);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e)
                    + " while serializing output params for server Ids to be deleted from the node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully prepared NTP Server Ids List to be deleted from the node";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;
    }

}