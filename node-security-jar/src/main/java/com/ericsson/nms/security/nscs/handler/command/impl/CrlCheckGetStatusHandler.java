/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
import java.util.Arrays;
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
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CrlCheckCommand;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * <p>
 * Initiates the process to get the crlcheck status on a set of nodes.
 * </p>
 *
 * @author xchowja
 *
 */
@UseValidator({ StarIsNotAllowedValidator.class })
@CommandType(NscsCommandType.CRL_CHECK_GET_STATUS)
@Local(CommandHandlerInterface.class)
public class CrlCheckGetStatusHandler implements CommandHandler<CrlCheckCommand>, CommandHandlerInterface {

    @Inject
    private NscsCMReaderService nscsCMReaderService;

    @EJB
    private NscsCommandManager commandManager;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Inject
    private NscsLogger nscsLogger;

    public static final String[] CRL_CHECK_HEADER_IPSEC = new String[] { "Node Name", "IPSEC CRL CHECK" };
    public static final String[] CRL_CHECK_HEADER_OAM = new String[] { "Node Name", "OAM CRL CHECK" };
    public static final String[] CRL_CHECK_ERROR_HEADER_OAM = new String[] { "Node Name", "OAM CRL CHECK", "Error Code", "Error Detail",
            "Suggested Solution" };
    public static final String[] CRL_CHECK_ERROR_HEADER_IPSEC = new String[] { "Node Name", "IPSEC CRL CHECK", "Error Code", "Error Detail",
            "Suggested Solution" };

    private static final int NO_OF_COLUMNS = 1;
    private static final int ERROR_HEADER_NO_OF_COLUMNS = 4;
    private static final String ERROR = "ERROR";
    private static final String NA = "NA";

    @Override
    public NscsCommandResponse process(final CrlCheckCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);
        final long startTime = System.currentTimeMillis();
        final String certType = command.getCertType();
        final Map<String, String> cRLCheckStatusMap = new HashMap<>();
        nscsLogger.debug("certType[{}]", certType);

        if (!commandManager.validateCertTypeValue(certType)) {
            nscsLogger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
            final String errmsg = String.format(" Invalid argument for parameter %s",
                    CrlCheckCommand.CERT_TYPE_PROPERTY + ". Accepted arguments are " + commandManager.getValidCertificateTypes());
            nscsLogger.error(errmsg);
            nscsLogger.commandHandlerFinishedWithError(command, errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final Map<String, String[]> invalidDynamicNodesMap = new HashMap<>();
        final List<NodeReference> inputNodes = command.getNodes();
        final List<NodeReference> uniqueNodes = new ArrayList<NodeReference>(new HashSet<NodeReference>(inputNodes));
        nscsLogger.debug("Number of input nodes {}", uniqueNodes.size());
        final boolean areInputNodesValid = commandManager.validateNodesForCrlCheck(inputNodes, certType, validNodesList, invalidNodesErrorMap,
                invalidDynamicNodesMap, true);
        NscsNameMultipleValueCommandResponse response = null;
        boolean cRLCheckStatusPartialResponse = false;

        if (areInputNodesValid) {
            nscsLogger.debug("All of the given input nodes are Valid [{}]", validNodesList.size());

            for (final NodeReference inputNode : validNodesList) {
                final NormalizableNodeReference normNode = nscsCMReaderService.getNormalizableNodeReference(inputNode);
                cRLCheckStatusMap.put(inputNode.getFdn(), getCRLCheckstatus(normNode, certType));
            }

            response = buildCRLCheckStatusResponseSuccess(cRLCheckStatusMap, certType);

        } else {

            if (!validNodesList.isEmpty()) {
                cRLCheckStatusPartialResponse = true;

                nscsLogger.info("Number of Valid Nodes are :[{}] and cRLCheckStatusPartialResponse is:[{}]", validNodesList.size(),
                        cRLCheckStatusPartialResponse);

                for (final NodeReference inputNode : validNodesList) {
                    final NormalizableNodeReference normNode = nscsCMReaderService.getNormalizableNodeReference(inputNode);
                    cRLCheckStatusMap.put(inputNode.getFdn(), getCRLCheckstatus(normNode, certType));
                }

                if (!invalidNodesErrorMap.isEmpty() || !invalidDynamicNodesMap.isEmpty()) {
                    response = buildCRLCheckStatusResponseFailure(cRLCheckStatusMap, certType, invalidNodesErrorMap, cRLCheckStatusPartialResponse);
                }

            } else {
                nscsLogger.info("Number of Invalid Nodes are: [{}] and cRLCheckStatusPartialResponse is:[{}]", invalidNodesErrorMap.size(),
                        cRLCheckStatusPartialResponse);

                response = buildCRLCheckStatusResponseFailure(cRLCheckStatusMap, certType, invalidNodesErrorMap, cRLCheckStatusPartialResponse);

            }
        }
        final long endTime = System.currentTimeMillis();
        nscsLogger.info("Total elapsed time for CrlCheck Get Status Handler: " + String.format("%.3f", (endTime - startTime) / 1000f));
        nscsLogger.commandHandlerFinishedWithSuccess(command, "workflow started successfully for any instance");
        return response;
    }

    private String getCRLCheckstatus(final NormalizableNodeReference normNode, final String certType) {

        nscsLogger.info("Start of getCRLCheckstatus method: normNodeFdn[{}],certType[{}]", normNode.getFdn(), certType);

        final String crlCheckStatus = moGetServiceFactory.getCrlCheckStatus(normNode, certType);

        nscsLogger.info("End of getCRLCheckstatus method: crlCheckStatus[{}]", crlCheckStatus);

        return crlCheckStatus;
    }

    private NscsNameMultipleValueCommandResponse buildCRLCheckStatusResponseSuccess(final Map<String, String> cRLCheckStatusMap,
            final String certType) {
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);

        nscsLogger.info("Start of buildCRLCheckStatusResponseSuccess method: cRLCheckStatusMap size[{}],certType[{}]", cRLCheckStatusMap.size(),
                certType);

        if (CertificateType.OAM.name().equals(certType)) {
            response.add(CRL_CHECK_HEADER_OAM[0], Arrays.copyOfRange(CRL_CHECK_HEADER_OAM, 1, CRL_CHECK_HEADER_OAM.length));
            final Set<java.util.Map.Entry<String, String>> entrySet = cRLCheckStatusMap.entrySet();
            for (final java.util.Map.Entry<String, String> entry : entrySet) {
                response.add(entry.getKey(), new String[] { entry.getValue() });

            }

        } else if (CertificateType.IPSEC.name().equals(certType)) {
            response.add(CRL_CHECK_HEADER_IPSEC[0], Arrays.copyOfRange(CRL_CHECK_HEADER_IPSEC, 1, CRL_CHECK_HEADER_IPSEC.length));
            final Set<java.util.Map.Entry<String, String>> entrySet = cRLCheckStatusMap.entrySet();
            for (final java.util.Map.Entry<String, String> entry : entrySet) {
                response.add(entry.getKey(), new String[] { entry.getValue() });
            }
        }

        nscsLogger.info("End of buildCRLCheckStatusResponseSuccess method");

        return response;

    }

