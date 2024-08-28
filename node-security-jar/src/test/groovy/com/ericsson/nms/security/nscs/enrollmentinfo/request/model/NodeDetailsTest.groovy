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
package com.ericsson.nms.security.nscs.enrollmentinfo.request.model

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.DuplicateNodeNamesException

import spock.lang.Unroll

class NodeDetailsTest extends CdiSpecification {

    @ObjectUnderTest
    private NodeDetails nodeDetails

    def 'object under test'() {
        expect:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
    }

    @Unroll
    def 'set node FDN #nodefdn'() {
        given:
        when:
        nodeDetails.setNodeFdn(nodefdn)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == nodefdn
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        nodefdn << [
            "the node FDN",
            "NODE",
            "NetworkElement=NODE",
            null
        ]
    }

    @Unroll
    def 'set cert type #certtype'() {
        given:
        when:
        nodeDetails.setCertType(certtype)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == certtype
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        certtype << [
            "the cert type",
            "OAM",
            "IPSEC",
            "",
            null
        ]
    }

    @Unroll
    def 'set entity profile name #entityprofilename'() {
        given:
        when:
        nodeDetails.setEntityProfileName(entityprofilename)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == entityprofilename
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        entityprofilename << [
            "the entity profile name",
            "",
            null
        ]
    }

    @Unroll
    def 'set key size #keysize'() {
        given:
        when:
        nodeDetails.setKeySize(keysize)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == keysize
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        keysize << [
            "the key size",
            "RSA_1024",
            "RSA_2048",
            "",
            null
        ]
    }

    @Unroll
    def 'set common name #commonname'() {
        given:
        when:
        nodeDetails.setCommonName(commonname)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == commonname
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        commonname << [
            "the common name",
            "",
            null
        ]
    }

    @Unroll
    def 'set enrollment mode #enrollmentmode'() {
        given:
        when:
        nodeDetails.setEnrollmentMode(enrollmentmode)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == enrollmentmode
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        enrollmentmode << [
            "the enrollment mode",
            "CMPv2_INITIAL",
            "",
            null
        ]
    }

    @Unroll
    def 'set subject alternative name #subjectalternativename'() {
        given:
        when:
        nodeDetails.setSubjectAltName(subjectalternativename)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == subjectalternativename
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        subjectalternativename << [
            "the subject alternative name",
            "1.2.3.4",
            "node.com",
            "",
            null
        ]
    }

    @Unroll
    def 'set subject alternative name type #subjectalternativenametype'() {
        given:
        when:
        nodeDetails.setSubjectAltNameType(subjectalternativenametype)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == subjectalternativenametype
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        subjectalternativenametype << [
            "the subject alternative name type",
            "IP_ADDRESS",
            "FQDN",
            "",
            null
        ]
    }

    @Unroll
    def 'set IP version #ipversion'() {
        given:
        when:
        nodeDetails.setIpVersion(ipversion)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == ipversion
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        ipversion << StandardProtocolFamily.values()
    }

    @Unroll
    def 'set OTP count #otpcount'() {
        given:
        when:
        nodeDetails.setOtpCount(otpcount)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == otpcount
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == null
        where:
        otpcount << [
            0,
            -1,
            1,
            null
        ]
    }

    @Unroll
    def 'set OTP validity period in minutes #otpvalidityperiodinminutes'() {
        given:
        when:
        nodeDetails.setOtpValidityPeriodInMinutes(otpvalidityperiodinminutes)
        then:
        nodeDetails != null
        and:
        nodeDetails.getNodeFdn() == null
        and:
        nodeDetails.getCertType() == null
        and:
        nodeDetails.getEntityProfileName() == null
        and:
        nodeDetails.getKeySize() == null
        and:
        nodeDetails.getCommonName() == null
        and:
        nodeDetails.getEnrollmentMode() == null
        and:
        nodeDetails.getSubjectAltName() == null
        and:
        nodeDetails.getSubjectAltNameType() == null
        and:
        nodeDetails.getIpVersion() == null
        and:
        nodeDetails.getOtpCount() == null
        and:
        nodeDetails.getOtpValidityPeriodInMinutes() == otpvalidityperiodinminutes
        where:
        otpvalidityperiodinminutes << [
            0,
            -1,
            1,
            1440,
            43200,
            null
        ]
    }

