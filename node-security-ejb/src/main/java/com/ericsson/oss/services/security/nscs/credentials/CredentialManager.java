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

import java.util.Map;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.oss.services.nscs.api.credentials.dto.NodeCredentialsDto;

@Local
public interface CredentialManager {

    String createOrUpdateNodeCredentials(NodeCredentialsDto dto);

    String configureEnrollmentMode(EnrollmentMode enrollmentMode, String nodeNameOrFdn);

    String configureSnmpV3(SnmpSecurityLevel snmpSecurityLevel, SnmpV3Attributes snmpV3Attributes, String nodeList);

    Map<String, SnmpV3Attributes> getSnmpV3Configuration(String nodeList, boolean isPlainText);
}
