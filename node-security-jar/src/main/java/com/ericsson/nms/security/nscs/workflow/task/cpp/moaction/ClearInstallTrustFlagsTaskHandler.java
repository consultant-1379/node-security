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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ClearInstallTrustFlagsTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_FLAGS
 * </p>
 * <p>
 * Removes the installtrust flag
 * </p>
 * 
 * @author emaynes on 16/06/2014.
 */
@WFTaskType(WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_FLAGS)
@Local(WFTaskHandlerInterface.class)
public class ClearInstallTrustFlagsTaskHandler implements WFActionTaskHandler<ClearInstallTrustFlagsTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moActionService;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public void processTask(final ClearInstallTrustFlagsTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);

        final String actionMessage = String.format("action [%s]", MoActionWithoutParameter.Security_cancelInstallTrustedCertificates.getAction());
        nscsLogger.info(task, "Performing " + actionMessage);
        try {
            moActionService.performMOAction(normalizable.getFdn(), MoActionWithoutParameter.Security_cancelInstallTrustedCertificates);
        } catch (final Exception e) {
            final String errorMessage = String.format("Exc [%s] msg [%s] performing [%s]", e.getClass().getName(), e.getMessage(), actionMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully performed " + actionMessage);

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Install trust flag cleared.");
    }
}
