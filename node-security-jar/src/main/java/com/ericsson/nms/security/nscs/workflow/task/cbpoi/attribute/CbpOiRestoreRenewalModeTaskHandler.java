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
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskTimeoutException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiRestoreRenewalModeTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

/**
 * <p>
 * Task handler for WorkflowTaskType.CBP_OI_RESTORE_RENEWAL_MODE.
 * </p>
 * <p>
 * Restore the renewal mode of the node credential to the original value or to automatic if this is the first configuration.
 * </p>
 */
@WFTaskType(WorkflowTaskType.CBP_OI_RESTORE_RENEWAL_MODE)
@Local(WFTaskHandlerInterface.class)
public class CbpOiRestoreRenewalModeTaskHandler implements WFQueryTaskHandler<CbpOiRestoreRenewalModeTask>, WFTaskHandlerInterface {

    private static final String DONE = "DONE";
    private static final String ONGOING = "ONGOING";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public String processTask(final CbpOiRestoreRenewalModeTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(task.getNode());

        // Extract output parameters that shall have been already set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        String result = null;
        try {
            final String restoreResult = restoreRenewalMode(task, normalizableNodeRef, outputParams);
            result = serializeRestoreRenewalModeResult(task, restoreResult, outputParams);
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", restoreResult);
        } catch (final Exception e) {
            final String errorMessage = e.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }
        return result;
    }

    /**
     * Restores the renewal mode of the node credential to the original value or to automatic if this is the first enrollment.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param outputParams
     *            the output parameters.
     * @return the result of the configuration.
     */
    private String restoreRenewalMode(final CbpOiRestoreRenewalModeTask task, final NormalizableNodeReference normalizableNodeRef,
            final Map<String, Serializable> outputParams) {

        String result = DONE;

        // Extract renewal mode from output parameters
        final String renewalMode = (String) outputParams.get(WorkflowOutputParameterKeys.RENEWAL_MODE.toString());
        nscsLogger.info(task, "From output params : renewal mode [{}]", renewalMode);

        if (ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_AUTOMATIC.equals(renewalMode)) {
            nscsLogger.info(task, "Changing renewal mode to [{}]", renewalMode);

            ManagedObject asymmetricKeyCmpMO = getAsymmetricKeyCmpMo(task, normalizableNodeRef, outputParams);
            if (asymmetricKeyCmpMO != null) {
                final Map<String, Object> asymmetricKeyCmpAttributes = new HashMap<>();
                asymmetricKeyCmpAttributes.put(ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_ATTR, renewalMode);
                nscsLogger.info(task, "Updating asymmetric-key cmp FDN [{}]", asymmetricKeyCmpMO.getFdn());
                nscsDpsUtils.updateMo(asymmetricKeyCmpMO, asymmetricKeyCmpAttributes);
            } else {
                result = checkIfNullAsymmetricKeyCmpIsAllowed(task, outputParams);
            }
        }

        return result;
    }

    /**
     * Finds the asymmetric-key cmp MO for the given node and output parameters.
     * 
     * @param task
     *            the task
     * @param normalizableNodeRef
     *            the node reference.
     * @param outputParams
     *            the output parameters.
     * @return the asymmetric-key cmp MO.
     */
    private ManagedObject getAsymmetricKeyCmpMo(final CbpOiRestoreRenewalModeTask task, final NormalizableNodeReference normalizableNodeRef,
            final Map<String, Serializable> outputParams) {

        ManagedObject asymmetricKeyCmpMO = null;

        final String asymmetricKeyCmpFdn = (String) outputParams.get(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_CMP_FDN.toString());
        nscsLogger.info(task, "From output params : asymmetric-key cmp FDN [{}]", asymmetricKeyCmpFdn);

        if (asymmetricKeyCmpFdn != null) {
            asymmetricKeyCmpMO = nscsDpsUtils.getMoByFdn(asymmetricKeyCmpFdn);
        } else {
            final String asymmetricKeyName = (String) outputParams.get(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_NAME.toString());
            nscsLogger.info(task, "From output params : asymmetric-key name [{}]", asymmetricKeyName);

            if (asymmetricKeyName != null) {
                final ManagedObject asymmetricKeyMO = nscsDpsUtils.getAsymmetricKeyMO(normalizableNodeRef, asymmetricKeyName);
                if (asymmetricKeyMO != null) {
                    final String unscopedMoType = ModelDefinition.CMP_TYPE;
                    asymmetricKeyCmpMO = nscsDpsUtils.getChildMo(asymmetricKeyMO, normalizableNodeRef, unscopedMoType,
                            CbpOiMoNaming.getName(unscopedMoType));
                }
            }
        }
        return asymmetricKeyCmpMO;
    }

