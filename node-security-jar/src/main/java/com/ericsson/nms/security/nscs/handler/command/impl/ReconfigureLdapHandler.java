/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.LdapReConfigurationCommand;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;

import javax.ejb.Local;
import javax.inject.Inject;

@CommandType(NscsCommandType.LDAP_RECONFIGURATION)
@Local(CommandHandlerInterface.class)
public class ReconfigureLdapHandler implements CommandHandler<LdapReConfigurationCommand>, CommandHandlerInterface {

    @Inject
    private LdapCommandHandlerHelper ldapCommandHandlerHelper;

    @Override
    public NscsCommandResponse process(LdapReConfigurationCommand command, CommandContext context) {
        return ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RECONFIGURE);
    }
}