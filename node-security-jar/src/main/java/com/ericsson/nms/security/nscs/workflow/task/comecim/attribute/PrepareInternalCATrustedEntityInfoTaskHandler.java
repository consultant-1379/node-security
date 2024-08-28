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
import java.util.*;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.PrepareInternalCATrustedEntityInfoTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.PREPARE_INTERNAL_CA_TRUSTED_ENTITY_INFO
 * </p>
 *
 * This class is used to prepare the trusted entity information for the Internal CA.
 */
@WFTaskType(WorkflowTaskType.PREPARE_INTERNAL_CA_TRUSTED_ENTITY_INFO)
@Local(WFTaskHandlerInterface.class)
public class PrepareInternalCATrustedEntityInfoTaskHandler implements WFQueryTaskHandler<PrepareInternalCATrustedEntityInfoTask>, WFTaskHandlerInterface {

    public static final String GETTING_FROM_PKI = "Getting from PKI";

    private String getFromPKIMessage = "";
    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Override
    public String processTask(final PrepareInternalCATrustedEntityInfoTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final String trustCerts = task.getTrustCerts();
        final TrustedCertCategory trustedCertificateCategory = trustCerts != null ? TrustedCertCategory.valueOf(trustCerts)
                : TrustedCertCategory.CORBA_PEERS;
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustCerts);
        nscsLogger.info(task, "From task : certificate type [" + certificateType + "]");
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String entityProfileName = task.getEntityProfileName();
        String caName = task.getTrustedCertificateAuthority();
        nscsLogger.info(task, "From task : entityProfileName [" + entityProfileName + "] caName [" + caName + "]");
        final boolean isReissue = Boolean.parseBoolean(task.getIsReissue());
        nscsLogger.info(task, "From task : isReissue [" + isReissue + "]");
        if (isReissue) {
            /*
             * Trust Distribute workflows invoked inside the Certificate Reissue. In this case the Trusted CA is set but ONLY for revoke purpose. It
             * shall not be used for trust distribution (use the trust category instead).
             */
            nscsLogger.info(task, "Trust Distribute workflow invoked inside Reissue. Trust category shall be used instead of Trusted CA.");
            caName = null;
        }

        String enrollmentCaName = null;

