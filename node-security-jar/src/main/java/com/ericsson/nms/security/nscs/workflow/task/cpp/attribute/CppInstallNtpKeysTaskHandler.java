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
import java.util.*;

import javax.ejb.Local;
import javax.inject.Inject;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NtpServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeSetting;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.validation.impl.NtpValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.model.NtpKeyData;
import com.ericsson.nms.security.nscs.ntp.utility.GetNtpKeyDataResponseEntity;
import com.ericsson.nms.security.nscs.ntp.utility.NtpUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppInstallNtpKeysTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Task handler for WorkflowTaskType.CPP_INSTALL_NTP_KEYS
 *
 * Install the NTP key data on node
 *
 * @author xkihari
 */

@WFTaskType(WorkflowTaskType.CPP_INSTALL_NTP_KEYS)
@Local(WFTaskHandlerInterface.class)
public class CppInstallNtpKeysTaskHandler implements WFQueryTaskHandler<CppInstallNtpKeysTask>, WFTaskHandlerInterface {

    @Inject
    private MOActionService moActionService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    NscsNodeUtility nscsNodeUtility;

    @Inject
    NtpValidator ntpValidator;

    @Inject
    NtpUtility ntpUtility;

    @Inject
    GetNtpKeyDataResponseEntity ntpKeyDataResponseEntity;

    @Override
    public String processTask(final CppInstallNtpKeysTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);

        final Map<String, Serializable> outputParams = getOutputParams(task);

