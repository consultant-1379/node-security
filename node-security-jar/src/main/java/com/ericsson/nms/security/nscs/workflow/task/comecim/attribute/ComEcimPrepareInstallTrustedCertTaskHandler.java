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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimPrepareInstallTrustedCertTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT.
 * </p>
 * <p>
 * Prepare, in its output parameters, the MO actions to install trusted certificates on specified COM ECIM node.
 * </p>
 *
 * @author emaborz
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT)
@Local(WFTaskHandlerInterface.class)
public class ComEcimPrepareInstallTrustedCertTaskHandler implements WFQueryTaskHandler<ComEcimPrepareInstallTrustedCertTask>, WFTaskHandlerInterface {

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

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiEntityManager;

    @Override
    public String processTask(final ComEcimPrepareInstallTrustedCertTask task) {

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

        /*
         * Extract output parameters set by previous handlers. They shall be set!
         */
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String serializedTrustedEntitiesInfo = (String) outputParams.get(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString());
        final List<NscsTrustedEntityInfo> trustedEntitiesInfo = NscsObjectSerializer.readObject(serializedTrustedEntitiesInfo);
        final String serializedEnrollmentCaInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString());
        final NscsTrustedEntityInfo enrollmentCaInfo = NscsObjectSerializer.readObject(serializedEnrollmentCaInfo);
        if ((trustedEntitiesInfo == null || trustedEntitiesInfo.isEmpty()) && enrollmentCaInfo == null) {
            final String errorMessage = "Missing trusted entities";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final Set<NscsTrustedEntityInfo> toBeInstalledTrustedEntitiesInfo = new HashSet<NscsTrustedEntityInfo>();
        if (trustedEntitiesInfo != null) {
            nscsLogger.info(task, "From output params : there are [" + trustedEntitiesInfo.size() + "] trusted CAs to be installed");
            toBeInstalledTrustedEntitiesInfo.addAll(trustedEntitiesInfo);
        }
        if (enrollmentCaInfo != null) {
            nscsLogger.info(task, "From output params : there is enrollment CA to be installed");
            toBeInstalledTrustedEntitiesInfo.add(enrollmentCaInfo);
        }

        for (final NscsTrustedEntityInfo trustedEntityInfo : toBeInstalledTrustedEntitiesInfo) {
            nscsLogger.info(task, "To be installed on node : " + trustedEntityInfo.stringify());
        }

        // Prepare installTrustedCertFromUri actions
        final WorkflowMoActions moActions = new WorkflowMoActions();
        final Iterator<NscsTrustedEntityInfo> itTrustedEntitiesInfo = toBeInstalledTrustedEntitiesInfo.iterator();
        while (itTrustedEntitiesInfo.hasNext()) {
            final NscsTrustedEntityInfo trustedEntityInfo = itTrustedEntitiesInfo.next();
            final MoActionWithParameter targetAction = MoActionWithParameter.ComEcim_CertM_installTrustedCertFromUri;
            final WorkflowMoParams moParams = new WorkflowMoParams();
            final String tdpsUrl = trustedEntityInfo.getTdpsUrl();
            if (tdpsUrl == null) {
                final String errorMessage = "Null TDPS url for trusted entity [" + trustedEntityInfo.stringify() + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            moParams.addParam("uri", tdpsUrl);
            moParams.addParam("uriPassword", "NULL", true);
            moParams.addParam("fingerprint", "NULL");

            if (task.getInterfaceFdn() != null) {
                moParams.addParam("trustPointInterface", task.getInterfaceFdn());
            }
            WorkflowMoAction moAction = new WorkflowMoActionWithParams(certMFdn, targetAction, moParams, POLL_TIMES);
            moActions.addTargetAction(moAction);
            final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
            nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);
        }
        final String result = prepareActionDone(task, moActions, outputParams);
        final String successMessage = "Successfully completed : prepare [installTrustedCert] action is in state [" + DONE + "]";
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
    private String prepareActionDone(final ComEcimPrepareInstallTrustedCertTask task, final WorkflowMoActions moActions,
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
    private String serializeResult(final ComEcimPrepareInstallTrustedCertTask task, final String result, final WorkflowMoActions moActions,
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
