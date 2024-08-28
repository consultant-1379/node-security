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
package com.ericsson.nms.security.nscs.workflow.task.proto

import java.security.cert.CertificateException

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.CACertSftpPublisher
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.proto.ProtoTask

class ProtoTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    ProtoTaskHandler taskHandler

    @MockedImplementation
    CACertSftpPublisher caCertSftpPublisher

    private ProtoTask task

    private nodeName = "proto001"

    def "Proto task handler with certs successfully published" () {
        given: "task for node"
        task = new ProtoTask("MeContext=" + nodeName, "LRAN", "ERBS")
        when: "processing the task"
        taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "caCertSftpPublisher is invoked"
        1 * caCertSftpPublisher.publishCertificates("ERBS", nodeName)
    }

    def "Proto task handler with CertificateException while publishing certs" () {
        given: "task for node"
        task = new ProtoTask("MeContext=" + nodeName, "LRAN", "ERBS")
        and: "caCertSftpPublisher throwing #exception"
        caCertSftpPublisher.publishCertificates(_, _) >> {throw new CertificateException()}
        when: "processing the task"
        taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "caCertSftpPublisher is invoked"
        1 * caCertSftpPublisher.publishCertificates("ERBS", nodeName)
    }

    def "Proto task handler with NscsPkiEntitiesManagerException while publishing certs" () {
        given: "task for node"
        task = new ProtoTask("MeContext=" + nodeName, "LRAN", "ERBS")
        and: "caCertSftpPublisher throwing #exception"
        caCertSftpPublisher.publishCertificates(_, _) >> {throw new NscsPkiEntitiesManagerException()}
        when: "processing the task"
        taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "caCertSftpPublisher is invoked"
        1 * caCertSftpPublisher.publishCertificates("ERBS", nodeName)
    }
}
