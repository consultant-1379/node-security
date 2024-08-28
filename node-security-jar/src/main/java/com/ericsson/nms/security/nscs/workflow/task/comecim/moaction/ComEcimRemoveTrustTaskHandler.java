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
package com.ericsson.nms.security.nscs.workflow.task.comecim.moaction;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertM;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskResult;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction.ComEcimRemoveTrustTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

@WFTaskType(WorkflowTaskType.COM_ECIM_REMOVE_TRUST)
@Local(WFTaskHandlerInterface.class)
public class ComEcimRemoveTrustTaskHandler implements WFQueryTaskHandler<ComEcimRemoveTrustTask>, WFTaskHandlerInterface {

    private static final String DELETE_FAILED = WFTaskResult.FALSE.getTaskResult();
    private static final String DELETE_SUCCESS = WFTaskResult.TRUE.getTaskResult();

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moActionService;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Override
    public String processTask(final ComEcimRemoveTrustTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        String result = DELETE_FAILED;

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String mirrorRootFdn = normNode.getFdn();
        final String nodeName = task.getNode().getName();

        // Extract output parameters set by previous handlers.
        // They shall be set!
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Extract trusted certificate name from output parameters.
        // It shall have been already set!
        final String trustedCertificateFdn = (String) outputParams.get(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString());
        if (trustedCertificateFdn == null || trustedCertificateFdn.isEmpty()) {
            final String errorMessage = "Missing trusted certificate FDN parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From output params : TrustedCertificate FDN [" + trustedCertificateFdn + "]");

        final ComEcimManagedElement targetRootMo = (ComEcimManagedElement) capabilityService.getMirrorRootMo(normNode);

        // Get CertM MO FDN
        final Mo certMMo = targetRootMo.systemFunctions.secM.certM;
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, certMMo.type());
        nscsLogger.debug(task, "Reading " + readMessage);
        final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo);
        if (certMFdn == null || certMFdn.isEmpty()) {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(nodeName, certMMo.type());
        }

        // Get TrustedCertificate
        final Mo trustedCertificateMo = targetRootMo.systemFunctions.secM.certM.trustedCertificate;
        final String readTrustedMessage = "MO by FDN [" + trustedCertificateFdn + "]";
        nscsLogger.debug(task, "Reading " + readTrustedMessage);
        final MoObject trustedCertificateMoObj = readerService.getMoObjectByFdn(trustedCertificateFdn);
        if (trustedCertificateMoObj == null) {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(nodeName, trustedCertificateMo.type());
        }

        // Perform CertM Action to delete MO
        final MoParams moParams = new MoParams();
        moParams.addParam(CertM.REMOVE_TRUSTED_CERT_TRUSTED_CERT, trustedCertificateFdn);
        final MoActionWithParameter targetAction = MoActionWithParameter.ComEcim_CertM_removeTrustedCert;
        final String actionMessage = NscsLogger.stringifyActionByFdn(certMFdn, targetAction, moParams);
        nscsLogger.debug(task, "Performing " + actionMessage);
        try {
            moActionService.performMOActionByMoFdn(certMFdn, targetAction, moParams);
            nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully performed " + actionMessage);
            result = DELETE_SUCCESS;
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while performing " + actionMessage;
            nscsLogger.workFlowTaskHandlerOngoing(task, errorMessage);
        }

        final String successMessage = "Completed : remove result is [" + result + "]";
        if (DELETE_SUCCESS.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, NscsLogger.REMOVE_FAILED);
        }

        return result;
    }

}
