/*-----------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.handler.command.utility;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;

/**
 * Auxiliary class to build the response to a command performed on multiple instances (nodes, proxy accounts).
 * 
 * The command can be successfully performed on all of the given instances.
 * 
 * The command can be successfully performed on none of the given instances.
 * 
 * The command can be successfully performed on some of the given instances.
 * 
 * If command is successfully performed on none or some of the given instances, the response contains an error table to identify the instances on
 * which the command was not successfully performed and the reason of the error.
 * 
 * Each row of the error table has a column containing the identifier of the instance (node name, node FDN, proxy account name) and other columns
 * containing the error details.
 */
public class NscsMultiInstanceCommandResponseBuilder extends NscsNameMultipleValueResponseBuilder {

    private static final String ERROR_CODE = "Error Code";
    private static final String ERROR_DETAILS = "Error Details";
    private static final String CAUSED_BY = "Caused By";
    private static final String SUGGESTED_SOLUTION = "Suggested Solution";

    private static Map<String, Integer> errorRow = new HashMap<>();

    static {
        errorRow.put(ERROR_CODE, 0);
        errorRow.put(ERROR_DETAILS, 1);
        errorRow.put(CAUSED_BY, 2);
        errorRow.put(SUGGESTED_SOLUTION, 3);
    }

    private static final int ERROR_ROW_SIZE = errorRow.size();

    public NscsMultiInstanceCommandResponseBuilder() {
        super(ERROR_ROW_SIZE);
    }

    /**
     * @param isDownloadRequestMessage
     */
    public NscsMultiInstanceCommandResponseBuilder(boolean isDownloadRequestMessage) {
        super(ERROR_ROW_SIZE, isDownloadRequestMessage);
    }

    /**
     * Build a success response to the multiple instance command.
     * 
     * The command has been successfully performed on all of the given instances.
     * 
     * @param successMessage
     *            the success message.
     * @return the response.
     */
    public NscsCommandResponse buildSuccessResponse(final String successMessage) {
        return NscsCommandResponse.message(successMessage);
    }

    /**
     * Build an error response to the multiple instance command.
     * 
     * The command has been successfully performed on none or some of the given instances.
     * 
     * The response contains an error table to identify the instances on which the command was not successfully performed and the reason of the error.
     * 
     * Each row of the error table has a column containing the identifier of the instance (node name, node FDN, proxy account name) and other columns
     * containing the error details.
     * 
     * @param errorMessage
     *            the error message.
     * @param idColumnName
     *            the name of the column containing the identifier of the instance (e.g. node name, node FDN, proxy account name).
     * @param failedInstances
     *            the map of the failed instances (key is the instance identifier, value is an NscsServiceException).
     * @return the response.
     */
    public NscsCommandResponse buildErrorResponse(final String errorMessage, final String idColumnName,
            final Map<String, NscsServiceException> failedInstances) {
        addErrorHeader(idColumnName);
        setAdditionalInformation(errorMessage);
        for (final Map.Entry<String, NscsServiceException> failedInstance : failedInstances.entrySet()) {
            addErrorRow(failedInstance.getKey(), failedInstance.getValue());
        }
        return getCommandResponse();
    }

    /**
     * Add the header of the error table.
     * 
     * @param idColumnName
     *            the name of the column containing the identifier of the instance (e.g. node name, node FDN, proxy account name).
     */
    private void addErrorHeader(final String idColumnName) {
        add(idColumnName, formatErrorTableHeader());
    }

    /**
     * Format the header row of the error table.
     *
     * @return the formatted header row.
     */
    private String[] formatErrorTableHeader() {
        return formatHeader(errorRow);
    }

    /**
     * Add an error row for a failed instance of given identifier to the response.
     * 
     * @param id
     *            the identifier of the failed instance (e.g. node name, node FDN, proxy account name).
     * @param nscsServiceException
     *            the NscsServiceException exception occurred for the failed instance.
     */
    private void addErrorRow(final String id, final NscsServiceException nscsServiceException) {
        add(id, formatErrorRow(nscsServiceException));
    }

    /**
     * Format the error row for the error table of response.
     *
     * @param nscsServiceException
     *            the NscsServiceException exception occurred for the failed object.
     * @return the formatted error row.
     */
    private String[] formatErrorRow(final NscsServiceException nscsServiceException) {
        String[] formattedRow = null;
        final Map<String, String> row = new HashMap<>();

        row.put(ERROR_CODE, Integer.toString(nscsServiceException.getErrorCode()));
        row.put(ERROR_DETAILS, nscsServiceException.getMessage());
        row.put(CAUSED_BY, stringifyThrowable(nscsServiceException.getCause()));
        row.put(SUGGESTED_SOLUTION, nscsServiceException.getSuggestedSolution());

        formattedRow = formatRow(errorRow, row);

        return formattedRow;
    }

    /**
     * Convert to string the given Throwable.
     * 
     * @param throwable
     *            the throwable.
     * @return a stringified version of the throwable.
     */
    private String stringifyThrowable(final Throwable throwable) {
        if (throwable == null) {
            return EMPTY_STRING;
        }
        return String.format("%s : %s", throwable.getClass().getCanonicalName(), throwable.getMessage());
    }

    /**
     * Return the command response to a partially successful command.
     * 
     * @return the command response.
     */
    protected NscsCommandResponse getCommandResponse() {
        return super.getResponse();
    }
}
