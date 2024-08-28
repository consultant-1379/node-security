package com.ericsson.nms.security.nscs.handler.command.impl

import javax.inject.Inject

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.ssh.SSHKeyNodeValidatorUtility
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Shared

class DeleteKeyGeneratorHandlerFcTest extends CdiSpecification {
    private static final String ALL_VALID_NODES_FORMAT = "Successfully started a job to delete SSH key."

    @ObjectUnderTest
    DeleteKeyGeneratorHandler deleteKeyGeneratorHandler

    @MockedImplementation
    NscsLogger nscsLogger

    @Inject
    KeyGeneratorCommand command

    @MockedImplementation
    private CommandContext context

    @Shared
    private JobStatusRecord jobStatusRecord

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    private SSHKeyNodeValidatorUtility mockNodeValidatorUtil

    def setup() {
        UUID jobId = UUID.randomUUID()
        jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    def "when delete ssh key command is called and all node are valid it returns a proper response message"() {
        given:
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        mockNodeValidatorUtil.validateSshKeyInputNodes(_ as List<NodeReference>, _ as Map<NodeReference, String>,
                _ as Map<String, NscsServiceException>, _ as String, null) >> true
        when:
        def response = (NscsMessageCommandResponse)deleteKeyGeneratorHandler.process(command, context)
        then:
        response.getMessage().contains(ALL_VALID_NODES_FORMAT)
    }
}
