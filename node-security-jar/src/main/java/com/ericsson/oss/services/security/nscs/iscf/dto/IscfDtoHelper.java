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
package com.ericsson.oss.services.security.nscs.iscf.dto;

import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameEdiPartyType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfSecDataDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlComboDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlIpsecDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlOamDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.NodeIdentifierDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.NodeModelInformationDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.SubjectAltNameParamDto;

/**
 * Auxiliary class to manage validation and conversion of ISCF DTOs.
 */
public final class IscfDtoHelper {

    private static final String NULL_DTO = "Null DTO";
    private static final String INVALID_DTO = "Invalid DTO";
    private static final String PLEASE_PROVIDE_A_NOT_NULL_DTO = "Please provide a not null DTO";
    private static final String PLEASE_PROVIDE_A_VALID_DTO = "Please provide a valid DTO";
    private static final String NULL_MANDATORY_DTO = "Null mandatory DTO";
    private static final String INVALID_MANDATORY_DTO = "Invalid mandatory DTO";
    private static final String PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO = "Please provide a not null mandatory DTO";
    private static final String PLEASE_PROVIDE_A_VALID_MANDATORY_DTO = "Please provide a valid mandatory DTO";

    private IscfDtoHelper() {
    }

    /**
     * Validates the ISCF XML OAM DTO.
     * 
     * It must be not null and containing not null OAM parameters.
     * 
     * @param dto
     *            the DTO to be validated.
     * @throws NscsBadRequestException
     *             if validation fails.
     */
    public static void validate(final IscfXmlOamDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
        if (dto.getParams() == null) {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
    }

    /**
     * Validates the ISCF XML IPSEC DTO.
     * 
     * It must be not null and containing not null IPSEC parameters.
     * 
     * @param dto
     *            the DTO to be validated.
     * @throws NscsBadRequestException
     *             if validation fails.
     */
    public static void validate(final IscfXmlIpsecDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
        if (dto.getParams() == null) {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
    }

    /**
     * Validates the ISCF XML COMBO DTO.
     * 
     * It must be not null and containing not null OAM and IPSEC parameters.
     * 
     * @param dto
     *            the DTO to be validated.
     * @throws NscsBadRequestException
     *             if validation fails.
     */
    public static void validate(final IscfXmlComboDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
        if (dto.getOamParams() == null) {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
        if (dto.getIpsecParams() == null) {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
    }

    /**
     * Validates the ISCF Security Data OAM DTO.
     * 
     * It must be not null.
     * 
     * @param dto
     *            the DTO to be validated.
     * @throws NscsBadRequestException
     *             if validation fails.
     */
    public static void validateSecDataOam(final IscfSecDataDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
    }

    /**
     * Validates the ISCF Security Data IPSEC and COMBO DTO.
     * 
     * It must be not null and containing not null IPSEC parameters.
     * 
     * @param dto
     *            the DTO to be validated.
     * @throws NscsBadRequestException
     *             if validation fails.
     */
    public static void validateSecDataIpsecAndCombo(final IscfSecDataDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
        if (dto.getSubjectAltNameParam() == null) {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
    }

    /**
     * Converts the given node identifier DTO.
     * 
     * @param dto
     *            the DTO to be converted.
     * @return the converted DTO.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static NodeIdentifier fromDto(final NodeIdentifierDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_MANDATORY_DTO, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO);
        }
        if (dto.getNodeFdn() == null) {
            throw new NscsBadRequestException(INVALID_MANDATORY_DTO, PLEASE_PROVIDE_A_VALID_MANDATORY_DTO);
        }
        return new NodeIdentifier(dto.getNodeFdn(), dto.getNodeSn());
    }

    /**
     * Converts the given node model information DTO.
     * 
     * @param dto
     *            the DTO to be converted.
     * @return the converted DTO.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static NodeModelInformation fromDto(final NodeModelInformationDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_MANDATORY_DTO, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO);
        }
        return new NodeModelInformation(dto.getTargetModelIdentifier(), dto.getModelIdentifierType(), dto.getTargetType());
    }

    /**
     * Converts the given subject alternative name parameter DTO.
     * 
     * @param dto
     *            the DTO to be converted.
     * @return the converted DTO.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static SubjectAltNameParam fromDto(final SubjectAltNameParamDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_MANDATORY_DTO, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO);
        }
        if (dto.getValue() != null) {
            final BaseSubjectAltNameDataType subjectAltNameData = new SubjectAltNameStringType(dto.getValue());
            return new SubjectAltNameParam(dto.getSubjectAltNameFormat(), subjectAltNameData);
        } else {
            final SubjectAltNameEdiPartyType subjectAltNameEdiParty = new SubjectAltNameEdiPartyType();
            subjectAltNameEdiParty.setNameAssigner(dto.getNameAssigner());
            subjectAltNameEdiParty.setPartyName(dto.getPartyName());
            return new SubjectAltNameParam(dto.getSubjectAltNameFormat(), subjectAltNameEdiParty);
        }
    }
}
