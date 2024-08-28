/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.service;

import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;

/**
 * Service provides Enrollment Information related methods related to enrollment info file generation
 *
 * @author tcsviga
 *
 */

public interface EnrollmentInfoService {

    /**
     * Generate OAM auto-integration Security Data for a node
     * 
     * @param NodeModelInformation
     *            The Node Model Information
     * @param EnrollmentRequestInfo
     *            The EnrollmentRequestInfo details
     *
     * @return SecurityDataResponse
     * @throws EnrollmentInfoServiceException
     */
    SecurityDataResponse generateSecurityDataOam(NodeModelInformation modelInfo, EnrollmentRequestInfo enrollmentRequestInfo)
            throws EnrollmentInfoServiceException;
}
