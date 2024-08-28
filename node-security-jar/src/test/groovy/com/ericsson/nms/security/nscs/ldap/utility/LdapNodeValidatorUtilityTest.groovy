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
package com.ericsson.nms.security.nscs.ldap.utility

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility

class LdapNodeValidatorUtilityTest extends CdiSpecification {

    @ObjectUnderTest
    private LdapNodeValidatorUtility ldapNodeValidatorUtility

    @MockedImplementation
    private NscsCMReaderService reader

    @MockedImplementation
    private NodeValidatorUtility nodeValidatorUtility;

    private String nodeFdn = "NODENAME"

    def 'object under test'() {
        expect:
        ldapNodeValidatorUtility != null
    }

    def 'valid node for ldap configuration'() {
        given:
        reader.getNormalizableNodeReference(_) >> mock(NormalizableNodeReference.class)
        and:
        nodeValidatorUtility.isNodeSynchronized(_) >> true
        and:
        nodeValidatorUtility.isCliCommandSupported(_, _) >> true
        when:
        ldapNodeValidatorUtility.validateNodeForLdapConfiguration(nodeFdn)
        then:
        noExceptionThrown()
    }

    def 'not existent node for ldap configuration'() {
        given:
        reader.getNormalizableNodeReference(_) >> null
        when:
        ldapNodeValidatorUtility.validateNodeForLdapConfiguration(nodeFdn)
        then:
        thrown(InvalidNodeNameException.class)
    }

    def 'unsynchronized node for ldap configuration'() {
        given:
        reader.getNormalizableNodeReference(_) >> mock(NormalizableNodeReference.class)
        and:
        nodeValidatorUtility.isNodeSynchronized(_) >> false
        when:
        ldapNodeValidatorUtility.validateNodeForLdapConfiguration(nodeFdn)
        then:
        thrown(NodeNotSynchronizedException.class)
    }

    def 'not supporting ldap node for ldap configuration'() {
        given:
        reader.getNormalizableNodeReference(_) >> mock(NormalizableNodeReference.class)
        and:
        nodeValidatorUtility.isNodeSynchronized(_) >> true
        and:
        nodeValidatorUtility.isCliCommandSupported(_, _) >> false
        when:
        ldapNodeValidatorUtility.validateNodeForLdapConfiguration(nodeFdn)
        then:
        thrown(UnsupportedNodeTypeException.class)
    }
}
