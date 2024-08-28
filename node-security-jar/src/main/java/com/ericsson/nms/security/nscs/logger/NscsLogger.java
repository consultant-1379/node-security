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
package com.ericsson.nms.security.nscs.logger;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithoutParams;
import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkFlowNodeTask;
import com.ericsson.oss.services.security.nscs.command.CommandHandlerStatsFormatter;
import com.ericsson.oss.services.security.nscs.command.EventDataCommandIdentifier;
import com.ericsson.oss.services.security.nscs.command.util.NscsCommandConstants;
import com.ericsson.oss.services.security.nscs.command.util.NscsCommandHelper;

/**
 * Auxiliary class to manage logger and system recorder in NSCS
 *
 */
public class NscsLogger {

    public static final String NOT_SUPPORTED = "not supported";
    public static final String NOT_VALID = "not valid";
    public static final String ALREADY_INSTALLED = "already installed";
    public static final String DO_NOT_REMOVE = "do not remove";
    public static final String ENABLE_DISABLE_FAILED = "enable/disable failed";
    public static final String REMOVE_FAILED = "remove failed";
    public static final String CRL_DOWNLOAD_INTERVAL_NOT_UPDATED = "CRL downlaod interval not updated";
    public static final String ACTION_PERFORMED_POLLING_PROGRESS_FORMAT = "%s performed. Polling progress";
    public static final String ACTION_PERFORMED_WAITING_EVENT_FORMAT = "%s performed. Waiting event";
    public static final String OTHER_ACTION_FINISHED_FORMAT = "finished action other than %s";
    public static final String OTHER_ACTION_ONGOING_FORMAT = "ongoing action other than %s";
    public static final String WRONG_ACTION_STATE_FORMAT = "wrong state for action %s";
    public static final String GOING_TO_EXECUTE_ACTION_FORMAT = "going to execute action %s";

    @Inject
    private Logger logger;

    @Inject
    private NscsSystemRecorder nscsSystemRecorder;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private NscsCompactAuditLogger nscsCompactAuditLogger;

    @Inject
    private NscsRemoteEjbLogger nscsRemoteEjbLogger;

    private CommandHandlerStatsFormatter commandHandlerStatsFormatter;

    /**
     * Common constants
     */
    private static final String NSCS_TAG = "[NSCS]";
    private static final String NSCS_SOURCE = "Node Security Service";
    private static final String NSCS_RESOURCE = "Node Security Service";

    /**
     * Handlers
     */
    private static final String CMD_HANDLER = "Command Handler";
    private static final String WF_TASK_HANDLER = "WorkFlow Task Handler";
    private static final String WF_HANDLER = "WorkFlow Handler";

    /**
     * Log an NSCS debug message.
     *
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void debug(final String format, final Object... args) {
        final String message = NSCS_TAG + " : " + format;
        logger.debug(message, args);
    }

    /**
     * Log an NSCS info message.
     *
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void info(final String format, final Object... args) {
        final String message = NSCS_TAG + " : " + format;
        logger.info(message, args);
    }

    /**
     * Log an NSCS warn message.
     *
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void warn(final String format, final Object... args) {
        final String message = NSCS_TAG + " : " + format;
        logger.warn(message, args);
    }

    /**
     * Log an NSCS error message.
     *
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void error(final String format, final Object... args) {
        final String message = NSCS_TAG + " : " + format;
        logger.error(message, args);
    }

    /**
     * Log an NSCS error message.
     *
     * @param e
     *            the throwable.
     * @param errorMessage
     *            the errorMessage.
     */
    public void error(final Throwable e, final String errorMessage) {
        final String message = NSCS_TAG + " : " + errorMessage;
        logger.error(message, e);
    }

