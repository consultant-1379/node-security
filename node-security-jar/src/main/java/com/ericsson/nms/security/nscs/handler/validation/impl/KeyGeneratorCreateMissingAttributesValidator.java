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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Checks if all nodes in the given command has all of the properties that the CredentialsCommand needs.
 *
 * @author egbobcs
 *
 */
public class KeyGeneratorCreateMissingAttributesValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    NscsCapabilityModelService capabilityModel;

    private static final Set<String> expectedAttributeKeys = new HashSet<>();

    static {
        expectedAttributeKeys.add(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY);
    }

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

            if (!command.getProperties().keySet().containsAll(expectedAttributeKeys)) {
                final String errMsg = "Command is not valid, there are missing attributes.";
                logger.error(errMsg);
                throw new CommandSyntaxException(errMsg);
            }

        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsPropertyCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }

        logger.info(this.getClass().getName() + " done");
    }

}
