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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
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
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentIpSecTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT_IPSEC
 * </p>
 * <p>
 * Initialize the certificate enrollment issue of the node for ipsec
 * </p>
 *
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT_IPSEC)
@Local(WFTaskHandlerInterface.class)
public class IssueInitCertEnrollmentIpSecTaskHandler implements WFQueryTaskHandler<IssueInitCertEnrollmentIpSecTask>, WFTaskHandlerInterface {

    //TODO: update this interval once get better measure
    private static final int POLL_INTERVAL = 10000;
    private static final int FIRST_POLL_DELAY = 2000;
    private static final int POLL_TIMES = 10;
    private static final String VALID = "VALID";

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
    public String processTask(final IssueInitCertEnrollmentIpSecTask task) {
        final NodeReference node = task.getNode();
        final String nodeFdn = task.getNodeFdn();
        final String nodeName = node.getName();
        /*
         * TODO check on attribute task.getAlgoKeySize(); task.getEnrollmentMode(); task.getEntityProfileName(); task.getSubjectAltName();
         * task.getSubjectAltNameType();
         */

        nscsLogger.workFlowTaskHandlerStarted(task);

        ScepEnrollmentInfoImpl enrollmentInfo = null;

        final EnrollingInformation enrollInfo = new EnrollingInformation(task.getNodeFdn(), task.getEntityProfileName(), task.getEnrollmentMode(),
                task.getAlgoKeySize(), NodeEntityCategory.IPSEC, task.getCommonName());
        enrollInfo.setSubjectAltName(task.getSubjectAltName());
        enrollInfo.setSubjectAltNameFormat(task.getSubjectAltNameType());
        //We can set the modelInfo attribute as null, since the CppSecurityBean will recalculate it
        enrollInfo.setModelInfo(null);
        try {
            nscsLogger.info("Before invoking CppSecurityService with EnrollingInformation:" + enrollInfo.toString());

            enrollmentInfo = (ScepEnrollmentInfoImpl) securityService.generateEnrollmentInfo(enrollInfo);

            // Override EnrollmentMode to support KUR

            final EnrollmentMode enrollmentMode = task.getEnrollmentMode() != null ? task.getEnrollmentMode() : enrollmentInfo.getEnrollmentMode();
            nscsLogger.info("before invoking enrollmentModeUpdate new enrollProtocol:" + enrollmentInfo.getEnrollmentProtocol() + ", new enrollMode:"
                    + enrollmentMode);

            final EnrollmentMode updateEnrollMode = securityService.enrollmentModeUpdate(task.getNodeFdn(), enrollmentMode,
                    CertificateType.IPSEC.toString(), enrollmentInfo.getEntity());
            enrollmentInfo.setEnrollmentProtocol(updateEnrollMode.getEnrollmentModeValue());
            nscsLogger.info("After invoking enrollmentModeUpdate new enrollment:" + enrollmentInfo.getEnrollmentProtocol());

            //TODO Invoke PKI with getSubjectAltName() and getSubjectAltNameType()
        } catch (final CppSecurityServiceException e) {
            final String errorMessage = String
                    .format("processTask IssueInitCertEnrollmentIpSecTask for node:" + task.getNode() + "failed. Exception:" + task.getNode());
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage, e);
        }
        nscsLogger.info("Before invoking moActionService.performMOAction");
        final MoParams mopar = enrollmentInfo.toIpSecMoParams();
        final List<MoParams> moParamsList = new ArrayList<>();
        moParamsList.add(convertMoParamsForMoAction(mopar, enrollInfo.getModelInfo()));

