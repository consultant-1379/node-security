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
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute;

import java.io.Serializable;
import java.util.*;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.*;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiRemoveTrustTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

@WFTaskType(WorkflowTaskType.CBP_OI_REMOVE_TRUST)
@Local(WFTaskHandlerInterface.class)
public class CbpOiRemoveTrustTaskHandler implements WFQueryTaskHandler<CbpOiRemoveTrustTask>, WFTaskHandlerInterface {

    private static final String DELETE_FAILED = WFTaskResult.FALSE.getTaskResult();
    private static final String DELETE_SUCCESS = WFTaskResult.TRUE.getTaskResult();

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCMWriterService nscsCMWriterService;

    @Override
    public String processTask(CbpOiRemoveTrustTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        String result = DELETE_FAILED;
        // Extract output parameters set by the previous handlers.
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        // Extract trusted certificate Fdn's from the output parameters.

        final String serializedTrustedCertificateFdns = (String) outputParams.get(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString());

        List<String> certificateMoFdns = NscsObjectSerializer.readObject(serializedTrustedCertificateFdns);
        if (certificateMoFdns == null || certificateMoFdns.isEmpty()) {
            final String errorMessage = "Missing trusted certificate FDN parameter";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From output params : CertificateMoFdn's [" + certificateMoFdns + "]");
        for (String certificateMoFdn : certificateMoFdns) {
            nscsCMWriterService.deleteMo(certificateMoFdn);
            if (readerService.getMoObjectByFdn(certificateMoFdn) != null) {
                result = DELETE_FAILED;
                break;
            }
            result = DELETE_SUCCESS;
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
