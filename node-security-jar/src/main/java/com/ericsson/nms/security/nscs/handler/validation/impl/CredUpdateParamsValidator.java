/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 *
 * @author enmadmin
 */
public class CredUpdateParamsValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

        logger.debug("Starting CredUpdateParamsValidator with command type: {}", command.getCommandType());

        if (command instanceof NscsPropertyCommand) {
            for (final NormalizableNodeReference node : context.getValidNodes()) {
                logger.debug("Creating  NetworkElementSecurity for Node : {}", node);

                if (hasUnexpectedCreateCredentialsAttributes(command, node)) {
                    logger.error("Command is not valid, there are unexpected attributes.");
                    throw new CommandSyntaxException();
                }

                if (!(nscsCapabilityModelService.isLdapCommonUserSupported(node))) {
                    if (missingUpdateCredentialsAttributes(command, node)) {
                        logger.error("Command is not valid, there are no expected attributes .");
                        throw new CommandSyntaxException(new String("Missing attributes"));
                    }
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsPropertyCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }

        logger.debug("Command validated");

    }

    private boolean hasUnexpectedCreateCredentialsAttributes(final NscsPropertyCommand command, final NormalizableNodeReference normNodeRef) {

        final Set<String> actualUnexpectedAttributeKeys = new HashSet<>(command.getProperties().keySet());

        boolean isResult = false;
        final boolean isValidCommand = nscsCapabilityModelService.isCliCommandSupported(normNodeRef, NscsCapabilityModelService.CREDENTIALS_COMMAND);

        if (isValidCommand) {
            final List<String> unexpectedAttributeKeys = nscsCapabilityModelService.getUnexpectedCredentialsParams(normNodeRef);
            actualUnexpectedAttributeKeys.retainAll(unexpectedAttributeKeys);
            isResult = (actualUnexpectedAttributeKeys.size() > 0);
        }

        return isResult;
    }

    private boolean missingUpdateCredentialsAttributes(final NscsPropertyCommand command, final NormalizableNodeReference normNodeRef) {

        final Set<String> actualAttributeKeys = new HashSet<>(command.getProperties().keySet());

        boolean isResult = true;
        final boolean isValidCommand = nscsCapabilityModelService.isCliCommandSupported(normNodeRef, NscsCapabilityModelService.CREDENTIALS_COMMAND);

        if (isValidCommand) {
            final List<String> expectedAttributeKeys = nscsCapabilityModelService.getExpectedCredentialsParams(normNodeRef);
            if (command.getProperties().keySet().contains(CredentialsCommand.NODECLI_USER_NAME_PROPERTY)) {
                expectedAttributeKeys.add(CredentialsCommand.NODECLI_USER_NAME_PROPERTY);
            }
            if (command.getProperties().keySet().contains(CredentialsCommand.NODECLI_USER_PASSPHRASE_PROPERTY)) {
                expectedAttributeKeys.add(CredentialsCommand.NODECLI_USER_PASSPHRASE_PROPERTY);
            }
            actualAttributeKeys.retainAll(expectedAttributeKeys);
            isResult = (actualAttributeKeys.size() == 0);
        }

        return isResult;
    }

}
