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
package com.ericsson.oss.services.nscs.api.iscf.dto;

import java.io.Serializable;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;

/**
 * REST ISCF XML OAM parameters DTO.
 */
public class IscfXmlOamParamsDto implements Serializable {

    private static final long serialVersionUID = 7962405829891102825L;

    private SecurityLevel wantedSecurityLevel;
    private SecurityLevel minimumSecurityLevel;

    /**
     * @return the wantedSecurityLevel
     */
    public SecurityLevel getWantedSecurityLevel() {
        return wantedSecurityLevel;
    }

    /**
     * @param wantedSecurityLevel
     *            the wantedSecurityLevel to set
     */
    public void setWantedSecurityLevel(final SecurityLevel wantedSecurityLevel) {
        this.wantedSecurityLevel = wantedSecurityLevel;
    }

    /**
     * @return the minimumSecurityLevel
     */
    public SecurityLevel getMinimumSecurityLevel() {
        return minimumSecurityLevel;
    }

    /**
     * @param minimumSecurityLevel
     *            the minimumSecurityLevel to set
     */
    public void setMinimumSecurityLevel(final SecurityLevel minimumSecurityLevel) {
        this.minimumSecurityLevel = minimumSecurityLevel;
    }
}
