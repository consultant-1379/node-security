package com.ericsson.nms.security.nscs.logger

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.recording.CommandPhase
import com.ericsson.oss.services.security.nscs.context.NscsContextService


class NscsRemoteEjbLoggerTest extends CdiSpecification {


    @ObjectUnderTest
    NscsRemoteEjbLogger nscsRemoteEjbLogger

    @MockedImplementation
    private NscsContextService nscsContextService

    @MockedImplementation
    NscsSystemRecorder nscsSystemRecorder



    def 'object under test' () {
        expect:
        nscsRemoteEjbLogger != null
    }

    def 'record remote ejb invocation started'() {
        given:
        nscsContextService.getClassNameContextValue() >> 'className'
        nscsContextService.getMethodNameContextValue() >> 'methodName'
        when:
        nscsRemoteEjbLogger.recordRemoteEjbStarted()
        then:
        1 * nscsRemoteEjbLogger.nscsSystemRecorder.recordCommand('EJB : className.methodName', CommandPhase.STARTED, 'Node Security Service', 'Node', '')
    }

    def 'record remote ejb invocation finished with success'() {
        given:
        nscsContextService.getClassNameContextValue() >> 'className'
        nscsContextService.getMethodNameContextValue() >> 'methodName'
        nscsContextService.getInputNodeNameContextValue() >> 'LTE01'
        when:
        nscsRemoteEjbLogger.recordRemoteEjbFinishedWithSuccess()
        then:
        1 * nscsRemoteEjbLogger.nscsSystemRecorder.recordCommand('EJB : className.methodName', CommandPhase.FINISHED_WITH_SUCCESS, 'Node Security Service', 'Node', 'Node [LTE01]')
    }

    def 'record remote ejb invocation finished with error'() {
        given:
        nscsContextService.getClassNameContextValue() >> 'className'
        nscsContextService.getMethodNameContextValue() >> 'methodName'
        nscsContextService.getInputNodeNameContextValue() >> 'LTE01'
        nscsContextService.getErrorDetailContextValue() >> 'err details'
        when:
        nscsRemoteEjbLogger.recordRemoteEjbFinishedWithError()
        then:
        1 * nscsRemoteEjbLogger.nscsSystemRecorder.recordCommand('EJB : className.methodName', CommandPhase.FINISHED_WITH_ERROR, 'Node Security Service', 'Node', 'Node [LTE01], Error Details [err details]')
    }

}
