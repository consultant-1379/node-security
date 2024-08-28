/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SetCiphersWfException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.CiphersConfiguration;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.EncryptCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.KeyExchangeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.MacCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.Nodes;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.SshProtocol;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.TlsProtocol;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CipherJobInfo;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.SetCiphersResponseBuilder;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.DuplicateNodeCiphersValidator;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.SetCiphersValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.XsdErrorHandler;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.security.nscs.util.NscsStringUtils;


/**
 * Initiates the process to set TLS or SSH ciphers on the nodes
 *
 * @author tcsnapa
 *
 */
@CommandType(NscsCommandType.SET_CIPHERS)
@Local(CommandHandlerInterface.class)
public class SetCiphersHandler implements CommandHandler<CiphersConfigCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsCommandManager commandManager;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    @Inject
    private XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Inject
    SetCiphersResponseBuilder setCiphersResponseBuilder;

    @Inject
    private SetCiphersValidator setCiphersValidator;

    @Inject
    private DuplicateNodeCiphersValidator duplicateNodeCiphersValidator;

    @Inject
    private CliUtil cliUtil;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsContextService nscsContextService;

    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

    @Override
    public NscsCommandResponse process(final CiphersConfigCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);

        List<NodeCiphers> nodeCiphersList = getNodeCiphersListFromCommand(command);

        final List<NodeReference> validNodesInXml = new ArrayList<>();
        final List<CipherJobInfo> cji = new ArrayList<>();

        validateNodeCiphersList(command, nodeCiphersList, validNodesInXml, cji);

        final Integer numValidNodes = getNumValidNodesInCipherJobInfoList(cji);
        nscsContextService.initItemsStatsForAsyncCommand(numValidNodes, Integer.valueOf(invalidNodesErrorMap.size()));

        JobStatusRecord jobStatusRecord = null;

        if (!cji.isEmpty()) {
            try {
                jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.SET_CIPHERS);
                commandManager.executeSetCiphersWfs(cji, jobStatusRecord);
            } catch (final Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new SetCiphersWfException();
            }
        }

        if (!validNodesInXml.isEmpty() && invalidNodesErrorMap.isEmpty()) {
            return setCiphersResponseBuilder.buildResponseForAllValidInputNodes(jobStatusRecord);
        } else if (!validNodesInXml.isEmpty() && !invalidNodesErrorMap.isEmpty()) {
            return setCiphersResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
        } else {
            return setCiphersResponseBuilder.buildResponseForAllInvalidInputNodes(command, invalidNodesErrorMap);
        }
    }

    /**
     * Validate the given Node Ciphers list for the given command.
     * Produce a list of valid and invalid nodes and, if at least one valid node is present, the info to start a CIPHER job.
     * 
     * @param command
     *            the command.
     * @param nodeCiphersList
     *            the Node Ciphers list.
     * @param validNodesInXml
     *            the list of valid nodes.
     * @param cji
     *            the info to start the CIPHER job.
     */
    private void validateNodeCiphersList(final CiphersConfigCommand command, List<NodeCiphers> nodeCiphersList,
            final List<NodeReference> validNodesInXml, final List<CipherJobInfo> cji) {
        for (final NodeCiphers nodeCiphers : nodeCiphersList) {
            final List<NodeReference> xmlInputValidNodesList = new ArrayList<>();
            setCiphersValidator.validateNodes(command, nodeCiphers, xmlInputValidNodesList, invalidNodesErrorMap);
            if (!xmlInputValidNodesList.isEmpty()) {
                validNodesInXml.addAll(xmlInputValidNodesList);
                cji.add(new CipherJobInfo(xmlInputValidNodesList, nodeCiphers));
            }
        }
    }

    /**
     * Get the Node Ciphers list from command.
     * The XML file, if present, is retrieved, validated, unmarshalled.
     * Possible node ciphers duplication is managed.
     * 
     * @param command
     *            the command.
     * @return the Node Ciphers list.
     */
    private List<NodeCiphers> getNodeCiphersListFromCommand(final CiphersConfigCommand command) {
        List<NodeCiphers> nodeCiphersList = new ArrayList<>();
        Map<String, NodeCiphers> validnodeCiphersMap = null;

        if (command.hasProperty(CiphersConfigCommand.XML_FILE_PROPERTY)) {
            final String userProvidedCiphersConfiguration = cliUtil.getCommandInputData(command, FileConstants.FILE_URI);
            final XsdErrorHandler xsdErrorHandler = xmlValidatorUtility.validateXMLSchemaWithErrorHandler(userProvidedCiphersConfiguration,
                    CiphersConstants.CIPHERS_CONFIGURATION_XSD);

            if (!xsdErrorHandler.isValid()) {
                nscsLogger.error(xsdErrorHandler.formatErrorMessages());
                nscsLogger.commandHandlerFinishedWithError(command, NscsErrorCodes.INVALID_INPUT_XML_FILE);
                throw new InvalidInputXMLFileException(NscsErrorCodes.INVALID_XML, NscsErrorCodes.XML_SCHEMA_VALIDATIONS_FAILED)
                        .setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_VALID_XML_SCHEMA);
            }

            final CiphersConfiguration ciphersConfiguration = xmlUnMarshallerUtility.xMLUnmarshaller(userProvidedCiphersConfiguration,
                    CiphersConfiguration.class);
            validnodeCiphersMap = duplicateNodeCiphersValidator.validate(ciphersConfiguration.getNodeCiphers(), invalidNodesErrorMap);
            for (final Map.Entry<String, NodeCiphers> entry : validnodeCiphersMap.entrySet()) {
                nodeCiphersList.add(entry.getValue());
            }
        } else {
            setCiphersValidator.validateCommand(command);
            nodeCiphersList = getNodeCipherFromCommand(command);
        }
        return nodeCiphersList;
    }

    private List<NodeCiphers> getNodeCipherFromCommand(final CiphersConfigCommand command) {

        final List<NodeCiphers> nodeSpecificCiphersConfigurationList = new ArrayList<>();
        final NodeCiphers nodeCiphers = new NodeCiphers();
        final Nodes nodes = new Nodes();
        nodeCiphers.setNodes(nodes);
        final Set<String> nodeFdn = new HashSet<>();
        for (final NodeReference nodereference : command.getNodes()) {
            nodeFdn.add(nodereference.getFdn());
        }
        nodeCiphers.getNodes().setNodeFdn(nodeFdn);
        if (command.getProtocolProperty().equalsIgnoreCase(CiphersConstants.PROTOCOL_TYPE_SSH)) {
            final SshProtocol sshprotocol = new SshProtocol();
            nodeCiphers.setSshProtocol(sshprotocol);

            setEncryptCiphers(command, sshprotocol);
            setKeyExchangeCiphers(command, sshprotocol);
            setMacCiphers(command, sshprotocol);
        } else {
            nodeCiphers.setTlsProtocol(new TlsProtocol());
            if (!command.getCipherFilterProperty().equals(CiphersConstants.EMPTY_CIPHERFILTER_TAG)) {
                nodeCiphers.getTlsProtocol().setCipherFilter(command.getCipherFilterProperty());
            } else {
                nodeCiphers.getTlsProtocol().setCipherFilter(Constants.EMPTY_STRING);
            }
        }
        nodeSpecificCiphersConfigurationList.add(nodeCiphers);
        return nodeSpecificCiphersConfigurationList;
    }

    private void setEncryptCiphers(final CiphersConfigCommand command, final SshProtocol sshprotocol) {
        if (NscsStringUtils.isNotEmpty(command.getEncryptAlgosProperty())) {
            sshprotocol.setEncryptCiphers(new EncryptCiphers());
            if (!command.getEncryptAlgosProperty().equals(CiphersConstants.EMPTY_TAG)) {
                sshprotocol.getEncryptCiphers().setCipher(nscsNodeUtility.convertStringToList(command.getEncryptAlgosProperty()));
            } else {
                sshprotocol.getEncryptCiphers().setCipher(new ArrayList<>());
            }
        }
    }

    private void setKeyExchangeCiphers(final CiphersConfigCommand command, final SshProtocol sshprotocol) {
        if (NscsStringUtils.isNotEmpty(command.getKexProperty())) {
            sshprotocol.setKeyExchangeCiphers(new KeyExchangeCiphers());
            if (!command.getKexProperty().equals(CiphersConstants.EMPTY_TAG)) {
                sshprotocol.getKeyExchangeCiphers().setCipher(nscsNodeUtility.convertStringToList(command.getKexProperty()));
            } else {
                sshprotocol.getKeyExchangeCiphers().setCipher(new ArrayList<>());
            }
        }
    }

    private void setMacCiphers(final CiphersConfigCommand command, final SshProtocol sshprotocol) {
        if (NscsStringUtils.isNotEmpty(command.getMacsProperty())) {
            sshprotocol.setMacCiphers(new MacCiphers());
            if (!command.getMacsProperty().equals(CiphersConstants.EMPTY_TAG)) {
                sshprotocol.getMacCiphers().setCipher(nscsNodeUtility.convertStringToList(command.getMacsProperty()));
            } else {
                sshprotocol.getMacCiphers().setCipher(new ArrayList<>());
            }
        }
    }

    /**
     * Get number of valid nodes present in the given Cipher Job Info list.
     * 
     * @param cipherJobInfoList
     *            the Rtsel Job Info list.
     * @return the number of nodes.
     */
    private Integer getNumValidNodesInCipherJobInfoList(final List<CipherJobInfo> cipherJobInfoList) {
        int numValidNodes = 0;
        for (final CipherJobInfo cipherJobInfo : cipherJobInfoList) {
            numValidNodes += cipherJobInfo.getValidNodesList().size();
        }
        return Integer.valueOf(numValidNodes);
    }

}
