package com.ericsson.nms.security.nscs.enrollmentinfo.response

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.CertificateType
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentAuthorityData
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerData
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerGroupData
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.NodeCredentialData
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.SecurityDataContainer
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustCategoryData
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustedCertificateData
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustCategories
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificate
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificates
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.VerboseEnrollmentInfo
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoService
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.services.security.nscs.command.enrollmentinfo.OtpConfigurationParameters

import spock.lang.Unroll

class EnrollmentInfoProviderTest extends CdiSpecification {

    @ObjectUnderTest
    private EnrollmentInfoProvider enrollmentInfoProvider

    @MockedImplementation
    private NscsCMReaderService nscsCMReaderService

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    private EnrollmentInfoService enrollmentInfoService

    @MockedImplementation
    private NscsNodeUtility nscsNodeUtility

    private List<String> crlsUri = Arrays.asList("http://192.168.0.155:8092/pki-cdps?ca_name=ENM_OAM_CA&amp;ca_cert_serialnumber=601e88c1a6b4122")//new ArrayList<>()

    private NodeModelInformation nodeModelInformation = mock(NodeModelInformation)
    private SecurityDataResponse response = mock(SecurityDataResponse)

    def 'object under test'() {
        expect:
        enrollmentInfoProvider != null
    }

