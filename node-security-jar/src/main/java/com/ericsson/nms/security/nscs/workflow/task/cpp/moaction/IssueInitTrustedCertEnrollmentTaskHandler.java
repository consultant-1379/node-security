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

import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParam;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitTrustedCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

import javax.ejb.Local;
import javax.inject.Inject;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE
 * </p>
 * <p>
 * Issue trusted certificates on the node
 * </p>
 * 
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE)
@Local(WFTaskHandlerInterface.class)
public class IssueInitTrustedCertEnrollmentTaskHandler implements WFActionTaskHandler<IssueInitTrustedCertEnrollmentTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    MOActionService moAction;

    @Inject
    CppSecurityService securityService;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    private SystemRecorder systemRecorder;

    @Override
    public void processTask(final IssueInitTrustedCertEnrollmentTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        TrustCategoryType trustCategory = null;
        final TrustedCertCategory category = task.getTrustCategory();

        if (category.equals(TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS)) {
            trustCategory = TrustCategoryType.LAAD;
        } else {
            trustCategory = TrustCategoryType.OAM;
        }

        final NodeReference node = task.getNode();

        final String trustedCA = task.getTrustedCertificateAuthority();

        TrustStoreInfo trustStoreInfo;

        if (trustedCA == null || trustedCA.isEmpty()) {
            String getMessage = String.format("trust store by category: category:" + category);
            nscsLogger.info(task, "Getting: " + getMessage);
            try {
                trustStoreInfo = securityService.getTrustStoreForNode(category, node, true, trustCategory);
            } catch (CertificateException | SmrsDirectoryException | UnknownHostException | CppSecurityServiceException e) {
                final String errorMessage = String.format("Exc: " + e.getClass().getName() + ", msg: " + e.getMessage() + ", getting:" + getMessage);
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new WorkflowTaskException(errorMessage);
            }
            nscsLogger.info(task, "Successfully got: " + getMessage);
        } else {
            String getMessage = String.format("trust store by CA: CA:" + trustedCA);
            nscsLogger.info(task, "Getting: " + getMessage);
            try {
                trustStoreInfo = securityService.getTrustStoreForNodeWithCA(category, trustedCA, node, true);
            } catch (CertificateException | SmrsDirectoryException | UnknownHostException | CppSecurityServiceException e) {
                final String errorMessage = String.format("Exc: " + e.getClass().getName() + "msg: " + e.getMessage() + "getting: " + getMessage);
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new WorkflowTaskException(errorMessage);
            }
            nscsLogger.info(task, "Successfully got: " + getMessage);
        }

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        MoParams moParams = trustStoreInfo.toMoParams();
        final String actionMessage = String.format("action: " + MoActionWithParameter.Security_installTrustedCertificates.getAction() + "on: "
                + MoActionWithParameter.Security_installTrustedCertificates.getMo().type() + "with params: " + moParams.toString());
        nscsLogger.info(task, "Performing: " + actionMessage);
        logM2MUser(moParams);
        try {
            nscsLogger.workFlowTaskHandlerOngoing(task, "Perform IssueInitTrustedCertEnrollmentTask for node: " + node);
            moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.Security_installTrustedCertificates, trustStoreInfo.toMoParams());
        } catch (Exception e) {
            final String errorMessage = String.format("Exc: " + e.getClass().getName() + " msg: " + e.getMessage() + " performing: " + actionMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Issue trusted certificates on node completed.");
    }

    private void logEvent(final String eventDesc, final String paramValue) {
        systemRecorder.recordEvent(eventDesc, EventLevel.COARSE, "Parameter Values : " + paramValue, "node-security", "");
    }

    private void logM2MUser(final MoParams moParams) {
        MoParam moParam = moParams.getParamMap().get("accountInfoList");
        List<MoParams> accountInfoListMoParams = (List<MoParams>) (moParam.getParam());
        if ((accountInfoListMoParams != null) && (!accountInfoListMoParams.isEmpty())) {
            MoParams accountInfoMoParams = accountInfoListMoParams.get(0);
            final String m2mUserId = (String) (accountInfoMoParams.getParamMap().get("userID").getParam());
            if (m2mUserId.startsWith("mm-cert")) {
                final String hiddenWord = (String) (accountInfoMoParams.getParamMap().get("password").getParam());
                final StringBuilder logParam = new StringBuilder("UserName=");
                final String encodedHiddenWord = Base64.getEncoder().encodeToString(hiddenWord.getBytes(StandardCharsets.UTF_8));
                logParam.append(m2mUserId).append("  HiddenWord=").append(encodedHiddenWord);
                logEvent("[TORF480878] Performing installTrustedCertificates MOAction ", logParam.toString());
            }
        }
    }

}
