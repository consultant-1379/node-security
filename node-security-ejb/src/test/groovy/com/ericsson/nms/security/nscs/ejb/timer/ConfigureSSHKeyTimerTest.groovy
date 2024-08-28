package com.ericsson.nms.security.nscs.ejb.timer

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenCommand
import com.ericsson.nms.security.nscs.workflow.task.ssh.ConfigureSSHKeyTimerDto
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender
import com.ericsson.oss.mediation.sec.model.SSHCommandResult
import com.ericsson.oss.mediation.sec.model.SSHCommandSuccess

import javax.ejb.Timer
import javax.ejb.TimerConfig
import javax.ejb.TimerService

class ConfigureSSHKeyTimerTest extends CdiSpecification{

    @ObjectUnderTest
    private ConfigureSSHKeyTimer configureSSHKeyTimer

    def duration = 1000
    def nes = "NetworkElement=SGSN10CORE001,SecurityFunction=1,NetworkElementSecurity=1"
    def nodeName = "SGSN10CORE001"
    def sshkeyOperation = "Delete_key"
    def configureSSHKeyTimerDto = new ConfigureSSHKeyTimerDto(nodeName, nes, sshkeyOperation)

    @MockedImplementation
    private TimerService timerService

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private EventSender<SSHCommandResult> sshKeyResultSuccessJob;

    @ImplementationInstance
    private Timer timerMock = [
            getInfo : {
                return configureSSHKeyTimerDto
            }
    ] as Timer

    @ImplementationInstance
    private EventSender<SSHCommandResult> sshKeyResultSuccessJobMockExcp = [
            send : { resultEvent ->
                Exception excp = new Exception()
                throw excp
            }
    ] as EventSender<SSHCommandResult>

    def setup() {}

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}


    def "when startSSHKeyTimer is called then createSingleActionTimer is performed properly"() {
        given:
        when:
        configureSSHKeyTimer.startSSHKeyTimer(duration, configureSSHKeyTimerDto)
        then:
        1 * timerService.createSingleActionTimer (1000, _ as TimerConfig)
    }

    def "when handleSSHKeyTimeout is called then SSHCommandSuccess message is sent "() {
        given:
        when:
        configureSSHKeyTimer.handleSSHKeyTimeout(timerMock)
        then:
        1 * sshKeyResultSuccessJob.send (_ as SSHCommandSuccess)
    }

    def "when handleSSHKeyTimeout raise an exception then logger error is called "() {
        given:
        configureSSHKeyTimer.sshKeyResultSuccessJob = sshKeyResultSuccessJobMockExcp
        when:
        configureSSHKeyTimer.handleSSHKeyTimeout(timerMock)
        then:
        2 * nscsLogger.error (_ as String, _ as Object )
    }

}
