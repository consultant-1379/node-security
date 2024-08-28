/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.enums;

/**
 * This enum is introduced to specify the trust category values: (OAM/IPSEC/LAAD) for all target types.
 * 
 * @author xkumkam
 * 
 */
public enum TrustCategoryType {

    IPSEC, OAM, LAAD;

    /*
     * Return the Enum name
     * 
     * @see java.lang.Enum#name()
     */
    @Override
    public String toString() {
        return this.name();
    }

    public TrustCategoryType toTrustCategoryType(String value) {
        TrustCategoryType trustCategoryType = null;
        switch (value) {
        case "IPSEC":
            trustCategoryType = TrustCategoryType.IPSEC;
            break;
        case "OAM":
            trustCategoryType = TrustCategoryType.OAM;
            break;
        case "LAAD":
            trustCategoryType = TrustCategoryType.LAAD;
            break;
        default:
            break;
        }
        return trustCategoryType;
    }

}
