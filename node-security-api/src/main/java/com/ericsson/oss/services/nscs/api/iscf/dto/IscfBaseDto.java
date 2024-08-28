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

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;

/**
 * Base ISCF DTO.
 */
public class IscfBaseDto implements Serializable {

    private static final long serialVersionUID = -5654911410993889733L;

    private NodeModelInformationDto nodeModelInfo;
    private EnrollmentMode enrollmentMode;

    /**
     * @return the nodeModelInfo
     */
    public NodeModelInformationDto getNodeModelInfo() {
        return nodeModelInfo;
    }

    /**
     * @param nodeModelInfo
     *            the nodeModelInfo to set
     */
    public void setNodeModelInfo(final NodeModelInformationDto nodeModelInfo) {
        this.nodeModelInfo = nodeModelInfo;
    }

    /**
     * @return the enrollmentMode
     */
    public EnrollmentMode getEnrollmentMode() {
        return enrollmentMode;
    }

    /**
     * @param enrollmentMode
     *            the enrollmentMode to set
     */
    public void setEnrollmentMode(final EnrollmentMode enrollmentMode) {
        this.enrollmentMode = enrollmentMode;
    }
}
