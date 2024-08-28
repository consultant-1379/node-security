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
package com.ericsson.nms.security.nscs.data.moget.param;

/**
 * This class contains NTPServer details such as <<keyId, serviceStatus, userLabel, ntpServerId>>.
 *
 * @author zkndsrv
 *
 */
public class NtpServer {
    private String keyId;
    private String serviceStatus;
    private String userLabel;
    private String ntpServerId;
    private String serverAddress;

    /**
     * @return the keyId
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * @param keyId
     *            the keyId to set
     */
    public void setKeyId(final String keyId) {
        this.keyId = keyId;
    }

    /**
     * @return the serviceStatus
     */
    public String getServiceStatus() {
        return serviceStatus;
    }

    /**
     * @param serviceStatus
     *            the serviceStatus to set
     */
    public void setServiceStatus(final String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    /**
     * @return the userLabel
     */
    public String getUserLabel() {
        return userLabel;
    }

    /**
     * @param userLabel the userLabel to set
     */
    public void setUserLabel(final String userLabel) {
        this.userLabel = userLabel;
    }

    /**
     * @return the ntpServerId
     */
    public String getNtpServerId() {
        return ntpServerId;
    }

    /**
     * @param ntpServerId the ntpServerId to set
     */
    public void setNtpServerId(final String ntpServerId) {
        this.ntpServerId = ntpServerId;
    }

    /**
     * @return the serverAddress
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(final String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NtpServer [keyId=").append(keyId).append(", serviceStatus=").append(serviceStatus).append(", userLabel=").append(userLabel).append(", ntpServerId=").append(ntpServerId)
                .append(", serverAddress=").append(serverAddress).append("]");
        return builder.toString();
    }

}
