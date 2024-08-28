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

import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.DeleteFilesSmrsTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_DELETE_FILES_SMRS
 * </p>
 * <p>
 * Delete (clean up) files of ipsec from SMRS.
 * </p>
 *
 * @author eanbuzz
 */
@WFTaskType(WorkflowTaskType.CPP_DELETE_FILES_SMRS)
@Local(WFTaskHandlerInterface.class)
public class DeleteFilesSmrsTaskHandler implements WFActionTaskHandler<DeleteFilesSmrsTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CppSecurityService securityService;

    @Inject
    NscsCMReaderService readerService;

    /**
     * Deletes the files from the SMRS server
     */
    @Override
    public void processTask(final DeleteFilesSmrsTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final String nodeName = task.getNode().getName();
        final String targetType = readerService.getTargetType(task.getNode().getFdn());
        securityService.cancelSmrsAccountForNode(nodeName, targetType);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Files and SMRS account removed with success");
    }
}