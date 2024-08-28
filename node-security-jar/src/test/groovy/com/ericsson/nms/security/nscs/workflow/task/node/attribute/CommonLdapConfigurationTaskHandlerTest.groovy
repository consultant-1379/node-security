package com.ericsson.nms.security.nscs.workflow.task.node.attribute

import static org.junit.Assert.*

import org.junit.Test

import com.ericsson.cds.cdi.support.rule.ImplementationClasses
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.ldap.utility.NscsObjectSerializerUtility
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.services.cm.cmwriter.api.CmWriterService
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.PerformSyncMoActionTask
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapServiceFactory
import com.ericsson.oss.services.security.nscs.workflow.task.util.LdapWorkflowHelper

class CommonLdapConfigurationTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private CommonLdapConfigurationTaskHandler taskHandler

    @MockedImplementation
    private NscsLogger nscsLogger
    
    @MockedImplementation
    private MOLdapServiceFactory moLdapServiceFactory
    
    @MockedImplementation
    private LdapWorkflowHelper ldapWorkflowHelper

    @MockedImplementation
    private NscsObjectSerializerUtility nscsObjectSerializerUtility

    @ImplementationInstance
    private NscsCMReaderService readerService = [
        getNormalizableNodeReference : { NodeReference nodeReference ->
            return normalizable
        }
    ] as NscsCMReaderService

    private CommonLdapConfigurationTask task= new CommonLdapConfigurationTask(nodeName)
    private nodeName = "NODENAME"
    private NormalizableNodeReference normalizable = mock(NormalizableNodeReference)

    def 'object under test'() {
        expect:
        taskHandler != null
    }

    def 'process task with success'() {
        given:
        ldapWorkflowHelper.getLdapConfiguration(task, normalizable) >> { return }
        when:
        def result = taskHandler.processTask(task)
        then:
        noExceptionThrown()
        and:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, _)
    }

    def 'process task with error'() {
        given:
        ldapWorkflowHelper.getLdapConfiguration(task, normalizable) >> { throw new Exception() }
        when:
        def result = taskHandler.processTask(task)
        then:
        thrown(Exception.class)
        and:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithError(task, _)
    }
}