    /**
     * Log an NSCS trace message.
     *
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void trace(final String format, final Object... args) {
        final String message = NSCS_TAG + " : " + format;
        logger.trace(message, args);
    }

    /**
     * Log an NSCS workflow task handler debug message.
     *
     * @param task
     *            the task
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void debug(final WorkFlowNodeTask task, final String format, final Object... args) {
        if (task == null) {
            this.debug(format, args);
            return;
        }
        final String taskName = task.getClass().getSimpleName();
        final String nodeName = task.getNode().getName();
        final StringBuilder sb = new StringBuilder();
        final String message = format != null
                ? (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("] : ").append(format).toString())
                : (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("]").toString());
        if (args != null && args.length > 0) {
            logger.debug(message, args);
        } else {
            logger.debug(message);
        }
    }

    /**
     * Log an NSCS workflow task handler info message.
     *
     * @param task
     *            the task
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void info(final WorkFlowNodeTask task, final String format, final Object... args) {
        if (task == null) {
            this.info(format, args);
            return;
        }
        final String taskName = task.getClass().getSimpleName();
        final String nodeName = task.getNode().getName();
        final StringBuilder sb = new StringBuilder();
        final String message = format != null
                ? (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("] : ").append(format).toString())
                : (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("]").toString());
        if (args != null && args.length > 0) {
            logger.info(message, args);
        } else {
            logger.info(message);
        }
    }

    /**
     * Log an NSCS workflow task handler error message.
     *
     * @param task
     *            the task
     * @param format
     *            the format
     * @param args
     *            the args
     */
    public void error(final WorkFlowNodeTask task, final String format, final Object... args) {
        if (task == null) {
            this.error(format, args);
            return;
        }
        final String taskName = task.getClass().getSimpleName();
        final String nodeName = task.getNode().getName();
        final StringBuilder sb = new StringBuilder();
        final String message = format != null
                ? (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("] : ").append(format).toString())
                : (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("]").toString());
        if (args != null && args.length > 0) {
            logger.error(message, args);
        } else {
            logger.error(message);
        }
    }

    /**
     * Log an NSCS workflow task handler error message.
     *
     * @param task
     *            the task
     * @param e
     *            the throwable
     * @param errorMessge
     *            the errorMessge
     */
    public void error(final WorkFlowNodeTask task, final Throwable e, final String errorMessge) {
        if (task == null) {
            this.error(e, errorMessge);
            return;
        }
        final String taskName = task.getClass().getSimpleName();
        final String nodeName = task.getNode().getName();
        final StringBuilder sb = new StringBuilder();
        final String message = errorMessge != null
                ? (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("] : ").append(errorMessge).toString())
                : (sb.append("[").append(taskName).append("] : node [").append(nodeName).append("]").toString());
        this.error(e, message);
    }

