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
package com.ericsson.nms.security.nscs.node.certificate.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.AlgorithmKeySizeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidEntityProfileNameDefaultXmlException;
import com.ericsson.nms.security.nscs.api.exception.InvalidEntityProfileNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputNodeListException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidSubjAltNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameSubjAltNameTypeEmptyXmlException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameTypeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCertificateTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

/**
 * This class is used to validate the node before performing certificate Issue operation
 *
 * @author xsrirko
 *
 */
public class CertificateIssueValidator {
    private static final String ACCEPTED_KEY_ALGORITHMS_ARE = "Accepted Key Algorithms are ";
    private static final String CERTIFICATE_SUBJECTDN_IS_NOT_PROVIDED = "CertificateSubjectDn is not provided";
    private static final String CHALLENGE_PHRASE_NOT_PROVIDED_FOR_CMPV2_INITIAL = "ChallengePhrase not provided for node enrollment using CMPv2_INITIAL";
    private static final String INTERFACE_FDN_IS_MANDATORY = "InterfaceFdn is mandatory during enrollment with External CA";
    private static final String CMPV2_INITIAL = "CMPv2_INITIAL";

    @Inject
    private CertificateEnrollmentValidator certEnrollmentValidator;

    @Inject
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private NscsCMReaderService reader;

    @EJB
    private NscsCommandManager commandManager;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NscsLogger logger;

