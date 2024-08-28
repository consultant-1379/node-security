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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.workflow.task.*;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CheckNodeSyncStatusTask;

/**
 * Task handler for WorkflowTaskType.VALIDATE_NODE_FOR_NTP
 *
 * Validates the node sync status
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.CHECK_NODE_SYNC_STATUS)
@Local(WFTaskHandlerInterface.class)
public class CheckNodeSyncStatusTaskHandler implements WFQueryTaskHandler<CheckNodeSyncStatusTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NodeValidatorUtility nodeValidator;

    @Override
    public String processTask(final CheckNodeSyncStatusTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(task.getNode());
        final String nodeFdn = node.getFdn();
        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing validation for node: " + nodeFdn + ".");

            if (!nodeValidator.isNodeSynchronized(normNode)) {
                final String errorMessage = "CheckNodeSyncStatusTaskHandler: " +NscsErrorCodes.THE_NODE_SPECIFIED_IS_NOT_SYNCHRONIZED + ". Node: " + nodeFdn;
                nscsLogger.error(errorMessage);
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new WorkflowTaskFailureException(NscsErrorCodes.THE_NODE_SPECIFIED_IS_NOT_SYNCHRONIZED);
            }

        final String successMessage = "CheckNodeSyncStatusTaskHandler: Sync validation success for Node : " + nodeFdn;
        nscsLogger.info(successMessage);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return "SUCCESS";
    }



}
