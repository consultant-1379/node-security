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
package com.ericsson.oss.services.nscs.api.credentials.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Models a list of credentials DTO.
 */
public class CredentialAttributesDto implements Serializable {

    private static final long serialVersionUID = 8951654372494880501L;

    private List<UserCredentialsDto> credentialsList;

    public CredentialAttributesDto() {
        /**
         * Empty constructor used by JSON parser.
         */
    }

    /**
     * @return a copy of the credentialsList
     */
    public List<UserCredentialsDto> getCredentialsList() {
        return Optional.ofNullable(credentialsList).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
    }

    /**
     * @param credentialsList
     *            the credentialsList to set
     */
    public void setCredentialsList(final List<UserCredentialsDto> credentialsList) {
        this.credentialsList = Optional.ofNullable(credentialsList).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CredentialAttributesDto [credentialsList=" + credentialsList + "]";
    }
}
