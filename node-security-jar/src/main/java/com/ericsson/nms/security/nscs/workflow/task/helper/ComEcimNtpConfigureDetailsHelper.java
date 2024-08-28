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
package com.ericsson.nms.security.nscs.workflow.task.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NtpSecurityPolicy;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeMntpServer;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.ntp.model.ComEcimNtpKeyData;
import com.ericsson.nms.security.nscs.ntp.utility.GetNtpKeyDataResponseEntity;
import com.ericsson.nms.security.nscs.ntp.utility.NtpUtility;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimNtpConfigureDetailsTask;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * Utility class to configure NTP on ComEcim nodes
 *
 * @author xkihari
 */

public class ComEcimNtpConfigureDetailsHelper {

    @Inject
    private NscsCMWriterService nscsCmWriterService;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NtpUtility ntpUtility;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCMReaderService nscsCmReaderService;

    @Inject
    private GetNtpKeyDataResponseEntity ntpKeyDataResponseEntity;

    /**
     * Update or delete NTP server details if already exists.
     *
     * @param normNodeRef
     *            the normalized node reference.
     * @param task
     *            ComEcimNtpConfigureDetails Task
     * @param ntpKeyData
     *            ComEcimNtpKey data
     * @param rootMo
     *            Mo reference
     * @param ntpserverAddressList
     *            list of existing NTP server
     * @param ntpServerCmObject
     *            CM object reference for NTP server
     * @param ntpSecrityPolicyCmResponse
     *            Cm response for NTP security policy
     *
     */
    public void updateOrDeleteNtpServerDetails(final NormalizableNodeReference normNodeRef, final ComEcimNtpConfigureDetailsTask task,
            final ComEcimNtpKeyData ntpKeyData, final List<String> ntpserverAddressList, final CmObject ntpServerCmObject,
            final CmResponse ntpSecurityPolicyCmResponse) {

        nscsLogger.info("Update or delete Ntp server details if already exists :: ntpKeyID [{}]", ntpKeyData.getId());
        final Map<String, Object> ntpServerAttributes = ntpServerCmObject.getAttributes();
        final String ntpSecurityPolicyRef = (String) ntpServerAttributes.get(TimeMntpServer.NTP_SECURITY_POLICY);
        final String serverAddress = (String) ntpServerAttributes.get(TimeMntpServer.SERVER_ADDRESS);
        final String userLabel = (String) ntpServerAttributes.get(TimeMntpServer.USER_LABEL);
        final String ntpServerId = (String) ntpServerAttributes.get(TimeMntpServer.NTP_SERVER_ID);
        final String trimmedNtpServerId = ntpUtility.getEnmHostId().substring(0, ntpUtility.getEnmHostId().indexOf('.')) + "_" + serverAddress;
        final List<String> listOfItserviceAddresses = ntpUtility.getNtpServerIpAddresses(normNodeRef);

        nscsLogger.info("ntpServerAttributes in the NTPServer :: [{}] ", ntpServerAttributes);
        nscsLogger.info("ntpServerId constructed from EnmHostId and serverAddress :: [{}] ", trimmedNtpServerId);

        if ((userLabel != null && userLabel.equals(NtpConstants.NTP_SERVER_ENM_USER_LABEL))
                && (ntpServerId != null && ntpServerId.equalsIgnoreCase(trimmedNtpServerId))
                && (ntpSecurityPolicyCmResponse != null && !ntpSecurityPolicyCmResponse.getCmObjects().isEmpty())) {

            for (final CmObject ntpSecuriyPolicyCmObjects : ntpSecurityPolicyCmResponse.getCmObjects()) {
                if (ntpSecuriyPolicyCmObjects.getFdn().equalsIgnoreCase(ntpSecurityPolicyRef)) {
                    nscsLogger.info("Updating ntpSecurityPolicy Mo configured through ENM internal");
                    updateNtpSecurityPolicy(ntpSecuriyPolicyCmObjects, ntpServerAttributes, ntpKeyData, task, ntpserverAddressList);
                }
            }
        } else {
            nscsLogger.info("Checking and deleting external ntp server and ntp security policy MOs on node : [{}].", normNodeRef.getFdn());
            checkAndDeleteExtMos(listOfItserviceAddresses, task, ntpServerCmObject, ntpSecurityPolicyCmResponse);
        }
    }

