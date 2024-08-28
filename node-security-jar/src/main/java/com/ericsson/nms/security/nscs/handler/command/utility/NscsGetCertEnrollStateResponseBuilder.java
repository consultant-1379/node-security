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

import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.util.CertDetails;

/**
 * Auxiliary class to build a name-multiple values response to get certificate
 * enrollment state.
 * 
 */
public class NscsGetCertEnrollStateResponseBuilder extends NscsNameMultipleValueResponseBuilder {

    /**
     * Name-MultipleValues response to get certificate enroll state
     */
    public static final String CERT_ENROLL_NAME_TITLE = NODE_NAME_NAME_TITLE;
    public static final String CERT_ENROLL_ACTION_STATE_TITLE = "Enroll State";
    public static final String CERT_ENROLL_ACTION_ERROR_MSG_TITLE = "Enroll Error Message";
    private static Map<String, Integer> theCertEnrollStateRow = new HashMap<String, Integer>();

    static {
        theCertEnrollStateRow.put(CERT_ENROLL_ACTION_STATE_TITLE, 0);
        theCertEnrollStateRow.put(CERT_ENROLL_ACTION_ERROR_MSG_TITLE, 1);
        theCertEnrollStateRow.put(SUBJECT_TITLE, 2);
        theCertEnrollStateRow.put(SERIAL_NUMBER_TITLE, 3);
        theCertEnrollStateRow.put(ISSUER_TITLE, 4);
        theCertEnrollStateRow.put(SUBJECT_ALT_NAME_TITLE, 5);
    }

    public static final int CERT_ENROLL_STATE_ROW_SIZE = theCertEnrollStateRow.size();

    public NscsGetCertEnrollStateResponseBuilder() {
        super(CERT_ENROLL_STATE_ROW_SIZE);
    }

    /**
     * Add the header row to the name multiple values response to get
     * certificate enroll state command.
     * 
     */
    public void addHeader() {
        add(CERT_ENROLL_NAME_TITLE, formatHeader());
    }

    /**
     * Format the header row of the multiple values section of response to get
     * certificate enroll state command.
     * 
     * @return the formatted header row
     */
    public String[] formatHeader() {
        return formatHeader(theCertEnrollStateRow);
    }

    /**
     * Add a row to the name multiple values response to get certificate enroll
     * state command according to given certificate state info.
     * 
     */
    public void addRow(final CertStateInfo certStateInfo) {
        add(certStateInfo.getNodeName(), formatRow(certStateInfo));
    }

    /**
     * Format a row for the multiple values section of response to the get
     * certificate enroll state command for the given certificate state info.
     * 
     * @param certStateInfo
     * @return the formatted row
     */
    public String[] formatRow(final CertStateInfo certStateInfo) {
        String[] formattedRow = null;
        if (certStateInfo != null) {
            Map<String, String> row = new HashMap<String, String>();
            row.put(CERT_ENROLL_ACTION_STATE_TITLE, certStateInfo.getState());
            row.put(CERT_ENROLL_ACTION_ERROR_MSG_TITLE, certStateInfo.getErrorMsg());
            if (certStateInfo.getCertificates() != null && !certStateInfo.getCertificates().isEmpty()) {
                CertDetails certDetails = certStateInfo.getCertificates().iterator().next();
                row.putAll(extractCertDetails(certDetails));
            }
            formattedRow = formatRow(theCertEnrollStateRow, row);
        }
        return formattedRow;
    }

}
