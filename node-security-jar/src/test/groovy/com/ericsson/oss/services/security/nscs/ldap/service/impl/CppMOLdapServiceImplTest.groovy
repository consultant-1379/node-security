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
package com.ericsson.oss.services.security.nscs.ldap.service.impl

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask

class CppMOLdapServiceImplTest extends CdiSpecification {

    @ObjectUnderTest
    private CppMOLdapServiceImpl cppMOLdapServiceImpl

    private CommonLdapConfigurationTask task = new CommonLdapConfigurationTask()
    private NormalizableNodeReference normalizable = mock(NormalizableNodeReference)

    def 'object under test injection'() {
        expect:
        cppMOLdapServiceImpl != null
    }

    def 'validate ldap configuration'() {
        given:
        when:
        cppMOLdapServiceImpl.validateLdapConfiguration(task, normalizable)
        then:
        thrown(UnsupportedNodeTypeException.class)
    }

    def 'ldap configuration'() {
        given:
        when:
        cppMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then:
        thrown(UnsupportedNodeTypeException.class)
    }
}