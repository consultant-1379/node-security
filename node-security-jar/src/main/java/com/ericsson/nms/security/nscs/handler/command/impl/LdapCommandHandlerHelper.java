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
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.LdapConfigurationCommand;
import com.ericsson.nms.security.nscs.api.command.types.LdapRenewCommand;
import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.api.exception.DuplicateNodeNamesException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.LdapConfigureWfException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.ldap.control.LdapConfigurationProvider;
import com.ericsson.nms.security.nscs.ldap.control.LdapConfigurationResponseObjectBuilder;
import com.ericsson.nms.security.nscs.ldap.entities.LdapConfigurations;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;
import com.ericsson.nms.security.nscs.ldap.utility.LdapCommandHandlerUtils;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConfigurationUnMarshaller;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.ldap.utility.UserProvidedLdapConfigurationValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

public class LdapCommandHandlerHelper {

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsCommandManager nscsCommandManager;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Inject
    private LdapConfigurationProvider ldapConfigurationProvider;

    @Inject
    private LdapConfigurationResponseObjectBuilder ldapConfigurationResponseObjectBuilder;

    @Inject
    private XmlValidatorUtility xmlValidatorUtility;

    @Inject
    private LdapConfigurationUnMarshaller ldapConfigurationUnMarshaller;

    @Inject
    private LdapCommandHandlerUtils ldapCommandHandlerUtils;

    @Inject
    private UserProvidedLdapConfigurationValidator userProvidedLdapConfigurationValidator;

    @Inject
    private NscsContextService nscsContextService;

    private static final String FILE_URI = "file:";
    private static final String XSD_VALIDATOR_FILE_NAME = "LdapConfigurationSchema.xsd";

