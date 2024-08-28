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
import java.util.EnumSet;
import java.util.Set;

import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;

/**
 * REST ISCF XML IPSEC parameters DTO.
 */
public class IscfXmlIpsecParamsDto implements Serializable {

    private static final long serialVersionUID = -1493825283554429302L;

    private String userLabel;
    private SubjectAltNameParamDto subjectAltNameParam;
    private Set<IpsecArea> ipsecAreas;

    /**
     * @return the userLabel
     */
    public String getUserLabel() {
        return userLabel;
    }

    /**
     * @param userLabel
     *            the userLabel to set
     */
    public void setUserLabel(final String userLabel) {
        this.userLabel = userLabel;
    }

    /**
     * @return the subjectAltNameParam
     */
    public SubjectAltNameParamDto getSubjectAltNameParam() {
        return subjectAltNameParam;
    }

    /**
     * @param subjectAltNameParam
     *            the subjectAltNameParam to set
     */
    public void setSubjectAltNameParam(final SubjectAltNameParamDto subjectAltNameParam) {
        this.subjectAltNameParam = subjectAltNameParam;
    }

    /**
     * @return a copy of the ipsecAreas or null if ipsecAreas is null
     */
    public Set<IpsecArea> getIpsecAreas() {
        return ipsecAreas != null ? EnumSet.copyOf(ipsecAreas) : null;
    }

    /**
     * @param ipsecAreas
     *            the ipsecAreas to set
     */
    public void setIpsecAreas(final Set<IpsecArea> ipsecAreas) {
        if (ipsecAreas == null) {
            this.ipsecAreas = null;
        } else {
            this.ipsecAreas = !ipsecAreas.isEmpty() ? EnumSet.copyOf(ipsecAreas) : EnumSet.noneOf(IpsecArea.class);
        }
    }
}
