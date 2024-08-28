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
package com.ericsson.oss.services.security.nscs.credentials

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.CredentialService
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.oss.services.nscs.api.credentials.dto.CredentialAttributesDto
import com.ericsson.oss.services.nscs.api.credentials.dto.NodeCredentialsDto

import spock.lang.Unroll

class CredentialManagerBeanTest extends CdiSpecification {

    @ObjectUnderTest
    CredentialManagerBean credentialManager

    @MockedImplementation
    CredentialService credentialService

    def "object under test should not be null" () {
        expect:
        credentialManager != null
    }

    def "create credentials for valid dto" () {
        given:
        def credAttrsDto = mock(CredentialAttributesDto)
        credAttrsDto.getCredentialsList() >> []
        def dto = mock(NodeCredentialsDto)
        dto.getNodeNameOrFdn() >> "this is the node name or fdn"
        dto.getNodeCredentials() >> credAttrsDto
        when:
        credentialManager.createOrUpdateNodeCredentials(dto)
        then:
        1 * credentialService.createNodeCredentials(_ as CredentialAttributes, _ as String)
        and:
        notThrown(Exception)
    }

    def "create credentials for null dto" () {
        given:
        when:
        credentialManager.createOrUpdateNodeCredentials(null)
        then:
        thrown(NscsBadRequestException)
    }

    def "create credentials for invalid dto" () {
        given:
        def credAttrsDto = mock(CredentialAttributesDto)
        def dto = mock(NodeCredentialsDto)
        dto.getNodeNameOrFdn() >> "this is the node name or fdn"
        when:
        credentialManager.createOrUpdateNodeCredentials(dto)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "configure enrollment mode" () {
        given:
        def nodeNameOrFdn = "this is the node name or fdn"
        when:
        credentialManager.configureEnrollmentMode(enrollmentMode, nodeNameOrFdn)
        then:
        1 * credentialService.configureEnrollmentMode(enrollmentMode, nodeNameOrFdn)
        and:
        notThrown(Exception)
        where:
        enrollmentMode << EnrollmentMode.values()
    }

    @Unroll
    def "configure SNMPv3 with security level #snmpSecurityLevel"() {
        given:
        def nodeList = "node1&node2"
        def snmpV3Attributes = new SnmpV3Attributes()
        when:
        credentialManager.configureSnmpV3(snmpSecurityLevel, snmpV3Attributes, nodeList)
        then:
        1 * credentialService.configureSnmpV3(snmpSecurityLevel, snmpV3Attributes, _ as List)
        and:
        notThrown(Exception)
        where:
        snmpSecurityLevel << SnmpSecurityLevel.values()
    }

    @Unroll
    def "get SNMPv3 configuration with plain text #isPlainText" () {
        given:
        def nodeList = "node1&node2"
        when:
        credentialManager.getSnmpV3Configuration(nodeList, isPlainText)
        then:
        1 * credentialService.getSnmpV3Configuration(isPlainText, _ as List)
        and:
        notThrown(Exception)
        where:
        isPlainText << [true, false]
    }
}