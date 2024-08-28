/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPrepareCATrustedPemCertificatesTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;


@WFTaskType(WorkflowTaskType.CBPOI_PREPARE_CA_TRUSTED_PEM_CERTIFICATES)
@Local(WFTaskHandlerInterface.class)
public class CbpOiPrepareCATrustedPemCertificatesTaskHandler implements WFQueryTaskHandler<CbpOiPrepareCATrustedPemCertificatesTask>, WFTaskHandlerInterface {

    @Inject
    NscsLogger nscsLogger;

    @Inject
    CppSecurityService securityService;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    NscsCapabilityModelService nscsCapabilityModelService;

    public static final String GETTING_FROM_PKI = "Getting from PKI";

    private String getFromPKIMessage = "";

    @Override
    public String processTask(final CbpOiPrepareCATrustedPemCertificatesTask prepareCATrustedPemCertificatesTask) {
        nscsLogger.workFlowTaskHandlerStarted(prepareCATrustedPemCertificatesTask);

        final String successMessage = "Successfully prepared CA trusted PEM certificates";
        final NodeReference node = prepareCATrustedPemCertificatesTask.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String entityProfileName = prepareCATrustedPemCertificatesTask.getEntityProfileName();
        String cliCaName = prepareCATrustedPemCertificatesTask.getTrustedCertificateAuthority();
        nscsLogger.info(prepareCATrustedPemCertificatesTask, "From task : entityProfileName [{}]  caName [{}]  node [{}]", entityProfileName, cliCaName, node.getName());
        final boolean isReissue = Boolean.parseBoolean(prepareCATrustedPemCertificatesTask.getIsReissue());
        nscsLogger.info(prepareCATrustedPemCertificatesTask, "From task : isReissue [{}]", isReissue);
        if (isReissue) {
        /*
             * Trust Distribute workflows invoked inside the Certificate Reissue. In this case the Trusted CA is set but ONLY for revoke purpose. It
             * shall not be used for trust distribution (use the trust category instead).
             */
            nscsLogger.info(prepareCATrustedPemCertificatesTask, "CbpOi Trust Distribute workflow invoked inside Reissue. Trust category shall be used instead of Trusted CA.");
            cliCaName = null;
        }

        /*
             * Extract output parameters possibly set by previous handlers
             */
        final Map<String, Serializable> outputParams = prepareCATrustedPemCertificatesTask.getOutputParams();
        NscsCbpOiTrustedEntityInfo oamCmpCaTrustedEntityInfo = null;
        if (outputParams == null) {
            nscsLogger.info(prepareCATrustedPemCertificatesTask, "Output params not yet set!");
        } else {
            final String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
            if (serializedEnrollmentInfo.isEmpty()) {
                nscsLogger.info(prepareCATrustedPemCertificatesTask, "Enrollment info not set in output params");
            } else {
                nscsLogger.info(prepareCATrustedPemCertificatesTask, "Enrollment info set in output params: get enrollment CA trust info");
                final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
                oamCmpCaTrustedEntityInfo = getEnrollmentCaTrustedEntityInfo(normNode, enrollmentInfo);
            }
        }

        HashSet<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo = getTrustedEntitiesFromPki(cliCaName, entityProfileName, normNode);
        if (oamCmpCaTrustedEntityInfo != null) {
            trustedEntitiesInfo.add(oamCmpCaTrustedEntityInfo);
        }

        if (trustedEntitiesInfo.isEmpty()) {
            final String errorMessage = "Got from PKI no trusted certificates";
            nscsLogger.workFlowTaskHandlerFinishedWithError(prepareCATrustedPemCertificatesTask, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(prepareCATrustedPemCertificatesTask, successMessage);

        return serializeResult(prepareCATrustedPemCertificatesTask, trustedEntitiesInfo, outputParams);
    }


    /**
     * This method will be used to get the trusted CA's whose certificates are to be installed on node
     *
     * @param caName
     * @param entityProfileName
     * @param normNode
     * @return
     */
    private HashSet<NscsCbpOiTrustedEntityInfo> getTrustedEntitiesFromPki(final String caName, final String entityProfileName,
                                                                          final NormalizableNodeReference normNode) {
        HashSet<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo = new HashSet<>();
        final String oamTrustCategoryName = getTrustCategoryName(normNode, false);
        try {
            if ((caName != null) && !caName.isEmpty()) {
                getFromPKIMessage = "CbpOi trusted entity info for CA name [" + caName + "]";
                NscsCbpOiTrustedEntityInfo trustedEntityInfo = securityService.getCbpOiTrustedCAInfoByName(caName);
                if (trustedEntityInfo != null) {
                    trustedEntityInfo.setTrustCategoryName(oamTrustCategoryName);
                    trustedEntitiesInfo.add(trustedEntityInfo);
                }
            } else {
                String actualEntityProfileName = entityProfileName;
                if (actualEntityProfileName == null || actualEntityProfileName.isEmpty()) {
                    getFromPKIMessage = "actual entity profile name for unspecified entity profile name";
                    actualEntityProfileName = securityService.getEntityProfileName(TrustedCertCategory.CORBA_PEERS, normNode);
                }
                getFromPKIMessage = "CbpOi trusted entities info for entity profile name [" + actualEntityProfileName + "]";
                trustedEntitiesInfo =
                    (HashSet<NscsCbpOiTrustedEntityInfo>) securityService.getCbpOiTrustedCAsInfoByEntityProfileName(actualEntityProfileName);
                for (NscsCbpOiTrustedEntityInfo trustedEntityInfo : trustedEntitiesInfo) {
                    trustedEntityInfo.setTrustCategoryName(oamTrustCategoryName);
                }
            }

        } catch (final CppSecurityServiceException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + GETTING_FROM_PKI + getFromPKIMessage;
            throw new WorkflowTaskException(errorMessage);
        }

        return trustedEntitiesInfo;
    }

    private NscsCbpOiTrustedEntityInfo getEnrollmentCaTrustedEntityInfo(final NormalizableNodeReference nodeRef, final ScepEnrollmentInfo ei) {
        nscsLogger.info("Get enrollment CA trust info for node [{}]", nodeRef);
        NscsCbpOiTrustedEntityInfo oamCmpTrustInfo = null;
        String caName = null;
        if ((ei == null) || (nodeRef == null)) {
            return oamCmpTrustInfo;
        }
        try {
            final Map<String, String> enrollmentCAAuthorizationModes = nscsCapabilityModelService.getEnrollmentCAAuthorizationModes(nodeRef);
            if (nscsCapabilityModelService.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.toString())) {
                caName = new CertSpec().getCNfromDN(ei.getPkiRootCertificateAuthorityDn());
            } else if (nscsCapabilityModelService.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.toString())) {
                caName = ei.getEnrollmentCaName();
            }
        } catch (final NscsCapabilityModelException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + "reading Capability Model";
            throw new WorkflowTaskException(errorMessage);
        }

        if ((caName != null) && (!caName.isEmpty())) {
            getFromPKIMessage = "CbpOi trusted entity info for CA name [" + caName + "]";
            try {
                oamCmpTrustInfo = securityService.getCbpOiTrustedCAInfoByName(caName);
            } catch (CppSecurityServiceException e) {
                final String errorMessage = NscsLogger.stringifyException(e) + GETTING_FROM_PKI + getFromPKIMessage;
                throw new WorkflowTaskException(errorMessage);
            }
            oamCmpTrustInfo.setTrustCategoryName(getTrustCategoryName(nodeRef, true));
        }
        return oamCmpTrustInfo;
    }

