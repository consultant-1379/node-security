/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.impl

import javax.ws.rs.core.Response.Status

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.CertificateRevocation
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.CertificateRevocations
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.Certificates
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentCmpConfig
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustCategories
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustCategory
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificate
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificates
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.VerboseEnrollmentInfo
import com.ericsson.oss.services.security.nscs.nbi.NbiManager
import com.ericsson.oss.services.security.nscs.nbi.api.dto.AlgorithmAndKeySizeNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CredentialAttributesNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CredentialsTypeNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.DomainRequestNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.EnrollmentRequestNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SnmpAttributesNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SnmpAttributesNbiDto.AuthAlgoEnum
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SnmpAttributesNbiDto.PrivAlgoEnum
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SubjectAlternativeNameNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SubjectAlternativeNameStringValueNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SubjectAlternativeNameTypeNbiDto
import com.ericsson.oss.services.security.nscs.nbi.api.dto.UserCredentialsNbiDto
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsLdapResponse
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance

import spock.lang.Unroll


class NbiV1NodesRestResourceTest extends CdiSpecification {

    @ObjectUnderTest
    NbiV1NodesRestResource restResource

    @ImplementationInstance
    NbiManager nbiManager = [
        createOrUpdateNodeCredentials : { String nodeNameOrFdn, CredentialAttributes credentialAttributes, String enablingPredefinedENMLDAPUser ->
            return
        },
        createOrUpdateNodeSnmp : { String nodeNameOrFdn, SnmpV3Attributes snmpV3Attributes, SnmpSecurityLevel snmpSecurityLevel ->
            return
        },
        generateEnrollmentInfo : { String nodeNameOrFdn, String domainName, String ipFamily, NodeDetails nodeDetails ->
            EnrollmentInfo enrollmentInfo = null
            if (scenario != "null-response") {
                enrollmentInfo = new EnrollmentInfo()
                VerboseEnrollmentInfo verboseEnrollmentInfo = null
                if (scenario != "null-verbose-response") {
                    verboseEnrollmentInfo = new VerboseEnrollmentInfo()
                    if (scenario != "null-enrollment-response") {
                        EnrollmentCmpConfig cmp = new EnrollmentCmpConfig()
                        TrustedCertificates trustedCerts = new TrustedCertificates()
                        TrustCategories trustCategories = new TrustCategories()
                        if (scenario == "empty-enrollment-response") {
                            trustedCerts.setTrustedCertificate([])
                            trustCategories.setTrustCategory([])
                        } else {
                            TrustedCertificate trustedCert = new TrustedCertificate()
                            TrustCategory trustCategory = new TrustCategory()
                            if (scenario != "empty-trust-response") {
                                CertificateRevocations crls = new CertificateRevocations()
                                Certificates certificates = new Certificates()
                                if (scenario != "null-not-empty-trust-response") {
                                    if (scenario == "empty-not-empty-trust-response") {
                                        crls.setCertificateRevocations([])
                                        certificates.setCertificate([])
                                    } else if (scenario == "not-empty-not-empty-trust-response") {
                                        CertificateRevocation crl = new CertificateRevocation()
                                        crls.setCertificateRevocations([crl])
                                        certificates.setCertificate(['cert1'])
                                    }
                                }
                                trustedCert.setCrls(crls)
                                trustCategory.setCertificates(certificates)
                            }
                            trustedCerts.setTrustedCertificate([trustedCert])
                            trustCategories.setTrustCategory([trustCategory])
                        }
                        verboseEnrollmentInfo.setEnrollmentCmpConfig(cmp)
                        verboseEnrollmentInfo.setTrustedCertificates(trustedCerts)
                        verboseEnrollmentInfo.setTrustCategories(trustCategories)
                    }
                    enrollmentInfo.setVerboseEnrollmentInfo(verboseEnrollmentInfo)
                }
            }
            return enrollmentInfo
        },
        deleteEnrollmentInfo : { String nodeNameOrFdn, String domainName ->
            if (scenario == "null-response") {
                return null
            }
            NscsResourceInstance resource = new NscsResourceInstance()
            if (scenario == "null-status-response") {
                return resource
            }
            resource.setStatus(scenario)
            return resource
        },
        createLdapConfiguration : { String nodeNameOrFdn, String ipFamily ->
            if (scenario == "null-response") {
                return null
            }
            NscsLdapResponse ldapResponse = new NscsLdapResponse()
            return ldapResponse
        },
        deleteLdapConfiguration : { String nodeNameOrFdn ->
            if (scenario == "null-response") {
                return null
            }
            NscsResourceInstance resource = new NscsResourceInstance()
            if (scenario == "null-status-response") {
                return resource
            }
            resource.setStatus(scenario)
            return resource
        }
    ] as NbiManager

