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

public class NtpResponseBuilder {

    @Inject
    NscsLogger nscsLogger;

    private static final String ALL_NODES_VALID = "All of the given input nodes are Valid. NTP remove workflow needs to be executed.";
    private static final String SOME_NODES_INVALID = "Some of the given input nodes are Invalid. NTP remove workflow will be executed for valid nodes.";
    private static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };
    private static final int NO_OF_COLUMNS = 3;

    public NscsCommandResponse buildResponseForNtp(final JobStatusRecord jobStatusRecord, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {

        if (jobStatusRecord == null) {
            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
            nscsLogger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
            for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
                response.add(entry.getKey().getFdn(), new String[] { "" + Integer.toString(entry.getValue().getErrorCode()), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
            }
            response.setAdditionalInformation(NtpConstants.NTP_REMOVE_NOT_EXECUTED);
            return response;
        }

        else if (invalidNodesErrorMap.isEmpty()) {

            String jobIdMessage = "";
            nscsLogger.info(ALL_NODES_VALID);
            jobIdMessage = NtpConstants.NTP_REMOVE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress information.";

            return NscsCommandResponse.message(jobIdMessage);
        }

        else {

            String jobIdMessage = "";
            nscsLogger.info(SOME_NODES_INVALID);
            jobIdMessage = String.format(NtpConstants.NTP_REMOVE_PARTIALLY_EXECUTED, jobStatusRecord.getJobId().toString());
            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
            nscsLogger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
            for (final java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
                response.add(entry.getKey().getFdn(), new String[] { "" + Integer.toString(entry.getValue().getErrorCode()), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
            }
            response.setAdditionalInformation(jobIdMessage);
            return response;
        }
    }
}
