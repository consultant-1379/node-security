/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.LdapRenewCommand;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;

@CommandType(NscsCommandType.LDAP_RENEW)
@Local(CommandHandlerInterface.class)
public class RenewLdapHandler implements CommandHandler<LdapRenewCommand>, CommandHandlerInterface {

    @Inject
    private LdapCommandHandlerHelper ldapCommandHandlerHelper;

    @Override
    public NscsCommandResponse process(final LdapRenewCommand command, final CommandContext context) {
        return ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW);
    }
}
