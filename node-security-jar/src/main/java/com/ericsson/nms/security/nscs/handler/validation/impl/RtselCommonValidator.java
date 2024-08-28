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

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

/**
 * This class defines the methods to validate nodes for RTSEL.
 *
 * @author xgvgvgv
 *
 */
public class RtselCommonValidator {

    @Inject
    protected NscsLogger nscsLogger;

    @Inject
    protected NscsCMReaderService reader;

    @Inject
    protected NodeValidatorUtility nodeValidatorUtility;

    @Inject
    protected NscsCapabilityModelService nscsCapabilityModelService;

    /**
     * This method is used to validate the nodes (rtsel)
     *
     * @param normNode
     * @param nodeRef
     * @param commandType
     * @return
     * @throws InvalidNodeNameException
     * @throws NetworkElementNotfoundException
     * @throws NodeNotCertifiableException
     * @throws NodeNotSynchronizedException
     * @throws UnassociatedNetworkElementException
     * @throws UnsupportedNodeTypeException
     */

    public void validateNode(final NormalizableNodeReference normNode, final NodeReference nodeRef, final NscsCommandType commandType)
            throws InvalidNodeNameException, NetworkElementNotfoundException, NodeNotCertifiableException, NodeNotSynchronizedException,
            UnassociatedNetworkElementException, UnsupportedNodeTypeException {
        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
                if (reader.exists(nodeRef.getFdn())) {
                    nscsLogger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    throw new UnassociatedNetworkElementException();
                }
            }
            nscsLogger.error("Invalid Node Name  [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }
        nodeValidatorUtility.validateNode(nodeRef);
        validateNodeTypeForRtselCommandSupport(normNode, commandType);

    }

    private void validateNodeTypeForRtselCommandSupport(final NormalizableNodeReference normNode, final NscsCommandType commandType)
            throws UnsupportedNodeTypeException {
        boolean isCliCommandSupported = false;
        switch (commandType.name()) {
        case "RTSEL_ACTIVATE":
            // break intentionally omitted
        case "RTSEL_DEACTIVATE":
            // break intentionally omitted
        case "RTSEL_GET":
            // break intentionally omitted
        case "RTSEL_DELETE":
            isCliCommandSupported = nscsCapabilityModelService.isCliCommandSupported(normNode, NscsCapabilityModelService.RTSEL_COMMAND);
            break;

        default:
            nscsLogger.error("Invalid RTSEL command:{}", commandType);
            break;
        }
        if (!isCliCommandSupported) {
            final String errorMsg = String.format("Unsupported node type [{}] for [{}] .", normNode.getNeType(), commandType.name());
            nscsLogger.error("NE Type validation failed: {}", errorMsg);
            throw new UnsupportedNodeTypeException();
        }
    }

    public void validateEnrollmentModeSupport(final NormalizableNodeReference normNodeRef, final String enrollmentMode)
            throws InvalidArgumentValueException {
        final boolean isEnrollmentModeSupported = nodeValidatorUtility.isEnrollmentModeSupported(enrollmentMode, normNodeRef);
        if (!isEnrollmentModeSupported) {
            final String nodeName = normNodeRef.getName();
            final String errorMessage = String.format("Unsupported enrollment Mode[%s] for node[%s] of type[%s]", enrollmentMode, nodeName,
                    normNodeRef.getNeType());
            nscsLogger.error(errorMessage);
            throw new InvalidArgumentValueException(errorMessage);
        }
    }
}
