/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
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

import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.NtpOperationNotSupportedException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.delete.request.model.Nodes;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.topologyCollectionsService.exception.rest.EmptyFileException;

/**
 * This class defines the methods to validate command and nodes for Ntp command.
 *
 * @author zkndsrv
 */
public class NtpValidator {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService nscsCmReaderService;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private FileUtil fileUtil;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    private static final String NTP_REMOVE_XML_VALIDATOR_FILENAME = "NtpRemoveConfiguration.xsd";

    /**
     * This method is used to validate nodes reference and type.
     *
     * @param inputNodes
     *            all unique input node list to validate.
     * @param validNodes
     *            only valid nodes are added to this list.
     * @param invalidNodesError
     *            all invalid nodes are added to this map.
     * @return havingAllValidNodes returns true if all are valid nodes false if any one node is invalid
     */
    public boolean validateNodes(final List<NodeReference> inputNodes, final List<NormalizableNodeReference> validNodes,
                                 final Map<NodeReference, NscsServiceException> invalidNodesError) {
        nscsLogger.debug("Number of unique nodes to validate for Ntp list: {}", inputNodes.size());
        boolean havingAllValidNodes = true;
        for (final NodeReference nodeRef : inputNodes) {
            final NormalizableNodeReference normNode = nscsCmReaderService.getNormalizableNodeReference(nodeRef);
            try {
                validateNode(normNode, nodeRef);
                validNodes.add(normNode);
            } catch (NetworkElementNotfoundException | NodeDoesNotExistException | NodeNotSynchronizedException | InvalidNodeNameException
                    | UnassociatedNetworkElementException | NtpOperationNotSupportedException exc) {
                havingAllValidNodes = false;
                invalidNodesError.put(nodeRef, exc);
                nscsLogger.error("Node [{}] has validation problem. Exception is [{}]", nodeRef.getFdn(), exc.getMessage());
            }
        }
        return havingAllValidNodes;
    }

    /**
     * This method is used to validate the nodes
     *
     * @param normNode
     *            a normalizable node reference based on a given node reference (FDN)
     * @param nodeRef
     *            represents a reference to a node, compound by the node FDN and name
     * @throws NtpOperationNotSupportedException
     *             throws exception if give Node doesn't support ntp feature
     */
    public void validateNode(final NormalizableNodeReference normNode, final NodeReference nodeRef) {

        nodeValidatorUtility.validate(normNode, nodeRef);

        if (!moGetServiceFactory.validateNodeForNtp(normNode)) {
            nscsLogger.error("Required Time Setting MO attributes are not available on Node [{}] for NTP support.", nodeRef.getFdn());
            throw new NtpOperationNotSupportedException();
        }
    }

    /**
     * This method is used to verify the extension of file used in secadm ntp command.
     *
     * @param fileName
     *            contains name of the file provided in the command.
     * @param fileExtention
     *            the fileExtention
     */
    public void verifyFileExtension(final String fileName, final String fileExtention) {
        if (fileName != null && !fileUtil.isValidFileExtension(fileName, fileExtention)) {
            final String errorMessage = "NtpValidator: Unsupported File Type for Ntp command with file name:" + fileName
                    + NscsErrorCodes.SUPPORTED_FILE_TYPE + fileExtention;
            nscsLogger.error(errorMessage);
            throw new InvalidArgumentValueException(NscsErrorCodes.UNSUPPORTED_FILE_TYPE)
            .setSuggestedSolution(NscsErrorCodes.SUPPORTED_FILE_TYPE + fileExtention);
        }
    }

    /**
     * This method is used to perform XSD schema validation of input XML file used in secadm ntp remove command.
     *
     * @param fileData
     *            contains file data in bytes.
     *
     * @throws EmptyFileException
     *             if provided input xml is empty.
     *
     * @throws InvalidInputXMLFileException
     *             if XML schema validation fails.
     */
    public void validateFileDataForNtpRemove(final String fileData) {
        if (null == fileData || fileData.isEmpty()) {
            nscsLogger.error(NscsErrorCodes.EMTPY_FILE_ERROR);
            throw new EmptyFileException(NscsErrorCodes.EMTPY_FILE_ERROR);
        }
        if (!xmlValidatorUtility.validateXMLSchema(fileData, NTP_REMOVE_XML_VALIDATOR_FILENAME)) {
            nscsLogger.error(NscsErrorCodes.XML_VALIDATION_FAILED);
            throw new InvalidInputXMLFileException(NscsErrorCodes.XML_VALIDATION_FAILED);
        }
    }

    /**
     * To Validate the Node data given in xml file. Node details should contain either the ntp serverIds list or the ntp KeyIds List to perform ntp
     * remove operation
     *
     * @param nodes
     *            The input nodes from xml file
     */
    public void validateXMLFileNodeDataForNtpRemove(final Nodes nodes) {

        final List<Nodes.Node> nodeList = nodes.getNode();
        for (final Nodes.Node node : nodeList) {
            final Nodes.Node.KeyIds keyIds = node.getKeyIds();
            final Nodes.Node.ServerIds serverIds = node.getServerIds();

            if ((keyIds == null && serverIds == null) || (keyIds != null && serverIds != null)) {
                nscsLogger.error(NscsErrorCodes.XML_VALIDATION_FAILED);
                throw new InvalidInputXMLFileException(NscsErrorCodes.XML_VALIDATION_FAILED);
            }
        }
    }

    /**
     * Validates maximum numbers of installedNtpkeyIds
     *
     * @param task
     *            the task
     * @param installedNtpKeyIdsSize
     *            the installedNtpKeyIdsSize
     * @throws WorkflowTaskException
     *             throws exception if given installedNtpKeyIdsSize equals the maximum number of ntp keys
     */
    public void validateMaxNumberOfNtpKeys(final WorkflowQueryTask task, final int installedNtpKeyIdsSize) {
        nscsLogger.info(task, "Validating Maximum Number Of Ntp Keys on NodeFdn:" + task.getNodeFdn());
        if (installedNtpKeyIdsSize >= NtpConstants.CPP_MAX_NTP_KEYS) {
            final String errorMessage = "NtpValidator: Maximum Ntp keys limit exceeded on the NodeFdn:" + task.getNodeFdn();
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException(NtpConstants.CPP_MAX_NTP_KEYS_EXCEEDED_EXCEP_MSG);

        }
    }

    /**
     * Validates maximum numbers of ntp servers
     *
     * @param task
     *            the task
     * @param ntpServerSize
     *            the ntpServerSize
     * @throws WorkflowTaskException
     *             throws exception if given ntpServerSize equals the maximum number of ntp servers
     */
    public void validateMaxNumberOfNtpServers(final WorkflowQueryTask task, final int ntpServerSize) {
        nscsLogger.info(task, "Validating Maximum Number Of Ntp Servers on NodeFdn:" + task.getNodeFdn());
        if (ntpServerSize > NtpConstants.CPP_MAX_NTP_SERVERS) {
            final String errorMessage = "NtpValidator: Maximum Ntp servers limit exceeded on the NodeFdn:" + task.getNodeFdn();
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException(NtpConstants.CPP_MAX_NTP_SERVERS_EXCEEDED_EXCEP_MSG);
        }
    }
}
