package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetResponseBuilder;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetSecurityLevelConstants;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetSecurityLevelDetails;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.CppGetSecurityLevelValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.utilities.NormalizedNodeUtils;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * <p>
 * Lists the security level of the requested nodes
 * </p>
 *
 * Created by emaynes on 02/05/2014.
 */
@CommandType(NscsCommandType.CPP_GET_SL)
@Local(CommandHandlerInterface.class)
public class CppGetSecurityLevelHandler implements CommandHandler<CppSecurityLevelCommand>, CommandHandlerInterface {

    public static final String NODE_NAME_HEADER = "Node Name";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String UNDEFINED_SECURITY_LEVEL = "undefined";
    public static final String ERROR_DETAILS_HEADER = "Error Details";
    public static final String NA = "NA";

    @Inject
    private CppGetResponseBuilder cppGetResponseBuilder;

    @Inject
    private CppGetSecurityLevelValidator cppGetSecurityLevelValidator;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NormalizedNodeUtils normalizedNodeUtils;

    /**
     *
     * @param nodeCommand
     *            NscsNodeCommand
     * @param context
     *            a CommandContext instance
     * @return NscsNameValueCommandResponse instance with node names and respective security level.
     * @throws NscsServiceException
     */
    @Override
    public NscsCommandResponse process(final CppSecurityLevelCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);
        nscsLogger.info("Fetching security level for nodes: {}", command.getNodes());
        List<NormalizableNodeReference> normNodesList = new ArrayList<NormalizableNodeReference>();
        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesError = new HashMap<>();
        final List<NodeReference> uniqueNodes = new ArrayList<NodeReference>(new HashSet<NodeReference>(command.getNodes()));
        boolean isAllNodes = true;
        boolean isAllNodesInvalid = false;
        List<CppGetSecurityLevelDetails> securityLevelAndAAStatusDetails = new ArrayList<>();
        if (!command.getNodes().isEmpty()) {
            normNodesList.addAll(normalizedNodeUtils.getNormalizedNodes(uniqueNodes, invalidNodesError));
            isAllNodesInvalid = (uniqueNodes.size() == invalidNodesError.size());
            isAllNodes = false;
        }
        if (isAllNodesInvalid) {
            return cppGetResponseBuilder.buildResponse(securityLevelAndAAStatusDetails, invalidNodesError);
        }

        final String levelValue = command.getSecurityLevel() == null ? null : SecurityLevel.getSecurityLevel(command.getSecurityLevel()).getLevel();
        nscsLogger.info(" Security Level value is: {}", levelValue);

