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
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeSetting;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpKeyIdData;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskTimeoutException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppCheckKeysRemovedTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CHECK_KEYS_REMOVED
 * </p>
 * <p>
 * Check provided Key Ids are removed from target nodes
 * </p>
 *
 * @author tcsviku
 *
 */
@WFTaskType(WorkflowTaskType.CPP_CHECK_KEYS_REMOVED)
@Local(WFTaskHandlerInterface.class)
public class CppCheckKeysRemovedTaskHandler implements WFQueryTaskHandler<CppCheckKeysRemovedTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    private static final String REMOVED = "REMOVED";
    private static final String NOT_REMOVED = "NOT_REMOVED";

    @Override
    public String processTask(final CppCheckKeysRemovedTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NtpKeyIdData nodeNtpKeyIdInfo = getNtpKeyListFromOutPutParams(task);

        final List<Integer> installedNtpKeyIds = getInstalledNtpKeys(task);
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters in CppCheckKeysRemovedTaskHandler";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final WorkflowMoAction targetWorkflowMoAction = getWorkflowMoActionFromOutPutParams(task, outputParams);
        final int remainingIterations = targetWorkflowMoAction.getRemainingPollTimes();

        if (targetWorkflowMoAction.getRemainingPollTimes() <= 0) {
            final String errorMessage = "CppCheckNtpKeysInstalledTaskHandler: Exceeded max poll iterations to check completion status of install ntp keys action.";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskTimeoutException(errorMessage);
        }

        targetWorkflowMoAction.setRemainingPollTimes(remainingIterations - 1);
        if (nodeNtpKeyIdInfo != null && installedNtpKeyIds.contains(nodeNtpKeyIdInfo.getKeyIdList().get(0))) {
                return serializeResult(task, NOT_REMOVED, targetWorkflowMoAction, outputParams);
            }
        final String successMessage = "Successfully verified key ids removed.";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return serializeResult(task, REMOVED, targetWorkflowMoAction, outputParams);

    }

    private NtpKeyIdData getNtpKeyListFromOutPutParams(final CppCheckKeysRemovedTask task) {

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String serializedNodeNtpKeyIdInfo = (String) outputParams.get(WorkflowOutputParameterKeys.NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE.toString());
        return NscsObjectSerializer.readObject(serializedNodeNtpKeyIdInfo);
    }

    private List<Integer> getInstalledNtpKeys(final CppCheckKeysRemovedTask task) {
        final NodeReference node = task.getNode();

        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(node);

        final String mirrorRootFdn = normalizableNodeRef.getFdn();

        final Mo rootMo = capabilityService.getMirrorRootMo(normalizableNodeRef);

        final String timeSettingFdn = getTimeSettingFdn(mirrorRootFdn, rootMo);

        final MoObject timeSettingMoObj = readerService.getMoObjectByFdn(timeSettingFdn);

        return timeSettingMoObj.getAttribute(TimeSetting.INSTALLED_NTP_KEY_IDS);
    }

    private String getTimeSettingFdn(final String mirrorRootFdn, final Mo rootMo) {

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

    private String serializeResult(final CppCheckKeysRemovedTask task, final String result, final WorkflowMoAction targetWorkflowMoAction, final Map<String, Serializable> outputParams) {

        String encodedWfQueryTaskResult = null;
        String serializedMoAction = null;
        try {
            serializedMoAction = NscsObjectSerializer.writeObject(targetWorkflowMoAction);
        } catch (final IOException ioException) {
            final String errorMessage = "CppCheckNtpKeysInstalledTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTION.toString(), serializedMoAction);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);
        nscsLogger.debug(task, "Serializing result [" + result + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully completed : Remove NTP key ID status is [" + result + "]";
        if (REMOVED.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, REMOVED);
        } else {
            nscsLogger.workFlowTaskHandlerOngoing(task, successMessage, NOT_REMOVED);
        }
        return encodedWfQueryTaskResult;
    }

    private WorkflowMoAction getWorkflowMoActionFromOutPutParams(final CppCheckKeysRemovedTask task, final Map<String, Serializable> outputParams) {

        final String serializedMoActions = (String) outputParams.get(WorkflowOutputParameterKeys.MO_ACTION.toString());
        final WorkflowMoAction targetWorkflowMoAction = NscsObjectSerializer.readObject(serializedMoActions);
        if (targetWorkflowMoAction == null) {
            final String errorMessage = "CppCheckKeysRemovedTaskHandler: Missing MO actions internal parameter, while deserializing MO action param to check ntp keys removed.";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        return targetWorkflowMoAction;
    }
}
