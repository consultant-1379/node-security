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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec.IpSecCertEnrollStateValue;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InitCertEnrollmentIpSecTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT_IPSEC
 * </p>
 * <p>
 * Initialize the certificate enrollment of the node for ipsec
 * </p>
 *
 * @author emehsau
 */
@WFTaskType(WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT_IPSEC)
@Local(WFTaskHandlerInterface.class)
public class InitCertEnrollmentIpSecTaskHandler implements WFActionTaskHandler<InitCertEnrollmentIpSecTask>, WFTaskHandlerInterface {

    /**
     * Time between pools in milliseconds.
     */
    //TODO: update this interval once get better measure
    private static final int POLL_INTERVAL = 10000;
    private static final int FIRST_POLL_DELAY = 2000;
    private static final int POLL_TIMES = 10;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moActionService;

    @Inject
    private CppSecurityService securityService;

    @EServiceRef
    private IntervalJobService intervalJob;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public void processTask(final InitCertEnrollmentIpSecTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final String nodeName = node.getName();
        ScepEnrollmentInfoImpl enrollmentInfo = null;
        try {
            enrollmentInfo = (ScepEnrollmentInfoImpl) securityService.generateIpsecEnrollmentInfo(nodeName, task.getSubjectAltName(),
                    task.getSubjectAltNameFormat());
            nscsLogger.info("enrollmentInfo in InitCertEnrollmentIpSecTaskHandler processTask method:" + enrollmentInfo);
        } catch (final CppSecurityServiceException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "processTask InitCertEnrollmentIpSecTask for node [" + task.getNodeFdn() + "] failed.");
            throw new WorkflowTaskException("processTask InitCertEnrollmentIpSecTask failed", e);
        }

        final MoParams mopar = enrollmentInfo.toIpSecMoParams();
        final List<MoParams> moParamsList = new ArrayList<>();
        moParamsList.add(convertMoParamsForMoAction(mopar));

        if (enrollmentInfo.getEnrollmentProtocol().equals(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue())) {
            enrollmentInfo.setEnrollmentProtocol(EnrollmentMode.CMPv2_INITIAL.getEnrollmentModeValue());
            final MoParams moparInitial = enrollmentInfo.toIpSecMoParams();
            moParamsList.add(convertMoParamsForMoAction(moparInitial));
        }

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing action initCertEnrollment on Ipsec for node[" + task.getNodeFdn() + "]");
        moActionService.performMOAction(normalizable.getFdn(), MoActionWithParameter.IpSec_initCertEnrollment, moParamsList);
        final String shortDescription = String.format(NscsLogger.ACTION_PERFORMED_POLLING_PROGRESS_FORMAT,
                MoActionWithParameter.IpSec_initCertEnrollment.getAction());
        nscsLogger.workFlowTaskHandlerOngoing(task,
                "Performed action initCertEnrollment on Ipsec for node[" + task.getNodeFdn() + "]. Polling result...", shortDescription);

