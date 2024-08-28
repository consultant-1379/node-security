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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.HttpsCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;

/**
 * Created by eelzkoc on 6/30/17.
 */
@CommandType(NscsCommandType.HTTPS_DEACTIVATE)
@Local(CommandHandlerInterface.class)
public class DeactivateHttpsHandler implements CommandHandler<HttpsCommand>, CommandHandlerInterface {

    @Inject
    HttpsCommandHandlerHelper httpsCommandHandlerHelper;

    @Override
    public NscsCommandResponse process(final HttpsCommand command, final CommandContext context)
            throws NscsServiceException {
        return httpsCommandHandlerHelper.processDeactivate(command, context);
    }
}
