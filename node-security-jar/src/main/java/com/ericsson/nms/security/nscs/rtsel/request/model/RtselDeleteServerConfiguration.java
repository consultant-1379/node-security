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
package com.ericsson.nms.security.nscs.rtsel.request.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "nodeRtselDetails" })
@XmlRootElement(name = "rtselDeleteServerConfiguration")
public class RtselDeleteServerConfiguration {

    @XmlElement(required = true)
    protected List<NodeRtselDetails> nodeRtselDetails;

    public List<NodeRtselDetails> getNodeRtselDetails() {
        if (nodeRtselDetails == null) {
            nodeRtselDetails = new ArrayList<NodeRtselDetails>();
        }
        return this.nodeRtselDetails;
    }

}
