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
package com.ericsson.nms.security.nscs.api.rest;

import java.io.Serializable;
import java.util.List;

public class DTOIpSecConfigSwitchStatus implements Serializable {
    private static final long serialVersionUID = -7966890794336283625L;
    private List<IpSecConfigSwitchStatus> switchStatusList;
    private String statusMessage;

    public List<IpSecConfigSwitchStatus> getSwitchStatusList() {
        return switchStatusList;
    }

    public void setSwitchStatusList(List<IpSecConfigSwitchStatus> result) {
        this.switchStatusList = result;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public DTOIpSecConfigSwitchStatus(List<IpSecConfigSwitchStatus> result, String statusMessage) {
        super();
        this.switchStatusList = result;
        this.statusMessage = statusMessage;
    }

    public DTOIpSecConfigSwitchStatus() {
        this(null, "");
    }

}
