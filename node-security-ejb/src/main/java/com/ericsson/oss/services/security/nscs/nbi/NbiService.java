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

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsLdapResponse;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance;

@Local
public interface NbiService {

    public void createOrUpdateNodeCredentials(String nodeName, CredentialAttributes credentialAttributes, String enablingPredefinedENMLDAPUser);

    public void createOrUpdateNodeSnmp(String nodeName, SnmpV3Attributes snmpV3Attributes, SnmpSecurityLevel snmpSecurityLevel);

    public EnrollmentInfo generateEnrollmentInfo(String nodeNameOrFdn, String domainName, String ipFamily, NodeDetails nodeDetails);

    public NscsResourceInstance deleteEnrollmentInfo(String nodeNameOrFdn, String domainName);

    public NscsLdapResponse createLdapConfiguration(String nodeNameOrFdn, String ipFamily);

    public NscsResourceInstance deleteLdapConfiguration(String nodeNameOrFdn);

}
