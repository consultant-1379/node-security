package com.ericsson.nms.security.nscs.handler.validation.impl;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Checks if all nodes in the given command have an associated NetworkElementSecurity MO
 * 
 * @author emaynes
 */
public class NodeMustHaveNetworkElementSecurityMoValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    /**
     * Checks if all nodes in the given command have an associated NetworkElementSecurity MO
     * 
     * @param command
     *            - expects to be a NscsNodeCommand
     * @param context a CommandContext instance
     * @throws NscsServiceException
     *             (NetworkElementSecurityNotfoundException) with a list of nodes without NetworkElementSecurityMo
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
    	logger.debug("Starting NodeMustHaveNetworkElementSecurityMoValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

            if (nodeCommand.isAllNodes()) {
                logger.debug("Command is using all nodes wildcard, no validation is required ... skipping");
            } else {
                final NetworkElementSecurityNotfoundException networkElementSecurityNotfoundException = new NetworkElementSecurityNotfoundException();
                for (final NormalizableNodeReference node : context.getValidNodes()) {
                    try {
                        if (!reader.exists(Model.getNomalizedRootMO(node.getNormalizedRef().getFdn())
                            .securityFunction.networkElementSecurity.withNames(node.getNormalizedRef().getName()).fdn())) {
                            context.setAsInvalidOrFailed(node, networkElementSecurityNotfoundException);
                            logger.debug("Setting node [{}] as invalid with exception NetworkElementSecurityNotfoundException", node);
                        }
                    }catch(final DataAccessException e){
                        context.setAsInvalidOrFailed(node, e);
                        logger.debug("Setting node [{}] as invalid due to exception : {}", node, e.getMessage());
                    }
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }
        logger.debug("Nodes are validated");
    }
}
