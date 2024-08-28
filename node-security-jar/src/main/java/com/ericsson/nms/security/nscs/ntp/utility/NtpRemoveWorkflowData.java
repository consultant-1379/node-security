/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ntp.utility;

import java.util.List;

import com.ericsson.nms.security.nscs.api.enums.NtpRemoveInputType;

/**
 * This class holds the NTP workflow data for node.
 *
 * @author xlakdag
 */

public class NtpRemoveWorkflowData {

    private String nodeFdn;

    private List<String> keyIdOrServerIdList;

    private NtpRemoveInputType ntpRemoveInputType;

    public NtpRemoveWorkflowData(final String nodeFdn, final List<String> keyIdOrServerIdList, final NtpRemoveInputType ntpRemoveInputType) {
        this.nodeFdn = nodeFdn;
        this.keyIdOrServerIdList = keyIdOrServerIdList;
        this.ntpRemoveInputType = ntpRemoveInputType;
    }

    /**
     * @return the nodeFdn
     */
    public String getNodeFdn() {
        return nodeFdn;
    }

    /**
     * @param nodeFdn
     *            the nodeFdn to set
     */
    public void setNodeFdn(final String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }

    /**
     * @return the keyIdOrServerIdList
     */
    public List<String> getKeyIdOrServerIdList() {
        return keyIdOrServerIdList;
    }

    /**
     * @param keyIdOrServerIdList
     *            the keyIdOrServerIdList to set
     */
    public void setKeyIdOrServerIdList(final List<String> keyIdorServerIdList) {
        this.keyIdOrServerIdList = keyIdorServerIdList;
    }

    /**
     * @return the ntpRemoveInputType
     */
    public NtpRemoveInputType getNtpRemoveInputType() {
        return ntpRemoveInputType;
    }

    /**
     * @param ntpRemoveInputType
     *            the ntpRemoveInputType to set
     */
    public void setNtpRemoveInputType(final NtpRemoveInputType ntpRemoveInputType) {
        this.ntpRemoveInputType = ntpRemoveInputType;
    }

}