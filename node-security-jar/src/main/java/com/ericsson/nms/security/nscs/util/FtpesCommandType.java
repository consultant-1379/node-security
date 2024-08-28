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

package com.ericsson.nms.security.nscs.util;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.handler.command.impl.FtpesCommandHandlerHelper;

public enum FtpesCommandType {

    ACTIVATE("activate", FtpesCommandHandlerHelper.FTPES_ACTIVATE_EXECUTED,
             FtpesCommandHandlerHelper.FTPES_ACTIVATE_WF_FAILED, NscsCommandType.FTPES_ACTIVATE),
    DEACTIVATE(
            "deactivate", FtpesCommandHandlerHelper.FTPES_DEACTIVATE_EXECUTED,
    FtpesCommandHandlerHelper.FTPES_DEACTIVATE_WF_FAILED, NscsCommandType.FTPES_DEACTIVATE);

    private final String name;
    private final String executedMessage;
    private final String failedMessage;
    private final NscsCommandType nscsCommandType;

    FtpesCommandType(String name, String executedMessage, String failedMessage, NscsCommandType nscsCommandType) {
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
