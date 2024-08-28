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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * This class contain methods to build the TLS response object in file format and to display the response on CLI for List Ciphers Command.
 *
 * @author SUNRISE
 */
public class CiphersTlsProtocolManager extends AbstractCiphersProtocolManager<CipherTlsProtocolInfo> implements CiphersProtocolManager {
    private static final String[] GET_CIPHERS_HEADER =
            new String[] { "Node Name", "Supported Ciphers", "Enabled Ciphers", "Error Details", "Protocol Version" };
    private static final String MULTIPLE_RESPONSE_FORMAT = "%-50s,%-75s,%-75s,%-100s,%-75s%n";
    private static final String FILENAME = "TLS_" + CiphersConstants.LIST_CIPHERS_CSV_FILE;

    @Inject
    private NscsLogger nscsLogger;
    @Inject
    private NscsCMReaderService nscsCmReaderService;
    @Inject
    private TlsCiphersMapImpl tlsCiphersMap;

    @Override
    public Map<String, Map<String, List<CipherTlsProtocolInfo>>> prepareNodeCiphersMap(final List<NodeReference> validNodes) {
        final Map<String, Map<String, List<CipherTlsProtocolInfo>>> nodeCiphersMap = new LinkedHashMap<>();
        validNodes.forEach(inputNode -> {
            final NormalizableNodeReference normNode = nscsCmReaderService.getNormalizableNodeReference(inputNode);
            final Map<String, List<CipherTlsProtocolInfo>> ciphersMap = tlsCiphersMap.getCiphers(normNode, CiphersConstants.PROTOCOL_TYPE_TLS);
            nodeCiphersMap.put(inputNode.getFdn(), ciphersMap);
        });
        return nodeCiphersMap;
    }

    @Override
    protected void fillReportMultiple(final PrintWriter pw, final Map<String, Map<String, List<CipherTlsProtocolInfo>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        fillReport(nodeCiphersMap, invalidNodesErrorMap, (nodeName, supported, enabled, errorDetails, protocolVersion) -> pw
                .printf(MULTIPLE_RESPONSE_FORMAT, nodeName, supported, enabled, errorDetails, protocolVersion));
    }

    @Override
    protected void fillReportSingle(final NscsNameMultipleValueCommandResponse response,
            final Map<String, Map<String, List<CipherTlsProtocolInfo>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        fillReport(nodeCiphersMap, invalidNodesErrorMap, (nodeName, supported, enabled, errorDetails, protocolVersion) -> response.add(nodeName,
                new String[] { supported, enabled, errorDetails, protocolVersion }));
    }

    @FunctionalInterface
    private interface CiphersProcessor {
        void process(final String node, final String supported, final String enabled, final String errorDetails, final String protocolVersion);
    }

    private void fillReport(final Map<String, Map<String, List<CipherTlsProtocolInfo>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final CiphersProcessor processor) {
        processor.process(GET_CIPHERS_HEADER[0], GET_CIPHERS_HEADER[1], GET_CIPHERS_HEADER[2], GET_CIPHERS_HEADER[3], GET_CIPHERS_HEADER[4]);

        nodeCiphersMap.forEach((node, ciphersValueMap) -> {
            nscsLogger.debug("Inside fillReport(TLS), node name is [{}]", node);
            final List<CipherTlsProtocolInfo> supportedCiphersList = ciphersValueMap.get(CiphersConstants.SUPPORTED_CIPHERS);
            if (supportedCiphersList == null || supportedCiphersList.isEmpty()) {
                fillReportForEmptyList(node, processor);
            } else {
                fillReportForValidNodes(node, supportedCiphersList, processor);
            }
        });

        fillReportForInvalidNodes(invalidNodesErrorMap, processor);
    }

    private void fillReportForEmptyList(final String node, final CiphersProcessor processor) {
        processor.process(node, "", "", "", "");
    }

    private void fillReportForValidNodes(final String node, final List<CipherTlsProtocolInfo> supportedCiphersList,
            final CiphersProcessor processor) {
        boolean firstRaw = true;
        for (final CipherTlsProtocolInfo cipherValue : supportedCiphersList) {
            final String supportedCipherName = cipherValue.getName();
            final String enabledCipherName = cipherValue.isEnabled() ? supportedCipherName : "";
            final String protocolVersion = cipherValue.getProtocolVersion();
            if (firstRaw) {
                processor.process(node, supportedCipherName, enabledCipherName, "NA", protocolVersion);
                firstRaw = false;
            } else {
                processor.process("", supportedCipherName, enabledCipherName, "", protocolVersion);
            }
        }
    }

    private void fillReportForInvalidNodes(final Map<NodeReference, NscsServiceException> invalidNodesErrorMap, final CiphersProcessor processor) {
        if (invalidNodesErrorMap.size() != 0) {
            nscsLogger.debug("Number of invalid nodes are :[{}]", invalidNodesErrorMap.size());
            invalidNodesErrorMap.forEach((k, v) -> processor.process(k.getFdn(), "NA", "NA", v.getMessage(), "NA"));
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
