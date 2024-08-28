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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeMntpServer;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.model.ComEcimNtpKeyData;
import com.ericsson.nms.security.nscs.ntp.utility.NtpConfigureStatusSender;
import com.ericsson.nms.security.nscs.ntp.utility.NtpUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.helper.ComEcimNtpConfigureDetailsHelper;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimNtpConfigureDetailsTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Task handler for WorkflowTaskType.COM_ECIM_NTP_CONFIGURE
 *
 * Configures the NTP server details on the node
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_NTP_CONFIGURE)
@Local(WFTaskHandlerInterface.class)
public class ComEcimNtpConfigureDetailsTaskHandler implements WFQueryTaskHandler<ComEcimNtpConfigureDetailsTask>, WFTaskHandlerInterface {

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NscsCMReaderService nscsCmReaderService;

    @Inject
    private NtpConfigureStatusSender ntpConfigureStatusSender;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private ComEcimNtpConfigureDetailsHelper comEcimNtpConfigureDetailsHelper;

    @Inject
    private NtpUtility ntpUtility;

    boolean areAllExternalServers = false;

    boolean isInternalServerExists = false;

    List<String> ntpserverAddressList = null;

    @Override
    public String processTask(final ComEcimNtpConfigureDetailsTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference nodeRef = task.getNode();
        final NormalizableNodeReference normNodeRef = nscsCmReaderService.getNormalizableNodeReference(nodeRef);
        final Map<String, Serializable> outputParams = task.getOutputParams();
        final ComEcimNtpKeyData ntpKeyData = getNtpKeysFromOutPutParams(task);
        nscsLogger.debug("NtpKeyId from itservices [{}]", ntpKeyData.getId());

        final String mirrorRootFdn = normNodeRef.getFdn();
        final Mo rootMo = nscsCapabilityModelService.getMirrorRootMo(normNodeRef);

        if (rootMo == null) {
            final String errorMessage = "ComEcimNtpConfigureTaskHandler: RootMo is null on Node : " + task.getNodeFdn();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("RootMo does not exists on node.");
        }
        ntpserverAddressList = new ArrayList<>();
        final String ntpMoFdn = ntpUtility.getNtpMoFdn(normNodeRef);
        final Mo ntpServerMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.timeM.ntp.ntpServer;
        final Mo ntpSecurityPolicyMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.timeM.ntp.ntpSecurityPolicy;
        final NscsModelInfo nscsModelInfo = comEcimNtpConfigureDetailsHelper.getNscsModelInfo(normNodeRef, ntpServerMo, task);

        final String readMessage = NscsLogger.stringifyReadParams(normNodeRef.getFdn(), ntpServerMo.type());
        nscsLogger.info(task, "Reading [{}] ", readMessage);

        try {
            final CmResponse ntpServerCmResponse = nscsCmReaderService.getMos(mirrorRootFdn, ModelDefinition.NTP_SERVER, ntpServerMo.namespace());
            final CmResponse ntpSecurityPolicyCmResponse = nscsCmReaderService.getMos(normNodeRef.getFdn(), TimeMntpServer.NTP_SECURITY_POLICY,
                    ntpSecurityPolicyMo.namespace());

            if (ntpServerCmResponse.getCmObjects() == null || ntpServerCmResponse.getCmObjects().isEmpty()) {
                comEcimNtpConfigureDetailsHelper.configureNtp(normNodeRef, task, ntpKeyData, ntpMoFdn, ntpServerMo, ntpSecurityPolicyMo, ntpSecurityPolicyCmResponse);
            } else {
                for (CmObject ntpServerCmObject : ntpServerCmResponse.getCmObjects()) {
                    comEcimNtpConfigureDetailsHelper.updateOrDeleteNtpServerDetails(normNodeRef, task, ntpKeyData, ntpserverAddressList,
                            ntpServerCmObject, ntpSecurityPolicyCmResponse);
                }

                nscsLogger.debug(task, "List of ntp server Addresses configured on node [{}]" + ntpserverAddressList);

                if (!ntpserverAddressList.isEmpty()) {
                    reConfigureNtpServer(normNodeRef, ntpMoFdn, nscsModelInfo, ntpSecurityPolicyCmResponse, rootMo, task, ntpKeyData);
                } else {
                    comEcimNtpConfigureDetailsHelper.configureNtp(normNodeRef, task, ntpKeyData, ntpMoFdn, ntpServerMo, ntpSecurityPolicyMo, ntpSecurityPolicyCmResponse);
                }
            }
        } catch (final Exception exception) {

            final String errorMessage = NscsLogger.stringifyException(exception) + "While configuring Ntp server with keyId " + ntpKeyData.getId()
                    + " for Node " + task.getNodeFdn();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while configuring Ntp server details. " + exception.getMessage());
        }
        ntpConfigureStatusSender.sendNtpConfigureStatus(task, "SUCCESS");

        nscsLogger.info("Process task completed for ntp configuration handler");

        return serializeResult(task, ntpKeyData, outputParams);
    }

