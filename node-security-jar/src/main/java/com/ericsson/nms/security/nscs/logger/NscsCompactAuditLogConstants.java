/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.logger;

/**
 * Auxiliary utility class containing general purpose constants for Compact Audit Log (CAL).
 * 
 * It shall not be instantiated hence a private constructor is defined to hide the implicit public one.
 */
public class NscsCompactAuditLogConstants {

    public static final int UNKNOWN_NUM_OF = -1;

    private NscsCompactAuditLogConstants() {
        throw new IllegalStateException("Utility class shall not be instantiated.");
    }

}
