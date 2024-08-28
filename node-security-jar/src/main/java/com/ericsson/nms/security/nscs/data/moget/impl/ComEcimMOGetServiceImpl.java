package com.ericsson.nms.security.nscs.data.moget.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.exception.NtpOperationNotSupportedException;
import com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean.KeyLength;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress.ActionResultType;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress.ActionStateType;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertM;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertificateContent;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Ntp;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionState;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moget.MOGetService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceType;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.ExtendedCertDetails;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@MOGetServiceType(moGetServiceType = "ECIM")
public class ComEcimMOGetServiceImpl implements MOGetService {

    public static final String EMPTY_FIELD = NscsNameMultipleValueResponseBuilder.EMPTY_STRING;
    public static final String NOT_AVAILABLE = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
    public static final String NOT_APPLICABLE = NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE;
    public static final String LEVEL_NOT_SUPPORTED = NscsNameMultipleValueResponseBuilder.LEVEL_NOT_SUPPORTED;

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCapabilityModelService capabilityModel;

    @Inject
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Override
    public CertStateInfo getCertificateIssueStateInfo(final NodeReference nodeRef, final String certType) {

        logger.debug("get ComEcim CertificateIssueStateInfo for nodeRef[{}] and certType[{}]", nodeRef, certType);
        if (nodeRef == null || certType == null || certType.isEmpty()) {
            logger.error("get ComEcim CertificateIssueStateInfo : wrong params : nodeRef[{}] and certType[{}]", nodeRef, certType);
            return null;
        }

        String nodeName;
        String certEnrollState;
        String certEnrollErrMsg;
        String serialNumber;
        String issuer;
        String subjectName;
        String subjectAltName;

        final CertStateInfo notAvailableCertStateInfo = new CertStateInfo(nodeRef.getFdn());

        final NormalizableNodeReference node = reader.getNormalizableNodeReference(nodeRef);
        final String mirrorRootFdn = node.getFdn();

        final String requestedAction = MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment.getAction();

        final Mo rootMo = capabilityModel.getMirrorRootMo(node);

        final String nodeCredentialFdn = nscsComEcimNodeUtility.getNodeCredentialFdn(mirrorRootFdn, rootMo, certType, node);
        if (nodeCredentialFdn == null || nodeCredentialFdn.isEmpty()) {
            final String errorMessage = String.format("Null or empty nodeCredentialFdn[%s] for nodeRef[%s] and certType[%s]", nodeCredentialFdn,
                    nodeRef, certType);
            logger.error("get ComEcim CertificateIssueStateInfo failed: {}", errorMessage);
            return notAvailableCertStateInfo;
        }

        logger.debug("Getting nodeCredentialFdn[{}]", nodeCredentialFdn);
        final MoObject nodeCredentialMoObj = reader.getMoObjectByFdn(nodeCredentialFdn);
        if (nodeCredentialMoObj == null) {
            final String errorMessage = String.format("NodeCredential MO with FDN[%s] not found for nodeRef[%s]", nodeCredentialFdn, nodeRef);
            logger.error("get ComEcim CertificateIssueStateInfo failed: {}", errorMessage);
            return notAvailableCertStateInfo;
        }

        // Extract NodeCredential enrollmentProgress
        final Map<String, Object> enrollmentProgress = nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_PROGRESS);
        if (enrollmentProgress == null) {
            final String errorMsg = String.format("Null action progress for node[%s] action[%s]", node.getName(), requestedAction);
            logger.error("get ComEcim CertificateIssueStateInfo: [{}]", errorMsg);

            return notAvailableCertStateInfo;
        }

        final StringBuilder state = new StringBuilder();
        final StringBuilder errMsg = new StringBuilder();
        extractInstallActionProgressInfo(enrollmentProgress, requestedAction, state, errMsg);
        certEnrollState = state.toString();
        certEnrollErrMsg = errMsg.toString();
        logger.debug("Extracted certEnrollState[{}] certEnrollErrMsg[{}]", certEnrollState, certEnrollErrMsg);

        String certIssuer = null;
        String certSerialNumber = null;
        String certSubjectName = null;
        String certSubjectAltName = null;

