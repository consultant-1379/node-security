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
package com.ericsson.nms.security.nscs.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.types.CrlCheckCommand;
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.AlgorithmKeySizeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.api.exception.EntityForNodeNotFoundException;
import com.ericsson.nms.security.nscs.api.exception.EntityWithValidCategoryNotFoundException;
import com.ericsson.nms.security.nscs.api.exception.InvalidAlarmServiceStateException;
import com.ericsson.nms.security.nscs.api.exception.InvalidEntityProfileNameDefaultXmlException;
import com.ericsson.nms.security.nscs.api.exception.InvalidEntityProfileNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputNodeListException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidSubjAltNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.MaxNodesExceededException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.SecurityMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameSubjAltNameTypeEmptyXmlException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameTypeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCertificateTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedTrustCategoryTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.FmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;

/**
 * This Utility class is used to validate each node before starting the workflow process.
 *
 * @author emehsau
 */
public class NodeValidatorUtility {

    public static final String ACCEPTED_ARGUMENTS_ARE = "Accepted arguments are ";
    public static final String ACCEPTED_KEY_ALGORITHMS_ARE = "Accepted Key Algorithms are ";
    public static final int MAX_NUMBER_OF_NODES_ALLOWED = 100;

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private NSCSComEcimNodeUtility nodeUtility;

