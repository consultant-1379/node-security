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

package com.ericsson.nms.security.nscs.handler.command.impl;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.SsoCommand;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;

/**
 * <p>
 * Updates a NetworkElementSecurity Mo associated to each of the specified nodes.
 * @author zkllsmg
 *         </p>
 */

@CommandType(NscsCommandType.SSO_DISABLE)
@Local(CommandHandlerInterface.class)
public class DisableSsoHandler implements CommandHandler<SsoCommand>, CommandHandlerInterface {

    @Inject
    SsoCommandHandlerHelper ssoCommandHandlerHelper;

    @Override
    public NscsCommandResponse process(final SsoCommand command, final CommandContext context) {
        return ssoCommandHandlerHelper.process(command, context, false);
    }
}