    /**
     * Configure NTP on the node
     *
     * @param normNodeRef
     *            the normalized node reference.
     * @param task
     *            ComEcimNtpConfigureDetails Task
     * @param ntpKeyData
     *            ComEcimNtpKey data
     * @param rootMo
     *            Mo reference
     * @param ntpMoFdn
     *            NTP Mo FDN
     * @param ntpServerMo
     *            NTP server Mo reference
     * @param ntpSecurityPolicyMo
     *            NTP security Policy Mo refernece
     *
     */
    public void configureNtp(final NormalizableNodeReference normNodeRef, final ComEcimNtpConfigureDetailsTask task,
            final ComEcimNtpKeyData ntpKeyData, final String ntpMoFdn, final Mo ntpServerMo, final Mo ntpSecurityPolicyMo, final CmResponse ntpSecurityPolicyCmResponse) {

        nscsLogger.info("Create NtpsecurityPolicy Mo and Ntpserver Mo : NtpKeyId [{}], ntpMoFdn[{}]", ntpKeyData.getId(), ntpMoFdn);
        final String readMessage = NscsLogger.stringifyReadParams(normNodeRef.getFdn(), ntpSecurityPolicyMo.type());
        nscsLogger.debug(task, "Reading [{}] ", readMessage);

        final List<String> listOfItserviceAddresses = ntpUtility.getNtpServerIpAddresses(normNodeRef);
        final String ntpSecurityPolicyFdn = createNtpSecurityPolicyMo(ntpSecurityPolicyMo, normNodeRef, task, ntpKeyData, ntpMoFdn, ntpSecurityPolicyCmResponse);

        final NscsModelInfo nscsModelInfo = getNscsModelInfo(normNodeRef, ntpServerMo, task);

        for (int serverAddressPos = 0; serverAddressPos < listOfItserviceAddresses.size(); serverAddressPos++) {
            createNtpServerMo(listOfItserviceAddresses.get(serverAddressPos), nscsModelInfo, ntpMoFdn, ntpServerMo, ntpSecurityPolicyFdn, task);
        }
    }

    /**
     * Get Nscs Model Info for ntp Server Mo
     *
     * @param normNodeRef
     *            the normalized node reference.
     * @param ntpServerMo
     *            NTP server Mo reference
     * @param task
     *            ComEcimNtpConfigureDetails Task
     * @return NscsModelInfo
     */
    public NscsModelInfo getNscsModelInfo(final NormalizableNodeReference normNodeRef, final Mo mo,
            final ComEcimNtpConfigureDetailsTask task) {
        try {
            return nscsModelServiceImpl.getModelInfo(normNodeRef.getTargetCategory(), normNodeRef.getNeType(), normNodeRef.getOssModelIdentity(),
                    mo.type());
        } catch (final NscsModelServiceException | IllegalArgumentException exception) {
            final String errorMessage = NscsLogger.stringifyException(exception) + " while getting model info of " + mo.type();
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException("Error while getting model information of node.");
        }
    }

    /**
     * creating NTP server MO
     *
     * @param serverAddress
     *            public Ip address of the ITservices
     * @param nscsModelInfo
     *            nscs Model Info for NTP server
     * @param ntpMoFdn
     *            NTP Mo FDN
     * @param ntpServerMo
     *            NTP server Mo reference
     * @param ntpSecurityPolicyFdn
     *            NTP security Policy Mo refernece
     * @param task
     *            ComEcimNtpConfigureDetails Task
     */
    public void createNtpServerMo(final String serverAddress, final NscsModelInfo nscsModelInfo, final String ntpMoFdn, final Mo ntpServerMo,
            final String ntpSecurityPolicyFdn, final ComEcimNtpConfigureDetailsTask task) {

        nscsLogger.info("Create Ntpserver Mo : rootMo : [{}], ntpServerMo : [{}], ntpSecurityPolicyFdn : [{}], ntpMoFdn :[{}]", ntpServerMo,
                ntpSecurityPolicyFdn, ntpMoFdn);

        final String ntpServerId = ntpUtility.buildNtpserverIdFromEnmHostId(serverAddress);
        final Map<String, Object> ntpServerAttributes = new HashMap<>();
        ntpServerAttributes.put(TimeMntpServer.ADMINISTRATIVE_STATE, NtpConstants.ADMINISTRATIVE_STATE_UNLOCKED);
        ntpServerAttributes.put(TimeMntpServer.NTP_SERVER_ID, ntpServerId);
        ntpServerAttributes.put(TimeMntpServer.SERVER_ADDRESS, serverAddress);
        ntpServerAttributes.put(TimeMntpServer.USER_LABEL, NtpConstants.NTP_SERVER_ENM_USER_LABEL);
        ntpServerAttributes.put(TimeMntpServer.NTP_SECURITY_POLICY, ntpSecurityPolicyFdn);

        nscsCmWriterService.createMo(ntpMoFdn, ntpServerMo.type(), nscsModelInfo.getNamespace(), nscsModelInfo.getVersion(), ntpServerId,
                ntpServerAttributes);

        nscsLogger.info(task, " NtpServer MO Successfully created  with Id [{}] and  ntpServerAttributes {} " + ntpServerId
                + " : " + ntpServerAttributes + " on Node : " + task.getNodeFdn());
    }

