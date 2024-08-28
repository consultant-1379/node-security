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
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiConfigureNodeCredentialServicesTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES.
 * </p>
 * <p>
 * Configure all possible services using the node credential that has been enrolled.
 * </p>
 */
@WFTaskType(WorkflowTaskType.CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES)
@Local(WFTaskHandlerInterface.class)
public class CbpOiConfigureNodeCredentialServicesTaskHandler implements WFQueryTaskHandler<CbpOiConfigureNodeCredentialServicesTask>, WFTaskHandlerInterface {

    private static final String PASSED = "PASSED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public String processTask(final CbpOiConfigureNodeCredentialServicesTask task) {

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
        String configureResult = null;
        try {
            configureResult = configureNodeCredentialServices(task, normalizableNodeRef, outputParams);
            result = serializeConfigureNodeCredentialServicesResult(task, configureResult, outputParams);
        } catch (final Exception e) {
            final String errorMessage = e.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }
        
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", configureResult);
        return result;
    }

    /**
     * Configure all possible services using the node credential that has been enrolled.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param outputParams
     *            the output parameters.
     * @return the result of the configuration.
     */
    private String configureNodeCredentialServices(final CbpOiConfigureNodeCredentialServicesTask task,
            final NormalizableNodeReference normalizableNodeRef,
            final Map<String, Serializable> outputParams) {

        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(task.getTrustedCertCategory());
        nscsLogger.info(task, "From task : certificate type [{}]", certificateType);

        final String isTrustDistributionRequired = task.getIsTrustDistributionRequired();
        nscsLogger.info(task, "From task : is trust distribute required [{}]", isTrustDistributionRequired);

        final String mirrorRootFdn = normalizableNodeRef.getFdn();
        final String targetType = normalizableNodeRef.getNeType();
        final String targetModelIdentity = normalizableNodeRef.getOssModelIdentity();
        nscsLogger.info(task, "From task : mirrorRootFdn [{}] targetType [{}] targetModelIdentity [{}]", mirrorRootFdn, targetType,
                targetModelIdentity);

        // Extract node credential FDN from output parameters.
        // It shall have been already set!
        final String asymmetricKeyCmpFdn = (String) outputParams.get(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_CMP_FDN.toString());
        nscsLogger.info(task, "From output params : asymmetric-key cmp FDN [{}]", asymmetricKeyCmpFdn);

        final String asymmetricKeyName = (String) outputParams.get(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_NAME.toString());
        nscsLogger.info(task, "From output params : asymmetric-key name [{}]", asymmetricKeyName);

        return PASSED;
    }

    /**
     * Prepares the return value of the task handler serializing a WorkflowQueryTaskResult containing both the result of the configuration performed
     * by the task handler and the output parameters.
     * 
     * @param task
     *            the task.
     * @param configureResult
     *            the result of the configuration performed by the task handler.
     * @param outputParams
     *            the output parameters.
     * @return the serialized result of the task handler.
     */
    private String serializeConfigureNodeCredentialServicesResult(final CbpOiConfigureNodeCredentialServicesTask task, final String configureResult,
            final Map<String, Serializable> outputParams) {

        final String message = String.format("serializing node credential services configuration result [%s]", configureResult);

        nscsLogger.debug(task, message);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(configureResult, outputParams);
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
