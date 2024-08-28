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

package com.ericsson.nms.security.nscs.cpp.seclevel.dto;

import java.io.Serializable;

/**
 * <p>
 * This SecLevelSwitchStatusDTO will have three strings (nodeName, code and message)
 * </p>
 */

public class SecLevelSwitchStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @param nodeName
     * @param code
     * @param message
     */

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

    /**
     * @param nodeName
     * @param code
     * @param message
     */
    /*
     * public SecLevelSwitchStatusDTO(String nodeName, String code, String message) { super(); this.nodeName = nodeName; this.code = code;
     * this.message = message; }
     */

}
