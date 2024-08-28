package com.ericsson.nms.security.nscs.workflow.task.node.attribute

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.DeleteLdapProxyAccountTask
import com.ericsson.oss.services.security.nscs.workflow.task.util.LdapWorkflowHelper

import spock.lang.Unroll

class DeleteLdapProxyAccountTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private DeleteLdapProxyAccountTaskHandler taskHandler

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private LdapWorkflowHelper ldapWorkflowHelper

    private DeleteLdapProxyAccountTask task = new DeleteLdapProxyAccountTask()

    def 'object under test'() {
        expect:
        taskHandler != null
    }

    @Unroll
    def 'process task with is renew #isrenew and no previous proxy account to delete'() {
        given:
        task.setIsRenew(isrenew)
        task.setLdapWorkflowContext([:])
        when:
        def result = taskHandler.processTask(task)
        then:
        noExceptionThrown()
        and:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, _, _)
        and:
        result == "NO_PREVIOUS_BIND_DN_TO_DELETE"
        where:
        isrenew << [true, false]
    }

    @Unroll
    def 'process task with is renew #isrenew and empty or null previous proxy account #prevbinddn to delete'() {
        given:
        task.setIsRenew(isrenew)
        task.setLdapWorkflowContext(["previousBindDn":prevbinddn])
        when:
        def result = taskHandler.processTask(task)
        then:
        noExceptionThrown()
        and:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, _, _)
        and:
        result == "NO_PREVIOUS_BIND_DN_TO_DELETE"
        where:
        isrenew << [true, false, true, false]
        prevbinddn << ["", null, null, ""]
    }

    def 'process task with is renew true and previous proxy account to delete'() {
        given:
        task.setIsRenew(true)
        task.setLdapWorkflowContext(["previousBindDn":"cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com"])
        and:
        ldapWorkflowHelper.deleteProxyAccount("cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com") >> true
        when:
        def result = taskHandler.processTask(task)
        then:
        noExceptionThrown()
        and:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, _, _)
        and:
        result == "PREVIOUS_BIND_DN_DELETED"
    }

    def 'process task with is renew false and previous proxy account to delete'() {
        given:
        task.setIsRenew(false)
        task.setLdapWorkflowContext(["previousBindDn":"cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com"])
        and:
        ldapWorkflowHelper.deleteProxyAccount("cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com") >> true
        when:
        def result = taskHandler.processTask(task)
        then:
        noExceptionThrown()
        and:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, _, _)
        and:
        result == "PREVIOUS_BIND_DN_NOT_DELETED"
    }

    def 'process task with is renew true and not existent previous proxy account to delete'() {
        given:
        task.setIsRenew(true)
        task.setLdapWorkflowContext(["previousBindDn":"cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com"])
        and:
        ldapWorkflowHelper.deleteProxyAccount("cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com") >> false
        when:
        def result = taskHandler.processTask(task)
        then:
        thrown(NscsLdapProxyException.class)
        and:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithError(task, _)
    }
}
