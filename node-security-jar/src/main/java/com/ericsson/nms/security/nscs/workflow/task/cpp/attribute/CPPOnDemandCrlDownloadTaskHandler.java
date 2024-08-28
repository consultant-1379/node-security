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
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CPPOnDemandCrlDownloadTask;

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
@WFTaskType(WorkflowTaskType.CPP_ON_DEMAND_CRL_DOWNLOAD)
@Local(WFTaskHandlerInterface.class)
public class CPPOnDemandCrlDownloadTaskHandler implements WFQueryTaskHandler<CPPOnDemandCrlDownloadTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService nscsCMReaderService;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NSCSCppNodeUtility nscsCppNodeUtility;

    @Inject
    private NscsCMWriterService nscsCMWriterService;

    private static final String CRL_DOWNLOAD_INTERVAL_UPDATED = "CRLDownloadIntervalUpdated";
    private static final String CRL_DOWNLOAD_INTERVAL_NOT_UPDATED = "CRLDownloadIntervalNotUpdated";
    private static final Integer CRL_ON_DEMAND_UPDATE_INTERVAL_DEFAULT = 60;

    @Override
    public String processTask(final CPPOnDemandCrlDownloadTask cppOnDemandCrlDownloadTask) {
        nscsLogger.workFlowTaskHandlerStarted(cppOnDemandCrlDownloadTask);

        final NodeReference nodeReference = cppOnDemandCrlDownloadTask.getNode();
        final NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference);

        nscsLogger.info(cppOnDemandCrlDownloadTask, "From task : normalizable [" + normalizableNodeReference + "]");

        //Get SecurityFdn
        final Mo rootMo = nscsCapabilityModelService.getMirrorRootMo(normalizableNodeReference);
        final String securityFdn = nscsCppNodeUtility.getSecurityFdn(normalizableNodeReference.getFdn(), rootMo);

        if (securityFdn == null) {
            final String errorMessage = "No Security MO for the given node [" + normalizableNodeReference.getName() + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(cppOnDemandCrlDownloadTask, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        String result = null;

        Integer existingIntervalValue = 0;

        try {

            existingIntervalValue = getCrlOnDemandUpdateInterval(securityFdn);

        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e)
                    + "while Reading of crlOnDemandUpdateInterval in NetworkElement Security MO ";
            nscsLogger.workFlowTaskHandlerFinishedWithError(cppOnDemandCrlDownloadTask, errorMessage);

            return CRL_DOWNLOAD_INTERVAL_NOT_UPDATED;

        }

        if (existingIntervalValue == 0) {
            result = updateMOForOnDemandCRLUpdate(cppOnDemandCrlDownloadTask, securityFdn, CRL_ON_DEMAND_UPDATE_INTERVAL_DEFAULT);

        } else {

            result = updateMOForOnDemandCRLUpdate(cppOnDemandCrlDownloadTask, securityFdn, existingIntervalValue);
        }

        final String successMessage = "Successfully completed : [" + result + "]";

        if (CRL_DOWNLOAD_INTERVAL_UPDATED.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(cppOnDemandCrlDownloadTask, successMessage);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(cppOnDemandCrlDownloadTask, successMessage,
                    NscsLogger.CRL_DOWNLOAD_INTERVAL_NOT_UPDATED);
        }

        return result;
    }

    private Integer getCrlOnDemandUpdateInterval(final String securityFdn) {

        nscsLogger.info("Starting of CPPOnDemandCrlDownloadTaskHandler::getCrlOnDemandUpdateInterval by getting securityFdn {}", securityFdn);

        final MoObject moObject = nscsCMReaderService.getMoObjectByFdn(securityFdn);
        final Integer crlOnDemandUpdateInterval = moObject.getAttribute(Security.CRL_ON_DEMAND_UPDATE_INTERVAL);

        nscsLogger.info("crlOnDemandUpdateInterval in CPPOnDemandCrlDownloadTaskHandler::getCrlOnDemandUpdateInterval method : [{}]",
                crlOnDemandUpdateInterval);

        return crlOnDemandUpdateInterval;

    }

    private String updateMOForOnDemandCRLUpdate(final CPPOnDemandCrlDownloadTask cppOnDemandCrlDownloadTask, final String securityFdn,
            final Integer crlOnDemandUpdateInterval) {

        final NscsCMWriterService.WriterSpecificationBuilder securityMoSpec = nscsCMWriterService.withSpecification();
        securityMoSpec.setFdn(securityFdn);
        securityMoSpec.setAttribute(Security.CRL_ON_DEMAND_UPDATE_INTERVAL, crlOnDemandUpdateInterval);
        final String updateMessage = NscsLogger.stringifyUpdateParams("Security", securityFdn);
        nscsLogger.info(cppOnDemandCrlDownloadTask, "Updating " + updateMessage);
        try {
            securityMoSpec.updateMO();

            nscsLogger.workFlowTaskHandlerOngoing(cppOnDemandCrlDownloadTask, "Successfully updated " + updateMessage);
            return CRL_DOWNLOAD_INTERVAL_UPDATED;

        } catch (final Exception e) {

            final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(cppOnDemandCrlDownloadTask, errorMessage);

            return CRL_DOWNLOAD_INTERVAL_NOT_UPDATED;

        }
    }
}