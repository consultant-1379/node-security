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
package com.ericsson.nms.security.nscs.timer.impl;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress.ActionResultType;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress.ActionStateType;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.service.proxy.NSCSServiceBeanProxy;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;

/**
 * Auxiliary class implementing a specific interval job able to monitor the
 * progress report of an asynchronous action performed on a COM/ECIM MO.
 * 
 * The check is performed for a specified number of times.
 * 
 * The Monitor is able also to inform a waiting workflow about the action
 * result.
 */
public class ComEcimAsyncActionProgressMonitor implements IntervalJobAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComEcimAsyncActionProgressMonitor.class);

    private int numOfExecutions;
    private final NormalizableNodeReference normalizableNodeRef;
    private final Mo targetMo;
    private final String moFdn;
    private final String requestedAction;
    private final String attributeToRead;
    private final NSCSServiceBeanProxy proxyService;
    private String successMessage;
    private String failureMessage;

    /**
     * The constructor from a given normalizable node reference, a given target
     * Mo, a given action, a given attribute to read, and a given NSCS Proxy
     * Service Bean (necessary to "decouple" from the management of the timer
     * the reception of any possible exceptions).
     * 
     * The number of executions (poll times) is passed too.
     * 
     * The messages that should be sent to workflow if the task execution
     * succeeded or failed can be specified too.
     * 
     * @param numOfExecutions
     *            the poll times.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param targetMo
     *            the target Mo object.
     * @param requestedAction
     *            the to be monitored action.
     * @param attributeToRead
     *            the to be read attribute.
     * @param proxyService
     *            the proxy service.
     * @param successMessage
     *            the message that should be sent to workflow if the task
     *            execution succeeds. If blank, no message will be sent to
     *            workflow.
     * @param failureMessage
     *            the message that should be sent to workflow if the task
     *            execution fails. If blank, no message will be sent to
     *            workflow.
     */
    public ComEcimAsyncActionProgressMonitor(final int numOfExecutions, final NormalizableNodeReference normalizableNodeRef, final Mo targetMo, final String requestedAction,
            final String attributeToRead, final NSCSServiceBeanProxy proxyService, final String successMessage, final String failureMessage) {

        Objects.requireNonNull(numOfExecutions, "Number of executions can't be null.");
        this.numOfExecutions = numOfExecutions;
        Objects.requireNonNull(normalizableNodeRef, "Normalizable node reference can't be null.");
        this.normalizableNodeRef = normalizableNodeRef;
        Objects.requireNonNull(targetMo, "Target Mo can't be null.");
        this.targetMo = targetMo;
        this.moFdn = null;
        Objects.requireNonNull(requestedAction, "Requested action can't be null.");
        this.requestedAction = requestedAction;
        Objects.requireNonNull(attributeToRead, "Attribute to read can't be null.");
        this.attributeToRead = attributeToRead;
        Objects.requireNonNull(proxyService, "Proxy service can't be null.");
        this.proxyService = proxyService;
        this.successMessage = successMessage;
        this.failureMessage = failureMessage;
    }

    /**
     * The constructor from a given normalizable node reference, a given MO FDN,
     * a given action, a given attribute to read, and a given NSCS Proxy Service
     * Bean (necessary to "decouple" from the management of the timer the
     * reception of any possible exceptions).
     * 
     * The number of executions (poll times) is passed too.
     * 
     * The messages that should be sent to workflow if the task execution
     * succeeded or failed can be specified too.
     * 
     * @param numOfExecutions
     *            the poll times.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param moFdn
     *            the MO FDN.
     * @param requestedAction
     *            the to be monitored action.
     * @param attributeToRead
     *            the to be read attribute.
     * @param proxyService
     *            the proxy service.
     * @param successMessage
     *            the message that should be sent to workflow if the task
     *            execution succeeds. If blank, no message will be sent to
     *            workflow.
     * @param failureMessage
     *            the message that should be sent to workflow if the task
     *            execution fails. If blank, no message will be sent to
     *            workflow.
     */
    public ComEcimAsyncActionProgressMonitor(final int numOfExecutions, final NormalizableNodeReference normalizableNodeRef, final String moFdn, final String requestedAction,
            final String attributeToRead, final NSCSServiceBeanProxy proxyService, final String successMessage, final String failureMessage) {

        Objects.requireNonNull(numOfExecutions, "Number of executions can't be null.");
        this.numOfExecutions = numOfExecutions;
        Objects.requireNonNull(normalizableNodeRef, "Normalizable node reference can't be null.");
        this.normalizableNodeRef = normalizableNodeRef;
        Objects.requireNonNull(moFdn, "MO FDN can't be null.");
        this.moFdn = moFdn;
        this.targetMo = null;
        Objects.requireNonNull(requestedAction, "Requested action can't be null.");
        this.requestedAction = requestedAction;
        Objects.requireNonNull(attributeToRead, "Attribute to read can't be null.");
        this.attributeToRead = attributeToRead;
        Objects.requireNonNull(proxyService, "Proxy service can't be null.");
        this.proxyService = proxyService;
        this.successMessage = successMessage;
        this.failureMessage = failureMessage;
    }

    @Override
    public boolean doAction(Map<JobActionParameters, Object> params) {

        LOGGER.info("ComEcim AsyncActionProgressMonitor: doAction for node [{}] action [{}] execution [{}]", this.normalizableNodeRef.getName(), this.requestedAction, this.numOfExecutions);
        final WorkflowHandler handler = (WorkflowHandler) params.get(JobActionParameters.WORKFLOW_HANDLER);
        final SystemRecorder systemRecorder = (SystemRecorder) params.get(JobActionParameters.SYSTEM_RECORDER);
        final String nodeName = this.normalizableNodeRef.getName();

        if (this.numOfExecutions <= 0) {
            String errMsg = String.format("Invalid number of executions [%s]", this.numOfExecutions);
            LOGGER.error("ComEcim AsyncActionProgressMonitor: [{}] for node [{}] action [{}]", errMsg, nodeName, this.requestedAction);
            if (this.failureMessage != null && !this.failureMessage.isEmpty()) {
                handler.dispatchMessage(this.normalizableNodeRef, this.failureMessage);
            }
            systemRecorder.recordSecurityEvent("Node Security Service - Monitoring action progress for COM ECIM node", "[" + nodeName + "] : " + errMsg, "", "NETWORK.INITIAL_NODE_ACCESS",
                    ErrorSeverity.ERROR, "COMPLETED");
            return true;
        }

        this.numOfExecutions--;

        Map<String, Object> reportProgress = reportProgressStatus();
        if (reportProgress == null) {
            String errMsg = String.format("Null action progress for node [%s] action [%s]", nodeName, this.requestedAction);
            LOGGER.error("ComEcim AsyncActionProgressMonitor: [{}]", errMsg);
            if (this.failureMessage != null && !this.failureMessage.isEmpty()) {
                handler.dispatchMessage(this.normalizableNodeRef, this.failureMessage);
            }
            systemRecorder.recordSecurityEvent("Node Security Service - Monitoring action progress for COM ECIM node", "[" + nodeName + "] : " + errMsg, "", "NETWORK.INITIAL_NODE_ACCESS",
                    ErrorSeverity.ERROR, "COMPLETED");
            return true;
        }

        ActionStateType reportProgressState = (ActionStateType) reportProgress.get(AsyncActionProgress.STATE);
        String actionName = (String) reportProgress.get(AsyncActionProgress.ACTION_NAME);
        ActionResultType actionResultType = (ActionResultType) reportProgress.get(AsyncActionProgress.RESULT);
        boolean isCancelAction = this.requestedAction.equals(MoActionWithoutParameter.ComEcim_CertM_cancel.getAction())
                || this.requestedAction.equals(MoActionWithoutParameter.ComEcim_NodeCredential_cancelEnrollment.getAction());

        if (isCancelAction || this.requestedAction.equals(actionName)) {

            switch (reportProgressState) {

            case FINISHED:
                // break intentionally omitted
            case CANCELLED:

                String message = this.failureMessage;
                String status = "ERROR";
                if (isCancelAction) {
                    if (ActionStateType.CANCELLED.equals(reportProgressState)) {
                        // cancel successfully completed
                        message = this.successMessage;
                        status = "SUCCESS";
                    }
                } else {
                    if (ActionStateType.FINISHED.equals(reportProgressState) && ActionResultType.SUCCESS.equals(actionResultType)) {
                        // action successfully completed
                        message = this.successMessage;
                        status = "SUCCESS";
                    }
                }

                String errMsg = String.format("Action for node [%s] action [%s] completed: state [%s] result [%s]", nodeName, this.requestedAction, reportProgressState.name(), status);
                LOGGER.info("[{}] - message to dispatch to WF [{}]", errMsg, message);
                if (message != null && !message.isEmpty()) {
                    try {
                        handler.dispatchMessage(this.normalizableNodeRef, message);
                    } catch (Exception e) {
                        String errorMessage = String.format("Exception [%s] msg [%s] while dispatching message [%s] to workflow for node [%s]", e.getClass().getName(), e.getMessage(), message,
                                nodeName);
                        LOGGER.error(errorMessage);
                    }
                }
                systemRecorder.recordSecurityEvent("Node Security Service - Monitoring action progress for COM ECIM node", "[" + nodeName + "] : " + errMsg, "", "NETWORK.INITIAL_NODE_ACCESS",
                        ErrorSeverity.INFORMATIONAL, "IN_PROGRESS");
                return true;

            default:

                if (this.numOfExecutions <= 0) {
                    // job fired too many times... aborting
                    String errMsg2 = String.format("Too many job retries for node [%s] action [%s]", nodeName, this.requestedAction);
                    LOGGER.error("ComEcim AsyncActionProgressMonitor: [{}]", errMsg2);
                    if (this.failureMessage != null && !this.failureMessage.isEmpty()) {
                        handler.dispatchMessage(this.normalizableNodeRef, this.failureMessage);
                    }
                    systemRecorder.recordSecurityEvent("Node Security Service - Monitoring action progress for COM ECIM node", "[" + nodeName + "] : " + errMsg2, "", "NETWORK.INITIAL_NODE_ACCESS",
                            ErrorSeverity.ERROR, "COMPLETED");
                    return true;
                }

                LOGGER.info("ComEcim AsyncActionProgressMonitor: node [{}] action [{}] in state [{}] still ongoing [{}]", nodeName, this.requestedAction, reportProgressState.name(),
                        this.numOfExecutions);
                return false;
            }
        } else {
            // Another action is set in AsyncProgressState or no action in
            // progress... error!
            String errMsg = String.format("Another action [%s] state [%s] is ongoing for node [%s], requested action [%s]", actionName, reportProgressState.name(), nodeName, this.requestedAction);
            LOGGER.error("ComEcim AsyncActionProgressMonitor: [{}]", errMsg);
            if (this.failureMessage != null && !this.failureMessage.isEmpty()) {
                handler.dispatchMessage(this.normalizableNodeRef, this.failureMessage);
            }
            systemRecorder.recordSecurityEvent("Node Security Service - Monitoring action progress for COM ECIM node", "[" + nodeName + "] : " + errMsg, "", "NETWORK.INITIAL_NODE_ACCESS",
                    ErrorSeverity.ERROR, "COMPLETED");
            return true;
        }
    }

    /**
     * Get specified asynchronous action progress for an object of given Mo
     * under a given root FDN or for an MO of given FDN.
     * 
     * @return the progress of an asynchronous action as a map of its fields.
     */
    private Map<String, Object> reportProgressStatus() {
        if (this.targetMo != null) {
            return proxyService.getAsyncActionProgressAttribute(this.attributeToRead, this.normalizableNodeRef, this.targetMo);
        } else if (this.moFdn != null) {
            return proxyService.getAsyncActionProgressAttribute(this.attributeToRead, this.moFdn);
        } else {
            LOGGER.error("Both targetMo and moFdn are NULL!!!");
            return null;
        }
    }
}
