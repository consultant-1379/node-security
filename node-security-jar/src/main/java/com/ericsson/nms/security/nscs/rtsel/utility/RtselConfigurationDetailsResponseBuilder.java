/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.rtsel.utility;

import java.util.*;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * This class contain methods to build the response object and display the response on CLI for rtsel get Command.
 * 
 * @author xvekkar
 *
 */
public class RtselConfigurationDetailsResponseBuilder {

    @Inject
    private NscsLogger nscsLogger;

    /**
     * 
     * This method is used to build the rtsel get response for node(s) to display in CLI.
     * 
     * @param rtselDetails
     *            is the map containing information about Real Time Sec Log details on node.
     * @param invalidNodesError
     *            map which contains information about node validity.
     *
     * @return {@link NscsNameMultipleValueCommandResponse} object containing command response.
     */
    @SuppressWarnings("unchecked")
    public NscsNameMultipleValueCommandResponse buildRtselDetailsResponse(final Map<String, Map<String, Object>> rtselDetails, final Map<NodeReference, NscsServiceException> invalidNodesError) {
        nscsLogger.info("Start Of buildRtselDetailsResponse : {}", rtselDetails.size());

        final String[] GET_RTSEL_HEADER = new String[] { RtselConstants.NODE_NAME_HEADER, RtselConstants.SYSLOG_SERVER_HEADER, RtselConstants.SYSLOG_SERVER_HEADER,
                RtselConstants.FEATURE_STATE_HEADER, RtselConstants.SERVER_LOG_LEVEL_HEADER, RtselConstants.CONNECTION_TIMEOUT_HEADER, RtselConstants.APPLICATION_NAME_HEADER,
                RtselConstants.STATUS_HEADER, RtselConstants.ERROR_DETAILS_HEADER };
        final int NO_OF_COLUMNS = 9;
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS - 1);

        final String additionalInformation = ((rtselDetails.size() > 0) ? "Rtsel details for " + rtselDetails.size() + " valid node(s)  "
                : "All the given node(s) are invalid, Error Detail(s) for respective node(s)")
                + ((invalidNodesError.size() > 0 && rtselDetails.size() > 0) ? " and Error Details for " + invalidNodesError.size() + " invalid node(s) " : "") + " is/are listed below.";
        response.setAdditionalInformation(additionalInformation);
        response.add(GET_RTSEL_HEADER[0], Arrays.copyOfRange(GET_RTSEL_HEADER, 1, GET_RTSEL_HEADER.length));
        if (rtselDetails.size() > 0) {
            final Iterator<Map.Entry<String, Map<String, Object>>> itr = rtselDetails.entrySet().iterator();
            while (itr.hasNext()) {
                final Map.Entry<String, Map<String, Object>> pair = itr.next();
                final String node = pair.getKey();
                nscsLogger.debug("Inside buildRtselDetailsResponse, node name is [{}]", node);
                final Map<String, Object> rtselValue = pair.getValue();
                final List<Map<String, String>> extServerListConfig = (List<Map<String, String>>) rtselValue.get(RtselConstants.EXT_SERVER_LIST_CONFIG);

                final List<List<String>> sysLogDetails = prepareSysLogServerDetails(extServerListConfig);
                for (int i = 0; i <= sysLogDetails.size(); i++) {
                    if (i == 0) {
                        response.add(
                                node,
                                new String[] { sysLogDetails.get(0).get(i), sysLogDetails.get(1).get(i), (String) rtselValue.get(RtselConstants.FEATURESTATE),
                                        (String) rtselValue.get(RtselConstants.EXT_SERVER_LOGLEVEL), rtselValue.get(RtselConstants.CONN_TIMEOUT).toString(),
                                        (String) rtselValue.get(RtselConstants.EXT_SERVER_APPNAME), (String) rtselValue.get(RtselConstants.STATUS), RtselConstants.NOT_APPLICABLE });
                    } else {
                        response.add(RtselConstants.EMPTY_STRING, new String[] { sysLogDetails.get(0).get(i), sysLogDetails.get(1).get(i), RtselConstants.EMPTY_STRING, RtselConstants.EMPTY_STRING,
                                RtselConstants.EMPTY_STRING, RtselConstants.EMPTY_STRING, RtselConstants.EMPTY_STRING, RtselConstants.EMPTY_STRING });
                    }
                }
            }
        }
        if (invalidNodesError.size() > 0) {
            final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesError.entrySet();
            for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
                response.add(entry.getKey().getFdn(), new String[] { RtselConstants.NOT_APPLICABLE, RtselConstants.NOT_APPLICABLE, RtselConstants.NOT_APPLICABLE, RtselConstants.NOT_APPLICABLE,
                        RtselConstants.NOT_APPLICABLE, RtselConstants.NOT_APPLICABLE, RtselConstants.NOT_APPLICABLE, entry.getValue().getMessage() });
            }
        }
        nscsLogger.info("End Of buildRtselDetailsResponse");
        return response;
    }

    /**
     * This method is used to prepare syslog server details.
     * 
     * @param extServerListConfig
     *            is list containing syslog server details. Syslog server can be in between 0-2.
     * 
     * @return {@link List<List<String>>} sysLogDetails
     */
    private List<List<String>> prepareSysLogServerDetails(final List<Map<String, String>> extServerListConfig) {
        final List<List<String>> sysLogDetails = new ArrayList<List<String>>();
        int i = 0;
        for (; i < extServerListConfig.size(); i++) {
            sysLogDetails.add(getSysLogServerDetails(extServerListConfig.get(i)));
        }
        for (; i < RtselConstants.MAX_NUM_OF_SYSLOG_SERVERS; i++) {
            sysLogDetails.add(getEmptySysLogServerDetails());
        }
        return sysLogDetails;
    }

    private List<String> getSysLogServerDetails(final Map<String, String> serverConfig) {
        final List<String> sysLogDetail = new ArrayList<String>();
        sysLogDetail.add(RtselConstants.SERVER_NAME_HEADER + serverConfig.get(RtselConstants.EXT_SERVER_NAME));
        sysLogDetail.add(RtselConstants.ADDRESS_HEADER + serverConfig.get(RtselConstants.EXT_SERVER_ADDRESS));
        sysLogDetail.add(RtselConstants.PROTOCOL_HEADER + serverConfig.get(RtselConstants.EXT_SERVER_PROTOCOL));
        return sysLogDetail;
    }

    private List<String> getEmptySysLogServerDetails() {
        final List<String> sysLogDetail = new ArrayList<String>();
        sysLogDetail.add(RtselConstants.SERVER_NAME_HEADER + RtselConstants.NOT_APPLICABLE);
        sysLogDetail.add(RtselConstants.ADDRESS_HEADER + RtselConstants.NOT_APPLICABLE);
        sysLogDetail.add(RtselConstants.PROTOCOL_HEADER + RtselConstants.NOT_APPLICABLE);
        return sysLogDetail;
    }

}