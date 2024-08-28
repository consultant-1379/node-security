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
package com.ericsson.nms.security.nscs

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel
import com.ericsson.oss.services.nscs.api.credentials.dto.NodeCredentialsDto
import com.ericsson.oss.services.security.nscs.credentials.CredentialManager

import spock.lang.Shared
import spock.lang.Unroll

class CredentialServiceRestResourceTest extends CdiSpecification {

    @ObjectUnderTest
    CredentialServiceRestResource credentialServiceRestResource

    @MockedImplementation
    CredentialManager credentialManager

    @Shared
    enrollmentModes = EnrollmentMode.values()

    @Shared
    snmpSecurityLevels = SnmpSecurityLevel.values()

    def "create credentials"() {
        given:
        def dto = mock(NodeCredentialsDto)
        when:
        credentialServiceRestResource.credentialsCreateOrUpdate(dto)
        then:
        1 * credentialManager.createOrUpdateNodeCredentials(dto)
    }

    @Unroll
    def "configure enrollment mode #enrollmentMode"() {
        given:
        def node = "this is the node"
        when:
        credentialServiceRestResource.configEnrollmentMode(node, enrollmentMode)
        then:
        1 * credentialManager.configureEnrollmentMode(enrollmentMode, node)
        where:
        enrollmentMode << enrollmentModes
    }

    @Unroll
    def "configure SNMPv3 #snmpSecurityLevel"() {
        given:
        def nodeList = "node1&node2"
        and:
        def snmpV3Attributes = mock(SnmpV3Attributes)
        when:
        credentialServiceRestResource.configureSnmpV3(nodeList, snmpSecurityLevel, snmpV3Attributes)
        then:
        1 * credentialManager.configureSnmpV3(snmpSecurityLevel, snmpV3Attributes, nodeList)
        where:
        snmpSecurityLevel << snmpSecurityLevels
    }

    @Unroll
    def "get SNMPv3 #snmpSecurityLevel"() {
        given:
        def nodeList = "node1&node2"
        and:
        def snmpV3Attributes = mock(SnmpV3Attributes)
        when:
        credentialServiceRestResource.getSnmpV3Configuration(nodeList, plainText)
        then:
        1 * credentialManager.getSnmpV3Configuration(nodeList, isPlainText)
        where:
        plainText << [null, "plaintext"]
        isPlainText << [false, true]
    }
}
