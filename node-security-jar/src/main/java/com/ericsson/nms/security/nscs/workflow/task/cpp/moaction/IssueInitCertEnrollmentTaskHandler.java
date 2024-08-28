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

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

@WFTaskType(WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT)
@Local(WFTaskHandlerInterface.class)
public class IssueInitCertEnrollmentTaskHandler implements WFQueryTaskHandler<IssueInitCertEnrollmentTask>, WFTaskHandlerInterface {

    private static final String VALID = "VALID";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moAction;

    @Inject
    private CppSecurityService cppSecService;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public String processTask(final IssueInitCertEnrollmentTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        nscsLogger.info("Task getRollbackTimeout:" + task.getRollbackTimeout());

        /*
         * TODO check on attribute task.getAlgoKeySize(); task.getEnrollmentMode(); task.getEntityProfileName(); task.getSubjectAltName();
         * task.getSubjectAltNameType();
         *
         * logger.info("Task getAlgoKeySize [{}]", task.getAlgoKeySize().toString()); logger.info("Task getEntityProfileName [{}]",
         * task.getEntityProfileName().toString()); logger.info( "Task getSubjectAltNameType [{}]", task.getSubjectAltNameType().toString());
         *
         */

        final NodeReference node = task.getNode();

        final String nodeName = node.getName();
        ScepEnrollmentInfoImpl enrollmentInfo = null;

        final EnrollingInformation enrollInfo = new EnrollingInformation(task.getNodeFdn(), task.getEntityProfileName(), task.getEnrollmentMode(),
                task.getAlgoKeySize(), NodeEntityCategory.OAM, task.getCommonName());
        enrollInfo.setSubjectAltName(task.getSubjectAltName());
        enrollInfo.setSubjectAltNameFormat(task.getSubjectAltNameType());
        //We can set the modelInfo attribute as null, since the CppSecurityBean will recalculate it
        enrollInfo.setModelInfo(null);
        try {
            nscsLogger.info("Before invoking CppSecurityService with EnrollingInformation:" + enrollInfo.toString());
            enrollmentInfo = (ScepEnrollmentInfoImpl) cppSecService.generateEnrollmentInfo(enrollInfo);
            nscsLogger.info("After invoking CppSecurityService");

            // Override EnrollmentMode to support KUR

            final EnrollmentMode enrollmentMode = task.getEnrollmentMode() != null ? task.getEnrollmentMode() : enrollmentInfo.getEnrollmentMode();
            nscsLogger.info("before invoking enrollmentModeUpdate new enrollProtocol:" + enrollmentInfo.getEnrollmentProtocol() + ", new enrollMode: "
                    + enrollmentMode);

            final EnrollmentMode updateEnrollMode = cppSecService.enrollmentModeUpdate(task.getNodeFdn(), enrollmentMode,
                    CertificateType.OAM.toString(), enrollmentInfo.getEntity());
            enrollmentInfo.setEnrollmentProtocol(updateEnrollMode.getEnrollmentModeValue());
            nscsLogger.info("After invoking enrollmentModeUpdate new enrollProtocol: " + enrollmentInfo.getEnrollmentProtocol());

            //TODO Invoke PKI with all new parameters, like EntityProfile, Algorithm Key etc...
            enrollmentInfo.setRollbackTimeout(task.getRollbackTimeout());
        } catch (final CppSecurityServiceException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "processTask IssueInitCertEnrollmentTask for node: " + task.getNodeFdn() + " failed. " + e.getMessage());
            throw new WorkflowTaskException("processTask IssueInitCertEnrollmentTask failed", e);
        }

        final MoParams mopar = enrollmentInfo.toMoParams();
        final List<MoParams> moParamsList = new ArrayList<>();
        MoParams moparInitial = null;
        moParamsList.add(convertMoParamsForMoAction(mopar, enrollInfo.getModelInfo()));

        if (enrollmentInfo.getEnrollmentProtocol().equals(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue())) {
            enrollmentInfo.setEnrollmentProtocol(EnrollmentMode.CMPv2_INITIAL.getEnrollmentModeValue());
            moparInitial = enrollmentInfo.toMoParams();
            //            nscsLogger.workFlowTaskHandlerOngoing(task, "MoParams for CMPv2_INITIAL: " + moparInitial);
            moParamsList.add(convertMoParamsForMoAction(moparInitial, enrollInfo.getModelInfo()));
            enrollmentInfo.setEnrollmentProtocol(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue()); // Restore value for serializeResult
            //            nscsLogger.workFlowTaskHandlerOngoing(task, "MoParams list with CMPv2_INITIAL: " + moParamsList);
        }

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.warn("Output params not yet set!");
        }

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        nscsLogger.workFlowTaskHandlerOngoing(task, "Perform IssueInitCertEnrollmentTask for node: " + node);
        moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.Security_initCertEnrollment, moParamsList);

        return validIssueInitCertEnrollment(nodeName, enrollmentInfo, outputParams, task);
    }

    private MoParams convertMoParamsForMoAction(final MoParams moparams, final NodeModelInformation modelInfo) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> map = (Map<String, Object>) moparams.getParam();

        for (final String key : map.keySet()) {
            //Recursive call for each items in the MAP
            final Object value = map.get(key);
            nscsLogger.debug("Before changes MoParams MAP key/value: " + key + "/" + value.toString());
        }

        final MoParams enrollmentDataParams = (MoParams) map.get("data");

        if (modelInfo != null) {
            final MoParams newEnrollmentDataParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
            map.put("data", newEnrollmentDataParams);
        }

        for (final String key : map.keySet()) {
            //Recursive call for each items in the MAP
            final Object value = map.get(key);
            nscsLogger.debug("After changes MoParams MAP key/value: " + key + "/" + value.toString());
        }
        return moparams;
    }

    private String validIssueInitCertEnrollment(final String nodeName, final ScepEnrollmentInfoImpl enrollmentInfo,

            final Map<String, Serializable> outputParams, final IssueInitCertEnrollmentTask task) {

        final String state = VALID;
        final String infoMessage = String.format("IssueInitCert node:" + nodeName);
        nscsLogger.info(infoMessage);
        return serializeResult(nodeName, enrollmentInfo, state, outputParams, task);

    }

    private String serializeResult(final String nodeName, final ScepEnrollmentInfoImpl enrollmentInfo, final String result,
            Map<String, Serializable> outputParams, final IssueInitCertEnrollmentTask task) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.debug("Issue Init Enrollment : initializing output params!");
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

        final String shortDescription = String.format(NscsLogger.ACTION_PERFORMED_WAITING_EVENT_FORMAT,
                MoActionWithParameter.Security_initCertEnrollment.getAction());
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "processTask IssueInitCertEnrollmentTask for node: " + nodeName + " is finished",
                shortDescription);
        return encodedWfQueryTaskResult;
    }
}
