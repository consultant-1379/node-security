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

package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.FtpesCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;

import javax.ejb.Local;
import javax.inject.Inject;

@CommandType(NscsCommandType.FTPES_DEACTIVATE)
@Local(CommandHandlerInterface.class)
public class DeactivateFtpesHandler implements CommandHandler<FtpesCommand>, CommandHandlerInterface {

    @Inject
    FtpesCommandHandlerHelper ftpesCommandHandlerHelper;

    @Override
    public NscsCommandResponse process(FtpesCommand command, CommandContext context) throws NscsServiceException {
        return ftpesCommandHandlerHelper.processDeactivate(command, context);
    }

}
