package com.ericsson.nms.security.nscs.util;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.handler.command.impl.HttpsCommandHandlerHelper;

/**
 * Created by ekrzsia on 7/26/17.
 */
public enum CommandType {

    ACTIVATE("activate", HttpsCommandHandlerHelper.HTTPS_ACTIVATE_EXECUTED,
            HttpsCommandHandlerHelper.HTTPS_ACTIVATE_WF_FAILED, NscsCommandType.HTTPS_ACTIVATE), DEACTIVATE(
                    "deactivate", HttpsCommandHandlerHelper.HTTPS_DEACTIVATE_EXECUTED,
                    HttpsCommandHandlerHelper.HTTPS_DEACTIVATE_WF_FAILED, NscsCommandType.HTTPS_DEACTIVATE);

    private final String name;
    private final String executedMessage;
    private final String failedMessage;
    private final NscsCommandType nscsCommandType;

    CommandType(String name, String executedMessage, String failedMessage, NscsCommandType nscsCommandType) {
        this.name = name;
        this.executedMessage = executedMessage;
        this.failedMessage = failedMessage;
        this.nscsCommandType = nscsCommandType;
    }

    public String getName() {
        return name;
    }

    public String getExecutedMessage() {
        return executedMessage;
    }

    public String getFailedMessage() {
        return failedMessage;
    }

    public NscsCommandType getNscsCommandType() {
        return nscsCommandType;
    }
}
