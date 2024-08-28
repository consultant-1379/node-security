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
package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.LdapReConfigurationCommand
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.oss.services.dto.JobStatusRecord
import spock.lang.Shared

import javax.inject.Inject

class ReconfigureLdapHandlerTest extends  CdiSpecification {

    @ObjectUnderTest
    ReconfigureLdapHandler reconfigureLdapHandler

    @Inject
    LdapReConfigurationCommand command

    @MockedImplementation
    CommandContext context

    @Shared
    private JobStatusRecord record

    @Shared
    private UUID JOB_UUID

    @Shared
    String responseMessageWithJobId

    def setup() {
        record = new JobStatusRecord()
        JOB_UUID = UUID.randomUUID()
        record.setJobId(JOB_UUID)
    }

    @ImplementationInstance
    LdapCommandHandlerHelper ldapCommandHandlerHelper = [
            processActivate : { command, context, commandType ->
                responseMessageWithJobId = String.format(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RECONFIGURE.getExecutedMessage(),
                        record.getJobId().toString())
                NscsCommandResponse.message(responseMessageWithJobId)
            }

    ] as LdapCommandHandlerHelper

    def 'call process successfully' () {
        given:
            reconfigureLdapHandler.ldapCommandHandlerHelper = ldapCommandHandlerHelper
        when:
            NscsMessageCommandResponse response = reconfigureLdapHandler.process(command,context) as NscsMessageCommandResponse
        then:
            response.getMessage().contains(responseMessageWithJobId)

    }

}
