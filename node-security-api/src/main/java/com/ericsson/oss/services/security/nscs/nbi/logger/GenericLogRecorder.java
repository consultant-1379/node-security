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

/**
 * Auxiliary class to record all parameters needed to log a generic operation.
 */
public class GenericLogRecorder implements Serializable {

    private static final long serialVersionUID = -6163709374747903775L;

    private String userId;
    private String sourceIpAddr;
    private String sessionId;

    public GenericLogRecorder() {
        // empty constructor
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * @return the sourceIpAddr
     */
    public String getSourceIpAddr() {
        return sourceIpAddr;
    }

    /**
     * @param sourceIpAddr
     *            the sourceIpAddr to set
     */
    public void setSourceIpAddr(final String sourceIpAddr) {
        this.sourceIpAddr = sourceIpAddr;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId
     *            the sessionId to set
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

}
