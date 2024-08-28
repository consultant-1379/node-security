package com.ericsson.nms.security.nscs.handler.validation.impl;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * Checks if all nodes in the given command have a corresponding MO in the
 * Normalized node structure, in other words, must have a corresponding
 * NetworkElement MO.
 * 
 * @author Mayke Nespoli
 */
public class NodeMustBeNormalizableValidator implements CommandValidator {

    @Inject
    private Logger logger;

    /**
     * Checks if all nodes in the given command have an associated NetworkElement MO
     *
     * @param command
     *            - expects to be a NscsNodeCommand
     * @throws com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException if parameter command is
     * not of com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand type
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
    	logger.debug("Starting NodeMustBeNormalizableValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

            if (nodeCommand.isAllNodes()) {
                logger.debug("Command is using all nodes wildcard, no validation is required for now ... skipping");
            } else {
                final NetworkElementNotfoundException networkElementNotfoundException = new NetworkElementNotfoundException();
                for (NormalizableNodeReference node : context.getValidNodes()) {
                    if ( ! node.hasNormalizedRef() ){
                        context.setAsInvalidOrFailed(node, networkElementNotfoundException);
                        logger.debug("Setting node [{}] as invalid with exception NetworkElementNotfoundException.", node);
                    }
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
            throw new UnexpectedCommandTypeException();
        }
    }
}
