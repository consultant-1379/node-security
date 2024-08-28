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
package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.CppIpSecStatusCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.IpSecNodeValidatorUtility;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.nms.security.nscs.utilities.Constants;

import javax.ejb.Local;
import javax.inject.Inject;

import java.util.*;

/**
 * Created with IntelliJ IDEA. User: ediniku Date: 25/09/14 Time: 14:21
 * 
 * Module to be called by CppIpSecHandler whenever "secadm ipsec --status" command is executed.
 */

@CommandType(NscsCommandType.CPP_IPSEC_STATUS)
@Local(CommandHandlerInterface.class)
public class CppIpSecStatusHandler implements CommandHandler<CppIpSecStatusCommand>, CommandHandlerInterface {

    public static final String ACTIVATED = "ACTIVATED";
    public static final String DEACTIVATED = "DEACTIVATED";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String INVALID_NODE = "INVALID NODE";
    public static final String NOT_APPLICABLE = "NA";
    public static final String[] STATUS_HEADER = new String[] { "Node Name", "IPsec OAM", "IPsec Traffic", "Error Code", "Error Details",
            "Suggested Solution" };
    public static final String TABLE_NAME_NODE_SECURITY_IPSEC = "Node IPsec Status";
    private static final int NO_OF_COLUMNS = 5; //5 attributes to display in the screen of ENM UI other than Node name

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CppIpSecStatusUtility cppIpSecStatusUtility;

    @Inject
    private IpSecNodeValidatorUtility ipSecNodeValidatorUtility;

    @Inject
    private FileUtil fileUtil;

    /**
     * Processes status command to find status of O&M and Traffic NscsNameMultipleValueCommandResponse
     * 
     * @param command
     *            a NscsPropertyCommand of subclass
     * @param context
     *            current command execution context
     * @return
     * @throws NscsServiceException
     */
    @Override
    public NscsCommandResponse process(final CppIpSecStatusCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);
    	nscsLogger.debug("List of valid nodes [{}]", context.getValidNodes());
        final Map<String, Object> properties = command.getProperties();
        final String fileName = (String) properties.get(Constants.FILE_NAME);

        if (fileName != null && !fileUtil.isValidFileExtension(fileName, Constants.FILE_EXT_TXT)) {
            throw new CommandSyntaxException(NscsErrorCodes.INVALID_FILE_TYPE_NOT_TXT);
        }

        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final List<NodeReference> inputNodes = new ArrayList<NodeReference>(command.getNodes());
        final List<NodeReference> uniqueNodes = new ArrayList<NodeReference>(new HashSet<NodeReference>(inputNodes));
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);

        nscsLogger.debug("Number of unique nodes {}", uniqueNodes.size());

        ipSecNodeValidatorUtility.validateNodesForIpsecStatus(uniqueNodes, validNodesList, invalidNodesErrorMap);

        final String additionalInformation = ((validNodesList.size() > 0) ? "IPsec Status details for " + validNodesList.size() + " valid node(s)  "
                : "All the given node(s) are invalid, Error Detail(s) for respective node(s)")
                + ((invalidNodesErrorMap.size() > 0 && validNodesList.size() > 0) ? " and Error Details for " + invalidNodesErrorMap.size()
                        + " invalid node(s) " : "") + " is/are listed below.";
        response.setAdditionalInformation(additionalInformation);

        prepareResponseHeader(response);

        if (validNodesList.size() > 0) {
            for (final NodeReference nodeRef : validNodesList) {
                final String nodeName = nodeRef.getName();
                final String featureState = cppIpSecStatusUtility.getIpSecFeatureState(nodeRef);
                if (featureState.equals(UNKNOWN)) {
                	nscsLogger.debug("Both OAM and Traffic state is [UNKNOWN] state on Node [{}]", nodeName);
                    addOMandTrafficStatusResponse(response, nodeName, UNKNOWN, UNKNOWN);
                    continue;
                }
                //TODO - to be updated and verified as part of MR 55921
                cppIpSecStatusUtility.setConfigurationRequirement(command.hasConfiguration());

                final String isOmActivated = cppIpSecStatusUtility.isOMActivated(nodeRef, featureState) ? ACTIVATED : DEACTIVATED;
                final String isTrafficActivated = cppIpSecStatusUtility.isTrafficActivated(nodeRef, featureState) ? ACTIVATED : DEACTIVATED;
                nscsLogger.debug("Status of OAM and Traffic on Node [{}] OAM [{}] Traffic [{}]", nodeName, isOmActivated, isTrafficActivated);
                if (command.hasConfiguration() && cppIpSecStatusUtility.getConfigurationInfo() != null) {
                    final String configurationType = cppIpSecStatusUtility.getConfigurationInfo().getOmConfigurationType();
                    addOMandTrafficStatusResponse(response, nodeName, isOmActivated + configurationType, isTrafficActivated);
                } else {
                    addOMandTrafficStatusResponse(response, nodeName, isOmActivated, isTrafficActivated);
                }
            }

        }
        if (invalidNodesErrorMap.size() > 0) {
            final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
            for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
                response.add(entry.getKey().getFdn(), new String[] { NOT_APPLICABLE, NOT_APPLICABLE, "" + entry.getValue().getErrorCode(),
                        entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
            }
        }

        nscsLogger.debug("IPsec Status Response [{}]", response.toString());
        nscsLogger.commandHandlerFinishedWithSuccess(command, "IPsec get status command has been executed successfully");
        nscsLogger.info("Finished processing get IPsec status command.");
        return response;
    }

    /**
     * Adding row to response
     * 
     * @param response
     * @param nodeName
     * @param OM_STATUS
     * @param TRAFFIC_STATUS
     */
    private void addOMandTrafficStatusResponse(final NscsNameMultipleValueCommandResponse response, final String nodeName, final String OAM_STATUS,
            final String TRAFFIC_STATUS) {
        response.add(nodeName, new String[] { OAM_STATUS, TRAFFIC_STATUS, NOT_APPLICABLE, NOT_APPLICABLE, NOT_APPLICABLE });
    }

    /**
     * Prepares response header to be displayed on the ENM UI
     * 
     * @param response
     */
    private void prepareResponseHeader(final NscsNameMultipleValueCommandResponse response) {
        response.setResponseHeaderTitle(TABLE_NAME_NODE_SECURITY_IPSEC);
        response.add(STATUS_HEADER[0], Arrays.copyOfRange(STATUS_HEADER, 1, STATUS_HEADER.length));

    }

}
