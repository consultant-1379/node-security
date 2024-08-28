/*-----------------------------------------------------------------------------
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
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.CbpOiAlgorithm;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPrepareOnlineEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CBP_OI_PREPARE_ONLINE_ENROLLMENT.
 * </p>
 * <p>
 * Prepare the action to be performed to trigger the online enrollment on the node.
 * </p>
 * <p>
 * Required incoming internal parameters:
 * <ul>
 * <li>ENROLLMENT_INFO : required always</li>
 * <li>IS_START_CMP_REQUIRED : it is optional, if missing renew-cmp action shall be triggered</li>
 * <li>CMP_SERVER_GROUP_NAME : required only if start-cmp action shall be triggered</li>
 * <li>ASYMMETRIC_KEYS_CMP_FDN : required only if start-cmp action shall be triggered</li>
 * <li>ASYMMETRIC_KEY_NAME : required only if start-cmp action shall be triggered</li>
 * <li>TRUSTED_CERTS_NAME : required only if start-cmp action shall be triggered</li>
 * <li>ASYMMETRIC_KEY_CMP_FDN : required only if renew-cmp action shall be triggered</li>
 * </ul>
 * </p>
 * <p>
 * Produced outgoing internal parameters:
 * <ul>
 * <li>MO_ACTIONS : always</li>
 * </ul>
 * </p>
 */
@WFTaskType(WorkflowTaskType.CBP_OI_PREPARE_ONLINE_ENROLLMENT)
@Local(WFTaskHandlerInterface.class)
public class CbpOiPrepareOnlineEnrollmentTaskHandler implements WFQueryTaskHandler<CbpOiPrepareOnlineEnrollmentTask>, WFTaskHandlerInterface {

    private static final String DONE = "DONE";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Override
    public String processTask(final CbpOiPrepareOnlineEnrollmentTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        // Extract output parameters that shall have been already set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        String result = null;
        try {
            final WorkflowMoActions moActions = new WorkflowMoActions();
            final String prepareResult = prepareOnlineEnrollmentAction(task, outputParams, moActions);
            result = serializePrepareOnlineEnrollmentResult(task, prepareResult, moActions, outputParams);
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", prepareResult);
        } catch (final Exception e) {
            final String errorMessage = e.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }
        return result;
    }

