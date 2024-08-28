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

import com.ericsson.nms.security.nscs.api.command.NscsCommand;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collection;

import static com.ericsson.nms.security.nscs.data.Model.NETWORK_ELEMENT;

/**
 * Checks if all nodes in the given command are SYNCHRONIZED
 * 
 * Created by egbobcs on 27/08/2014.
 */
public class NodeIsSynchedValidator implements CommandValidator{

	@Inject
	private Logger logger;

	@Inject
	private NscsCMReaderService reader;

	public static final String WILDCARD_NOT_SUPPORTED = "Command is using all nodes wildcard. Validation not supported for wildcard";

	/**
	 * Checks if all nodes in the given command are SYNCHRONIZED
	 * 
	 * The check assumes that the node exists.
	 * 
	 * @param command
	 *            - expects to be a NscsNodeCommand
	 * @throws NscsServiceException
	 *             (NodeNotSynchronizedException) with a list of non-synched nodes.
	 */
	@Override
	public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
		logger.debug("Starting NodeIsSynchedValidator with command type: {}", command.getCommandType());

		assertNscsNodeCommand(command);

		final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;

		if (nodeCommand.isAllNodes()) {
			logger.debug(WILDCARD_NOT_SUPPORTED + ", throwing exception");
			throw new IllegalArgumentException(WILDCARD_NOT_SUPPORTED);
		}

        if ( context.getValidNodes().size() > 0 ) {

            //Check the nodes are SYNCHRONIZED using cm-reader
            final CmResponse response = reader.getMOAttribute(context.toNormalizedRef(context.getValidNodes()),
                    NETWORK_ELEMENT.cmFunction.type(),
                    NETWORK_ELEMENT.cmFunction.namespace(),
                    NETWORK_ELEMENT.cmFunction.SYNC_STATUS);

            final Collection<CmObject> cmObjects = response.getCmObjects();

            if (cmObjects.size() != nodeCommand.getNodes().size()) {
                logger.warn("CmObject.size [{}] is not equals to nodeList.size [{}]. Make sure all nodes are exists", cmObjects.size(), nodeCommand.getNodes().size());
            }

            final NodeNotSynchronizedException nodeNotSynchronizedException = new NodeNotSynchronizedException();

            for (final CmObject o : cmObjects) {
                final String status = (String) o.getAttributes().get(NETWORK_ELEMENT.cmFunction.SYNC_STATUS);
                final NodeReference currentNode = new NodeRef(o.getFdn());
                logger.debug("Node [{}] SYNC status is: [{}]", currentNode.getFdn(), status);

                if ( ! ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name().equals(status)) {
                    context.setAsInvalidOrFailed(currentNode, nodeNotSynchronizedException);
                }
            }

        }
	}

	private void assertNscsNodeCommand(final NscsCommand command) {    
		if (!NscsNodeCommand.isNscsNodeCommand(command)) {
			logger.warn("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
			throw new UnexpectedCommandTypeException();	
		}    	
	}
}
