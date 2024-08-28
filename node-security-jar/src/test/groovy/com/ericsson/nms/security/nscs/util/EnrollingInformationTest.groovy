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
package com.ericsson.nms.security.nscs.util

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory
import com.ericsson.oss.services.security.nscs.command.enrollmentinfo.OtpConfigurationParameters

import spock.lang.Unroll

class EnrollingInformationTest extends CdiSpecification {

    private static final String NODE = "NODE"
    private static final String NODE_EP = "NODE_EP"
    private static final String COMMON_NAME = "COMMON_NAME"
    private static final EnrollmentMode ENROLLMENT_MODE_CMPV2_VC = EnrollmentMode.CMPv2_VC
    private static final AlgorithmKeys ALGORITHM_KEY_RSA_2048 = AlgorithmKeys.RSA_2048
    private static final NodeModelInformation VDU_MODEL_INFO = new NodeModelInformation("1.0", ModelIdentifierType.MIM_VERSION, "vDU")

    def 'constructor with params'() {
        given:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        expect:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
    }

    @Unroll
    def 'set by node FDN #nodefdn'() {
        given:
        when:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(nodefdn, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        then:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == nodefdn
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        nodefdn << [NODE, "", null]
    }

    @Unroll
    def 'set by entity profile name #entityprofilename'() {
        given:
        when:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, entityprofilename, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        then:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == entityprofilename
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        entityprofilename << [NODE_EP, "", null]
    }

    @Unroll
    def 'set by enrollment mode #enrollmentmode'() {
        given:
        when:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, enrollmentmode, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        then:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == enrollmentmode
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        enrollmentmode << [
            ENROLLMENT_MODE_CMPV2_VC,
            null
        ]
    }

    @Unroll
    def 'set by key algorithm and size #keysize'() {
        given:
        when:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, keysize,
                NodeEntityCategory.OAM, COMMON_NAME)
        then:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == keysize
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        keysize << [
            ALGORITHM_KEY_RSA_2048,
            null
        ]
    }

    @Unroll
    def 'set by node entity category #category'() {
        given:
        when:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                category, COMMON_NAME)
        then:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == category
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        category << [
            NodeEntityCategory.OAM,
            null
        ]
    }

    @Unroll
    def 'set by common name #commonname'() {
        given:
        when:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, commonname)
        then:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == commonname
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        commonname << [
            COMMON_NAME,
            "",
            null
        ]
    }

    @Unroll
    def 'set by subject alternative name #san'() {
        given:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        and:
        def BaseSubjectAltNameDataType sanString = new SubjectAltNameStringType(san)
        enrollingInfo.setSubjectAltName(sanString)
        expect:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == sanString
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        san << [
            "172.16.0.4",
            "2001:cdba:0000:0000:0000:0000:3257:9652",
            "?",
            "",
            null
        ]
    }

    @Unroll
    def 'set by subject alternative name format #sanformat'() {
        given:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        and:
        enrollingInfo.setSubjectAltNameFormat(sanformat)
        expect:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == sanformat
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        sanformat << [
            SubjectAltNameFormat.IPV4,
            SubjectAltNameFormat.IPV6,
            null
        ]
    }

    @Unroll
    def 'set by node model info #modelinfo'() {
        given:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        and:
        enrollingInfo.setModelInfo(modelinfo)
        expect:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == modelinfo
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        modelinfo << [
            VDU_MODEL_INFO,
            null
        ]
    }

    @Unroll
    def 'set by IP version #ipversion'() {
        given:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        and:
        enrollingInfo.setIpVersion(ipversion)
        expect:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == ipversion
        enrollingInfo.getOtpConfigurationParameters() == null
        enrollingInfo.getOtpCount() == null
        enrollingInfo.getOtpValidityPeriodInMinutes() == null
        enrollingInfo.toString() != null
        where:
        ipversion << [
            StandardProtocolFamily.INET,
            StandardProtocolFamily.INET6,
            null
        ]
    }

    @Unroll
    def 'set by OTP configuration parameters #otpcount and #otpvalidity'() {
        given:
        def EnrollingInformation enrollingInfo = new EnrollingInformation(NODE, NODE_EP, ENROLLMENT_MODE_CMPV2_VC, ALGORITHM_KEY_RSA_2048,
                NodeEntityCategory.OAM, COMMON_NAME)
        and:
        def OtpConfigurationParameters otpConfigurationParameters = new OtpConfigurationParameters(otpcount, otpvalidity)
        enrollingInfo.setOtpConfigurationParameters(otpConfigurationParameters)
        expect:
        enrollingInfo != null
        enrollingInfo.getNodeFdn() == NODE
        enrollingInfo.getEntityProfileName() == NODE_EP
        enrollingInfo.getEnrollmentMode() == ENROLLMENT_MODE_CMPV2_VC
        enrollingInfo.getKeySize() == ALGORITHM_KEY_RSA_2048
        enrollingInfo.getCategory() == NodeEntityCategory.OAM
        enrollingInfo.getCommonName() == COMMON_NAME
        enrollingInfo.getSubjectAltName() == null
        enrollingInfo.getSubjectAltNameFormat() == null
        enrollingInfo.getModelInfo() == null
        enrollingInfo.getIpVersion() == null
        enrollingInfo.getOtpConfigurationParameters() == otpConfigurationParameters
        enrollingInfo.getOtpCount() == otpcount
        enrollingInfo.getOtpValidityPeriodInMinutes() == otpvalidity
        enrollingInfo.toString() != null
        where:
        otpcount << [
            5,
            5,
            5,
            null,
            null,
            null
        ]
        otpvalidity << [
            -1,
            43200,
            null,
            -1,
            43200,
            null
        ]
    }
}
