/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.credentials;

import java.util.List;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.CredentialService;
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.nscs.api.credentials.dto.NodeCredentialsDto;
import com.ericsson.oss.services.security.nscs.credentials.dto.CredentialDtoHelper;
import com.ericsson.oss.services.security.nscs.interceptor.NscsRecordedCommand;
import com.ericsson.oss.services.security.nscs.interceptor.NscsSecurityViolationHandled;
import com.ericsson.oss.services.security.nscs.util.CommonDtoHelper;

public class CredentialManagerBean implements CredentialManager {

    private static final String RESOURCE = "nodesec_cred";
    private static final String READ = "read";
    private static final String CREATE = "create";
    private static final String UPDATE = "update";

    @EServiceRef
    private CredentialService credentialService;

    @Override
    @Authorize(resource = RESOURCE, action = CREATE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public String createOrUpdateNodeCredentials(final NodeCredentialsDto dto) {
        CredentialDtoHelper.validate(dto);
        final CredentialAttributes credentialAttributes = CredentialDtoHelper.fromDto(dto.getNodeCredentials());
        final String nodeNameOrFdn = dto.getNodeNameOrFdn();
        credentialService.createNodeCredentials(credentialAttributes, nodeNameOrFdn);
        return String.format("Successfully created/updated node credentials for %s", nodeNameOrFdn);
    }

    @Override
    @Authorize(resource = RESOURCE, action = UPDATE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public String configureEnrollmentMode(final EnrollmentMode enrollmentMode, final String nodeNameOrFdn) {
        credentialService.configureEnrollmentMode(enrollmentMode, nodeNameOrFdn);
        return String.format("Successfully configured enrollment mode %s for %s", enrollmentMode, nodeNameOrFdn);
    }

    @Override
    @Authorize(resource = RESOURCE, action = UPDATE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public String configureSnmpV3(final SnmpSecurityLevel snmpSecurityLevel, final SnmpV3Attributes snmpV3Attributes, final String nodeList) {
        final List<String> nodeNameOrFdnList = CommonDtoHelper.fromNodeListDto(nodeList);
        credentialService.configureSnmpV3(snmpSecurityLevel, snmpV3Attributes, nodeNameOrFdnList);
        return String.format("Successfully configured SNMPv3: %s for %s", snmpSecurityLevel, nodeNameOrFdnList);
    }

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public Map<String, SnmpV3Attributes> getSnmpV3Configuration(final String nodeList, final boolean isPlainText) {
        final List<String> nodeNameOrFdnList = CommonDtoHelper.fromNodeListDto(nodeList);
        return credentialService.getSnmpV3Configuration(isPlainText, nodeNameOrFdnList);
    }
}
