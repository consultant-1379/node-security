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
package com.ericsson.nms.security.nscs.utilities;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean.KeyLength;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress.ActionResultType;
import com.ericsson.nms.security.nscs.data.ModelDefinition.AsyncActionProgress.ActionStateType;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertM;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Ikev2PolicyProfile;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetconfTls;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential.KeyInfo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionState;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;

/**
 * Utility class for handling COM ECIM node
 *
 */
public class NSCSComEcimNodeUtility {

    @Inject
    private Logger logger;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private ComEcimMoNaming comEcimMoNaming;

    @Inject
    private ConfigurationListener configurationListener;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    /**
     * @param asyncActionProgressAttribute
     *            The AsyncActionProgress attribute to read
     * @param normNode
     *            The Normalizable node ref
     * @param targetMo
     *            The MO where to read the AsyncActionProgress
     * @return
     * @see NSCSComEcimNodeUtility#getAsyncActionProgressAttribute(String, String, CmResponse)
     * @throws MissingMoException
     * @throws UnexpectedErrorException
     */
    public Map<String, Object> getAsyncActionProgressAttribute(final String asyncActionProgressAttribute, final NormalizableNodeReference normNode,
            final Mo targetMo) throws MissingMoException, UnexpectedErrorException {

        final CmResponse cmCertReportProgressAttribute = reader.getMOAttribute(normNode, targetMo.type(), targetMo.namespace(),
                asyncActionProgressAttribute);

        return getAsyncActionProgressAttribute(normNode.getFdn(), asyncActionProgressAttribute, cmCertReportProgressAttribute);
    }

    /**
     * @param asyncActionProgressAttribute
     *            The MO attribute of type @see {@link ModelDefinition.AsyncActionProgress}
     * @param moFdn
     *            The MO fdn to get data from
     * @return Map<key, value> where keys are {@link ModelDefinition.AsyncActionProgress#ACTION_NAME},
     *         {@link ModelDefinition.AsyncActionProgress#RESULT}, {@link ModelDefinition.AsyncActionProgress#STATE}
     * @throws MissingMoException
     * @throws UnexpectedErrorException
     */
    public Map<String, Object> getAsyncActionProgressAttribute(final String asyncActionProgressAttribute, final String moFdn)
            throws MissingMoException, UnexpectedErrorException {

        final CmResponse cmAsyncActionProgressAttributeResponse = reader.getMoByFdn(moFdn);

        return getAsyncActionProgressAttribute(moFdn, asyncActionProgressAttribute, cmAsyncActionProgressAttributeResponse);
    }

