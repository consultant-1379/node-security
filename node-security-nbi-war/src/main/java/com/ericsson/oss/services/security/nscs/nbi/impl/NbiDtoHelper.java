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
package com.ericsson.oss.services.security.nscs.nbi.impl;

import java.net.StandardProtocolFamily;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributesBuilder;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.SnmpAuthProtocol;
import com.ericsson.nms.security.nscs.api.enums.SnmpPrivProtocol;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.CertificateRevocation;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustCategory;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificate;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.VerboseEnrollmentInfo;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.AlgorithmAndKeySizeNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CredentialAttributesNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CredentialsResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CredentialsTypeNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CrlNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.DomainRequestNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.DomainResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.EnrollmentCmpConfigNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.EnrollmentRequestNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.EnrollmentResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.LdapResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.ResourceInstanceNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SnmpAttributesNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SubjectAlternativeNameNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SubjectAlternativeNameStringValueNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SubjectAlternativeNameTypeNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.TrustCategoryNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.TrustedCertificateNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.UserCredentialsNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsLdapResponse;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourcesConstants;

/**
 * Auxiliary class to manage conversions between NBI DTO and internal NSCS format.
 */
public final class NbiDtoHelper {

    private static final String DISABLE = "disable";
    private static final String ENABLE = "enable";
    private static final String NULL_DTO = "Null DTO";
    private static final String INVALID_DTO = "Invalid DTO";
    private static final String PLEASE_PROVIDE_A_NOT_NULL_DTO = "Please provide a not null DTO";
    private static final String PLEASE_PROVIDE_A_VALID_DTO = "Please provide a valid DTO";
    private static final String NULL_MANDATORY_DTO_ATTR = "Null mandatory DTO attribute";
    private static final String PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR = "Please provide a not null mandatory DTO attribute";

    private NbiDtoHelper() {
        // intentionally left empty
    }

    /**
     * Validate and converts the credentials from DTO to NSCS format.
     *
     * @param dto
     *            the credentials DTO.
     * @return the NSCS credentials.
     */
    public static CredentialAttributes fromDto(final CredentialAttributesNbiDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
        if (dto.getEnablingPredefinedENMLDAPUser() != null) {
            validateEnableDisable(dto.getEnablingPredefinedENMLDAPUser());
        }
        final List<UserCredentialsNbiDto> credentialsList = dto.getCredentialsList();
        final CredentialAttributesBuilder credentialAttributesBuilder = new CredentialAttributesBuilder();
        if (!credentialsList.isEmpty()) {
            for (final UserCredentialsNbiDto credential : credentialsList) {
                final CredentialsTypeNbiDto credType = credential.getCredentialsType();
                if (credType == null || (credential.getCredUser() == null && credential.getCredPass() == null)) {
                    throw new NscsBadRequestException(NULL_MANDATORY_DTO_ATTR, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR);
                }
                switch (credType) {
                    case ROOT:
                        credentialAttributesBuilder.addRoot(credential.getCredUser(), credential.getCredPass());
                        break;
                    case SECURE:
                        credentialAttributesBuilder.addSecure(credential.getCredUser(), credential.getCredPass());
                        break;
                    case NORMAL:
                        credentialAttributesBuilder.addUnsecure(credential.getCredUser(), credential.getCredPass());
                        break;
                    case NWI_E_A:
                        credentialAttributesBuilder.addNwieaSecure(credential.getCredUser(), credential.getCredPass());
                        break;
                    case NWI_E_B:
                        credentialAttributesBuilder.addNwiebSecure(credential.getCredUser(), credential.getCredPass());
                        break;
                    case NODE_CLI:
                        credentialAttributesBuilder.addNodeCliUser(credential.getCredUser(), credential.getCredPass());
                        break;
                }
            }
        }

        return credentialAttributesBuilder.build();
    }


