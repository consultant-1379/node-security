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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.RealTimeSecLog;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.AddExternalServerTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_ADD_EXTERNAL_SERVER.
 * </p>
 * <p>
 * Adds ExternalServer Details to node to activate RTSEL.
 * </p>
 *
 * @author tcsramc
 *
 */
@WFTaskType(WorkflowTaskType.CPP_ADD_EXTERNAL_SERVER)
@Local(WFTaskHandlerInterface.class)
public class AddExternalServerTaskHandler implements WFActionTaskHandler<AddExternalServerTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moAction;

    @Inject
    NscsCMReaderService nscsCmReaderService;

    @Inject
    NscsNodeUtility nscsNodeUtility;

    final Mo realTimeSecLogMo = Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog;

    @SuppressWarnings("unchecked")
    @Profiled
    @Override
    public void processTask(final AddExternalServerTask addExternalServerTask) {

        nscsLogger.workFlowTaskHandlerStarted(addExternalServerTask);
        try {
            final NodeReference nodeReference = addExternalServerTask.getNode();
            final NormalizableNodeReference normalizableNodeRef = nscsCmReaderService.getNormalizableNodeReference(nodeReference);

            final String mirrorRootFdn = normalizableNodeRef.getFdn();
            final String requestedAttrs[] = { RealTimeSecLog.EXT_SERVER_LIST_CONFIG };
            final CmResponse cmResponseRtsel = nscsCmReaderService.getMos(mirrorRootFdn, realTimeSecLogMo.type(), realTimeSecLogMo.namespace(),
                    requestedAttrs);
            final List<Map<String, Object>> serverDetailsFromNode = (List<Map<String, Object>>) cmResponseRtsel.getCmObjects().iterator().next()
                    .getAttributes().get(RealTimeSecLog.EXT_SERVER_LIST_CONFIG);
            final List<Map<String, Object>> serverDetailsFromInput = addExternalServerTask.getServerConfig();
            if (serverDetailsFromNode == null || serverDetailsFromNode.size() == 0) {
                for (final Map<String, Object> serverToConfigure : serverDetailsFromInput) {
                    addExternalServer(nodeReference, serverToConfigure, addExternalServerTask);
                }
            } else {
                for (final Map<String, Object> serverToConfigure : serverDetailsFromInput) {
                    int serverCount = 0;
                    for (final Map<String, Object> serveralrdyConfigured : serverDetailsFromNode) {
                        if (serveralrdyConfigured.containsValue(serverToConfigure.get(RtselConstants.EXT_SERVER_NAME))) {
                            serverCount = serverCount + 1;
                            break;
                        }
                    }
                    if (serverCount == 0) {
                        addExternalServer(nodeReference, serverToConfigure, addExternalServerTask);
                    }
                }
            }
        } catch (final Exception exception) {
            final String errorMessage = NscsErrorCodes.FAILED_TO_CONFIGURE_EXT_SERVER;
            nscsLogger.workFlowTaskHandlerFinishedWithError(addExternalServerTask,
                    "Action '" + MoActionWithParameter.RealTimeSecLog_addExternalServer + " failed!");
            throw new WorkflowTaskFailureException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(addExternalServerTask,
                "Configuring ExternalServer task completed for node [" + addExternalServerTask.getNodeFdn() + "].");
    }

    private void addExternalServer(final NodeReference nodeReference, final Map<String, Object> serverConfig,
            final AddExternalServerTask addExternalServerTask) {
        final MoParams moParams = toMoParams((String) serverConfig.get(RtselConstants.EXT_SERVER_ADDRESS),
                (String) serverConfig.get(RtselConstants.EXT_SERVER_PROTOCOL), (String) serverConfig.get(RtselConstants.EXT_SERVER_NAME));
        final MoParams moObject = new MoParams();
        moObject.addParam(RtselConstants.EXT_SERVER, moParams);

        final List<MoParams> moParamsasList = new ArrayList<>();
        moParamsasList.add(moObject);

        final NormalizableNodeReference normalizable = nscsCmReaderService.getNormalizableNodeReference(nodeReference);
        final MoActionWithParameter targetAction = MoActionWithParameter.RealTimeSecLog_addExternalServer;
        final String moFdn = nscsNodeUtility.getSingleInstanceMoFdn(normalizable.getFdn(), realTimeSecLogMo);

        final String actionMessage = NscsLogger.stringifyActionByFdn(moFdn, targetAction, moObject);
        nscsLogger.debug(addExternalServerTask, "Performing " + actionMessage);
        moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.RealTimeSecLog_addExternalServer, moParamsasList);
    }

    private MoParams toMoParams(final String extServerAddress, final String extServProtocol, final String serverName) {
        final MoParams params = new MoParams();
        params.addParam(RtselConstants.EXT_SERVER_ADDRESS, extServerAddress);
        params.addParam(RtselConstants.EXT_SERVER_PROTOCOL, extServProtocol);
        params.addParam(RtselConstants.EXT_SERVER_NAME, serverName);
        return params;
    }
}
