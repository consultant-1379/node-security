
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
import java.util.Set;
import java.util.UUID;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.CppIpSecWfsConfiguration;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequest;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestWfsConfiguration;
import com.ericsson.nms.security.nscs.ssh.SSHKeyRequestDto;
import com.ericsson.nms.security.nscs.ssh.SSHKeyWfsConfigurationDto;
import com.ericsson.nms.security.nscs.ssh.SSHKeyWfsUtility;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.cert.issue.CertIssueWfParams;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.RevocationReason;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.api.scheduler.WorkflowSchedulerInterface;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentInfo;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.NodeEnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.TrustedCAInformation;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CipherJobInfo;
import com.ericsson.nms.security.nscs.handler.command.impl.LdapCommandHandlerHelper;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes.NodeTrustInfo;
import com.ericsson.nms.security.nscs.util.CommandType;
import com.ericsson.nms.security.nscs.util.FtpesCommandType;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.security.nscs.command.util.CertificateTypeHelper;

@Stateless
public class NscsCommandManagerBean implements NscsCommandManager {

    @Inject
    private NscsCapabilityModelService capabilityModel;

    @Inject
    private Logger logger;

    @Inject
    private NodeValidatorUtility validatorUtility;

    @EJB
    private NscsPkiEntitiesManagerIF pkiManager;

    @EJB
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    @EJB
    private WorkflowSchedulerInterface workflowScheduler;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CppIpSecWfsConfiguration cppIpSecWfsConfiguration;

    @Inject
    private SSHKeyWfsUtility sshKeyWfsUtility;

    @EServiceRef
    WorkflowHandler wfHandler;

    /**
     * Return the list of valid certificate types.
     *
     * @return valid certificate types.
     */
    @Override
    public List<String> getValidCertificateTypes() {
        return CertificateTypeHelper.getValidCertificateTypes();
    }

