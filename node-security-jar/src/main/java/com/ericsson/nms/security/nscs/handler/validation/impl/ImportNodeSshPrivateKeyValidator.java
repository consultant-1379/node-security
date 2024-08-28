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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.util.FileUtil;

/**
 * This class defines the methods to validate command and nodes for sshkey import command.
 *
 * @author zkttmnk
 */
public class ImportNodeSshPrivateKeyValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private FileUtil fileUtil;

    /**
     * This method is used to verify the fileName and extension of file used in secadm sshprivatekey command.
     *
     * @param fileName
     *            contains name of the file provided in the command.
     * @param fileExtention
     *            the fileExtention
     */
    public void verifyFileNameAndExtension(final String fileName, final String fileExtention) {
        if (fileName == null) {
            final String errorMessage = "UpdateSshPrivateKeyValidator: Unsupported File Name :" + fileName + " for sshkey import command. "
                    + "Please provide valid file name";
            logger.error(errorMessage);
            throw new InvalidArgumentValueException(NscsErrorCodes.INVALID_FILE_NAME)
                    .setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_FILE_NAME + NscsErrorCodes.EXAMPLE_NODE_SSH_PRIVATE_KEY_FILE_NAME);
        }
        if (!fileUtil.isValidFileExtension(fileName, fileExtention)) {
            final String errorMessage = "UpdateSshPrivateKeyValidator: Unsupported File Type for sshkey import command with file name:" + fileName
                    + NscsErrorCodes.SUPPORTED_FILE_TYPE + fileExtention;
            logger.error(errorMessage);
            throw new InvalidArgumentValueException(NscsErrorCodes.UNSUPPORTED_FILE_TYPE)
                    .setSuggestedSolution(NscsErrorCodes.SUPPORTED_FILE_TYPE + fileExtention);
        }
    }

    /**
     * @param normNode
     *            the NormalizableNodeReference
     * @param nodeRef
     *            the NodeReference
     * @return validityState true when provided node is valid and have securityFunction and networkElementSecurity MO's
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
        if (nodeRef == null || !reader.exists(nodeRef.getFdn())) {
            throw new NodeDoesNotExistException();
        }
        if (!reader.exists(Model.getNomalizedRootMO(normNode.getNormalizedRef().getFdn()).securityFunction
                .withNames(normNode.getNormalizedRef().getName()).fdn())) {
            throw new SecurityFunctionMoNotfoundException();
        }
        if (!reader.exists(Model.getNomalizedRootMO(normNode.getNormalizedRef().getFdn()).securityFunction.networkElementSecurity
                .withNames(normNode.getNormalizedRef().getName()).fdn())) {
            throw new NetworkElementSecurityNotfoundException();
        }
        return true;
    }

    public boolean isNodeExists(final NodeReference nodeRef) {
        boolean isNodeExists = false;
        if (Model.NETWORK_ELEMENT.isPresent(nodeRef.getFdn()) && reader.exists(nodeRef.getFdn())) {
            isNodeExists = true;
        }
        return isNodeExists;
    }

    /**
     * @param nodeRef
     *            the NodeReference
     * @return normNode returns NormalizableNodeReference in case of valid node
     */
    public NormalizableNodeReference validateNode(final NodeReference nodeRef) {
        NormalizableNodeReference normNode = null;
        try {
            normNode = reader.getNormalizableNodeReference(nodeRef);
            validate(normNode, nodeRef);
        } catch (NetworkElementNotfoundException | NodeDoesNotExistException | NodeNotSynchronizedException | InvalidNodeNameException
                | UnassociatedNetworkElementException exc) {
            logger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            throw exc;
        }
        return normNode;
    }
}