    @Inject
    private NSCSCppNodeUtility nscsCppNodeUtility;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Inject
    private MoAttributeHandler moAttributeHandler;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    /**
     * This is a generic method used to validate the nodes
     *
     * @param normNode
     *            a normalizable node reference based on a given node reference (FDN)
     * @param nodeRef
     *            represents a reference to a node, compound by the node FDN and name
     * @return : {@link Boolean}
     *         <p>
     *         true: if node is valid.
     *         </p>
     *         false: if any of validation is failed.
     */
    public boolean validate(final NormalizableNodeReference normNode, final NodeReference nodeRef) {

        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn()) && reader.exists(nodeRef.getFdn())) {
                logger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                throw new UnassociatedNetworkElementException();
            }
            logger.error("Invalid Node Name  [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }
        if (!isNodeExists(nodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", nodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }
        if (!isNodeSynchronized(normNode)) {
            logger.error("Node [{}] is not synchronized.", nodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }
        if (nodeRef == null || !reader.exists(nodeRef.getFdn())) {
            throw new NodeDoesNotExistException();
        }
        return true;
    }

    /**
     * This method will validate whether given node exists, node is synchronized and certificate is supported.
     *
     * @param nodeRef
     * @return : {@link Boolean}
     *         <p>
     *         true: if node exists, synch and is certifiable.
     *         </p>
     *         false: if any of validation is failed.
     */
    public boolean validateNode(final NodeReference nodeRef) throws UnassociatedNetworkElementException, NodeDoesNotExistException,
            NetworkElementNotfoundException, NodeNotCertifiableException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException {

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
                if (reader.exists(nodeRef.getFdn())) {
                    logger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    throw new UnassociatedNetworkElementException();
                }
            }
            logger.error("Invalid Node Name [{}]", nodeRef.getFdn());
            throw new NodeDoesNotExistException();
        }

        final NodeReference normalizedNodeRef = normNode.getNormalizedRef();

        if (normalizedNodeRef == null) {
            logger.error("NetworkElement MO doesn't exist for [{}].", nodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isNodeExists(normalizedNodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", normalizedNodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isCertificateSupportedForNode(normNode)) {
            logger.error("Node [{}] doesn't support certificates.", normalizedNodeRef.getFdn());
            throw new NodeNotCertifiableException();
        }

        if (!hasNodeSecurityFunctionMO(normalizedNodeRef)) {
            logger.error("Node [{}]  doesn't have valid SecurityFunctionMO.", normalizedNodeRef.getFdn());
            throw new SecurityFunctionMoNotfoundException();
        }

        if (!isNodeSynchronized(normNode)) {
            logger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }
        return true;
    }

    /**
     * This method will validate whether given node exists, node is synchronized and certificate is supported.
     *
     * @param inputNode
     * @param certType
     * @return : {@link Boolean}
     *         <p>
     *         true: if node exists, synch and is certifiable.
     *         </p>
     *         false: if any of validation is failed.
     * @throws NscsPkiEntitiesManagerException
     */
    public boolean validateNodeIssue(final Node inputNode, final String certType)
            throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException, NodeNotCertifiableException,
            UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException, InvalidInputNodeListException,
            InvalidEntityProfileNameXmlException, InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException,
            SubjAltNameSubjAltNameTypeEmptyXmlException, SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException {
        final String fdn = inputNode.getNodeFdn();
        final NodeReference nodeRef = new NodeRef(fdn);

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
                if (reader.exists(nodeRef.getFdn())) {
                    logger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    throw new UnassociatedNetworkElementException();
                }
            }
            logger.error("Invalid Node Name [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }

        final NodeReference normalizedNodeRef = normNode.getNormalizedRef();

        if (normalizedNodeRef == null) {
            logger.error("NetworkElement MO doesn't exist for [{}].", nodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isNodeExists(normalizedNodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", normalizedNodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isCertificateSupportedForNode(normNode)) {
            logger.error("Node [{}] doesn't support certificates.", normalizedNodeRef.getFdn());
            throw new NodeNotCertifiableException();
        }

        if (!isCertificateTypeSupported(normNode, certType)) {
            final String nodeName = normNode.getName();
            final String errorMessage = String.format("Unsupported certType[%s] for node[%s] of type[%s]", certType, nodeName, normNode.getNeType());
            logger.error(errorMessage);
            throw new UnsupportedCertificateTypeException(errorMessage);
        }

        validateXmlForNode(inputNode, certType);

        if (!hasNodeSecurityFunctionMO(normalizedNodeRef)) {
            logger.error("Node [{}]  doesn't have valid SecurityFunctionMO.", normalizedNodeRef.getFdn());
            throw new SecurityFunctionMoNotfoundException();
        }

        if (!isNodeSynchronized(normNode)) {
            logger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }

        return true;
    }

    /**
     * This method will validate whether given node exists, node is synchronized and certificate is supported for the trust commands.
     *
     * @param nodeRef
     * @param trustCategoryType
     *
     */
    public void validateNodeTrust(final NodeReference nodeRef, final String trustCategory, final Boolean isExternalCA)
            throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException, NodeNotCertifiableException,
            UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException {

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
                if (reader.exists(nodeRef.getFdn())) {
                    logger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    throw new UnassociatedNetworkElementException();
                }
            }
            logger.error("Invalid Node Name [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }

        final NodeReference normalizedNodeRef = normNode.getNormalizedRef();

        if (normalizedNodeRef == null) {
            logger.error("NetworkElement MO doesn't exist for [{}].", nodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isNodeExists(normalizedNodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", normalizedNodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isCliCommandSupported(normNode, NscsCapabilityModelService.TRUST_COMMAND)) {
            logger.error("Unsupported node type for command {}", NscsCapabilityModelService.TRUST_COMMAND);
            throw new UnsupportedNodeTypeException();
        }

        if (!isCertificateSupportedForNode(normNode)) {
            logger.error("Node [{}] doesn't support certificates.", normalizedNodeRef.getFdn());
            throw new NodeNotCertifiableException();
        }

        if (!isTrustCategoryTypeSupported(normNode, trustCategory)) {
            final String nodeName = normNode.getName();
            logger.error("Unsupported certType/trustCategory {} for node{} of type{}", trustCategory, nodeName, normNode.getNeType());
            throw new UnsupportedTrustCategoryTypeException();
        }

        if (!hasNodeSecurityFunctionMO(normalizedNodeRef)) {
            logger.error("Node [{}]  doesn't have valid SecurityFunctionMO.", normalizedNodeRef.getFdn());
            throw new SecurityFunctionMoNotfoundException();
        }

        if (!isNodeSynchronized(normNode)) {
            logger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }

        if (isExternalCA && !validateNodeTypeForExtCa(normNode)) {
            logger.error("Node [{}]  doesn't support external CA trust distribution.", normalizedNodeRef.getFdn());
            throw new UnsupportedNodeTypeException().setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE);
        }
    }

    /**
     * This method will validate whether given node exists, node is synchronized and certificate is supported.
     *
     * @param nodeRef
     * @param certType
     * @param command
     * @return : {@link Boolean}
     *         <p>
     *         true: if node exists, synch and supports certificate.
     *         </p>
     *         false: if any of validation is failed.
     */
    public boolean validateNodeGetCertEnrollTrustInstallState(final NodeReference nodeRef, final String certType, final String command)
            throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException, NodeNotSynchronizedException,
            NodeNotCertifiableException, UnsupportedCertificateTypeException, UnsupportedNodeTypeException {

        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        if (normNode == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn()) && reader.exists(nodeRef.getFdn())) {
                logger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                throw new UnassociatedNetworkElementException();
            }
            logger.error("Invalid Node Name [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }

        final NodeReference normalizedNodeRef = normNode.getNormalizedRef();

        if (normalizedNodeRef == null) {
            logger.error("NetworkElement MO doesn't exist for [{}].", nodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isNodeExists(normalizedNodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", normalizedNodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isCliCommandSupported(normNode, command)) {
            logger.error("Unsupported node type for command {}", command);
            throw new UnsupportedNodeTypeException();
        }

        if (!isCertificateSupportedForNode(normNode)) {
            logger.error("Node [{}] doesn't support certificates.", normalizedNodeRef.getFdn());
            throw new NodeNotCertifiableException();
        }

        if (!isCertificateTypeSupported(normNode, certType)) {
            final String nodeName = normNode.getName();
            final String errorMessage = String.format("Unsupported certType[%s] for node[%s] of type[%s]", certType, nodeName, normNode.getNeType());
            logger.error(errorMessage);
            throw new UnsupportedCertificateTypeException(errorMessage);
        }

        if (!isNodeSynchronized(normNode)) {
            logger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }

        return true;
    }

    public boolean validateNodeCertificateReissue(final NodeReference nodeRef, final String certType)
            throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException, NodeNotCertifiableException,
            UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException {
        final NormalizableNodeReference normalizableNodeReference = reader.getNormalizableNodeReference(nodeRef);

        validateBlockingErrorsNodesForCertificateReissue(nodeRef, normalizableNodeReference, certType);
        validateNonBlockingErrorsNodesForCertificateReissue(normalizableNodeReference);

        return true;
    }

    public boolean validateNodesWithEntitiesForCertificateReissue(final NodeReference nodeRef, final List<Entity> entities, final String certType,
            final List<Entity> validEntities) throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException,
            NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException,
            EntityForNodeNotFoundException, EntityWithValidCategoryNotFoundException
    //                  ,EntityWithActiveCertificateNotFoundException
    {

        final NormalizableNodeReference normalizableNodeRef = reader.getNormalizableNodeReference(nodeRef);

        validateBlockingErrorsNodesForCertificateReissue(nodeRef, normalizableNodeRef, certType);
        validateEntitiesFromNodeForCertificateReissue(nodeRef, entities, certType, validEntities);
        validateNonBlockingErrorsNodesForCertificateReissue(normalizableNodeRef);

        return true;
    }

    public boolean validateNodesWithEntitiesForCertificateReissue(final NodeReference nodeRef, final String certType)
            throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException, NodeNotCertifiableException,
            UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException, EntityForNodeNotFoundException {
        final long startTime = System.currentTimeMillis();

        final NormalizableNodeReference normalizableNodeRef = reader.getNormalizableNodeReference(nodeRef);

        validateBlockingErrorsNodesForCertificateReissue(nodeRef, normalizableNodeRef, certType);

        long currentTime = System.currentTimeMillis();
        logger.debug("Partial elapsed time [VALIDATOR:validateBlockingErrorsNodesForCertificateReissue]: {}",
                String.format("%.3f", (currentTime - startTime) / 1000f));
        long previousTime = currentTime;

        // Validation of Node Entity from PKI is done in ComEcimCheckIsExternalCATaskHandler as required for changes of External CA support for certificate reissue operation

        currentTime = System.currentTimeMillis();
        logger.debug("Partial elapsed time [VALIDATOR:validateEntitiesFromNodeForCertificateReissue]: {}",
                String.format("%.3f", (currentTime - previousTime) / 1000f));
        previousTime = currentTime;

        validateNonBlockingErrorsNodesForCertificateReissue(normalizableNodeRef);

        currentTime = System.currentTimeMillis();
        logger.debug("Partial elapsed time [VALIDATOR:validateNonBlockingErrorsNodesForCertificateReissue]: {}",
                String.format("%.3f", (currentTime - previousTime) / 1000f));
        logger.debug("Total elapsed time [VALIDATOR:validateNodesWithEntitiesForCertificateReissue]: {}",
                String.format("%.3f", (currentTime - startTime) / 1000f));

        return true;
    }

    /**
     * @param entity
     * @param entityName
     * @return node name from entity
     */
    public static String getNodeNameFromEntity(final Entity entity) {
        String entityNodeName = "";
        final String entityName = entity.getEntityInfo().getName();
        final NodeEntityCategory nodeEntityCategory = NscsPkiEntitiesManagerJar.findNodeEntityCategory(entity.getCategory());

        if (nodeEntityCategory == null) {
            return entityNodeName;
        }
        final String suffix = "-" + nodeEntityCategory.toString();
        if (entityName.endsWith(suffix)) {
            entityNodeName = entityName.substring(0, entityName.length() - suffix.length());
        }

        return entityNodeName;
    }

    private boolean validateBlockingErrorsNodesForCertificateReissue(final NodeReference nodeRef,
            final NormalizableNodeReference normalizableNodeReference, final String certType)
            throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException, NodeNotCertifiableException,
            UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException {
        if (normalizableNodeReference == null) {
            if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
                if (reader.exists(nodeRef.getFdn())) {
                    logger.error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
                    throw new UnassociatedNetworkElementException();
                }
            }
            logger.error("Invalid Node Name [{}]", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        }

        final NodeReference normalizedNodeRef = normalizableNodeReference.getNormalizedRef();

        if (normalizedNodeRef == null) {
            logger.error("NetworkElement MO doesn't exist for [{}].", normalizableNodeReference.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isNodeExists(normalizedNodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", normalizedNodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!isCliCommandSupported(normalizableNodeReference, NscsCapabilityModelService.CERTIFICATE_COMMAND)) {
            logger.error("Node [{}] doesn't support certificates.", normalizedNodeRef.getFdn());
            throw new NodeNotCertifiableException();
        }

        if (!isCertificateSupportedForNode(normalizableNodeReference)) {
            logger.error("Node [{}] doesn't support certificates.", normalizedNodeRef.getFdn());
            throw new NodeNotCertifiableException();
        }

        if (!isCertificateTypeSupported(normalizableNodeReference, certType)) {
            final String nodeName = normalizableNodeReference.getName();
            final String errorMessage = String.format("Unsupported certType[%s] for node[%s] of type[%s]", certType, nodeName,
                    normalizableNodeReference.getNeType());
            logger.error(errorMessage);
            throw new UnsupportedCertificateTypeException(errorMessage);
        }

        if (!hasNodeSecurityFunctionMO(normalizedNodeRef)) {
            logger.error("Node [{}]  doesn't have valid SecurityFunctionMO.", normalizedNodeRef.getFdn());
            throw new SecurityFunctionMoNotfoundException();
        }
        return true;
    }

    private boolean validateNonBlockingErrorsNodesForCertificateReissue(final NormalizableNodeReference normalizableNodeReference)
            throws NodeNotSynchronizedException {

        if (!isNodeSynchronized(normalizableNodeReference)) {
            logger.error("Node [{}] is not synchronized.", normalizableNodeReference.getFdn());
            throw new NodeNotSynchronizedException();
        }
        return true;
    }

    private boolean validateEntitiesFromNodeForCertificateReissue(final NodeReference nodeRef, final List<Entity> entities, final String certType,
            final List<Entity> validEntities) {

        logger.debug("validateEntitiesFromNodeForCertificateReissue. fdn: [{}], certType: [{}]", nodeRef.getFdn(), certType);

        final List<Entity> foundEntities = getEntitiesForNode(nodeRef, entities, certType);

        if (foundEntities.isEmpty()) {
            logger.error("Can't find the entity for node [{}].", nodeRef.getFdn());
            throw new EntityForNodeNotFoundException();
        } else {
            logger.debug("Found {} entities for node {}.", foundEntities.size(), nodeRef.getFdn());
            for (final Entity entity : foundEntities) {
                if (!isEntityCategoryValid(entity, certType)) {
                    logger.error("Entity [{}] associated to node [{}] has invalid category.", entity.getEntityInfo().getName(), nodeRef.getFdn());
                    throw new EntityWithValidCategoryNotFoundException();
                }
            }
            //                  for (Entity entity : foundEntities){
            //                          if (!hasEntityActiveCertificate(entity)){
            //                                  throw new EntityWithActiveCertificateNotFoundException();
            //                          }
            //                  }

            validEntities.addAll(foundEntities);
        }

        return true;
    }

    //  public boolean validateEntityForCertificateReissue (final Entity entity, final String certType)
    //                  throws EntityWithValidCategoryNotFoundException,
    //                  EntityWithActiveCertificateNotFoundException {
    //
    //          if (!isEntityCategoryValid(entity, certType)){
    //                  logger.error("Input entity [{}] has an invalid category ", entity.getEntityInfo().getName());
    //                                                  throw new EntityWithValidCategoryNotFoundException();
    //          }
    //
    //          if (!hasEntityActiveCertificate(entity)){
    //                  logger.error("Entity [{}] hasn't an active certificate available", entity.getEntityInfo().getName());
    //                                                          throw new EntityWithActiveCertificateNotFoundException();
    //          }
    //
    //          return true;
    //  }

    private boolean isEntityCategoryValid(final Entity entity, final String certType) {
        if (certType.isEmpty()) {
            return (isEntityCategoryValidOAM(entity) || isEntityCategoryValidIPSEC(entity));
        } else if (certType.equals(CertificateType.OAM.toString())) {
            return (isEntityCategoryValidOAM(entity));
        } else if (certType.equals(CertificateType.IPSEC.toString())) {
            return (isEntityCategoryValidIPSEC(entity));
        } else {
            return false;
        }
    }

    private List<Entity> getEntitiesForNode(final NodeReference nodeRef, final List<Entity> entities, final String certType) {
        final List<Entity> nodeEntity = new ArrayList<>();

        if (certType.isEmpty()) {
            final Entity oamEntity = getEntityForNode(nodeRef, entities, "-oam");
            final Entity ipsecEntity = getEntityForNode(nodeRef, entities, "-ipsec");
            if (oamEntity != null) {
                nodeEntity.add(oamEntity);
            }
            if (ipsecEntity != null) {
                nodeEntity.add(ipsecEntity);
            }
        } else if (certType.equals(CertificateType.OAM.toString())) {
            final Entity oamEntity = getEntityForNode(nodeRef, entities, "-oam");
            if (oamEntity != null) {
                nodeEntity.add(oamEntity);
            }
        } else if (certType.equals(CertificateType.IPSEC.toString())) {
            final Entity ipsecEntity = getEntityForNode(nodeRef, entities, "-ipsec");
            if (ipsecEntity != null) {
                nodeEntity.add(ipsecEntity);
            }
        }
        return nodeEntity;
    }

    private Entity getEntityForNode(final NodeReference nodeRef, final List<Entity> entities, final String suffix) {
        Entity nodeEntity = null;
        final String entityNameFromNode = nodeRef.getName() + suffix;
        if (entities != null) {
            logger.debug("looking for entity name [{}] in entities", entityNameFromNode);
            for (final Entity entity : entities) {
                if (entity.getEntityInfo().getName().equals(entityNameFromNode)) {
                    nodeEntity = entity;
                    break;
                }
            }
        } else {
            try {
                logger.debug("checking entity name [{}] is available", entityNameFromNode);
                if (!nscsPkiManager.isEntityNameAvailable(entityNameFromNode, EntityType.ENTITY)) {
                    nodeEntity = nscsPkiManager.getPkiEntity(entityNameFromNode);
                }
            } catch (final NscsPkiEntitiesManagerException ex) {
                logger.error("Unable to find entity {} ", entityNameFromNode);
            }
        }
        if (nodeEntity != null) {
            logger.debug("Entity {} found.", entityNameFromNode);
        }
        return nodeEntity;
    }

    private boolean isEntityCategoryValidOAM(final Entity entity) {
        final String category = entity.getCategory().getName();
        logger.debug("category {}", category);
        final NodeEntityCategory nodeEntityCategory = NscsPkiEntitiesManagerJar.findNodeEntityCategory(entity.getCategory());
        if (nodeEntityCategory != null) {
            if (NodeEntityCategory.OAM.equals(nodeEntityCategory)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEntityCategoryValidIPSEC(final Entity entity) {
        final String category = entity.getCategory().getName();
        logger.debug("category {}", category);
        final NodeEntityCategory nodeEntityCategory = NscsPkiEntitiesManagerJar.findNodeEntityCategory(entity.getCategory());
        if (nodeEntityCategory != null) {
            if (NodeEntityCategory.IPSEC.equals(nodeEntityCategory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check whether node exists or not.
     *
     * @param nodeRef
     *            : {@link NodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if node exists
     *         </p>
     *         false: if node doesn't exist
     */
    public boolean isNodeExists(final NodeReference nodeRef) {
        boolean isNodeExists = false;
        if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn())) {
            if (reader.exists(nodeRef.getFdn())) {
                isNodeExists = true;
            }
        }
        return isNodeExists;
    }

    /**
     * This method will validate if certificate management is supported for given node.
     *
     * @param normNodeRef
     *            the node reference.
     * @return : {@link Boolean}
     *         <p>
     *         true: if node supports certificate management.
     *         </p>
     *         false: if node doesn't support certificate management.
     */
    public boolean isCertificateSupportedForNode(final NormalizableNodeReference normNodeRef) {
        return nscsCapabilityModelService.isCertificateManagementSupported(normNodeRef);
    }

    /**
     * This method will validate if trust category is supported for given node.
     *
     * @param normNodeRef
     *            the node reference.
     * @return : {@link Boolean}
     *         <p>
     *         true: if node supports given trust category.
     *         </p>
     *         false: if node doesn't support given trust category.
     */
    public boolean isTrustCategoryTypeSupported(final NormalizableNodeReference normNodeRef, final String trustCategory) {
        return nscsCapabilityModelService.isTrustCategoryTypeSupported(normNodeRef, trustCategory);
    }

    /**
     * This method will validate if given certificate type is supported for given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @param certType
     *            the certificate type.
     * @return : {@link Boolean}
     *         <p>
     *         true: if certificate type is supported for the node reference.
     *         </p>
     *         false: if certificate type is not supported for the node reference.
     */
    public boolean isCertificateTypeSupported(final NormalizableNodeReference normNodeRef, final String certType) {
        return nscsCapabilityModelService.isCertTypeSupported(normNodeRef, certType);
    }

    /**
     * This method will validate if given command is supported for given node.
     *
     * @param normNodeRef
     *            the node reference.
     * @param command
     *            the command.
     * @return : {@link Boolean}
     *         <p>
     *         true: if command is supported for the node.
     *         </p>
     *         false: if command is not supported for the node.
     */
    public boolean isCliCommandSupported(final NormalizableNodeReference normNodeRef, final String command) {
        return nscsCapabilityModelService.isCliCommandSupported(normNodeRef, command);
    }

    /**
     * This method will validate if the number of node is allowed.
     *
     * @param size
     * @return : {@link Boolean}
     *         <p>
     *         true: if number of nodes is allowed.
     *         </p>
     *         false: if number of nodes isn't allowed.
     */
    public boolean isNumberOfNodesAllowed(final int size) {
        final int maxNumberOfNodesAllowed = MAX_NUMBER_OF_NODES_ALLOWED;
        if (size > maxNumberOfNodesAllowed) {
            logger.warn("Number of nodes specified, for issue certificate operation, exceeds the maximum : {}", maxNumberOfNodesAllowed);
            throw new MaxNodesExceededException(maxNumberOfNodesAllowed);
        }
        return true;
    }

    /**
     * Method to check whether node has SecurityFunctionMO or not.
     *
     * @param nodeRef
     *            : {@link NodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if SecurityFunctionMO exists
     *         </p>
     *         false: if SecurityFunctionMO doesn't exist
     */
    public boolean hasNodeSecurityFunctionMO(final NodeReference nodeRef) {
        logger.debug("Checking SecurityFunctionMO existence for node [{}] ", nodeRef.getFdn());
        return reader.exists(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn());
    }

    /**
     * Method to check whether node is synchronized or not.
     *
     * @param normNodeRef
     *            : {@link NormalizableNodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if node is synchronized
     *         </p>
     *         false: if node is not synchronized
     */
    public boolean isNodeSynchronized(final NormalizableNodeReference normNodeRef) {
        boolean isSynch = false;
        final CmResponse response = reader.getMOAttribute(normNodeRef.getNormalizedRef(), Model.NETWORK_ELEMENT.cmFunction.type(),
                Model.NETWORK_ELEMENT.cmFunction.namespace(), CmFunction.SYNC_STATUS);
        logger.debug("Response is : {}, and size of response : {}", response, response.getCmObjects().size());
        if (response.getCmObjects().isEmpty() || response.getCmObjects().size() > 1) {
            logger.error("CmFunction MO is not configured for : {}", normNodeRef.getFdn());
        } else {
            final String status = (String) response.getCmObjects().iterator().next().getAttributes().get(CmFunction.SYNC_STATUS);
            logger.debug("Node [{}] SYNC status is: [{}]", normNodeRef.getFdn(), status);
            if (ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name().equals(status)) {
                isSynch = true;
            }

        }
        return isSynch;
    }

    /**
     * Method to check whether alarm is enabled or not for given node.
     *
     * @param normNodeRef
     *            : {@link NormalizableNodeReference}
     * @return {@link Boolean}
     *         <p>
     *         true: if alarm is enabled on the node.
     *         </p>
     *         false: if alarm is disabled on the node.
     */
    public boolean isFmSupervisionEnabled(final NormalizableNodeReference normNodeRef) {
        boolean isAlarmEnabled = false;
        final CmResponse response = reader.getMOAttribute(normNodeRef.getNormalizedRef(), Model.NETWORK_ELEMENT.fmFunction.type(),
                Model.NETWORK_ELEMENT.fmFunction.namespace(), FmFunction.CURRENT_SERVICE_STATE);
        logger.info("Response is : {}, and size of response : {}", response, response.getCmObjects().size());
        if (response.getCmObjects().isEmpty() || response.getCmObjects().size() > 1) {
            logger.error("FmFunction MO is not configured for : {}", normNodeRef.getFdn());
        } else {
            final String status = (String) response.getCmObjects().iterator().next().getAttributes().get(FmFunction.CURRENT_SERVICE_STATE);
            logger.info("Node [{}] alarm status is: [{}]", normNodeRef.getFdn(), status);
            if (ModelDefinition.FmFunction.AlarmStatusValue.IN_SERVICE.name().equals(status)) {
                isAlarmEnabled = true;
            }
            logger.info("Alarm is enabled or not on the node : [{}]", isAlarmEnabled);
        }
        return isAlarmEnabled;
    }

    /**
     * Method to check the alarm's current service state value and verify the
     * supervision status for the given node.
     *
     * @param normNodeRef
     *            Normalizable Node Reference object of a given node.
     * @throws InvalidAlarmServiceStateException
     *            when the alarm current service state for the given node is invalid
     */
    public void checkFmAlarmCurrentServiceState(final NormalizableNodeReference normNodeRef) {
        String currentServiceState ;
        final CmResponse response = reader.getMOAttribute(normNodeRef.getNormalizedRef(), Model.NETWORK_ELEMENT.fmFunction.type(),
                Model.NETWORK_ELEMENT.fmFunction.namespace(), FmFunction.CURRENT_SERVICE_STATE);
        logger.info("Response for fetching the Alarm's current service state is : {}, and size of response : {}", response,
                response.getCmObjects().size());
        if (response.getCmObjects().isEmpty() || response.getCmObjects().size() > 1) {
            logger.error("FmFunction MO is not configured for : {}", normNodeRef.getFdn());
            throw new InvalidAlarmServiceStateException(NscsErrorCodes.ALARM_SUPERVISION_NOT_ENABLED_ON_NODE)
                    .setSuggestedSolution("please check the error log for more details.");
        } else {
            currentServiceState = (String) response.getCmObjects().iterator().next().getAttributes().get(FmFunction.CURRENT_SERVICE_STATE);
            logger.info("Node [{}] alarm status is: [{}]", normNodeRef.getFdn(), currentServiceState);

            if (FmFunction.AlarmStatusValue.IDLE.name().equals(currentServiceState)) {
                logger.error("Alarm Supervision is disabled for the node {}", normNodeRef.getName());
                throw new InvalidAlarmServiceStateException(NscsErrorCodes.ALARM_SUPERVISION_NOT_ENABLED_ON_NODE)
                        .setSuggestedSolution("Check online help for enabling alarm supervision on the nodes.");
            } else if (!FmFunction.AlarmStatusValue.IN_SERVICE.name().equals(currentServiceState)) {
                final String errorMessage = "Invalid Alarm Current Service State was set for the node [" + normNodeRef.getName()
                        + "], its value is " + currentServiceState;
                logger.error(errorMessage);
                throw new InvalidAlarmServiceStateException(errorMessage)
                        .setSuggestedSolution("Ensure the Alarm current service state as IN_SERVICE on the Node.");
            }
        }
    }


    public Set<String> validateDuplicatedNodes(final List<NodeReference> inputNodes) {
        final Map<NodeReference, Boolean> nonUniqueNodes = new HashMap<>(inputNodes.size());

        final Set<String> duplicatedNodes = new HashSet<>();
        for (final NodeReference node : inputNodes) {
            if (nonUniqueNodes.containsKey(node)) {
                nonUniqueNodes.put(node, Boolean.TRUE);
            } else {
                nonUniqueNodes.put(node, Boolean.FALSE);
            }
        }

        for (final Map.Entry<NodeReference, Boolean> entry : nonUniqueNodes.entrySet()) {
            if (entry.getValue()) {
                duplicatedNodes.add(entry.getKey().getFdn());
            }
        }
        return duplicatedNodes;
    }

    public void validateXmlForNode(final Node inputNode, final String certType) throws InvalidEntityProfileNameXmlException,
            InvalidInputNodeListException, InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException,
            SubjAltNameSubjAltNameTypeEmptyXmlException, SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException {

        logger.debug("XML VALIDATION : node {}", inputNode.getNodeFdn());

        final String entityProfileName = inputNode.getEntityProfileName().trim();
        final String keySize = inputNode.getKeySize().trim();

        final NodeReference nodeRef = new NodeRef(inputNode.getNodeFdn());
        final String entityName = buildEntityNameFromNodeRef(nodeRef, certType);
        logger.debug("Entity name for node {} and certType {} is {}", nodeRef.getName(), certType, entityName);

        Boolean isEntityExist = false;

        if (!entityProfileName.isEmpty()) {
            if (isEntityProfileNameAvailable(entityProfileName)) {
                logger.error("ErrorMsg : {}", NscsErrorCodes.REQUESTED_ENTITY_PROFILE_NAME_DOES_NOT_EXIST);
                throw new InvalidEntityProfileNameXmlException();
            }
        }

        if ((!keySize.isEmpty())) {

            EntityProfile entityProfile;
            if (!entityProfileName.isEmpty()) {
                try {
                    entityProfile = nscsPkiManager.getEntityProfile(entityProfileName);
                } catch (final NscsPkiEntitiesManagerException e) {
                    final String errorMessage = NscsLogger.stringifyException(e) + " while getEntityProfile";
                    logger.error(errorMessage);
                    throw new InvalidEntityProfileNameXmlException();
                }
                logger.debug("Retrieved entityProfile {} from input value", entityProfile);
            } else {
                try {
                    if (isEntityExist || !nscsPkiManager.isEntityNameAvailable(entityName, EntityType.ENTITY)) {
                        isEntityExist = true;
                        final Entity entity = nscsPkiManager.getPkiEntity(entityName);
                        entityProfile = entity.getEntityProfile();
                        logger.debug("Retrieved entityProfile {} from node entity {}", entityProfile, entityName);
                    } else {
                        // Entity does not exist for node: get default profile
                        final NodeModelInformation nodeModelInfo = reader.getNodeModelInformation(inputNode.getNodeFdn());
                        final NodeEntityCategory nodeEntityCategory = getNodeEntityCategory(certType);
                        if (nodeEntityCategory == null) {
                            logger.error("ErrorMsg : {}", "");
                            throw new InvalidInputNodeListException();
                        }
                        final String defaultEntityProfile = nscsCapabilityModelService.getDefaultEntityProfile(nodeModelInfo, nodeEntityCategory);
                        if (isEntityProfileNameAvailable(defaultEntityProfile)) {
                            logger.error(NscsErrorCodes.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST);
                            throw new InvalidEntityProfileNameDefaultXmlException();
                        }
                        entityProfile = nscsPkiManager.getEntityProfile(defaultEntityProfile);
                        logger.debug("Retrieved entityProfile {} from default entityProfile {}", entityProfile, defaultEntityProfile);
                    }
                } catch (final NscsPkiEntitiesManagerException e) {
                    final String errorMessage = NscsLogger.stringifyException(e) + " while isEntityNameAvailable or getPkiEntity";
                    logger.error(errorMessage);
                    throw new InvalidInputNodeListException();
                }
            }
            final List<String> supportedAlgorithmKeySizeValues = new ArrayList<>();
            if (!isAlgorithmKeySizeInProfileRange(keySize, entityProfile, supportedAlgorithmKeySizeValues)) {
                logger.error(NscsErrorCodes.KEY_ALGORITHM_NOT_SUPPORTED_BY_ENTITY_PROFILE, keySize, entityProfile.getName(), supportedAlgorithmKeySizeValues);
                throw new AlgorithmKeySizeNotSupportedXmlException("The given Key Algorithm [" + keySize + "] is not in supported list of Entity Profile [" + entityProfile.getName() + "]. "
                        + ACCEPTED_KEY_ALGORITHMS_ARE + supportedAlgorithmKeySizeValues);
            }

        }

        final String subjAltName = inputNode.getSubjectAltName().trim();
        final String subjAltNameType = inputNode.getSubjectAltNameType().trim();

        if (certType.equals(CertificateType.IPSEC.name())) {
            try {
                if (isEntityExist || nscsPkiManager.isEntityNameAvailable(entityName, EntityType.ENTITY)) {
                    isEntityExist = true;
                    if (subjAltName.isEmpty() || subjAltNameType.isEmpty()) {
                        logger.error("ErrorMsg : {}", NscsErrorCodes.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY);
                        throw new SubjAltNameSubjAltNameTypeEmptyXmlException();
                    }
                }
            } catch (final NscsPkiEntitiesManagerException e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " while isEntityNameAvailable";
                logger.error(errorMessage);
                throw new InvalidInputNodeListException();
            }
        }

        if (!subjAltNameType.isEmpty()) {
            final List<String> supportedSubjectAltNameFieldType = new ArrayList<>();
            if (!isSubjectAltNameFieldTypeInSupportedRange(subjAltNameType, supportedSubjectAltNameFieldType)) {
                logger.error("ErrorMsg : {}", NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED);
                throw new SubjAltNameTypeNotSupportedXmlException(ACCEPTED_ARGUMENTS_ARE + supportedSubjectAltNameFieldType);
            }
        }

        if (SubjectAltNameFieldType.IP_ADDRESS.name().equals(subjAltNameType)) {
            if (!NscsCommonValidator.getInstance().isValidIPAddress(subjAltName)) {
                logger.error("ErrorMsg : {}",
                        NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID + NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT);
                throw new InvalidSubjAltNameXmlException();
            } else {
                logger.debug("subject alt name {} validated as IP address", subjAltName);
            }
        }
    }

    /**
     * This method will return Node entity category based on the certificate type
     *
     * @param certType
     *            Certificate type - IPSEC/OAM
     * @return NodeEntityCategory
     */
    public NodeEntityCategory getNodeEntityCategory(final String certType) {
        final CertificateType certificateType = CertificateType.toCertificateType(certType);
        if (certificateType != null) {
            return NodeEntityCategory.fromCertType(certificateType);
        }
        logger.warn("Wrong conversion from certType to NodeEntityCategory!!");
        return null;
    }

    /**
     * This method will be used to build entiy name for a given node based on the certificate type IPSEC/OAM.
     *
     * @param nodeRef
     *            Node reference
     * @param certType
     *            certificate type IPSEC/OAM.
     * @return node entity name
     */
    public String buildEntityNameFromNodeRef(final NodeReference nodeRef, final String certType) {
        final String entityName = nodeRef.getName() + "-" + certType.toLowerCase();
        return entityName;
    }

    /**
     * This method will check whether the given entity profile is exists or not from PKI service
     *
     * @param value
     *            entity profile name
     * @return true if entity profile exists in PKI service otherwise false
     */
    public boolean isEntityProfileNameAvailable(final String value) {
        boolean isAvailable = false;
        try {
            isAvailable = nscsPkiManager.isEntityProfileNameAvailable(value);
        } catch (final NscsPkiEntitiesManagerException ex) {
            logger.warn("ProfileServiceException with message {} retrieved for profile value {}", ex.getMessage(), value);
        }

        if (isAvailable) {
            logger.warn("Profile value {} is not created", value);
        }
        return isAvailable;
    }

    /**
     * This method checks if the given algorithm keySize value is in the range supported by the certificate profile
     *
     * @param value
     *            supported algorithm keySize value
     * @param entityProfile
     *            entity Profile with which the certificate profile can be obtained
     * @param supportedAlgorithmKeySizeValues
     *            list of supported algorithm keySize values
     * @return true if the given algorithm keySize is in the range supported by the certificate profile else false
     */
    public boolean isAlgorithmKeySizeInProfileRange(final String value, final EntityProfile entityProfile,
            final List<String> supportedAlgorithmKeySizeValues) {
        if (entityProfile == null) {
            logger.error("entityProfile is null!!!");
            return false;
        }
        if (entityProfile.getCertificateProfile() == null) {
            logger.error("entityProfile.getCertificateProfile() is null!!!");
            return false;
        }

        final List<Algorithm> algorithms = entityProfile.getCertificateProfile().getKeyGenerationAlgorithms();

        if (algorithms != null) {
            for (final Algorithm algorithm : algorithms) {
                final AlgorithmKeys algorithmKey = AlgorithmKeys.toAlgorithmKeys(algorithm.getName(), algorithm.getKeySize());
                logger.debug("Supported Algorithm with name [{}] and keySize [{}] is [{}]", algorithm.getName(), algorithm.getKeySize(),
                        algorithmKey);
                if (algorithmKey != null) {
                    supportedAlgorithmKeySizeValues.add(algorithmKey.toString());
                }
            }

            for (final String supportedAlgorithmKeySizeValue : supportedAlgorithmKeySizeValues) {
                if (value.equals(supportedAlgorithmKeySizeValue)) {
                    return true;
                }
            }
            logger.warn("Keysize value {} is not in range {}", value, supportedAlgorithmKeySizeValues);
        } else {
            logger.error("entityProfile.getCertificateProfile().getKeyGenerationAlgorithms() is null!!!");
        }
        return false;
    }

    /**
     * This method verifies if the given subjectAltNameFieldType value is either IP_ADDRESS or DNS_NAME
     *
     * @param value
     *            subjectAltNameFieldType value
     * @param subjectAltNameTypeList
     *            list of supported subjectAltNameFields
     * @return true if the given subjectAltNameFieldType is either IP_ADDRESS or DNS_NAME else false
     */
    public boolean isSubjectAltNameFieldTypeInSupportedRange(final String value, final List<String> subjectAltNameTypeList) {
        //      List<SubjectAltNameFieldType> validValues = Arrays.asList(SubjectAltNameFieldType.values());
        subjectAltNameTypeList.add(SubjectAltNameFieldType.IP_ADDRESS.name());
        subjectAltNameTypeList.add(SubjectAltNameFieldType.DNS_NAME.name());
        subjectAltNameTypeList.add(SubjectAltNameFieldType.RFC822_NAME.name());

        for (final String subjAltNameType : subjectAltNameTypeList) {
            if (subjAltNameType.equals(value)) {
                return true;
            }
        }
        logger.warn("SubjectAltNameType value {} is not in supported range {}", value, subjectAltNameTypeList);
        return false;
    }

    /**
     * This method will validate whether given node exists, node is synchronized and certificate is supported.
     *
     * @param nodeRef
     *            represents a reference to a node, compound by the node FDN and name.
     * @param certType
     *            the certificate type value.
     * @return : {@link Boolean}
     *         <p>
     *         true: if all validations are passed.
     *         </p>
     *         false: if any of validation is failed.
     */

    public boolean validateNodeForCrlCheck(final String command, final NodeReference nodeRef, final String certType, final Boolean isReadCmd)
            throws UnassociatedNetworkElementException, NodeDoesNotExistException, NetworkElementNotfoundException, NodeNotCertifiableException,
            UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException,
            UnSupportedNodeReleaseVersionException, TrustCategoryMODoesNotExistException, SecurityMODoesNotExistException,
            NscsCapabilityModelException, UnsupportedNodeTypeException {

        validateNode(nodeRef);
        final NormalizableNodeReference normNodeRef = reader.getNormalizableNodeReference(nodeRef);
        final String neType = normNodeRef.getNeType();
        if (!isNeTypeSupported(normNodeRef, command)) {
            final String errorMsg = String.format("Unsupported neType[%s]", neType);
            logger.error("NE Type validation failed: {}", errorMsg);
            throw new UnsupportedNodeTypeException();
        }

        if (isReadCmd && !isCertificateTypeSupported(normNodeRef, certType)) {
            final String nodeName = normNodeRef.getName();
            final String errorMessage = String.format("Unsupported certType[%s] for node[%s] of type[%s]", certType, nodeName, neType);
            logger.error(errorMessage);
            throw new UnsupportedCertificateTypeException(errorMessage);
        }

        if (!isReadCmd && !isCertificateTypeSupportedforCrlCheck(normNodeRef, certType)) {
            final String nodeName = normNodeRef.getName();
            final String errorMessage = String.format("Unsupported certType[%s] for node[%s] of type[%s]", certType, nodeName, neType);
            logger.error(errorMessage);
            throw new UnsupportedCertificateTypeException(errorMessage);
        }

        validateNodeForCrlCheckMO(normNodeRef, certType);

        return true;
    }

    /**
     * Validates Security MO existence on given Normalizable Node reference
     *
     * @param normNodeRef
     *            the Normalizable Node Reference
     * @return : {@link Boolean}
     *         <p>
     *         true: if the Security Mo Exists in the norm node reference
     *         </p>
     *         false: if the Security Mo not exists in the norm node reference
     */
    public boolean isSecurityMOExists(final NormalizableNodeReference normNodeRef) {

        logger.debug("isSecurityMOExists: start for normNodeRef[{}]", normNodeRef);

        if (normNodeRef == null) {
            final String errorMsg = String.format("Null normNodeRef!");
            logger.error("isSecurityMOExists: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }

        boolean isSupported = false;
        final Mo rootMo = nscsCapabilityModelService.getMirrorRootMo(normNodeRef);
        final String securityFdn = nscsCppNodeUtility.getSecurityFdn(normNodeRef.getFdn(), rootMo);
        logger.debug("Security Fdn is {}", securityFdn);

        if (securityFdn != null && !securityFdn.isEmpty()) {
            isSupported = true;
            logger.debug("isSecurityMOExists MO exists for the node :{}", normNodeRef.getFdn());
        }

        logger.debug("isSecurityMOExists: return supported[{}]", isSupported);

        return isSupported;
    }

    /**
     * This method will validate if the given certificate type for crl check enable or disable is supported for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @param certType
     *            the certificate type
     * @return : {@link Boolean}
     *         <p>
     *         true: if the certificate type is valid
     *         </p>
     *         false: if the certificate type is not valid
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isCertificateTypeSupportedforCrlCheck(final NormalizableNodeReference normNodeRef, final String certType) {
        return nscsCapabilityModelService.isCertTypeSupportedforCrlCheck(normNodeRef, certType);
    }

    /**
     * This method will validate if the given command is supported for the given normalizable node reference.
     *
     * @param normNodeRef
     *            the normalizable node reference.
     * @return : {@link Boolean}
     *         <p>
     *         true: if command is supported for the node.
     *         </p>
     *         false: if command is not supported for the node.
     * @throws NscsCapabilityModelException
     *             This exception is thrown if the given normalizable node reference is null
     */
    public boolean isNeTypeSupported(final NormalizableNodeReference normNodeRef, final String command) throws NscsCapabilityModelException {
        return nscsCapabilityModelService.isCliCommandSupported(normNodeRef, command);
    }

    /**
     * This method will check whether Trust Category MO exists for the given normalizable node reference.
     *
     * @param normNodeRef
     *            The normalizable node reference.
     * @param certType
     *            the certificate type value.
     * @return {@link boolean}.
     *         <p>
     *         true: if TrustCategory MO exists for the node.
     *         </p>
     *         false: if TrustCategory MO doesn't exists for the given node.
     * @throws NscsCapabilityModelException
     *             This exception is thrown if the NormalizableNodeReference is null
     */
    public boolean isTrustCategoryMOExists(final NormalizableNodeReference normNodeRef, final String certType) throws NscsCapabilityModelException {

        logger.debug("isTrustCategoryMOExists: start for normNodeRef[{}]", normNodeRef);

        if (normNodeRef == null) {
            final String errorMsg = String.format("Null normNodeRef!");
            logger.error("isTrustCategoryMOExists: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }

        boolean isSupported = false;
        final Mo rootMo = nscsCapabilityModelService.getMirrorRootMo(normNodeRef);
        if (CrlCheckCommand.ALL.equals(certType)) {

            final String trustCategoryIpsecFdn = nodeUtility.getTrustCategoryFdn(normNodeRef.getFdn(), rootMo, "IPSEC", normNodeRef);
            final String trustCategoryOamFdn = nodeUtility.getTrustCategoryFdn(normNodeRef.getFdn(), rootMo, "OAM", normNodeRef);
            logger.debug("Trust Category Fdn are {} {}", trustCategoryIpsecFdn, trustCategoryOamFdn);

            if (trustCategoryIpsecFdn != null && !trustCategoryIpsecFdn.isEmpty() && trustCategoryOamFdn != null && !trustCategoryOamFdn.isEmpty()) {
                isSupported = true;
                logger.debug("TrustCategory MO exists for the node :{}", normNodeRef.getFdn());
            }

        } else {
            final String trustCategoryFdn = nodeUtility.getTrustCategoryFdn(normNodeRef.getFdn(), rootMo, certType, normNodeRef);
            logger.debug("Trust Category Fdn is {}", trustCategoryFdn);

            if (trustCategoryFdn != null && !trustCategoryFdn.isEmpty()) {
                isSupported = true;
                logger.debug("TrustCategory MO exists for the node :{}", normNodeRef.getFdn());
            }
        }
        logger.debug("isTrustCategoryMOExists: return supported[{}]", isSupported);

        return isSupported;
    }

    /**
     * To validate existance of crl check specific MO
     *
     * @param normNode
     *            the Norm Node Reference
     * @param certType
     *            the certificate Type
     */
    boolean validateNodeForCrlCheckMO(final NormalizableNodeReference normNode, final String certType)
            throws SecurityMODoesNotExistException, TrustCategoryMODoesNotExistException {

        return moGetServiceFactory.validateNodeForCrlCheckMO(normNode, certType);
    }

    /**
     * This method will validate whether given node exists, node is synchronized .
     *
     * @param nodeRef
     *            represents a reference to a node, compound by the node FDN and name.
     * @return : {@link Boolean}
     *         <p>
     *         true: if all validations are passed.
     *         </p>
     *         false: if any of validation is failed.
     */
    public boolean validateNodeForOnDemandCrlDownload(final String command, final NodeReference nodeRef)
            throws UnassociatedNetworkElementException, NodeDoesNotExistException, NetworkElementNotfoundException, NodeNotCertifiableException,
            SecurityFunctionMoNotfoundException, NodeNotSynchronizedException, UnSupportedNodeReleaseVersionException, NscsCapabilityModelException,
            UnsupportedNodeTypeException, SecurityMODoesNotExistException {
        validateNode(nodeRef);
        final NormalizableNodeReference normNodeRef = reader.getNormalizableNodeReference(nodeRef);
        final String neType = normNodeRef.getNeType();
        if (!isNeTypeSupported(normNodeRef, command)) {
            final String errorMsg = String.format("Unsupported neType[%s]", neType);
            logger.error("NE Type validation failed for crl download: {}", errorMsg);
            throw new UnsupportedNodeTypeException();
        }
        validateNodeForCrlDownloadMo(normNodeRef);
        return true;
    }

    /**
     * This method will validate Node for Security Mo Existence
     *
     * @param normNodeRef
     *            the Normlizable Node Reference
     * @return
     */
    private boolean validateNodeForCrlDownloadMo(final NormalizableNodeReference normNodeRef) {

        if (nscsCapabilityModelService.isNeTypeSupportedForCrlDownloadMoValidation(normNodeRef) && !isSecurityMOExists(normNodeRef)) {

            logger.error("Node [{}] doesn't have Security MO.", normNodeRef.getFdn());
            throw new SecurityMODoesNotExistException();
        }

        return true;
    }

    /**
     * Checks if given enrollment mode is supported by node.
     *
     * @param enrollmentMode
     *            provided enrollment mode
     * @param normNodeRef
     *            the node reference
     * @return The method will return true if EnrollmentMode is supported by given node
     */
    public boolean isEnrollmentModeSupported(final String enrollmentMode, final NormalizableNodeReference normNodeRef)
            throws NscsCapabilityModelException {

        boolean isSupported = false;

        final List<String> supportedEnrollmentModes = nscsCapabilityModelService.getSupportedEnrollmentModes(normNodeRef);
        if (supportedEnrollmentModes != null && supportedEnrollmentModes.contains(enrollmentMode)) {
            isSupported = true;
            logger.debug("is EnrollmentModeSupported: enrollmentMode[{}] is supported", enrollmentMode);
        } else {
            logger.error("is EnrollmentModeSupported: enrollmentMode[{}] is unsupported for node[{}]: supported ones[{}]", enrollmentMode,
                    normNodeRef, supportedEnrollmentModes);
        }

        return isSupported;
    }

    /**
     * This method validates node in charge of supporting Https on CPP platform
     *
     * @param nodeReference
     *            is given node (reference) for validation
     * @throws NodeDoesNotExistException
     *             when given node does not exists or reference is null
     * @throws InvalidNodeException
     *             when given node does not supports https commands or could not get normalized reference for a node
     * @throws NodeNotSynchronizedException
     *             when given node is not synchronized
     * @throws CouldNotReadMoAttributeException
     *             when could not find webserver attribute in Security MO for given node
     */
    public void validateNodeForHttpsStatus(final NodeReference nodeReference) {

        logger.debug("Starting validation for node: {}", nodeReference);

        if (nodeReference == null || !reader.exists(nodeReference.getFdn())) {
            throw new NodeDoesNotExistException();
        }

        final NormalizableNodeReference normalizableNodeReference = reader.getNormalizableNodeReference(nodeReference);

        if (normalizableNodeReference == null) {
            throw new InvalidNodeException("Could not find normalized reference for node: " + nodeReference.getFdn() + ".");
        }

        if (!isCliCommandSupported(normalizableNodeReference, NscsCapabilityModelService.HTTPS_COMMAND)) {
            throw new UnsupportedNodeTypeException().setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE);
        }

        if (!isNodeSynchronized(normalizableNodeReference)) {
            throw new NodeNotSynchronizedException();
        }

        final ModelDefinition.Security securityMO = Model.ME_CONTEXT.managedElement.systemFunctions.security;

        try {
            moAttributeHandler.getMOAttributeValue(normalizableNodeReference.getFdn(), securityMO.type(), securityMO.namespace(),
                    ModelDefinition.Security.WEBSERVER);
        } catch (CouldNotReadMoAttributeException couldNotReadMoAttributeException) {
            throw new UnSupportedNodeReleaseVersionException();

        }
    }

    public void validateNodeForFtpes(final NodeReference nodeReference)
            throws InvalidNodeException, NodeDoesNotExistException, NodeNotSynchronizedException, UnsupportedNodeTypeException {

        logger.debug("Starting ftpes validation for node: {}", nodeReference);

        if (nodeReference == null || !reader.exists(nodeReference.getFdn())) {
            throw new NodeDoesNotExistException();
        }

        final NormalizableNodeReference normalizableNodeReference = reader.getNormalizableNodeReference(nodeReference);
        if (normalizableNodeReference == null) {
            throw new InvalidNodeException("Could not find normalized reference for node: " + nodeReference.getFdn() + ".");
        }

        if (!isCliCommandSupported(normalizableNodeReference, NscsCapabilityModelService.FTPES_COMMAND)) {
            throw new UnsupportedNodeTypeException().setSuggestedSolution("Check Online Help for the supported node types.");
        }
        if (!isNodeSynchronized(normalizableNodeReference)) {
            throw new NodeNotSynchronizedException();
        }

    }

    /**
     * This method validates the subjectAltNameType is in the supported range or not
     *
     * @param subjAltNameType
     *            the subjectAltNameType supported by the user
     */
    public void validateSubjectAltNameType(final String subjAltNameType) {
        if (!subjAltNameType.isEmpty()) {
            final List<String> supportedSubjectAltNameFieldType = new ArrayList<>();
            if (!isSubjectAltNameFieldTypeInSupportedRange(subjAltNameType, supportedSubjectAltNameFieldType)) {
                logger.error("ErrorMsg : {}", NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED);
                throw new SubjAltNameTypeNotSupportedXmlException(ACCEPTED_ARGUMENTS_ARE + supportedSubjectAltNameFieldType);
            }
        }
    }

    /**
     * This method validates the SAN for the subjectAltNameType is IP_ADDRESS
     *
     * @param subjAltName
     *            given subjectAltName value
     */
    public void validateSanIpAddress(final String subjAltName) {
        if (!NscsCommonValidator.getInstance().isValidIPAddress(subjAltName)) {
            logger.error("ErrorMsg : {}",
                    NscsErrorCodes.REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID + NscsErrorCodes.PLEASE_SPECIFY_A_VALID_SUBJECT_ALT_NAME_FORMAT);
            throw new InvalidSubjAltNameXmlException();
        }
    }

    /**
     * This method will validate node type for enrollment/trust distribution using external CA based on the existence of mandatory attribute for ext
     * ca operation on the node.
     *
     * @param normNodeRef
     *            Normalized node reference
     * @return true if the given node is of supported node type to perform ext ca operation otherwise false.
     */
    public boolean validateNodeTypeForExtCa(final NormalizableNodeReference normNodeRef) {
        final String targetCategory = normNodeRef.getTargetCategory();
        final String targetType = normNodeRef.getNeType();
        final String targetModelIdentity = normNodeRef.getOssModelIdentity();
        return nscsModelServiceImpl.isExtCAOperationSupported(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets if the specified attribute exists in specified MO.
     *
     * @param normNodeRef
     *            the node reference.
     * @param requiredMo
     *            the node MO need to be checked
     * @param attribute
     *            the required attribute in MO
     * @return true if isMoAttributeExists attribute exists in required MO, false otherwise.
     */
    public boolean isMoAttributeExists(final NormalizableNodeReference normNodeRef, final Mo requiredMo, final String attribute) {
        final String targetCategory = normNodeRef.getTargetCategory();
        final String targetType = normNodeRef.getNeType();
        final String targetModelIdentity = normNodeRef.getOssModelIdentity();
        return nscsModelServiceImpl.isMoAttributeExists(targetCategory, targetType, targetModelIdentity, requiredMo.namespace(), requiredMo.type(), attribute);
    }

      /**
       * To validate existance of ntp check specific MO
       *
       * @param normNode
       *            the Norm Node Reference
       */
       public boolean validateNodeForNtp(final NormalizableNodeReference normNode){
           return moGetServiceFactory.validateNodeForNtp(normNode);
    }

}