    /**
     * Checks if the cmp MO under the asymmetric-key MO can be null. If it is allowed a retry mechanism is triggered.
     * 
     * Note also that the check is actually performed only if the start online enrollment action has been successfully performed (that is if the
     * output parameter REMAINING_NUM_OF_RETRIES has been set by a previous task handler) otherwise the check is not performed since either the MOs
     * will be never created or the output parameters could be not correctly set. In this case the task handler finishes immediately.
     * 
     * The cmp MO under the asymmetric-key MO can be null if this is the first enrollment: the AVCs related to the creations of asymmetric-key MO and
     * cmp MO under the asymmetric-key MO could be delayed respect of current task handler execution, so the restore renewal mode retry mechanism is
     * triggered.
     * 
     * If this is not the first enrollment, the MOs should be already present so the check throws an exception.
     * 
     * @param task
     *            the task.
     * @param outputParams
     *            the output parameters.
     * @return the result of the check.
     */
    private String checkIfNullAsymmetricKeyCmpIsAllowed(CbpOiRestoreRenewalModeTask task, Map<String, Serializable> outputParams) {

        String result = ONGOING;

        final String maxNumOfRetries = (String) outputParams.get(WorkflowOutputParameterKeys.MAX_NUM_OF_RETRIES.toString());
        String remainingNumOfRetries = (String) outputParams.get(WorkflowOutputParameterKeys.REMAINING_NUM_OF_RETRIES.toString());
        nscsLogger.info(task, "From output params : remaining [{}] of [{}] retries", remainingNumOfRetries, maxNumOfRetries);

        if (remainingNumOfRetries != null) {
            final String asymmetricKeyCmpFdn = (String) outputParams.get(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_CMP_FDN.toString());
            nscsLogger.info(task, "From output params : asymmetric-key cmp FDN [{}]", asymmetricKeyCmpFdn);

            final boolean isFirstEnrollment = (asymmetricKeyCmpFdn == null);

            if (isFirstEnrollment) {
                int numRetries = Integer.parseInt(remainingNumOfRetries);
                if (numRetries > 1) {
                    remainingNumOfRetries = String.valueOf(numRetries - 1);
                    outputParams.put(WorkflowOutputParameterKeys.REMAINING_NUM_OF_RETRIES.toString(), remainingNumOfRetries);
                    final String message = String.format("Ongoing : still [%s] of [%s] retry attempts", remainingNumOfRetries, maxNumOfRetries);
                    nscsLogger.workFlowTaskHandlerOngoing(task, message);
                } else {
                    final String errorMessage = String.format("Exceeded max num of [%s] retry attempts", maxNumOfRetries);
                    nscsLogger.error(task, errorMessage);
                    throw new WorkflowTaskTimeoutException(errorMessage);
                }
            } else {
                final String errorMsg = "Missing asymmetric-key cmp MO";
                nscsLogger.error(task, errorMsg);
                throw new MissingMoException(errorMsg);
            }
        } else {
            nscsLogger.info(task, "Skipping retry mechanism since online enrollment action was not successfully performed");
            result = DONE;
        }

        return result;
    }

    /**
     * Prepares the return value of the task handler serializing a WorkflowQueryTaskResult containing both the result of the preparation performed by
     * the task handler and the output parameters.
     * 
     * @param task
     *            the task.
     * @param restoreResult
     *            the result of the task handler.
     * @param outputParams
     *            the output parameters.
     * @return the serialized result of the task handler.
     */
    private String serializeRestoreRenewalModeResult(final CbpOiRestoreRenewalModeTask task, final String restoreResult,
            final Map<String, Serializable> outputParams) {

        final String message = String.format("serializing prepare online enrollment result [%s]", restoreResult);

        nscsLogger.debug(task, message);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(restoreResult, outputParams);
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
