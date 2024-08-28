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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CompactAuditLogSummaryResult extends CompactAuditLogResult implements Serializable {

    private static final long serialVersionUID = 898202643344763600L;

    private String entity;
    private Map<String, Serializable> result;

    public CompactAuditLogSummaryResult() {
        super();
    }

    /**
     * @return the entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @param entity
     *            the entity to set
     */
    public void setEntity(final String entity) {
        this.entity = entity;
    }

    /**
     * Get the result map.
     * 
     * If result is not null, return an unmodifiable map (wrapper over the modifiable map, not allowing modifications to it directly, but reflecting
     * underlying mutable map changes).
     * 
     * If result is null, return an immutable empty map.
     * 
     * @return the result.
     */
    public Map<String, Serializable> getResult() {
        return result == null ? Collections.emptyMap() : Collections.unmodifiableMap(result);
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(final Map<String, Serializable> result) {
        if (result != null) {
            this.result = new HashMap<>(result);
        } else {
            this.result = null;
        }
    }

}
