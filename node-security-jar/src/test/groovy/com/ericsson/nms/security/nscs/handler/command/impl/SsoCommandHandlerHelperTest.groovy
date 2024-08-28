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

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.SsoCommand
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger

import spock.lang.Shared

class SsoCommandHandlerHelperTest extends CdiSpecification {

    @ObjectUnderTest
    private SsoCommandHandlerHelper commandHandlerHelper

    @MockedImplementation
    private CommandContext context

    @MockedImplementation
    private NscsLogger nscsLogger;

    @Shared
    private SsoCommand command

    @Shared
    private String expectedResult



    def setupSpec() {
        command = new SsoCommand()
        expectedResult = "SSO attribute updated successfully"
    }

    def "When everything is correct task should enable SSO"() {

        when:
        NscsMessageCommandResponse response = commandHandlerHelper.process(command,context,true)

        then:
        response.message == expectedResult
    }

    def "When everything is correct task should disable SSO"(){

        when:
        NscsMessageCommandResponse response = commandHandlerHelper.process(command,context,false)

        then:
        response.message == expectedResult
    }
}