    private void reConfigureNtpServer(final NormalizableNodeReference normNodeRef, final String ntpMoFdn, final NscsModelInfo nscsModelInfo,
            final CmResponse ntpSecurityPolicyCmResponse, final Mo rootMo, final ComEcimNtpConfigureDetailsTask task,
            final ComEcimNtpKeyData ntpKeyData) {

        nscsLogger.info("Re-configure Ntp server details :: ntpKeyData [{}]", ntpKeyData.getId());
        final Mo ntpServerMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.timeM.ntp.ntpServer;
        final Mo ntpSecurityPolicyMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.timeM.ntp.ntpSecurityPolicy;
        final List<String> listOfItserviceAddresses = ntpUtility.getNtpServerIpAddresses(normNodeRef);
        for (int serverAddressPos = 0; serverAddressPos < listOfItserviceAddresses.size(); serverAddressPos++) {
            if (!ntpserverAddressList.contains(listOfItserviceAddresses.get(serverAddressPos))) {
                final String ntpSecurityPolicyFdn = comEcimNtpConfigureDetailsHelper.verifyIfNtpSecurityPolicyExists(ntpSecurityPolicyCmResponse,
                        ntpSecurityPolicyMo, normNodeRef, task, ntpKeyData, ntpMoFdn);
                comEcimNtpConfigureDetailsHelper.createNtpServerMo(listOfItserviceAddresses.get(serverAddressPos), nscsModelInfo, ntpMoFdn,
                        ntpServerMo, ntpSecurityPolicyFdn, task);
            }
        }
    }

    private ComEcimNtpKeyData getNtpKeysFromOutPutParams(final ComEcimNtpConfigureDetailsTask task) {

        ComEcimNtpKeyData ntpKeyData = null;
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing output parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String ntpKeyDetails = (String) outputParams.get(WorkflowOutputParameterKeys.NTP_KEY.toString());
        final ObjectMapper mapper = new ObjectMapper();
        try {
            ntpKeyData = mapper.readValue(ntpKeyDetails, ComEcimNtpKeyData.class);
        } catch (final IOException ioException) {
            final String errorMessage = "Failure while converting JSON to java object";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage, ioException.getCause());
        }
        return ntpKeyData;
    }

    private String serializeResult(final ComEcimNtpConfigureDetailsTask task, final ComEcimNtpKeyData ntpKey, final Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;

        final ObjectMapper mapper = new ObjectMapper();

        String ntpKeys = null;
        try {
            ntpKeys = mapper.writeValueAsString(ntpKey);
        } catch (final IOException ioException) {
            final String errorMessage = "ComEcimNtpConfigureDetailsTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        outputParams.put(WorkflowOutputParameterKeys.NTP_KEY.toString(), ntpKeys);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params in ComEcimNtpConfigureDetailsTaskHandler";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String ntpConfigSuccessMsg = "NTP configure success for Node: " + task.getNode().getFdn() + " with NtpKeyId: " + ntpKey.getId();
        nscsLogger.info(task, ntpConfigSuccessMsg);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, ntpConfigSuccessMsg);

        return encodedWfQueryTaskResult;
    }

}
