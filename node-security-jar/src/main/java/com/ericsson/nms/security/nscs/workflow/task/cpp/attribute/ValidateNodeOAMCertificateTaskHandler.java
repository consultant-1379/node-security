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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ValidateNodeOAMCertificateTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_VALIDATE_NODE_OAM_CERTIFICATE
 * </p>
 * <p>
 * Check whether the node has a valid certificate issued by ENM CA.
 * </p>
 * 
 * @author tcsnapa.
 */
@WFTaskType(WorkflowTaskType.CPP_VALIDATE_NODE_OAM_CERTIFICATE)
@Local(WFTaskHandlerInterface.class)
public class ValidateNodeOAMCertificateTaskHandler implements WFQueryTaskHandler<ValidateNodeOAMCertificateTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CppSecurityService cppSecurityService;

    private String isCertValid = "VALID_CERT";
    private final String VALID_CERT_ON_NODE = "Node has been installed with a valid certificate issued by ENM CA.";
    private final String INVALID_CERT_ON_NODE = "Node has not been installed with a valid certificate issued by ENM CA.";

    @Override
    public String processTask(final ValidateNodeOAMCertificateTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        boolean isNodeHasValidCert = false;
        try {
            isNodeHasValidCert = cppSecurityService.isNodeHasValidCertificate(task.getNodeFdn(), CertificateType.OAM.name());
        } catch (CppSecurityServiceException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "processTask ValidateNodeCertificateTask for node [" + task.getNodeFdn() + "] failed.");
            throw new WorkflowTaskException("processTask ValidateNodeCertificateTask failed", e);
        }
        if (isNodeHasValidCert) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, VALID_CERT_ON_NODE);
        } else {
            isCertValid = "INVALID_CERT";
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, INVALID_CERT_ON_NODE);
        }
        return isCertValid;
    }
}
