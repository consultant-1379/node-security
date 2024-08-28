/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPrepareInstallTrustedCertsTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

import javax.ejb.Local;

@WFTaskType(WorkflowTaskType.CBPOI_PREPARE_INSTALL_TRUSTED_CERTS)
@Local(WFTaskHandlerInterface.class)
public class CbpOiPrepareInstallTrustedCertsTaskHandler implements WFQueryTaskHandler<CbpOiPrepareInstallTrustedCertsTask>, WFTaskHandlerInterface {

    @Inject
    NscsLogger nscsLogger;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    NscsCapabilityModelService nscsCapabilityModelService;

    private static final String DONE = "DONE";

    @Override
    public String processTask(final CbpOiPrepareInstallTrustedCertsTask prepareInstallTrustedCertsTask) {
        nscsLogger.workFlowTaskHandlerStarted(prepareInstallTrustedCertsTask);

        final String successMessage = "Successfully prepared data for Install Trust Certificates Action";
        final NodeReference node = prepareInstallTrustedCertsTask.getNode();
        final String nodeName = node.getName();
        nscsLogger.info(prepareInstallTrustedCertsTask, "From task : node [{}]", nodeName);

        /*
         * Extract output parameters possibly set by previous handlers
         */
        final Map<String, Serializable> outputParams = prepareInstallTrustedCertsTask.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Output params not yet set! ";
            nscsLogger.workFlowTaskHandlerFinishedWithError(prepareInstallTrustedCertsTask, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        final String serializedTrustedEntitiesInfo = (String) outputParams.get(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString());

        final String errorMessage = "Missing trusted entities from previous task";
        List<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo = NscsObjectSerializer.readObject(serializedTrustedEntitiesInfo);
        if ((trustedEntitiesInfo == null) || trustedEntitiesInfo.isEmpty()) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(prepareInstallTrustedCertsTask, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Prepare installCertificatePem actions
        final WorkflowMoActions wflowMoActions = prepareMoActions(prepareInstallTrustedCertsTask, trustedEntitiesInfo);

        final String result = prepareActionDone(prepareInstallTrustedCertsTask, wflowMoActions, outputParams);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(prepareInstallTrustedCertsTask, successMessage);
        return result;
    }

    private WorkflowMoActions prepareMoActions(final CbpOiPrepareInstallTrustedCertsTask task, final List<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo) {
        final WorkflowMoActions moActions = new WorkflowMoActions();
        final Iterator<NscsCbpOiTrustedEntityInfo> itTrustedEntitiesInfo = trustedEntitiesInfo.iterator();
        while (itTrustedEntitiesInfo.hasNext()) {
            final NscsCbpOiTrustedEntityInfo trustedEntityInfo = itTrustedEntitiesInfo.next();
            final MoActionWithParameter targetAction = MoActionWithParameter.CBPOI_CERTIFICATES_INSTALL_CERTIFICATE_PEM;
            final WorkflowMoParams moParams = new WorkflowMoParams();
            moParams.addParam(ModelDefinition.CERTIFICATES_INSTALL_CERTIFICATE_PEM_NAME, trustedEntityInfo.getName());
            moParams.addParam(ModelDefinition.CERTIFICATES_INSTALL_CERTIFICATE_PEM_PEM, trustedEntityInfo.getBase64PemCertificate());

            WorkflowMoAction moAction = new WorkflowMoActionWithParams(trustedEntityInfo.getTrustCategoryFdn(), targetAction, moParams, 0);
            moActions.addTargetAction(moAction);
            final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
            nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);
        }
        return moActions;
    }

    private String prepareActionDone(final CbpOiPrepareInstallTrustedCertsTask task, final WorkflowMoActions moActions, final Map<String, Serializable> outputParams) {
        final String state = DONE;
        return serializeResult(task, state, moActions, outputParams);
    }

    /**
     * @param task
     * @param result
     * @param moActions
     * @return It may return null string
     */
    private String serializeResult(final CbpOiPrepareInstallTrustedCertsTask task, final String result, final WorkflowMoActions moActions,
                                   final Map<String, Serializable> outputParams) {
        final List<String> certificatesNames = new ArrayList<>();
        final Iterator<WorkflowMoAction> itMoActions = moActions.getTargetActions().iterator();
        while (itMoActions.hasNext()) {
            final WorkflowMoActionWithParams moActionWithParams = (WorkflowMoActionWithParams) itMoActions.next();
            certificatesNames.add((String) (moActionWithParams.getTargetActionParams().getParamMap()
                    .get(ModelDefinition.CERTIFICATES_INSTALL_CERTIFICATE_PEM_NAME).getParam()));
        }
        nscsLogger.info(task, "Installing certificates [{}]", (Object) certificatesNames);
        // Serialize MO actions in output parameters
        final String serializedMoActions = serializeWorkflowObject(task, moActions);
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTIONS.toString(), serializedMoActions);

        final WorkflowQueryTaskResult workflowTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        nscsLogger.debug(task, "Serializing result [" + result + "]");
        return serializeWorkflowObject(task, workflowTaskResult);
    }

    private <T extends Serializable> String serializeWorkflowObject(final CbpOiPrepareInstallTrustedCertsTask task, T object) {
        String serializedResult;
        try {
            serializedResult = NscsObjectSerializer.writeObject(object);
        } catch (final IOException exc) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Received exception: " + exc.getMessage());
            throw new UnexpectedErrorException(NscsLogger.stringifyException(exc) + " while serializing task output params");
        }
        return serializedResult;
    }
}