        // Extract NodeCredential certificateContent
        final Map<String, Object> certificateContent = nodeCredentialMoObj.getAttribute(NodeCredential.CERTIFICATE_CONTENT);
        if (certificateContent != null) {
            certIssuer = (String) certificateContent.get(CertificateContent.ISSUER);
            certSerialNumber = (String) certificateContent.get(CertificateContent.SERIAL_NUMBER);
            certSubjectName = (String) certificateContent.get(CertificateContent.SUBJECT_DIST_NAME);
            certSubjectAltName = NOT_AVAILABLE;
        }

        issuer = certIssuer != null ? CertDetails.alignNodeCertDNFieldNamesWithRFC(certIssuer) : NOT_AVAILABLE;
        serialNumber = certSerialNumber != null ? certSerialNumber : EMPTY_FIELD;
        subjectName = certSubjectName != null ? CertDetails.alignNodeCertDNFieldNamesWithRFC(certSubjectName) : NOT_AVAILABLE;
        subjectAltName = certSubjectAltName != null ? certSubjectAltName : NOT_AVAILABLE;
        logger.debug("Got issuer[{}] serialNumber[{}] subject[{}] subjectAltName[{}]", issuer, serialNumber, subjectName, subjectAltName);

        nodeName = node.getName();
        if (node.getNormalizedRef() != null) {
            nodeName = node.getNormalizedRef().getFdn();
        }
        logger.debug("Got node name[{}]", nodeName);

