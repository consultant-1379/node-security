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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import static com.ericsson.nms.security.nscs.integration.jee.test.command.SecurityProtocolChangeTests.CPP_NODE;
import static com.ericsson.nms.security.nscs.integration.jee.test.command.SecurityProtocolChangeTests.RADIO_NODE;

public enum SecurityProtocol {

    HTTPS(CPP_NODE, RADIO_NODE), FTPES(RADIO_NODE, CPP_NODE);

    private String supportedNodes;
    private String unsupportedNodes;

    SecurityProtocol(String supportedNodes, String unsupportedNodes) {
        this.supportedNodes = supportedNodes;
        this.unsupportedNodes = unsupportedNodes;
    }

    public String activate() {
        return this.name().toLowerCase() + " activate -n ";
    }

    public String deactivate() {
        return this.name().toLowerCase() + " deactivate -n ";
    }

    public String getSupportedNodes() {
        return supportedNodes;
    }

    public String getUnsupportedNodes() {
        return unsupportedNodes;
    }
}
