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

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.moaction.MoActionState;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithoutParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskTimeoutException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowMoActionResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CheckMoActionProgressTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CHECK_MO_ACTION_PROGRESS. Check progress of currently ongoing MO action.
 * </p>
 *
 * @author emaborz
 */
@WFTaskType(WorkflowTaskType.CHECK_MO_ACTION_PROGRESS)
@Local(WFTaskHandlerInterface.class)
public class CheckMoActionProgressTaskHandler implements WFQueryTaskHandler<CheckMoActionProgressTask>, WFTaskHandlerInterface {

    private static final int CANCEL_POLL_TIMES = 10;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Override
    public String processTask(final CheckMoActionProgressTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        String result = null;

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

        // Get ongoing or to be checked action
        final int moActionListSize = moActionList.size();
        final boolean areTherePendingMoActions = (moActionListSize > 1);

        nscsLogger.info(task, "MO_ACTIONS size [" + moActionListSize + "]");

        String targetActionName = null;
        MoActionState actionState = null;
        WorkflowMoActionResult moActionResult = null;
        WorkflowMoAction cancelWorkflowMoAction = null;
        String actionMessage = null;

        final Iterator<WorkflowMoAction> it = moActionList.iterator();
        while (it.hasNext()) {
            final WorkflowMoAction targetWorkflowMoAction = it.next();

            final WorkflowMoActionState targetWorkflowMoActionState = targetWorkflowMoAction.getState();
            String actionCheck = null;
            if (WorkflowMoActionState.PERFORMING_IT.equals(targetWorkflowMoActionState)) {
                actionCheck = "progress";
            } else if (WorkflowMoActionState.CHECK_IT.equals(targetWorkflowMoActionState)) {
                actionCheck = "status";
            } else {
                // Skip such item
                continue;
            }

            actionMessage = NscsLogger.stringifyAction(targetWorkflowMoAction);
            nscsLogger.info(task, "Checking " + actionCheck + " of " + actionMessage);
            final int currentRemainingIterations = targetWorkflowMoAction.getRemainingPollTimes();
            final String moFdn = targetWorkflowMoAction.getTargetMoFdn();

            /**
             * Get action state
             */
            final NodeReference nodeRef = task.getNode();
            if (targetWorkflowMoAction instanceof WorkflowMoActionWithoutParams) {
                /**
                 * MO action without parameters
                 */
                final MoActionWithoutParameter targetAction = ((WorkflowMoActionWithoutParams) targetWorkflowMoAction).getTargetAction();
                targetActionName = targetAction.getAction();
                actionState = moGetServiceFactory.getMoActionState(nodeRef, moFdn, targetAction);
            } else if (targetWorkflowMoAction instanceof WorkflowMoActionWithParams) {
                /**
                 * MO Action with parameters
                 */
                final MoActionWithParameter targetAction = ((WorkflowMoActionWithParams) targetWorkflowMoAction).getTargetAction();
                targetActionName = targetAction.getAction();
                actionState = moGetServiceFactory.getMoActionState(nodeRef, moFdn, targetAction);
            } else {
                final String errorMessage = "Unknown MO action type for " + actionMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }

            nscsLogger.info(task, "Got state [" + actionState + "] checking " + actionCheck + " of " + actionMessage);

            if (WorkflowMoActionState.PERFORMING_IT.equals(targetWorkflowMoActionState)) {
                /**
                 * Ongoing action
                 */
                targetWorkflowMoAction.setRemainingPollTimes(currentRemainingIterations - 1);
                actionMessage = NscsLogger.stringifyAction(targetWorkflowMoAction);

                switch (actionState) {
                case ONGOING:
                    if (targetWorkflowMoAction.getRemainingPollTimes() <= 0) {
                        final String errorMessage = "Exceeded max poll iterations for still ongoing " + actionMessage;
                        nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                        throw new WorkflowTaskTimeoutException(errorMessage);
                    }
                    nscsLogger.workFlowTaskHandlerOngoing(task, "Still ongoing " + actionMessage);
                    moActionResult = WorkflowMoActionResult.ONGOING;
                    result = updateMoActionProgressState(task, moActions, moActionResult, outputParams);
                    break;
                case FINISHED_WITH_SUCCESS:
                    /**
                     * Remove finished MO action from the list
                     */
                    it.remove();
                    final String finishedWithSuccessActionMessage = "Removed from MO_ACTIONS successfully finished " + actionMessage;
                    nscsLogger.workFlowTaskHandlerOngoing(task, finishedWithSuccessActionMessage);
                    if (areTherePendingMoActions) {
                        moActionResult = WorkflowMoActionResult.FINISHED_WITH_PENDING;
                    } else {
                        moActionResult = WorkflowMoActionResult.FINISHED_WITH_SUCCESS;
                    }
                    result = updateMoActionProgressState(task, moActions, moActionResult, outputParams);
                    break;
                case FINISHED_WITH_ERROR:
                    final String errorMessage = "Finished with failure " + actionMessage;
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new WorkflowTaskFailureException(errorMessage);
                case OTHER_ACTION_FINISHED:
                    final String otherActionFinishedMessage = "Other action finished while checking " + actionCheck + " of " + actionMessage;
                    final String ongoingShortMessageOnFinished = String.format(NscsLogger.OTHER_ACTION_FINISHED_FORMAT, targetActionName);
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, otherActionFinishedMessage, ongoingShortMessageOnFinished);
                    throw new UnexpectedErrorException(otherActionFinishedMessage);
                case OTHER_ACTION_ONGOING:
                    final String otherActionOngoingMessage = "Other action ongoing while checking " + actionCheck + " of " + actionMessage;
                    final String ongoingShortMessageOnOngoing = String.format(NscsLogger.OTHER_ACTION_ONGOING_FORMAT, targetActionName);
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, otherActionOngoingMessage, ongoingShortMessageOnOngoing);
                    throw new UnexpectedErrorException(otherActionOngoingMessage);
                default:
                    final String defaultErrorMessage = "Wrong action state [" + actionState + "] while checking " + actionCheck + " of "
                            + actionMessage;
                    final String finishedWithErrorShortMessage = String.format(NscsLogger.WRONG_ACTION_STATE_FORMAT, targetActionName);
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, defaultErrorMessage, finishedWithErrorShortMessage);
                    throw new UnexpectedErrorException(defaultErrorMessage);
                }
            } else if (WorkflowMoActionState.CHECK_IT.equals(targetWorkflowMoActionState)) {
                /**
                 * To be checked action
                 */
                if (MoActionState.ONGOING.equals(actionState)) {
                    /**
                     * The requested action is currently ongoing, it is necessary to cancel it before performing it again.
                     */
                    moActionResult = WorkflowMoActionResult.ONGOING;

                    // Prepare cancel action
                    if (MoActionWithParameter.ComEcim_CertM_installTrustedCertFromUri.getAction().equals(targetActionName)) {
                        cancelWorkflowMoAction = new WorkflowMoActionWithoutParams(moFdn, MoActionWithoutParameter.ComEcim_CertM_cancel,
                                CANCEL_POLL_TIMES);
                    } else if (MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment.getAction().equals(targetActionName)) {
                        cancelWorkflowMoAction = new WorkflowMoActionWithoutParams(moFdn,
                                MoActionWithoutParameter.ComEcim_NodeCredential_cancelEnrollment, CANCEL_POLL_TIMES);
                    } else {
                        final String checkItInfoMessage = "Unexpected action to check [" + targetActionName + "] while checking " + actionCheck
                                + " of " + actionMessage;
                        nscsLogger.info(task, checkItInfoMessage);
                    }
                } else {
                    /**
                     * The requested action is not currently ongoing
                     */
                    moActionResult = WorkflowMoActionResult.IDLE;
                }

                /**
                 * Remove checked MO action from the list
                 */
                it.remove();
                final String successfullyCheckedActionMessage = "Removed from MO_ACTIONS successfully checked (state [" + actionState + "]) "
                        + actionMessage;
                nscsLogger.workFlowTaskHandlerOngoing(task, successfullyCheckedActionMessage);
                result = moActionResult.name();
            }
            break;
        }

        if (targetActionName == null) {
            final String errorMessage = "No ongoing or to be checked MO action";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        if (cancelWorkflowMoAction != null) {
            moActions.addTargetAction(cancelWorkflowMoAction);
            final String addCancelActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(cancelWorkflowMoAction);
            final String cancelAction = ((WorkflowMoActionWithoutParams) cancelWorkflowMoAction).getTargetAction().getAction();
            final String ongoingShortMessage = String.format(NscsLogger.GOING_TO_EXECUTE_ACTION_FORMAT, cancelAction);
            nscsLogger.workFlowTaskHandlerOngoing(task, addCancelActionMessage, ongoingShortMessage);
            result = updateMoActionProgressState(task, moActions, moActionResult, outputParams);
        }

        final String successMessage = "Successfully completed check of " + actionMessage + "] : state is [" + moActionResult + "]";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);

        return result;
    }

    /**
     *
     * @param task
     * @param moActions
     * @param moActionResult
     * @param outputParams
     * @return
     */
    private String updateMoActionProgressState(final CheckMoActionProgressTask task, final WorkflowMoActions moActions,
            final WorkflowMoActionResult moActionResult, final Map<String, Serializable> outputParams) {
        final String result = moActionResult.name();
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
    private String serializeResult(final CheckMoActionProgressTask task, final String result, final WorkflowMoActions moActions,
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