    public static SnmpV3Attributes fromDto(final SnmpAttributesNbiDto snmpAttributesDto) {
        if (snmpAttributesDto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
        final SnmpV3Attributes snmpV3Attributes = new SnmpV3Attributes();
        validateEnableDisable(snmpAttributesDto.getAuthPriv());
        if (ENABLE.equals(snmpAttributesDto.getAuthPriv())) {
            if (snmpAttributesDto.getPrivPassword() == null || snmpAttributesDto.getPrivAlgo() == null) {
                throw new NscsBadRequestException(NULL_MANDATORY_DTO_ATTR, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR);
            }
            snmpV3Attributes.setPrivKey(snmpAttributesDto.getPrivPassword());
            snmpV3Attributes.setPrivProtocol(SnmpPrivProtocol.valueOf(snmpAttributesDto.getPrivAlgo().value()));
        } else {
            if (snmpAttributesDto.getPrivPassword() != null || snmpAttributesDto.getPrivAlgo() != null) {
                throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
            }
        }
        if (snmpAttributesDto.getAuthPassword() == null || snmpAttributesDto.getAuthAlgo().value() == null) {
            throw new NscsBadRequestException(NULL_MANDATORY_DTO_ATTR, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR);
        }
        snmpV3Attributes.setAuthKey(snmpAttributesDto.getAuthPassword());
        snmpV3Attributes.setAuthProtocol(SnmpAuthProtocol.valueOf(snmpAttributesDto.getAuthAlgo().value()));

        return snmpV3Attributes;
    }

    public static SnmpSecurityLevel fromDto(final String authPriv) {
        return ENABLE.equals(authPriv) ? SnmpSecurityLevel.AUTH_PRIV : SnmpSecurityLevel.AUTH_NO_PRIV;
    }

    /**
     * Converts the LDAP configuration from NSCS to DTO format.
     *
     * @param ldapResponse
     *            the NSCS LDAP configuration.
     * @return the LDAP configuration DTO.
     * @throws UnexpectedErrorException
     *             if conversion fails
     */
    public static LdapResponseNbiDto toLdapDto(final NscsLdapResponse ldapResponse) {
        if (ldapResponse == null) {
            throw new UnexpectedErrorException(NULL_DTO);
        }
        final LdapResponseNbiDto dto = new LdapResponseNbiDto();
        dto.setBindDn(ldapResponse.getBindDn());
        dto.setBindPassword(ldapResponse.getBindPassword());
        dto.setBaseDn(ldapResponse.getBaseDn());
        dto.setLdapIpAddress(ldapResponse.getLdapIpAddress());
        dto.setFallbackLdapIpAddress(ldapResponse.getFallbackLdapIpAddress());
        dto.setTlsPort(ldapResponse.getTlsPort());
        dto.setLdapsPort(ldapResponse.getLdapsPort());
        return dto;
    }

    /**
     * Converts the resource instance from NSCS to DTO format.
     *
     * @param resourceInstance
     *            the NSCS resource instance.
     * @return the resource instance DTO.
     * @throws UnexpectedErrorException
     *             if conversion fails
     */
    public static ResourceInstanceNbiDto toDto(final NscsResourceInstance resourceInstance) {
        if (resourceInstance == null) {
            throw new UnexpectedErrorException(NULL_DTO);
        }
        final ResourceInstanceNbiDto dto = new ResourceInstanceNbiDto();
        dto.setResource(resourceInstance.getResource());
        dto.setResourceId(resourceInstance.getResourceId());
        dto.setSubResource(resourceInstance.getSubResource());
        dto.setSubResourceId(resourceInstance.getSubResourceId());
        return dto;
    }

    /**
     * Converts the NSCS resource instance to response status.
     *
     * @param resourceInstance
     *            the NSCS resource instance.
     * @return the response status.
     * @throws UnexpectedErrorException
     *             if conversion fails
     */
    public static Response.Status toResponseStatus(final NscsResourceInstance resourceInstance) {
        if (resourceInstance == null) {
            throw new UnexpectedErrorException(NULL_DTO);
        }
        Response.Status responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
        final String status = resourceInstance.getStatus();
        if (status == null) {
            throw new UnexpectedErrorException(INVALID_DTO);
        }
        switch (status) {
        case NscsResourcesConstants.STATUS_OK:
            responseStatus = Response.Status.OK;
            break;
        case NscsResourcesConstants.STATUS_NO_CONTENT:
            responseStatus = Response.Status.NO_CONTENT;
            break;
        case NscsResourcesConstants.STATUS_NOT_FOUND:
            responseStatus = Response.Status.NOT_FOUND;
            break;
        case NscsResourcesConstants.STATUS_GONE:
            responseStatus = Response.Status.GONE;
            break;
        default:
            break;
        }
        return responseStatus;
    }

    /**
     * Converts the enrollment request from DTO to NSCS format.
     * 
     * Note that domainName is not checked at this stage, since with future flexibility the check could require access to DPS to validate it. For this
     * reason the check is performed at EJB level.
     *
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param domainName
     *            the domain name.
     * @param ipFamily
     *            the IP family.
     * @param dto
     *            the enrollment request DTO.
     * @return the NSCS enrollment request.
     */
    public static NodeDetails fromEnrollmentInfoDto(final String nodeNameOrFdn, final String domainName, final String ipFamily,
                                                    final EnrollmentRequestNbiDto dto) {
        final NodeDetails nodeDetails = new NodeDetails();
        nodeDetails.setNodeFdn(nodeNameOrFdn);
        nodeDetails.setCertType(domainName);
        final StandardProtocolFamily protocolFamily = fromIpFamilyDto(ipFamily);
        nodeDetails.setIpVersion(protocolFamily);
        if (dto != null) {
            final DomainRequestNbiDto domainDto = dto.getDomain();
            fromDomainDto(domainDto, nodeDetails);
        }
        return nodeDetails;
    }

    /**
     * Converts the IP family from DTO to NSCS format.
     *
     * @param dto
     *            the IP family DTO.
     * @return the NSCS IP family.
     */
    public static StandardProtocolFamily fromIpFamilyDto(final String dto) {
        if (dto == null) {
            return null;
        }
        if ("INET".equals(dto)) {
            return StandardProtocolFamily.INET;
        } else if ("INET6".equals(dto)) {
            return StandardProtocolFamily.INET6;
        } else {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
    }

    /**
     * Converts the enrollment request from NSCS to NBI DTO format.
     *
     * @param enrollmentInfo
     *            the NSCS enrollment info.
     * @return the enrollment response DTO.
     */
    public static EnrollmentResponseNbiDto toEnrollmentInfoDto(final EnrollmentInfo enrollmentInfo) {
        if (enrollmentInfo == null) {
            throw new UnexpectedErrorException(NULL_DTO);
        }
        if (enrollmentInfo.getVerboseEnrollmentInfo() == null) {
            throw new UnexpectedErrorException(INVALID_DTO);
        }
        final EnrollmentResponseNbiDto dto = new EnrollmentResponseNbiDto();

        final DomainResponseNbiDto domainDto = new DomainResponseNbiDto();
        domainDto.setDomainName(enrollmentInfo.getVerboseEnrollmentInfo().getCertificateType());

        // enrollment CMP configuration
        final EnrollmentCmpConfigNbiDto cmpDto = toEnrollmentCmpConfigDto(enrollmentInfo);
        domainDto.setEnrollmentCmpConfig(cmpDto);

        // trusted certificates
        final List<TrustedCertificateNbiDto> trustedCertsDto = toTrustedCertificatesDto(enrollmentInfo);
        domainDto.setTrustedCertificates(trustedCertsDto);

        // trust categories
        final List<TrustCategoryNbiDto> trustCategoriesDto = toTrustCategoriesDto(enrollmentInfo);
        domainDto.setTrustCategories(trustCategoriesDto);

        dto.setDomain(domainDto);

        return dto;
    }

    /**
     * Converts the trust categories from NSCS to NBI DTO format.
     *
     * @param enrollmentInfo
     *            the NSCS enrollment info.
     * @return the trust categories DTO.
     */
    private static List<TrustCategoryNbiDto> toTrustCategoriesDto(final EnrollmentInfo enrollmentInfo) {
        final List<TrustCategoryNbiDto> trustCategoriesDto = new ArrayList<>();
        if (enrollmentInfo.getVerboseEnrollmentInfo().getTrustCategories() != null) {
            for (final TrustCategory trustCategory : enrollmentInfo.getVerboseEnrollmentInfo().getTrustCategories().getTrustCategory()) {
                final TrustCategoryNbiDto trustCategoryDto = new TrustCategoryNbiDto();
                trustCategoryDto.setId(trustCategory.getName());
                List<String> certificatesDto = new ArrayList<>();
                if (trustCategory.getCertificates() != null) {
                    certificatesDto = new ArrayList<>(trustCategory.getCertificates().getCertificate());
                }
                trustCategoryDto.setCertificates(certificatesDto);
                trustCategoriesDto.add(trustCategoryDto);
            }
        }
        return trustCategoriesDto;
    }

    /**
     * Converts the trusted certificates from NSCS to NBI DTO format.
     *
     * @param enrollmentInfo
     *            the NSCS enrollment info.
     * @return the trusted certificates DTO.
     */
    private static List<TrustedCertificateNbiDto> toTrustedCertificatesDto(final EnrollmentInfo enrollmentInfo) {
        final List<TrustedCertificateNbiDto> trustedCertsDto = new ArrayList<>();
        if (enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates() != null) {
            for (final TrustedCertificate trustedCert : enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates().getTrustedCertificate()) {
                final TrustedCertificateNbiDto trustedCertDto = new TrustedCertificateNbiDto();
                trustedCertDto.setId(trustedCert.getName());
                trustedCertDto.setCaSubjectName(trustedCert.getCaSubjectName());
                trustedCertDto.setCaFingerprint(trustedCert.getCafingerprint());
                trustedCertDto.setCaPem(trustedCert.getCaPem());
                trustedCertDto.setTdpsUri(trustedCert.getTdpsUri());
                final List<CrlNbiDto> crlsDto = toCrlsDto(trustedCert);
                trustedCertDto.setCrls(crlsDto);
                trustedCertsDto.add(trustedCertDto);
            }
        }
        return trustedCertsDto;
    }

    /**
     * Converts the CRLs for the given trusted certificate from NSCS to NBI DTO format.
     *
     * @param trustedCert
     *            the NSCS trusted certificate.
     * @return the CRLs DTO.
     */
    private static List<CrlNbiDto> toCrlsDto(final TrustedCertificate trustedCert) {
        final List<CrlNbiDto> crlsDto = new ArrayList<>();
        if (trustedCert.getCrls() != null) {
            for (final CertificateRevocation crl : trustedCert.getCrls().getCertificateRevocations()) {
                final CrlNbiDto crlDto = new CrlNbiDto();
                crlDto.setId(crl.getCrlName());
                crlDto.setCdpsUri(crl.getCrlUri());
                crlsDto.add(crlDto);
            }
        }
        return crlsDto;
    }

    /**
     * Converts the enrollment CMP configuration from NSCS to NBI DTO format.
     *
     * @param enrollmentInfo
     *            the NSCS enrollment info.
     * @return the enrollment response DTO.
     */
    private static EnrollmentCmpConfigNbiDto toEnrollmentCmpConfigDto(final EnrollmentInfo enrollmentInfo) {
        final EnrollmentCmpConfigNbiDto cmpDto = new EnrollmentCmpConfigNbiDto();
        final VerboseEnrollmentInfo verboseEnrollmentInfo = enrollmentInfo.getVerboseEnrollmentInfo();
        cmpDto.setEnrollmentAuthorityName(enrollmentInfo.getIssuerCA());
        cmpDto.setUrl(enrollmentInfo.getUrl());
        cmpDto.setAlgorithm(enrollmentInfo.getKeyInfo());
        cmpDto.setSubjectName(enrollmentInfo.getSubjectName());
        cmpDto.setChallengePassword(enrollmentInfo.getChallengePassword());
        if (verboseEnrollmentInfo.getEnrollmentCmpConfig() != null) {
            cmpDto.setEnrollmentAuthorityId(verboseEnrollmentInfo.getEnrollmentCmpConfig().getEnrollmentAuthority());
            cmpDto.setEnrollmentAuthorityType(verboseEnrollmentInfo.getEnrollmentCmpConfig().getAuthorityType());
            cmpDto.setEnrollmentServerGroupId(verboseEnrollmentInfo.getEnrollmentCmpConfig().getEnrollmentServerGroupId());
            cmpDto.setEnrollmentServerId(verboseEnrollmentInfo.getEnrollmentCmpConfig().getEnrollmentServerId());
            cmpDto.setCmpTrustCategoryId(verboseEnrollmentInfo.getEnrollmentCmpConfig().getCacerts());
            cmpDto.setCertificateId(verboseEnrollmentInfo.getEnrollmentCmpConfig().getNodeCredentialId());
            cmpDto.setTrustCategoryId(verboseEnrollmentInfo.getEnrollmentCmpConfig().getTrustedCerts());
        }
        return cmpDto;
    }

    /**
     * Converts the CredentialAttributeNbiDto to CredentialAttributeResponseDto.
     *
     * @param credentials
     *            the NSCS credentail attribute.
     * @return the credential attribute DTO
     * @throws UnexpectedErrorException
     *             if conversion fails
     */
    public static CredentialsResponseNbiDto toResponseDto(final CredentialAttributesNbiDto credentialAttributesNbiDto) {
        final CredentialsResponseNbiDto response = new CredentialsResponseNbiDto();
        final List<UserCredentialsNbiDto> credentials = credentialAttributesNbiDto.getCredentialsList();
        final List<CredentialsTypeNbiDto> credTypes = new ArrayList<>();
        for (final UserCredentialsNbiDto credential : credentials) {
            final CredentialsTypeNbiDto credType = credential.getCredentialsType();
            credTypes.add(credType);
        }
        response.setCredentials(credTypes);
        return response;
    }

    private static void validateEnableDisable(final String toBeChecked) {
        if (!(ENABLE.equalsIgnoreCase(toBeChecked) || DISABLE.equalsIgnoreCase(toBeChecked))) {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
    }

    /**
     * Converts the domain from DTO to NSCS format.
     * 
     * @param domainDto
     *            the domain DTO.
     * @param nodeDetails
     *            the NSCS node details.
     */
    private static void fromDomainDto(final DomainRequestNbiDto domainDto, final NodeDetails nodeDetails) {
        if (domainDto != null) {
            nodeDetails.setEntityProfileName(domainDto.getEntityProfileName());
            final AlgorithmAndKeySizeNbiDto algoDto = domainDto.getAlgorithmAndKeySize();
            nodeDetails.setKeySize(algoDto != null ? algoDto.name() : null);
            nodeDetails.setCommonName(domainDto.getCommonName());
            final List<SubjectAlternativeNameNbiDto> sansDto = domainDto.getSubjectAlternativeNames();
            fromSubjectAlternativeNamesDto(sansDto, nodeDetails);
            nodeDetails.setOtpCount(domainDto.getOtpCount());
            nodeDetails.setOtpValidityPeriodInMinutes(domainDto.getOtpValidityPeriodInMinutes());
        }
    }

    /**
     * Converts the subject alternatives names from DTO to NSCS format.
     * 
     * @param sansDto
     *            the subject alternatives names DTO.
     * @param nodeDetails
     *            the NSCS node details.
     */
    private static void fromSubjectAlternativeNamesDto(final List<SubjectAlternativeNameNbiDto> sansDto, final NodeDetails nodeDetails) {
        if (sansDto != null && !sansDto.isEmpty()) {
            if (sansDto.size() == 1) {
                final SubjectAlternativeNameNbiDto sanDto = sansDto.get(0);
                final SubjectAlternativeNameTypeNbiDto sanTypeDto = sanDto.getSubjectAlternativeNameType();
                nodeDetails.setSubjectAltNameType(sanTypeDto != null ? sanTypeDto.name() : null);
                final SubjectAlternativeNameStringValueNbiDto sanValueDto = sanDto.getSubjectAlternativeNameValue();
                nodeDetails.setSubjectAltName(sanValueDto != null ? sanValueDto.getValue() : null);
            } else {
                throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
            }
        }
    }

}
