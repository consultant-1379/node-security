/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.pki;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskResult;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.pki.RevokeNodeCertificateTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.REVOKE_NODE_CERTIFICATE
 * </p>
 * 
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.REVOKE_NODE_CERTIFICATE)
@Local(WFTaskHandlerInterface.class)
public class RevokeNodeCertificateTaskHandler implements WFQueryTaskHandler<RevokeNodeCertificateTask>, WFTaskHandlerInterface {
    private static final String EXTERNAL_CA = "EXTERNAL_CA";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Override
    public String processTask(RevokeNodeCertificateTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        String revocationResult = this.revokeCertificate(task);

        final String successMessage = "Completed : revocation result is [" + revocationResult + "]";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);

        return revocationResult;
    }

    /**
     * 
     * @param task
     * @return
     */
    private String revokeCertificate(RevokeNodeCertificateTask task) {

        WFTaskResult result = WFTaskResult.FALSE;

        final String revocationReason = task.getRevocationReason();

        Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String nodeFdn = task.getNodeFdn();
        String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
        ScepEnrollmentInfoImpl enrollmentInfo = extractEnrollmentInfo(serializedEnrollmentInfo);

        if (enrollmentInfo == null) {
            final String errorMessage = "Missing enrollment info parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        Entity entity = null;

        if (EXTERNAL_CA.equals(task.getCertificateEnrollmentCa())) {
            String entityNameFromNode = task.getNode().getName();
            final String trustedCertCategory = task.getTrustedCertCategory();
            final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);
            if (certificateType.equals(CertificateType.OAM.toString())) {
                entityNameFromNode = entityNameFromNode + "-oam";
            } else if (certificateType.equals(CertificateType.IPSEC.toString())) {
                entityNameFromNode = entityNameFromNode + "-ipsec";
            }

            try {
                entity = nscsPkiManager.getPkiEntity(entityNameFromNode);
            } catch (final NscsPkiEntitiesManagerException exception) {
                nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Entity not found in PKI for revocation", exception.getMessage());
                result = WFTaskResult.TRUE;
                return result.getTaskResult();
            }
        } else {
            entity = enrollmentInfo.getEntity();
        }

        String certificateSerialNumber = null;
        String certificateIssuer = null;

        final String entityName = entity.getEntityInfo().getName();
        nscsLogger.debug(task, "From enrollment info : entity [ {} ]", entityName);
        Certificate activeCert = entity.getEntityInfo().getActiveCertificate();
        if (activeCert != null) {
            certificateSerialNumber = activeCert.getSerialNumber();
            if (activeCert.getIssuer() == null) {
                nscsLogger.info(task, "From enrollmentInfo : can't find active certificate issuer for entity [" + entityName + "]");
            } else {
                certificateIssuer = activeCert.getIssuer().getName();
            }

            if (certificateSerialNumber == null || certificateSerialNumber.isEmpty() || certificateIssuer == null || certificateIssuer.isEmpty()) {
                //error condition
                final String errorMessage = "Can't revoke certificate for node [" + nodeFdn + "] - Invalid certificateSerialNumber [" + certificateSerialNumber + "] or invalid certificateIssuer ["
                        + certificateIssuer + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            } else {
                nscsLogger.info(task, "From enrollmentInfo : certificateSerialNumber [" + certificateSerialNumber + "] certificateIssuer [" + certificateIssuer + "]");

                EnrollmentMode enrollmentMode = nscsNodeUtility.getEnrollmentMode(nodeFdn);
                nscsLogger.info(task, "enrollmentMode [" + enrollmentMode.name() + "] : revocationReason [" + revocationReason + "]");

                //the revocationReason is the VALUE of enum com.ericsson.nms.security.nscs.api.enums.RevocationReason

                if (EnrollmentMode.NOT_SUPPORTED.equals(enrollmentMode)) {
                    final String errorMessage = "Invalid enrollmentMode [" + enrollmentMode.name() + "]";
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new UnexpectedErrorException(errorMessage);
                } else if (EnrollmentMode.SCEP.equals(enrollmentMode)) {
                    final String revokeMessage = "revocation of certificate : SN [" + certificateSerialNumber + "] : issuer [" + certificateIssuer + "] : reason [" + revocationReason + "]";
                    nscsLogger.workFlowTaskHandlerOngoing(task, "Performing PKI " + revokeMessage);
                    try {
                        securityService.revokeCertificateByIssuerName(certificateIssuer, certificateSerialNumber, revocationReason);
                        result = WFTaskResult.TRUE;
                    } catch (CppSecurityServiceException e) {
                        final String errorMessage = NscsLogger.stringifyException(e) + " while performing " + revokeMessage;
                        nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                        throw new UnexpectedErrorException(errorMessage);
                    }
                } else {
                    nscsLogger.info(task, "No need to perform revocation for enrollmentMode [" + enrollmentMode.name() + "]");
                    result = WFTaskResult.TRUE;
                }
            }
        } else {
            nscsLogger.info(task, "No need to perform revocation for node [" + nodeFdn + "], null active certificate found");
            result = WFTaskResult.TRUE;
        }

        return result.getTaskResult();
    }

    /**
     * 
     * @param serializedEnrollmentInfo
     * @return
     */
    private ScepEnrollmentInfoImpl extractEnrollmentInfo(String serializedEnrollmentInfo) {

        ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
        return enrollmentInfo;
    }

}
