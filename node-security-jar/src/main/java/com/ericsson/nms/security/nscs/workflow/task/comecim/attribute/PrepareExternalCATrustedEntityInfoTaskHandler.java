/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.PrepareExternalCATrustedEntityInfoTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO
 * </p>
 * 
 * This class is used to prepare the trusted entity information for the External CA.
 * 
 */
@WFTaskType(WorkflowTaskType.PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO)
@Local(WFTaskHandlerInterface.class)
public class PrepareExternalCATrustedEntityInfoTaskHandler implements WFQueryTaskHandler<PrepareExternalCATrustedEntityInfoTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Override
    public String processTask(final PrepareExternalCATrustedEntityInfoTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final Map<String, Serializable> outputParams = task.getOutputParams();
        /*
         * Extract trusted entities info
         */
        HashSet<NscsTrustedEntityInfo> trustedEntitiesInfo = new HashSet<>();
        final List<Map<String, String>> trustedCACertificatesList = task.getExternalTrustedCACertificateInfo();
        for (Map<String, String> trustedCACertificates : trustedCACertificatesList) {
            NscsTrustedEntityInfo trustedEntityInfo = new NscsTrustedEntityInfo("",
                    (BigInteger) CertDetails.convertSerialNumberToDecimalFormat(trustedCACertificates.get("certificateSerialNumber")),
                    (String) trustedCACertificates.get("trustedCACertIssuerDn"), (String) trustedCACertificates.get("tdpsUrl"));

            trustedEntitiesInfo.add(trustedEntityInfo);
        }

        nscsLogger.info(task, "Got from XML inputs no of trusted entities info [" + trustedEntitiesInfo.size() + "] ");

        return serializeResult(task, trustedEntitiesInfo, outputParams);
    }

    private String serializeResult(final PrepareExternalCATrustedEntityInfoTask task, final HashSet<NscsTrustedEntityInfo> trustedEntitiesInfo,
            final Map<String, Serializable> outputParams) {
        Map<String, Serializable> outParams = outputParams;
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params");
            outParams = new HashMap<>();
        }

        /*
         * Serialize trusted entities info in output parameters
         */
        if (trustedEntitiesInfo != null && !trustedEntitiesInfo.isEmpty()) {
            String serializedtrustedEntitiesInfo = null;
            try {
                serializedtrustedEntitiesInfo = NscsObjectSerializer.writeObject(trustedEntitiesInfo);
            } catch (final IOException e1) {
                final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing trusted entities info";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            outParams.put(WorkflowOutputParameterKeys.TRUSTED_ENTITY_INFO.toString(), serializedtrustedEntitiesInfo);
        }

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully prepared External CA trusted entity info";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;

    }

}
