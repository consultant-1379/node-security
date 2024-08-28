/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidJobException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.InvalidAlarmServiceStateException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * This class defines the methods to validate command and nodes for setting security level.
 *
 * @author xlakdag
 *
 */
public class CppSetSecurityLevelValidator {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private Logger logger;

    /**
     * To validate the nodes for set security level
     *
     * @param inputNodesList
     *            the input nodes list
     * @param validNodesList
     *            the valid nodes list
     * @param invalidNodesErrorMap
     *            invalid node error details
     * @param currentSecurityLevels
     *            current security levels
     * @param requiredSecurityLevel
     *            required security levels
     * @param requestedEnrollmentModes
     *            requested enrollment modes
     * @return This method will return true if all the provided nodes are valid ones
     */
    public boolean validateNodes(final List<Node> inputNodesList, final List<NormalizableNodeReference> validNodesList,
            final Map<Node, NscsServiceException> invalidNodesErrorMap, final Map<String, SecurityLevel> currentSecurityLevels,
            final SecurityLevel requiredSecurityLevel, final Map<String, String> requestedEnrollmentModes) {
        nscsLogger.debug(String.format("validateNodesForSecurityLevelChange.InputNodesList: %s", inputNodesList));

        boolean havingAllValidNodes = true;
        for (final Node node : inputNodesList) {

            final String fdn = node.getNodeFdn();
            try {
                final NodeReference nodeRef = new NodeRef(fdn);
                final String enrollmentMode = node.getEnrollmentMode();

                nodeValidatorUtility.validateNode(nodeRef);

                final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
                validateNodeSupport(NscsCapabilityModelService.SECURITYLEVEL_COMMAND, normNode, enrollmentMode);

                final SecurityLevel currentSecurityLevel = getTheCurrentSecurityLevel(normNode);
                currentSecurityLevels.put(normNode.getFdn(), currentSecurityLevel);
                requestedEnrollmentModes.put(normNode.getFdn(), enrollmentMode);

                validateSecurityLevelChange(normNode, currentSecurityLevel, requiredSecurityLevel);

                validNodesList.add(normNode);

            } catch (UnassociatedNetworkElementException | NodeDoesNotExistException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | NodeNotSynchronizedException | InvalidArgumentValueException | InvalidJobException | SecurityFunctionMoNotfoundException
                    | UnsupportedNodeTypeException | InvalidAlarmServiceStateException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(node, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", node.getNodeFdn(), exc.getMessage());
            }
        }

        return havingAllValidNodes;
    }

    private void validateNodeSupport(final String command, final NormalizableNodeReference normNodeRef, final String enrollmentMode)
            throws InvalidArgumentValueException, NscsCapabilityModelException, UnsupportedNodeTypeException {

        final String neType = normNodeRef.getNeType();
        if (!nodeValidatorUtility.isNeTypeSupported(normNodeRef, command)) {
            final String errorMsg = String.format("Unsupported neType[%s]", neType);
            logger.error("NE Type validation failed: {}", errorMsg);
            throw new UnsupportedNodeTypeException().setSuggestedSolution("Check Online Help for Supported Nodes.");
        }

        final boolean isEnrollmentModeSupported = nodeValidatorUtility.isEnrollmentModeSupported(enrollmentMode, normNodeRef);
        if (!isEnrollmentModeSupported) {
            final String nodeName = normNodeRef.getName();
            final String errorMessage = String.format("Unsupported enrollment Mode[%s] for node[%s] of type[%s]", enrollmentMode, nodeName, neType);
            logger.error(errorMessage);
            throw new InvalidArgumentValueException(errorMessage);
        }

        nodeValidatorUtility.checkFmAlarmCurrentServiceState(normNodeRef);
    }

    private void validateSecurityLevelChange(final NormalizableNodeReference normNode, final SecurityLevel currentSecurityLevel,
            final SecurityLevel requiredSecurityLevel) {

        if (currentSecurityLevel.compareTo(SecurityLevel.LEVEL_NOT_SUPPORTED) == 0) {
            final String nodeName = normNode.getName();
            final String errorMessage = String.format("Node[%s] is with Security Level[%s]", nodeName, currentSecurityLevel.name());
            logger.error(errorMessage);
            throw new InvalidJobException(errorMessage).setSuggestedSolution("NA");
        }

        if (!(currentSecurityLevel.compareTo(requiredSecurityLevel) != 0)) {
            final String nodeName = normNode.getName();
            final String errorMessage = String.format("Node[%s] is already in the required Security Level[%s]", nodeName,
                    requiredSecurityLevel.name());
            logger.error(errorMessage);
            throw new InvalidJobException(errorMessage).setSuggestedSolution("NA");
        }

    }

    private SecurityLevel getTheCurrentSecurityLevel(final NormalizableNodeReference normNode) {
        SecurityLevel securityLevel = SecurityLevel.LEVEL_NOT_SUPPORTED;

        final CmResponse cmResponse = reader.getMOAttribute(normNode.getFdn(), Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL);

        if (cmResponse != null) {

            final CmObject securityMo = cmResponse.getCmObjects().iterator().next();
            final Object osLevel = securityMo.getAttributes()
                    .get(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL);
            if (osLevel != null) {
                securityLevel = SecurityLevel.valueOf(osLevel.toString());
            }
        }

        return securityLevel;
    }

}
