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

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

public class CheckUserTypeValidator implements CommandValidator {

    final public static String USER_TYPE = "usertype";

   private static final String ROOT_USER = "root";
   private static final String SECURE_USER = "secure";
   private static final String NORMAL_USER = "normal";
   private static final String NWIEA_SECURE_USER = "nwieasecure";
   private static final String NWIEB_SECURE_USER = "nwiebsecure";
   private static final String NODECLI_USER = "nodecli";

    @Inject
    private Logger logger;

    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

        logger.debug("Starting CheckAutoupdateEnableDisableValidator with command type: {}", command.getCommandType());

        final Map<String, Object> properties = command.getProperties();
        if (properties.containsKey(USER_TYPE)) {
            final String action = (String) properties.get(USER_TYPE);
            if (!(action.equals(ROOT_USER) || action.equals(SECURE_USER)|| action.equals(NORMAL_USER)|| action.equals(NWIEA_SECURE_USER)|| action.equals(NWIEB_SECURE_USER)|| action.equals(NODECLI_USER))) {
                logger.error("Got an unexpected action '{}' expecting " + ROOT_USER + " or " + SECURE_USER + " or " + NORMAL_USER + " or " + NWIEA_SECURE_USER + " or " + NWIEB_SECURE_USER + " or " + NODECLI_USER, action);
                throw new CommandSyntaxException();
            }
        }
        logger.debug("Command validated");
    }
}
