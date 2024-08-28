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
package com.ericsson.nms.security.nscs.cpp.seclevel.util;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NodeIsInWorkflowException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;

/**
 * This Utility class is used to validate each node before starting the workflow process.
 *
 * @author emehsau
 *
 */
public class SecurityLevelOperationNodeValidator {
    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @EServiceRef
    private WfQueryService wfQuery;

    @Inject
    NodeValidatorUtility nodeValidatorUtility;

    /**
     * This method will validate whether given node exists is synchronized, has valid IpSec MO and also whether alarm
     * supervision is enabled.
     *
     * @param nodeRef
     *             Node Reference object of a given node.
     *
     * @throws InvalidAlarmServiceStateException
     *             when the alarm current service state for the given node is invalid
     * @throws NetworkElementNotfoundException
     *             when the Network Element is not found.
     * @throws NodeDoesNotExistException
     *             when the node does not exist
     * @throws NodeNotCertifiableException
     *             when certificate cannot be generated for the given node
     * @throws NodeNotSynchronizedException
     *             when node is not synchronized.
     * @throws NscsCapabilityModelException
     *             when error occurs in NSCS Capability Model Service
     * @throws NodeIsInWorkflowException
     *             when node is already in use by other workflow.
     * @throws SecurityFunctionMoNotfoundException
     *             when SecurityFunction MO does not exist for the given node
     * @throws UnassociatedNetworkElementException
     *             when MeContext MO does not exist for the associated NetworkElement MO of the given node
     * @throws UnsupportedNodeTypeException
     *             when the given node is not supported.
     *
     * @return : {@link Boolean}
     *         <p>
     *         true: if node exists in synch, has sec level MO and alarm supervision enabled.
     *         </p>
     */
    public boolean validate(final NodeReference nodeRef) {
        nodeValidatorUtility.validateNode(nodeRef);
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        validateNodeSupportForSecurityLevel(NscsCapabilityModelService.SECURITYLEVEL_COMMAND, normNode);
        checkFmAlarmCurrentServiceState(normNode);
        if (isNodeAssociatedWithExistingWF(normNode.getNormalizedRef())) {
            logger.warn("Node [{}]  is already associated with Workflow.", normNode.getFdn());
            throw new NodeIsInWorkflowException();
        }
        return true;
    }

    /**
     * Method to check whether node exists or not.
     *
     * @param nodeRef
     *            : {@link NodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if node exists
     *         </p>
     *         false: if node doesn't exists
     */
    public boolean isNodeExists(final NodeReference nodeRef) {
        boolean isNodeExists = false;
        if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
            if (reader.exists(nodeRef.getFdn())) {
                isNodeExists = true;
            }
        }
        return isNodeExists;
    }

    /**
     * Method to check whether node has IpSec or not.
     *
     * @param nodeRef
     *            : {@link NodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if node has IpSec MO
     *         </p>
     *         false: if node doesn't has IpSec MO
     */
    public boolean isNodeHasIpSecMO(final NodeReference nodeRef) {
        logger.debug("Checking IpSec MO Node exists for node : {} ", nodeRef.getFdn());
        return reader.exists(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeRef.getName()).fdn());
    }

    /**
     * Method to check whether node is synchronized or not.
     *
     * @param normNodeRef
     *            : {@link NormalizableNodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if node is synchronized
     *         </p>
     *         false: if node is not synchronized
     */
    public boolean isNodeSynchronized(final NormalizableNodeReference normNodeRef) {
        boolean isSynch = false;
        final CmResponse response = reader.getMOAttribute(normNodeRef.getNormalizedRef(), Model.NETWORK_ELEMENT.cmFunction.type(),
                Model.NETWORK_ELEMENT.cmFunction.namespace(), CmFunction.SYNC_STATUS);
        logger.debug("Response is : {}, and size of response : {}", response, response.getCmObjects().size());
        if (response.getCmObjects().isEmpty() || response.getCmObjects().size() > 1) {
            logger.info("CmFunction MO is not configured for : {}", normNodeRef.getFdn());
        } else {
            final String status = (String) response.getCmObjects().iterator().next().getAttributes().get(CmFunction.SYNC_STATUS);
            logger.info("Node [{}] SYNC status is: [{}]", normNodeRef.getFdn(), status);
            if (CmFunction.SyncStatusValue.SYNCHRONIZED.name().equals(status)) {
                isSynch = true;
            }

        }
        return isSynch;
    }

    /**
     * Method to check whether node is associated with any workflow.
     *
     * @param nodeRef
     *            : {@link NodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if node is already associated with workflow
     *         </p>
     *         false: if node is not associated with any existing WF
     */
    public boolean isNodeAssociatedWithExistingWF(final NodeReference nodeRef) {
        logger.debug("Checking whether node [{}] participating in any other workflow", nodeRef.getFdn());
        return wfQuery.isWorkflowInProgress(nodeRef);
    }

    private void validateNodeSupportForSecurityLevel(final String command, final NormalizableNodeReference normNodeRef)
            throws InvalidArgumentValueException, NscsCapabilityModelException, UnsupportedNodeTypeException {

        if (!nodeValidatorUtility.isNeTypeSupported(normNodeRef, command)) {
            final String errorMsg = String.format("Unsupported neType[%s]", normNodeRef.getNeType());
            logger.error("NE Type validation failed: {}", errorMsg);
            throw new UnsupportedNodeTypeException().setSuggestedSolution("Check Online Help for Supported Nodes.");
        }

    }

    /**
     * Method to check whether node is having a valid alarm current service state.
     *
     * @param normNodeRef
     *             Normalizable Node Reference object of a given node.
     *
     * @throws InvalidAlarmServiceStateException
     *             when the alarm current service state for the given node is invalid
     *
     */
    private void checkFmAlarmCurrentServiceState(final NormalizableNodeReference normNodeRef) {
        nodeValidatorUtility.checkFmAlarmCurrentServiceState(normNodeRef);
    }
}
