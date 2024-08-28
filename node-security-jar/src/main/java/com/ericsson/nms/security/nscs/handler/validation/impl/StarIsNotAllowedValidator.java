package com.ericsson.nms.security.nscs.handler.validation.impl;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.CommandContext;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Throws an error if command is using start '*' as node list argument.
 * 
 * @author emaynes
 */
public class StarIsNotAllowedValidator implements CommandValidator {


    @Inject
    private Logger logger;

    /**
     * Throws an error if command is using start '*' as node list argument.
     * 
     * @param command
     *            - expects to be a NscsNodeCommand
     * @param context a CommandContext instance
     * @throws NscsServiceException
     *             (UnsupportedCommandArgumentException)
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
    	logger.debug("Starting StarIsNotAllowedValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;
            if (nodeCommand.isAllNodes()) {
                throw new UnsupportedCommandArgumentException(NscsErrorCodes.NODE_LIST_CANNOT_BE_STAR, NscsErrorCodes.PLEASE_SPECIFY_NODES_USING_NODELIST_OR_NODEFILE);
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
            throw new UnexpectedCommandTypeException();
        }
    }
}
