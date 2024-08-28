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
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.node.certificate.validator.NodeMoValidatorForExternalCA;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.helper.ExternalCaCertificateReissueSerializer;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckNodeCredentialForExternalCAReIssueTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL_FOR_EXTERNAL_CA_REISSUE.
 * </p>
 * <p>
 * This task Handler validates the NodeCredential, EnrollmentAuthority and EnrollmentServerGroup MOs for performing reissue operation with External CA
 * </p>
 *
 * @author xsrirko
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL_FOR_EXTERNAL_CA_REISSUE)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckNodeCredentialForExternalCAReIssueTaskHandler
        implements WFQueryTaskHandler<ComEcimCheckNodeCredentialForExternalCAReIssueTask>, WFTaskHandlerInterface {

    private static final String VALID = "VALID";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NSCSComEcimNodeUtility comEcimNodeUtility;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    NodeMoValidatorForExternalCA nodeMoValidatorForExternalCA;

    @Inject
    private ComEcimMoNaming comEcimMoNaming;

    @Inject
    private ExternalCaCertificateReissueSerializer serializer;

    @Override
    public String processTask(final ComEcimCheckNodeCredentialForExternalCAReIssueTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(task.getNode());

        final Mo rootMo = capabilityService.getMirrorRootMo(normalizableNodeRef);

        final String mirrorRootFdn = normalizableNodeRef.getFdn();

        final String trustedCertCategory = task.getTrustedCertCategory();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);

        final String nodeCredentialFdn = comEcimNodeUtility.getNodeCredentialFdn(mirrorRootFdn, rootMo, certificateType, normalizableNodeRef);

        final MoObject nodeCredentialMoObj = getNodeCredentialMoObject(task, normalizableNodeRef, rootMo, nodeCredentialFdn, certificateType);

        nodeMoValidatorForExternalCA.validateNodeCredentialMo(nodeCredentialMoObj);

        final String enrollmentServerGroupFdn = nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_SERVER_GROUP);
        nodeMoValidatorForExternalCA.validateEnrollmentServerGroupMo(enrollmentServerGroupFdn, rootMo);

        final Map<String, String> enrollmentCAAuthorizationModes = capabilityService.getEnrollmentCAAuthorizationModes(normalizableNodeRef);
        final String enrollmentAuthorityFdn = nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_AUTHORITY);
        nodeMoValidatorForExternalCA.validateEnrollmentAuthority(enrollmentAuthorityFdn, certificateType, enrollmentCAAuthorizationModes);

        final String nodeCredentialMsg = "Current NodeCredential FDN [" + nodeCredentialFdn + "] ";
        final List<String> reservedByUser = nodeCredentialMoObj.getAttribute(NodeCredential.RESERVED_BY_USER);
        if (reservedByUser == null || reservedByUser.isEmpty()) {
            nscsLogger.error(task, nodeCredentialMsg + "has null or empty reservedByUser attribute!!!");
        } else {
            nscsLogger.info(task, nodeCredentialMsg + "has reservedByUser attribute with size [" + reservedByUser.size() + "]");
        }

        prepareEnrollmentInfo(task, normalizableNodeRef, outputParams);

        final String actualRenewalMode = nodeCredentialMoObj.getAttribute(NodeCredential.RENEWAL_MODE);
        outputParams.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(), actualRenewalMode);

        final String trustCategoryFdn = comEcimNodeUtility.getTrustCategoryFdn(mirrorRootFdn, rootMo, certificateType, normalizableNodeRef);
        outputParams.put(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString(), trustCategoryFdn);

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, nodeCredentialMsg);
        return serializer.serializeResult(task, VALID, nodeCredentialFdn, nodeCredentialFdn, reservedByUser, outputParams);
    }

    private MoObject getNodeCredentialMoObject(final ComEcimCheckNodeCredentialForExternalCAReIssueTask task,
            final NormalizableNodeReference normalizableNodeRef, Mo rootMo, final String nodeCredentialFdn, final String certificateType) {

        final Mo nodeCredentialMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.nodeCredential;
        final String mirrorRootFdn = normalizableNodeRef.getFdn();
        final String targetCategory = normalizableNodeRef.getTargetCategory();
        final String nodeType = normalizableNodeRef.getNeType();
        final String tMI = normalizableNodeRef.getOssModelIdentity();

        final String modelInfoParams = String.format("targetCategory [%s] nodeType [%s] targetModelIdentity [%s] model [%s]", targetCategory,
                nodeType, tMI, nodeCredentialMo.type());
        nscsLogger.info(task, "Getting model info for " + modelInfoParams);
        Map<String, NscsModelInfo> nscsModelInfos = null;
        try {
            nscsModelInfos = nscsModelServiceImpl.getModelInfoList(targetCategory, nodeType, tMI, nodeCredentialMo.type());
        } catch (NscsModelServiceException | IllegalArgumentException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while getting model info of " + modelInfoParams;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        if (nscsModelInfos == null) {
            final String errorMessage = "Got null model info for " + modelInfoParams;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final NscsModelInfo nodeCredentialModelInfo = nscsModelInfos.get(nodeCredentialMo.type());
        MoObject nodeCredentialMoObj = null;
        if (nodeCredentialFdn != null && !nodeCredentialFdn.isEmpty()) {
            nodeCredentialMoObj = readerService.getMoObjectByFdn(nodeCredentialFdn);
        } else {
            final NormalizableNodeReference node = null;
            final String nodeCredentialName = comEcimMoNaming.getDefaultName(nodeCredentialModelInfo.getName(), certificateType, node);

            final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
            final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, certMMo.type());
            nscsLogger.debug(task, "Reading " + readMessage);
            final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo, null);
            if (certMFdn == null || certMFdn.isEmpty()) {
                final String errorMessage = "Error while reading " + readMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new MissingMoException(task.getNode().getName(), certMMo.type());
            }
            final String defaultNodeCredentialFdn = nodeCredentialMo.getFdnByParentFdn(certMFdn, nodeCredentialName);
            nodeCredentialMoObj = readerService.getMoObjectByFdn(defaultNodeCredentialFdn);
        }

        if (nodeCredentialMoObj == null) {
            final String errorMessage = "NodeCredential [" + nodeCredentialFdn + "] not found for certificate type [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(task.getNode().getName(), nodeCredentialMo.type());
        }
        return nodeCredentialMoObj;
    }

    private void prepareEnrollmentInfo(final ComEcimCheckNodeCredentialForExternalCAReIssueTask task,
            final NormalizableNodeReference normalizableNodeRef, final Map<String, Serializable> outputParams) {

        final String enrollmentModeFromTask = task.getEnrollmentMode();
        EnrollmentMode enrollmentMode = null;
        final String enrollmentModeFromParams = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString());
        if (enrollmentModeFromParams != null && !enrollmentModeFromParams.isEmpty()) {
            enrollmentMode = EnrollmentMode.valueOf(enrollmentModeFromParams);
        }
        if (enrollmentMode == null) {
            enrollmentMode = nscsNodeUtility.getEnrollmentMode(enrollmentModeFromTask, normalizableNodeRef);
        }
        nscsLogger.info(task, "enrollmentMode [" + enrollmentMode.name() + "]");

        String serializedEnrollmentInfo = null;
        try {
            final ScepEnrollmentInfoImpl enrollmentInfo = new ScepEnrollmentInfoImpl(null, null, null, null, 0, null, null, enrollmentMode, null,
                    null);
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollmentInfo);
        } catch (final CertificateEncodingException | IOException | NoSuchAlgorithmException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing enrollment info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), enrollmentMode.name());
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
    }

}