    /**
     * Prepares the MO actions to be performed to trigger the online enrollment on the node.
     * 
     * @param task
     *            the task.
     * @param outputParams
     *            the output parameters.
     * @param moActions
     *            the MO actions to be performed.
     * @return the result of the preparation of the MO actions.
     */
    private String prepareOnlineEnrollmentAction(final CbpOiPrepareOnlineEnrollmentTask task, final Map<String, Serializable> outputParams,
            final WorkflowMoActions moActions) {

        // Extract enrollment info from the output parameters
        final String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
        final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
        if (enrollmentInfo == null) {
            final String errorMessage = "Missing enrollment info internal parameter";
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From output params : enrollment info [{}]", enrollmentInfo);

        // Extract isStartCmpRequired from the output parameters
        final boolean isStartCmpRequired = Boolean
                .parseBoolean((String) outputParams.get(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED.toString()));
        nscsLogger.info(task, "From output params : isStartCmpRequired [{}]", isStartCmpRequired);

        final boolean isReissue = Boolean.parseBoolean(task.getIsReissue());
        nscsLogger.info(task, "From task : isReissue [{}]", isReissue);

        if (isStartCmpRequired) {
            prepareStartCmpAction(task, enrollmentInfo, outputParams, moActions);
        } else {
            prepareRenewCmpAction(task, enrollmentInfo, outputParams, moActions);
        }

        return DONE;
    }

    /**
     * Prepares start-cmp action.
     * 
     * @param task
     *            the task.
     * @param enrollmentInfo
     *            the enrollment info.
     * @param outputParams
     *            the output parameters.
     * @param moActions
     *            the MO actions.
     */
    private void prepareStartCmpAction(final CbpOiPrepareOnlineEnrollmentTask task, final ScepEnrollmentInfoImpl enrollmentInfo,
            final Map<String, Serializable> outputParams, final WorkflowMoActions moActions) {

        final String asymmetricKeysCmpFdn = extractNotNullParamFromOutputParams(task, WorkflowOutputParameterKeys.ASYMMETRIC_KEYS_CMP_FDN,
                outputParams);
        final String asymmetricKeyName = extractNotNullParamFromOutputParams(task, WorkflowOutputParameterKeys.ASYMMETRIC_KEY_NAME, outputParams);
        final String cmpServerGroupName = extractNotNullParamFromOutputParams(task, WorkflowOutputParameterKeys.CMP_SERVER_GROUP_NAME, outputParams);
        final String trustedCerts = extractNotNullParamFromOutputParams(task, WorkflowOutputParameterKeys.TRUSTED_CERTS_NAME, outputParams);

        // Extract key size from enrollment info and convert it to node format.
        final String algorithm = nscsCbpOiNodeUtility.getAlgorithmFromEnrollmentInfo(enrollmentInfo);
        nscsLogger.info(task, "From enrollment info : algorithm [{}]", algorithm);

        // Extract challenge password from enrollment info.
        final String challenge = enrollmentInfo.getChallengePassword();

        // Extract subject DN from enrollment info.
        final String subjectDn = enrollmentInfo.getDistinguishedName();

        // Prepare start-cmp action
        final MoActionWithParameter targetAction = MoActionWithParameter.CBPOI_ASYMMETRIC_KEYS_CMP_START_CMP;
        final WorkflowMoParams moParams = new WorkflowMoParams();
        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_ALGORITHM, algorithm);
        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_CERTIFICATE_NAME, asymmetricKeyName);
        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_CMP_SERVER_GROUP, cmpServerGroupName);
        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_NAME, asymmetricKeyName);
        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_CHALLENGE, challenge, true);
        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_SUBJECT, subjectDn);

        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_TRUSTED_CERTS, trustedCerts);

        final WorkflowMoAction moAction = new WorkflowMoActionWithParams(asymmetricKeysCmpFdn, targetAction, moParams, 0);
        moActions.addTargetAction(moAction);
        final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
        nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);
    }

    /**
     * Prepares renew-cmp action.
     * 
     * @param task
     *            the task.
     * @param enrollmentInfo
     *            the enrollment info.
     * @param outputParams
     *            the output parameters.
     * @param moActions
     *            the MO actions.
     */
    private void prepareRenewCmpAction(final CbpOiPrepareOnlineEnrollmentTask task, final ScepEnrollmentInfoImpl enrollmentInfo,
            final Map<String, Serializable> outputParams, final WorkflowMoActions moActions) {

        final String asymmetricKeyCmpFdn = extractNotNullParamFromOutputParams(task, WorkflowOutputParameterKeys.ASYMMETRIC_KEY_CMP_FDN,
                outputParams);

        // Extract key size from enrollment info and convert it to node format.
        final String keySize = enrollmentInfo.getKeySize();
        final String algorithm = CbpOiAlgorithm.fromEnrollmentInfoKeySize(keySize).getNodeAlgorithm();
        nscsLogger.info(task, "From enrollment info : key size [{}] algorithm [{}]", keySize, algorithm);

        // Prepare renew-cmp action
        final MoActionWithParameter targetAction = MoActionWithParameter.CBPOI_ASYMMETRIC_KEY_CMP_RENEW_CMP;
        final WorkflowMoParams moParams = new WorkflowMoParams();
        moParams.addParam(ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP_ALGORITHM, algorithm);


        final WorkflowMoAction moAction = new WorkflowMoActionWithParams(asymmetricKeyCmpFdn, targetAction, moParams, 0);
        moActions.addTargetAction(moAction);
        final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
        nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);
    }

    /**
     * Extracts from the output parameters the not null value of the given parameter. An exception is thrown if the value is null or empty.
     * 
     * @param task
     *            the task.
     * @param the
     *            parameter to extract.
     * @param outputParams
     *            the output parameters.
     * @return the value of the specified parameter.
     */
    private String extractNotNullParamFromOutputParams(final CbpOiPrepareOnlineEnrollmentTask task, final WorkflowOutputParameterKeys param,
            final Map<String, Serializable> outputParams) {

        final String value = (String) outputParams.get(param.toString());
        if (value == null || value.isEmpty()) {
            final String errorMessage = String.format("Missing %s internal parameter", param.toString());
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From output params : {} [{}]", param.toString(), value);
        return value;
    }

    /**
     * Prepares the return value of the task handler serializing a WorkflowQueryTaskResult containing both the result of the preparation performed by
     * the task handler and the output parameters.
     * 
     * @param task
     *            the task.
     * @param prepareResult
     *            the result of the preparation performed by the task handler.
     * @param moActions
     *            the MO actions to be performed.
     * @param outputParams
     *            the output parameters.
     * @return the serialized result of the task handler.
     */
    private String serializePrepareOnlineEnrollmentResult(final CbpOiPrepareOnlineEnrollmentTask task, final String prepareResult,
            final WorkflowMoActions moActions,
            final Map<String, Serializable> outputParams) {

        final String message = String.format("serializing prepare online enrollment result [%s]", prepareResult);

        nscsLogger.debug(task, message);

        // Serialize MO actions in output parameters
        String serializedMoActions = null;
        try {
            serializedMoActions = NscsObjectSerializer.writeObject(moActions);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing MO actions";
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.MO_ACTIONS.toString(), serializedMoActions);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(prepareResult, outputParams);
        String encodedWfQueryTaskResult = null;
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = String.format("%s while %s", NscsLogger.stringifyException(e), message);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedWfQueryTaskResult;
    }
}
