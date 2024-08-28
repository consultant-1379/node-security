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
package com.ericsson.oss.services.security.nscs.credentials.dto;

import java.util.List;

import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.UserCredentials;
import com.ericsson.nms.security.nscs.api.enums.CredentialsType;
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.oss.services.nscs.api.credentials.dto.CredentialAttributesDto;
import com.ericsson.oss.services.nscs.api.credentials.dto.NodeCredentialsDto;
import com.ericsson.oss.services.nscs.api.credentials.dto.UserCredentialsDto;

/**
 * Auxiliary class to manage validation and conversion of Credential DTOs.
 */
public final class CredentialDtoHelper {

    private static final String NULL_DTO = "Null DTO";
    private static final String INVALID_DTO = "Invalid DTO";
    private static final String PLEASE_PROVIDE_A_NOT_NULL_DTO = "Please provide a not null DTO";
    private static final String PLEASE_PROVIDE_A_VALID_DTO = "Please provide a valid DTO";
    private static final String NULL_MANDATORY_DTO_ATTR = "Null mandatory DTO attribute";
    private static final String PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR = "Please provide a not null mandatory DTO attribute";

    private CredentialDtoHelper() {
    }

    /**
     * Validates the Node Credentials DTO.
     * 
     * @param dto
     *            the DTO to be validated.
     * @throws NscsBadRequestException
     *             if validation fails.
     */
    public static void validate(final NodeCredentialsDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
    }

    /**
     * Converts the given credentials parameter DTO.
     * 
     * @param dto
     *            the DTO to be converted.
     * @return the converted DTO.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static CredentialAttributes fromDto(final CredentialAttributesDto dto) {
        if (dto == null) {
            throw new NscsBadRequestException(NULL_DTO, PLEASE_PROVIDE_A_NOT_NULL_DTO);
        }
        final List<UserCredentialsDto> credentialsList = dto.getCredentialsList();
        if (credentialsList == null) {
            throw new NscsBadRequestException(INVALID_DTO, PLEASE_PROVIDE_A_VALID_DTO);
        }
        UserCredentials root = null;
        UserCredentials normal = null;
        UserCredentials secure = null;
        UserCredentials nwiea = null;
        UserCredentials nwieb = null;
        UserCredentials nodecli = null;

        for (final UserCredentialsDto credentials : credentialsList) {
            if (credentials.getCredType() == null) {
                throw new NscsBadRequestException(NULL_MANDATORY_DTO_ATTR, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR);
            }
            if (credentials.getCredUser() == null && credentials.getCredPass() == null) {
                throw new NscsBadRequestException(NULL_MANDATORY_DTO_ATTR, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR);
            }
            if (CredentialsType.ROOT.equals(credentials.getCredType())) {
                root = new UserCredentials(credentials.getCredUser(), credentials.getCredPass());
            } else if (CredentialsType.NORMAL.equals(credentials.getCredType())) {
                normal = new UserCredentials(credentials.getCredUser(), credentials.getCredPass());
            } else if (CredentialsType.SECURE.equals(credentials.getCredType())) {
                secure = new UserCredentials(credentials.getCredUser(), credentials.getCredPass());
            } else if (CredentialsType.NWI_E_A.equals(credentials.getCredType())) {
                nwiea = new UserCredentials(credentials.getCredUser(), credentials.getCredPass());
            } else if (CredentialsType.NWI_E_B.equals(credentials.getCredType())) {
                nwieb = new UserCredentials(credentials.getCredUser(), credentials.getCredPass());
            } else if (CredentialsType.NODE_CLI.equals(credentials.getCredType())) {
                nodecli = new UserCredentials(credentials.getCredUser(), credentials.getCredPass());
            }
        }
        return new CredentialAttributes(root, normal, secure, nwiea, nwieb, nodecli);
    }
}
