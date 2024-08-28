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

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCertificateTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

/**
 * This class is to validate the node support for certificate Issue, Trust Distribution and Certificate re-issue operations.
 *
 * @author xsrirko
 *
 */
public class CertificateEnrollmentValidator {

    @Inject
    NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsLogger logger;

    /**
     * This method validates the node against the nscs operation to be performed
     *
     * @param nodeRef
     *            node reference for validation
     * @param certType
     *            certificate type to validated for the support on the node
     * @param isExternalCa
     *            value to verify if the operation supports External Ca
     */
    public void validate(final NodeReference nodeRef, final String certType, final boolean isExternalCa) {

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

        if (!nodeValidatorUtility.isNodeExists(normalizedNodeRef)) {
            logger.error("NetworkElement [{}] doesn't exist.", normalizedNodeRef.getFdn());
            throw new NetworkElementNotfoundException();
        }

        if (!nodeValidatorUtility.isCertificateSupportedForNode(normNode)) {
            logger.error("Node [{}] doesn't support certificates.", normalizedNodeRef.getFdn());
            throw new NodeNotCertifiableException();
        }

        if (!nodeValidatorUtility.isCertificateTypeSupported(normNode, certType)) {
            final String nodeName = normNode.getName();
            final String errorMessage = String.format("Unsupported certType[%s] for node[%s]", certType, nodeName);
            logger.error(errorMessage);
            throw new UnsupportedCertificateTypeException(errorMessage);
        }

        if (!nodeValidatorUtility.hasNodeSecurityFunctionMO(normalizedNodeRef)) {
            logger.error("Node [{}]  doesn't have valid SecurityFunctionMO.", normalizedNodeRef.getFdn());
            throw new SecurityFunctionMoNotfoundException();
        }

        if (!nodeValidatorUtility.isNodeSynchronized(normNode)) {
            logger.error("Node [{}] is not synchronized.", normalizedNodeRef.getFdn());
            throw new NodeNotSynchronizedException();
        }

        if (isExternalCa && !nodeValidatorUtility.validateNodeTypeForExtCa(normNode)) {
            throw new UnsupportedNodeTypeException().setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SUPPORTED_NODE);
        }
    }

    /**
     * This method validates certificate Type for the enrollment performed using External CA
     *
     * @param certType
     *            input Certificate Type
     * @return true when the given certificate Type is IPSEC
     */
    public boolean validateCertificateTypeForExtCa(final String certType) {
        if (certType.equals(CertificateType.IPSEC.toString())) {
            return true;
        } else {
            logger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
            throw new InvalidArgumentValueException("The enrollment with ExternalCA is allowed only with certType IPSEC");
        }
    }
}