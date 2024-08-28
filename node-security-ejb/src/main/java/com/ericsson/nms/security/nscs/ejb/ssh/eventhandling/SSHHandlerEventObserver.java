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
package com.ericsson.nms.security.nscs.ejb.ssh.eventhandling;

import static com.ericsson.nms.security.nscs.cpp.model.CPPCommands.SECMODE_F_S;
import static com.ericsson.nms.security.nscs.cpp.model.CPPCommands.SECMODE_F_U;
import static com.ericsson.nms.security.nscs.cpp.model.CPPCommands.SECMODE_L_1;
import static com.ericsson.nms.security.nscs.cpp.model.CPPCommands.SECMODE_L_2;
import static com.ericsson.nms.security.nscs.cpp.model.CPPCommands.SECMODE_W_S;
import static com.ericsson.nms.security.nscs.cpp.model.CPPCommands.SECMODE_W_U;
import static com.ericsson.nms.security.nscs.cpp.model.CPPCommands.SECMODE_S;
import static com.ericsson.nms.security.nscs.ejb.ssh.eventhandling.SSHHandlerEventObserver.SSHCommandsOutputs.SECMODE_ALREADY_SET;
import static com.ericsson.nms.security.nscs.ejb.ssh.eventhandling.SSHHandlerEventObserver.SSHCommandsOutputs.SECMODE_SECURITY_OK;
import static com.ericsson.nms.security.nscs.ejb.ssh.eventhandling.SSHHandlerEventObserver.SSHCommandsOutputs.SECMODE_OK;
import static com.ericsson.nms.security.nscs.ejb.ssh.eventhandling.SSHHandlerEventObserver.SSHCommandsOutputs.SSH_KEY_CREATE_OK;
import static com.ericsson.nms.security.nscs.ejb.ssh.eventhandling.SSHHandlerEventObserver.SSHCommandsOutputs.SSH_KEY_UPDATE_OK;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.CPP_COMMAND_FILE_TRANSFER_CLIENT_MODE_FAIL;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.CPP_COMMAND_FILE_TRANSFER_CLIENT_MODE_SUCCESS;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.CPP_COMMAND_HTTPS_FAIL;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.CPP_COMMAND_HTTPS_SUCCESS;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.CPP_COMMAND_OPERATIONAL_SECURITY_LEVEL_FAIL;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.CPP_COMMAND_OPERATIONAL_SECURITY_LEVEL_SUCCESS;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.SSH_KEY_GENERATION_COMMAND_FAIL;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.SSH_KEY_GENERATION_COMMAND_SUCCESS;
import static com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants.*;


import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenCommand;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.mediation.sec.model.SSHCommandFailure;
import com.ericsson.oss.mediation.sec.model.SSHCommandResult;
import com.ericsson.oss.mediation.sec.model.SSHCommandSuccess;

import java.util.regex.Pattern;

/**
 * <p>
 * Main listener for SSH events dispatched by the mediation layer
 * </p>
 * Created by emaynes on 11/04/2014.
 */
@Stateless
public class SSHHandlerEventObserver {

    @Inject
    private Logger logger;

    @EServiceRef
    private WorkflowHandler workflowHandler;
    @Inject
    private SystemRecorder systemRecorder;

    public void commandResultHandlerFailure(@Observes @Modeled final SSHCommandFailure sshCommandResultFailure) {
        final String command = sshCommandResultFailure.getCommand();
        final NodeReference node = new NodeRef(sshCommandResultFailure.getFdn());
        logger.error("Command keygen mediation event failure for command: {} with node: {}", command, node.getName());
        dispatchFailMessage(command, node);
    }

    /**
     * Listener method for SSHCommandResult events
     *
     * @param sshCommandResult SSHCommandResult events SSHCommandSuccess event
     */
    public void commandResultHandler(@Observes @Modeled final SSHCommandResult sshCommandResult) {
        final String command = sshCommandResult.getCommand();
        final NodeReference node = new NodeRef(sshCommandResult.getFdn());
        final String commandOutput = sshCommandResult.getCommandOutput() == null ? ""
                : sshCommandResult.getCommandOutput();
        logger.info("SSHHandlerEventObserver got a message, command [{}]", command);

        if (sshCommandResult instanceof SSHCommandSuccess) {
            logger.info("SSH command ran successfully [{}] for node [{}]", commandOutput, node.getFdn());

            if (checkCommandOutput(commandOutput)) {
                dispatchSuccessMessage(command, commandOutput, node);
            } else {
                logger.warn("SSH command returned unexpected output [{}]", commandOutput);
                dispatchFailMessage(command, node);
            }
        } else {
            // Please DO NOT change this logger info. TAF Test is checking this log
            logger.info("SSHHandlerEventObserver failure on node [{}]", node.getName());

            logger.info("SSH command failed [{}]", commandOutput);
            dispatchFailMessage(command, node);
        }
    }

    private boolean isActiveHTTPS(String commandOutput) {
        return !Pattern
                .compile(WEB_SERVER_UNSECURE_REGEX)
                .matcher(commandOutput)
                .find();
    }

