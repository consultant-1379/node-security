/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
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
public class CredMandatoryParamsValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

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

        logger.debug("Starting CredMandatoryParamsValidator with command type: {}", command.getCommandType());

        if (command instanceof NscsPropertyCommand) {
            for (final NormalizableNodeReference node : context.getValidNodes()) {
                logger.debug("Creating  NetworkElementSecurity for Node : {}", node);

                if (hasUnexpectedCreateCredentialsAttributes(command, context, node)) {
                    logger.error("Command is not valid, there are unexpected attributes.");
                    throw new CommandSyntaxException();
                }

                if (hasExpectedCredentialsAttributes(command, context, node)
                        || !(nscsCapabilityModelService.isLdapCommonUserSupported(node))) {
                    if (missingCreateCredentialsAttributes(command, context, node)) {
                        logger.error("Command is not valid, there are missing attributes.");
                        throw new CommandSyntaxException();
                    }
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsPropertyCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }

        logger.debug("Command validated");
    }

    private boolean hasUnexpectedCreateCredentialsAttributes(final NscsPropertyCommand command, final CommandContext context,
            final NormalizableNodeReference node) {

        final Set<String> actualUnexpectedAttributeKeys = new HashSet<>(command.getProperties().keySet());

        boolean isResult = false;
        boolean isValidCommand = false;

        try {
            isValidCommand = nscsCapabilityModelService.isCliCommandSupported(node, NscsCapabilityModelService.CREDENTIALS_COMMAND);
        } catch (final NscsCapabilityModelException e) {
            logger.error(e.getMessage());

            final NetworkElementNotfoundException networkElementNotfoundException = new NetworkElementNotfoundException();

            if (!node.hasNormalizedRef()) {
                context.setAsInvalidOrFailed(node, networkElementNotfoundException);
                logger.debug("Setting node [{}] as invalid with exception NetworkElementNotfoundException.", node);
            }
        }

        if (isValidCommand) {
            final List<String> unexpectedAttributeKeys = nscsCapabilityModelService.getUnexpectedCredentialsParams(node);
            actualUnexpectedAttributeKeys.retainAll(unexpectedAttributeKeys);
            isResult = (actualUnexpectedAttributeKeys.size() > 0);
        }

        return isResult;
    }

    private boolean hasExpectedCredentialsAttributes(final NscsPropertyCommand command, final CommandContext context,
            final NormalizableNodeReference node) {

        final Set<String> actualExpectedAttributeKeys = new HashSet<>(command.getProperties().keySet());

        boolean isResult = false;
        boolean isValidCommand = false;

        try {
            isValidCommand = nscsCapabilityModelService.isCliCommandSupported(node, NscsCapabilityModelService.CREDENTIALS_COMMAND);
        } catch (final NscsCapabilityModelException e) {
            logger.error(e.getMessage());

            final NetworkElementNotfoundException networkElementNotfoundException = new NetworkElementNotfoundException();

            if (!node.hasNormalizedRef()) {
                context.setAsInvalidOrFailed(node, networkElementNotfoundException);
                logger.debug("Setting node [{}] as invalid with exception NetworkElementNotfoundException.", node);
            }
        }

        if (isValidCommand) {
            final List<String> expectedAttributeKeys = nscsCapabilityModelService.getExpectedCredentialsParams(node);
            actualExpectedAttributeKeys.retainAll(expectedAttributeKeys);
            isResult = (actualExpectedAttributeKeys.size() > 0);
        }

        return isResult;
    }

    private boolean missingCreateCredentialsAttributes(final NscsPropertyCommand command, final CommandContext context,
            final NormalizableNodeReference node) {

        final Set<String> actualAttributeKeys = new HashSet<>(command.getProperties().keySet());

        boolean isResult = false;
        boolean isValidCommand = false;

        try {
            isValidCommand = nscsCapabilityModelService.isCliCommandSupported(node, NscsCapabilityModelService.CREDENTIALS_COMMAND);
        } catch (final NscsCapabilityModelException e) {
            logger.error(e.getMessage());

            final NetworkElementNotfoundException networkElementNotfoundException = new NetworkElementNotfoundException();

            if (!node.hasNormalizedRef()) {
                context.setAsInvalidOrFailed(node, networkElementNotfoundException);
                logger.debug("Setting node [{}] as invalid with exception NetworkElementNotfoundException.", node);
            }
        }

        if (isValidCommand) {
            final List<String> expectedAttributeKeys = nscsCapabilityModelService.getExpectedCredentialsParams(node);
            actualAttributeKeys.retainAll(expectedAttributeKeys);
            isResult = (actualAttributeKeys.size() < expectedAttributeKeys.size());
        }

        return isResult;
    }

}
