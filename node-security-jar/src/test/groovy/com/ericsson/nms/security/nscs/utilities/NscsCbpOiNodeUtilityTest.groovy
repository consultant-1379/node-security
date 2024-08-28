/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.utilities

import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import org.slf4j.Logger

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameEdiPartyType
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.util.CertDetails

import spock.lang.Unroll

class NscsCbpOiNodeUtilityTest extends CdiSpecification {

    @ObjectUnderTest
    NscsCbpOiNodeUtility nscsCbpOiNodeUtility

    @MockedImplementation
    private Logger logger

    @MockedImplementation
    NscsNodeUtility nscsNodeUtility

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService

    def 'object under test injection' () {
        expect:
        nscsCbpOiNodeUtility != null
    }

    def 'get OAM trust category names' () {
        given:
        def NormalizableNodeReference normalizableNodeRef = mock(NormalizableNodeReference)
        and:
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_) >> ["OAM":"oamTrustCategory", "IPSEC":"ipsecTrustCategory"]
        and:
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM":"oamCmpCaTrustCategory", "IPSEC":"ipsecCmpCaTrustCategory"]
        when:
        def trustCategoryNames = nscsCbpOiNodeUtility.getTrustCategoryNames(normalizableNodeRef, "OAM")
        then:
        trustCategoryNames.size() == 2
        and:
        trustCategoryNames.contains("oamTrustCategory")
        and:
        trustCategoryNames.contains("oamCmpCaTrustCategory")
    }

    @Unroll
    def 'get node credential name for certtype #certtype' () {
        given:
        def NormalizableNodeReference normalizableNodeRef = mock(NormalizableNodeReference)
        and:
        nscsCapabilityModelService.getComEcimDefaultNodeCredentialIds(_) >> ["OAM":"oamNodeCredential", "IPSEC":"ipsecNodeCredential"]
        when:
        def nodeCredentialName = nscsCbpOiNodeUtility.getNodeCredentialName(normalizableNodeRef, certtype)
        then:
        nodeCredentialName == name
        where:
        certtype << ["OAM", "IPSEC", "UNKNOWN"]
        and:
        name << [
            "oamNodeCredential",
            "ipsecNodeCredential",
            null
        ]
    }

    def 'convertToX509Cert test method' () {
        given:
        String certificateString = "MIAGCSqGSIb3DQEHAqCAMIACAQExADCABgkqhkiG9w0BBwEAAKCAMIIDSjCCAjKgAwIBAgII DeRtSVavR4swDQYJKoZIhvcNAQELBQAwQzEYMBYGA1UEAwwPRU5NX1BLSV9Sb290X0NBMQsw CQYDVQQGEwJTRTELMAkGA1UECgwCS0kxDTALBgNVBAsMBFJBTkkwHhcNMjAwOTE3MTEyNzA3 WhcNMzAwOTE3MTEyNzA3WjBDMRgwFgYDVQQDDA9FTk1fUEtJX1Jvb3RfQ0ExCzAJBgNVBAYT AlNFMQswCQYDVQQKDAJLSTENMAsGA1UECwwEUkFOSTCCASIwDQYJKoZIhvcNAQEBBQADggEP ADCCAQoCggEBAIghsw3vcOLZ63cV1+yPlzCHXATDT+JYUorYsnCFT6FtwS9ZeypDpvvn3oT1 WVfEwsOSVR4pv9ZFwwSqj1karJkh2ugDNTN4Dx1n6ohnB8/InCD6JM6sbkGXTt/aP9YF378e awr7HTiLA+BK3ldlxSnPItlR8BA0RnLJWSLPUiOrwGk2OyGNZtYUU/g0ef5vQjurXigqsbh0 OnELHK6fnjKYi08O8VaJpMQ8ZCFtCKdN8AD76b6HqcALFUTMe988982DUECPW2OXHbn/AcK6 2dVanB1YspLcwy9WVK8UIngRrKnARIDs+xZs1MAmNTrDHh0OM6ho9SyRrb8LlfXx52ECAwEA AaNCMEAwHQYDVR0OBBYEFASKnIlLlp1qziIJOR9upH/bv97dMA8GA1UdEwEB/wQFMAMBAf8w DgYDVR0PAQH/BAQDAgEGMA0GCSqGSIb3DQEBCwUAA4IBAQAa2kKBi8drd9SRTXiIYJwwunJh 1gm/Yc189i6UE4Seygyg9SjB+4TA076GQVBTFk5aXsHqmiip6bDFGCYLuYolM0Ei/beua+La biIcz0VGLMV3tvxbL+OLSNxmvn/hTBLUguZOsAY+oJrVqZpocYTHtJP9/AGeqfsOKGKnJck5 5c3LaXmkrg9CwXQXM4X10ccj6o3XbHvQhrVKQH24h6XyO+JKsnm8ADaMiL1Mg0ZlXalznx1u JANrDwRb5GQ1Bik3HaBFCcvWs8ULN6jmtpPxTY+/000rH6Yvqtr4gzrwGMi3ZzgacAKgrFNr XgVfRvYgZmx2J69Xkxc04A472kQ/AAAxAAAAAAAAAA==";
        BigInteger serialNumber = BigInteger.valueOf(1001045178937460619);
        when:
        X509Certificate certificate = nscsCbpOiNodeUtility.convertToX509Cert(certificateString)
        then:
        assert (certificate.getSerialNumber() == serialNumber)
    }

    @Unroll
    def "convertToX509Cert from null or empty string" () {
        given:
        when:
        def X509Certificate certificate = nscsCbpOiNodeUtility.convertToX509Cert(cert)
        then:
        certificate == expected
        where:
        cert                                    || expected
        null                                    || null
        ""                                      || null
        "   "                                   || null
    }

    @Unroll
    def "convertToX509Cert from invalid string" () {
        given:
        when:
        def X509Certificate certificate = nscsCbpOiNodeUtility.convertToX509Cert(cert)
        then:
        thrown(expected)
        where:
        cert                                    || expected
        "  MIAGCSqGSIb3DQEHAqCAMIACAQExADCAB  " || CertificateException
    }

    @Unroll
    def "get node credential X509 certificate from string" () {
        given:
        def cert = "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"
        def subject = "CN=cloud257-vdu-oam, OU=RANI, O=KI, C=SE"
        def issuer = "CN=NE_OAM_CA, OU=RANI, O=KI, C=SE"
        when:
        def X509Certificate certificate = nscsCbpOiNodeUtility.getNodeCredentialX509Certificate(cert)
        then:
        CertDetails.matchesSN(certificate.getSerialNumber().toString(), serialNumber.toString())
        and:
        CertDetails.matchesDN(certificate.getSubjectX500Principal().getName(), subject)
        and:
        CertDetails.matchesDN(certificate.getIssuerX500Principal().getName(), issuer)
        where:
        serialNumber              || expected
        "7482000334329793508"     || false
        "0x67D567A9A914CBE4"      || false
        "0X67D567A9A914CBE4"      || false
        "67:D5:67:A9:A9:14:CB:E4" || false
    }

    @Unroll
    def "get node credential X509 certificate from invalid string" () {
        given:
        when:
        def X509Certificate certificate = nscsCbpOiNodeUtility.getNodeCredentialX509Certificate(cert)
        then:
        certificate == expected
        where:
        cert                                    || expected
        null                                    || null
        ""                                      || null
        "   "                                   || null
        "  MIAGCSqGSIb3DQEHAqCAMIACAQExADCAB  " || null
    }

    @Unroll
    def "generate X509 certificates from invalid string" () {
        given:
        when:
        def List<X509Certificate> certificates = nscsCbpOiNodeUtility.generateX509Certificates(cert)
        then:
        certificates == expected
        where:
        cert                                    || expected
        null                                    || []
        ""                                      || []
        "   "                                   || []
    }

    @Unroll
    def "generate X509 certificates from corrupted string" () {
        given:
        when:
        def List<X509Certificate> certificates = nscsCbpOiNodeUtility.generateX509Certificates(cert)
        then:
        thrown(expected)
        where:
        cert                                    || expected
        "  MIAGCSqGSIb3DQEHAqCAMIACAQExADCAB  " || CertificateException
    }

    @Unroll
    def "get serial number and issuer and subject from X509 certificate" () {
        given:
        def cert = "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"
        def subject = "CN=cloud257-vdu-oam, OU=RANI, O=KI, C=SE"
        def issuer = "CN=NE_OAM_CA, OU=RANI, O=KI, C=SE"
        when:
        def X509Certificate certificate = nscsCbpOiNodeUtility.getNodeCredentialX509Certificate(cert)
        then:
        CertDetails.matchesSN(nscsCbpOiNodeUtility.getSerialNumber(certificate).toString(), serialNumber.toString())
        and:
        CertDetails.matchesDN(nscsCbpOiNodeUtility.getSubject(certificate), subject)
        and:
        CertDetails.matchesDN(nscsCbpOiNodeUtility.getIssuer(certificate), issuer)
        where:
        serialNumber              || expected
        "7482000334329793508"     || false
        "0x67D567A9A914CBE4"      || false
        "0X67D567A9A914CBE4"      || false
        "67:D5:67:A9:A9:14:CB:E4" || false
    }

    def "get serial number and issuer and subject from null X509 certificate" () {
        given:
        when:
        def X509Certificate certificate = null
        then:
        nscsCbpOiNodeUtility.getSerialNumber(certificate) == null
        and:
        nscsCbpOiNodeUtility.getSubject(certificate) == null
        and:
        nscsCbpOiNodeUtility.getIssuer(certificate) == null
    }

    def "get serial number and issuer and subject from X509 certificate with null serial number and issuer and subject" () {
        given:
        when:
        def X509Certificate certificate = Mock(X509Certificate)
        certificate.getSubjectX500Principal() >> null
        certificate.getIssuerX500Principal() >> null
        certificate.getSerialNumber() >> null
        then:
        nscsCbpOiNodeUtility.getSerialNumber(certificate) == null
        and:
        nscsCbpOiNodeUtility.getSubject(certificate) == null
        and:
        nscsCbpOiNodeUtility.getIssuer(certificate) == null
    }

    @Unroll
    def 'get algorithm from enrollment info' () {
        given:
        ScepEnrollmentInfoImpl enrollmentInfo = Mock(ScepEnrollmentInfoImpl)
        enrollmentInfo.getKeySize() >> keysize
        when:
        def algorithm = nscsCbpOiNodeUtility.getAlgorithmFromEnrollmentInfo(enrollmentInfo)
        then:
        algorithm == expected
        where:
        keysize   || expected
        "0"       || "rsa1024"
        "1"       || "rsa2048"
        "2"       || "rsa3072"
        "3"       || "rsa4096"
        "4"       || null
        "5"       || "secp224r1"
        "6"       || "secp256r1"
        "7"       || "secp384r1"
        "8"       || null
        "9"       || "secp521r1"
        null      || null
        ""        || null
        "unknown" || null
    }
    def 'get algorithm from null enrollment info' () {
        given:
        when:
        def algorithm = nscsCbpOiNodeUtility.getAlgorithmFromEnrollmentInfo(null)
        then:
        algorithm == null
    }

    @Unroll
    def 'get subject alternative names from enrollment info' () {
        given:
        def SubjectAltNameStringType subjectAltName = new SubjectAltNameStringType(value)
        def SubjectAltNameFormat subjectAltNameType = format
        ScepEnrollmentInfoImpl enrollmentInfo = Mock(ScepEnrollmentInfoImpl)
        enrollmentInfo.getSubjectAltName() >> subjectAltName
        enrollmentInfo.getSubjectAltNameType() >> subjectAltNameType
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNamesFromEnrollmentInfo(enrollmentInfo)
        then:
        expected.equals(sans)
        where:
        format                    | value                                     || expected
        SubjectAltNameFormat.IPV4 | "145.34.23.123"                           || "IP:145.34.23.123"
        SubjectAltNameFormat.IPV6 | "2001:0db8:0000:0000:0000:ff00:0042:8329" || "IP:2001:0db8:0000:0000:0000:ff00:0042:8329"
        SubjectAltNameFormat.IPV6 | "2001:db8:0:0:0:ff00:42:8329"             || "IP:2001:db8:0:0:0:ff00:42:8329"
        SubjectAltNameFormat.IPV6 | "2001:db8::ff00:42:8329"                  || "IP:2001:db8::ff00:42:8329"
        SubjectAltNameFormat.FQDN | "someserialnumber.ericsson.com"           || "DNS:someserialnumber.ericsson.com"
    }

    def 'get subject alternative names from null enrollment info' () {
        given:
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNamesFromEnrollmentInfo(null)
        then:
        sans == null
    }

    @Unroll
    def 'get subject alternative names from invalid enrollment info' () {
        given:
        def SubjectAltNameStringType subjectAltNameData = new SubjectAltNameStringType(value)
        ScepEnrollmentInfoImpl enrollmentInfo = Mock(ScepEnrollmentInfoImpl)
        enrollmentInfo.getSubjectAltName() >> subjectAltNameData
        enrollmentInfo.getSubjectAltNameType() >> format
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNamesFromEnrollmentInfo(enrollmentInfo)
        then:
        sans == expected
        where:
        format                    | value                    || expected
        null                      | "any"                    || null
        SubjectAltNameFormat.NONE | "any"                    || null
        SubjectAltNameFormat.IPV4 | null                     || null
        SubjectAltNameFormat.IPV4 | "invalid"                || null
        SubjectAltNameFormat.IPV4 | "2001:db8::ff00:42:8329" || null
        SubjectAltNameFormat.IPV6 | null                     || null
        SubjectAltNameFormat.IPV6 | "invalid"                || null
        SubjectAltNameFormat.IPV6 | "145.34.23.123"          || null
        SubjectAltNameFormat.FQDN | null                     || null
    }

    @Unroll
    def 'get subject alternative names from a list of subject alternative name parameters' () {
        given:
        def SubjectAltNameStringType subjectAltNameData1 = new SubjectAltNameStringType(value1)
        def SubjectAltNameParam subjectAltNameParam1 = new SubjectAltNameParam(format1, subjectAltNameData1)
        def SubjectAltNameStringType subjectAltNameData2 = new SubjectAltNameStringType(value2)
        def SubjectAltNameParam subjectAltNameParam2 = new SubjectAltNameParam(format2, subjectAltNameData2)
        def subjectAltNameParams = [
            subjectAltNameParam1,
            subjectAltNameParam2
        ]
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNamesFromParamsList(subjectAltNameParams)
        then:
        expected.equals(sans)
        where:
        format1                   | value1                   | format2                   | value2                   || expected
        SubjectAltNameFormat.IPV4 | "145.34.23.123"          | null                      | "any"                    || "IP:145.34.23.123"
        null                      | "any"                    | SubjectAltNameFormat.IPV4 | "145.34.23.123"          || "IP:145.34.23.123"
        SubjectAltNameFormat.IPV4 | "145.34.23.123"          | SubjectAltNameFormat.NONE | "any"                    || "IP:145.34.23.123"
        SubjectAltNameFormat.IPV4 | "145.34.23.123"          | SubjectAltNameFormat.IPV4 | null                     || "IP:145.34.23.123"
        SubjectAltNameFormat.IPV6 | "2001:db8::ff00:42:8329" | null                      | "any"                    || "IP:2001:db8::ff00:42:8329"
        SubjectAltNameFormat.IPV6 | "2001:db8::ff00:42:8329" | SubjectAltNameFormat.NONE | "any"                    || "IP:2001:db8::ff00:42:8329"
        SubjectAltNameFormat.IPV6 | "2001:db8::ff00:42:8329" | SubjectAltNameFormat.IPV4 | null                     || "IP:2001:db8::ff00:42:8329"
        SubjectAltNameFormat.FQDN | "someSN.ericsson.com"    | null                      | "any"                    || "DNS:someSN.ericsson.com"
        SubjectAltNameFormat.FQDN | "someSN.ericsson.com"    | SubjectAltNameFormat.IPV4 | null                     || "DNS:someSN.ericsson.com"
        SubjectAltNameFormat.FQDN | "someSN.ericsson.com"    | SubjectAltNameFormat.NONE | "any"                    || "DNS:someSN.ericsson.com"
        SubjectAltNameFormat.IPV4 | "145.34.23.123"          | SubjectAltNameFormat.IPV6 | "2001:db8::ff00:42:8329" || "IP:145.34.23.123 IP:2001:db8::ff00:42:8329"
        SubjectAltNameFormat.IPV6 | "2001:db8::ff00:42:8329" | SubjectAltNameFormat.IPV4 | "145.34.23.123"          || "IP:2001:db8::ff00:42:8329 IP:145.34.23.123"
        SubjectAltNameFormat.FQDN | "someSN.ericsson.com"    | SubjectAltNameFormat.IPV4 | "145.34.23.123"          || "DNS:someSN.ericsson.com IP:145.34.23.123"
    }

    def 'get subject alternative names from a null list of subject alternative name parameters' () {
        given:
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNamesFromParamsList(null)
        then:
        sans == null
    }

    def 'get subject alternative names from an empty list of subject alternative name parameters' () {
        given:
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNamesFromParamsList([])
        then:
        sans == null
    }

    def 'get subject alternative name from a null subject alternative name parameters' () {
        given:
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNameFromParams(null)
        then:
        sans == null
    }

    @Unroll
    def 'get subject alternative name from invalid format and data' () {
        given:
        def SubjectAltNameStringType subjectAltNameData = new SubjectAltNameStringType(value)
        def SubjectAltNameFormat subjectAltNameFormat = format
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNameFromFormatAndData(subjectAltNameFormat, subjectAltNameData)
        then:
        sans == expected
        where:
        format                    | value || expected
        null                      | "any" || null
        SubjectAltNameFormat.IPV4 | null  || null
        SubjectAltNameFormat.IPV4 | "?"   || null
    }
    def 'get subject alternative name from unsupported data' () {
        given:
        def SubjectAltNameEdiPartyType subjectAltNameData = new SubjectAltNameEdiPartyType()
        when:
        def sans = nscsCbpOiNodeUtility.getSubjectAltNameFromFormatAndData(SubjectAltNameFormat.FQDN, subjectAltNameData)
        then:
        sans == null
    }
}