    /**
     * @param moFdn
     *            The MO fdn
     * @param asyncActionProgressAttribute
     *            The MO attribute of type @see {@link ModelDefinition.AsyncActionProgress}
     * @param cmResponse
     *            The CM response to loop over
     * @return Map<key, value> where keys are {@link ModelDefinition.AsyncActionProgress#ACTION_NAME},
     *         {@link ModelDefinition.AsyncActionProgress#RESULT}, {@link ModelDefinition.AsyncActionProgress#STATE}
     * @throws MissingMoException
     * @throws UnexpectedErrorException
     */
    private Map<String, Object> getAsyncActionProgressAttribute(final String moFdn, final String asyncActionProgressAttribute,
            final CmResponse cmResponse) throws MissingMoException, UnexpectedErrorException {

        final String attributeToRead = asyncActionProgressAttribute;
        String stateValue = null;
        String actionNameValue = null;
        String resultValue = null;
        final Map<String, Object> reportProgress = new HashMap<String, Object>();

        if (cmResponse.getCmObjects().isEmpty()) {
            final String errorMessage = String.format("Node [%s] - MissingMoException", moFdn);
            final MissingMoException ex = new MissingMoException(errorMessage);
            logger.error(errorMessage);
            systemRecorder.recordSecurityEvent("Node Security Service - Checking attribute " + attributeToRead + " on COM ECIM node",
                    " [" + moFdn + "] : " + errorMessage, "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "IN-PROGRESS");
            throw ex;
        } else if (cmResponse.getCmObjects().size() > 1) {
            final UnexpectedErrorException ex = new UnexpectedErrorException(
                    String.format("Got too many results (%s) was expecting 1", cmResponse.getCmObjects().size()));
            final String errorMessage = String.format(
                    "Node [%s] - Got too many results [%s] in the CMReader response. Raising UnexpectedErrorException", moFdn,
                    cmResponse.getCmObjects().size());
            logger.error(errorMessage);
            systemRecorder.recordSecurityEvent("Node Security Service - Checking attribute " + attributeToRead + " on COM ECIM node",
                    " [" + moFdn + "] : " + errorMessage, "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "IN-PROGRESS");
            throw ex;
        } else {
            // Reading the AsyncActionProgress struct
            @SuppressWarnings("unchecked")
            final Map<String, Object> reportProgressAttributeValue = (Map<String, Object>) cmResponse.getCmObjects().iterator().next().getAttributes()
                    .get(attributeToRead);

            // read the 'state' attribute as ActionStateType
            stateValue = (String) reportProgressAttributeValue.get(AsyncActionProgress.STATE);
            final ActionStateType reportProgressState = ActionStateType.valueOf(stateValue);

            // read the 'result' attribute as ActionStateType
            resultValue = (String) reportProgressAttributeValue.get(AsyncActionProgress.RESULT);
            final ActionResultType reportProgressResult = ActionResultType.valueOf(resultValue);

            // read the action name attribute
            actionNameValue = (String) reportProgressAttributeValue.get(AsyncActionProgress.ACTION_NAME);

            logger.debug(
                    "Node [{}] - Reading [{}] attribute " + " param [{}], value [{}], enum value [{}]" + " param [{}], value [{}], enum value [{}]"
                            + " param [{}], value [{}]",
                    moFdn, attributeToRead, AsyncActionProgress.STATE, stateValue, reportProgressState.name(), AsyncActionProgress.RESULT,
                    resultValue, reportProgressResult.name(), AsyncActionProgress.ACTION_NAME, actionNameValue);

            // put
            // KEY: state, VALUE: stateValue
            // KEY: actionName, VALUE: actionNameValue
            // KEY: result, VALUE: resultValue
            reportProgress.put(AsyncActionProgress.STATE, reportProgressState);
            reportProgress.put(AsyncActionProgress.ACTION_NAME, actionNameValue);
            reportProgress.put(AsyncActionProgress.RESULT, reportProgressResult);
        }
        return reportProgress;
    }

    /**
     * Gets the Ldap MOFdn on the Node
     *
     * @param normalizableReference
     * @return LdapMoFdn
     */
    public String getLdapMoFdn(final NormalizableNodeReference normalizableReference) {
        final String mirrorRootFdn = normalizableReference.getFdn();
        return nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, LdapConstants.COMECIM_LDAP_MO, "");
    }

    /**
     * Get KeyInfo value (as expected by COM/ECIM node) for the given enrollment info.
     *
     * @param enrollmentInfo
     * @return the KeyInfo value or null if conversion fails.
     */
    public String getKeySizeFromEnrollmentInfo(final ScepEnrollmentInfo enrollmentInfo) {
        String outputKeyInfoValue = null;
        String inputKeyLengthValue = null;
        if (enrollmentInfo != null) {
            inputKeyLengthValue = enrollmentInfo.getKeySize();
            if (inputKeyLengthValue != null) {
                final KeyLength keyLength = KeyLength.getKeySizeFromValue(inputKeyLengthValue);
                // Please note that previous method returns KeyLength.RSA2048 as
                // default
                if (keyLength != null) {
                    final KeyInfo keyInfo = KeyInfo.fromKeyLength(keyLength);
                    if (keyInfo != null) {
                        outputKeyInfoValue = keyInfo.name();
                    }
                }
            }
        }
        if (outputKeyInfoValue != null) {
            logger.debug("enrollmentInfoKeyLength[{}] converted to keyInfo[{}]", inputKeyLengthValue, outputKeyInfoValue);
        } else {
            logger.error("enrollmentInfoKeyLength[{}] conversion failed to KeyInfo", inputKeyLengthValue);
        }
        return outputKeyInfoValue;
    }

    /**
     * Get the NodeCredential FDN for the given certificate type under the specified root MO.
     *
     * @param mirrorRootFdn
     *            the FDN of the mirror root MO.
     * @param rootMo
     *            the root Mo.
     * @param certificateType
     *            the certificate type.
     * @param normNodeRef
     *            the node reference.
     * @return the FDN of involved NodeCredential or null.
     */
    public String getNodeCredentialFdn(final String mirrorRootFdn, final Mo rootMo, final String certificateType,
            final NormalizableNodeReference normNodeRef) {

        logger.debug("Get NodeCredentialFdn for mirrorRootFdn[{}] rootMo[{}] certType[{}]", mirrorRootFdn, rootMo, certificateType);

        if (mirrorRootFdn == null || mirrorRootFdn.isEmpty() || rootMo == null || certificateType == null || certificateType.isEmpty()) {
            logger.error("Get NodeCredentialFdn : invalid value : mirrorRootFdn[{}] rootMo[{}] certType[{}]", mirrorRootFdn, rootMo, certificateType);
            return null;
        }

        String nodeCredentialFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        if (CertificateType.OAM.name().equals(certificateType)) {
            // Get NetconfTls
            final Mo netconfTlsMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.netconfTls;
            final String netconfTlsFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, netconfTlsMo, attributes, NetconfTls.NODE_CREDENTIAL);
            if (netconfTlsFdn != null) {
                nodeCredentialFdn = (String) attributes.get(NetconfTls.NODE_CREDENTIAL);
            } else {
                logger.error("Get NodeCredentialFdn failed for mirrorRootFdn[{}] rootMo[{}] certType[{}]", mirrorRootFdn, rootMo.type(),
                        certificateType);
            }
        } else if (CertificateType.IPSEC.name().equals(certificateType)) {
            // Get Ikev2PolicyProfile
            if (nscsCapabilityModelService.isIkev2PolicyProfileSupported(normNodeRef)) {
                final Mo ikev2PolicyProfileMo = ((ComEcimManagedElement) rootMo).transport.ikev2PolicyProfile;
                final String[] requestedAttrsIKEv2 = { Ikev2PolicyProfile.CREDENTIAL };
                final String ikev2PolicyProfileFdn = getIkev2PolicyProfileFdn(mirrorRootFdn, ikev2PolicyProfileMo, attributes, requestedAttrsIKEv2, normNodeRef);
                if (ikev2PolicyProfileFdn != null) {
                    nodeCredentialFdn = (String) attributes.get(Ikev2PolicyProfile.CREDENTIAL);
                } else {
                    logger.error("Get NodeCredentialFdn failed for mirrorRootFdn[{}] rootMo[{}] certType[{}]", mirrorRootFdn, rootMo.type(),
                            certificateType);
                }
            } else {
                final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
                final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo);
                if (certMFdn == null || certMFdn.isEmpty()) {
                    final String errorMessage = String.format("Null or empty certMFdn[%s] for rootMo[%s] and certType[%s]", certMFdn, rootMo,
                            certificateType);
                    logger.error("Get NodeCredentialFdn failed for {}", errorMessage);
                    return null;
                }
                final Mo nodeCredentialMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.nodeCredential;

                final String nodeCredentialName = comEcimMoNaming.getDefaultName(nodeCredentialMo.type(), certificateType, normNodeRef);
                nodeCredentialFdn = nodeCredentialMo.getFdnByParentFdn(certMFdn, nodeCredentialName);
                MoObject nodeCredentialsMoObj = null;
                logger.debug("Has NodeCredential [" + nodeCredentialFdn + "] been already created?");
                nodeCredentialsMoObj = reader.getMoObjectByFdn(nodeCredentialFdn);
                if (nodeCredentialsMoObj == null) {
                    logger.debug("NodeCredential [" + nodeCredentialFdn + "] not created");
                    nodeCredentialFdn = null;
                }
            }
        } else {
            logger.error("Get NodeCredentialFdn for mirrorRootFdn[{}] rootMo[{}]: unknown certType[{}]", mirrorRootFdn, rootMo.type(),
                    certificateType);
        }

