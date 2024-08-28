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
 * GetTrustCertInstallStateCommand class for the get TrustCertInstallState related command.
 * 
 * @author enmadmin
 */

public class GetTrustCertInstallStateCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 865067322546678121L;

    public static final String CERT_TYPE_PROPERTY = "certtype";
    public static final String TRUST_CATEGORY_PROPERTY = "trustcategory";

    /**
     * This method will return the wanted certificate type for which to perform get trust certificate installation state operation.
     * 
     * @return the getCertType
     */
    public String getCertType() {
        return getValueString(CERT_TYPE_PROPERTY);
    }

    /**
     * This method will return the wanted trust category type for which to perform get trust certificate installation state operation.
     * 
     * @return the trust category
     */
    public String getTrustCategory() {
        return getValueString(TRUST_CATEGORY_PROPERTY);
    }

}