        return installNtpKeyIds(normalizable, task, outputParams);
    }

    private String installNtpKeyIds(final NormalizableNodeReference normNode, final CppInstallNtpKeysTask task,
            final Map<String, Serializable> outputParams) {

        final List<Integer> installedNtpKeyIdsFromNode = getInstalledNtpKeyIdsFromNode(normNode);
        nscsLogger.info("installedNtpKeyIdsFromNode : {} and node :{}", installedNtpKeyIdsFromNode, task.getNode());

        NtpKeyData ntpKeyData = ntpUtility.getNtpKeyDataFromOutPutParams(task, outputParams, "Cpp Install Ntp Keys");
        int ntpKeyIdFromNTPServerDB = ntpKeyData.getId();
        nscsLogger.info("installedNtpKeyIdFromDB : {}", ntpKeyIdFromNTPServerDB);

        final Mo rootMo = capabilityService.getMirrorRootMo(normNode);
        final WorkflowMoAction moAction = new WorkflowMoActionWithParams(ntpUtility.getTimeSettingMOFdn(normNode.getFdn(), rootMo),
                MoActionWithParameter.CPP_INSTALL_NTP_KEYS, toWorkflowParams(ntpKeyData), NtpConstants.CPP_POLL_TIMES);

        try {

            if (installedNtpKeyIdsFromNode.contains(ntpKeyIdFromNTPServerDB)) {

                final CmResponse cmResponse = readerService.getMos(normNode.getFdn(), NtpConstants.CPP_NTP_SERVER, getNtpServerNameSpace(normNode));
                nscsLogger.info("cmResponse for NTPserver MOs  : {} and CMResponse objects {}", cmResponse, cmResponse.getCmObjects());
                if (cmResponse.getCmObjects() != null && !cmResponse.getCmObjects().isEmpty()) {

                    final Set<Integer> externalNtpKeys = getExternalNtpKeys(cmResponse, normNode);
                    boolean isNewNtpKeyGenerated = false;

                    while (externalNtpKeys.contains(ntpKeyIdFromNTPServerDB)) {
                        ntpKeyData = getNewKeyNtpKeyData(task);
                        ntpKeyIdFromNTPServerDB = ntpKeyData.getId();
                        isNewNtpKeyGenerated = true;
                    }

                    if (isNewNtpKeyGenerated) {
                        ntpValidator.validateMaxNumberOfNtpKeys(task, installedNtpKeyIdsFromNode.size());
                    }

                    nscsLogger.info("Installing internal Ntp key on Node {} for existing Ntp server", ntpKeyIdFromNTPServerDB);
                    performInstallNtpKeysAction(normNode, task, ntpKeyData);

                } else {
                    nscsLogger.info("No Ntp server found on Node: [{}] installing Ntp key [{}}", normNode.getFdn(), ntpKeyData.getId());
                    performInstallNtpKeysAction(normNode, task, ntpKeyData);
                }

            } else {
                ntpValidator.validateMaxNumberOfNtpKeys(task, installedNtpKeyIdsFromNode.size());
                nscsLogger.info("Installing new Ntp key on Node: [{}]", normNode.getFdn());
                performInstallNtpKeysAction(normNode, task, ntpKeyData);
            }

        } catch (final Exception exception) {
            final String errorMessage = "CppInstallNtpKeysTaskHandler: " + NscsLogger.stringifyException(exception);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while installing Ntp key ids on node. " + exception.getMessage());
        }
        return serializeResult(task, ntpKeyData, moAction, outputParams);
    }

    private Set<Integer> getExternalNtpKeys(final CmResponse cmResponse, final NormalizableNodeReference normNode) { 
        final Set<Integer> externalNtpKeys = new TreeSet<>();
        final List<String> listOfItserviceAddresses = ntpUtility.getNtpServerIpAddresses(normNode);
        for (final CmObject cmObject : cmResponse.getCmObjects()) {
            final Map<String, Object> ntpServerAttributes = cmObject.getAttributes();
            final String userLabel = (String) ntpServerAttributes.get(NtpConstants.CPP_USER_LABEL);
            final Integer ntpKeyId = new Integer ((ntpServerAttributes.get(NtpConstants.CPP_NTP_KEY_ID)).toString());
            final String ntpServerId = (String) ntpServerAttributes.get(NtpConstants.CPP_NTP_SERVER_ID);
            final String serverAddress = (String) ntpServerAttributes.get(NtpServer.SERVER_ADDRESS);
            final String formattedEnmHostId = ntpUtility.buildNtpserverIdFromEnmHostId(serverAddress);
            if (!ntpServerId.equals(formattedEnmHostId) || userLabel == null || !userLabel.equals(NtpConstants.NTP_SERVER_ENM_USER_LABEL)) {
                nscsLogger.info("External NTP Server Id [{}] and User Label [{}] with NtpKeyId [{}] found on Node [{}] ", ntpServerId, userLabel,
                        ntpKeyId, cmObject.getFdn());
                if (!listOfItserviceAddresses.contains(serverAddress)) {
                externalNtpKeys.add(ntpKeyId);
                }
            }
        }
        return externalNtpKeys;

    }

    private String getNtpServerNameSpace(final NormalizableNodeReference normalizable) {
        final Mo rootMo = capabilityService.getMirrorRootMo(normalizable);
        final Mo ntpServerMo = ((CppManagedElement) rootMo).systemFunctions.timeSetting.ntpServer;
        return ntpServerMo.namespace();
    }

    private void performInstallNtpKeysAction(final NormalizableNodeReference normNode, final CppInstallNtpKeysTask task, final NtpKeyData ntpKeyData) {
        final String actionMessage = String.format("action: %s on: %s", MoActionWithParameter.CPP_INSTALL_NTP_KEYS.getAction(),
                MoActionWithParameter.CPP_INSTALL_NTP_KEYS.getMo().type());
        nscsLogger.info("Performing Mo action [{}] on Node [{}] with Ntp key id [{}]", actionMessage, task.getNodeFdn(), ntpKeyData.getId());

        try {
            final List<MoParams> ntpKeyDataList = new ArrayList<>();
            ntpKeyDataList.add(toMoParams(ntpKeyData));
            final MoParams moParams = toMoParams(ntpKeyDataList);

            nscsLogger.workFlowTaskHandlerOngoing(task, "Going to install Ntp key id:" + ntpKeyData.getId() + " on Node:" + normNode.getFdn());

            moActionService.performMOAction(normNode.getFdn(), MoActionWithParameter.CPP_INSTALL_NTP_KEYS, moParams);

            nscsLogger.info("Install Ntp Keys action triggered with Ntp key id {} on Node: {}", ntpKeyData.getId(), ntpKeyData.getNodeFdn());

            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task,
                    "Install Ntp Keys action triggered with Ntp key id:" + ntpKeyData.getId() + " on Node:" + normNode.getFdn());

        } catch (final Exception exception) {
            final String errorMessage = "CppInstallNtpKeysTaskHandler:" + NscsLogger.stringifyException(exception) + " while performing "
                    + actionMessage;
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Failed to perform install Ntp key MO action.");
        }
    }

    private static MoParams toMoParams(final List<MoParams> ntpKeyDataList) {
        final MoParams params = new MoParams();
        params.addParam("ntpKeyDataList", ntpKeyDataList);
        return params;
    }

    private List<Integer> getInstalledNtpKeyIdsFromNode(final NormalizableNodeReference node) {
        final Mo rootMo = capabilityService.getMirrorRootMo(node);
        final String timeSettingFdn = ntpUtility.getTimeSettingMOFdn(node.getFdn(), rootMo);
        final MoObject timeSettingMoObj = readerService.getMoObjectByFdn(timeSettingFdn);
        return timeSettingMoObj.getAttribute(TimeSetting.INSTALLED_NTP_KEY_IDS);
    }

    private String serializeResult(final CppInstallNtpKeysTask task, final NtpKeyData ntpKeyData, final WorkflowMoAction moAction,
            final Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        final ObjectMapper mapper = new ObjectMapper();

        String ntpKeys = null;
        try {
            ntpKeys = mapper.writeValueAsString(ntpKeyData);
        } catch (final IOException ioException) {
            final String errorMessage = "CppInstallNtpKeysTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while mapping Ntp key data parameter.");
        }

        String serializedMoActions = null;
        try {
            serializedMoActions = NscsObjectSerializer.writeObject(moAction);
        } catch (final IOException ioException) {
            final String errorMessage = "CppInstallNtpKeysTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while serializing installed Ntp key ids work flow Mo action.");
        }
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTION.toString(), serializedMoActions);
        outputParams.put(WorkflowOutputParameterKeys.NTP_KEY.toString(), ntpKeys);
        nscsLogger.info(task, "Initialized output params InstallNtpKeysTask!");
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException ioException) {
            final String errorMessage = "CppInstallNtpKeysTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while serializing Ntp key data parameter.");
        }
        return encodedWfQueryTaskResult;
    }

    private MoParams toMoParams(final NtpKeyData ntpKeyData) {

        final DigestAlgorithm digestAlgorithm = ntpKeyData.getDigestAlgorithm();
        final String ntpKeyValue = ntpKeyData.getKey();
        final int ntpId = ntpKeyData.getId();
        return toMoParams(digestAlgorithm, ntpKeyValue, ntpId);
    }

    private static MoParams toMoParams(final DigestAlgorithm digestAlgorithm, final String ntpKeyValue, final int ntpId) {
        final MoParams params = new MoParams();
        params.addParam("ntpKeyId", String.valueOf(ntpId));
        params.addParam("ntpKey", ntpKeyValue);
        params.addParam("keyType", digestAlgorithm.toString());
        return params;
    }

    private WorkflowMoParams toWorkflowParams(final NtpKeyData ntpKeyData) {

        final DigestAlgorithm digestAlgorithm = ntpKeyData.getDigestAlgorithm();
        final String ntpKeyValue = ntpKeyData.getKey();
        final int ntpId = ntpKeyData.getId();
        return toWorkflowParams(digestAlgorithm, ntpKeyValue, ntpId);
    }

    private static WorkflowMoParams toWorkflowParams(final DigestAlgorithm digestAlgorithm, final String ntpKeyValue, final int ntpId) {

        final WorkflowMoParams params = new WorkflowMoParams();
        params.addParam("ntpKeyId", String.valueOf(ntpId));
        params.addParam("ntpKey", ntpKeyValue);
        params.addParam("keyType", digestAlgorithm.toString());
        return params;
    }

    private Map<String, Serializable> getOutputParams(final CppInstallNtpKeysTask task) {

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters in CppInstallNtpKeysTaskHandler";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Missing install Ntp keys out put parameters.");
        }

        return outputParams;
    }

    private NtpKeyData getNewKeyNtpKeyData(final CppInstallNtpKeysTask task) {
        nscsLogger.info("Getting new Ntp key data from Ntpservice for Node:[{}]", task.getNodeFdn());
        String responseEntity = null;
        NtpKeyData ntpKeyData = null;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            responseEntity = ntpKeyDataResponseEntity.getNtpKeyDataFromNtpService(task, true);
            nscsLogger.info("Cpp Get Ntp Key Data ResponseEntity {} from the http request", responseEntity);

                ntpKeyData = mapper.readValue(responseEntity, NtpKeyData.class);
                if (ntpKeyData == null) {
                    final String errorMessage = "CppInstallNtpKeysTaskHandler: Null NtpKeyData for Node: " + task.getNodeFdn();
                    nscsLogger.error(errorMessage);
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new WorkflowTaskException("Empty Ntp key data from ntp service.");
                }

            nscsLogger.info("New Ntp key data generated with key id: [{}] for Node:[{}].", ntpKeyData.getId(), task.getNodeFdn());

        } catch (final Exception exception) {
            final String errorMessage = "CppInstallNtpKeysTaskHandler: " + NscsLogger.stringifyException(exception);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while mapping new key Ntp key data.");
        }
        return ntpKeyData;
    }
}
