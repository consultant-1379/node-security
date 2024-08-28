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
package com.ericsson.nms.security.nscs.handler.command.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.ExtendedCertDetails;

/**
 * Auxiliary class to build a name-multiple values response.
 *
 */
public class NscsNameMultipleValueResponseBuilder {

    /**
     * Common constants
     */
    public static final String EMPTY_STRING = NscsNameMultipleValueCommandResponse.EMPTY_STRING;
    public static final String NOT_AVAILABLE = "N/A";
    public static final String NOT_APPLICABLE = "Not Applicable";
    public static final String NODE_NAME_NAME_TITLE = "Node Name";

    /**
     * Certificate details
     */
    public static final String SERIAL_NUMBER_TITLE = "Serial Number";
    public static final String ISSUER_TITLE = "Issuer";
    public static final String SUBJECT_TITLE = "Subject";
    public static final String SUBJECT_ALT_NAME_TITLE = "Subject Alternative Name";

    /**
     * Dynamic errors section
     */
    public static final String DYNAMIC_ERRORS_NAME_TITLE = "Dynamic errors:";
    private static Map<String, Integer> theDynamicErrorsRow = new HashMap<String, Integer>();
    public static final int DYNAMIC_ERRORS_ROW_SIZE = theDynamicErrorsRow.size();

    /**
     * Error details section
     */
    public static final String ERROR_DETAILS_NAME_TITLE = NODE_NAME_NAME_TITLE;
    public static final String ERROR_DETAILS_CODE_TITLE = "Error Code";
    public static final String ERROR_DETAILS_MSG_TITLE = "Error Message";
    private static Map<String, Integer> theErrorDetailsRow = new HashMap<String, Integer>();

    static {
        theErrorDetailsRow.put(ERROR_DETAILS_CODE_TITLE, 0);
        theErrorDetailsRow.put(ERROR_DETAILS_MSG_TITLE, 1);
    }

    public static final int ERROR_DETAILS_ROW_SIZE = theErrorDetailsRow.size();
    public static final String LEVEL_NOT_SUPPORTED = "LEVEL_NOT_SUPPORTED";
    public static final String IPSEC_NOT_SUPPORTED = "IPSEC_NOT_SUPPORTED";
    public static final String UNSYNCHRONIZED = "UNSYNCHRONIZED";
    public static final String SYNCHRONIZED = "SYNCHRONIZED";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String LEVEL_UNDEFINED = "LEVEL_UNDEFINED";
    public static final String LEVEL_1 = "LEVEL_1";
    public static final String LEVEL_2 = "LEVEL_2";
    public static final String OPERATION_IN_PROGRESS = "Operation in progress";
    public static final String SL2_ACTIVATION_IN_PROGRESS = "SL2 activation in progress";
    public static final String SL2_DEACTIVATION_IN_PROGRESS = "SL2 deactivation in progress";
    public static final String IPSEC_ACTIVATION_IN_PROGRESS = "IPSEC activation in progress";
    public static final String IPSEC_DEACTIVATION_IN_PROGRESS = "IPSEC deactivation in progress";

    private NscsNameMultipleValueCommandResponse response;
    private int numberOfColumns;

