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
import com.ericsson.nms.security.nscs.api.command.types.GetTrustCertInstallStateCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetTrustInstallStateResponseBuilder;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.TrustValidator;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;

/**
 * <p>
 * Lists the following info of the requested nodes - certEnrollState - serialNumber - issuer
 * </p>
 * 
 */
@UseValidator({ NoDuplNodeNamesAllowedValidator.class })
@CommandType(NscsCommandType.GET_TRUST_CERT_INSTALL_STATE)
@Local(CommandHandlerInterface.class)
public class GetTrustCertInstallStateHandler implements CommandHandler<GetTrustCertInstallStateCommand>, CommandHandlerInterface {

    private static final String GET_TRUST_CERT_INSTALL_STATE_NOT_EXECUTED = "Trust get command not executed as some provided nodes are invalid.";
    private static final String CERT_TYPE_WARNING_MSG = " [WARNING : The command with --certtype option will be deprecated in the future. Use --trustcategory property instead of --certtype]";

    @Inject
    private Logger logger;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @EJB
    private NscsCommandManager commandManager;

    @Inject
    private TrustValidator trustValidator;

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
    public NscsCommandResponse process(final GetTrustCertInstallStateCommand command, final CommandContext context) throws NscsServiceException {
        logger.info("start: process task for GetTrustCertInstallStateCommand");
        final Boolean hasCertTypeProperty = command.getProperties().containsKey(GetTrustCertInstallStateCommand.CERT_TYPE_PROPERTY);
        final NscsGetTrustInstallStateResponseBuilder responseBuilder = new NscsGetTrustInstallStateResponseBuilder();
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        String trustCategory = command.getTrustCategory();
        if (command.getCertType() != null) {
            trustCategory = command.getCertType();
        }

        logger.info("Fetching TrustCertInstallState parameters for nodes: {}", context.getValidNodes());

        trustValidator.validateCommandForCertTypeAndTrustCategory(command.getCertType(), trustCategory);
        trustValidator.validateInputNodes(nscsInputNodeRetrievalUtility.getNodeReferenceList(command), trustCategory, validNodesList,
                invalidNodesErrorMap, false);

        if (invalidNodesErrorMap.isEmpty()) {
            logger.info("Preparing response for success scenario");
            responseBuilder.addHeader();
            for (final NodeReference nodeRef : validNodesList) {
                final CertStateInfo trustCertificateInfo = moGetServiceFactory.getTrustCertificateStateInfo(nodeRef, trustCategory);
                logger.info("CertStateInfo is {}", trustCertificateInfo);
                responseBuilder.addRows(trustCertificateInfo);

            }
        } else {
            if (!validNodesList.isEmpty()) {
                logger.info("Preparing response for partial success scenario");
                responseBuilder.addHeader();
                for (final NodeReference nodeRef : validNodesList) {
                    final CertStateInfo trustCertificateInfo = moGetServiceFactory.getTrustCertificateStateInfo(nodeRef, trustCategory);
                    logger.info("CertStateInfo is {}", trustCertificateInfo);
                    responseBuilder.addRows(trustCertificateInfo);
                }

                if (!invalidNodesErrorMap.isEmpty()) {
                    responseBuilder.addErrorRows(invalidNodesErrorMap);
                }

            } else {
                logger.info("Preparing response for all invalid nodes scenario");
                if (!invalidNodesErrorMap.isEmpty()) {
                    for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
                        context.setAsInvalidOrFailed(entry.getKey(), entry.getValue());
                    }
                    if (hasCertTypeProperty) {
                        return NscsCommandResponse.message(GET_TRUST_CERT_INSTALL_STATE_NOT_EXECUTED + CERT_TYPE_WARNING_MSG
                                + ". Details are given below :");
                    }
                    return NscsCommandResponse.message(GET_TRUST_CERT_INSTALL_STATE_NOT_EXECUTED);
                }
            }
        }
        if (hasCertTypeProperty) {
            responseBuilder.getResponse().setAdditionalInformation(CERT_TYPE_WARNING_MSG);
        }
        return responseBuilder.getResponse();
    }
}
