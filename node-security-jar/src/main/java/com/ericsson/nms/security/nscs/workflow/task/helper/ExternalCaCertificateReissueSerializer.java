/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.helper;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * This class is used for serializing the outputParams for certificate reissue operation with External CA
 *
 * @author xsrirko
 *
 */
public class ExternalCaCertificateReissueSerializer {

    @Inject
    private NscsLogger nscsLogger;

    /**
     * Serialize output parameters with given parameters.
     *
     * @param task
     *            object of WorkflowQueryTask
     * @param result
     *            result which will be serialized
     * @param nodeCredentialFdn
     *            fdn of NodeCredential MO
     * @param currentNodeCredentialFdn
     *            fdn of existing NodeCredential MO
     * @param reservedByUser
     *            MO parameter of NodeCredential
     * @param outputParams
     *            Map object in which the output Parameters will be added.
     * @return the serialized output parameters or null
     */
    public String serializeResult(final WorkflowQueryTask task, final String result, final String nodeCredentialFdn,
            final String currentNodeCredentialFdn, final List<String> reservedByUser, Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outputParams = new HashMap<>();
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

        if ("NOT_VALID".equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, NscsLogger.NOT_VALID);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }
        return encodedWfQueryTaskResult;
    }

}
