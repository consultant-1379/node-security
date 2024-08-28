/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.seclevel.util;

/**
 * This class is having the list of nodes and list server namess to start workflow.
 *
 * @author xkihari
 *
 */
public class CppGetSecurityLevelDetails {

    private String nodeName;
    private String securityLevelStatus;
    private String localRbacStatus;

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
    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the securityLevelStatus
     */
    public String getSecurityLevelStatus() {
        return securityLevelStatus;
    }

    /**
     * @param securityLevelStatus
     *            the securityLevelStatus to set
     */
    public void setSecurityLevelStatus(final String securityLevelStatus) {
        this.securityLevelStatus = securityLevelStatus;
    }

    /**
     * @return the localRbacStatus
     */
    public String getLocalRbacStatus() {
        return localRbacStatus;
    }

    /**
     * @param localRbacStatus
     *            the localRbacStatus to set
     */
    public void setLocalRbacStatus(final String localRbacStatus) {
        this.localRbacStatus = localRbacStatus;
    }

}
