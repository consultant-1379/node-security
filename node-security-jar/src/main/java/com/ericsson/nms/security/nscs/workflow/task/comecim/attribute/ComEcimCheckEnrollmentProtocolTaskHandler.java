/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
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
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertMCapabilities.EnrollmentSupport;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckEnrollmentProtocolTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL.
 * </p>
 * <p>
 * Check, in CertMCapabilities MO, if enrollment mode is supported..
 * </p>
 *
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckEnrollmentProtocolTaskHandler implements WFQueryTaskHandler<ComEcimCheckEnrollmentProtocolTask>, WFTaskHandlerInterface {

    private static final String NOT_SUPPORTED = "NOT_SUPPORTED";
    private static final String SUPPORTED = "SUPPORTED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Override
    public String processTask(final ComEcimCheckEnrollmentProtocolTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        nscsLogger.info(task, "From task : normalizable [" + normalizable + "]");
        final String mirrorRootFdn = normalizable.getFdn();

        // Extract output parameters possibly set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.debug(task, "Output params not yet set!");
        }

        final EnrollmentMode enrollmentMode = nscsNodeUtility.getEnrollmentMode(task.getEnrollmentMode(), normalizable);
        nscsLogger.info(task, "From task : enrollment mode [" + enrollmentMode + "]");

        final Mo rootMo = capabilityService.getMirrorRootMo(normalizable);

        // Read CertMCapabilities MO
        final Mo certMCapabilitiesMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.certMCapabilities;
        final Map<String, Object> certMCapabilitiesAttributes = new HashMap<String, Object>();
        final String requestedAttrs[] = { ModelDefinition.CertMCapabilities.ENROLLMENT_SUPPORT,
                ModelDefinition.CertMCapabilities.FINGERPRINT_SUPPORT };
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, certMCapabilitiesMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        final String certMCapabilitiesFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMCapabilitiesMo, certMCapabilitiesAttributes,
                requestedAttrs);
        if (certMCapabilitiesFdn == null || certMCapabilitiesFdn.isEmpty() || certMCapabilitiesAttributes == null
                || certMCapabilitiesAttributes.isEmpty()) {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoAttributeException(normalizable.getFdn(), certMCapabilitiesMo.type(),
                    ModelDefinition.CertMCapabilities.ENROLLMENT_SUPPORT);
        }
        nscsLogger.info(task, "Successfully read " + readMessage + " : attrs [" + certMCapabilitiesAttributes + "]");

        final EnrollmentSupport enrollmentSupport = EnrollmentSupport.fromEnrollmentMode(enrollmentMode);
        @SuppressWarnings("unchecked")
        final List<String> enrollmentSupportValue = (List<String>) certMCapabilitiesAttributes
                .get(ModelDefinition.CertMCapabilities.ENROLLMENT_SUPPORT);
        if (enrollmentSupportValue.contains(enrollmentSupport.name())) {
            return enrollmentModeSupported(task, enrollmentMode.name(), outputParams);
        } else {
            return enrollmentModeNotSupported(task, enrollmentMode.name(), outputParams);
        }
    }

    /**
     *
     * @param task
     * @param enrollmentMode
     * @param outputParams
     * @return
     */
    private String enrollmentModeSupported(final ComEcimCheckEnrollmentProtocolTask task, final String enrollmentMode,
            final Map<String, Serializable> outputParams) {
        final String state = SUPPORTED;
        return serializeResult(task, state, enrollmentMode, outputParams);
    }

    /**
     *
     * @param task
     * @param enrollmentMode
     * @param outputParams
     * @return
     */
    private String enrollmentModeNotSupported(final ComEcimCheckEnrollmentProtocolTask task, final String enrollmentMode,
            final Map<String, Serializable> outputParams) {
        final String state = NOT_SUPPORTED;
        return serializeResult(task, state, enrollmentMode, outputParams);
    }

    /**
     * @param task
     * @param result
     * @param enrollmentMode
     * @param outputParams
     * @return It may return null string
     */
    private String serializeResult(final ComEcimCheckEnrollmentProtocolTask task, final String result, final String enrollmentMode,
            Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outputParams = new HashMap<String, Serializable>();
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), enrollmentMode);
        nscsLogger.info(task, "Added ENROLLMENT_MODE [" + enrollmentMode + "] to output params");
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        nscsLogger.debug(task, "Serializing result [" + result + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessageSerialize = NscsLogger.stringifyException(e) + " while serializing object";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessageSerialize);
            throw new UnexpectedErrorException(errorMessageSerialize);
        }
        final String successMessage = "Successfully completed : requested enrollment mode [" + enrollmentMode + "] is [" + result + "]";
        if (NOT_SUPPORTED.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, NscsLogger.NOT_SUPPORTED);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }
        return encodedWfQueryTaskResult;
    }
}
