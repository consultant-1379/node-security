/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger;

/**
 * The management mode, from a Compact Audit Log point of view, of a REST.
 */
public enum NbiCompactAuditLogMode {
    COMPACT_AUDIT_LOGGED_SYNC_NODES_REST,
    COMPACT_AUDIT_LOGGED_ASYNC_NODES_REST,
    NOT_COMPACT_AUDIT_LOGGED;

}
