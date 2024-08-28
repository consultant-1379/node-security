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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.oss.services.security.nscs.nbi.NbiManager;
import com.ericsson.oss.services.security.nscs.nbi.api.V1Api;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CredentialAttributesNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.CredentialsResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.EnrollmentRequestNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.EnrollmentResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.LdapResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.ResourceInstanceNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SnmpAttributesNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.api.dto.SnmpResponseNbiDto;
import com.ericsson.oss.services.security.nscs.nbi.interceptor.NbiLoggerInterceptor;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsLdapResponse;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance;

/**
 * Implementation of NBI nodes REST resource (version v1)
 */
@RequestScoped
@Interceptors({ NbiLoggerInterceptor.class })
public class NbiV1NodesRestResource implements V1Api {

    private static Logger logger = LoggerFactory.getLogger(NbiV1NodesRestResource.class);

    @Inject
    private NbiManager nbiManager;

    @Override
    public Response createOrUpdateNodeCredentials(final String nodeNameOrFdn, final CredentialAttributesNbiDto credentialAttributesDto) {

        logger.info("createOrUpdateNodeCredentials: start for [{}]", nodeNameOrFdn);

        final CredentialAttributes credentialAttributes = NbiDtoHelper.fromDto(credentialAttributesDto);
        nbiManager.createOrUpdateNodeCredentials(nodeNameOrFdn, credentialAttributes, credentialAttributesDto.getEnablingPredefinedENMLDAPUser());
        final CredentialsResponseNbiDto response = NbiDtoHelper.toResponseDto(credentialAttributesDto);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Override
    public Response createOrUpdateNodeSnmp(final String nodeNameOrFdn, final SnmpAttributesNbiDto snmpAttributesDto) {

        logger.info("createOrUpdateNodeSnmp: start for [{}]", nodeNameOrFdn);

        final SnmpV3Attributes snmpV3Attributes = NbiDtoHelper.fromDto(snmpAttributesDto);
        final SnmpSecurityLevel snmpSecurityLevel = NbiDtoHelper.fromDto(snmpAttributesDto.getAuthPriv());

        nbiManager.createOrUpdateNodeSnmp(nodeNameOrFdn, snmpV3Attributes, snmpSecurityLevel);
        final SnmpResponseNbiDto response = new SnmpResponseNbiDto();
        response.setAuthPriv(snmpAttributesDto.getAuthPriv());
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Override
    public Response generateEnrollmentInfo(final String nodeNameOrFdn, final String domainName, final String ipFamily,
            final EnrollmentRequestNbiDto enrollmentRequestNbiDto) {

        logger.info("generateEnrollmentInfo: start for [{}] and [{}] and [{}] and [{}]", nodeNameOrFdn, domainName, ipFamily,
                enrollmentRequestNbiDto);

        final NodeDetails nodeDetails = NbiDtoHelper.fromEnrollmentInfoDto(nodeNameOrFdn, domainName, ipFamily, enrollmentRequestNbiDto);
        final EnrollmentInfo enrollmentInfo = nbiManager.generateEnrollmentInfo(nodeNameOrFdn, domainName, ipFamily, nodeDetails);
        final EnrollmentResponseNbiDto response = NbiDtoHelper.toEnrollmentInfoDto(enrollmentInfo);

        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Override
    public Response deleteEnrollmentInfo(final String nodeNameOrFdn, final String domainName) {

        logger.info("deleteEnrollmentInfo: start for [{}] and [{}]", nodeNameOrFdn, domainName);

        final NscsResourceInstance resourceInstance = nbiManager.deleteEnrollmentInfo(nodeNameOrFdn, domainName);
        final ResourceInstanceNbiDto response = NbiDtoHelper.toDto(resourceInstance);
        final Response.Status status = NbiDtoHelper.toResponseStatus(resourceInstance);

        return Response.status(status).entity(response).build();
    }

    @Override
    public Response createLdapConfiguration(final String nodeNameOrFdn, final String ipFamily) {

        logger.info("createLdapConfiguration: start for [{}] and [{}]", nodeNameOrFdn, ipFamily);

        final NscsLdapResponse ldapResponse = nbiManager.createLdapConfiguration(nodeNameOrFdn, ipFamily);
        final LdapResponseNbiDto response = NbiDtoHelper.toLdapDto(ldapResponse);

        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Override
    public Response deleteLdapConfiguration(final String nodeNameOrFdn) {

        logger.info("deleteLdapConfiguration: start for [{}]", nodeNameOrFdn);

        final NscsResourceInstance resourceInstance = nbiManager.deleteLdapConfiguration(nodeNameOrFdn);
        final Response.Status status = NbiDtoHelper.toResponseStatus(resourceInstance);
        if (Response.Status.NO_CONTENT.equals(status)) {
            return Response.status(status).build();
        }

        final ResourceInstanceNbiDto response = NbiDtoHelper.toDto(resourceInstance);
        return Response.status(status).entity(response).build();
    }

}
