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
package com.ericsson.nms.security.nscs.workflow.task.node.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithoutParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowMoActionResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.PerformMoActionTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.PERFORM_MO_ACTION. Perform specified action onto specified MOs.
 * </p>
 *
 * @author emaborz
 */
@WFTaskType(WorkflowTaskType.PERFORM_MO_ACTION)
@Local(WFTaskHandlerInterface.class)
public class PerformMoActionTaskHandler implements WFQueryTaskHandler<PerformMoActionTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moActionService;

    @Override
    public String processTask(final PerformMoActionTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        // Extract output parameters set by previous handlers.
        // They shall have been already set!
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String serializedMoActions = (String) outputParams.get(WorkflowOutputParameterKeys.MO_ACTIONS.toString());
        final WorkflowMoActions moActions = NscsObjectSerializer.readObject(serializedMoActions);
        if (moActions == null) {
            final String errorMessage = "Missing MO actions internal parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final List<WorkflowMoAction> moActionList = moActions.getTargetActions();
        if (moActionList == null || moActionList.isEmpty()) {
            final String errorMessage = "No MO actions to be performed";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Get first pending action
        final Iterator<WorkflowMoAction> it = moActionList.iterator();
        WorkflowMoAction targetMoAction = null;

        while (it.hasNext()) {
            final WorkflowMoAction currentMoAction = it.next();
            if (WorkflowMoActionState.PENDING.equals(currentMoAction.getState())) {
                targetMoAction = currentMoAction;
                break;
            } else {
                final String infoMessage = "Not pending MO action [" + currentMoAction + "] : skipping it!";
                nscsLogger.info(task, infoMessage);
            }
        }

        if (targetMoAction == null) {
            final String errorMessage = "No pending MO actions to be performed";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        String targetActionName = null;
        String actionMessage = null;
        final String moFdn = targetMoAction.getTargetMoFdn();

        if (targetMoAction instanceof WorkflowMoActionWithoutParams) {
            /**
             * MO action without parameters
             */
            final MoActionWithoutParameter targetAction = ((WorkflowMoActionWithoutParams) targetMoAction).getTargetAction();
            targetActionName = targetAction.getAction();
            actionMessage = NscsLogger.stringifyActionByFdn(moFdn, targetAction);
            nscsLogger.debug(task, "Performing " + actionMessage);
            try {
                moActionService.performMOActionByMoFdn(moFdn, targetAction);
            } catch (final Exception e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " while performing " + actionMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
        } else if (targetMoAction instanceof WorkflowMoActionWithParams) {
            /**
             * MO Action with parameters
             */
            final MoActionWithParameter targetAction = ((WorkflowMoActionWithParams) targetMoAction).getTargetAction();
            targetActionName = targetAction.getAction();
            final WorkflowMoParams targetActionParams = ((WorkflowMoActionWithParams) targetMoAction).getTargetActionParams();
            if (targetActionParams != null/*
                                           * && targetActionParams. getActionParams() != null
                                           */) {
                final MoParams targetMoActionParams = new MoParams(targetActionParams);
                actionMessage = NscsLogger.stringifyActionByFdn(moFdn, targetAction, targetMoActionParams);
                nscsLogger.debug(task, "Performing " + actionMessage);
                try {
                    moActionService.performMOActionByMoFdn(moFdn, targetAction, targetMoActionParams);
                } catch (final Exception e) {
                    final String errorMessage = NscsLogger.stringifyException(e) + " while performing " + actionMessage;
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new UnexpectedErrorException(errorMessage);
                }
            } else {
                final String errorMessage = "No MO action parameters for action [" + targetActionName + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
        } else {
            final String errorMessage = "Unknown MO action type";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully performed " + actionMessage);

        targetMoAction.setState(WorkflowMoActionState.PERFORMING_IT);

        final String result = moActionIsOngoing(task, moActions, outputParams);

        final String successMessage = "Successfully completed : action [" + targetActionName + "] state is ["
                + WorkflowMoActionState.PERFORMING_IT.name() + "]";
        final String shortDescription = String.format(NscsLogger.ACTION_PERFORMED_POLLING_PROGRESS_FORMAT, targetActionName);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, shortDescription);

        return result;
    }

    /**
     *
     * @param task
     * @param moActions
     * @param outputParams
     * @return
     */
    private String moActionIsOngoing(final PerformMoActionTask task, final WorkflowMoActions moActions,
            final Map<String, Serializable> outputParams) {
        final String result = WorkflowMoActionResult.ONGOING.name();
        return serializeResult(task, result, moActions, outputParams);
    }

    /**
     *
     * @param task
     * @param result
     * @param moActions
     * @param outputParams
     * @return
     */
    private String serializeResult(final PerformMoActionTask task, final String result, final WorkflowMoActions moActions,
            Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outputParams = new HashMap<String, Serializable>();
        }
        String serializedMoActions = null;
        try {
            serializedMoActions = NscsObjectSerializer.writeObject(moActions);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing MO actions";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTIONS.toString(), serializedMoActions);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        nscsLogger.debug(task, "Serializing result [" + result + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedWfQueryTaskResult;
    }

}
