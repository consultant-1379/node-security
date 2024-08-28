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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NtpServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeSetting;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.validation.impl.NtpValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.ntp.model.NtpKeyData;
import com.ericsson.nms.security.nscs.ntp.utility.NtpConfigureStatusSender;
import com.ericsson.nms.security.nscs.ntp.utility.NtpUtility;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppConfigureNtpServerTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Task handler for WorkflowTaskType.CPP_CONFIGURE_NTP_SERVER
 *
 * Configures the NTP server details on the node
 *
 * @author xjangop
 */
@WFTaskType(WorkflowTaskType.CPP_CONFIGURE_NTP_SERVER)
@Local(WFTaskHandlerInterface.class)
public class CppConfigureNtpServerTaskHandler implements WFQueryTaskHandler<CppConfigureNtpServerTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCMWriterService writerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NtpConfigureStatusSender ntpConfigStatusSender;

    @Inject
    NtpUtility ntpUtility;

    @Inject
    NscsNodeUtility nscsNodeUtility;

    @Inject
    NtpValidator ntpValidator;

    @Override
    public String processTask(final CppConfigureNtpServerTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final Map<String, Serializable> outputParams = getOutputParams(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(node);
        final String mirrorRootFdn = normalizableNodeRef.getFdn();
        nscsLogger.debug(task, "From task : mirrorRootFdn [{}] for Node [{}].", mirrorRootFdn, task.getNodeFdn());

        final Mo rootMo = capabilityService.getMirrorRootMo(normalizableNodeRef);
        if (rootMo == null) {
            final String errorMessage = "CppConfigureNtpServerTaskHandler: RootMo is null on Node : " + task.getNodeFdn();
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("RootMo does not exists on node.");
        }

        final String timeSettingFdn = ntpUtility.getTimeSettingMOFdn(mirrorRootFdn, rootMo);
        final MoObject timeSettingMoObj = readerService.getMoObjectByFdn(timeSettingFdn);
        final List<Integer> installedNtpKeyIds = timeSettingMoObj.getAttribute(TimeSetting.INSTALLED_NTP_KEY_IDS);
        nscsLogger.info("InstalledNtpKeyIds [{}] for Node [{}].", installedNtpKeyIds, task.getNodeFdn());

        if (installedNtpKeyIds.isEmpty()) {
            final String errorMessage = "CppConfigureNtpServerTaskHandler: Empty installedNtpKeyIds on Node : " + task.getNodeFdn();
            nscsLogger.error("Empty [{}] for Node : [{}] ", TimeSetting.INSTALLED_NTP_KEY_IDS, timeSettingMoObj + task.getNodeFdn());
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("No Installed Ntp key ids found on node.");
        }

        final NtpKeyData ntpKeyData = ntpUtility.getNtpKeyDataFromOutPutParams(task, outputParams, "Configuring Ntp Server");
        final List<String> ntpServerAddressList = new LinkedList<>();
        final Mo ntpServerMo = ((CppManagedElement) rootMo).systemFunctions.timeSetting.ntpServer;
        final String ntpServerNamespace = ntpServerMo.namespace();
        final List<String> listOfItserviceAddresses = ntpUtility.getNtpServerIpAddresses(normalizableNodeRef);
        nscsLogger.info("List of itservices Ip addresses [{}].", listOfItserviceAddresses);
        try {

            final CmResponse ntpServerCmResponse = readerService.getMos(mirrorRootFdn, NtpConstants.CPP_NTP_SERVER, ntpServerNamespace);
            nscsLogger.debug("Get Mos for moName[{}], moNameSpace[{}] under rootFdn[{}] cmResponse[{}].", NtpConstants.CPP_NTP_SERVER,
                    ntpServerNamespace, mirrorRootFdn, ntpServerCmResponse);

            if (ntpServerCmResponse.getCmObjects() == null || ntpServerCmResponse.getCmObjects().isEmpty()) {
                nscsLogger.info(task, "No ntp servers on node : " + normalizableNodeRef.getFdn() + " configuring all new ntp servers");
                ntpValidator.validateMaxNumberOfNtpServers(task, listOfItserviceAddresses.size());
                configureNtpServer(normalizableNodeRef, task, timeSettingFdn, ntpServerMo, ntpKeyData.getId(), listOfItserviceAddresses);
            } else {
                nscsLogger.info(task, "Found ntp servers on node : " + normalizableNodeRef.getFdn());
                for (CmObject ntpServerCmObject : ntpServerCmResponse.getCmObjects()) {
                    updateOrDeleteNtpServerIfExists(normalizableNodeRef, task, ntpKeyData, ntpServerAddressList, ntpServerCmObject);
                }

                final int ntpServerCountOnNode = readerService.getMos(mirrorRootFdn, NtpConstants.CPP_NTP_SERVER, ntpServerNamespace).getCmObjects()
                        .size();
                nscsLogger.info(task,
                        "List of existsing updated internal ntp servers " + ntpServerAddressList + " on node: " + normalizableNodeRef.getFdn());
                if (!ntpServerAddressList.isEmpty()) {

                    nscsLogger.info(task,
                            "Checking for itservices IP addresses which are not existsing on node : " + normalizableNodeRef.getFdn());
                    ntpValidator.validateMaxNumberOfNtpServers(task,
                            ntpServerCountOnNode + (listOfItserviceAddresses.size() - ntpServerAddressList.size()));
                    reConfigureNtpServer(normalizableNodeRef, timeSettingFdn, ntpServerMo, task, ntpKeyData, ntpServerAddressList);
                } else {

                    ntpValidator.validateMaxNumberOfNtpServers(task, ntpServerCountOnNode + listOfItserviceAddresses.size());
                    configureNtpServer(normalizableNodeRef, task, timeSettingFdn, ntpServerMo, ntpKeyData.getId(), listOfItserviceAddresses);
                }
            }

        } catch (final Exception exception) {

            final String errorMessage = NscsLogger.stringifyException(exception) + "While configuring NtpServer MO with keyId "
                    + ntpKeyData.getId() + " for Node " + task.getNodeFdn();

            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while configuring Ntp server MO details. " + exception.getMessage());
        }

        ntpConfigStatusSender.sendNtpConfigureStatus(task, "SUCCESS");

        return serializeResult(task, ntpKeyData, outputParams);
    }

    private void updateNTPServer(final CppConfigureNtpServerTask task, final NtpKeyData ntpKeyData, final CmObject ntpServerCmObj,
            final String ntpServerIpAddr) {
        nscsLogger.info("CppConfigureNtpServerTaskHandler: updating NTPServer with KeyId [{}] for Node [{}] ", ntpKeyData.getId(),
                ntpKeyData.getNodeFdn());
        final NscsCMWriterService.WriterSpecificationBuilder ntpServerSpec = writerService.withSpecification();

        ntpServerSpec.setFdn(ntpServerCmObj.getFdn());
        ntpServerSpec.setNotNullAttribute(NtpServer.NTP_KEY_ID, ntpKeyData.getId());
        ntpServerSpec.setNotNullAttribute(NtpServer.SERVER_ADDRESS, ntpServerIpAddr);
        ntpServerSpec.setNotNullAttribute(NtpServer.USER_LABEL, NtpConstants.NTP_SERVER_ENM_USER_LABEL);
        ntpServerSpec.setNotNullAttribute(NtpServer.SERVICE_ACTIVE, true);

        try {
            ntpServerSpec.updateMO();
        } catch (final Exception exception) {
            final String errorMessage = "CppConfigureNtpServerTaskHandler: " + NscsLogger.stringifyException(exception) + " while updating ntpKeyId: "
                    + ntpKeyData.getId() + "for Node " + task.getNodeFdn();
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while updating Ntp Server MO on node.");
        }
        nscsLogger.info("NTPServer updated successfully with ntpKeyId:[{}] serverAddress:[{}] ntpServerId:[{}] ntpUserLabel:[{}] for Node [{}]",
                ntpKeyData.getId(), ntpServerIpAddr, ntpUtility.getEnmHostId(), NtpConstants.NTP_SERVER_ENM_USER_LABEL, task.getNodeFdn());

    }

    private void createNtpServer(final CppConfigureNtpServerTask task, final NormalizableNodeReference normalizableNodeRef,
            final Map<String, Object> ntpServerAttributes, final Mo ntpServerMo, final String timeSettingFdn) {
        nscsLogger.info("Creating NtpServer for Node:[{}] ", normalizableNodeRef.getFdn());

        final String targetCategory = normalizableNodeRef.getTargetCategory();
        final String nodeType = normalizableNodeRef.getNeType();
        final String ossModelIdentity = normalizableNodeRef.getOssModelIdentity();

        NscsModelInfo nscsModelInfo = null;
        try {
            nscsModelInfo = nscsModelServiceImpl.getModelInfo(targetCategory, nodeType, ossModelIdentity, ntpServerMo.type());
        } catch (final NscsModelServiceException | IllegalArgumentException exception) {
            final String errorMessage = "CppConfigureNtpServerTaskHandler: " + NscsLogger.stringifyException(exception) + " for Node: "
                    + task.getNodeFdn();
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while getting model information of node.");
        }
        if (nscsModelInfo == null) {
            final String errorMessage = "CppConfigureNtpServerTaskHandler: " + "Null Model Info for Node: " + task.getNodeFdn();
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("No model information found for node.");
        }

        try {
            writerService.createMo(timeSettingFdn, ntpServerMo.type(), nscsModelInfo.getNamespace(), nscsModelInfo.getVersion(),
                    (String) ntpServerAttributes.get(NtpConstants.CPP_NTP_SERVER_ID), ntpServerAttributes);

        } catch (final Exception exception) {
            final String errorMessage = "CppConfigureNtpServerTaskHandler: " + NscsLogger.stringifyException(exception)
                    + " while creating ntpServerAttributes: " + ntpServerAttributes + "for Node: " + task.getNodeFdn();
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while creating Ntp Server MO on node.");
        }

        nscsLogger.info("NtpServer MO Successfully created with ntpServerAttributes {} " + ntpServerAttributes + " on Node : " + task.getNodeFdn());
    }

    private String serializeResult(final CppConfigureNtpServerTask task, final NtpKeyData ntpKey, final Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;

        final ObjectMapper mapper = new ObjectMapper();

        String ntpKeys = null;
        try {
            ntpKeys = mapper.writeValueAsString(ntpKey);
        } catch (final IOException ioException) {
            final String errorMessage = "CppConfigureNtpServerTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while mapping Ntp key data parameter.");
        }

        outputParams.put(WorkflowOutputParameterKeys.NTP_KEY.toString(), ntpKeys);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params in CppConfigureNtpServerTaskHandler";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while serializing Ntp key data parameter.");
        }

        final String ntpConfigSuccessMsg = "NTP configure success for Node: " + task.getNode().getFdn() + " with NtpKeyId: " + ntpKey.getId();
        nscsLogger.info(task, ntpConfigSuccessMsg);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, ntpConfigSuccessMsg);

        return encodedWfQueryTaskResult;
    }

    public Map<String, Serializable> getOutputParams(final CppConfigureNtpServerTask task) {
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters in CppConfigureNtpServerTaskHandler";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Missing configure Ntp Server out put parameters.");
        }

        return outputParams;
    }

    private Map<String, Object> prepareNtpServerConfig(final int ntpKeyId, final String ntpServerId, final String ntpServerIpAddress) {

        final Map<String, Object> ntpServerAttributes = new HashMap<>();
        ntpServerAttributes.put(NtpServer.NTP_KEY_ID, ntpKeyId);
        ntpServerAttributes.put(NtpServer.NTP_SERVER_ID, ntpServerId);
        ntpServerAttributes.put(NtpServer.SERVER_ADDRESS, ntpServerIpAddress);
        ntpServerAttributes.put(NtpServer.SERVICE_ACTIVE, true);
        ntpServerAttributes.put(NtpServer.USER_LABEL, NtpConstants.NTP_SERVER_ENM_USER_LABEL);

        return ntpServerAttributes;
    }

    private void configureNtpServer(final NormalizableNodeReference normalizableNodeRef, final CppConfigureNtpServerTask task,
            final String timeSettingFdn, final Mo ntpServerMo, final int ntpKeyId, final List<String> listOfItserviceAddresses) {
        for (final String serverAddress : listOfItserviceAddresses) {
            final Map<String, Object> ntpServerAttributes = prepareNtpServerConfig(ntpKeyId, ntpUtility.buildNtpserverIdFromEnmHostId(serverAddress),
                    serverAddress);
            createNtpServer(task, normalizableNodeRef, ntpServerAttributes, ntpServerMo, timeSettingFdn);
        }
    }

    private void reConfigureNtpServer(final NormalizableNodeReference normNodeRef, final String timeSettingFdn, final Mo ntpServerMo,
            final CppConfigureNtpServerTask task, final NtpKeyData ntpKeyData, final List<String> ntpServerAddressList) {
        final List<String> listOfItserviceAddresses = ntpUtility.getNtpServerIpAddresses(normNodeRef);
        for (final String itserviceAddresses : listOfItserviceAddresses) {
            if (!ntpServerAddressList.contains(itserviceAddresses)) {
                nscsLogger.info(task,
                        "Itservices Ip address " + itserviceAddresses + " need to configure in new ntp server on node: " + normNodeRef.getFdn());
                final Map<String, Object> ntpServerAttributes = prepareNtpServerConfig(ntpKeyData.getId(),
                        ntpUtility.buildNtpserverIdFromEnmHostId(itserviceAddresses), itserviceAddresses);
                createNtpServer(task, normNodeRef, ntpServerAttributes, ntpServerMo, timeSettingFdn);
            }
        }
    }

    private void updateOrDeleteNtpServerIfExists(final NormalizableNodeReference normNodeRef, final CppConfigureNtpServerTask task,
            final NtpKeyData ntpKeyData, final List<String> existingNtpserverList, final CmObject ntpServerCmObject) {

        nscsLogger.info("Update or delete ntp servers if already exists for ntp key id [{}] on node: [{}]", ntpKeyData.getId(), normNodeRef.getFdn());
        final Map<String, Object> ntpServerAttributes = ntpServerCmObject.getAttributes();
        final String serverAddress = (String) ntpServerAttributes.get(NtpServer.SERVER_ADDRESS);
        final String userLabel = (String) ntpServerAttributes.get(NtpServer.USER_LABEL);
        final String ntpServerId = (String) ntpServerAttributes.get(NtpServer.NTP_SERVER_ID);
        final String formattedNtpServerId = ntpUtility.buildNtpserverIdFromEnmHostId(serverAddress);
        final List<String> listOfItserviceAddresses = ntpUtility.getNtpServerIpAddresses(normNodeRef);

        nscsLogger.info("Ntp server attributes : [{}] on node: [{}]", ntpServerAttributes, normNodeRef.getFdn());
        nscsLogger.info("ntpServerId constructed from EnmHostId and serverAddress :: [{}]", formattedNtpServerId);

        if (userLabel != null && userLabel.equals(NtpConstants.NTP_SERVER_ENM_USER_LABEL) && ntpServerId.equalsIgnoreCase(formattedNtpServerId)) {
            nscsLogger.info("Updating ntp server Mo: [{}] configured through ENM internal on node : [{}].", ntpServerId, normNodeRef.getFdn());
            updateNTPServer(task, ntpKeyData, ntpServerCmObject, serverAddress);
            existingNtpserverList.add(serverAddress);
        } else {
            nscsLogger.info("Checking external ntp server MOs on node : [{}].", normNodeRef.getFdn());
            for (final String itserviceAddresses : listOfItserviceAddresses) {
                if (serverAddress != null && serverAddress.equalsIgnoreCase(itserviceAddresses)) {
                    final String deleteNtpServerMoMsg = "Deleting external ntp server: " + ntpServerId
                            + " which has internal itservices IP address : " + itserviceAddresses + " for node : " + ntpServerCmObject.getFdn();
                    nscsLogger.info(task, deleteNtpServerMoMsg);
                    writerService.deleteMo(ntpServerCmObject.getFdn());
                    nscsLogger.workFlowTaskHandlerOngoing(task,
                            "Deleted external ntp server: " + ntpServerId + " for node : " + ntpServerCmObject.getFdn());
                }
            }
        }
    }

}
