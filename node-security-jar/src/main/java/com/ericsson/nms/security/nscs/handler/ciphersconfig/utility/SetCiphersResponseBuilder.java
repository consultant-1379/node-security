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
package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

import java.util.Arrays;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SetCiphersWfException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.dto.JobStatusRecord;

public class SetCiphersResponseBuilder {

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsCommandManager commandManager;

    final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };

    /**
     * This method constructs and returns the response object if all the input nodes are invalid.
     * 
     * @param command
     *            object of type CiphersConfigCommand
     * @param invalidNodesErrorMap
     *            map of invalid nodes
     * @return 
     *             object of type NscsNameMultipleValueCommandResponse
     */
    public NscsNameMultipleValueCommandResponse buildResponseForAllInvalidInputNodes(final CiphersConfigCommand command, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CiphersConstants.NO_OF_COLUMNS);
        response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
        nscsLogger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
        for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
            response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
        }
        response.setAdditionalInformation(CiphersConstants.SET_CIPHERS_NOT_EXECUTED);
        nscsLogger.commandHandlerFinishedWithError(command, CiphersConstants.SET_CIPHERS_NOT_EXECUTED);
        return response;
    }

    /** This method constructs and returns the response object if all the input nodes are valid.
     * 
     * @param jobStatusRecord
     *              object of type JobStatusRecord
     * @return
     *         object of type NscsCommandResponse
     */
    public NscsCommandResponse buildResponseForAllValidInputNodes(final JobStatusRecord jobStatusRecord) {
        String jobIdMessage = "";
        try {
            nscsLogger.info("All of the given input nodes are Valid. Set Ciphers workflow need to be executed.");
            jobIdMessage = CiphersConstants.SET_CIPHERS_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress information.";
        } catch (Exception ex) {
            nscsLogger.error(ex.getMessage(), ex);
            throw new SetCiphersWfException();
        }
        return NscsCommandResponse.message(jobIdMessage);
    }
    
    /**This method constructs and returns the response object if some of the input nodes are valid.
     * 
     * @param jobStatusRecord
     *              object of type JobStatusRecord
     * @param invalidNodesErrorMap
     *            map of invalid nodes
     * @return
     *         object of type NscsCommandResponse
     */
    public NscsCommandResponse buildResponseForPartialValidInputNodes(final JobStatusRecord jobStatusRecord, final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
        String jobIdMessage = "";

        nscsLogger.info("Some of the given input nodes are Valid. Set Ciphers workflow need to be executed.");
        jobIdMessage = String.format(CiphersConstants.SET_CIPHERS_EXECUTED_DYN_ISSUE, jobStatusRecord.getJobId().toString());
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CiphersConstants.NO_OF_COLUMNS);
        response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
        nscsLogger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
        for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
            response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
        }
        response.setAdditionalInformation(jobIdMessage);
        return response;
    } 
}