        logger.debug("Get NodeCredentialFdn return[{}]", nodeCredentialFdn);
        return nodeCredentialFdn;
    }

    /**
     * Get the TrustCategory FDN for the given certificate type under the specified root MO.
     *
     * @param mirrorRootFdn
     *            the FDN of the mirror root MO.
     * @param rootMo
     *            the root Mo.
     * @param trustCategory
     *            the trust category type.
     * @param normNodeRef
     *            the node reference.
     * @return the FDN of involved TrustCategory or null.
     */

    public String getTrustCategoryFdn(final String mirrorRootFdn, final Mo rootMo, final String trustCategory,
            final NormalizableNodeReference normNodeRef) {

        logger.debug("Get TrustCategoryFdn for mirrorRootFdn[{}] rootMo[{}] certType[{}]", mirrorRootFdn, rootMo, trustCategory);

        if (mirrorRootFdn == null || mirrorRootFdn.isEmpty() || rootMo == null || trustCategory == null || trustCategory.isEmpty()) {
            logger.error("Get TrustCategoryFdn : invalid value : mirrorRootFdn[{}] rootMo[{}] trustCategory[{}]", mirrorRootFdn, rootMo,
                    trustCategory);
            return null;
        }

        String trustCategoryFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        if (CertificateType.OAM.name().equals(trustCategory)) {
            // Get NetconfTls
            final Mo netconfTlsMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.netconfTls;
            final String netconfTlsFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, netconfTlsMo, attributes, NetconfTls.TRUST_CATEGORY);
            if (netconfTlsFdn != null) {
                trustCategoryFdn = (String) attributes.get(NetconfTls.TRUST_CATEGORY);
            } else {
                logger.error("Get TrustCategoryFdn failed for mirrorRootFdn[{}] rootMo[{}] trustCategory[{}]", mirrorRootFdn, rootMo.type(),
                        trustCategory);
            }
        } else if (CertificateType.IPSEC.name().equals(trustCategory)) {
            // Get Ikev2PolicyProfile
            if (nscsCapabilityModelService.isIkev2PolicyProfileSupported(normNodeRef)) {
                final Mo ikev2PolicyProfileMo = ((ComEcimManagedElement) rootMo).transport.ikev2PolicyProfile;
                final String[] requestedAttrsIKEv2 = { Ikev2PolicyProfile.TRUST_CATEGORY };
                final String ikev2PolicyProfileFdn = getIkev2PolicyProfileFdn(mirrorRootFdn, ikev2PolicyProfileMo, attributes, requestedAttrsIKEv2, normNodeRef);
                if (ikev2PolicyProfileFdn != null) {
                    trustCategoryFdn = (String) attributes.get(Ikev2PolicyProfile.TRUST_CATEGORY);
                } else {
                    logger.error("Get TrustCategoryFdn failed for mirrorRootFdn[{}] rootMo[{}] trustCategory[{}]", mirrorRootFdn, rootMo.type(),
                            trustCategory);
                }
            } else {
                final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
                final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo);
                if (certMFdn == null || certMFdn.isEmpty()) {
                    final String errorMessage = String.format("Null or empty certMFdn[%s] for rootMo[%s] and trustCategory[%s]", certMFdn, rootMo,
                            trustCategory);
                    logger.error("Get TrustCategoryFdn failed for {}", errorMessage);
                    return null;
                }

                final Mo trustCategoryMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.trustCategory;
                final String trustCategoryName = comEcimMoNaming.getDefaultName(trustCategoryMo.type(), trustCategory, normNodeRef);
                trustCategoryFdn = trustCategoryMo.getFdnByParentFdn(certMFdn, trustCategoryName);

                MoObject trustCategoryMoObj = null;
                logger.debug("Has TrustCategory [" + trustCategoryFdn + "] been already created?");
                trustCategoryMoObj = reader.getMoObjectByFdn(trustCategoryFdn);
                if (trustCategoryMoObj == null) {
                    logger.debug("TrustCategory [" + trustCategoryFdn + "] not created");
                    trustCategoryFdn = null;
                }
            }

        } else {
            logger.error("Get TrustCategoryFdn for mirrorRootFdn[{}] rootMo[{}]: unknown trustCategory[{}]", mirrorRootFdn, rootMo.type(),
                    trustCategory);
        }

        logger.debug("Get TrustCategoryFdn return[{}]", trustCategoryFdn);
        return trustCategoryFdn;
    }

    /**
     * Return attribute name containing action progress report for the given action without parameters. Only actions related to COM/ECIM nodes are
     * considered.
     *
     * @param action
     * @return
     */
    public String getMoActionProgressAttributeName(final MoActionWithoutParameter action) {

        logger.debug("get MoActionProgressAttributeName for action [" + action.getAction() + "]");

        String attributeName = null;
        switch (action) {
        case ComEcim_CertM_cancel:
            // break intentionally omitted
        case ComEcim_CertM_downloadCrl:
            attributeName = CertM.REPORT_PROGRESS;
            break;
        case ComEcim_NodeCredential_cancelEnrollment:
            attributeName = NodeCredential.ENROLLMENT_PROGRESS;
            break;
        default:
            break;

        }
        logger.debug("get MoActionProgressAttributeName return [" + attributeName + "]");
        return attributeName;
    }

    /**
     * Return attribute name containing action progress report for the given action with parameters. Only actions related to COM/ECIM nodes are
     * considered.
     *
     * @param action
     * @return
     */
    public String getMoActionProgressAttributeName(final MoActionWithParameter action) {

        logger.debug("get MoActionProgressAttributeName for action [" + action.getAction() + "]");

        String attributeName = null;
        switch (action) {
        case ComEcim_CertM_installTrustedCertFromUri:
            // break intentionally omitted
        case ComEcim_CertM_removeTrustedCert:
            attributeName = CertM.REPORT_PROGRESS;
            break;
        case ComEcim_NodeCredential_installCredentialFromUri:
            // break intentionally omitted
        case ComEcim_NodeCredential_startOfflineCsrEnrollment:
            // break intentionally omitted
        case ComEcim_NodeCredential_startOnlineEnrollment:
            attributeName = NodeCredential.ENROLLMENT_PROGRESS;
            break;
        default:
            break;
        }
        logger.debug("get MoActionProgressAttributeName return [" + attributeName + "]");
        return attributeName;
    }

    /**
     * Check if requested action is a cancel action or not.
     *
     * @param requestedAction
     * @return
     */
    private boolean isCancelAction(final String requestedAction) {
        logger.debug("is CancelAction: action [" + requestedAction + "]");
        boolean isCancel = false;
        if (requestedAction != null) {
            isCancel = MoActionWithoutParameter.ComEcim_CertM_cancel.getAction().equals(requestedAction)
                    || MoActionWithoutParameter.ComEcim_NodeCredential_cancelEnrollment.getAction().equals(requestedAction);
        } else {
            logger.error("is CancelAction: action [" + requestedAction + "]: wrong parameters");
        }
        logger.debug("is CancelAction: return [" + isCancel + "]");
        return isCancel;
    }

    /**
     * Get action state for the given action and the read action progress.
     *
     * @param requestedAction
     * @param actionProgress
     * @return
     */
    public MoActionState getMoActionState(final String requestedAction, final Map<String, Object> actionProgress) {
        logger.debug("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress + "]");
        MoActionState moActionState = null;
        if (actionProgress != null && requestedAction != null) {
            final String actionState = (String) actionProgress.get(AsyncActionProgress.STATE);
            if (actionState != null && !actionState.isEmpty()) {
                final ActionStateType actionStateType = ActionStateType.valueOf(actionState);
                final String actionName = (String) actionProgress.get(AsyncActionProgress.ACTION_NAME);
                final String actionResult = (String) actionProgress.get(AsyncActionProgress.RESULT);
                ActionResultType actionResultType = ActionResultType.NOT_AVAILABLE;
                if (actionResult != null && !actionResult.isEmpty()) {
                    actionResultType = ActionResultType.valueOf(actionResult);
                }
                logger.debug(
                        "get MoActionState: got action name [" + actionName + "] state [" + actionStateType + "] result [" + actionResultType + "]");
                if (actionName != null && !actionName.isEmpty()) {
                    final boolean isCancelAction = isCancelAction(requestedAction);
                    if (isCancelAction || requestedAction.equals(actionName)) {
                        switch (actionStateType) {
                        case FINISHED:
                            if (!isCancelAction) {
                                if (ActionResultType.SUCCESS.equals(actionResultType)) {
                                    // action successfully completed
                                    moActionState = MoActionState.FINISHED_WITH_SUCCESS;
                                } else {
                                    // action failed
                                    moActionState = MoActionState.FINISHED_WITH_ERROR;
                                }
                            } else {
                                // FINISHED a cancel action
                                moActionState = MoActionState.OTHER_ACTION_FINISHED;
                                logger.error("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress
                                        + "]: FINISHED a cancel action");
                            }
                            break;
                        case RUNNING:
                            if (!isCancelAction) {
                                // action ongoing
                                moActionState = MoActionState.ONGOING;
                            } else {
                                // RUNNING a cancel action
                                moActionState = MoActionState.OTHER_ACTION_ONGOING;
                                logger.error("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress
                                        + "]: RUNNING a cancel action");
                            }
                            break;
                        case CANCELLED:
                            if (isCancelAction) {
                                // cancel action successfully completed
                                moActionState = MoActionState.FINISHED_WITH_SUCCESS;
                            } else {
                                // CANCELLED a not cancel action
                                moActionState = MoActionState.OTHER_ACTION_FINISHED;
                                logger.error("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress
                                        + "]: CANCELLED a not cancel action");
                            }
                            break;
                        case CANCELLING:
                            if (isCancelAction) {
                                // cancel action ongoing
                                moActionState = MoActionState.ONGOING;
                            } else {
                                // CANCELLING a not cancel action
                                moActionState = MoActionState.OTHER_ACTION_ONGOING;
                                logger.error("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress
                                        + "]: CANCELLING a not cancel action");
                            }
                            break;
                        default:
                            logger.error("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress + "]: unknown state ["
                                    + actionStateType + "]");
                            break;

                        }
                    } else {
                        // another action or no action ongoing
                        if (ActionStateType.FINISHED.equals(actionStateType) || ActionStateType.CANCELLED.equals(actionStateType)) {
                            moActionState = MoActionState.OTHER_ACTION_FINISHED;
                        } else {
                            moActionState = MoActionState.OTHER_ACTION_ONGOING;
                        }
                        logger.error("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress + "]: another action ["
                                + actionName + "] in state [" + actionStateType + "]");
                    }
                } else {
                    // No action
                    logger.debug("get MoActionState: empty action name");
                    moActionState = MoActionState.IDLE;
                }
            } else {
                // Empty action state
                logger.debug("get MoActionState: empty action state");
                moActionState = MoActionState.IDLE;
            }
        } else {
            logger.error("get MoActionState: action [" + requestedAction + "] progress [" + actionProgress + "]: wrong parameters");
        }
        logger.debug("get MoActionState: return [" + moActionState + "]");
        return moActionState;
    }

    /**
     * This method is used to get ikev2PolicyProfileFdn based on PIB parameter
     * EnforcedIKEv2PolicyProfileID value
     *
     * @param transportFdn
     *              the FDN of the mirror root MO.
     * @param ikev2PolicyProfileMo
     *              the requested MO type
     * @param attributes
     *              the attributes of existing MO to be returned or null if attributes are not required.
     * @param requestedAttrs
     *              The list of requested attributes.
     * @param normalizableNodeReference
     *            node reference to a MeContext.
     * @return String
     *            - returns Ikev2PolicyProfileFdn based on the value of PIB parameter enforcedIKEv2PolicyProfileID.
     */
    public String getIkev2PolicyProfileFdn(final String transportFdn, final Mo ikev2PolicyProfileMo, final Map<String, Object> attributes,
            final String[] requestedAttrs, final NormalizableNodeReference normalizableNodeReference) {

        final String enforcedIKEv2PolicyProfileID = configurationListener.getEnforcedIKEv2PolicyProfileID();

        if (enforcedIKEv2PolicyProfileID.isEmpty() || enforcedIKEv2PolicyProfileID.equalsIgnoreCase("NONE")) {
            return nscsNodeUtility.getSingleInstanceMoFdn(transportFdn, ikev2PolicyProfileMo, attributes, requestedAttrs);
        } else {
            return getIkev2PolicyProfileFdn(normalizableNodeReference, attributes);
        }
    }

    /**
     * This method is used to get Ikev2PolicyProfileFdn on the given node whose id matches enforcedIKEv2PolicyProfileID value.
     *
     * @param normNode
     *              node reference to a MeContext.
     * @param attributes
     *              the attributes of existing MO to be returned or null if attributes are not required.
     * @return String
     *            - - returns Ikev2PolicyProfileFdn based on the value of PIB parameter enforcedIKEv2PolicyProfileID.
     */
    private String getIkev2PolicyProfileFdn(final NormalizableNodeReference normNode, final Map<String, Object> attributes) {
        final String[] requestedAttrsIKEv2 = { ModelDefinition.Ikev2PolicyProfile.CREDENTIAL,
                ModelDefinition.Ikev2PolicyProfile.IKEV2_POLICY_PROFILE_ID, ModelDefinition.Ikev2PolicyProfile.TRUST_CATEGORY };
        String ikev2PolicyProfileFdn = null;
        final CmResponse ikev2PolicyProfileMOs = reader.getMos(normNode.getFdn(),
                Model.ME_CONTEXT.comManagedElement.transport.ikev2PolicyProfile.type(),
                Model.ME_CONTEXT.comManagedElement.transport.ikev2PolicyProfile.namespace(), requestedAttrsIKEv2);
        if (ikev2PolicyProfileMOs != null) {
            for (final CmObject ikev2PolicyProfileMOsCmObj : ikev2PolicyProfileMOs.getCmObjects()) {
                if (ikev2PolicyProfileMOsCmObj.getAttributes() != null) {
                    final String ikev2PolicyProfileId = (String) ikev2PolicyProfileMOsCmObj.getAttributes()
                            .get(ModelDefinition.Ikev2PolicyProfile.IKEV2_POLICY_PROFILE_ID);
                    if (ikev2PolicyProfileId.equals(configurationListener.getEnforcedIKEv2PolicyProfileID())) {
                        ikev2PolicyProfileFdn = ikev2PolicyProfileMOsCmObj.getFdn();
                        attributes.putAll(ikev2PolicyProfileMOsCmObj.getAttributes());
                        break;
                    }
                } else {
                    logger.error("Requested attributes not available in ikev2 Policy Profile MO");
                }
            }
        } else {
            logger.error("ikev2 Policy Profile MO is not present on the node");
        }
        return ikev2PolicyProfileFdn;
    }

    /**
     * This method is used to get ikev2PolicyProfile MO name
     *
     * @return String
     *             - ikev2PolicyProfileMOName is the name for ikev2 policy profile MO.
     */
    public String getIkev2PolicyProfileMOName() {

        String ikev2PolicyProfileMOName;
        final String enforcedIKEv2PolicyProfileID = configurationListener.getEnforcedIKEv2PolicyProfileID();
        if (enforcedIKEv2PolicyProfileID.isEmpty() || enforcedIKEv2PolicyProfileID.equalsIgnoreCase("NONE")) {
            ikev2PolicyProfileMOName = ComEcimMoNaming.getName(Model.ME_CONTEXT.comManagedElement.transport.ikev2PolicyProfile.type());
        } else {
            ikev2PolicyProfileMOName = configurationListener.getEnforcedIKEv2PolicyProfileID();
        }
        return ikev2PolicyProfileMOName;
    }

    /**
     * Gets the name of the attribute of Trusted Certificate MO containing the reserved-by info for the given node reference.
     * 
     * In CertM 3.0 the 'reservedBy' attribute replaces the deprecated 'reservedByCategory' attribute.
     * 
     * @param normNodeRef
     *            the node reference.
     * @return 'reservedBy' if defined in model for the given node, 'reservedByCategory' otherwise.
     */
    public String getTrustedCertificateReservedByMoAttribute(final NormalizableNodeReference normNodeRef) {
        if (normNodeRef == null) {
            final String errorMsg = "get TrustedCertificateReservedByMoAttribute : null normalizable node reference.";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        final String targetCategory = normNodeRef.getTargetCategory();
        final String targetType = normNodeRef.getNeType();
        final String targetModelIdentity = normNodeRef.getOssModelIdentity();
        final String refMimNs = ModelDefinition.REF_MIM_NS_ECIM_CERTM;
        final String primaryType = ModelDefinition.TRUSTED_CERTIFICATE_TYPE;
        String moAttribute = ModelDefinition.TrustedCertificate.RESERVED_BY;
        final boolean isReservedBySupported = nscsModelServiceImpl.isMoAttributeExists(targetCategory, targetType, targetModelIdentity,
                refMimNs, primaryType, moAttribute);
        if (!isReservedBySupported) {
            moAttribute = ModelDefinition.TrustedCertificate.RESERVED_BY_CATEGORY;
        }
        logger.info(
                "get TrustedCertificateReservedByMoAttribute : returns {} for targetCategory {} targetType {} targetModelIdentity {} refMimNs {} primaryType {}",
                moAttribute, targetCategory, targetType, targetModelIdentity, refMimNs, primaryType);
        return moAttribute;
    }
}
