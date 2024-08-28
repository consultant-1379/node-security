package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.SecurityLevelCommonUtils;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Checks if all nodes in the given command have an associated Security MO
 * 
 * @author emaynes
 */
public class NodeMustHaveSecurityMoValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;
    
    @Inject
    private SecurityLevelCommonUtils securityLevelCommonUtils;
    

    /**
     * Checks if all nodes in the given command have an associated Security MO
     * 
     * @param command
     *            - expects to be a NscsNodeCommand
     * @param context a CommandContext instance
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
    	logger.debug("Starting NodeMustHaveSecurityMoValidator with command type: {}", command.getCommandType());

        if (NscsNodeCommand.isNscsNodeCommand(command)) {
            final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

            if (nodeCommand.isAllNodes()) {
                logger.debug("Command is using all nodes wildcard, no validation is required for ccp type nodes... skipping");
            } else {

                final List<NormalizableNodeReference> nodes = context.getValidNodes();
                final NodeNotSynchronizedException nodeNotSynchronizedException = new NodeNotSynchronizedException();

                for (NormalizableNodeReference node : nodes) {
                    if (! reader.exists(securityLevelCommonUtils.getSecurityFdn(node)) ){
                        context.setAsInvalidOrFailed(node, nodeNotSynchronizedException);
                    }
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
            throw new UnexpectedCommandTypeException();
        }
        logger.debug("Validated command: Security MO exists");
    }

}
