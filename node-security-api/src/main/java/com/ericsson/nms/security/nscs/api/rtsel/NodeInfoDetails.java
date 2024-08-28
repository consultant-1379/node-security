/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.rtsel;

import java.util.List;

/**
 * @author xchowja
 *
 */
public class NodeInfoDetails {

    private List<String> nodeFdnsList;
    private String entityProfileName;
    private String enrollmentMode;
    private String keySize;

    /**
     * 
     */
    public NodeInfoDetails() {
        super();
    }

    /**
     * @param nodeFdnsList
     *          - the nodeFdnsList to set
     * @param entityProfileName
     *          - he entityProfileName to set
     * @param enrollmentMode
     *          - the enrollmentMode to set
     * @param keySize
     *          - the keySize to set
     */
    public NodeInfoDetails(final List<String> nodeFdnsList, final String entityProfileName, final String enrollmentMode, final String keySize) {
        super();
        this.nodeFdnsList = nodeFdnsList;
        this.entityProfileName = entityProfileName;
        this.enrollmentMode = enrollmentMode;
        this.keySize = keySize;
    }

    /**
     * @return the nodeFdnsList
     */
    public List<String> getNodeFdnsList() {
        return nodeFdnsList;
    }

    /**
     * @param nodeFdnsList
     *            the nodeFdnsList to set
     */
    public void setNodeFdnsList(final List<String> nodeFdnsList) {
        this.nodeFdnsList = nodeFdnsList;
    }

    /**
     * @return the entityProfileName
     */
    public String getEntityProfileName() {
        return entityProfileName;
    }

    /**
     * @param entityProfileName
     *            the entityProfileName to set
     */
    public void setEntityProfileName(final String entityProfileName) {
        this.entityProfileName = entityProfileName;
    }

    /**
     * @return the enrollmentMode
     */
    public String getEnrollmentMode() {
        return enrollmentMode;
    }

    /**
     * @param enrollmentMode
     *            the enrollmentMode to set
     */
    public void setEnrollmentMode(final String enrollmentMode) {
        this.enrollmentMode = enrollmentMode;
    }

    /**
     * @return the keySize
     */
    public String getKeySize() {
        return keySize;
    }

    /**
     * @param keySize
     *            the keySize to set
     */
    public void setKeySize(final String keySize) {
        this.keySize = keySize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NodeInfoDetails [nodeFdnsList=" + nodeFdnsList + ", entityProfileName=" + entityProfileName + ", enrollmentMode=" + enrollmentMode + ", keySize=" + keySize + "]";
    }

}