    /**
     * Check whether NtpSecurityPolicy Mo already exists on node else create and return the FDN
     *
     * @param ntpSecurityPolicyCmResponse
     *            Cm Response for NTP server policy
     * @param ntpSecurityPolicyMo
     *            ntp security policy Mo reference
     * @param normNodeRef
     *            the normalized node reference.
     * @param task
     *            ComEcimNtpConfigureDetails Task
     * @param ntpKeyData
     *            ComEcimNtpKey data
     * @param ntpMoFdn
     *            NTP Mo FDN
     * @return NTP Security Policy Fdn
     */
    public String verifyIfNtpSecurityPolicyExists(final CmResponse ntpSecurityPolicyCmResponse, final Mo ntpSecurityPolicyMo,
            final NormalizableNodeReference normNodeRef, final ComEcimNtpConfigureDetailsTask task, final ComEcimNtpKeyData ntpKeyData,
            final String ntpMoFdn) {

        nscsLogger.info("Check if NtpseucirtyPolicy already exists else create :: ntpMoFdn [{}], ntpKeyId: [{}] ", ntpMoFdn, ntpKeyData.getId());

        String ntpSecurityPolicyFdn = null;
        if (ntpSecurityPolicyCmResponse.getCmObjects() != null && !ntpSecurityPolicyCmResponse.getCmObjects().isEmpty()) {
            for (final CmObject cmObjects : ntpSecurityPolicyCmResponse.getCmObjects()) {
                ntpSecurityPolicyFdn = cmObjects.getFdn();
            }
        } else {
            ntpSecurityPolicyFdn = createNtpSecurityPolicyMo(ntpSecurityPolicyMo, normNodeRef, task, ntpKeyData, ntpMoFdn, ntpSecurityPolicyCmResponse);
        }
        return ntpSecurityPolicyFdn;
    }

    private void updateNtpSecurityPolicy(final CmObject ntpSecuriyPolicyCmObjects, final Map<String, Object> ntpServerAttributes,
            final ComEcimNtpKeyData ntpKeyData, final ComEcimNtpConfigureDetailsTask task, final List<String> ntpserverAddressList) {

        nscsLogger.info("Update NtpSecurityPolicy Mo :: ntpServerAttributes : [{}], ntpKeyId: [{}] ", ntpServerAttributes, ntpKeyData.getId());

        ntpserverAddressList.add((String) ntpServerAttributes.get(TimeMntpServer.SERVER_ADDRESS));
        updateNtpSecurityPolicyMo(task, ntpSecuriyPolicyCmObjects, ntpKeyData);
    }

    private void updateNtpSecurityPolicyMo(final ComEcimNtpConfigureDetailsTask task, final CmObject ntpSecuriyPolicyCmObjects,
            final ComEcimNtpKeyData ntpKeyData) {

        nscsLogger.debug("Updating NtpSecurityPolicy Mo ::  cmObjects [{}] for node [{}]", ntpSecuriyPolicyCmObjects,
                ntpSecuriyPolicyCmObjects.getFdn());

        final NscsCMWriterService.WriterSpecificationBuilder ntpServerSpec = nscsCmWriterService.withSpecification();
        ntpServerSpec.setFdn(ntpSecuriyPolicyCmObjects.getFdn());
        ntpServerSpec.setNotNullAttribute(NtpSecurityPolicy.KEY_ID, ntpKeyData.getId());
        ntpServerSpec.setNotNullAttribute(NtpSecurityPolicy.KEY_ALGORITHM, ntpKeyData.getDigestAlgorithm().toString());
        ntpServerSpec.setNotNullAttribute(NtpSecurityPolicy.PRE_SHARED_KEY, ntpKeyData.getKey());

        try {
            ntpServerSpec.updateMO();
        } catch (final Exception exception) {
            final String errorMessage = NscsLogger.stringifyException(exception) + " while updating NtpSecurityPolicy mo ";
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException("Error while updating NtpSecurityPolicy mo on node");
        }
        nscsLogger.info("NTPSecurityPolicy updated successfully with NTP_KEY_ID:[{}] KEY_ALGORITHM:[{}] for node [{}]", ntpKeyData.getId(),
                ntpKeyData.getDigestAlgorithm(), ntpSecuriyPolicyCmObjects.getName());
    }

