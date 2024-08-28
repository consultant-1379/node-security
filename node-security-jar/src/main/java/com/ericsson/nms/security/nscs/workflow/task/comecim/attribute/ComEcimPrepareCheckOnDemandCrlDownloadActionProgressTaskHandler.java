/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithoutParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_PREPARE_CHECK_ON_DEMAND_CRL_DOWNLOAD.
 * </p>
 * <p>
 * Prepare the MO action to check start download Crl MO action on specified COM ECIM node.
 * </p>
 *
 * @author xsaufar
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_PREPARE_CHECK_ON_DEMAND_CRL_DOWNLOAD)
@Local(WFTaskHandlerInterface.class)
public class ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTaskHandler
        implements WFQueryTaskHandler<ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask>, WFTaskHandlerInterface {

    private static final String DONE = "DONE";

    // TODO: update this interval once get better measure
    private static final int POLL_TIMES = 10;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Override
    public String processTask(final ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final String nodeName = node.getName();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String mirrorRootFdn = normNode.getFdn();

        final Mo rootMo = capabilityService.getMirrorRootMo(normNode);

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

        // Prepare downloadCrl action
        final WorkflowMoActions moActions = new WorkflowMoActions();
        final MoActionWithoutParameter targetAction = MoActionWithoutParameter.ComEcim_CertM_downloadCrl;

        final WorkflowMoAction moAction = new WorkflowMoActionWithoutParams(certMFdn, targetAction, POLL_TIMES);
        moAction.setState(WorkflowMoActionState.CHECK_IT);
        moActions.addTargetAction(moAction);
        final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
        nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);

        final String result = prepareActionDone(task, moActions, null);
        final String successMessage = "Successfully completed : prepare check [downloadCrl] action is in state [" + DONE + "]";
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
    private String prepareActionDone(final ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask task, final WorkflowMoActions moActions,
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
    private String serializeResult(final ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask task, final String result,
            final WorkflowMoActions moActions, Map<String, Serializable> outputParams) {
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