    private String scenario

    def 'object under test'() {
        expect:
        restResource != null
    }

    def 'put node credentials, enable ldapuser'() {
        given:
        def CredentialAttributesNbiDto dto = new CredentialAttributesNbiDto()
        def UserCredentialsNbiDto secureCredentialsNbiDto = new UserCredentialsNbiDto()
        secureCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.SECURE)
        secureCredentialsNbiDto.setCredUser("secureusername")
        secureCredentialsNbiDto.setCredPass("secureuserpass")
        def UserCredentialsNbiDto rootCredentialsNbiDto = new UserCredentialsNbiDto()
        rootCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.ROOT)
        rootCredentialsNbiDto.setCredUser("rootusername")
        rootCredentialsNbiDto.setCredPass("rootuserpass")
        def UserCredentialsNbiDto nodecliCredentialsNbiDto = new UserCredentialsNbiDto()
        nodecliCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.NODE_CLI)
        nodecliCredentialsNbiDto.setCredUser("nodecliusername")
        nodecliCredentialsNbiDto.setCredPass("nodecliuserpass")
        def UserCredentialsNbiDto normalCredentialsNbiDto = new UserCredentialsNbiDto()
        normalCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.NORMAL)
        normalCredentialsNbiDto.setCredUser("normalusername")
        normalCredentialsNbiDto.setCredPass("normaluserpass")
        def UserCredentialsNbiDto nwieaCredentialsNbiDto = new UserCredentialsNbiDto()
        nwieaCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.NWI_E_A)
        nwieaCredentialsNbiDto.setCredUser("nwieausername")
        nwieaCredentialsNbiDto.setCredPass("nwieauserpass")
        def UserCredentialsNbiDto nwiebCredentialsNbiDto = new UserCredentialsNbiDto()
        nwiebCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.NWI_E_B)
        nwiebCredentialsNbiDto.setCredUser("nwiebusername")
        nwiebCredentialsNbiDto.setCredPass("nwiebuserpass")

        dto.setCredentialsList(Arrays.asList(secureCredentialsNbiDto, rootCredentialsNbiDto, nodecliCredentialsNbiDto, normalCredentialsNbiDto, nwieaCredentialsNbiDto,nwiebCredentialsNbiDto))
        dto.setEnablingPredefinedENMLDAPUser("enable")
        when:
        def response = restResource.createOrUpdateNodeCredentials("nodename", dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        response.getEntity().toString().contains("SECURE")
        response.getEntity().toString().contains("NORMAL")
        response.getEntity().toString().contains("ROOT")
        response.getEntity().toString().contains("NODE_CLI")
        response.getEntity().toString().contains("NWI_E_A")
        response.getEntity().toString().contains("NWI_E_B")
    }

    def 'put node credentials, disable ldapuser'() {
        given:
        def CredentialAttributesNbiDto dto = new CredentialAttributesNbiDto()
        def UserCredentialsNbiDto secureCredentialsNbiDto = new UserCredentialsNbiDto()
        secureCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.SECURE)
        secureCredentialsNbiDto.setCredUser("secureusername")
        secureCredentialsNbiDto.setCredPass("secureuserpass")
        dto.setCredentialsList(Arrays.asList(secureCredentialsNbiDto))
        dto.setEnablingPredefinedENMLDAPUser("disable")
        when:
        def response = restResource.createOrUpdateNodeCredentials("nodename", dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        response.getEntity().toString().contains("SECURE")
    }

    def 'put node credentials, null ldapuser'() {
        given:
        def CredentialAttributesNbiDto dto = new CredentialAttributesNbiDto()
        def UserCredentialsNbiDto secureCredentialsNbiDto = new UserCredentialsNbiDto()
        secureCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.SECURE)
        secureCredentialsNbiDto.setCredUser("secureusername")
        secureCredentialsNbiDto.setCredPass("secureuserpass")
        dto.setCredentialsList(Arrays.asList(secureCredentialsNbiDto))
        when:
        def response = restResource.createOrUpdateNodeCredentials("nodename", dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        response.getEntity().toString().contains("SECURE")
    }

    def 'put node credentials, credentialsList empty'() {
        given:
        def CredentialAttributesNbiDto dto =  new CredentialAttributesNbiDto()
        dto.setEnablingPredefinedENMLDAPUser("enable")
        when:
        def response = restResource.createOrUpdateNodeCredentials("nodename", dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'put node credentials, dto null get NscsBadRequestException'() {
        given:
        def CredentialAttributesNbiDto dto = null
        when:
        def response = restResource.createOrUpdateNodeCredentials("nodename", dto)
        then:
        def error = thrown(NscsBadRequestException)
    }

    def 'put node credentials, wrong enablingPredefinedENMLDAPUser get NscsBadRequestException'() {
        given:
        def CredentialAttributesNbiDto dto =  new CredentialAttributesNbiDto()
        def UserCredentialsNbiDto secureCredentialsNbiDto = new UserCredentialsNbiDto()
        secureCredentialsNbiDto.setCredentialsType(CredentialsTypeNbiDto.SECURE)
        secureCredentialsNbiDto.setCredUser("secureusername")
        secureCredentialsNbiDto.setCredPass("secureuserpass")
        dto.setCredentialsList(Arrays.asList(secureCredentialsNbiDto))
        dto.setEnablingPredefinedENMLDAPUser("able")
        when:
        def response = restResource.createOrUpdateNodeCredentials("nodename", dto)
        then:
        def error = thrown(NscsBadRequestException)
    }

    def 'put node snmp AUTHPRIV'() {
        given:
        def SnmpAttributesNbiDto dto = new SnmpAttributesNbiDto()
        dto.setAuthPriv("enable")
        dto.setAuthPassword("AuthPass")
        dto.setPrivPassword("PrivPass")
        dto.setAuthAlgo(AuthAlgoEnum.fromValue("SHA1"))
        dto.setPrivAlgo(PrivAlgoEnum.fromValue("AES128"))
        def SnmpSecurityLevel snmpSecurityLevel= NbiDtoHelper.fromDto(dto.getAuthPriv());
        when:
        def response = restResource.createOrUpdateNodeSnmp("nodename", dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        response.getEntity().toString().contains("enable")
        and:
        snmpSecurityLevel == SnmpSecurityLevel.AUTH_PRIV
    }

    def 'put node snmp AUTHNOPRIV'() {
        given:
        def SnmpAttributesNbiDto dto = new SnmpAttributesNbiDto()
        dto.setAuthPriv("disable")
        dto.setAuthPassword("AuthPass")
        dto.setAuthAlgo(AuthAlgoEnum.fromValue("SHA1"))
        def SnmpSecurityLevel snmpSecurityLevel= NbiDtoHelper.fromDto(dto.getAuthPriv());
        when:
        def response = restResource.createOrUpdateNodeSnmp("nodename", dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        response.getEntity().toString().contains("disable")
        and:
        snmpSecurityLevel == SnmpSecurityLevel.AUTH_NO_PRIV
    }

    def 'put node snmp AUTHNOPRIV with priv data, get exception'() {
        given:
        def SnmpAttributesNbiDto dto = new SnmpAttributesNbiDto()
        dto.setAuthPriv("disable")
        dto.setAuthPassword("AuthPass")
        dto.setPrivPassword("PrivPass")
        dto.setAuthAlgo(AuthAlgoEnum.fromValue("SHA1"))
        dto.setPrivAlgo(PrivAlgoEnum.fromValue("AES128"))
        def SnmpSecurityLevel snmpSecurityLevel= NbiDtoHelper.fromDto(dto.getAuthPriv());
        when:
        def response = restResource.createOrUpdateNodeSnmp("nodename", dto)
        then:
        def error = thrown(NscsBadRequestException)
    }

    def 'put node snmp AUTHPRIV without priv data, get Exception'() {
        given:
        def SnmpAttributesNbiDto dto = new SnmpAttributesNbiDto()
        dto.setAuthPriv("enable")
        dto.setAuthPassword("AuthPass")
        dto.setAuthAlgo(AuthAlgoEnum.fromValue("SHA1"))
        def SnmpSecurityLevel snmpSecurityLevel= NbiDtoHelper.fromDto(dto.getAuthPriv());
        when:
        def response = restResource.createOrUpdateNodeSnmp("nodename", dto)
        then:
        then:
        def error = thrown(NscsBadRequestException)
    }

    def 'put node snmp AUTHPRIV without auth data, get Exception'() {
        given:
        def SnmpAttributesNbiDto dto = new SnmpAttributesNbiDto()
        dto.setAuthPriv("enable")
        dto.setPrivPassword("PrivPass")
        dto.setPrivAlgo(PrivAlgoEnum.fromValue("AES128"))
        def SnmpSecurityLevel snmpSecurityLevel= NbiDtoHelper.fromDto(dto.getAuthPriv());
        when:
        def response = restResource.createOrUpdateNodeSnmp("nodename", dto)
        then:
        then:
        def error = thrown(NscsBadRequestException)
    }

    def 'put node snmp, dto null get exception'() {
        given:
        def SnmpAttributesNbiDto dto = null
        when:
        def response = restResource.createOrUpdateNodeSnmp("nodename", dto)
        then:
        def error = thrown(NscsBadRequestException)
    }

    def 'put node snmp, wrong AUTHPRIV  get exception'() {
        given:
        def SnmpAttributesNbiDto dto = new SnmpAttributesNbiDto()
        dto.setAuthPriv("able")
        dto.setAuthPassword("AuthPass")
        dto.setPrivPassword("PrivPass")
        dto.setAuthAlgo(AuthAlgoEnum.fromValue("SHA1"))
        dto.setPrivAlgo(PrivAlgoEnum.fromValue("AES128"))
        when:
        def response = restResource.createOrUpdateNodeSnmp("nodename", dto)
        then:
        def error = thrown(NscsBadRequestException)
    }

    @Unroll
    def 'post node domain with null DTO and IP family #ipfamily'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", ipfamily, null)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        where:
        ipfamily << [null, "INET", "INET6"]
    }

    @Unroll
    def 'post node domain with empty DTO and IP family #ipfamily'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", ipfamily, dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        where:
        ipfamily << [null, "INET", "INET6"]
    }

    @Unroll
    def 'post node domain with empty domain DTO and IP family #ipfamily'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        def DomainRequestNbiDto domainDto = new DomainRequestNbiDto()
        dto.setDomain(domainDto)
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", ipfamily, dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        where:
        ipfamily << [null, "INET", "INET6"]
    }

    @Unroll
    def 'post node domain with algorithm DTO #algorithm'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        def DomainRequestNbiDto domainDto = new DomainRequestNbiDto()
        domainDto.setAlgorithmAndKeySize(algorithm)
        dto.setDomain(domainDto)
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        where:
        algorithm << AlgorithmAndKeySizeNbiDto.values()
    }

    @Unroll
    def 'post node domain with one valid SAN DTO of type #santype'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        def DomainRequestNbiDto domainDto = new DomainRequestNbiDto()
        def SubjectAlternativeNameNbiDto sanDto = new SubjectAlternativeNameNbiDto()
        def SubjectAlternativeNameTypeNbiDto sanTypeDto = santype;
        def SubjectAlternativeNameStringValueNbiDto sanValueDto = new SubjectAlternativeNameStringValueNbiDto()
        sanValueDto.setValue('value')
        sanDto.setSubjectAlternativeNameType(sanTypeDto)
        sanDto.setSubjectAlternativeNameValue(sanValueDto)
        domainDto.setSubjectAlternativeNames([sanDto])
        dto.setDomain(domainDto)
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        where:
        santype << SubjectAlternativeNameTypeNbiDto.values()
    }

    @Unroll
    def 'post node domain with one invalid SAN DTO of type #santype and value #sanvalue'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        def DomainRequestNbiDto domainDto = new DomainRequestNbiDto()
        def SubjectAlternativeNameNbiDto sanDto = new SubjectAlternativeNameNbiDto()
        def SubjectAlternativeNameTypeNbiDto sanTypeDto = santype;
        def SubjectAlternativeNameStringValueNbiDto sanValueDto = new SubjectAlternativeNameStringValueNbiDto()
        sanValueDto.setValue(sanvalue)
        sanDto.setSubjectAlternativeNameType(sanTypeDto)
        sanDto.setSubjectAlternativeNameValue(sanValueDto)
        domainDto.setSubjectAlternativeNames([sanDto])
        dto.setDomain(domainDto)
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, dto)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
        where:
        santype << [
            SubjectAlternativeNameTypeNbiDto.DNS_NAME,
            null,
            null
        ]
        sanvalue << [null, "value", null]
    }

    def 'post node domain with multiple SAN DTOs'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        def DomainRequestNbiDto domainDto = new DomainRequestNbiDto()
        def SubjectAlternativeNameNbiDto sanDto = new SubjectAlternativeNameNbiDto()
        def SubjectAlternativeNameTypeNbiDto sanTypeDto = SubjectAlternativeNameTypeNbiDto.DNS_NAME;
        def SubjectAlternativeNameStringValueNbiDto sanValueDto = new SubjectAlternativeNameStringValueNbiDto()
        sanValueDto.setValue("value")
        sanDto.setSubjectAlternativeNameType(sanTypeDto)
        sanDto.setSubjectAlternativeNameValue(sanValueDto)
        domainDto.setSubjectAlternativeNames([sanDto, sanDto])
        dto.setDomain(domainDto)
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, dto)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'post node domain with null response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "null-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'post node domain with null verbose response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "null-verbose-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'post node domain with null enrollment response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "null-enrollment-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'post node domain with empty enrollment response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "empty-enrollment-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'post node domain with empty trust response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "empty-trust-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'post node domain with null not empty trust response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "null-not-empty-trust-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'post node domain with empty not empty trust response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "empty-not-empty-trust-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'post node domain with not empty not empty trust response'() {
        given:
        def EnrollmentRequestNbiDto dto = new EnrollmentRequestNbiDto()
        scenario = "not-empty-not-empty-trust-response"
        when:
        def response = restResource.generateEnrollmentInfo("nodename", "domainname", null, null)
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'delete node domain null response'() {
        given:
        scenario = "null-response"
        when:
        def response = restResource.deleteEnrollmentInfo("nodename", "domainname")
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'delete node domain null status response'() {
        given:
        scenario = "null-status-response"
        when:
        def response = restResource.deleteEnrollmentInfo("nodename", "domainname")
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'delete node domain status OK response'() {
        given:
        scenario = "OK"
        when:
        def response = restResource.deleteEnrollmentInfo("nodename", "domainname")
        then:
        response != null
        and:
        response.getStatus() == 200
    }

    def 'delete node domain status NO_CONTENT response'() {
        given:
        scenario = "NO_CONTENT"
        when:
        def response = restResource.deleteEnrollmentInfo("nodename", "domainname")
        then:
        response != null
        and:
        response.getStatus() == 204
    }

    def 'delete node domain status NOT_FOUND response'() {
        given:
        scenario = "NOT_FOUND"
        when:
        def response = restResource.deleteEnrollmentInfo("nodename", "domainname")
        then:
        response != null
        and:
        response.getStatus() == 404
    }

    def 'delete node domain status GONE response'() {
        given:
        scenario = "GONE"
        when:
        def response = restResource.deleteEnrollmentInfo("nodename", "domainname")
        then:
        response != null
        and:
        response.getStatus() == 410
    }

    def 'delete node domain status UNKNOWN response'() {
        given:
        scenario = "UNKNOWN"
        when:
        def response = restResource.deleteEnrollmentInfo("nodename", "domainname")
        then:
        response != null
        and:
        response.getStatus() == 500
    }

    def 'post node ldap'() {
        given:
        when:
        def response = restResource.createLdapConfiguration("nodename", "INET")
        then:
        response != null
        and:
        response.getStatus() == Status.OK.getStatusCode()
    }

    def 'post node ldap null response'() {
        given:
        scenario = "null-response"
        when:
        def response = restResource.createLdapConfiguration("nodename", "INET")
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'delete node ldap null response'() {
        given:
        scenario = "null-response"
        when:
        def response = restResource.deleteLdapConfiguration("nodename")
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'delete node ldap null status response'() {
        given:
        scenario = "null-status-response"
        when:
        def response = restResource.deleteLdapConfiguration("nodename")
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'delete node ldap status OK response'() {
        given:
        scenario = "OK"
        when:
        def response = restResource.deleteLdapConfiguration("nodename")
        then:
        response != null
        and:
        response.getStatus() == 200
    }

    def 'delete node ldap status NO_CONTENT response'() {
        given:
        scenario = "NO_CONTENT"
        when:
        def response = restResource.deleteLdapConfiguration("nodename")
        then:
        response != null
        and:
        response.getStatus() == 204
    }

    def 'delete node ldap status NOT_FOUND response'() {
        given:
        scenario = "NOT_FOUND"
        when:
        def response = restResource.deleteLdapConfiguration("nodename")
        then:
        response != null
        and:
        response.getStatus() == 404
    }

    def 'delete node ldap status GONE response'() {
        given:
        scenario = "GONE"
        when:
        def response = restResource.deleteLdapConfiguration("nodename")
        then:
        response != null
        and:
        response.getStatus() == 410
    }

    def 'delete node ldap status UNKNOWN response'() {
        given:
        scenario = "UNKNOWN"
        when:
        def response = restResource.deleteLdapConfiguration("nodename")
        then:
        response != null
        and:
        response.getStatus() == 500
    }
}