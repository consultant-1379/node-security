package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.GetCertEnrollStateCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetCertEnrollStateResponseBuilder;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.oss.services.security.nscs.command.util.CertificateTypeHelper;

/**
 * <p>
 * Lists the following info of the requested nodes - certEnrollState - serialNumber - issuer
 * </p>
 *
 */
@UseValidator({ NoDuplNodeNamesAllowedValidator.class })
@CommandType(NscsCommandType.GET_CERT_ENROLL_STATE)
@Local(CommandHandlerInterface.class)
public class GetCertEnrollStateHandler implements CommandHandler<GetCertEnrollStateCommand>, CommandHandlerInterface {

    public static final String GET_CERT_ENROLL_STATE_NOT_EXECUTED = "Certificate get command not executed as some provided nodes are invalid. Details are given below :";

    @Inject
    private Logger logger;

    @Inject
    private NscsCapabilityModelService capabilityModel;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @EJB
    private NscsCommandManager commandManager;

    /**
     *
     * @param nodeCommand
     *            NscsNodeCommand
     * @param context
     *            a CommandContext instance
     * @return NscsNameValueCommandResponse instance with node names.
     * @throws NscsServiceException
     */
    @Override
    public NscsCommandResponse process(final GetCertEnrollStateCommand command, final CommandContext context) throws NscsServiceException {

        final String certType = command.getCertType();
        final List<String> validCertTypes = CertificateTypeHelper.getValidCertificateTypes();
        if (certType == null || !validCertTypes.contains(certType)) {
            logger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
            final String errmsg = String.format(" Invalid value for argument %s. Requested value is %s. Accepted values are %s",
                    GetCertEnrollStateCommand.CERT_TYPE_PROPERTY, certType, validCertTypes);
            logger.error(errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }

        final NscsGetCertEnrollStateResponseBuilder responseBuilder = new NscsGetCertEnrollStateResponseBuilder();

        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final Map<String, String[]> invalidDynamicNodesMap = new HashMap<>();

        final List<NodeReference> inputNodes = command.getNodes();
        logger.info("Fetching certEnrollState parameters for nodes[{}] certType[{}]", inputNodes, certType);
        logger.info("Number of input nodes[{}]", inputNodes.size());

        final boolean areInputNodesValid = commandManager.validateNodesGetCertEnrollTrustInstallState(NscsCapabilityModelService.CERTIFICATE_COMMAND,
                certType, inputNodes, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap);
        logger.info("areInputNodesValid[{}]", areInputNodesValid);
        if (areInputNodesValid || (!invalidDynamicNodesMap.isEmpty() && invalidDynamicNodesMap.size() == invalidNodesErrorMap.size())) {
            logger.info("Inside areInputNodesValid[{}]", areInputNodesValid);
            if (validNodesList != null && !validNodesList.isEmpty()) {
                responseBuilder.addHeader();
                logger.info("Adding title to table");
            }
            logger.info("After validNodesList[{}]", validNodesList);
            for (final NodeReference nodeRef : validNodesList) {
                final CertStateInfo getCertStateInfo = moGetServiceFactory.getCertificateIssueStateInfo(nodeRef, command.getCertType());
                logger.info("CertStateInfo is {}", getCertStateInfo);
                responseBuilder.addRow(getCertStateInfo);
            }

            // Print Nodes in dynamic error map
            if (!invalidDynamicNodesMap.isEmpty()) {
                responseBuilder.addDynamicErrorSection(invalidDynamicNodesMap);
            }
        } else {
            if (!invalidNodesErrorMap.isEmpty()) {
                for (final Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
                    context.setAsInvalidOrFailed(entry.getKey(), entry.getValue());
                }
                return NscsCommandResponse.message(GET_CERT_ENROLL_STATE_NOT_EXECUTED);
            }
        }

        return responseBuilder.getResponse();
    }
}