    @Unroll
    def 'get verbose enrollment info with EP #entityprofile'() {
        given:
        nscsCMReaderService.getNodeModelInformation(_) >> nodeModelInformation
        def SecurityDataContainer securityDataContainer = buildSecurityDataContainer("NODE", entityprofile)
        response.getSecurityDataContainers() >> [securityDataContainer]
        response.getTrustedCertificateData() >> buildListTrustedCertificateData(entityprofile)
        enrollmentInfoService.generateSecurityDataOam(_, _) >> response
        nscsNodeUtility.isNodeIpv6(_ as String) >> false
        def EnrollmentRequestInfo enrollmentRequestInfo = buildEnrollmentRequestInfo("NODE", entityprofile)
        and:
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM" : "oamCmpCaTrustCategory"]
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_)  >> ["OAM" : "oamTrustCategory"]
        when:
        def EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true)
        then:
        enrollmentInfo != null
        and:
        def VerboseEnrollmentInfo verboseEnrollmentInfo = enrollmentInfo.getVerboseEnrollmentInfo()
        verboseEnrollmentInfo != null
        and:
        def TrustCategories trustCategories = verboseEnrollmentInfo.getTrustCategories()
        def TrustedCertificates trustedCertificates = verboseEnrollmentInfo.getTrustedCertificates()
        trustCategories.getTrustCategory().size() == 2
        def List<TrustedCertificate> trustedCertificateList = trustedCertificates.getTrustedCertificate()
        trustedCertificateList.size() == trustedcertificatelistsize
        def Set<String> trustedCertificateNameSet = new HashSet()
        for (TrustedCertificate trustedCertificate : trustedCertificateList) {
            trustedCertificateNameSet.add(trustedCertificate.getName())
        }
        trustedCertificateNameSet.size() == trustedcertificatelistsize
        where:
        entityprofile << ["NODE_EP", "NODE_EXTRA_CA_EP"]
        trustedcertificatelistsize << [5, 6]
    }

    @Unroll
    def 'get verbose enrollment info with EP #entityprofile and null trusted certificates'() {
        given:
        nscsCMReaderService.getNodeModelInformation(_) >> nodeModelInformation
        def SecurityDataContainer securityDataContainer = buildSecurityDataContainer("NODE", entityprofile)
        response.getSecurityDataContainers() >> [securityDataContainer]
        response.getTrustedCertificateData() >> null
        enrollmentInfoService.generateSecurityDataOam(_, _) >> response
        nscsNodeUtility.isNodeIpv6(_ as String) >> false
        def EnrollmentRequestInfo enrollmentRequestInfo = buildEnrollmentRequestInfo("NODE", entityprofile)
        and:
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM" : "oamCmpCaTrustCategory"]
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_)  >> ["OAM" : "oamTrustCategory"]
        when:
        def EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true)
        then:
        enrollmentInfo != null
        and:
        def VerboseEnrollmentInfo verboseEnrollmentInfo = enrollmentInfo.getVerboseEnrollmentInfo()
        verboseEnrollmentInfo != null
        and:
        def TrustCategories trustCategories = verboseEnrollmentInfo.getTrustCategories()
        def TrustedCertificates trustedCertificates = verboseEnrollmentInfo.getTrustedCertificates()
        trustCategories.getTrustCategory().size() == 2
        def List<TrustedCertificate> trustedCertificateList = trustedCertificates.getTrustedCertificate()
        trustedCertificateList.size() == 0
        def Set<String> trustedCertificateNameSet = new HashSet()
        for (TrustedCertificate trustedCertificate : trustedCertificateList) {
            trustedCertificateNameSet.add(trustedCertificate.getName())
        }
        trustedCertificateNameSet.size() == 0
        where:
        entityprofile << ["NODE_EP", "NODE_EXTRA_CA_EP"]
    }

    @Unroll
    def 'get verbose enrollment info with EP #entityprofile and empty trusted certificates'() {
        given:
        nscsCMReaderService.getNodeModelInformation(_) >> nodeModelInformation
        def SecurityDataContainer securityDataContainer = buildSecurityDataContainer("NODE", entityprofile)
        response.getSecurityDataContainers() >> [securityDataContainer]
        response.getTrustedCertificateData() >> []
        enrollmentInfoService.generateSecurityDataOam(_, _) >> response
        nscsNodeUtility.isNodeIpv6(_ as String) >> false
        def EnrollmentRequestInfo enrollmentRequestInfo = buildEnrollmentRequestInfo("NODE", entityprofile)
        and:
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM" : "oamCmpCaTrustCategory"]
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_)  >> ["OAM" : "oamTrustCategory"]
        when:
        def EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true)
        then:
        enrollmentInfo != null
        and:
        def VerboseEnrollmentInfo verboseEnrollmentInfo = enrollmentInfo.getVerboseEnrollmentInfo()
        verboseEnrollmentInfo != null
        and:
        def TrustCategories trustCategories = verboseEnrollmentInfo.getTrustCategories()
        def TrustedCertificates trustedCertificates = verboseEnrollmentInfo.getTrustedCertificates()
        trustCategories.getTrustCategory().size() == 2
        def List<TrustedCertificate> trustedCertificateList = trustedCertificates.getTrustedCertificate()
        trustedCertificateList.size() == 0
        def Set<String> trustedCertificateNameSet = new HashSet()
        for (TrustedCertificate trustedCertificate : trustedCertificateList) {
            trustedCertificateNameSet.add(trustedCertificate.getName())
        }
        trustedCertificateNameSet.size() == 0
        where:
        entityprofile << ["NODE_EP", "NODE_EXTRA_CA_EP"]
    }

    def 'get non verbose enrollment info'() {
        given:
        nscsCMReaderService.getNodeModelInformation(_) >> nodeModelInformation
        def SecurityDataContainer securityDataContainer = buildSecurityDataContainer("NODE", "NODE_EP")
        response.getSecurityDataContainers() >> [securityDataContainer]
        response.getTrustedCertificateData() >> buildListTrustedCertificateData("NODE_EP")
        enrollmentInfoService.generateSecurityDataOam(_, _) >> response
        nscsNodeUtility.isNodeIpv6(_ as String) >> false
        def EnrollmentRequestInfo enrollmentRequestInfo = buildEnrollmentRequestInfo("NODE", "NODE_EP")
        and:
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM" : "oamCmpCaTrustCategory"]
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_)  >> ["OAM" : "oamTrustCategory"]
        when:
        def EnrollmentInfo enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, false)
        then:
        enrollmentInfo != null
        and:
        enrollmentInfo.getVerboseEnrollmentInfo() == null
    }

    private EnrollmentRequestInfo buildEnrollmentRequestInfo(final String nodeName, final String entityProfile) {
        final EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setNodeIdentifier(new NodeIdentifier(nodeName, null));
        enrollmentRequestInfo.setNodeName(nodeName);
        enrollmentRequestInfo.setCertType("OAM");
        enrollmentRequestInfo.setEntityProfile(entityProfile);
        enrollmentRequestInfo.setKeySize("RSA_2048");
        enrollmentRequestInfo.setCommonName(nodeName+"-oam");
        enrollmentRequestInfo.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        final SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.IPV4;
        final SubjectAltNameStringType subjectAltNameStringType = new SubjectAltNameStringType("1.1.1.1");
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameStringType);
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltNameParam);
        enrollmentRequestInfo.setIpVersion(StandardProtocolFamily.INET);
        final OtpConfigurationParameters otpConfigurationParameters = new OtpConfigurationParameters(1, 43200);
        enrollmentRequestInfo.setOtpConfigurationParameters(otpConfigurationParameters);
        return enrollmentRequestInfo;
    }

    private SecurityDataContainer buildSecurityDataContainer(final String nodeName, final String entityProfile) {
        final NodeCredentialData nodeCredentialData = buildNodeCredentialData(nodeName);
        final TrustCategoryData trustCategoryData = buildTrustCategoryData(nodeName, entityProfile);
        final SecurityDataContainer securityDataContainer = new SecurityDataContainer(CertificateType.OAM, nodeCredentialData, trustCategoryData);
        return securityDataContainer;
    }

    private NodeCredentialData buildNodeCredentialData(final String nodeName) {
        final EnrollmentServerGroupData enrollmentServerGroupData = buildEnrollmentServerGroupData(nodeName);
        final EnrollmentAuthorityData enrollmentAuthorityData = buildEnrollmentAuthorityData(nodeName);
        final NodeCredentialData nodeCredentialData = new NodeCredentialData("nodeCredentialOam",
                "CN="+nodeName+"-oam,C=SE,O=ERICSSON,OU=BUCI DUAC NAM", "rsa4096",
                enrollmentServerGroupData, enrollmentAuthorityData, "OTP");
        return nodeCredentialData;
    }

    private EnrollmentServerGroupData buildEnrollmentServerGroupData(final String nodeName) {
        final EnrollmentServerData enrollmentServerData = buildEnrollmentServerData();
        final EnrollmentServerGroupData enrollmentServerGroupData = new EnrollmentServerGroupData("1");
        enrollmentServerGroupData.addEnrollmentServer(enrollmentServerData);
        return enrollmentServerGroupData;
    }

    private EnrollmentServerData buildEnrollmentServerData() {
        final EnrollmentServerData enrollmentServerData = new EnrollmentServerData("1",
                "http://192.168.0.155:8091/pkira-cmp/NE_OAM_CA/synch", "CMP",
                null);
        return enrollmentServerData;
    }

    private EnrollmentAuthorityData buildEnrollmentAuthorityData(final String nodeName) {
        final EnrollmentAuthorityData enrollmentAuthorityData = new EnrollmentAuthorityData("1", null,
                "ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=2",
                "REGISTRATION_AUTHORITY", "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=NE_OAM_CA");
        return enrollmentAuthorityData;
    }

    private TrustCategoryData buildTrustCategoryData(final String nodeName, final String entityProfile) {
        final List<String> trustedCertificateFdnList = new ArrayList<>();
        trustedCertificateFdnList.add("ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=1");
        trustedCertificateFdnList.add("ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=2");
        trustedCertificateFdnList.add("ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=3");
        trustedCertificateFdnList.add("ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=4");
        trustedCertificateFdnList.add("ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=5");
        if ("NODE_EXTRA_CA_EP".equals(entityProfile)) {
            trustedCertificateFdnList.add("ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=6");
        }
        final TrustCategoryData trustCategoryData = new TrustCategoryData("oamTrustCategory", trustedCertificateFdnList);
        return trustCategoryData;
    }

    private List<TrustedCertificateData> buildListTrustedCertificateData(final String entityProfile) {
        final List<TrustedCertificateData> trustedCertificateDataList = new ArrayList<>()
        final TrustedCertificateData trustedCertificateData1 = new TrustedCertificateData(
                "ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=1",
                "16:E9:A0:4A:30:46:EE:D2:5D:7E:F1:EB:63:7A:27:87:84:2D:42:3B:88:33:E5:36:B7:43:19:B2:CB:27:1D:0E",
                "OU=Rosersberg_stsvp6enm44,O=Ericsson,C=SE,CN=ENM_PKI_Root_CA",
                "OU=Rosersberg_stsvp6enm44, O=Ericsson, C=SE, CN=ENM_PKI_Root_CA",
                "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/stsvp6enm44_PKI_Root_CA/1820e0466dbe8a7d/active/stsvp6enm44_PKI_Root_CA",
                "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURlakNDQW1LZ0F3SUJBZ0lJR0NEZ1JtMitpbjB3RFFZSktvWklodmNOQVFFTEJRQXdXekVZTUJZR0ExVUUKQXd3UFJVNU5YMUJMU1Y5U2IyOTBYME5CTVFzd0NRWURWUVFHRXdKVFJURVJNQThHQTFVRUNnd0lSWEpwWTNOegpiMjR4SHpBZEJnTlZCQXNNRmxKdmMyVnljMkpsY21kZmMzUnpkbkEyWlc1dE5EUXdIaGNOTWpJd09URTFNVGMwCk5qTTBXaGNOTXpJd09URTFNVGMwTmpNMFdqQmJNUmd3RmdZRFZRUUREQTlGVGsxZlVFdEpYMUp2YjNSZlEwRXgKQ3pBSkJnTlZCQVlUQWxORk1SRXdEd1lEVlFRS0RBaEZjbWxqYzNOdmJqRWZNQjBHQTFVRUN3d1dVbTl6WlhKegpZbVZ5WjE5emRITjJjRFpsYm0wME5EQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCCkFMWlhVU1h3LzE2d3N0L0liRGUvUnpRRTRGS2J5QS9iNytFWm9FL0pTblZDLzlrL3RWREJ3THJHd1F1c3hOdmoKT0VHM0NPQ1RyZUZuWk1NWnlhckY2ajU4bE1rcWRLem9qT1MxcnhEbi8zWmlydTMvNjBRTlRKU1RwVXJXcDJFZwpvR0lYc2VRY1RSaFMrbUFjYUlKS1orakRqRzNmblBUSWo5bVlLNERQdlNZSVFRb3ZrYUgvQ2NJNWtEWlBGOUZWClNBdjkvcTgzTFFZQ1k3azZvMUdRMGF6VEZROTg0VDhtL0Q3RHlBanhmZGRMTEdQLzZjUy92YjVkeGZ4VFA2blkKMEhMTFBOd1BPWUxaY1ZaLytaY1NTSGNoUzRyaTFVSGVwaWtmaXNkOUo4TW4wck9QY0pjU2pVT3RxNWdhd0J5eQpQUU5IWjMwWmcwVmlSbGtybzNNYnloc0NBd0VBQWFOQ01FQXdIUVlEVlIwT0JCWUVGSEtFRnZMdHN0cW5rYmxoCk9MdzVKN1h2TTlNVk1BOEdBMVVkRXdFQi93UUZNQU1CQWY4d0RnWURWUjBQQVFIL0JBUURBZ0VHTUEwR0NTcUcKU0liM0RRRUJDd1VBQTRJQkFRQzFUUWpJVWpYRHIyOFhLaGpHRDcyeHkyTVZmYkwweHA2ZEVKWmZCTVE3ckhPbAoydVZrT1pGc0htZXRlL0sxU2c5Z24vTElnWGlsVFZ1aE1WT1krLzJTWmZNL1pyQUFLRWpKRVVoS1hJU282bmdRCjBib2UwbitmWDFJTmlsdnZCZHVRd2c3UlM3T29tSTFPdEphdUlpejhEMWJRcmF0VGxUbGJ3ejNmenBjV0xSdE0KTXFMRHZmV2N3SHBZVFdHeUF2bElLNmtpUTUya0Zacjhwcmt2SG9YeVVnSFQwU2lqSjFDS2xHTW83ZW5UL3FyaQpCdnI5YmxVekxrdzFGZnkyUjhlWllFYVpPYmltVDJMMERFcnFrWHMxeUV2Tlc3UDJ5ZzBUby9ZVDZQeUJmMS9rCnNzV2hERXR1c2dXK1JMdVN1NWFzemtnOUlRVU8vQVZIOGF3NGFwRFQKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=",
                "OU=Rosersberg_stsvp6enm44,O=Ericsson,C=SE,CN=ENM_PKI_Root_CA",
                )
        trustedCertificateData1.setCrlsUri(crlsUri)
        trustedCertificateDataList.add(trustedCertificateData1)
        final TrustedCertificateData trustedCertificateData2 = new TrustedCertificateData(
                "ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=2",
                "DE:54:F4:FA:96:22:EC:45:30:DE:37:01:33:18:16:4A:7D:46:E2:5D:C7:FE:7C:3A:E1:20:C3:C9:0B:12:B5:8A",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=ENM_PKI_Root_CA",
                "OU=BUCI_DUAC_NAM, O=ERICSSON, C=SE, CN=ENM_PKI_Root_CA",
                "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/5ac6b6d088452df/active/ENM_PKI_Root_CA",
                "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURhRENDQWxDZ0F3SUJBZ0lJQmF4cmJRaUVVdDh3RFFZSktvWklodmNOQVFFTEJRQXdVakVZTUJZR0ExVUUKQXd3UFJVNU5YMUJMU1Y5U2IyOTBYME5CTVFzd0NRWURWUVFHRXdKVFJURVJNQThHQTFVRUNnd0lSVkpKUTFOVApUMDR4RmpBVUJnTlZCQXNNRFVKVlEwbGZSRlZCUTE5T1FVMHdIaGNOTWpNd016TXhNRE15TVRNMFdoY05Nek13Ck16TXhNRE15TVRNMFdqQlNNUmd3RmdZRFZRUUREQTlGVGsxZlVFdEpYMUp2YjNSZlEwRXhDekFKQmdOVkJBWVQKQWxORk1SRXdEd1lEVlFRS0RBaEZVa2xEVTFOUFRqRVdNQlFHQTFVRUN3d05RbFZEU1Y5RVZVRkRYMDVCVFRDQwpBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUtqVGY3QVFrbmZCVFRZQ0F0dWp6YUVOCmdUcElyMkVQbi9zMk5VY3d3dzRydXNnblp2USt5T0tON2ttNXlsRGl3UUVXb3g2MmZsMVl2OGFhUHVidnRacmwKL3NWaU5hemYzanQwOUdqRUN3TGp1YWNkcURYR3RXc2dsd1V0YXRRaGh1aHlhcnhwWkt1TWxHZ3czM3VZY29aKwpOdVpobHJpd2dCeVJlNkcwVllEbW9LczZmWTBRSkFWWXZGRHZsUnlFSUtjR2wrM3huVGFhd2FaSjlOeTFUenNzCjZ0VUhFREszSWJxNEdqRnhERzhSaDZqWHdpTEhsQyt3aDJtREFodWZBc1JtMklSZk9xY3hESUtRMEdGVWJiWmQKdGxhalZHZWZ2dS93NGJvZ3JJbVlLdFBoNXhPeENZbmQ5ZmlqdVpXYndJRzMrcDFRUUduWUt3bFpYNTBmTDdVQwpBd0VBQWFOQ01FQXdIUVlEVlIwT0JCWUVGSXdWN21YMmhiYTRvWHFDMFEvbTFDVnVxQk1qTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0RnWURWUjBQQVFIL0JBUURBZ0VHTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFDSnBoRjUKN3FuNERSclpJZXM4UzJsYTFTUW04QysvbUozTlJVekhKYXBZNFJEcEc4TFhJSGViQlV6YjgrWlUvRVlBZDFsTwptWWRUT1FISDJkM1ZGVW12WExKck9CcW56aUw0K1dBZy83Y1IrbWZGSERHY1BLQ2pDYVBZY3BzNW1qazFyRUFrCmFzYnRZbFlNM1pDWHYvM1YyUTVOS3UxNC9vcHljVWV3LzdQYllRVEllc1U1cVFlUHhYdVMzdzF6L05pRFpTNkUKRzhETHNxWWZwS0F0VmxNeFk5d2t3WVVrQUlGZm5jcWlGUFZoWmdiMjJjWUNJME5yQnV6Tm5WbGZFeC9pMmxSRQo4MVJ6ZmkwejBmdit2Tzc4dnhjYytnQnV4c1lhcWorY0N3SktZK2lTZWJVeWdtcEtpYTlWOXNuTlAya1NJbCtZCnRwVjhwU09pRklPMmczenMKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=ENM_PKI_Root_CA",
                )
        trustedCertificateData2.setCrlsUri(crlsUri)
        trustedCertificateDataList.add(trustedCertificateData2)
        final TrustedCertificateData trustedCertificateData3 = new TrustedCertificateData(
                "ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=3",
                "18:DE:43:7A:7D:6F:1A:D7:12:93:1F:E9:3E:86:1B:33:69:8A:8B:DE:C1:92:BA:5A:82:D2:AB:55:E3:D9:06:46",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=NE_OAM_CA",
                "OU=BUCI_DUAC_NAM, O=ERICSSON, C=SE, CN=NE_OAM_CA",
                "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/NE_OAM_CA/35de63e5e57da597/active/ENM_PKI_Root_CA",
                "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUViekNDQTFlZ0F3SUJBZ0lJTmQ1ajVlVjlwWmN3RFFZSktvWklodmNOQVFFTEJRQXdVakVZTUJZR0ExVUUKQXd3UFJVNU5YMUJMU1Y5U2IyOTBYME5CTVFzd0NRWURWUVFHRXdKVFJURVJNQThHQTFVRUNnd0lSVkpKUTFOVApUMDR4RmpBVUJnTlZCQXNNRFVKVlEwbGZSRlZCUTE5T1FVMHdIaGNOTWpNd016TXhNRE15TVRRMVdoY05NekV3Ck16TXhNRE15TVRRMVdqQk1NUkl3RUFZRFZRUUREQWxPUlY5UFFVMWZRMEV4Q3pBSkJnTlZCQVlUQWxORk1SRXcKRHdZRFZRUUtEQWhGVWtsRFUxTlBUakVXTUJRR0ExVUVDd3dOUWxWRFNWOUVWVUZEWDA1QlRUQ0NBU0l3RFFZSgpLb1pJaHZjTkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFJMW9kUmluUnpZaWxwcGlLL2ZIUmRUak5EZ1U3RXdrCnZKQTIvN01UNkZldVo3dm9pWGZFeFVyajlMOXk3U0k4QVdGakZ1VTl6MEFSZmVTSEYyalNuS0ZiNFRJVHBacFoKT01RQ21pTUxKVEhuNmxUdHpXcFRWbmErZ3RhZHorSjVrb2s5MkFldERZcTQwa2ZZL05ycUlZWTQ2Y1h4UmE2cwp3MjRROEIrTmVjeUhSNnRjUkVFR0syY0JjZWNqRjUwREZGZERIU3VSaXpIb2JzWWVEZHdaSll2MXYvSi9QZG16Ck5HTVlVMVROQ1FPeThtSEFsNnorMjBkbmpDT1RiNnQrR3dKcHdUUllQNFErZ1dMNlpQWmZ3ZnhwYWxMRytIYmcKbzNwK0tyNkR5OVNYVXhNcWgyVDBLeU5qWG5BcmNQY2FRcEZHeFB4QkJrekJyLzVWaDdUbjB3Y0NBd0VBQWFPQwpBVTB3Z2dGSk1JSGxCZ05WSFI4RWdkMHdnZG93WmFCam9HR0dYMmgwZEhBNkx5OHhPVEl1TVRZNExqQXVNVFUxCk9qZ3dPVEl2Y0d0cExXTmtjSE0vWTJGZmJtRnRaVDFGVGsxZlVFdEpYMUp2YjNSZlEwRW1ZMkZmWTJWeWRGOXoKWlhKcFlXeHVkVzFpWlhJOU5XRmpObUkyWkRBNE9EUTFNbVJtTUhHZ2I2QnRobXRvZEhSd09pOHZXekl3TURFNgpNV0kzTURvNE1tRXhPakV3TXpvNk1UZ3hYVG80TURreUwzQnJhUzFqWkhCelAyTmhYMjVoYldVOVJVNU5YMUJMClNWOVNiMjkwWDBOQkptTmhYMk5sY25SZmMyVnlhV0ZzYm5WdFltVnlQVFZoWXpaaU5tUXdPRGcwTlRKa1pqQWQKQmdOVkhRNEVGZ1FVcXlXTHFjSWtZSjQ3MzFwMzZWaDBWaWRSMnNnd0R3WURWUjBUQVFIL0JBVXdBd0VCL3pBZgpCZ05WSFNNRUdEQVdnQlNNRmU1bDlvVzJ1S0Y2Z3RFUDV0UWxicWdUSXpBT0JnTlZIUThCQWY4RUJBTUNBUVl3CkRRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFGTEo3MlB0OW5zZ0VWK21KNlRCb3NsSUVVbjZ2eXdaM3hXYk9saFUKN2ZFZytxWnFyZVZvdDV4akdqK0FuU2wrS1hnNFZZTXJWc1huck91S0RtSndFb0Jac01rYU1JeTJXODJPSnFsMwpjZ0hHNGJTWUwxcHlWL0dubjlqbFptRmFIVzJyN2NiTHJJaEVSbFJNNUZaOEVrbEdCR1dzdTdwZm5BS2ZaLy9XCk5PMkxoRkJzRFdBckRsNjFyTGpVMCtFVGs5cjluTDIxeGgzencybytGakJPU0RmYVBRZnVVKzVnVTJwc1oxamkKMFdLdmlxWXVjZGRWbEpMZUU1MnNqNGg2RGhXWmgrcXJjQkduTGlWVFppYXVpSTVJSXJZRy9VQ3NkYjVkdmZsVgpRQkI2a25VR3JGdjBYNHd1VVd0UmZSNFFhMHhNMEVyd0FBRHVNc0FCMFk4bmxZdz0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=ENM_PKI_Root_CA",
                )
        trustedCertificateData3.setCrlsUri(crlsUri)
        trustedCertificateDataList.add(trustedCertificateData3)
        final TrustedCertificateData trustedCertificateData4 = new TrustedCertificateData(
                "ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=4",
                "C4:6D:E5:65:2E:FD:BB:7D:F8:9B:C3:A5:86:C5:8A:AC:57:8E:8F:03:6B:1D:B6:1F:24:67:87:15:85:51:A1:A5",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=ENM_Infrastructure_CA",
                "OU=BUCI_DUAC_NAM, O=ERICSSON, C=SE, CN=ENM_Infrastructure_CA",
                "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_Infrastructure_CA/31ef99af217c185b/active/ENM_PKI_Root_CA",
                "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUVlekNDQTJPZ0F3SUJBZ0lJTWUrWnJ5RjhHRnN3RFFZSktvWklodmNOQVFFTEJRQXdVakVZTUJZR0ExVUUKQXd3UFJVNU5YMUJMU1Y5U2IyOTBYME5CTVFzd0NRWURWUVFHRXdKVFJURVJNQThHQTFVRUNnd0lSVkpKUTFOVApUMDR4RmpBVUJnTlZCQXNNRFVKVlEwbGZSRlZCUTE5T1FVMHdIaGNOTWpNd016TXhNRE15TVRRMldoY05NekV3Ck16TXhNRE15TVRRMldqQllNUjR3SEFZRFZRUUREQlZGVGsxZlNXNW1jbUZ6ZEhKMVkzUjFjbVZmUTBFeEN6QUoKQmdOVkJBWVRBbE5GTVJFd0R3WURWUVFLREFoRlVrbERVMU5QVGpFV01CUUdBMVVFQ3d3TlFsVkRTVjlFVlVGRApYMDVCVFRDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTXBGRzJlSnZsZk1xamR4Cm9nV0xuTjNHbDhWVythWnhheExTR3k2cnorSk55ZFFnRzdiNXNoUlFHRXVaYXcvYURNTWE0UTlTdzErdkZ1S0cKQUdiMEJkRDNNUm5GQ0xVbXVUSzBZa0hKeFBqVnMxWGVNbU5xVHVOR2paOURHaXdtMGNSemhsV1BxclpYMDFLcwpMWkRLNGtLeFYzTXFNT2xzS2ZwVy9aNENZZFg4YmM3RXduR2w2SW54d01OdlRlUHphU2U1ek5yVUZPL0pXK1h0CjczYVZ6cWY2UTFDbzFEelVteW1QOE9mWE9SbFJCSWpoMWsxSC96M3lmZ3JqRlBRVzl5MzhrRGlZeDlPSjZuTDEKYVoyS0srejk2dGdBVFd0QWRiZGJrRG9sWHpOZWd2Sm9oU3ZhZktiRy9VU0toRUxOeGtXYnF0OGd2YlAzWUdYUwpnWjRFaU1FQ0F3RUFBYU9DQVUwd2dnRkpNSUhsQmdOVkhSOEVnZDB3Z2Rvd1phQmpvR0dHWDJoMGRIQTZMeTh4Ck9USXVNVFk0TGpBdU1UVTFPamd3T1RJdmNHdHBMV05rY0hNL1kyRmZibUZ0WlQxRlRrMWZVRXRKWDFKdmIzUmYKUTBFbVkyRmZZMlZ5ZEY5elpYSnBZV3h1ZFcxaVpYSTlOV0ZqTm1JMlpEQTRPRFExTW1SbU1IR2diNkJ0aG10bwpkSFJ3T2k4dld6SXdNREU2TVdJM01EbzRNbUV4T2pFd016bzZNVGd4WFRvNE1Ea3lMM0JyYVMxalpIQnpQMk5oClgyNWhiV1U5UlU1TlgxQkxTVjlTYjI5MFgwTkJKbU5oWDJObGNuUmZjMlZ5YVdGc2JuVnRZbVZ5UFRWaFl6WmkKTm1Rd09EZzBOVEprWmpBZEJnTlZIUTRFRmdRVTZiWlo5WFNaU0g5aG91MXN2T0hVQ2dXbURFOHdEd1lEVlIwVApBUUgvQkFVd0F3RUIvekFmQmdOVkhTTUVHREFXZ0JTTUZlNWw5b1cydUtGNmd0RVA1dFFsYnFnVEl6QU9CZ05WCkhROEJBZjhFQkFNQ0FRWXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBRGpEby93MURXM2NCbURMUjhqcFdvUjQKZXdnTzRZUFVNc1RQNlRwNlVIUDRKbGR5dDY4QkxDV1AwR0twK25iRVk3QzBUb2R5TEVRTlM4N2N1cWJITG5RUAo2cVhiZjQxMWhRN1B3M3RoRmF3NytlOFRXdm1UVFozcWFFV2x0RzcwWkFISUhYOFhHei9LbjViZzYrNVIwbi85ClRlcUJYZmJiK2VKcE1MRWc4MkNiQkV0QUd5N0FKa0M4ME9yRTJCeC9idzRBWFV5c1VXblJMa3JBNGJiVlJzSlEKUWlrOFhCQmg1eGhpQ3NzL3lObEJYcEJlYnpaZWp4dTAvazZxSnhyY0o1M1c0REtIQ2JUV1ZZUmpyZUFpR2ltbApDK1A3S3JkQ1VQQzgvVkhyeTNSNzAzYVNGWStvTkJEcjgyaUpLOW9ublIvRU5ocENYWGpBYmNwdmtPSGpOVGM9Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=ENM_PKI_Root_CA",
                )
        trustedCertificateData4.setCrlsUri(crlsUri)
        trustedCertificateDataList.add(trustedCertificateData4)
        final TrustedCertificateData trustedCertificateData5 = new TrustedCertificateData(
                "ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=5",
                "12:B5:08:EE:AF:02:9F:F7:A3:75:0F:F0:0C:57:33:89:84:01:0B:F1:32:DF:2D:14:09:54:DE:6C:8E:95:CC:0D",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=ENM_OAM_CA",
                "OU=BUCI_DUAC_NAM, O=ERICSSON, C=SE, CN=ENM_OAM_CA",
                "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_OAM_CA/1c4a7891ee3cbcd7/active/ENM_Infrastructure_CA",
                "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUVoRENDQTJ5Z0F3SUJBZ0lJSEVwNGtlNDh2TmN3RFFZSktvWklodmNOQVFFTEJRQXdXREVlTUJ3R0ExVUUKQXd3VlJVNU5YMGx1Wm5KaGMzUnlkV04wZFhKbFgwTkJNUXN3Q1FZRFZRUUdFd0pUUlRFUk1BOEdBMVVFQ2d3SQpSVkpKUTFOVFQwNHhGakFVQmdOVkJBc01EVUpWUTBsZlJGVkJRMTlPUVUwd0hoY05Nak13TXpNeE1ETXlNVFE1CldoY05NekV3TXpNeE1ESTFNRFE1V2pCTk1STXdFUVlEVlFRRERBcEZUazFmVDBGTlgwTkJNUXN3Q1FZRFZRUUcKRXdKVFJURVJNQThHQTFVRUNnd0lSVkpKUTFOVFQwNHhGakFVQmdOVkJBc01EVUpWUTBsZlJGVkJRMTlPUVUwdwpnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFDTmREYStLUEhZR0huQWNxUS90dmRkClBMSUkwbUJxUVJlaDJBc1BjWTVPWXlwUlI1elJvWVEwdnRvTy9nSjBmenJ0THVsTnhnMno3VC9UY2QvekcwYTAKS1ZyQlNQUERJaG5xdnNvK2djc2ZKNnpDd0F1clg4djY5Z3JiUGo5N0tPQ2tQUnh0OWMwWkJXTEZWRWEybnFjSQpOSkc2VExZN2tuM0VQQjYwUnBZa2thVlBuODhEUytoZW0yd0t4OXdYNTZCWmJnY1dtLzNNcVUzMmhXSlZUN3JJClZPZ296OERJMnVUcmRLdk4rRUVRQzR5MWFTRzhTcXk2cUd5bXdmNHV6OFVmOGFlWDJ2SVB5ZE1ES1V6citXMWgKK1A3TG1IazNDODh2V2RneG1JSlRDNmMvMUlVM3pLOHo4UmdxdUhBeUJFd211TVBIY2x0YTJuMEMrWDFYRmVjUgpBZ01CQUFHamdnRmJNSUlCVnpDQjh3WURWUjBmQklIck1JSG9NR3lnYXFCb2htWm9kSFJ3T2k4dk1Ua3lMakUyCk9DNHdMakUxTlRvNE1Ea3lMM0JyYVMxalpIQnpQMk5oWDI1aGJXVTlSVTVOWDBsdVpuSmhjM1J5ZFdOMGRYSmwKWDBOQkptTmhYMk5sY25SZmMyVnlhV0ZzYm5WdFltVnlQVE14WldZNU9XRm1NakUzWXpFNE5XSXdlS0Iyb0hTRwpjbWgwZEhBNkx5OWJNakF3TVRveFlqY3dPamd5WVRFNk1UQXpPam94T0RGZE9qZ3dPVEl2Y0d0cExXTmtjSE0vClkyRmZibUZ0WlQxRlRrMWZTVzVtY21GemRISjFZM1IxY21WZlEwRW1ZMkZmWTJWeWRGOXpaWEpwWVd4dWRXMWkKWlhJOU16Rmxaams1WVdZeU1UZGpNVGcxWWpBZEJnTlZIUTRFRmdRVXFERU1xbHFoRWRRS2JJRzBDWDJGVlJaVQpLL2N3RHdZRFZSMFRBUUgvQkFVd0F3RUIvekFmQmdOVkhTTUVHREFXZ0JUcHRsbjFkSmxJZjJHaTdXeTg0ZFFLCkJhWU1UekFPQmdOVkhROEJBZjhFQkFNQ0FRWXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBSm0wdXlmMkFoc0EKS1pBdFl5VkFhKzJrckFTdmd2RG9ZOHM5QWZiLzRJZC9xeVhvQzM4Y2NPT1FvNVVkQko5R2FRS2JBbmFCQkFCbQpLUjNsdDVnZGYwLy9kd2pDZWRHRFpIdUM4SStCb3FVWG1yNTFrSHJ1eHVaWEhnQWNQSmRmTE4rbDdLdE1NbkZnClhIWFY2V25RajY4ejFBVkEvdjkxbUVSVnNnRVorQU0wb2xlY3paVDIrMTZOTVVtMWhPK2RscFlHd1hPdGV4QjMKaStIaEIrWHA5bHhiOUJVZVlmSEpEYUU2RDdZSzU0MXdWTDBjT2dUdXhsYnRiZlh2TXUxSWRUMFhGQXNGN1YwWApyU0tXVGYya2tYS3FDQjVCUjd4Tzl2Rm12TDVSekNzT1RncUl1aU9JRzVFbFdWWTd1V0JvVGhpQ1FkcUZXbzJWCit6QzkwbDhGOHR3PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==",
                "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=ENM_Infrastructure_CA",
                )
        trustedCertificateData5.setCrlsUri(crlsUri)
        trustedCertificateDataList.add(trustedCertificateData5)
        if ("NODE_EXTRA_CA_EP".equals(entityProfile)) {
            final TrustedCertificateData trustedCertificateData6 = new TrustedCertificateData(
                    "ManagedElement=1,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=6",
                    "26:EA:A0:4A:30:46:EE:D2:5D:7E:F1:EB:63:7A:27:87:84:2D:42:3B:88:33:E5:36:B7:43:19:B2:CB:27:1D:0E",
                    "OU = Rosersberg_stsvp6enm45, CN  =  ENM\\, PKI Root CA  , O = Ericsson, C=SE",
                    "OU = Rosersberg_stsvp6enm45, CN  =  ENM\\, PKI Root CA  , O = Ericsson, C=SE",
                    "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/stsvp6enm44_PKI_Root_CA/1820e0466dbe8a7d/active/stsvp6enm44_PKI_Root_CA",
                    "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURlakNDQW1LZ0F3SUJBZ0lJR0NEZ1JtMitpbjB3RFFZSktvWklodmNOQVFFTEJRQXdXekVZTUJZR0ExVUUKQXd3UFJVNU5YMUJMU1Y5U2IyOTBYME5CTVFzd0NRWURWUVFHRXdKVFJURVJNQThHQTFVRUNnd0lSWEpwWTNOegpiMjR4SHpBZEJnTlZCQXNNRmxKdmMyVnljMkpsY21kZmMzUnpkbkEyWlc1dE5EUXdIaGNOTWpJd09URTFNVGMwCk5qTTBXaGNOTXpJd09URTFNVGMwTmpNMFdqQmJNUmd3RmdZRFZRUUREQTlGVGsxZlVFdEpYMUp2YjNSZlEwRXgKQ3pBSkJnTlZCQVlUQWxORk1SRXdEd1lEVlFRS0RBaEZjbWxqYzNOdmJqRWZNQjBHQTFVRUN3d1dVbTl6WlhKegpZbVZ5WjE5emRITjJjRFpsYm0wME5EQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCCkFMWlhVU1h3LzE2d3N0L0liRGUvUnpRRTRGS2J5QS9iNytFWm9FL0pTblZDLzlrL3RWREJ3THJHd1F1c3hOdmoKT0VHM0NPQ1RyZUZuWk1NWnlhckY2ajU4bE1rcWRLem9qT1MxcnhEbi8zWmlydTMvNjBRTlRKU1RwVXJXcDJFZwpvR0lYc2VRY1RSaFMrbUFjYUlKS1orakRqRzNmblBUSWo5bVlLNERQdlNZSVFRb3ZrYUgvQ2NJNWtEWlBGOUZWClNBdjkvcTgzTFFZQ1k3azZvMUdRMGF6VEZROTg0VDhtL0Q3RHlBanhmZGRMTEdQLzZjUy92YjVkeGZ4VFA2blkKMEhMTFBOd1BPWUxaY1ZaLytaY1NTSGNoUzRyaTFVSGVwaWtmaXNkOUo4TW4wck9QY0pjU2pVT3RxNWdhd0J5eQpQUU5IWjMwWmcwVmlSbGtybzNNYnloc0NBd0VBQWFOQ01FQXdIUVlEVlIwT0JCWUVGSEtFRnZMdHN0cW5rYmxoCk9MdzVKN1h2TTlNVk1BOEdBMVVkRXdFQi93UUZNQU1CQWY4d0RnWURWUjBQQVFIL0JBUURBZ0VHTUEwR0NTcUcKU0liM0RRRUJDd1VBQTRJQkFRQzFUUWpJVWpYRHIyOFhLaGpHRDcyeHkyTVZmYkwweHA2ZEVKWmZCTVE3ckhPbAoydVZrT1pGc0htZXRlL0sxU2c5Z24vTElnWGlsVFZ1aE1WT1krLzJTWmZNL1pyQUFLRWpKRVVoS1hJU282bmdRCjBib2UwbitmWDFJTmlsdnZCZHVRd2c3UlM3T29tSTFPdEphdUlpejhEMWJRcmF0VGxUbGJ3ejNmenBjV0xSdE0KTXFMRHZmV2N3SHBZVFdHeUF2bElLNmtpUTUya0Zacjhwcmt2SG9YeVVnSFQwU2lqSjFDS2xHTW83ZW5UL3FyaQpCdnI5YmxVekxrdzFGZnkyUjhlWllFYVpPYmltVDJMMERFcnFrWHMxeUV2Tlc3UDJ5ZzBUby9ZVDZQeUJmMS9rCnNzV2hERXR1c2dXK1JMdVN1NWFzemtnOUlRVU8vQVZIOGF3NGFwRFQKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=",
                    "OU = Rosersberg_stsvp6enm45, CN  =  ENM\\, PKI Root CA  , O = Ericsson, C=SE",

                    )
            trustedCertificateData6.setCrlsUri(crlsUri)
            trustedCertificateDataList.add(trustedCertificateData6)
        }
        return trustedCertificateDataList;
    }
}