        return new CertStateInfo(nodeName, certEnrollState, certEnrollErrMsg,
                ExtendedCertDetails.certDetailsFactory(issuer, serialNumber, subjectName, subjectAltName));
    }

    @Override
    public CertStateInfo getTrustCertificateStateInfo(final NodeReference nodeRef, final String trustCategory) {

        logger.debug("get ComEcim TrustCertificateStateInfo for nodeRef[{}] and trustCategory[{}]", nodeRef, trustCategory);
        if (nodeRef == null || trustCategory == null || trustCategory.isEmpty()) {
            logger.error("get ComEcim TrustCertificateStateInfo : wrong params : nodeRef[{}] and trustCategory[{}]", nodeRef, trustCategory);
            return null;
        }

        String nodeName;
        String trustCertInstallState;
        String trustCertInstallErrMsg;
        String serialNumber;
        String issuer;
        String subjectName;
        String subjectAltName;

        final CertStateInfo notAvailableCertStateInfo = new CertStateInfo(nodeRef.getFdn());

        final NormalizableNodeReference node = reader.getNormalizableNodeReference(nodeRef);
        final String mirrorRootFdn = node.getFdn();

        final String requestedAction = MoActionWithParameter.ComEcim_CertM_installTrustedCertFromUri.getAction();

        final Mo rootMo = capabilityModel.getMirrorRootMo(node);

        final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
        final Map<String, Object> certMAttributes = new HashMap<String, Object>();
        final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo, certMAttributes, CertM.REPORT_PROGRESS);
        if (certMFdn == null || certMFdn.isEmpty()) {
            final String errorMessage = String.format("Null or empty certMFdn[%s] for nodeRef[%s] and trustCategory[%s]", certMFdn, nodeRef,
                    trustCategory);
            logger.error("get ComEcim TrustCertificateStateInfo: {}", errorMessage);
            return notAvailableCertStateInfo;
        }

        @SuppressWarnings("unchecked")
        final Map<String, Object> installTrustProgress = (Map<String, Object>) certMAttributes.get(CertM.REPORT_PROGRESS);
        if (installTrustProgress == null) {
            final String errorMsg = String.format("Null action progress for node[%s] action[%s]", node.getName(), requestedAction);
            logger.error("get ComEcim TrustCertificateStateInfo: {}", errorMsg);

            return notAvailableCertStateInfo;
        }

        final StringBuilder state = new StringBuilder();
        final StringBuilder errMsg = new StringBuilder();
        extractInstallActionProgressInfo(installTrustProgress, requestedAction, state, errMsg);
        trustCertInstallState = state.toString();
        trustCertInstallErrMsg = errMsg.toString();
        logger.debug("Extracted trustCertInstallState[{}] trustCertInstallErrMsg[{}]", trustCertInstallState, trustCertInstallErrMsg);

        final String trustCategoryFdn = nscsComEcimNodeUtility.getTrustCategoryFdn(mirrorRootFdn, rootMo, trustCategory, node);
        if (trustCategoryFdn == null || trustCategoryFdn.isEmpty()) {
            final String errorMessage = String.format("Null or empty trustCategoryFdn[%s] for nodeRef[%s] and trustCategory[%s]", trustCategoryFdn,
                    nodeRef, trustCategory);
            logger.error("get ComEcim TrustCertificateStateInfo: {}", errorMessage);
            return notAvailableCertStateInfo;
        }

        logger.debug("Getting trustCategoryFdn[{}]", trustCategoryFdn);
        final MoObject trustCategoryMoObj = reader.getMoObjectByFdn(trustCategoryFdn);
        if (trustCategoryMoObj == null) {
            final String errorMessage = String.format("TrustCategory MO with FDN[%s] not found for nodeRef[%s]", trustCategoryFdn, nodeRef);
            logger.error("get ComEcim TrustCertificateStateInfo: {}", errorMessage);
            return notAvailableCertStateInfo;
        }

        final List<String> trustedCertificateFds = trustCategoryMoObj.getAttribute(TrustCategory.TRUSTED_CERTIFICATES);
        if (trustedCertificateFds == null || trustedCertificateFds.isEmpty()) {
            final String errorMessage = String.format("TrustCategory MO with FDN[%s] has null or empty trustedCertificates list for nodeRef[%s]",
                    trustCategoryFdn, nodeRef);
            logger.error("get ComEcim TrustCertificateStateInfo: {}", errorMessage);
            return notAvailableCertStateInfo;
        }

        final List<CertDetails> trustedCertificates = new ArrayList<>();
        //        Mo trustedCertificateMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.trustedCertificate;
        for (final String trustedCertificateFdn : trustedCertificateFds) {
            //            String trustedCertificateName = trustedCertificateMo.extractName(trustedCertificateNodeFdn);
            //            String trustedCertificateFdn = trustedCertificateMo.getFdnByParentFdn(certMFdn, trustedCertificateName);
            if (trustedCertificateFdn == null || trustedCertificateFdn.isEmpty()) {
                final String errorMessage = String.format("Null or empty trustedCertificateFdn[%s] for nodeFdn[{}] nodeRef[%s] and trustCategory[%s]",
                        trustedCertificateFdn, trustedCertificateFdn, nodeRef, trustCategory);
                logger.error("get ComEcim TrustCertificateStateInfo: {}", errorMessage);
                return notAvailableCertStateInfo;
            }

            String certIssuer = null;
            String certSerialNumber = null;
            String certSubjectName = null;
            String certSubjectAltName = null;

            logger.debug("Getting trustedCertificateFdn[{}]", trustedCertificateFdn);
            final MoObject trustedCertificateMoObj = reader.getMoObjectByFdn(trustedCertificateFdn);
            if (trustedCertificateMoObj == null) {
                final String errorMessage = String.format("TrustedCertificate MO with FDN[%s] not found for nodeRef[%s]", trustedCertificateFdn,
                        nodeRef);
                logger.error("get ComEcim TrustCertificateStateInfo : {}", errorMessage);
            } else {
                // Extract NodeCredential certificateContent
                final Map<String, Object> certificateContent = trustedCertificateMoObj.getAttribute(TrustedCertificate.CERTIFICATE_CONTENT);
                if (certificateContent != null) {
                    certIssuer = (String) certificateContent.get(CertificateContent.ISSUER);
                    certSerialNumber = (String) certificateContent.get(CertificateContent.SERIAL_NUMBER);
                    certSubjectName = (String) certificateContent.get(CertificateContent.SUBJECT_DIST_NAME);
                    certSubjectAltName = NOT_AVAILABLE;
                }
            }

            issuer = certIssuer != null ? CertDetails.alignNodeCertDNFieldNamesWithRFC(certIssuer) : NOT_AVAILABLE;
            serialNumber = certSerialNumber != null ? certSerialNumber : EMPTY_FIELD;
            subjectName = certSubjectName != null ? CertDetails.alignNodeCertDNFieldNamesWithRFC(certSubjectName) : NOT_AVAILABLE;
            subjectAltName = certSubjectAltName != null ? certSubjectAltName : NOT_AVAILABLE;
            logger.debug("Got issuer[{}] serialNumber[{}] subject[{}]", issuer, serialNumber, subjectName);

            final CertDetails trustedCertificateInfo = ExtendedCertDetails.certDetailsFactory(issuer, serialNumber, subjectName, subjectAltName);
            trustedCertificates.add(trustedCertificateInfo);
        }

        nodeName = node.getName();
        if (node.getNormalizedRef() != null) {
            nodeName = node.getNormalizedRef().getFdn();
        }
        logger.debug("Got node name[{}]", nodeName);

        return new CertStateInfo(nodeName, trustCertInstallState, trustCertInstallErrMsg, trustedCertificates);
    }

    @Override
    public MoActionState getMoActionState(final String moFdn, final MoActionWithoutParameter action) {
        logger.debug("get MoActionProgressState: moFdn [{}] action [{}]", moFdn, action);
        MoActionState moActionState = null;
        final MoObject moObj = reader.getMoObjectByFdn(moFdn);
        if (moObj == null) {
            logger.error("get MoActionProgressState: moFdn [{}] action [{}]: no such MO", moFdn, action);
        } else {
            final String actionProgressAttributeName = nscsComEcimNodeUtility.getMoActionProgressAttributeName(action);
            logger.debug("get MoActionProgressState: got actionProgress attribute [{}]", actionProgressAttributeName);
            final Map<String, Object> actionProgress = moObj.getAttribute(actionProgressAttributeName);
            if (actionProgress == null) {
                logger.error("get MoActionProgressState: moFdn [{}] action [{}]: actionProgress attribute [{}] is null"
                        , moFdn, action, actionProgressAttributeName);
            } else {
                final String actionName = action.getAction();
                moActionState = nscsComEcimNodeUtility.getMoActionState(actionName, actionProgress);
            }
        }
        logger.debug("get MoActionProgressState: return [{}]", moActionState);
        return moActionState;
    }

    @Override
    public MoActionState getMoActionState(final String moFdn, final MoActionWithParameter action) {
        logger.debug("get MoActionProgressState: moFdn [{}] action [{}]", moFdn, action);
        MoActionState moActionState = null;
        final MoObject moObj = reader.getMoObjectByFdn(moFdn);
        if (moObj == null) {
            logger.error("get MoActionProgressState: moFdn [{}] action [{}]: no such MO", moFdn, action);
        } else {
            final String actionProgressAttributeName = nscsComEcimNodeUtility.getMoActionProgressAttributeName(action);
            logger.debug("get MoActionProgressState: got actionProgress attribute [{}]", actionProgressAttributeName);
            final Map<String, Object> actionProgress = moObj.getAttribute(actionProgressAttributeName);
            if (actionProgress == null) {
                logger.error("get MoActionProgressState: moFdn [{}] action [{}]: actionProgress attribute [{}] is null"
                        , moFdn, action, actionProgressAttributeName);
            } else {
                final String actionName = action.getAction();
                moActionState = nscsComEcimNodeUtility.getMoActionState(actionName, actionProgress);
            }
        }
        logger.debug("get MoActionProgressState: return [{}]", moActionState);
        return moActionState;
    }

    /**
     * Extract from given action progress the action state and error message according to given requested install action.
     *
     * @param actionProgress
     * @param requestedInstallAction
     * @param actionState
     * @param actionErrorMsg
     */
    private void extractInstallActionProgressInfo(final Map<String, Object> actionProgress, final String requestedInstallAction,
            final StringBuilder actionState, final StringBuilder actionErrorMsg) {

        logger.debug("extract InstallActionProgressInfo for actionProgress[{}] requestedAction[{}]", actionProgress, requestedInstallAction);

        String currActionState = null;
        String currActionErrorMsg = null;
        final String actionName = (String) actionProgress.get(AsyncActionProgress.ACTION_NAME);
        if (requestedInstallAction.equals(actionName)) {
            final String actionProgressState = (String) actionProgress.get(AsyncActionProgress.STATE);
            ActionStateType actionStateType = null;
            try {
                actionStateType = ActionStateType.valueOf(actionProgressState);
            } catch (final Exception e) {
                currActionState = NOT_AVAILABLE;
                currActionErrorMsg = String.format("Unknown state: %s retrieved for the requested action: %s", actionProgressState, actionName);
            }

            if (actionStateType != null) {
                switch (actionStateType) {
                case CANCELLING:
                case CANCELLED:
                    currActionState = "FAILURE";
                    currActionErrorMsg = String.format("Invalid state: %s retrieved for the requested action: %s", actionStateType, actionName);
                    break;
                case RUNNING:
                    currActionState = "RUNNING";
                    currActionErrorMsg = "";
                    break;
                case FINISHED:
                    final String actionProgressResult = (String) actionProgress.get(AsyncActionProgress.RESULT);
                    ActionResultType actionResultType = null;
                    try {
                        actionResultType = ActionResultType.valueOf(actionProgressResult);
                    } catch (final Exception e) {
                        currActionState = NOT_AVAILABLE;
                        currActionErrorMsg = String.format("Unknown result: %s retrieved for action state: %s", actionProgressResult,
                                actionStateType);
                    }

                    if (actionResultType != null) {
                        final String actionResultInfo = (String) actionProgress.get(AsyncActionProgress.RESULT_INFO);

                        switch (actionResultType) {
                        case SUCCESS:
                            currActionState = "IDLE";
                            currActionErrorMsg = actionResultInfo;
                            break;
                        case NOT_AVAILABLE:
                            currActionState = "FAILURE";
                            currActionErrorMsg = String.format("Invalid result: %s retrieved for action state: %s", actionResultType,
                                    actionStateType);
                            break;
                        case FAILURE:
                            currActionState = "FAILURE";
                            currActionErrorMsg = actionResultInfo;
                            break;
                        default:
                            currActionState = NOT_AVAILABLE;
                            currActionErrorMsg = String.format("Unknown result: %s retrieved for action state: %s", actionResultType,
                                    actionStateType);
                            break;
                        }
                    }
                    break;
                default:
                    currActionState = NOT_AVAILABLE;
                    currActionErrorMsg = String.format("Unknown state: %s retrieved for the requested action: %s", actionStateType, actionName);
                    break;
                }
            }
        } else {
            // Another action is set in action progress or no action in progress
            currActionState = "IDLE";
            currActionErrorMsg = NOT_AVAILABLE;
            final String errorMsg = String.format("Another action: [%s] is ongoing, requested action: %s", actionName, requestedInstallAction);
            logger.error(errorMsg);
        }

        logger.debug("extract InstallActionProgressInfo extracts currActionState[{}] currActionErrorMsg[{}]", currActionState, currActionErrorMsg);

        if (actionState != null) {
            actionState.append(currActionState);
        }
        if (actionErrorMsg != null) {
            actionErrorMsg.append(currActionErrorMsg);
        }
        logger.debug("extract InstallActionProgressInfo return actionState[{}] actionErrorMsg[{}]", actionState, actionErrorMsg);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.data.moget.MOGetService#getSecurityLevel( com.ericsson.nms.security.nscs.data.nodereference.
     * NormalizableNodeReference, java.lang.String)
     */
    @Override
    public String getSecurityLevel(final NormalizableNodeReference nodeRef, final String syncstatus) {
        return LEVEL_NOT_SUPPORTED;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.data.moget.MOGetService#getIpsecConfig(com
     * .ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference, java.lang.String)
     */
    @Override
    public String getIpsecConfig(final NormalizableNodeReference normNode, final String syncstatus) {
        return NscsNameMultipleValueResponseBuilder.IPSEC_NOT_SUPPORTED;
    }

    /*
     * To get crlCheckMO Attribute Value from Trustcategory MO
     */
    @Override
    public String getCrlCheckStatus(final NormalizableNodeReference normNode, final String certType) {
        logger.info("Start of ComEcimMOGetServiceImpl::getCrlCheckStatus method: normNodeFdn[{}],certType[{}]", normNode.getFdn(), certType);

        final Mo rootMo = capabilityModel.getMirrorRootMo(normNode);
        final String trustCategoryFdn = nscsComEcimNodeUtility.getTrustCategoryFdn(normNode.getFdn(), rootMo, certType, normNode);
        final MoObject MoObject = reader.getMoObjectByFdn(trustCategoryFdn);
        final String cRLCheckStatus = MoObject.getAttribute(TrustCategory.CRL_CHECK).toString();
        logger.info("cRLCheckStatus in ComEcimMOGetServiceImpl::getCrlCheckStatus method: [{}]", cRLCheckStatus);

        logger.info("End of ComEcimMOGetServiceImpl::getCrlCheckStatus method ");

        return cRLCheckStatus;
    }

    /*
     * To Validate Trust category MO for crl check
     */
    @Override
    public boolean validateNodeForCrlCheckMO(final NormalizableNodeReference normNode, final String certType)
            throws TrustCategoryMODoesNotExistException {

        logger.info("Start of ComEcimMOGetServiceImpl::validateNodeForCrlCheckMO method: normNodeFdn[{}],certType[{}]", normNode.getFdn(), certType);

        if (!nodeValidatorUtility.isTrustCategoryMOExists(normNode, certType)) {
            logger.error("Node [{}] doesn't have TrustCategory MO.", normNode.getFdn());
            throw new TrustCategoryMODoesNotExistException();
        }

        return true;
    }

    @Override
    public List<NtpServer> listNtpServerDetails(final NormalizableNodeReference normNode) {

        logger.info("Fetching list of ntp server details for normNodeFdn[{}] ::listNtpServerDetails method: ", normNode.getFdn());

        List<NtpServer> listNtpServerDetails = new ArrayList<>();
        final String mirrorRootFdn = normNode.getFdn();
        CmResponse ntpSecurityPolicyResponse = null;
        CmResponse ntpServerResponse = null;
        NtpServer ntpServer = null;
        String keyId = null;
        CmObject ntpServerData = null;
        final Mo mo = capabilityModel.getMirrorRootMo(normNode);
        final Mo ntpSecurityPolicyMo = ((ComEcimManagedElement) mo).systemFunctions.sysM.timeM.ntp.ntpSecurityPolicy;
        final Mo ntpServerMo = ((ComEcimManagedElement) mo).systemFunctions.sysM.timeM.ntp.ntpServer;

        try {
            ntpSecurityPolicyResponse = reader.getMos(mirrorRootFdn, ModelDefinition.NTP_SECURITY_POLICY, ntpSecurityPolicyMo.namespace());
        } catch (DataAccessSystemException | DataAccessException e) {
            logger.error("Node [{}] Error while reading NtpSecurityPolicy MO. Exception is [{}]", normNode.getFdn(), e.getMessage());
        }

        try {
            ntpServerResponse = reader.getMos(mirrorRootFdn, ModelDefinition.NTP_SERVER, ntpServerMo.namespace());
        } catch (DataAccessSystemException | DataAccessException e) {
            logger.error("Node [{}] Error while reading NtpServer MO. Exception is [{}]", normNode.getFdn(), e.getMessage());
        }

        if (ntpServerResponse != null) {
            for (CmObject ntpServerDetails : ntpServerResponse.getCmObjects()) {
                ntpServer = buildAssociatedNtpServer(ntpSecurityPolicyResponse, ntpServerDetails);
                if (ntpServer != null) {
                    listNtpServerDetails.add(ntpServer);
                }
            }
        }
        if (ntpSecurityPolicyResponse != null) {
            for (CmObject ntpSecurityPolicyDetails : ntpSecurityPolicyResponse.getCmObjects()) {
                keyId = String.valueOf(ntpSecurityPolicyDetails.getAttributes().get(ModelDefinition.NtpSecurityPolicy.KEY_ID));
                if (!isNtpKeyIdFound(listNtpServerDetails, keyId)) {
                    ntpServer = buildNtpServerDetails(keyId, ntpServerData);
                    listNtpServerDetails.add(ntpServer);
                }
            }
        }
        if (listNtpServerDetails.isEmpty()) {
            ntpServer = buildNtpServerDetails(NtpConstants.NA, ntpServerData);
            listNtpServerDetails.add(ntpServer);
        }
        logger.info("List of Ntp server details [{}] : for normNodeFdn [{}] : ", listNtpServerDetails, normNode.getFdn());
        return listNtpServerDetails;
    }

    private boolean isNtpKeyIdFound(final List<NtpServer> listNtpServerDetails, final String keyId){
        boolean isFound = false;
        if (listNtpServerDetails != null && !listNtpServerDetails.isEmpty()) {
            for (final NtpServer ntpServerObj : listNtpServerDetails) {
                if (ntpServerObj.getKeyId().equals(keyId)) {
                    isFound = true;
                    break;
                }
            }
        }
        return  isFound;
    }

    private NtpServer buildAssociatedNtpServer(final CmResponse ntpSecurityPolicyResponse, final CmObject ntpServerDetails) {
        NtpServer ntpServer = null;
        final String ntpSecurityPolicyRef = (String) ntpServerDetails.getAttributes().get(ModelDefinition.TimeMntpServer.NTP_SECURITY_POLICY);
        if (ntpSecurityPolicyRef != null && !ntpSecurityPolicyRef.isEmpty() && !ntpSecurityPolicyRef.equalsIgnoreCase("")) {
            if ((ntpSecurityPolicyResponse != null && ntpSecurityPolicyResponse.getCmObjects() != null) && !ntpSecurityPolicyResponse.getCmObjects().isEmpty()) {
                ntpServer = buildNtpServerResponse(ntpSecurityPolicyResponse, ntpServerDetails);
            }
        } else {
            ntpServer = buildNtpServerDetails("", ntpServerDetails);
        }
        return ntpServer;
    }

    private NtpServer buildNtpServerResponse(final CmResponse ntpSecurityPolicyResponse, CmObject ntpServerDetails) {
        NtpServer ntpServer = null;
        String keyId = null;
        final String ntpSecurityPolicyRef = (String) ntpServerDetails.getAttributes().get(ModelDefinition.TimeMntpServer.NTP_SECURITY_POLICY);
        for (CmObject ntpSecurityPolicyDetails : ntpSecurityPolicyResponse.getCmObjects()) {
            keyId = String.valueOf(ntpSecurityPolicyDetails.getAttributes().get(ModelDefinition.NtpSecurityPolicy.KEY_ID));
            if (ntpSecurityPolicyRef.equalsIgnoreCase(ntpSecurityPolicyDetails.getFdn())) {
                ntpServer = buildNtpServerDetails(keyId, ntpServerDetails);
                break;
            }
        }
        return ntpServer;
    }

    private NtpServer buildNtpServerDetails(final String keyId, final CmObject ntpServerDetails) {
        NtpServer ntpServer = new NtpServer();
        final String administrativeState = (ntpServerDetails != null && ntpServerDetails.getAttributes() != null) ? (String) ntpServerDetails.getAttributes().get(ModelDefinition.TimeMntpServer.ADMINISTRATIVE_STATE) : NtpConstants.NA;
        final String ntpServerId = (ntpServerDetails != null && ntpServerDetails.getAttributes() != null) ? (String) ntpServerDetails.getAttributes().get(ModelDefinition.TimeMntpServer.NTP_SERVER_ID) : NtpConstants.NA;
        final String userLabel = (ntpServerDetails != null && ntpServerDetails.getAttributes() != null) ? (String) ntpServerDetails.getAttributes().get(ModelDefinition.TimeMntpServer.USER_LABEL) : NtpConstants.NA;
        final String serverAddress = (ntpServerDetails != null && ntpServerDetails.getAttributes() != null) ? (String) ntpServerDetails.getAttributes().get(ModelDefinition.TimeMntpServer.SERVER_ADDRESS) : NtpConstants.NA;
        ntpServer.setKeyId(keyId);
        ntpServer.setUserLabel(userLabel);
        ntpServer.setNtpServerId(ntpServerId);
        ntpServer.setServiceStatus(administrativeState);
        ntpServer.setServerAddress(serverAddress);
        return ntpServer;
    }

    @Override
    public boolean validateNodeForNtp(NormalizableNodeReference nodeRef) {
        logger.info("Start of ComEcimMOGetServiceImpl::validateNodeForNtp method: normNodeFdn[{}]", nodeRef.getFdn());
        final String targetCategory = nodeRef.getTargetCategory();
        final String targetType = nodeRef.getNeType();
        final String targetModelIdentity = nodeRef.getOssModelIdentity();
        final Mo mo = capabilityModel.getMirrorRootMo(nodeRef);
        final Mo ntpMo = ((ComEcimManagedElement) mo).systemFunctions.sysM.timeM.ntp;
        final String moAttribute = Ntp.SUPPORTED_KEY_ALGO;
        boolean validNode = false;
        validNode = nscsModelServiceImpl.isNtpOperationSupported(targetCategory, targetType, targetModelIdentity, ModelDefinition.REF_MIM_NS_ECIM_TIMEM, ntpMo.type(), moAttribute);
        if (!validNode) {
            logger.error("Node [{}] doesn't have supportedKeyAlgorithm attribute under Ntp MO.", nodeRef.getFdn());
            throw new NtpOperationNotSupportedException(NtpConstants.SUPPORTED_KEY_ALGO_DOESNOT_EXISTS);
        }
        return validNode;
    }

    @Override
    public String getNodeSupportedFormatOfKeyAlgorithm(NodeReference nodeRef, String keySize) {
        return NscsPkiUtils.convertAlgorithmNamesToNodeSupportedFormat(
                NscsPkiUtils.convertKeyLengthToAlgorithmKeys(KeyLength.getKeySizeFromValue(keySize)).name());
    }
}