    private void checkAndDeleteExtMos(final List<String> listOfItserviceAddresses, final ComEcimNtpConfigureDetailsTask task, final CmObject ntpServerCmObject, final CmResponse ntpSecurityPolicyCmResponse) {

        final Map<String, Object> ntpServerAttributes = ntpServerCmObject.getAttributes();
        final String ntpSecurityPolicyRef = (String) ntpServerAttributes.get(TimeMntpServer.NTP_SECURITY_POLICY);
        final String ntpServerFdn = ntpServerCmObject.getFdn();
        final String serverAddress = (String) ntpServerAttributes.get(TimeMntpServer.SERVER_ADDRESS);

        for (int serverAddressPos = 0; serverAddressPos < listOfItserviceAddresses.size(); serverAddressPos++) {
            if (serverAddress != null && !serverAddress.isEmpty()
                    && serverAddress.equalsIgnoreCase(listOfItserviceAddresses.get(serverAddressPos))) {
                nscsLogger.info(task," processTask : deleting external NtpServer FDN :[{}] having NtpService serverAddress ",  ntpServerCmObject.getFdn());
                nscsCmWriterService.deleteMo(ntpServerCmObject.getFdn());
                nscsLogger.info(task, " NtpServer MO has been deleted Successfully on Node Fdn [{}] ", task.getNodeFdn());
                deleteNtpSecurityPolicy(ntpSecurityPolicyCmResponse, ntpSecurityPolicyRef, ntpServerFdn );
            }
        }
    }

    private void deleteNtpSecurityPolicy(final CmResponse ntpSecurityPolicyCmResponse, final String ntpSecurityPolicyRef, final String ntpServerFdn) {
        if (ntpSecurityPolicyRef != null && ntpSecurityPolicyCmResponse != null) {
            for (final CmObject ntpSecuriyPolicyCmObject : ntpSecurityPolicyCmResponse.getCmObjects()) {
                final Map<String, Object> ntpSecurityPolicyAttributes = ntpSecuriyPolicyCmObject.getAttributes();
                final List ntpServerRefIds = (List) ntpSecurityPolicyAttributes.get("ntpServerRef");
                if (ntpServerRefIds != null && ntpServerRefIds.size() == 1 && ntpServerRefIds.iterator().next().toString().equals(ntpServerFdn)) {
                    nscsCmWriterService.deleteMo(ntpSecurityPolicyRef);
                    nscsLogger.info(" NtpSecurityPolicy Mo has been deleted Successfully ");
                } else {
                    nscsLogger.info(" Null or Multiple NtpSecurityPolicy MO exists on the node ");
                }
            }
        } else {
            nscsLogger.info(" Empty ntpSecurityPolicyCmResponse or ntpSecurityPolicyRef ");
        }
    }

