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

class CreateKeyGeneratorHandlerFcTest extends CdiSpecification {

    private static final String ALL_VALID_NODES_FORMAT = "Successfully started a job for creating SSH key."

    @ObjectUnderTest
    CreateKeyGeneratorHandler createKeyGeneratorHandler

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

    def "when create ssh key command is called and all node are valid it returns a proper response message"() {
        given:
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        command.setProperties(["algorithm-type-size": "RSA_2048"])

        mockNodeValidatorUtil.validateSshKeyInputNodes(_ as List<NodeReference>, _ as Map<NodeReference, String>,
                _ as Map<String, NscsServiceException>, _ as String, _ as String) >> true
        when:
        def response = (NscsMessageCommandResponse)createKeyGeneratorHandler.process(command, context)
        then:
        response.getMessage().contains(ALL_VALID_NODES_FORMAT)
    }
}
