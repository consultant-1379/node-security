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
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeSetting;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;

import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.model.NtpKeyData;
import com.ericsson.nms.security.nscs.ntp.utility.NtpUtility;
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppCheckNtpKeysInstalledTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CHECK_KEYS_INSTALLED
 * </p>
 * <p>
 * Checks if NTP keys installed or not
 * </p>
 *
 * @author xjangop
 */
@WFTaskType(WorkflowTaskType.CPP_CHECK_NTP_KEYS_INSTALLED)
@Local(WFTaskHandlerInterface.class)
public class CppCheckNtpKeysInstalledTaskHandler implements WFQueryTaskHandler<CppCheckNtpKeysInstalledTask>, WFTaskHandlerInterface {

    private static final String INSTALLED = "INSTALLED";
    private static final String NOT_INSTALLED = "NOT_INSTALLED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NtpUtility ntpUtility;

    @Inject
    NscsNodeUtility nscsNodeUtility;

    @Override
    public String processTask(final CppCheckNtpKeysInstalledTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final Map<String, Serializable> outputParams = getOutputParams(task);

        final NtpKeyData ntpKeyData = ntpUtility.getNtpKeyDataFromOutPutParams(task, outputParams, "Cpp Check Ntp Keys Installed.");
        final WorkflowMoAction targetWorkflowMoAction = getWorkflowMoActionFromOutPutParams(task, outputParams);

        final int remainingIterations = targetWorkflowMoAction.getRemainingPollTimes();

        final Integer ntpKeyId = ntpKeyData.getId();

        final List<Integer> installedNtpKeyIds = getInstalledNtpKeys(task);

        if (targetWorkflowMoAction.getRemainingPollTimes() <= 0) {
            final String errorMessage = "CppCheckNtpKeysInstalledTaskHandler: Exceeded maximum poll iterations to check completion status of install ntp keys action.";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskTimeoutException("Exceeded maximum poll iterations during install ntp keys action.");
        }

        targetWorkflowMoAction.setRemainingPollTimes(remainingIterations - 1);

        if (installedNtpKeyIds.contains(ntpKeyId)) {

            return serializeResult(task, ntpKeyData, INSTALLED, targetWorkflowMoAction, outputParams);
        } else {
            nscsLogger.info(
                    "Install ntp keys action status check: Ntp key [{}] for Node : [{}] with NtpKeyId [{}]. Remaining iterations to check [{}].",
                    NOT_INSTALLED, task.getNodeFdn(), ntpKeyId, targetWorkflowMoAction.getRemainingPollTimes());
            return serializeResult(task, ntpKeyData, NOT_INSTALLED, targetWorkflowMoAction, outputParams);

        }

    }

    private WorkflowMoAction getWorkflowMoActionFromOutPutParams(final CppCheckNtpKeysInstalledTask task,
            final Map<String, Serializable> outputParams) {

        final String serializedMoActions = (String) outputParams.get(WorkflowOutputParameterKeys.MO_ACTION.toString());
        final WorkflowMoAction targetWorkflowMoAction = NscsObjectSerializer.readObject(serializedMoActions);
        if (targetWorkflowMoAction == null) {
            final String errorMessage = "CppCheckNtpKeysInstalledTaskHandler: Missing MO actions internal parameter, while deserializing MO action param to check ntp keys installed.";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Missing work flow Mo action parameter.");
        }

        return targetWorkflowMoAction;
    }

    private List<Integer> getInstalledNtpKeys(final CppCheckNtpKeysInstalledTask task) {
        final NodeReference node = task.getNode();

        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(node);

        final String mirrorRootFdn = normalizableNodeRef.getFdn();

        final Mo rootMo = capabilityService.getMirrorRootMo(normalizableNodeRef);

        final String timeSettingFdn = ntpUtility.getTimeSettingMOFdn(mirrorRootFdn, rootMo);

        final MoObject timeSettingMoObj = readerService.getMoObjectByFdn(timeSettingFdn);

        return timeSettingMoObj.getAttribute(TimeSetting.INSTALLED_NTP_KEY_IDS);
    }

    private String serializeResult(final CppCheckNtpKeysInstalledTask task, final NtpKeyData ntpKeyData, final String result,
            final WorkflowMoAction wfAction, final Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;

        final ObjectMapper mapper = new ObjectMapper();

        String serializedNtpKeyData = null;
        try {
            serializedNtpKeyData = mapper.writeValueAsString(ntpKeyData);
        } catch (final IOException ioException) {
            final String errorMessage = "CppCheckNtpKeysInstalledTaskHandler:" + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while serializing ntp key data.");
        }

        String serializedMoAction = null;
        try {
            serializedMoAction = NscsObjectSerializer.writeObject(wfAction);
        } catch (final IOException ioException) {
            final String errorMessage = "CppCheckNtpKeysInstalledTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while serializing WorkflowMoAction data.");
        }
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTION.toString(), serializedMoAction);
        outputParams.put(WorkflowOutputParameterKeys.NTP_KEY.toString(), serializedNtpKeyData);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException ioException) {
            final String errorMessage = "CppCheckNtpKeysInstalledTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while serializing ntp key and WorkflowMoAction data.");
        }

        if (result.equals(INSTALLED)) {
            final String successMessage = "Ntp keys installed on Node : " + task.getNodeFdn() + " with NtpKeyId:  " + ntpKeyData.getId() + ".";
            nscsLogger.info(successMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }
        if (result.equals(NOT_INSTALLED)) {
            final String ongoingMessage = "Ntp keys not yet installed on Node : " + task.getNodeFdn() + " with NtpKeyId:  " + ntpKeyData.getId() + "."
                    + "Remaining iterations to check : " + wfAction.getRemainingPollTimes();
            nscsLogger.info(ongoingMessage);
            nscsLogger.workFlowTaskHandlerOngoing(task, ongoingMessage);
        }

        return encodedWfQueryTaskResult;
    }

    private Map<String, Serializable> getOutputParams(final CppCheckNtpKeysInstalledTask task) {

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters in CppCheckNtpKeysInstalledTaskHandler";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Missing ntp key details out put parameters.");
        }

        return outputParams;
    }

}
