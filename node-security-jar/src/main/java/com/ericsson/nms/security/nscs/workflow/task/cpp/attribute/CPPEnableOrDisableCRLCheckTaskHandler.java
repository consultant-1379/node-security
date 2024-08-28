/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CPPEnableOrDisableCRLCheckTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_ENABLE_OR_DISABLE_CRL_CHECK
 * </p>
 * <p>
 * Enable or Disable CRL Check of the target COM ECIM nodes
 * </p>
 *
 * @author xlakdag
 */
@WFTaskType(WorkflowTaskType.CPP_ENABLE_OR_DISABLE_CRL_CHECK)
@Local(WFTaskHandlerInterface.class)
public class CPPEnableOrDisableCRLCheckTaskHandler implements WFQueryTaskHandler<CPPEnableOrDisableCRLCheckTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService nscsCMReaderService;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NSCSCppNodeUtility nscsCppNodeUtility;;

    @Inject
    private NscsCMWriterService nscsCMWriterService;

    private static final String CRL_CHECK_ENABLED = "CRLCheckEnabled";
    private static final String CRL_CHECK_ACTIVATED = "ACTIVATED";
    private static final String CRL_CHECK_NOT_ENABLED = "CRLCheckNotEnabled";

    private static final String CRL_CHECK_DISABLED = "CRLCheckDisabled";
    private static final String CRL_CHECK_DEACTIVATED = "DEACTIVATED";
    private static final String CRL_CHECK_NOT_DISABLED = "CRLCheckNotDisabled";

    @Override
    public String processTask(final CPPEnableOrDisableCRLCheckTask cppEnableOrDisableCRLCheckTask) {
        nscsLogger.workFlowTaskHandlerStarted(cppEnableOrDisableCRLCheckTask);

        final NodeReference nodeReference = cppEnableOrDisableCRLCheckTask.getNode();
        final NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference);

        nscsLogger.info(cppEnableOrDisableCRLCheckTask, "From task : normalizable [" + normalizableNodeReference + "]");

        String result = null;

        if (cppEnableOrDisableCRLCheckTask.getCrlCheckStatus().equals(CRL_CHECK_ACTIVATED)) {
            result = enableCRLCheck(cppEnableOrDisableCRLCheckTask, normalizableNodeReference, cppEnableOrDisableCRLCheckTask.getCertType(),
                    cppEnableOrDisableCRLCheckTask.getCrlCheckStatus());

        } else if (cppEnableOrDisableCRLCheckTask.getCrlCheckStatus().equals(CRL_CHECK_DEACTIVATED)) {

            result = disableCRLCheck(cppEnableOrDisableCRLCheckTask, normalizableNodeReference, cppEnableOrDisableCRLCheckTask.getCertType(),
                    cppEnableOrDisableCRLCheckTask.getCrlCheckStatus());
        }

        final String successMessage = "Successfully completed : [" + result + "]";

        if (CRL_CHECK_ENABLED.equals(result) || CRL_CHECK_DISABLED.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(cppEnableOrDisableCRLCheckTask, successMessage);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(cppEnableOrDisableCRLCheckTask, successMessage, NscsLogger.ENABLE_DISABLE_FAILED);
        }

        return result;
    }

    private String enableCRLCheck(final CPPEnableOrDisableCRLCheckTask cppEnableOrDisableCRLCheckTask,
            final NormalizableNodeReference normalizableNodeReference, final String certType, final String crlCheckStatus) {

        try {
            nscsLogger.info("Starting of enableCRLCheck by getting Node Fdn MO {}", normalizableNodeReference.getFdn());

            updateMOForCRLCheck(cppEnableOrDisableCRLCheckTask, normalizableNodeReference, certType, crlCheckStatus);

            final String updateMessage = NscsLogger.stringifyUpdateParams("CRLCheck Enabled successfully for node {}",
                    normalizableNodeReference.getFdn());
            nscsLogger.workFlowTaskHandlerOngoing(cppEnableOrDisableCRLCheckTask, updateMessage);

            return CRL_CHECK_ENABLED;

        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyUpdateParams("Update of Enrollment mode in NetworkElementSecurity MO failed!",
                    e.getMessage());
            nscsLogger.workFlowTaskHandlerFinishedWithError(cppEnableOrDisableCRLCheckTask, errorMessage);

            return CRL_CHECK_NOT_ENABLED;

        }
    }

    private String disableCRLCheck(final CPPEnableOrDisableCRLCheckTask cppEnableOrDisableCRLCheckTask,
            final NormalizableNodeReference normalizableNodeReference, final String certType, final String crlCheckStatus) {

        try {
            nscsLogger.info("Starting of disableCRLCheck by getting Node Fdn MO {}", normalizableNodeReference.getFdn());

            updateMOForCRLCheck(cppEnableOrDisableCRLCheckTask, normalizableNodeReference, certType, crlCheckStatus);

            final String updateMessage = NscsLogger.stringifyUpdateParams("CRLCheck Disabled successfully for node {}",
                    normalizableNodeReference.getFdn());
            nscsLogger.workFlowTaskHandlerOngoing(cppEnableOrDisableCRLCheckTask, updateMessage);

            return CRL_CHECK_DISABLED;

        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyUpdateParams("Update of Enrollment mode in NetworkElementSecurity MO failed!",
                    e.getMessage());
            nscsLogger.workFlowTaskHandlerFinishedWithError(cppEnableOrDisableCRLCheckTask, errorMessage);

            return CRL_CHECK_NOT_DISABLED;

        }
    }

    private void updateMOForCRLCheck(final CPPEnableOrDisableCRLCheckTask cppEnableOrDisableCRLCheckTask,
            final NormalizableNodeReference normalizableNodeReference, final String certType, final String crlCheckStatus) {

        final CppManagedElement targetRootMo = (CppManagedElement) nscsCapabilityModelService.getMirrorRootMo(normalizableNodeReference);
        final String securityFdn = nscsCppNodeUtility.getSecurityFdn(normalizableNodeReference.getFdn(), targetRootMo);
        nscsLogger.info("Getting the securityFdn MO {} fdn", securityFdn);

        if (securityFdn == null) {
            final String errorMessage = "No Security MO for the given certificate type [" + certType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(cppEnableOrDisableCRLCheckTask, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        nscsCMWriterService.withSpecification(securityFdn).setAttribute(Security.CERT_REV_STATUS_CHECK, crlCheckStatus).updateMO();
    }
}