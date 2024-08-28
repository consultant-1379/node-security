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

import com.ericsson.nms.security.nscs.api.enums.CredentialsType;

/**
 * Models credentials DTO.
 */
public class UserCredentialsDto implements Serializable {

    private static final long serialVersionUID = -8066465631373288765L;

    private CredentialsType credType;
    private String credUser;
    private String credPass;

    public UserCredentialsDto() {
        /**
         * Empty constructor used by JSON parser.
         */
    }

    /**
     * @return the credType
     */
    public CredentialsType getCredType() {
        return credType;
    }

    /**
     * @param credType
     *            the credType to set
     */
    public void setCredType(final CredentialsType credType) {
        this.credType = credType;
    }

    /**
     * @return the credUser
     */
    public String getCredUser() {
        return credUser;
    }

    /**
     * @param credUser
     *            the credUser to set
     */
    public void setCredUser(final String credUser) {
        this.credUser = credUser;
    }

    /**
     * @return the credPass
     */
    public String getCredPass() {
        return credPass;
    }

    /**
     * @param credPass
     *            the credPass to set
     */
    public void setCredPass(final String credPass) {
        this.credPass = credPass;
    }

    @Override
    public String toString() {
        return "UserCredentialsDto [credType=" + credType + ", credUser=" + credUser + ", credPass=******]";
    }
}
