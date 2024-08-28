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

import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * throws MaxNodesExceededException if user's entry exceeds Max number of Nodes allowed
 * This validates before other validator class
 *
 * User: ejuhpar
 * Date: 19/11/14
 */
public class MaxNodeToSetSecurityLevelValidator implements CommandValidator {

    private static final int MAX_NO_OF_NODES_ALLOWED = 160;

    @Inject
    private Logger logger;

    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
        logger.debug("Starting  MaxNodeToSetSecurityLevelValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

            if (nodeCommand.isAllNodes()) {
                logger.debug("Command is using all nodes wildcard, no validation is required... skipping");
            } else {
                if (nodeCommand.getNodes().size() > MAX_NO_OF_NODES_ALLOWED) {
                    throw new MaxNodesExceededException(MAX_NO_OF_NODES_ALLOWED);
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
            throw new UnexpectedCommandTypeException();
        }
    }
}
