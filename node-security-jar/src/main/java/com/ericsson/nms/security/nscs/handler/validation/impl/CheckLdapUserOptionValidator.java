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
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

public class CheckLdapUserOptionValidator implements CommandValidator {

    final public static String LDAPUSER = CredentialsCommand.LDAP_USER_ENABLE_PROPERTY;

   private static final String ENABLE = "enable";
   private static final String DISABLE = "disable";

    @Inject
    private Logger logger;

    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

        logger.debug("Starting CheckLdapUserEnableDisableValidator with command type: {}", command.getCommandType());

        final Map<String, Object> properties = command.getProperties();
        if (properties.containsKey(LDAPUSER)) {
            final String action = (String) properties.get(LDAPUSER);
            if (!(action.equals(ENABLE) || action.equals(DISABLE))) {
                logger.error("Got an unexpected action '{}' expecting " + ENABLE + " or " + DISABLE, action);
                throw new CommandSyntaxException();
            }
        }
        logger.debug("Command validated");
    }
}
