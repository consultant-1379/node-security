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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.util.NtpKeyIdData;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.RemoveNtpKeysTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * Task handler for WorkflowTaskType.REMOVE_NTP_KEYS
 * 
 * Perform MO action on node to remove installed NTP keys.
 * 
 * @author xvekkar
 */
@WFTaskType(WorkflowTaskType.REMOVE_NTP_KEYS)
@Local(WFTaskHandlerInterface.class)
public class RemoveNtpKeysTaskHandlers implements WFQueryTaskHandler<RemoveNtpKeysTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private MOActionService moActionService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    public static final String REMOVING_NTP_KEYS_ON_NODE = "Removing NTP key on Node: ";

    String result = "NOT_REMOVED";

    @Override
    public String processTask(final RemoveNtpKeysTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters, output parameter null for RemoveNtpKeysTask";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String serializedNtpKeyIdsListToBeRemoved = (String) outputParams.get(WorkflowOutputParameterKeys.NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE.toString());
        final NtpKeyIdData nodeNtpKeyIdInfo = NscsObjectSerializer.readObject(serializedNtpKeyIdsListToBeRemoved);
        nscsLogger.info(task, "RemoveNtpKeysTask for node [{}]", node.getName());
        String serializeResult = null;
        try {

            serializeResult = performMoActionOnNode(normalizable, task, nodeNtpKeyIdInfo);
        } catch (final Exception exception) {
            final String errorMessage = exception.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        final String successMessage = "Successfully performed MO action on node";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return serializeResult;

    }

    /**
     * @param node
     *            the network element on which action is to be performed.
     * @param task
     *            Onging task
     * @param ntpKeyIdsList
     *            list of key ids to be removed from the node.
     * @return String Status of MO action as REMOVED or NOT_REMOVED.
     */
    private String performMoActionOnNode(final NormalizableNodeReference node, final RemoveNtpKeysTask task, final NtpKeyIdData nodeNtpKeyIdInfo) {
        final String actionMessage = String.format("action: %s on: %s", MoActionWithParameter.CPP_REMOVE_NTP_KEYS.getAction(), MoActionWithParameter.CPP_REMOVE_NTP_KEYS.getMo().type());
        nscsLogger.info(task, "Performing: " + actionMessage);
        final WorkflowMoAction moAction = new WorkflowMoActionWithParams(getTimeSettingMOFdn(node), MoActionWithParameter.CPP_REMOVE_NTP_KEYS, toWorkflowParams(nodeNtpKeyIdInfo),
                NtpConstants.POLL_TIMES);
        try {
            nscsLogger.workFlowTaskHandlerOngoing(task, REMOVING_NTP_KEYS_ON_NODE + node);
            if (nodeNtpKeyIdInfo != null && !nodeNtpKeyIdInfo.getKeyIdList().isEmpty()) {
                moActionService.performMOAction(node.getFdn(), MoActionWithParameter.CPP_REMOVE_NTP_KEYS, toMoParams(nodeNtpKeyIdInfo.getKeyIdList()));
            }
            result = "REMOVED";
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, REMOVING_NTP_KEYS_ON_NODE + node + " task completed.");
            return serializeResult(task, result, moAction);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " UnexpectedErrorException" + actionMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
    }

    private MoParams toMoParams(final List<Integer> ntpKeyIdsListToBeRemoved) {
        final MoParams params = new MoParams();
        final List<String> keyList = new ArrayList<>();
        for (final Integer key : ntpKeyIdsListToBeRemoved) {
            keyList.add(String.valueOf(key));
        }
        params.addListParam("ntpKeyIdList", keyList);
        return params;

    }

    private String serializeResult(final RemoveNtpKeysTask task, final String result, final WorkflowMoAction moAction) {
        String encodedWfQueryTaskResult = null;
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters, output parameter null for RemoveNtpKeysTask";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        String serializedMoActions = null;
        try {
            nscsLogger.info(task, "serializedMoActions {}", moAction.getRemainingPollTimes());
            serializedMoActions = NscsObjectSerializer.writeObject(moAction);
        } catch (final IOException e) {
            final String errorMessage = "Exception occured while serializing ntp key Id list in  RemoveNtpKeysTask";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.CPP_ARE_NTP_KEYS_REMOVED.toString(), result);
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTION.toString(), serializedMoActions);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedWfQueryTaskResult;
    }

    private String getTimeSettingMOFdn(final NormalizableNodeReference node) {
        final Mo rootMo = capabilityService.getMirrorRootMo(node);
        final String mirrorRootFdn = node.getFdn();
        nscsLogger.debug("Get timeSetting for mirrorRootFdn[{}] rootMo[{}] ", mirrorRootFdn, rootMo);
        if (mirrorRootFdn == null || mirrorRootFdn.isEmpty() || rootMo == null) {
            nscsLogger.error("Get TimeSettingFdn : invalid value : mirrorRootFdn[{}] rootMo[{}] ", mirrorRootFdn, rootMo);
            return null;
        }
        final Mo timeSettingMo = ((CppManagedElement) rootMo).systemFunctions.timeSetting;
        final String timeSettingFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, timeSettingMo);

        nscsLogger.debug("Get timeSetting return[{}]", timeSettingFdn);
        return timeSettingFdn;
    }

    private WorkflowMoParams toWorkflowParams(final NtpKeyIdData nodeNtpKeyIdInfo) {

        final WorkflowMoParams params = new WorkflowMoParams();
        params.addParam("ntpKeyIdInfo", String.valueOf(nodeNtpKeyIdInfo));
        return params;
    }
}
