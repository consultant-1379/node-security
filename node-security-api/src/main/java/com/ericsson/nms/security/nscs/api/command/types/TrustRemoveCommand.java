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

/**
 * TrustRemoveCommand class for trust remove related command.
 * 
 * @author enmadmin
 */

public class TrustRemoveCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 3941098137451322694L;

    public static final String ISDN_PROPERTY = "issuer-dn";
    public static final String CA_PROPERTY = "ca";
    public static final String SERIALNUMBER_PROPERTY = "serialnumber";
    public static final String CERT_TYPE_PROPERTY = "certtype";
    public static final String TRUST_CATEGORY_PROPERTY = "trustcategory";

    /**
     * This method will return the issuer distinguish name for which to perform trust remove operation.
     * 
     * @return the issuer-dn
     */
    public String getIssuerDn() {
        return getValueString(ISDN_PROPERTY);
    }

    /**
     * This method will return the ca for which to perform trust remove operation.
     * 
     * @return the ca
     */
    public String getCaValue() {
        return getValueString(CA_PROPERTY);
    }

    /**
     * This method will return the serial number for which to perform trust remove operation.
     * 
     * @return the serialNumber
     */
    public String getSerialNumber() {
        return getValueString(SERIALNUMBER_PROPERTY);
    }

    /**
     * This method will return the certificate type for which to perform trust remove operation.
     * 
     * @return the certType
     */
    public String getCertType() {
        return getValueString(CERT_TYPE_PROPERTY);
    }

    /**
     * This method will return the trust category for which to perform trust remove operation.
     * 
     * @return the trustcategory
     */
    public String getTrustCategory() {
        return getValueString(TRUST_CATEGORY_PROPERTY);
    }

}
