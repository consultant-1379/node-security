/*-----------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.manager

import com.ericsson.nms.security.nscs.api.exception.IpSecWfException
import com.ericsson.nms.security.nscs.api.exception.SshKeyWfException
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestWfsConfiguration
import com.ericsson.nms.security.nscs.ssh.SSHKeyWfsConfigurationDto

import java.util.Map.Entry

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.WorkflowHandler
import com.ericsson.nms.security.nscs.api.cert.issue.CertIssueWfParams
import com.ericsson.nms.security.nscs.api.exception.TestWfsException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentDetails
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentInfo
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.dto.WfResult

import spock.lang.Unroll

class NscsCommandManagerProcessorImplGroovyTest extends CdiSpecification {

    @ObjectUnderTest
    NscsCommandManagerProcessorImpl nscsCommandManagerProcessorImpl

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    WorkflowHandler wfHandler;

    @Unroll
    def "execute certificate issue workflow"() {
        given:
        def ExternalCAEnrollmentDetails extCaEnrollmentDetails = Mock(ExternalCAEnrollmentDetails)
        def ExternalCAEnrollmentInfo extCAEnrollmentInfo = Mock(ExternalCAEnrollmentInfo)
        extCAEnrollmentInfo.getExternalCAEnrollmentDetails() >> extCaEnrollmentDetails
        def Nodes.Node inputNode = Mock(Nodes.Node)
        inputNode.getNodeFdn() >> "NetworkElement=" + nodeName
        def CertIssueWfParams wfParams = new CertIssueWfParams(certType, "profile", "san", "sanType", "em", "ks", "cn")
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        nscsCapabilityModelService.getIssueOrReissueCertWf(_, wfParams.getCertType()) >> wf
        when:
        def WfResult wfResult = nscsCommandManagerProcessorImpl.executeCertificateIssueSingleWf(extCAEnrollmentInfo, inputNode, wfParams, isReissue, "reason", jobStatusRecord, 1)
        then:
        notThrown(Exception)
        where:
        nodeName | certType | isReissue | wf                           || expected
        "VDU"    | "OAM"    | true      | "CbpOiStartOnlineEnrollment" || null
        "VDU"    | "OAM"    | false     | "CbpOiStartOnlineEnrollment" || null
        "ECIM"   | "OAM"    | true      | "COMIssueCert"               || null
        "ECIM"   | "OAM"    | false     | "COMIssueCert"               || null
        "CPP"    | "OAM"    | true      | "CPPIssueCert"               || null
        "CPP"    | "OAM"    | false     | "CPPIssueCert"               || null
        "CPP"    | "IPSEC"  | true      | "CPPIssueCertIpSec"          || null
        "CPP"    | "IPSEC"  | false     | "CPPIssueCertIpSec"          || null
    }

    @Unroll
    def "execute certificate reissue workflow"() {
        given:
        def EntityInfo ei = Mock(EntityInfo)
        ei.getName() >> "entity"
        def Entity entity = Mock(Entity)
        entity.getEntityInfo() >> ei
        def NodeReference nodeReference = new NodeRef(nodeName)
        def Entry<Entity, NodeReference> entry = Mock()
        entry.getKey() >> entity
        entry.getValue() >> nodeReference
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        nscsCapabilityModelService.getIssueOrReissueCertWf(_, certType) >> wf
        when:
        def WfResult wfResult = nscsCommandManagerProcessorImpl.executeCertificateReIssueSingleWf(entry, "reason", certType, jobStatusRecord, 1)
        then:
        notThrown(Exception)
        where:
        nodeName | certType | wf                           || expected
        "VDU"    | "OAM"    | "CbpOiStartOnlineEnrollment" || null
        "VDU"    | "OAM"    | "CbpOiStartOnlineEnrollment" || null
        "ECIM"   | "OAM"    | "COMIssueCert"               || null
        "ECIM"   | "OAM"    | "COMIssueCert"               || null
        "CPP"    | "OAM"    | "CPPIssueCert"               || null
        "CPP"    | "OAM"    | "CPPIssueCert"               || null
        "CPP"    | "IPSEC"  | "CPPIssueCertIpSec"          || null
        "CPP"    | "IPSEC"  | "CPPIssueCertIpSec"          || null
    }

    @Unroll
    def "execute certificate reissue workflow for node reference"() {
        given:
        def NodeReference nodeReference = new NodeRef(nodeName)
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        nscsCapabilityModelService.getIssueOrReissueCertWf(_, certType) >> wf
        when:
        def WfResult wfResult = nscsCommandManagerProcessorImpl.executeCertificateReIssueSingleWf(nodeReference, "reason", certType, jobStatusRecord, 1)
        then:
        notThrown(Exception)
        where:
        nodeName | certType | wf                           || expected
        "VDU"    | "OAM"    | "CbpOiStartOnlineEnrollment" || null
        "VDU"    | "OAM"    | "CbpOiStartOnlineEnrollment" || null
        "ECIM"   | "OAM"    | "COMIssueCert"               || null
        "ECIM"   | "OAM"    | "COMIssueCert"               || null
        "CPP"    | "OAM"    | "CPPIssueCert"               || null
        "CPP"    | "OAM"    | "CPPIssueCert"               || null
        "CPP"    | "IPSEC"  | "CPPIssueCertIpSec"          || null
        "CPP"    | "IPSEC"  | "CPPIssueCertIpSec"          || null
    }

    @Unroll
    def "execute test single workflow for workflow ID #workflowId" () {
        given:
        def NodeReference nodeReference = new NodeRef("testNode")
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        when:
        def WfResult wfResult = nscsCommandManagerProcessorImpl.executeTestSingleWf(nodeReference, jobStatusRecord, workflowId)
        then:
        notThrown(Exception)
        where:
        workflowId << [1, 2, 3, 4, 5, 6, 7]
    }

    def "execute test single workflow with exception of workflow handler" () {
        given:
        def NodeReference nodeReference = new NodeRef("testNode")
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        wfHandler.getScheduledWorkflowInstanceResult(_, _, _, _, _) >> {throw new Exception()}
        when:
        def WfResult wfResult = nscsCommandManagerProcessorImpl.executeTestSingleWf(nodeReference, jobStatusRecord, 1)
        then:
        thrown(TestWfsException)
    }

    def "execute ipsec single workflow with success" () {
        given:
        NodeReference nodeReference = new NodeRef("testNode")
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration = new IpSecRequestWfsConfiguration()
        when:
        nscsCommandManagerProcessorImpl.executeIpSecWorkflow(nodeReference, ipSecRequestWfsConfiguration,
                jobStatusRecord, 1)
        then:
        notThrown(Exception)
    }

    def "execute ipsec single workflow with exception of workflow handler" () {
        given:
        NodeReference nodeReference = new NodeRef("testNode")
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration = new IpSecRequestWfsConfiguration()
        wfHandler.getScheduledWorkflowInstanceResult(_, _, _, _, _) >> {throw new Exception()}
        when:
        nscsCommandManagerProcessorImpl.executeIpSecWorkflow(nodeReference, ipSecRequestWfsConfiguration,
                jobStatusRecord, 1)
        then:
        thrown(IpSecWfException)
    }

    def "execute sshkey single workflow with success" () {
        given:
        NodeReference nodeReference = new NodeRef("testNode")
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        SSHKeyWfsConfigurationDto sshKeyWfsConfigurationDto = new SSHKeyWfsConfigurationDto()
        when:
        nscsCommandManagerProcessorImpl.executeSshKeyWorkflow(nodeReference, sshKeyWfsConfigurationDto,
                jobStatusRecord, 1)
        then:
        notThrown(Exception)
    }

    def "execute sshkey single workflow with exception of workflow handler" () {
        given:
        NodeReference nodeReference = new NodeRef("testNode")
        JobStatusRecord jobStatusRecord = new JobStatusRecord()
        SSHKeyWfsConfigurationDto sshKeyWfsConfigurationDto = new SSHKeyWfsConfigurationDto()
        wfHandler.getScheduledWorkflowInstanceResult(_, _, _, _, _) >> {throw new Exception()}
        when:
        nscsCommandManagerProcessorImpl.executeSshKeyWorkflow(nodeReference, sshKeyWfsConfigurationDto,
                jobStatusRecord, 1)
        then:
        thrown(SshKeyWfException)
    }

}
