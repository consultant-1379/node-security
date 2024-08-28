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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadTrustedCertificateInstallationFailureTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_INTALLATION_FAILURE
 * </p>
 * <p>
 * Fetches the value of the attribute TRUSTED_CERTIFICATE_INSTALLATION_FAILURE in the Security MO
 * </p>
 *
 * @author emaynes on 18/06/2014.
 */
@WFTaskType(WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_INTALLATION_FAILURE)
@Local(WFTaskHandlerInterface.class)
public class ReadTrustedCertificateInstallationFailureTaskHandler implements WFQueryTaskHandler<ReadTrustedCertificateInstallationFailureTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public String processTask(final ReadTrustedCertificateInstallationFailureTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);

        String resultFlag = "";

        final String getMessage = String.format("attribute [%s] of %s MO", Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE,
                Model.ME_CONTEXT.managedElement.systemFunctions.security.type());

        nscsLogger.info(task, "Reading " + getMessage);
        final CmResponse cmFailureFlagAttributeResponse = readerService.getMOAttribute(normNode,
                Model.ME_CONTEXT.managedElement.systemFunctions.security.type(), Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE);

        nscsLogger.debug(task, "Read " + getMessage + ": response [" + cmFailureFlagAttributeResponse + "]");

        if (cmFailureFlagAttributeResponse == null || cmFailureFlagAttributeResponse.getCmObjects() == null
                || cmFailureFlagAttributeResponse.getCmObjects().isEmpty()) {

            final String errorMessage = String.format("Failed read {} : empty or null response", getMessage);

            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);

            throw new MissingMoAttributeException(node.getFdn(), Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                    Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE);

        } else if (cmFailureFlagAttributeResponse.getCmObjects().size() > 1) {

            final String errorMessage = String.format("Failed read {} : too many results [{}] in response", getMessage,
                    cmFailureFlagAttributeResponse.getCmObjects().size());

            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);

            throw new UnexpectedErrorException(errorMessage);

        } else {

            final Boolean flagValue = (Boolean) cmFailureFlagAttributeResponse.getCmObjects().iterator().next().getAttributes()
                    .get(Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE);

            nscsLogger.info(task, "Successfully read " + getMessage + ": value [" + flagValue + "]");

            if (flagValue != null) {
                resultFlag = flagValue.toString();
            }
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Return trustedCertificateInstallationFailure: " + resultFlag);

        return resultFlag;
    }
}