    private String createNtpSecurityPolicyMo(final Mo ntpSecurityPolicyMo, final NormalizableNodeReference normNodeRef,
            final ComEcimNtpConfigureDetailsTask task, ComEcimNtpKeyData ntpKeyData, final String ntpMoFdn,
            final CmResponse ntpSecurityPolicyCmResponse) {

        nscsLogger.info("Create NtpSecurityPolicy Mo : ntpSecurityPolicyMo [{}] normNodeRef [{}] ntpKeyID [{}] ntpMoFdn[{}] ", ntpSecurityPolicyMo,
                normNodeRef, ntpKeyData.getId(), ntpMoFdn);

        final String nodeType = normNodeRef.getNeType();
        final String tMI = normNodeRef.getOssModelIdentity();
        nscsLogger.debug(task, "Reading ntpMoFdn [{}], nodeType [{}], oss model identity [{}] for node [{}]", ntpMoFdn, nodeType, tMI,
                normNodeRef.getName());

        NscsModelInfo nscsModelInfo = null;
        nscsModelInfo = getNscsModelInfo(normNodeRef, ntpSecurityPolicyMo, task);
        try {
            final Map<String, Object> ntpSecurityPolicyAttributes = new HashMap<>();
            ntpSecurityPolicyAttributes.put(NtpSecurityPolicy.KEY_ALGORITHM, ntpKeyData.getDigestAlgorithm().toString());

            long newKeyId = ntpKeyData.getId();

            List<Long> keyIdListFromNsp = new ArrayList<>();
            for (final CmObject ntpSecurityPolicyCmObj : ntpSecurityPolicyCmResponse.getCmObjects()) {
                final Map<String, Object> ntpSecurityPolicyAttributess = ntpSecurityPolicyCmObj.getAttributes();
                if (!ntpSecurityPolicyAttributess.isEmpty()) {
                    keyIdListFromNsp.add((Long) ntpSecurityPolicyAttributess.get(NtpSecurityPolicy.KEY_ID));
                }
            }
            while (keyIdListFromNsp.contains(newKeyId)) {
                ntpKeyData = getNewNTPKeyData(task);
                nscsLogger.info("New Ntp keyId [{}] from NtpService ", ntpKeyData.getId());
                newKeyId = ntpKeyData.getId();
            }

            ntpSecurityPolicyAttributes.put(NtpSecurityPolicy.KEY_ID, newKeyId);
            ntpSecurityPolicyAttributes.put(NtpSecurityPolicy.NTP_SECURITY_POLICY_ID, ntpUtility.getEnmHostId());
            ntpSecurityPolicyAttributes.put(NtpSecurityPolicy.PRE_SHARED_KEY, ntpKeyData.getKey());

            final String ntpSecurityPolicyServerId = (String) ntpSecurityPolicyAttributes.get(NtpSecurityPolicy.NTP_SECURITY_POLICY_ID);
            nscsCmWriterService.createMo(ntpMoFdn, ntpSecurityPolicyMo.type(), nscsModelInfo.getNamespace(), nscsModelInfo.getVersion(),
                    ntpSecurityPolicyServerId, ntpSecurityPolicyAttributes);

            nscsLogger.workFlowTaskHandlerOngoing(task, " Ntpsecuritypolicy mo successfully created  with id " + ntpSecurityPolicyServerId
                    + " and  ntpServerAttributes : " + ntpSecurityPolicyAttributes + " on node : " + task.getNodeFdn());

            final String ntpSecurityPolicyFdn = ntpSecurityPolicyMo.getFdnByParentFdn(ntpMoFdn, ntpSecurityPolicyServerId);
            nscsLogger.debug(task, "NtpsecurityPolicyMo Fdn [{}]", ntpSecurityPolicyFdn);

            final MoObject ntpSecurityPolicyMoObj = nscsCmReaderService.getMoObjectByFdn(ntpSecurityPolicyFdn);

            return ntpSecurityPolicyMoObj.getFdn();
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while creating NtpSecurityPolicy mo";
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException("Error while creating NtpSecurityPolicy mo");
        }
    }

    private ComEcimNtpKeyData getNewNTPKeyData(final ComEcimNtpConfigureDetailsTask task) {
        nscsLogger.info("Getting new Ntp key data from Ntpservice for Node:[{}]", task.getNodeFdn());
        String responseEntity = null;
        ComEcimNtpKeyData ntpKeyData = null;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            responseEntity = ntpKeyDataResponseEntity.getNtpKeyDataFromNtpService(task, true);
            nscsLogger.info("Get Ntp Key Data ResponseEntity {} from the http request", responseEntity);

                ntpKeyData = mapper.readValue(responseEntity, ComEcimNtpKeyData.class);
                if (ntpKeyData == null) {
                    final String errorMessage = "ComEcimNtpConfigureDetailsTask: Null NtpKeyData for Node: " + task.getNodeFdn();
                    nscsLogger.error(task, errorMessage);
                    throw new WorkflowTaskException(errorMessage);
                }

            nscsLogger.info("New Ntp key data has been generated with key id: [{}] for Node:[{}].", ntpKeyData.getId(), task.getNodeFdn());

        } catch (final Exception exception) {
            final String errorMessage = "ComEcimNtpConfigureDetailsTask: " + NscsLogger.stringifyException(exception);
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
        return ntpKeyData;
    }

}
