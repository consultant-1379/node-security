/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow;

import javax.ejb.*;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.DpsNodeLoader;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.cache.NscsWorkflowNodeStatusDataHandler;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;

@Stateless
public class NscsWorkflowNodeStatusDataHandlerImpl implements NscsWorkflowNodeStatusDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(NscsWorkflowNodeStatusDataHandlerImpl.class);

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    NscsNodeUtility nscsNodeUtility;

    @Inject
    NscsNodesCacheHandler nscsNodeStatusDataHandler;

    @Inject
    private DpsNodeLoader nodeLoader;

    /**
     * Update node cache status depending on workflow
     *
     * @param nodeName
     * @param workflowName
     * @param isWorkflowCompleted
     */
    @Override
    public void updateNodeCacheStatusByWorkflow(final String nodeName, final String workflowName, final boolean isWorkflowCompleted) {
        //TODO Update cache data for SL2 Activation and IPSEC disable/enable
        boolean isSL2WorkflowOperation = false;
        boolean isIPSECWorkflowOperation = false;
        String attributeToUpdate = "";
        String value = NscsNameMultipleValueResponseBuilder.OPERATION_IN_PROGRESS;

        if (WorkflowNames.WORKFLOW_CPPActivateSL2.getWorkflowName().equalsIgnoreCase(workflowName)) {
            //activating SL2
            isSL2WorkflowOperation = true;
            attributeToUpdate = NodesConfigurationStatusRecord.OPERATIONALSECURITYLEVEL_ATTR;
            value = DpsNodeLoader.normalizeSecurityLevel(NscsNameMultipleValueResponseBuilder.SL2_ACTIVATION_IN_PROGRESS);
        } else if (WorkflowNames.WORKFLOW_CPPDeactivateSL2.getWorkflowName().equalsIgnoreCase(workflowName)) {
            //deactivating SL2
            isSL2WorkflowOperation = true;
            attributeToUpdate = NodesConfigurationStatusRecord.OPERATIONALSECURITYLEVEL_ATTR;
            value = DpsNodeLoader.normalizeSecurityLevel(NscsNameMultipleValueResponseBuilder.SL2_DEACTIVATION_IN_PROGRESS);
        } else if (WorkflowNames.WORKFLOW_CPPActivateIpSec.getWorkflowName().equalsIgnoreCase(workflowName)) {
            //activating IPSEC
            isIPSECWorkflowOperation = true;
            attributeToUpdate = NodesConfigurationStatusRecord.IPSECCONFIG_ATTR;
            value = NscsNameMultipleValueResponseBuilder.IPSEC_ACTIVATION_IN_PROGRESS;
        } else if (WorkflowNames.WORKFLOW_CPPDeactivateIpSec.getWorkflowName().equalsIgnoreCase(workflowName)) {
            //deactivating IPSEC
            isIPSECWorkflowOperation = true;
            attributeToUpdate = NodesConfigurationStatusRecord.IPSECCONFIG_ATTR;
            value = NscsNameMultipleValueResponseBuilder.IPSEC_DEACTIVATION_IN_PROGRESS;
        }

        if (isSL2WorkflowOperation || isIPSECWorkflowOperation) {

            final NodesConfigurationStatusRecord record = nodeLoader.getNode(nodeName);

            if (record != null) {

                if (isWorkflowCompleted) {
                    final NodeReference nodeRef = new NodeRef(nodeName);
                    final NormalizableNodeReference normNode = readerService.getNormalizedNodeReference(nodeRef);
                    if (isSL2WorkflowOperation) {
                        //if SL2 workflow completed, then read SL status from node
                        value = nscsNodeUtility.getSecurityLevel(normNode, record.getSyncstatus());
                        record.setOperationalsecuritylevel(DpsNodeLoader.normalizeSecurityLevel(value));
                    } else if (isIPSECWorkflowOperation) {
                        //if IPSEC workflow completed, then read IPSEC config status from node
                        value = nscsNodeUtility.getIpsecConfig(normNode, record.getSyncstatus());
                        record.setIpsecconfig(value);
                    }
                    logger.info("Detected completed workflow " + workflowName + " for node " + nodeName);
                } else {
                    //workflow in progress, set the 'progress' information for SL or IPSEC
                    if (isSL2WorkflowOperation) {
                        record.setOperationalsecuritylevel(value);
                    } else if (isIPSECWorkflowOperation) {
                        record.setIpsecconfig(value);
                    }
                }

                logger.info(
                        "Updating node cache status. Node [{}], workflowName [{}], isSL2WorkflowOperation [{}], isIPSECWorkflowOperation [{}], isWorkflowCompleted [{}], attributeToUpdate [{}], value [{}]",
                        nodeName, workflowName, isSL2WorkflowOperation, isIPSECWorkflowOperation, isWorkflowCompleted, attributeToUpdate, value);

                updateCache(record);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void updateCache(final NodesConfigurationStatusRecord record) {
        nscsNodeStatusDataHandler.insertOrUpdateNode(record.getName(), record);
    }

}
