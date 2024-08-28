/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

// This validator will be replaced with CheckPlainTextValidator when this parameter will be optional
public class CheckPlainTextSNMPValidator implements CommandValidator {

    final public static String PLAIN_TEXT = "plaintext";

   private static final String SHOW = "show";

    @Inject
    private Logger logger;

    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

        logger.debug("Starting CheckPlainTextSNMPValidator with command type: {}", command.getCommandType());

        final Map<String, Object> properties = command.getProperties();
        if (properties.containsKey(PLAIN_TEXT)) {
            final String action = (String) properties.get(PLAIN_TEXT);
            if (!(action.equals(SHOW))) {
                logger.error("Got an unexpected action '{}' expecting " + SHOW, action);
                throw new CommandSyntaxException();
            }
        }
        logger.debug("Command validated");
    }
}
