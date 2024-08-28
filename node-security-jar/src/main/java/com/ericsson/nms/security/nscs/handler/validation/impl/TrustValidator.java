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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.oss.services.security.nscs.util.NscsEnumUtils;

import com.ericsson.nms.security.nscs.api.command.types.TrustDistributeCommand;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedTrustCategoryTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.handler.command.impl.TrustDistributeHandler;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes.NodeTrustInfo;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.services.topologyCollectionsService.exception.rest.EmptyFileException;
/**
 * This class defines the methods to validate command and nodes for trust distribution.
 *
 * @author xkumkam
 *
 */
public class TrustValidator {
    private static final String INTERFACE_FDN_IS_MANDATORY = "InterfaceFdn is mandatory during trust distribution with External CA";

    @Inject
    NscsLogger logger;

    @Inject
    NodeValidatorUtility validatorUtility;

    @Inject
    NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    public static final String EXTCA_VALIDATOR_FILENAME = "ExternalCATrustDistributionInfoSchema.xsd";

    /**
     * This method is used to validate the input nodes for secadm trust commands
     *
     * @param inputNodesList
     * @param trustCategory
     * @param validNodesList
     * @param invalidNodesErrorMap
     */
    public void validateInputNodes(final List<NodeReference> inputNodesList, final String trustCategory, final List<NodeReference> validNodesList,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final Boolean isExternalCa) {

        logger.info(String.format("ValidateNodesForTrust. InputNodesList: %s", inputNodesList));
        logger.info("trustCategoryType[{}]", trustCategory);

        for (final NodeReference nodeRef : inputNodesList) {
            try {
                validatorUtility.validateNodeTrust(nodeRef, trustCategory, isExternalCa);
                validNodesList.add(nodeRef);
            } catch (UnassociatedNetworkElementException | InvalidNodeNameException | NetworkElementNotfoundException | NodeNotCertifiableException
                    | UnsupportedTrustCategoryTypeException | SecurityFunctionMoNotfoundException | NodeNotSynchronizedException | UnsupportedNodeTypeException exc){
                invalidNodesErrorMap.put(nodeRef, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
    }

    /**
     * This method is used to validate the input nodes for secadm trust distribute command
     *
     * @param inputNodesList
     * @param trustCategory
     * @param validNodesList
     * @param invalidNodesErrorMap
     */
    public void validateNodesForTrustDistribute(final List<NodeTrustInfo> inputNodesList, final String trustCategory,
            final List<NodeTrustInfo> validNodesList, final Map<NodeTrustInfo, NscsServiceException> invalidNodesErrorMap, final Boolean isExternalCa) {

        logger.info(String.format("ValidateNodesForTrust. InputNodesList: %s", inputNodesList));
        logger.info("trustCategoryType[{}]", trustCategory);

        for (final NodeTrustInfo node : inputNodesList) {
            final NodeReference nodeRef = new NodeRef(node.getNodeFdn());
            try {
                if (isExternalCa && (node.getInterfaceFdn() == null || node.getInterfaceFdn().isEmpty())) {
                    throw new InvalidArgumentValueException(INTERFACE_FDN_IS_MANDATORY);
                }

                validatorUtility.validateNodeTrust(nodeRef, trustCategory, isExternalCa);
                validNodesList.add(node);
            } catch (final UnassociatedNetworkElementException | InvalidArgumentValueException | InvalidNodeNameException
                    | NetworkElementNotfoundException | NodeNotCertifiableException | UnsupportedTrustCategoryTypeException
                    | SecurityFunctionMoNotfoundException | NodeNotSynchronizedException | UnsupportedNodeTypeException exc) {
                invalidNodesErrorMap.put(node, exc);
                logger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
    }

    /**
     * This method is used to validate the Trust distribute command for the occurrence of nodelist/nodefile property when ever there is LAAD with ca
     * property occurs in the command.
     * 
     * @param command
     *            Nscs node command of type TrustDistributeCommand
     */
    public void validateTrustDistributeCommand(final TrustDistributeCommand command) {
        String trustCategory = command.getTrustCategory();
        if (command.getCertType() != null) {
            trustCategory = command.getCertType();
        }
        if ((command.getProperties().containsKey(TrustDistributeCommand.CA_PROPERTY) && trustCategory.equals(TrustCategoryType.LAAD.toString()) && !(command
                .getProperties().containsKey(TrustDistributeCommand.NODE_LIST_FILE_PROPERTY) || command.getProperties().containsKey(
                TrustDistributeCommand.NODE_LIST_PROPERTY)))) {
            throw new CommandSyntaxException("LAAD with --ca property must be followed by either node list or node file propetry.");
        }
        if ((command.getProperties().containsKey(TrustDistributeCommand.XML_FILE_PROPERTY) && !command.getProperties().containsKey(
                TrustDistributeCommand.EXTERNAL_CA_PROPERTY))
                || (!command.getProperties().containsKey(TrustDistributeCommand.XML_FILE_PROPERTY) && command.getProperties().containsKey(
                        TrustDistributeCommand.EXTERNAL_CA_PROPERTY))) {

            throw new CommandSyntaxException(NscsErrorCodes.INVALID_COMMAND_FOR_EXTERNAL_CA);

        }
    }

    /**
     * This method is validates the given certificate type value and trust category value against the specified values in the enums
     * {@link CertificateType} and {@link TrustCategoryType}
     * 
     * @param certType
     *            is the Certificate Type i.e. either IPSEC or OAM
     * @param trustCategory
     *            is the TrustCategory Type i.e. one of the value from {IPSEC, OAM, LAAD}
     */
    public void validateCommandForCertTypeAndTrustCategory(final String certType, final String trustCategory) {
        String errMsg = "";
        if (certType != null) {
            if (!NscsEnumUtils.isValidEnum(CertificateType.class, certType)) {
                if (certType.equals(TrustCategoryType.LAAD.toString())) {
                    errMsg = "Use --trustcategory property for LAAD instead of --certType property !";
                } else {
                    errMsg = String.format(TrustDistributeHandler.INVALID_ARGUMENT, TrustDistributeCommand.CERT_TYPE_PROPERTY
                            + ". Accepted arguments are [IPSEC,  OAM]");
                }
            }
        } else {
            if (!NscsEnumUtils.isValidEnum(TrustCategoryType.class, trustCategory)) {
                errMsg = String.format(TrustDistributeHandler.INVALID_ARGUMENT, TrustDistributeCommand.TRUST_CATEGORY_PROPERTY
                        + ". Accepted arguments are [IPSEC,  OAM,  LAAD]");
            }
        }
        if (!errMsg.isEmpty()) {
            logger.error(errMsg);
            throw new InvalidArgumentValueException(errMsg);
        }

    }

    /**
     * This method validates trust category Type for the trust distribution using External CA
     *
     * @param trustCategory
     *          input trustCategory Type
     *
     */
    public void validateTrustCategoryTypeForExtCa(final String trustCategory) {
        if (!trustCategory.equals(TrustCategoryType.IPSEC.toString())) {
            logger.error(NscsErrorCodes.IPSEC_TRUST_DISTRIBUTION_IS_ALLOWED_FOR_EXTERNAL_CA);
            throw new InvalidArgumentValueException(NscsErrorCodes.IPSEC_TRUST_DISTRIBUTION_IS_ALLOWED_FOR_EXTERNAL_CA);
        }
    }

    public void validateFileDataForExtCa(final String fileData) {
        if (null == fileData || fileData.isEmpty()) {
            logger.error("Empty input XML file");
            throw new EmptyFileException("Empty input XML file");
        }
        if (!xmlValidatorUtility.validateXMLSchema(fileData, EXTCA_VALIDATOR_FILENAME)){
            logger.error(NscsErrorCodes.XML_VALIDATION_FAILED);
            throw new InvalidInputXMLFileException(NscsErrorCodes.XML_VALIDATION_FAILED);
        }
    }
}
