/*------------------------------------------------------------------------------
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

package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.moaction.MOActionService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.itpf.sdk.recording.EventLevel
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitTrustedCertIpSecEnrollmentTask
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory
import spock.lang.Shared

import java.nio.charset.StandardCharsets

class IssueInitTrustedCertIpSecEnrollmentTaskHandlerFcTest extends CdiSpecification {

    @ObjectUnderTest
    IssueInitTrustedCertIpSecEnrollmentTaskHandler taskHandler

    @MockedImplementation
    NscsCMReaderService readerService;

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    SystemRecorder systemRecorder;

    @MockedImplementation
    CppSecurityService securityService

    @MockedImplementation
    MOActionService moAction

    @MockedImplementation
    NormalizableNodeReference normNode

    @Shared
    def task = new IssueInitTrustedCertIpSecEnrollmentTask()

    static def nodeName = "ERBS0001"
    static def netsimM2mUserName = "mm-cert--1536871905"
    static def m2mHiddenWord = "HiddenWord"

    def setup() {
        task.getParameters().clear()
        NodeReference nodeRef = Mock()
        nodeRef.getName() >> nodeName
        readerService.getNormalizableNodeReference(_) >> normNode
        normNode.getFdn() >> nodeName
        task.setNode(nodeRef)
    }

    def 'When task handler is invoked and trust info is retrieved, then M2M user data is system recorded' () {
    given: 'TrustStore with SRMS account is retrieved from PKI'
        def smrsAccountInfo = new SmrsAccountInfo(netsimM2mUserName, m2mHiddenWord.toCharArray(), "localhost", "/home/smrs", "ERBS")
        def trustStoreInfo = new TrustStoreInfo(TrustedCertCategory.IPSEC, (Set)[], [smrsAccountInfo], DigestAlgorithm.SHA1)
        securityService.getTrustStoreForNode(TrustedCertCategory.IPSEC, _, true, TrustCategoryType.IPSEC) >> trustStoreInfo
    when: 'task handler is invoked'
        taskHandler.processTask(task)
    then: 'record is logged on system recorder with encoded M2M user data'
        1 * systemRecorder.recordEvent(_, EventLevel.COARSE, _, "node-security", "") >> {
            final String eventDescription = it[0]
            final String parameters = it[2]
            assert eventDescription.contains("[TORF480878]")
            assert parameters.contains(netsimM2mUserName)
            assert parameters.endsWith(Base64.getEncoder().encodeToString(m2mHiddenWord.getBytes(StandardCharsets.UTF_8)))
        }
    }

}
