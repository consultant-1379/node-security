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
package com.ericsson.oss.services.security.nscs.iscf

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.IscfService
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfSecDataDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlComboDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlIpsecDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlIpsecParamsDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlOamDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlOamParamsDto
import com.ericsson.oss.services.nscs.api.iscf.dto.NodeIdentifierDto
import com.ericsson.oss.services.nscs.api.iscf.dto.NodeModelInformationDto
import com.ericsson.oss.services.nscs.api.iscf.dto.SubjectAltNameParamDto

import spock.lang.Shared
import spock.lang.Unroll

class IscfManagerBeanTest extends CdiSpecification {

    @ObjectUnderTest
    IscfManagerBean iscfManager

    @MockedImplementation
    IscfService iscfService

    private IscfXmlOamParamsDto oamParams = mock(IscfXmlOamParamsDto)
    private IscfXmlIpsecParamsDto ipsecParams = mock(IscfXmlIpsecParamsDto)
    private NodeModelInformationDto nodeModelInfo = new NodeModelInformationDto("this is the target category", "this is the target type", ModelIdentifierType.OSS_IDENTIFIER, "this is the model identifier")
    private SubjectAltNameParamDto subjectAltNameParam = new SubjectAltNameParamDto(SubjectAltNameFormat.IPV4, "11.22.33.44")
    private NodeIdentifierDto nodeIdentifier = new NodeIdentifierDto("this is the fdn", "this is the serial number")

    @Shared
    SubjectAltNameParamDto sanString = new SubjectAltNameParamDto(SubjectAltNameFormat.IPV4, "11.22.33.44")

    @Shared
    SubjectAltNameParamDto sanEdiParty = new SubjectAltNameParamDto("this is the name assigner", "this is the party name")

    def setup() {
        oamParams.getMinimumSecurityLevel() >> SecurityLevel.LEVEL_2
        oamParams.getWantedSecurityLevel() >> SecurityLevel.LEVEL_2
        ipsecParams.getUserLabel() >> "this is the user label"
        ipsecParams.getSubjectAltNameParam() >> subjectAltNameParam
        ipsecParams.getIpsecAreas() >> [
            IpsecArea.OM,
            IpsecArea.TRANSPORT
        ]
    }

    def "object under test should not be null" () {
        expect:
        iscfManager != null
    }

    def "get iscf xml oam for valid dto" () {
        given:
        def dto = mock(IscfXmlOamDto)
        dto.getLogicalName() >> "this is the logical name"
        dto.getNodeFdn() >> "this is the node fdn"
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getParams() >> oamParams
        dto.getNodeModelInfo() >> nodeModelInfo
        when:
        iscfManager.generateXmlOam(dto)
        then:
        1 * iscfService.generate(_ as String, _ as String, _ as SecurityLevel,
                _ as SecurityLevel, _ as EnrollmentMode, _ as NodeModelInformation)
        and:
        notThrown(Exception)
    }

    def "get iscf xml oam for null dto" () {
        given:
        when:
        iscfManager.generateXmlOam(null)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf xml oam for invalid dto" () {
        given:
        def dto = mock(IscfXmlOamDto)
        dto.getLogicalName() >> "this is the logical name"
        dto.getNodeFdn() >> "this is the node fdn"
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getParams() >> null
        dto.getNodeModelInfo() >> nodeModelInfo
        when:
        iscfManager.generateXmlOam(dto)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf xml ipsec for valid dto" () {
        given:
        def dto = mock(IscfXmlIpsecDto)
        dto.getLogicalName() >> "this is the logical name"
        dto.getNodeFdn() >> "this is the node fdn"
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getParams() >> ipsecParams
        dto.getNodeModelInfo() >> nodeModelInfo
        when:
        iscfManager.generateXmlIpsec(dto)
        then:
        1 * iscfService.generate(_ as String, _ as String, _ as String,
                _ as SubjectAltNameParam, _ as Set, _ as EnrollmentMode, _ as NodeModelInformation)
        and:
        notThrown(Exception)
    }

