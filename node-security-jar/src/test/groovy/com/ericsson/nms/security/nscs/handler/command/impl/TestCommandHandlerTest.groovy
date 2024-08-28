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
package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.types.TestCommand
import com.ericsson.nms.security.nscs.api.exception.TestWfsException
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

class TestCommandHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private TestCommandHandler testCommandHandler

    @MockedImplementation
    private CommandContext context

    @MockedImplementation
    private NscsJobCacheHandler cacheHandler

    private TestCommand command

    def 'setup' () {
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(UUID.randomUUID())
        cacheHandler.insertJob(NscsCommandType.TEST_COMMAND) >> jobStatusRecord
    }

    def 'object under test'() {
        expect:
        testCommandHandler != null
    }

    def 'process sync test command'() {
        given:
        command = new TestCommand()
        command.setCommandType(NscsCommandType.TEST_COMMAND)
        when:
        def response = testCommandHandler.process(command, context)
        then:
        response != null
    }

    def 'process async test command with valid'() {
        given:
        command = new TestCommand()
        command.setCommandType(NscsCommandType.TEST_COMMAND)
        command.setProperties(["workflows": "1"])
        when:
        def response = testCommandHandler.process(command, context)
        then:
        response != null
    }

    def 'process async test command with valid and invalid'() {
        given:
        command = new TestCommand()
        command.setCommandType(NscsCommandType.TEST_COMMAND)
        command.setProperties(["workflows": "-1"])
        when:
        def response = testCommandHandler.process(command, context)
        then:
        response != null
    }

    def 'process async test command without valid'() {
        given:
        command = new TestCommand()
        command.setCommandType(NscsCommandType.TEST_COMMAND)
        command.setProperties(["workflows": "0"])
        when:
        def response = testCommandHandler.process(command, context)
        then:
        response != null
    }

    def 'process async test command with wrong params'() {
        given:
        command = new TestCommand()
        command.setCommandType(NscsCommandType.TEST_COMMAND)
        command.setProperties(["workflows": "wrong"])
        when:
        def response = testCommandHandler.process(command, context)
        then:
        thrown(TestWfsException.class)
    }

    def 'process async test command with exception'() {
        given:
        command = new TestCommand()
        command.setCommandType(NscsCommandType.TEST_COMMAND)
        command.setProperties(["workflows": "wrong"])
        cacheHandler.insertJob(NscsCommandType.TEST_COMMAND) >> { throw new NullPointerException() }
        when:
        def response = testCommandHandler.process(command, context)
        then:
        thrown(TestWfsException.class)
    }
}
