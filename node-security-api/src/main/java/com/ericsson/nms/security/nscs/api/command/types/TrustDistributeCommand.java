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
 * TrustDistributeCommand class for trust distribute related command.
 *
 * @author enmadmin
 */

public class TrustDistributeCommand extends NscsNodeCommand {

    private static final long serialVersionUID = -8827068502942860410L;
    public static final String CERT_TYPE_PROPERTY = "certtype";
    public static final String CA_PROPERTY = "ca";
    public static final String TRUST_CATEGORY_PROPERTY = "trustcategory";
    public static final String EXTERNAL_CA_PROPERTY = "extca";
    public static final String XML_FILE_PROPERTY = "xmlfile";

    /**
     * This method will return the wanted certificate type for which to perform trust distribution operation.
     *
     * @return the getCertType
     */
    public String getCertType() {
        return getValueString(CERT_TYPE_PROPERTY);
    }

    public String getCaValue() {
        return getValueString(CA_PROPERTY);
    }

    /**
     * This method will return the required trust category to perform trust distribution operation.
     *
     * @return the trust category
     */
    public String getTrustCategory() {
        return getValueString(TRUST_CATEGORY_PROPERTY);
    }

    /**
     * This method will returns the external ca property to perform trust distribution operation.
     *
     * @return the getCertType
     */
    public String getExternalCA() {
        return getValueString(EXTERNAL_CA_PROPERTY);
    }

    /**
     * This method will returns the required external ca xml file property to perform trust distribution operation.
     *
     * @return the getCertType
     */
    public String getXmlInputFile() {
        return getValueString(XML_FILE_PROPERTY);
    }

}
