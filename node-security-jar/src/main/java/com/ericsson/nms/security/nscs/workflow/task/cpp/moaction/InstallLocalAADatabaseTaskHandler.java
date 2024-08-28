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

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.AccountInfo;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.laad.utility.LaadFilesDistributeConstants;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallLocalAADatabaseTask;

/**
 * Task handler for CPP_INSTALL_LAAD_ACTION to install laad files on the node
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.CPP_INSTALL_LAAD_ACTION)
@Local(WFTaskHandlerInterface.class)
public class InstallLocalAADatabaseTaskHandler implements WFActionTaskHandler<InstallLocalAADatabaseTask>, WFTaskHandlerInterface {

    @Inject
    private CppSecurityService securityService;

    @Inject
    private MOActionService moAction;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsLogger nscsLogger;

    @Override
    public void processTask(final InstallLocalAADatabaseTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);

        final String actionMessage = String
                .format("action: %s on: %s", MoActionWithParameter.SECURITY_INSTALL_LOCAL_AA_DATABASE.getAction(),  MoActionWithParameter.SECURITY_INSTALL_LOCAL_AA_DATABASE.getMo().type());
        nscsLogger.info(task, "Performing: " + actionMessage);

        try {
            nscsLogger.workFlowTaskHandlerOngoing(task, LaadFilesDistributeConstants.INSTALLING_LAAD_FILES_ON_NODE + node);
            moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.SECURITY_INSTALL_LOCAL_AA_DATABASE, toMoParams(node.getName(), normalizable.getNeType()));
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, LaadFilesDistributeConstants.INSTALLING_LAAD_FILES_ON_NODE + node + " task completed.");
        } catch (final Exception exception) {
            final String errorMessage = String.format("Exc: %s msg: %s performing: %s", exception.getClass().getName(), exception.getMessage(), actionMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
    }

    private MoParams toMoParams(final String nodeName, final String neType) throws CertificateException {
        List<SmrsAccountInfo> accountInfos = securityService.getSmrsAccountInfoForCertificate(nodeName, neType);
        String laadFilesRelativePath = null;
        if (accountInfos != null && !accountInfos.isEmpty()) {
            laadFilesRelativePath = accountInfos.get(0).getSmrsRelativePath();
        }
        String authenticationPath = getAuthenticationFileName(laadFilesRelativePath);
        String authorizationPath = getAuthorizationFileName(laadFilesRelativePath);
        // build accountInfoList from accountInfos
        final List<MoParams> accountInfoList = new ArrayList<>();
        for (final AccountInfo a : accountInfos) {
            accountInfoList.add(a.toMoParams());
        }
        return toMoParams(authenticationPath, authorizationPath, "0", 30, accountInfoList);
    }

    private static MoParams toMoParams(final String authenticationPath, final String authorizationPath, final String startTime, final long duration, final List<MoParams> accountInfoList) {
        final MoParams params = new MoParams();
        params.addParam("authenticationFileName", authenticationPath);
        params.addParam("authorizationFileName", authorizationPath);
        params.addParam("startTime", startTime);
        params.addParam("duration", String.valueOf(duration));
        params.addParam("accountInfoList", accountInfoList);
        return params;
    }

    private String getAuthorizationFileName(final String laadFilesRelativePath) {
        return laadFilesRelativePath + LaadFilesDistributeConstants.AUTHORIZATION_FILE;
    }

    private String getAuthenticationFileName(final String laadFilesRelativePath) {
        return laadFilesRelativePath + LaadFilesDistributeConstants.AUTHENTICATION_FILE;
    }
}