    /**
     * Log and record a CLI command start.
     * 
     * If the command parsing fails, only a generic command text shall be logged to avoid unwanted logging of sensitive data (passwords and keys).
     * 
     * @param prefix
     *            the prefix.
     * @param command
     *            the command or null for command with syntax error.
     */
    public void commandStarted(final String prefix, final NscsCliCommand command) {
        final String commandName = String.format("%s : command type [%s]", CMD_HANDLER, NscsPropertyCommand.COMMAND_TYPE_PROPERTY);
        final CommandPhase commandPhase = CommandPhase.STARTED;
        final String obfuscatedCommandText = command != null ? NscsCommandHelper.obfuscateCommandText(command.getCommandText())
                : NscsCommandConstants.GENERIC_SECADM_COMMAND_BODY;
        final String source = String.format("%s : command invoked [%s %s]", NscsPropertyCommand.NscsPropertyCommandInvoker.CLI.toString(), prefix,
                obfuscatedCommandText);
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("command properties [%s]", command != null ? command.getProperties().toString() : null);
        /**
         * TAF is parsing the following command logger and TAF team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Log and record a CLI command end with success.
     * 
     * @param prefix
     *            the prefix.
     * @param command
     *            the command.
     * @param responseMsg
     *            the responseMsg.
     */
    public void commandFinishedWithSuccess(final String prefix, final NscsCliCommand command, final String responseMsg) {
        final String commandName = String.format("%s : command type [%s]", CMD_HANDLER, NscsPropertyCommand.COMMAND_TYPE_PROPERTY);
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
        final String obfuscatedCommandText = NscsCommandHelper.obfuscateCommandText(command.getCommandText());
        final String source = String.format("%s : command invoked [%s %s]", NscsPropertyCommand.NscsPropertyCommandInvoker.CLI.toString(), prefix,
                obfuscatedCommandText);
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("response [%s]", responseMsg);
        /**
         * Customers are parsing the following compact audit log and they need to be notified of changes.
         */
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit(prefix, obfuscatedCommandText);
        /**
         * TAF is parsing the following command logger and TAF team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Log and record a CLI command end with error.
     * 
     * If the command parsing fails, only a generic command text shall be logged to avoid unwanted logging of sensitive data (passwords and keys).
     *
     * @param prefix
     *            the prefix.
     * @param command
     *            the command or null for command with syntax error.
     * @param errorMsg
     *            the error message.
     */
    public void commandFinishedWithError(final String prefix, final NscsCliCommand command, final String errorMsg) {
        final String commandName = String.format("%s : command type [%s]", CMD_HANDLER, NscsPropertyCommand.COMMAND_TYPE_PROPERTY);
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_ERROR;
        final String obfuscatedCommandText = command != null ? NscsCommandHelper.obfuscateCommandText(command.getCommandText())
                : NscsCommandConstants.GENERIC_SECADM_COMMAND_BODY;
        final String source = String.format("%s : command invoked [%s %s]", NscsPropertyCommand.NscsPropertyCommandInvoker.CLI.toString(), prefix,
                obfuscatedCommandText);
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("response [%s]", errorMsg);
        /**
         * Customers are parsing the following compact audit log and they need to be notified of changes.
         */
        nscsCompactAuditLogger.recordCommandFinishedWithErrorCompactAudit(prefix, obfuscatedCommandText, errorMsg);
        /**
         * TAF is parsing the following command logger and TAF team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Log and record a REST end with success.
     */
    public void restFinishedWithSuccess() {
        /**
         * Customers are parsing the following compact audit log and they need to be notified of changes.
         */
        nscsCompactAuditLogger.recordRestFinishedWithSuccessCompactAudit();
    }

    /**
     * Log and record a REST end with error set in the context.
     */
    public void restFinishedWithError() {
        /**
         * Customers are parsing the following compact audit log and they need to be notified of changes.
         */
        nscsCompactAuditLogger.recordRestFinishedWithErrorCompactAudit();
    }

    /**
     * Log and record an NSCS command handler start.
     *
     * @param command
     *            the command
     */
    public void commandHandlerStarted(final NscsPropertyCommand command) {
        commandHandlerStatsFormatter = new CommandHandlerStatsFormatter();
        final String commandName = String.format("%s : command type [%s]", CMD_HANDLER, command.getCommandType().toString());
        final CommandPhase commandPhase = CommandPhase.STARTED;
        final String source = command.getCommandInvokerValue().toString();
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("command properties [%s]", command.getProperties());
        /**
         * TAF is parsing the following command logger and TAF team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Log and record an NSCS command handler ongoing.
     *
     * @param command
     *            the command
     * @param additionalInfo
     *            the additionalInfo
     */
    public void commandHandlerOngoing(final NscsPropertyCommand command, final String additionalInfo) {
        final String commandName = String.format("%s : command type [%s]", CMD_HANDLER, command.getCommandType().toString());
        final CommandPhase commandPhase = CommandPhase.ONGOING;
        final String source = command.getCommandInvokerValue().toString();
        final String resource = NSCS_RESOURCE;
        /**
         * TAF is parsing the following command logger and TAF team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Log and record an NSCS command handler end with success.
     *
     * @param command
     *            the command
     * @param responseMsg
     *            the responseMsg
     */
    public void commandHandlerFinishedWithSuccess(final NscsPropertyCommand command, final String responseMsg) {
        final String commandType = command.getCommandType().toString();
        if (commandHandlerStatsFormatter == null) {
            logger.warn("Unbalanced {} log for [{}]. Missing commandHandlerStarted invocation.", CMD_HANDLER, commandType);
        }
        final String commandName = String.format("%s : command type [%s]", CMD_HANDLER, commandType);
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
        final String source = command.getCommandInvokerValue().toString();
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("response [%s]", responseMsg);
        /**
         * DDP is parsing the following event data and DDP team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommandHandlerCompletedEvent(commandHandlerStatsFormatter);
        /**
         * TAF is parsing the following command logger and TAF team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Log and record an NSCS command handler end with error.
     *
     * @param command
     *            the command
     * @param responseMsg
     *            the responseMsg
     */
    public void commandHandlerFinishedWithError(final NscsPropertyCommand command, final String responseMsg) {
        final String commandType = command.getCommandType().toString();
        if (commandHandlerStatsFormatter == null) {
            logger.warn("Unbalanced {} log for [{}]. Missing commandHandlerStarted invocation.", CMD_HANDLER, commandType);
        }
        final String commandName = String.format("%s : command type [%s]", CMD_HANDLER, commandType);
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_ERROR;
        final String source = command.getCommandInvokerValue().toString();
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("response [%s]", responseMsg);
        /**
         * TAF is parsing the following command logger and TAF team needs to be notified of changes
         */
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Update the NSCS command handler statistics formatter.
     * 
     * @param commandId
     *            the command identifier if SDK Event Data required or null if SKK EVent Data not required.
     * @param numItems
     *            the total number of items on which the command has been performed.
     * @param numSuccessItems
     *            the number of valid items on which the command has been successfully performed.
     * @param numErrorItems
     *            the number of valid items on which the command has failed.
     */
    public void updateCommandHandlerStatsFormatter(final EventDataCommandIdentifier commandId, final Integer numItems,
            final Integer numSuccessItems, final Integer numErrorItems) {
        commandHandlerStatsFormatter.setCommandId(commandId.name());
        commandHandlerStatsFormatter.setNumItems(numItems);
        commandHandlerStatsFormatter.setNumSuccessItems(numSuccessItems);
        commandHandlerStatsFormatter.setNumErrorItems(numErrorItems);
    }

    /**
     * Log and record an NSCS workflow start.
     *
     * @param workflowName
     *            the workflowName
     * @param workflowInstanceId
     *            the workflowInstanceId
     * @param nodeKey
     *            the nodeKey
     * @param additionalInfo
     *            the additionalInfo
     */
    public void workFlowStarted(final String workflowName, final String workflowInstanceId, final String nodeKey, final String additionalInfo) {
        final String commandName = WF_HANDLER + " [" + workflowName + "]";
        final CommandPhase commandPhase = CommandPhase.STARTED;
        final String source = NSCS_SOURCE;
        final String resource = "node [" + nodeKey + "]";
        final String extendedInfo = "workflow id [" + workflowInstanceId + "] : " + additionalInfo;
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, extendedInfo);
    }

    /**
     * Log and record an NSCS workflow end with success.
     *
     * @param workflowName
     *            the workflowName
     * @param workflowInstanceId
     *            the workflowInstanceId
     * @param nodeKey
     *            the nodeKey
     * @param additionalInfo
     *            the additionalInfo
     */
    public void workFlowFinishedWithSuccess(final String workflowName, final String workflowInstanceId, final String nodeKey,
            final String additionalInfo) {
        final String commandName = WF_HANDLER + " [" + workflowName + "]";
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
        final String source = NSCS_SOURCE;
        final String resource = "node [" + nodeKey + "]";
        final String extendedInfo = "workflow id [" + workflowInstanceId + "] : " + additionalInfo;
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, extendedInfo);
    }

    /**
     * Log and record an NSCS workflow end with error.
     *
     * @param workflowName
     *            the workflowName
     * @param workflowInstanceId
     *            the workflowInstanceId
     * @param nodeKey
     *            the nodeKey
     * @param additionalInfo
     *            the additionalInfo
     */
    public void workFlowFinishedWithError(final String workflowName, final String workflowInstanceId, final String nodeKey,
            final String additionalInfo) {
        final String commandName = WF_HANDLER + " [" + workflowName + "]";
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_ERROR;
        final String source = NSCS_SOURCE;
        final String resource = "node [" + nodeKey + "]";
        final String extendedInfo = "workflow id [" + workflowInstanceId + "] : " + additionalInfo;
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, extendedInfo);
    }

    /**
     * Log and record an NSCS workflow task handler start.
     *
     * @param task
     *            the task
     */
    public void workFlowTaskHandlerStarted(final WorkFlowNodeTask task) {
        final String taskName = task.getClass().getSimpleName();
        final String commandName = WF_TASK_HANDLER + " [" + taskName + "]";
        final CommandPhase commandPhase = CommandPhase.STARTED;
        final String source = NSCS_SOURCE;
        final String resource = "node [" + task.getNode().getName() + "]";
        final String workflowName = task.getWorkflowDefinitionId();
        final String workflowInstanceId = task.getWorkflowInstanceId();
        final String wfWakeId = task.getWfWakeId();
        final String additionalInfo = "task params [" + task.stringify().toString() + "] : workflow name [" + workflowName + "] : workflow id ["
                + workflowInstanceId + "]";
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);

        if (wfWakeId != null) {
            final WfResult wfResult = cacheHandler.getWfResult(UUID.fromString(wfWakeId));
            if (wfResult != null) {
                final String taskShortDescription = task.getShortDescription();
                addWorkflowDetailsChunk(wfResult, taskShortDescription);
            }
            cacheHandler.updateWorkflowOnly(wfResult);
        }
    }

    /**
     * Log and record an NSCS workflow task handler ongoing.
     *
     * @param task
     *            the task
     * @param additionalInfo
     *            the additionalInfo
     */
    public void workFlowTaskHandlerOngoing(final WorkFlowNodeTask task, final String additionalInfo) {
        workFlowTaskHandlerOngoing(task, additionalInfo, null);
    }

    /**
     * Log and record an NSCS workflow task handler ongoing logging also the given result.
     *
     * @param task
     *            the task
     * @param additionalInfo
     *            the additionalInfo
     * @param result
     *            the result
     */
    public void workFlowTaskHandlerOngoing(final WorkFlowNodeTask task, final String additionalInfo, final String result) {
        final String taskName = task.getClass().getSimpleName();
        final String commandName = WF_TASK_HANDLER + " [" + taskName + "]";
        final CommandPhase commandPhase = CommandPhase.ONGOING;
        final String source = NSCS_SOURCE;
        final String resource = "node [" + task.getNode().getName() + "]";
        final String workflowName = task.getWorkflowDefinitionId();
        final String workflowInstanceId = task.getWorkflowInstanceId();
        final String wfWakeId = task.getWfWakeId();
        final String extendedInfo = "workflow name [" + workflowName + "] : workflow id [" + workflowInstanceId + "] : " + additionalInfo;
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, extendedInfo);

        if (wfWakeId != null) {
            final WfResult wfResult = cacheHandler.getWfResult(UUID.fromString(wfWakeId));
            if (wfResult != null) {
                final String taskShortDescription = task.getShortDescription();
                String replacement = "[" + taskShortDescription + " ... ongoing]";
                if (result != null) {
                    replacement = "[" + taskShortDescription + " ... " + result.replaceAll("\\.\\.\\.", "") + "]";
                }
                updateLastWorkflowDetailsChunk(wfResult, taskShortDescription, replacement);
            }
            cacheHandler.updateWorkflowOnly(wfResult);
        }
    }

    public void workFlowTaskHandlerFinishedWithSuccess(final WorkFlowNodeTask task, final String additionalInfo) {
        workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, null, null);
    }

    public void workFlowTaskHandlerFinishedWithSuccess(final WorkFlowNodeTask task, final String additionalInfo,
            final Map<String, Object> additionalResults) {
        workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, null, additionalResults);
    }