    public NscsCommandResponse processActivate(final LdapConfigurationCommand command, final CommandContext context,
            final LDAPConfigurationMode commandType) {

        nscsLogger.commandHandlerStarted(command);

        nscsLogger.info("Command context is {}", context);

        // MANUAL command only return configuration ldap parameters
        // no action on nodes
        if (command.hasProperty(LdapConstants.MANUAL)) {
            nscsLogger.info("ldap configure with MANUAL property");
            final Map<String, Object> ldapConfiguration = ldapConfigurationProvider.getLdapServerConfiguration();
            final String proxyAccountName = getProxyAccountNameFromLdapConfiguration(ldapConfiguration);
            nscsContextService.setProxyAccountNameContextValue(proxyAccountName);
            nscsLogger.commandHandlerFinishedWithSuccess(command, "ldap MANUAL configured successfully");
            return ldapConfigurationResponseObjectBuilder.build(ldapConfiguration);
        }

        nscsLogger.info("Get Ldap Configuration XML");
        // Get the content of the user provided LDAP configuration XML file.
        final String userProvidedLdapConfiguration = ldapCommandHandlerUtils.getLdapConfigurationXML(command, FILE_URI);
        // Validate the user provided LDAP Configuration file as per schema.
        final boolean isValidLdapConfiguration = xmlValidatorUtility.validateXMLSchema(userProvidedLdapConfiguration, XSD_VALIDATOR_FILE_NAME);

        if (!isValidLdapConfiguration) {
            nscsLogger.error(NscsErrorCodes.XML_VALIDATION_FAILED);
            InvalidInputXMLFileException invalidInputXMLFileException = new InvalidInputXMLFileException();
            nscsLogger.commandHandlerFinishedWithError(command, invalidInputXMLFileException.getMessage());
            throw invalidInputXMLFileException;
        }

        if (LDAPConfigurationMode.LDAP_RENEW.equals(commandType) && !((LdapRenewCommand) command).isForce()) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, "confirmation requested for renew command");
            return ldapConfigurationResponseObjectBuilder.buildLdapRenewConfirmationResponse();
        }

        nscsLogger.info("Ldap Configuration XML is valid - now build node list");
        // Un-marshal the user provided LDAP configuration from XML into a Java Container Object.
        final LdapConfigurations nodeSpecificConfigurationsBatch = ldapConfigurationUnMarshaller
                .buildLdapConfigurationFromXMLContent(userProvidedLdapConfiguration);

        final List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationList = nodeSpecificConfigurationsBatch.getList();
        if (nodeSpecificLdapConfigurationList == null || nodeSpecificLdapConfigurationList.isEmpty()) {
            final String errorMsg = "No nodes specified in XML file.";
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw new InvalidInputXMLFileException(errorMsg);
        }
        List<NodeSpecificLdapConfiguration> uniqueNodes;
        try {
            uniqueNodes = buildListWithoutDuplicates(nodeSpecificLdapConfigurationList);
        } catch (final DuplicateNodeNamesException e) {
            final String errorMsg = String.format("Duplicate node exception occurred: %s", NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw new InvalidInputXMLFileException(e.getMessage());
        }

        final List<NodeSpecificLdapConfiguration> validNodes = new ArrayList<>();
        final Map<String, NscsServiceException> invalidNodes = new HashMap<>();
        for (final NodeSpecificLdapConfiguration ldapConfigurationPerNode : uniqueNodes) {
            validateLdapOnNode(ldapConfigurationPerNode, validNodes, invalidNodes);
        }

        return processCommand(command, commandType, uniqueNodes, validNodes, invalidNodes);
    }

    /**
     * Get proxy account name from the LDAP configuration.
     * 
     * @param ldapConfiguration
     *            the LDAP configuration
     * @return the proxy account name or null.
     * @throws InvalidNameException
     */
    private String getProxyAccountNameFromLdapConfiguration(final Map<String, Object> ldapConfiguration) {
        String proxyAccountName = null;
        final String bindDn = (String) ldapConfiguration.get(LdapConstants.BIND_DN);
        try {
            final LdapName ldapName = new LdapName(bindDn);
            for (Rdn rdn : ldapName.getRdns()) {
                if ("CN".equalsIgnoreCase(rdn.getType())) {
                    proxyAccountName = (String) rdn.getValue();
                    break;
                }
            }
        } catch (final InvalidNameException e) {
            final String errorMsg = String.format("Invalid bind DN [%s]", bindDn);
            nscsLogger.error(errorMsg, e);
        }
        return proxyAccountName;
    }

    /**
     * Checks duplicates in given input list, removing duplicates (all LDAP configuration parameters are equal) and throwing an exception if not equal
     * items related to same node (conflicting duplicates) are present.
     * 
     * @param nodeSpecificLdapConfigurationList
     *            the input list.
     * @return a new list without duplicates.
     * @throws {@link
     *             DuplicateNodeNamesException} if not equal items related to same node (conflicting duplicates) are present.
     */
    private List<NodeSpecificLdapConfiguration> buildListWithoutDuplicates(
            final List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationList) {
        nscsLogger.info("Command executed on unique node list of size {}", nodeSpecificLdapConfigurationList.size());
        // remove duplicates
        final Comparator<NodeSpecificLdapConfiguration> comparator = (NodeSpecificLdapConfiguration o1,
                NodeSpecificLdapConfiguration o2) -> (o1.compareUserDefinedLdapParams(o2));
        Set<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationSet = new TreeSet<>(comparator);
        nodeSpecificLdapConfigurationSet.addAll(nodeSpecificLdapConfigurationList);
        final List<NodeSpecificLdapConfiguration> uniqueNodes = new ArrayList<>(nodeSpecificLdapConfigurationSet);
        nscsLogger.info("Perform validation check on unique node list size {}", uniqueNodes.size());
        return uniqueNodes;
    }

    /**
     * Process command for the given nodes.
     * 
     * @param command
     *            the command.
     * @param commandType
     *            the command type.
     * @param uniqueNodes
     *            the given nodes.
     * @param validNodes
     *            the valid nodes.
     * @param invalidNodes
     *            the invalid nodes.
     * @return the command response.
     * @throws {@link
     *             LdapConfigureWfException} if error occurs while starting job.
     */
    private NscsCommandResponse processCommand(final LdapConfigurationCommand command, final LDAPConfigurationMode commandType,
            final List<NodeSpecificLdapConfiguration> uniqueNodes, final List<NodeSpecificLdapConfiguration> validNodes,
            final Map<String, NscsServiceException> invalidNodes) {
        nscsLogger.info("Adding LDAP job for [{}] of [{}] input nodes.", validNodes.size(), uniqueNodes.size());
        JobStatusRecord jobStatusRecord;
        String responseMessageWithJobId;

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(invalidNodes.size()));

        // Policy : the command is executed on valid nodes (if any) and, for each invalid node, a compliance with error details is returned.
        if (invalidNodes.isEmpty()) {
            nscsLogger.info("All of the given input nodes [{}] are valid. An LDAP job needs to be added.", uniqueNodes.size());
            try {
                jobStatusRecord = nscsJobCacheHandler.insertJob(commandType.getNscsCommandType());
                executeLdapWfs(commandType, uniqueNodes, jobStatusRecord);
                responseMessageWithJobId = String.format(commandType.getExecutedMessage(), jobStatusRecord.getJobId().toString());
                nscsLogger.commandHandlerFinishedWithSuccess(command, "workflows started successfully for all valid nodes");
                return ldapConfigurationResponseObjectBuilder.buildSuccessResponse(responseMessageWithJobId);
            } catch (final NscsServiceException e) {
                final String errorMsg = String.format("Exception occurred starting job for LDAP configuration: %s.",
                        NscsLogger.stringifyException(e));
                nscsLogger.error(errorMsg, e);
                nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
                throw new LdapConfigureWfException(commandType.getFailedMessage());
            }
        } else {
            if (validNodes.isEmpty()) {
                nscsLogger.error("All of the given input nodes [{}] are invalid. No LDAP job needs to be added.", uniqueNodes.size());
                nscsLogger.commandHandlerFinishedWithError(command, "no workflows started for all invalid nodes");
                return ldapConfigurationResponseObjectBuilder.buildErrorResponse(commandType.getAllFailedMessage(), invalidNodes);
            } else {
                nscsLogger.info("Only [{}] of the given input nodes [{}] are valid. An LDAP job needs to be added.", validNodes.size(),
                        uniqueNodes.size());
                try {
                    jobStatusRecord = nscsJobCacheHandler.insertJob(commandType.getNscsCommandType());
                    executeLdapWfs(commandType, validNodes, jobStatusRecord);
                    responseMessageWithJobId = String.format(commandType.getPartiallyExecutedMessage(), jobStatusRecord.getJobId().toString());
                    nscsLogger.commandHandlerFinishedWithSuccess(command, "workflows started successfully for some valid nodes");
                    return ldapConfigurationResponseObjectBuilder.buildErrorResponse(responseMessageWithJobId, invalidNodes);
                } catch (final NscsServiceException e) {
                    final String errorMsg = String.format("Exception occurred starting job for LDAP configuration: %s.",
                            NscsLogger.stringifyException(e));
                    nscsLogger.error(errorMsg, e);
                    nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
                    throw new LdapConfigureWfException(commandType.getFailedMessage());
                }
            }
        }
    }

    /**
     * Validate given node for LDAP configuration and update accordingly the valid/invalid nodes list/map.
     * 
     * @param ldapConfigurationPerNode
     *            the node LDAP configuration.
     * @param validNodes
     *            the list of valid nodes.
     * @param invalidNodes
     *            the map of invalid nodes.
     */
    private void validateLdapOnNode(final NodeSpecificLdapConfiguration ldapConfigurationPerNode,
            final List<NodeSpecificLdapConfiguration> validNodes, final Map<String, NscsServiceException> invalidNodes) {

        try {
            userProvidedLdapConfigurationValidator.validate(ldapConfigurationPerNode);
            validNodes.add(ldapConfigurationPerNode);
        } catch (final InvalidNodeNameException | InvalidFileContentException | CouldNotReadMoAttributeException | NodeNotSynchronizedException
                | UnsupportedNodeTypeException ldapValidationException) {
            final String fdn = ldapConfigurationPerNode.getNodeFdn();
            final NodeReference nodeRef = new NodeRef(fdn);
            final String errorMsg = String.format("Exception occurred validating node %s for LDAP configuration: %s.", nodeRef.getName(),
                    NscsLogger.stringifyException(ldapValidationException));
            nscsLogger.error(errorMsg, ldapValidationException);
            invalidNodes.put(nodeRef.getFdn(), ldapValidationException);
        }
    }

    /**
     * Execute the LDAP workflows for the specified nodes.
     * 
     * @param commandType
     *            the type of LDAP command.
     * @param uniqueNodes
     *            the list of nodes without duplicate.
     * @param jobStatusRecord
     *            the job status record.
     */
    private void executeLdapWfs(final LDAPConfigurationMode commandType, final List<NodeSpecificLdapConfiguration> uniqueNodes,
            JobStatusRecord jobStatusRecord) {
        if (LDAPConfigurationMode.LDAP_CONFIGURE.equals(commandType)) {
            nscsCommandManager.executeConfigureLdapWfs(uniqueNodes, jobStatusRecord);
        } else if (LDAPConfigurationMode.LDAP_RECONFIGURE.equals(commandType)) {
            nscsCommandManager.executeReconfigureLdapWfs(uniqueNodes, jobStatusRecord);
        } else {
            nscsCommandManager.executeRenewLdapWfs(uniqueNodes, jobStatusRecord);
        }
    }

    public enum LDAPConfigurationMode {
        LDAP_CONFIGURE ("configure", "Successfully started a job for configure LDAP operation. Perform 'secadm job get -j %s' to get progress info.", "Error during Ldap Configure operation.", NscsCommandType.LDAP_CONFIGURATION),
        LDAP_RECONFIGURE ("reconfigure", "Successfully started a job for reconfigure LDAP operation. Perform 'secadm job get -j %s' to get progress info.", "Error during Ldap Reconfigure operation.", NscsCommandType.LDAP_RECONFIGURATION),
        LDAP_RENEW("renew","Successfully started a job for renew LDAP operation. Perform 'secadm job get -j %s' to get progress info.","Error during Ldap Renew operation.", NscsCommandType.LDAP_RENEW);
        private final String ldapName;
        private final String ldapExecutedMessage;
        private final String ldapFailedMessage;
        private final NscsCommandType ldapNscsCommandType;

        LDAPConfigurationMode(String name, String executedMessage, String failedMessage, NscsCommandType nscsCommandType) {
            this.ldapName = name;
            this.ldapExecutedMessage = executedMessage;
            this.ldapFailedMessage = failedMessage;
            this.ldapNscsCommandType = nscsCommandType;
        }

        public String getName() {
            return ldapName;
        }

        public String getExecutedMessage() {
            return ldapExecutedMessage;
        }

        public String getPartiallyExecutedMessage() {
            return String.format(NscsErrorCodes.OPERATION_WITH_SOME_INVALID_NODES_FORMAT, ldapExecutedMessage);
        }

        public String getFailedMessage() {
            return ldapFailedMessage;
        }

        public String getAllFailedMessage() {
            return String.format(NscsErrorCodes.OPERATION_WITH_ALL_INVALID_NODES_FORMAT, ldapFailedMessage);
        }

        public NscsCommandType getNscsCommandType() {
            return ldapNscsCommandType;
        }
    }

}
