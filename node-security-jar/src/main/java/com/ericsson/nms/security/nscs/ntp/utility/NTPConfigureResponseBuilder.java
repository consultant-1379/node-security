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
package com.ericsson.nms.security.nscs.ntp.utility;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.oss.services.dto.JobStatusRecord;

/**
 * This class defines the methods to build the response for Ntp configure on the requested nodes
 *
 * @author xjangop
 */

public class NTPConfigureResponseBuilder {

    @Inject
    private NscsLogger logger;

    private static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };
    private static final int NO_OF_COLUMNS = 3;

    /**
     * This method is used to build response data of ntp configure for all invalid input nodes
     *
     * @param invalidNodesErrorMap
     *            contains invalid nodes map with node reference and type of exception
     * @return response contains ntp configure all invalid nodes response to display on cli
     */
    public NscsNameMultipleValueCommandResponse buildResponseForAllInvalidInputNodes(
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
        response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
        logger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
        for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
            response.add(entry.getKey().getFdn(), new String[] { "" + Integer.toString(entry.getValue().getErrorCode()),
                    entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
        }
        response.setAdditionalInformation(NtpConstants.NTP_CONFIG_NOT_EXECUTED);
        return response;
    }

    /**
     * This method is used to build response data of ntp configure for all valid input nodes
     *
     * @param jobStatusRecord
     *            the jobStatusRecord
     * @return response contains ntp configure all valid nodes response to display on cli
     */
    public NscsCommandResponse buildResponseForAllValidInputNodes(final JobStatusRecord jobStatusRecord) {
        String jobIdMessage = "";
        logger.info("All of the given input nodes are Valid. NTP configure workflow need to be executed.");
        jobIdMessage = NtpConstants.NTP_CONFIG_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString()
                + "' to get progress information.";

        return NscsCommandResponse.message(jobIdMessage);
    }

    /**
     * This method is used to build response data of ntp configure for partial valid input nodes
     *
     * @param jobStatusRecord
     *            the jobStatusRecord
     * @param invalidNodesErrorMap
     *            contains invalid nodes map with node reference and type of exception
     * @return response contains ntp configure valid and invalid nodes response to display on cli
     */
    public NscsCommandResponse buildResponseForPartialValidInputNodes(final JobStatusRecord jobStatusRecord,
            final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        String jobIdMessage = "";

        logger.info("Some of the given input nodes are Valid. NTP configure workflow need to be executed.");
        jobIdMessage = String.format(NtpConstants.NTP_CONFIG_PARTIALLY_EXECUTED, jobStatusRecord.getJobId().toString());
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
        response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
        logger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
        for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
            response.add(entry.getKey().getFdn(), new String[] { "" + Integer.toString(entry.getValue().getErrorCode()),
                    entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
        }
        response.setAdditionalInformation(jobIdMessage);
        return response;
    }
}
