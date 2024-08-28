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

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.NodeIsInWorkflowException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;


public class NodeNotInWFValidator implements CommandValidator {

	@Inject
	private Logger logger;

	@EServiceRef
	private WfQueryService wfQuery;

	@Override
	public void validate(final NscsPropertyCommand command, final CommandContext context)
			throws NscsServiceException {
		logger.debug("Starting NodeNotInWFValidator with command type: {}", command.getCommandType());

		final HashSet<NormalizableNodeReference> validNodes = new HashSet<>(context.getValidNodes());
        final Set<NodeReference> nodesInProgress = wfQuery.getWorkflowsInProgress(validNodes);
		final NodeIsInWorkflowException e = new NodeIsInWorkflowException();
		if (nodesInProgress.size() > 0) {
			for (final NodeReference node : nodesInProgress) {
				logger.debug("Node [{}] is in progress, setting as invalid", node.getFdn());
				context.setAsInvalidOrFailed(node, e);
			}
			logger.debug("All nodes are validated some of them are invalid.");
		} else {				
			logger.debug("All nodes are validated and found valid.");
		}		
	}
}
