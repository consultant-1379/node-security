/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.service

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.CertificateType
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo
import com.ericsson.nms.security.nscs.iscf.IscfServiceValidators
import com.ericsson.nms.security.nscs.iscf.SecurityDataCollector

class EnrollmentInfoServiceBeanTest extends CdiSpecification {

    @ObjectUnderTest
    private EnrollmentInfoServiceBean enrollmentInfoServiceBean
    
    @MockedImplementation
    private IscfServiceValidators iscfServiceValidators

    @MockedImplementation
    private SecurityDataCollector securityDataCollector

    @MockedImplementation
    private SecurityDataResponse securityDataResponse

    def 'object under test'() {
        expect:
        enrollmentInfoServiceBean != null
    }

    def 'successful execution'() {
        given:
        def NodeModelInformation modelInfo = mock(NodeModelInformation)
        def NodeIdentifier nodeIdentifier = mock(NodeIdentifier)
        nodeIdentifier.getFdn() >> "NODE"
        def EnrollmentRequestInfo enrollmentRequestInfo = mock(EnrollmentRequestInfo)
        enrollmentRequestInfo.getNodeIdentifier() >> nodeIdentifier
        and:
        securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), modelInfo, enrollmentRequestInfo) >> securityDataResponse
        when:
        def SecurityDataResponse response = enrollmentInfoServiceBean.generateSecurityDataOam(modelInfo, enrollmentRequestInfo)
        then:
        notThrown(Exception.class)
        and:
        response != null
    }

    def 'failed execution'() {
        given:
        def NodeModelInformation modelInfo = mock(NodeModelInformation)
        def NodeIdentifier nodeIdentifier = mock(NodeIdentifier)
        nodeIdentifier.getFdn() >> "NODE"
        def EnrollmentRequestInfo enrollmentRequestInfo = mock(EnrollmentRequestInfo)
        enrollmentRequestInfo.getNodeIdentifier() >> nodeIdentifier
        and:
        securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), modelInfo, enrollmentRequestInfo) >> { throw new IscfServiceException()}
        when:
        def SecurityDataResponse response = enrollmentInfoServiceBean.generateSecurityDataOam(modelInfo, enrollmentRequestInfo)
        then:
        thrown(EnrollmentInfoServiceException.class)
    }
}
