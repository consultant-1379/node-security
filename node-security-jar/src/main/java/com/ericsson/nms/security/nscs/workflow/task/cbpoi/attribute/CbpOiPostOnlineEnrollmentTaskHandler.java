/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
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
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPostOnlineEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CBP_OI_POST_ONLINE_ENROLLMENT.
 * </p>
 * <p>
 * Perform operations requested after a successful action performed to trigger the online enrollment on the node.
 * </p>
 */
@WFTaskType(WorkflowTaskType.CBP_OI_POST_ONLINE_ENROLLMENT)
@Local(WFTaskHandlerInterface.class)
public class CbpOiPostOnlineEnrollmentTaskHandler implements WFQueryTaskHandler<CbpOiPostOnlineEnrollmentTask>, WFTaskHandlerInterface {

    private static final String DONE = "DONE";
    private static final String NUM_OF_RETRIES = "3";

    @Inject
    private NscsLogger nscsLogger;

    @Override
    public String processTask(final CbpOiPostOnlineEnrollmentTask task) {

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
            final String prepareResult = postOnlineEnrollment(task, outputParams);
            result = serializePostOnlineEnrollmentResult(task, prepareResult, outputParams);
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", prepareResult);
        } catch (final Exception e) {
            final String errorMessage = e.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }
        return result;
    }

    /**
     * Prepares the operations required after a successful action performed to trigger the online enrollment on the node.
     * 
     * @param task
     *            the task.
     * @param outputParams
     *            the output parameters.
     * @return the result of the preparation of the operations.
     */
    private String postOnlineEnrollment(final CbpOiPostOnlineEnrollmentTask task, final Map<String, Serializable> outputParams) {

        prepareRetriesOfRestoreRenewalMode(task, outputParams);

        return DONE;
    }

    /**
     * Prepares output parameters related to retries of restore renewal mode task.
     * 
     * @param task
     *            the task.
     * @param outputParams
     *            the output parameters.
     */
    private void prepareRetriesOfRestoreRenewalMode(final CbpOiPostOnlineEnrollmentTask task, final Map<String, Serializable> outputParams) {

        nscsLogger.info(task, "Adding output params: max [{}] and remaining [{}] retries", NUM_OF_RETRIES, NUM_OF_RETRIES);
        outputParams.put(WorkflowOutputParameterKeys.MAX_NUM_OF_RETRIES.toString(), NUM_OF_RETRIES);
        outputParams.put(WorkflowOutputParameterKeys.REMAINING_NUM_OF_RETRIES.toString(), NUM_OF_RETRIES);

    }

    /**
     * Prepares the return value of the task handler serializing a WorkflowQueryTaskResult containing both the result of the operations performed by
     * the task handler and the output parameters.
     * 
     * @param task
     *            the task.
     * @param postResult
     *            the result of the operations performed by the task handler.
     * @param outputParams
     *            the output parameters.
     * @return the serialized result of the task handler.
     */
    private String serializePostOnlineEnrollmentResult(final CbpOiPostOnlineEnrollmentTask task, final String postResult,
            final Map<String, Serializable> outputParams) {

        final String message = String.format("serializing post online enrollment result [%s]", postResult);

        nscsLogger.debug(task, message);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(postResult, outputParams);
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
