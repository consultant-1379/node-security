package com.ericsson.nms.security.nscs.laad.service;

import com.ericsson.nms.security.nscs.api.laad.ex.LaadServiceException;
import com.ericsson.oss.itpf.smrs.SmrsAccount;

/**
 * Service to install laad file on the node
 * @author enatbol
 */
public interface InstallLaad {

    /**
     * 
     * To install laad file on the node and returns the CmResponse object
     * 
     * @return - the NewCmResponse
     */
    SmrsAccount installLaad() throws LaadServiceException;
}
