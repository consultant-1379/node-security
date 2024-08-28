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

import java.util.EnumSet;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.iscf.IscfServiceValidators;
import com.ericsson.nms.security.nscs.iscf.SecurityDataCollector;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * Implementation of the EnrollmentInfoService interface.
 *
 * @see com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoService
 * 
 * @author tcsviga
 *
 */
@Stateless
public class EnrollmentInfoServiceBean implements EnrollmentInfoService {

    @Inject
    private IscfServiceValidators iscfServiceValidators;

    @Inject
    private SecurityDataCollector securityDataCollector;

    @Inject
    private Logger logger;

    @Override
    public SecurityDataResponse generateSecurityDataOam(final NodeModelInformation modelInfo, final EnrollmentRequestInfo enrollmentRequestInfo) throws EnrollmentInfoServiceException {

        logger.info("Generate OAM Security Data for node [{}] ", enrollmentRequestInfo.getNodeIdentifier().getFdn());

        // This method is only invoked by the generateenrollmentinfo command handler.
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataOam(enrollmentRequestInfo.getNodeIdentifier(), enrollmentRequestInfo.getSubjectAltNameParam(), enrollmentRequestInfo.getEnrollmentMode(), modelInfo);
        } catch (final Exception e) {
            final String errorMessage = String.format("Generate OAM Security Data ISCF Validation Exception: %s", NscsLogger.stringifyException(e));
            logger.error(errorMessage);
            throw new EnrollmentInfoServiceException(errorMessage);
        }

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), modelInfo, enrollmentRequestInfo);
        } catch (final Exception e) {
            logger.error("Generate OAM Security Data Validation Exception: {}", NscsLogger.stringifyException(e));
            throw new EnrollmentInfoServiceException("Error occurred while getting response");
        }
        logger.debug("generate SecurityDataOam returns: \nSecurityDataResponse [{}]", secDataResp);

        return secDataResp;
    }

}
