/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

import java.util.LinkedList;
import java.util.List;

import com.ericsson.nms.security.nscs.api.enums.RevocationReason;

/**
 * ReissueCertificateCommand class for ressue related command.
 *
 * @author enmadmin
 */

public class CertificateReissueCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 4440765751514402345L;

    public static final String CERT_TYPE_PROPERTY = "certtype";
    public static final String CA_PROPERTY = "ca";
    public static final String SERIAL_NUMBER_PROPERTY = "serialnumber";
    public static final String REASON_PROPERTY = "reason";

    private static final List<String> reasonList = new LinkedList<>();

    static {

        reasonList.add(RevocationReason.UNSPECIFIED.toString());
        reasonList.add(RevocationReason.KEY_COMPROMISE.toString());
        reasonList.add(RevocationReason.CA_COMPROMISE.toString());
        reasonList.add(RevocationReason.AFFILIATION_CHANGED.toString());
        reasonList.add(RevocationReason.SUPERSEDED.toString());
        reasonList.add(RevocationReason.CESSATION_OF_OPERATION.toString());
        reasonList.add(RevocationReason.CERTIFICATE_HOLD.toString());
        reasonList.add(RevocationReason.REMOVE_FROM_CRL.toString());
        reasonList.add(RevocationReason.PRIVILEGE_WITHDRAWN.toString());
        reasonList.add(RevocationReason.AA_COMPROMISE.toString());

    }

    /**
     * @return the certificatetype
     */
    public String getCertType() {
        return getValueString(CERT_TYPE_PROPERTY);
    }

    /**
     * @return the ca
     */
    public String getCA() {
        return getValueString(CA_PROPERTY);
    }

    /**
     * @return the serial number
     */
    public String getSerialNumber() {
        return getValueString(SERIAL_NUMBER_PROPERTY);
    }

    /**
     * @return the revocation reason
     */
    public String getReason() {
        return getValueString(REASON_PROPERTY);
    }

    /**
     * @return the revocationreasonlist
     */
    public static List<String> getRevocationReasonlist() {
        return reasonList;
    }

}
