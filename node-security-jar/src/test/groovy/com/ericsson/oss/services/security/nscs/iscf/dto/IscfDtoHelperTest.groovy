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
package com.ericsson.oss.services.security.nscs.iscf.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameEdiPartyType
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
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

class IscfDtoHelperTest extends CdiSpecification {

    private NodeModelInformationDto nodeModelInfo = new NodeModelInformationDto("this is the target category", "this is the target type", ModelIdentifierType.OSS_IDENTIFIER, "this is the model identifier")
    private NodeIdentifierDto nodeIdentifierDto = new NodeIdentifierDto("this is the fdn", "this is the serial number")

    @Shared
    SubjectAltNameParamDto subjectAltNameStringTypeDto = new SubjectAltNameParamDto(SubjectAltNameFormat.IPV4, "11.22.33.44")

    @Shared
    SubjectAltNameParamDto subjectAltNameEdiPartyDto = new SubjectAltNameParamDto("this is the name assigner", "this is the party name")

    def "validate valid ISCF XML OAM dto"() {
        given:
        def params = new IscfXmlOamParamsDto()
        params.setMinimumSecurityLevel(SecurityLevel.LEVEL_2)
        params.setWantedSecurityLevel(SecurityLevel.LEVEL_2)
        def dto = new IscfXmlOamDto()
        dto.setLogicalName("this is the logical name")
        dto.setNodeFdn("this is the node fdn")
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setParams(params)
        when:
        IscfDtoHelper.validate(dto)
        then:
        notThrown(NscsBadRequestException)
    }

    def "validate null ISCF XML OAM dto"() {
        given:
        when:
        IscfDtoHelper.validate((IscfXmlOamDto)null)
        then:
        thrown(NscsBadRequestException)
    }