    public void workFlowTaskHandlerFinishedWithSuccess(final WorkFlowNodeTask task, final String additionalInfo, final String result) {
        workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, result, null);
    }

    /**
     * Log and record an NSCS workflow task handler end with success logging also the given result.
     *
     * @param task
     *            the task
     * @param additionalInfo
     *            the additionalInfo
     * @param additionalResults
     *            the additionalResults
     * @param result
     *            the result
     */
    public void workFlowTaskHandlerFinishedWithSuccess(final WorkFlowNodeTask task, final String additionalInfo, final String result,
            final Map<String, Object> additionalResults) {

        final String taskName = task.getClass().getSimpleName();
        final String commandName = WF_TASK_HANDLER + " [" + taskName + "]";
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
        final String source = NSCS_SOURCE;
        final String resource = "node [" + task.getNode().getName() + "]";
        final String workflowName = task.getWorkflowDefinitionId();
        final String workflowInstanceId = task.getWorkflowInstanceId();
        final String wfWakeId = task.getWfWakeId();
        final String extendedInfo = "workflow name [" + workflowName + "] : workflow id [" + workflowInstanceId + "] : " + additionalInfo;
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, extendedInfo);

        if (wfWakeId != null) {
            final WfResult wfResult = cacheHandler.getWfResult(UUID.fromString(wfWakeId));

            if (additionalResults != null && !additionalResults.isEmpty()) {
                wfResult.setWfParams(additionalResults);
            }

            if (wfResult != null) {
                final String taskShortDescription = task.getShortDescription();
                String replacement = "";
                if (result != null) {
                    replacement = "[" + taskShortDescription + ": " + result.replaceAll("\\.\\.\\.", "") + "]";
                }
                updateLastWorkflowDetailsChunk(wfResult, taskShortDescription, replacement);
            }
            cacheHandler.updateWorkflowOnly(wfResult);
        }
    }

    /**
     * Log and record an NSCS workflow task handler end with error.
     *
     * @param task
     *            the task
     * @param additionalInfo
     *            the additionalInfo
     */
    public void workFlowTaskHandlerFinishedWithError(final WorkFlowNodeTask task, final String additionalInfo) {
        workFlowTaskHandlerFinishedWithError(task, additionalInfo, null);

    }

    /**
     * Log and record an NSCS workflow task handler end with error logging also the given result.
     *
     * @param task
     *            the task
     * @param additionalInfo
     *            the additionalInfo
     * @param result
     *            the result
     */
    public void workFlowTaskHandlerFinishedWithError(final WorkFlowNodeTask task, final String additionalInfo, final String result) {
        final String taskName = task.getClass().getSimpleName();
        final String commandName = WF_TASK_HANDLER + " [" + taskName + "]";
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_ERROR;
        final String source = NSCS_SOURCE;
        final String resource = "node [" + task.getNode().getName() + "]";
        final String workflowName = task.getWorkflowDefinitionId();
        final String workflowInstanceId = task.getWorkflowInstanceId();
        final String wfWakeId = task.getWfWakeId();
        final String extendedInfo = "workflow name [" + workflowName + "] : workflow id [" + workflowInstanceId + "] : " + additionalInfo;
        nscsSystemRecorder.recordCommand(commandName, commandPhase, source, resource, extendedInfo);

        if (wfWakeId != null) {
            final WfResult wfResult = cacheHandler.getWfResult(UUID.fromString(wfWakeId));
            if (wfResult != null) {
                final String taskShortDescription = task.getShortDescription();
                String replacement = "[" + taskShortDescription + " failed]";
                if (result != null) {
                    replacement = "[" + taskShortDescription + " failed: " + result.replaceAll("\\.\\.\\.", "") + "]";
                }
                updateLastWorkflowDetailsChunk(wfResult, taskShortDescription, replacement);
            }
            cacheHandler.updateWorkflowOnly(wfResult);
        }
    }

    /**
     * @param wfResult
     *            the wfResult
     * @param taskShortDescription
     *            the taskShortDescription
     */
    public void addWorkflowDetailsChunk(final WfResult wfResult, final String taskShortDescription) {
        final String message = wfResult.getMessage();
        wfResult.setMessage((message != null && !message.isEmpty() ? (message + "[") : ("[")) + taskShortDescription + " ... ]");
    }

    /**
     * @param wfResult
     *            the wfResult
     * @param taskShortDescription
     *            the taskShortDescription
     * @param replacement
     *            the replacement
     */
    public void updateLastWorkflowDetailsChunk(final WfResult wfResult, final String taskShortDescription, final String replacement) {
        String message = wfResult.getMessage();
        if (message != null) {
            final Matcher matcher = Pattern.compile("\\[" + taskShortDescription + "\\s*\\.\\.\\..*\\]$").matcher(message);
            message = matcher.replaceFirst(Matcher.quoteReplacement(replacement));
            wfResult.setMessage(message);
        }
    }

    /**
     * Log and record a remote EJB invocation.
     */
    public void remoteEjbStarted() {
        nscsRemoteEjbLogger.recordRemoteEjbStarted();
    }

    /**
     * Log and record a remote Ejb invocation end with success.
     */
    public void remoteEjbFinishedWithSuccess(){
        nscsRemoteEjbLogger.recordRemoteEjbFinishedWithSuccess();
    }

    /**
     * Log and record a remote Ejb invocation end with error.
     */
    public void remoteEjbFinishedWithError() {
        nscsRemoteEjbLogger.recordRemoteEjbFinishedWithError();
    }

    /**
     *
     * @param e
     *            the e
     * @return string exception
     */
    public static String stringifyException(final Exception e) {
        return "Exception : class [" + e.getClass().getName() + "] : msg [" + e.getMessage() + "]";
    }

    /**
     *
     * @param rootMoFdn
     *            the rootMoFdn
     * @param moType
     *            the moType
     * @param moNamespace
     *            the moNamespace
     * @param requestedAttributes
     *            the requestedAttributes
     * @return the ready params
     * @deprecated
     */
    @Deprecated
    public static String stringifyReadParams(final String rootMoFdn, final String moType, final String moNamespace,
            final String... requestedAttributes) {
        return stringifyReadParams(rootMoFdn, moType, requestedAttributes);
    }

    /**
     *
     * @param rootMoFdn
     *            the rootMoFdn
     * @param moType
     *            the moType
     * @param requestedAttributes
     *            the requestedAttributes
     * @return the ready params
     */
    public static String stringifyReadParams(final String rootMoFdn, final String moType, final String... requestedAttributes) {
        return "MOs : root [" + rootMoFdn + "] : type [" + moType + "] : requested attrs " + Arrays.toString(requestedAttributes);
    }

    /**
     *
     * @param parentMoFdn
     *            the parentMoFdn
     * @param moType
     *            the moType
     * @param moNamespace
     *            the moNamespace
     * @param moVersion
     *            the moVersion
     * @param moName
     *            the moName
     * @param moAttributes
     *            the moAttributes
     * @return the createtd params
     */
    public static String stringifyCreateParams(final String parentMoFdn, final String moType, final String moNamespace, final String moVersion,
            final String moName, final Map<String, Object> moAttributes) {
        return "MO : parent [" + parentMoFdn + "] : type [" + moType + "] : ns [" + moNamespace + "] : ver [" + moVersion
                + "] : name [" + moName + "] : attrs [" + moAttributes + "]";
    }

    /**
     *
     * @param moType
     *            the moType
     * @param moFdn
     *            the moFdn
     * @return the update params
     */
    public static String stringifyUpdateParams(final String moType, final String moFdn) {
        return "MO : type [" + moType + "] : fdn [" + moFdn + "]";
    }

    /**
     *
     * @param action
     *            the action
     * @return the action
     */
    public static String stringifyAction(final WorkflowMoAction action) {
        final WorkflowMoActionState actionState = action.getState();
        final int maxPolls = action.getMaxPollTimes();
        final int polls = action.getRemainingPollTimes();
        final String moFdn = action.getTargetMoFdn();
        String actionName = null;
        if (action instanceof WorkflowMoActionWithoutParams) {
            final MoActionWithoutParameter targetAction = ((WorkflowMoActionWithoutParams) action).getTargetAction();
            actionName = targetAction.getAction();
        } else if (action instanceof WorkflowMoActionWithParams) {
            final MoActionWithParameter targetAction = ((WorkflowMoActionWithParams) action).getTargetAction();
            actionName = targetAction.getAction();
        } else {
            actionName = "Unknown MO action";
        }
        return "action [" + actionName + "] : fdn [" + moFdn + "] : state [" + actionState + "] : still [" + polls + "] of ["
                + maxPolls + "] polls";
    }

    /**
     *
     * @param moFdn
     *            the moFdn
     * @param action
     *            the action
     * @return action by Fdn
     */
    public static String stringifyActionByFdn(final String moFdn, final MoActionWithoutParameter action) {
        return "action [" + action.getAction() + "] : fdn [" + moFdn + "]";
    }

    /**
     *
     * @param moFdn
     *            the moFdn
     * @param action
     *            the action
     * @param params
     *            the params
     * @return the action by Fdn
     */
    public static String stringifyActionByFdn(final String moFdn, final MoActionWithParameter action, final MoParams params) {
        return "action [" + action.getAction() + "] : params [" + params + "] : fdn [" + moFdn + "]";
    }

    /**
     *
     * @param moFdn
     *            the moFdn
     * @param action
     *            the action
     * @return the action by Fdn
     */
    public static String stringifyActionByFdn(final String moFdn, final MoActionWithParameter action) {
        return "action [" + action.getAction() + "] : fdn [" + moFdn + "]";
    }

    /**
     *
     * @param moFdn
     *            the moFdn
     * @return the delete params
     */
    public static String stringifyDeleteParams(final String moFdn) {
        return "MO : fdn [" + moFdn + "]";
    }

    /**
     *
     * @return true if Debug Log Level Enabled
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}