    def 'equality to itself'() {
        expect:
        nodeDetails.equals(nodeDetails) == true
    }

    def 'equality to null'() {
        expect:
        nodeDetails.equals(null) == false
    }

    def 'equality to other class'() {
        given:
        def nodeDetailsList = new NodeDetailsList()
        expect:
        nodeDetails.equals(nodeDetailsList) == false
    }

    @Unroll
    def 'equality by node FDN #nodefdn vs #othernodefdn'() {
        given:
        nodeDetails.setNodeFdn(nodefdn)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setNodeFdn(othernodefdn)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
        where:
        nodefdn << [
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            "NODE",
            "NetworkElement=NODE"
        ]
        othernodefdn << [
            "NODE",
            "NODE",
            "NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE",
            null,
            null,
            null,
            "OTHERNODE",
            "OTHERNODE",
            "NetworkElement=OTHERNODE",
            "NetworkElement=OTHERNODE"
        ]
        isequal << [
            true,
            true,
            false,
            true,
            true,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by cert type #certtype vs #othercerttype'() {
        given:
        nodeDetails.setCertType(certtype)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setCertType(othercerttype)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
        where:
        certtype << [
            "OAM",
            null,
            "OAM",
            null,
            "OAM"
        ]
        othercerttype << [
            "OAM",
            null,
            null,
            "OAM",
            "IPSEC"
        ]
        isequal << [
            true,
            true,
            true,
            true,
            false
        ]
    }

    @Unroll
    def 'equality by entity profile name #entityprofilename vs #otherentityprofilename'() {
        given:
        nodeDetails.setEntityProfileName(entityprofilename)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setEntityProfileName(otherentityprofilename)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
        where:
        entityprofilename << [
            "the entity profile name",
            null,
            "the entity profile name",
            null,
            "the entity profile name"
        ]
        otherentityprofilename << [
            "the entity profile name",
            null,
            null,
            "the entity profile name",
            "the other entity profile name"
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
        nodeDetails.setKeySize(keysize)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setKeySize(otherkeysize)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
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
        nodeDetails.setCommonName(commonname)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setCommonName(othercommonname)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
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
        nodeDetails.setEnrollmentMode(enrollmentmode)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setEnrollmentMode(otherenrollmentmode)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
        where:
        enrollmentmode << [
            "the enrollment mode",
            null,
            "the enrollment mode",
            null,
            "the enrollment mode"
        ]
        otherenrollmentmode << [
            "the enrollment mode",
            null,
            null,
            "the enrollment mode",
            "the other enrollment mode"
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
    def 'equality by subject alternative name #subjectaltname vs #othersubjectaltname'() {
        given:
        nodeDetails.setSubjectAltName(subjectaltname)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setSubjectAltName(othersubjectaltname)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
        where:
        subjectaltname << [
            "the subject alternative name",
            null,
            "the subject alternative name",
            null,
            "the subject alternative name"
        ]
        othersubjectaltname << [
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
    def 'equality by subject alternative name type #subjectaltnametype vs #othersubjectaltnametype'() {
        given:
        nodeDetails.setSubjectAltNameType(subjectaltnametype)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setSubjectAltNameType(othersubjectaltnametype)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
        where:
        subjectaltnametype << [
            "the subject alternative name type",
            null,
            "the subject alternative name type",
            null,
            "the subject alternative name type"
        ]
        othersubjectaltnametype << [
            "the subject alternative name type",
            null,
            null,
            "the subject alternative name type",
            "the other subject alternative name type"
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
        nodeDetails.setIpVersion(ipversion)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setIpVersion(otheripversion)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
        }
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
    def 'equality by OTP count #otpcount vs #otherotpcount'() {
        given:
        nodeDetails.setOtpCount(otpcount)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setOtpCount(otherotpcount)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
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
        nodeDetails.setOtpValidityPeriodInMinutes(otpvalidity)
        and:
        def otherNodeDetails = new NodeDetails()
        and:
        otherNodeDetails.setOtpValidityPeriodInMinutes(otherotpvalidity)
        expect:
        nodeDetails.equals(otherNodeDetails) == isequal
        and:
        if (isequal) {
            nodeDetails.hashCode() == otherNodeDetails.hashCode()
        } else {
            nodeDetails.hashCode() != otherNodeDetails.hashCode()
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

    @Unroll
    def 'compare end entity for node #nodefdn and cert type #certtype with itself'() {
        given:
        nodeDetails.setNodeFdn(nodefdn)
        nodeDetails.setCertType(certtype)
        when:
        int compare = nodeDetails.compareEndEntity(nodeDetails)
        then:
        compare == 0
        where:
        nodefdn << [
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null
        ]
        certtype << [
            "OAM",
            "OAM",
            "OAM",
            "IPSEC",
            "IPSEC",
            "IPSEC",
            null,
            null,
            null
        ]
    }

    @Unroll
    def 'compare end entity for node #nodefdn and cert type #certtype with null'() {
        given:
        nodeDetails.setNodeFdn(nodefdn)
        nodeDetails.setCertType(certtype)
        when:
        int compare = nodeDetails.compareEndEntity(null)
        then:
        compare == 1
        where:
        nodefdn << [
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null
        ]
        certtype << [
            "OAM",
            "OAM",
            "OAM",
            "IPSEC",
            "IPSEC",
            "IPSEC",
            null,
            null,
            null
        ]
    }

    @Unroll
    def 'compare end entity for same node #nodefdn and #othernodefdn and different cert type #certtype vs #othercerttype and same params'() {
        given:
        nodeDetails.setNodeFdn(nodefdn)
        nodeDetails.setCertType(certtype)
        and:
        def otherNodeDetails = new NodeDetails()
        otherNodeDetails.setNodeFdn(othernodefdn)
        otherNodeDetails.setCertType(othercerttype)
        when:
        int compare = nodeDetails.compareEndEntity(otherNodeDetails)
        then:
        compare != 0
        where:
        nodefdn << [
            "NODE",
            "NetworkElement=NODE",
            "NODE",
            "NetworkElement=NODE"
        ]
        othernodefdn << [
            "NODE",
            "NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE"
        ]
        certtype << [
            "OAM",
            null,
            "IPSEC",
            "IPSEC"
        ]
        othercerttype << [
            "IPSEC",
            "IPSEC",
            null,
            "OAM"
        ]
    }

    @Unroll
    def 'compare end entity for non conflicting duplicates: same node #nodefdn and #othernodefdn and same cert type #certtype vs #othercerttype and same params'() {
        given:
        nodeDetails.setNodeFdn(nodefdn)
        nodeDetails.setCertType(certtype)
        and:
        def otherNodeDetails = new NodeDetails()
        otherNodeDetails.setNodeFdn(othernodefdn)
        otherNodeDetails.setCertType(othercerttype)
        when:
        int compare = nodeDetails.compareEndEntity(otherNodeDetails)
        then:
        compare == 0
        where:
        nodefdn << [
            "NODE",
            "NetworkElement=NODE",
            "NODE",
            "NetworkElement=NODE",
            "NODE"
        ]
        othernodefdn << [
            "NODE",
            "NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE"
        ]
        certtype << [
            "OAM",
            null,
            "OAM",
            null,
            "IPSEC"
        ]
        othercerttype << [
            "OAM",
            null,
            null,
            "OAM",
            "IPSEC"
        ]
    }

    @Unroll
    def 'compare end entity for conflicting duplicates: same node #nodefdn and #othernodefdn and same cert type #certtype vs #othercerttype and different params'() {
        given:
        nodeDetails.setNodeFdn(nodefdn)
        nodeDetails.setCertType("OAM")
        nodeDetails.setEntityProfileName("PROFILE")
        and:
        def otherNodeDetails = new NodeDetails()
        otherNodeDetails.setNodeFdn(othernodefdn)
        otherNodeDetails.setCertType("OAM")
        otherNodeDetails.setEntityProfileName("OTHERPROFILE")
        when:
        int compare = nodeDetails.compareEndEntity(otherNodeDetails)
        then:
        thrown(DuplicateNodeNamesException.class)
        where:
        nodefdn << [
            "NODE",
            "NetworkElement=NODE",
            "NODE",
            "NetworkElement=NODE",
            "NODE"
        ]
        othernodefdn << [
            "NODE",
            "NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE"
        ]
        certtype << [
            "OAM",
            null,
            "OAM",
            "null",
            "IPSEC"
        ]
        othercerttype << [
            "OAM",
            null,
            null,
            "OAM",
            "IPSEC"
        ]
    }
}
