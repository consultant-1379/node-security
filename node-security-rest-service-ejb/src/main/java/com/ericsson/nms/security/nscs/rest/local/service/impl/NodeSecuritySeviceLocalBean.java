/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016

 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.rest.local.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.InvalidAlarmServiceStateException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.RequestedLevelAlreadySetException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelProcessor;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequest;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequestType;
import com.ericsson.nms.security.nscs.cpp.level.SecurityLevelProcessorFactory;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.SecurityLevelCommonUtils;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.SecurityLevelOperationNodeValidator;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.model.SecurityLevelSwitchStatus;
import com.ericsson.nms.security.nscs.rest.local.service.NodeSecuritySeviceLocal;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EPredefinedRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@Stateless
public class NodeSecuritySeviceLocalBean implements NodeSecuritySeviceLocal {

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private SecurityLevelProcessorFactory securityLevelProcessorFactory;

    @Inject
    private Logger log;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private SecurityLevelOperationNodeValidator securityLevelOperationNodeValidator;

    @Inject
    NscsNodeUtility nscsNodeUtility;

    @Inject
    SecurityLevelCommonUtils securityLevelCommonUtils;

    @Inject
    private NscsContextService nscsContextService;

    /**
     * This method is apply the Security level change initiation from Level (1 to 2) and (2 to 1)
     * 
     * @param The
     *            parameters of this method accepts two strings i.e., secLevelDTO object which contains secLevel and nodeList
     * 
     * @return a JSON which contains success or failure message
     * 
     * @throws Exception
     *             is thrown if there is any JSON processing exception when the execution process is in progress.
     */

    @Override
    @Authorize(resource = "oam", action = "execute", role = { EPredefinedRole.ADMINISTRATOR })
    public List<SecurityLevelSwitchStatus> changeSecurityLevel(final List<String> nodeNames, final SecurityLevel wantedSecLevel) {

        final List<SecurityLevelSwitchStatus> responseDTOList = new ArrayList<SecurityLevelSwitchStatus>();
        //  String enrollmentMode = null;
        final String successCodeString = "S00001";

        final List<String> nodesList = nodeNames;

        if (nodesList.size() == 0 || nodesList.isEmpty()) {
            throw new InvalidNodeException();
        } else {

            final SecLevelRequest request = new SecLevelRequest();

            Integer valid = 0;
            Integer skipped = 0;
            Integer invalid = 0;

            for (final String node : nodesList) {

                final NodeReference nodeRef = new NodeRef(node);

                final NormalizableNodeReference normNodeRef = readerService.getNormalizableNodeReference(nodeRef);

                try {
                    securityLevelOperationNodeValidator.validate(nodeRef);
                } catch (InvalidAlarmServiceStateException | NetworkElementNotfoundException | NodeDoesNotExistException | NodeNotCertifiableException
                        | NodeNotSynchronizedException | NscsCapabilityModelException | SecurityFunctionMoNotfoundException
                        | UnassociatedNetworkElementException | UnsupportedNodeTypeException exc) {

                    responseDTOList.add(new SecurityLevelSwitchStatus(nodeRef.getName().toString(), getErrorCode(exc.getErrorType().toInt()), exc
                            .getMessage()));

                    log.warn("Node [{}] has validation problem. Node is [{}]", node, exc.getMessage());
                    invalid++;
                    continue;
                }

                // Read EnrollmentMode for node
                final EnrollmentMode enrollmentMode = nscsNodeUtility.getEnrollmentMode(node);
                //   enrollmentMode = nscsCapabilityModelService.getDefaultEnrollmentMode(normNodeRef);

                log.info("Reading default EnrollmentMode from Capability Model based on node referencd[{}]", enrollmentMode.name());

                log.info("getting fdn from the NODE {} ", node);
                log.debug("getting CurrentSecurityLevel for node name {} ", nodeRef);

                final SecurityLevel currentSecurityLevel = securityLevelCommonUtils.getCurrentSecurityLevel(normNodeRef);
                log.debug("CurrentSecurityLevel for node name {} is {}", nodeRef, currentSecurityLevel.toString());

                log.info("EnrollmentMode  :: [{}]", enrollmentMode.name());
                securityLevelCommonUtils.setEnrollmentMode(enrollmentMode.name(), normNodeRef);

                request.setRequiredSecurityLevel(wantedSecLevel);
                request.setCurrentSecurityLevel(currentSecurityLevel);
                request.setNodeName(nodeRef.getName());
                request.setNodeFDN(nodeRef.getFdn());

                SecLevelRequestType requestType = null;

                try {
                    requestType = constructRequestType(request);
                } catch (RequestedLevelAlreadySetException exec) {

                    responseDTOList.add(new SecurityLevelSwitchStatus(nodeRef.getName().toString(), getErrorCode(exec.getErrorType().toInt()), exec.getMessage()));
                    log.info("CppSetSecurityLevelHandler Node {} is already in the required Security Level", node);

                    systemRecorder.recordEvent("Node Security Service - Node Security level is the same as requested", EventLevel.DETAILED,
                            "anonymous", "NETWORK.INITIAL_NODE_ACCESS", "COMPLETED");
                    skipped++;
                    continue;
                }

                responseDTOList.add(new SecurityLevelSwitchStatus(nodeRef.getName().toString(), successCodeString, "Security level switch started"));

                request.setSecLevelRequestType(requestType);

                final SecLevelProcessor levelProcessor = securityLevelProcessorFactory.createSecLevelProcessor(request);

                levelProcessor.processCommand(request);
                valid++;
            }

            nscsContextService.setNumValidItemsContextValue(valid);
            nscsContextService.setNumSkippedItemsContextValue(skipped);
            nscsContextService.setNumInvalidItemsContextValue(invalid);

            return responseDTOList;
        }
    }

    private String getErrorCode(final int errorCode) {

        String errorCodeValue = null;

        if (errorCode <= 9) {
            errorCodeValue = String.valueOf("E0000" + "" + errorCode);
        } else if (errorCode > 9 && errorCode <= 99) {
            errorCodeValue = String.valueOf("E000" + "" + errorCode);
        } else {
            errorCodeValue = String.valueOf("E00" + "" + errorCode);
        }

        return errorCodeValue;
    }

    private SecLevelRequestType constructRequestType(final SecLevelRequest secLevelRequest) throws RequestedLevelAlreadySetException {
        SecLevelRequestType requestType = null;
        if (secLevelRequest.getCurrentSecurityLevel().compareTo(secLevelRequest.getRequiredSecurityLevel()) < 0) {
            requestType = SecLevelRequestType.ACTIVATE_SECURITY_LEVEL;
        } else if (secLevelRequest.getCurrentSecurityLevel().compareTo(secLevelRequest.getRequiredSecurityLevel()) > 0) {
            requestType = SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL;
        } else {
            throw new RequestedLevelAlreadySetException();
        }
        log.debug("Request Type : " + requestType);
        return requestType;
    }
}