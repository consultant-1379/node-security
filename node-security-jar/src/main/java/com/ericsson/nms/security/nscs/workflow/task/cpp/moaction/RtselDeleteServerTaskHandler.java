/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import java.util.Set;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ServerInfo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.RtselDeleteServerTask;

/**
 * TaskHandler to delete RTSEL server configuration for CPP nodes
 * @author xchowja
 *
 */
@WFTaskType(WorkflowTaskType.RTSEL_DELETE_SERVER)
@Local(WFTaskHandlerInterface.class)
public class RtselDeleteServerTaskHandler implements WFActionTaskHandler<RtselDeleteServerTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private MOActionService moAction;

    @Override
    public void processTask(final RtselDeleteServerTask rtselDeleteServerTask) {
        nscsLogger.workFlowTaskHandlerStarted(rtselDeleteServerTask);
        final NodeReference nodeReference = rtselDeleteServerTask.getNode();
        try {
            final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference);
            final Set<String> serverNames = rtselDeleteServerTask.getServerNames();
            nscsLogger.workFlowTaskHandlerOngoing(rtselDeleteServerTask, "Performing MO action on the node to delete ExternalServer.");
            for (final String serverName : serverNames) {
                final MoParams moParams = (MoParams) toMoParams(serverName);
                moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.RealTimeSecLog_deleteExternalServer, moParams);
            }
        } catch (final Exception exception) {
            final String errorMessage = NscsErrorCodes.RTSEL_DELETE_SERVER_NAMES_ON_NODE_FAILED;
            nscsLogger.workFlowTaskHandlerFinishedWithError(rtselDeleteServerTask, "Action '" + MoActionWithParameter.RealTimeSecLog_deleteExternalServer + " failed!");
            throw new WorkflowTaskFailureException(errorMessage);
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(rtselDeleteServerTask, "Deletion of ExternalServer is completed.");

    }

    private Object toMoParams(final String serverName) {
        final MoParams params = new MoParams();
        params.addParam(ServerInfo.SERVER_NAME, serverName);
        return params;

    }

}
