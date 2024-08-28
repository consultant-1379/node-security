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
package com.ericsson.oss.services.security.nscs.ldap.service.impl;

import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask;
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapService;
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapServiceType;

@MOLdapServiceType(moLdapServiceType = "CPP")
public class CppMOLdapServiceImpl implements MOLdapService {

    private static final String LDAP_UNSUPPORTED_FOR_CPP_NODE_PLATFORM = "LDAP unsupported for CPP node platform";

    @Override
    public void validateLdapConfiguration(CommonLdapConfigurationTask task, NormalizableNodeReference normalizable) {
        throw new UnsupportedNodeTypeException(LDAP_UNSUPPORTED_FOR_CPP_NODE_PLATFORM);
    }

    @Override
    public void ldapConfigure(CommonLdapConfigurationTask task, NormalizableNodeReference normalizable) {
        throw new UnsupportedNodeTypeException(LDAP_UNSUPPORTED_FOR_CPP_NODE_PLATFORM);
    }

}