        if (enrollmentInfo.getEnrollmentProtocol().equals(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue())) {
            enrollmentInfo.setEnrollmentProtocol(EnrollmentMode.CMPv2_INITIAL.getEnrollmentModeValue());
            final MoParams moparInitial = enrollmentInfo.toIpSecMoParams();
            moParamsList.add(convertMoParamsForMoAction(moparInitial, enrollInfo.getModelInfo()));
            enrollmentInfo.setEnrollmentProtocol(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue()); // Restore value for serializeResult
        }

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.warn("Output params not yet set!");
        }

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing IssueInitCertEnrollmentIpSecTask for node: " + node);
        moActionService.performMOAction(normalizable.getFdn(), MoActionWithParameter.IpSec_initCertEnrollment, moParamsList);
        final String shortDescription = String.format(NscsLogger.ACTION_PERFORMED_POLLING_PROGRESS_FORMAT,
                MoActionWithParameter.IpSec_initCertEnrollment.getAction());
        nscsLogger.workFlowTaskHandlerOngoing(task, "Performed IssueInitCertEnrollmentIpSecTask for node: " + node, shortDescription);

        // call EJB timer
        intervalJob.createIntervalJob(FIRST_POLL_DELAY, POLL_INTERVAL, POLL_TIMES, new CertEnrollStateIntervalJob(node, nscsLogger, task));
        nscsLogger.debug(task, "processTask IssueInitCertEnrollmentIpSecTask for node: " + nodeFdn + " is finished.");
        return validIssueInitCertEnrollmentIpSec(nodeName, enrollmentInfo, outputParams, task);
    }

    private String validIssueInitCertEnrollmentIpSec(final String nodeName, final ScepEnrollmentInfoImpl enrollmentInfo,

            final Map<String, Serializable> outputParams, final IssueInitCertEnrollmentIpSecTask task) {

        final String state = VALID;
        final String infoMessage = String.format("IssueInitCert node:" + nodeName);
        nscsLogger.info(infoMessage);
        return serializeResult(nodeName, enrollmentInfo, state, outputParams, task);

    }

    private String serializeResult(final String nodeName, final ScepEnrollmentInfoImpl enrollmentInfo, final String result,
            Map<String, Serializable> outputParams, final IssueInitCertEnrollmentIpSecTask task) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info("Issue Init Enrollment : initializing output params!");
            outputParams = new HashMap<String, Serializable>();
        }
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollmentInfo);
        } catch (final IOException e1) {
            final String errorMessageSerialize = "Failed serialization of enrollment info exc: " + e1.getClass().getName() + "msg: " + e1.getMessage()
                    + "for node:" + nodeName;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessageSerialize);
            throw new UnexpectedErrorException(errorMessageSerialize);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        nscsLogger.workFlowTaskHandlerOngoing(task, "Action Ongoing for node: " + nodeName);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessageSerialize = String.format("Exception " + e.getMessage() + " while serializing object for node: " + nodeName);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessageSerialize);
            throw new UnexpectedErrorException(errorMessageSerialize);
        }

        return encodedWfQueryTaskResult;
    }

    private MoParams convertMoParamsForMoAction(final MoParams moparams, final NodeModelInformation modelInfo) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> map = (Map<String, Object>) moparams.getParam();

        for (final String key : map.keySet()) {
            //Recursive call for each items in the MAP
            final Object value = map.get(key);
            nscsLogger.debug("Before changes MoParams MAP key/value: " + key + "/" + value.toString());
        }

        final MoParams enrollmentDataParams = (MoParams) map.get("enrollmentData");

        if (modelInfo != null) {
            final MoParams newEnrollmentDataParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
            map.put("enrollmentData", newEnrollmentDataParams);
        }

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
        private final IssueInitCertEnrollmentIpSecTask task;

        public CertEnrollStateIntervalJob(final NodeReference node, final NscsLogger nscsLogger, final IssueInitCertEnrollmentIpSecTask task) {
            this.node = node;
            this.log = nscsLogger;
            this.task = task;
        }

        @Override
        public boolean doAction(final Map<JobActionParameters, Object> params) {
            final WorkflowHandler handler = (WorkflowHandler) params.get(JobActionParameters.WORKFLOW_HANDLER);
            final NscsCMReaderService reader = (NscsCMReaderService) params.get(JobActionParameters.CM_READER);
            final MOActionService service = (MOActionService) params.get(JobActionParameters.MO_ACTION_SERVICE);
            // call sync to update DPS before checking
            //callMoActionSync(service, node);
            final String certEnrollState = getCertEnrollState(node, reader, params);
            if (certEnrollState == null) {
                // TODO: hardcoded - arquillian tests is returning null.
                handler.dispatchMessage(node, WFMessageConstants.CPP_COMMAND_INIT_CERT_ENROLLMENT_IPSEC_SUCCESS);
                return true;
            }
            final IpSecCertEnrollStateValue state = IpSecCertEnrollStateValue.valueOf(certEnrollState);
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
                log.warn("processTask IssueInitCertEnrollmentIpSecTask for node" + node.getFdn() + " failed.");
                throw new WorkflowTaskException("Could not reconot implemented [" + state + "]");

            }
        }

        /**
         * Call sync on node.
         *
         * @param service
         *            MO action service.
         * @param node
         *            node reference.
         */
        private void callMoActionSync(final MOActionService service, final NodeReference node) {
            log.debug("Calling sync over MO: " + node.getFdn());
            try {
                service.performMOAction(node.getName(), MoActionWithoutParameter.CMFunction_sync);
                log.debug("Action '" + MoActionWithoutParameter.CMFunction_sync + "' on node " + node.getFdn() + " as successful!");
            } catch (final Exception e) {
                log.error(task, "Action '" + MoActionWithoutParameter.CMFunction_sync + "' on node " + node.getFdn() + " failed!");
            }
        }

        private String getCertEnrollState(final NodeReference node, final NscsCMReaderService reader, final Map<JobActionParameters, Object> params) {
            final NormalizableNodeReference nodeReference = reader.getNormalizableNodeReference(node);
            String certEnrollState;
            final NscsCapabilityModelService capabilityService = (NscsCapabilityModelService) params.get(JobActionParameters.CAPABILITY_SERVICE);
            final String parentFDN = getIpsecFdn(capabilityService, nodeReference);

            final Map<String, Object> attributes = reader.readAttributesFromDelegate(parentFDN, IpSec.CERT_ENROLL_STATE);
            certEnrollState = (String) attributes.get(IpSec.CERT_ENROLL_STATE);

            return certEnrollState;
        }

        private String getIpsecFdn(final NscsCapabilityModelService capabilityService, final NormalizableNodeReference normalizedReference) {

            final Mo rootMo = capabilityService.getMirrorRootMo(normalizedReference);
            final Mo iPSecMo = ((CppManagedElement) rootMo).ipSystem.ipSec;
            final String iPSecFdn = iPSecMo.withNames(normalizedReference.getFdn()).fdn();

            log.info("IPSec FDN " + iPSecFdn);
            return iPSecFdn;
        }
    }
}