    private void dispatchFailMessage(String command, NodeReference node) {
        String messageToDispatch = "";
        boolean needToDispatch = false;

        if (command.equals(SECMODE_F_S.toString()) || command.equals(SECMODE_F_U.toString())) {
            messageToDispatch = CPP_COMMAND_FILE_TRANSFER_CLIENT_MODE_FAIL;
            needToDispatch = true;
        }
        if (command.equals(SECMODE_L_1.toString()) || command.equals(SECMODE_L_2.toString())) {
            messageToDispatch = CPP_COMMAND_OPERATIONAL_SECURITY_LEVEL_FAIL;
            needToDispatch = true;
        }
        if (command.equals(SECMODE_W_S.toString()) || command.equals(SECMODE_W_U.toString())) {
            messageToDispatch = CPP_COMMAND_HTTPS_FAIL;
            needToDispatch = true;
        }
        if (command.equals(SSHKeyGenCommand.SSH_KEY_CREATE.toString())
                || command.equals(SSHKeyGenCommand.SSH_KEY_UPDATE.toString())
                || command.equals(SSHKeyGenCommand.SSH_KEY_DELETE.toString())) {
            messageToDispatch = SSH_KEY_GENERATION_COMMAND_FAIL;
            needToDispatch = true;
        }

        if (needToDispatch) {
            workflowHandler.dispatchMessage(node, messageToDispatch);
        }

        boolean isSuccessMessage = false;
        this.logDispatchStatus(needToDispatch, isSuccessMessage, messageToDispatch, node, command);

    }

    private void dispatchSuccessMessage(final String command, final String commandOutput,
                                        NodeReference node) {
        String messageToDispatch = "";
        boolean needToDispatch = false;

        if (command.equals(SECMODE_F_S.toString()) || command.equals(SECMODE_F_U.toString())) {
            messageToDispatch = CPP_COMMAND_FILE_TRANSFER_CLIENT_MODE_SUCCESS;
            needToDispatch = true;
        }

        if (command.equals(SECMODE_L_1.toString()) || command.equals(SECMODE_L_2.toString())) {
            messageToDispatch = CPP_COMMAND_OPERATIONAL_SECURITY_LEVEL_SUCCESS;
            needToDispatch = true;
        }

        if (command.equals(SECMODE_W_S.toString()) || command.equals(SECMODE_W_U.toString())) {
            messageToDispatch = CPP_COMMAND_HTTPS_SUCCESS;
            // see TORF-680745
            needToDispatch = false;
        }

        if (command.equals(SSHKeyGenCommand.SSH_KEY_CREATE.toString())
                || command.equals(SSHKeyGenCommand.SSH_KEY_UPDATE.toString())
                || command.equals(SSHKeyGenCommand.SSH_KEY_DELETE.toString())) {
            messageToDispatch = SSH_KEY_GENERATION_COMMAND_SUCCESS;
            needToDispatch = true;
        }

        if (command.equals(SECMODE_S.toString())) {
            if (isActiveHTTPS(commandOutput)) {
                messageToDispatch = CPP_NODE_HTTPS;
                needToDispatch = true;
            } else {
                messageToDispatch = CPP_NODE_HTTP;
                needToDispatch = true;
            }
        }

        if (needToDispatch) {
            workflowHandler.dispatchMessage(node, messageToDispatch);
        }

        boolean isSuccessMessage = true;
        this.logDispatchStatus(needToDispatch, isSuccessMessage, messageToDispatch, node, command);

        // Please DO NOT change this logger info. TAF Test is checking this log
        logger.info("SSHHandlerEventObserver successfully executed on node [{}]", node.getName());

    }

    private void logDispatchStatus(boolean isMessageDispacted, boolean isSuccessMessage, String messageToDispatch,
            NodeReference node, String command) {
        String message;
        String resultMessage = (isSuccessMessage) ? "success" : "failed";

        if (isMessageDispacted) {
            message = String.format("Dispatched [%s] workflow message [%s], node [%s], command [%s]", resultMessage,
                    messageToDispatch, node, command);
            logger.info(message);
            systemRecorder.recordSecurityEvent("Node Security Service - SSHHandlerEventObserver",
                    "Event received notification: " + message, "", "NETWORK.INITIAL_NODE_ACCESS",
                    ErrorSeverity.INFORMATIONAL, "IN-PROGRESS");
        } else {
            message = String.format("None [%s] workflow message sent for node [%s], command [%s]", resultMessage, node,
                    command);
            logger.error(message);
        }

    }

    public enum SSHCommandsOutputs {

        SECMODE_OK("SECMODE_OK"), SECMODE_ALREADY_SET("SECMODE_ALREADY_SET"), SSH_KEY_UPDATE_OK(
                "Update_key_OK"), SSH_KEY_CREATE_OK("Copy_key_OK"), SSH_KEY_DELETE_OK("Delete_key_OK"),SSH_KEY_UPDATE_FAIL(
                        "Update_key_FAIL"), SSH_KEY_CREATE_FAIL(
                                "Copy_key_FAIL"), SECMODE_SECURITY_OK("Security configuration settings:");

        private final String text;

        SSHCommandsOutputs(final String value) {
            this.text = value;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    private boolean checkSecmodeCommandOutput (final String commandOutput) {
        return commandOutput.contains(SECMODE_OK.toString())
                || commandOutput.contains(SECMODE_ALREADY_SET.toString());
    }

    private boolean checkSecmodeSCommandOutput(final String commandOutput) {
        return commandOutput.contains(SECMODE_SECURITY_OK.toString()) && commandOutput.contains(WEB_SERVER_PROPERTY);
    }

    private boolean checkSshKeyCommandOutput(final String commandOutput) {
        return commandOutput.contains(SSH_KEY_CREATE_OK.toString())
                || commandOutput.contains(SSH_KEY_UPDATE_OK.toString())
                || commandOutput.contains(SSHCommandsOutputs.SSH_KEY_DELETE_OK.toString());
    }

    private boolean checkCommandOutput(final String commandOutput) {
        return checkSecmodeCommandOutput(commandOutput)
                || checkSecmodeSCommandOutput(commandOutput)
                || checkSshKeyCommandOutput(commandOutput);
    }

}
