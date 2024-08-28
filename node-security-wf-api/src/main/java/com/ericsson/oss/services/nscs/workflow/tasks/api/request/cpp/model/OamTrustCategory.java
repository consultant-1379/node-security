/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model;

import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;

/**
 * Enumeration used for CPP trusted certificate categories
 * 
 */
public enum OamTrustCategory {

    LOCAL_AA_DB_FILE_SIGNERS, AA_SERVERS, CORBA_PEERS, ERICSSON_1, SYSLOG_SERVERS, ENROLLMENT_SERVERS, SECURITY_RELATED_FILES, UNKNOWN;

    /**
     * @param trustCategoryType
     *            type of the certificate OAM or IPSEC
     * @return trustCategory
     */
    public static OamTrustCategory fromCertificateType(String trustCategoryType) {
        OamTrustCategory trustCategory = null;
        if (trustCategoryType.equals(TrustCategoryType.OAM.toString())) {
            trustCategory = OamTrustCategory.CORBA_PEERS;
        } else if (trustCategoryType.equals(TrustCategoryType.LAAD.toString())) {
            trustCategory = OamTrustCategory.LOCAL_AA_DB_FILE_SIGNERS;
        }
        return trustCategory;
    }

    public static OamTrustCategory fromName(final String name) {
        for (final OamTrustCategory c : OamTrustCategory.values()) {
            if (c.toString().equals(name)) {
                return c;
            }
        }
        return UNKNOWN;
    }

    /*
     * Return the Enum name
     * 
     * @see java.lang.Enum#name()
     */
    @Override
    public String toString() {
        return this.name();
    }
}