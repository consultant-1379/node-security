/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.manager

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.CppIpSecWfsConfiguration
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequest
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestWfsConfiguration
import com.ericsson.nms.security.nscs.ssh.SSHKeyRequestDto
import com.ericsson.nms.security.nscs.ssh.SSHKeyWfsConfigurationDto
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.dto.WfResult
import spock.lang.Unroll

import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED

class NscsCommandManagerBeanGroovyTest extends CdiSpecification {

    @ObjectUnderTest
    NscsCommandManagerBean nscsCommandManagerBean

    @MockedImplementation
    NscsCommandManagerProcessor nscsCommandManagerProcessorImpl

    @MockedImplementation
    CppIpSecWfsConfiguration cppIpSecWfsConfiguration

    def "test execute test workflows" () {
        given:
        NodeReference nodeReference = new NodeRef("TEST_WFS_1")
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        nscsCommandManagerProcessorImpl.executeTestSingleWf(nodeReference, jobStatusRecord, 1) >> new WfResult()
        when:
        nscsCommandManagerBean.executeTestWfs(1, jobStatusRecord)
        then:
        notThrown(Exception)
    }

    def "test execute test workflows no results" () {
        given:
        NodeReference nodeReference = new NodeRef("TEST_WFS_1")
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        nscsCommandManagerProcessorImpl.executeTestSingleWf(nodeReference, jobStatusRecord, 1) >> null
        when:
        nscsCommandManagerBean.executeTestWfs(1, jobStatusRecord)
        then:
        notThrown(Exception)
    }

    def "execute ipsec workflows" () {
        given:
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        List<IpSecRequest> requests = new ArrayList<>()
        requests.add(new IpSecRequest())

        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration = new IpSecRequestWfsConfiguration()
        ipSecRequestWfsConfiguration.setNodeFdn("nodeFdn")
        cppIpSecWfsConfiguration.configureIpSecWorkflow(_ as IpSecRequest) >> ipSecRequestWfsConfiguration

        nscsCommandManagerProcessorImpl.executeIpSecWorkflow(_ as NodeReference, _ as IpSecRequestWfsConfiguration,
                jobStatusRecord, 1) >> new WfResult()
        when:
        nscsCommandManagerBean.executeIpSecWorkflows(requests, jobStatusRecord)
        then:
        notThrown(Exception)
    }

    def "execute ipsec workflows no result" () {
        given:
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        List<IpSecRequest> requests = new ArrayList<>()
        requests.add(new IpSecRequest())

        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration = new IpSecRequestWfsConfiguration()
        ipSecRequestWfsConfiguration.setNodeFdn("nodeFdn")
        cppIpSecWfsConfiguration.configureIpSecWorkflow(_ as IpSecRequest) >> ipSecRequestWfsConfiguration

        nscsCommandManagerProcessorImpl.executeIpSecWorkflow(_ as NodeReference, _ as IpSecRequestWfsConfiguration,
                jobStatusRecord, 1) >> null
        when:
        nscsCommandManagerBean.executeIpSecWorkflows(requests, jobStatusRecord)
        then:
        notThrown(Exception)
    }

    @Unroll
    def "execute sshkey workflows" () {
        given:
        JobStatusRecord jobStatusRecord = new JobStatusRecord()

        SSHKeyRequestDto request = new SSHKeyRequestDto()
        request.setFdn("SSH_KEY_NODE_WFS_1")
        request.setSshkeyOperation(command)
        request.setAlgorithm("RSA_1024")
        List<SSHKeyRequestDto> requests = new ArrayList<>()
        requests.add(request)

        nscsCommandManagerProcessorImpl.executeSshKeyWorkflow(_ as NodeReference, _ as SSHKeyWfsConfigurationDto,
                jobStatusRecord, 1) >> new WfResult()
        when:
        nscsCommandManagerBean.executeSshKeyWorkflows(requests, jobStatusRecord)
        then:
        notThrown(exception)
        where:
        command | exception
        SSH_KEY_TO_BE_CREATED | Exception
        SSH_KEY_TO_BE_UPDATED | Exception
        SSH_KEY_TO_BE_DELETED | Exception
    }

    def "execute sshkey workflows no result" () {
        given:
        JobStatusRecord jobStatusRecord = new JobStatusRecord()

        SSHKeyRequestDto request = new SSHKeyRequestDto()
        request.setFdn("SSH_KEY_NODE_WFS_1")
        request.setSshkeyOperation(SSH_KEY_TO_BE_CREATED)
        request.setAlgorithm("RSA_1024")
        List<SSHKeyRequestDto> requests = new ArrayList<>()
        requests.add(request)

        nscsCommandManagerProcessorImpl.executeSshKeyWorkflow(_ as NodeReference , _ as SSHKeyWfsConfigurationDto,
                _ as JobStatusRecord, 1) >> null
        when:
        nscsCommandManagerBean.executeSshKeyWorkflows(requests, jobStatusRecord )
        then:
        notThrown(Exception)
    }
}
