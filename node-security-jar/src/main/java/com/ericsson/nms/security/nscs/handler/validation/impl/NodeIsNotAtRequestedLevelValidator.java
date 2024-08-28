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

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.RequestedLevelAlreadySetException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;

/**
 * Checks if all nodes in the given command are not in the requested security level already. 
 * If any of the nodes are in the requested security level marks them as invalid.
 * 
 * Created by egbobcs on 17/10/2014.
 */
public class NodeIsNotAtRequestedLevelValidator implements CommandValidator {

	@Inject
	private NscsCMReaderService reader;

	@Inject
	private Logger logger;

	/**
	 * 
	 * Checks if all nodes in the given command are not in the requested security level already. 
	 * If any of the nodes are in the requested security level marks them as invalid.
	 * 
	 * @param command
	 *            - expects to be a NscsNodeCommand
	 * @param context a CommandContext instance
	 */     
	@Override
	public void validate(final NscsPropertyCommand command, final CommandContext context)
			throws NscsServiceException {

		logger.debug("Starting NodeIsNotAtRequestedLevelValidator with command type: {}", command.getCommandType());

		final SecurityLevel reqLevel = SecurityLevel.getSecurityLevel(command.getValueString(CppSecurityLevelCommand.SECURITY_LEVEL_PROPERTY));		
		logger.debug("Requested security level is [{}]", reqLevel);

		final List<NormalizableNodeReference> validNodes = context.getValidNodes();
		logger.debug("Valid nodes size is [{}]", validNodes.size());

        if ( validNodes != null && !validNodes.isEmpty() ) {

            //Check that none of the nodes are at requested level using CM Reader
            final Collection<CmObject> cmObjects = reader.getMOAttribute(validNodes,
                    Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                    Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                    Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL,
                    reqLevel.getLevel()).getCmObjects();

            if ( cmObjects.size() > 0) {
                final RequestedLevelAlreadySetException e = new RequestedLevelAlreadySetException();
                for (final CmObject o : cmObjects) {
                    final NodeReference currentNode = new NodeRef(o.getFdn());
                    logger.debug("Node [{}] is at the requested level already [{}]", currentNode.getFdn(), reqLevel);
                    context.setAsInvalidOrFailed(currentNode, e);
                }
                logger.debug("All nodes are validated some of them are invalid.");
            } else {
                logger.debug("All nodes are validated and found valid.");
            }

        }

	}
}
