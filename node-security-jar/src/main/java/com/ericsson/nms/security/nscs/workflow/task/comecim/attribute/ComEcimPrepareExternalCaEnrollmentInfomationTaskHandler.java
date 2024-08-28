/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean.KeyLength;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NSCSCertificateUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimPrepareExternalCaEnrollmentInfoTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

@WFTaskType(WorkflowTaskType.COM_ECIM_PREPARE_EXT_CA_ENROLLMENT_INFO)
@Local(WFTaskHandlerInterface.class)
public class ComEcimPrepareExternalCaEnrollmentInfomationTaskHandler implements WFQueryTaskHandler<ComEcimPrepareExternalCaEnrollmentInfoTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Override
    public String processTask(final ComEcimPrepareExternalCaEnrollmentInfoTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Output params are not yet set!";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(node);
        nscsLogger.info(task,
                "From task : nodeType [" + normalizableNodeRef.getNeType() + "] tMI [" + normalizableNodeRef.getOssModelIdentity() + "]");

        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(task.getTrustedCertCategory());
        nscsLogger.info(task, "From task : certificate type [" + certificateType + "]");

        final String nodeFdn = task.getNodeFdn();

        final NodeModelInformation nodeModelInfo = readerService.getNodeModelInformation(nodeFdn);
        nscsLogger.info(task, "From DPS : nodeModelInfo [" + nodeModelInfo.toString() + "]");

        final String enrollmentModeFromTask = task.getEnrollmentMode();
        final EnrollmentMode enrollmentMode = nscsNodeUtility.getEnrollmentMode(enrollmentModeFromTask, normalizableNodeRef);

        final String keyAlgorithmFromTask = task.getKeyAlgorithm();
        nscsLogger.info(task, "enrollmentMode [" + enrollmentMode.name() + "]");
        final AlgorithmKeys keyAlgorithm = nscsNodeUtility.getAlgorithmKeys(keyAlgorithmFromTask, normalizableNodeRef);
        nscsLogger.info(task, "keyAlgorithm [" + keyAlgorithm.name() + "]");

        // Get the enrolling info
        ScepEnrollmentInfoImpl enrollmentInfo = null;
        try {

            final X509Certificate extCaCert = NSCSCertificateUtility.prepareX509Certificate(task.getCaCertificate());

            enrollmentInfo = (ScepEnrollmentInfoImpl) generateExtCaNodeEnrollmentInfo(task, nodeFdn, normalizableNodeRef, keyAlgorithm,
                    enrollmentMode, extCaCert, nodeModelInfo);
            nscsLogger.debug(task, "Successfully got enrollment info [" + enrollmentInfo + "]");

        } catch (final CertificateException | CppSecurityServiceException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while getting enrollment info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return serializeResult(task, enrollmentInfo, outputParams);
    }

    private String serializeResult(final ComEcimPrepareExternalCaEnrollmentInfoTask task, final ScepEnrollmentInfoImpl enrollmentInfo,
            Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        Map<String, Serializable> outParams = outputParams;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outParams = new HashMap<>();
        }

        // Serialize Enrollment Info in output parameters
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollmentInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        outParams.put(WorkflowOutputParameterKeys.IS_ONLINE_ENROLLMENT.toString(), "TRUE");

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully completed : external ca enrollment information is prepared";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);

        return encodedWfQueryTaskResult;
    }

    private ScepEnrollmentInfo generateExtCaNodeEnrollmentInfo(final ComEcimPrepareExternalCaEnrollmentInfoTask task, final String nodeFdn,
            final NormalizableNodeReference normRef, final AlgorithmKeys keySize, EnrollmentMode enrollmentMode, final X509Certificate extCaCert,
            final NodeModelInformation modelInfo) throws CppSecurityServiceException {
        // Check on Network Element Security
        if (normRef == null) {
            final String errorMsg = "NormalizedNodeReference MO is null for node [" + nodeFdn + "]";
            nscsLogger.error(errorMsg);
            throw new UnexpectedErrorException(errorMsg);
        }

        enrollmentMode = securityService.configureNESAndGetEnrollmentMode(enrollmentMode, nodeFdn, normRef);

        final KeyLength keyLength = NscsPkiUtils.convertAlgorithmKeysToKeyLength(keySize);

        try {
            nscsLogger.info("init ScepEnrollmentInfoImpl");
            nscsLogger.debug("Getting Digest Algorithm");
            final DigestAlgorithm digestAlgorithm = nscsCapabilityModelService.getDefaultDigestAlgorithm(modelInfo);
            nscsLogger.debug(
                    "Digest Algorithm " + "- getDigestValuePrefix [{}], " + "- getEnmDigestAlgorithmValue [{}], "
                            + "- getStandardDigestAlgorithmValue [{}]",
                    digestAlgorithm.getDigestValuePrefix(), digestAlgorithm.getEnmDigestAlgorithmValue(),
                    digestAlgorithm.getStandardDigestAlgorithmValue());

            final Entity entity = NscsPkiUtils.createEntity(task.getCertificateSubjectDn(), task.getSubjectAltName(), task.getSubjectAltNameType());

            final ScepEnrollmentInfo scepInfo = new ScepEnrollmentInfoImpl(entity, task.getEnrollmentServerUrl(), extCaCert, digestAlgorithm, 0,
                    task.getChallengePassword(), keyLength.toString(), enrollmentMode, extCaCert, null);
            scepInfo.setCertificateAuthorityDn(CertDetails.getBcX500Name(task.getCertificateAuthorityDn()));

            nscsLogger.debug("ScepEnrollmentInfo is constructed [{}]", scepInfo);

            return scepInfo;
        } catch (CertificateEncodingException | IllegalArgumentException | NoSuchAlgorithmException ex) {
            final String errorMsg = String.format("Before updating EnrollmentInfo: exception[%s] msg[%s]", ex.getClass(), ex.getMessage());
            nscsLogger.error("{} : ex[{}]", errorMsg, ex);
            throw new UnexpectedErrorException(errorMsg);
        }
    }

}