    /**
     * This method validates the list of nodes for performing certificate issue operation
     *
     * @param inputNodesList
     *            list of nodes to be validated
     * @param certType
     *            certificate type to be checked for node support
     * @param validNodeList
     *            list of valid nodes
     * @param invalidNodeErrors
     *            map of exception for each node reference
     * @param isEnrollmentWithExternalCA
     *            boolean value to verify if the enrollment is with external ca
     * @throws NscsServiceException
     *             thrown when any unexpected error occurred while validating the node
     */
    public void validate(final List<Node> inputNodesList, final String certType, final List<Node> validNodeList,
            final Map<NodeReference, NscsServiceException> invalidNodeErrors, final boolean isEnrollmentWithExternalCA) {
        logger.info("ValidateNodesForCertificateIssue. InputNodesList size: " + inputNodesList.size() + " certType: " + certType);

        if (areCommonNameDuplicated(inputNodesList)) {
            logger.error(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND);
            throw new InvalidInputNodeListException("Duplicated CommonName attributes are found");
        }
        for (final Node inputNode : inputNodesList) {
            final String fdn = inputNode.getNodeFdn();
            final NodeReference nodeRef = new NodeRef(fdn);
            try {

                certEnrollmentValidator.validate(nodeRef, certType, isEnrollmentWithExternalCA);

                if (isEnrollmentWithExternalCA) {
                    validateExtCaXmlForNode(inputNode);
                } else {
                    validateXmlForNode(inputNode, certType);
                }

                validNodeList.add(inputNode);
            } catch (AlgorithmKeySizeNotSupportedXmlException | InvalidArgumentValueException | InvalidEntityProfileNameDefaultXmlException
                    | InvalidEntityProfileNameXmlException | InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | SecurityFunctionMoNotfoundException | InvalidInputNodeListException | InvalidSubjAltNameXmlException
                    | SubjAltNameSubjAltNameTypeEmptyXmlException | SubjAltNameTypeNotSupportedXmlException | NscsCapabilityModelException
                    | NodeNotSynchronizedException | UnassociatedNetworkElementException | UnsupportedCertificateTypeException
                    | UnsupportedNodeTypeException exc) {
                invalidNodeErrors.put(nodeRef, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", fdn, exc.getMessage(), exc);
            }
        }

        final List<String> enrollmentValuesErrorMsg = new ArrayList<>();
        if (!commandManager.isEnrollmentModeSupportedForNodeList(validNodeList, enrollmentValuesErrorMsg)) {
            logger.error(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND);
            throw new InvalidInputNodeListException(enrollmentValuesErrorMsg.get(0));
        }
    }

    private void validateXmlForNode(final Node inputNode, final String certType) {

        logger.debug("XML VALIDATION : node {}", inputNode.getNodeFdn());

        if (!inputNode.getCertificateSubjectDn().isEmpty()) {
            throw new InvalidArgumentValueException("CertificateSubjectDn is not required. Try removing CertificateSubjectDn field from XML");
        }

        if (!inputNode.getChallengePhrase().isEmpty()) {
            throw new InvalidArgumentValueException("ChallengePhrase is not required. Try removing ChallengePhrase field from XML");
        }

        validateSubjectAltNameAndType(certType, inputNode);

        final String entityProfileName = inputNode.getEntityProfileName().trim();
        final String keySize = inputNode.getKeySize().trim();

        final NodeReference nodeRef = new NodeRef(inputNode.getNodeFdn());
        final String entityName = nodeValidatorUtility.buildEntityNameFromNodeRef(nodeRef, certType);
        logger.debug("Entity name for node {} and certType {} is {}", nodeRef.getName(), certType, entityName);

        if (!entityProfileName.isEmpty() && nodeValidatorUtility.isEntityProfileNameAvailable(entityProfileName)) {
            logger.error(NscsErrorCodes.REQUESTED_ENTITY_PROFILE_NAME_DOES_NOT_EXIST);
            throw new InvalidEntityProfileNameXmlException();
        }

        if (!keySize.isEmpty()) {
            final EntityProfile entityProfile = getEntityProfile(entityName, entityProfileName, certType, inputNode);
            validateKeySize(keySize, entityProfile);
        }
    }

    private void validateExtCaXmlForNode(final Node inputNode) {
        if (inputNode.getCertificateSubjectDn() == null || inputNode.getCertificateSubjectDn().isEmpty()) {
            throw new InvalidArgumentValueException(CERTIFICATE_SUBJECTDN_IS_NOT_PROVIDED);
        }

        if(inputNode.getInterfaceFdn() == null || inputNode.getInterfaceFdn().isEmpty()){
            throw new InvalidArgumentValueException(INTERFACE_FDN_IS_MANDATORY);
        }

        if (inputNode.getEnrollmentMode().equalsIgnoreCase(CMPV2_INITIAL)
                && (inputNode.getChallengePhrase() == null || inputNode.getChallengePhrase().isEmpty())) {
            throw new InvalidArgumentValueException(CHALLENGE_PHRASE_NOT_PROVIDED_FOR_CMPV2_INITIAL);
        }
        validateSubjectAltNameAndType(CertificateType.IPSEC.name(), inputNode);
    }

    private void validateSubjectAltNameAndType(final String certType, final Node inputNode) {
        final String subjAltName = inputNode.getSubjectAltName().trim();
        final String subjAltNameType = inputNode.getSubjectAltNameType().trim();

        if (certType.equals(CertificateType.IPSEC.name()) && (subjAltName.isEmpty() || subjAltNameType.isEmpty())) {
            logger.error(NscsErrorCodes.SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY);
            throw new SubjAltNameSubjAltNameTypeEmptyXmlException();
        }

        nodeValidatorUtility.validateSubjectAltNameType(subjAltNameType);

        if (SubjectAltNameFieldType.IP_ADDRESS.name().equals(subjAltNameType)) {
            nodeValidatorUtility.validateSanIpAddress(subjAltName);
        }
    }

    private boolean areCommonNameDuplicated(final List<Node> xmlNodeList) {
        final List<String> commonNameList = new ArrayList<>();
        for (final Node node : xmlNodeList) {
            final String currentCommonName = node.getCommonName();
            if (currentCommonName != null && !currentCommonName.isEmpty()) {
                if (commonNameList.contains(currentCommonName.trim())) {
                    return true;
                }
                commonNameList.add(currentCommonName);
            }
        }
        return false;
    }

    private void validateKeySize(final String keySize, final EntityProfile entityProfile) {
        final List<String> supportedAlgorithmKeySizeValues = new ArrayList<>();
        if (!nodeValidatorUtility.isAlgorithmKeySizeInProfileRange(keySize, entityProfile, supportedAlgorithmKeySizeValues)) {
            logger.error(NscsErrorCodes.KEY_ALGORITHM_NOT_SUPPORTED_BY_ENTITY_PROFILE, keySize, entityProfile.getName(), supportedAlgorithmKeySizeValues);
            throw new AlgorithmKeySizeNotSupportedXmlException("The given Key Algorithm [" + keySize + "] is not in supported list of Entity Profile [" + entityProfile.getName() + "]. "
                    + ACCEPTED_KEY_ALGORITHMS_ARE + supportedAlgorithmKeySizeValues);
        }
    }

    private EntityProfile getEntityProfile(final String entityName, final String entityProfileName, final String certType, final Node inputNode) {
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
                if (!nscsPkiManager.isEntityNameAvailable(entityName, EntityType.ENTITY)) {
                    final Entity entity = nscsPkiManager.getPkiEntity(entityName);
                    entityProfile = entity.getEntityProfile();
                    logger.debug("Retrieved entityProfile {} from node entity {}", entityProfile, entityName);
                } else {
                    // Entity does not exist for node: get default profile
                    entityProfile = getDefaultEntityProfile(certType, inputNode);
                }
            } catch (final NscsPkiEntitiesManagerException e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " while getPkiEntity";
                logger.error(errorMessage);
                throw new InvalidInputNodeListException();
            }
        }
        return entityProfile;
    }

    private EntityProfile getDefaultEntityProfile(final String certType, final Node inputNode) throws NscsPkiEntitiesManagerException {
        final NodeModelInformation nodeModelInfo = reader.getNodeModelInformation(inputNode.getNodeFdn());
        final NodeEntityCategory nodeEntityCategory = nodeValidatorUtility.getNodeEntityCategory(certType);
        if (nodeEntityCategory == null) {
            throw new InvalidInputNodeListException();
        }

        final String defaultEntityProfile = nscsCapabilityModelService.getDefaultEntityProfile(nodeModelInfo, nodeEntityCategory);
        if (nodeValidatorUtility.isEntityProfileNameAvailable(defaultEntityProfile)) {
            logger.error(NscsErrorCodes.DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST);
            throw new InvalidEntityProfileNameDefaultXmlException();
        }
        final EntityProfile entityProfile = nscsPkiManager.getEntityProfile(defaultEntityProfile);

        logger.debug("Retrieved entityProfile {} from default entityProfile {}", entityProfile, defaultEntityProfile);

        return entityProfile;
    }
}
