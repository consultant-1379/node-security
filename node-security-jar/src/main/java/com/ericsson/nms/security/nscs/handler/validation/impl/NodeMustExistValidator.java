package com.ericsson.nms.security.nscs.handler.validation.impl;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Checks if all nodes in the given command exists
 *
 * @author emaynes
 */
public class NodeMustExistValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService readerService;

    /**
     * Checks if all nodes in the given command exists
     *
     * @param command
     *            - expects to be a NscsNodeCommand
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
        logger.debug("Starting NodeMustExistValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

            if (nodeCommand.isAllNodes()) {
                logger.debug("Command is using all nodes wildcard, no validation is required... skipping");
            } else {
                final UnassociatedNetworkElementException unassociatedNetworkElementException = new UnassociatedNetworkElementException();
                for (final NodeReference node : context.getNodesNotFound()) {
                    if (Model.NETWORK_ELEMENT.isPresent(node.getFdn())) {
                        if (readerService.exists(node.getFdn())) {
                            context.setAsInvalidOrFailed(node, unassociatedNetworkElementException);
                            continue;
                        }
                    }
                    final InvalidNodeNameException invalidNodeNameException = new InvalidNodeNameException();
                    context.setAsInvalidOrFailed(node, invalidNodeNameException);
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
            throw new UnexpectedCommandTypeException();
        }
    }
}
