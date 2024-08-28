package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.List;

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
 * Checks if all nodes in the given command have an associated Security Function MO
 * 
 * @author xpawpio
 */
public class NodeMustHaveSecurityFunctionMoValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    /**
     * Checks if all nodes in the given command have an associated Security Function MO
     * 
     * @param command
     *            - expects to be a NscsNodeCommand
     * @param context a CommandContext instance
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
    	logger.debug("Starting NodeMustHaveSecurityFunctionMoValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

            if (nodeCommand.isAllNodes()) {
                logger.debug("Command is using all nodes wildcard, no validation is required for ccp type nodes... skipping");
            } else {

                final List<NormalizableNodeReference> nodes = context.getValidNodes();

                final SecurityFunctionMoNotfoundException securityFunctionNotFoundException = new SecurityFunctionMoNotfoundException();                

                for (NormalizableNodeReference node : nodes) {
                    if (! reader.exists(Model.getNomalizedRootMO(node.getNormalizedRef().getFdn()).securityFunction.withNames(node.getNormalizedRef().getName()).fdn())){
                        context.setAsInvalidOrFailed(node, securityFunctionNotFoundException);
                    }
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
            throw new UnexpectedCommandTypeException();
        }
        logger.debug("Validated command: Security Function MO exists");
    }

}