    @Override
    public boolean validateNodesForCertificateIssue(final List<Node> inputNodesList, final String certType, final List<Node> validNodeList,
            final Map<NodeReference, NscsServiceException> invalidNodeErrors, final Map<String, String[]> invalidNodeDynamicErrors)
            throws NscsServiceException {
        logger.info("ValidateNodesForCertificateIssue. InputNodesList size: " + inputNodesList.size() + " certType: " + certType);

        //TODO: temporarily commented
        //validatorUtility.isNumberOfNodesAllowed(inputNodesList.size());
        boolean havingAllValidNodes = true;
        for (final Node inputNode : inputNodesList) {
            final String fdn = inputNode.getNodeFdn();
            final NodeReference nodeRef = new NodeRef(fdn);
            try {
                validatorUtility.validateNodeIssue(inputNode, certType);
                validNodeList.add(inputNode);
            } catch (UnassociatedNetworkElementException | InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | UnsupportedCertificateTypeException | SecurityFunctionMoNotfoundException | InvalidInputNodeListException
                    | InvalidEntityProfileNameXmlException | InvalidEntityProfileNameDefaultXmlException | AlgorithmKeySizeNotSupportedXmlException
                    | SubjAltNameSubjAltNameTypeEmptyXmlException | SubjAltNameTypeNotSupportedXmlException | InvalidSubjAltNameXmlException exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(nodeRef, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", fdn, exc.getMessage(), exc);
            } catch (final NodeNotSynchronizedException exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(nodeRef, exc);
                invalidNodeDynamicErrors.put(nodeRef.getFdn(), new String[] { "" + exc.getErrorCode(), exc.getMessage() });
                logger.error("Node [{}] has dynamic params validation problem. Exception is [{}]", fdn, exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    @Override
    public boolean validateNodesGetCertEnrollTrustInstallState(final String command, final String certType, final List<NodeReference> inputNodesList,
            final List<NodeReference> validNodesList, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            final Map<String, String[]> invalidDynamicNodesMap) {

        logger.info(String.format("ValidateNodesGetCertEnrollTrustInstallState. InputNodesList: %s", inputNodesList));
        logger.info("command[{}] certType[{}]", command, certType);
        //TODO: temporarily commented
        //validatorUtility.isNumberOfNodesAllowed(inputNodesList.size());
        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : inputNodesList) {
            try {
                validatorUtility.validateNodeGetCertEnrollTrustInstallState(nodeRef, certType, command);
                validNodesList.add(nodeRef);
            } catch (UnassociatedNetworkElementException | InvalidNodeNameException | UnsupportedCertificateTypeException
                    | NetworkElementNotfoundException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            } catch (NodeNotSynchronizedException | UnsupportedNodeTypeException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                invalidDynamicNodesMap.put(nodeRef.getFdn(), new String[] { "" + exc.getErrorCode(), exc.getMessage() });
                logger.error("Node [{}] has dynamic params validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    @Override
    public boolean validateNodesForCertificateReissue(final Map<Entity, NodeReference> inputNodesEntity, final List<Entity> unassociatedEntities,
            final Map<Entity, NodeReference> validNodes, final Map<Entity, NscsServiceException> invalidNodeErrors,
            final Map<String, String[]> invalidNodeDynamicErrors) {
        logger.info("ValidateNodesForCertificateReissue for {} entities", inputNodesEntity.size());
        boolean havingAllValidNodes = true;

        for (final Entry<Entity, NodeReference> entry : inputNodesEntity.entrySet()) {
            final Entity entity = entry.getKey();
            final NodeReference nodeRef = entry.getValue();
            logger.info("Name: {} - Category: {} - Fdn: {}", entity.getEntityInfo().getName(), entity.getCategory(), nodeRef.getFdn());
            try {
                final NodeEntityCategory nodeEntityCategory = NscsPkiEntitiesManagerJar.findNodeEntityCategory(entity.getCategory());
                final CertificateType certType = NodeEntityCategory.toCertType(nodeEntityCategory);
                logger.info("entityCategory[{}] converted to certType[{}]", nodeEntityCategory.name(), certType.name());
                validatorUtility.validateNodeCertificateReissue(nodeRef, certType.name());
                validNodes.put(entity, nodeRef);
            } catch (UnassociatedNetworkElementException | InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | UnsupportedCertificateTypeException | SecurityFunctionMoNotfoundException exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(entity, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            } catch (final NodeNotSynchronizedException exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(entity, exc);
                invalidNodeDynamicErrors.put(entity.getEntityInfo().getName(), new String[] { "" + exc.getErrorCode(), exc.getMessage() });
                logger.error("Node [{}] associated to entity [{}] has non blicking validation problem. Exception is [{}]", nodeRef.getFdn(),
                        entity.getEntityInfo().getName(), exc.getMessage());
            }
        }

        if (!unassociatedEntities.isEmpty()) {
            havingAllValidNodes = false;
            for (final Entity ent : unassociatedEntities) {
                final NscsServiceException exc = new InvalidNodeNameException();
                invalidNodeErrors.put(ent, exc);
                invalidNodeDynamicErrors.put(ent.getEntityInfo().getName(), new String[] { "" + exc.getErrorCode(), exc.getMessage() });
                logger.info("Node associated to entity [{}] does not exist. Exception is [{}]", ent.getEntityInfo().getName(), exc.getMessage());
            }
        }

        return havingAllValidNodes;
    }

    @Override
    public boolean validateNodesWithEntitiesForCertificateReissue(final List<Entity> inputEntityList, final String certType,
            final List<NodeReference> inputNodes, final Map<Entity, NodeReference> validEntityNodeMap,
            final Map<NodeReference, NscsServiceException> invalidNodeErrors, final Map<String, String[]> invalidNodeDynamicErrors) {
        logger.info(String.format("ValidateNodesForCertificateReissue. InputNodesList: %s", inputNodes));
        logger.info("certType[{}]", certType);
        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : inputNodes) {
            try {
                final List<Entity> validEntitiesList = new ArrayList<>();
                validatorUtility.validateNodesWithEntitiesForCertificateReissue(nodeRef, inputEntityList, certType, validEntitiesList);
                for (final Entity entity : validEntitiesList) {
                    validEntityNodeMap.put(entity, nodeRef);
                }

            } catch (UnassociatedNetworkElementException | InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | UnsupportedCertificateTypeException | SecurityFunctionMoNotfoundException | EntityForNodeNotFoundException
            //                    | EntityWithValidCategoryNotFoundException
            //                    | EntityWithActiveCertificateNotFoundException
            exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(nodeRef, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            } catch (NodeNotSynchronizedException | EntityWithValidCategoryNotFoundException exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(nodeRef, exc);
                invalidNodeDynamicErrors.put(nodeRef.getFdn(), new String[] { "" + exc.getErrorCode(), exc.getMessage() });
                logger.error("Node [{}] has dynamic params validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    @Override
    public boolean validateNodesWithEntitiesForCertificateReissue(final String certType, final List<NodeReference> inputNodes,
            final List<NodeReference> validNodesList, final Map<NodeReference, NscsServiceException> invalidNodeErrors,
            final Map<String, String[]> invalidNodeDynamicErrors) {
        logger.info(String.format("ValidateNodesForCertificateReissue. InputNodesList: %s", inputNodes));
        logger.info("certType[{}]", certType);
        boolean havingAllValidNodes = true;

        for (final NodeReference nodeRef : inputNodes) {
            try {
                validatorUtility.validateNodesWithEntitiesForCertificateReissue(nodeRef, certType);
                validNodesList.add(nodeRef);

            } catch (UnassociatedNetworkElementException | InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | UnsupportedCertificateTypeException | SecurityFunctionMoNotfoundException | EntityForNodeNotFoundException exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(nodeRef, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            } catch (NodeNotSynchronizedException | EntityWithValidCategoryNotFoundException exc) {
                havingAllValidNodes = false;
                invalidNodeErrors.put(nodeRef, exc);
                invalidNodeDynamicErrors.put(nodeRef.getFdn(), new String[] { "" + exc.getErrorCode(), exc.getMessage() });
                logger.error("Node [{}] has dynamic params validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    @Override
    public boolean validateCertTypeValue(final String inputCertType) {
        logger.info(String.format("ValidateCertTypeValue. InputCertType: %s", inputCertType));
        boolean isCertTypeValueAllowed = false;
        if (CertificateTypeHelper.isCertificateTypeValid(inputCertType)) {
            isCertTypeValueAllowed = true;
        }
        logger.info(String.format("State = %s, CertType = %s", isCertTypeValueAllowed, inputCertType));
        return isCertTypeValueAllowed;
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeCertificateIssueWfs(final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList, final String certType,
            final JobStatusRecord jobStatusRecord) {

        final String revocationReason = RevocationReason.UNSPECIFIED.toString();
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        final boolean isReissue = true;
        int workflowId = 1;

        for (final NodeEnrollmentDetails nodeEnrollmentDetails : nodeEnrollmentDetailsList) {

            final List<Node> inputNodeList = nodeEnrollmentDetails.getNodes().getNode();

            final ExternalCAEnrollmentInfo extCAEnrollmentInfo = nodeEnrollmentDetails.getExternalCAEnrollmentInfo();

            logger.info("executeCertificateIssueWfs. InputNodeList: " + inputNodeList + " and CertType: " + certType);

            for (final Node inputNode : inputNodeList) {
                final String entityProfileName = inputNode.getEntityProfileName();
                final String subjAltName = inputNode.getSubjectAltName();
                final BaseSubjectAltNameDataType subjectAltNameDataType = new SubjectAltNameStringType(subjAltName);
                final String subjAltNameType = NscsPkiUtils
                        .convertSubjectAltNameFieldTypeToNscsFormat(toSubjectAltNameFieldType(inputNode.getSubjectAltNameType()), subjectAltNameDataType).name();
                final String enrollmentMode = toEnrollmentMode(inputNode.getEnrollmentMode());
                final String keySize = toAlgorithmKeySize(inputNode.getKeySize());
                final String commonName = inputNode.getCommonName();

                final CertIssueWfParams wfParams = new CertIssueWfParams(certType, entityProfileName, subjAltName, subjAltNameType, enrollmentMode,
                        keySize, commonName);
                final WfResult result = nscsCommandManagerProcessor.executeCertificateIssueSingleWf(extCAEnrollmentInfo, inputNode, wfParams,
                        isReissue, revocationReason, jobStatusRecord, workflowId);
                if (result != null) {
                    wfResultMap.put(result.getWfWakeId(), result);
                    workflowId++;
                }
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeCertificateReissueWfs(final Map<Entity, NodeReference> validNodeEntityList, final String inputReason, final String certType,
            final JobStatusRecord jobStatusRecord) {
        logger.info("executeCertificateReissueWfs() - validNodeEntityList size [{}]", validNodeEntityList.size());
        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();

        int workflowId = 1;
        for (final Entry<Entity, NodeReference> entry : validNodeEntityList.entrySet()) {

            final WfResult result = nscsCommandManagerProcessor.executeCertificateReIssueSingleWf(entry, inputReason, certType, jobStatusRecord,
                    workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);

        //workflowScheduler.forcedSchedule();
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeCertificateReissueWfs(final List<NodeReference> validNodesList, final String inputReason, final String certType,
                                             final JobStatusRecord jobStatusRecord) {
        logger.info("executeCertificateReissueWfs() - validNodesList size [{}]", validNodesList.size());
        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();

        int workflowId = 1;
        for (final NodeReference entry : validNodesList) {

            final WfResult result = nscsCommandManagerProcessor.executeCertificateReIssueSingleWf(entry, inputReason, certType, jobStatusRecord,
                    workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);

        //workflowScheduler.forcedSchedule();
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeTrustDistributeWfs(final String trustCategory, final String caName, final JobStatusRecord jobStatusRecord,
            final List<TrustedCAInformation> trustedCAInformationlist) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();
        int workflowId = 1;
        for (final TrustedCAInformation trustedCAInformation : trustedCAInformationlist) {
            final List<NodeTrustInfo> nodeList = trustedCAInformation.getValidNodes();
            for (final NodeTrustInfo validNode : nodeList) {
                final WfResult result = nscsCommandManagerProcessor.executeTrustDistributeSingleWf(validNode, trustCategory, caName, jobStatusRecord,
                        workflowId, trustedCAInformation.getTrustedCACertificates());
                if (result != null) {
                    wfResultMap.put(result.getWfWakeId(), result);
                    workflowId++;
                }
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeTrustRemoveWfs(final List<NodeReference> validNodesList, final String issuerDn, final String serialNumber,
            final String certType, final JobStatusRecord jobStatusRecord) {
        logger.info("executeTrustRemoveWfs() - InputNodeList size: " + validNodesList.size() + ", issuerDn: " + issuerDn + ", serialNumber:"
                + serialNumber);
        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();
        int workflowId = 1;
        for (final NodeReference validNode : validNodesList) {

            final WfResult result = nscsCommandManagerProcessor.executeTrustRemoveSingleWf(validNode, issuerDn, serialNumber, certType,
                    jobStatusRecord, workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
        //        workflowScheduler.forcedSchedule();
    }

    /**
     * @param inputNodeList
     *          is the list of NodeReference values
     * @param errorMsg
     *          is the list of error messages
     */
    @Override
    public boolean isEnrollmentModeSupportedForNodeList(final List<Node> inputNodeList, final List<String> errorMsg) {

        for (final Node inputNode : inputNodeList) {
            final NodeReference nodeRef = new NodeRef(inputNode.getNodeFdn());
            final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
            final List<String> supportedEnrollmentModes = capabilityModel.getSupportedEnrollmentModes(normNode);
            final String enrollmentMode = inputNode.getEnrollmentMode().trim();
            if (supportedEnrollmentModes == null || (!enrollmentMode.isEmpty() && !supportedEnrollmentModes.contains(enrollmentMode))) {
                errorMsg.add(String.format(" Invalid Enrollment Mode [%s] for node [%s] with neType [%s]. Accepted arguments are [%s]",
                        enrollmentMode, normNode.getFdn(), normNode.getNeType(), supportedEnrollmentModes));
                return false;
            }
        }
        return true;
    }

    public SubjectAltNameFieldType toSubjectAltNameFieldType(final String subjectAltNameType) {

        if (subjectAltNameType != null && !subjectAltNameType.isEmpty()) {
            try {
                logger.debug("toSubjectAltNameFieldType value [{}]", subjectAltNameType);
                return SubjectAltNameFieldType.valueOf(subjectAltNameType);
            } catch (final Exception ex) {
                logger.error("Invalid value for SubjectAltNameType: {}", subjectAltNameType);
            }
        } else {
            logger.debug("toSubjectAltNameFieldType value is null or empty");
        }
        return SubjectAltNameFieldType.OTHER_NAME;
    }

    public String toEnrollmentMode(final String value) {
        if (value != null && !value.isEmpty()) {
            try {
                logger.debug("toEnrollmentMode value [{}]", value);
                return EnrollmentMode.valueOf(value).name();
            } catch (final Exception ex) {
                logger.warn("Invalid value for Enrollment Mode: {}", value);
            }
        } else {
            logger.debug("toEnrollmentMode null or empty value");
        }
        return "";
    }

    public String toAlgorithmKeySize(final String value) {
        if (value != null && !value.isEmpty()) {
            try {
                logger.debug("toAlgorithmKeySize value {}", value);
                return AlgorithmKeys.valueOf(value).name();
            } catch (final Exception ex) {
                logger.error("Invalid value for AlgorithmKeySize: [{}]", value);
            }
        } else {
            logger.debug("toAlgorithmKeySize null or empty value");
        }
        return "";
    }

    @Override
    public boolean validateReasonValue(final String reason) {
        logger.info(String.format("validateReasonValue. Reason: %s", reason));
        final boolean isReasonValueAllowed = isReasonValid(reason);
        logger.info(String.format("State = %s, Reason = %s", isReasonValueAllowed, reason));
        return isReasonValueAllowed;
    }

    private boolean isReasonValid(final String reason) {
        boolean isReasonValueAllowed = false;
        for (final RevocationReason rr : RevocationReason.values()) {
            if (rr.toString().equals(reason)) {
                isReasonValueAllowed = true;
            }
        }
        logger.info(String.format("State = %s, reason = %s", isReasonValueAllowed, reason));
        return isReasonValueAllowed;
    }

    @Override
    public boolean isNodePresent(final String entityNodeName) {

        final NodeReference nodeRef = new NodeRef(entityNodeName);
        return validatorUtility.isNodeExists(nodeRef);
    }

    @Override
    public Set<String> validateDuplicatedNodes(final List<NodeReference> inputNodes) {
        return validatorUtility.validateDuplicatedNodes(inputNodes);
    }

    /**
     * This method is used to validate the given nodes for CrlCheck command.If all of the given input nodes are valid then this method will return
     * true.If any one of the given node is invalid then this method will return false.
     *
     * @param inputNodesList
     *            is the list of NodeReference values
     * @param certType
     *            the certificate type value.
     * @param validNodesList
     *            Only valid nodes are added to this list.
     * @param invalidNodesErrorMap
     *            All invalid nodes are added to this map.
     * @param invalidDynamicNodesMap
     *            Only unsynchronized nodes are added to this map.
     *
     * @return {@link Boolean}
     *         <p>
     *         true: if all nodes are valid.
     *         </p>
     *         false: if any one of the given node is invalid.
     *
     */
    @Override
    public boolean validateNodesForCrlCheck(final List<NodeReference> inputNodesList, final String certType, final List<NodeReference> validNodesList,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final Map<String, String[]> invalidDynamicNodesMap,
            final Boolean isReadCmd) {
        nscsLogger.debug(String.format("validateNodesForCrlCheck.InputNodesList: %s", inputNodesList));
        nscsLogger.debug("certType[{}]", certType);
        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : inputNodesList) {
            try {
                validatorUtility.validateNodeForCrlCheck(NscsCapabilityModelService.CRLCHECK_COMMAND, nodeRef, certType, isReadCmd);
                validNodesList.add(nodeRef);
            } catch (UnassociatedNetworkElementException | NodeDoesNotExistException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | UnsupportedCertificateTypeException | SecurityFunctionMoNotfoundException | UnSupportedNodeReleaseVersionException
                    | TrustCategoryMODoesNotExistException | SecurityMODoesNotExistException | UnsupportedNodeTypeException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            } catch (final NodeNotSynchronizedException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                invalidDynamicNodesMap.put(nodeRef.getFdn(), new String[] { "" + exc.getErrorCode(), exc.getMessage(), exc.getSuggestedSolution() });
                nscsLogger.error("Node [{}] has dynamic params validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;

    }

    /**
     * This method is used to start WorkflowInstance for Crl check operation for nodes.
     *
     * @param validNodesList
     *            contains list of valid nodes.
     * @param certType
     *            the certificate type value.
     * @param crlCheckStatus
     *            status of CRL Check.
     * @param jobStatusRecord
     *            the CRL check enable/disable jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeCrlCheckWfs(final List<NodeReference> validNodesList, final String certType, final String crlCheckStatus,
                                   final JobStatusRecord jobStatusRecord) throws NscsServiceException {
        nscsLogger.debug(String.format("executeCrlCheckWfs. InputNodeList: %s and CertType: %s", validNodesList, certType));
        if (!validNodesList.isEmpty()) {
            startCrlCheckWfs(validNodesList, certType, crlCheckStatus, jobStatusRecord);
        }
    }

    private void startCrlCheckWfs(final List<NodeReference> validNodesList, final String certType, final String crlCheckStatus,
            final JobStatusRecord jobStatusRecord) {
        logger.debug(String.format("startCrlCheckWfs. InputNodeList: %s and CertType: %s", validNodesList, certType));

        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();
        int workflowId = 1;
        for (final NodeReference validNode : validNodesList) {
            try {
                final WfResult result = nscsCommandManagerProcessor.executeCrlCheckWfs(validNode, certType, crlCheckStatus, jobStatusRecord,
                        workflowId);

                if (result != null) {
                    wfResultMap.put(result.getWfWakeId(), result);
                    workflowId++;
                }
            } catch (final Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                throw new CrlCheckEnableOrDisableWfException();
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    /**
     * This method is used to validate the given nodes for on demand crl download command.If all of the given input nodes are valid then this method
     * will return true.If any one of the given node is invalid then this method will return false.
     *
     * @param inputNodesList
     *            is the list of NodeReference values
     * @param validNodesList
     *            Only valid nodes are added to this list.
     * @param invalidNodesErrorMap
     *            All invalid nodes are added to this map.
     * @param invalidDynamicNodesMap
     *            Only unsynchronized nodes are added to this map.
     *
     * @return {@link Boolean}
     *         <p>
     *         true: if all nodes are valid.
     *         </p>
     *         false: if any one of the given node is invalid.
     *
     */
    @Override
    public boolean validateNodesForOnDemandCrlDownload(final List<NodeReference> inputNodesList, final List<NodeReference> validNodesList,
                                                       final Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
                                                       final Map<String, String[]> invalidDynamicNodesMap) {
        nscsLogger.debug(String.format("validateNodesFromEntitiesForOnDemandCrlDownload.InputNodesList: %s", inputNodesList));
        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : inputNodesList) {
            try {
                validatorUtility.validateNodeForOnDemandCrlDownload(NscsCapabilityModelService.CRLDOWNLOAD_COMMAND, nodeRef);
                validNodesList.add(nodeRef);
            } catch (UnassociatedNetworkElementException | NodeDoesNotExistException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | SecurityFunctionMoNotfoundException | UnSupportedNodeReleaseVersionException | UnsupportedNodeTypeException
                    | SecurityMODoesNotExistException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            } catch (final NodeNotSynchronizedException exc) {
                havingAllValidNodes = false;
                invalidNodesErrorMap.put(nodeRef, exc);
                invalidDynamicNodesMap.put(nodeRef.getFdn(), new String[] { "" + exc.getErrorCode(), exc.getMessage(), exc.getSuggestedSolution() });
                nscsLogger.error("Node [{}] has dynamic params validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;

    }

    /**
     * This method is used to start WorkflowInstance for on demand crl download operation for nodes.
     *
     * @param validNodesList
     *            contains list of valid nodes.
     * @param jobStatusRecord
     *            the on demand CRL download jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeOnDemandCrlDownloadWfs(final List<NodeReference> validNodesList, final JobStatusRecord jobStatusRecord)
            throws NscsServiceException {
        nscsLogger.debug(String.format("executeOnDemandCrlDownloadWfs : InputNodeList: %s ", validNodesList));

        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();
        final Map<String, Object> workFlowParams = new HashMap<String, Object>();

        int workflowId = 1;
        for (final NodeReference validNode : validNodesList) {
            try {
                final WfResult result = nscsCommandManagerProcessor.executeCrlDownload(validNode, workFlowParams, jobStatusRecord, workflowId);

                if (result != null) {
                    wfResultMap.put(result.getWfWakeId(), result);
                    workflowId++;
                }
            } catch (final Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                throw new OnDemandCrlDownloadWfException();
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    @Override
    public void executeSetCiphersWfs(final List<CipherJobInfo> cipherJobList, final JobStatusRecord jobStatusRecord) {

        int newWorkflowId = 1;

        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();

        for (final CipherJobInfo cji : cipherJobList) {
            nscsLogger.debug(String.format("executeSetCiphersWfs. InputNodeList: %s", cji.getValidNodesList()));
            for (final NodeReference validNode : cji.getValidNodesList()) {
                final WfResult result = nscsCommandManagerProcessor.executeSetCiphersSingleWf(validNode, cji.getNodeCiphers(), jobStatusRecord,
                        newWorkflowId);
                if (result != null) {
                    wfResultMap.put(result.getWfWakeId(), result);
                    newWorkflowId++;
                }
            }
        }

        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    /**
     * This method is used to start WorkflowInstance for activate HTTPS attribute for nodes.
     *
     * @param nodesList
     *            contains list of nodes.
     * @param jobStatusRecord
     *            the HTTPS attribute activate jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeActivateHttpsWfs(final List<NodeReference> nodesList, final JobStatusRecord jobStatusRecord) throws NscsServiceException {
        nscsLogger.debug(String.format("executeActivateHttpsWfs. InputNodeList: %s", nodesList));

        executeHttpsWfs(nodesList, jobStatusRecord, CommandType.ACTIVATE);
    }

    /**
     * This method is used to start WorkflowInstance for deactivate HTTPS attribute for nodes.
     *
     * @param nodesList
     *            contains list of nodes.
     * @param jobStatusRecord
     *            the HTTPS attribute deactivate jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeDeactivateHttpsWfs(final List<NodeReference> nodesList, final JobStatusRecord jobStatusRecord) throws NscsServiceException {
        nscsLogger.debug(String.format("executeDeactivateHttpsWfs. InputNodeList: %s", nodesList));

        executeHttpsWfs(nodesList, jobStatusRecord, CommandType.DEACTIVATE);
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeGetHttpsStatusWfs(final List<NodeReference> nodesList, final JobStatusRecord jobStatusRecord) throws NscsServiceException {

        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        int workflowId = 1;

        for (final NodeReference node : nodesList) {

            final WfResult result = nscsCommandManagerProcessor.executeGetHttpsStatusWfs(node, jobStatusRecord, workflowId);

            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);

    }

    /**
     * This method is used to start WorkflowInstance for activate FTPES attribute for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            the FTPES attribute activate jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeActivateFtpesWfs(final List<NodeReference> nodes, final JobStatusRecord jobStatusRecord) {
        nscsLogger.debug(String.format("executeActivateFtpesWfs. InputNodeList: %s", nodes));

        executeFtpesWfs(nodes, jobStatusRecord, FtpesCommandType.ACTIVATE);
    }

    /**
     * This method is used to start WorkflowInstance for deactivate FTPES attribute for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            the FTPES attribute deactivate jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeDeactivateFtpesWfs(final List<NodeReference> nodes, final JobStatusRecord jobStatusRecord) {
        nscsLogger.debug(String.format("executeDeactivateFtpesWfs. InputNodeList: %s", nodes));

        executeFtpesWfs(nodes, jobStatusRecord, FtpesCommandType.DEACTIVATE);
    }

    private void executeHttpsWfs(final List<NodeReference> nodesList, final JobStatusRecord jobStatusRecord, final CommandType commandType) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        int workflowId = 1;

        for (final NodeReference node : nodesList) {

            WfResult result = null;
            final CertIssueWfParams wfParams = new CertIssueWfParams("OAM", null, null, null, EnrollmentMode.CMPv2_INITIAL.getEnrollmentModeValue(),
                    null, null);

            switch (commandType) {
            case ACTIVATE:
                result = nscsCommandManagerProcessor.executeActivateHttpsWfs(node, wfParams, true, RevocationReason.UNSPECIFIED.toString(),
                        jobStatusRecord, workflowId);
                break;
            case DEACTIVATE:
                result = nscsCommandManagerProcessor.executeDeactivateHttpsWfs(node, jobStatusRecord, workflowId);
                break;
            default:
                throw new IllegalArgumentException("Not supported command type.");
            }

            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    private void executeFtpesWfs(final List<NodeReference> nodesList, final JobStatusRecord jobStatusRecord, final FtpesCommandType commandType) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        int workflowId = 1;

        for (final NodeReference node : nodesList) {

            WfResult result = null;
            final CertIssueWfParams wfParams = new CertIssueWfParams("OAM", null, null, null, null, null, null);

            switch (commandType) {
            case ACTIVATE:
                result = nscsCommandManagerProcessor.executeActivateFtpesWfs(node, wfParams, true, RevocationReason.UNSPECIFIED.toString(),
                        jobStatusRecord, workflowId);
                break;
            case DEACTIVATE:
                result = nscsCommandManagerProcessor.executeDeactivateFtpesWfs(node, jobStatusRecord, workflowId);
                break;
            default:
                throw new IllegalArgumentException("Not supported command type.");
            }

            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    /**
     * This method is used to start WorkflowInstance for configure LDAP attribute for nodes.
     *
     * @param nodeSpecificLdapConfiguration
     *            contains list of nodes.
     * @param jobStatusRecord
     *            the LDAP attribute activate jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeConfigureLdapWfs(final List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfiguration, final JobStatusRecord jobStatusRecord) {
        nscsLogger.debug(String.format("executeConfigureLdapWfs. InputNodeList: %s", nodeSpecificLdapConfiguration));

        executeLdapWfs(nodeSpecificLdapConfiguration, jobStatusRecord, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE);
    }

    /**
     * This method is used to start WorkflowInstance for reconfigure LDAP attribute for nodes.
     *
     * @param nodeSpecificLdapConfiguration
     *            contains list of nodes.
     * @param jobStatusRecord
     *            the LDAP attribute deactivate jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeReconfigureLdapWfs(final List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfiguration, final JobStatusRecord jobStatusRecord) {
        nscsLogger.debug(String.format("executeReconfigureLdapWfs. InputNodeList: %s", nodeSpecificLdapConfiguration));

        executeLdapWfs(nodeSpecificLdapConfiguration, jobStatusRecord, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RECONFIGURE);
    }

    /**
     * Start WorkflowInstance for renew LDAP attribute for nodes.
     *
     * @param nodeSpecificLdapConfiguration
     *            contains list of nodes.
     * @param jobStatusRecord
     *            the job status record.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeRenewLdapWfs(final List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfiguration, final JobStatusRecord jobStatusRecord) {
        nscsLogger.debug(String.format("execute RenewLdapWfs. InputNodeList: %s", nodeSpecificLdapConfiguration));
        executeLdapWfs(nodeSpecificLdapConfiguration, jobStatusRecord, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW);
    }

    private void executeLdapWfs(final List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfiguration, final JobStatusRecord jobStatusRecord, final LdapCommandHandlerHelper.LDAPConfigurationMode commandType) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        int workflowId = 1;
        nscsLogger.debug(String.format("executeLdapWfs. InputNodeListSize: %d", nodeSpecificLdapConfiguration.size()));

        for (final NodeSpecificLdapConfiguration node : nodeSpecificLdapConfiguration) {
            WfResult result;
            switch (commandType) {
            case LDAP_RENEW:
                result = nscsCommandManagerProcessor.executeLdapWf(node, jobStatusRecord, workflowId, true);
                break;
            case LDAP_RECONFIGURE:
            case LDAP_CONFIGURE:
            default:
                result = nscsCommandManagerProcessor.executeConfigureLdapWfs(node, jobStatusRecord, workflowId);
                break;
            }

            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                workflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeTestWfs(final int numWorkflows, final JobStatusRecord jobStatusRecord) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<>();
        for (int workflowId = 1; workflowId <= numWorkflows; workflowId++) {
            final NodeReference nodeRef = new NodeRef("TEST_WFS_" + workflowId);
            final WfResult result = nscsCommandManagerProcessor.executeTestSingleWf(nodeRef, jobStatusRecord, workflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    @Override
    public void executeIpSecWorkflows(final List<IpSecRequest> requests, final JobStatusRecord jobStatusRecord) {

        int newWorkflowId = 1;

        final Map<UUID, WfResult> wfResultMap = new HashMap<>();

        for (final IpSecRequest request : requests) {
            nscsLogger.info(String.format("executeIpsecWfs.InputNodeList: %s", request.getNodeFdn()));

            IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration = cppIpSecWfsConfiguration.configureIpSecWorkflow(request);
            final NodeReference nodeReference = new NodeRef(ipSecRequestWfsConfiguration.getNodeFdn());
            final WfResult result = nscsCommandManagerProcessor.executeIpSecWorkflow(nodeReference, ipSecRequestWfsConfiguration, jobStatusRecord,
                    newWorkflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                newWorkflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    @Override
    public void executeSshKeyWorkflows(final List<SSHKeyRequestDto> requests, final JobStatusRecord jobStatusRecord) {

        int newWorkflowId = 1;

        final Map<UUID, WfResult> wfResultMap = new HashMap<>();

        for (final SSHKeyRequestDto request : requests) {
            nscsLogger.info(String.format("executeSshKeyWfs.InputNodeList: %s", request.toString()));

            SSHKeyWfsConfigurationDto sshKeyWorkflow = sshKeyWfsUtility.configureSshKeyWorkflow(request);
            nscsLogger.info(String.format("executeSshKeyWfs.WK parameter: %s", sshKeyWorkflow.toString()));

            final NodeReference nodeReference = new NodeRef(request.getFdn());
            final WfResult result = nscsCommandManagerProcessor.executeSshKeyWorkflow(nodeReference, sshKeyWorkflow, jobStatusRecord,
                    newWorkflowId);
            if (result != null) {
                wfResultMap.put(result.getWfWakeId(), result);
                newWorkflowId++;
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

}
