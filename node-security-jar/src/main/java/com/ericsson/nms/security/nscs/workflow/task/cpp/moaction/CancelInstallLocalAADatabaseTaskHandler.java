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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CancelInstallLocalAADatabaseTask;

/**
 * Task handler for WorkflowTaskType.CPP_CANCEL_INSTALL_LOCALAADATABASE to set the localAADatabaseInstallationFailure attribute value to false, if the installLocalAADatabase action failed
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.CPP_CANCEL_INSTALL_LAAD_FAILURE)
@Local(WFTaskHandlerInterface.class)
public class CancelInstallLocalAADatabaseTaskHandler implements WFActionTaskHandler<CancelInstallLocalAADatabaseTask>, WFTaskHandlerInterface {

    @Inject
    private MOActionService moActionService;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsLogger nscsLogger;

    @Override
    public void processTask(CancelInstallLocalAADatabaseTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);

        final String actionMessage = String.format("action [%s]", MoActionWithoutParameter.SECURITY_CANCEL_INSTALL_LOCAL_AA_DATABASE.getAction());
        nscsLogger.info(task, "Performing " + actionMessage);
        try {
            moActionService.performMOAction(normalizable.getFdn(), MoActionWithoutParameter.SECURITY_CANCEL_INSTALL_LOCAL_AA_DATABASE);
        } catch (final Exception exception) {
            throw new WorkflowTaskException(exception);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully performed" + actionMessage);

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Install LAAD failure flag cleared.");
    }

}
