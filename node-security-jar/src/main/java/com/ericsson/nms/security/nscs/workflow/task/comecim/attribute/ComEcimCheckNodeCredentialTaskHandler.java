/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentAuthority;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer.EnrollmentProtocol;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential.RenewalMode;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckNodeCredentialTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL.
 * </p>
 * <p>
 * Create the NodeCredential MO if missing, check the state if existing
 * </p>
 *
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckNodeCredentialTaskHandler implements WFQueryTaskHandler<ComEcimCheckNodeCredentialTask>, WFTaskHandlerInterface {

    private static final String NOT_VALID = "NOT_VALID";
    private static final String VALID = "VALID";
    private static final String EXTERNAL_CA = "EXTERNAL_CA";
    private static final String NODE_CREDENTIAL_SUBJECT_NAME = "nodeCredentialSubjectName";
    private static final String NODE_CREDENTIAL_SUBJECT_ALT_NAME = "nodeCredentialSubjectAltName";
    private static final String NODE_CREDENTIAL_FDN = "nodeCredentialFdn";
    private static final String NODE_CREDENTIAL_KEY_INFO = "nodeCredentialKeyInfo";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCMWriterService writerService;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NSCSComEcimNodeUtility comEcimNodeUtility;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private ComEcimMoNaming comEcimMoNaming;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Override
    public String processTask(final ComEcimCheckNodeCredentialTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final String trustedCertCategory = task.getTrustedCertCategory();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);
        nscsLogger.info(task, "From task : certificate type [" + certificateType + "]");

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(node);
        final String mirrorRootFdn = normalizableNodeRef.getFdn();
        nscsLogger.info(task, "From task : mirrorRootFdn [" + mirrorRootFdn + "]");
        final String nodeName = task.getNode().getName();
        final String targetCategory = normalizableNodeRef.getTargetCategory();
        final String nodeType = normalizableNodeRef.getNeType();
        final String tMI = normalizableNodeRef.getOssModelIdentity();
        nscsLogger.info(task, "From task : nodeType [" + nodeType + "] tMI [" + tMI + "]");
        final boolean isReissue = Boolean.parseBoolean(task.getIsReissue());
        nscsLogger.info(task, "From task : isReissue [" + isReissue + "]");

        // Extract output parameters possibly set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String certificateEnrollmentCa = task.getCertificateEnrollmentCa();
        boolean isExternalCa = false;
        if (EXTERNAL_CA.equals(certificateEnrollmentCa)) {
            isExternalCa = true;
        }

        final String enrollmentAuthorityCertificate = (String) outputParams
                .get(WorkflowOutputParameterKeys.ENROLLMENT_CA_TRUSTED_CERTIFICATE_FDN.toString());

        final Mo rootMo = capabilityService.getMirrorRootMo(normalizableNodeRef);

        // Get CertM MO FDN
        final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, certMMo.type());
        nscsLogger.debug(task, "Reading " + readMessage);
        final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo);
        if (certMFdn == null || certMFdn.isEmpty()) {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(nodeName, certMMo.type());
        }

        // Get NodeCredential MO
        String nodeCredentialFdn = comEcimNodeUtility.getNodeCredentialFdn(mirrorRootFdn, rootMo, certificateType, normalizableNodeRef);
        MoObject nodeCredentialMoObj = null;
        boolean isToBeCreatedNodeCredential = false;
        boolean isNodeCredentialAlreadyCreatedWithoutCredentialUser = false;

        final Mo nodeCredentialMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.nodeCredential;
        final Mo enrollmentAuthorityMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.enrollmentAuthority;
        final Mo enrollmentServerGroupMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.enrollmentServerGroup;
        final Mo enrollmentServerMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.enrollmentServerGroup.enrollmentServer;
        final String requestedModels[] = { nodeCredentialMo.type(), enrollmentAuthorityMo.type(), enrollmentServerGroupMo.type(),
                enrollmentServerMo.type() };
        final String modelInfoParams = String.format("targetCategory [%s] nodeType [%s] targetModelIdentity [%s] models [%s]", targetCategory,
                nodeType, tMI, Arrays.toString(requestedModels));
        nscsLogger.info(task, "Getting model info for " + modelInfoParams);
        Map<String, NscsModelInfo> nscsModelInfos = null;
        try {
            nscsModelInfos = nscsModelServiceImpl.getModelInfoList(targetCategory, nodeType, tMI, requestedModels);
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
        nscsLogger.info(task, "Successfully got model info.");
        final NscsModelInfo nodeCredentialModelInfo = nscsModelInfos.get(nodeCredentialMo.type());
        final NscsModelInfo enrollmentAuthorityModelInfo = nscsModelInfos.get(enrollmentAuthorityMo.type());
        final NscsModelInfo enrollmentServerGroupModelInfo = nscsModelInfos.get(enrollmentServerGroupMo.type());
        final NscsModelInfo enrollmentServerModelInfo = nscsModelInfos.get(enrollmentServerMo.type());

        String nodeCredentialName = null;

        if (nodeCredentialFdn != null && !nodeCredentialFdn.isEmpty()) {
            // The NodeCredential should be "already created"
            nodeCredentialName = nodeCredentialMo.extractName(nodeCredentialFdn);
            nscsLogger.debug(task, "NodeCredential [" + nodeCredentialFdn + "] should be already created");
            nodeCredentialMoObj = readerService.getMoObjectByFdn(nodeCredentialFdn);
            if (nodeCredentialMoObj == null) {
                final String errorMessage = "NodeCredential [" + nodeCredentialFdn + "] not found for certificate type [" + certificateType + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new MissingMoException(nodeName, nodeCredentialMo.type());
            }
            nscsLogger.info(task, "Already created NodeCredential [" + nodeCredentialFdn + "]");
        } else {
            // The NodeCredential should be "not yet created".
            // Check if it is already present as result of a previously failed
            // enrollment
            nodeCredentialName = comEcimMoNaming.getDefaultName(nodeCredentialModelInfo.getName(), certificateType, normalizableNodeRef);
            nodeCredentialFdn = nodeCredentialMo.getFdnByParentFdn(certMFdn, nodeCredentialName);
            nscsLogger.debug(task, "NodeCredential [" + nodeCredentialFdn + "] should be not yet created");
            nodeCredentialMoObj = readerService.getMoObjectByFdn(nodeCredentialFdn);
            if (nodeCredentialMoObj == null) {
                isToBeCreatedNodeCredential = true;
                nscsLogger.info(task, "Not yet created NodeCredential [" + nodeCredentialFdn + "]");
            } else {
                isNodeCredentialAlreadyCreatedWithoutCredentialUser = true;
                nscsLogger.info(task, "NodeCredential [" + nodeCredentialFdn + "] already created without credential user");
            }
        }

        if (isReissue) {
            if (isToBeCreatedNodeCredential || isNodeCredentialAlreadyCreatedWithoutCredentialUser) {
                final String errorMessage = "NodeCredential [" + nodeCredentialFdn + "] not correctly created on reissue";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new MissingMoException(nodeName, nodeCredentialMo.type());
            }
        }

        // Extract task parameters and convert to Security Service format
        final String enrollmentModeFromTask = task.getEnrollmentMode();
        EnrollmentMode enrollmentMode = null;
        if (outputParams != null) {
            final String enrollmentModeFromParams = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString());
            if (enrollmentModeFromParams != null && !enrollmentModeFromParams.isEmpty()) {
                enrollmentMode = EnrollmentMode.valueOf(enrollmentModeFromParams);
            }
        }
        if (enrollmentMode == null) {
            enrollmentMode = nscsNodeUtility.getEnrollmentMode(enrollmentModeFromTask, normalizableNodeRef);
        }
        nscsLogger.info(task, "enrollmentMode [" + enrollmentMode.name() + "]");

        final String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
        final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
        if (enrollmentInfo == null) {
            final String errorMessage = "Missing enrollment info internal parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Extract parameters from Enrollment Info
        final String nodeCredentialSubjectName = enrollmentInfo.getDistinguishedName();
        nscsLogger.info(task, "From enrollment info : subjectName [" + nodeCredentialSubjectName + "]");
        final String nodeCredentialKeyInfo = NscsPkiUtils.convertAlgorithmNamesToNodeSupportedFormat(comEcimNodeUtility.getKeySizeFromEnrollmentInfo(enrollmentInfo));
        nscsLogger.info(task, "From enrollment info : keyInfo [" + nodeCredentialKeyInfo + "]");

        String nodeCredentialSubjectAltName = null;
        if (isExternalCa) {
            final SubjectAltNameStringType subjectAltName = (SubjectAltNameStringType) enrollmentInfo.getSubjectAltName();
            SubjectAltNameFormat subjectAltNameType = enrollmentInfo.getSubjectAltNameType();
            nodeCredentialSubjectAltName = nscsNodeUtility.prepareNodeCredentialSubjectAltName(subjectAltName.getValue(),
                    subjectAltNameType.name());
            nscsLogger.info(task, "External CA subjectAltName [" + nodeCredentialSubjectAltName + "]");
        }

        final String enrollmentAuthorityDn = CertDetails.getBcX500Name(enrollmentInfo.getCertificateAuthorityDn());
        nscsLogger.info(task, "From enrollment info : certificateAuthorityDn [" + enrollmentAuthorityDn + "]");

        // TODO_ENM_PKI_ROOT_CA
        // byte[] caFingerprint = enrollmentInfo.getServerCertFingerPrint();
        String enrollmentAuthorityFingerprint = null;
        final Map<String, String> enrollmentCAAuthorizationModes = capabilityService.getEnrollmentCAAuthorizationModes(normalizableNodeRef);
        if (capabilityService.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, certificateType)) {
            final byte[] caFingerprint = enrollmentInfo.getPkiRootCertFingerPrint();
            enrollmentAuthorityFingerprint = CertSpec.bytesToHex(caFingerprint);
            nscsLogger.info(task,
                    "From enrollment info : serverCertFingerprint [" + enrollmentAuthorityFingerprint + "]. It is the PKI_Root_CA fingerprint!");
        }

        final String enrollmentServerUrl = enrollmentInfo.getServerURL();
        nscsLogger.info(task, "From enrollment info : enrollmentServerUrl [" + enrollmentServerUrl + "]");

        boolean isValidNodeCredential = false;
        String currentNodeCredentialFdn = null;
        List<String> reservedByUser = new ArrayList<>();
        String certificateState = null;
        String actualRenewalMode = null;
        String enrollmentAuthorityName = null;
        String enrollmentAuthorityFdn = null;
        String enrollmentServerGroupName = null;
        String enrollmentServerGroupFdn = null;
        String enrollmentServerName = null;
        String enrollmentServerFdn = null;
        final String enrollmentProtocol = EnrollmentProtocol.fromEnrollmentMode(enrollmentMode).name();
        final boolean isDeprecatedEnrollmentAuthorityUsed = capabilityService.isDeprecatedEnrollmentAuthorityUsed(normalizableNodeRef);
        nscsLogger.info(task, "From capability : isDeprecatedEnrollmentAuthorityUsed [" + isDeprecatedEnrollmentAuthorityUsed + "]");
        final boolean isDeprecatedAuthorityTypeSupported = capabilityService.isDeprecatedAuthorityTypeSupported(normalizableNodeRef);
        nscsLogger.info(task, "From capability : isDeprecatedAuthorityTypeSupported [" + isDeprecatedAuthorityTypeSupported + "]");
        final String authorityType = (isDeprecatedAuthorityTypeSupported ? EnrollmentAuthority.AuthorityType.REGISTRATION_AUTHORITY.name() : null);

        final Map<String, String> nodeCredentialMap = new HashMap<>();
        nodeCredentialMap.put(NODE_CREDENTIAL_SUBJECT_NAME, nodeCredentialSubjectName);
        nodeCredentialMap.put(NODE_CREDENTIAL_SUBJECT_ALT_NAME, nodeCredentialSubjectAltName);
        nodeCredentialMap.put(NODE_CREDENTIAL_FDN, nodeCredentialFdn);
        nodeCredentialMap.put(NODE_CREDENTIAL_KEY_INFO, nodeCredentialKeyInfo);

        if (!isToBeCreatedNodeCredential) {

            // The NodeCredential has been already created.
            // Consistency check on subject name of NodeCredential.
            final String actualNodeCredentialSubjectName = (String) nodeCredentialMoObj.getAttribute(NodeCredential.SUBJECT_NAME);

            final String subjectNameMsg = "actual [" + actualNodeCredentialSubjectName + "] : expected [" + nodeCredentialSubjectName
                    + "] : NodeCredential [" + nodeCredentialFdn + "]";
            if (nodeCredentialSubjectName != null) {
                if (!CertDetails.matchesNotAlignedToRfcDN(nodeCredentialSubjectName, actualNodeCredentialSubjectName)) {
                    /**
                     * Subject name DN doesn't match. Check if it should partially match: this covers following pRBS behavior: actual subjectName on
                     * NodeCredential MO could be (if node hasn't been auto-integrated) something like "C=SE,O=Ericsson" while the subject name coming
                     * from PKI is something like "CN=C829930736.Ericsson.SE,C=SE,O=Ericsson"
                     */
                    final boolean isConfiguredSubjectNameUsedForEnrollment = capabilityService
                            .isConfiguredSubjectNameUsedForEnrollment(normalizableNodeRef);
                    nscsLogger.info(task,
                            "From capability : isConfiguredSubjectNameUsedForEnrollment [" + isConfiguredSubjectNameUsedForEnrollment + "]");
                    if (!isConfiguredSubjectNameUsedForEnrollment
                            && CertDetails.partiallyMatchesNotAlignedToRfcDN(actualNodeCredentialSubjectName, nodeCredentialSubjectName)) {
                        /**
                         * pRBS-like behavior: the subject names partially match
                         */
                        final String infoMsg = "subject names partially match : " + subjectNameMsg;
                        nscsLogger.info(task, infoMsg);
                    } else {
                        /**
                         * Subject name changes but correspondent attribute in current NodeCredential MO can't be modified since it is immutable. So a
                         * new NodeCredential MO referencing the same EnrollmentAuthority and EnrollmentServerGroup MOs shall be created. The current
                         * NodeCredential MO is not automatically deleted but all MOs referencing it shall be updated to reference the new
                         * NodeCredential MO. So its current FDN shall be saved and passed to following workflows.
                         */
                        currentNodeCredentialFdn = nodeCredentialFdn;
                        final String currentNodeCredentialMsg = "Current NodeCredential FDN [" + currentNodeCredentialFdn + "] ";
                        nscsLogger.info(task, currentNodeCredentialMsg);
                        reservedByUser = nodeCredentialMoObj.getAttribute(NodeCredential.RESERVED_BY_USER);
                        if (reservedByUser == null || reservedByUser.isEmpty()) {
                            nscsLogger.error(task, currentNodeCredentialMsg + "has null or empty reservedByUser attribute!!!");
                        } else {
                            nscsLogger.info(task,
                                    currentNodeCredentialMsg + "has reservedByUser attribute with size [" + reservedByUser.size() + "]");
                        }

                        final String infoMsg = "Immutable subjectName attribute cannot be changed : " + subjectNameMsg;
                        nscsLogger.workFlowTaskHandlerOngoing(task, infoMsg);
                    }
                } else if (!nodeCredentialSubjectName.equals(actualNodeCredentialSubjectName)) {
                    // Subject name differs only for order of fields
                    final String infoMsg = "subjectName differs for order : " + subjectNameMsg;
                    nscsLogger.info(task, infoMsg);
                }
            } else {
                final String infoMsg = "Null subjectName from PKI : " + subjectNameMsg;
                nscsLogger.info(task, infoMsg);
            }

            // Consistency check on EnrollmentServerGroup and EnrollmentServer
            enrollmentServerGroupFdn = (String) nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_SERVER_GROUP);
            if (enrollmentServerGroupFdn == null) {
                // Already existent NodeCredential without referenced
                // EnrollmentServerGroup. Coming from offline enrollment?
                // Create EnrollmentServerGroup
                nscsLogger.info(task, "NodeCredential [" + nodeCredentialFdn + "] for certificate type [" + certificateType
                        + "] coming from offline enrollment: create EnrollmentServerGroup");
                enrollmentServerGroupName = createEnrollmentServerGroup(task, certMFdn, enrollmentServerGroupModelInfo, certificateType);
                enrollmentServerGroupFdn = enrollmentServerGroupMo.getFdnByParentFdn(certMFdn, enrollmentServerGroupName);
            } else {
                nscsLogger.info(task, "Getting already created EnrollmentServerGroup [" + enrollmentServerGroupFdn + "]");
                final MoObject enrollmentServerGroupMoObj = readerService.getMoObjectByFdn(enrollmentServerGroupFdn);
                if (enrollmentServerGroupMoObj == null) {
                    final String errorMessage = "EnrollmentServerGroup [" + enrollmentServerGroupFdn + "] not found";
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new MissingMoException(nodeName, enrollmentServerGroupMo.type());
                }
                enrollmentServerGroupName = enrollmentServerGroupMo.extractName(enrollmentServerGroupFdn);
                nscsLogger.debug(task, "EnrollmentServerGroup [" + enrollmentServerGroupFdn + "] does not require update");
            }

            // Check EnrollmentServer
            String actualUri = null;
            String actualEnrollmentAuthorityInEnrollmentServer = null;
            String actualEnrollmentInterface = null;
            final StringBuilder sbUri = new StringBuilder();
            final StringBuilder sbEnrollmentInterface = (isExternalCa ? new StringBuilder() : null);
            final StringBuilder sbEnrollmentAuthorityInEnrollmentServer = (isDeprecatedEnrollmentAuthorityUsed ? new StringBuilder() : null);
            enrollmentServerFdn = checkEnrollmentServer(task, enrollmentServerGroupFdn, enrollmentServerMo, enrollmentProtocol, sbUri,
                    sbEnrollmentAuthorityInEnrollmentServer, sbEnrollmentInterface);
            if (enrollmentServerFdn != null) {
                actualUri = sbUri.toString();
                actualEnrollmentInterface = (sbEnrollmentInterface != null ? sbEnrollmentInterface.toString() : null);
                actualEnrollmentAuthorityInEnrollmentServer = (sbEnrollmentAuthorityInEnrollmentServer != null
                        ? sbEnrollmentAuthorityInEnrollmentServer.toString() : null);
                if (actualEnrollmentAuthorityInEnrollmentServer != null && actualEnrollmentAuthorityInEnrollmentServer.isEmpty()) {
                    actualEnrollmentAuthorityInEnrollmentServer = null;
                }
                nscsLogger.info(task,
                        "Already existent EnrollmentServer[" + enrollmentServerFdn + "] for protocol[" + enrollmentProtocol + "] with uri["
                                + actualUri + "] enrollmentInterface[" + actualEnrollmentInterface + "] enrollmentAuthority["
                                + actualEnrollmentAuthorityInEnrollmentServer + "]");
            }

            // Consistency check on EnrollmentAuthority
            if (actualEnrollmentAuthorityInEnrollmentServer != null) {
                enrollmentAuthorityFdn = actualEnrollmentAuthorityInEnrollmentServer;
                nscsLogger.info(task, "EnrollmentAuthority FDN [" + enrollmentAuthorityFdn
                        + "] set from enrollmentAuthority attribute of EnrollmentServer [" + enrollmentServerFdn + "]");
            } else {
                enrollmentAuthorityFdn = (String) nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_AUTHORITY);
                nscsLogger.info(task, "EnrollmentAuthority FDN [" + enrollmentAuthorityFdn
                        + "] set from enrollmentAuthority attribute of NodeCredential [" + nodeCredentialFdn + "]");
            }

            if (enrollmentAuthorityFdn == null) {
                // Already existent NodeCredential without referenced
                // EnrollmentAuthority. Coming from offline enrollment?
                // Create EnrollmentAuthority
                nscsLogger.info(task, "NodeCredential [" + nodeCredentialFdn + "] for certificate type [" + certificateType
                        + "] coming from offline enrollment: create EnrollmentAuthority");
                enrollmentAuthorityName = createEnrollmentAuthority(task, certMFdn, enrollmentAuthorityModelInfo, certificateType,
                        enrollmentAuthorityDn, enrollmentAuthorityFingerprint, enrollmentAuthorityCertificate, authorityType,
                        enrollmentCAAuthorizationModes);
                enrollmentAuthorityFdn = enrollmentAuthorityMo.getFdnByParentFdn(certMFdn, enrollmentAuthorityName);
            } else {
                nscsLogger.debug(task, "Getting already created EnrollmentAuthority [" + enrollmentAuthorityFdn + "]");
                final MoObject enrollmentAuthorityMoObj = readerService.getMoObjectByFdn(enrollmentAuthorityFdn);
                if (enrollmentAuthorityMoObj == null) {
                    final String errorMessage = "EnrollmentAuthority [" + enrollmentAuthorityFdn + "] not found";
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new MissingMoException(nodeName, enrollmentAuthorityMo.type());
                }

                // Check and update (if needed) EnrollmentAuthority
                final boolean isEnrollmentAuthorityUpdateNeeded = checkAndUpdateEnrollmentAuthority(task, enrollmentAuthorityFdn,
                        enrollmentAuthorityMoObj, certificateType, enrollmentAuthorityDn, enrollmentAuthorityFingerprint,
                        enrollmentAuthorityCertificate, enrollmentCAAuthorizationModes);
                enrollmentAuthorityName = enrollmentAuthorityMo.extractName(enrollmentAuthorityFdn);
                nscsLogger.debug(task, "EnrollmentAuthority [" + enrollmentAuthorityFdn + "] : isEnrollmentAuthorityUpdateNeeded ["
                        + isEnrollmentAuthorityUpdateNeeded + "]");
            }

            // Create or update EnrollmentServer
            if (enrollmentServerFdn == null) {
                enrollmentServerName = createEnrollmentServer(task, enrollmentServerGroupFdn, enrollmentServerModelInfo, enrollmentServerUrl,
                        enrollmentProtocol, (isDeprecatedEnrollmentAuthorityUsed ? enrollmentAuthorityFdn : null),
                        getEnrollmentInterfaceValue(task.getInterfaceFdn(), normalizableNodeRef, isExternalCa));
                enrollmentServerFdn = enrollmentServerMo.getFdnByParentFdn(enrollmentServerGroupFdn, enrollmentServerName);
            } else {
                updateEnrollmentServer(task, enrollmentServerFdn, enrollmentServerUrl, enrollmentProtocol,
                        (isDeprecatedEnrollmentAuthorityUsed ? enrollmentAuthorityFdn : null),
                        getEnrollmentInterfaceValue(task.getInterfaceFdn(), normalizableNodeRef, isExternalCa));
            }

            // Get current renewalMode
            actualRenewalMode = (String) nodeCredentialMoObj.getAttribute(NodeCredential.RENEWAL_MODE);

            // Check and update NodeCredential (if needed) or create a new
            // NodeCredential MO.

            if (currentNodeCredentialFdn == null) {
                // Update current NodeCredential (if needed).
                final boolean isNodeCredentialUpdateNeeded = checkAndUpdateNodeCredential(task, nodeCredentialMoObj, nodeCredentialMap,
                        enrollmentAuthorityFdn, enrollmentServerGroupFdn);
                nodeCredentialName = nodeCredentialMo.extractName(nodeCredentialFdn);
                nscsLogger.debug(task,
                        "NodeCredential [" + nodeCredentialFdn + "] : isNodeCredentialUpdateNeeded [" + isNodeCredentialUpdateNeeded + "]");

                // Get certificate state
                certificateState = (String) nodeCredentialMoObj.getAttribute(NodeCredential.CERTIFICATE_STATE);
                isValidNodeCredential = true;
                nscsLogger.info(task,
                        "isReissue [" + isReissue + "] : NodeCredential [" + nodeCredentialFdn + "] in valid state [" + certificateState + "]");
            } else {
                // Create a new NodeCredential MO due to subjectName change
                nscsLogger.info(task, "Creating new NodeCredential MO due to subjectName change for certType [" + certificateType + "]");
                final String toBeCreatedNodeCredentialName = comEcimMoNaming.getFirstAvailableName(nodeCredentialModelInfo.getName(),
                        nodeCredentialModelInfo.getNamespace(), certMFdn);
                nscsLogger.info(task, "Using first available name [" + toBeCreatedNodeCredentialName + "]");
                nodeCredentialName = createNodeCredential(task, certMFdn, toBeCreatedNodeCredentialName, nodeCredentialModelInfo,
                        nodeCredentialMap, enrollmentAuthorityFdn, enrollmentServerGroupFdn);

                nodeCredentialFdn = nodeCredentialMo.getFdnByParentFdn(certMFdn, nodeCredentialName);
                isValidNodeCredential = true;
                nscsLogger.info(task, "Successfully created new NodeCredential [" + nodeCredentialFdn + "] in valid state [CREATED]");
            }
        } else {
            // Not yet created NodeCredential hierarchy
            nscsLogger.info(task, "Not yet created NodeCredential hierarchy for certificate type [" + certificateType + "]");

            // Create EnrollmentAuthority
            enrollmentAuthorityName = createEnrollmentAuthority(task, certMFdn, enrollmentAuthorityModelInfo, certificateType, enrollmentAuthorityDn,
                    enrollmentAuthorityFingerprint, enrollmentAuthorityCertificate, authorityType, enrollmentCAAuthorizationModes);
            enrollmentAuthorityFdn = enrollmentAuthorityMo.getFdnByParentFdn(certMFdn, enrollmentAuthorityName);

            // Create EnrollmentServerGroup
            enrollmentServerGroupName = createEnrollmentServerGroup(task, certMFdn, enrollmentServerGroupModelInfo, certificateType);
            enrollmentServerGroupFdn = enrollmentServerGroupMo.getFdnByParentFdn(certMFdn, enrollmentServerGroupName);

            // Create EnrollmentServer
            enrollmentServerName = createEnrollmentServer(task, enrollmentServerGroupFdn, enrollmentServerModelInfo, enrollmentServerUrl,
                    enrollmentProtocol, (isDeprecatedEnrollmentAuthorityUsed ? enrollmentAuthorityFdn : null),
                    getEnrollmentInterfaceValue(task.getInterfaceFdn(), normalizableNodeRef, isExternalCa));

            // Create NodeCredential
            actualRenewalMode = RenewalMode.AUTOMATIC.name();
            nscsLogger.info(task,
                    "RenewalMode will be set to [" + actualRenewalMode + "] after NodeCredential create and successful online enrollment");
            /**
             * The NodeCredential creation should occur only with NetSim at very first enrollment. In real use case with real node, such MO is already
             * created (either by AutoProvisioning or by manual offline enrollment or after migration from OSS-RC).
             */
            final String toBeCreatedNodeCredentialName = comEcimMoNaming.getDefaultName(nodeCredentialModelInfo.getName(), certificateType,
                    normalizableNodeRef);
            nscsLogger.info(task, "Using default name [" + toBeCreatedNodeCredentialName + "]");
            nodeCredentialName = createNodeCredential(task, certMFdn, toBeCreatedNodeCredentialName, nodeCredentialModelInfo, nodeCredentialMap,
                    enrollmentAuthorityFdn, enrollmentServerGroupFdn);

            nodeCredentialFdn = nodeCredentialMo.getFdnByParentFdn(certMFdn, nodeCredentialName);
            isValidNodeCredential = true;
            nscsLogger.info(task, "Successfully created NodeCredential [" + nodeCredentialFdn + "] in valid state [CREATED]");
        }

        if (isValidNodeCredential) {
            return nodeCredentialIsValid(task, nodeCredentialFdn, currentNodeCredentialFdn, reservedByUser, actualRenewalMode, enrollmentMode.name(),
                    enrollmentInfo, outputParams);
        } else {
            return nodeCredentialIsNotValid(task, nodeCredentialFdn, currentNodeCredentialFdn, reservedByUser, actualRenewalMode, certificateState,
                    enrollmentMode.name(), enrollmentInfo, outputParams);
        }
    }

    /**
     * Check whether given NodeCredential MO's current attributes differ from the given ones. If at least one attribute differs, update the MO.
     *
     * @param task
     * @param nodeCredentialMoObj
     * @param nodeCredentialMap
     * @param enrollmentAuthorityFdn
     * @param enrollmentServerGroupFdn
     * @return true if update occurs, false otherwise
     */
    private boolean checkAndUpdateNodeCredential(final ComEcimCheckNodeCredentialTask task, final MoObject nodeCredentialMoObj,
            final Map<String, String> nodeCredentialMap, final String enrollmentAuthorityFdn, final String enrollmentServerGroupFdn) {

        boolean isNodeCredentialUpdateNeeded = false;
        final NscsCMWriterService.WriterSpecificationBuilder nodeCredentialSpec = writerService.withSpecification();
        final String actualEnrollmentAuthority = (String) nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_AUTHORITY);
        if (enrollmentAuthorityFdn != null && !enrollmentAuthorityFdn.equals(actualEnrollmentAuthority)) {
            nodeCredentialSpec.setNotNullAttribute(NodeCredential.ENROLLMENT_AUTHORITY, enrollmentAuthorityFdn);
            isNodeCredentialUpdateNeeded = true;
            nscsLogger.info(task, "enrollmentAuthority changes from [" + actualEnrollmentAuthority + "] to [" + enrollmentAuthorityFdn + "]");
        }
        final String actualEnrollmentServerGroup = (String) nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_SERVER_GROUP);
        if (enrollmentServerGroupFdn != null && !enrollmentServerGroupFdn.equals(actualEnrollmentServerGroup)) {
            nodeCredentialSpec.setNotNullAttribute(NodeCredential.ENROLLMENT_SERVER_GROUP, enrollmentServerGroupFdn);
            isNodeCredentialUpdateNeeded = true;
            nscsLogger.info(task, "enrollmentServerGroup changes from [" + actualEnrollmentServerGroup + "] to [" + enrollmentServerGroupFdn + "]");
        }
        final String nodeCredentialKeyInfo = nodeCredentialMap.get(NODE_CREDENTIAL_KEY_INFO);
        final String actualNodeCredentialKeyInfo = (String) nodeCredentialMoObj.getAttribute(NodeCredential.KEY_INFO);
        if (nodeCredentialKeyInfo != null && !nodeCredentialKeyInfo.equals(actualNodeCredentialKeyInfo)) {
            nodeCredentialSpec.setNotNullAttribute(NodeCredential.KEY_INFO, nodeCredentialKeyInfo);
            isNodeCredentialUpdateNeeded = true;
            nscsLogger.info(task, "keyInfo changes from [" + actualNodeCredentialKeyInfo + "] to [" + nodeCredentialKeyInfo + "]");
        }
        // TODO NodeCredential.RENEWAL_MODE
        final String currentRenewalMode = (String) nodeCredentialMoObj.getAttribute(NodeCredential.RENEWAL_MODE);
        final String expectedRenewalMode = RenewalMode.MANUAL.name();
        if (!expectedRenewalMode.equals(currentRenewalMode)) {
            nodeCredentialSpec.setNotNullAttribute(NodeCredential.RENEWAL_MODE, expectedRenewalMode);
            isNodeCredentialUpdateNeeded = true;
            nscsLogger.info(task, "renewalMode changes from [" + currentRenewalMode + "] to [" + expectedRenewalMode + "]");
        }

        final String nodeCredentialSubjectAltName = nodeCredentialMap.get(NODE_CREDENTIAL_SUBJECT_ALT_NAME);
        if (nodeCredentialSubjectAltName != null) {
            final String currentSubjectAltName = nodeCredentialMoObj.getAttribute(NodeCredential.SUBJECT_ALT_NAME);
            if (!nodeCredentialSubjectAltName.equals(currentSubjectAltName)) {
                nodeCredentialSpec.setNotNullAttribute(NodeCredential.SUBJECT_ALT_NAME, nodeCredentialSubjectAltName);
                isNodeCredentialUpdateNeeded = true;
                nscsLogger.info(task, "subjectAltName changes from [" + currentSubjectAltName + "] to [" + nodeCredentialSubjectAltName + "]");
            }
        }

        final String nodeCredentialFdn = nodeCredentialMap.get(NODE_CREDENTIAL_FDN);
        if (isNodeCredentialUpdateNeeded) {
            nodeCredentialSpec.setFdn(nodeCredentialFdn);
            // Update NodeCredential
            final String updateMessage = NscsLogger.stringifyUpdateParams("NodeCredential", nodeCredentialFdn);
            nscsLogger.info(task, "Updating " + updateMessage);
            try {
                nodeCredentialSpec.updateMO();
            } catch (final Exception e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
        }
        return isNodeCredentialUpdateNeeded;
    }

    /**
     * Check whether, under given EnrollmentServerGroup MO, an EnrollmentServer MO with given value of 'protocol' attribute can be found. If such MO
     * is present, return as output parameters the values of its 'uri' and 'enrollmentAuthority' attributes.
     *
     * @param task
     * @param enrollmentServerGroupFdn
     * @param enrollmentServerMo
     * @param enrollmentProtocol
     * @param sbUri
     *            output parameter containing wanted MO's 'uri' attribute value. If null the attribute shall neither be read.
     * @param sbEnrollmentAuthority
     *            output parameter containing wanted MO's 'enrollmentAuthority' attribute value. If null the attribute shall neither be read.
     * @param sbEnrollmentInterface
     *            output parameter containing wanted MO's 'enrollmentInterface' attribute value. If null the attribute shall neither be read.
     * @return the FDN of wanted EnrollmentServer MO if found, null otherwise
     *
     */
    private String checkEnrollmentServer(final ComEcimCheckNodeCredentialTask task, final String enrollmentServerGroupFdn,
            final Mo enrollmentServerMo, final String enrollmentProtocol, final StringBuilder sbUri, final StringBuilder sbEnrollmentAuthority,
            final StringBuilder sbEnrollmentInterface) {

        String enrollmentServerFdn = null;
        final List<String> attrs = new ArrayList<String>();
        attrs.add(EnrollmentServer.PROTOCOL);
        if (sbUri != null) {
            attrs.add(EnrollmentServer.URI);
        }
        if (sbEnrollmentInterface != null) {
            attrs.add(EnrollmentServer.ENROLLMENT_INTERFACE);
        }
        if (sbEnrollmentAuthority != null) {
            attrs.add(EnrollmentServer.ENROLLMENT_AUTHORITY);
        }
        final String[] requestedAttrs = attrs.toArray(new String[0]);
        final String readMessage = NscsLogger.stringifyReadParams(enrollmentServerGroupFdn, enrollmentServerMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        try {
            final CmResponse enrollmentServerResponse = readerService.getMos(enrollmentServerGroupFdn, enrollmentServerMo.type(),
                    enrollmentServerMo.namespace(), requestedAttrs);
            if (enrollmentServerResponse != null && enrollmentServerResponse.getCmObjects() != null
                    && enrollmentServerResponse.getCmObjects().size() >= 1) {
                for (final CmObject enrollmentServerCmObj : enrollmentServerResponse.getCmObjects()) {
                    final String actualProtocol = (String) enrollmentServerCmObj.getAttributes().get(EnrollmentServer.PROTOCOL);
                    if (enrollmentProtocol != null && !enrollmentProtocol.equals(actualProtocol)) {
                        continue;
                    }
                    // Found enrollment server with expected protocol
                    enrollmentServerFdn = enrollmentServerCmObj.getFdn();
                    nscsLogger.info(task, "Found EnrollmentServer[" + enrollmentServerFdn + "] for protocol[" + enrollmentProtocol
                            + "] under EnrollmentServerGroup[" + enrollmentServerGroupFdn + "]");

                    if (sbUri != null) {
                        final String actualUri = (String) enrollmentServerCmObj.getAttributes().get(EnrollmentServer.URI);
                        nscsLogger.info(task, "uri[" + actualUri + "]");
                        if (actualUri != null) {
                            sbUri.delete(0, sbUri.length());
                            sbUri.append(actualUri);
                        }
                    }

                    if (sbEnrollmentInterface != null) {
                        final String actualEnrollmentInterface = (String) enrollmentServerCmObj.getAttributes().get(EnrollmentServer.ENROLLMENT_INTERFACE);
                        nscsLogger.info(task, "enrollmentInterface[" + actualEnrollmentInterface + "]");
                        if (actualEnrollmentInterface != null) {
                            sbEnrollmentInterface.delete(0, sbEnrollmentInterface.length());
                            sbEnrollmentInterface.append(actualEnrollmentInterface);
                        }
                    }

                    if (sbEnrollmentAuthority != null) {
                        final String actualEnrollmentAuthority = (String) enrollmentServerCmObj.getAttributes()
                                .get(EnrollmentServer.ENROLLMENT_AUTHORITY);
                        nscsLogger.info(task, "enrollmentAuthority [" + actualEnrollmentAuthority + "]");
                        if (actualEnrollmentAuthority != null) {
                            sbEnrollmentAuthority.delete(0, sbEnrollmentAuthority.length());
                            sbEnrollmentAuthority.append(actualEnrollmentAuthority);
                        }
                    }
                }
            } else {
                nscsLogger.info(task, "No EnrollmentServer MOs under enrollmentServerGroup [" + enrollmentServerGroupFdn + "]");
            }
        } catch (DataAccessSystemException | DataAccessException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        return enrollmentServerFdn;
    }

    /**
     * Update given EnrollmentServer MO with given values of 'uri', 'protocol' and 'enrollmentAuthority' attributes.
     *
     * @param task
     * @param enrollmentServerFdn
     * @param uri
     * @param protocol
     * @param enrollmentAuthority
     * @param enrollmentInterface
     * @throws UnexpectedErrorException
     */
    private void updateEnrollmentServer(final ComEcimCheckNodeCredentialTask task, final String enrollmentServerFdn, final String uri,
            final String protocol, final String enrollmentAuthority, final String enrollmentInterface) {
        final NscsCMWriterService.WriterSpecificationBuilder enrollmentServerSpec = writerService.withSpecification();
        enrollmentServerSpec.setFdn(enrollmentServerFdn);
        enrollmentServerSpec.setNotNullAttribute(EnrollmentServer.URI, uri);
        enrollmentServerSpec.setNotNullAttribute(EnrollmentServer.PROTOCOL, protocol);
        enrollmentServerSpec.setNotNullAttribute(EnrollmentServer.ENROLLMENT_INTERFACE, enrollmentInterface);

        if (enrollmentAuthority != null) {
            enrollmentServerSpec.setNotNullAttribute(EnrollmentServer.ENROLLMENT_AUTHORITY, enrollmentAuthority);
        }
        final String updateMessage = NscsLogger.stringifyUpdateParams("EnrollmentServer", enrollmentServerFdn);
        nscsLogger.info(task, "Updating " + updateMessage);
        try {
            enrollmentServerSpec.updateMO();
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
    }

    /**
     * Check whether given EnrollmentAuthority MO's current attributes differ from the given ones. If at least one attribute differs, update the MO.
     *
     * @param task
     * @param enrollmentAuthorityFdn
     * @param enrollmentAuthorityMoObj
     * @param enrollmentAuthorityDn
     * @param enrollmentAuthorityFingerprint
     * @param enrollmentCAAuthorizationModes
     * @return true if update occurs, false otherwise
     * @throws UnexpectedErrorException
     */
    private boolean checkAndUpdateEnrollmentAuthority(final ComEcimCheckNodeCredentialTask task, final String enrollmentAuthorityFdn,
            final MoObject enrollmentAuthorityMoObj, final String certificateType, final String enrollmentAuthorityDn,
            final String enrollmentAuthorityFingerprint, final String wantedTrustCertificateFdn,
            final Map<String, String> enrollmentCAAuthorizationModes) throws UnexpectedErrorException {

        boolean isEnrollmentAuthorityUpdateNeeded = false;
        final NscsCMWriterService.WriterSpecificationBuilder enrollmentAuthoritySpec = writerService.withSpecification();
        final String actualEnrollmentAuthorityDn = (String) enrollmentAuthorityMoObj.getAttribute(EnrollmentAuthority.ENROLLMENT_AUTHORITY_NAME);
        if (enrollmentAuthorityDn != null && !enrollmentAuthorityDn.equals(actualEnrollmentAuthorityDn)) {
            enrollmentAuthoritySpec.setNotNullAttribute(EnrollmentAuthority.ENROLLMENT_AUTHORITY_NAME, enrollmentAuthorityDn);
            isEnrollmentAuthorityUpdateNeeded = true;
            nscsLogger.info(task, "enrollmentAuthorityDn changes from [" + actualEnrollmentAuthorityDn + "] to [" + enrollmentAuthorityDn + "]");
        }
        if (capabilityService.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, certificateType)) {
            final String actualEnrollmentAuthorityFingerprint = (String) enrollmentAuthorityMoObj
                    .getAttribute(EnrollmentAuthority.ENROLLMENT_CA_FINGERPRINT);
            if (enrollmentAuthorityFingerprint != null && !enrollmentAuthorityFingerprint.equalsIgnoreCase(actualEnrollmentAuthorityFingerprint)) {
                enrollmentAuthoritySpec.setNotNullAttribute(EnrollmentAuthority.ENROLLMENT_CA_FINGERPRINT, enrollmentAuthorityFingerprint);
                isEnrollmentAuthorityUpdateNeeded = true;
                nscsLogger.info(task, "enrollmentAuthorityFingerprint changes from [" + actualEnrollmentAuthorityFingerprint + "] to ["
                        + enrollmentAuthorityFingerprint + "]");
            }
        }
        if (wantedTrustCertificateFdn != null) {
            nscsLogger.info(task, "From output params : enrollment CA trusted certificate FDN [" + wantedTrustCertificateFdn + "]");
            if (capabilityService.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes, certificateType)
                    || capabilityService.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes, certificateType)) {

                final String actualEnrollmentAuthorityCertificateFdn = (String) enrollmentAuthorityMoObj
                        .getAttribute(EnrollmentAuthority.ENROLLMENT_CA_CERTIFICATE);
                if ((actualEnrollmentAuthorityCertificateFdn == null) || !wantedTrustCertificateFdn.equals(actualEnrollmentAuthorityCertificateFdn)) {
                    enrollmentAuthoritySpec.setNotNullAttribute(EnrollmentAuthority.ENROLLMENT_CA_CERTIFICATE, wantedTrustCertificateFdn);
                    isEnrollmentAuthorityUpdateNeeded = true;
                    nscsLogger.info("enrollmentAuthorityCaCertificate changes from [" + actualEnrollmentAuthorityCertificateFdn + "] to ["
                            + wantedTrustCertificateFdn + "]");
                }
            }
        }
        if (isEnrollmentAuthorityUpdateNeeded) {
            enrollmentAuthoritySpec.setFdn(enrollmentAuthorityFdn);
            // Update EnrollmentAuthority
            final String updateMessage = NscsLogger.stringifyUpdateParams("EnrollmentAuthority", enrollmentAuthorityFdn);
            nscsLogger.info(task, "Updating " + updateMessage);
            try {
                enrollmentAuthoritySpec.updateMO();
            } catch (final Exception e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
        }
        return isEnrollmentAuthorityUpdateNeeded;
    }

    /**
     * Create, under given CertM MO, an EnrollmentAuthority MO with name dependent on the given certificate type and with given values of
     * 'enrollmentAuthorityName' and 'enrollmentCaFingerprint' attributes.
     *
     * @param task
     * @param certMFdn
     * @param enrollmentAuthorityModelInfo
     * @param certificateType
     * @param enrollmentAuthorityDn
     * @param enrollmentAuthorityFingerprint
     * @param authorityType
     * @param enrollmentCAAuthorizationModes
     * @return the name of created MO
     * @throws UnexpectedErrorException
     */
    private String createEnrollmentAuthority(final ComEcimCheckNodeCredentialTask task, final String certMFdn,
            final NscsModelInfo enrollmentAuthorityModelInfo, final String certificateType, final String enrollmentAuthorityDn,
            final String enrollmentAuthorityFingerprint, final String wantedTrustCertificateFdn, final String authorityType,
            final Map<String, String> enrollmentCAAuthorizationModes) throws UnexpectedErrorException {

        final String enrollmentAuthorityType = enrollmentAuthorityModelInfo.getName();
        final String enrollmentAuthorityNamespace = enrollmentAuthorityModelInfo.getNamespace();
        final String enrollmentAuthorityVersion = enrollmentAuthorityModelInfo.getVersion();
        final NormalizableNodeReference node = null;
        final String enrollmentAuthorityName = comEcimMoNaming.getDefaultName(enrollmentAuthorityType, certificateType, node);
        final Map<String, Object> enrollmentAuthorityAttributes = new HashMap<String, Object>();
        if (authorityType != null) {
            enrollmentAuthorityAttributes.put(ModelDefinition.EnrollmentAuthority.AUTHORITY_TYPE, authorityType);
        }
        enrollmentAuthorityAttributes.put(ModelDefinition.EnrollmentAuthority.ENROLLMENT_AUTHORITY_NAME, enrollmentAuthorityDn);
        if (capabilityService.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, certificateType)) {
            enrollmentAuthorityAttributes.put(ModelDefinition.EnrollmentAuthority.ENROLLMENT_CA_FINGERPRINT, enrollmentAuthorityFingerprint);
        }
        if (wantedTrustCertificateFdn != null) {
            nscsLogger.info(task, "From output params : enrollment CA trusted certificate FDN [" + wantedTrustCertificateFdn + "]");
            if (capabilityService.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes, certificateType)
                    || capabilityService.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes, certificateType)) {
                enrollmentAuthorityAttributes.put(ModelDefinition.EnrollmentAuthority.ENROLLMENT_CA_CERTIFICATE, wantedTrustCertificateFdn);
            }
        }
        final String createMessage = NscsLogger.stringifyCreateParams(certMFdn, enrollmentAuthorityType, enrollmentAuthorityNamespace,
                enrollmentAuthorityVersion, enrollmentAuthorityName, enrollmentAuthorityAttributes);
        nscsLogger.info(task, "Creating " + createMessage);
        try {
            writerService.createMo(certMFdn, enrollmentAuthorityType, enrollmentAuthorityNamespace, enrollmentAuthorityVersion,
                    enrollmentAuthorityName, enrollmentAuthorityAttributes);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while creating " + createMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully created " + createMessage);
        return enrollmentAuthorityName;
    }

    /**
     * Create, under given CertM MO, an EnrollmentServerGroup MO with name dependent on the given certificate type.
     *
     * @param task
     * @param certMFdn
     * @param enrollmentServerGroupModelInfo
     * @param certificateType
     * @return the name of created MO
     * @throws UnexpectedErrorException
     */
    private String createEnrollmentServerGroup(final ComEcimCheckNodeCredentialTask task, final String certMFdn,
            final NscsModelInfo enrollmentServerGroupModelInfo, final String certificateType) throws UnexpectedErrorException {

        final String enrollmentServerGroupType = enrollmentServerGroupModelInfo.getName();
        final String enrollmentServerGroupNamespace = enrollmentServerGroupModelInfo.getNamespace();
        final String enrollmentServerGroupVersion = enrollmentServerGroupModelInfo.getVersion();
        final NormalizableNodeReference node = null;
        final String enrollmentServerGroupName = comEcimMoNaming.getDefaultName(enrollmentServerGroupType, certificateType, node);
        final String createMessage = NscsLogger.stringifyCreateParams(certMFdn, enrollmentServerGroupType, enrollmentServerGroupNamespace,
                enrollmentServerGroupVersion, enrollmentServerGroupName, null);
        nscsLogger.info(task, "Creating " + createMessage);
        try {
            writerService.createMo(certMFdn, enrollmentServerGroupType, enrollmentServerGroupNamespace, enrollmentServerGroupVersion,
                    enrollmentServerGroupName, null);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while creating " + createMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully created " + createMessage);
        return enrollmentServerGroupName;
    }

    /**
     * Create, under given EnrollmentServerGroup MO, an EnrollmentServer MO with name dependent on given enrollment protocol and with given values of
     * 'uri', 'protocol' and 'enrollmentAuthority' attributes.
     *
     * @param task
     * @param enrollmentServerGroupFdn
     * @param enrollmentServerModelInfo
     * @param uri
     * @param protocol
     * @param enrollmentAuthority
     * @param enrollmentInterface
     * @return the name of created MO
     * @throws UnexpectedErrorException
     */
    private String createEnrollmentServer(final ComEcimCheckNodeCredentialTask task, final String enrollmentServerGroupFdn,
            final NscsModelInfo enrollmentServerModelInfo, final String uri, final String protocol, final String enrollmentAuthority,
            final String enrollmentInterface) {

        final String enrollmentServerType = enrollmentServerModelInfo.getName();
        final String enrollmentServerNamespace = enrollmentServerModelInfo.getNamespace();
        final String enrollmentServerVersion = enrollmentServerModelInfo.getVersion();
        final NormalizableNodeReference node = null;
        final String enrollmentServerName = comEcimMoNaming.getDefaultName(enrollmentServerType, protocol, node);
        final Map<String, Object> enrollmentServerAttributes = new HashMap<String, Object>();
        if (uri != null) {
            enrollmentServerAttributes.put(ModelDefinition.EnrollmentServer.URI, uri);
        }
        if (protocol != null) {
            enrollmentServerAttributes.put(ModelDefinition.EnrollmentServer.PROTOCOL, protocol);
        }
        if (enrollmentAuthority != null) {
            enrollmentServerAttributes.put(ModelDefinition.EnrollmentServer.ENROLLMENT_AUTHORITY, enrollmentAuthority);
        }
        if (enrollmentInterface != null) {
            enrollmentServerAttributes.put(EnrollmentServer.ENROLLMENT_INTERFACE, enrollmentInterface);
        }

        final String createMessage = NscsLogger.stringifyCreateParams(enrollmentServerGroupFdn, enrollmentServerType, enrollmentServerNamespace,
                enrollmentServerVersion, enrollmentServerName, enrollmentServerAttributes);
        nscsLogger.info(task, "Creating " + createMessage);
        try {
            writerService.createMo(enrollmentServerGroupFdn, enrollmentServerType, enrollmentServerNamespace, enrollmentServerVersion,
                    enrollmentServerName, enrollmentServerAttributes);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while creating " + createMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully created " + createMessage);
        return enrollmentServerName;
    }

    /**
     * Create a NodeCredential MO.
     *
     * @param task
     * @param certMFdn
     * @param nodeCredentialName
     * @param nodeCredentialModelInfo
     * @param nodeCredentialMap
     * @param enrollmentAuthorityFdn
     * @param enrollmentServerGroupFdn
     * @throws UnexpectedErrorException
     * @return the java.lang.String
     */
    private String createNodeCredential(final ComEcimCheckNodeCredentialTask task, final String certMFdn, final String nodeCredentialName,
            final NscsModelInfo nodeCredentialModelInfo, final Map<String, String> nodeCredentialMap, final String enrollmentAuthorityFdn,
            final String enrollmentServerGroupFdn) {
        final String nodeCredentialType = nodeCredentialModelInfo.getName();
        final String nodeCredentialNamespace = nodeCredentialModelInfo.getNamespace();
        final String nodeCredentialVersion = nodeCredentialModelInfo.getVersion();
        final String currentRenewalMode = RenewalMode.MANUAL.name();
        final Map<String, Object> nodeCredentialAttributes = new HashMap<String, Object>();

        final String nodeCredentialSubjectName = nodeCredentialMap.get(NODE_CREDENTIAL_SUBJECT_NAME);
        nodeCredentialAttributes.put(ModelDefinition.NodeCredential.SUBJECT_NAME, nodeCredentialSubjectName);

        final String nodeCredentialSubjectAltName = nodeCredentialMap.get(NODE_CREDENTIAL_SUBJECT_ALT_NAME);
        if (nodeCredentialSubjectAltName != null) {
            nodeCredentialAttributes.put(ModelDefinition.NodeCredential.SUBJECT_ALT_NAME, nodeCredentialSubjectAltName);
        }

        final String nodeCredentialKeyInfo = nodeCredentialMap.get(NODE_CREDENTIAL_KEY_INFO);
        nodeCredentialAttributes.put(ModelDefinition.NodeCredential.KEY_INFO, nodeCredentialKeyInfo);

        nodeCredentialAttributes.put(ModelDefinition.NodeCredential.RENEWAL_MODE, currentRenewalMode);
        if (enrollmentAuthorityFdn != null) {
            nodeCredentialAttributes.put(ModelDefinition.NodeCredential.ENROLLMENT_AUTHORITY, enrollmentAuthorityFdn);
        }
        nodeCredentialAttributes.put(ModelDefinition.NodeCredential.ENROLLMENT_SERVER_GROUP, enrollmentServerGroupFdn);

        final String createMessage = NscsLogger.stringifyCreateParams(certMFdn, nodeCredentialType, nodeCredentialNamespace, nodeCredentialVersion,
                nodeCredentialName, nodeCredentialAttributes);
        nscsLogger.info(task, "Creating " + createMessage);
        try {
            writerService.createMo(certMFdn, nodeCredentialType, nodeCredentialNamespace, nodeCredentialVersion, nodeCredentialName,
                    nodeCredentialAttributes);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while creating " + createMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully created " + createMessage);
        return nodeCredentialName;
    }

    /**
     * Update serialized output parameters with given parameters when given NodeCredential is valid.
     *
     * @param task
     * @param nodeCredentialFdn
     * @param currentNodeCredentialFdn
     * @param reservedByUser
     * @param actualRenewalMode
     * @param enrollmentMode
     * @param enrollmentInfo
     * @param outputParams
     * @return the updated serialized output parameters
     */
    private String nodeCredentialIsValid(final ComEcimCheckNodeCredentialTask task, final String nodeCredentialFdn,
            final String currentNodeCredentialFdn, final List<String> reservedByUser, final String actualRenewalMode, final String enrollmentMode,
            final ScepEnrollmentInfoImpl enrollmentInfo, final Map<String, Serializable> outputParams) {
        final String state = VALID;
        return serializeResult(task, state, enrollmentInfo, nodeCredentialFdn, currentNodeCredentialFdn, reservedByUser, actualRenewalMode,
                enrollmentMode, outputParams);
    }

    /**
     * Update serialized output parameters with given parameters when given NodeCredential is not valid.
     *
     * @param task
     * @param nodeCredentialFdn
     * @param currentNodeCredentialFdn
     * @param reservedByUser
     * @param actualRenewalMode
     * @param certificateState
     * @param enrollmentMode
     * @param enrollmentInfo
     * @param outputParams
     * @return the updated serialized output parameters
     */
    private String nodeCredentialIsNotValid(final ComEcimCheckNodeCredentialTask task, final String nodeCredentialFdn,
            final String currentNodeCredentialFdn, final List<String> reservedByUser, final String actualRenewalMode, final String certificateState,
            final String enrollmentMode, final ScepEnrollmentInfoImpl enrollmentInfo, final Map<String, Serializable> outputParams) {
        final String state = NOT_VALID;
        return serializeResult(task, state, enrollmentInfo, nodeCredentialFdn, currentNodeCredentialFdn, reservedByUser, actualRenewalMode,
                enrollmentMode, outputParams);
    }

    /**
     * Serialize output parameters with given parameters.
     *
     * @param task
     * @param result
     * @param enrollmentInfo
     * @param nodeCredentialFdn
     * @param currentNodeCredentialFdn
     * @param reservedByUser
     * @param actualRenewalMode
     * @param enrollmentMode
     * @param outputParams
     * @return the serialized output parameters or null
     */
    private String serializeResult(final ComEcimCheckNodeCredentialTask task, final String result, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String nodeCredentialFdn, final String currentNodeCredentialFdn, final List<String> reservedByUser, final String actualRenewalMode,
            final String enrollmentMode, Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outputParams = new HashMap<String, Serializable>();
        }

        outputParams.put(WorkflowOutputParameterKeys.NODE_CREDENTIAL_FDN.toString(), nodeCredentialFdn);
        if (currentNodeCredentialFdn != null) {
            outputParams.put(WorkflowOutputParameterKeys.CURRENT_NODE_CREDENTIAL_FDN.toString(), currentNodeCredentialFdn);
            String serializedReservedByUser = null;
            try {
                serializedReservedByUser = NscsObjectSerializer.writeObject(reservedByUser);
            } catch (final IOException e1) {
                final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing NodeCredential reservedByUser";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            outputParams.put(WorkflowOutputParameterKeys.RESERVED_BY_USER.toString(), serializedReservedByUser);
        }

        // Set MO action to be checked in following service tasks
        final WorkflowMoAction moAction = new WorkflowMoActionWithParams(nodeCredentialFdn,
                MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment);
        moAction.setState(WorkflowMoActionState.CHECK_IT);
        final WorkflowMoActions moActions = new WorkflowMoActions();
        moActions.addTargetAction(moAction);
        final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
        nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);

        // Serialize MO actions in output parameters
        String serializedMoActions = null;
        try {
            serializedMoActions = NscsObjectSerializer.writeObject(moActions);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing MO actions";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTIONS.toString(), serializedMoActions);

        outputParams.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(), actualRenewalMode);
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), enrollmentMode);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        nscsLogger.debug(task, "Serializing result [" + result + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully completed : NodeCredential [" + nodeCredentialFdn + "] state is [" + result + "]";
        if (NOT_VALID.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, NscsLogger.NOT_VALID);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }
        return encodedWfQueryTaskResult;
    }

    private String getEnrollmentInterfaceValue(final String interfaceFdn, final NormalizableNodeReference nodeRef, final boolean isExternalCa) {
        final boolean enrollmentInterfaceUpdateSupported = nodeValidatorUtility.validateNodeTypeForExtCa(nodeRef);
        return (enrollmentInterfaceUpdateSupported && isExternalCa) ? interfaceFdn : null;
    }
}