        /*
         * Extract output parameters possibly set by previous handlers
         */
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.info(task, "Output params not yet set!");
        } else {
            final String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
            final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
            if (enrollmentInfo == null) {
                nscsLogger.info(task, "Enrollment info not set in output params");
            } else {
                enrollmentCaName = enrollmentInfo.getEnrollmentCaName();
                nscsLogger.info(task, "From enrollment info : enrollment CA name [" + enrollmentCaName + "]");
            }

        }

        /*
         * Get from PKI info on trusted CAs and enrollment CA (if any) whose certificates are to be installed on node
         */

        final String nodeIpAddress = nscsNodeUtility.getNodeIpAddress(normNode);
        final boolean isIPv6Node = NscsNodeUtility.isIPv6Address(nodeIpAddress);
        nscsLogger.info(task, "nodeIpAddress [" + nodeIpAddress + "] isIPv6Node [" + isIPv6Node + "]");

        HashSet<NscsTrustedEntityInfo> trustedEntitiesInfo = getTrustInfoFromPki(caName, isIPv6Node, entityProfileName, trustedCertificateCategory,
                normNode);

        if (trustedEntitiesInfo == null || trustedEntitiesInfo.isEmpty()) {
            final String errorMessage = "Got from PKI no " + getFromPKIMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        NscsTrustedEntityInfo enrollmentCaEntityInfo = checkEnrollmentInfo(enrollmentCaName, trustedEntitiesInfo, isIPv6Node, task);

        return serializeResult(task, trustedEntitiesInfo, enrollmentCaEntityInfo, outputParams);
    }

    /**
     * This method is used to validate the enrollment CA information
     * @param enrollmentCaName
     * @param trustedEntitiesInfo
     * @param isIPv6Node
     * @param task
     * @return
     */
    private NscsTrustedEntityInfo checkEnrollmentInfo(final String enrollmentCaName, final HashSet<NscsTrustedEntityInfo> trustedEntitiesInfo,
            final boolean isIPv6Node, final PrepareInternalCATrustedEntityInfoTask task) {
        NscsTrustedEntityInfo enrollmentCaEntityInfo = null;
        if (enrollmentCaName != null) {
            for (final NscsTrustedEntityInfo trustedEntityInfo : trustedEntitiesInfo) {
                if (enrollmentCaName.equals(trustedEntityInfo.getName())) {
                    enrollmentCaEntityInfo = trustedEntityInfo;
                    break;
                }
            }
            if (enrollmentCaEntityInfo == null) {
                try {
                    getFromPKIMessage = "entity info for enrollment CA name [" + enrollmentCaName + "]";
                    enrollmentCaEntityInfo = securityService.getTrustedCAInfoByName(enrollmentCaName, isIPv6Node);
                } catch (final CppSecurityServiceException e) {
                    final String errorMessage = NscsLogger.stringifyException(e) + GETTING_FROM_PKI + getFromPKIMessage;
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new WorkflowTaskException(errorMessage);
                }
            }
        }

        return enrollmentCaEntityInfo;
    }

    /**
     * This method will be used to get the trusted CA's whose certificates are to be installed on node
     *
     * @param caName
     * @param isIPv6Node
     * @param entityProfileName
     * @param trustedCertificateCategory
     * @param normNode
     * @return
     */
    private HashSet<NscsTrustedEntityInfo> getTrustInfoFromPki(final String caName, final boolean isIPv6Node, final String entityProfileName,
            final TrustedCertCategory trustedCertificateCategory, final NormalizableNodeReference normNode) {
        HashSet<NscsTrustedEntityInfo> trustedEntitiesInfo = new HashSet<>();
        try {
            if (caName != null && !caName.isEmpty()) {
                getFromPKIMessage = "trusted entity info for CA name [" + caName + "]";
                final NscsTrustedEntityInfo trustedEntityInfo = securityService.getTrustedCAInfoByName(caName, isIPv6Node);

                trustedEntitiesInfo.add(trustedEntityInfo);
            } else {
                String actualEntityProfileName = entityProfileName;
                if (actualEntityProfileName == null || actualEntityProfileName.isEmpty()) {
                    getFromPKIMessage = "actual entity profile name for unspecifed entity profile name [" + entityProfileName
                            + "] and trust category [" + trustedCertificateCategory + "]";
                    actualEntityProfileName = securityService.getEntityProfileName(trustedCertificateCategory, normNode);
                }
                getFromPKIMessage = "trusted entities info for entity profile name [" + actualEntityProfileName + "]";
                trustedEntitiesInfo = (HashSet<NscsTrustedEntityInfo>) securityService.getTrustedCAsInfoByEntityProfileName(actualEntityProfileName,
                        isIPv6Node);
            }

        } catch (final CppSecurityServiceException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + GETTING_FROM_PKI + getFromPKIMessage;
            throw new WorkflowTaskException(errorMessage);
        }

        return trustedEntitiesInfo;
    }

    private String serializeResult(final PrepareInternalCATrustedEntityInfoTask task, final HashSet<NscsTrustedEntityInfo> trustedEntitiesInfo,
            final NscsTrustedEntityInfo enrollmentCaEntityInfo, final Map<String, Serializable> outputParams) {

        Map<String, Serializable> outParams = outputParams;
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params");
            outParams = new HashMap<>();
        }

        /*
         * Serialize trusted entities info in output parameters
         */
        String serializedtrustedEntitiesInfo = null;
        try {
            serializedtrustedEntitiesInfo = NscsObjectSerializer.writeObject(trustedEntitiesInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing trusted entities info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outParams.put(WorkflowOutputParameterKeys.TRUSTED_ENTITY_INFO.toString(), serializedtrustedEntitiesInfo);

        /*
         * Serialize enrollment CA entity info in output parameters
         */
        if (enrollmentCaEntityInfo != null) {
            String serializedEnrollmentCaEntityInfo = null;
            try {
                serializedEnrollmentCaEntityInfo = NscsObjectSerializer.writeObject(enrollmentCaEntityInfo);
            } catch (final IOException e1) {
                final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment CA entity info";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            outParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString(), serializedEnrollmentCaEntityInfo);
        }

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully prepared internal CA trusted entity info";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;

    }
}
