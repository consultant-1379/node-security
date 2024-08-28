/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.utility;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpConstants;

/**
 * This class defines the methods to build the response for Ntp list on the requested nodes
 *
 * @author zkndsrv
 *
 */
public class ListNtpServerResponseBuilder extends NscsNameMultipleValueResponseBuilder {

    @Inject
    private NscsLogger nscsLogger;

    private static final String[] LIST_NTP_HEADER = new String[] { NtpConstants.NODE_NAME_HEADER, NtpConstants.KEY_ID, NtpConstants.NTP_USER_LABEL,
            NtpConstants.NTP_SERVER_ID, NtpConstants.SERVER_ADDRESS_HEADER, NtpConstants.SERVICE_STATUS_HEADER, NtpConstants.ERROR_DETAILS_HEADER, NtpConstants.SUGGESTED_SOLUTION };
    private static final int NO_OF_COLUMNS = 7;
    private static Map<String, Integer> ntpResultheaders = new HashMap<>();
    static {
        ntpResultheaders.put(LIST_NTP_HEADER[1], 0);
        ntpResultheaders.put(LIST_NTP_HEADER[2], 1);
        ntpResultheaders.put(LIST_NTP_HEADER[3], 2);
        ntpResultheaders.put(LIST_NTP_HEADER[4], 3);
        ntpResultheaders.put(LIST_NTP_HEADER[5], 4);
        ntpResultheaders.put(LIST_NTP_HEADER[6], 5);
        ntpResultheaders.put(LIST_NTP_HEADER[7], 6);
    }

    public static final int NTP_LIST_ROW_SIZE = ntpResultheaders.size();

    /**
     * @param numberOfColumns
     */
    public ListNtpServerResponseBuilder() {
        super(NTP_LIST_ROW_SIZE);
    }

    /**
     * This method is used to build response data of ntp list for all valid input nodes
     *
     * @param ntpserverDetails
     *        contains list of ntp server details on selected nodes
     * @return response
     *        contains ntp list all valid nodes response to display on cli
     */
    public NscsNameMultipleValueCommandResponse buildResponseForAllvalidInputNodes(final Map<String, List<NtpServer>> ntpserverDetails) {

        nscsLogger.info("Start of buildListNTPKeyIDsResponseSuccess method: ntpserverDetails size[{}]", ntpserverDetails.size());

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
        response.add(LIST_NTP_HEADER[0], Arrays.copyOfRange(LIST_NTP_HEADER, 1, LIST_NTP_HEADER.length));
        final Set<Entry<String, List<NtpServer>>> entrySet = ntpserverDetails.entrySet();

        for (final Entry<String, List<NtpServer>> entry : entrySet) {
            boolean isFirstRow = true;
            List<NtpServer> ntpValues = entry.getValue();
            for (final NtpServer entryVal : ntpValues) {
                if (isFirstRow) {
                    isFirstRow = false;
                    response.add(entry.getKey(), formatRow(entryVal));
                } else {
                    response.add(EMPTY_STRING, formatRow(entryVal));
                }
            }
        }
        return response;
    }