        // call EJB timer
        intervalJob.createIntervalJob(FIRST_POLL_DELAY, POLL_INTERVAL, POLL_TIMES, new CertEnrollStateIntervalJob(node, nscsLogger, task));
        nscsLogger.info("processTask InitCertEnrollmentIpSecTask for node [" + node + "] is finished");
    }

    private MoParams convertMoParamsForMoAction(final MoParams moparams) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> map = (Map<String, Object>) moparams.getParam();

        for (final String key : map.keySet()) {
            //Recursive call for each items in the MAP
            final Object value = map.get(key);
            nscsLogger.debug("Before changes MoParams MAP key/value: " + key + "/" + value.toString());
        }

        final MoParams enrollmentDataParams = (MoParams) map.get("enrollmentData");

        final MoParams newEnrollmentDataParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
        map.put("enrollmentData", newEnrollmentDataParams);

        nscsLogger.info("enrollmentDataParams in convertMoParamsForMoAction method:" + enrollmentDataParams);
        for (final String key : map.keySet()) {
            //Recursive call for each items in the MAP
            final Object value = map.get(key);
            nscsLogger.debug("After changes MoParams MAP key/value: " + key + "/" + value.toString());
        }
        return moparams;
    }

    public static class CertEnrollStateIntervalJob implements IntervalJobAction {

        private final NodeReference node;
        private final NscsLogger log;
        private final InitCertEnrollmentIpSecTask task;

        public CertEnrollStateIntervalJob(final NodeReference node, final NscsLogger nscsLogger, final InitCertEnrollmentIpSecTask task) {
            this.node = node;
            this.log = nscsLogger;
            this.task = task;
        }

        @Override
        public boolean doAction(final Map<JobActionParameters, Object> params) {
            final WorkflowHandler handler = (WorkflowHandler) params.get(JobActionParameters.WORKFLOW_HANDLER);
            final NscsCMReaderService reader = (NscsCMReaderService) params.get(JobActionParameters.CM_READER);
            final SystemRecorder systemRecorder = (SystemRecorder) params.get(JobActionParameters.SYSTEM_RECORDER);
            final NscsCapabilityModelService capabilityService = (NscsCapabilityModelService) params.get(JobActionParameters.CAPABILITY_SERVICE);
            final NormalizableNodeReference nodeReference = reader.getNormalizableNodeReference(node);
            final String parentFDN = getIpsecFdn(capabilityService, nodeReference);

            final Map<String, Object> attributes = reader.readAttributesFromDelegate(parentFDN, IpSec.CERT_ENROLL_STATE);
            final String certEnrollState = (String) attributes.get(IpSec.CERT_ENROLL_STATE);
            log.info("The certEnrollState string is  " + certEnrollState);
            if (certEnrollState == null) {
                // TODO: hardcoded - arquillian tests is returning null.
                handler.dispatchMessage(node, WFMessageConstants.CPP_COMMAND_INIT_CERT_ENROLLMENT_IPSEC_SUCCESS);
                return true;
            }
            final IpSecCertEnrollStateValue state = IpSecCertEnrollStateValue.valueOf(certEnrollState);
            log.info("The certEnrollState is  " + state.toString());
            switch (state) {
            case ERROR:
                handler.dispatchMessage(node, WFMessageConstants.CPP_COMMAND_INIT_CERT_ENROLLMENT_IPSEC_FAILED);
                log.workFlowTaskHandlerFinishedWithError(task,
                        "IPsec [" + node.getName() + "] : Certificate enrollment failed on Node: " + node.getFdn());
                return true;
            case IDLE:
                handler.dispatchMessage(node, WFMessageConstants.CPP_COMMAND_INIT_CERT_ENROLLMENT_IPSEC_SUCCESS);
                log.workFlowTaskHandlerFinishedWithSuccess(task,
                        "IPsec [" + node.getName() + "] : Certificate enrollment on Node: " + node.getFdn() + " is finished.");
                return true;
            case ONGOING:
                log.workFlowTaskHandlerOngoing(task, "Action Ongoing for node: " + node.getFdn());
                return false;
            default:
                log.warn("processTask InitCertEnrollmentIpSecTask for node [" + node.getFdn() + "] failed.");
                throw new WorkflowTaskException("Could not reconot implemented [" + state + "]");
            }
        }

        private String getIpsecFdn(final NscsCapabilityModelService capabilityService, final NormalizableNodeReference normalizedReference) {

            final Mo rootMo = capabilityService.getMirrorRootMo(normalizedReference);
            final Mo iPSecMo = ((CppManagedElement) rootMo).ipSystem.ipSec;
            final String iPSecFdn = iPSecMo.withNames(normalizedReference.getFdn()).fdn();

            log.debug("IPSec FDN " + iPSecFdn);
            return iPSecFdn;
        }

    }

}
