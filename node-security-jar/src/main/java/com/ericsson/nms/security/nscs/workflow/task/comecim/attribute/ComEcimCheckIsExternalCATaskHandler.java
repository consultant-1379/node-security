/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.math.BigInteger;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerRisc;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckIsExternalCATask;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CHECK_IS_EXTERNAL_CA.
 * </p>
 * <p>
 * Get the CertificateEnrollmentCA (EXTERNAL_CA or ENM_PKI_CA) for certificate issue and reissue operations
 * </p>
 *
 * @author xsrirko
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_IS_EXTERNAL_CA)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckIsExternalCATaskHandler implements WFQueryTaskHandler<ComEcimCheckIsExternalCATask>, WFTaskHandlerInterface {
    private static final String ENM_PKI_CA = "ENM_PKI_CA";
    private static final String EXTERNAL_CA = "EXTERNAL_CA";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    NscsPkiEntitiesManagerRisc nscsPkiEntitiesManagerRisc;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Inject
    NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NscsCMReaderService reader;

    @Override
    public String processTask(final ComEcimCheckIsExternalCATask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);

        String certificateEnrollmentCa = task.getCertificateEnrollmentCa();
        /*
         * The value of certificateEnrollmentCa will be either EXTERNAL_CA or ENM_PKI_CA as per the --extca option provided for certificate issue
         * command. In case of certificate reissue operation, this value will be null
         */
        if (certificateEnrollmentCa == null) {
            nscsLogger.info(task, "Verifying certificateEnrollmentCa for certificate reissue operation");

            final String trustedCertCategory = task.getTrustedCertCategory();
            final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);

            final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(task.getNode());
            if (!CertificateType.IPSEC.name().equals(certificateType) || !nodeValidatorUtility.validateNodeTypeForExtCa(normNode)) {
                nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task,
                        "certificateEnrollmentCa for certType " + certificateType + " is " + ENM_PKI_CA);
                return ENM_PKI_CA;
            }

            final CertStateInfo certStateInfo = moGetServiceFactory.getCertificateIssueStateInfo(task.getNode(), certificateType);
            if (certStateInfo.getCertificates() != null && !certStateInfo.getCertificates().isEmpty()) {
                final CertDetails certDetail = certStateInfo.getCertificates().iterator().next();
                final String subjectDn = certDetail.getSubject();
                final String issuerDn = certDetail.getIssuer();
                final BigInteger serialNumber = certDetail.getSerial();
                if (subjectDn == null || issuerDn == null || serialNumber == null) {
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, "NodeCredential MO not found",
                            "Invalid operation, perform certificate issue prior to reissue operation");
                    throw new MissingMoException("Invalid operation, perform certificate issue prior to reissue operation");
                }

                certificateEnrollmentCa = getCertificateEnrollmentCa(task, subjectDn, serialNumber, issuerDn);
            } else {
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, "NodeCredential MO not found",
                        "Invalid operation, perform certificate issue prior to reissue operation");
                throw new MissingMoException("Invalid operation, perform certificate issue prior to reissue operation");
            }
        } else {
            certificateEnrollmentCa = task.getCertificateEnrollmentCa();
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "certificateEnrollmentCa is " + certificateEnrollmentCa);
        return certificateEnrollmentCa;
    }

    private String getCertificateEnrollmentCa(final ComEcimCheckIsExternalCATask task, final String subjectDn, final BigInteger serialNumber,
            final String issuerDn) {
        String certificateEnrollmentCa = null;
        final String hexaDecimalSerialNumber = serialNumber.toString(16);
        nscsLogger.info("Certificate Details from NodeCredential [SubjectDn: {}, SerialNumber : {}, IssuerDn : {}]", subjectDn,
                hexaDecimalSerialNumber, issuerDn);
        try {
            if (nscsPkiEntitiesManagerRisc.isCertificateExist(subjectDn, hexaDecimalSerialNumber, issuerDn)) {
                certificateEnrollmentCa = ENM_PKI_CA;
            } else {
                certificateEnrollmentCa = EXTERNAL_CA;
            }
        } catch (NscsPkiEntitiesManagerException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while validation of node certificate with ENM PKI ";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return certificateEnrollmentCa;
    }
}
