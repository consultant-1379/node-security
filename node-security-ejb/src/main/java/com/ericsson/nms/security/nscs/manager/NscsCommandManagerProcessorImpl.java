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
package com.ericsson.nms.security.nscs.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestWfsConfiguration;
import com.ericsson.nms.security.nscs.ssh.SSHKeyWfsConfigurationDto;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.cert.issue.CertIssueWfParams;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentInfo;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes.NodeTrustInfo;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.TrustedCACertificateInfo;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.TrustedCACertificates;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import com.ericsson.oss.services.security.nscs.command.util.CiphersCommandHelper;

@Stateless
public class NscsCommandManagerProcessorImpl implements NscsCommandManagerProcessor {

    private static final String EXTERNAL_CA = "EXTERNAL_CA";
    private static final String ENM_PKI_CA = "ENM_PKI_CA";
    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";

    @Inject
    private NscsCapabilityModelService capabilityModel;

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @EServiceRef
    WorkflowHandler wfHandler;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeCertificateIssueSingleWf(final ExternalCAEnrollmentInfo extCAEnrollmentInfo, final Node inputNode,
            final CertIssueWfParams wfParams, final boolean isReissue, final String revocationReason, final JobStatusRecord jobStatusRecord,
            final int workflowId) {

        logger.debug("executeCertificateIssueSingleWf() - nodeFdn: [{}]", inputNode.getNodeFdn());
        final NodeReference nodeRef = new NodeRef(inputNode.getNodeFdn());
        final String workflowName = capabilityModel.getIssueOrReissueCertWf(nodeRef, wfParams.getCertType());
        WfResult result = null;

        if (WorkflowNames.WORKFLOW_CPPIssueCertificate.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.toString().equals(workflowName)) {

            final Map<String, Object> workflowVars = new HashMap<>();
            if (WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.toString().equals(workflowName)) {
                workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString(), Boolean.toString(false));
            } else {
                workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString(), Boolean.toString(isReissue));
            }
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "");
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "");
            workflowVars.put(WorkflowParameterKeys.REVOCATION_REASON.toString(), revocationReason);
            workflowVars.put(WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString(), wfParams.getSubjAltName());
            workflowVars.put(WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString(), wfParams.getSubjAltNameType());
            workflowVars.put(WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString(), wfParams.getEntityProfileName());
            workflowVars.put(WorkflowParameterKeys.ENROLLMENT_MODE.toString(), wfParams.getEnrollmentMode());
            workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(), wfParams.getKeySize());
            workflowVars.put(WorkflowParameterKeys.COMMON_NAME.toString(), wfParams.getCommonName());

            if (WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString().equals(workflowName)
                    || WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.toString().equals(workflowName)) {
                workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(),
                        TrustedCertCategory.fromCertificateType(CertificateType.valueOf(wfParams.getCertType())).name());
            }

            if (isReissue) {
                workflowVars.put(WorkflowParameterKeys.REVOCATION_REASON.toString(), revocationReason);
            }

            putEnrollmentCAWorkflowVars(inputNode, extCAEnrollmentInfo, workflowVars);

            try {
                result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);

            } catch (final Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new CertificateIssueWfException();
            }
            logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeRef,
                    wfParams.getCertType());
        } else {
            logger.error("Invalid scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeRef,
                    wfParams.getCertType());
        }
        return result;
    }

    private void putEnrollmentCAWorkflowVars(final Node inputNode, final ExternalCAEnrollmentInfo extCAEnrollmentInfo,
            final Map<String, Object> workflowVars) {
        if (extCAEnrollmentInfo != null) {
            final ExternalCAEnrollmentDetails extCaEnrollmentDetails = extCAEnrollmentInfo.getExternalCAEnrollmentDetails();
            workflowVars.put(WorkflowParameterKeys.EXTERNAL_CA_CERTIFICATE_AUTHORITY_DN.toString(), extCaEnrollmentDetails.getCertificateAuthorityDn());
            workflowVars.put(WorkflowParameterKeys.EXTERNAL_CA_CERTIFICATE_SUBJECT_DN.toString(), inputNode.getCertificateSubjectDn());
            workflowVars.put(WorkflowParameterKeys.EXTERNAL_CA_CERTIFICATE.toString(), extCaEnrollmentDetails.getCACertificate());
            workflowVars.put(WorkflowParameterKeys.EXTERNAL_CA_CHALLENGE_PASSWORD.toString(), inputNode.getChallengePhrase());
            workflowVars.put(WorkflowParameterKeys.EXTERNAL_CA_ENROLLMENT_SERVER_URL.toString(), extCaEnrollmentDetails.getEnrollmentServerUrl());
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), EXTERNAL_CA);
            workflowVars.put(WorkflowParameterKeys.IS_EXTERNAL_CA_REISSUE.toString(), FALSE);
            if (inputNode.getInterfaceFdn() != null) {
                workflowVars.put(WorkflowParameterKeys.EXTERNAL_CA_INTERFACE_FDN.toString(), inputNode.getInterfaceFdn());
            }

            final TrustedCACertificates trustedCACertificates = extCAEnrollmentInfo.getTrustedCACertificates();
            if (trustedCACertificates != null) {
                workflowVars.put(WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString(), TRUE);
                final List<TrustedCACertificateInfo> uniqueTrustedCACertInfos = trustedCACertificates.getTrustedCACertificateInfo();
                workflowVars.put(WorkflowParameterKeys.EXTERNAL_TRUSTED_CA_CERTIFICATE_INFO.toString(),
                        prepareTrustedEntitiesInfo(uniqueTrustedCACertInfos));
            } else {
                workflowVars.put(WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString(), FALSE);
            }

        } else {
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), ENM_PKI_CA);
            workflowVars.put(WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString(), TRUE);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeCertificateReIssueSingleWf(final Entry<Entity, NodeReference> entry, final String revocationReason, final String certType,
            final JobStatusRecord jobStatusRecord, final int workflowId) throws NscsServiceException {
        WfResult result = null;
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        boolean isExecutable = false;
        boolean isReissue = false;
        String issuer = "";
        String serialNumber = "";
        final String workflowName = capabilityModel.getIssueOrReissueCertWf(entry.getValue(), certType);
        logger.debug("executeCertificateReIssueSingleWf() - Preparing workflow [{}] data for entity name [{}], category [{}]", workflowName,
                entry.getKey().getEntityInfo().getName(), certType);
        final NodeReference nodeRef = new NodeRef(entry.getValue().getFdn());
        final EnrollmentMode enrollMode = getEnrollmentMode(entry.getValue());

        if (WorkflowNames.WORKFLOW_CPPIssueCertificate.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.toString().equals(workflowName)) {
            if (entry.getKey().getEntityInfo().getIssuer() != null) {
                issuer = entry.getKey().getEntityInfo().getIssuer().getName();
            }
            if (entry.getKey().getEntityInfo().getActiveCertificate() != null) {
                serialNumber = entry.getKey().getEntityInfo().getActiveCertificate().getSerialNumber();
            }
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), issuer);
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), serialNumber);
            workflowVars.put(WorkflowParameterKeys.ENROLLMENT_MODE.toString(), enrollMode.name());
            if (EnrollmentMode.SCEP.equals(enrollMode)) {
                isReissue = true;
                logger.debug("EnrollmentMode is : [{}] , isReissue is [{}]", enrollMode, isReissue);
            }
            if (WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString().equals(workflowName)
                    || WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.toString().equals(workflowName)) {
                isReissue = true;
                logger.debug("WORKFLOW COMECIM ComIssueCert or CbpOi StartOnlineEnrollment: isReissue is [{}]", isReissue);
                workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(),
                        TrustedCertCategory.fromCertificateType(CertificateType.valueOf(certType)).name());
            }
            workflowVars.put(WorkflowParameterKeys.IS_EXTERNAL_CA_REISSUE.toString(), TRUE);
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString(), Boolean.toString(isReissue));
            if (!revocationReason.isEmpty()) {
                workflowVars.put(WorkflowParameterKeys.REVOCATION_REASON.toString(), revocationReason);
            }
            workflowVars.put(WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString(), TRUE);
            isExecutable = true;
        } else {
            logger.error("Invalid workflow name for node: [{}] with certificate type [{}] ", workflowName, entry.getValue().getName(), certType);
        }

        if (isExecutable) {
            try {
                result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);

            } catch (final Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new CertificateIssueWfException();
            }
            logger.debug("Started scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeRef,
                    certType);
        }

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeCertificateReIssueSingleWf(final NodeReference entry, final String revocationReason, final String certType,
            final JobStatusRecord jobStatusRecord, final int workflowId) throws NscsServiceException {
        WfResult result = null;
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        boolean isExecutable = false;
        boolean isReissue = false;
        final String workflowName = capabilityModel.getIssueOrReissueCertWf(entry, certType);
        logger.debug("executeCertificateReIssueSingleWf() - Preparing workflow [{}] data for node name [{}], category [{}]", workflowName,
                entry.getName(), certType);
        final NodeReference nodeRef = new NodeRef(entry.getFdn());
        final EnrollmentMode enrollMode = getEnrollmentMode(entry);

        if (WorkflowNames.WORKFLOW_CPPIssueCertificate.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.toString().equals(workflowName)) {
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "");
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "");
            workflowVars.put(WorkflowParameterKeys.ENROLLMENT_MODE.toString(), enrollMode.name());
            workflowVars.put(WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString(), TRUE);
            workflowVars.put(WorkflowParameterKeys.IS_EXTERNAL_CA_REISSUE.toString(), TRUE);
            if (EnrollmentMode.SCEP.equals(enrollMode)) {
                isReissue = true;
                logger.debug("EnrollmentMode is : [{}] , isReissue is [{}]", enrollMode, isReissue);
            }
            if (WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString().equals(workflowName)
                    || WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.toString().equals(workflowName)) {
                isReissue = true;
                logger.debug("WORKFLOW COMECIM ComIssueCert or CbpOi StartOnlineEnrollment: isReissue is [{}]", isReissue);
                workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(),
                        TrustedCertCategory.fromCertificateType(CertificateType.valueOf(certType)).name());
            }

            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString(), Boolean.toString(isReissue));
            if (!revocationReason.isEmpty()) {
                workflowVars.put(WorkflowParameterKeys.REVOCATION_REASON.toString(), revocationReason);
            }
            isExecutable = true;
        } else {
            logger.error("Invalid workflow name for node: [{}] with certificate type [{}] ", workflowName, entry.getName(), certType);
        }

        if (isExecutable) {
            try {
                result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);

            } catch (final Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new CertificateIssueWfException();
            }
            logger.debug("Started scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeRef,
                    certType);
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeTrustDistributeSingleWf(final NodeTrustInfo node, final String trustCategory, final String caName,
            final JobStatusRecord jobStatusRecord, final int workflowId, final TrustedCACertificates trustedCACertificates) {
        WfResult result = null;
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        boolean isExecutable = false;
        final NodeReference nodeRef = new NodeRef(node.getNodeFdn());
        final String workflowName = capabilityModel.getTrustDistributeWf(nodeRef, trustCategory);
        logger.debug("executeTrustDistributeSingleWf() - nodeFdn: " + nodeRef.getFdn() + " workflowName " + workflowName);

        if (WorkflowNames.WORKFLOW_CPPIssueTrustCert.toString().equals(workflowName)) {
            if (trustCategory.equals(TrustCategoryType.OAM.toString())) {
                workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
            } else if (trustCategory.equals(TrustCategoryType.LAAD.toString())) {
                workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS.toString());
            }
            isExecutable = true;

        } else if (WorkflowNames.WORKFLOW_CPPIssueTrustCertIpSec.toString().equals(workflowName)) {
            workflowVars.put(WorkflowParameterKeys.TRUST_CERTS.toString(), TrustedCertCategory.IPSEC.toString());
            isExecutable = true;
        } else if (WorkflowNames.WORKFLOW_COMECIM_ComIssueTrustCert.toString().equals(workflowName)) {
            if (trustedCACertificates != null) {
                final List<TrustedCACertificateInfo> uniqueTrustedCACertInfos = trustedCACertificates.getTrustedCACertificateInfo();
                workflowVars.put(WorkflowParameterKeys.EXTERNAL_TRUSTED_CA_CERTIFICATE_INFO.toString(),
                        prepareTrustedEntitiesInfo(uniqueTrustedCACertInfos));
                workflowVars.put(WorkflowParameterKeys.EXTERNAL_CA_INTERFACE_FDN.toString(), node.getInterfaceFdn());
                workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), EXTERNAL_CA);
            } else {
                workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), ENM_PKI_CA);
            }
            workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.fromTrustCategoryType(TrustCategoryType.valueOf(trustCategory)).name());
            isExecutable = true;
        } else if (WorkflowNames.WORKFLOW_CBPOI_INSTALL_TRUST_CERTS.toString().equals(workflowName)) {
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), ENM_PKI_CA);
            isExecutable = true;
        } else {
            logger.error("Invalid workflow name [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeRef, trustCategory);
        }

        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), caName);

        if (isExecutable) {
            try {
                result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);
            } catch (final Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new CertificateIssueWfException();
            }
            logger.debug("Started scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeRef,
                    trustCategory);
        }
        return result;
    }

    /**
     * This method is used to prepare the trustedEntitiesInfo map which contains tdpsUrl,trustedCACertIssuerDn,certificateSerialNumber values from the
     * list of TrustedCACertificateInfo.
     * 
     * @param trustedCACertificateInfos
     * @return List<Map<String, String>>
     */
    private List<Map<String, String>> prepareTrustedEntitiesInfo(final List<TrustedCACertificateInfo> trustedCACertificateInfos) {
        Map<String, String> trustedEntitiesInfo = null;
        List<Map<String, String>> trustedCACertInfos = new ArrayList<>();
        for (final TrustedCACertificateInfo trustedCACertificateInfo : trustedCACertificateInfos) {
            trustedEntitiesInfo = new HashMap<>();
            trustedEntitiesInfo.put("tdpsUrl", trustedCACertificateInfo.getTDPSUrl());
            trustedEntitiesInfo.put("trustedCACertIssuerDn", trustedCACertificateInfo.getTrustedCACertIssuerDn());
            trustedEntitiesInfo.put("certificateSerialNumber", trustedCACertificateInfo.getCertificateSerialNumber());
            trustedCACertInfos.add(trustedEntitiesInfo);
        }
        return trustedCACertInfos;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeTrustRemoveSingleWf(final NodeReference validNode, final String issuerDn, final String serialNumber,
            final String trustCategory, final JobStatusRecord jobStatusRecord, final int workflowId) {
        WfResult result = null;

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        boolean isExecutable = false;
        final String workflowName = capabilityModel.getTrustRemoveWf(validNode, trustCategory);

        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), serialNumber);
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), issuerDn);

        if (WorkflowNames.WORKFLOW_CPPRemoveTrustOAM.toString().equals(workflowName)) {
            if (trustCategory.equals(TrustCategoryType.OAM.toString())) {
                workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
            } else if (trustCategory.equals(TrustCategoryType.LAAD.toString())) {
                workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS.toString());
            }
            isExecutable = true;
        } else if (WorkflowNames.WORKFLOW_CPPRemoveTrustIPSEC.toString().equals(workflowName)) {
            workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.IPSEC.toString());
            isExecutable = true;
        } else if (WorkflowNames.WORKFLOW_COMECIMRemoveTrust.toString().equals(workflowName)
                || WorkflowNames.WORKFLOW_CBPOI_REMOVE_TRUST.toString().equals(workflowName)) {
            workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(),
                    TrustedCertCategory.fromCertificateType(CertificateType.valueOf(trustCategory)).toString());
            isExecutable = true;
        } else {
            logger.error("Invalid workflow name [{}] for node: [{}] with variable certificate type [{}] ", workflowName, validNode, trustCategory);
        }

        if (isExecutable) {
            try {
                result = wfHandler.getScheduledWorkflowInstanceResult(validNode, workflowName, workflowVars, jobStatusRecord, workflowId);
            } catch (final Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new CertificateIssueWfException();
            }
            logger.debug("Started scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, validNode,
                    trustCategory);
        }
        return result;
    }

    @Override
    public void insertWorkflowBatch(final Map<UUID, WfResult> wfResultMap) {
        wfHandler.insertWorkflowBatch(wfResultMap);
    }

    private EnrollmentMode getEnrollmentMode(final NodeReference nodeRef) {

        EnrollmentMode enrMode = EnrollmentMode.NOT_SUPPORTED;
        try {
            logger.debug("Getting Enrollment Mode in NetworkElementSecurity MO {}", nodeRef.getFdn());

            final CmResponse response = reader.getMOAttribute(nodeRef, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), NetworkElementSecurity.ENROLLMENT_MODE);

            if (response != null) {
                String stringEnrMode = "";
                for (final CmObject cmObjIntfs : response.getCmObjects()) {

                    stringEnrMode = (String) cmObjIntfs.getAttributes().get(NetworkElementSecurity.ENROLLMENT_MODE);
                    logger.debug("EnrollmentMode retrieved: {}", stringEnrMode);
                }
                if (!stringEnrMode.isEmpty()) {
                    enrMode = EnrollmentMode.valueOf(stringEnrMode);
                }
            }

            logger.debug("EnrolmentMode for node {} is {}", nodeRef.getFdn(), enrMode);

        } catch (final Exception e) {
            logger.warn("Get of Enrollment mode in NetworkElementSecurity MO failed!", e);
        }

        return enrMode;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeSetCiphersSingleWf(final NodeReference nodeRef, final NodeCiphers nodeCiphers, final JobStatusRecord jobStatusRecord,
            final int workflowId) {
        logger.debug("executeSetCiphersSingleWf() - nodeFdn: [{}]", nodeRef.getFdn());
        WfResult result = null;
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        final NormalizableNodeReference normalizableNodeRef = reader.getNormalizableNodeReference(nodeRef);
        final Map<String, String> cipherMoNames = CiphersCommandHelper.getCipherMoNames();
        final Map<String, Map<String, String>> cipherMoAttributes = capabilityModel.getCipherMoAttributes(normalizableNodeRef);
        final Map<String, Map<String, Object>> cipherAttributeValuesForWorkFlow = getCipherAttributeValues(nodeCiphers, cipherMoAttributes,
                cipherMoNames);
        workflowVars.put(WorkflowParameterKeys.MO_ATTRIBUTES_KEY_VALUES.toString(), cipherAttributeValuesForWorkFlow);
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, WorkflowNames.WORKFLOW_SET_CIPHERS.getWorkflowName(), workflowVars,
                    jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new SetCiphersWfException();
        }

        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] ", WorkflowNames.WORKFLOW_SET_CIPHERS.getWorkflowName(), nodeRef);
        return result;
    }

    private Map<String, Map<String, Object>> getCipherAttributeValues(final NodeCiphers nodeCiphers,
            final Map<String, Map<String, String>> cipherMoAttributes, final Map<String, String> cipherMoNames) {
        final Map<String, Map<String, Object>> cipherAttributeValues = new HashMap<String, Map<String, Object>>();
        Map<String, String> moAttributes = null;

        if (nodeCiphers.getTlsProtocol() != null) {
            final Map<String, Object> moAttributesValueMap = new HashMap<String, Object>();
            moAttributes = cipherMoAttributes.get(CiphersConstants.PROTOCOL_TYPE_TLS);
            moAttributesValueMap.put(moAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_CIPHER_FILTER),
                    nodeCiphers.getTlsProtocol().getCipherFilter());
            cipherAttributeValues.put(cipherMoNames.get(CiphersConstants.PROTOCOL_TYPE_TLS), moAttributesValueMap);
        }
        if (nodeCiphers.getSshProtocol() != null) {
            final Map<String, Object> moAttributesValueMap = new HashMap<String, Object>();
            moAttributes = cipherMoAttributes.get(CiphersConstants.PROTOCOL_TYPE_SSH);
            if (nodeCiphers.getSshProtocol().getMacCiphers() != null) {
                moAttributesValueMap.put(moAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_MAC),
                        nodeCiphers.getSshProtocol().getMacCiphers().getCipher());
            }
            if (nodeCiphers.getSshProtocol().getKeyExchangeCiphers() != null) {
                moAttributesValueMap.put(moAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_KEY_EXCHANGE),
                        nodeCiphers.getSshProtocol().getKeyExchangeCiphers().getCipher());
            }
            if (nodeCiphers.getSshProtocol().getEncryptCiphers() != null) {
                moAttributesValueMap.put(moAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_CIPHER),
                        nodeCiphers.getSshProtocol().getEncryptCiphers().getCipher());
            }
            cipherAttributeValues.put(cipherMoNames.get(CiphersConstants.PROTOCOL_TYPE_SSH), moAttributesValueMap);
        }
        return cipherAttributeValues;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeCrlDownload(final NodeReference nodeRef, final Map<String, Object> workFlowParams, final JobStatusRecord jobStatusRecord,
            final int workflowId) {

        WfResult result = null;
        final String nodeFdn = nodeRef.getName();
        final String workFlowName = capabilityModel.getOnDemandCrlDownloadWf(nodeRef);
        try {
            logger.debug("Starting workflow [{}] for node: [{}] ", workFlowName, nodeFdn);
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workFlowName, workFlowParams, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new OnDemandCrlDownloadWfException();
        }
        logger.info("Started workflow [{}] for node: [{}] ", workFlowName, nodeFdn);

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeCrlCheckWfs(final NodeReference nodeRef, final String certType, final String crlCheckStatus,
            final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        final String workFlowName = capabilityModel.getCrlCheckWf(nodeRef, certType);
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.CERT_TYPE.toString(), certType);
        workflowVars.put(WorkflowParameterKeys.CRL_CHECK_STATUS.toString(), crlCheckStatus);
        try {
            logger.debug("Starting workflow [{}] for node: [{}] with certificate type: [{}] and variables: [{}]", workFlowName, nodeRef, certType,
                    workflowVars);
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workFlowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new CrlCheckEnableOrDisableWfException();
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeActivateHttpsWfs(final NodeReference nodeReference, final CertIssueWfParams wfParams, final boolean isReissue,
            final String revocationReason, final JobStatusRecord jobStatusRecord, final int workflowId) {

        final String workflowName = WorkflowNames.WORKFLOW_CPP_ACTIVATE_HTTPS.getWorkflowName();

        logger.debug("executeActivateHttpsWfs() - nodeFdn: [{}]", nodeReference.getFdn());
        WfResult result = null;

        final Map<String, Object> workflowVars = new HashMap<>();
        workflowVars.put(WorkflowParameterKeys.CERT_TYPE.toString(), wfParams.getCertType());
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(),
                TrustedCertCategory.fromCertificateType(CertificateType.valueOf(wfParams.getCertType())).name());
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), ENM_PKI_CA);
        workflowVars.put(WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString(), TRUE);
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeReference, workflowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new HttpsActivateOrDeactivateWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeReference,
                wfParams.getCertType());

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeDeactivateHttpsWfs(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        final Map<String, Object> workflowVars = new HashMap<>();
        final String workflowName = WorkflowNames.WORKFLOW_CPP_DEACTIVATE_HTTPS.getWorkflowName();

        try {
            logger.debug("Starting workflow [{}] for node: [{}] with variables: [{}]", workflowName, nodeRef, workflowVars);
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);

        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new HttpsActivateOrDeactivateWfException();
        }

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeGetHttpsStatusWfs(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        final Map<String, Object> workflowVars = new HashMap<>();
        final String workflowName = WorkflowNames.WORKFLOW_CPP_GET_HTTPS.getWorkflowName();
        try {
            logger.debug("Starting workflow [{}] for node: [{}] with variables: [{}]", workflowName, nodeRef, workflowVars);
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);

        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new GetHttpsWfException();
        }

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeActivateFtpesWfs(final NodeReference nodeReference, final CertIssueWfParams wfParams, final boolean isReissue,
            final String revocationReason, final JobStatusRecord jobStatusRecord, final int workflowId) {

        final String workflowName = WorkflowNames.WORKFLOW_COM_ACTIVATE_FTPES.getWorkflowName();

        logger.debug("executeActivateFtpesWfs() - nodeFdn: [{}]", nodeReference.getFdn());
        WfResult result = null;

        final Map<String, Object> workflowVars = new HashMap<>();
        workflowVars.put(WorkflowParameterKeys.CERT_TYPE.toString(), wfParams.getCertType());
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(),
                TrustedCertCategory.fromCertificateType(CertificateType.valueOf(wfParams.getCertType())).name());
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), ENM_PKI_CA);
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeReference, workflowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new FtpesActivateOrDeactivateWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] with variable certificate type [{}] ", workflowName, nodeReference,
                wfParams.getCertType());

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeDeactivateFtpesWfs(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        String workflowName = null;
        final Map<String, Object> workflowVars = new HashMap<>();
        workflowName = WorkflowNames.WORKFLOW_COM_DEACTIVATE_FTPES.getWorkflowName();

        try {
            logger.debug("Starting workflow [{}] for node: [{}] with variables: [{}]", workflowName, nodeRef, workflowVars);
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);

        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);

            throw new FtpesActivateOrDeactivateWfException();
        }

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeConfigureLdapWfs(final NodeSpecificLdapConfiguration node, final JobStatusRecord jobStatusRecord, final int workflowId) {

        final String nodeFdn = node.getNodeFdn();
        final NodeReference nodeReference = new NodeRef(nodeFdn);
        final NormalizableNodeReference normalizableNodeRef = reader.getNormalizableNodeReference(nodeReference);
        final String workflowName = capabilityModel.getLdapConfigureWorkflow(normalizableNodeRef);

        WfResult result = null;

        final Map<String, Object> workflowVars = new HashMap<>();
        workflowVars.put(WorkflowParameterKeys.TLS_MODE.toString(), node.getTlsMode());
        workflowVars.put(WorkflowParameterKeys.USE_TLS.toString(), node.getUseTls());
        workflowVars.put(WorkflowParameterKeys.USER_LABEL.toString(), node.getUserLabel());

        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeReference, workflowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new LdapConfigureWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] with variable label type [{}] ", workflowName, nodeReference,
                node.getUserLabel());

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeLdapWf(final NodeSpecificLdapConfiguration node, final JobStatusRecord jobStatusRecord, final int workflowId,
            final boolean isRenew) {

        final String nodeFdn = node.getNodeFdn();
        final NodeReference nodeReference = new NodeRef(nodeFdn);
        final String workflowName = "LdapConfiguration";

        WfResult result = null;

        final Map<String, Object> workflowVars = new HashMap<>();
        workflowVars.put(WorkflowParameterKeys.TLS_MODE.toString(), node.getTlsMode());
        workflowVars.put(WorkflowParameterKeys.USE_TLS.toString(), node.getUseTls());
        workflowVars.put(WorkflowParameterKeys.USER_LABEL.toString(), node.getUserLabel());
        workflowVars.put(WorkflowParameterKeys.IS_RENEW.toString(), Boolean.valueOf(isRenew));

        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeReference, workflowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new LdapConfigureWfException();
        }
        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] with variable label type [{}] ", workflowName, nodeReference,
                node.getUserLabel());

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeTestSingleWf(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord, final int workflowId) {

        WfResult result = null;
        final String workflowName = "TestHighLevel";
        final Map<String, Object> workflowVars = new HashMap<>();
        if (workflowId % 7 == 0) {
            setTestWorkflowParameters(workflowVars, "THROW_ERROR", "ANY");
        } else if (workflowId % 6 == 0) {
            setTestWorkflowParameters(workflowVars, "THROW_FAILURE", "ANY");
        } else if (workflowId % 5 == 0) {
            setTestWorkflowParameters(workflowVars, "THROW_TIMEOUT", "ANY");
        } else if (workflowId % 4 == 0) {
            setTestWorkflowParameters(workflowVars, "CHECK_KO", "ANY");
        } else if (workflowId % 3 == 0) {
            setTestWorkflowParameters(workflowVars, "CHECK_OK", "TIMEOUT");
        } else if (workflowId % 2 == 0) {
            setTestWorkflowParameters(workflowVars, "CHECK_OK", "FAILURE");
        } else {
            setTestWorkflowParameters(workflowVars, "CHECK_OK", "SUCCESS");
        }
        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars, jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new TestWfsException(ex.getMessage(), ex.getCause());
        }
        return result;
    }

    private void setTestWorkflowParameters(final Map<String, Object> workflowVars, final String checkResult, final String actionResult) {
        workflowVars.put(WorkflowParameterKeys.TEST_CHECK_RESULT.toString(), checkResult);
        workflowVars.put(WorkflowParameterKeys.TEST_ACTION_RESULT.toString(), actionResult);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeIpSecWorkflow(final NodeReference nodeRef, final IpSecRequestWfsConfiguration request, final JobStatusRecord jobStatusRecord,
                                            final int workflowId) {
        logger.info("executeActivateIpSecWfs() - nodeFdn: [{}]", nodeRef.getFdn());
        WfResult result;
        final Map<String, Object> workflowVars = request.getWorkflowParams();
        final String workflowName = request.getWorkflowName();

        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars,
                    jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new IpSecWfException();
        }

        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] ", workflowName, nodeRef);
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WfResult executeSshKeyWorkflow(final NodeReference nodeRef, final SSHKeyWfsConfigurationDto request, final JobStatusRecord jobStatusRecord,
                                          final int workflowId) {
        logger.info("executeSshKeyWorkflow() - nodeFdn: [{}]", nodeRef.getFdn());
        WfResult result;
        final Map<String, Object> workflowVars = request.getWorkflowParams();
        final String workflowName = request.getWorkflowName();

        try {
            result = wfHandler.getScheduledWorkflowInstanceResult(nodeRef, workflowName, workflowVars,
                    jobStatusRecord, workflowId);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new SshKeyWfException();
        }

        logger.debug("Got scheduled workflow for child WF [{}] for node: [{}] ", workflowName, nodeRef);
        return result;
    }
}
