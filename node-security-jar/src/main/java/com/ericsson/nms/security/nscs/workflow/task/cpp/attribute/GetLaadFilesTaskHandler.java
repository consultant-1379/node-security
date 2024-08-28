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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import java.security.cert.CertificateException;
import java.util.List;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.laad.utility.LaadFilesDistributeConstants;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppGetLaadFilesTask;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.LaadService;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.exception.LaadDataRetrievalException;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.exception.LaadFileSigningException;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.exception.LaadFilesGenerationException;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.exception.LaadUserRetrievalException;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.exception.PasswordHashGenerationException;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.model.LaadFiles;
import com.ericsson.oss.services.security.accesscontrol.cppaa.laad.api.model.NodeInfo;

/**
 * Task handler for WorkflowTaskType.CPP_GET_LAAD_FILES Fetches the laad files from the LAAD service and uploads them into SMRS
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.CPP_GET_LAAD_FILES)
@Local(WFTaskHandlerInterface.class)
public class GetLaadFilesTaskHandler implements WFQueryTaskHandler<CppGetLaadFilesTask>, WFTaskHandlerInterface {

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CppSecurityService cppSecurityService;

    @Inject
    private SmrsUtils smrsUtils;

    @EServiceRef
    private LaadService laadService;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    private String laadFileUploadStatus = "SUCCESS";

    @Override
    public String processTask(final CppGetLaadFilesTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        try {
            nscsLogger.workFlowTaskHandlerOngoing(task, LaadFilesDistributeConstants.GET_AND_UPLOADING_LAAD_FILES_TO_SMRS + " for Node : " + node.getName());
            getAndUploadLaadFiles(node, normNode);
        } catch (LaadDataRetrievalException | LaadFilesGenerationException | LaadFileSigningException | LaadUserRetrievalException | PasswordHashGenerationException
                | WorkflowTaskException | NscsCapabilityModelException exception) {
            laadFileUploadStatus = "FAILURE";

            final String errorMessage = String.format("Exception: %s Msg: %s Performing: %s LAAD Files Upload Status: %s for Node %s", exception.getClass().getName(), exception.getMessage(),
                    LaadFilesDistributeConstants.GET_AND_UPLOADING_LAAD_FILES_TO_SMRS, laadFileUploadStatus, node.getName());

            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
        }
        return laadFileUploadStatus;
    }

    private void getAndUploadLaadFiles(final NodeReference node, final NormalizableNodeReference normNode) {
        nscsLogger.info(" Inside getAndUploadLaadFiles() for Node: "+ node.getName());
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setNodeName(node.getName());
        final String passwordHashAlgorithm = nscsCapabilityModelService.getDefaultPasswordHashAlgorithm(normNode);
        nodeInfo.setPasswordHashAlgo(passwordHashAlgorithm);
        nscsLogger.info(" Password hash algorithm {}", passwordHashAlgorithm);

        final LaadFiles laadFiles = laadService.getLaadFiles(nodeInfo);
        try {
            uploadLaadFilesToSmrs(laadFiles, node, normNode.getNeType());
        } catch (CertificateException | SmrsDirectoryException exception) {

            final String errorMessage = String.format("Exception: %s Msg: %s Performing: %s for Node: %s", exception.getClass().getName(), exception.getMessage(),
                    LaadFilesDistributeConstants.UPLOADING_LAAD_FILES_TO_SMRS, node.getName());

            nscsLogger.error(errorMessage);
            systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, LaadFilesDistributeConstants.UPLOADING_LAAD_FILES_TO_SMRS, "GetLaadFilesTaskHandler",
                    "Failed to Upload LAAD Files to SMRS for Node: " + node.getName());
            throw new WorkflowTaskException(errorMessage);
        }
    }

    private void uploadLaadFilesToSmrs(final LaadFiles laadFiles, final NodeReference node, final String neType) throws CertificateException {
        List<SmrsAccountInfo> addressInfo = null;
        nscsLogger.info(LaadFilesDistributeConstants.UPLOADING_LAAD_FILES_TO_SMRS + " to Node: ", node.getName());
        addressInfo = cppSecurityService.getSmrsAccountInfoForCertificate(node.getName(), neType);
        for (final SmrsAccountInfo smrsAccount : addressInfo) {
            final String settingUri = smrsUtils.uploadFileToSmrs(smrsAccount, LaadFilesDistributeConstants.AUTHENTICATION_FILE, laadFiles.getAuthenticationFile());
            final String settingUri1 = smrsUtils.uploadFileToSmrs(smrsAccount, LaadFilesDistributeConstants.AUTHORIZATION_FILE, laadFiles.getAuthorizationFile());
            nscsLogger.info("Uploaded LAAD files to SMRS: {} {} for Node {}", settingUri, settingUri1, node.getName());
        }
    }

}
