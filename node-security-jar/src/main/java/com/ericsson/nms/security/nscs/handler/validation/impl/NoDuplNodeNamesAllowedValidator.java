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

import java.util.*;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Throws an error if a command is passing a duplicated node name in the node list.
 * @author xpawpio
 * 
 */
public class NoDuplNodeNamesAllowedValidator implements CommandValidator {

	@Inject
	private Logger logger;

	/**
	 *  Throws an error if a command is passing a duplicated node name in the node list.
	 * 
	 * @param command
	 *            - expects to be a NscsNodeCommand
	 * @param context a CommandContext instance
	 *   
	 */
	@Override
	public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
		logger.debug("Starting NoDuplNodeNamesAllowedValidator with command type: {}", command.getCommandType());


		if (NscsNodeCommand.isNscsNodeCommand(command)) {
			final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;
			if (nodeCommand.isAllNodes()) {
				logger.debug("Command is using all nodes wildcard, no validation is required... skipping");
			} else {
//                List<NormalizableNodeReference> validNodes = context.getValidNodes();
                List<NodeReference> validNodes = context.getAllNodes();

                final Map<NodeReference, Boolean> nonUniqueNodes = new HashMap<>(validNodes.size());

                for(NodeReference node : validNodes) {
                    if(nonUniqueNodes.containsKey(node)){
                        nonUniqueNodes.put(node, Boolean.TRUE);
                        context.setAsInvalidOrFailed(node, new DuplicateNodeNamesException());
                    } else {
                        nonUniqueNodes.put(node, Boolean.FALSE);
                    }
                }

                for (Map.Entry<NodeReference, Boolean> entry : nonUniqueNodes.entrySet()) {
                    if ( entry.getValue() ) {
                        context.setAsInvalidOrFailed(entry.getKey(), new DuplicateNodeNamesException());
                    }
                }
            }
        }
        else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }

    }
}


