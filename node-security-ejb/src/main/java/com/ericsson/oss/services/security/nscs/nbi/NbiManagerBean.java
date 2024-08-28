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
package com.ericsson.oss.services.security.nscs.nbi;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.security.nscs.nbi.interceptor.NbiManagerExceptionInterceptor;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsLdapResponse;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance;

@Stateless
@Interceptors({ NbiManagerExceptionInterceptor.class })
public class NbiManagerBean implements NbiManager {

    private static final String NODESEC_LDAP_NBI = "nodesec_ldap_nbi";
    private static final String NODESEC_DOMAINS_NBI = "nodesec_domains_nbi";
    private static final String NODESEC_CREDENTIALS_NBI = "nodesec_credentials_nbi";
    private static final String NODESEC_SNMPV3_NBI = "nodesec_snmpv3_nbi";
    private static final String CREATE = "create";
    private static final String DELETE = "delete";
    private static final String UPDATE = "update";

    @Inject
    private NbiService nbiService;

    @Override
    @Authorize(resource = NODESEC_CREDENTIALS_NBI, action = UPDATE)
    public void createOrUpdateNodeCredentials(final String nodeName, final CredentialAttributes credentialAttributes,
                                              final String enablingPredefinedENMLDAPUser) {
        nbiService.createOrUpdateNodeCredentials(nodeName, credentialAttributes, enablingPredefinedENMLDAPUser);
    }

    @Override
    @Authorize(resource = NODESEC_SNMPV3_NBI, action = UPDATE)
    public void createOrUpdateNodeSnmp(final String nodeName, SnmpV3Attributes snmpV3Attributes, SnmpSecurityLevel snmpSecurityLevel) {
        nbiService.createOrUpdateNodeSnmp(nodeName, snmpV3Attributes, snmpSecurityLevel);
    }

    @Override
    @Authorize(resource = NODESEC_DOMAINS_NBI, action = CREATE)
    public EnrollmentInfo generateEnrollmentInfo(final String nodeNameOrFdn, final String domainName, final String ipFamily,
            final NodeDetails nodeDetails) {
        return nbiService.generateEnrollmentInfo(nodeNameOrFdn, domainName, ipFamily, nodeDetails);
    }

    @Override
    @Authorize(resource = NODESEC_DOMAINS_NBI, action = DELETE)
    public NscsResourceInstance deleteEnrollmentInfo(final String nodeNameOrFdn, final String domainName) {
        return nbiService.deleteEnrollmentInfo(nodeNameOrFdn, domainName);
    }

    @Override
    @Authorize(resource = NODESEC_LDAP_NBI, action = CREATE)
    public NscsLdapResponse createLdapConfiguration(final String nodeNameOrFdn, final String ipFamily) {
        return nbiService.createLdapConfiguration(nodeNameOrFdn, ipFamily);
    }

    @Override
    @Authorize(resource = NODESEC_LDAP_NBI, action = DELETE)
    public NscsResourceInstance deleteLdapConfiguration(final String nodeNameOrFdn) {
        return nbiService.deleteLdapConfiguration(nodeNameOrFdn);
    }

}