    /**
     * This method is used to build response data of ntp list for partial valid input nodes
     *
     * @param ntpserverDetails
     *        contains list of ntp Server details on selected nodes
     * @param invalidNodesErrorMap
     *        contains invalid nodes map with node reference and type of exception
     * @return response
     *        contains ntp list valid and invalid nodes response to display on cli
     */
    public NscsNameMultipleValueCommandResponse buildResponseForPartialValidInputNodes(final Map<String, List<NtpServer>> ntpserverDetails,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {

        nscsLogger.info("Start of buildResponseForPartialValidInputNodes method: ntpserverDetails size[{}]", ntpserverDetails.size());

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
        response.add(LIST_NTP_HEADER[0], Arrays.copyOfRange(LIST_NTP_HEADER, 1, LIST_NTP_HEADER.length));
        final Set<Entry<String, List<NtpServer>>> entrySet = ntpserverDetails.entrySet();

        for (final Entry<String, List<NtpServer>> entry : entrySet) {
            boolean isFirstRow = true;
            List<NtpServer> ntpValues = entry.getValue();
            for (final NtpServer entryVal : ntpValues) {
                if (isFirstRow) {
                    isFirstRow = false;
                    response.add(entry.getKey(), formatRow(entryVal));
                } else {
                    response.add(EMPTY_STRING, formatRow(entryVal));
                }
            }
        }
        for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
            response.add(entry.getKey().getName(), new String[] { NtpConstants.NA, NtpConstants.NA, NtpConstants.NA, NtpConstants.NA, NtpConstants.NA, entry.getValue().getMessage(),
                    entry.getValue().getSuggestedSolution() });
        }
        return response;
    }
    /**
     * This method is used to build response data of ntp list for all invalid input nodes
     *
     * @param invalidNodesErrorMap
     *        contains invalid nodes map with node reference and type of exception
     * @return response
     *         contains ntp list all invalid nodes response to display on cli
     */
    public NscsNameMultipleValueCommandResponse buildResponseForAllInvalidInputNodes(final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {

        nscsLogger.info("Start of buildResponseForAllInvalidInputNodes method: ntpserverDetails size[{}]", invalidNodesErrorMap.size());

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
        response.add(LIST_NTP_HEADER[0], Arrays.copyOfRange(LIST_NTP_HEADER, 1, LIST_NTP_HEADER.length));

        for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
            response.add(entry.getKey().getName(), new String[] { NtpConstants.NA, NtpConstants.NA, NtpConstants.NA, NtpConstants.NA, NtpConstants.NA, entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
        }
        return response;
    }

    /**
     * Adds the first row (one row for each Node) to the name,keyId,userLabel,ntpServerId,serviceStatus,errorMsg,suggestedSol
     *
     * @param keyId
     *         ntp key id configured on node
     * @param serviceStatus
     *         ntp server status configured on node
     * @param errorMsg
     *         error message that needs to be shown for invalid nodes
     * @param suggestedSol
     *         suggested solution for the error message shown
     * @return formattedRow
     *         it formats the rows with above params to display multiple rows for each node
     */
    public String[] formatRow(final NtpServer entryVal) {
        String[] formattedRow = null;
        Map<String, String> row = new HashMap<>();
        String suggestedSolution = "";
        String errorDetailsMsg = "";
        if (NtpConstants.NA.equals(entryVal.getKeyId()) && NtpConstants.NA.equals(entryVal.getServerAddress())) {
            errorDetailsMsg = NtpConstants.NTP_SERVER_ERR_MSG + "\n";
            suggestedSolution = NtpConstants.NA + "\n";
        }
        if ((!NtpConstants.NA.equals(entryVal.getKeyId()) && NtpConstants.NA.equals(entryVal.getServiceStatus()))) {
            errorDetailsMsg = NtpConstants.NTP_SERVER_ERR_MSG + "\n";
            suggestedSolution = entryVal.getKeyId().equals("") ? "" : NtpConstants.NTP_SERVER_SOLUTION + "\n";
        }
        row.put(LIST_NTP_HEADER[1], entryVal.getKeyId());
        row.put(LIST_NTP_HEADER[2], entryVal.getUserLabel());
        row.put(LIST_NTP_HEADER[3], entryVal.getNtpServerId());
        row.put(LIST_NTP_HEADER[4], entryVal.getServerAddress());
        row.put(LIST_NTP_HEADER[5], entryVal.getServiceStatus());
        row.put(LIST_NTP_HEADER[6], errorDetailsMsg);
        row.put(LIST_NTP_HEADER[7], suggestedSolution);
        formattedRow = formatRow(ntpResultheaders, row);
        return formattedRow;
    }
}
