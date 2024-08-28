/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowMoActionResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.PerformSyncMoActionTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.PERFORM_SYNC_MO_ACTION.
 * </p>
 * <p>
 * Perform specified synchronous MO action onto specified MO. Synchronous actions are actions performed by node (EOI YANG based) in a synchronous way,
 * the result is returned when the node has completed the action itself. No need to poll for the action progress report.
 * </p>
 */
@WFTaskType(WorkflowTaskType.PERFORM_SYNC_MO_ACTION)
@Local(WFTaskHandlerInterface.class)
public class PerformSyncMoActionTaskHandler implements WFQueryTaskHandler<PerformSyncMoActionTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moActionService;

    @Override
    public String processTask(final PerformSyncMoActionTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        WorkflowMoActionResult moActionResult;
        String result;
        try {
            final Map<String, Serializable> outputParams = extractOutputParamsFromTask(task);
            final WorkflowMoActions moActions = extractMoActionsFromOutputParams(outputParams);
            moActionResult = performSyncMoAction(task, moActions);
            result = serializeSyncMoActionResult(task, moActionResult.name(), moActions, outputParams);
        } catch (final Exception e) {
            final String errorMessage = e.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", moActionResult.name());
        return result;
    }

    /**
     * Extracts output parameters already set by previous task handler. They shall have been already set.
     * 
     * @param task
     *            the task.
     * @return the output parameters.
     */
    private Map<String, Serializable> extractOutputParamsFromTask(final PerformSyncMoActionTask task) {
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            throw new UnexpectedErrorException(errorMessage);
        }
        return outputParams;
    }

    /**
     * Extracts MO actions from output parameters.
     * 
     * @param outputParams
     *            the output parameters.
     * @return the MO actions.
     */
    private WorkflowMoActions extractMoActionsFromOutputParams(final Map<String, Serializable> outputParams) {
        final String serializedMoActions = (String) outputParams.get(WorkflowOutputParameterKeys.MO_ACTIONS.toString());
        final WorkflowMoActions moActions = NscsObjectSerializer.readObject(serializedMoActions);
        if (moActions == null) {
            final String errorMessage = "Missing sync MO actions internal parameter";
            throw new UnexpectedErrorException(errorMessage);
        }
        return moActions;
    }

    /**
     * Gets first (pending) synchronous MO action, executes it and exits.
     * 
     * @param task
     *            the task.
     * @param moActions
     *            the list of (pending) synchronous MO actions.
     * @return FINISHED_WITH_SUCCESS if the last sync MO action successfully performed, FINISHED_WITH_PENDING if a sync MO action successfully
     *         performed but there are still pending actions.
     */
    private WorkflowMoActionResult performSyncMoAction(final PerformSyncMoActionTask task, final WorkflowMoActions moActions) {
        final List<WorkflowMoAction> moActionList = moActions.getTargetActions();
        if (moActionList == null || moActionList.isEmpty()) {
            final String errorMessage = "No sync MO actions to be performed";
            throw new UnexpectedErrorException(errorMessage);
        }

        // Get first (pending) sync MO action
        // Only pending sync MO actions shall be present
        final Iterator<WorkflowMoAction> it = moActionList.iterator();

        while (it.hasNext()) {
            final WorkflowMoAction targetMoAction = it.next();
            if (WorkflowMoActionState.PENDING.equals(targetMoAction.getState())) {

                final String actionMessage = performSyncTargetMoAction(task, targetMoAction);

                // Remove finished sync MO action from the list
                it.remove();

                final String finishedWithSuccessActionMessage = "Removed from MO_ACTIONS successfully finished " + actionMessage;
                nscsLogger.workFlowTaskHandlerOngoing(task, finishedWithSuccessActionMessage);

                // Exit
                break;

            } else {
                final String errorMessage = String.format("Not pending sync MO action [%s]", targetMoAction);
                throw new UnexpectedErrorException(errorMessage);
            }
        }

        WorkflowMoActionResult moActionResult = null;
        if (moActionList.isEmpty()) {
            moActionResult = WorkflowMoActionResult.FINISHED_WITH_SUCCESS;
        } else {
            moActionResult = WorkflowMoActionResult.FINISHED_WITH_PENDING;
        }
        return moActionResult;
    }

    /**
     * Performs a sync target MO action.
     * 
     * @param task
     *            the task.
     * @param targetMoAction
     *            the sync target MO action.
     * @return the action message.
     */
    private String performSyncTargetMoAction(final PerformSyncMoActionTask task, final WorkflowMoAction targetMoAction) {

        String actionMessage = null;
        if (targetMoAction instanceof WorkflowMoActionWithoutParams) {
            actionMessage = performSyncMoActionWithoutParams(task, targetMoAction);
        } else if (targetMoAction instanceof WorkflowMoActionWithParams) {
            actionMessage = performSyncMoActionWithParams(task, targetMoAction);
        } else {
            final String errorMessage = "Unknown sync MO action type";
            throw new UnexpectedErrorException(errorMessage);
        }

        return actionMessage;
    }

    /**
     * Performs a sync target MO action with parameters.
     * 
     * @param task
     *            the task.
     * @param targetMoAction
     *            the sync target MO action.
     * @return the action message.
     */
    private String performSyncMoActionWithParams(final PerformSyncMoActionTask task, final WorkflowMoAction targetMoAction) {
        String actionMessage;
        final MoActionWithParameter targetAction = ((WorkflowMoActionWithParams) targetMoAction).getTargetAction();
        final String targetActionName = targetAction.getAction();
        final WorkflowMoParams targetActionParams = ((WorkflowMoActionWithParams) targetMoAction).getTargetActionParams();
        if (targetActionParams != null) {
            final MoParams targetMoActionParams = new MoParams(targetActionParams);
            final String moFdn = targetMoAction.getTargetMoFdn();
            actionMessage = NscsLogger.stringifyActionByFdn(moFdn, targetAction, targetMoActionParams);
            nscsLogger.info(task, "Performing " + actionMessage);
            try {
                moActionService.performMOActionByMoFdn(moFdn, targetAction, targetMoActionParams);
            } catch (final Exception e) {
                final String errorMessage = String.format("%s while performing %s", NscsLogger.stringifyException(e), actionMessage);
                throw new WorkflowTaskFailureException(errorMessage);
            }
        } else {
            final String errorMessage = String.format("No MO action parameters for action [%s]", targetActionName);
            throw new UnexpectedErrorException(errorMessage);
        }
        return actionMessage;
    }

    /**
     * Performs a sync target MO action without parameters.
     * 
     * @param task
     *            the task.
     * @param targetMoAction
     *            the sync target MO action.
     * @return the action message.
     */
    private String performSyncMoActionWithoutParams(final PerformSyncMoActionTask task, final WorkflowMoAction targetMoAction) {
        String actionMessage;
        final MoActionWithoutParameter targetAction = ((WorkflowMoActionWithoutParams) targetMoAction).getTargetAction();
        final String moFdn = targetMoAction.getTargetMoFdn();
        actionMessage = NscsLogger.stringifyActionByFdn(moFdn, targetAction);
        nscsLogger.info(task, "Performing " + actionMessage);
        try {
            moActionService.performMOActionByMoFdn(moFdn, targetAction);
        } catch (final Exception e) {
            final String errorMessage = String.format("%s while performing %s", NscsLogger.stringifyException(e), actionMessage);
            throw new WorkflowTaskFailureException(errorMessage);
        }
        return actionMessage;
    }

    /**
     * Prepares the return value of the task handler serializing a WorkflowQueryTaskResult containing both the result of the action performed by the
     * task handler and the output parameters.
     * 
     * @param task
     *            the task.
     * @param moActionResult
     *            the result of the action performed by the task handler.
     * @param moActions
     *            the list of (pending) synchronous MO actions.
     * @param outputParams
     *            the output parameters.
     * @return the serialized result of the task handler.
     */
    private String serializeSyncMoActionResult(final PerformSyncMoActionTask task, final String moActionResult, final WorkflowMoActions moActions,
            Map<String, Serializable> outputParams) {

        String serializedMoActions = null;
        try {
            serializedMoActions = NscsObjectSerializer.writeObject(moActions);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing MO actions";
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTIONS.toString(), serializedMoActions);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(moActionResult, outputParams);

        final String message = String.format("serializing perform sync MO action result [%s]", moActionResult);
        nscsLogger.debug(task, message);

        String encodedWfQueryTaskResult = null;
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = String.format("%s while %s", NscsLogger.stringifyException(e), message);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedWfQueryTaskResult;
    }
}
