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
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Checks if all nodes in the given command has all of the properties that the CredentialsCommand needs.
 *
 * @author egbobcs
 *
 */
public class KeyGeneratorSupportedNodeTypeValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    NscsCapabilityModelService capabilityModelService;

    /**
     * Checks if all nodes in the given command has all of the properties that the CredentialsCommand needs.
     *
     * @param command
     *            - expects to be a NscsNodeCommand
     * @param context
     *            a CommandContext instance
     * @throws CommandSyntaxException
     *             in case some of the parameters are missing
     *
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

        logger.info("Starting " + this.getClass().getName());

        if (command instanceof NscsPropertyCommand) {

            // Check Supported Node Types SGSN-MME SupportedNodeValidator
            final UnsupportedNodeTypeException unsupportedNodeException = new UnsupportedNodeTypeException();
            if (context.getValidNodes().size() > 0) {
                final StringBuilder sb = new StringBuilder();
                for (final NormalizableNodeReference nnr : context.getValidNodes()) {
                    if (!capabilityModelService.isCliCommandSupported(nnr, NscsCapabilityModelService.SSHKEY_COMMAND)) {
                        sb.append(nnr.getName() + " ");
                        context.setAsInvalidOrFailed(nnr, unsupportedNodeException);
                    }
                }
                if (sb.length() > 0) {
                    final String errmsg = String.format("Unsupported command for the following nodes. %s", sb.toString());
                    logger.error(errmsg);
                }
            }

        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsPropertyCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }

        logger.info(this.getClass().getName() + " done");
    }

}