    private String getTrustCategoryName(final NormalizableNodeReference normNodeRef, boolean isEnrollmentCaTrustCategory) {
        String trustCategoryName = "";
        Map<String, String> moDefaultNames = null;
        if (isEnrollmentCaTrustCategory) {
            moDefaultNames = nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(normNodeRef);
        }
        else {
            moDefaultNames = nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(normNodeRef);
        }
        if (moDefaultNames != null) {
            trustCategoryName = moDefaultNames.get(TrustCategoryType.OAM.toString());
        }
        return trustCategoryName;
    }

    private String serializeResult(final CbpOiPrepareCATrustedPemCertificatesTask task, 
            final HashSet<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo, final Map<String, Serializable> outputParams) {

        final Map<String, Serializable> outParams = outputParams == null ? new HashMap<>() : outputParams;
        /*
         * Serialize trusted entities info in output parameters
         */
        String serializedTrustedEntitiesInfo = null;
        try {
            serializedTrustedEntitiesInfo = NscsObjectSerializer.writeObject(trustedEntitiesInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing trusted entities info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outParams.put(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString(), serializedTrustedEntitiesInfo);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outParams);

        String serializedWfQueryTaskResult;
        try {
            serializedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException exc) {
            final String exceptionMessage = NscsLogger.stringifyException(exc) + " while preparing workflow task result";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, exceptionMessage);
            throw new UnexpectedErrorException(exceptionMessage);
        }
        final String successMessage = "Successfully prepared internal CA trusted entities info";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return serializedWfQueryTaskResult;

    }
}