    /**
     * @param numberOfColumns
     */
    public NscsNameMultipleValueResponseBuilder(int numberOfColumns) {
        this.response = NscsCommandResponse.nameMultipleValue(numberOfColumns);
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * Constructs a NscsDownloadRequestMessageCommandResponse or a NscsNameMultipleValueCommandResponse.
     * 
     * @param numberOfColumns
     *            the number of columns.
     * @param isDownloadRequestMessage
     *            true if a NscsDownloadRequestMessageCommandResponse is requested.
     */
    public NscsNameMultipleValueResponseBuilder(int numberOfColumns, boolean isDownloadRequestMessage) {
        this.response = isDownloadRequestMessage ? new NscsDownloadRequestMessageCommandResponse(numberOfColumns)
                : NscsCommandResponse.nameMultipleValue(numberOfColumns);
        this.numberOfColumns = numberOfColumns;
    }
    /**
     * @return the response
     */
    public NscsNameMultipleValueCommandResponse getResponse() {
        return response;
    }

    /**
     * @return the numberOfColumns
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * Add a row to the name-multiple values response according to given name and values.
     * 
     * @param name
     * @param values
     */
    public void add(final String name, final String[] values) {
        this.response.add(name, values);
    }

    /**
     * Set additional info to the name-multiple values response according to given value.
     * 
     * @param additionalInformation
     */
    public void setAdditionalInformation(final String additionalInformation) {
        this.response.setAdditionalInformation(additionalInformation);
    }

    /**
     * Add a dynamic error section to the name-multiple values response according to given map of dynamically invalid nodes.
     * 
     * @param invalidDynamicNodesMap
     */
    public void addDynamicErrorSection(final Map<String, String[]> invalidDynamicNodesMap) {
        if (invalidDynamicNodesMap != null) {
            add(DYNAMIC_ERRORS_NAME_TITLE, formatHeader(theDynamicErrorsRow));
            add(ERROR_DETAILS_NAME_TITLE, formatHeader(theErrorDetailsRow));
            for (Entry<String, String[]> entry : invalidDynamicNodesMap.entrySet()) {
                Map<String, String> rowValues = new HashMap<String, String>();
                rowValues.put(ERROR_DETAILS_CODE_TITLE, entry.getValue()[0]);
                rowValues.put(ERROR_DETAILS_MSG_TITLE, entry.getValue()[1]);
                this.response.add(entry.getKey(), formatRow(theErrorDetailsRow, rowValues));
            }
        }
    }

    /**
     * Format the header row of the multiple values according to a given table mapping the column name to the column index.
     * 
     * If the column index specified in the table exceeds the multiple values section size, the correspondent item is skipped. The unspecified columns of the multiple values section will contain
     * EMPTY_STRING.
     * 
     * @param table
     * @return the formatted header row
     */
    public String[] formatHeader(final Map<String, Integer> table) {

        int headerSize = this.numberOfColumns;
        String[] header = new String[headerSize];
        for (int i = 0; i < headerSize; i++) {
            header[i] = EMPTY_STRING;
        }
        for (String columnName : table.keySet()) {
            Integer columnIndex = table.get(columnName);
            if (columnIndex != null && columnIndex < headerSize) {
                header[columnIndex] = columnName;
            }
        }
        return header;
    }

    /**
     * Format the row of the multiple values according to a given table mapping the column name to the column index and a given table mapping the column name to the column value.
     * 
     * If the column index specified in the table exceeds the multiple values section size, the correspondent item is skipped. The unspecified columns of the multiple values section will contain
     * EMPTY_STRING.
     * 
     * @param table
     * @param rowValues
     * @return the formatted row
     */
    public String[] formatRow(final Map<String, Integer> table, final Map<String, String> rowValues) {

        int rowSize = this.numberOfColumns;
        String[] row = new String[rowSize];
        for (int i = 0; i < rowSize; i++) {
            row[i] = EMPTY_STRING;
        }
        for (String columnName : rowValues.keySet()) {
            String value = rowValues.get(columnName);
            Integer column = table.get(columnName);
            if (column != null && column < rowSize) {
                row[column] = value;
            }
        }
        return row;
    }

    /**
     * Extract from the given certificate details the values of the fields.
     * 
     * The NOT_AVAILABLE value is returned if a field is null or empty.
     * 
     * @param certDetails
     * @return
     */
    protected Map<String, String> extractCertDetails(final CertDetails certDetails) {
        Map<String, String> row = new HashMap<String, String>();
        if (certDetails != null) {
            String issuer = (certDetails.getIssuer() != null && !certDetails.getIssuer().isEmpty()) ? certDetails.getIssuer() : NOT_AVAILABLE;
            String serial = (certDetails.getSerial() != null && !certDetails.getSerial().toString().isEmpty()) ? certDetails.getSerial().toString() : NOT_AVAILABLE;
            String subject = (certDetails.getSubject() != null && !certDetails.getSubject().isEmpty()) ? certDetails.getSubject() : NOT_AVAILABLE;
            row.put(ISSUER_TITLE, issuer);
            row.put(SERIAL_NUMBER_TITLE, serial);
            row.put(SUBJECT_TITLE, subject);
            if (certDetails instanceof ExtendedCertDetails) {
                String subjectAltName = (((ExtendedCertDetails) certDetails).getSubjectAltName() != null && !((ExtendedCertDetails) certDetails).getSubjectAltName().isEmpty())
                        ? ((ExtendedCertDetails) certDetails).getSubjectAltName() : NOT_AVAILABLE;
                row.put(SUBJECT_ALT_NAME_TITLE, subjectAltName);
            }
        }
        return row;
    }
}