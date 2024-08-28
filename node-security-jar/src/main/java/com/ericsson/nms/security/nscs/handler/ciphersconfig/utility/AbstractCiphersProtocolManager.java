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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.FileUtil;

/**
 * This class contain methods to build the SSH/TLS response object in file format and to display the response on CLI for List Ciphers Command.
 *
 * @author SUNRISE
 */
public abstract class AbstractCiphersProtocolManager<T> {
    private static final String SUCCESS_MESSAGE = "Ciphers file downloaded successfully.";

    @Inject
    private FileUtil fileUtil;
    @Inject
    private NscsLogger nscsLogger;

    public NscsCommandResponse buildGetCiphersResponse(final List<NodeReference> validNodes,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) throws IOException {
        final Map<String, Map<String, List<T>>> nodeCiphersMap =
                !validNodes.isEmpty() ? prepareNodeCiphersMap(validNodes) : new LinkedHashMap<>();
        return validNodes.size() + invalidNodesErrorMap.size() == 1 ? buildCiphersResponseForSingleNode(nodeCiphersMap, invalidNodesErrorMap)
                : buildCiphersResponseForMultipleNodes(nodeCiphersMap, invalidNodesErrorMap);
    }

    protected abstract Map<String, Map<String, List<T>>> prepareNodeCiphersMap(final List<NodeReference> validNodes);

    /**
     * This method is used to build the Ciphers response for multiple nodes in the CSV file format.
     *
     * @param nodeCiphersMap
     *            is the map containing information about supported and enabled ciphers for each node.
     * @param invalidNodesErrorMap
     *            map which contains information about invalid nodes.
     * @param fileName
     *            name of the file to which ciphers response is exported.
     * @return {@link NscsCommandResponse} object containing command response.
     */
    private NscsCommandResponse buildCiphersResponseForMultipleNodes(
            final Map<String, Map<String, List<T>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) throws IOException {
        nscsLogger.info("Inside buildCiphersResponseForMultipleNodes method");
        final ByteArrayOutputStream byteOutPut = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(byteOutPut, StandardCharsets.UTF_8);
        final PrintWriter pw = new PrintWriter(writer);

        fillReportMultiple(pw, nodeCiphersMap, invalidNodesErrorMap);

        pw.close();
        final byte[] byteData = byteOutPut.toByteArray();
        final String fileIdentifier = fileUtil.createDeletableDownloadFileIdentifier(byteData, getFileName(), CiphersConstants.CSV_CONTENT_TYPE);
        final NscsCommandResponse commandResponse = buildNscsCommandResponse(SUCCESS_MESSAGE, fileIdentifier);
        nscsLogger.info("End Of buildCiphersResponseForMultipleNodes");
        return commandResponse;
    }

    /**
     * This method is used to build the Ciphers response for single node to display in CLI.
     *
     * @param nodeCiphersMap
     *            is the map containing information about supported and enabled ciphers for node.
     * @param invalidNodesErrorMap
     *            map which contains information about invalid nodes.
     * @param response
     *            Object of NscsNameMultipleValueCommandResponse class to display command response on CLI.
     * @return {@link NscsNameMultipleValueCommandResponse} object containing command response.
     */
    private NscsNameMultipleValueCommandResponse buildCiphersResponseForSingleNode(
            final Map<String, Map<String, List<T>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        nscsLogger.info("Inside buildCiphersResponseForSingleNode method");
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(getColumnNumber());

        fillReportSingle(response, nodeCiphersMap, invalidNodesErrorMap);

        nscsLogger.info("End Of buildCiphersResponseForSingleNode");
        return response;
    }

    protected abstract void fillReportMultiple(final PrintWriter pw, final Map<String, Map<String, List<T>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap);

    protected abstract void fillReportSingle(final NscsNameMultipleValueCommandResponse response,
            final Map<String, Map<String, List<T>>> nodeCiphersMap,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap);

    protected abstract String getFileName();

    protected abstract int getColumnNumber();

    private NscsCommandResponse buildNscsCommandResponse(final String message, final String fileIdentifier) {
        final NscsDownloadRequestMessageCommandResponse nscsDownloadReqMsgcommandResponse =
                new NscsDownloadRequestMessageCommandResponse(0, fileIdentifier, message);
        nscsDownloadReqMsgcommandResponse.setFileIdentifier(fileIdentifier);
        nscsDownloadReqMsgcommandResponse.setMessage(message);
        return nscsDownloadReqMsgcommandResponse;
    }
}
