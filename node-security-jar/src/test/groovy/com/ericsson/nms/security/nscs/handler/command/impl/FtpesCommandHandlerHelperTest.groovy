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

package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.FtpesCommand
import com.ericsson.nms.security.nscs.api.exception.FtpesActivateOrDeactivateWfException
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.util.FtpesCommandType
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler
import spock.lang.Shared

import javax.inject.Inject

class FtpesCommandHandlerHelperTest extends CdiSpecification {

    @ObjectUnderTest
    private FtpesCommandHandlerHelper commandHandlerHelper

    @MockedImplementation
    private CommandContext context

    @MockedImplementation
    private NscsLogger nscsLogger;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler

    @Shared
    private JobStatusRecord record

    @Shared
    private UUID JOB_UUID

    @Shared
    private FtpesCommand command

    @Shared
    private String expectedActivateResult

    @Shared
    private String expectedDeactivateResult


    def setupSpec() {
        record = new JobStatusRecord()
        JOB_UUID = UUID.randomUUID()
        record.setJobId(JOB_UUID)
        command = new FtpesCommand()
        expectedActivateResult = "Successfully started a job for FTPES activate operation. Perform 'secadm job get -j " +JOB_UUID+
                "' to get progress info."
        expectedDeactivateResult = "Successfully started a job for FTPES deactivate operation. Perform 'secadm job get -j " +JOB_UUID+
                "' to get progress info."
    }

    def "When everything is correct task should activate FTPES"() {
        given:
        nscsJobCacheHandler.insertJob(FtpesCommandType.ACTIVATE.getNscsCommandType()) >> { return record }

        when:
        NscsMessageCommandResponse response = commandHandlerHelper.processActivate(command,context)

        then:
        response.message == expectedActivateResult
    }

    def "When everything is correct task should deactivate FTPES"(){
        given:
        nscsJobCacheHandler.insertJob(FtpesCommandType.DEACTIVATE.getNscsCommandType()) >> { return record }

        when:
        NscsMessageCommandResponse response = commandHandlerHelper.processDeactivate(command,context)

        then:
        response.message == expectedDeactivateResult
    }

    def "When we couldn't execute operation task should throw FtpesActivateOrDeactivateWfException"(){
        given:
        nscsJobCacheHandler.insertJob(FtpesCommandType.DEACTIVATE.getNscsCommandType()) >> { throw new FtpesActivateOrDeactivateWfException() }

        when:
        commandHandlerHelper.processDeactivate(command,context)

        then:
        thrown FtpesActivateOrDeactivateWfException
    }

}