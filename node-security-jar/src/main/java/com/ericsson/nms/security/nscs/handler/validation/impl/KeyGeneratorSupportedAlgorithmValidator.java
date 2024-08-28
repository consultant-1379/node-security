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

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;

/**
 * Checks if algorithm-type-size property of the given KeyGeneratorCommand command is valid.
 * 
 * @author egbobcs
 *
 */
public class KeyGeneratorSupportedAlgorithmValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    /**
     * Checks if algorithm-type-size property of the given KeyGeneratorCommand command is valid.
     * 
     * @param command
     *            the command.
     * @param context
     *            the command context.
     * @throws {@link
     *             InvalidArgumentValueException} if algorithm-type-size property has invalid value.
     * @throws {@link
     *             UnexpectedCommandTypeException} if command is not an instance of {@link NscsPropertyCommand}.
     */
    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

        logger.info("Starting {}", this.getClass().getName());

        if (command instanceof NscsPropertyCommand) {

            // Check Algorithm-Type-Size if present in the command
            final KeyGeneratorCommand keygenCommand = (KeyGeneratorCommand) command;
            if (keygenCommand.getProperties().keySet().contains(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY)) {

                // Get supported algorithm and key size from model
                final Collection<String> supportedAlgorithmAndKeySize = nscsModelServiceImpl.getSupportedAlgorithmAndKeySize();
                logger.info("From model service: supportedAlgorithmAndKeySize is {}", supportedAlgorithmAndKeySize);

                if (!supportedAlgorithmAndKeySize.contains(keygenCommand.getAlgorithmTypeSize())) {
                    final String errmsg = String.format("Invalid argument for parameter %s. Accepted arguments are %s",
                            KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, supportedAlgorithmAndKeySize);
                    logger.error(errmsg);
                    throw new InvalidArgumentValueException(errmsg);
                }
            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsPropertyCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }

        logger.info("{} done", this.getClass().getName());
    }

}
