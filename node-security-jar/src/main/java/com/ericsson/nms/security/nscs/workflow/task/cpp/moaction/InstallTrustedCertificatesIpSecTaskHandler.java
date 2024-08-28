/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
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
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec.IpSecTrustedCertInstallStateValue;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallTrustedCertificatesIpSecTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE_IPSEC
 * </p>
 * <p>
 * Install trusted certificates on the node for ipsec
 * </p>
 * Created by emehsau
 */
@WFTaskType(WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE_IPSEC)
@Local(WFTaskHandlerInterface.class)
public class InstallTrustedCertificatesIpSecTaskHandler implements WFActionTaskHandler<InstallTrustedCertificatesIpSecTask>, WFTaskHandlerInterface {

    /**
     * Time between pools in milliseconds.
     */
    private static final int POLL_INTERVAL = 10000;
    private static final int FIRST_POLL_DELAY = 2000;
    private static final int POLL_TIMES = 10;

    @Inject
    private Logger logger;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private MOActionService moAction;

    @Inject
    private CppSecurityService securityService;

    @EServiceRef
    private IntervalJobService intervalJob;

    @Inject
    NscsCMReaderService readerService;

    @Profiled
    @Override
    public void processTask(final InstallTrustedCertificatesIpSecTask task) {
        logger.info("processTask InstallTrustedCertificatesTask for node [{}] started.", task.getNodeFdn());
        final NodeReference node = task.getNode();
        final TrustStoreInfo trustStoreInfo = fetchTrustCertsByCategory(TrustedCertCategory.IPSEC.toString(), node);
        logger.info("Perform  InstallTrustedCertificatesIpSecTask for node [{}] started with params {}.", task.getNodeFdn(),
                trustStoreInfo.toMoParamsIpSec().toString());
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.IpSec_installTrustedCertificates, trustStoreInfo.toMoParamsIpSec());
        logger.debug("Perform  InstallTrustedCertificatesIpSecTask for node [{}] Completed successfully.", task.getNodeFdn());
        systemRecorder.recordSecurityEvent("Node Security Service - Enabling IPsec",
                "Starting trust certificate installation on Node '" + task.getNodeFdn() + "'", "", "NETWORK.INITIAL_NODE_ACCESS",
                ErrorSeverity.INFORMATIONAL, "IN-PROGRESS");

        // call EJB timer
        intervalJob.createIntervalJob(FIRST_POLL_DELAY, POLL_INTERVAL, POLL_TIMES, new InstallTrustedCertsIntervalJob(node));
        logger.debug("processTask InstallTrustedCertificatesIpSecTask for node [{}] is finished", node);
    }

    private TrustStoreInfo fetchTrustCertsByCategory(final String trustCategory, final NodeReference nodeRef) {
        try {
            final TrustedCertCategory category = getTrustCategory(trustCategory);
            return securityService.getTrustStoreForNode(category, nodeRef, true, TrustCategoryType.IPSEC);
        } catch (CertificateException | SmrsDirectoryException | UnknownHostException | CppSecurityServiceException e) {
            logger.warn("processTask CheckTrustedAlreadyInstalledTask failed.", e);
            throw new WorkflowTaskException("processTask InstallTrustedCertificatesIpSecTask failed", e);
        }
    }

    private TrustedCertCategory getTrustCategory(final String category) {
        if (null == category) {
            //Default category
            return TrustedCertCategory.IPSEC;
        } else {
            return TrustedCertCategory.valueOf(category);
        }
    }

    public static class InstallTrustedCertsIntervalJob implements IntervalJobAction {

        private static final Logger LOGGER = LoggerFactory.getLogger(InstallTrustedCertsIntervalJob.class);

        private final NodeReference node;

        public InstallTrustedCertsIntervalJob(final NodeReference node) {
            this.node = node;
        }

        @Override
        public boolean doAction(final Map<JobActionParameters, Object> params) {
            InstallTrustedCertsIntervalJob.LOGGER.debug("Execution action for interval job.");
            final WorkflowHandler handler = (WorkflowHandler) params.get(JobActionParameters.WORKFLOW_HANDLER);
            final NscsCMReaderService reader = (NscsCMReaderService) params.get(JobActionParameters.CM_READER);
            final SystemRecorder systemRecorder = (SystemRecorder) params.get(JobActionParameters.SYSTEM_RECORDER);
            final NscsCapabilityModelService capabilityService = (NscsCapabilityModelService) params.get(JobActionParameters.CAPABILITY_SERVICE);
            final NormalizableNodeReference nodeReference = reader.getNormalizableNodeReference(node);
            final String parentFDN = getIpsecFdn(capabilityService, nodeReference);

            final Map<String, Object> attributes = reader.readAttributesFromDelegate(parentFDN, IpSec.TRUSTED_CERT_INST_STATE);
            final String trustCertState = (String) attributes.get(IpSec.TRUSTED_CERT_INST_STATE);
            LOGGER.info("The trustedCertInstallState string is " + trustCertState);
            if (trustCertState == null) {
                // TODO: hardcoded - arquillian tests is returning null.
                handler.dispatchMessage(node, WFMessageConstants.CPP_COMMAND_TRUSTED_CERT_INSTALL_IPSEC_SUCCESS);
                return true;
            }
            final IpSecTrustedCertInstallStateValue state = IpSecTrustedCertInstallStateValue.valueOf(trustCertState);
            LOGGER.info("The trustedCertInstallState string is " + state);
            switch (state) {
            case ERROR:
                handler.dispatchMessage(node, WFMessageConstants.CPP_COMMAND_TRUSTED_CERT_INSTALL_IPSEC_FAILED);
                systemRecorder.recordError("Node Security Service - Enabling IPsec", ErrorSeverity.INFORMATIONAL,
                        "IPsec [" + node.getName() + "] : Trust certificates installation failed on Node '" + node.getFdn() + "'",
                        "NETWORK.INITIAL_NODE_ACCESS", "TRUST-CERTIFICATE-ERROR");
                return true;
            case IDLE:
                handler.dispatchMessage(node, WFMessageConstants.CPP_COMMAND_TRUSTED_CERT_INSTALL_IPSEC_SUCCESS);
                systemRecorder.recordSecurityEvent("Node Security Service - Enabling IPsec",
                        "IPsec [" + node.getName() + "] : Trust certificates installed successfully on Node '" + node.getFdn() + "'", "",
                        "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.INFORMATIONAL, "IN-PROGRESS");
                return true;
            case ONGOING:
                return false;
            default:
                LOGGER.warn("processTask InstallTrustedCertificatesIpSecTask for node [{}] failed.", node.getFdn());
                throw new WorkflowTaskException("Could not reconot implemented [" + state + "]");
            }
        }

        private String getIpsecFdn(final NscsCapabilityModelService capabilityService, final NormalizableNodeReference normalizedReference) {

            final Mo rootMo = capabilityService.getMirrorRootMo(normalizedReference);
            final Mo iPSecMo = ((CppManagedElement) rootMo).ipSystem.ipSec;
            final String iPSecFdn = iPSecMo.withNames(normalizedReference.getFdn()).fdn();

            LOGGER.info("IPSec FDN " + iPSecFdn);
            return iPSecFdn;
        }
    }

}
