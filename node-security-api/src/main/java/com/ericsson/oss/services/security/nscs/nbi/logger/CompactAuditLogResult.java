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

public class CompactAuditLogResult implements Serializable {

    private static final long serialVersionUID = 9086689322870979072L;

    private String opType;
    private String id;

    public CompactAuditLogResult() {
        // empty constructor
    }

    /**
     * @return the opType
     */
    public String getOpType() {
        return opType;
    }

    /**
     * @param opType
     *            the opType to set
     */
    public void setOpType(final String opType) {
        this.opType = opType;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }

}
