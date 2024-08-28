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
 * Checks if all nodes in the given command exists at least as normalized MO. The validator can possibly move nodes from notFound to valid list if
 * normalized MO exists.
 *
 * @author emaborz
 */
public class NormalizedNodeMustExistValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService readerService;

    /**
     * Checks if all nodes in the given command exists at least as normalized MO. The validator can possibly move nodes from notFound to valid list if
     * normalized MO exists.
     * 
     * @param command
     *            - expects to be a NscsNodeCommand
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
        logger.debug("Starting NormalizedNodeMustExistValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

            if (nodeCommand.isAllNodes()) {
                logger.debug("Command is using all nodes wildcard, no validation is required... skipping");
            } else {
                for (final NodeReference node : context.getNodesNotFound()) {
                    if (Model.NETWORK_ELEMENT.isPresent(node.getFdn())) {
                        if (readerService.exists(node.getFdn())) {
                            // Move node from notFound to Valid
                            context.setAsValid(node);
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
