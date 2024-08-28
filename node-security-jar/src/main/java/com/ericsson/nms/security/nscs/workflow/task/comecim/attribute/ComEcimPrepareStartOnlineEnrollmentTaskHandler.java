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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Certificate.CertificateState;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.VendorCredential;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimPrepareStartOnlineEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_PREPARE_START_ONLINE_ENROLLMENT.
 * </p>
 * <p>
 * Prepare, in its output parameters, the MO action to start online enrollment on specified COM ECIM node.
 * </p>
 *
 * @author emaborz
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_PREPARE_START_ONLINE_ENROLLMENT)
@Local(WFTaskHandlerInterface.class)
public class ComEcimPrepareStartOnlineEnrollmentTaskHandler implements WFQueryTaskHandler<ComEcimPrepareStartOnlineEnrollmentTask>, WFTaskHandlerInterface {

    private static final String DONE = "DONE";

    // TODO: update this interval once get better measure
    private static final int POLL_TIMES = 16;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiEntityManager;

    @Override
    public String processTask(final ComEcimPrepareStartOnlineEnrollmentTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(task.getNode());
        final String trustedCertCategory = task.getTrustedCategory();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);
        nscsLogger.info(task, "From task : certificate type [" + certificateType + "]");
        final String nodeName = task.getNode().getName();
        final String mirrorRootFdn = normalizable.getFdn();
        final Mo rootMo = capabilityService.getMirrorRootMo(normalizable);

        // Extract output parameters set by previous handlers.
        // They shall be set!
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Extract enrollment mode from output parameters.
        // It shall have been already set!
        EnrollmentMode enrollmentMode = null;
        final String enrollmentModeFromParams = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString());
        if (enrollmentModeFromParams != null && !enrollmentModeFromParams.isEmpty()) {
            enrollmentMode = EnrollmentMode.valueOf(enrollmentModeFromParams);
        }
        if (enrollmentMode == null) {
            final String errorMessage = "Missing enrollment mode internal parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From task : enrollmentMode [" + enrollmentMode.name() + "]");

        // Extract node credential FDN from output parameters.
        // It shall have been already set!
        final String nodeCredentialFdn = (String) outputParams.get(WorkflowOutputParameterKeys.NODE_CREDENTIAL_FDN.toString());
        if (nodeCredentialFdn == null || nodeCredentialFdn.isEmpty()) {
            final String errorMessage = "Missing node credential FDN internal parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From task : Node Credential FDN [" + nodeCredentialFdn + "]");

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

        // Get NodeCredential
        final Mo nodeCredentialMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.nodeCredential;
        final String readNodeCredentialMessage = "MO by FDN [" + nodeCredentialFdn + "]";
        nscsLogger.debug(task, "Reading " + readNodeCredentialMessage);
        final MoObject nodeCredentialMoObj = readerService.getMoObjectByFdn(nodeCredentialFdn);
        if (nodeCredentialMoObj == null) {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(nodeName, nodeCredentialMo.type());
        }

        final String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
        final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
        if (enrollmentInfo == null) {
            final String errorMessage = "Missing enrollment info internal parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Check EnrollmentMode and VendorCredential
        String challengePassword = enrollmentInfo.getChallengePassword();
        if (EnrollmentMode.CMPv2_VC.equals(enrollmentMode)) {
            nscsLogger.info(task, "Using enrollment mode [" + enrollmentMode.name() + "] : check for VendorCredential");
            // The VendorCredential should be present and valid on node
            final Mo vendorCredentialMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.vendorCredential;
            final Map<String, Object> vendorCredentialAttributes = new HashMap<String, Object>();
            final String readVCMessage = NscsLogger.stringifyReadParams(certMFdn, vendorCredentialMo.type());
            nscsLogger.debug(task, "Reading " + readVCMessage);
            final String vendorCredentialFdn = nscsNodeUtility.getSingleInstanceMoFdn(certMFdn, vendorCredentialMo, vendorCredentialAttributes,
                    VendorCredential.CERTIFICATE_STATE);
            nscsLogger.info(task, "Read VendorCredential FDN [" + vendorCredentialFdn + "]");
            if (vendorCredentialFdn != null && !vendorCredentialFdn.isEmpty()) {
                if (vendorCredentialAttributes != null && !vendorCredentialAttributes.isEmpty()) {
                    final String vendorCredentialState = (String) vendorCredentialAttributes.get(VendorCredential.CERTIFICATE_STATE);
                    if (CertificateState.VALID.name().equals(vendorCredentialState)) {
                        challengePassword = "NULL";
                        nscsLogger.info(task, "VendorCredential MO with fdn [" + vendorCredentialFdn + "] in valid state: going to use ["
                                + challengePassword + "] as challengePassword");
                    } else {
                        nscsLogger.info(task, "VendorCredential MO with fdn [" + vendorCredentialFdn + "] in wrong state [" + vendorCredentialState
                                + "]: going to use challengePassword");
                    }
                } else {
                    nscsLogger.info(task, "VendorCredential MO with fdn [" + vendorCredentialFdn + "] : null or empty attrs ["
                            + vendorCredentialAttributes + "] : going to use challengePassword");
                }
            } else {
                nscsLogger.info(task, "Read null or empty VendorCredential FDN [" + vendorCredentialFdn + "] : going to use challengePassword");
            }
        } else {
            nscsLogger.info(task, "Using enrollment mode [" + enrollmentMode.name() + "] : going to use challengePassword");
        }

        // Prepare startOnlineEnrollment action
        final WorkflowMoActions moActions = new WorkflowMoActions();
        final MoActionWithParameter targetAction = MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment;
        final WorkflowMoParams moParams = new WorkflowMoParams();
        moParams.addParam(ModelDefinition.NodeCredential.START_ONLINE_ENROLLMENT_CHALLENGE_PASSWORD, challengePassword);
        final WorkflowMoAction moAction = new WorkflowMoActionWithParams(nodeCredentialFdn, targetAction, moParams, POLL_TIMES);
        moActions.addTargetAction(moAction);
        final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
        nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);

        final String result = prepareActionDone(task, moActions, outputParams);
        final String successMessage = "Successfully completed : prepare [startOnlineEnrollment] action is in state [" + DONE + "]";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return result;
    }

    /**
     *
     * @param task
     * @param toBeInstalledCAEntities
     * @param certMFdn
     * @param outputParams
     * @return
     */
    private String prepareActionDone(final ComEcimPrepareStartOnlineEnrollmentTask task, final WorkflowMoActions moActions,
            final Map<String, Serializable> outputParams) {
        final String state = DONE;
        return serializeResult(task, state, moActions, outputParams);
    }

    /**
     * @param task
     * @param result
     * @param toBeInstalledCAEntities
     * @param certMFdn
     * @param outputParams
     * @return It may return null string
     */
    private String serializeResult(final ComEcimPrepareStartOnlineEnrollmentTask task, final String result, final WorkflowMoActions moActions,
            Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params");
            outputParams = new HashMap<String, Serializable>();
        }

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

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        nscsLogger.debug(task, "Serializing result [" + result + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedWfQueryTaskResult;
    }

}
