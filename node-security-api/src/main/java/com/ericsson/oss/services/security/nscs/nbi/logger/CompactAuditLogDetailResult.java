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

public class CompactAuditLogDetailResult extends CompactAuditLogResult implements Serializable {

    private static final long serialVersionUID = 3412482956291239366L;

    private Map<String, Serializable> currentValues;
    private Map<String, Serializable> oldValues;
    private String error;

    public CompactAuditLogDetailResult() {
        super();
    }

    /**
     * Get the currentValues map.
     * 
     * If currentValues is not null, return an unmodifiable map (wrapper over the modifiable map, not allowing modifications to it directly, but
     * reflecting underlying mutable map changes).
     * 
     * If currentValues is null, return an immutable empty map.
     * 
     * @return the currentValues.
     */
    public Map<String, Serializable> getCurrentValues() {
        return currentValues == null ? Collections.emptyMap() : Collections.unmodifiableMap(currentValues);
    }

    /**
     * @param currentValues
     *            the currentValues to set
     */
    public void setCurrentValues(final Map<String, Serializable> currentValues) {
        if (currentValues != null) {
            this.currentValues = new HashMap<>(currentValues);
        } else {
            this.currentValues = null;
        }
    }

    /**
     * Get the oldValues map.
     * 
     * If oldValues is not null, return an unmodifiable map (wrapper over the modifiable map, not allowing modifications to it directly, but
     * reflecting underlying mutable map changes).
     * 
     * If oldValues is null, return an immutable empty map.
     * 
     * @return the oldValues.
     */
    public Map<String, Serializable> getOldValues() {
        return oldValues == null ? Collections.emptyMap() : Collections.unmodifiableMap(oldValues);
    }

    /**
     * @param oldValues
     *            the oldValues to set
     */
    public void setOldValues(final Map<String, Serializable> oldValues) {
        if (oldValues != null) {
            this.oldValues = new HashMap<>(oldValues);
        } else {
            this.oldValues = null;
        }
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error
     *            the error to set
     */
    public void setError(final String error) {
        this.error = error;
    }

}
