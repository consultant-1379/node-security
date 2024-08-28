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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.model.NtpKeyData;
import com.ericsson.nms.security.nscs.ntp.utility.NtpConfigureStatusSender;
import com.ericsson.nms.security.nscs.ntp.utility.NtpUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.NtpConfigureFailureStatusSenderTask;

/**
 * Task handler for WorkflowTaskType.NTP_CONFIGURE_FAILURE_STATUS_SENDER
 *
 * Remove the NTP key data mapping for the node in case of any failure
 *
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.NTP_CONFIGURE_FAILURE_STATUS_SENDER)
@Local(WFTaskHandlerInterface.class)
public class NtpConfigureFailureStatusSenderTaskHandler implements WFQueryTaskHandler<NtpConfigureFailureStatusSenderTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NtpConfigureStatusSender ntpConfigStatusSender;

    @Inject
    NtpUtility ntpUtility;

    @Override
    public String processTask(final NtpConfigureFailureStatusSenderTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);
        final Map<String, Serializable> outputParams = getOutputParams(task);

        final NtpKeyData ntpKeyData = ntpUtility.getNtpKeyDataFromOutPutParams(task, outputParams, "Ntp Configure Status");

        ntpConfigStatusSender.sendNtpConfigureStatus(task, "FAILED");

        final String ntpConfigFailureMsg = "NTP configure failed for Node: " + task.getNode().getFdn() + " with NtpKeyId: " + ntpKeyData.getId();

        nscsLogger.info(task, ntpConfigFailureMsg);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, ntpConfigFailureMsg);

        return "SUCCESS";
    }

    public Map<String, Serializable> getOutputParams(final NtpConfigureFailureStatusSenderTask task) {

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters in NtpConfigureFailureStatusSenderTaskHandler";
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Missing Ntp configure status out put parameters.");
        }

        return outputParams;
    }

}
