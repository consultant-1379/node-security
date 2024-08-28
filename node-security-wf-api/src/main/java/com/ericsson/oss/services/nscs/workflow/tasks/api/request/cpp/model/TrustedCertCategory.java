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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;

/**
 * Enumeration used for CPP trusted certificate categories
 * 
 * @author egbobcs
 */
public enum TrustedCertCategory {

    LOCAL_AA_DB_FILE_SIGNERS(Constants.UNKNOWN_CATEGORY), AA_SERVERS(Constants.UNKNOWN_CATEGORY), CORBA_PEERS("corbaPeer"), ERICSSON_1(Constants.UNKNOWN_CATEGORY), SYSLOG_SERVERS(Constants.UNKNOWN_CATEGORY), ENROLLMENT_SERVERS(
            Constants.UNKNOWN_CATEGORY), IPSEC("ipsecPeer"), SECURITY_RELATED_FILES(Constants.UNKNOWN_CATEGORY);

    private final String iscfCategoryString;

    private TrustedCertCategory(final String iscfCategoryString) {
        this.iscfCategoryString = iscfCategoryString;
    }

    /**
     * Gets the category in the format as it is defined for ISCF files In case of CORBA_PEERS: corbaPeer In case of IPSEC: ipsecPeer UNKNOWN_CATEGORY otherwise (SL3 is not supported yet)
     * 
     * @return category as ISCF string
     */
    public String getIscfCategoryName() {
        return this.iscfCategoryString;
    }

    /**
     * @param certType the certType
     * @return Convert a CertificateType to TrustedCertCategory. Default value is {@link #ERICSSON_1}
     */
    public static TrustedCertCategory fromCertificateType(CertificateType certType) {
        TrustedCertCategory category = TrustedCertCategory.ERICSSON_1;
        switch (certType) {
        case IPSEC:
            category = TrustedCertCategory.IPSEC;
            break;
        case OAM:
            category = TrustedCertCategory.CORBA_PEERS;
            break;
        default:
            break;
        }
        return category;
    }

    /**
     * @param trustCategory the trustCategory
     * @return Convert a trustCategoryType to TrustedCertCategory. Default value is {@link #ERICSSON_1}
     */
    public static TrustedCertCategory fromTrustCategoryType(TrustCategoryType trustCategory) {
        TrustedCertCategory category = TrustedCertCategory.ERICSSON_1;
        switch (trustCategory) {
        case IPSEC:
            category = TrustedCertCategory.IPSEC;
            break;
        case OAM:
            category = TrustedCertCategory.CORBA_PEERS;
            break;
        case LAAD:
            category = TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS;
            break;
        default:
            break;
        }
        return category;
    }
}
