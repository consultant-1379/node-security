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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.types.CrlCheckCommand;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimEnableOrDisableCRLCheckTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_ENABLE_OR_DISABLE_CRL_CHECK
 * </p>
 * <p>
 * Enable or Disable CRL Check of the target COM ECIM nodes
 * </p>
 *
 * @author xchowja
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_ENABLE_OR_DISABLE_CRL_CHECK)
@Local(WFTaskHandlerInterface.class)
public class ComEcimEnableOrDisableCRLCheckTaskHandler implements WFQueryTaskHandler<ComEcimEnableOrDisableCRLCheckTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService nscsCMReaderService;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;;

    @Inject
    private NscsCMWriterService nscsCMWriterService;

    private static final String CRL_CHECK_ENABLED = "CRLCheckEnabled";
    private static final String CRL_CHECK_ACTIVATED = "ACTIVATED";
    private static final String CRL_CHECK_NOT_ENABLED = "CRLCheckNotEnabled";

    private static final String CRL_CHECK_DISABLED = "CRLCheckDisabled";
    private static final String CRL_CHECK_DEACTIVATED = "DEACTIVATED";
    private static final String CRL_CHECK_NOT_DISABLED = "CRLCheckNotDisabled";

    @Override
    public String processTask(final ComEcimEnableOrDisableCRLCheckTask comEcimEnableOrDisableCRLCheckTask) {
        nscsLogger.workFlowTaskHandlerStarted(comEcimEnableOrDisableCRLCheckTask);

        final NodeReference nodeReference = comEcimEnableOrDisableCRLCheckTask.getNode();
        final NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference);

        nscsLogger.info(comEcimEnableOrDisableCRLCheckTask, "From task : normalizable [" + normalizableNodeReference + "]");

        String result = null;

        if (comEcimEnableOrDisableCRLCheckTask.getCrlCheckStatus().equals(CRL_CHECK_ACTIVATED)) {
            result = enableCRLCheck(comEcimEnableOrDisableCRLCheckTask, normalizableNodeReference, comEcimEnableOrDisableCRLCheckTask.getCertType(),
                    comEcimEnableOrDisableCRLCheckTask.getCrlCheckStatus());

        } else if (comEcimEnableOrDisableCRLCheckTask.getCrlCheckStatus().equals(CRL_CHECK_DEACTIVATED)) {

            result = disableCRLCheck(comEcimEnableOrDisableCRLCheckTask, normalizableNodeReference, comEcimEnableOrDisableCRLCheckTask.getCertType(),
                    comEcimEnableOrDisableCRLCheckTask.getCrlCheckStatus());
        }

        final String successMessage = "Successfully completed : [" + result + "]";

        if (CRL_CHECK_ENABLED.equals(result) || CRL_CHECK_DISABLED.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(comEcimEnableOrDisableCRLCheckTask, successMessage);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(comEcimEnableOrDisableCRLCheckTask, successMessage, NscsLogger.ENABLE_DISABLE_FAILED);
        }

        return result;
    }

    private String enableCRLCheck(final ComEcimEnableOrDisableCRLCheckTask comEcimEnableOrDisableCRLCheckTask,
            final NormalizableNodeReference normalizableNodeReference, final String certType, final String crlCheckStatus) {

        try {
            nscsLogger.info("Starting of enableCRLCheck by getting Node Fdn MO {}", normalizableNodeReference.getFdn());

            updateMOForCRLCheck(comEcimEnableOrDisableCRLCheckTask, normalizableNodeReference, certType, crlCheckStatus);

            final String updateMessage = NscsLogger.stringifyUpdateParams("CRLCheck Enabled successfully for node {}",
                    normalizableNodeReference.getFdn());
            nscsLogger.workFlowTaskHandlerOngoing(comEcimEnableOrDisableCRLCheckTask, updateMessage);

            return CRL_CHECK_ENABLED;

        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyUpdateParams("Update of Enrollment mode in NetworkElementSecurity MO failed!",
                    e.getMessage());
            nscsLogger.workFlowTaskHandlerFinishedWithError(comEcimEnableOrDisableCRLCheckTask, errorMessage);

            return CRL_CHECK_NOT_ENABLED;

        }
    }

    private String disableCRLCheck(final ComEcimEnableOrDisableCRLCheckTask comEcimEnableOrDisableCRLCheckTask,
            final NormalizableNodeReference normalizableNodeReference, final String certType, final String crlCheckStatus) {

        try {
            nscsLogger.info("Starting of disableCRLCheck by getting Node Fdn MO {}", normalizableNodeReference.getFdn());

            updateMOForCRLCheck(comEcimEnableOrDisableCRLCheckTask, normalizableNodeReference, certType, crlCheckStatus);

            final String updateMessage = NscsLogger.stringifyUpdateParams("CRLCheck Disabled successfully for node {}",
                    normalizableNodeReference.getFdn());
            nscsLogger.workFlowTaskHandlerOngoing(comEcimEnableOrDisableCRLCheckTask, updateMessage);

            return CRL_CHECK_DISABLED;

        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyUpdateParams("Update of Enrollment mode in NetworkElementSecurity MO failed!",
                    e.getMessage());
            nscsLogger.workFlowTaskHandlerFinishedWithError(comEcimEnableOrDisableCRLCheckTask, errorMessage);

            return CRL_CHECK_NOT_DISABLED;

        }
    }

    private void updateMOForCRLCheck(final ComEcimEnableOrDisableCRLCheckTask enableOrDisableCRLCheckTask,
            final NormalizableNodeReference normalizableNodeReference, final String certType, final String crlCheckStatus) {

        final ComEcimManagedElement targetRootMo = (ComEcimManagedElement) nscsCapabilityModelService.getMirrorRootMo(normalizableNodeReference);

        if (CrlCheckCommand.ALL.equals(certType)) {

            String trustCategoryIpsecFdn = null;
            String trustCategoryOamFdn = null;
            trustCategoryIpsecFdn = nscsComEcimNodeUtility.getTrustCategoryFdn(normalizableNodeReference.getFdn(), targetRootMo, "IPSEC",
                    normalizableNodeReference);
            trustCategoryOamFdn = nscsComEcimNodeUtility.getTrustCategoryFdn(normalizableNodeReference.getFdn(), targetRootMo, "OAM",
                    normalizableNodeReference);

            nscsLogger.info("Getting the trustCategoryFdn MO {} , {} fdn", trustCategoryIpsecFdn, trustCategoryOamFdn);

            if (trustCategoryIpsecFdn == null || trustCategoryOamFdn == null) {
                final String errorMessage = "No TrustCategory MO for the given certificate type [" + certType + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(enableOrDisableCRLCheckTask, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            } else {
                nscsCMWriterService.withSpecification(trustCategoryOamFdn).setAttribute(TrustCategory.CRL_CHECK, crlCheckStatus).updateMO();
                nscsCMWriterService.withSpecification(trustCategoryIpsecFdn).setAttribute(TrustCategory.CRL_CHECK, crlCheckStatus).updateMO();
            }
        } else {
            String trustCategoryFdn = null;
            trustCategoryFdn = nscsComEcimNodeUtility.getTrustCategoryFdn(normalizableNodeReference.getFdn(), targetRootMo, certType,
                    normalizableNodeReference);

            nscsLogger.info("Getting the trustCategoryFdn MO {} fdn", trustCategoryFdn);

            if (trustCategoryFdn == null) {
                final String errorMessage = "No TrustCategory MO for the given certificate type [" + certType + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(enableOrDisableCRLCheckTask, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsCMWriterService.withSpecification(trustCategoryFdn).setAttribute(TrustCategory.CRL_CHECK, crlCheckStatus).updateMO();
        }
    }
}