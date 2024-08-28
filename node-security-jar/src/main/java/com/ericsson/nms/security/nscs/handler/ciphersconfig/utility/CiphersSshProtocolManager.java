/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.impl.GetCiphersConfigurationImpl;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * This class contain methods to build the SSH response object in file format and to display the response on CLI for List Ciphers Command.
 * Was GetCiphersResponseBuilder
 *
 * @author sunrise
 */
public class CiphersSshProtocolManager extends AbstractCiphersProtocolManager<String> implements CiphersProtocolManager {
    private static final String[] GET_CIPHERS_HEADER = new String[] { "Node Name", "Supported Ciphers", "Enabled Ciphers", "Error Details" };
    private static final String MULTIPLE_RESPONSE_FORMAT = "%-50s,%-75s,%-75s,%-100s%n";
    private static final String FILENAME = "SSH_" + CiphersConstants.LIST_CIPHERS_CSV_FILE;

    @Inject
    private NscsLogger nscsLogger;
    @Inject
    private NscsCMReaderService nscsCmReaderService;
    @Inject
    private GetCiphersConfigurationImpl getCiphersConfigurationImpl;
    @Inject
    private CiphersConfigurationUtil ciphersConfigurationUtil;

    @Override
    public Map<String, Map<String, List<String>>> prepareNodeCiphersMap(final List<NodeReference> validNodes) {
        NormalizableNodeReference normNode = null;
        Map<String, Map<String, List<String>>> ciphersMap = null;
        final Map<String, Map<String, List<String>>> nodeCiphersMap = new LinkedHashMap<>();
        for (final NodeReference inputNode : validNodes) {
            final Map<String, List<String>> ciphersResponseMap = new HashMap<>();
            normNode = nscsCmReaderService.getNormalizableNodeReference(inputNode);
            ciphersMap = getCiphersConfigurationImpl.getSshCiphers(normNode, CiphersConstants.PROTOCOL_TYPE_SSH);
            if (ciphersMap != null) {
                final Map<String, List<String>> supportedCiphersMap = ciphersMap.get(CiphersConstants.SUPPORTED_CIPHERS);
                ciphersResponseMap.put(
                        CiphersConstants.SUPPORTED_CIPHERS,
                        ciphersConfigurationUtil.prepareSshCiphersResponse(supportedCiphersMap.get(CiphersConstants.KEY_EXCHANGE_ALGORITHMS),
                                supportedCiphersMap.get(CiphersConstants.ENCRYPTION_ALGORITHMS),
                                supportedCiphersMap.get(CiphersConstants.MAC_ALGORITHMS)));
                final Map<String, List<String>> enabledCiphersMap = ciphersMap.get(CiphersConstants.ENABLED_CIPHERS);
                ciphersResponseMap.put(
                        CiphersConstants.ENABLED_CIPHERS,
                        ciphersConfigurationUtil.prepareSshCiphersResponse(enabledCiphersMap.get(CiphersConstants.SELECTED_KEY_EXCHANGE_ALGORITHMS),
                                enabledCiphersMap.get(CiphersConstants.SELECTED_ENCRYPTION_ALGORITHMS),
                                enabledCiphersMap.get(CiphersConstants.SELECTED_MAC_ALGORITHMS)));
            } else {
                ciphersResponseMap.put(CiphersConstants.SUPPORTED_CIPHERS, null);
                ciphersResponseMap.put(CiphersConstants.ENABLED_CIPHERS, null);
            }
            nodeCiphersMap.put(inputNode.getFdn(), ciphersResponseMap);
        }
        return nodeCiphersMap;
    }

    @Override
    protected void fillReportMultiple(final PrintWriter pw, final Map<String, Map<String, List<String>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        fillReport(nodeCiphersMap, invalidNodesErrorMap, (nodeName, supported, enabled, errorDetails) -> pw
                .printf(MULTIPLE_RESPONSE_FORMAT, nodeName, supported, enabled, errorDetails));
    }

    @Override
    protected void fillReportSingle(final NscsNameMultipleValueCommandResponse response, final Map<String, Map<String, List<String>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        fillReport(nodeCiphersMap, invalidNodesErrorMap, (nodeName, supported, enabled, errorDetails) -> response.add(nodeName,
                new String[] { supported, enabled, errorDetails }));
    }

    @FunctionalInterface
    private interface CiphersProcessor {
        void process(final String node, final String supported, final String enabled, final String errorDetails);
    }

    private void fillReport(final Map<String, Map<String, List<String>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final CiphersProcessor processor) {
        processor.process(GET_CIPHERS_HEADER[0], GET_CIPHERS_HEADER[1], GET_CIPHERS_HEADER[2], GET_CIPHERS_HEADER[3]);

        nodeCiphersMap.forEach((node, ciphersValueMap) -> {
            final List<String> supportedCiphersList = ciphersValueMap.get(CiphersConstants.SUPPORTED_CIPHERS);
            final List<String> enabledCiphersList = ciphersValueMap.get(CiphersConstants.ENABLED_CIPHERS);
            // the answer differs for Tls and Ssh protocols because for Tls also information about Tls protocol is reported for each cipher
            if (supportedCiphersList == null || supportedCiphersList.isEmpty()) {
                fillReportForEmptyList(node, processor);
            } else {
                fillReportForValidNodes(node, supportedCiphersList, enabledCiphersList, processor);
            }
        });

        fillReportForInvalidNodes(invalidNodesErrorMap, processor);
    }

    private void fillReportForEmptyList(final String node, final CiphersProcessor processor) {
        processor.process(node, "", "", "");
    }

    private void fillReportForValidNodes(final String node, final List<String> supportedCiphersList, final List<String> enabledCiphersList,
            final CiphersProcessor processor) {
        for (int i = 0; i < supportedCiphersList.size(); i++) {
            final String enabledCipherName =
                    enabledCiphersList.contains(supportedCiphersList.get(i)) ? supportedCiphersList.get(i) : "";
            if (i == 0) {
                processor.process(node, supportedCiphersList.get(i), enabledCipherName, "NA");
            } else {
                processor.process("", supportedCiphersList.get(i), enabledCipherName, "");
            }
        }
    }

    private void fillReportForInvalidNodes(final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final CiphersProcessor processor) {
        if (invalidNodesErrorMap.size() != 0) {
            nscsLogger.debug("Number of invalid nodes are :[{}]", invalidNodesErrorMap.size());
            final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
            for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
                processor.process(entry.getKey().getFdn(), "NA", "NA", entry.getValue().getMessage());
            }
        }
    }

    @Override
    protected String getFileName() {
        return FILENAME;
    }

    @Override
    protected int getColumnNumber() {
        return GET_CIPHERS_HEADER.length - 1;
    }
}
