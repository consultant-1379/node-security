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
import java.util.Set;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.util.CertDetails;

/**
 * Auxiliary class to build a name-multiple values response to get trust install state.
 * 
 */
public class NscsGetTrustInstallStateResponseBuilder extends NscsNameMultipleValueResponseBuilder {

    /**
     * Name-MultipleValues response to get trust install state
     */
    public static final String TRUST_INSTALL_NAME_TITLE = NODE_NAME_NAME_TITLE;
    public static final String TRUST_INSTALL_ACTION_STATE_TITLE = "Install State";
    public static final String TRUST_INSTALL_ACTION_ERROR_MSG_TITLE = "Install Error Message";
    private static Map<String, Integer> theTrustInstallStateRow = new HashMap<String, Integer>();

    static {
        theTrustInstallStateRow.put(TRUST_INSTALL_ACTION_STATE_TITLE, 0);
        theTrustInstallStateRow.put(TRUST_INSTALL_ACTION_ERROR_MSG_TITLE, 1);
        theTrustInstallStateRow.put(SUBJECT_TITLE, 2);
        theTrustInstallStateRow.put(SERIAL_NUMBER_TITLE, 3);
        theTrustInstallStateRow.put(ISSUER_TITLE, 4);
    }

    public static final int TRUST_INSTALL_STATE_ROW_SIZE = theTrustInstallStateRow.size();

    public NscsGetTrustInstallStateResponseBuilder() {
        super(TRUST_INSTALL_STATE_ROW_SIZE);
    }

    /**
     * Add the header row to the name multiple values response to get trust install state command.
     * 
     */
    public void addHeader() {
        add(TRUST_INSTALL_NAME_TITLE, formatHeader());
    }

    /**
     * Format the header row of the multiple values section of response to get trust install state command.
     * 
     * @return the formatted header row
     */
    public String[] formatHeader() {
        return formatHeader(theTrustInstallStateRow);
    }

    /**
     * Add the rows (one row for each trusted certificate) to the name multiple values response to get trust install state command according to given
     * certificate state info.
     * 
     * The node name is present only for the first row. The first row contains also the action state and action error message.
     * 
     * @param certStateInfo
     */
    public void addRows(final CertStateInfo certStateInfo) {
        if (certStateInfo != null) {
            if (certStateInfo.getCertificates() == null || certStateInfo.getCertificates().isEmpty()) {
                add(certStateInfo.getNodeName(), formatRow(certStateInfo.getState(), certStateInfo.getErrorMsg(), null));
            } else {
                boolean isFirstRow = true;
                for (CertDetails certDetails : certStateInfo.getCertificates()) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        add(certStateInfo.getNodeName(), formatRow(certStateInfo.getState(), certStateInfo.getErrorMsg(), certDetails));
                    } else {
                        add(EMPTY_STRING, formatRow(certDetails));
                    }
                }
            }
        }
    }

    /**
     * Add the Error rows Node name and Error details will be added to the response
     * 
     * @param invalidNodesErrorMap
     */
    public void addErrorRows(final Map<NodeReference, NscsServiceException> invalidNodesErrorMap) {
            final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
            for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
                add(entry.getKey().getFdn(), formatRow("ERROR", entry.getValue().getMessage(), null));
            }
            

    }

    /**
     * Format the first row for the multiple values section of response to the get trust install state command for the given values.
     * 
     * The first row contains also the fields common to all certificates (e.g. the action state and action error message).
     * 
     * @param state
     * @param errorMsg
     * @param certDetails
     * @return
     */
    public String[] formatRow(final String state, final String errorMsg, final CertDetails certDetails) {
        String[] formattedRow = null;
        Map<String, String> row = new HashMap<String, String>();
        row.put(TRUST_INSTALL_ACTION_STATE_TITLE, state);
        row.put(TRUST_INSTALL_ACTION_ERROR_MSG_TITLE, errorMsg);
        row.putAll(extractCertDetails(certDetails));
        formattedRow = formatRow(theTrustInstallStateRow, row);
        return formattedRow;
    }

    /**
     * Format a row following the first one for the multiple values section of response to the get trust install state command for the given values.
     * 
     * These rows don't contain common fields.
     * 
     * @param certDetails
     * @return
     */
    public String[] formatRow(final CertDetails certDetails) {
        String[] formattedRow = null;
        Map<String, String> row = extractCertDetails(certDetails);
        formattedRow = formatRow(theTrustInstallStateRow, row);
        return formattedRow;
    }
}
