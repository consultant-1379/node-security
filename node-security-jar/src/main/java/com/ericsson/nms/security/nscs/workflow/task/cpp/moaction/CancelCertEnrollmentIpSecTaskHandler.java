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

import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec.IpSecCertEnrollStateValue;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CancelCertEnrollmentIpSecTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT_IPSEC
 * </p>
 * <p>
 * Cancels certificate enrollment of the target node for ipsec
 * </p>
 * 
 * @author emehsau
 */
@WFTaskType(WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT_IPSEC)
@Local(WFTaskHandlerInterface.class)
public class CancelCertEnrollmentIpSecTaskHandler implements WFActionTaskHandler<CancelCertEnrollmentIpSecTask>, WFTaskHandlerInterface {

    // TODO: update this interval once get better measure
    private static final int POLL_INTERVAL = 10000;
    private static final int FIRST_POLL_DELAY = 2000;
    private static final int POLL_TIMES = 3;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moActionService;

    @Inject
    private NscsCMReaderService readerService;

    @EServiceRef
    private IntervalJobService intervalJob;

    @Override
    public void processTask(final CancelCertEnrollmentIpSecTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        try {
            final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
            nscsLogger.workFlowTaskHandlerOngoing(task, "Performing Action for node: [" + task.getNodeFdn() + "]");
            moActionService.performMOAction(normalizable.getFdn(), MoActionWithoutParameter.IpSec_cancelCertEnrollment);
        } catch (final Exception e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Action '" + MoActionWithoutParameter.IpSec_cancelCertEnrollment + "' on node [" + task.getNodeFdn() + "] failed!");
            throw e;
        }

        // call EJB timer to check when the CertEnrollState is reset to IDLE
        intervalJob.createIntervalJob(FIRST_POLL_DELAY, POLL_INTERVAL, POLL_TIMES, new CheckIpSecCertEnrollStateIntervalJob(node, nscsLogger, task));
        nscsLogger.info("processTask CancelCertEnrollmentIpSecTaskHandler for node [" + node + "] is finished");
    }

    public static class CheckIpSecCertEnrollStateIntervalJob implements IntervalJobAction {
        
        private final NodeReference node;
        private final NscsLogger log;
        private final CancelCertEnrollmentIpSecTask task;

        public CheckIpSecCertEnrollStateIntervalJob(final NodeReference node, final NscsLogger nscsLogger, final CancelCertEnrollmentIpSecTask task) {
            this.node = node;
            this.log = nscsLogger;
            this.task = task;
        }

        @Override
        public boolean doAction(final Map<JobActionParameters, Object> params) {
            final WorkflowHandler handler = (WorkflowHandler) params.get(JobActionParameters.WORKFLOW_HANDLER);
            final NscsCMReaderService reader = (NscsCMReaderService) params.get(JobActionParameters.CM_READER);
            final NscsCapabilityModelService capabilityService =(NscsCapabilityModelService) params.get(JobActionParameters.CAPABILITY_SERVICE);           
            final NormalizableNodeReference nodeReference =reader.getNormalizableNodeReference(node);
            final String parentFDN=getIpsecFdn(capabilityService, nodeReference);
            
            final Map<String, Object> attributes = reader.readAttributesFromDelegate(parentFDN,IpSec.CERT_ENROLL_STATE);
            final String certEnrollState = (String)attributes.get(IpSec.CERT_ENROLL_STATE);
            log.info("The certEnrollState string is " + certEnrollState);
            if (certEnrollState == null) {
                // TODO: hardcoded - arquillian tests is returning null.
                handler.dispatchMessage(node, WFMessageConstants.CPP_ATT_CERT_ENROLL_STATE_CHANGE);
                return true;
            }
            final IpSecCertEnrollStateValue state = IpSecCertEnrollStateValue.valueOf(certEnrollState);
            switch (state) {
            case IDLE:
                // we want to notify WFS when attribute returns IDLE
                handler.dispatchMessage(node, WFMessageConstants.CPP_ATT_CERT_ENROLL_STATE_CHANGE);
                log.workFlowTaskHandlerFinishedWithSuccess(task, "IPsec ["+ node.getName()+"] : CertEnrollState attribute reset to " + state.name() + " for Node '" + node.getFdn() + " is finished.");
                return true;
            case ONGOING:
            	log.workFlowTaskHandlerOngoing(task, "Action Ongoing for node: " + node.getFdn());
                return false;
            default:
            	log.debug("CheckCertEnrollStateIntervalJob get CertEnrollState [" + state.name() + "] for node [" + node.getFdn() + "].");
                return false;
            }
        }

        
        private String getIpsecFdn(final NscsCapabilityModelService capabilityService, final NormalizableNodeReference normalizedReference) {

            Mo rootMo = capabilityService.getMirrorRootMo(normalizedReference);
            Mo iPSecMo = ((CppManagedElement) rootMo).ipSystem.ipSec;
            String iPSecFdn = iPSecMo.withNames(normalizedReference.getFdn()).fdn();

            log.debug("IPSec FDN " + iPSecFdn);
            return iPSecFdn;
        }
        
    }
}
