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


package com.ericsson.nms.security.nscs.workflow.task.cpp.node.validation;

import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.validation.NodeValidationHttpsTask;

import javax.ejb.Local;
import javax.inject.Inject;


@WFTaskType(WorkflowTaskType.CPP_NODE_VALIDATION_HTTPS)
@Local(WFTaskHandlerInterface.class)
public class NodeValidationHttpsTaskHandler implements WFActionTaskHandler<NodeValidationHttpsTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NodeValidatorUtility nodeValidator;

    @Override
    public void processTask(NodeValidationHttpsTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final String nodeFdn = node.getFdn();

        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing validation for node: " + nodeFdn + ".");
        try{
            nodeValidator.validateNodeForHttpsStatus(node);
        }
        catch (InvalidNodeException ex){
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Could not find normalized reference for node " + nodeFdn + ".");
            throw ex;
        }
        catch (NodeDoesNotExistException ex){
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Could not find node " + nodeFdn + ".");
            throw new InvalidNodeException("Node does not exist. " + nodeFdn);
        }
        catch (UnsupportedNodeTypeException ex) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Node " + nodeFdn + " does not supports https commands.");
            throw new InvalidNodeException("Node does not supports https commands. " + nodeFdn);
        }
        catch (NodeNotSynchronizedException ex) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Node " + nodeFdn + " is not synchronized." );
            throw new InvalidNodeException("Node is not synchronized. " + nodeFdn);
        }
        catch (UnSupportedNodeReleaseVersionException ex){
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Unsupported Node Release Version for " + nodeFdn + ".");
            throw new InvalidNodeException("Unsupported Node Release Version for " + nodeFdn);
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Validation of the node " + nodeFdn + " finished with Success.");
    }
}