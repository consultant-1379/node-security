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
package com.ericsson.nms.security.nscs.certificate.issue.input.xml

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetailsList
import com.ericsson.oss.services.security.nscs.command.enrollmentinfo.OtpConfigurationParameters

import spock.lang.Unroll

class EnrollmentRequestInfoTest extends CdiSpecification {

    private static final String NODE = "NODE"
    private static final String NODE_EP = "NODE_EP"
    private static final String OTHER_NODE_EP = "OTHER_ODE_EP"
    private static final String COMMON_NAME = "COMMON_NAME"
    private static final EnrollmentMode ENROLLMENT_MODE_CMPV2_VC = EnrollmentMode.CMPv2_VC
    private static final EnrollmentMode ENROLLMENT_MODE_CMPV2_INITIAL = EnrollmentMode.CMPv2_INITIAL
    private static final NodeIdentifier NODE_IDENTIFIER = new NodeIdentifier(NODE, null)

    def 'constructor'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        expect:
        enrollmentRequestInfo != null
        enrollmentRequestInfo.getNodeIdentifier() == null
        enrollmentRequestInfo.getCertType() == null
        enrollmentRequestInfo.getEntityProfile() == null
        enrollmentRequestInfo.getKeySize() == null
        enrollmentRequestInfo.getCommonName() == null
        enrollmentRequestInfo.getEnrollmentMode() == null
        enrollmentRequestInfo.getSubjectAltNameParam() == null
        enrollmentRequestInfo.getNodeName() == null
        enrollmentRequestInfo.getIpVersion() == null
        enrollmentRequestInfo.getOtpConfigurationParameters() == null
        enrollmentRequestInfo.toString() != null
    }

    def 'equality to itself'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        expect:
        enrollmentRequestInfo.equals(enrollmentRequestInfo) == true
    }

    def 'equality to null'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        expect:
        enrollmentRequestInfo.equals(null) == false
    }

    def 'equality to other class'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        def NodeDetailsList nodeDetailsList = new NodeDetailsList()
        expect:
        enrollmentRequestInfo.equals(nodeDetailsList) == false
    }

    @Unroll
    def 'equality by entity profile name #entityprofilename vs #otherentityprofilename'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        enrollmentRequestInfo.setEntityProfile(entityprofilename)
        and:
        def EnrollmentRequestInfo otherEnrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        otherEnrollmentRequestInfo.setEntityProfile(otherentityprofilename)
        expect:
        enrollmentRequestInfo.equals(otherEnrollmentRequestInfo) == isequal
        and:
        if (isequal) {
            enrollmentRequestInfo.hashCode() == otherEnrollmentRequestInfo.hashCode()
        } else {
            enrollmentRequestInfo.hashCode() != otherEnrollmentRequestInfo.hashCode()
        }
        and:
        enrollmentRequestInfo.toString() != null
        where:
        entityprofilename << [
            NODE_EP,
            null,
            NODE_EP,
            null,
            NODE_EP
        ]
        otherentityprofilename << [
            NODE_EP,
            null,
            null,
            NODE_EP,
            OTHER_NODE_EP
        ]
        isequal << [
            true,
            true,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by key size #keysize vs #otherkeysize'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        enrollmentRequestInfo.setKeySize(keysize)
        and:
        def EnrollmentRequestInfo otherEnrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        otherEnrollmentRequestInfo.setKeySize(otherkeysize)
        expect:
        enrollmentRequestInfo.equals(otherEnrollmentRequestInfo) == isequal
        and:
        if (isequal) {
            enrollmentRequestInfo.hashCode() == otherEnrollmentRequestInfo.hashCode()
        } else {
            enrollmentRequestInfo.hashCode() != otherEnrollmentRequestInfo.hashCode()
        }
        and:
        enrollmentRequestInfo.toString() != null
        where:
        keysize << [
            "the key size",
            null,
            "the key size",
            null,
            "the key size"
        ]
        otherkeysize << [
            "the key size",
            null,
            null,
            "the key size",
            "the other key size"
        ]
        isequal << [
            true,
            true,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by common name #commonname vs #othercommonname'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        enrollmentRequestInfo.setCommonName(commonname)
        and:
        def EnrollmentRequestInfo otherEnrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        otherEnrollmentRequestInfo.setCommonName(othercommonname)
        expect:
        enrollmentRequestInfo.equals(otherEnrollmentRequestInfo) == isequal
        and:
        if (isequal) {
            enrollmentRequestInfo.hashCode() == otherEnrollmentRequestInfo.hashCode()
        } else {
            enrollmentRequestInfo.hashCode() != otherEnrollmentRequestInfo.hashCode()
        }
        and:
        enrollmentRequestInfo.toString() != null
        where:
        commonname << [
            "the common name",
            null,
            "the common name",
            null,
            "the common name"
        ]
        othercommonname << [
            "the common name",
            null,
            null,
            "the common name",
            "the other common name"
        ]
        isequal << [
            true,
            true,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by enrollment mode #enrollmentmode vs #otherenrollmentmode'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        enrollmentRequestInfo.setEnrollmentMode(enrollmentmode)
        and:
        def EnrollmentRequestInfo otherEnrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        otherEnrollmentRequestInfo.setEnrollmentMode(otherenrollmentmode)
        expect:
        enrollmentRequestInfo.equals(otherEnrollmentRequestInfo) == isequal
        and:
        if (isequal) {
            enrollmentRequestInfo.hashCode() == otherEnrollmentRequestInfo.hashCode()
        } else {
            enrollmentRequestInfo.hashCode() != otherEnrollmentRequestInfo.hashCode()
        }
        and:
        enrollmentRequestInfo.toString() != null
        where:
        enrollmentmode << [
            ENROLLMENT_MODE_CMPV2_VC,
            null,
            ENROLLMENT_MODE_CMPV2_VC,
            null,
            ENROLLMENT_MODE_CMPV2_VC
        ]
        otherenrollmentmode << [
            ENROLLMENT_MODE_CMPV2_VC,
            null,
            null,
            ENROLLMENT_MODE_CMPV2_VC,
            ENROLLMENT_MODE_CMPV2_INITIAL
        ]
        isequal << [
            true,
            true,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by subject alternative name param #san vs #othersan'() {
        given:
        def SubjectAltNameStringType sanStr = new SubjectAltNameStringType(san)
        def SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, sanStr)
        and:
        def SubjectAltNameStringType otherSanStr = new SubjectAltNameStringType(othersan)
        def SubjectAltNameParam otherSubjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, otherSanStr)
        and:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltNameParam)
        and:
        def EnrollmentRequestInfo otherEnrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        otherEnrollmentRequestInfo.setSubjectAltNameParam(otherSubjectAltNameParam)
        expect:
        enrollmentRequestInfo.equals(otherEnrollmentRequestInfo) == isequal
        and:
        if (isequal) {
            enrollmentRequestInfo.hashCode() == otherEnrollmentRequestInfo.hashCode()
        } else {
            enrollmentRequestInfo.hashCode() != otherEnrollmentRequestInfo.hashCode()
        }
        and:
        enrollmentRequestInfo.toString() != null
        where:
        san << [
            "the subject alternative name",
            null,
            "the subject alternative name",
            null,
            "the subject alternative name"
        ]
        othersan << [
            "the subject alternative name",
            null,
            null,
            "the subject alternative name",
            "the other subject alternative name"
        ]
        isequal << [
            true,
            true,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by IP version #ipversion vs #otheripversion'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        enrollmentRequestInfo.setIpVersion(ipversion)
        and:
        def EnrollmentRequestInfo otherEnrollmentRequestInfo = new EnrollmentRequestInfo()
        and:
        otherEnrollmentRequestInfo.setIpVersion(otheripversion)
        expect:
        enrollmentRequestInfo.equals(otherEnrollmentRequestInfo) == isequal
        and:
        if (isequal) {
            enrollmentRequestInfo.hashCode() == otherEnrollmentRequestInfo.hashCode()
        } else {
            enrollmentRequestInfo.hashCode() != otherEnrollmentRequestInfo.hashCode()
        }
        and:
        enrollmentRequestInfo.toString() != null
        where:
        ipversion << [
            StandardProtocolFamily.INET,
            StandardProtocolFamily.INET6,
            null,
            StandardProtocolFamily.INET,
            StandardProtocolFamily.INET,
            null,
            null,
            StandardProtocolFamily.INET6,
            StandardProtocolFamily.INET6
        ]
        otheripversion << [
            StandardProtocolFamily.INET,
            StandardProtocolFamily.INET6,
            null,
            null,
            StandardProtocolFamily.INET6,
            StandardProtocolFamily.INET,
            StandardProtocolFamily.INET6,
            null,
            StandardProtocolFamily.INET
        ]
        isequal << [
            true,
            true,
            true,
            false,
            false,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by OTP configuration parameters #otpcount and #otpvalidity vs #otherotpcount and #otherotpvalidity'() {
        given:
        def EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo()
        def OtpConfigurationParameters otpConfigurationParameters = new OtpConfigurationParameters(otpcount, otpvalidity)
        enrollmentRequestInfo.setOtpConfigurationParameters(otpConfigurationParameters)
        and:
        def EnrollmentRequestInfo otherEnrollmentRequestInfo = new EnrollmentRequestInfo()
        def OtpConfigurationParameters otherOtpConfigurationParameters = new OtpConfigurationParameters(otherotpcount, otherotpvalidity)
        otherEnrollmentRequestInfo.setOtpConfigurationParameters(otherOtpConfigurationParameters)
        expect:
        enrollmentRequestInfo.equals(otherEnrollmentRequestInfo) == isequal
        and:
        if (isequal) {
            enrollmentRequestInfo.hashCode() == otherEnrollmentRequestInfo.hashCode()
        } else {
            enrollmentRequestInfo.hashCode() != otherEnrollmentRequestInfo.hashCode()
        }
        and:
        enrollmentRequestInfo.toString() != null
        where:
        otpcount << [
            5,
            null,
            5,
            null
        ]
        otpvalidity << [
            43200,
            43200,
            43200,
            43200
        ]
        otherotpcount << [
            5,
            null,
            null,
            5
        ]
        otherotpvalidity << [
            43200,
            43200,
            43200,
            43200
        ]
        isequal << [
            true,
            true,
            false,
            false
        ]
    }
}
