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

import com.ericsson.nms.security.nscs.api.model.NodeReference;
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimDeleteNtpSecPolicyAndNtpServerMoTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO
 * </p>
 * <p>
 * Check provided key id to verify key IDs are mapped to NTP Security Policy MO and NTP Security Policy MO is mapped to NTP Server on target nodes. If any provided key Id is mapped to NTP Security
 * Policy MO then that NTP Security Policy MO will be removed and also the NTP Server MO will be deleted. Also If the key ids belong to Ntp Server in the same environment, then list of key ids is
 * prepared to remove the mapping of Key ids and nodes from Ntp Service database .
 * </p>
 *
 * @author xvekkar
 *
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO)
@Local(WFTaskHandlerInterface.class)
public class ComEcimDeleteNtpSecPolicyAndNtpServerMoTaskHandler implements WFQueryTaskHandler<ComEcimDeleteNtpSecPolicyAndNtpServerMoTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCMWriterService nscsCMWriterService;

    @Inject
    private NtpUtility ntpUtility;

    private static final String ALL = "all";

    @Override
    public String processTask(final ComEcimDeleteNtpSecPolicyAndNtpServerMoTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        String serializedKeyIds = null;
        String encodedWfQueryTaskResult = null;
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String serializedNodeNtpKeyIdsInfo = (String) outputParams.get(WorkflowOutputParameterKeys.NTP_KEY_IDS_TO_BE_REMOVED_FROM_NODE.toString());
        final NtpKeyIdData nodeNtpKeyIdInfo = NscsObjectSerializer.readObject(serializedNodeNtpKeyIdsInfo);
        final String serializedNtpServerIdsListToBeDeleted = (String) outputParams.get(WorkflowOutputParameterKeys.NTP_SERVER_IDS_TO_BE_DELETED_FROM_NODE.toString());
        final List<String> ntpServerIdsListToBeDeleted = NscsObjectSerializer.readObject(serializedNtpServerIdsListToBeDeleted);
        List<Integer> keyIds;
        if (nodeNtpKeyIdInfo != null) {
            keyIds = getKeyIdsToRemoveMappingFromNTPServer(normNode, nodeNtpKeyIdInfo, task);
        } else {
            keyIds = getKeyIdsToRemoveMappingFromNTPServer(normNode, ntpServerIdsListToBeDeleted, task);
        }

        try {
            serializedKeyIds = NscsObjectSerializer.writeObject(keyIds);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing node key Id info to remove mapping from NTP server";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.MAPPING_TO_BE_REMOVED_FOR_KEY_IDS.toString(), serializedKeyIds);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully deleted required MO";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;

    }

    private List<Integer> getKeyIdsToRemoveMappingFromNTPServer(final NormalizableNodeReference normNode, final List<String> ntpServerIdsListToBeDeleted,
            final ComEcimDeleteNtpSecPolicyAndNtpServerMoTask task) {
        nscsLogger.info("Remove Ntp Server MO and Ntp Security Policy MO based on server id");
        final CmResponse ntpServerMOs = getNtpServerMOs(normNode, task);
        final List<Integer> keyIds = new ArrayList<>();
        final boolean areAllServersSelected = areAllServersSelected(ntpServerIdsListToBeDeleted);
        for (final CmObject ntpServerCmObj : ntpServerMOs.getCmObjects()) {
            final String ntpServerId = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.TimeMntpServer.NTP_SERVER_ID);
            if (areAllServersSelected || ntpServerIdsListToBeDeleted.contains(ntpServerId)) {
                nscsCMWriterService.deleteMo(ntpServerCmObj.getFdn());
                final CmResponse ntpSecurityPolicyMOs = getNtpSecurityPolicyMOs(normNode, task);
                deleteNtpSecurityPolicyMO(ntpSecurityPolicyMOs, ntpServerCmObj, keyIds);
            }
        }
        nscsLogger.info("mappingToBeRemovedForKeyId [{}] ", keyIds);
        return keyIds;
    }

    private void deleteNtpSecurityPolicyMO(final CmResponse ntpSecurityPolicyMOs, final CmObject ntpServerCmObj, final List<Integer> keyIds) {

        final String ntpSecurityPolicy = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.TimeMntpServer.NTP_SECURITY_POLICY);
        for (final CmObject ntpSecurityPolicyCmObj : ntpSecurityPolicyMOs.getCmObjects()) {
            final List<String> ntpServerRefs = (List<String>) ntpSecurityPolicyCmObj.getAttributes().get(ModelDefinition.NtpSecurityPolicy.NTP_SERVER_REF);
            if (ntpSecurityPolicyCmObj.getFdn().equals(ntpSecurityPolicy) && isNtpServerRefEmpty(ntpServerRefs, ntpServerCmObj)) {
                nscsCMWriterService.deleteMo(ntpSecurityPolicyCmObj.getFdn());
                if (isEnmServerId(ntpServerCmObj)) {
                    final Long ntpKeyId = (Long) ntpSecurityPolicyCmObj.getAttributes().get(ModelDefinition.NtpSecurityPolicy.KEY_ID);
                    keyIds.add(Integer.parseInt(ntpKeyId.toString()));
                }
            }
        }
    }

    private boolean isNtpServerRefEmpty(final List<String> ntpServerRefs, final CmObject ntpServerCmObj) {
        return (ntpServerRefs.isEmpty() || (ntpServerRefs.size() == 1 && ntpServerRefs.get(0).contains(ntpServerCmObj.getFdn())));
    }

    private boolean areAllServersSelected(final List<String> ntpServerIdsListToBeDeleted) {
        return (ntpServerIdsListToBeDeleted.size() == 1 && ALL.equalsIgnoreCase(ntpServerIdsListToBeDeleted.get(0).trim()));
    }

    private List<Integer> getKeyIdsToRemoveMappingFromNTPServer(final NormalizableNodeReference normNode, final NtpKeyIdData nodeNtpKeyIdInfo, final ComEcimDeleteNtpSecPolicyAndNtpServerMoTask task) {
        final CmResponse ntpServerMOs = getNtpServerMOs(normNode, task);
        final CmResponse ntpSecurityPolicyMOs = getNtpSecurityPolicyMOs(normNode, task);
        final List<Integer> keyIds = new ArrayList<>();
        for (final CmObject ntpSecurityPolicyCmObj : ntpSecurityPolicyMOs.getCmObjects()) {
            final Long ntpKeyId = (Long) ntpSecurityPolicyCmObj.getAttributes().get(ModelDefinition.NtpSecurityPolicy.KEY_ID);
            final List<String> ntpServerRefs = (List<String>) ntpSecurityPolicyCmObj.getAttributes().get(ModelDefinition.NtpSecurityPolicy.NTP_SERVER_REF);
            if (nodeNtpKeyIdInfo.getKeyIdList().isEmpty() || nodeNtpKeyIdInfo.getKeyIdList().contains(Integer.parseInt(ntpKeyId.toString()))) {
                deleteMO(ntpServerMOs, ntpSecurityPolicyCmObj.getFdn());
            }
            if (isEnmServerId(ntpServerMOs, ntpServerRefs)) {
                keyIds.add(Integer.parseInt(ntpKeyId.toString()));
            }
        }
        nscsLogger.info("mappingToBeRemovedForKeyId [{}] ", keyIds);
        return keyIds;
    }

    private boolean isEnmServerId(final CmResponse ntpServerMOs, final List<String> ntpServerRefs) {
        for (final CmObject ntpServerCmObj : ntpServerMOs.getCmObjects()) {
            if (ntpServerRefs != null && ntpServerRefs.contains(ntpServerCmObj.getFdn())) {
                return isEnmServerId(ntpServerCmObj);
            }
        }
        return false;
    }

    private boolean isEnmServerId(final CmObject ntpServerCmObj) {
        final String ntpServerId = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.TimeMntpServer.NTP_SERVER_ID);
        final String ntpServerAddress = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.SERVER_ADDRESS);
        return ntpUtility.buildNtpserverIdFromEnmHostId(ntpServerAddress).equalsIgnoreCase(ntpServerId);
    }

    private void deleteMO(final CmResponse ntpServerMOs, final String ntpSecurityPolicyFdn) {
        nscsLogger.info("Delete Ntp configured MOs");
        for (final CmObject ntpServerCmObj : ntpServerMOs.getCmObjects()) {
            final String ntpSecurityPolicy = (String) ntpServerCmObj.getAttributes().get(ModelDefinition.TimeMntpServer.NTP_SECURITY_POLICY);
            if (ntpSecurityPolicyFdn.equals(ntpSecurityPolicy)) {
                nscsCMWriterService.deleteMo(ntpServerCmObj.getFdn());
            }
        }
        nscsCMWriterService.deleteMo(ntpSecurityPolicyFdn);
    }

    private CmResponse getNtpSecurityPolicyMOs(final NormalizableNodeReference normNode, final ComEcimDeleteNtpSecPolicyAndNtpServerMoTask task) {
        final String[] requestedAttrsNtpSecurityPolicy = { ModelDefinition.NtpSecurityPolicy.KEY_ID, ModelDefinition.NtpSecurityPolicy.NTP_SERVER_REF };
        CmResponse ntpSecurityPolicyMOs = readerService.getMos(normNode.getFdn(), NtpConstants.COM_NTP_SECURITY_POLICY_MO.type(), NtpConstants.COM_NTP_SECURITY_POLICY_MO.namespace(),
                requestedAttrsNtpSecurityPolicy);

        if (ntpSecurityPolicyMOs == null || ntpSecurityPolicyMOs.getCmObjects() == null || ntpSecurityPolicyMOs.getCmObjects().isEmpty()) {
            final String errorMessage = "Failed to read NTP Security Policy MO from node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return ntpSecurityPolicyMOs;
    }

    private CmResponse getNtpServerMOs(final NormalizableNodeReference normNode, final ComEcimDeleteNtpSecPolicyAndNtpServerMoTask task) {
        final String[] requestedAttrsNtpServer = { ModelDefinition.TimeMntpServer.NTP_SERVER_ID, ModelDefinition.TimeMntpServer.NTP_SECURITY_POLICY, ModelDefinition.TimeMntpServer.SERVER_ADDRESS };
        CmResponse ntpServerMOs = readerService.getMos(normNode.getFdn(), NtpConstants.COM_NTP_SERVER_MO.type(), NtpConstants.COM_NTP_SERVER_MO.namespace(), requestedAttrsNtpServer);

        if (ntpServerMOs == null || ntpServerMOs.getCmObjects() == null || ntpServerMOs.getCmObjects().isEmpty()) {
            final String errorMessage = "Failed to read NTP Server MO from node";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        return ntpServerMOs;
    }
}