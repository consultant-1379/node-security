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

public class DTOIpSecConfigValidityStatus implements Serializable {
    private static final long serialVersionUID = -7966890794336283625L;
    private List<IpSecConfigValidityStatus> ipSecConfigValidityStatus;

    public List<IpSecConfigValidityStatus> getIpSecConfigValidityStatus() {
        return ipSecConfigValidityStatus;
    }

    public void setIpSecConfigValidityStatus(final List<IpSecConfigValidityStatus> result) {
        this.ipSecConfigValidityStatus = result;
    }

    public DTOIpSecConfigValidityStatus(final List<IpSecConfigValidityStatus> result) {
        super();
        this.ipSecConfigValidityStatus = result;
    }

    public DTOIpSecConfigValidityStatus() {
        this(null);
    }

}
