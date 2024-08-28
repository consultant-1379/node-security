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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckAndUpdateEndEntityTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_AND_UPDATE_ENDENTITY)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckAndUpdateEndEntityTaskHandler implements WFQueryTaskHandler<ComEcimCheckAndUpdateEndEntityTask>, WFTaskHandlerInterface {

    private static final String VALID = "VALID";
    private static final String NOT_VALID = "NOT_VALID";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;


    @Override
    public String processTask(final ComEcimCheckAndUpdateEndEntityTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.info(task, "Output params are not yet set!");
        }

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(node);
        final String tMI = normalizableNodeRef.getOssModelIdentity();
        final String nodeType = normalizableNodeRef.getNeType();
        nscsLogger.info(task, "From task : nodeType [" + nodeType + "] tMI [" + tMI + "]");

        final String trustedCertCategory = task.getTrustedCertCategory();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);
        nscsLogger.info(task, "From task : certificate type [" + certificateType + "]");

        final String nodeFdn = task.getNodeFdn();
        final BaseSubjectAltNameDataType subjectAltName = new SubjectAltNameStringType(task.getSubjectAltName());
        final SubjectAltNameFormat subjectAltNameFormat = (task.getSubjectAltNameType() != null
                ? SubjectAltNameFormat.valueOf(task.getSubjectAltNameType()) : null);

        NodeEntityCategory nodeEntityCategory = null;
        if (TrustedCertCategory.CORBA_PEERS.name().equals(trustedCertCategory)) {
            nodeEntityCategory = NodeEntityCategory.OAM;
        } else if (TrustedCertCategory.IPSEC.name().equals(trustedCertCategory)) {
            nodeEntityCategory = NodeEntityCategory.IPSEC;
        } else {
            final String errorMessage = "Unknown trust category [" + trustedCertCategory + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "nodeEntityCategory [" + nodeEntityCategory.name() + "]");

        final NodeModelInformation nodeModelInfo = readerService.getNodeModelInformation(nodeFdn);
        nscsLogger.info(task, "From DPS : nodeModelInfo [" + nodeModelInfo.toString() + "]");

        // Extract task parameters and convert to Security Service format
        final String enrollmentModeFromTask = task.getEnrollmentMode();
        EnrollmentMode enrollmentMode = null;
        AlgorithmKeys keyAlgorithm = null;
        final String keyAlgorithmFromTask = task.getKeyAlgorithm();

        if (outputParams != null) {
            final String enrollmentModeFromParams = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString());
            if (enrollmentModeFromParams != null && !enrollmentModeFromParams.isEmpty()) {
                enrollmentMode = EnrollmentMode.valueOf(enrollmentModeFromParams);
            }
            final String keyAlgorithmFromParams = (String) outputParams.get(WorkflowOutputParameterKeys.ALGORITHM_KEYS.toString());
            if (keyAlgorithmFromParams != null && !keyAlgorithmFromParams.isEmpty()) {
                keyAlgorithm = AlgorithmKeys.valueOf(keyAlgorithmFromParams);
            }
        }

        if (enrollmentMode == null) {
            enrollmentMode = nscsNodeUtility.getEnrollmentMode(enrollmentModeFromTask, normalizableNodeRef);
        }
        nscsLogger.info(task, "enrollmentMode [" + enrollmentMode.name() + "]");
        if (keyAlgorithmFromTask != null && !keyAlgorithmFromTask.isEmpty()) {
            keyAlgorithm = AlgorithmKeys.valueOf(keyAlgorithmFromTask);
        }
        nscsLogger.info(task, "keyAlgorithm [" + keyAlgorithm + "]");

        // Build the EnrollingInformation object containing the parameters to
        // invoke the get enrollment info from PKI (via Security Service).
        // TODO DespicableUs add commonName to EnrollingInformation
        final String commonName = task.getCommonName();
        final EnrollingInformation enrollInfo = new EnrollingInformation(nodeFdn, task.getEntityProfileName(), enrollmentMode, keyAlgorithm,
                nodeEntityCategory, commonName);
        enrollInfo.setSubjectAltName(subjectAltName);
        enrollInfo.setSubjectAltNameFormat(subjectAltNameFormat);
        enrollInfo.setModelInfo(nodeModelInfo);

        // Get the enrolling info
        ScepEnrollmentInfoImpl enrollmentInfo = null;
        try {
            nscsLogger.info(task, "Invoking securityService with enrollInfo [" + enrollInfo + "]");
            enrollmentInfo = (ScepEnrollmentInfoImpl) securityService.generateEnrollmentInfo(enrollInfo);

            // TODO
            // enrollmentInfo.setRollbackTimeout(task.getRollbackTimeout());

            if (enrollmentInfo == null) {
                final String errorMessage = "Got null enrollment info";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.debug(task, "Successfully got enrollment info [" + enrollmentInfo + "]");

        } catch (final CppSecurityServiceException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while getting enrollment info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return entityStateValid(task, enrollmentInfo, outputParams);
    }

    private String entityStateValid(final ComEcimCheckAndUpdateEndEntityTask task, final ScepEnrollmentInfoImpl enrollmentInfo,
            final Map<String, Serializable> outputParams) {
        final String state = VALID;
        return serializeResult(task, state, enrollmentInfo, outputParams);
    }

    private String serializeResult(final ComEcimCheckAndUpdateEndEntityTask task, final String state, final ScepEnrollmentInfoImpl enrollmentInfo,
            Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outputParams = new HashMap<String, Serializable>();
        }

        // Serialize Enrollment Info in output parameters
        String serializedEnrollmentInfo = null;
        try {
            serializedEnrollmentInfo = NscsObjectSerializer.writeObject(enrollmentInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), serializedEnrollmentInfo);
        outputParams.put(WorkflowOutputParameterKeys.IS_ONLINE_ENROLLMENT.toString(), "TRUE");

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(state, outputParams);

        nscsLogger.debug(task, "Serializing result [" + state + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully completed : Creation And Updation of EndEntity state is [" + state + "]";
        if (NOT_VALID.equals(state)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, NscsLogger.NOT_VALID);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }

        return encodedWfQueryTaskResult;
    }
}