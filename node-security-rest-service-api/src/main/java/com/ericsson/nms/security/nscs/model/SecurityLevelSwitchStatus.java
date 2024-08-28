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
package com.ericsson.nms.security.nscs.model;

import java.io.Serializable;

/**
 * <p>
 * Security level change request.
 * </p>
 * <p>
 * Encapsulate information need by a NodeSecuritySeviceLocalBean implementation
 * </p>
 */

public class SecurityLevelSwitchStatus implements Serializable {

    /**
     * @param nodeName
     * @param code
     * @param message
     */
    public SecurityLevelSwitchStatus(String nodeName, String code, String message) {
        super();
        this.nodeName = nodeName;
        this.code = code;
        this.message = message;
    }

    private static final long serialVersionUID = 1L;

    String nodeName;
    String code;
    String message;

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName
     *            the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
