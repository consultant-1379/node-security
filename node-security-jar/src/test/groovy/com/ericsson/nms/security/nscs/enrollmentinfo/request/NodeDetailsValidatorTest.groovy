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
package com.ericsson.nms.security.nscs.enrollmentinfo.request

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException
import com.ericsson.nms.security.nscs.api.exception.InvalidSubjAltNameXmlException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility

import spock.lang.Unroll

class NodeDetailsValidatorTest extends CdiSpecification {

    private static final String NODE = "NODE"

    private static final String CMPV2_VC = "CMPv2_VC"

    @ObjectUnderTest
    private NodeDetailsValidator nodeDetailsValidator

    @MockedImplementation
    private NscsCMReaderService nscsCmReaderService

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    private NodeValidatorUtility nodeValidatorUtility

    @MockedImplementation
    private NormalizableNodeReference normalized

    def "object under test"() {
        expect:
        nodeDetailsValidator != null
    }

    @Unroll
    def "validate valid OTP count #otpcount"() {
        given:
        def NodeDetails nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(NODE)
        nodeDetails.setOtpCount(otpcount)
        and:
        nscsCmReaderService.getNormalizedNodeReference(_) >> normalized
        normalized.getName() >> NODE
        nscsCapabilityModelService.isCertificateManagementSupported(_) >> true
        and:
        nscsCapabilityModelService.getDefaultEnrollmentMode(_) >> CMPV2_VC
        and:
        nodeValidatorUtility.isCertificateSupportedForNode(_) >> true
        and:
        nodeValidatorUtility.isCertificateTypeSupported(_, _) >> true
        when:
        def EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validate(nodeDetails)
        then:
        notThrown(Exception.class)
        and:
        enrollmentRequestInfo != null
        enrollmentRequestInfo.getNodeName() == NODE
        enrollmentRequestInfo.getOtpConfigurationParameters() != null
        enrollmentRequestInfo.getOtpConfigurationParameters().getOtpCount() == otpcount
        where:
        otpcount << [null, 0, 1, 5]
    }

    @Unroll
    def "validate invalid OTP count #otpcount"() {
        given:
        def NodeDetails nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(NODE)
        nodeDetails.setOtpCount(otpcount)
        and:
        nscsCmReaderService.getNormalizedNodeReference(_) >> normalized
        normalized.getName() >> NODE
        nscsCapabilityModelService.isCertificateManagementSupported(_) >> true
        and:
        nscsCapabilityModelService.getDefaultEnrollmentMode(_) >> CMPV2_VC
        and:
        nodeValidatorUtility.isCertificateSupportedForNode(_) >> true
        and:
        nodeValidatorUtility.isCertificateTypeSupported(_, _) >> true
        when:
        def EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validate(nodeDetails)
        then:
        thrown(InvalidArgumentValueException.class)
        where:
        otpcount << [-1, -100]
    }

    @Unroll
    def "validate valid OTP validity period in minutes #otpvalidity"() {
        given:
        def NodeDetails nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(NODE)
        nodeDetails.setOtpValidityPeriodInMinutes(otpvalidity)
        and:
        nscsCmReaderService.getNormalizedNodeReference(_) >> normalized
        normalized.getName() >> NODE
        nscsCapabilityModelService.isCertificateManagementSupported(_) >> true
        and:
        nscsCapabilityModelService.getDefaultEnrollmentMode(_) >> CMPV2_VC
        and:
        nodeValidatorUtility.isCertificateSupportedForNode(_) >> true
        and:
        nodeValidatorUtility.isCertificateTypeSupported(_, _) >> true
        when:
        def EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validate(nodeDetails)
        then:
        notThrown(Exception.class)
        and:
        enrollmentRequestInfo != null
        enrollmentRequestInfo.getNodeName() == NODE
        enrollmentRequestInfo.getOtpConfigurationParameters() != null
        enrollmentRequestInfo.getOtpConfigurationParameters().getOtpValidityPeriodInMinutes() == otpvalidity
        where:
        otpvalidity << [
            -1,
            1,
            5,
            1440,
            43200,
            null
        ]
    }

    @Unroll
    def "validate invalid OTP validity period in minutes #otpvalidity"() {
        given:
        def NodeDetails nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(NODE)
        nodeDetails.setOtpValidityPeriodInMinutes(otpvalidity)
        and:
        nscsCmReaderService.getNormalizedNodeReference(_) >> normalized
        normalized.getName() >> NODE
        nscsCapabilityModelService.isCertificateManagementSupported(_) >> true
        and:
        nscsCapabilityModelService.getDefaultEnrollmentMode(_) >> CMPV2_VC
        and:
        nodeValidatorUtility.isCertificateSupportedForNode(_) >> true
        and:
        nodeValidatorUtility.isCertificateTypeSupported(_, _) >> true
        when:
        def EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validate(nodeDetails)
        then:
        thrown(InvalidArgumentValueException.class)
        where:
        otpvalidity << [
            -2,
            0,
            43201
        ]
    }
    @Unroll
    def "validate valid SAN Type #subjectaltname #subjectaltnametype"() {
        given:
        NodeDetails nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(NODE)
        nodeDetails.setSubjectAltName(subjectaltname)
        nodeDetails.setSubjectAltNameType(subjectaltnametype)
        and:
        nscsCmReaderService.getNormalizedNodeReference(_) >> normalized
        normalized.getName() >> NODE
        nscsCapabilityModelService.isCertificateManagementSupported(_) >> true
        and:
        nscsCapabilityModelService.getDefaultEnrollmentMode(_) >> CMPV2_VC
        and:
        nodeValidatorUtility.isCertificateSupportedForNode(_) >> true
        and:
        nodeValidatorUtility.isCertificateTypeSupported(_, _) >> true
        when:
        def EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validate(nodeDetails)
        then:
        notThrown(Exception.class)
        and:
        enrollmentRequestInfo != null
        enrollmentRequestInfo.getNodeName() == NODE
        enrollmentRequestInfo.getOtpConfigurationParameters() != null
        enrollmentRequestInfo.getSubjectAltNameParam().getSubjectAltNameData().toString() == subjectaltname
        where:
        subjectaltname << [
                "5G139vDURI001",
                "user@5G139vDURI001.ie"
        ]
        subjectaltnametype << [
                "DNS_NAME",
                "RFC822_NAME"
        ]
    }

    @Unroll
    def "validate invalid SAN Type #subjectaltname #subjectaltnametype"() {
        given:
        NodeDetails nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(NODE)
        nodeDetails.setSubjectAltName(subjectaltname)
        nodeDetails.setSubjectAltNameType(subjectaltnametype)
        and:
        nscsCmReaderService.getNormalizedNodeReference(_) >> normalized
        normalized.getName() >> NODE
        nscsCapabilityModelService.isCertificateManagementSupported(_) >> true
        and:
        nscsCapabilityModelService.getDefaultEnrollmentMode(_) >> CMPV2_VC
        and:
        nodeValidatorUtility.isCertificateSupportedForNode(_) >> true
        and:
        nodeValidatorUtility.isCertificateTypeSupported(_, _) >> true
        when:
        def EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validate(nodeDetails)
        then:
        thrown(InvalidSubjAltNameXmlException.class)
        where:
        subjectaltname << [
                "5G139vDURI001..",
                "user@5G139vDURI001"
        ]
        subjectaltnametype << [
                "DNS_NAME",
                "RFC822_NAME"
        ]
    }
}