    def "get iscf xml ipsec for null dto" () {
        given:
        when:
        iscfManager.generateXmlIpsec(null)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf xml ipsec for invalid dto" () {
        given:
        def dto = mock(IscfXmlIpsecDto)
        dto.getLogicalName() >> "this is the logical name"
        dto.getNodeFdn() >> "this is the node fdn"
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getParams() >> null
        dto.getNodeModelInfo() >> nodeModelInfo
        when:
        iscfManager.generateXmlIpsec(dto)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf xml combo for valid dto" () {
        given:
        def dto = mock(IscfXmlComboDto)
        dto.getLogicalName() >> "this is the logical name"
        dto.getNodeFdn() >> "this is the node fdn"
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getOamParams() >> oamParams
        dto.getIpsecParams() >> ipsecParams
        dto.getNodeModelInfo() >> nodeModelInfo
        when:
        iscfManager.generateXmlCombo(dto)
        then:
        1 * iscfService.generate(_ as String, _ as String, _ as SecurityLevel,
                _ as SecurityLevel, _ as String, _ as SubjectAltNameParam, _ as Set,
                _ as EnrollmentMode, _ as NodeModelInformation)
        and:
        notThrown(Exception)
    }

    def "get iscf xml combo for null dto" () {
        given:
        when:
        iscfManager.generateXmlCombo(null)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf xml combo for invalid dto oam" () {
        given:
        def dto = mock(IscfXmlComboDto)
        dto.getLogicalName() >> "this is the logical name"
        dto.getNodeFdn() >> "this is the node fdn"
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getOamParams() >> null
        dto.getIpsecParams() >> ipsecParams
        dto.getNodeModelInfo() >> nodeModelInfo
        when:
        iscfManager.generateXmlCombo(dto)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf xml combo for invalid dto ipsec" () {
        given:
        def dto = mock(IscfXmlComboDto)
        dto.getLogicalName() >> "this is the logical name"
        dto.getNodeFdn() >> "this is the node fdn"
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getOamParams() >> oamParams
        dto.getIpsecParams() >> null
        dto.getNodeModelInfo() >> nodeModelInfo
        when:
        iscfManager.generateXmlCombo(dto)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "delete iscf PKI EE for node #node" () {
        given:
        when:
        iscfManager.cancel(node)
        then:
        1 * iscfService.cancel(node)
        and:
        notThrown(Exception)
        where:
        node << [null, "", "this is the node"]
    }

    @Unroll
    def "get iscf security data oam for valid dto #san and #ipver" () {
        given:
        def dto = mock(IscfSecDataDto)
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getNodeModelInfo() >> nodeModelInfo
        dto.getSubjectAltNameParam() >> san
        dto.getNodeId() >> nodeIdentifier
        dto.getIpVersion() >> ipver
        when:
        iscfManager.generateSecurityDataOam(dto)
        then:
        expectedsanandip * iscfService.generateSecurityDataOam(_ as NodeIdentifier, _ as SubjectAltNameParam, _ as EnrollmentMode,
                _ as NodeModelInformation, _ as StandardProtocolFamily)
        and:
        expectedsanonly * iscfService.generateSecurityDataOam(_ as NodeIdentifier, _ as SubjectAltNameParam, _ as EnrollmentMode,
                _ as NodeModelInformation)
        and:
        expectediponly * iscfService.generateSecurityDataOam(_ as NodeIdentifier, _ as EnrollmentMode,
                _ as NodeModelInformation, _ as StandardProtocolFamily)
        and:
        expectednone * iscfService.generateSecurityDataOam(_ as NodeIdentifier, _ as EnrollmentMode,
                _ as NodeModelInformation)
        and:
        notThrown(Exception)
        where:
        san << [
            sanString,
            sanString,
            null,
            null,
            sanEdiParty,
            sanEdiParty,
            null,
            null
        ]
        ipver << [
            StandardProtocolFamily.INET,
            null,
            StandardProtocolFamily.INET,
            null,
            StandardProtocolFamily.INET,
            null,
            StandardProtocolFamily.INET,
            null
        ]
        expectedsanandip << [1, 0, 0, 0, 1, 0, 0, 0]
        expectedsanonly << [0, 1, 0, 0, 0, 1, 0, 0]
        expectediponly << [0, 0, 1, 0, 0, 0, 1, 0]
        expectednone << [0, 0, 0, 1, 0, 0, 0, 1]
    }

    def "get iscf security data oam for null dto" () {
        given:
        when:
        iscfManager.generateSecurityDataOam(null)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "get iscf security data ipsec for valid dto #ipver" () {
        given:
        def dto = mock(IscfSecDataDto)
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getNodeModelInfo() >> nodeModelInfo
        dto.getSubjectAltNameParam() >> sanString
        dto.getNodeId() >> nodeIdentifier
        dto.getIpVersion() >> ipver
        when:
        iscfManager.generateSecurityDataIpsec(dto)
        then:
        expectedsanandip * iscfService.generateSecurityDataIpsec(_ as NodeIdentifier, _ as SubjectAltNameParam, _ as EnrollmentMode,
                _ as NodeModelInformation, _ as StandardProtocolFamily)
        and:
        expectedsanonly * iscfService.generateSecurityDataIpsec(_ as NodeIdentifier, _ as SubjectAltNameParam, _ as EnrollmentMode,
                _ as NodeModelInformation)
        and:
        notThrown(Exception)
        where:
        ipver << [
            StandardProtocolFamily.INET,
            null
        ]
        expectedsanandip << [1, 0]
        expectedsanonly << [0, 1]
    }

    def "get iscf security data ipsec for null dto" () {
        given:
        when:
        iscfManager.generateSecurityDataIpsec(null)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf security data ipsec for invalid dto null san" () {
        given:
        def dto = mock(IscfSecDataDto)
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getNodeModelInfo() >> nodeModelInfo
        dto.getSubjectAltNameParam() >> null
        dto.getNodeId() >> nodeIdentifier
        dto.getIpVersion() >> StandardProtocolFamily.INET
        when:
        iscfManager.generateSecurityDataIpsec(dto)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "get iscf security data combo for valid dto #ipver" () {
        given:
        def dto = mock(IscfSecDataDto)
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getNodeModelInfo() >> nodeModelInfo
        dto.getSubjectAltNameParam() >> sanString
        dto.getNodeId() >> nodeIdentifier
        dto.getIpVersion() >> ipver
        when:
        iscfManager.generateSecurityDataCombo(dto)
        then:
        expectedsanandip * iscfService.generateSecurityDataCombo(_ as NodeIdentifier, _ as SubjectAltNameParam, _ as EnrollmentMode,
                _ as NodeModelInformation, _ as StandardProtocolFamily)
        and:
        expectedsanonly * iscfService.generateSecurityDataCombo(_ as NodeIdentifier, _ as SubjectAltNameParam, _ as EnrollmentMode,
                _ as NodeModelInformation)
        and:
        notThrown(Exception)
        where:
        ipver << [
            StandardProtocolFamily.INET,
            null
        ]
        expectedsanandip << [1, 0]
        expectedsanonly << [0, 1]
    }

    def "get iscf security data combo for null dto" () {
        given:
        when:
        iscfManager.generateSecurityDataCombo(null)
        then:
        thrown(NscsBadRequestException)
    }

    def "get iscf security data combo for invalid dto null san" () {
        given:
        def dto = mock(IscfSecDataDto)
        dto.getEnrollmentMode() >> EnrollmentMode.CMPv2_INITIAL
        dto.getNodeModelInfo() >> nodeModelInfo
        dto.getSubjectAltNameParam() >> null
        dto.getNodeId() >> nodeIdentifier
        dto.getIpVersion() >> StandardProtocolFamily.INET
        when:
        iscfManager.generateSecurityDataCombo(dto)
        then:
        thrown(NscsBadRequestException)
    }
}
