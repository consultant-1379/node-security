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

import java.net.UnknownHostException;
import java.security.cert.CertificateException;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallTrustedCertificatesTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE
 * </p>
 * <p>
 * Install trusted certificates on the node
 * </p>
 * 
 * @author emaynes
 */
@WFTaskType(WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE)
@Local(WFTaskHandlerInterface.class)
public class InstallTrustedCertificatesTaskHandler implements WFActionTaskHandler<InstallTrustedCertificatesTask>, WFTaskHandlerInterface {

    @Inject
    private Logger logger;

    @Inject
    MOActionService moAction;

    @Inject
    CppSecurityService securityService;

    @Inject
    NscsCMReaderService readerService;

    @Override
    public void processTask(final InstallTrustedCertificatesTask task) {

        logger.debug("processTask InstallTrustedCertificatesTask for node [{}] started.", task.getNodeFdn());
        final NodeReference node = task.getNode();

        final TrustedCertCategory category = task.getTrustCategory();

        TrustStoreInfo trustStoreInfo;
        try {
            trustStoreInfo = securityService.getTrustStoreForNode(category, node, true, TrustCategoryType.OAM);
        } catch (CertificateException | SmrsDirectoryException | UnknownHostException | CppSecurityServiceException e) {
            logger.error("processTask InstallTrustedCertificatesTask for node [{}] failed.", task.getNodeFdn(), e);
            throw new WorkflowTaskException("processTask InitCertEnrollmentTask failed", e);
        }

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.Security_installTrustedCertificates, trustStoreInfo.toMoParams());
        logger.debug("Installing trusted certificates into node [{}].", node);

    }
}
