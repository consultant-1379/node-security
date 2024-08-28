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
package com.ericsson.nms.security.nscs.workflow.task.comecim.node.validation;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpKeyIdData;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimValidateNodeForNtpRemoveTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * Task handler for WorkflowTaskType.COM_ECIM_VALIDATE_NODE_FOR_NTP_REMOVE
 *
 * Workflow task handler representing a request to validate the Node synchronization for NTP remove.
 *
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_VALIDATE_NODE_FOR_NTP_REMOVE)
@Local(WFTaskHandlerInterface.class)
public class ComEcimValidateNodeForNtpRemoveTaskHandler implements WFQueryTaskHandler<ComEcimValidateNodeForNtpRemoveTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NodeValidatorUtility nodeValidator;

    private static final String ALL = "all";

    @Override
    public String processTask(final ComEcimValidateNodeForNtpRemoveTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(task.getNode());

        final String mirrorRootFdn = normNode.getFdn();

        final Map<String, Serializable> outputParams = task.getOutputParams();

        String serializeResult = null;

        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing validation for node: " + mirrorRootFdn + ".");

        verifyNodeSynchronization(task, normNode);

        if (task.getNtpKeyIdList() != null && !task.getNtpKeyIdList().isEmpty()) {
            final NtpKeyIdData ntpKeyIdsListToBeRemoved = getNtpKeyData(task, mirrorRootFdn);
            serializeResult = (String) serializeResult(task, ntpKeyIdsListToBeRemoved, outputParams);
        } else if (task.getNtpServerIdList() != null && !task.getNtpServerIdList().isEmpty()) {
            final List<String> ntpServerIdList = task.getNtpServerIdList();
            serializeResult = serializeResult(task, ntpServerIdList, outputParams);
        } else {
            final String errorMessage = "Both KeyIds and ServerIds input lists are empty.";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        return serializeResult;
    }

    private NtpKeyIdData getNtpKeyData(final ComEcimValidateNodeForNtpRemoveTask task, final String mirrorRootFdn) {

        final List<String> ntpKeyIdList = task.getNtpKeyIdList();
        NtpKeyIdData ntpKeyIdsListToBeRemoved = null;
        final List<Integer> keyIdsTobeRemoved = new ArrayList<>();
        if (ntpKeyIdList.size() == 1 && ALL.equalsIgnoreCase(ntpKeyIdList.get(0).trim())) {
            ntpKeyIdsListToBeRemoved = new NtpKeyIdData(mirrorRootFdn, keyIdsTobeRemoved);
        } else {
            for (final String keyId : ntpKeyIdList) {
                keyIdsTobeRemoved.add(Integer.parseInt(keyId));
            }
            if (!keyIdsTobeRemoved.isEmpty()) {
                ntpKeyIdsListToBeRemoved = new NtpKeyIdData(mirrorRootFdn, keyIdsTobeRemoved);
            }
        }
        return ntpKeyIdsListToBeRemoved;
    }

    private void verifyNodeSynchronization(final ComEcimValidateNodeForNtpRemoveTask task, final NormalizableNodeReference normNode) {
        if (!nodeValidator.isNodeSynchronized(normNode)) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "process Task ValidateNodeForNtpTask for node [" + normNode + "] failed.");
            throw new WorkflowTaskException("process task Validate Node For Ntp failed");
        }
    }

    private Object serializeResult(final ComEcimValidateNodeForNtpRemoveTask task, final NtpKeyIdData ntpKeyIdsListToBeRemoved, final Map<String, Serializable> outputParams) {

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
                final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing output params for key Ids to be removed from the node";
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

    private String serializeResult(final ComEcimValidateNodeForNtpRemoveTask task, final List<String> ntpServerIdList, final Map<String, Serializable> outputParams) {
        Map<String, Serializable> outParams = outputParams;
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params");
            outParams = new HashMap<>();
        }

        /*
         * Serialize node ntp server id info in output parameters
         */
        if (ntpServerIdList != null && !ntpServerIdList.isEmpty()) {
            String serializedNtpServerIdsListToBeDeleted = null;
            try {
                serializedNtpServerIdsListToBeDeleted = NscsObjectSerializer.writeObject(ntpServerIdList);
            } catch (final IOException e1) {
                final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing output params for server Ids to be deleted from the node";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            outParams.put(WorkflowOutputParameterKeys.NTP_SERVER_IDS_TO_BE_DELETED_FROM_NODE.toString(), serializedNtpServerIdsListToBeDeleted);
        }

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params for server Ids to be deleted from the node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully prepared node NTP Server Ids List to be deleted from the node";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;
    }

}