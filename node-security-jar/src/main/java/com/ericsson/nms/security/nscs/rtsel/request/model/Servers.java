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

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "servers", propOrder = { "serverName" })
public class Servers {

    @XmlElement(required = true)
    protected Set<String> serverName;

    /**
     * @return the serverName
     */
    public Set<String> getServerName() {
        if (serverName == null) {
            serverName = new HashSet<String>();
        }
        return this.serverName;
    }

    /**
     * @param serverName
     *            the serverName to set
     */
    public void setServerName(final Set<String> serverName) {
        this.serverName = serverName;
    }
}