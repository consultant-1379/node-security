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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CleanupM2MUserAndSmrsTask;

/**
 * <p>
 * Request to clean M2M user and SMRS files / directory after an install of trusted certificates
 * </p>
 *
 * @author enmadmin
 */
@WFTaskType(WorkflowTaskType.CPP_CLEANUP_M2M_USER_AND_SMRS)
@Local(WFTaskHandlerInterface.class)
public class CleanupM2MUserAndSmrsTaskHandler implements WFActionTaskHandler<CleanupM2MUserAndSmrsTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    CppSecurityService securityService;

    @Inject
    NscsCMReaderService readerService;

    @Override
    public void processTask(final CleanupM2MUserAndSmrsTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        nscsLogger.workFlowTaskHandlerOngoing(task, "Perform CleanupM2MUserAndSmrsTask for node: " + node);
        securityService.cancelSmrsAccountForNode(normalizable.getName(), normalizable.getNeType());

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Cleanup M2M user and SMRS account completed.");
    }
}