        final CmResponse cmResponse = reader.getMOAttribute(normNodesList, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), ModelDefinition.Security.OPERATIONAL_SECURITY_LEVEL,
                levelValue);

        if (cmResponse.getCmObjects().isEmpty()) {
            if (levelValue == null) {
                nscsLogger.commandHandlerFinishedWithError(command,
                        CppGetSecurityLevelConstants.ERROR_IN_READING_OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_FOR_THE_NODES);
                return NscsCommandResponse.message(CppGetSecurityLevelConstants.ERROR_IN_READING_OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_FOR_THE_NODES);
            } else {
                nscsLogger.commandHandlerFinishedWithError(command, NscsErrorCodes.NO_NODES_FOUND_AT_REQUESTED_SECURITY_LEVEL);
                return NscsCommandResponse.message(NscsErrorCodes.NO_NODES_FOUND_AT_REQUESTED_SECURITY_LEVEL);
            }
        }
        if (isAllNodes) {
            normNodesList = normalizedNodeUtils.getNormalizedNodesFromCmResponse(cmResponse);
            nscsLogger.info("The node list obtained from the all node is : [{}]", normNodesList);
        }

        cppGetSecurityLevelValidator.validateNodes(normNodesList, validNodesList, invalidNodesError);

        if (!validNodesList.isEmpty()) {
            securityLevelAndAAStatusDetails = getSecurityLevelValues(validNodesList, cmResponse, invalidNodesError);
        }
        nscsLogger.commandHandlerFinishedWithSuccess(command, "Fetched Security Levels and Local AA status for Provided nodes");
        return cppGetResponseBuilder.buildResponse(securityLevelAndAAStatusDetails, invalidNodesError);
    }

    public static String formatLevel(final String level) {
        String formatted = "";
        if (level != null) {
            formatted = level.replace('_', ' ').toLowerCase();
        }
        return formatted;
    }

    private Map<String, String> getSecurityLevel(final CmResponse cmresponse, final Map<NodeReference, NscsServiceException> invalidNodesError) {
        final Map<String, String> securityLevelDetails = new LinkedHashMap<String, String>();
        for (final CmObject cmObject : cmresponse.getCmObjects()) {
            final String cmObjectFdn = cmObject.getFdn();
            String secLevel = CppGetSecurityLevelConstants.UNDEFINED_SECURITY_LEVEL;

            if (cmObjectFdn == null || !cppGetSecurityLevelValidator.isValidNode(cmObjectFdn, invalidNodesError)) {
                continue;
            }
            try {
                secLevel = (String) cmObject.getAttributes().get(ModelDefinition.Security.OPERATIONAL_SECURITY_LEVEL);
            } catch (final NullPointerException e) {
                nscsLogger.error("No cmObjects returned from the DPS, undefined Security Level");
            }
            securityLevelDetails.put(cppGetSecurityLevelValidator.extractNodeName(cmObjectFdn), formatLevel(secLevel));
        }
        return securityLevelDetails;
    }

    private List<CppGetSecurityLevelDetails> getSecurityLevelValues(final List<NormalizableNodeReference> validNodesList, final CmResponse cmResponse,
            final Map<NodeReference, NscsServiceException> invalidNodesError) {
        final List<CppGetSecurityLevelDetails> securityLevelAndAAStatusDetails = new ArrayList<>();
        final Map<String, String> securityLevelValues = getSecurityLevel(cmResponse, invalidNodesError);
        for (final NormalizableNodeReference normNode : validNodesList) {
            final String mirrorRootFdn = normNode.getFdn();
            final Mo secuirtyMo = Model.ME_CONTEXT.managedElement.systemFunctions.security;
            final Map<String, Object> attributes = new HashMap<>();
            String securityCapabilityFdn = null;
            if (isUserAuthenticationAndAuthorizationSupported(normNode)) {
                final String[] requestedAttrs = { ModelDefinition.Security.USER_AUTHENTICATION_AND_AUTHORIZATION };
                securityCapabilityFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, secuirtyMo, attributes, requestedAttrs);
            }
            String localRabcStatus = (securityCapabilityFdn == null || securityCapabilityFdn.isEmpty() || attributes == null || attributes.isEmpty())
                    ? CppGetSecurityLevelConstants.NA : ((String) attributes.get(ModelDefinition.Security.USER_AUTHENTICATION_AND_AUTHORIZATION));
            String securityLevelValue = securityLevelValues.get(normNode.getName()) == null || securityLevelValues.get(normNode.getName()).isEmpty()
                    ? CppGetSecurityLevelConstants.NA : securityLevelValues.get(normNode.getName());

            nscsLogger.info(" localAAStatus {} and security level {} for the node {}", localRabcStatus, securityLevelValue, mirrorRootFdn);

            switch (localRabcStatus) {
            case CppGetSecurityLevelConstants.LOCALAADATABASE:
                localRabcStatus = CppGetSecurityLevelConstants.ACTIVATED;
                break;
            case CppGetSecurityLevelConstants.NODE_PASSPHRASE:
                localRabcStatus = CppGetSecurityLevelConstants.DEACTIVATED;
                break;
            case CppGetSecurityLevelConstants.LOCALAADATABASEUNCONFIRMED:
                localRabcStatus = CppGetSecurityLevelConstants.UNCONFIRMED;
                break;
            default:
                localRabcStatus = CppGetSecurityLevelConstants.NA;
                break;
            }
            final CppGetSecurityLevelDetails cppGetSecurityLevelDetails = new CppGetSecurityLevelDetails();
            cppGetSecurityLevelDetails.setSecurityLevelStatus(securityLevelValue);
            cppGetSecurityLevelDetails.setLocalRbacStatus(localRabcStatus);
            cppGetSecurityLevelDetails.setNodeName(normNode.getName());
            securityLevelAndAAStatusDetails.add(cppGetSecurityLevelDetails);
        }
        return securityLevelAndAAStatusDetails;
    }

    /**
     * Gets from Capability Model if userAuthenticationAndAuthorization attribute exists in Security MO.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if userAuthenticationAndAuthorization attribute exists in Security MO, false otherwise.
     */
    private boolean isUserAuthenticationAndAuthorizationSupported(final NormalizableNodeReference normNodeRef) {
        final String targetCategory = normNodeRef.getTargetCategory();
        final String targetType = normNodeRef.getNeType();
        final String targetModelIdentity = normNodeRef.getOssModelIdentity();
        final Mo mo = nscsCapabilityModelService.getMirrorRootMo(normNodeRef);
        final Mo securityMo = ((CppManagedElement) mo).systemFunctions.security;
        nscsLogger.info(" Name space for the MO : {} and Security Mo type is {}", securityMo.namespace(), securityMo.type());
        return nscsModelServiceImpl.isMoAttributeExists(targetCategory, targetType, targetModelIdentity,
               securityMo.namespace(), securityMo.type(), CppGetSecurityLevelConstants.USER_AUTHENTICATION_AND_AUTHORIZATION);
    }
}