    private NscsNameMultipleValueCommandResponse buildCRLCheckStatusResponseFailure(final Map<String, String> cRLCheckStatusMap,
            final String certType, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final boolean cRLCheckPartialResponse) {

        nscsLogger.info("Start of buildCRLCheckStatusResponseFailure method: cRLCheckStatusMap[{}],certType[{}],invalidNodesErrorMap size [{}]",
                cRLCheckStatusMap, certType, invalidNodesErrorMap.size());

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(ERROR_HEADER_NO_OF_COLUMNS);

        if (CertificateType.OAM.name().equals(certType)) {
            response.add(CRL_CHECK_ERROR_HEADER_OAM[0], Arrays.copyOfRange(CRL_CHECK_ERROR_HEADER_OAM, 1, CRL_CHECK_ERROR_HEADER_OAM.length));
            if (cRLCheckPartialResponse) {
                final Set<java.util.Map.Entry<String, String>> entrySet = cRLCheckStatusMap.entrySet();
                for (final java.util.Map.Entry<String, String> entry : entrySet) {
                    response.add(entry.getKey(), new String[] { entry.getValue(), NA, NA, NA });

                }
            }

        } else if (CertificateType.IPSEC.name().equals(certType)) {
            response.add(CRL_CHECK_ERROR_HEADER_IPSEC[0], Arrays.copyOfRange(CRL_CHECK_ERROR_HEADER_IPSEC, 1, CRL_CHECK_ERROR_HEADER_IPSEC.length));
            if (cRLCheckPartialResponse) {
                final Set<java.util.Map.Entry<String, String>> entrySet = cRLCheckStatusMap.entrySet();
                for (final java.util.Map.Entry<String, String> entry : entrySet) {
                    response.add(entry.getKey(), new String[] { entry.getValue(), NA, NA, NA });
                }
            }
        }

        final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
        for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
            response.add(entry.getKey().getFdn(), new String[] { ERROR, "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(),
                    entry.getValue().getSuggestedSolution() });
        }

        nscsLogger.info("End of buildCRLCheckStatusResponseFailure method");

        return response;

    }
}