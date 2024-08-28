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
package com.ericsson.oss.services.security.nscs.command.enrollmentinfo

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails

import spock.lang.Unroll

class OtpConfigurationParametersTest extends CdiSpecification {

    @Unroll
    "constructor by otp count #otpcount and validity #otpvalidity"() {
        given:
        def otpConfigurationParameters = new OtpConfigurationParameters(otpcount, otpvalidity)
        expect:
        otpConfigurationParameters != null
        and:
        otpConfigurationParameters.getOtpCount() == otpcount
        and:
        otpConfigurationParameters.getOtpValidityPeriodInMinutes() == otpvalidity
        where:
        otpcount << [
            5,
            null,
            5,
            null,
            5,
            null
        ]
        otpvalidity << [
            -1,
            -1,
            43200,
            43200,
            null,
            null
        ]
    }

    def 'equality to itself'() {
        given:
        def otpConfigurationParameters = new OtpConfigurationParameters(5, 43200)
        expect:
        otpConfigurationParameters.equals(otpConfigurationParameters) == true
    }

    def 'equality to null'() {
        given:
        def otpConfigurationParameters = new OtpConfigurationParameters(5, 43200)
        expect:
        otpConfigurationParameters.equals(null) == false
    }

    def 'equality to other class'() {
        given:
        def otpConfigurationParameters = new OtpConfigurationParameters(5, 43200)
        and:
        def nodeDetails = new NodeDetails()
        nodeDetails.setOtpCount(5)
        nodeDetails.setOtpValidityPeriodInMinutes(43200)
        expect:
        otpConfigurationParameters.equals(nodeDetails) == false
    }

    @Unroll
    def 'equality by OTP count #otpcount vs #otherotpcount'() {
        given:
        def otpConfigurationParameters = new OtpConfigurationParameters(otpcount, 43200)
        and:
        def otherOtpConfigurationParameters = new OtpConfigurationParameters(otherotpcount, 43200)
        expect:
        otpConfigurationParameters.equals(otherOtpConfigurationParameters) == isequal
        and:
        if (isequal) {
            otpConfigurationParameters.hashCode() == otherOtpConfigurationParameters.hashCode()
        } else {
            otpConfigurationParameters.hashCode() != otherOtpConfigurationParameters.hashCode()
        }
        where:
        otpcount << [
            5,
            null,
            5,
            null,
            5
        ]
        otherotpcount << [
            5,
            null,
            null,
            5,
            1
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
    def 'equality by OTP validity #otpvalidity vs #otherotpvalidity'() {
        given:
        def otpConfigurationParameters = new OtpConfigurationParameters(5, otpvalidity)
        and:
        def otherOtpConfigurationParameters = new OtpConfigurationParameters(5, otherotpvalidity)
        expect:
        otpConfigurationParameters.equals(otherOtpConfigurationParameters) == isequal
        and:
        if (isequal) {
            otpConfigurationParameters.hashCode() == otherOtpConfigurationParameters.hashCode()
        } else {
            otpConfigurationParameters.hashCode() != otherOtpConfigurationParameters.hashCode()
        }
        where:
        otpvalidity << [
            -1,
            43200,
            null,
            -1,
            -1,
            null,
            null,
            43200,
            43200
        ]
        otherotpvalidity << [
            -1,
            43200,
            null,
            null,
            43200,
            -1,
            43200,
            null,
            -1
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
}