    def "validate invalid ISCF XML OAM dto"() {
        given:
        def dto = new IscfXmlOamDto()
        dto.setLogicalName("this is the logical name")
        dto.setNodeFdn("this is the node fdn")
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setParams(null)
        when:
        IscfDtoHelper.validate(dto)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "validate valid ISCF XML IPSEC dto #subjectAltNameParam and #ipsecAreas"() {
        given:
        def params = new IscfXmlIpsecParamsDto()
        params.setUserLabel("this is the user label")
        params.setSubjectAltNameParam(subjectAltNameParam)
        params.setIpsecAreas(ipsecAreas)
        def dto = new IscfXmlIpsecDto()
        dto.setLogicalName("this is the logical name")
        dto.setNodeFdn("this is the node fdn")
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setParams(params)
        when:
        IscfDtoHelper.validate(dto)
        then:
        notThrown(NscsBadRequestException)
        where:
        subjectAltNameParam << [
            subjectAltNameStringTypeDto,
            subjectAltNameEdiPartyDto
        ]
        ipsecAreas << [
            [
                IpsecArea.OM,
                IpsecArea.TRANSPORT
            ] as Set,
            [
                IpsecArea.OM,
                IpsecArea.TRANSPORT
            ] as Set
        ]
    }

    def "validate null ISCF XML IPSEC dto"() {
        given:
        when:
        IscfDtoHelper.validate((IscfXmlIpsecDto)null)
        then:
        thrown(NscsBadRequestException)
    }

    def "validate invalid ISCF XML IPSEC dto"() {
        given:
        def dto = new IscfXmlIpsecDto()
        dto.setLogicalName("this is the logical name")
        dto.setNodeFdn("this is the node fdn")
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setParams(null)
        when:
        IscfDtoHelper.validate(dto)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "validate valid ISCF XML COMBO dto #subjectAltNameParam and #ipsecAreas"() {
        given:
        def oamParams = new IscfXmlOamParamsDto()
        oamParams.setMinimumSecurityLevel(SecurityLevel.LEVEL_2)
        oamParams.setWantedSecurityLevel(SecurityLevel.LEVEL_2)
        def ipsecParams = new IscfXmlIpsecParamsDto()
        ipsecParams.setUserLabel("this is the user label")
        ipsecParams.setSubjectAltNameParam(subjectAltNameParam)
        ipsecParams.setIpsecAreas(ipsecAreas)
        def dto = new IscfXmlComboDto()
        dto.setLogicalName("this is the logical name")
        dto.setNodeFdn("this is the node fdn")
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setOamParams(oamParams)
        dto.setIpsecParams(ipsecParams)
        when:
        IscfDtoHelper.validate(dto)
        then:
        notThrown(NscsBadRequestException)
        where:
        subjectAltNameParam << [
            subjectAltNameStringTypeDto,
            subjectAltNameEdiPartyDto
        ]
        ipsecAreas << [
            [
                IpsecArea.OM,
                IpsecArea.TRANSPORT
            ] as Set,
            [
                IpsecArea.OM,
                IpsecArea.TRANSPORT
            ] as Set
        ]
    }

    def "validate null ISCF XML COMBO dto"() {
        given:
        when:
        IscfDtoHelper.validate((IscfXmlComboDto)null)
        then:
        thrown(NscsBadRequestException)
    }

    def "validate invalid ISCF XML COMBO dto null OAM"() {
        given:
        def ipsecParams = new IscfXmlIpsecParamsDto()
        ipsecParams.setUserLabel("this is the user label")
        ipsecParams.setSubjectAltNameParam(subjectAltNameStringTypeDto)
        ipsecParams.setIpsecAreas([
            IpsecArea.OM,
            IpsecArea.TRANSPORT
        ] as Set)
        def dto = new IscfXmlComboDto()
        dto.setLogicalName("this is the logical name")
        dto.setNodeFdn("this is the node fdn")
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setOamParams(null)
        dto.setIpsecParams(ipsecParams)
        when:
        IscfDtoHelper.validate(dto)
        then:
        thrown(NscsBadRequestException)
    }

    def "validate invalid ISCF XML COMBO dto null IPSEC"() {
        given:
        def oamParams = new IscfXmlOamParamsDto()
        oamParams.setMinimumSecurityLevel(SecurityLevel.LEVEL_2)
        oamParams.setWantedSecurityLevel(SecurityLevel.LEVEL_2)
        def dto = new IscfXmlComboDto()
        dto.setLogicalName("this is the logical name")
        dto.setNodeFdn("this is the node fdn")
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setOamParams(oamParams)
        dto.setIpsecParams(null)
        when:
        IscfDtoHelper.validate(dto)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "validate valid ISCF security data OAM dto #subjectAltNameParam"() {
        given:
        def dto = new IscfSecDataDto()
        dto.setNodeId(nodeIdentifierDto)
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setSubjectAltNameParam(subjectAltNameParam)
        when:
        IscfDtoHelper.validateSecDataOam(dto)
        then:
        notThrown(NscsBadRequestException)
        where:
        subjectAltNameParam << [
            null,
            subjectAltNameStringTypeDto,
            subjectAltNameEdiPartyDto
        ]
    }

    def "validate null ISCF security data OAM dto"() {
        given:
        when:
        IscfDtoHelper.validateSecDataOam(null)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "validate valid ISCF security data IPSEC and COMBO dto #subjectAltNameParam"() {
        given:
        def dto = new IscfSecDataDto()
        dto.setNodeId(nodeIdentifierDto)
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setSubjectAltNameParam(subjectAltNameParam)
        when:
        IscfDtoHelper.validateSecDataIpsecAndCombo(dto)
        then:
        notThrown(NscsBadRequestException)
        where:
        subjectAltNameParam << [
            subjectAltNameStringTypeDto,
            subjectAltNameEdiPartyDto
        ]
    }

    def "validate null ISCF security data IPSEC and COMBO dto"() {
        given:
        when:
        IscfDtoHelper.validateSecDataIpsecAndCombo(null)
        then:
        thrown(NscsBadRequestException)
    }

    def "validate invalid ISCF security data IPSEC and COMBO dto"() {
        given:
        def dto = new IscfSecDataDto()
        dto.setNodeId(nodeIdentifierDto)
        dto.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL)
        dto.setNodeModelInfo(nodeModelInfo)
        dto.setSubjectAltNameParam(null)
        when:
        IscfDtoHelper.validateSecDataIpsecAndCombo(dto)
        then:
        thrown(NscsBadRequestException)
    }

    def "convert valid node identifier dto"() {
        given:
        when:
        def NodeIdentifier nodeIdentifier = IscfDtoHelper.fromDto(nodeIdentifierDto)
        then:
        nodeIdentifier != null
        nodeIdentifier.getFdn() == nodeIdentifierDto.getNodeFdn()
        nodeIdentifier.getSerialNumber() == nodeIdentifierDto.getNodeSn()
    }

    def "convert null node identifier dto"() {
        given:
        when:
        IscfDtoHelper.fromDto((NodeIdentifierDto)null)
        then:
        thrown(NscsBadRequestException)
    }

    def "convert invalid node identifier dto"() {
        given:
        def nodeId = new NodeIdentifierDto(null, "this is the node serial number")
        when:
        IscfDtoHelper.fromDto(nodeId)
        then:
        thrown(NscsBadRequestException)
    }

    def "convert valid node model information dto"() {
        given:
        when:
        def NodeModelInformation nodeModelInformation = IscfDtoHelper.fromDto(nodeModelInfo)
        then:
        nodeModelInformation != null
        nodeModelInformation.getModelIdentifierType() == nodeModelInfo.getModelIdentifierType()
        nodeModelInformation.getModelIdentifier() == nodeModelInfo.getTargetModelIdentifier()
        nodeModelInformation.getNodeType() == nodeModelInfo.getTargetType()
    }

    def "convert null node model information dto"() {
        given:
        when:
        IscfDtoHelper.fromDto((NodeModelInformationDto)null)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "convert valid subject alternative name param dto #subjectAltNameParamDto"() {
        given:
        when:
        def SubjectAltNameParam san = IscfDtoHelper.fromDto(subjectAltNameParamDto)
        then:
        san != null
        and:
        san.getSubjectAltNameData() instanceof SubjectAltNameStringType == isSubjectAltNameStringType
        and:
        san.getSubjectAltNameData() instanceof SubjectAltNameEdiPartyType == isSubjectAltNameEdiPartyType
        where:
        subjectAltNameParamDto << [
            subjectAltNameStringTypeDto,
            subjectAltNameEdiPartyDto
        ]
        isSubjectAltNameStringType << [true, false]
        isSubjectAltNameEdiPartyType << [false, true]
    }

    def "convert null subject alternative name param dto"() {
        given:
        when:
        IscfDtoHelper.fromDto((SubjectAltNameParamDto)null)
        then:
        thrown(NscsBadRequestException)
    }
}
